/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.process;

import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

/**
 * @author kamal.govindraj Provides methods to deal with Process definition
 *         (things like create a new process, query a process definition etc)
 */
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    JbpmTemplate jbpmTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see tavant.twms.bpm.ProcessDefinitionService#deploy(java.io.InputStream)
     */
    public void deploy(final ProcessDefinition processDefinition) {

        if (processDefinition == null) {
            throw new IllegalArgumentException("processDefinition can't be null");
        }

        jbpmTemplate.execute(new JbpmCallback() {
            public Object doInJbpm(JbpmContext context) {
                context.deployProcessDefinition(processDefinition);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see tavant.twms.bpm.ProcessDefinitionService#undeploy(java.lang.String)
     */
    public void undeploy(final String processName) {
        jbpmTemplate.execute(new JbpmCallback() {
            public Object doInJbpm(JbpmContext context) {

                ProcessDefinition processDefintion = context.getGraphSession().findLatestProcessDefinition(
                        processName);

                if (processDefintion != null) {
                    context.getGraphSession().deleteProcessDefinition(processDefintion);
                } else {
                    // TODO need to think about exception handling
                    throw new RuntimeException("Error : could find process with name " + processName);
                }
                return null;
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see tavant.twms.bpm.ProcessDefinitionService#find(java.lang.String)
     */
    public ProcessDefinition find(final String processName) {
        return (ProcessDefinition) jbpmTemplate.execute(new JbpmCallback() {
            public Object doInJbpm(JbpmContext context) {
                return context.getGraphSession().findLatestProcessDefinition(processName);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<ProcessDefinition> findAll() {
        return (List<ProcessDefinition>) jbpmTemplate.execute(new JbpmCallback() {
            public Object doInJbpm(JbpmContext context) {
                return context.getGraphSession().findLatestProcessDefinitions();
            }
        });
    }

    public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
        this.jbpmTemplate = jbpmTemplate;
    }
}

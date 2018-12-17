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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.scheduler.exe.Timer;
import org.springframework.util.Assert;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.ProcessVariables;

/**
 * @author vineeth.varghese
 * @date Jul 13, 2006
 */
public class ProcessServiceImpl implements ProcessService {

    private JbpmTemplate jbpmTemplate;

    private ProcessDefinitionService processDefinitionService;

    public ProcessInstance startProcess(final String processName, 
            final ProcessVariables processVariables) {
        if (processVariables == null || processName == null) {
            throw new IllegalArgumentException("Can't start process [" + processName + "] with varibles["
                    + processVariables + "]");
        }
        final ProcessDefinition def = processDefinitionService.find(processName);
        return (ProcessInstance) jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) {
                ProcessInstance instance = new ProcessInstance(def);
                processVariables.extractOutContextVariables(instance.getContextInstance());
                instance.signal();
                context.save(instance);
                return instance;
            }

        });
    }
    
    public ProcessInstance startProcessWithTransition(final String processName, 
            final ProcessVariables processVariables, final String transitionName) {
        if (processVariables == null || processName == null) {
            throw new IllegalArgumentException("Can't start process [" + processName + "] with varibles["
                    + processVariables + "] and transition[" + transitionName + "]");
        }
        final ProcessDefinition def = processDefinitionService.find(processName);
        return (ProcessInstance) jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) {
                ProcessInstance instance = new ProcessInstance(def);
                processVariables.extractOutContextVariables(instance.getContextInstance());
                instance.signal(transitionName);
                context.save(instance);
                return instance;
            }

        });
    }

    @SuppressWarnings("unchecked")
    public List<ProcessInstance> findAllProcessesByName(final String processName) {
        Assert.notNull(processName, "Cannot load Processes with null process name");
        final ProcessDefinition def = processDefinitionService.find(processName);
        jbpmTemplate.setProcessDefinition(def);
        return jbpmTemplate.findProcessInstances();
    }

    public ProcessInstance findProcess(Long processId) {
        Assert.notNull(processId, "Cannot load ProcessInstance with null identifier");
        return jbpmTemplate.findProcessInstance(processId);
    }

    /**
     * @param jbpmTemplate
     *            the jbpmTemplate to set
     */
    public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
        this.jbpmTemplate = jbpmTemplate;
    }

    /**
     * @param processDefinitionService
     *            the processDefinitionService to set
     */
    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private Date convertToJavaDate(CalendarDate domainLanguageDate){
    	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    	Date date;
		try {
			date = sdf.parse(domainLanguageDate.toString(DATE_FORMAT));
			return date;
		} catch (ParseException e) {
			return null;
		}
    	
	}

	public void updateDueDateForPartReturn(final Timer timer,
			final CalendarDate updatedDueDate) {
		final Date dueDate = convertToJavaDate(updatedDueDate);
		jbpmTemplate.execute(new JbpmCallback() {
					public Object doInJbpm(JbpmContext context) {
						timer.setDueDate(dueDate);
						context.save(timer.getTaskInstance());
						return timer;
					}
				});
	}
    
}

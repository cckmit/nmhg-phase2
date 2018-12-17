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
package tavant.twms.jbpm.infra;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.SubProcessResolver;
import org.springframework.util.Assert;

import tavant.twms.process.ProcessDefinitionService;

/**
 * TODO : Look at org.jbpm.graph.node.DbSubProcessResolver. This is for binding
 * the processes together. This is a jbpm bug read
 * http://wiki.tavant.com/twiki/bin/view/WarrantyProduct/JBPMLab
 */
public class DBSubProcessResolver implements SubProcessResolver {

    private static final Logger logger = Logger.getLogger(DBSubProcessResolver.class);

    ProcessDefinitionService processDefinitionService;

    public ProcessDefinition findSubProcess(Element subProcessElement) {
        String subProcessName = subProcessElement.attributeValue("name");
        if(logger.isDebugEnabled())
        {
            logger.debug("Attempting to find sub process name [" + subProcessName + "]");
        }
        ProcessDefinition subProcessDefinition = getProcessDefinitionService().find(subProcessName);
        // Assuming we need only the latest version
        Assert.notNull(subProcessDefinition, "Couldnot find subprocess with name [" + subProcessName
                + "] Is it deployed before parent process?");
        if(logger.isDebugEnabled())
        {
            logger.debug("Sub process found was [" + subProcessDefinition + "]");
        }
        return subProcessDefinition;
    }

    public ProcessDefinitionService getProcessDefinitionService() {
        if (this.processDefinitionService == null) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Trying to get process definition service");
            }
            BeanLocator beanLocator = new BeanLocator();
            this.processDefinitionService = (ProcessDefinitionService) beanLocator
                    .lookupBean("processDefinitionService");
            if(logger.isDebugEnabled())
            {
                logger.debug("Process Definition Service [" + this.processDefinitionService + "]");
            }
            Assert.notNull(this.processDefinitionService, "Couldnt look up processDefinition service");
        }
        return this.processDefinitionService;
    }

}

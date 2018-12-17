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
package tavant.twms.jbpm.nodes;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import tavant.twms.jbpm.infra.ServiceBeanInvoker;

public class ServiceNode extends Node {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ServiceNode.class);

    Delegation decisionDelegation = new Delegation();

    public ServiceNode() {
        super();
    }

    public ServiceNode(String name) {
        super(name);
    }

    @Override
    public void read(Element element, JpdlXmlReader jpdlXmlReader) {
        element.addAttribute("class", ServiceBeanInvoker.class.getName());
        element.addAttribute("config-type", "bean");
        this.decisionDelegation.read(element, jpdlXmlReader);
        if(logger.isDebugEnabled())
        {
            logger.debug("Reading service node");
        }
        super.read(element, jpdlXmlReader);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        ServiceBeanInvoker serviceBeanInvoker = (ServiceBeanInvoker) this.decisionDelegation.getInstance();
        if(logger.isDebugEnabled())
        {
            logger.debug("Service bean Invoker [" + serviceBeanInvoker + "]");
        }
        serviceBeanInvoker.executeServiceNode(executionContext);
        leave(executionContext);
    }
}

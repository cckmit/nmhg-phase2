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

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.xml.JpdlXmlReader;

/**
 * @author subin.p
 * @date Apr 05, 2007
 */
public class CloneTokenNode extends Node {

    private static final Logger logger = Logger.getLogger(CloneTokenNode.class);

    private String endTransition;

    private String normalTransition;

    public CloneTokenNode() {
        super();
    }

    public CloneTokenNode(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(Element cloneElement, JpdlXmlReader jpdlReader) {
        super.read(cloneElement, jpdlReader);
        this.name = cloneElement.attributeValue("name");
        List<Element> transitionElements = cloneElement.elements("transition");
        if(transitionElements.size() < 2) {
            throw new IllegalArgumentException("There should be atleast two transitions" +
                        "for the clone-token-node");
        }

        //  TODO : Check if we need to persist the endTransition and the normalTransition - duplication.
        //  if - elseif - else.
        Element firstTransition = transitionElements.get(0);
        Element secondTransition = transitionElements.get(1);
        if (Boolean.parseBoolean(firstTransition.attributeValue("end-process-transition"))) {
            this.endTransition = firstTransition.attributeValue("name");
            this.normalTransition = secondTransition.attributeValue("name");
        }else if(Boolean.parseBoolean(secondTransition.attributeValue("end-process-transition"))){
            this.endTransition = secondTransition.attributeValue("name");
            this.normalTransition = firstTransition.attributeValue("name");
        }else {
            throw new IllegalArgumentException("There should be atleast one transition" +
            "with the 'end-process' attribute set to true.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        if (token.isAbleToReactivateParent()) {
            Token cloneToken = new Token();
            cloneToken.setProcessInstance(token.getProcessInstance());
            ExecutionContext cloneContext = new ExecutionContext(cloneToken);
            cloneContext.setEvent(executionContext.getEvent());
            cloneContext.setAction(executionContext.getAction());
            ProcessInstance processInstance = token.getProcessInstance();
            if(logger.isDebugEnabled())
            {
                logger.debug("Token[" + token + "] is able to reactivate Parent Token");
            }
            token.setAbleToReactivateParent(false);
            leave(cloneContext, this.endTransition);

            // TODO - Need to understand the implications.Giving life to a dead processInstance :-)
            processInstance.setEnd(null);
            cloneToken.setProcessInstance(processInstance);
            processInstance.setRootToken(cloneToken);
            leave(executionContext, this.normalTransition);
        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("Token[" + token + "] CANNOT reactivate Parent Token");
            }
            leave(executionContext); // TODO : Check it out.. Does reactivate parent matter ?
        }
    }

    public String getEndTransition() {
        return this.endTransition;
    }

    public String getNormalTransition() {
        return this.normalTransition;
    }
}

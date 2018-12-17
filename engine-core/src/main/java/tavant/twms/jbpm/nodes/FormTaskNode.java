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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class FormTaskNode extends TaskNode {

    private static final Logger logger = Logger.getLogger(FormTaskNode.class);
    private static final String INPUT_FORM = "inputForm";
    List<TransitionCondition> transitionConditions = new ArrayList<TransitionCondition>();
    Map<String, String> formNodes = new TreeMap<String, String>();

    public FormTaskNode() {
        super();
    }

    public FormTaskNode(String name) {
        super(name);
    }

    @Override
    public void read(Element element, JpdlXmlReader jpdlReader) {
        super.read(element, jpdlReader);
        readFormInputs(element);
        readTransitionConditions(element);
    }

    @SuppressWarnings("unchecked")
    private void readFormInputs(Element taskNodeElement) {
        if(logger.isDebugEnabled()) {
            logger.debug("Reading form inputs");
        }
        Element taskElement = taskNodeElement.element("task");
        if(logger.isDebugEnabled()) {
            logger.debug("Task element is [" + taskElement + "]");
        }
        if (taskElement == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("No Forms found since task element is null");
            }
            return;
        }
        Element formsElement = taskElement.element("forms");
        if(logger.isDebugEnabled()) {
            logger.debug("Form element is [" + formsElement + "]");
        }
        if (formsElement == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("No Forms found since forms element is null");
            }
            return;
        }
        List<Element> list = formsElement.elements();
        for (Element formElement : list) {
            this.formNodes.put(formElement.getName(), formElement.getTextTrim());
            if(logger.isDebugEnabled()) {
                logger.debug("Added form of type [" + formElement.getName() + "] name ["
                        + formElement.getTextTrim() + "]");
            }
        }
    }



    @SuppressWarnings("unchecked")
    private void readTransitionConditions(Element taskNodeElement) {
        Element taskElement = taskNodeElement.element("task");
        if(logger.isDebugEnabled()) {
            logger.debug("Task element is [" + taskElement + "]");
        }
        if (taskElement == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("No Task available to transition out from");
            }
            return;
        }
        List<Element> transitionElements = taskNodeElement.elements("transition");
        for (Element transitionElement : transitionElements) {
            Element conditionElement = transitionElement.element("condition");
            if (conditionElement != null) {
                String conditionExpression = getConditionExpression(conditionElement);
                if (StringUtils.hasText(conditionExpression)) {
                    String transitionName = transitionElement.attributeValue("name");
                    if(logger.isDebugEnabled()) {
                        logger.debug("Transition[" + transitionName + "] is attached with Expression["
                                + conditionExpression + "]");
                    }
                    this.transitionConditions.add(new TransitionCondition(transitionName, conditionExpression));
                }
            }
        }
    }

    String getConditionExpression(Element conditionElement) {
        String expression = conditionElement.attributeValue("expression");
        if (expression != null) {
            return expression;
        } else {
            return conditionElement.getText();
        }
    }

    public Map<String, String> getFormNodes() {
        return this.formNodes;
    }

    public void setFormNodes(Map<String, String> formNodes) {
        this.formNodes = formNodes;
    }

    public String getDefaultForm() {
        String defaultFormName = this.formNodes.get(INPUT_FORM);
        Assert.hasText(defaultFormName, "Attempting to retrieve form" + " for form task node["
                    + getName() + "] which has no input forms configured");
        return defaultFormName;
    }

    @SuppressWarnings("unchecked")
    public List<Transition> getAvailableTransitions(TaskInstance taskInstance) {
        Token token = taskInstance.getToken();
        List<Transition> availableTransitions = new ArrayList<Transition>();
        availableTransitions.addAll(getLeavingTransitions());
        for (TransitionCondition condition : this.transitionConditions) {
            Object result = JbpmExpressionEvaluator.evaluate(condition.getExpression(),
                    new ExecutionContext(token));
            if (Boolean.FALSE.equals(result)) {
                logger.debug("Removing Transiton[" + condition.getTransitionName()
                        + "] from the list of available transition");
                availableTransitions.remove(getLeavingTransition(condition.getTransitionName()));
            }
        }
        return availableTransitions;
    }

    /**
     * @return the transitionConditions
     */
    public List<TransitionCondition> getTransitionConditions() {
        return this.transitionConditions;
    }

    /**
     * @param transitionConditions the transitionConditions to set
     */
    public void setTransitionConditions(List<TransitionCondition> transitionConditions) {
        this.transitionConditions = transitionConditions;
    }
}

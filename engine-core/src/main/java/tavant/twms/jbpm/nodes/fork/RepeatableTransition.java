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
package tavant.twms.jbpm.nodes.fork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.ContextVariableProvider;


/**
 * @author vineeth.varghese
 * @date Nov 10, 2006
 */
public class RepeatableTransition implements Serializable {

    private static final Logger logger = Logger.getLogger(RepeatableTransition.class);

    private String transitionName;

    private String repetitionCriteriaClass;

    private String inputExpression;

    private String outputExpression;

    public RepeatableTransition() {
        //for hibernate
    }

    public List<ForkedPath> getForkedPaths(ExecutionContext executionContext) {
        List<ForkedPath> forkedPath = new ArrayList<ForkedPath>();
        if(logger.isDebugEnabled())
        {
            logger.debug("Attempting to branch transition for [" + this + "]");
        }
        RepetitionCriteria repetitionCriteria = getRepetitionCriteria();
        Object input = new ContextVariableProvider(executionContext).getContextVariable(this.inputExpression);
        Collection<RepeatContext> repeatContexts = repetitionCriteria.evaluate(input);
        if(logger.isDebugEnabled()){
            logger.debug("Extracted repeat context [" + repeatContexts + "]");
        }
        for(RepeatContext repeatContext : repeatContexts) {
            ExecutionContext childExecutionContext =
                ExecutionContextUtil.createChildExecutionContext(executionContext, this.transitionName);
            if(logger.isDebugEnabled()){
                logger.debug("Created Child ExecutionContext with Token["
                        + childExecutionContext.getToken().getFullName() + "]");
            }
            repeatContext.conditionToken(childExecutionContext.getToken());
            ForkedPath forkPath = new ForkedPath(this.transitionName,
                    childExecutionContext, this.outputExpression, repeatContext.getObjectForRepeat());
            forkedPath.add(forkPath);
            if(logger.isDebugEnabled())
            {
                logger.debug("Added fork path [" + forkPath + "]");
            }
        }
        return forkedPath;
    }

    public void read(Element forkElement) {
        this.transitionName = forkElement.attributeValue("name");
        Assert.hasText(this.transitionName,
                "Multipliable transition should have a non-null transition name");
        this.repetitionCriteriaClass = forkElement.element("repetition-criteria").getTextTrim();
        Assert.state(ClassUtils.isPresent(this.repetitionCriteriaClass),
                "Unable to load the multiplier class [" + this.repetitionCriteriaClass + "]");
        this.inputExpression = forkElement.elementTextTrim("input-expression");
        this.outputExpression = forkElement.elementTextTrim("output-expression");
    }


    private RepetitionCriteria getRepetitionCriteria() {
       return (RepetitionCriteria) ReflectionUtil.createNewInstance(this.repetitionCriteriaClass);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("transition name", this.transitionName)
                .append("multiplier class name", this.repetitionCriteriaClass)
                .append("input expression", this.inputExpression)
                .append("input expression", this.outputExpression)
                .toString();
    }

    /**
     * @return the transitionName
     */
    public String getTransitionName() {
        return this.transitionName;
    }
}

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
package tavant.twms.jbpm.action;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author vineeth.varghese
 * @date Feb 26, 2007
 */
public class TaskInstanceEndAction extends Action {

	private static final Logger logger = Logger
			.getLogger(TaskInstanceEndAction.class);

	String transition;

	String condition;

	public String getTransition() {
		return this.transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

	@Override
    public void execute(ExecutionContext executionContext) throws Exception {
		Assert.notNull(this.transition, "Transition cannot be null");
		// Assuming a TaskNode will have only one Task
		if (isConditionSatified(executionContext)) {
			executionContext.getTaskInstance().end(getTransition());
		}
	}

	private boolean isConditionSatified(ExecutionContext executionContext) {
		//If condition is not specified assume it is "execute always"
		if(!StringUtils.hasText(this.condition)) {
			return true;
		}
		Object conditionValue = JbpmExpressionEvaluator.evaluate(this.condition,
				executionContext);
		if(logger.isDebugEnabled())
		{
        		logger.debug("The condition [" + this.condition + "] evaluated to ["
        				+ conditionValue + "]");
		}
		Assert.state(conditionValue instanceof Boolean, "The condition ["
				+ this.condition + "] evaluates to [" + conditionValue
				+ "] which is not a Boolean");
		return (Boolean) conditionValue;
	}
}

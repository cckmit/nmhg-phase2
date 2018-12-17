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
package tavant.twms.jbpm.assignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.jbpm.infra.ContextVariableProvider;

/**
 * Provide a variable list (to look up from execution context or spring context)
 * and a domain service API, which gives back a username, This API assigns the
 * task to that person
 *
 * @author kannan.ekanath
 *
 */
public class ScriptAssignmentHandler implements AssignmentHandler {

	private static final Logger logger = Logger
			.getLogger(ScriptAssignmentHandler.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String beanName;

	private String methodName;

	private final List<String> parameters = new ArrayList<String>();

	private final BeanLocator beanLocator = new BeanLocator();

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		Assert
				.state(StringUtils.hasText(this.beanName),
						"Bean Name cannot be empty");
		Assert.state(StringUtils.hasText(this.methodName),
				"Method Name cannot be empty");
		Object[] paramValues = getParameterValues(executionContext);
		Object returnObject = ReflectionUtil.executeMethod(getBean(),
				this.methodName, paramValues);
		Assert.state(returnObject instanceof String, "Returned object [" + returnObject + "] from [" + this + "] is not a String or a user name");
		//assume that the returned string will be a valid user name
		if(logger.isDebugEnabled())
		{
		    logger.debug("user name is [" + returnObject + "]");
		}
		assignable.setActorId((String) returnObject);
	}

	@SuppressWarnings("unchecked")
	Object[] getParameterValues(ExecutionContext executionContext) {
		ContextVariableProvider contextVariableProvider = new ContextVariableProvider(
				executionContext);
		Collection values = contextVariableProvider
				.getContextVariables(this.parameters);
		if(logger.isDebugEnabled())
		{
		    logger.debug("Paramter values are [" + values + "]");
		}
		return values.toArray();
	}

	Object getBean() {
		Object bean = this.beanLocator.lookupBean(this.beanName);
		if(logger.isDebugEnabled())
		{
		    logger.debug("Bean name [" + this.beanName + "] and bean [" + bean
                            + "] were resolved");
		}
		Assert.state(bean != null, "Bean [" + this.beanName
				+ "] not found in spring context");
		return bean;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("Bean name", this.beanName).append(
				"Method name", this.methodName)
				.append("Parameter names", this.parameters).toString();
	}

}

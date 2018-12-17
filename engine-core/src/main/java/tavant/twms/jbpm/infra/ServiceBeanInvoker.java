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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.EngineException;
import tavant.twms.jbpm.bsh.BeanScript;

public class ServiceBeanInvoker {

    private static final Logger logger = Logger.getLogger(ServiceBeanInvoker.class);

    String outputName;

    String beanName;

    String methodName;

    List<String> parameters = new ArrayList<String>();

    Element postProcess;

    BeanLocator beanLocator = new BeanLocator();

    Element transition;

    public static final String SERVICE_BEAN_RESULT = "result";

    public ServiceBeanInvoker() {
    }

    public void executeServiceNode(ExecutionContext executionContext) {
        Assert.state(StringUtils.hasText(this.beanName), "Bean Name cannot be empty");
        Assert.state(StringUtils.hasText(this.methodName), "Method Name cannot be empty");
        Assert.state(!CollectionUtils.isEmpty(this.parameters), "Parameters cannot be empty");
        Object[] paramValues = getParameterValues(executionContext);
        Object returnObject = ReflectionUtil.executeMethod(getBean(), this.methodName, paramValues);
        String variableName = SERVICE_BEAN_RESULT;
        if (StringUtils.hasText(this.outputName)) {
            variableName = this.outputName;
        }
        executionContext.getContextInstance().setTransientVariable(variableName, returnObject);
        if(logger.isDebugEnabled())
        {
            logger.debug("Set transient variable [" + variableName + "] with value [" + returnObject
                    + "]");
        }
        if (this.postProcess != null && StringUtils.hasLength(this.postProcess.getText())) {
            executeScript(executionContext, returnObject);
        }
    }

    void executeScript(ExecutionContext executionContext, Object returnObject) {
        try {
            BeanScript beanScript = new BeanScript(this.postProcess.getText());
            if(logger.isDebugEnabled()){
                logger.debug("Executing script [" + this.postProcess + "]");
            }
            beanScript.execute(executionContext);
        } catch (Exception e) {
            logger.error("error",e);
            throw new EngineException("Unexpected exception during service execution", e);
        }
    }

    @SuppressWarnings("unchecked")
    Object[] getParameterValues(ExecutionContext executionContext) {
        ContextVariableProvider contextVariableProvider = new ContextVariableProvider(executionContext);
        Collection values = contextVariableProvider.getContextVariables(this.parameters);
        if(logger.isDebugEnabled()){
            logger.debug("Paramter values are [" + values + "]");
        }
        return values.toArray();
    }

    Object getBean() {
        Object bean = this.beanLocator.lookupBean(this.beanName);
        if(logger.isDebugEnabled())
        {
            logger.debug("Bean name [" + this.beanName + "] and bean [" + bean + "] were resolved");
        }
        Assert.state(bean != null, "Bean [" + this.beanName + "] not found in spring context");
        return bean;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void setPostProcess(Element postProcess) {
        this.postProcess = postProcess;
    }

    public void setTransition(Element transition) {
        this.transition = transition;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("bean name", this.beanName)
            .append("method name", this.methodName)
            .append("parameters", this.parameters)
            .append("post process", this.postProcess)
            .append("output name", this.outputName)
            .toString();
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }


}

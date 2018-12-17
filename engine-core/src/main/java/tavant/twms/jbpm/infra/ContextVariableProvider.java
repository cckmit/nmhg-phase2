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

import org.apache.commons.lang.StringUtils;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.Assert;

import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.OgnlBeanProvider;

/**
 * @author vineeth.varghese
 * @date Aug 22, 2006
 */
public class ContextVariableProvider {
    
    private ExecutionContext executionContext;
    
    private BeanProvider beanProvider = new OgnlBeanProvider();
    
    public ContextVariableProvider(ExecutionContext context) {
        Assert.notNull(context, "Need a valid ExecutionContext to extract variables from");
        this.executionContext = context;
    }    
    
    @SuppressWarnings("unchecked")
    public Collection getContextVariables(List<String> expressions) {
        Assert.notNull(expressions, "Collection of expression passed is null");
        Collection values = new ArrayList();
        for (String expression : expressions) {
            values.add(getContextVariable(expression));
        }
        return values;
    }
    
    /**
     * Extract all the objects represented by the expression from the 
     * ExecutionContext and if the object obtained is a collection,
     * its contents are added to the output collection.
     * @param expressions
     * @return 
     */
    @SuppressWarnings("unchecked")    
    public Collection getExplodedContextVariables(List<String> expressions) {
        Assert.notNull(expressions, "Collection of expression passed is null");
        Collection values = new ArrayList();
        for (String expression : expressions) {
            Object obj = getContextVariable(expression);
            if (obj instanceof Collection) {
                values.addAll((Collection)obj);
            } else {
                values.add(obj);
            }            
        }
        return values;        
    }

    /**
     * An expression is essentially a way to identify one or more objects in an object graph.
     * However, ognl expression does not let you say  
     * @param expression
     * @return
     */
    public Object getContextVariable(String expression) {        
        Assert.notNull(expression, "Expression String is null");
        String contextVariableName = null;
        String subExpression = null;
        int dotIndex = expression.indexOf('.');
        if (dotIndex != -1) {
            contextVariableName = expression.substring(0, dotIndex);
            subExpression = expression.substring(dotIndex + 1);
        } else {
            contextVariableName = expression;
        }
        Object target = getValueForVariable(contextVariableName);
        if (StringUtils.isNotEmpty(subExpression)) {
            target = beanProvider.getProperty(subExpression, target);
        }
        return target;
    }

	protected Object getValueForVariable(String contextVariableName) {
		return executionContext.getVariable(contextVariableName);
	}
}

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

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.springframework.util.StringUtils;

public class ExpressionAssignmentHandler implements AssignmentHandler {

    private static final Logger logger = Logger.getLogger(ExpressionAssignmentHandler.class);

    private String expression;

    public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
        if(logger.isDebugEnabled())
        {
            logger.debug("Expression for assignment[" + this.expression + "]");
        }
        String[] tokens = this.expression.split("=");
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Expression [" + this.expression + "] is not valid. "
                    + "Try something like actor=claim.filedBy.name");
        }
        /*
         * If the configuration is for an actor we assume this is an ognl
         * expression. For the time being pooledActor is kept but we will get
         * rid of this later.
         */
        if (tokens[0].equalsIgnoreCase("actor")) {
            assignable.setActorId(extractActor(executionContext, tokens[1]));
        } else if (tokens[0].equalsIgnoreCase("pooledActor")) {
            assignable.setPooledActors(new String[] { tokens[1] });
        } else {
            throw new IllegalArgumentException("Expression rhs is not one of the supported "
                    + "[actor | pooledActor] but [" + tokens[1] + "]");
        }
    }

    private String extractActor(ExecutionContext executionContext, String lhs) {
        if (isOgnlExpression(lhs)) {
            return evaulateOgnlExpression(executionContext, lhs);
        }
        return lhs;
    }

    private String evaulateOgnlExpression(ExecutionContext executionContext, String ognlExpression) {
        String expressionToEvaluate = ognlExpression.substring(5, ognlExpression.length() - 1);
        return (String) extractVariable(executionContext, expressionToEvaluate).toString();
    }

    Object extractVariable(ExecutionContext executionContext, String variable) {
        String contextVariableName = null;
        String ognlExpression = null;
        int dotIndex = variable.indexOf('.');
        if (dotIndex != -1) {
            contextVariableName = variable.substring(0, dotIndex);
            ognlExpression = variable.substring(dotIndex + 1);
        } else {
            contextVariableName = variable;
        }
        Object value = executionContext.getVariable(contextVariableName);
        if(logger.isDebugEnabled())
        {
            logger.debug("Context variable[" + contextVariableName + "] is object[" + value + "]");
        }
        if (StringUtils.hasText(ognlExpression)) {
            value = evaulateOgnlExpression(value, ognlExpression);
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Actor obtained from the expression[" + this.expression + "] is [" + value + "]");
        }
        return value;
    }

    private Object evaulateOgnlExpression(Object target, String ognlExpression) {
        try {
            return Ognl.getValue(Ognl.parseExpression(ognlExpression), new OgnlContext(), target);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isOgnlExpression(String lhs) {
        return lhs.startsWith("ognl{");
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}

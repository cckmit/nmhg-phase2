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

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

public class ForkedPath {

    private static final Logger logger = Logger.getLogger(ForkedPath.class);

    String transitionName;

    ExecutionContext executionContext;

    String variableName;

    Object value;

    public ForkedPath(String transitionName, ExecutionContext executionContext) {
        this(transitionName, executionContext, null, null);
    }

    public ForkedPath(String transitionName, ExecutionContext executionContext, String variableName, Object value) {
        Assert.notNull(executionContext, "Cannot take a null execution context");
        Assert.notNull(executionContext.getToken(), "Execution context with no token name");
        this.transitionName = transitionName;
        this.executionContext = executionContext;
        this.variableName = variableName;
        this.value = value;
    }

    public ExecutionContext getExecutionContext() {
        return this.executionContext;
    }

    public String getTransitionName() {
        return this.transitionName;
    }

    public void takeTransition(Node node) {
        if(this.variableName != null) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Setting variable [" + this.variableName + "] to value [" + this.value + "]");
            }
            this.executionContext.setVariable(this.variableName, this.value);
        }
        node.leave(this.executionContext, this.transitionName);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
        .append("transition name", this.transitionName)
        .append("token name", this.executionContext.getToken().getName())
        .append("variable name", this.variableName)
        .append("value", this.value)
        .toString();
    }


}

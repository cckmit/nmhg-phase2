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

import java.io.Serializable;

/**
 * @author vineeth.varghese
 * @date Sep 17, 2006
 */
public class TransitionCondition implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    String transitionName;
    String expression;
    
    public TransitionCondition() {
        
        // TODO Auto-generated constructor stub
    }
    public TransitionCondition(String transitionName, String expression) {
        this.transitionName = transitionName;
        this.expression = expression;
    }
    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }
    /**
     * @return the transitionName
     */
    public String getTransitionName() {
        return transitionName;
    }

}

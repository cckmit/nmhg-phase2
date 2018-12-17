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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jun 5, 2007
 * Time: 1:31:20 PM
 */

package tavant.twms.web.security.authz.condition.evaluator;

import com.opensymphony.xwork2.util.ValueStack;
import org.springframework.util.Assert;
import tavant.twms.security.condition.evaluator.ConditionEvaluator;

public class StrutsAwareConditionEvaluator implements ConditionEvaluator {

    public boolean evaluate(String condition,
                            Object evaluationContext) {

        Assert.isAssignable(ValueStack.class, evaluationContext.getClass(),
                "Expecting evaluation context of type" + ValueStack.class + 
                        ". Encountered context : " +
                        evaluationContext.getClass());

        ValueStack stack = (ValueStack) evaluationContext;
        Object result = stack.findValue(condition);
        Assert.isTrue(Boolean.class.equals(result.getClass()),
                    "Authorization condition is invalid, since it doesn't " +
                        "evaluate to a boolean.");

        return (Boolean) result;
    }
}

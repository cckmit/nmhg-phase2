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
 * Date: Jun 4, 2007
 * Time: 6:05:47 PM
 */

package tavant.twms.security.condition.evaluator;

import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.util.Assert;

import java.util.Map;

public class OgnlConditionEvaluator implements ConditionEvaluator {

    public boolean evaluate(String condition,
                            Object evaluationContext) {

        Assert.isAssignable(Map.class, evaluationContext.getClass(),
                "Expecting evaluation context of type Map<String, Object>. " +
                        "Encountered context : " +
                        evaluationContext.getClass());

        try {
            Object result = Ognl.getValue(condition, evaluationContext);
            Assert.isTrue(Boolean.class.equals(result.getClass()),
                    "Authorization condition is invalid, since it doesn't " +
                            "evaluate to a boolean.");

            return (Boolean) result;
        } catch (OgnlException e) {
            throw new RuntimeException("Exception while evaluating the " +
                    "authorization condition : ", e);
        }
    }
}

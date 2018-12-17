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
 * Time: 5:31:00 PM
 */

package tavant.twms.web.security.authz.tags;

import tavant.twms.security.condition.evaluator.ConditionEvaluator;
import tavant.twms.security.condition.evaluator.OgnlConditionEvaluator;

import java.util.HashMap;
import java.util.Map;

public class OgnlAwareAuthorizeTag extends AbstractConditionAwareAuthorizeTag {

    protected Map<String, Object> createEvaluationContext() {
        Map<String, Object> evaluationContext = new HashMap<String, Object>(20);
        addCurrentUserToContext(evaluationContext);
        return evaluationContext;
    }

    protected void addCurrentUserToContext(Map<String, Object> ognlContext) {
        ognlContext.put("_currentUser", getCurrentUser());
    }

    protected ConditionEvaluator getConditionEvaluator() {
        return (ConditionEvaluator) getApplicationBean("ognlConditionEvaluator",
                OgnlConditionEvaluator.class);
    }
}

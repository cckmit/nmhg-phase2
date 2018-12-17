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
 * Time: 8:04:46 PM
 */

package tavant.twms.web.security.authz.tags;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.jsp.TagUtils;
import tavant.twms.security.condition.context.DomainAwareContext;
import tavant.twms.security.condition.evaluator.ConditionEvaluator;
import tavant.twms.web.security.authz.condition.evaluator.StrutsAwareConditionEvaluator;

public class StrutsAwareAuthorizeTag
        extends AbstractConditionAwareAuthorizeTag {

    private ValueStack stack;
    private DomainAwareContext ctx;

    @Override
    protected ValueStack createEvaluationContext() {
        stack = TagUtils.getStack(pageContext);

        pushDomainAwareContextToStack();
        
        return stack;
    }

    private void pushDomainAwareContextToStack() {
        ctx = (DomainAwareContext)
                getApplicationBean("domainAwareContext",
                        DomainAwareContext.class);
        stack.push(ctx);
    }

    @Override
    protected void cleanUp() {
        stack.getRoot().remove(ctx);
        super.cleanUp();
    }

    protected ConditionEvaluator getConditionEvaluator() {
        return (ConditionEvaluator) getApplicationBean(
                "strutsAwareConditionEvaluator",
                StrutsAwareConditionEvaluator.class);
    }

}

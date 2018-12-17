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
 * Time: 4:17:47 PM
 */

package tavant.twms.web.security.authz.tags;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.condition.evaluator.ConditionEvaluator;

import javax.servlet.jsp.JspException;

public abstract class AbstractConditionAwareAuthorizeTag extends PermissionAwareAuthorizeTag {

    protected String condition;
    protected boolean invertResult;
    public static final String EVAL_RESULT_PAGE_ATTRIBUTE = "tavant.twms.web.security.authz.evalResult";

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setInvertResult(boolean invertResult) {
        this.invertResult = invertResult;
    }

    @Override
    public int doStartTag() throws JspException {
        int parentEvalResult = super.doStartTag();
        if(!skipRoleCheck) {
            parentEvalResult = invertResultIfRequired(parentEvalResult);
        }

        if (parentEvalResult == SKIP_BODY) {
            return processResult(SKIP_BODY);
        }

        if (StringUtils.hasText(condition)) {

            try {
                final Object evaluationContext = createEvaluationContext();

                if (evaluationContext == null) {
                    throw new RuntimeException("Authorization condition " +
                            "specified, but no evaluation context found.");
                }

                int originalResult = evaluateCondition(evaluationContext) ?
                        EVAL_BODY_INCLUDE : SKIP_BODY;

                return (skipRoleCheck) ?
                        invertResultIfRequired(originalResult) : originalResult;

            } finally {
                cleanUp();
            }
        }

        return processResult(parentEvalResult);
    }

    private int processResult(int parentEvalResult) {
        pageContext.setAttribute(EVAL_RESULT_PAGE_ATTRIBUTE, (parentEvalResult == EVAL_BODY_INCLUDE));
        return parentEvalResult;
    }

    protected int invertResultIfRequired(int originalResult) {
        if(invertResult) {
            return (originalResult == SKIP_BODY) ?
                    EVAL_BODY_INCLUDE : SKIP_BODY;
        }

        return originalResult;
    }

    protected void cleanUp() {
    }

    private boolean evaluateCondition(Object evaluationContext) {
        return getConditionEvaluator().evaluate(condition, evaluationContext);
    }

    protected abstract ConditionEvaluator getConditionEvaluator();

    protected abstract Object createEvaluationContext();

    protected Object getApplicationBean(String beanName, Class clazz) {
        ApplicationContext appCtx =
                WebApplicationContextUtils.getWebApplicationContext(
                        pageContext.getServletContext());
        return appCtx.getBean(beanName, clazz);
    }

    protected User getCurrentUser() {
        SecurityHelper securityHelper = (SecurityHelper)
                getApplicationBean("securityHelper", SecurityHelper.class);
        return securityHelper.getLoggedInUser();
    }
}

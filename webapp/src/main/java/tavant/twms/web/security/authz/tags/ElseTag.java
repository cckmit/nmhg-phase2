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
package tavant.twms.web.security.authz.tags;

import static tavant.twms.web.security.authz.tags.AbstractConditionAwareAuthorizeTag.EVAL_RESULT_PAGE_ATTRIBUTE;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ElseTag extends TagSupport {

    public int doStartTag() throws JspException {
        boolean result = false;

        Object authzEvalResult = pageContext.getAttribute(EVAL_RESULT_PAGE_ATTRIBUTE);

        if(authzEvalResult instanceof Boolean) {
            result = ((Boolean) authzEvalResult);
            pageContext.removeAttribute(EVAL_RESULT_PAGE_ATTRIBUTE);
        }

        return result ? SKIP_BODY : EVAL_BODY_INCLUDE;
    }

}
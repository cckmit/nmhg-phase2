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
 * Time: 3:09:46 PM
 */

package tavant.twms.web.security.authz.tags;

import org.acegisecurity.taglibs.authz.AuthorizeTag;

import javax.servlet.jsp.JspException;

public class PermissionAwareAuthorizeTag extends AuthorizeTag {

    protected boolean skipRoleCheck;

    public void setSkipRoleCheck(boolean skipRoleCheck) {
        this.skipRoleCheck = skipRoleCheck;
    }

    @Override
    public int doStartTag() throws JspException {

        if (evalParentIfRequired()) {
            return SKIP_BODY;
        }

        return EVAL_BODY_INCLUDE;
    }

    private boolean evalParentIfRequired() throws JspException {
        return !skipRoleCheck && (super.doStartTag() == SKIP_BODY);
    }
}

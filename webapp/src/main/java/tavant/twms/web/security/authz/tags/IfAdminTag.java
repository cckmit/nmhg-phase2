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

import javax.servlet.jsp.JspException;

/**
 * Allows inclusion of a tag body only if the current user is a n Admin.
 * 
 */
public class IfAdminTag extends StrutsAwareAuthorizeTag {

    public int doStartTag() throws JspException {
        setIfAllGranted("admin");
        return super.doStartTag();
    }

}

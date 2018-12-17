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
 * This tag has a comma seperated attribute called roles. <br>
 * Skips the evaluation of the tag body if the logged in user is present in atleast
 * one of the roles
 * 
 * @author kannan.ekanath
 * 
 */
public class IfUserNotInRoleTag extends StrutsAwareAuthorizeTag {

	private static final long serialVersionUID = 1L;

	private String roles;

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public int doStartTag() throws JspException {
		setInvertResult(true);
		setIfAnyGranted(roles);

		return super.doStartTag();
	}

}

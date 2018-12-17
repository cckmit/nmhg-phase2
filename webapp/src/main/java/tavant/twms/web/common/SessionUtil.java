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
package tavant.twms.web.common;

import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SessionUtil {

    @SuppressWarnings("unchecked")
	public static void setLocale(Map session, Locale locale){
    	session.put(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE,locale);
    }
}

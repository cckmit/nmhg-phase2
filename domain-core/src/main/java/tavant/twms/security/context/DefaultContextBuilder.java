/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.security.context;

import org.aopalliance.intercept.MethodInvocation;

/**
 * This is the default context builder.It returns the method call argument
 * value. This should be used in places where the method parameters are not
 * VO's. The value will be {@link Long} of {@link String}.
 * 
 * @see ResourceContextBuilder
 */
public class DefaultContextBuilder implements ResourceContextBuilder {

	public Object createContext(String resourceName, Object runtimeContext) {
		if (runtimeContext instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) runtimeContext;
			if (method.getArguments().length > 0) {
				// This will be Usually accountId or userId.
				return method.getArguments()[0];
			}
		}
		return null;
	}
}

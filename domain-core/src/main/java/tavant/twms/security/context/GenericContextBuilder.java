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

import java.lang.reflect.InvocationTargetException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;


/**
 * This context builder is applicable only to the method calls involving VO's
 * which contains an the userId or accountId as an property which can be
 * retrieved by a getter method.
 * 
 */
public class GenericContextBuilder implements ResourceContextBuilder {
	private String property;
	private Logger logger = Logger.getLogger(getClass());

	public Object createContext(String resourceName, Object runtimeContext) {
		if (runtimeContext instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) runtimeContext;
			if (method.getArguments().length > 0) {
				Object vo = method.getArguments()[0];
				try {
					Object value = PropertyUtils.getNestedProperty(vo, property);
					return value;
				} catch (IllegalAccessException e) {
					logger.error("Illegal Access Exception", e);
				} catch (InvocationTargetException e) {
					logger.error("InvocationTargetException", e);
				} catch (NoSuchMethodException e) {
					logger.error("NoSuchMethodException", e);
					throw new RuntimeException("The Property " + property + " couldn't be found in the class "
							+ vo.getClass().getName(), e);
				}
			}
		}
		return null;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

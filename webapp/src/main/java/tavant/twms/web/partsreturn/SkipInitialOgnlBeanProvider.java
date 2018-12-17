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
package tavant.twms.web.partsreturn;

import tavant.twms.infra.OgnlNullSafeBeanProvider;

/**
 * The getProperty method skips the first part of the ognlExpression and 
 * apply it on the getProperty of the OgnlNullSafeBeanProvider.
 * input expression: ab.cd.ef  ---> output : cd.ef 
 * @author subin.p
 *
 */
public class SkipInitialOgnlBeanProvider extends OgnlNullSafeBeanProvider {

    public Object getProperty(String ognlExpression, Object target) {
    	return super.getProperty(skipInitialPart(ognlExpression), target);
    }
    
    private String skipInitialPart(String expression) {
    	return expression.substring(expression.indexOf(".") + 1);
    }
}

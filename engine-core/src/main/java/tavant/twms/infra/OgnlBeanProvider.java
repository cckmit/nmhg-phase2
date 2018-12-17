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
package tavant.twms.infra;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.Logger;

/**
 * @author vineeth.varghese
 * @date Aug 22, 2006
 */
public class OgnlBeanProvider implements BeanProvider {

    private static final Logger logger = Logger.getLogger(OgnlBeanProvider.class);

    /* (non-Javadoc)
     * @see tavant.twms.process.infra.BeanProvider#getProperty(java.lang.Object, java.lang.String)
     */
    public Object getProperty(String ognlExpression, Object target) {
        if (target == null || ognlExpression == null) {
            throw new IllegalArgumentException("Can't handle null passed for target["
                    + target + "] or ognlExpression[" + ognlExpression + "]");
        }
        try {
            return Ognl.getValue(ognlExpression, target) ;
        } catch (OgnlException e) {
            logger.error("Encountered OgnlException while evaluating [" + ognlExpression
                    + "] again [" + target + "]", e);
            throw new RuntimeException(e);
        }
    }

}

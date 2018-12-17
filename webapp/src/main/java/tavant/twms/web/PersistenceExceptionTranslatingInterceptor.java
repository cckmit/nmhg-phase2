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
package tavant.twms.web;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * @author radhakrishnan.j
 *
 */
public class PersistenceExceptionTranslatingInterceptor implements Interceptor {
    private static Logger logger = LogManager.getLogger(PersistenceExceptionTranslatingInterceptor.class);
    
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Action action = (Action)invocation.getAction();
        try {
            return invocation.invoke();
        } catch(DataIntegrityViolationException ex) {
            logger.error("Encountered exception ",ex);
            if( action instanceof ValidationAware ) {
                ValidationAware validationAware = (ValidationAware)action;
                validationAware.addActionError("Business object is not unique");
            }
        } catch(DataAccessException ex) {
            logger.error("Encountered exception ",ex);
            if( action instanceof ValidationAware ) {
                ValidationAware validationAware = (ValidationAware)action;
                validationAware.addActionError(ex.getMessage());
                validationAware.addActionError("This operation could not be performed due to a technical problem.");
            }
        }
        return Action.INPUT;
    }
    
}

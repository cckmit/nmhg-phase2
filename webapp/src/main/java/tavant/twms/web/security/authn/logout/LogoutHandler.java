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
 * 
 */
package tavant.twms.web.security.authn.logout;

import org.acegisecurity.Authentication;
import org.acegisecurity.ui.logout.SecurityContextLogoutHandler;
import org.apache.log4j.Logger;

import tavant.twms.security.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Security;
import java.util.List;

/**
 * An extension of Acegi's {@link SecurityContextLogoutHandler} that performs
 * warranty-specific cleanup before delegating to the parent for performing
 * the actual logout operation. Currently the cleanup process involves :
 * <br/>
 * <ul>
 * <li>Removing the Org User from Session.</li>
 * <li>Removing the User Dealership from Session.</li>
 * <li>Removing the User Locale from Session.</li>
 * <li>Removing the xml'ized User Locale from Session.</li>
 * </ul>
 * 
 * @author <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 * 
 */
public class LogoutHandler extends SecurityContextLogoutHandler {

    private List<String> sessionAttributesToBeCleared;
    private Logger logger = Logger.getLogger(LogoutHandler.class);

    public void setSessionAttributesToBeCleared(List<String> sessionAttributesToBeCleared) {
        this.sessionAttributesToBeCleared = sessionAttributesToBeCleared;
    }

    @Override
    public void logout(HttpServletRequest request, 
            HttpServletResponse response, Authentication authentication) {
 
        if(logger.isDebugEnabled()) {
            logger.debug("Processing request for User Logout...");
        }
        
        HttpSession session = request.getSession();
        

        // The logout action should remove the values in the session.
        for(String sessionAttribute : sessionAttributesToBeCleared) {
            session.removeAttribute(sessionAttribute);

            if(logger.isDebugEnabled()) {
                logger.debug("Cleared session attribute [" + sessionAttribute
                        + "]...");
            }
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Initiating Acegi Logout...");
        }        
        
        // Let the parent do the actual logout.
        super.logout(request, response, authentication);
    }

}
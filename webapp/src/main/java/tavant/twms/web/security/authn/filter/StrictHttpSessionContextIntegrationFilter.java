package tavant.twms.web.security.authn.filter;

import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.apache.log4j.Logger;
import tavant.twms.security.SecurityHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Overridden version of Acegi's {@link HttpSessionContextIntegrationFilter}. 
 * Checks if the current user is the "system user" and, if found true, rolls 
 * back the parent's behavior of overwriting the security context saved in 
 * session with the currently active one.
 * <p/>
 * Implementation Note: Ideally, the above behavior would have been in a 
 * separate method which we could override and handle accordingly. Unfortunately,
 * that is not the case with the parent. Hence, we are first letting the parent
 * have a free run, and then rolling back just the session update operation.
 * <p/>
 * <em><font color="red">This is only a temporary fix! We need to work out a 
 * proper approach for implementing the "system user" concept, rather than 
 * working around peripheral issues such as these.</font></em>
 * @author <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 *
 */
public class StrictHttpSessionContextIntegrationFilter extends
        HttpSessionContextIntegrationFilter {
    
    public StrictHttpSessionContextIntegrationFilter() throws ServletException {
		super();
	}

	private static final Logger logger = Logger.getLogger(
            StrictHttpSessionContextIntegrationFilter.class);

    private SecurityHelper securityHelper;

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpSession httpSession = ((HttpServletRequest) request).getSession();
        
        // Backup the original authentication context.
        Object orginalContext = 
            httpSession.getAttribute(ACEGI_SECURITY_CONTEXT_KEY); 
        
        try {
            // Now, let the parent do its job.
            super.doFilter(request, response, chain);
        } finally {
        	if(((HttpServletRequest) request).isRequestedSessionIdValid()){
	            Object newContext = 
	                httpSession.getAttribute(ACEGI_SECURITY_CONTEXT_KEY); 
	            // Are we working with a system user? If so, rollback the save.
	            if((newContext instanceof SecurityContext) && 
	                    (securityHelper.isSystemSecurityContext(
	                            (SecurityContext) newContext))) {
	                httpSession.setAttribute(ACEGI_SECURITY_CONTEXT_KEY, 
	                        orginalContext);
	                
	                if(logger.isDebugEnabled()) {
	                    logger.debug("Detected overwriting of authenticated security " +
	                    		"context saved in session, with the system user one. " +
	                    		"Reverting back to original Security Context : " + 
	                    		orginalContext);
	                }
	            }
        	}
        }
    }
}
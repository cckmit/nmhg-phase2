package tavant.twms.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;

import tavant.twms.security.SecurityHelper;

/*
 * This class is written to log the response time for each request.
 * The request and response times are logged separately for each request.
 * Format
 * Time - Request - uniqueIdentifier - login - sessionId - action  
 * Time - Response - uniqueIdentifier - login - sessionId - action - responseTime 
 */
public class ResponseTimeLogFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ResponseTimeLogFilter.class);
    private SecurityHelper securityHelper = new SecurityHelper();
    public static ThreadLocal<String> sessionId = new ThreadLocal<String>();

	public void destroy() {		
	}

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		String login = null;
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		
		try {
			if(securityHelper !=null && SecurityContextHolder.getContext().getAuthentication()!=null 
					&& !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String))
			{
				if(securityHelper.getLoggedInUser()!= null)
				{
					login = securityHelper.getLoggedInUser().getName();
				}
			      						
			}
		} catch (Exception e) {
			// do nothing			
		}
		
		long requestTime =0;
		long responseTime =0;
		
		if( logger.isInfoEnabled())
		{
			requestTime= new Date().getTime();
            StringBuffer sb = new StringBuffer();
            sessionId.set(request.getRequestedSessionId());

            if (request.getRequestURI().endsWith("inventorySearchBody.action") ||
                    request.getRequestURI().endsWith("claimSearchSummaryBody.action") ||
                    request.getRequestURI().endsWith("get_matching_customers_for_match_read.action") ||
                    request.getRequestURI().endsWith("get_matching_customers_for_multi_claim.action") ||
                    request.getRequestURI().endsWith("itemSearchBody.action")) {
                sb.append(" - {");
                for (Iterator iterator = request.getParameterMap().entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    sb.append(entry.getKey()).append("=");
                    String[] values = (String[]) entry.getValue();
                    if (values.length > 0) {
                        sb.append(values[0]);
                    }
                    sb.append(", ");
                }
                sb.append("}");
            }
            logger.info("Request - " + requestTime + " - " + login + " - " + request.getRequestedSessionId() + " - "
                    + request.getRequestURI() + sb.toString());
        }
		
		chain.doFilter(servletRequest, servletResponse);
		
		if(logger.isInfoEnabled())
		{
			responseTime = new Date().getTime();
            logger.info("Response - " + requestTime + " - " + login + " - " + request.getRequestedSessionId() + " - "
                    + request.getRequestURI() + " - " + (responseTime - requestTime));			
            sessionId.remove();
		}
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {			
	}
}

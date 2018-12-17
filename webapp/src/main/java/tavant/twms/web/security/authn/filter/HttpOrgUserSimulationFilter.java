package tavant.twms.web.security.authn.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import tavant.twms.security.SecurityHelper;

public class HttpOrgUserSimulationFilter implements Filter,InitializingBean {

	private SecurityHelper securityHelper;
	
	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// check if user simulation is necessary
		if (!StringUtils.isBlank(request.getParameter("TWMS_WIDGET"))) {
			
			HttpSession httpSession = ((HttpServletRequest) request)
					.getSession();
			Object originalContext = httpSession
					.getAttribute(HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);
			if (originalContext == null) {											
				// populate the context
				securityHelper.populateFocUser();
				SecurityContext sc = SecurityContextHolder.getContext();
				httpSession.setAttribute(HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY,sc);
			}
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException { }

	public void afterPropertiesSet() throws Exception { }

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	

}

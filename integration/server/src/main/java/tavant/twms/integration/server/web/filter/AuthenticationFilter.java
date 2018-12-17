/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.web.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author prasad.r
 */
public class AuthenticationFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) request;
        if(!hsr.getRequestURI().contains("authenticateUser")){
            String loggedInUser = (String) hsr.getSession().getAttribute("USER_LOGIN");
            if (loggedInUser == null || "".equals(loggedInUser.trim())) {
                hsr.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}

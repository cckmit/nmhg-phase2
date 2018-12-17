/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.filter;

import java.io.IOException;
import java.util.Calendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* A filter to set an 'Expires' header in HTTP response. This filter is mapped
 * to all static resources like jpg, js, etc. The expiry date is set to the
 * 1st of next month. This would prevent
 * browsers from checking with the server if the resource was modified on every 
 * new session thereby reducing the traffic to the website significantly.
 * {@link http://developer.yahoo.com/performance/rules.html}
 */
public class HeaderFilter implements Filter {

    private static final Pattern p = Pattern.compile("\\d+\\/scripts\\/",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest hsr = (HttpServletRequest) request;
        Matcher m = p.matcher(hsr.getRequestURI()); 
        if(m.find()){
            hsr.getRequestDispatcher(getPath(hsr.getRequestURI())).forward(request, response);
            return;
        }
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        // setting cache expire after a month
        today.set(Calendar.MONTH, today.get(Calendar.MONTH)+1);
                
        ((HttpServletResponse) response).setDateHeader("Expires", today.getTime().getTime());
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }

    private String getPath(String requestURI) {
        if(requestURI.startsWith("/"))
            requestURI = requestURI.substring(1);
        return requestURI.substring(requestURI.indexOf("/"));
    }

}

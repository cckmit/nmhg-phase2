package tavant.twms.filter;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.RequestUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author : janmejay.singh
 * Date: Aug 7, 2007
 * Time: 2:46:50 PM
 */
public class UiExtFilter implements Filter {

    public static final String STATIC_RESOURCES = "tavant/twms/uiext/resources/";

    public static final String RESOURCE_REQUEST_PREFIX = "/ui-ext";

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    private final Calendar lastModifiedCal = Calendar.getInstance();

    /**
     * This is meant to ignore certain things from getting served.
     * For example if the user wants to prevent some specific CSS files from
     * being served... u can include .css as a ignored pattern. 
     */
    private List<String> ignorePatterns = new ArrayList<String>();

    private boolean isResourceIgnored(String resourceName) {
        for(String s: ignorePatterns) {
            if(resourceName.indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        String ignorePatternCSV = filterConfig.getInitParameter("ignore-patterns");
        if(ignorePatternCSV != null && !"".equals(ignorePatternCSV)) {
            ignorePatterns = Arrays.asList(ignorePatternCSV.split(","));
        }
        
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String resourcePath = RequestUtils.getServletPath(request);
        if ("".equals(resourcePath) && null != request.getPathInfo()) {
            resourcePath = request.getPathInfo();
        }

        if (resourcePath.startsWith(RESOURCE_REQUEST_PREFIX)) {
            String name = resourcePath.substring(RESOURCE_REQUEST_PREFIX.length());
            if(isResourceIgnored(name)) {
                //user has explicitly asked not to load these files from the jar
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                findStaticResource(name, request, response);
            }
        }
    }

    /**
     * Locate a static resource and copy directly to the response,
     * setting the appropriate caching headers.
     *
     * @param name The resource name
     * @param request The request
     * @param response The response
     * @throws IOException If anything goes wrong
     */
    protected void findStaticResource(String name, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!name.endsWith(".class")) {
            InputStream is = findInputStream(name, STATIC_RESOURCES);
            if (is != null) {
                Calendar cal = Calendar.getInstance();

                // check for if-modified-since, prior to any other headers
                long ifModifiedSince = 0;
                try {
                    ifModifiedSince = request.getDateHeader("If-Modified-Since");
                } catch (Exception e) {
                    //doing nothing .... just ignore
                }
                long lastModifiedMillis = lastModifiedCal.getTimeInMillis();
                long now = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                long expires = cal.getTimeInMillis();

                if (ifModifiedSince > 0 && ifModifiedSince <= lastModifiedMillis) {
                    // not modified, content is not sent - only basic headers and status SC_NOT_MODIFIED
                    response.setDateHeader("Expires", expires);
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    is.close();
                    return;
                }

                // set the content-type header
                String contentType = getContentType(name);
                if (contentType != null) {
                    response.setContentType(contentType);
                }

                //if (serveStaticBrowserCache) {
                //TODO: this shd be picked up from a property file... put me in a if
                // set heading information for caching static content
                response.setDateHeader("Date", now);
                response.setDateHeader("Expires", expires);
                response.setDateHeader("Retry-After", expires);
                response.setHeader("Cache-Control", "public");
                response.setDateHeader("Last-Modified", lastModifiedMillis);
                //} else {
//                        response.setHeader("Cache-Control", "no-cache");
//                        response.setHeader("Pragma", "no-cache");
//                        response.setHeader("Expires", "-1");
                //}

                try {
                    copy(is, response.getOutputStream());
                } finally {
                    is.close();
                }
                return;
            }
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Copy bytes from the input stream to the output stream.
     *
     * @param input The input stream
     * @param output The output stream
     * @throws IOException If anything goes wrong
     */
    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * Look for a static resource in the classpath.
     *
     * @param name The resource name
     * @param packagePrefix The package prefix to use to locate the resource
     * @return The inputstream of the resource
     * @throws IOException If there is a problem locating the resource
     */
    protected InputStream findInputStream(String name, String packagePrefix) throws IOException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        resourcePath = URLDecoder.decode(resourcePath, "UTF-8");//TODO: FIX ME!!! pick me from the property file

        return ClassLoaderUtil.getResourceAsStream(resourcePath, getClass());
    }

    /**
     * Determine the content type for the resource name.
     *
     * @param name The resource name
     * @return The mime type
     */
    protected String getContentType(String name) {
        if (name.endsWith(".js")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    public void destroy() {}
}

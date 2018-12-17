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
package tavant.twms.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import java.util.Map;
import java.util.Random;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import tavant.twms.security.model.OrgAwareUserDetails;

/**
 * Interceptor to log all exceptions which result in the user being displayed a system error page.
 * Logs login id, action name, session id and request paramters in addition to the exception stack trace. 
 */
public class ExceptionLoggingInterceptor extends ExceptionMappingInterceptor {

    private static final Logger log = Logger.getLogger(ExceptionLoggingInterceptor.class);
    private boolean logRequestParameters = true;

    @Override
    public void init() {
        super.init();
        String property = System.getProperty("logRequestParameters");
        if (property != null && "false".equalsIgnoreCase(property)) {
            logRequestParameters = false;
        }
    }

    @Override
    protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {
        String userName = getUserName();
        Throwable cause = getCause(exceptionHolder.getException());
        String exceptionMessage = getExceptionMessage(cause);
        Exception e = new Exception(exceptionMessage);
        e.setStackTrace(cause.getStackTrace());
        ExceptionHolder eh = new ExceptionHolder(e);
        super.publishException(invocation, eh);
        log.error(userName + " - "
                + invocation.getProxy().getActionName() + " - "
                + getRequestParameters(invocation.getInvocationContext().getParameters()) + " - "
                + eh.getException().getMessage(),
                eh.getException());
    }

    private String getRequestParameters(Map parameters) {
        if (!logRequestParameters) {
            return "";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Object o : parameters.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String name = (String) entry.getKey();
            sb.append(name).append("=");
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String[]) {
                    String[] values = (String[]) entry.getValue();
                    for (String value : values) {
                        sb.append(value);
                        if (values.length > 1) {
                            sb.append(";");
                        }
                    }
                } else {
                    sb.append(entry.getValue());
                }
            }
            sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    private String getUserName() {
        String userName = "NOT LOGGED IN";
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if(authentication.getPrincipal() instanceof OrgAwareUserDetails)
                userName = ((OrgAwareUserDetails)authentication.getPrincipal()).getUsername();
        }
        return userName;
    }

    private Throwable getCause(Throwable t) {
        return t.getCause() != null ? getCause(t.getCause()) : t;
    }

    private String getExceptionMessage(Throwable t) {
        return "Exception Identifier : " + new Random(System.currentTimeMillis()).nextLong() + ", " + t;
    }
}

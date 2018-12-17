package tavant.twms.annotations.form.handler;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * User: Janmejay.singh
 * Date: Aug 1, 2007
 * Time: 5:46:34 PM
 */
public abstract class StrutsActionElementGenerator implements ElementGenerator {
    
    protected String className;
    protected String methodName;

    public StrutsActionElementGenerator(final String actionClassName, final String methodName) {
        className = actionClassName;
        this.methodName = methodName;
    }

    protected Element getActionNode(Document document, String actionName, String convertorName) {
        Element action = document.createElement(ACTION_TAG_NAME);
        action.setAttribute(ACTION_NAME, actionName);
        action.setAttribute(ACTION_CLASS, className);
        action.setAttribute(ACTION_METHOD, methodName);
        if (convertorName != null && !convertorName.equals("")) action.setAttribute(ACTION_CONVERTER, convertorName);
        return action;
    }

    protected Element getInterceptorRefNode(Document document, String interceptorRefName) {
        Element interceptorRef = document.createElement(INTERCEPTOR_REF_TAG_NAME);
        interceptorRef.setAttribute(INTERCEPTOR_REF_NAME, interceptorRefName);
        return interceptorRef;
    }
}

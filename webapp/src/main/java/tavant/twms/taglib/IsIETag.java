package tavant.twms.taglib;

import org.apache.struts2.views.jsp.ComponentTagSupport;
import org.apache.struts2.components.Component;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see IsIE
 * 
 * @author janmejay.singh
 * Date: Aug 10, 2007
 * Time: 2:42:01 PM
 */

@SuppressWarnings("serial")
public class IsIETag extends ComponentTagSupport {

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new IsIE(stack, req);
    }

}


package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author janmejay.singh
 * Date: Apr 18, 2007
 * Time: 12:53:12 PM
 */
public class LoopTag extends ComponentTagSupport {

    private String repeat;
    private String status;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new Loop(valueStack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Loop loop = (Loop) component;
        loop.setStatus(status);
        loop.setRepeat(repeat);
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    @Override
    public int doAfterBody() throws JspException {
        if (component.end(pageContext.getOut(), getBody())) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            return SKIP_BODY;
        }
    }
}

package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : May 11, 2007
 * Time : 12:42:43 AM
 */
public class BodyTag extends AbstractClosingTag {

    private String shownWithinTab;
    private String smudgeAlert;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse) {
        return new Body(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Body body = (Body) component;
        body.setSmudgeAlert(smudgeAlert);
    }

    public void setSmudgeAlert(String smudgeAlert) {
        this.smudgeAlert = smudgeAlert;
    }
}

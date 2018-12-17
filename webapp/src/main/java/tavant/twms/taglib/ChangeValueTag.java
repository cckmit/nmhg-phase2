package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ChangeValue
 *
 * @author janmejay.singh
 */
public class ChangeValueTag extends AbstractUITag {

    private String ofId;
    private String on;
    private String to;
    private String ifTargetVal;
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new ChangeValue(stack, request, response);
    }

    protected void populateParams() {
        super.populateParams();
        ChangeValue changeVal = (ChangeValue) component;
        changeVal.setOfId(ofId);
        changeVal.setTo(to);
        changeVal.setOn(on);
        changeVal.setIfTargetVal(ifTargetVal);
    }

    public String getOfId() {
        return ofId;
    }

    public void setOfId(String ofId) {
        this.ofId = ofId;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getIfTargetVal() {
        return ifTargetVal;
    }

    public void setIfTargetVal(String ifTargetVal) {
        this.ifTargetVal = ifTargetVal;
    }
}

package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Publish
 *
 * @author janmejay.singh
 */
public class PublishTag extends AbstractUITag {

    private String publish;
    private String on;
    private String ifTargetVal;
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Publish(stack, request, response);
    }

    protected void populateParams() {
        super.populateParams();
        Publish changeVal = (Publish) component;
        changeVal.setPublish(publish);
        changeVal.setOn(on);
        changeVal.setIfTargetVal(ifTargetVal);
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String ofId) {
        this.publish = ofId;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getIfTargetVal() {
        return ifTargetVal;
    }

    public void setIfTargetVal(String ifTargetVal) {
        this.ifTargetVal = ifTargetVal;
    }
}

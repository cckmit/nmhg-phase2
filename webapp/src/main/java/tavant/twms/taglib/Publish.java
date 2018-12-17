package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Publishes given event, on another event getting published, and if the event.target.value is equal to a given ifTargetVal.</li>
 * </ul>
 *
 * @author janmejay.singh
 */
public class Publish extends UIBean {

    private boolean tagWasUsedBefore;
    
    private String publish;
    private String on;
    private String ifTargetVal;
    
    public static final String TEMPLATE = "twms_publish";

    public Publish(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("publish", publish);
        addParameter("on", on);
        addParameter("ifTargetVal", ifTargetVal);
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
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

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }
}

package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Generates some javascript which changes the value of an input field to a given value on a particular event.</li>
 * <li>Can optionally check the event.target.value, and change the field value, only if event.target.value is equal to given value.</li> 
 * </ul>
 *
 * @author janmejay.singh
 */
public class ChangeValue extends UIBean {

    private boolean tagWasUsedBefore;
    
    private String ofId;
    private String on;
    private String to;
    private String ifTargetVal;
    
    public static final String TEMPLATE = "twms_changeValue";

    public ChangeValue(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("ofId", ofId);
        addParameter("on", on);
        addParameter("to", to);
        if(ifTargetVal != null) {
            addParameter("ifTargetVal", ifTargetVal);
        }
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
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

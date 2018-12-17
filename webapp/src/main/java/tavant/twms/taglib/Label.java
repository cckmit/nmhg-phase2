package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Creates a label for an element.
 * 
 * @author janmejay.singh
 */
public class Label extends UIBean {
    
    private String forId;
    private String value;
    
    public static final String TEMPLATE = "twms_label";

    public Label(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("for", forId);
        addParameter("value", value);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public String getForId() {
        return forId;
    }

    public void setForId(String forId) {
        this.forId = forId;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
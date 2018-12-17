package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a HTML button, with optional 'type' attribute.</li> 
 * </ul>
 *
 * @author janmejay.singh
 */
public class Button extends InputComponent {
    
    public static final String TEMPLATE = "twms_button";
    
    private String type;
    private String publishOnClick;

    public Button(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(type != null) {
            addParameter("type", type);
        }
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublishOnClick() {
        return publishOnClick;
    }

    public void setPublishOnClick(String publishOnClick) {
        this.publishOnClick = publishOnClick;
    }
}

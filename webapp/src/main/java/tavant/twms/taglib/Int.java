package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a HTML input that can be used for entering integers.(Has mouse wheel support)</li>
 * <li>Can have max/min limits, and seprator(example... '10,569' here comma is the separator)(optional).</li>
 * <li>It can publish an event on change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="autoComplete" tld-body-content="JSP" description="AutoComplete tag" tld-tag-class="tavant.twms.taglib.AutoCompleteTag"
 */
public class Int extends InputComponent {
    
    public static final String TEMPLATE = "twms_int";
    
    private String maxLimit;
    private String minLimit;
    private String separator;

    public Int(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(maxLimit != null) {
            addParameter("maxLimit", findValue(maxLimit, Integer.class));
        }
        if(minLimit != null) {
            addParameter("minLimit", findValue(minLimit, Integer.class));
        }
        if(separator != null) {
            addParameter("separator", findString(separator));
        }
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(String minLimit) {
        this.minLimit = minLimit;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}

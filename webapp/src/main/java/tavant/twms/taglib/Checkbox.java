package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a HTML checkbox.</li> 
 * <li>It can publish an event on change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="checkbox" tld-body-content="empty" description="Checkbox tag" tld-tag-class="tavant.twms.taglib.CheckboxTag"
 */
public class Checkbox extends InputComponent {
    
    public static final String TEMPLATE = "twms_checkbox";
    
    public Checkbox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

}

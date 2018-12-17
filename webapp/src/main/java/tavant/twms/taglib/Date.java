package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a dojo date input component.</li>
 * <li>It can publish an event on the change(optional).</li>
 * <li>It can execute a function when changed(optional).</li> 
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="date" tld-body-content="empty" description="Date tag" tld-tag-class="tavant.twms.taglib.DateTag"
 */
public class Date extends Text {
    
    public static final String TEMPLATE = "twms_date";

    public Date(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}

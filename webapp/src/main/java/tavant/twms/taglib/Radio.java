package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <ul>
 * <li>Creates a bunch of HTML radio buttons.</li> 
 * <li>It can publish an event on change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="radio" tld-body-content="empty" description="Radio tag" tld-tag-class="tavant.twms.taglib.RadioTag"
 */
public class Radio extends InputComponent {
    
    public static final String TEMPLATE = "twms_radio";
    public String list;
    public String listLabel;
    public String listValue;

    public Radio(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("list", findValue(list, List.class));
        addParameter("listLabel", listLabel);
        addParameter("listValue", listValue);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getListLabel() {
        return listLabel;
    }

    public void setListLabel(String listLabel) {
        this.listLabel = listLabel;
    }

    public String getListValue() {
        return listValue;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}

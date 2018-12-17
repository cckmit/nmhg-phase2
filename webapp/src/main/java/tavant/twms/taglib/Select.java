package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <ul>
 * <li>Creates a dojo combobox, which behaves like a select element.</li>
 * <li>It can publish an event on the change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * <li>It can have autosuggest function(optional).</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="select" tld-body-content="empty" description="Select tag" tld-tag-class="tavant.twms.taglib.SelectTag"
 */
public class Select extends AutoSuggestable {
    
    public static final String TEMPLATE = "twms_select";
    
    private String list;
    private String listKey;
    private String listValue;
    private String buttonImageUri;
    
    public Select(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        validate();
        if(!isAutoSuggestEnabled()) {
            addParameter("list", findValue(list, List.class));
            addParameter("listKey", findString(listKey));
            addParameter("listValue", findString(listValue));
        }
        if(buttonImageUri != null) {
            addParameter("buttonImageUri", buttonImageUri);
        }
        if(getMaxListLength() != null && !isAutoSuggestEnabled()) {
            //if auto suggest is enabled... the super class will take care of setting the parameter
            addParameter("maxListLength", getMaxListLength());
        } else {
            addParameter("maxListLength", String.valueOf(10));
        }
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    private void validate() {
        if(!isAutoSuggestEnabled()) {
            if(list == null) {
                throw new IllegalArgumentException("'list' attribute is mandatory in 'select' tag, if autoSugggest(attribute) is not set to true.");
            }
            if(listKey == null) {
                throw new IllegalArgumentException("'listKey' attribute is mandatory in 'select' tag, if autoSugggest(attribute) is not set to true.");
            }
            if(listValue == null) {
                throw new IllegalArgumentException("'listValue' attribute is mandatory in 'select' tag, if autoSugggest(attribute) is not set to true.");
            }
        }
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getListKey() {
        return listKey;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public String getListValue() {
        return listValue;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
    
    public String getButtonImageUri() {
        return buttonImageUri;
    }

    public void setButtonImageUri(String buttonImageUri) {
        this.buttonImageUri = buttonImageUri;
    }
}

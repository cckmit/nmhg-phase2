package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a HTML input(text).</li> 
 * <li>It can publish an event on change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * <li>It can have autosuggest function(optional).</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="text" tld-body-content="empty" description="Text tag" tld-tag-class="tavant.twms.taglib.TextTag"
 */
public class Text extends AutoSuggestable {
    
    public static final String TEMPLATE = "twms_text";
    
    private String maxLength;
    private String size;

    public Text(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        validate();
        if(maxLength != null) {
            addParameter("maxLength", maxLength);
        }
        if(size != null) {
            addParameter("size", size);
        }
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    private void validate() {
        if(isAutoSuggestEnabled()) {
           if(maxLength != null) {
               throw new IllegalArgumentException("'maxlength' attribute is illegal in 'text' tag, when autoSuggest(attribute) is set to true.");
           }
           if(size != null) {
               throw new IllegalArgumentException("'size' attribute is illegal in 'text' tag, when autoSuggest(attribute) is set to true.");
           }
        }
    }
    
    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}

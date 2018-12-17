package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Text
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class TextTag extends AutoSuggestableTag {

    private String size;
    private String maxLength;
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Text(stack, request, response);
    }
    
    protected void populateParams() {
        super.populateParams();
        Text field = (Text) component;
        field.setMaxLength(maxLength);
        field.setSize(size);
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

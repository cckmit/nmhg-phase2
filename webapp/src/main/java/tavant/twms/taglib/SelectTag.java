package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Select
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class SelectTag extends AutoSuggestableTag {

    private String maxListLength;
    private String list;
    private String listKey;
    private String listValue;
    private String buttonImageUri;
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Select(stack, request, response);
    }
    
    protected void populateParams() {
        super.populateParams();
        Select field = (Select) component;
        field.setMaxListLength(maxListLength);
        field.setList(list);
        field.setListKey(listKey);
        field.setListValue(listValue);
        field.setButtonImageUri(buttonImageUri);
    }

    public String getMaxListLength() {
        return maxListLength;
    }

    public void setMaxListLength(String maxListLength) {
        this.maxListLength = maxListLength;
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

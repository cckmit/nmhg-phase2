package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Label
 * 
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class LabelTag extends AbstractUITag {
    
    private String forId;
    private String value;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Label(stack, request, response);
    }
    
    @Override
    public void populateParams() {
        super.populateParams();
        Label label = (Label) component;
        label.setForId(forId);
        label.setValue(value);
    }

    public String getFor() {
        return forId;
    }

    public void setFor(String forId) {
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

package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Button
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class ButtonTag extends InputComponentTag {
    
    private String publishOnClick;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Button(stack, request, response);
    }

    public String getPublishOnClick() {
        return publishOnClick;
    }

    public void setPublishOnClick(String publishOnClick) {
        this.publishOnClick = publishOnClick;
    }
}

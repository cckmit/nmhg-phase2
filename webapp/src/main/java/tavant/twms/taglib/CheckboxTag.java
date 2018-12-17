package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Checkbox
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class CheckboxTag extends InputComponentTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Checkbox(stack, request, response);
    }
}

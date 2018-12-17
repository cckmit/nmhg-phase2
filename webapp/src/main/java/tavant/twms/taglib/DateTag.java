package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Date
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class DateTag extends TextTag {
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Date(stack, request, response);
    }
}

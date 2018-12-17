package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see RepeatDelete 
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class RepeatDeleteTag extends AbstractClosingTag {

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new RepeatDelete(stack, req, res);
    }
}

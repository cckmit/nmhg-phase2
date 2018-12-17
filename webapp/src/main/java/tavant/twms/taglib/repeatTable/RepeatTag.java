package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see tavant.twms.taglib.repeatTable.Repeat
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class RepeatTag extends AbstractClosingTag {
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Repeat(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Repeat repeat = (Repeat) component;
        repeat.setId(id);
        repeat.setCellpadding(cellpadding);
        repeat.setCellspacing(cellspacing);
        repeat.setWidth(width);
    }

    private String cellpadding;
    private String cellspacing;
    private String width;

    public void setCellpadding(String cellpadding) {
        this.cellpadding = cellpadding;
    }

    public void setCellspacing(String cellspacing) {
        this.cellspacing = cellspacing;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}

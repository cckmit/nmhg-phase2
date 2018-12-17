package tavant.twms.taglib.summaryTable;

import org.apache.struts2.views.jsp.ui.AbstractUITag;
import org.apache.struts2.components.Component;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Button
 * @author janmejay.singh
 */
public class ButtonTag extends AbstractUITag {

    private String align;
    private String disabled;
    private String summaryTableId;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new Button(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Button button = (Button) component;
        button.setAlign(align);
        button.setDisabled(disabled);
        button.setSummaryTableId(summaryTableId);
    }

    public void setSummaryTableId(String summaryTableId) {
        this.summaryTableId = summaryTableId;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    @Override
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }
}

package tavant.twms.taglib.treeTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : Jun 12, 2007
 * Time : 12:13:44 PM
 */
public class AddButtonTag extends AbstractClosingTag {

    private String rowType;
    private String identifierCssClass;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new AddButton(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        AddButton button = (AddButton) component;
        button.setRowType(rowType);
        button.setIdentifierCssClass(identifierCssClass);
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public void setIdentifierCssClass(String identifierCssClass) {
        this.identifierCssClass = identifierCssClass;
    }
}

package tavant.twms.taglib.treeTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : Jun 12, 2007
 * Time : 12:13:07 PM
 */
public class AddButton extends ClosingUIBean {

    public static final String TEMPLATE = "treeAddButton-close",
                               OPEN_TEMPLATE = "treeAddButton";

    private String rowType;
    private String identifierCssClass;

    protected AddButton(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("rowType", findString(rowType));
        addParameter("identifierCssClass", findString(identifierCssClass));
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public void setIdentifierCssClass(String identifierCssClass) {
        this.identifierCssClass = identifierCssClass;
    }
}


package tavant.twms.taglib.treeTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : Jun 12, 2007
 * Time : 12:14:31 PM
 */
public class DropButtonTag extends AbstractClosingTag {

    private String identifierCssClass;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new DropButton(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        DropButton button = (DropButton) component;
        button.setIdentifierCssClass(identifierCssClass);
    }

    public void setIdentifierCssClass(String identifierCssClass) {
        this.identifierCssClass = identifierCssClass;
    }
}

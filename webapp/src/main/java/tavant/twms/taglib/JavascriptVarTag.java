package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see JavascriptVar
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class JavascriptVarTag extends AbstractClosingTag {

    private String varName;
    private String ajaxMode = Boolean.FALSE.toString();

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new JavascriptVar(stack, request, response);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        JavascriptVar var = (JavascriptVar) component;
        var.setVarName(varName);
        var.setAjaxMode(ajaxMode);
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setAjaxMode(String ajaxMode) {
        this.ajaxMode = ajaxMode;
    }
}
package tavant.twms.taglib.nlist;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class NListTag extends AbstractClosingTag {

    private String rowTemplateUrl;
    private String paramsVar;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        return new NList(stack, request, response);
    }

    @Override
    public void populateParams() {
        super.populateParams();
        NList nList = (NList) component;

        nList.setRowTemplateUrl(rowTemplateUrl);
        nList.setParamsVar(paramsVar);
    }

    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    public int doAfterBody() throws JspException {
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN; // causes this method to get invoked again.
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            
            return SKIP_BODY;
        }
    }    

    public void setRowTemplateUrl(String rowTemplateUrl) {
        this.rowTemplateUrl = rowTemplateUrl;
    }

    public void setParamsVar(String paramsVar) {
        this.paramsVar = paramsVar;
    }

}

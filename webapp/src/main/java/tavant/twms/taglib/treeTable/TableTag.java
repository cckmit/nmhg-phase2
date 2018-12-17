package tavant.twms.taglib.treeTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : Jun 12, 2007
 * Time : 12:06:47 PM
 */
public class TableTag extends AbstractUITag {

    private String loadOn;
    private String serializeOn;
    private String returnBy;
    private String onValidationErrors;
    private String onTreeRendered;
	private String nodeAgent;
    private String indentCssClass;
    private String rootRowClass;
    private String headTemplateVar;
    private String headCssClass;
    private String bodyCssClass;
    private String publishOnInstantiation;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new Table(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Table table = (Table) component;
        table.setLoadOn(loadOn);
        table.setSerializeOn(serializeOn);
        table.setReturnBy(returnBy);
        table.setOnValidationErrors(onValidationErrors);
        table.setOnTreeRendered(onTreeRendered);
        table.setNodeAgent(nodeAgent);
        table.setIndentCssClass(indentCssClass);
        table.setRootRowClass(rootRowClass);
        table.setHeadTemplateVar(headTemplateVar);
        table.setHeadCssClass(headCssClass);
        table.setBodyCssClass(bodyCssClass);
        table.setPublishOnInstantiation(publishOnInstantiation);
    }

    public void setLoadOn(String loadOn) {
        this.loadOn = loadOn;
    }

    public void setSerializeOn(String serializeOn) {
        this.serializeOn = serializeOn;
    }

    public void setPublishOnInstantiation(String publishOnInstantiation) {
        this.publishOnInstantiation = publishOnInstantiation;
    }

    public void setReturnBy(String returnBy) {
        this.returnBy = returnBy;
    }

    public void setOnValidationErrors(String onValidationErrors) {
        this.onValidationErrors = onValidationErrors;
    }

	public void setOnTreeRendered(String onTreeRendered) {
		this.onTreeRendered = onTreeRendered;
	}
    
    public void setNodeAgent(String nodeAgent) {
        this.nodeAgent = nodeAgent;
    }

    public void setIndentCssClass(String indentCssClass) {
        this.indentCssClass = indentCssClass;
    }

    public void setRootRowClass(String rootRowClass) {
        this.rootRowClass = rootRowClass;
    }

    public void setHeadTemplateVar(String headTemplateVar) {
        this.headTemplateVar = headTemplateVar;
    }

    public void setBodyCssClass(String bodyCssClass) {
        this.bodyCssClass = bodyCssClass;
    }

    public void setHeadCssClass(String headCssClass) {
        this.headCssClass = headCssClass;
    }
}

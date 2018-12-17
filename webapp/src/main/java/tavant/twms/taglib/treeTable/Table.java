package tavant.twms.taglib.treeTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.isUsedBefore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 * Date : Jun 12, 2007
 * Time : 12:07:24 PM
 */
public class Table extends UIBean {

    public static final String TEMPLATE = "treeTable";

    private String loadOn;
    private String serializeOn;
    private String returnBy;
    private String onValidationErrors;
    private String onTreeRendered;
	private String indentCssClass;
    private String nodeAgent;
    private String rootRowClass;
    private String headTemplateVar;
    private String headCssClass;
    private String bodyCssClass;
    private String publishOnInstantiation;

    protected Table(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("tagWasUsedBefore", isUsedBefore(request, this.getClass()));
        addParameter("loadOn", findString(loadOn));
        addParameter("nodeAgent", findString(nodeAgent));
        addParameter("headTemplateVar", findString(headTemplateVar));
        addParameter("rootRowClass",
                rootRowClass != null ? findString(rootRowClass) : "tavant.twms.treeTable.RootRow");
        addParameter("indentCssClass",
                indentCssClass != null ? findString(indentCssClass) : "indentable");
        if (serializeOn != null) {
            addParameter("serializeOn", findString(serializeOn));
        }
        if (returnBy != null) {
            addParameter("returnBy", findString(returnBy));
        }
        if (onValidationErrors != null) {
            addParameter("onValidationErrors", findString(onValidationErrors));
        }
        if (onTreeRendered != null) {
            addParameter("onTreeRendered", findString(onTreeRendered));
        }
        if (headCssClass != null) {
            addParameter("headCssClass", findString(headCssClass));
        }
        if (bodyCssClass != null) {
            addParameter("bodyCssClass", findString(bodyCssClass));
        }
        if (publishOnInstantiation != null) {
            addParameter("publishOnInstantiation", findString(publishOnInstantiation));
        }
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setLoadOn(String loadOn) {
        this.loadOn = loadOn;
    }

    public void setSerializeOn(String serializeOn) {
        this.serializeOn = serializeOn;
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

    public void setIndentCssClass(String indentCssClass) {
        this.indentCssClass = indentCssClass;
    }

    public void setNodeAgent(String nodeAgent) {
        this.nodeAgent = nodeAgent;
    }

    public void setRootRowClass(String rootRowClass) {
        this.rootRowClass = rootRowClass;
    }

    public void setHeadTemplateVar(String headTemplateVar) {
        this.headTemplateVar = headTemplateVar;
    }

    public void setHeadCssClass(String tHeadCssClass) {
        this.headCssClass = tHeadCssClass;
    }

    public void setBodyCssClass(String tBodyCssClass) {
        this.bodyCssClass = tBodyCssClass;
    }

    public void setPublishOnInstantiation(String publishOnInstantiation) {
        this.publishOnInstantiation = publishOnInstantiation;
    }
}

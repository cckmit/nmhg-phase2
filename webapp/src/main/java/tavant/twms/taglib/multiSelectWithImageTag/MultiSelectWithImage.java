package tavant.twms.taglib.multiSelectWithImageTag;

import org.apache.struts2.components.ListUIBean;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.util.ValueStack;


/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Apr 1, 2010
 * Time: 7:06:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultiSelectWithImage extends ListUIBean {
    final public static String TEMPLATE = "multiSelectWithImage";
    private String action;
    private String property;
    private String inputType;
    private String docId;


    public MultiSelectWithImage(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.action != null) {
            addParameter("action", findString(this.action));
        }
        if (this.property != null) {
            addParameter("property", findString(this.property));
        }
        if (this.inputType != null) {
            addParameter("inputType", findString(this.inputType));
        }
        if (this.docId != null) {
            addParameter("docId", findString(this.docId));
        }
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}

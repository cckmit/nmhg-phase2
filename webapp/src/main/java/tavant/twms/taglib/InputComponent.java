package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>This class is extended by all the tags, that build any HTML Form component.</li> 
 * </ul>
 *
 * @author janmejay.singh
 */
public abstract class InputComponent extends UIBean {

    private String readOnly;
    private String dependsOn;//comma seprated ids of fields that this tag will listen to.
    private String readOnlyCssClass;
    private String publishOnChange;

    protected boolean hasForm;
    protected boolean tagWasUsedBefore;
    protected boolean parentFormReadOnly;

    public InputComponent(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
        this.readOnly = Boolean.toString(false);
        this.parentFormReadOnly = false;//is case of no form, this shd be false
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("hasForm", hasForm);
        if(readOnly != null) {
            addParameter("readOnly", findValue(readOnly, Boolean.class));
        }
        if(publishOnChange != null) {
            addParameter("publishOnChange", findString(publishOnChange));
        }
        if(readOnlyCssClass != null) {
            addParameter("readOnlyCssClass", readOnlyCssClass);
        }
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        addParameter("isIE", TaglibUtil.isIE(request));
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getReadOnlyCssClass() {
        return readOnlyCssClass;
    }

    public void setReadOnlyCssClass(String readOnlyCssClass) {
        this.readOnlyCssClass = readOnlyCssClass;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getPublishOnChange() {
        return publishOnChange;
    }

    public void setPublishOnChange(String publishOnChange) {
        this.publishOnChange = publishOnChange;
    }
}

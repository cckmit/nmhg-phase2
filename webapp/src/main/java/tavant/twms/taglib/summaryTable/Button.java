package tavant.twms.taglib.summaryTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.getBoolean;
import static tavant.twms.taglib.TaglibUtil.geti18NVal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author janmejay.singh
 */
public class Button extends UIBean {

    private String align;
    private String disabled;
    private String  summaryTableId;

    private static final String TEMPLATE = "summaryTableButton";
    
    public Button(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("align", (align != null) ? align : "left");
        addParameter("disabled", getBoolean(findValue(this.disabled)));
        addParameter("label", geti18NVal(getStack(), findString(label)));
        addParameter("summaryTableId", summaryTableId);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setSummaryTableId(String summaryTableId) {
        this.summaryTableId = summaryTableId;
    }

    @Override
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }
}

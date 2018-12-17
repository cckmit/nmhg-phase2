package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.isUsedBefore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 *         Date : Jun 5, 2007
 *         Time : 4:39:30 PM
 */
public class DynamicAccordionSection extends UIBean  {

    private static final String TEMPLATE = "twms_dynamic_accordion_section";

    private String fetchOn;//event on which it should fetch it
    private String appendMode;//(boolean)should only append or shd refresh the entire thingy
    private String fetchFrom;//what url to ping???
    
    public DynamicAccordionSection(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("fetchOn", findString(fetchOn));
        if(appendMode != null) {
            addParameter("appendMode", findValue(appendMode, Boolean.class));
        }
        addParameter("fetchFrom", findString(fetchFrom));
        addParameter("tagWasUsedBefore", isUsedBefore(request, this.getClass()));
    }

    public void setFetchOn(String fetchOn) {
        this.fetchOn = fetchOn;
    }

    public void setAppendMode(String appendMode) {
        this.appendMode = appendMode;
    }

    public void setFetchFrom(String fetchFrom) {
        this.fetchFrom = fetchFrom;
    }
}

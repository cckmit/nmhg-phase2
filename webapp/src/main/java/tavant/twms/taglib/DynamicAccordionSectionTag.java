package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 *         Date : Jun 5, 2007
 *         Time : 7:07:07 PM
 */
public class DynamicAccordionSectionTag extends AbstractUITag {

    private String fetchOn;
    private String appendMode;
    private String fetchFrom;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new DynamicAccordionSection(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        DynamicAccordionSection section = (DynamicAccordionSection) component;
        section.setFetchFrom(fetchFrom);
        section.setFetchOn(fetchOn);
        section.setAppendMode(appendMode != null ? appendMode : Boolean.FALSE.toString());
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

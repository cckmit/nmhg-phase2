package tavant.twms.taglib.domainAware;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 *         Date : May 18, 2007
 *         Time : 12:26:12 PM
 */
@SuppressWarnings("serial")
public class LovTag extends AbstractUITag {
    private String className;
    private String businessUnitName;

    public void setClassName(String className) {
        this.className = className;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new Lov(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Lov lov = (Lov) component;
        lov.setClassName(className);
        lov.setBusinessUnitName(businessUnitName);
    }
}

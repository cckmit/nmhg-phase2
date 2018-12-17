package tavant.twms.taglib.domainAware;

import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts2.components.UIBean;
import org.springframework.util.StringUtils;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.taglib.TaglibUtil;

/**
 * @author : janmejay.singh
 *         Date : May 18, 2007
 *         Time : 12:26:54 PM
 */
public class Lov extends UIBean {

    public static final String TEMPLATE = "twms_lov";

    private String className;

    private String businessUnitName;

    private LovRepository lovRepository;

    Logger logger = Logger.getLogger(this.getClass());

    public Lov(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
        lovRepository = fetchLovRepository();
        assert lovRepository != null;
        setTheme("simple");
    }

    private LovRepository fetchLovRepository() {
        BeanLocator beanLocator = new BeanLocator();
        return (LovRepository) beanLocator.lookupBean("lovRepository");
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(StringUtils.hasText(businessUnitName)){
            businessUnitName = (String) getStack().findValue(businessUnitName);
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnitName);
        }
        List<ListOfValues> listOfvalues = null;
        listOfvalues =  lovRepository.findAllActive(findString(className));
        addParameter("lovList", listOfvalues);
        addParameter("I18nSelect",new DefaultTextProvider().getText("label.common.selectHeader"));
        addParameter("tagWasUsedBefore", TaglibUtil.isUsedBefore(request, this.getClass()));
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }
}

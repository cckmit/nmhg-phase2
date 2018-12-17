package tavant.twms.interceptor;

import org.springframework.util.StringUtils;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

@SuppressWarnings("serial")
public class WarrantyAdminBusinessUnitInterceptor extends AbstractInterceptor {
	@Override
	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation invocation) throws Exception {		
		String selectedBu = new SecurityHelper().getWarrantyAdminBusinessUnit();		
		if (selectedBu != null && StringUtils.hasText(selectedBu)) {
				SelectedBusinessUnitsHolder
						.setSelectedBusinessUnit(selectedBu);
			} else {
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(null);
			}
		return invocation.invoke();
	}
}

package tavant.twms.interceptor;


import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


@SuppressWarnings("serial")
public class BusinessUnitInterceptor extends AbstractInterceptor {
	@Override
	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation invocation) throws Exception {		
		BusinessUnit selectedBu = new SecurityHelper().getDefaultBusinessUnit();		
		if (selectedBu != null && StringUtils.hasText(selectedBu.getName())) {
			String isthisworking = selectedBu.getName();
			if (StringUtils.hasText(isthisworking)) {
				SelectedBusinessUnitsHolder
						.setSelectedBusinessUnit(isthisworking);
			} else {
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(null);
			}
		}
		return invocation.invoke();
	}
}

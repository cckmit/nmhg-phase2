package tavant.twms.domain.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.hibernate.Filter;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import org.springframework.beans.factory.annotation.Required;



import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

@Aspect
public class DisableSpecificBuSelectionFilter {
	private SecurityHelper securityHelper;
	
	@Before("@annotation(tavant.twms.annotations.common.DisableSpecificBuSelection)")
	public void applyBefore(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();
		daoSupport.getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("bu_name");
	}

	@After("@annotation(tavant.twms.annotations.common.DisableSpecificBuSelection)")
	public void applyAfter(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();
Filter filter = daoSupport.getHibernateTemplate().enableFilter("bu_name");
		
		String threadLocalBuName = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
		
		//if a business unit exist in the thread local, it the means user is performing an operation
		// specific to a BU and that takes priority
		if (threadLocalBuName != null && StringUtils.hasText(threadLocalBuName)) 
		{
			filter.setParameter("name", threadLocalBuName);
		}
		else if (securityHelper.getDefaultBusinessUnit() != null)
		{
			//this flow when the user is a SingleLineDealer or admin who always works on a given BU
			filter.setParameter("name", securityHelper
						.getDefaultBusinessUnit().getName());
			
		}
		else 
		{
			//By default all the BUs of the user will be set here
			Collection<String> bus = getBusinessUnitsForLoggedInUser();
			filter.setParameterList("name", bus);
		}
	}
	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}
	private Collection<String> getBusinessUnitsForLoggedInUser() {
		try {
			Set<BusinessUnit> businessUnits = securityHelper.getLoggedInUser()
					.getBusinessUnits();
			Collection<String> bus = new ArrayList<String>(1);
			for (BusinessUnit businessUnit : businessUnits) {
				bus.add(businessUnit.getName());
			}
			return bus;
		} catch (IllegalStateException e) {
			//this exception means acegi filter is not yet called and hence returning an empty collection to handle hibernate exception
			return new ArrayList<String>(1);
		}
	}

}
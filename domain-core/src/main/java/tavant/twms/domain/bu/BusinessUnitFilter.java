package tavant.twms.domain.bu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.StringUtils;

import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;



@Aspect
public class BusinessUnitFilter {
	private SecurityHelper securityHelper;

	/**
	 * This method intercepts every method call and then enables the bu filter.
	 * this way the bu filter need not be hard-coded inside every method.
	 * 
	 * @param daoSupport
	 *            the joinpoint
	 */
	@Before("execution(* tavant.twms.infra.GenericRepository+.*(..)) || "
			+ "execution(* tavant.twms.domain.failurestruct.FailureStructureRepository.*(..)) ||"
            + "execution(* tavant.twms.domain.common.LovRepository.*(..)) ||"
            + "execution(* tavant.twms.infra.DomainRepository.*(..))")
    public void apply(JoinPoint joinPoint) {
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
	
	/**
	 * This method intercepts every method call and then enables the bu filter.
	 * this way the bu filter need not be hard-coded inside every method.
	 * 
	 * @param daoSupport
	 *            the joinpoint
	 */
	@Before("execution(* tavant.twms.domain.orgmodel.DealershipRepositoryImpl+.*(..)) || "
            + " execution(* tavant.twms.domain.orgmodel.ServiceProviderRepositoryImpl+.*(..)) || " 
            + " execution(* tavant.twms.domain.orgmodel.SupplierRepositoryImpl+.*(..)) || " 
            + " execution(* tavant.twms.domain.orgmodel.OrganizationRepositoryImpl+.getOrganizationAddressBySiteNumber(..)) ")
	public void applyBUFilterBeforeQueryOnParty(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();

		Filter filter = daoSupport.getHibernateTemplate().enableFilter("party_bu_name");
		
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
	//Filter Added to Draft claim upload getOrganizationAddressBySiteNumber()
	@After("execution(* tavant.twms.domain.orgmodel.DealershipRepositoryImpl+.*(..)) || "
          + " execution(* tavant.twms.domain.orgmodel.ServiceProviderRepositoryImpl+.*(..))  || " 
            + " execution(* tavant.twms.domain.orgmodel.SupplierRepositoryImpl+.*(..)) || " 
            + " execution(* tavant.twms.domain.orgmodel.OrganizationRepositoryImpl+.getOrganizationAddressBySiteNumber(..)) ")
	public void removeBUFilterAfterQueryOnParty(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();
		daoSupport.getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("party_bu_name");
	}


	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	/**
	 * @return business units for logged in user
	 */
	@SuppressWarnings("unchecked")
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

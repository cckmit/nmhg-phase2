package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;

public class ServiceProviderRepositoryImpl extends GenericRepositoryImpl<ServiceProvider, Long>
        implements ServiceProviderRepository
{
	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.ServiceProviderRepository#findServiceProviderNamesStartingWith(java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<String> findServiceProviderNamesStartingWith(final String serviceProviderName,
			final int pageNumber, final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select sp.name from ServiceProvider sp where upper(sp.name) like :serviceProviderName")
								.setParameter("serviceProviderName", serviceProviderName.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}
	

	/**
	 * @param serviceProviderName
	 * @return
	 */
	public ServiceProvider findServiceProviderByName(final String serviceProviderName) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider sp where upper(sp.name)=:serviceProviderName")
								.setParameter("serviceProviderName", serviceProviderName.toUpperCase())
								.uniqueResult();
					};
				});
	}
	
	public Dealership findDealerDetailsByNumber(final String dealerNumber) {
		return (Dealership) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Dealership dealer where dealer.dealerNumber=:dealerNumber")
								.setParameter("dealerNumber", dealerNumber)
								.uniqueResult();
					};
				});
	}


	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.ServiceProviderRepository#findServiceProviderNumbersStartingWith(java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<String> findServiceProviderNumbersStartingWith(final String serviceProviderNumber,
			final int pageNumber, final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select sp.serviceProviderNumber from ServiceProvider sp where sp.serviceProviderNumber like :spNumber")
								.setParameter("spNumber", serviceProviderNumber.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}
	
	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.ServiceProviderRepository#findServiceProviderByNumber(java.lang.String)
	 */
	public ServiceProvider findServiceProviderByNumber(final String serviceProviderNumber) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider sp where sp.serviceProviderNumber =:spNumber")
								.setParameter("spNumber", serviceProviderNumber)
								.uniqueResult();
					};
				});
	}
	


	public ServiceProvider findServiceProviderById(Long id) {
		return (ServiceProvider) getHibernateTemplate().get(ServiceProvider.class, id);
	}
	
    /**
     * This method disables business unit filter before executing the query 
     * and re-enables the business unit after execution
     */
	public List<ServiceProvider> findServiceProviderByNumberWithOutBU(final String serviceProviderNumber) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("party_bu_name");
						session.disableFilter("excludeInactive");
						List<Object> objects= session
								.createQuery(
										"select sp from ServiceProvider sp where sp.serviceProviderNumber =:spNumber")
								.setParameter("spNumber", serviceProviderNumber)
								.list();
						session.enableFilter("party_bu_name");
						session.enableFilter("excludeInactive");
						return objects;						
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findNationalAccountsWhoseNameStartsWith(
			final String nationalAccountName, final int pageNumber,
			final int pageSize) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select sp from ServiceProvider sp,NationalAccount na where upper(sp.name) like :nationalAccountName and sp.id = na.id")
								.setParameter("nationalAccountName",
										nationalAccountName.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findNationalAccountsWhoseNumberStartsWith(
			final String nationalAccountNumber, final int pageNumber,
			final int pageSize) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select sp from ServiceProvider sp,NationalAccount na where upper(na.nationalAccountNumber) like :nationalAccountNumber and sp.id = na.id")
								.setParameter("nationalAccountNumber",nationalAccountNumber.toUpperCase()+ "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
		
	}
	public PageResult<ServiceProvider> findAllNationalAccounts(String nationalAccountName, ListCriteria listCriteria){
        String companyCustomerSearchQuery = "from ServiceProvider sp,NationalAccount na where upper(sp.name) like :nationalAccountName and sp.id = na.id";
        Map<String, Object> params = new HashMap<String, Object>(); 
        params.put("nationalAccountName", nationalAccountName + "%");           
        return   findPageUsingQueryForDistinctItems(companyCustomerSearchQuery, "sp.name asc", 
              "select distinct(sp)", listCriteria.getPageSpecification(), new QueryParameters(params),"distinct sp");
  }
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ServiceProvider findServiceProviderByNumberAndBusinessUnit(final String serviceProviderNumber, final String businessUnit) {
        return (ServiceProvider) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.disableFilter("party_bu_name");
                ServiceProvider serviceProvider = (ServiceProvider) session
                        .createQuery(
                                "select sp from ServiceProvider sp join sp.businessUnits businessUnits where sp.serviceProviderNumber =:spNumber and businessUnits.name=:businessUnit")
                        .setParameter("spNumber", serviceProviderNumber).setParameter("businessUnit", businessUnit).uniqueResult();
                session.enableFilter("party_bu_name");
                return serviceProvider;
            };
        });
    }


}

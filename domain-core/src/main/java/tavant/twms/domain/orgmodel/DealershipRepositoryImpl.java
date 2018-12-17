/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.orgmodel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 *
 */
public class DealershipRepositoryImpl extends GenericRepositoryImpl<User, Long> implements
		DealershipRepository {
	
	public static final int ORACLE_IN_QUERY_LIMIT = 1000;

	public void createDealership(ServiceProvider newDealership) {
		getHibernateTemplate().save(newDealership);
	}

	public void updateDealership(ServiceProvider dealer) {
		getHibernateTemplate().update(dealer);
	}
	
	public void updateShipmentAddress(Address shipmentAddress)
	{
		getHibernateTemplate().update(shipmentAddress);
	}

    public void createShipmentAddress(Address shipmentAddress)
    {
         getHibernateTemplate().saveOrUpdate(shipmentAddress);
    }


    /**
	 * @see tavant.twms.domain.orgmodel.DealershipRepository#findPolicyDefinitionById(java.lang.Long)
	 */
	public ServiceProvider findByDealerId(Long id) {
		return (ServiceProvider) getHibernateTemplate().get(ServiceProvider.class, id);
	}

	/**
	 * @see tavant.twms.domain.orgmodel.DealershipRepository#findByName(java.lang.String)
	 */
	public ServiceProvider findByDealerName(final String dealerName) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where upper(dealer.name)=:dealerName")
								.setParameter("dealerName", dealerName.toUpperCase())
								.uniqueResult();
					};
				});
	}
	
	public ServiceProvider findCertifiedDealerByNumber(final String dealerNumber) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where dealer.serviceProviderNumber=:dealerNumber and dealer.certified=1")
								.setParameter("dealerNumber", dealerNumber)
								.uniqueResult();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findDealerNamesStartingWith(final String dealerName,
			final int pageNumber, final int pageSize) {
			return (List<String>) getHibernateTemplate().execute(
					new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							return session
									.createQuery(
										"select dealer.name from ServiceProvider dealer where upper(dealer.name) like :dealerName")
								.setParameter("dealerName", dealerName.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
									.setMaxResults(pageSize).list();
						};
					});
		}

	@SuppressWarnings("unchecked")
	public List<String> findDealerNumbersStartingWith(final String dealerNumber,
			final int pageNumber, final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select dealer.name from ServiceProvider dealer where dealer.serviceProviderNumber like :dealerNumber")
								.setParameter("dealerNumber", dealerNumber + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}


	/**
	 * @see tavant.twms.domain.orgmodel.DealershipRepository#findByName(java.lang.String)
	 */
	public ServiceProvider findByDealerNumber(final String dealerNumber) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where dealer.serviceProviderNumber=:dealerNumber")
								.setParameter("dealerNumber", dealerNumber)
								.uniqueResult();
					};
				});
	}
	
	public List<ServiceProvider> findByDealerListByNumber(final String dealerNumber) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where dealer.serviceProviderNumber=:dealerNumber")
								.setParameter("dealerNumber", dealerNumber)
								.list();
					};
				});
	}


	// FIXME : Temp method till the dealer search screen is ready for Reports
	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findAllDealers(final String dealerName) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where dealer.name like :dealerName ")
								.setParameter("dealerName", dealerName.toUpperCase() + "%")
								.list();
					};
				});

	}

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findAllBUDealers(final String businessUnitName) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria buDealerCriteria = session.createCriteria(ServiceProvider.class);

						buDealerCriteria.add(Restrictions.eq("isPartOfOrganization",
								findByDealerNumber(businessUnitName)));
						return buDealerCriteria.list();
					};
				});

	}
	

	
	
	

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findDealersByFamily(final String dealerFamilyCode)
	{
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						return session.createQuery(
								"from ServiceProvider dealer where dealer.dealerFamilyCode = :dealerFamilyCode")
								.setParameter("dealerFamilyCode", dealerFamilyCode)
								.list();
					}
				}
		);
	}

	// FIXME : Temp method till the dealer search screen is ready for Reports
	@SuppressWarnings("unchecked")
	public List<Supplier> findAllSuppliers(final String supplierName) {
		return (List<Supplier>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Supplier supplier where supplier.name like :supplierName ")
								.setParameter("supplierName",
										supplierName + "%").list();
					};
				});

	}

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findDealersWhoseNameStartsWith(
			final String dealerName, final int pageNumber, final int pageSize) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where upper(dealer.name) like :dealerName")
								.setParameter("dealerName", dealerName.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findDealersWhoseNumberStartingWith(
			final String dealerNumber, final int pageNumber, final int pageSize){
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where upper(dealer.serviceProviderNumber) like :dealerNumber")
								.setParameter("dealerNumber", dealerNumber + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});

	}

	public boolean isDealer(final User user) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		PageResult<ServiceProvider> dealers = (PageResult<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria userCriteria = session
								.createCriteria(ServiceProvider.class, "serviceProvider");
						int numProviders = user.getBelongsToOrganizations().size();
						List<String> addedAliases = new ArrayList<String>(10);
						
			          if(numProviders > 0) {
			          	String processedProperty = getCriteriaHelper().processNestedAssociations(userCriteria,
									"id", addedAliases);
			          	
			              int i = 1;
			             // boolean belongsToGroup = "true".equalsIgnoreCase(searchCriteria.getBelongsToDealerGroup());
			              Junction paritionedInQuery = Restrictions.disjunction();

			              List<Long> providerIds =
			                      new ArrayList<Long>(numProviders);
			              for(Organization provider : user.getBelongsToOrganizations()) {
			                  providerIds.add(provider.getId());

			                  // Oracle supports only 1000 elements inside an "IN"
			                  // query. Hence we use multiple IN queries joined
			                  // by an OR clause.
			                  if(i++ == ORACLE_IN_QUERY_LIMIT) {
			                  		paritionedInQuery.add(Restrictions.in(processedProperty, providerIds));
			                      providerIds.clear();
			                      i = 1;
			                  }
			              }

			              if(i > 1) {              	
			              		paritionedInQuery.add(Restrictions.in(processedProperty, providerIds));
			              }

			              userCriteria.add(paritionedInQuery);
			              
			          }
			          ProjectionList projectionList = Projections.projectionList();
                      projectionList.add(Projections.countDistinct("id"));
                      projectionList.add(Projections.rowCount());
						userCriteria.setProjection(projectionList);
						Object[] result = (Object[]) userCriteria.uniqueResult();

						Long numResults = (Long) result[0];
                      
						if (logger.isDebugEnabled()) {
							logger.debug("User Search based on Criteria "
									+ userCriteria + " returned ["
									+ numResults + "] results.");
						}
						// Remove the count projection and set the criteria to
						// the
						// default state.
						userCriteria.setProjection(null);						
						userCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

						PageSpecification pageSpecification = new PageSpecification();
						return fetchDealersUsingSQLQuery(userCriteria,
                                session, pageSpecification);
					}
				});
		return dealers != null && !dealers.getResult().isEmpty() ;
	}
	
	
	private Object fetchDealersUsingSQLQuery(
			Criteria userCriteria, Session session,
			PageSpecification pageSpecification) {
		 List<ServiceProvider> matchingUsers = null;
	        try {
	            CriteriaImpl c = (CriteriaImpl) userCriteria;
	            SessionImpl s = (SessionImpl) c.getSession();
	            SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
	            String[] implementors = factory.getImplementors(c.getEntityOrClassName());
	            CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]),
	                    factory, c, implementors[0], s.getLoadQueryInfluencers());
//	                  Field f = OuterJoinLoader.class.getDeclaredField("sql");
//			    f.setAccessible(true);
//			    String sql = (String)f.get(loader);
	            StringBuilder sb = new StringBuilder(loader.toString());
	            sb.delete(0, "org.hibernate.loader.criteria.CriteriaLoader(".length()).deleteCharAt(sb.length() - 1);
	            int endIndex = sb.indexOf(" from ");
	            sb.replace("select ".length(), endIndex, "");
	            sb.insert("select ".length(), "distinct  this_.*" + getSortString(sb.toString()));
	            CriteriaQueryTranslator cqt = new CriteriaQueryTranslator(factory, c, c.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);
	            org.hibernate.engine.QueryParameters qp = cqt.getQueryParameters();
	            qp.processFilters(sb.toString(), s);
	            SQLQuery q = session.createSQLQuery(qp.getFilteredSQL());
	            Object[] values = qp.getFilteredPositionalParameterValues();
	            Type[] types = qp.getFilteredPositionalParameterTypes();
	            for (int i = 0; i < values.length; i++) {
	                q.setParameter(i, values[i], types[i]);
	            }
	            matchingUsers = q.list();
	            if (matchingUsers != null) {
	                return new PageResult<ServiceProvider>(matchingUsers,
	                        pageSpecification,
	                        pageSpecification.convertRowsToPages(pageSpecification.getPageNumber()));
	            }
	        } catch (Exception e) {
	            // ignoring exception and logging for debuging purpose only !!
	            // and giving a chance for user to see few results !
	            logger.error("Error in fetchDealersUsingSQLQuery()", e);
	        }
	        return null;
	};

	   private String getSortString(String s) {
           int orderIndex = s.indexOf("order by")+"order by".length();
           String retVal = null;
           if(s.endsWith("asc")){
               retVal = s.substring(orderIndex, s.length()-3);
           }else if (s.endsWith("desc")){
               retVal = s.substring(orderIndex, s.length()-4);
           }
           return (retVal != null ) ? ((retVal.trim().startsWith("this")) ?  "" :  ", " + retVal) : "";
       }
	
	@SuppressWarnings("unchecked")
	/**
	 * This API is used to search for dealers by Name or Number. If both are given, it will do AND condition
	 */
	public List<ServiceProvider> findDealersByNumberOrName(final String dealerNumber,final String dealerName) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria dealerCriteria = session.createCriteria(ServiceProvider.class);
						if(StringUtils.hasText(dealerNumber) && StringUtils.hasText(dealerName))
						{
							dealerCriteria.add(Restrictions.and(Restrictions.ilike("serviceProviderNumber", dealerNumber,MatchMode.ANYWHERE),Restrictions.ilike("name", dealerName,MatchMode.ANYWHERE)));
						}
						else if (StringUtils.hasText(dealerNumber))
						{
							dealerCriteria.add(Restrictions.ilike("serviceProviderNumber", dealerNumber,MatchMode.ANYWHERE));
						}
						else if (StringUtils.hasText(dealerName))
						{
							dealerCriteria.add(Restrictions.ilike("name", dealerName,MatchMode.ANYWHERE));
						}
						dealerCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return dealerCriteria.list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findAllOtherDealersByBUName(final String businessUnitName, final String dealerFamilyCode) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria buDealerCriteria = session.createCriteria(ServiceProvider.class);

						buDealerCriteria.add(Restrictions.ne("dealerFamilyCode", dealerFamilyCode));

						buDealerCriteria.add(Restrictions.eq("isPartOfOrganization",
								findByDealerNumber(businessUnitName)));
						return buDealerCriteria.list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public ServiceProvider findDealersByNumberWithoutLike(final String dealerNumber) {
		return (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealership where dealership.dealerNumber = :dealerNumber")
								.setParameter("dealerNumber", dealerNumber)
								.uniqueResult();
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findCertifiedDealersWhoseNameStartsWith(
			final String dealerName, final int pageNumber, final int pageSize) {
		return (List<ServiceProvider>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ServiceProvider dealer where upper(dealer.name) like :dealerName and dealer.certified=1")
								.setParameter("dealerName", dealerName.toUpperCase() + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public String findDealerBrands(final Organization organization) {
		return  (String) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createSQLQuery(
										"select brand from Dealership dealer where dealer.id =:organizationId")
								.setParameter("organizationId", organization.getId())
								.uniqueResult();
					};
				});
	}


    public ServiceProvider findDealerByServiceProviderID(final String serviceProviderId) {
        return (ServiceProvider) getHibernateTemplate().execute(
                new HibernateCallback() {


                    /**
                     * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
                     */
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "from ServiceProvider dealer where dealer.serviceProviderNumber=:serviceProviderId")
                                .setParameter("serviceProviderId", serviceProviderId)
                                .uniqueResult();
                    };
                });
    }

    @SuppressWarnings("unchecked")
	public String findMarketingGroupCodeBrandByDealership(final Dealership dealer) {
		return (String) getHibernateTemplate().execute(
				new HibernateCallback() {

					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createSQLQuery(
										"Select brand from marketing_group_brand where marketing_group=:marketingGroup")
								.setParameter("marketingGroup", dealer.getMarketingGroup())
								.uniqueResult();
					};
				});
	}

	public List<BigDecimal> findServiceProviderIds(String dealerNumber,
			String businessUnit) {
		String query = "select serviceProvider.id from service_provider  serviceProvider, bu_org_mapping buOrgMapping"
				+ " where serviceProvider.id=buOrgMapping.org and buOrgMapping.bu=:businessUnit and serviceProvider.service_provider_number like :dealerCode";
		List<BigDecimal> serviceProviderIds = getSession()
				.createSQLQuery(query).setParameter("businessUnit",
						businessUnit).setParameter("dealerCode",
						"%" + dealerNumber).list();
		return serviceProviderIds;
	}

}

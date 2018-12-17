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

package tavant.twms.domain.claim;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.DealerGroupRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ServiceProviderRepository;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author kamal.govindraj
 *
 */
public class ClaimRepositoryImpl extends GenericRepositoryImpl<Claim, Long>
		implements ClaimRepository {

	public static final int ORACLE_IN_QUERY_LIMIT = 1000;
	
	public static final String NCR = "ncr";
	public static final String NCR_WITH_30DAYS = "ncrWith30Days";

	private DealerGroupRepository dealerGroupRepository;
	private ItemGroupRepository itemGroupRepository;

	private ConfigParamService configParamService;
	private ServiceProviderRepository serviceProviderRepository;

	public ServiceProviderRepository getServiceProviderRepository() {
		return serviceProviderRepository;
	}

	public void setServiceProviderRepository(
			ServiceProviderRepository serviceProviderRepository) {
		this.serviceProviderRepository = serviceProviderRepository;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.claim.ClaimRepository#find(java.lang.Long)
	 */
	public Claim find(Long id) {
		return (Claim) getHibernateTemplate().get(Claim.class, id);
	}
	
	public Claim findClaimWithServiceInfoAttributes(Long id) {
		Claim claim=(Claim) getHibernateTemplate().get(Claim.class, id);
		getHibernateTemplate().initialize(claim.getServiceInformation().getFaultClaimAttributes());
		getHibernateTemplate().initialize(claim.getServiceInformation().getPartClaimAttributes());
		return claim;
	}
	

	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.claim.ClaimRepository#findClaimAudit(java.lang.Long)
	 */
	public ClaimAudit findClaimAudit(Long id) {
		return (ClaimAudit) getHibernateTemplate().get(ClaimAudit.class, id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.claim.ClaimRepository#save(tavant.twms.domain.claim.Claim)
	 */
	@Override
	public void save(Claim newClaim) {
		getHibernateTemplate().save(newClaim);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.claim.ClaimRepository#update(tavant.twms.domain.claim.Claim)
	 */
	@Override
	public void update(Claim claimToBeUpdated) {
		getHibernateTemplate().update(claimToBeUpdated);
	}
	
	
	public void updateInstalledParts(InstalledParts installedParts)
	{
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
		hibernateTemplate.update(installedParts);
	}
	
	public void updateLineItemGroup(LineItemGroup lineItemGroup)
	{
		getHibernateTemplate().saveOrUpdate(lineItemGroup);
		
	}
	
	public void updatePayment(Payment payment)
	{
		getHibernateTemplate().update(payment);
	}

	@SuppressWarnings("unchecked")
	public Collection<Claim> findAllPreviousClaimsForItem(Long invItemId) {
		return getHibernateTemplate()
				.find(
						"select claim from Claim claim join claim.claimedItems as claimedItems where "
								+ "claimedItems.itemReference.referredInventoryItem.id = ?"
								+ " and claim.activeClaimAudit.state not in ('DRAFT','DRAFT_DELETED','DELETED','DEACTIVATED') order by claim.activeClaimAudit.failureDate asc ",
								 invItemId );
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Claim> findAllPreviousClaimsForMajorComp(Long majorComInvId) {
		return getHibernateTemplate()
				.find(
						"select claim from Claim claim where "
								+ " claim.partItemReference.referredInventoryItem.id = ?"
								+ " and claim.activeClaimAudit.state not in ('DRAFT','DRAFT_DELETED','DELETED','DEACTIVATED') order by claim.activeClaimAudit.failureDate asc",
								new Object[] { majorComInvId });
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Claim> findAllClaimsForItemFiledByDealer(String serialNumber,final ServiceProvider serviceProvider) {
		return getHibernateTemplate()
				.find(
						"select claim from Claim claim join claim.claimedItems as claimedItems where "
								+ "claimedItems.itemReference.referredInventoryItem.serialNumber = ?"
								+ " and claim.forDealer = ?"
								+ " and claim.activeClaimAudit.state not in ('DRAFT','DRAFT_DELETED','DELETED','DEACTIVATED') ",
								new Object[] { serialNumber, serviceProvider });
								
		}

	@SuppressWarnings("unchecked")
	public Collection<ClaimState> findAllClaimStates() {
		return getHibernateTemplate().find("from ClaimState");
	}

	@Override
	public void delete(Claim claim) {
		getHibernateTemplate().delete(claim);
	}

	public Claim findClaimByNumber(final String claimNumber) {
		return (Claim) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {				
				return session
						.createQuery(
								"from Claim claim where upper(claim.claimNumber) = :claimNumber")
						.setParameter("claimNumber", claimNumber)
						.uniqueResult();
			}
		});
	}
	
	public Claim findClaimByNumber(final String claimNumber,
			final String dealerNumber) {
		return (Claim) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"from Claim claim where claim.claimNumber=:claimNumber and forDealer.dealerNumber=:dealerNumber")
						.setParameter("claimNumber", claimNumber).setParameter(
								"dealerNumber", dealerNumber).uniqueResult();

			}
		});
	}

	public void createClaimAudit(ClaimAudit claimAudit) {
		getHibernateTemplate().save(claimAudit);

	}

	public PageResult<Claim> findClaimsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final QueryParameters params) {
		return super.findPageUsingQuery(queryWithoutSelect, orderByClause,
				selectClause, pageSpecification, params);
	}

	public PageResult<Claim> findClaimsUsingDynamicQuery(String claimNumber,
			PageSpecification pageSpecification) {

		Session session = getSession();
		Criteria crit = session.createCriteria(Claim.class);
		crit.add(Expression.eq("claimNumber", claimNumber));
		List claims = crit.list();

		/*if(pageSpecification.getPageSize()%500 == 0){
        	pageSpecification.setPageNumber(0);
        }*/

		PageResult<Claim> page = new PageResult<Claim>(claims,
				pageSpecification, 1);

		return page;

	}

	@SuppressWarnings("unchecked")
	public List<Claim> findAllClaimsForMultiMaintainance(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, final QueryParameters params) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
		countQuery.append(queryWithoutSelect);
		StringBuffer filterAndSort = new StringBuffer();
		if (selectClause != null && !("".equals(selectClause.trim()))) {
			filterAndSort.append(selectClause);
		}
		filterAndSort.append(queryWithoutSelect);
		// filterAndSort.append("and state like '%CLOSED'");
		// filterAndSort.append("and all elements
		// (claim.recoveryClaims.recoveryClaimState.state) like '%CLOSED%'");
		if (orderByClause != null && orderByClause.trim().length() > 0) {
			filterAndSort.append(" order by ");
			filterAndSort.append(orderByClause);
		}
		final String finalQuery = filterAndSort.toString();

		List<Claim> claims = (List<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						if (logger.isDebugEnabled()) {
							logger.debug(" Unpaginated query for findPage("
									+ queryWithoutSelect + ",listCriteria) [ "
									+ finalQuery + " ]");
						}
						Query query = session.createQuery(finalQuery);
						populateQueryParams(query, params);
						// query.setProperties(parameterMap);
						return query.list();
					}
				});
		return claims;
	}

	@SuppressWarnings("unchecked")
	public Collection<Claim> findAllClaimsInState(ClaimState state) {
		return getHibernateTemplate().find("from Claim where activeClaimAudit.state = ?", state);
	}

	@SuppressWarnings("unchecked")
	public List<Claim> findClaimsToRetryCreditSubmission() {
		final String query = "select claim from Claim claim "
			+ " where claim.activeClaimAudit.state = 'PENDING_PAYMENT_SUBMISSION' and "
			+ " (select count(*) from SyncTracker syncTracker "
			+ " where syncTracker.uniqueIdValue = claim.claimNumber "
			+ " and syncTracker.syncType = 'Claim' ) < "
			+ " (select count(*) from ClaimAudit claimAudit "
			+ " where claimAudit.forClaim = claim.id "
			+ " and claimAudit.previousState = 'PENDING_PAYMENT_SUBMISSION' ) ";
		return (List<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						session.disableFilter("bu_name");
						return session.createQuery(query).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<SyncTracker> findAllCreditSubmissionsAwaitingNotification() {
		return getHibernateTemplate()
				.find(
						"select syncTracker from SyncTracker syncTracker "
								+ " where syncTracker.syncType='Claim'"
								+ " and syncTracker.uniqueIdValue in "
								+ " (select claim.claimNumber  "
								+ " from Claim claim where claim.activeClaimAudit.state = 'PENDING_PAYMENT_RESPONSE' )");
	}

	@SuppressWarnings("unchecked")
	public Collection<Claim> findAllPreviousClaimsForClaimedItems(
			String serialNumber, Double hoursInService) {
		return getHibernateTemplate()
				.find(
						"select claim "
								+ "from Claim claim join claim.claimedItems as claimedItems where "
								+ "claimedItems.itemReference.referredInventoryItem.serialNumber = ?"
								+ " and " + "claimedItems.hoursInService = ?",
						new Object[] { serialNumber, hoursInService });
	}

	
	public PageResult<Claim> findAllHistClaimsMatchingCriteria(
			final ListCriteria listCriteria,ServiceProvider loggedInUser){
		PageSpecification ps = listCriteria.getPageSpecification();
	     Map<String,Object> parameterMap = new HashMap<String,Object>(2);
	     StringBuilder sb = new StringBuilder();
	     if(loggedInUser!=null){
		     sb.append("from Claim claim  where lower(claim.histClmNo) =  '"+ listCriteria.getHistoricalClaimNumber().toLowerCase()+"'" +" and claim.forDealer.id=" +loggedInUser.getId()+ " and not activeClaimAudit.state='DRAFT_DELETED'");
	     }
	     else{
	    	 sb.append("from Claim claim  where lower(claim.histClmNo) =  '"+ listCriteria.getHistoricalClaimNumber().toLowerCase()+"'" +" and not activeClaimAudit.state in('DRAFT','DRAFT_DELETED')");
	     }
	     String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
	     if(org.apache.commons.lang.StringUtils.isNotBlank(paramterizedFilterCriteria)){
	    	 sb.append(" and ").append(paramterizedFilterCriteria);
	    	 parameterMap.putAll(listCriteria.getParameterMap());
	     }
	     return findPageUsingQuery(sb.toString() , 
	    		 listCriteria.getSortCriteriaString(), "select claim ", ps, new QueryParameters(parameterMap));
	}
	
	public PageResult<Claim> getAllAcceptedClaimsMatchingCriteriaForDealer(
			final ListCriteria listCriteria,ServiceProvider loggedInUser){
		PageSpecification ps = listCriteria.getPageSpecification();
	    Map<String,Object> parameterMap = new HashMap<String,Object>(2);
	    StringBuilder query = new StringBuilder();
		query.append("from Claim claim where claim.forDealer.id = " +loggedInUser.getId()
				+ " and activeClaimAudit.state = 'ACCEPTED_AND_CLOSED'"
				+ " and activeClaimAudit.payment.activeCreditMemo.creditMemoDate < sysdate - 60"
				+ " and activeClaimAudit.payment.activeCreditMemo.creditMemoDate > sysdate - 180"
				+ " and NOT EXISTS"
				+ " (select ca from ClaimAudit ca join ca.serviceInformation serviceInfo join "
				+ " serviceInfo.serviceDetail serviceDetail join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join "
				+ " replacedInstalledParts.replacedParts replacedPartAlias "
				+ " where ca = claim.activeClaimAudit and replacedPartAlias.partToBeReturned = true)");
				
	    String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
	    if(org.apache.commons.lang.StringUtils.isNotBlank(paramterizedFilterCriteria)){
	    	query.append(" and ").append(paramterizedFilterCriteria);
	    	parameterMap.putAll(listCriteria.getParameterMap());
	    }
	    return findPageUsingQuery(query.toString() , 
	    		listCriteria.getSortCriteriaString(), "select claim ", ps, new QueryParameters(parameterMap));
	}
	
	public PageResult<Claim> getAllPartShippedNotReceivedClaims(
			ListCriteria listCriteria, Long buConfigDays) {
		PageSpecification ps = listCriteria.getPageSpecification();
	    Map<String,Object> parameterMap = new HashMap<String,Object>(2);
	    StringBuilder query = new StringBuilder();
	    query.append(" from Claim claim , TaskInstance taskInstance"
				+ " where taskInstance.isOpen = true "
				+ " and taskInstance.claimId = claim.id"
				+ " and claim.prtShpNtrcvd=false "
				+ " and claim.activeClaimAudit.state not in ('DENIED','ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','PENDING_PAYMENT_SUBMISSION','PENDING_PAYMENT_RESPONSE') "
				+ " and taskInstance.create in (select max(create) from TaskInstance where id in (taskInstance.id) "
				+ " and taskInstance.name in ('Parts Shipped') and isOpen = true)"
				+ " and taskInstance.create +  " + buConfigDays + " <= sysdate");
				
	    String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
	    if(org.apache.commons.lang.StringUtils.isNotBlank(paramterizedFilterCriteria)){
	    	query.append(" and ").append(paramterizedFilterCriteria);
	    	parameterMap.putAll(listCriteria.getParameterMap());
	    }
	    return findPageUsingQuery(query.toString() , 
	    		listCriteria.getSortCriteriaString(), "select claim ", ps, new QueryParameters(parameterMap));
	}
	
	@SuppressWarnings("unchecked")
	public PageResult<Claim> findAllClaimsMatchingCriteria(
			final ClaimSearchCriteria claimSearchCriteria) {

		return (PageResult<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {

					@SuppressWarnings("unchecked")
					public Object doInHibernate(Session session) {

						Criteria claimCriteria = session
								.createCriteria(Claim.class, "claim");
						List<String> addedAliases = buildSearchCriteria(
								claimSearchCriteria, claimCriteria,session);
						 Criterion isNotNCR = Restrictions.eq(NCR,Boolean.FALSE);
						 Criterion isNotNCRWith30Days =Restrictions.eq(NCR_WITH_30DAYS,Boolean.FALSE);
						 if("false".equals(claimSearchCriteria.getIncludeNCRClaims())){
						   claimCriteria.add(Restrictions.conjunction().add(isNotNCR).add(isNotNCRWith30Days));
						 }
						addFilterRestrictions(claimCriteria,
								claimSearchCriteria, addedAliases);
						// Get the count of results, first.
                        ProjectionList projectionList = Projections.projectionList();
                        projectionList.add(Projections.countDistinct("id"));
                        projectionList.add(Projections.rowCount());
						claimCriteria.setProjection(projectionList);
						Object[] result = (Object[]) claimCriteria.uniqueResult();

						Long numResults = (Long) result[0];
                        
						if (logger.isDebugEnabled()) {
							logger.debug("Claim Search based on Criteria "
									+ claimSearchCriteria + " returned ["
									+ numResults + "] results.");
						}
						// Remove the count projection and set the criteria to
						// the
						// default state.
						claimCriteria.setProjection(null);						
						claimCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

						PageSpecification pageSpecification = claimSearchCriteria.getPageSpecification();

						List<Claim> matchingClaims = new ArrayList<Claim>();
						if (numResults > 0
								&& numResults > pageSpecification.offSet()) {
							addSortRestrictions(claimCriteria,
                            claimSearchCriteria, addedAliases);

                            // hack for search by part return status [TSESA-142]
                            // use the hack version if the count(id) and count (distinct id) are not same
                            if(!numResults.equals(result[1])){
                                PageResult<Claim> pageResult = fetchClaimsUsingSQLQuery(claimCriteria,
                                        session, pageSpecification, numResults);
                                if(pageResult != null)
                                    return pageResult;
                            } 
							matchingClaims = claimCriteria.setFirstResult(
									pageSpecification.offSet()).setMaxResults(
									pageSpecification.getPageSize()).list();
						}

						return new PageResult<Claim>(matchingClaims,
								pageSpecification, pageSpecification
										.convertRowsToPages(numResults));
					}

				});
	}


    /**
     * Hack for fetching distinct claims
     * @param claimCriteria
     * @return
     */
    private PageResult<Claim> fetchClaimsUsingSQLQuery(Criteria claimCriteria,
            Session session, PageSpecification pageSpecification, long numResults) {
        List<Claim> matchingClaims = null;
        try {
            CriteriaImpl c = (CriteriaImpl) claimCriteria;
            SessionImpl s = (SessionImpl) c.getSession();
            SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
            String[] implementors = factory.getImplementors(c.getEntityOrClassName());
            CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]),
                    factory, c, implementors[0], s.getLoadQueryInfluencers());
//                  Field f = OuterJoinLoader.class.getDeclaredField("sql");
//		    f.setAccessible(true);
//		    String sql = (String)f.get(loader);
            StringBuilder sb = new StringBuilder(loader.toString());
            sb.delete(0, "org.hibernate.loader.criteria.CriteriaLoader(".length()).deleteCharAt(sb.length() - 1);
            int endIndex = sb.indexOf(" from ");
            sb.replace("select ".length(), endIndex, "");
            sb.insert("select ".length(), "distinct  this_.* " + getSortString(sb.toString()));
            CriteriaQueryTranslator cqt = new CriteriaQueryTranslator(factory, c, c.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);
            org.hibernate.engine.QueryParameters qp = cqt.getQueryParameters();
            qp.processFilters(sb.toString(), s);
            SQLQuery q = session.createSQLQuery(qp.getFilteredSQL()).addEntity(Claim.class);
            Object[] values = qp.getFilteredPositionalParameterValues();
            Type[] types = qp.getFilteredPositionalParameterTypes();
            for (int i = 0; i < values.length; i++) {
                q.setParameter(i, values[i], types[i]);
            }
            matchingClaims = q.setFirstResult(pageSpecification.offSet()).setMaxResults(pageSpecification.getPageSize()).list();
            if (matchingClaims != null) {
                return new PageResult<Claim>(matchingClaims,
                        pageSpecification,
                        pageSpecification.convertRowsToPages(numResults));
            }
        } catch (Exception e) {
            // ignoring exception and logging for debuging purpose only !!
            // and giving a chance for user to see few results !
            logger.error("Error in fetchClaimsUsingSQLQuery()", e);
        }
        return null;
    }

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

	private void addSortRestrictions(Criteria claimCriteria,
			ClaimSearchCriteria claimSearchCriteria, List<String> addedAliases) {
		// Sorting should use LEFT JOIN.
		int joinType = CriteriaSpecification.LEFT_JOIN;
		Map<String, String> sortCriteria = claimSearchCriteria
				.getSortCriteria();
		for (String propertyName : sortCriteria.keySet()) {
			//Format of criteria key for enum field - enum:enumType:propertyName
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							(propertyName.startsWith("enum:")?propertyName.split(":")[2]:propertyName),
							joinType, addedAliases);
			String sortDirection = sortCriteria.get(propertyName);
			final Order sortOrder = getSortOrder(processedProperty,
					sortDirection);
			claimCriteria.addOrder(sortOrder);
		}
	}

	private Order getSortOrder(String propertyName, String sortDirection) {
		if ("asc".equalsIgnoreCase(sortDirection)) {
			return Order.asc(propertyName);
		} else {
			return Order.desc(propertyName);
		}
	}

	private void addFilterRestrictions(Criteria claimCriteria,
			ClaimSearchCriteria claimSearchCriteria, List<String> addedAliases) {
		Map<String, String> filterCriteria = claimSearchCriteria
				.getFilterCriteria();
		for (String propertyName : filterCriteria.keySet()) {
			if (propertyName.endsWith("Date"))
				continue;
			String filterData = filterCriteria.get(propertyName);
			String enumType = null;
			//Format of criteria key for enum field - enum:enumType:propertyName
			if(propertyName.startsWith("enum:")) {
				String[] parts = propertyName.split(":");
				enumType = parts[1];
				propertyName = parts[2];
			}
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria, propertyName,
							addedAliases);
			if (StringUtils.hasText(filterData)) {
				if(enumType != null) {
					claimCriteria.add(Restrictions.in(
							processedProperty, getEnumsForFilterRestriction(enumType, filterData, claimSearchCriteria.getShowClaimStatusToDealer())));
				}
				else if(filterData.equalsIgnoreCase("true")||filterData.equalsIgnoreCase("false")){
					claimCriteria.add(Restrictions.eq(processedProperty, new Boolean(filterData.toLowerCase())));
				}
				else if(processedProperty.contains("ncr") &&
						(!filterData.equalsIgnoreCase("true")||!filterData.equalsIgnoreCase("false"))){
					claimCriteria.add(Restrictions.eq(processedProperty, null));
				}
				else {
					// Filters are partial-search (LIKE) based.
					SimpleExpression filterExpression = Restrictions.like(
					// Filters are always AND'ed together.
							processedProperty, filterData, MatchMode.START)
							.ignoreCase();
					claimCriteria.add(filterExpression);
				}
			}
		}
		getSQLFilterCriteriaForDate(claimCriteria,filterCriteria);
	}

	public void getSQLFilterCriteriaForDate(Criteria claimCriteria, Map<String, String> filterCriteria){
		if (filterCriteria.size() > 0) {
            for (Map.Entry<String, String> filterOption : filterCriteria.entrySet()) {
                String thePropertyExpression = filterOption.getKey();
                String value = filterOption.getValue();
                if(thePropertyExpression.endsWith("Date")&& StringUtils.hasText(value))
                	getCriteriaHelper().dateLike(claimCriteria, thePropertyExpression, value);
              }
        }
    }

	private Collection getEnumsForFilterRestriction(String enumType, String filterData, boolean showClaimStatusToDealer) {
		if("ClaimState".equals(enumType))
			return ClaimState.getStatesStartingWith(filterData, showClaimStatusToDealer);
		return null;
	}

	private void buildStatusDateCriteria(Criteria claimCriteria,
                                         ClaimSearchCriteria searchCriteria, String statusProperty,
                                         String dateProperty, Object fromDate, Object toDate, List<String> addedAliases) {
		if(dateProperty != null)
			getCriteriaHelper().dateRangeIfNotNull(claimCriteria, dateProperty, fromDate, toDate);
		List<ClaimState> stateList = new ArrayList<ClaimState>();
		if(searchCriteria.getInProgressState() != null && searchCriteria.getInProgressState())
			stateList.addAll(searchCriteria.getStateListInProgress());
		if (searchCriteria.getStatesList().size() > 0)
			stateList.addAll(searchCriteria.getStatesList());
		if(stateList.size() > 0)    {
            String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria, statusProperty, addedAliases);            
            	claimCriteria.add(Restrictions.in(processedProperty, stateList));            
        }
	}

	private void buildStatusDateCriteria(DetachedCriteria claimCriteria,
			ClaimSearchCriteria searchCriteria, String statusProperty,
			String dateProperty, Object fromDate, Object toDate) {
		if(dateProperty != null)
			getCriteriaHelper().dateRangeIfNotNull(claimCriteria, dateProperty, fromDate, toDate);
		List<ClaimState> stateList = new ArrayList<ClaimState>();
		if(searchCriteria.getInProgressState() != null && searchCriteria.getInProgressState())
			stateList.addAll(searchCriteria.getStateListInProgress());
		if (searchCriteria.getStatesList().size() > 0)
			stateList.addAll(searchCriteria.getStatesList());
		if(stateList.size() > 0)
			claimCriteria.add(Restrictions.in(statusProperty, stateList));
	}

	private List<String> buildSearchCriteria(
			final ClaimSearchCriteria searchCriteria, Criteria claimCriteria,Session session) {

		List<String> addedAliases = new ArrayList<String>(10);

		if (searchCriteria.getSelectedBusinessUnits() != null
				&& (searchCriteria.getSelectedBusinessUnits().length>0)) {
			claimCriteria.add(Restrictions.in("businessUnitInfo",
					searchCriteria.getSelectedBusinessUnits()));
		}

		if (StringUtils.hasText(searchCriteria.getClaimNumber())) {
			getCriteriaHelper().likeIfNotNull(claimCriteria,
					"claimNumber", searchCriteria.getClaimNumber().toUpperCase());
		}
		
		if (StringUtils.hasText(searchCriteria.getHistoricalClaimNumber())) {
			getCriteriaHelper().ilikeIfNotNull(claimCriteria,
					"histClmNo", searchCriteria.getHistoricalClaimNumber().concat("%"));
		}

		if (StringUtils.hasText(searchCriteria.getAuthNumber())) {
			getCriteriaHelper().likeIfNotNull(claimCriteria,
					"authNumber", searchCriteria.getAuthNumber().concat("%"));
		}
		
        if (StringUtils.hasText(searchCriteria.getWorkOrderNumber())) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.workOrderNumber", addedAliases);
           getCriteriaHelper().subStringStartsIfNotNull(claimCriteria,
                    processedProperty, searchCriteria.getWorkOrderNumber().concat("%"));
        }
		
		if (StringUtils.hasText(searchCriteria.getInvoiceNumber())) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.invoiceNumber", addedAliases);
			getCriteriaHelper().subStringStartsIfNotNull(claimCriteria,
                    processedProperty, searchCriteria.getInvoiceNumber().concat("%"));
		}

		if (searchCriteria.getClaimType()!= null && (!(searchCriteria.getClaimType().isEmpty()))) {
			claimCriteria.add(Restrictions.in("type",
					searchCriteria.getClaimType()));
		}

		if (StringUtils.hasText(searchCriteria.getProductType())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.unserializedItem.product.name",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
                    searchCriteria.getProductType()));
		}
		
		if (StringUtils.hasText(searchCriteria.getProductGroupCode())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.unserializedItem.product.groupCode",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
                    searchCriteria.getProductGroupCode()));
		}
		
		if (StringUtils.hasText(searchCriteria.getGroupCodeForProductFamily())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.unserializedItem.product.isPartOf.groupCode",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
                    searchCriteria.getGroupCodeForProductFamily()));
		}
		
		if (StringUtils.hasText(searchCriteria.getMarketingGroupCode())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"forDealer.marketingGroup",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
                    searchCriteria.getMarketingGroupCode().concat("%")));
		}
				
		// Changes done to fetch both Serialized and non-serialized claims on Model
		if (StringUtils.hasText(searchCriteria.getModelNumber())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.model.name",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getModelNumber()));
		}
		if (StringUtils.hasText(searchCriteria.getCausalPart())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							"activeClaimAudit.serviceInformation.causalPart.brandItems.itemNumber",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getCausalPart().concat("%")));
			//Fix for NMHGSLMS-1281
            //claimCriteria.add(Restrictions.eqProperty("brandItems.brand", "claim.brand"));
        }
		if (StringUtils.hasText(searchCriteria.getDateCode())){
			String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria, "activeClaimAudit.serviceInformation.oemPartReplaced.dateCode", addedAliases);
			getCriteriaHelper().subStringStartsIfNotNull(claimCriteria, processedProperty, searchCriteria.getDateCode().concat("%"));
		}
		if (StringUtils.hasText(searchCriteria.getFaultCode())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							"activeClaimAudit.serviceInformation.faultCode", addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getFaultCode()));
		}

		if (StringUtils.hasText(searchCriteria.getSerialNumber())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.referredInventoryItem.serialNumber",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
                    searchCriteria.getSerialNumber().concat("%")));

		}
	
		if (StringUtils.hasText(searchCriteria.getVinNumber())) {
			String processedProperty = getCriteriaHelper()
			.processNestedAssociations(
					      claimCriteria,
					      "claimedItems.vinNumber",
					      addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getVinNumber().concat("%")));
		}
		if (StringUtils.hasText(searchCriteria.getCreditMemoNumber())) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							"claimAudits.payment.activeCreditMemo.creditMemoNumber",
							addedAliases);
			claimCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getCreditMemoNumber().concat("%")));
		}
		if (searchCriteria.getBuildForm() != null
				&& searchCriteria.getBuildTo() != null) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.referredInventoryItem.builtOn",
							addedAliases);
			getCriteriaHelper().dateRangeIfNotNull(claimCriteria, processedProperty,
					searchCriteria.getBuildForm(), searchCriteria.getBuildTo());
		}

		/*
		 * boolean flag = false; if(flag){ String processedProperty =
		 * getCriteriaHelper() .processNestedAssociations( claimCriteria,
		 * "claimedItems.itemReference.referredInventoryItem.builtOn",
		 * addedAliases); getCriteriaHelper().eqIfNotNull(claimCriteria,
		 * processedProperty, searchCriteria.getBuildForm()); }
		 *
		 * if(flag){
		 * claimCriteria.add(Restrictions.eq("claimedItems.itemReference.referredInventoryItem.builtOn",
		 * searchCriteria.getBuildForm())); }
		 */

		if (searchCriteria.getManufacturingSite() != null) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"claimedItems.itemReference.referredInventoryItem.manufacturingSiteInventory.id",
							addedAliases);
			claimCriteria.add(Restrictions.in(processedProperty, searchCriteria
					.getManufacturingSite()));
		}

        if (searchCriteria.getChildDealers() != null && (searchCriteria.getChildDealers().length>0)) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							"forDealer.id",
							addedAliases);
			claimCriteria.add(Restrictions.in(processedProperty, searchCriteria
					.getChildDealers()));
		}

		if (searchCriteria.getServiceMangerRequest() != null) {
			getCriteriaHelper().eqIfNotNull(claimCriteria, "serviceManagerRequest",
					searchCriteria.getServiceMangerRequest().booleanValue());
		}

        if (searchCriteria.getWarrantyOrderRequest() != null) {
            getCriteriaHelper().eqIfNotNull(claimCriteria, "warrantyOrder",
                    searchCriteria.getWarrantyOrderRequest().booleanValue());
        }

		if(searchCriteria.getOfDate() != null && searchCriteria.getOfDate().equalsIgnoreCase("dateLastModified")) {
			if(searchCriteria.getOfClaimStatus() == null || searchCriteria.getOfClaimStatus().equalsIgnoreCase("currentState")) {
				String auditDate = getCriteriaHelper().processNestedAssociations(
						claimCriteria,"claimAudits.updatedTime",addedAliases);
				DetachedCriteria dc = DetachedCriteria.forClass(ClaimAudit.class, "claimAudit")
						.add(Restrictions.eqProperty("claimAudit.forClaim", "claim.id"))
						.add(Restrictions.eq("claimAudit.multiClaimMaintenance", false));
				dc.setProjection(Projections.max("claimAudit.id"));
				claimCriteria.add(Subqueries.propertyIn("claimAudits.id", dc));
				buildStatusDateCriteria(claimCriteria, searchCriteria, "activeClaimAudit.state", auditDate,
						getDateForCalendarDate(searchCriteria.getFromDate()),
						getDateForCalendarDate(searchCriteria.getToDate()==null
								? null : searchCriteria.getToDate().plusDays(1)), addedAliases);
			} else {
				DetachedCriteria dc = DetachedCriteria.forClass(ClaimAudit.class, "claimAudit")
						.add(Restrictions.eqProperty("claimAudit.forClaim", "claim.id"))
						.add(Restrictions.eq("claimAudit.multiClaimMaintenance", false));
				buildStatusDateCriteria(dc, searchCriteria, "claimAudit.previousState", "claimAudit.updatedTime",
						getDateForCalendarDate(searchCriteria.getFromDate()),
						getDateForCalendarDate(searchCriteria.getToDate()==null
								? null : searchCriteria.getToDate().plusDays(1)));
				dc.setProjection(Projections.property("id"));
				claimCriteria.add(Subqueries.exists(dc));
			}
		} 
		else if (searchCriteria.getOfDate() != null && searchCriteria.getOfDate().equalsIgnoreCase("lastUpdatedOnDate")) {
			/*
			 * FIX for SLMSPROD-848. Without this else-if, the system generated class cast exception as CaelndarDate
			 * cannot be casted to util.Date. Hence, conversion done from CalendarDate to Date when this option
			 * is selected.
			 */
			buildStatusDateCriteria(claimCriteria, searchCriteria, "activeClaimAudit.state", searchCriteria.getOfDate(),
					getDateForCalendarDate(searchCriteria.getFromDate()),
					getDateForCalendarDate(searchCriteria.getToDate()),
					addedAliases);
		}
		else {
			buildStatusDateCriteria(claimCriteria, searchCriteria, "activeClaimAudit.state", searchCriteria.getOfDate(),
					searchCriteria.getFromDate(), searchCriteria.getToDate(), addedAliases);
		}
		
	    boolean partsReplacedInstalledSectionEnabled = configParamService
		.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
				.getName());
		if (searchCriteria.getPartsReturnStatusList().size() > 0) {
			String partReplacedtableToBeUsed = "activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced.partReturns.status";
			if(partsReplacedInstalledSectionEnabled) {
				partReplacedtableToBeUsed =  "activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.partReturns.status" ;
			}
			
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(
							claimCriteria,
							partReplacedtableToBeUsed,
							addedAliases);
			claimCriteria.add(Restrictions.in(processedProperty, searchCriteria
					.getPartsReturnStatusList()));
		}

		// Fix for ESESA-1432
		if (searchCriteria.getUserIds() != null
                && searchCriteria.getUserIds().length > 0) {
          Long[] id = searchCriteria.getUserIds();
          final org.hibernate.type.Type[] types = new org.hibernate.type.Type[id.length];
          java.util.Arrays.fill(types, org.hibernate.Hibernate.LONG); 
          final StringBuilder questionMarks= new StringBuilder();
          for(int j=0;j<id.length;j++){
                if(j>0) 
                      questionMarks.append(",");
            questionMarks.append("?");
          }
          claimCriteria.add(Restrictions.disjunction().add(Restrictions.in("filedBy.id", 
            searchCriteria.getUserIds())).add(Restrictions.in("lastUpdatedBy.id",
            searchCriteria.getUserIds())).add(Restrictions.sqlRestriction (
            "claim_number in (select distinct(c.claim_number) from Claim c , "+
            " JBPM_TASKINSTANCE taskInstance "+
	        " where taskInstance.isopen_ = 1 "+
	        " and  taskInstance.actorid_ in (select login from org_user where id IN "+
            " ("+questionMarks.toString()+")))" ,id,types)));
    }
		
		if (searchCriteria.getAssignToUserIds() != null
                && searchCriteria.getAssignToUserIds().length > 0) {
          Long[] id = searchCriteria.getAssignToUserIds();
          final org.hibernate.type.Type[] types = new org.hibernate.type.Type[id.length];
          java.util.Arrays.fill(types, org.hibernate.Hibernate.LONG); 
          final StringBuilder questionMarks= new StringBuilder();
          for(int j=0;j<id.length;j++){
                if(j>0) 
                      questionMarks.append(",");
            questionMarks.append("?");
          }
          claimCriteria.add(Restrictions.sqlRestriction ("claim_number in (select distinct(c.claim_number) from Claim c , claim_audit ca "+
            " where c.active_claim_Audit = ca.id and ca.assign_to_user IN " +
            " ("+questionMarks.toString()+"))" ,id,types));
    }

		if (StringUtils.hasText(searchCriteria.getDealerNumber())) {

			String processedProperty = getCriteriaHelper()
			.processNestedAssociations(
					claimCriteria,
					"forDealer.serviceProviderNumber",
					addedAliases);
            if(isThirdPartyApplicable(searchCriteria.getLoggedInUser())){
                claimCriteria.add(Restrictions.sqlRestriction(
                        " ( for_dealer in (select id from third_party) and "+
                        " filed_by in (select org_users.org_user from Service_Provider org,org_user_belongs_to_orgs org_users " +
                        "	where org.id=org_users.belongs_to_organizations and org.service_provider_number = ?)) ) ",
                        new String[]{searchCriteria.getDealerNumber(),searchCriteria.getDealerNumber()},
                        new org.hibernate.type.Type[]{Hibernate.STRING,Hibernate.STRING}
                ));
            }
            else{
            	claimCriteria.add(Restrictions.eq(processedProperty,
				searchCriteria.getDealerNumber()));
            }
		}

		if (StringUtils.hasText(searchCriteria.getDealerName())) {
			String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria,
					"forDealer.name",addedAliases);
			/*claimCriteria.add(Restrictions.eq(processedProperty, searchCriteria.getDealerName()));*/
		    getCriteriaHelper().ilikeIfNotNull(claimCriteria, processedProperty, searchCriteria.getDealerName().concat("%"));
		}

                if (StringUtils.hasText(searchCriteria.getDealerGroup())) {
                    List<ServiceProvider> providersInGroup =
                            dealerGroupRepository.findProvidersAtAllLevelForGroup(
                                searchCriteria.getDealerGroup());
                    int numProviders = providersInGroup.size();

                    if(numProviders > 0) {
                    	String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria,
    							"forDealer.id", addedAliases);
                    	
                        int i = 1;
                        boolean belongsToGroup = "true".equalsIgnoreCase(searchCriteria.getBelongsToDealerGroup());
                        Junction paritionedInQuery = belongsToGroup ? Restrictions.disjunction():Restrictions.conjunction();

                        List<Long> providerIds =
                                new ArrayList<Long>(numProviders);
                        for(ServiceProvider provider : providersInGroup) {
                            providerIds.add(provider.getId());

                            // Oracle supports only 1000 elements inside an "IN"
                            // query. Hence we use multiple IN queries joined
                            // by an OR clause.
                            if(i++ == ORACLE_IN_QUERY_LIMIT) {
                            	if(belongsToGroup) {
                            		paritionedInQuery.add(Restrictions.in(processedProperty, providerIds));
                            	}else {
                            		paritionedInQuery.add(Restrictions.not(Restrictions.in(processedProperty, providerIds)));
                            	}
                                providerIds.clear();
                                i = 1;
                            }
                        }

                        if(i > 1) {
                        	if(belongsToGroup) {
                        		paritionedInQuery.add(Restrictions.in(processedProperty, providerIds));
                        	}else {
                        		paritionedInQuery.add(Restrictions.not(Restrictions.in(processedProperty, providerIds)));
                        	}
                        }

                        claimCriteria.add(paritionedInQuery);
                        
                    } else {
                        // The group doesn't have any dealers, and hence the
                        // search shouldn't return any results. Since there
                        // is no way we can short circuit this flow right
                        // now and just return empty results, we are putting
                        // in a deliberate fallacy below, to make sure that
                        // no claims are returned when the criteria gets
                        // executed eventually.
                        claimCriteria.add(Restrictions.eq("id",
                                Long.MIN_VALUE));
                    }
                }

        if (searchCriteria.getAcceptanceReason() != null
                && searchCriteria.getAcceptanceReason().length > 0) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.acceptanceReason.id", addedAliases);
            getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
                    searchCriteria.getAcceptanceReason());
        }
        
        if (searchCriteria.getRejectionReason() != null
                && searchCriteria.getRejectionReason().length > 0) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.rejectionReasons.id", addedAliases);
            getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
                    searchCriteria.getRejectionReason());
        }
        
        if (searchCriteria.getOnHoldReason() != null
                && searchCriteria.getOnHoldReason().length > 0) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.putOnHoldReasons.id", addedAliases);
            getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
                    searchCriteria.getOnHoldReason());
        }
        
        if (searchCriteria.getForwaredReason() != null
                && searchCriteria.getForwaredReason().length > 0) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.requestInfoFromUser.id", addedAliases);
            getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
                    searchCriteria.getForwaredReason());
        }

        if (searchCriteria.getAccountabilityCodeList() != null
                && searchCriteria.getAccountabilityCodeList().length > 0) {
            String processedProperty = getCriteriaHelper().
                    processNestedAssociations(claimCriteria, "activeClaimAudit.accountabilityCode.id", addedAliases);
            getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
                    searchCriteria.getAccountabilityCodeList());
        }
        
      //To get the Audited Claims which are flagged for Manual Review
        if(null != searchCriteria.getManuallyReviewed()){
        	if(searchCriteria.getManuallyReviewed().booleanValue()){
        		claimCriteria.add(Restrictions.eq("manualReviewConfigured", Boolean.TRUE));
        	}else{
        		getCriteriaHelper().eqIfNotNull(claimCriteria, "manualReviewConfigured", null);
        	}
        }

		if (searchCriteria.getResubmitted() != null) { // This is for fetching resubmitted claims
			if (searchCriteria.getResubmitted().booleanValue()) {				
				claimCriteria.add(Restrictions.eq("appealed", Boolean.TRUE));
			} else {
				getCriteriaHelper().eqIfNotNull(claimCriteria, "appealed", null);
			}
		}
		
		if (searchCriteria.getCampaignList() != null
				&& searchCriteria.getCampaignList().length > 0) {
			getCriteriaHelper().inIfNotNull(claimCriteria, "campaign.id",
					searchCriteria.getCampaignList());
		}
		
		if (searchCriteria.getDealerGroups() != null
				&& searchCriteria.getDealerGroups().length > 0) {
			Set<Long> id = new HashSet<Long>();
			for(Long dealerGroupId : searchCriteria.getDealerGroups()){
				List<ServiceProvider> dealerGroupsList = dealerGroupRepository.findProvidersAtAllLevelForGroupByGroupId(dealerGroupId);
				if(CollectionUtils.isNotEmpty(dealerGroupsList)){
					for(ServiceProvider serviceProvider: dealerGroupsList){
						id.add(serviceProvider.getId());
					}
				}
			} 
			final org.hibernate.type.Type[] types = new org.hibernate.type.Type[id.size()];
			java.util.Arrays.fill(types, org.hibernate.Hibernate.LONG);
			final StringBuilder questionMarks = new StringBuilder();
			if(CollectionUtils.isNotEmpty(id)){
			for (int j = 0; j < id.size(); j++) {
				if (j > 0)
					questionMarks.append(",");
				questionMarks.append("?");
			}
		}
	 	if(CollectionUtils.isNotEmpty(id)){
	    	claimCriteria.add(Restrictions.sqlRestriction("for_dealer" +
					" IN"+ " ("+ questionMarks.toString() + ")",id.toArray(), types));
			}
		}
		
		if (searchCriteria.getPartGroups() != null
				&& searchCriteria.getPartGroups().length > 0) {
			Set<Long> id = new HashSet<Long>();
			for(Long partGroupId : searchCriteria.getPartGroups()){
				List<Item> items = itemGroupRepository.findItemsAtAllLevelForGroup(partGroupId);
				if(CollectionUtils.isNotEmpty(items)){
					for(Item item: items){
						id.add(item.getId());
					}
				}
			}	
			final org.hibernate.type.Type[] types = new org.hibernate.type.Type[id.size()];
			java.util.Arrays.fill(types, org.hibernate.Hibernate.LONG);
			final StringBuilder questionMarks = new StringBuilder();
			for (int j = 0; j < id.size(); j++) {
				if (j > 0)
					questionMarks.append(",");
				questionMarks.append("?");
			}
			if(CollectionUtils.isNotEmpty(id)){
			claimCriteria.add(Restrictions.sqlRestriction("claim_number in (select distinct(c.claim_number) from Claim c ,claim_audit ca, service_information s where ca.id = c.active_claim_audit and ca.service_information = s.id and s.causal_part in " +
					"("+ questionMarks.toString() + "))",id.toArray(), types));
			}
		}

		if (searchCriteria.getFailedRules() != null
				&& searchCriteria.getFailedRules().length > 0) {

			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							"activeClaimAudit.ruleFailures.ruleNumber", addedAliases);
			getCriteriaHelper().inIfNotNull(claimCriteria, processedProperty,
					searchCriteria.getFailedRules());
		}

		if (searchCriteria.getNotFailed() != null
				&& searchCriteria.getNotFailed().length > 0) {
			String processedProperty = getCriteriaHelper()
					.processNestedAssociations(claimCriteria,
							"activeClaimAudit.ruleFailures.ruleNumber", addedAliases);
			claimCriteria.add(Restrictions.not(Restrictions.in(
					processedProperty, searchCriteria.getNotFailed())));
		}

		if (searchCriteria.getRestrictSearch() != null
				&& searchCriteria.getRestrictSearch().booleanValue()) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(new java.util.Date());
			cal.add(Calendar.YEAR, -3);
			claimCriteria.add(Restrictions.gt("filedOnDate", CalendarDate.date(
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
							.get(Calendar.DATE))));
		}

        if (searchCriteria.getTotalAmountClaim() != null) {
            String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria,
                    "activeClaimAudit.payment.lineItemGroups.name", addedAliases);
            claimCriteria.add(Restrictions.eq(processedProperty, Section.TOTAL_CLAIM));
            DetachedCriteria amountCriteria = DetachedCriteria.forClass(LineItemGroup.class, "lineItemGroup");
            amountCriteria.add(Restrictions.sqlRestriction("total_credit_curr = ?"
                    + " and total_credit_amt " + searchCriteria.getTotalAmountOperator() + " ?",
                    new Object[]{searchCriteria.getTotalAmountClaim().breachEncapsulationOfCurrency(),
                            searchCriteria.getTotalAmountClaim().breachEncapsulationOfAmount()},
                    new org.hibernate.type.Type[]{Hibernate.CURRENCY, Hibernate.BIG_DECIMAL}));
            amountCriteria.setProjection(Projections.property("id"));
            claimCriteria.add(Subqueries.propertyIn("lineItemGroups.id", amountCriteria));
        }

        if (searchCriteria.getStatesList().isEmpty()) { // the following restriction is required only if serach is not based on claim state
            String processedProperty = getCriteriaHelper().processNestedAssociations(claimCriteria, "activeClaimAudit.state", addedAliases);
            claimCriteria.add(Restrictions.not(Restrictions.eq(processedProperty, ClaimState.DRAFT_DELETED)));
        }
        
		if (searchCriteria.isDuplicateClaim()) {
			claimCriteria
					.add(Restrictions
							.sqlRestriction(" claim_number in( select c.claim_number from claim c, claim_audit_rule_failures crf, failed_rule fr, domain_rule dr, domain_predicate dp "
									+ "where c.active_claim_audit=crf.claim_audit and fr.rule_detail in crf.rule_failures and fr.rule_number = dr.rule_number "
									+"and dr.predicate=dp.id and dp.context = ?)", AdminConstants.CLAIM_DUPLICACY_RULES, Hibernate.STRING));
		}
		return addedAliases;
	}

    /**
     * Perf fix: Added logic to put third party clause only for Hussmann BU.
     */
	
	private boolean isThirdPartyApplicable(User user){
		return false;
	}
        
	private Date getDateForCalendarDate(CalendarDate calendarDate) {
		if(calendarDate == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return sdf.parse(calendarDate.toString("MM/dd/yyyy"));
		} catch (ParseException e) {
			return null;
		}
	}

	public List<Claim> findClaimsWithPartsShipped(String buQueryAppended) {
		StringBuffer  query = new StringBuffer(" select distinct(claim) from Claim claim , TaskInstance taskInstance"
				+ " where taskInstance.isOpen = true "
				+ " and taskInstance.claimId = claim.id"
				+ " and claim.prtShpNtrcvd=false "
				+ " and claim.activeClaimAudit.state not in ('DENIED','ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','PENDING_PAYMENT_SUBMISSION','PENDING_PAYMENT_RESPONSE') "
				+ " and taskInstance.create in (select max(create) from TaskInstance where id in (taskInstance.id) "
				+ " and  taskInstance.name in ('Parts Shipped') and isOpen = true)"
				+ " and ( ");
		return getSession().createQuery(query.append(buQueryAppended).toString()+" )").list();
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findProductTypes(final String itemGroupType) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select parent from ItemGroup parent join parent.scheme.purposes purpose "
												+ " where parent.itemGroupType= :itemGroupType " +
												 " and purpose.name='PRODUCT STRUCTURE' "
												+ " order by parent.name ")
								.setParameter("itemGroupType", itemGroupType)
								.list();
					}
				});
	}

	public String findClaimByPartReturnId(final Long partReturnId)
	{
		return (String)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createSQLQuery(
								"select c.id from part_return pr ,base_part_return bpr,oem_part_replaced opr, service_information si,service s, service_oemparts_replaced sor,claim c where pr.id = bpr.id and bpr.oem_part_replaced = opr.id and opr.id = sor.oemparts_replaced and sor.service = s.id and s.id = si.service_detail and si.id = c.service_information and bpr.id =2820").uniqueResult();
			}
		});
	}

    public void setDealerGroupRepository(DealerGroupRepository dealerGroupRepository) {
        this.dealerGroupRepository = dealerGroupRepository;
    }

    public List<Claim> findClaimToReassign()	{
		return (List<Claim>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"select claim from Claim claim where claim.activeClaimAudit.state in ('PROCESSOR_REVIEW','ON_HOLD_FOR_PART_RETURN')").list();
			}
		});
	}

    public List<Claim> findSMRClaimToReassign()	{
		return (List<Claim>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"select claim from Claim claim where claim.activeClaimAudit.state in ('SERVICE_MANAGER_REVIEW')").list();
			}
		});
	}

    public List<Claim> findClaimsForRecovery(){
		return (List<Claim>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"select claim from Claim claim where claim.activeClaimAudit.internalComment like 'TAVRECOVERY: %'")
								.list();
			}
		});
	}

	public List<Claim> findClaimsForRecovery(final int pageNumber,final int pageSize){
		return (List<Claim>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"select claim from Claim claim where claim.activeClaimAudit.internalComment like 'TAVRECOVERY: %'" +
								" order by claim.id ")
								.setFirstResult((pageNumber-1)*pageSize)
								.setMaxResults(pageSize)
								.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public PageResult<Claim> findClaimsForRecovery(final ListCriteria listCriteria){
		 PageSpecification ps = listCriteria.getPageSpecification();
	     Map<String,Object> parameterMap = new HashMap<String,Object>(2);
	     StringBuilder sb = new StringBuilder();
	     sb.append("from Claim claim join claim.claimedItems as claimedItems where claim.pendingRecovery = true ");
	     String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
	     if(org.apache.commons.lang.StringUtils.isNotBlank(paramterizedFilterCriteria)){
	    	 sb.append(" and ").append(paramterizedFilterCriteria);
	    	 parameterMap.putAll(listCriteria.getParameterMap());
	     }
	     return findPageUsingQuery(sb.toString() , 
	    		 listCriteria.getSortCriteriaString(), "select claim ", ps, new QueryParameters(parameterMap));
	}
	
	@SuppressWarnings("unchecked")
	public List<Claim> findClaimsForIds(final List<Long> claimIds) {
		return (List<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {
					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria claimCriteria = session.createCriteria(Claim.class);
						claimCriteria.add(Restrictions.in("id",	claimIds));
						claimCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return claimCriteria.list();
					};
				});		
	}
	@SuppressWarnings("unchecked")
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart, final Claim claim){
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<Claim> resultantClaims =  session
						.createQuery(
								" select claim from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join replacedInstalledParts.hussmanInstalledParts installedParts " +
								" where installedParts.item.id = :itemParam" +
								" and upper(installedParts.serialNumber) = :serialNumberParam" +
								" and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED','DRAFT_DELETED','DEACTIVATED')" +
								" and claim != :claimParam").
								setParameter("itemParam", installedPart.getItem().getId()).
								setParameter("claimParam", claim).
								setParameter("serialNumberParam", installedPart.getSerialNumber().toUpperCase())
								.list();
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart){
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<Claim> resultantClaims =  session
						.createQuery(
								" select claim from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join replacedInstalledParts.hussmanInstalledParts installedParts " +
								" where installedParts.item.id = :itemParam" +
								" and upper(installedParts.serialNumber) = :serialNumberParam" +
								" and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED','DRAFT_DELETED','DEACTIVATED')").
								setParameter("itemParam", installedPart.getItem().getId()).
								setParameter("serialNumberParam", installedPart.getSerialNumber().toUpperCase())
								.list();
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	}	

	public Boolean isAnyClaimWithInstalledParts(final List<String> installedParts){
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<Claim> resultantClaims =  session
						.createQuery(
								" select claim from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail " +
                                " join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join replacedInstalledParts.hussmanInstalledParts installedParts " +
								" where upper(installedParts.serialNumber) in (:serialNumberParam)" +
								" and claim.activeClaimAudit.state not in ('DELETED','DRAFT_DELETED','DEACTIVATED')").
								setParameterList("serialNumberParam", toUpperCaseList(installedParts))
								.list();
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	}
    
	public Boolean isAnyClaimWithReplacedParts(final List<String> replacedParts){
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                    String queryString = " select claim from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join " + 
                    " serviceInfo.serviceDetail serviceDetail join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join " + 
                    " replacedInstalledParts.replacedParts replacedPartAlias " +
                    " where claim.activeClaimAudit.state not in ('DELETED','DRAFT_DELETED','DEACTIVATED') " + 
                    " and upper(replacedPartAlias.serialNumber) in (:serialNumberParam)" ;

				List<Claim> resultantClaims =  session
						.createQuery(queryString).
								setParameterList("serialNumberParam", toUpperCaseList(replacedParts))
								.list();
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	}    
    
    private List<String> toUpperCaseList(List<String> list){
        if(list != null && !list.isEmpty()){
            List<String> ucList = new ArrayList<String>(list.size());
            for (String s : list) {
                ucList.add(s.toUpperCase());
            }
            return ucList;
        }
        return Collections.EMPTY_LIST;
    }
    
	public boolean areOpenClaimsPresentForServiceProvider(final String serviceProviderNumber) {
		// TODO Auto-generated method stub
		long rowCount =  (Long)getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
						
					Query q =  session.createQuery("select count(claim.id) from Claim claim where claim.forDealer.serviceProviderNumber=:serviceProviderNumber "
						+ " and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DRAFT','DRAFT_DELETED','DELETED','DEACTIVATED') ")
						.setString("serviceProviderNumber", serviceProviderNumber);
					return q.uniqueResult();
				};
				});
		return (rowCount > 0)? true : false;
	}

	@SuppressWarnings("unchecked")
	public Boolean isAnyOpenClaimWithPolicyOnInventoryItem(final InventoryItem inventoryItem, final RegisteredPolicy policy) {
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {			
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<Claim> resultantClaims =  session
						.createQuery(
								" select claim from Claim claim join claim.claimedItems as claimedItems join claimedItems.applicablePolicy applicablePolicy" +
								" where (claimedItems.itemReference.referredInventoryItem.id = :invItemId" +
								" or claim.partItemReference.referredInventoryItem.id = :invItemId)" +
								" and applicablePolicy.registeredPolicy.id = :policyDefinitionIdParam" +
								" and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED','DRAFT_DELETED','DEACTIVATED')").
								setParameter("invItemId", inventoryItem.getId()).
								setParameter("policyDefinitionIdParam", policy.getId())
								.list();
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	}
	
	public ConfigParamService getConfigParamService() {
		return this.configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public Boolean isAnyOpenClaimWithReplacedPart(final OEMPartReplaced oemPartReplaced,final Claim claim){	    	
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String queryString = " select claim from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts join replacedInstalledParts.replacedParts replacedPartAlias " +
								" where replacedPartAlias.itemReference.referredInventoryItem.id = :replacedInvItemParam" +								
								" and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED','DRAFT_DELETED','DEACTIVATED')" ;
				if(claim != null && claim.getId() != null){
					queryString = queryString + " and claim != :claimParam";
				}
				Query query = session.createQuery(queryString);				
				query.setParameter("replacedInvItemParam", oemPartReplaced.getItemReference().getReferredInventoryItem().getId());				
				if(claim != null && claim.getId() != null){
					query.setParameter("claimParam", claim);
				}	
				List<Claim> resultantClaims = query.list(); 
			if (resultantClaims != null && resultantClaims.size() > 0)
				return true;
			else 
				return false;
			}
		});
	    	
	}

	@SuppressWarnings("unchecked")
	public Boolean isAnyActiveClaimOnMajorComponent(final InventoryItem inventoryItem) {
		return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Long numberOfRows = (Long)session
								.createQuery(
										" select count(claim) from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail"
												+ " join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts"
												+ " join replacedInstalledParts.replacedParts replacedPart"
												+ " where replacedPart.itemReference.referredInventoryItem.id = :invItemIdParam"
												+ " and claim.activeClaimAudit.state not in ('DELETED','DRAFT_DELETED','DEACTIVATED')")
								.setParameter("invItemIdParam",
										inventoryItem.getId()).uniqueResult();
						if (numberOfRows > 0) {
							return true;
						} else {
							numberOfRows = (Long)session
									.createQuery(
											" select count(claim) from Claim claim join claim.activeClaimAudit.serviceInformation serviceInfo join serviceInfo.serviceDetail serviceDetail"
													+ " join serviceDetail.hussmanPartsReplacedInstalled replacedInstalledParts"
													+ " join replacedInstalledParts.hussmanInstalledParts installedParts "
													+ " where"
													+ "(installedParts.item.id = :itemParam"
													+ " and upper(installedParts.serialNumber) = :serialNumberParam)"
													+ " and claim.activeClaimAudit.state not in ('DELETED','DRAFT_DELETED','DEACTIVATED')")
									.setParameter("itemParam",
											inventoryItem.getOfType().getId())
									.setParameter(
											"serialNumberParam",
											inventoryItem.getSerialNumber()
													.toUpperCase()).uniqueResult();
							if (numberOfRows > 0) {
								return true;
							} else {
								numberOfRows =(Long) session
										.createQuery(
												" select count(claim) from Claim claim"
														+ " where"
														+ " claim.partItemReference.referredInventoryItem.id = :invItemIdParam"
														+ " and claim.activeClaimAudit.state not in ('DELETED','DRAFT_DELETED','DEACTIVATED')")
										.setParameter("invItemIdParam",
												inventoryItem.getId()).uniqueResult();
								if (numberOfRows> 0) {
									return true;
								} else {
									return false;
								}
							}
						}
					}
				});
	}
		
	
	@SuppressWarnings("unchecked")
	public List<Long> findClaimsForBOMUpdation(){
		return (List<Long>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
                Criteria c = session.createCriteria(Claim.class);
                c.setProjection(Projections.id());
                c.add(Restrictions.eq("bomUpdationNeeded", true));
                return c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Long findAllClaimsCountForMultiClaimMaintenance(String queryWithoutSelect, final QueryParameters params) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
        countQuery.append(queryWithoutSelect);
        
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQuery.toString());
				populateQueryParams(query, params);
				return query.uniqueResult();
				
            }
        });
        return numberOfRows;
	}

	public ClaimedItem findClaimedItem(final Long claimId, final Long inventoryId) {
		return (ClaimedItem) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
								.createQuery(
										"from ClaimedItem claimedItem where claimedItem.claim.id = :claimId and " +
										"claimedItem.itemReference.referredInventoryItem.id = :inventoryId")
								.setParameter("claimId", claimId).setParameter(
										"inventoryId", inventoryId)
								.uniqueResult();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findTasksAssingedToUser(final String userId) {
		
		List<String> taskInstance =  (List) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
						
					Query q =  session.createQuery("SELECT ti.name FROM TaskInstance ti WHERE ti.isOpen = true " +
							" AND ti.actorId = :actorId").setString("actorId", userId);
					return q.list();
				};
				});
		
		return taskInstance;
	}

	@SuppressWarnings("unchecked")
	public Claim findLastFiledClaim() {
		// TODO Auto-generated method stub
		return (Claim) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {				
				return session
						.createQuery(
								"from Claim claim where claim.id=(select MAX(c.id) from Claim c)");
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Claim> getClaimsForItalyClaimNotification(final Date recentTime){
		final String query = "select claim from Claim claim, InventoryItem item, ClaimedItem claimedItem, ListOfValues lovs "
			+ " where claim.id=claimedItem.claim and claimedItem.itemReference.referredInventoryItem = item.id and item.manufacturingSiteInventory = lovs.id"
			+ " and lovs.code in (:manufacturingLocations) and claim.activeClaimAudit.updatedTime is not null and claim.activeClaimAudit.updatedTime >= :recentTime"
			+ " and claim.activeClaimAudit.state != 'DRAFT' and claim.type in (:types) and claim.brand in (:brands) order by claim.activeClaimAudit.updatedTime asc";
		return (List<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						session.disableFilter("bu_name");
						//where type in ('MCHINE','CAMPAIGN') and brand in ('YALE','HYSTER')
						List<String> types = new ArrayList(); 
						types.add("MACHINE");
						types.add("CAMPAIGN");
						List<String> brands = new ArrayList(); 
						brands.add("YALE");
						brands.add("HYSTER");
						List<String> manufacturingLocations = new ArrayList();
						manufacturingLocations.add("060");
						manufacturingLocations.add("046");
						return session.createQuery(query).setParameter("recentTime" , recentTime).setParameterList("types", types).setParameterList("brands", brands).setParameterList("manufacturingLocations",manufacturingLocations).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findProductTypesByBrand(final String itemGroupType,
			final String brandType) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select parent from ItemGroup parent join parent.scheme.purposes purpose "
												+ " where parent.itemGroupType= :itemGroupType and (upper(parent.companyType) = upper(:brandType))" +
												 " and purpose.name='PRODUCT STRUCTURE' "
												+ " order by parent.name ")
								.setParameter("itemGroupType", itemGroupType)
								.setParameter("brandType", brandType)
								.list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemGroup> findModelTypesByBrand(final String itemGroupType,
			final String brandType) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select parent from ItemGroup parent join parent.isPartOf product join parent.scheme.purposes purpose "
												+ " where parent.itemGroupType= :itemGroupType and (upper(product.companyType) = upper(:brandType))" +
												 " and purpose.name='PRODUCT STRUCTURE' "
												+ " order by parent.name ")
								.setParameter("itemGroupType", itemGroupType)
								.setParameter("brandType", brandType)
								.list();
					}
				});
	}
	

	public Long getCountOfPendingRecoveryClaims(){
		final StringBuffer countQueryClause = new StringBuffer(" select count(*) from Claim claim where claim.pendingRecovery = true ");
        Long recoveryPendingClaimsCount = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQueryClause.toString());
				return query.uniqueResult();
            }
        });
        return recoveryPendingClaimsCount;
	}
	
	public int updateCreditSubmissionDate(final String claimNumber) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						return session
						.createQuery(
								"update ClaimAudit ca set ca.creditDate = :creditDate" +
								" where ca.creditDate is null and" +
								" ca.state in ('PENDING_PAYMENT_SUBMISSION', 'ACCEPTED_AND_CLOSED', 'DENIED_AND_CLOSED')" +
						" and exists (select 1 from Claim c where c.activeClaimAudit.id = ca.id and c.claimNumber = :claimNumber)")
						.setParameter("creditDate", Clock.today())
						.setParameter("claimNumber", claimNumber)
						.executeUpdate();
					}
				});
	}
	
	public int updateCreditSubmissionDateForAcceptedOrDeniedClaims() {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						return session
								.createQuery(
										"update ClaimAudit ca set ca.creditDate = :creditDate" +
												" where ca.creditDate is null" +
										" and ca.state in ('ACCEPTED_AND_CLOSED', 'DENIED_AND_CLOSED')" +
								" and exists (select 1 from Claim c where c.activeClaimAudit.id = ca.id )")
								.setParameter("creditDate", Clock.today())
								.executeUpdate();
					}
				});
	}

	public int updateEPOerrorMessagesDetails(final Claim claim) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						return session
						.createQuery(
								"update ClaimAudit ca set ca.isPriceFetchDown = :isPriceFetchDown ,ca.isPriceFetchReturnZero=:isPriceFetchReturnZero,ca.priceFetchErrorMessage=:priceFetchErrorMessage" +
								" where ca.id=:id")
						.setParameter("isPriceFetchDown", claim.getActiveClaimAudit().getIsPriceFetchDown())
						.setParameter("isPriceFetchReturnZero", claim.getActiveClaimAudit().getIsPriceFetchReturnZero())
						.setParameter("priceFetchErrorMessage", claim.getActiveClaimAudit().getPriceFetchErrorMessage())
						.setParameter("id", claim.getActiveClaimAudit().getId())
						.executeUpdate();
					}
				});
		
	}
	
	public ItemGroupRepository getItemGroupRepository() {
		return itemGroupRepository;
	}

	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}
	


	@SuppressWarnings("unchecked")
	public List<MarketingGroupsLookup> lookUpMktgGroupCodes(
			final MarketingGroupsLookup lookup, final boolean forProcessor) {
		final String queryString = getQureyStrForMarketingGropusLookUp(forProcessor);
		return (List<MarketingGroupsLookup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(queryString);
						query.setParameter("truckMktgGrpCode",
								lookup.getTruckMktgGroupCode())
								.setParameter("warrantyType",
										lookup.getWarrantyType())
								.setParameter("claimType",
										lookup.getClaimType().toUpperCase());
						if (forProcessor) {
							query.setParameter("dealerMktgGroupCode",
									lookup.getDealerMktgGroupCode());
						}
						return query.list();
					}
				});
	}
	public Location getLocationForDefaultPartReturn(final String defaultPartReturnLocation){
		return (Location) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" from Location loc where loc.code=:defaultPartReturnLocation").
										setParameter("defaultPartReturnLocation", defaultPartReturnLocation)										
										.list().get(0);
					}
				});
	}
	private String getQureyStrForMarketingGropusLookUp(boolean forProcessor) {
		StringBuffer queryString = new StringBuffer();
		queryString
				.append("from MarketingGroupsLookup lookUp where "
						+ "lookUp.truckMktgGroupCode=:truckMktgGrpCode "
						+ "and lookUp.claimType=:claimType and lookUp.warrantyType=:warrantyType ");
		if (forProcessor) {
			queryString
					.append("and lookUp.dealerMktgGroupCode=:dealerMktgGroupCode");
		}

		return queryString.toString();
	}

}

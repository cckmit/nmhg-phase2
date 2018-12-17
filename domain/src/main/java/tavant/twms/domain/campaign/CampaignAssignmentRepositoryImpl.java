 /*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.campaign;

import static org.apache.commons.lang.StringUtils.isBlank;
import tavant.twms.domain.orgmodel.DealerGroupRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

public class CampaignAssignmentRepositoryImpl extends
		GenericRepositoryImpl<CampaignNotification, Long> implements
		CampaignAssignmentRepository {

	public static final int ORACLE_IN_QUERY_LIMIT = 1000;
	private CriteriaHelper criteriaHelper;
	private DealerGroupRepository dealerGroupRepository;
	private DealershipRepository dealershipRepository;
	
	public void saveAllNotifications(List<CampaignNotification> notifications) {
		getHibernateTemplate().saveOrUpdateAll(notifications);
	}

	public void deleteNotificationsForCampaign(Campaign campaign) {
		List<CampaignNotification> notifications = findNotificationsWithNoClaimForCampaign(campaign);
		getHibernateTemplate().deleteAll(notifications);

	}

	
	 public void setDealerGroupRepository(DealerGroupRepository dealerGroupRepository) {
	        this.dealerGroupRepository = dealerGroupRepository;
	    }
	@SuppressWarnings("unchecked")
	private List<CampaignNotification> findNotificationsWithNoClaimForCampaign(
			Campaign campaign) {
		List<CampaignNotification> notifications = getHibernateTemplate()
				.find(
						"from CampaignNotification campaignNotification "
								+ "where campaignNotification.campaign = ? "
								+ "and campaignNotification.claim is null "
								+ "and campaignNotification.campaign.notificationsGenerated = false",
						campaign);
		return notifications;
	}

	@SuppressWarnings("unchecked")
	public List<CampaignNotification> findCampaignNotificationsForCampaign(
			Campaign campaign) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactive");
		List<CampaignNotification> notifications = getHibernateTemplate()
				.find(
						"select distinct cn from CampaignNotification cn "
								+ "where cn.campaign = ? ",
						campaign);
		getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactive");
		return notifications;
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> findCampaignStatus(InventoryItem item)
	{
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactive");
		List<CampaignNotification> notifications = getHibernateTemplate()
				.find(
						"select distinct cn from CampaignNotification cn "
								+ "where item = ? ",
								item);
		getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactive");
		return notifications;
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> findAllCampaignsForNotification() {
		return getHibernateTemplate().find(
				"from Campaign campaign "
						+ "where campaign.notificationsGenerated = false");
	}
	
	public StringBuffer getBaseQueryForCampaign(ServiceProvider dealer){
		if(dealer.isNationalAccount()){
			return new StringBuffer(
					getCampaignNotificationForNationalAccountQuery());
		}else{
			return new StringBuffer(
				getCampaignNotificationForDealerQuery());
		}
	}

	@SuppressWarnings("unchecked")
	public PageResult<CampaignNotification> findAllCampaignNotificationsForDealer(
			final CampaignAssignmentCriteria criteria) {
		final StringBuffer baseQuery = getBaseQueryForCampaign(criteria.getDealer()).append("and campaignNotification.status = 'Active' ");		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealer", criteria.getDealersList());
		params.put("today", Clock.today());
		params.putAll(criteria.getParameterMap());
		if (criteria.isFilterCriteriaSpecified()) {
			String paramterizedFilterCriteria = criteria
					.getParamterizedFilterCriteria();
			baseQuery.append("and " + paramterizedFilterCriteria);
		}
		String sortCriteria = "campaignNotification."+getSortCriteriaString(criteria);
		return findPageUsingQuery(baseQuery.toString(),
				         sortCriteria, criteria
						.getPageSpecification(), params);
	}
	
	@SuppressWarnings("unchecked")
	public PageResult<CampaignNotification> findAllCampaignNotificationRequestsByStatus(
			final CampaignAssignmentCriteria criteria, FieldModUpdateStatus campaignNotificationStatus,boolean isUserADealer,String businessUnitName) {
		Map<String, Object> params = new HashMap<String, Object>();
		final StringBuffer baseQuery = getBaseQueryForCampaignNotifications(criteria.getDealer(),isUserADealer);
		if(isUserADealer)
		{
		params.put("dealer", criteria.getDealersList());
		params.put("today", Clock.today());
		params.put("campaignNotificationStatus", campaignNotificationStatus);
		}
		else
		{
		params.put("today", Clock.today());
		params.put("campaignNotificationStatus", campaignNotificationStatus);
		params.put("businessUnitName",businessUnitName);
		}
		params.putAll(criteria.getParameterMap());
		if (criteria.isFilterCriteriaSpecified()) {
			String paramterizedFilterCriteria = criteria
					.getParamterizedFilterCriteria();
			baseQuery.append("and " + paramterizedFilterCriteria);
		}
		String sortCriteria = "campaignNotification."+getSortCriteriaString(criteria);
		return findPageUsingQuery(baseQuery.toString(),
				         sortCriteria, criteria
						.getPageSpecification(), params);
	}
	
	public StringBuffer getBaseQueryForCampaignNotifications(ServiceProvider dealer,boolean isUserADealer){
		if(isUserADealer){
			return new StringBuffer(getBaseQueryForCampaign(dealer)).append("and campaignNotification.campaignStatus = :campaignNotificationStatus ");
		}else{
			return new StringBuffer(getPendingReviewCampaignsForProcessorQuery());
		}
	}
	
	
	
   private String getCampaignNotificationForDealerQuery() {
		final String baseQuery = "from CampaignNotification campaignNotification "
				+ "where campaignNotification.dealership in (:dealer) "
				+ "and campaignNotification.claim is null "
				+ "and campaignNotification.campaign.d.active = 1 " 
				+ "and campaignNotification.campaign.fromDate <= :today "				
				+ "and campaignNotification.campaign.tillDate >= :today "
				+ "and campaignNotification.notificationStatus = 'PENDING' ";
		return baseQuery;
	}
	
	private String getPendingReviewCampaignsForProcessorQuery() {
		final String baseQuery = "from CampaignNotification campaignNotification "
				+ "where campaignNotification.claim is null "
				+ "and campaignNotification.campaign.d.active = 1 " 
				+ "and campaignNotification.campaign.fromDate <= :today "				
				+ "and campaignNotification.campaign.tillDate >= :today "
				+ "and campaignNotification.notificationStatus = 'PENDING' "
				+ "and campaignNotification.campaignStatus = :campaignNotificationStatus "
				+ "and campaignNotification.campaign.businessUnitInfo= :businessUnitName ";
		return baseQuery;
	}
	
	
	private String getCampaignNotificationForNationalAccountQuery() {
		final String baseQuery = "from CampaignNotification campaignNotification join campaignNotification.campaign campaign join campaign.applicableNationalAccounts applicableNationalAccounts "
				+ "where applicableNationalAccounts in (:dealer) "
				+ "and campaignNotification.claim is null "
				+ "and campaignNotification.campaign.d.active = 1 " 
				+ "and campaignNotification.campaign.fromDate <= :today "				
				+ "and campaignNotification.campaign.tillDate >= :today "
				+ "and campaignNotification.notificationStatus = 'PENDING' ";
		return baseQuery;
	}
	
	@SuppressWarnings("unchecked")
	public int findCampaignNotificationsCountForDealer(ServiceProvider dealer) {
		final String baseQuery = getBaseQueryForCampaign(dealer).toString()+ "and campaignNotification.status='Active' ";
		List<Organization> dealersWhoseFPICanBeViewed=new ArrayList<Organization>();
		dealersWhoseFPICanBeViewed.add(dealer);
        if(dealer !=null){
		    dealersWhoseFPICanBeViewed.addAll(getChildOrganizations(dealer.getId()));
        }
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealer", dealersWhoseFPICanBeViewed);
		params.put("today", Clock.today());
		List<CampaignNotification> records = findUsingQuery(baseQuery, params);
		return records == null ? 0 : records.size();
	}
	
	@SuppressWarnings("unchecked")
	public int findAllCampaignNotificationCountByStatus(ServiceProvider dealer, FieldModUpdateStatus campaignNotificationStatus,boolean isUserADealer,String businessUnitName) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Organization> dealersWhoseFPICanBeViewed=new ArrayList<Organization>();
		dealersWhoseFPICanBeViewed.add(dealer);
        if(dealer != null){
		    dealersWhoseFPICanBeViewed.addAll(getChildOrganizations(dealer.getId()));
        }
		final String baseQuery = getBaseQueryForCampaignNotifications(dealer,isUserADealer).toString();
		if(isUserADealer)
		{
		params.put("dealer", dealersWhoseFPICanBeViewed);
		params.put("today", Clock.today());
		params.put("campaignNotificationStatus", campaignNotificationStatus);
		}
		else
		{
		params.put("today", Clock.today());
		params.put("campaignNotificationStatus", campaignNotificationStatus);
		params.put("businessUnitName",businessUnitName);
		}
		List<CampaignNotification> records = findUsingQuery(baseQuery, params);
		return records == null ? 0 : records.size();
	}
	
    private String getSortCriteriaString(CampaignAssignmentCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append(columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public CampaignNotification findNotificationForClaim(Claim claim) {
		List<CampaignNotification> notifications = getHibernateTemplate().find(
				"from CampaignNotification campaignNotification "
						+ "where campaignNotification.claim = ?", claim);
		return notifications != null && notifications.size() > 0 ? notifications
				.get(0)
				: null;
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> findAllNotificationsForClaim(Claim claim) {
		List<CampaignNotification> notificationList = getHibernateTemplate().find(
				"from CampaignNotification campaignNotification "
						+ "where campaignNotification.claim = ?", claim);
		return notificationList != null && notificationList.size() > 0 ? notificationList
				: null;
	}

	@SuppressWarnings("unchecked")
	public CampaignNotification findNotificationForCampaignAndItem(
			Campaign campaign, InventoryItem item) {

		String query = "from CampaignNotification campaignNotification where "
				+ "campaignNotification.campaign = :campaign and "
				+ "campaignNotification.item = :item";

		List<CampaignNotification> notifications = getHibernateTemplate()
				.findByNamedParam(query, new String[] { "campaign", "item" },
						new Object[] { campaign, item });

		return notifications == null || notifications.size() == 0 ? null
				: (CampaignNotification) notifications.get(0);
	}

	@SuppressWarnings("unchecked")
	public PageResult<CampaignNotification> findAllItemsMatchingCriteria(
			final CampaignItemsSearchCriteria campaignItemsSearchCriteria) {

		return (PageResult<CampaignNotification>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Criteria campaignsCriteria = session
								.createCriteria(CampaignNotification.class);

						List addedAliases = buildSearchCriteria(
								campaignItemsSearchCriteria, campaignsCriteria);

						campaignsCriteria.setProjection(Projections.rowCount());
						long numResults = (Long) campaignsCriteria
								.uniqueResult();

						campaignsCriteria.setProjection(null);
						campaignsCriteria
								.setResultTransformer(Criteria.ROOT_ENTITY);

						PageSpecification pageSpecification = campaignItemsSearchCriteria
								.getPageSpecification();

						List<CampaignNotification> matchingItems = new ArrayList<CampaignNotification>();

						if (numResults > 0
								&& numResults > pageSpecification.offSet()) {
							addSortRestrictions(campaignsCriteria,
									campaignItemsSearchCriteria, addedAliases);
							addFilterRestrictions(campaignsCriteria,
									campaignItemsSearchCriteria, addedAliases);

							matchingItems = campaignsCriteria.setFirstResult(
									pageSpecification.offSet()).setMaxResults(
									pageSpecification.getPageSize()).list();
						}

						return new PageResult<CampaignNotification>(
								matchingItems, pageSpecification,
								pageSpecification
										.convertRowsToPages(numResults));

					}

					private void addSortRestrictions(
							Criteria campaignCriteria,
							CampaignItemsSearchCriteria campaignItemsSearchCriteria,
							List addedAliases) {

						Map<String, String> sortCriteria = campaignItemsSearchCriteria
								.getSortCriteria();

						for (String propertyName : sortCriteria.keySet()) {
							String processedProperty = criteriaHelper
									.processNestedAssociations(
											campaignCriteria, propertyName,
											addedAliases);
							String sortDirection = sortCriteria
									.get(propertyName);
							final Order sortOrder = getSortOrder(
									processedProperty, sortDirection);

							campaignCriteria.addOrder(sortOrder);
						}
					}

					private Order getSortOrder(String propertyName,
							String sortDirection) {
						if ("asc".equalsIgnoreCase(sortDirection)) {
							return Order.asc(propertyName);
						} else {
							return Order.desc(propertyName);
						}
					}

					private void addFilterRestrictions(
							Criteria campaignCriteria,
							CampaignItemsSearchCriteria searchCriteria,
							List addedAliases) {
						Map<String, String> filterCriteria = searchCriteria
								.getFilterCriteria();

						for (String propertyName : filterCriteria.keySet()) {

							String processedProperty = criteriaHelper
									.processNestedAssociations(
											campaignCriteria, propertyName,
											addedAliases);

							String filterData = filterCriteria
									.get(propertyName);

							if (StringUtils.hasText(filterData)) {
								// Filters are partial-search (LIKE) based.
								SimpleExpression filterExpression = Restrictions
										.like(processedProperty, filterData,
												MatchMode.START);
								// Filters are always AND'ed together.
								campaignCriteria.add(filterExpression);
							}
						}
					}

					private List buildSearchCriteria(
							final CampaignItemsSearchCriteria searchCriteria,
							Criteria campaignCriteria) {

						List addedAliases = new ArrayList<String>(5);

						if (StringUtils.hasText(searchCriteria
								.getSerialNumber())) {
							String property = criteriaHelper
									.processNestedAssociations(
											campaignCriteria,
											"item.serialNumber", addedAliases);
							campaignCriteria.add(Restrictions.like(property,
									searchCriteria.getSerialNumber(),
									MatchMode.ANYWHERE));
						}

						if (StringUtils.hasText(searchCriteria
								.getAssignedDealerId())) {
							String dealerId = searchCriteria
									.getAssignedDealerId();
							campaignCriteria
									.add(Restrictions.eq("dealership",
											dealershipRepository
													.findByDealerId(new Long(
															dealerId))));
						}

						if (StringUtils.hasText(searchCriteria.getStatus())) {
							campaignCriteria.add(Restrictions.eq(
									"notificationStatus", searchCriteria
											.getStatus()));
						}
						return addedAliases;
					}

				});

	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProvider> findAllAssignedDealersForCampaign(Campaign campaign) {
		List<ServiceProvider> dealers = getHibernateTemplate()
				.find(
						" select dealership from CampaignNotification c where c.campaign = ?",
						campaign);
		Set<ServiceProvider> dealersSet = new LinkedHashSet<ServiceProvider>();
		dealersSet.addAll(dealers);
		List<ServiceProvider> returnList = new ArrayList<ServiceProvider>(dealersSet);
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public CampaignNotification findCompletedNotificationOnInventory(
			final Campaign campaign, final InventoryItem item) {
		String query = " from CampaignNotification campaignNotification "
				+ " where campaignNotification.campaign =:campaign "
				+ " and campaignNotification.item = :item and upper(campaignNotification.notificationStatus) = 'COMPLETE'";
		List<CampaignNotification> campaignNotifications = getHibernateTemplate()
				.findByNamedParam(query, new String[] { "campaign", "item" },
						new Object[] { campaign, item });
		return campaignNotifications == null
				|| campaignNotifications.size() == 0 ? null
				: (CampaignNotification) campaignNotifications.get(0);
	}

	public PageResult<CampaignNotification> findCampaignsForPredefinedSearches(
			CampaignCriteria searchObj, ListCriteria listCriteria,
			ServiceProvider dealerShip) {
		StringBuilder queryBuilder = new StringBuilder();		
		String fromClause = " from CampaignNotification campaignNotification where notificationStatus is not null ";
		queryBuilder.append(fromClause);
		Map<String, Object> searchMap = new HashMap<String, Object>();
		populateSearchConditions(searchObj, queryBuilder, searchMap, dealerShip);

		String filter = listCriteria.getParamterizedFilterCriteria();
		if (!org.apache.commons.lang.StringUtils.isBlank(filter)) {
			queryBuilder.append(" and ").append(filter);
		}

		final String countQuery = " select count(*)" + queryBuilder.toString();
		final QueryParameters filterParams = new QueryParameters(listCriteria
				.getParameterMap());
		final QueryParameters searchParamsMap = new QueryParameters(searchMap);

		Long countOfRecords = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(countQuery);
						query.setProperties(filterParams);
						populateQueryParams(query, filterParams);
						populateQueryParams(query, searchParamsMap);
						return query.uniqueResult();
					}
				});

		List result = new ArrayList<RecoveryClaim>();

		final PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		final int offSet = pageSpecification.offSet().intValue();

		if (countOfRecords.longValue() > offSet) {
			String orderByClause = listCriteria.getSortCriteriaString();
			if (!isBlank(orderByClause))
				queryBuilder.append(" order by ").append(orderByClause);

			final String finalQuery = queryBuilder.toString();
			result = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Query query = session.createQuery(finalQuery);
							query.setProperties(filterParams);
							populateQueryParams(query, filterParams);
							populateQueryParams(query, searchParamsMap);
							return query.setFirstResult(offSet).setMaxResults(
									pageSpecification.getPageSize()).list();
						}
					});
		}

		int noOfPages = pageSpecification.convertRowsToPages(countOfRecords);
		return new PageResult<CampaignNotification>(result, pageSpecification,
				noOfPages);

	}


	private void populateSearchConditions(CampaignCriteria searchObj,
			StringBuilder queryBuilder,
			Map<String, Object> namedSearchParameters, ServiceProvider dealership) {
	
	
        if (searchObj != null) {
        	// Fix for NMHGSLMS-992
			if (searchObj.getSelectedBusinessUnits() != null
					&& !(searchObj.getSelectedBusinessUnits().length>0)) {
				queryBuilder
						.append("and campaign.businessUnitInfo in (")
						.append(
								searchObj
										.getSelectedBusinessUnitInfoDelimitedByComma())
						.append(")");
			}
			//ITS-648 added BU filter in else part
			else
			{
				String[] businessUnitInfo = new String[getSecurityHelper().getLoggedInUser().getBusinessUnits().size()+1];
				int index=0;
			    queryBuilder
				.append("and campaign.businessUnitInfo in (");
                
		        for(BusinessUnit bu:getSecurityHelper().getLoggedInUser().getBusinessUnits())
		        {
			    businessUnitInfo[index] = bu.getName();	
			    index++;
		        }
			    searchObj.setSelectedBusinessUnits(businessUnitInfo);
	            queryBuilder
	            .append(searchObj
						.getSelectedBusinessUnitInfoDelimitedByComma())
				.append(")");
	            searchObj.setSelectedBusinessUnits(null);
		    }
			
			if (!isBlank(searchObj.getCampaignClass())) {
				queryBuilder.append(" and campaign.campaignClass.code like '")
						.append(searchObj.getCampaignClass()).append("%'");
			}

			if (!isBlank(searchObj.getDealerNumber())) {
				queryBuilder.append(" and dealership.serviceProviderNumber like '")
						.append(searchObj.getDealerNumber()).append("%'");
				
			}

			if (!isBlank(searchObj.getDealerName())) {
				queryBuilder.append(" and upper(dealership.name) like '")
						.append(searchObj.getDealerName().toUpperCase()).append("%'");
			}
			if (!isBlank(searchObj.getDealerGroup())) {
				List<ServiceProvider> providersInGroup =
                    dealerGroupRepository.findProvidersAtAllLevelForGroup(
                    		searchObj.getDealerGroup());
                 
                    queryBuilder.append(" and  dealership in (:providerIds)");
    				namedSearchParameters.put("providerIds",providersInGroup);
                    
                } 
               
			if (!isBlank(searchObj.getSerialNumber())) {
				queryBuilder.append(" and item.serialNumber like '").append(
						searchObj.getSerialNumber()).append("%'");
			}

			if (!isBlank(searchObj.getCampaignCode())) {
				queryBuilder.append(" and campaign.code like '").append(
						searchObj.getCampaignCode()).append("%'");
			
			}
			if (!isBlank(searchObj.getCampaignStatus())) {
				queryBuilder.append(" and notificationStatus like '").append(
						searchObj.getCampaignStatus()).append("%'");
				if(searchObj.getCampaignStatus().equalsIgnoreCase("PENDING"))
				{
				queryBuilder.append(" and campaign.fromDate <= :today ");
				queryBuilder.append(" and campaign.tillDate >= :today ");
				namedSearchParameters.put("today", Clock.today());
				}
			}
			
			if (!isBlank(searchObj.getCampaignReason())) {
				queryBuilder.append(" and fieldModInvStatus.code like '")
						.append(searchObj.getCampaignReason()).append("%'");
			}

			if (dealership != null) {
				queryBuilder.append(" and  dealership in (:dealers)");
				List<Organization> dealersWhoseFPICanBeViewed=new ArrayList<Organization>();
				dealersWhoseFPICanBeViewed.add(dealership);
				dealersWhoseFPICanBeViewed.addAll(getChildOrganizations(dealership.getId()));
				namedSearchParameters.put("dealers", dealersWhoseFPICanBeViewed);
			}

			if ((searchObj.getMaxRangeCampaignAge() != null)
					&& (searchObj.getMinRangeCampaignAge() != null)) {

				Calendar cal = new GregorianCalendar();

				cal.setTime(new java.util.Date());
				cal.add(Calendar.MONTH, -searchObj.getMaxRangeCampaignAge()
						.intValue());
				namedSearchParameters.put("startOfRange", CalendarDate.from(
						TimePoint.from(cal), TimeZone.getDefault()));

				cal.setTime(new java.util.Date());
				cal.add(Calendar.MONTH, -searchObj.getMinRangeCampaignAge()
						.intValue());
				namedSearchParameters.put("endOfRange", CalendarDate.from(
						TimePoint.from(cal), TimeZone.getDefault()));

				queryBuilder.append(" and  campaign.fromDate >=  ").append(
						":startOfRange ")
						.append(" and campaign.fromDate  <=  ").append(
								":endOfRange");
			}
		}
	}



	@SuppressWarnings("unchecked")
	public List<String> findAllStatusForCampaign() {
		String queryString = "select distinct c.notificationStatus from CampaignNotification c ";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findInventoriesForCampaignWithClaim(final Campaign campaign, 
			final List<String> listOfSerialNumbers) {
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			public List<String> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria campaignNotificationCriteria = session.createCriteria(CampaignNotification.class);
				campaignNotificationCriteria.add(Restrictions.eq("campaign", campaign));
				List<String> addedAliases = new ArrayList<String>(5);
				
				int i = 1;
                List<String> listOfSNs =
                        new ArrayList<String>(listOfSerialNumbers.size());
                for(String provider : listOfSerialNumbers) {
                    listOfSNs.add(provider);

                    // Oracle supports only 1000 elements inside an "IN"
                    // query. Hence we use multiple IN queries joined
                    // by an OR clause.
                    if(i++ == 1000) {
                        
                        campaignNotificationCriteria.add(Restrictions.in(criteriaHelper
        						.processNestedAssociations(
        								campaignNotificationCriteria, "item.serialNumber",
        								addedAliases), listOfSNs));
                        
                        listOfSNs.clear();
                        i = 1;
                    }
                }

                if(i > 1) {
                	 campaignNotificationCriteria.add(Restrictions.in(criteriaHelper
     						.processNestedAssociations(
     								campaignNotificationCriteria, "item.serialNumber",
     								addedAliases), listOfSNs));
                }
				campaignNotificationCriteria.add(Restrictions.isNotNull("claim"));
				List<CampaignNotification> notifications = campaignNotificationCriteria.list();
				
				if (CollectionUtils.isNotEmpty(notifications))
				{
					List<String> claimExistForInventoryItems = new ArrayList<String>(5);
					for (Iterator<CampaignNotification> iterator = notifications.iterator(); iterator.hasNext();) {
						CampaignNotification notification = (CampaignNotification) iterator.next();
						claimExistForInventoryItems.add(notification.getItem().getSerialNumber());
					}
					return claimExistForInventoryItems;
				}
					
				return null;
			}});
	}

	public PageResult<CampaignNotification> findAllCampaignNotificationsForCampaign(final Long campaignId, PageSpecification pageSpecification) {
		String baseQuery = " from CampaignNotification cn where cn.campaign.id = :campaignId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("campaignId", campaignId);
		return findPageUsingQuery(baseQuery.toString(), "id", pageSpecification, params);

	}

	public List<Claim> findAllClaimsForCampaignNotifications(
			final CampaignNotification campaignNotification) {
		return (List<Claim>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select campaignNotification.claim  from  CampaignNotification campaignNotification where campaignNotification.item= :inventoryItem and campaignNotification.id=:campNotificationId")
								.setParameter("inventoryItem", campaignNotification.getItem())
								.setParameter("campNotificationId", campaignNotification.getId())
								.list();
					}
				});
	}

}

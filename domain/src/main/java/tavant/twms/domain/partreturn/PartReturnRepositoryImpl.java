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

package tavant.twms.domain.partreturn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.CustomPropertyAliasProjection;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.TypedQueryParameter;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class PartReturnRepositoryImpl extends
		GenericRepositoryImpl<PartReturn, Long> implements PartReturnRepository {
	// todo-new GenericRepository supports returning PageResult for any
	// object.Migrate to that method.
	private CriteriaHelper criteriaHelper;

	private ConfigParamService configParamService;

	public CriteriaHelper getCriteriaHelper() {
		return criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@SuppressWarnings("unchecked")
	public PageResult<PartReturnClaimSummary> findPartReturnsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final List<TypedQueryParameter> parameterMap,
			final Map<String, Object> paramsMap) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
		countQuery.append(queryWithoutSelect);

		if (logger.isDebugEnabled()) {
			logger.debug("findPageUsingQuery(" + queryWithoutSelect + ","
					+ orderByClause + ") count query is [" + countQuery + "]");
			logger.debug("findPageUsingQuery(" + queryWithoutSelect + ","
					+ orderByClause + ") count query is [" + countQuery + "]");
		}
		Long numberOfRows = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery(countQuery.toString());
						// query.setProperties(parameterMap);
						populateQueryParams(query, parameterMap);
						if ((paramsMap != null) && !paramsMap.isEmpty()) {
							query.setProperties(paramsMap);
							if ((paramsMap != null) && !paramsMap.isEmpty()) {
								populateQueryProperties(query, paramsMap);
							}
						}
						return query.uniqueResult();
					}
				});
		Integer numberOfPages = pageSpecification
				.convertRowsToPages(numberOfRows);

		List<PartReturnClaimSummary> rowsInPage = new ArrayList<PartReturnClaimSummary>();
		PageResult<PartReturnClaimSummary> page = new PageResult<PartReturnClaimSummary>(
				rowsInPage, pageSpecification, numberOfPages);

		StringBuffer filterAndSort = new StringBuffer();
		if ((selectClause != null) && !("".equals(selectClause.trim()))) {
			filterAndSort.append(selectClause);
		}
		filterAndSort.append(queryWithoutSelect);
		if ((orderByClause != null) && (orderByClause.trim().length() > 0)) {
			filterAndSort.append(" order by ");
			filterAndSort.append(orderByClause);
		}
		final String finalQuery = filterAndSort.toString();

		final Integer pageOffset = pageSpecification.offSet();
		if ((numberOfRows > 0) && (numberOfRows > pageOffset)) {

			final Integer pageSize = pageSpecification.getPageSize();

			rowsInPage = (List<PartReturnClaimSummary>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							if (logger.isDebugEnabled()) {
								logger.debug(" Unpaginated query for findPage("
										+ queryWithoutSelect
										+ ",listCriteria) [ " + finalQuery
										+ queryWithoutSelect
										+ ",listCriteria) [ " + finalQuery
										+ " ]");
							}

							Query query = session.createQuery(finalQuery);
							populateQueryParams(query, parameterMap);
							if ((paramsMap != null) && !paramsMap.isEmpty()) {
								query.setProperties(paramsMap);
							}
							if ((paramsMap != null) && !paramsMap.isEmpty()) {
								populateQueryProperties(query, paramsMap);
							}
							// query.setProperties(parameterMap);
							return query.setFirstResult(pageOffset)
									.setMaxResults(pageSize).list();
						}
					});
			page = new PageResult<PartReturnClaimSummary>(rowsInPage,
					pageSpecification, numberOfPages);
		}
		return page;

	}

	private void populateQueryParams(Query query, Object params) {
		if (params instanceof List) {
			List<TypedQueryParameter> paramsList = (List<TypedQueryParameter>) params;
			int i = 0;
			for (TypedQueryParameter param : paramsList) {
				query.setParameter(i++, param.getValue(), param.getType());
			}

		} else {
			// todo
			throw new RuntimeException("....");
		}
	}

	private static Map<String, String> inboxFieldProjectionMap = new HashMap<String, String>();
	static {
		inboxFieldProjectionMap.put("claimId","claim.id");
		inboxFieldProjectionMap.put("partReturnId","partReturns.id");
		inboxFieldProjectionMap.put("partNumber","brandItems.itemNumber");
		inboxFieldProjectionMap.put("locationCode","returnLocation.code");
		inboxFieldProjectionMap.put("claimNumber","claimNumber");
		inboxFieldProjectionMap.put("status","partReturns.status");
		inboxFieldProjectionMap.put("shipmentNumber","shipment.transientId");
        inboxFieldProjectionMap.put("wpraNumber","wpra.wpraNumber");
        inboxFieldProjectionMap.put("dealerName","forDealer.name");
        inboxFieldProjectionMap.put("dueDate","partReturns.dueDate");
        inboxFieldProjectionMap.put("claimAudit","claim.activeClaimAudit");
	}  
	
	@SuppressWarnings("unchecked")
	public PageResult<PartReturnClaimSummary> findAllClaimsMatchingCriteria(
			final PartReturnSearchCriteria partReturnSearchCriteria) {

		return (PageResult<PartReturnClaimSummary>) getHibernateTemplate()
				.execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) {

				Criteria partReturnCriteria = session.createCriteria(
						Claim.class, "claim");
				
				List<String> addedAliases = buildSearchCriteria(partReturnSearchCriteria,
						partReturnCriteria);				
				addFilterRestrictions(partReturnCriteria,
						partReturnSearchCriteria, addedAliases, true);

				// Get the count of results, first.
				partReturnCriteria.setProjection(Projections.rowCount());

				long numResults = (Long) partReturnCriteria
						.uniqueResult();
							
				partReturnCriteria.setProjection(null);
				
				ProjectionList projectionList = Projections.projectionList();
				for(String key : inboxFieldProjectionMap.keySet()){
					//added if check to fix NMHGSLMS-888
					if(!key.equals("wpraNumber")){						
							projectionList.add(new CustomPropertyAliasProjection(inboxFieldProjectionMap.get(key),key));					
					}else{
						if(!AdminConstants.NMHGAMERICA.equals(SelectedBusinessUnitsHolder.getSelectedBusinessUnit())){
							projectionList.add(new CustomPropertyAliasProjection(inboxFieldProjectionMap.get(key),key));
						}							
					}
				}
					
				partReturnCriteria.setProjection(projectionList);
				
				PageSpecification pageSpecification = partReturnSearchCriteria
						.getPageSpecification();

				List<PartReturnClaimSummary> matchingPRs = new ArrayList<PartReturnClaimSummary>();
				if (numResults > 0
						&& numResults > pageSpecification.offSet()) {
					
					addSortRestrictions(partReturnCriteria,
							partReturnSearchCriteria, addedAliases);
									
					matchingPRs = partReturnCriteria
							.setFirstResult(pageSpecification.offSet())
							.setMaxResults(pageSpecification.getPageSize())
							.setResultTransformer(Transformers.aliasToBean(PartReturnClaimSummary.class))
							.list();
				}

				return new PageResult<PartReturnClaimSummary>(matchingPRs,
						pageSpecification, pageSpecification
								.convertRowsToPages(numResults));
			}

		});
	}
	
	private void addSortRestrictions(Criteria partReturnCriteria,
			PartReturnSearchCriteria partReturnSearchCriteria, List<String> addedAliases) {
		// Sorting should use LEFT JOIN.
		int joinType = CriteriaSpecification.LEFT_JOIN;
		Map<String, String> sortCriteria = partReturnSearchCriteria
				.getSortCriteria();
		for (String propertyName : sortCriteria.keySet()) {
			String processedProperty = PartReturnRepositoryImpl.this.criteriaHelper
					.processNestedAssociations(partReturnCriteria, propertyName,
							joinType, addedAliases);
			String sortDirection = sortCriteria.get(propertyName);
			final Order sortOrder = getSortOrder(processedProperty,
					sortDirection);
			partReturnCriteria.addOrder(sortOrder);
		}
	}

	private Order getSortOrder(String propertyName, String sortDirection) {
		if ("asc".equalsIgnoreCase(sortDirection)) {
			return Order.asc(propertyName);
		} else {
			return Order.desc(propertyName);
		}
	}

	private void addFilterRestrictions(Criteria partReturnCriteria,
			PartReturnSearchCriteria partReturnSearchCriteria, 
			List<String> addedAliases,boolean countQuery) {
		Map<String, String> filterCriteria = partReturnSearchCriteria
				.getFilterCriteria();
		for (String propertyName : filterCriteria.keySet()) {
			String processedProperty = PartReturnRepositoryImpl.this.criteriaHelper
					.processNestedAssociations(partReturnCriteria, 
							(countQuery ? inboxFieldProjectionMap.get(propertyName): propertyName),
							addedAliases);
			String filterData = filterCriteria.get(propertyName);
			if (StringUtils.hasText(filterData)) {
				if (processedProperty.equalsIgnoreCase("claimNumber")) {
					partReturnCriteria.add(Restrictions.ilike(
							"claim.claimNumber", filterData, MatchMode.START));
				} else {
					partReturnCriteria.add(Restrictions.ilike(
							processedProperty, filterData, MatchMode.START));
				}
			}
		}
	}

	private List<String> buildSearchCriteria(
			final PartReturnSearchCriteria searchCriteria,
			Criteria partReturnCriteria) {
		List<String> addedAliases = new ArrayList<String>(10);
		
		String partReplacedtableToBeUsed = "oemPartsReplaced";
		boolean partsReplacedInstalledSectionEnabled = configParamService
		.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
				.getName());
		if(partsReplacedInstalledSectionEnabled) {
			partReplacedtableToBeUsed =  "hussmanPartsReplacedInstalled.replacedParts" ;
		}
		
		criteriaHelper
				.processNestedAssociations(
						partReturnCriteria,
						"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.actionTaken",
						addedAliases);
		
		criteriaHelper
		.processNestedAssociations(
				partReturnCriteria,
				"activeClaimAudit.serviceInformation.causalBrandPart.brand",
				addedAliases);
				
		criteriaHelper
				.processNestedAssociations(
						partReturnCriteria,
						"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".itemReference.unserializedItem.brandItems.itemNumber",
						addedAliases);
        partReturnCriteria.add(Restrictions.eqProperty("brandItems.brand", "causalBrandPart.brand"));  //using causal part brand instead of claim brand since it is same as that of replaced part brand as per NMHGSLMS-434

		partReturnCriteria
				.add(Restrictions
						.eq(
								criteriaHelper
										.processNestedAssociations(
												partReturnCriteria,
												"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partToBeReturned",
												addedAliases), true));

        if(StringUtils.hasText(searchCriteria.getPartNumber())) {
                String processedProperty = criteriaHelper
                        .processNestedAssociations(partReturnCriteria,
                                "activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.partScrapped", addedAliases);
                partReturnCriteria.add(Restrictions.eq(processedProperty,
                        searchCriteria.isScrapped()));

                String processedPropertyForNumber = criteriaHelper
                        .processNestedAssociations(partReturnCriteria,
                                "activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.itemReference.referredItem.brandItems.itemNumber", addedAliases);
                partReturnCriteria.add(Restrictions.ilike(processedPropertyForNumber,
                        searchCriteria.getPartNumber().concat("%")));
                partReturnCriteria.add(Restrictions.eqProperty("brandItems.brand", "causalBrandPart.brand")); //using causal part brand instead of claim brand since it is same as that of replaced part brand as per NMHGSLMS-434

        }
        //modified to fix NMHGSLMS-888
        if(StringUtils.hasText(searchCriteria.getWpraNumber())) {
	        String processedPropertyForWpra = criteriaHelper
	                .processNestedAssociations(partReturnCriteria,
	                        "activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.wpra.wpraNumber", addedAliases);
	        partReturnCriteria.add(Restrictions.ilike(processedPropertyForWpra,
	                "%".concat(searchCriteria.getWpraNumber().concat("%"))));
        }else{
        	criteriaHelper
			.processNestedAssociations(
					partReturnCriteria,
					"activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.wpra.wpraNumber",
					CriteriaSpecification.LEFT_JOIN,
					addedAliases);
        }
        if (searchCriteria.getSelectedBusinessUnits() != null
				&& (!(searchCriteria.getSelectedBusinessUnits().isEmpty()))) {
			partReturnCriteria.add(Restrictions.in("businessUnitInfo",
					searchCriteria.getSelectedBusinessUnits()));
		}
		if (StringUtils.hasText(searchCriteria.getDealerNumber())) {
			String processedProperty = getCriteriaHelper()
			.processNestedAssociations(
					partReturnCriteria,
					"forDealer.serviceProviderNumber",
					addedAliases);
			
			partReturnCriteria.add(Restrictions.sqlRestriction(
					" ( service_provider_number like ?  or " +
					" ( for_dealer in (select id from third_party) and "+
					" filed_by in (select org_users.org_user from Service_Provider org,org_user_belongs_to_orgs org_users " +
							"	where org.id=org_users.belongs_to_organizations and org.service_provider_number = ?)) ) ",
					new String[]{searchCriteria.getDealerNumber(),searchCriteria.getDealerNumber()},
					new org.hibernate.type.Type[]{Hibernate.STRING,Hibernate.STRING}
					));
			/*String processedProperty = criteriaHelper
					.processNestedAssociations(partReturnCriteria,
							"forDealer.dealerNumber", addedAliases);
			partReturnCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getDealerNumber()));*/
		}
		
		if (StringUtils.hasText(searchCriteria.getSerialNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"claimedItems.itemReference.referredInventoryItem.serialNumber",
							addedAliases);
			partReturnCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getSerialNumber().concat("%")));

		}
		if (StringUtils.hasText(searchCriteria.getTrackingNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.shipment.trackingId",
							addedAliases);
			partReturnCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getTrackingNumber().concat("%")));

		} else {
			criteriaHelper
			.processNestedAssociations(
					partReturnCriteria,
					"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.shipment.trackingId",
					CriteriaSpecification.LEFT_JOIN,
					addedAliases);
		}
		if (StringUtils.hasText(searchCriteria.getStatus())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.status",
							addedAliases);
			partReturnCriteria.add(Restrictions.eq(processedProperty,
					PartReturnStatus.getPartReturnStatus(searchCriteria.getStatus())));

		}
		
		if (searchCriteria.getClaimStatus()!=null) {
			List<ClaimState> claimStatus=new ArrayList<ClaimState>();
			for(String status:searchCriteria.getClaimStatus())
			{
				claimStatus.add(getClaimStatus(status));
			}
			
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"activeClaimAudit.state",
							addedAliases);
			partReturnCriteria.add(Restrictions.in(processedProperty,claimStatus));

		}
		
		if (searchCriteria.getFromDate()!= null
				&& searchCriteria.getToDate() != null) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.dueDate",
							addedAliases);
			getCriteriaHelper().dateRangeIfNotNull(partReturnCriteria, processedProperty,
					searchCriteria.getFromDate(), searchCriteria.getToDate());
		}
		
		
		if (StringUtils.hasText(searchCriteria.getDealerName())) {
			String processedPropertyForDealer = criteriaHelper
					.processNestedAssociations(partReturnCriteria,
							"forDealer.name", addedAliases);
			partReturnCriteria.add(Restrictions.ilike(processedPropertyForDealer,
					searchCriteria.getDealerName().concat("%")));
		}else {
			criteriaHelper
			.processNestedAssociations(
					partReturnCriteria,
					"forDealer.name",
					CriteriaSpecification.LEFT_JOIN,
					addedAliases);
		}
		
		if (StringUtils.hasText(searchCriteria.getClaimNumber())) {			
			criteriaHelper.likeIfNotNull(partReturnCriteria, "claim.claimNumber",
					searchCriteria.getClaimNumber().toUpperCase().concat("%"));
		}
		
		if (StringUtils.hasText(searchCriteria.getReturnToLocation())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partReturnCriteria,
							"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.returnLocation.code",
							addedAliases);
			partReturnCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getReturnToLocation().concat("%")));
		} else {
			criteriaHelper
			.processNestedAssociations(
					partReturnCriteria,
					"activeClaimAudit.serviceInformation.serviceDetail." + partReplacedtableToBeUsed + ".partReturns.returnLocation.code",
					CriteriaSpecification.LEFT_JOIN,
					addedAliases);
		}
		return addedAliases;
	}
	
	//TODO
	  public  ClaimState getClaimStatus(String status)
	    {
	        if(!StringUtils.hasText(status)){
	            return null;
	        }    
	        else if (ClaimState.SUBMITTED.getState().equalsIgnoreCase(status))
	        	return ClaimState.SUBMITTED;
	        
	        else if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equalsIgnoreCase(status))
	        	return ClaimState.SERVICE_MANAGER_REVIEW;
	        else if (ClaimState.SERVICE_MANAGER_RESPONSE.getState().equalsIgnoreCase(status))
	        	return ClaimState.SERVICE_MANAGER_RESPONSE;
	        else if (ClaimState.SERVICE_MANAGER_RESPONSE.getState().equalsIgnoreCase(status))
	        	return ClaimState.SERVICE_MANAGER_RESPONSE;
	        else if (ClaimState.MANUAL_REVIEW.getState().equalsIgnoreCase(status))
	        	return ClaimState.MANUAL_REVIEW;
	        else if (ClaimState.ON_HOLD.getState().equalsIgnoreCase(status))
	        	return ClaimState.ON_HOLD;
	        else if (ClaimState.ON_HOLD_FOR_PART_RETURN.getState().equalsIgnoreCase(status))
	        	return ClaimState.ON_HOLD_FOR_PART_RETURN;
	        else if (ClaimState.FORWARDED.getState().equalsIgnoreCase(status))
	        	return ClaimState.FORWARDED;
	        else if (ClaimState.TRANSFERRED.getState().equalsIgnoreCase(status))
	        	return ClaimState.TRANSFERRED;
	        else if (ClaimState.ADVICE_REQUEST.getState().equalsIgnoreCase(status))
	        	return ClaimState.ADVICE_REQUEST;
	        else if (ClaimState.REPLIES.getState().equalsIgnoreCase(status))
	        	return ClaimState.REPLIES;
	        else if (ClaimState.EXTERNAL_REPLIES.getState().equalsIgnoreCase(status))
	        	return ClaimState.EXTERNAL_REPLIES;
	        else if (ClaimState.PROCESSOR_REVIEW.getState().equalsIgnoreCase(status))
	        	return ClaimState.PROCESSOR_REVIEW;
	        else if (ClaimState.APPROVED.getState().equalsIgnoreCase(status))
	        	return ClaimState.APPROVED;
	        else if (ClaimState.REJECTED.getState().equalsIgnoreCase(status))
	        	return ClaimState.REJECTED;
	        else if (ClaimState.ACCEPTED.getState().equalsIgnoreCase(status))
	        	return ClaimState.ACCEPTED;
	        else if (ClaimState.WAITING_FOR_PART_RETURNS.getState().equalsIgnoreCase(status))
	        	return ClaimState.WAITING_FOR_PART_RETURNS;
	        else if (ClaimState.REJECTED_PART_RETURN.getState().equalsIgnoreCase(status))
	        	return ClaimState.REJECTED_PART_RETURN;
	        else if (ClaimState.REACCEPTED.getState().equalsIgnoreCase(status))
	        	return ClaimState.REACCEPTED;
	        else if (ClaimState.PENDING_PAYMENT_SUBMISSION.getState().equalsIgnoreCase(status))
	        	return ClaimState.PENDING_PAYMENT_SUBMISSION;
	        else if (ClaimState.PENDING_PAYMENT_RESPONSE.getState().equalsIgnoreCase(status))
	        	return ClaimState.PENDING_PAYMENT_RESPONSE;
	        else if (ClaimState.DELETED.getState().equalsIgnoreCase(status))
	        	return ClaimState.DELETED;
	        else if (ClaimState.CLOSED.getState().equalsIgnoreCase(status))
	        	return ClaimState.CLOSED;
	        else if (ClaimState.ACCEPTED_AND_CLOSED.getState().equalsIgnoreCase(status))
	        	return ClaimState.ACCEPTED_AND_CLOSED;
	        else if (ClaimState.DENIED.getState().equalsIgnoreCase(status))
	        	return ClaimState.DENIED;
	        else if (ClaimState.DENIED_AND_CLOSED.getState().equalsIgnoreCase(status))
	        	return ClaimState.DENIED_AND_CLOSED;
	        else if (ClaimState.APPEALED.getState().equalsIgnoreCase(status))
	        	return ClaimState.APPEALED;
	        else if (ClaimState.REOPENED.getState().equalsIgnoreCase(status))
	        	return ClaimState.REOPENED;
	        else if (ClaimState.CP_REVIEW.getState().equalsIgnoreCase(status))
	        	return ClaimState.CP_REVIEW;
	        else if (ClaimState.CP_TRANSFER.getState().equalsIgnoreCase(status))
	        	return ClaimState.CP_TRANSFER;
	        else if (ClaimState.IN_PROGRESS.getState().equalsIgnoreCase(status))
	        	return ClaimState.IN_PROGRESS;
	        else if (ClaimState.DEACTIVATED.getState().equalsIgnoreCase(status))
	        	return ClaimState.DEACTIVATED;
	        else if (ClaimState.INVALID.getState().equalsIgnoreCase(status))
	        	return ClaimState.INVALID;
	        else
	        	 throw new IllegalArgumentException("Cannot understand the claim Status");     
	    }

	@SuppressWarnings("unchecked")
	public List<Claim> getAllDraftClaims() {
		String queryString = " SELECT claim FROM TaskInstance taskInstance, Claim claim"
			+ " where taskInstance.isOpen = true"
			+ " and taskInstance.claimId = claim.id"
			+ " and taskInstance.name = 'Draft Claim' ";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}

	public List<Claim> forwardedClaimCrossedOverDueDays(int forwardedOverdueDate) {
		String queryString = "SELECT distinct claim FROM TaskInstance taskInstance, Claim claim"
			+ " where taskInstance.isOpen = true"
			+ " and taskInstance.claimId = claim.id "
			+ " and taskInstance.name in ('Forwarded')"
			+ " and claim.activeClaimAudit.state in ('FORWARDED') "
			+ " and taskInstance.create + :forwardedOverdueDate<= sysdate ";
		Query query = getSession().createQuery(queryString);
		query.setInteger("forwardedOverdueDate", forwardedOverdueDate);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findAllStatusForPartReturn() {
		String queryString = "select distinct p.actionTaken from PartReturn p "
				+ " where p.actionTaken is not null ";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findAllLocationsForPartReturn() {
		String queryString = "select l.code from Warehouse w join w.location l";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}

	private void populateQueryProperties(Query query,
			Map<String, Object> paramsMap) {
		Set<Map.Entry<String, Object>> entrySet = paramsMap.entrySet();

		for (Map.Entry<String, Object> entry : entrySet) {
			if (entry.getValue() instanceof TypedQueryParameter) {
				TypedQueryParameter qp = (TypedQueryParameter) entry.getValue();

				// work around to handle issue with data being stored in DB as
				// "PART_RECIEVED" etc. and being displayed on GUI as
				// "PART RECIEVED" etc.
				// we need a better way to handle this.
				if (entry.getKey().equalsIgnoreCase("partReturnstatusstatus")) {
					if (qp.getValue().toString().contains(" ")) {
						String value = qp.getValue().toString().replace(" ",
								"_");
						qp.setValue(value);
					}
				}

				query.setParameter(entry.getKey(), qp.getValue(), qp.getType());
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Claim> fetchShipmentGeneratedClaimsCrossedWindowPeriodDays(
			int windowPeriodDays) {
		String queryString = "SELECT distinct claim FROM TaskInstance taskInstance, Claim claim"
				+ " where taskInstance.isOpen = true"
				+ " and taskInstance.claimId = claim.id "
				+ " and taskInstance.name ='Shipment Generated' "
				+ " and claim.activeClaimAudit.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED') "
				+ " and taskInstance.create + :windowPeriodDays <= sysdate ";
		Query query = getSession().createQuery(queryString);
		query.setInteger("windowPeriodDays", windowPeriodDays);
		return query.list();
	}

	public void updatePartReturnConfiguration(
			PartReturnConfiguration partReturnConfiguration) {
		getHibernateTemplate().saveOrUpdate(partReturnConfiguration);
	}

	
	public ConfigParamService getConfigParamService() {
		return this.configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

}

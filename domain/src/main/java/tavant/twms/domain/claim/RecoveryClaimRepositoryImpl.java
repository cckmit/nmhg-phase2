/*
 *   Copyright (c) 2007 Tavant Technologies
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

import com.domainlanguage.time.CalendarDate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.SupplierPartReturnClaimSummary;
import tavant.twms.domain.supplier.RecoveryClaimCriteria;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.CustomPropertyAliasProjection;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.infra.TypedQueryParameter;

/**
 * @author pradipta.a
 */
public class RecoveryClaimRepositoryImpl extends
		GenericRepositoryImpl<RecoveryClaim, Long> implements
		RecoveryClaimRepository {

	private String spaceDelimiter = "  ";
	private CriteriaHelper criteriaHelper;

	public CriteriaHelper getCriteriaHelper() {
		return criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	public List<RecoveryClaim> findClaimInState(RecoveryClaimState state) {
		return getHibernateTemplate().find(
				"from RecoveryClaim a where a.recoveryClaimState=?", state);
	}

	public RecoveryClaim find(Long id) {
		return (RecoveryClaim) getHibernateTemplate().get(RecoveryClaim.class,
				id);
	}
	
	
	public RecoveryClaim find(String recoveryClaimNumber) {
		List<RecoveryClaim> list = getHibernateTemplate().find(
				"from RecoveryClaim a where a.recoveryClaimNumber=?", recoveryClaimNumber);
		return list.get(0);
	}
	
	public RecoveryClaim findByRecoveryClaimNumber(final String recoveryClaimNumber) {
        return (RecoveryClaim) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	return session.createQuery("from RecoveryClaim recClaim where recClaim.recoveryClaimNumber =:recoveryClaimNumber").setString(
            	        "recoveryClaimNumber", recoveryClaimNumber).uniqueResult();
            }
        });
}

	@SuppressWarnings("unchecked")
	public List<RecoveryClaim> findAllRecClaimsForMultiMaintainance(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, final QueryParameters params) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
		countQuery.append(queryWithoutSelect);
		StringBuffer filterAndSort = new StringBuffer();
		if (selectClause != null && !("".equals(selectClause.trim()))) {
			filterAndSort.append(selectClause);
		}
		filterAndSort.append(queryWithoutSelect);
		// filterAndSort.append("and all elements
		// (claim.recoveryClaims.recoveryClaimState.state) like '%CLOSED%'");
		if (orderByClause != null && orderByClause.trim().length() > 0) {
			filterAndSort.append(" order by ");
			filterAndSort.append(orderByClause);
		}
		final String finalQuery = filterAndSort.toString();

		List<RecoveryClaim> recClaims = (List<RecoveryClaim>) getHibernateTemplate()
				.execute(new HibernateCallback() {
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
		return recClaims;
	}

	public PageResult<RecoveryClaim> findRecoveryClaimsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final QueryParameters paramsMap) {
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");
		countQuery.append(queryWithoutSelect);

		if (logger.isDebugEnabled()) {
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
						populateQueryParams(query, paramsMap);
						return query.uniqueResult();
					}
				});
		Integer numberOfPages = pageSpecification
				.convertRowsToPages(numberOfRows);

		List<RecoveryClaim> rowsInPage = new ArrayList<RecoveryClaim>();
		PageResult<RecoveryClaim> page = new PageResult<RecoveryClaim>(
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
			rowsInPage = (List<RecoveryClaim>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							if (logger.isDebugEnabled()) {
								logger.debug(" Unpaginated query for findPage("
										+ queryWithoutSelect
										+ ",listCriteria) [ " + finalQuery
										+ " ]");
							}

							Query query = session.createQuery(finalQuery);
							populateQueryParams(query, paramsMap);
							// query.setProperties(parameterMap);
							return query.setFirstResult(pageOffset)
									.setMaxResults(pageSize).list();
						}
					});
			page = new PageResult<RecoveryClaim>(rowsInPage, pageSpecification,
					numberOfPages);
		}
		return page;

	}

	public List<RecoveryClaim> findRecClaimForClaim(Long id) {
		return getHibernateTemplate().find(
				"from RecoveryClaim a where a.claim.id=?", id);
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

	public RecoveryClaim findActiveRecoveryClaimForClaim(
			String warrantyClaimNumber) {
		List<RecoveryClaim> recoveryClaims = getHibernateTemplate()
				.find(
						"from RecoveryClaim a where a.claim.claimNumber=? and a.recoveryClaimState in ('WAIT_FOR_DEBIT','WAIT_FOR_NO_RESPONSE_AUTO_DEBIT','WAIT_FOR_DISPUTED_AUTO_DEBIT')",
						warrantyClaimNumber);
		return recoveryClaims.get(0);
	}

	public PageResult<RecoveryClaim> findRecClaimsForPredefinedSearch(
			RecoveryClaimCriteria searchObj, ListCriteria listCriteria) {
		StringBuilder queryBuilder = new StringBuilder();
		String fromClause = " from RecoveryClaim recClaim where 1=1  ";
		queryBuilder.append(fromClause);
		Map<String, Object> searchParameters = new HashMap<String, Object>();
		populateSearchConditions(searchObj, queryBuilder, searchParameters);

		String filter = listCriteria.getParamterizedFilterCriteria();
		if (!StringUtils.isBlank(filter)) {
			queryBuilder.append(" and ").append(filter);
		}

		final String countQuery = " select count(*)" + queryBuilder.toString();
		final QueryParameters filterParams = new QueryParameters(listCriteria
				.getParameterMap());
		final QueryParameters searchParams = new QueryParameters(
				searchParameters);
		Long countOfRecords = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(countQuery);
						query.setProperties(filterParams);
						populateQueryParams(query, filterParams);
						populateQueryParams(query, searchParams);
						return query.uniqueResult();
					}
				});

		List result = new ArrayList<RecoveryClaim>();

		final PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		final int offSet = pageSpecification.offSet().intValue();

		if (countOfRecords.longValue() > offSet) {
			String orderByClause = listCriteria.getSortCriteriaString();
			if (!StringUtils.isEmpty(orderByClause))
				queryBuilder.append(" order by ").append(orderByClause);

			final String finalQuery = queryBuilder.toString();
			result = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Query query = session.createQuery(finalQuery);
							query.setProperties(filterParams);
							populateQueryParams(query, filterParams);
							populateQueryParams(query, searchParams);
							return query.setFirstResult(offSet).setMaxResults(
									pageSpecification.getPageSize()).list();
						}
					});
		}

		int noOfPages = pageSpecification.convertRowsToPages(countOfRecords);
		return new PageResult<RecoveryClaim>(result, pageSpecification,
				noOfPages);

	}

	private void populateSearchConditions(RecoveryClaimCriteria searchObj,
			StringBuilder queryBuilder,
			Map<String, Object> searchqueryParameters) {
		if (searchObj != null) {
			if (searchObj.getSelectedBusinessUnits() != null
					&& !(searchObj.getSelectedBusinessUnits().isEmpty())) {
				queryBuilder
						.append("and claim.businessUnitInfo in (")
						.append(
								searchObj
										.getSelectedBusinessUnitInfoDelimitedByComma())
						.append(")");
			}
			if (!StringUtils.isBlank(searchObj.getRecoveryClaimNumber())) {
				queryBuilder
						.append(spaceDelimiter)
						.append(
								" and  upper(recoveryClaimNumber) like upper('")
						.append(searchObj.getRecoveryClaimNumber()).append("%')");
			}
			if (!StringUtils.isBlank(searchObj.getDocumentNumber())) {
				queryBuilder
						.append(spaceDelimiter)
						.append(
								" and  upper(documentNumber) like upper('")
						.append(searchObj.getDocumentNumber()).append("%')");
			}
			if (!StringUtils.isBlank(searchObj.getClaimNumber())) {
				queryBuilder.append(spaceDelimiter).append(
						"and  upper(claim.claimNumber) like upper('").append(
						searchObj.getClaimNumber()).append("%')");
			}

			if (!StringUtils.isBlank(searchObj.getSupplierNumber())) {
				queryBuilder.append(spaceDelimiter).append(
						"and  contract.supplier.supplierNumber like '").append(
						searchObj.getSupplierNumber()).append("%'");
			}

			if (!StringUtils.isBlank(searchObj.getSupplierName())) {
				queryBuilder.append(spaceDelimiter).append(
						"and  upper(contract.supplier.name) like upper('").append(
						searchObj.getSupplierName()).append("%')");
			}
			//fix for 'unable to save processor recovery predefined search with 'status 'as search parameter'
			if (searchObj.getState()!=null && searchObj.getState().length>0) {
				queryBuilder.append(
						spaceDelimiter + "and  recoveryClaimState in (:states) ");
				List<ClaimState> claimStatus=new ArrayList<ClaimState>();
				for(String status:searchObj.getState())
				{
					claimStatus.add(getClaimStatus(status));
				}
				searchqueryParameters.put("states",claimStatus);
			}

			if (!StringUtils.isBlank(searchObj.getSupplierMemoNumber())) {
				queryBuilder
						.append(spaceDelimiter)
						.append(
								" and  upper(recoveryPayment.activeCreditMemo.creditMemoNumber) like upper('")
						.append(searchObj.getSupplierMemoNumber()).append("%')");
			}

			if (!StringUtils.isBlank(searchObj.getPartNumber())) {
				queryBuilder.append(spaceDelimiter).append(
						" and  contract.itemsCovered.number like '").append(
						searchObj.getPartNumber()).append("%'");
			}

			if ((searchObj.getStartClaimPayDate() != null)
					&& (searchObj.getEndClaimPayDate() != null)) {
				queryBuilder
						.append(
								" and recoveryPayment.activeCreditMemo.creditMemoDate >= :startClaimPayDate")
						.append(
								" and  recoveryPayment.activeCreditMemo.creditMemoDate <= :endClaimPayDate");
				searchqueryParameters.put("startClaimPayDate", searchObj
						.getStartClaimPayDate());
				searchqueryParameters.put("endClaimPayDate", searchObj
						.getEndClaimPayDate());
			}
			
			
			if ((searchObj.getStartWarrantyRequestDate() != null)
					&& (searchObj.getEndWarrantyRequestDate() != null)) {
				queryBuilder
					.append(" and  recClaim.d.createdOn >= :startWarrantyRequestDate")
					.append(" and  recClaim.d.createdOn <= :endWarrantyRequestDate ) ");
				searchqueryParameters.put("startWarrantyRequestDate", searchObj
						.getStartWarrantyRequestDate());
				searchqueryParameters.put("endWarrantyRequestDate", searchObj
						.getEndWarrantyRequestDate());

			}

			if ((searchObj.getStartClosedDate() != null)
					&& (searchObj.getEndClosedDate() != null)) {
				queryBuilder
						.append(
								" and  recClaim.id in ( select forRecoveryClaim from RecoveryClaimAudit audit  where")
						.append(" recoveryClaimState like'%CLOSED%'").append(
								" and  createdOn >= :startClosedDate").append(
								" and  createdOn <= :endClosedDate )");
				searchqueryParameters.put("startClosedDate", searchObj
						.getStartClosedDate());
				searchqueryParameters.put("endClosedDate", searchObj
						.getEndClosedDate());
			}

		}

	}
	
	private static Map<String, String> inboxFieldProjectionMap = new HashMap<String, String>();
	static {
		inboxFieldProjectionMap.put("claim","claim");
		inboxFieldProjectionMap.put("recoveryClaimNumber","recoveryClaimNumber");
		inboxFieldProjectionMap.put("recoveryClaimId","id");
	}  

	@SuppressWarnings("unchecked")
	public PageResult<SupplierPartReturnClaimSummary> findAllRecoveryClaimsMatchingCriteria(
			final PartRecoverySearchCriteria partRecoverySearchCriteria) {

		return (PageResult<SupplierPartReturnClaimSummary>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Criteria partRecoveryCriteria = session
								.createCriteria(RecoveryClaim.class);
						buildSearchCriteria(partRecoverySearchCriteria,
								partRecoveryCriteria);
						partRecoveryCriteria.setProjection(Projections
								.rowCount());

						long numResults = (Long) partRecoveryCriteria
								.uniqueResult();
						partRecoveryCriteria
					 	.setResultTransformer(Transformers.aliasToBean(SupplierPartReturnClaimSummary.class));
					
					ProjectionList projectionList = Projections.projectionList();
					for(String key : inboxFieldProjectionMap.keySet())
						projectionList.add(new CustomPropertyAliasProjection(inboxFieldProjectionMap.get(key),key));
						partRecoveryCriteria.setProjection(projectionList);
						partRecoveryCriteria
								.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

						PageSpecification pageSpecification = partRecoverySearchCriteria
								.getPageSpecification();

						List<SupplierPartReturnClaimSummary> matchingPRs = new ArrayList<SupplierPartReturnClaimSummary>();

						if (numResults > 0
								&& numResults > pageSpecification.offSet()) {
							matchingPRs = partRecoveryCriteria.setFirstResult(
									pageSpecification.offSet()).setMaxResults(
									pageSpecification.getPageSize())
									.setResultTransformer(Transformers.aliasToBean(SupplierPartReturnClaimSummary.class))
									.list();
						}

						return new PageResult<SupplierPartReturnClaimSummary>(matchingPRs,
								pageSpecification, pageSpecification
										.convertRowsToPages(numResults));
					}

				});
	}

	private void buildSearchCriteria(PartRecoverySearchCriteria searchCriteria,
			Criteria partRecoveryCriteria) {
		List<String> addedAliases = new ArrayList<String>(10);
		
		if (searchCriteria.getSelectedBusinessUnits() != null
				&& (!(searchCriteria.getSelectedBusinessUnits().isEmpty()))) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(partRecoveryCriteria,
							"businessUnitInfo", addedAliases);
			partRecoveryCriteria.add(Restrictions.in(processedProperty,
					searchCriteria.getSelectedBusinessUnits()));

		}
		
		if (!StringUtils.isBlank(searchCriteria.getSupplierName())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(partRecoveryCriteria,
							"contract.supplier.name", addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getSupplierName().concat("%")));
		}
		if (!StringUtils.isBlank(searchCriteria.getSupplierNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(partRecoveryCriteria,
							"contract.supplier.supplierNumber", addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getSupplierNumber().concat("%")));
		}
		if (!StringUtils.isBlank(searchCriteria.getPartNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(partRecoveryCriteria,
							"recoveryClaimInfo.recoverableParts.oemPart.itemReference.unserializedItem.number", addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getPartNumber().concat("%")));
		}
		if (!StringUtils.isBlank(searchCriteria.getClaimNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(partRecoveryCriteria,
							"claim.claimNumber", addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getClaimNumber().concat("%")));
		}
		if (!StringUtils.isBlank(searchCriteria.getTrackingNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partRecoveryCriteria,
							"recoveryClaimInfo.recoverableParts.supplierPartReturns.supplierShipment.trackingId",
							addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getTrackingNumber().concat("%")));
		}
		if (searchCriteria.getStatus() != null) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partRecoveryCriteria,
							"recoveryClaimInfo.recoverableParts.supplierPartReturns.status",
							addedAliases);
			partRecoveryCriteria.add(Restrictions.eq(processedProperty,searchCriteria.getStatus()));

		}
		if (!StringUtils.isBlank(searchCriteria.getRgaNumber())) {
			String processedProperty = criteriaHelper
					.processNestedAssociations(
							partRecoveryCriteria,
							"recoveryClaimInfo.recoverableParts.supplierPartReturns.rgaNumber",
							addedAliases);
			partRecoveryCriteria.add(Restrictions.ilike(processedProperty,
					searchCriteria.getRgaNumber().concat("%")));

		}
		
		          
        }
	@SuppressWarnings("unchecked")
	public List<PartReturnStatus> findAllStatusForPartRecovery() {
		String queryString = "select distinct b.status from BasePartReturn b order by b.status";
		Query query = getSession().createQuery(queryString);
		return query.list();
	}

	@SuppressWarnings("unchecked")
    public RecoveryClaim findActiveRecoveryClaimForClaimForOfflineDebit(String warrantyClaimNumber) {
		List<RecoveryClaim> recoveryClaims = getHibernateTemplate()
		.find(
				"from RecoveryClaim a where a.recoveryClaimNumber=? and a.recoveryClaimState in ('READY_FOR_DEBIT','NO_RESPONSE_AND_AUTO_DEBITTED','DISPUTED_AND_AUTO_DEBITTED')",
				warrantyClaimNumber);
		return recoveryClaims.size() == 0 ? null : recoveryClaims.get(0);
	}

    @SuppressWarnings("unchecked")
    public List<SupplierRecoveryPaymentAudit> fetchAllRecClaimPaymentAudits(final List<Long> recClaimAuditIds) {
        return (List<SupplierRecoveryPaymentAudit>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                String queryString = "select distinct srpa from SupplierRecoveryPaymentAudit srpa where "
                    + " srpa.recClaimAuditId in (:claimAuditIds)";
                Map<String, Object> params = new HashMap<String, Object>(2);
                params.put("claimAuditIds", recClaimAuditIds);

                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.list();
            }
        });
    }

    public void updateAllRecClaimPaymentAudits(SupplierRecoveryPaymentAudit paymentAudit) {
        getHibernateTemplate().saveOrUpdate(paymentAudit);
    }
    
    public List<RecoveryClaim> findRecoveryClaimsForVRRDownload(final PartRecoverySearchCriteria criteria, 
            final int pageNum, final int recordsPerPage){
        return (List<RecoveryClaim>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return getHibernateTemplate().execute(new HibernateCallback() {

                    public Object doInHibernate(Session session) throws HibernateException, SQLException {
                        return session.createSQLQuery(buildSQLQuery(criteria, false))
                                .addEntity(RecoveryClaim.class)
                                .setParameterList("businessUnitNames", criteria.getSelectedBusinessUnits())
                                .setFirstResult(pageNum * recordsPerPage)
                                .setMaxResults(recordsPerPage).list();
                    }
                });
            }
        });
    }
    
    public Long findRecClmsCountForVRRDownload(final PartRecoverySearchCriteria criteria){
        return (Long) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createSQLQuery(buildSQLQuery(criteria, true))
                        .addScalar("count", new org.hibernate.type.LongType())
                        .setParameterList("businessUnitNames", criteria.getSelectedBusinessUnits())
                        .uniqueResult();
            }
        });
    }
    
    private String buildSQLQuery(PartRecoverySearchCriteria criteria, boolean isCountQuery){
        StringBuilder sb = (isCountQuery) ? new StringBuilder("select count(rc.id) count ") : new StringBuilder("select rc.* ");
        sb.append("from RECOVERY_CLAIM rc ");
        if(StringUtils.isNotBlank(criteria.getSupplierNumber())){
            sb.append(", SUPPLIER s, CONTRACT c ");
        }
        sb.append("where ");
        sb.append("rc.BUSINESS_UNIT_INFO in(:businessUnitNames) and rc.D_ACTIVE = 1 ");
        buildDateClause(sb, "rc.D_CREATED_ON", criteria.getFromCreatedDate(),criteria.getToCreatedDate());
        buildDateClause(sb, "rc.D_UPDATED_ON", criteria.getFromUpdatedDate(),criteria.getToUpdatedDate());
        if(criteria.getRecoveryClaimState() != null){
            sb.append("and rc.RECOVERY_CLAIM_STATE = '").append(criteria.getRecoveryClaimState().name()).append("' ");
        }            
        if(StringUtils.isNotBlank(criteria.getSupplierNumber())){
            sb.append("and rc.CONTRACT = c.ID and c.SUPPLIER = s.id and s.SUPPLIER_NUMBER = '")
                    .append(criteria.getSupplierNumber()).append("' ");
        }
        return sb.toString();
    }

    private void buildDateClause(StringBuilder sb, String colName, CalendarDate fromDate, CalendarDate toDate) {
        if(fromDate == null && toDate == null) 
            return;
        if(fromDate != null && toDate != null){
            sb.append("and ").append(colName).append(" between '").append(fromDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)).
                    append("' and '").append(toDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)).append("' ");
        }else if(fromDate != null){
            sb.append("and ").append(colName).append(" >= '").append(fromDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)).append("' ");
        }else{
            sb.append("and ").append(colName).append(" <= '").append(toDate.toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY)).append("' ");
        }
    }
    
    public RecoveryClaim findRecoveryClaimByPartNumber(final Map<String,Object> params) {
        Object []paramValue={ params.get("partNumber"),params.get("taskName"),params.get("actorId")};
        final String spquery=   "select recoveryClaim from TaskInstance taskInstance, HibernateLongInstance vi,"
              + "ModuleInstance mi,"
              + "RecoveryClaim recoveryClaim join recoveryClaim.recoveryClaimInfo.recoverableParts as recoverableParts join recoverableParts.supplierItem as supplierItem,"
              + "Claim claim join claim.claimedItems as claimedItems "
              + "where taskInstance.isOpen = true  "
              + "and taskInstance.taskMgmtInstance = mi "
              + "and mi.processInstance = vi.processInstance "
              + "and vi.name = 'recoveryClaim' "
              + "and vi.class = 'H'  "
              + "and recoveryClaim.claim = claim.id "
              + "and vi.value.id = recoveryClaim.id "
              + "and supplierItem.number = ? "
              + "and taskInstance.task.name= ? "
              + "and taskInstance.actorId = ? ";
              List<RecoveryClaim> list = getHibernateTemplate().find(spquery, paramValue);
                    return list.get(0);
  }     

    public  ClaimState getClaimStatus(String status)
    {
        if(!StringUtils.isBlank(status)){
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
    
    public String findRecClaimFromPartReturn(final Long partReturnId){
    	  return (String) getHibernateTemplate().execute(new HibernateCallback() {
              public Object doInHibernate(Session session) throws HibernateException, SQLException {
                  String queryString = getQueryStringForConfirmDealerPRs();
                  Map<String, Object> params = new HashMap<String, Object>(2);
                  params.put("partReturnId", partReturnId);
                  params.put("taskName", CONFIRM_DEALER_PART_RETURNS_TASK);
                  Query query = session.createSQLQuery(queryString);
                  query.setProperties(params);
                  return query.uniqueResult();
              }
          });
    }
    
    private String getQueryStringForConfirmDealerPRs(){
    	
    	return "select rec.recovery_claim_number from oem_part_replaced oem,"
    		 +"base_part_return b,jbpm_taskinstance j,recovery_claim rec,recovery_claim_info recInfo,recoverable_part recPart,rec_claim_info_rec_parts recParts where b.oem_part_replaced =oem.id "
    		 +"and b.id  = j.part_return_id and j.part_return_id= :partReturnId "
    		 +"and recinfo.recovery_claim = rec.id and recparts.recovery_claim_info = recInfo.id and recPart.id = recparts.recoverable_parts "
    		 +"and recPart.oem_part =oem.id and j.isopen_=1 and j.name_= :taskName";
    }
    
}

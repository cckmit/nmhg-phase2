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
package tavant.twms.worklist.supplier;

import java.sql.SQLException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.CustomTaskInstance;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.QueryExecutionCallBack;
import tavant.twms.worklist.WorkListCriteria;

/**
 * 
 * @author kannan.ekanath
 * 
 */

public class SupplierRecoveryWorkListDao extends HibernateDaoSupport
		implements
			SupplierRecoveryQueries {
	
	ConfigParamService configParamService;

	private static final Logger logger = Logger
			.getLogger(SupplierRecoveryWorkListDao.class);

	public InboxItemList getSupplierRecoveryClaimList(
			final WorkListCriteria criteria) {
		if(criteria.getUser().hasRole("masterSupplier") || criteria.getUser().isInternalUser()) {
			return execute("recoveryClaim", ALL_ACTORS_REC_CLAIM_VIEW_QUERY,
					criteria);
		}
		return execute("recoveryClaim", SUPPLIER_RECOVERY_CLAIM_VIEW_QUERY,
				criteria);
	}
	
	public InboxItemList getSupplierRecoveryPartList(
			final WorkListCriteria criteria) {
		return execute("distinct supplierPartReturns", SUPPLIER_RECOVERY_PART_VIEW_QUERY,
				criteria);
		}
	
	public InboxItemList getSupplierRecoveryTaskList(
			final WorkListCriteria criteria) {
		return execute("taskInstance", PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY_AT_BARCODE_LEVEL,
				criteria);
	}

	public InboxItemList getSupplierShipmentList(final WorkListCriteria criteria) {
		return execute("distinct shipment", PART_SHIPPER_SHIPMENT_AND_CLAIM_VIEW_QUERY_BARCODE_ENABLED, criteria);
	}

	public InboxItemList getSupplierPartReceiptList(final WorkListCriteria criteria) {
        Map<String, Object> params = criteria.getParameterMap();
        params.put("actorId", criteria.getUser().getName());
        params.put("taskName", criteria.getTaskName());
        String queryForExec= null;
        if(criteria.getUser().hasRole("masterSupplier"))
        	queryForExec = MASTER_SUPPLIER_CONFIRM_PARTRETURNS_VIEW_QUERY;
        else
        	queryForExec = SUPPLIER_CONFIRM_PARTRETURNS_VIEW_QUERY;
        HibernateCallback callBack = new QueryExecutionCallBack("distinct taskInstance",
        		queryForExec, criteria, params) {
            @Override
            protected List transformResults(List list) {
                    List results = new ArrayList(list.size());
                    Map<String, TaskInstance> data = new HashMap<String, TaskInstance>();
                    for (int i = 0; i < list.size(); i++) {
                        Object[] resultArray = (Object[]) list.get(i);
                        CustomTaskInstance customTaskInstance = (CustomTaskInstance) resultArray[0];
                        SupplierPartReturn spr = (SupplierPartReturn) customTaskInstance.getVariable("supplierPartReturn");
                        String shipmentNo = spr.getSupplierShipment().getTransientId();
                        data.put(shipmentNo, customTaskInstance);
                    }
                    results.addAll(data.values());
                    return results;
                }
            @Override
            protected int executeCountQuery(Session session) {
                /**
                 * TODO : Bad performance, Unable to get a count query to work
                 * For now, execute the filterquery without setting first
                 * results etc and then run it
                 */
                String filterQueryString = getFilterQuery();
                if (logger.isInfoEnabled()) {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if (logger.isInfoEnabled()) {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString)
                        .setProperties(bindParams);
                List results = filterQuery.list();
                return transformResults(results).size();
            }
            
            /**
             * TODO : Bad performance
             * For now, executing the filterquery without setting first
             * results etc
             */
            @Override
            protected List executeFilterQuery(Session session) {
                String filterQueryString = getFilterQuery();
                if(logger.isInfoEnabled())
                {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if(logger.isInfoEnabled())
                {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString).setProperties(bindParams);
                return(transformResults(filterQuery.list()));
            }

        };

        return (InboxItemList) getHibernateTemplate().execute(callBack);
	}

	public InboxItemList getSupplierLocationList(final WorkListCriteria criteria) {
		Map<String, Object> params = criteria.getParameterMap();
		params.put("actorId", criteria.getUser().getName());
		params.put("taskName", criteria.getTaskName());

		HibernateCallback callBack = new QueryExecutionCallBack("taskInstance",
				SupplierRecoveryWorkListDao.PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY_AT_BARCODE_LEVEL, criteria, params) {
			@Override
			protected int executeCountQuery(Session session) {
				/**
				 * TODO : Bad performance, Unable to get a count query to work
				 * For now, execute the filterquery without setting first
				 * results etc and then run it
				 */
				String filterQueryString = getFilterQuery();
				if (logger.isInfoEnabled()) {
					logger.info("Filter query is [" + filterQueryString + "]");
				}
				Map<String, Object> bindParams = getBindParams();
				if (logger.isInfoEnabled()) {
					logger.info("Count query params " + bindParams);
				}
				Query filterQuery = session.createQuery(filterQueryString)
						.setProperties(bindParams);
				return (filterQuery.list().size());
			}

		};
		return (InboxItemList) getHibernateTemplate().execute(callBack);
	}

	public InboxItemList getSupplierClaimAndShipmentList(
			final WorkListCriteria criteria) {

		Map<String, Object> params = criteria.getParameterMap();
		params.put("actorId", criteria.getUser().getName());
		params.put("taskName", criteria.getTaskName());
		final String entityToSelect = "recoveryClaim";
		HibernateCallback callBack = new QueryExecutionCallBack(entityToSelect,
				SUPPLIER_RESPONSE_VIEW_QUERY, criteria, params) {

			@Override
			protected int executeCountQuery(Session session) {
				/**
				 * TODO : Bad performance, Unable to get a count query to work
				 * For now, execute the filterquery without setting first
				 * results etc and then run it
				 */
				String filterQueryString = getFilterQuery();
				if (logger.isInfoEnabled()) {
					logger.info("Filter query is [" + filterQueryString + "]");
				}
				Map<String, Object> bindParams = getBindParams();
				if (logger.isInfoEnabled()) {
					logger.info("Count query params " + bindParams);
				}
				Query filterQuery = session.createQuery(filterQueryString)
						.setProperties(bindParams);
				return (filterQuery.list().size());
			}

		};
		return (InboxItemList) getHibernateTemplate().execute(callBack);
	}

	public InboxItemList getDuePartClaimList(final WorkListCriteria criteria) {

		return execute("distinct recoveryClaim", DUE_PART_VIEW_QUERY, criteria);
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllNotShippedPartTasksForLocation(OEMPartReplaced part) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partId", part.getId());
		return (List<TaskInstance>) execute(NOT_SHIPPED_PARTS_FOR_PART, params);
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findDuePartNotGeneratedTasks(Claim claim) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("claimId", claim.getId());
		return (List<TaskInstance>) execute(DUE_PARTS_NOT_GENERATED_FOR_CLAIM,
				params);
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findPartShipperShipmentOpenTasks(RecoveryClaim recClaim) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recClaimId", recClaim.getId());
		return (List<TaskInstance>) execute(
				NOT_SHIPPED_PARTS_FOR_RECOVERY_CLAIM, params);
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForSupplierLocation(
			String taskName, String actorId, Long recClaimId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recClaimId", recClaimId);
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_FOR_SUPPLIER_LOCATION_QUERY, params);
		
		return tasks;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getRoutedTaskInstancesForRecClaimId(Long recClaimId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recClaimId", recClaimId);
		params.put("taskName", WorkflowConstants.ROUTED_TO_NMHG);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				ROUTED_TASKS_FOR_REC_CLAIM, params);
		return tasks;
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForClaimLocation(Long recClaimId,
			String taskName, String actorId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", recClaimId);
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_FOR_DUE_PART_QUERY, params);
		Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
		return tasks;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForClaimLocation(Long recClaimId,
			String taskName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", recClaimId);
		params.put("taskName", taskName);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_FOR_SUPPLIER_PARTS_RECEIPT_QUERY, params);
		Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
		return tasks;
	}

	@SuppressWarnings("unchecked")
	public List<RecoverablePart> getRecoveredPartsForPreview(Long recClaimId,
			 String actorId , String taskName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", recClaimId);
		params.put("actorId", actorId);
		params.put("taskName", taskName);
		List<RecoverablePart> recoveredParts = (List<RecoverablePart>) execute(
				RECOVERED_PARTS_DETAIL_QUERY, params);
		return recoveredParts;
	}

	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForSupplier(Long supplierId,
			String taskName, String actorId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", supplierId);
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_FOR_SUPPLIER_QUERY, params);
		Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
		return tasks;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForPartsToBeAdded(Location locationId, String taskName, String actorId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		List<TaskInstance> tasks = null;
		params.put("locationId", locationId);
		tasks = (List<TaskInstance>) execute(PREVIEW_TASK_LIST_FOR_ADDPARTS, params);
		Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
		return tasks;
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForSupplierShipment(
			Long shipmentId, String taskName, String actorId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("shipmentId", shipmentId);
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		List<TaskInstance> tasks = (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_FOR_SHIPMENT_QUERY, params);
		Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
		return tasks;
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> getPreviewPaneForShipmentAndClaimGroup(
			Long claimId, Long shipmentId, String taskName, String actorId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("claimId", claimId);
		params.put("shipmentId", shipmentId);
		params.put("taskName", taskName);
		params.put("actorId", actorId);
		return (List<TaskInstance>) execute(
				PREVIEW_TASK_LIST_SHIPMENT_CLAIM_QUERY, params);
	}
	
	public List<TaskInstance> getTasksForSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns,
			String taskName) {
		if (!CollectionUtils.isEmpty(supplierPartReturns)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("supplierPartReturns", supplierPartReturns);
			params.put("taskName", taskName);
			return (List<TaskInstance>) execute(TASKS_LIST_FOR_SUPPLIER_PART_RETURNS, params);

		}
		return Collections.EMPTY_LIST;
	}
	
	public TaskInstance getTaskForSupplierPartReturn(SupplierPartReturn supplierPartReturn,String taskName) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierPartReturn",supplierPartReturn );
		params.put("taskName",taskName );
		
		return (TaskInstance)getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("Executing [" + TASK_FOR_SUPPLIER_PART_RETURN + "] with params "
							+ params);
				}
				return session.createQuery(TASK_FOR_SUPPLIER_PART_RETURN).setProperties(params).uniqueResult();
			}

		});		
	}
	
	public List<SupplierPartReturn> getSupplierPartReturns(Claim claim,OEMPartReplaced oemPartReplaced , Contract contract){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("claim", claim);
		params.put("oemPart", oemPartReplaced);
		params.put("contract", contract);
		return (List<SupplierPartReturn>) execute(
				SUPPLIER_PART_RETURNS_FOR_OEMPART, params);
		
	}

	private InboxItemList execute(String entity, String query,
			WorkListCriteria criteria) {
		Map<String, Object> params = criteria.getParameterMap();
		params.put("actorId", criteria.getUser().getName());
		params.put("taskName", criteria.getTaskName());
		HibernateCallback callBack = new QueryExecutionCallBack(entity, query,
				criteria, params);
		return (InboxItemList) getHibernateTemplate().execute(callBack);
	}

	private Object execute(final String query, final Map<String, Object> params) {
		return getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("Executing [" + query + "] with params "
							+ params);
				}
				return session.createQuery(query).setProperties(params).list();
			}

		});
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    //All part shipper can view the tasks in Supplier Parts Claimed inbox. Refer CR/BUG NMHGSLMS-196
    public InboxItemList getSupplierLocationListForAssignedShipper(final WorkListCriteria criteria) {
        Map<String, Object> params = criteria.getParameterMap();
        params.put("actorId", criteria.getUser().getName());
        params.put("taskName", criteria.getTaskName());

        HibernateCallback callBack = new QueryExecutionCallBack("distinct taskInstance",
                SupplierRecoveryWorkListDao.PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY, criteria, params) {
            @Override
            protected int executeCountQuery(Session session) {
                /**
                 * TODO : Bad performance, Unable to get a count query to work
                 * For now, execute the filterquery without setting first
                 * results etc and then run it
                 */
                String filterQueryString = getFilterQuery();
                if (logger.isInfoEnabled()) {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if (logger.isInfoEnabled()) {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString)
                        .setProperties(bindParams);
                List results = filterQuery.list();
                return transformResults(results).size();
            }

            @Override
            protected List transformResults(List list) {
                List results = new ArrayList(list.size());
                Map<Long, TaskInstance> data = new HashMap<Long, TaskInstance>();
                for (int i = 0; i < list.size(); i++) {
                    Object[] resultArray = (Object[]) list.get(i);
                    CustomTaskInstance customTaskInstance = (CustomTaskInstance) resultArray[0];
                    Long claimId = customTaskInstance.getClaimId();
                    data.put(claimId, customTaskInstance);
                }
                results.addAll(data.values());
                return results;
            }

        };
        return (InboxItemList) getHibernateTemplate().execute(callBack);
    }
    
    public InboxItemList getRoutedPartReturnRequests(final WorkListCriteria criteria) {
        Map<String, Object> params = criteria.getParameterMap();
        params.put("taskName", criteria.getTaskName());

        HibernateCallback callBack = new QueryExecutionCallBack("distinct recoveryClaim",
                ROUTED_TO_NMHG_QUERY, criteria, params) {
            @Override
            protected int executeCountQuery(Session session) {
                /**
                 * TODO : Bad performance, Unable to get a count query to work
                 * For now, execute the filterquery without setting first
                 * results etc and then run it
                 */
                String filterQueryString = getFilterQuery();
                if (logger.isInfoEnabled()) {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if (logger.isInfoEnabled()) {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString)
                        .setProperties(bindParams);
                return (filterQuery.list().size());
            }

        };
        return (InboxItemList) getHibernateTemplate().execute(callBack);
    }

    public InboxItemList getPartShipperRecoveryClaimList(
            final WorkListCriteria criteria) {
        return execute("distinct recoveryClaim", PART_SHIPPER_RECOVERY_CLAIM_VIEW_QUERY,
                criteria);
    }

    public List<Shipment> getAllShipmentsForRecoveryClaim(
            final Long recClaimId, final String taskName) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("recClaimId",recClaimId );
        params.put("taskName",taskName);
        return (List<Shipment>) execute(SHIPMENT_LIST_FOR_SUPPLIER_PART_RETURNS, params);


    }

    public List<TaskInstance> getAllTaskInstancesForRecoveryClaim(
            final Long recClaimId, final String taskName) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("recClaimId",recClaimId );
        params.put("taskName",taskName);
        return (List<TaskInstance>) execute(TASK_LIST_FOR_SUPPLIER_PART_RETURNS, params);


    }

    public InboxItemList getPartReceiverReceiptView(final WorkListCriteria criteria) {
        Map<String, Object> params = criteria.getParameterMap();
        params.put("actorId", criteria.getUser().getName());
        params.put("taskName", criteria.getTaskName());

        HibernateCallback callBack = new QueryExecutionCallBack("distinct taskInstance",
                SupplierRecoveryWorkListDao.PART_RECEIVER_RECOVERY_VIEW_QUERY, criteria, params) {
            @Override
            protected List transformResults(List list) {
                    List results = new ArrayList(list.size());
                    Map<Long, TaskInstance> data = new HashMap<Long, TaskInstance>();
                    for (int i = 0; i < list.size(); i++) {
                        Object[] resultArray = (Object[]) list.get(i);
                        CustomTaskInstance customTaskInstance = (CustomTaskInstance) resultArray[0];
                        Long claimId = customTaskInstance.getClaimId();
                        data.put(claimId, customTaskInstance);
                    }
                    results.addAll(data.values());
                    return results;
                }
            @Override
            protected int executeCountQuery(Session session) {
                /**
                 * TODO : Bad performance, Unable to get a count query to work
                 * For now, execute the filterquery without setting first
                 * results etc and then run it
                 */
                String filterQueryString = getFilterQuery();
                if (logger.isInfoEnabled()) {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if (logger.isInfoEnabled()) {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString)
                        .setProperties(bindParams);
                List results = filterQuery.list();
                return transformResults(results).size();
            }
            
            /**
             * TODO : Bad performance
             * For now, executing the filterquery without setting first
             * results etc
             */
            @Override
            protected List executeFilterQuery(Session session) {
                String filterQueryString = getFilterQuery();
                if(logger.isInfoEnabled())
                {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if(logger.isInfoEnabled())
                {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString).setProperties(bindParams);
                return(transformResults(filterQuery.list()));
            }

        };


        return (InboxItemList) getHibernateTemplate().execute(callBack);
    }
    
    public InboxItemList getPartInspectorInspectView(final WorkListCriteria criteria) {
        Map<String, Object> params = criteria.getParameterMap();
        params.put("actorId", criteria.getUser().getName());
        params.put("taskName", criteria.getTaskName());

        HibernateCallback callBack = new QueryExecutionCallBack("distinct taskInstance",
                SupplierRecoveryWorkListDao.PART_INSPECTOR_RECOVERY_VIEW_QUERY, criteria, params) {
            @Override
            protected List transformResults(List list) {
                    List results = new ArrayList(list.size());
                    Map<Long, TaskInstance> data = new HashMap<Long, TaskInstance>();
                    for (int i = 0; i < list.size(); i++) {
                        Object[] resultArray = (Object[]) list.get(i);
                        CustomTaskInstance customTaskInstance = (CustomTaskInstance) resultArray[0];
                        Long claimId = customTaskInstance.getClaimId();
                        data.put(claimId, customTaskInstance);
                    }
                    results.addAll(data.values());
                    return results;
                }
            @Override
            protected int executeCountQuery(Session session) {
                /**
                 * TODO : Bad performance, Unable to get a count query to work
                 * For now, execute the filterquery without setting first
                 * results etc and then run it
                 */
                String filterQueryString = getFilterQuery();
                if (logger.isInfoEnabled()) {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if (logger.isInfoEnabled()) {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString)
                        .setProperties(bindParams);
                List results = filterQuery.list();
                return transformResults(results).size();
            }
            
            /**
             * TODO : Bad performance
             * For now, executing the filterquery without setting first
             * results etc
             */
            @Override
            protected List executeFilterQuery(Session session) {
                String filterQueryString = getFilterQuery();
                if(logger.isInfoEnabled())
                {
                    logger.info("Filter query is [" + filterQueryString + "]");
                }
                Map<String, Object> bindParams = getBindParams();
                if(logger.isInfoEnabled())
                {
                    logger.info("Count query params " + bindParams);
                }
                Query filterQuery = session.createQuery(filterQueryString).setProperties(bindParams);
                return(transformResults(filterQuery.list()));
            }

        };


        return (InboxItemList) getHibernateTemplate().execute(callBack);
    }
    
    public InboxItemList getSupplierRecoveryDistinctRecoveryClaimList(
            final WorkListCriteria criteria) {
    	if(criteria.getUser().hasRole("masterSupplier") || criteria.getUser().isInternalUser()) {
			return execute("recoveryClaim", ALL_ACTORS_REC_CLAIM_VIEW_QUERY,
					criteria);
		}
        return execute("distinct recoveryClaim", SUPPLIER_RECOVERY_CLAIM_VIEW_QUERY,
                criteria);
    }

    public List<TaskInstance> getConfirmPartReturnsInstances(RecoveryClaim recClaimId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recClaimId", recClaimId.getId());
        return (List<TaskInstance>) execute(
                PART_RETURN_INSTANCES, params);
    }

    public List<TaskInstance> getRecoveryShipmentGeneratedInstances(RecoveryClaim recClaimId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recClaimId", recClaimId.getId());
        return (List<TaskInstance>) execute(
                SUPPLIER_SHIPMENT_GENERATED_INSTANCES, params);
    }

    public List<TaskInstance> getConfirmDealerPartReturnsInstances(Claim claim){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("claimId", claim.getId());
        return (List<TaskInstance>) execute(
                CONFIRM_DEALER_PART_RETURN_INSTANCES, params);
    }

    public List<TaskInstance> getPartReturnShipmentInstances(Claim claim){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("claimId", claim.getId());
        return (List<TaskInstance>) execute(
                SHIPMENT_GENERATED_INSTANCES, params);
    }

    public List<TaskInstance> findAllShipmentAwaitedTasks(List<RecoverablePart> tobeShippedParts, String taskName){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tobeShippedParts",tobeShippedParts);
        params.put("taskName",taskName);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_TO_BE_SHIPPED_PARTS_FROM_NMHG, params);
    }

    public List<TaskInstance> findAllAwaitedTasks(RecoverablePart tobeShippedParts, String taskName){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tobeShippedParts",tobeShippedParts);
        params.put("taskName",taskName);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_TO_BE_SHIPPED_PARTS_FROM_NMHG_FOR_SUPPLIER, params);
    }
}

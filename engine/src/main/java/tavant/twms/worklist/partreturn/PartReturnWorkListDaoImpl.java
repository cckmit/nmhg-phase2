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
package tavant.twms.worklist.partreturn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.scheduler.exe.Timer;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.CustomTaskInstance;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.QueryExecutionCallBack;
import tavant.twms.worklist.WorkListCriteria;


/**
 *
 * @author subin.p
 *
 */
public class PartReturnWorkListDaoImpl extends HibernateDaoSupport implements PartReturnQueries, PartReturnWorkListDao {

    private static final Logger logger = Logger.getLogger(PartReturnWorkListDaoImpl.class);

    public PartReturnWorkList getPartReturnWorkListByLocation(WorkListCriteria criteria) {
    	return execute("location", LOCATION_VIEW_FOR_DEALER_QUERY + createWhereClauseBasedOnUser(criteria), criteria);
    }

    public PartReturnWorkList getPartReturnWorkListByClaim(WorkListCriteria criteria) {
    	if(WorkflowConstants.DUE_PARTS_RECEIPT.equals(criteria.getTaskName()))
    		return execute("claim", DUE_PARTS_RECEIPT_CLAIM_VIEW_QUERY, criteria);
    	else if(WorkflowConstants.DUE_PARTS_INSPECTION.equals(criteria.getTaskName()))
    		return execute("claim", DUE_PARTS_INSPECTION_CLAIM_VIEW_QUERY, criteria);
        else if(WorkflowConstants.DEALER_REQUESTED_PART.equals(criteria.getTaskName()))
            return  execute("claim", CLAIM_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY , criteria);
        else if(WorkflowConstants.WPRA_TO_BE_GENERATED.equals(criteria.getTaskName()))
            return  execute("claim", CLAIM_VIEW_QUERY_COMMON , criteria);
        else
    		return execute("claim", CLAIM_VIEW_QUERY_COMMON + createWhereClauseBasedOnUser(criteria), criteria);
    }

    public PartReturnWorkList getPartReturnWorkListByShipment(WorkListCriteria criteria) {
    	if(WorkflowConstants.DUE_PARTS_RECEIPT.equals(criteria.getTaskName()))
    		return execute("shipment", DUE_PARTS_RECEIPT_SHIPMENT_VIEW_QUERY, criteria);
    	else if(WorkflowConstants.DUE_PARTS_INSPECTION.equals(criteria.getTaskName()))
    		return execute("shipment", DUE_PARTS_INSPECTION_SHIPMENT_VIEW_QUERY, criteria);
        else if(WorkflowConstants.CLAIMED_PARTS_RECEIPT.equals(criteria.getTaskName()))
            return execute("shipment", SHIPMENT_VIEW_FOR_DEALER_QUERY + " and task.actorId in(select user.name from User user  join user.belongsToOrganizations as org where org.id = :belongsToOrg)", criteria);
    	else if(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(criteria.getTaskName()) || WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED.equals(criteria.getTaskName())){
    		return execute("shipment",SHIPMENT_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY, criteria);
    	} else if(WorkflowConstants.CONFIRM_DEALER_PART_RETURNS.equalsIgnoreCase(criteria.getTaskName())) {
    		return this.getPartReturnWorkListByTaskInstance(criteria);
    	}
    	return execute("shipment", SHIPMENT_VIEW_FOR_DEALER_QUERY + createWhereClauseBasedOnUser(criteria), criteria);
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllTasksForShipment(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_SHIPMENT_QUERY, criteria);
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> printAllTasksForShipment(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_SHIPMENT_QUERY_PRINT, criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllTasksForLocation(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_LOCATION_QUERY + createWhereClauseBasedOnUser(criteria), criteria);
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllTasksForClaim(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_CLAIM_QUERY, criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<TaskInstance> findPartReturnInspectionTasksForClaim(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_CLAIM_QUERY_FOR_INSPECTION, criteria);
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findPartReturnReceiptTasksForClaim(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_CLAIM_QUERY_FOR_RECEIPT, criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllDueAndOverduePartTasksForLocation(
            final WorkListCriteria criteria) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("shipmentId", new Long(criteria.getIdentifier()));
        params.put("actorId", criteria.getUser().getName());
        params.put("dealerId", criteria.getServiceProvider());
        if(criteria.getServiceProviderList() != null)
        	params.put("dealerIds", criteria.getServiceProviderList());
        return (List<TaskInstance>) execute(DUE_AND_OVERDUE_PARTS_FOR_LOCATION + createWhereClauseBasedOnUser(criteria), params);
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllNotShippedPartTasksForLocation(Claim claim) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("claimId", claim.getId());
        return (List<TaskInstance>) execute(NOT_SHIPPED_PARTS_FOR_CLAIM, params);
    }
    
    private PartReturnWorkList execute(String entity, String query, WorkListCriteria criteria) {
        // This query would use distinct on the id and this mandates a default sort on that column.
        Map<String, Object> params = criteria.getParameterMap();
        if( AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType())) {
            params.put("actorId", criteria.getUser().getBelongsToOrganization().getId().toString());
        }else {
            params.put("actorId", criteria.getUser().getName());
        }
        params.put("belongsToOrg", criteria.getServiceProvider().getId());
        params.put("taskName", criteria.getTaskName());
        params.put("dealerId", criteria.getServiceProvider());
        
        if(criteria.getServiceProviderList() != null)
            params.put("dealerIds", criteria.getServiceProviderList());
        HibernateCallback callBack = new QueryExecutionCallBack("distinct " + entity, query,
                criteria, params);
        return new PartReturnWorkList((InboxItemList) getHibernateTemplate().execute(callBack));
    }
    
    private PartReturnWorkList execute(String entity, String query, WorkListCriteria criteria, String wpra) {
        // This query would use distinct on the id and this mandates a default sort on that column.
        Map<String, Object> params = criteria.getParameterMap();
        if( AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType())) {
            params.put("actorId", criteria.getUser().getBelongsToOrganization().getId().toString());
        }else {
            params.put("actorId", criteria.getUser().getName());
        }

        params.put("dealerId", criteria.getServiceProvider());
        params.put("wpraNumber", wpra);
        
        HibernateCallback callBack = new QueryExecutionCallBack(entity, query,
                criteria, params);
        return new PartReturnWorkList((InboxItemList) getHibernateTemplate().execute(callBack));
    }
    
    private Object execute(final String query, final WorkListCriteria criteria) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", new Long(criteria.getIdentifier()));
        params.put("taskName", criteria.getTaskName());
        setQueryParameterBasedOnUser(params,criteria);        
        return execute(query, params);
    }
    
    private PartReturnWorkList executeQuery(String entity, String query, WorkListCriteria criteria) {
    	  Map<String, Object> params = criteria.getParameterMap();
    	  
    	  if(AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType())){
    		  if(criteria.getServiceProviderList() != null){
    			  query+=" and (claim.forDealer in (:dealerIds) ) " ;
                  params.put("dealerIds", criteria.getServiceProviderList());
    		  }
    	  } else if(!AdminConstants.INTERNAL.equals(criteria.getUser().getUserType())) {
    		  query+=" and (claim.forDealer = :dealerId ) " ;
    		  params.put("dealerId", criteria.getServiceProvider());
    	  }
         
          HibernateCallback callBack = new QueryExecutionCallBack("distinct " + entity, query,
                  criteria, params);
          return new PartReturnWorkList((InboxItemList) getHibernateTemplate().execute(callBack));
    }


    private Object execute(final String query, final Map<String, Object> params) {
        return getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Executing [" + query + "] with params " + params);
                }
                return session.createQuery(query).setProperties(params).list();
            }

        });
    }

     private Object execute(final String query) {
        return getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(query).list();
            }

        });
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllTaskInstanceForParts(List<OEMPartReplaced> removedParts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("removedParts",removedParts);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_REMOVED_PARTS, params);
    }
    
    public Timer findTimerForTaskInstance(TaskInstance partReturnTaskInstance) { 
    	if(partReturnTaskInstance == null)
    		return null;
    	 Map<String, Object> params = new HashMap<String, Object>();
         params.put("taskInstance",partReturnTaskInstance);
          List<Timer> timerList = (List<Timer>)execute(PART_RETURN_TIMER_FOR_TASK_INSTANCE, params);
          if(!timerList.isEmpty())
          return timerList.get(0);
          else
          return null;
    }

    public Timer findTimerForRecoveryClaim(RecoveryClaim recoveryClaim) {
        if(recoveryClaim == null)
            return null;
        Map<String, Object> params = new HashMap<String, Object>();
        List<TaskInstance> instances = findInstancesForRecoveryClaim(recoveryClaim);
        if(instances.size() > 0){
            params.put("recoveryClaimIds",instances);
            List<Timer> timerList = (List<Timer>)execute("SELECT timer from Timer timer where taskInstance in (:recoveryClaimIds)", params);
            if(!timerList.isEmpty())
                return timerList.get(0);
            else
                return null;
            }
        return null;
    }

    private List<TaskInstance> findInstancesForRecoveryClaim(final RecoveryClaim recoveryClaim){
        return (List<TaskInstance>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "from TaskInstance where claim_id = :recoveryClaim and name_ in ('On Hold For Part Return', 'New', 'Disputed', 'Reopened Claims')")
                                .setParameter("recoveryClaim", recoveryClaim.getId())
                                .list();
                    }
                });
    }

    private String createWhereClauseBasedOnUser(WorkListCriteria criteria){
		
		if(AdminConstants.INTERNAL.equals(criteria.getUser().getUserType()) 
				|| AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType()))
		{
			return " and (task.actorId = :actorId) ";
		}
        else if(AdminConstants.SUPPLIER_USER.equals(criteria.getUser().getUserType())){
            return " and (task.actorId = :actorId) ";
        }
		else 
		{    if(isThirdPartyApplicable(criteria))
	        	return " and ( claim.forDealer = :dealerId or (claim.forDealer not in (:dealerIds) " +
				"    and claim.forDealer in (select tp from ThirdParty tp) and claim.filedBy in " +
				" (select users from Organization org join org.users users where org= :dealerId ) ) )";
	        else
	        	return " and (claim.forDealer = :dealerId )";			
		}		
	}
    
    private String createWhereClauseBasedOnWpraNumber(WorkListCriteria criteria){
    	return " and (wpra.wpraNumber = :wpraNumber) ";
    }
    
    private boolean isThirdPartyApplicable(WorkListCriteria criteria){
		return false;
	}
    
    private void setQueryParameterBasedOnUser(Map<String, Object> params,WorkListCriteria criteria) {
        if(AdminConstants.INTERNAL.equals(criteria.getUser().getUserType())) {
            params.put("actorId", criteria.getUser().getName());
        }
        else if( AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType())) {
            params.put("actorId", criteria.getUser().getBelongsToOrganization().getId().toString());
        }
        else {
            params.put("dealerId", criteria.getServiceProvider());
            if(isThirdPartyApplicable(criteria)){
                params.put("dealerIds", criteria.getServiceProviderList());
            }
        }
    }

    //Added for part shipper group by dealer location view

     public PartReturnWorkList getPartReturnWorkListByDealerLocation(WorkListCriteria criteria) {
    	return execute("serviceprovider", LOCATION_VIEW_FOR_PART_SHIPPER_QUERY, criteria);
    }

     public List<TaskInstance> findAllTasksForDealerLocation(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_DEALER_LOCATION_QUERY + PART_RETURN_DEALER_WHERE_CLAUSE , criteria);
     }
     
     public List<TaskInstance> findAllTasksForDealer(WorkListCriteria criteria) {
         return (List<TaskInstance>) execute(PREVIEW_DEALER_LOCATION_QUERY + PART_RETURN_DEALER_WHERE_CLAUSE, criteria);
     }
     
     
     public PartReturnWorkList getPartReturnWorkListByWpra(WorkListCriteria criteria) {
    	return execute("wpra", WPRA_VIEW_FOR_PROCESSOR_QUERY, criteria);
    }
     
     public PartReturnWorkList getPartReturnWorkListForWpraByActorId(WorkListCriteria criteria) {
     	return execute("wpra", WPRA_VIEW_FOR_PROCESSOR_QUERY_BY_ACTOR_ID, criteria);
     }
     
     public PartReturnWorkList getPartReturnWorkListForWpraByDealership(WorkListCriteria criteria) {
      	return execute("wpra", WPRA_VIEW_FOR_PROCESSOR_QUERY_BY_DEALERSHIP, criteria);
      }

     @SuppressWarnings("unchecked")
     public List<TaskInstance> findAllTasksForWPRA(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_WPRA_QUERY, criteria);
    }
     @SuppressWarnings("unchecked")
     public List<TaskInstance> printAllTasksForWPRA(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_WPRA_QUERY_PRINT, criteria);
    }
     
     @SuppressWarnings("unchecked")
     public List<TaskInstance> findAllTasksForWpraByActorId(WorkListCriteria criteria) {
        return (List<TaskInstance>) execute(PREVIEW_WPRA_QUERY_BY_ACTOR_ID, criteria);
    }    
     
     public List<TaskInstance>  findAllTasksForWpraByDealership(WorkListCriteria criteria){
    	 return (List<TaskInstance>) execute(PREVIEW_WPRA_QUERY_BY_DEALERSHIP, criteria);
     }
     
     public List<TaskInstance> findAllPrepareDuePartTaskInstanceForParts(List<OEMPartReplaced> removedParts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("removedParts",removedParts);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_PREPARE_DUE_PARTS, params);
    }

    public List<TaskInstance> findAllPrepareDuePartAndWpraTaskInstanceForParts(List<OEMPartReplaced> removedParts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("removedParts",removedParts);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_PREPARE_DUE_PARTS_AND_WPRA, params);
    }

    public List<TaskInstance> findAllWpraTasks(){
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_WPRA);
    }
    
    public List<TaskInstance> findAllWpraTasksBetweenGivenDate(CalendarDate startDate,CalendarDate endDate){
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate",startDate.toString("dd-MMM-yy"));	
        params.put("endDate",endDate.toString("dd-MMM-yy"));
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_WPRA_BETWEEN_DATE,params);
    }

     public List<TaskInstance> findAllPrepareDuePartsTasks(){
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_PREPAREDUEPART);
    }
     public List<TaskInstance> findAllPrepareDuePartsTasksBetweenGivenDate(CalendarDate startDate,CalendarDate endDate)
     {
    	 Map<String, Object> params = new HashMap<String, Object>();
         params.put("startDate",startDate.toString("dd-MMM-yy"));	
         params.put("endDate",endDate.toString("dd-MMM-yy"));
         return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_PREPAREDUEPART_BETWEEN_DATE,params); 
     }
     
     public PartReturnWorkList getPartReturnWorkListByWpraNumber(WorkListCriteria criteria, String wpraNumber){
    	 return execute("pr", WPRA_SEARCH_FOR_PROCESSOR_QUERY + createWhereClauseBasedOnWpraNumber(criteria), criteria, wpraNumber);
     }

    public TaskInstance findAllTasksForShipmentForPartReturn(PartReturn partReturn, String taskName){
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("partReturn",partReturn );
        params.put("taskName",taskName );

        return (TaskInstance)getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing [" + TASK_FOR_SUPPLIER_DEALER_PART_RETURN + "] with params "
                            + params);
                }
                return session.createQuery(TASK_FOR_SUPPLIER_DEALER_PART_RETURN).setProperties(params).uniqueResult();
            }

        });
    }

    public PartReturnWorkList getShipmentGeneratedWorkListByWpra(WorkListCriteria criteria) {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing [" + TASK_FOR_SHIPMENT_GENERATED_WPRA_VIEW + "] with params "
                    + criteria.getParamterizedFilterCriteria());
        }

        return execute("wpra", TASK_FOR_SHIPMENT_GENERATED_WPRA_VIEW + createWhereClauseBasedOnUser(criteria), criteria);
    }

    public List<TaskInstance> findShipmentGeneratedTasksForWPRA(WorkListCriteria criteria){

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("wpra",new Long(criteria.getIdentifier()) );
        params.put("taskName", criteria.getTaskName());
        return (List<TaskInstance>) execute(SHIPMENT_GENERATED_TASK_INSTANCE_FOR_WPRA, params);
    }

    public List<Shipment> findShipmentsForWPRA(String wpraId){
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("wpra",new Long(wpraId) );
        return (List<Shipment>) execute("select distinct(shipment) from PartReturn where wpra=(:wpra)", params);
    }

    public PartReturnWorkList getPartReturnWorkListByDealerLocationForPartShipper(WorkListCriteria criteria) {
        return execute("serviceprovider", LOCATION_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY, criteria);
    }


    public List<TaskInstance> findAllClaimedPartReceiptAndDealerPartShipped(List<OEMPartReplaced> receivedParts){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("receivedParts",receivedParts);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_CLAIMED_PART_RECEIPT_AND_DEALER_PART_SHIPPED, params);
    }

    public List<TaskInstance> findAllNotCollectedParts(int daysAllowed){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("daysAllowed",daysAllowed);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_NOT_COLLECTED_PARTS, params);
    }

    public List<TaskInstance> findAllRejectedPartsForDealer(OEMPartReplaced oemPart){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("oemPart",oemPart);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_OEM_PART_FOR_REJECTED_PARTS_INBOX_FOR_DEALER, params);
    }
    
    public List<TaskInstance> findAllRejectedPartsTasks(){
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskName",WorkflowConstants.REJETCTED_PARTS_INBOX);	
        return (List<TaskInstance>) execute(REJECTED_PARTS_QUERY,params); 
    }

    public PartReturnWorkList getCEVAWorkListByWpra(WorkListCriteria criteria) {
        return execute("wpra", TASK_FOR_SHIPMENT_GENERATED_WPRA_VIEW, criteria);
    }

    public List<TaskInstance> findAllPartTaskInstanceForParts(List<Long> partReturnsIds, String taskName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("partReturnsIds",partReturnsIds);
        params.put("taskName" , taskName);
        return (List<TaskInstance>) execute(TASK_INSTANCE_FOR_PARTS, params);
    }
    
    private PartReturnWorkList getPartReturnWorkListByTaskInstance(WorkListCriteria criteria){
    	 Map<String, Object> params = criteria.getParameterMap();
	        params.put("actorId", criteria.getUser().getName());
	        params.put("taskName", criteria.getTaskName());
	        String queryForExec= null;
	        if(criteria.getUser().hasRole("masterSupplier"))
	        	queryForExec = MASTER_SUPPLIER_CONFIRM_DEALER_PARTRETURNS_VIEW_QUERY;
	        else
	        	queryForExec = SUPPLIER_CONFIRM_DEALER_PARTRETURNS_VIEW_QUERY;
	        HibernateCallback callBack = new QueryExecutionCallBack("distinct taskInstance",
	        		queryForExec, criteria, params) {
	            @Override
	            protected List transformResults(List list) {
	                    List results = new ArrayList(list.size());
	                    Map<String, TaskInstance> data = new HashMap<String, TaskInstance>();
	                    for (int i = 0; i < list.size(); i++) {
	                        Object[] resultArray = (Object[]) list.get(i);
	                        CustomTaskInstance customTaskInstance = (CustomTaskInstance) resultArray[0];
	                        PartReturn pr = (PartReturn) customTaskInstance.getVariable("partReturn");
	                        String shipmentNo = pr.getShipment().getTransientId();
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
	        return new PartReturnWorkList((InboxItemList) getHibernateTemplate().execute(callBack));
    }
}

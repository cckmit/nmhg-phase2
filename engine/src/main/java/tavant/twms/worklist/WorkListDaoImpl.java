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
package tavant.twms.worklist;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.partreturn.PartReturnQueries;
import tavant.twms.worklist.supplier.SupplierRecoveryQueries;

public class WorkListDaoImpl extends BaseWorkListDaoImpl implements WorkListDao{

	private static final Logger logger = Logger.getLogger(WorkListDaoImpl.class);

	private static final String COUNT_PART_TASK_FOR_RECEIPT_QUERY = "countPartTasksForReceipt";
	
	private static final String COUNT_PART_TASK_FOR_INSPECTION_QUERY = "countPartTasksForInspection";

    private static final String COUNT_TASKNAME_QUERY = "countTasksByTaskName";
	
	private static final String COUNT_PART_SHIPPER_SHIPMENTS_QUERY = "countPartShipperShipmentsQuery";
	
	private static final String COUNT_SRA_PARTS_RECOVERY_RECOVERY_CLAIMS_QUERY = "countSRAPartsRecoveryRecoveryClaimsQuery";
	
	private static final String COUNT_SRA_PARTS_RECOVERY_RECOVERY_CLAIMS_QUERY_FOR_MASTER_SUPPLIER = "countSRAPartsRecoveryRecoveryClaimsQueryForMasterSupplier";
	
	private static final String COUNT_SUPPLIER_SHIPMENTS_QUERY = "countSupplierShipmentsQuery";
	
	private static final String COUNT_ALL_SUPPLIERS_SHIPMENTS_QUERY = "countAllSuppliersShipmentsQuery";
	
	private static final String CURRENT_REC_CLAIM_ASSIGNEE = "currentAssigneeForRecClaim";


	private static final String FROM_CLAUSE = "from TaskInstance ti,"
			+ "Claim claim ";

	private static final String WHERE_CLAUSE = "where ti.isOpen = true and "
			+ "ti.claimId = claim.id and "
			+ "ti.task.name= :taskName and ";



	/**
	 * Method to retrieve the List of TaskInstances and the Total count of the
	 * task instances for a particular user.
	 * 
	 * @param criteria
	 * @return WorkList - The worklist object with the list of task instances
	 *         and the count.
	 */
	public WorkList getWorkList(WorkListCriteria criteria) {
		Assert.state((criteria.getTaskName() != null),
				"Task name cannot be null");
		String countQuery = createDynamicCountQuery(criteria);
		String workListQuery = createDynamicWorkListQuery(criteria);
		return execute(criteria, workListQuery, countQuery);
	}
	
	 /**
     * Perf fix: Added logic to put third party clause only for Hussmann BU.
     */
	
	private boolean isThirdPartyApplicable(WorkListCriteria criteria){
		return false;
	}
    
	/**
	 * Returns the tasks and the count for that particular task for a given
	 * actor.
	 * 
	 * @param criteria
	 * @return Map<Task, Long> - The task along with count.
	 */	
	public Map<Task, Long> getAllTasks(final WorkListCriteria criteria,User loggedInUser) {

        Map<Task, Long> taskTypesWithCount = new LinkedHashMap<Task, Long>();
		List<Task> taskTypes = getTasks(criteria);
        if (taskTypes.size() == 0) {
            return taskTypesWithCount;
        }
        if ("ClaimSubmission".equals(criteria.getProcess())) {
            if(criteria.getDisplayNewClaimToAllProcessors()){
                for(Task task : taskTypes){
                    if(task.getName().equals(WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME)){
                        taskTypes.remove(task);
                        List<Task> newInboxTasks = new ArrayList<Task>();
                        newInboxTasks.add(task);
                        criteria.setUserGroup(findAvailableUsersBelongingToRole("processor",loggedInUser));
                        getCount(formClaimTasksCountQuery(criteria), criteria, newInboxTasks, taskTypesWithCount, true);
                        break;
                    }
                }
                criteria.setUserGroup(null);
            }

            for(Task task : taskTypes){
                if(task.getName().equals(WorkflowConstants.REJECTED_PART_RETURN)){
                    taskTypes.remove(task);
                    List<Task> newInboxTasks = new ArrayList<Task>();
                    newInboxTasks.add(task);
                    criteria.setUserGroup(findAvailableUsersBelongingToRole("processor",loggedInUser));
                    getCount(formClaimTasksCountQuery(criteria), criteria, newInboxTasks, taskTypesWithCount, true);
                    break;
                }
            }
            criteria.setUserGroup(null);
            getCount(formClaimTasksCountQuery(criteria), criteria, taskTypes, taskTypesWithCount, true);
        } else if ("PartsReturn".equals(criteria.getProcess()) && (criteria.getUser().hasRole("dealer"))) {
        	List<String> taskNames = new ArrayList<String>();
            taskNames.add(WorkflowConstants.DUE_PARTS_TASK);
            taskNames.add(PartReturnStatus.SHIPMENT_GENERATED.getStatus());
            taskNames.add(WorkflowConstants.PARTS_SHIPPED);
            taskNames.add(WorkflowConstants.OVERDUE_PARTS_TASK);
            taskNames.add(WorkflowConstants.REJETCTED_PARTS_INBOX);
            taskNames.add(WorkflowConstants.PREPARE_DUE_PARTS);
            taskNames.add(WorkflowConstants.THIRD_PARTY_DUE_PARTS_TASK);
            List<Task> tasks = new ArrayList<Task>();
            for (Task task : taskTypes) {
                if (taskNames.contains(task.getName())) {
                    tasks.add(task);
                }
            }
            getCount(formPartReturnTasksCountQuery(criteria), criteria, tasks, taskTypesWithCount,false);
            for (Task task : taskTypes) {
                if (!taskNames.contains(task.getName())) {
                    Long taskCount = getPartReturnCount(criteria, task.getName());
                    taskTypesWithCount.put(task, taskCount);
                }
            }
        } else if ("PartsReturn".equals(criteria.getProcess()) || "SupplierPartReturn".equals(criteria.getProcess())){
        	 for (Task task : taskTypes) {
                 Long taskCount = getPartReturnCount(criteria, task.getName());
                 taskTypesWithCount.put(task, taskCount);
             }
        } else if ("SupplierRecovery".equals(criteria.getProcess())) {
        	for(Task task : taskTypes) {
        		Long taskCount = getRecoveryClaimTaskCount(criteria, task.getName());
                taskTypesWithCount.put(task, taskCount);
        	}
        }
        else {
        	getCount(criteria, taskTypes,taskTypesWithCount);
        }
        
        return taskTypesWithCount;
	}

    @SuppressWarnings("unchecked")
	public List<String> getPartReturnInboxOrders()
    {
    	return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery("select prio.inboxName from PartReturnInboxOrder prio order by prio.priority");			
						return query.list();
					}
				});	
    }
	private void getCount(final String queryStr, final WorkListCriteria criteria,
            final List<Task> tasks, final Map<Task, Long> taskTypesWithCount, final boolean isSQLQuery) {
		getHibernateTemplate().execute(new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                Query query = null;
                if(isSQLQuery){
                    query = session.createSQLQuery(queryStr);
                }else{
                    query = session.createQuery(queryStr);
                }
										
                query.setParameterList("taskNames", getTaskNames(tasks));  
               
                setQueryParameterBasedOnUser(query,criteria,isSQLQuery);                
				List<Object[]> result = (List<Object[]>) query.list();
                for (Task task : tasks) {
                    boolean countPresent = false;
                    for (Object[] objects : result) {
                        if (objects[1].equals(task.getName())) {
                            if(objects[0] instanceof Long) 
                                taskTypesWithCount.put(task, (Long) objects[0]);
                            else
                                taskTypesWithCount.put(task, ((BigDecimal) objects[0]).longValue());
                            countPresent = true;
                            break;
                        }
                    }
                    if (!countPresent) {
                        taskTypesWithCount.put(task, 0L);
                    }
                }
                return null;
            }
		});
	}


    private Long getPartReturnCount(final WorkListCriteria criteria, final String taskName) {
		return (Long) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = null;
				if (taskName.equals(WorkflowConstants.DUE_PARTS_RECEIPT)) {
					query = session.getNamedQuery(COUNT_PART_TASK_FOR_RECEIPT_QUERY);
				} else if (taskName.equals(WorkflowConstants.DUE_PARTS_INSPECTION)) {
					query = session.getNamedQuery(COUNT_PART_TASK_FOR_INSPECTION_QUERY);
				} else if (taskName.equals(WorkflowConstants.SUPPLIER_PARTS_CLAIMED)) {
                    query = session.createQuery(getCountForSupplierPartsClaimed());
                }
                else if (taskName.equals(WorkflowConstants.SUPPLIER_PARTS_SHIPPED)
						|| taskName.equals(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED)) {
					query = session.getNamedQuery(COUNT_PART_SHIPPER_SHIPMENTS_QUERY);
				} else if (taskName.equals(WorkflowConstants.PART_NOT_IN_WAREHOUSE)
						|| taskName.equals(WorkflowConstants.AWAITING_SHIPMENT_TO_WAREHOUSE)) {
					query = session.getNamedQuery(COUNT_SRA_PARTS_RECOVERY_RECOVERY_CLAIMS_QUERY);
				} else if(taskName.equals(WorkflowConstants.PART_FOR_RETURN_TO_NMHG)
                        || taskName.equals(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)
                        || taskName.equals(WorkflowConstants.PARTS_SHIPPED_TO_NMHG)) {
					if(criteria.getUser().hasRole("masterSupplier")) {
						query = session.getNamedQuery(COUNT_SRA_PARTS_RECOVERY_RECOVERY_CLAIMS_QUERY_FOR_MASTER_SUPPLIER);
					} else {
						query = session.getNamedQuery(COUNT_SRA_PARTS_RECOVERY_RECOVERY_CLAIMS_QUERY);
					}//fix for NMHGSLMS-1020
				} else if (taskName.equals(WorkflowConstants.CONFIRM_PART_RETURNS)) {
					if(criteria.getUser().hasRole("masterSupplier")) {
						query = session.getNamedQuery(COUNT_ALL_SUPPLIERS_SHIPMENTS_QUERY);
					} else {
						query = session.getNamedQuery(COUNT_SUPPLIER_SHIPMENTS_QUERY);
					}					
				} else if(taskName.equals(WorkflowConstants.CONFIRM_DEALER_PART_RETURNS)){
					if(criteria.getUser().hasRole("masterSupplier")) {
						query = session.createQuery(getAllSupplierDealerPartReturn());
					} else {
						query = session.createQuery(getSupplierDealerPartReturn());
					}
                }
                else if(taskName.equals(WorkflowConstants.CEVA_TRACKING)){
                    query = session.createQuery(formCevaTrackingCountQuery());
                    query.setString("taskName", taskName);
                    return (query.uniqueResult());
                }
                else if(taskName.equals(WorkflowConstants.GENERATED_WPRA)){
                    query = session.createQuery(formWPRAGeneratedCountQuery());
                    query.setString("taskName", taskName);
                    return (query.uniqueResult());
                }
                else if(taskName.equals(WorkflowConstants.CLAIMED_PARTS_RECEIPT))  {
                    query = session.createQuery(countBasedOnShipment());
            		query.setLong("belongsToOrg", criteria.getServiceProvider().getId());
            		query.setString("taskName", taskName);
                    return (query.uniqueResult());
                }
                else if(taskName.equals(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) ||  taskName.equals(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED)){
                    query = session.createQuery(countBasedOnShipmentForPartShipper());
                }
                else if(taskName.equals(WorkflowConstants.DEALER_REQUESTED_PART))  {
                    query = session.createQuery(countDealerRequestedPArtsBasedOnClaim());
                }
                else if(taskName.equals(WorkflowConstants.WPRA_TO_BE_GENERATED)){
                    query = session.createQuery(countRequiredPartsFromDealerCountOnClaim());
                    query.setString("taskName", taskName);
                    return (query.uniqueResult());
                }
                else if(taskName.equals(WorkflowConstants.SUPPLIER_PARTS_RECEIPT)){
                    query = session.createQuery(getCountForSupplierPartsReceipt());
                }
                else if(taskName.equals(WorkflowConstants.SUPPLIER_PARTS_INSPECTION)){
                    query = session.createQuery(getCountForSupplierPartsInspect());
                }else if(taskName.equals(WorkflowConstants.ROUTED_TO_NMHG)){
                	query = session.createQuery(getRoutedPartReturnCount());
                	query.setString("taskName", taskName);
                    return (query.uniqueResult());
                } else if(taskName.equals(WorkflowConstants.AWAITING_SHIPMENT)) {
                	query = session.createQuery(getCountForAwaitingShipment());
                	query.setString("taskName", taskName);
                	query.setString("actorId", criteria.getUser().getName());
                }
                else {
					query = session.getNamedQuery(COUNT_TASKNAME_QUERY);
					query.setParameter("processDefinition", criteria.getProcess());
				}
				if(!(taskName.equals(WorkflowConstants.CONFIRM_DEALER_PART_RETURNS)
						|| taskName.equals(WorkflowConstants.PART_FOR_RETURN_TO_NMHG)
                        || taskName.equals(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)
                        || taskName.equals(WorkflowConstants.PARTS_SHIPPED_TO_NMHG)
                        || taskName.equals(WorkflowConstants.CONFIRM_PART_RETURNS))
                        || !criteria.getUser().hasRole("masterSupplier")) {
					query.setString("actorId", criteria.getUser().getName());
				}
				query.setString("taskName", taskName);
				return (query.uniqueResult());
			}
		});
	}
    
    private String getCountForAwaitingShipment() {
    	return "select count(distinct recoveryClaim)" + SupplierRecoveryQueries.REC_CLAIM_TASK_INSTANCE_ASSIGNED_TO;
    }

    private String getCountForSupplierPartsReceipt(){
        return "select COUNT(distinct taskInstance.claimId)" + SupplierRecoveryQueries.PART_RECEIVER_RECOVERY_VIEW_QUERY;
    }

    private String getCountForSupplierPartsInspect(){
        return "select COUNT(distinct taskInstance.claimId)" + SupplierRecoveryQueries.PART_INSPECTOR_RECOVERY_VIEW_QUERY;
    }

    private String getCountForSupplierPartsClaimed(){
        return "select COUNT(distinct recoveryClaim)" + SupplierRecoveryQueries.PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY;
    }

    private String countRequiredPartsFromDealerCountOnClaim(){

        return "select COUNT(distinct claim)" + PartReturnQueries.CLAIM_VIEW_QUERY_COMMON;
    }

    private String countDealerRequestedPArtsBasedOnClaim(){

        return "select COUNT(distinct claim)" + PartReturnQueries.CLAIM_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY;
    }

    private String countBasedOnShipmentForPartShipper(){

        return "select COUNT(distinct shipment.id)" + PartReturnQueries.SHIPMENT_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY;
    }

    private String countBasedOnShipment(){

        return "select COUNT(distinct s.id) from TaskInstance ti, Shipment s, PartReturn bs where ti.isOpen = true and ti.partReturnId = bs.id " +
                "and ti.task.name= :taskName " +
                "and ti.actorId in(select user.name from User user  join user.belongsToOrganizations as org where org.id = :belongsToOrg) and bs.shipment = s.id";
    }

    private String formWPRAGeneratedCountQuery(){

        return "select count( distinct wpra) from TaskInstance task, Wpra wpra, BasePartReturn pr, Claim claim where task.isOpen = true and task.task.name = :taskName and  pr.wpra= wpra.id and task.partReturnId = pr.id and task.claimId = claim.id";
    }

    private String formCevaTrackingCountQuery(){

        return "select COUNT(distinct w.id) from TaskInstance ti, Wpra w, PartReturn bs where ti.isOpen = true and ti.partReturnId = bs.id " +
                "and ti.task.name= :taskName " +
                "and w.id = bs.wpra";
    }

    public String getSupplierDealerPartReturn(){
        return getAllSupplierDealerPartReturn() + " and ti.actorId = :actorId ";
    }
    
    public String getAllSupplierDealerPartReturn(){
        return "select COUNT(distinct s.id) from TaskInstance ti, Shipment s, PartReturn bs, Claim c where ti.isOpen = true and ti.partReturnId = bs.id " +
                "and ti.task.name= :taskName " +
                "and s.id = bs.shipment and ti.claimId = c.id";
    }
    
    public String getRoutedPartReturnCount(){
    	return "select count(distinct recoveryClaim.id) " + SupplierRecoveryQueries.ROUTED_TO_NMHG_QUERY;
    }

	@SuppressWarnings("unchecked")
	protected WorkList execute(final WorkListCriteria criteria,
			final String workListQuery, final String countQuery) {
		return (WorkList) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						WorkList workList = new WorkList();
						Query wQuery = session.createQuery(workListQuery);
						setQueryParameterBasedOnUser(wQuery, criteria,false);								
						wQuery.setString("taskName", criteria.getTaskName())
								.setFirstResult(
										criteria.getPageSpecification()
												.offSet()).setMaxResults(
										criteria.getPageSpecification()
												.getPageSize());
						// Now the filter clause will be added to the criteria.
						// All filter params will be bind values
						wQuery.setProperties(criteria.getParameterMap());
						Query cQuery = session.createQuery(countQuery)							
								.setString("taskName", criteria.getTaskName());
						setQueryParameterBasedOnUser(cQuery, criteria,false);			
						// Now the filter clause will be added to the criteria.
						// All filter params will be bind values
						cQuery.setProperties(criteria.getParameterMap());
						workList.setTaskList(wQuery.list());	
						workList
								.setTaskListCount(((Long) cQuery.uniqueResult())
										.intValue());
						return workList;
					}
				});
	}

	private String createDynamicCountQuery(WorkListCriteria criteria) {
		StringBuffer countDynamicQuery = new StringBuffer();
		countDynamicQuery.append("select count(ti) ");
		countDynamicQuery.append(FROM_CLAUSE);
        countDynamicQuery.append(WHERE_CLAUSE).append(createWhereClauseBasedOnUser(criteria));
		addFilterCriteria(criteria, countDynamicQuery);
		return countDynamicQuery.toString();
	}
	
	private String createWhereClauseBasedOnUser(WorkListCriteria criteria){
		
		if(AdminConstants.INTERNAL.equals(criteria.getUser().getUserType())
                || AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType()))
		{
            if(criteria.getUserGroup() != null && criteria.getUserGroup().size() > 0){
                return " ti.actorId in (:actorId) ";
            }
			return " (ti.actorId = :actorId) ";
		}
		else 
		{   
			if(isThirdPartyApplicable(criteria))
	        	return " ( claim.forDealer = :dealerId or (claim.forDealer not in (:dealerIds) " +
				"    and claim.forDealer in (select tp from ThirdParty tp) and claim.filedBy in " +
				" (select users from Organization org join org.users users where org= :dealerId ) ) )";
	        else
	        		return "(claim.forDealer = :dealerId )";
		}			
	}

    private List<String> getUserNames(List<User> users){
        List<String> usersNames = new ArrayList<String>();
        for(User usr : users){
           usersNames.add(usr.getName());
        }
        return usersNames;
    }
	
	private void setQueryParameterBasedOnUser(Query query,WorkListCriteria criteria, boolean isSQLQuery){
		if(AdminConstants.INTERNAL.equals(criteria.getUser().getUserType()))
		{
            if(criteria.getUserGroup() != null && criteria.getUserGroup().size() >0){
                query.setParameterList("actorId", getUserNames(criteria.getUserGroup()));
            }else{
			    query.setString("actorId", criteria.getUser().getName());
            }
		}
        else if(AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType()))
        		{
        			query.setString("actorId", criteria.getUser().getBelongsToOrganization().getId().toString());
        		}
		else 
		{   
                    if(!isSQLQuery){
                        query.setParameter("dealerId", criteria.getServiceProvider());
                        if(isThirdPartyApplicable(criteria)){
                            query.setParameterList("dealerIds", criteria.getServiceProviderList());
                        }
                    }else{
                        query.setParameter("dealerId", criteria.getServiceProvider().getId());
                        if(isThirdPartyApplicable(criteria)){
                            List<Long> ids = new ArrayList<Long>(criteria.getServiceProviderList().size());
                            for (Organization org : criteria.getServiceProviderList()) {
                                ids.add(org.getId());
                            }
                            query.setParameterList("dealerIds", ids);
                        }
                    }
    	        	}
                if(isSQLQuery){
                    List<String> l = new ArrayList<String>();
                    for(BusinessUnit bu : criteria.getUser().getBusinessUnits()){
                        l.add(bu.getName());
                    }
                    query.setParameterList("buNames", l);
                }
	}

	private String createDynamicWorkListQuery(WorkListCriteria criteria) {
		StringBuffer workListDynamicQuery = new StringBuffer();
		// Temporarily removed distinct::Oracle doesn't support a distinct query
		// with an order by clause until
		// order by column is added in the selected columns list.Moreover,we are
		// not sure why distinct
		// is needed here.Vineeth V is looking into it.
		workListDynamicQuery.append("select ti ");
		workListDynamicQuery.append(FROM_CLAUSE);
		workListDynamicQuery.append(WHERE_CLAUSE).append(createWhereClauseBasedOnUser(criteria));
		addFilterCriteria(criteria, workListDynamicQuery);
		addSortCriteria(criteria, workListDynamicQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("The dynamic work list query created is: "
					+ workListDynamicQuery.toString());
		}
		return workListDynamicQuery.toString();
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> getNotShippedPartReturnTaskinstancesForClaim(final Long claimId) {
		final StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT taskInstance FROM TaskInstance taskInstance, Claim claim ");
		queryString.append("  where taskInstance.isOpen = true ");
		queryString.append("  and taskInstance.claimId = claim.id ");
		queryString.append("  and taskInstance.name in ('Due Parts', 'Overdue Parts', 'Shipment Generated', 'Third Party Due Parts', 'CEVA Tracking', 'Required Parts From Dealer', 'Prepare Due Parts', 'WPRA Generated For Parts') ");
		queryString.append("  and claim.id = :claimId ");
		return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(queryString.toString()).setParameter("claimId", claimId).list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> getAllFocClaimTaskinstancesToBeSubmitted(String duePeriodConstraint) {
		final StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT taskInstance ");
		queryString.append(" FROM TaskInstance taskInstance, Claim claim ");
		queryString.append(" where taskInstance.isOpen = true  ");
		queryString.append(" and taskInstance.name='Waiting For Labor'");	
		queryString.append(" and taskInstance.claimId = claim.id ");
		queryString.append(" and claim.foc = :trueVar ");
		queryString.append(" and claim.activeClaimAudit.state = 'DRAFT' and ( ");
		queryString.append(duePeriodConstraint);
		queryString.append("  ) ");
		return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(queryString.toString())
				.setParameter("trueVar", true).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public User getCurrentAssigneeForRecClaim(final Long recClaimId) {
		List<User> claimAssignees = (List<User>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.getNamedQuery(CURRENT_REC_CLAIM_ASSIGNEE);
						query.setParameter("recClaimId", recClaimId);
						return query.list();
					}
				});
		return claimAssignees == null || claimAssignees.isEmpty() ? null : claimAssignees.get(0);
	}
	
	 @SuppressWarnings("unchecked")
		public List<TaskInstance> getAllOpenTasksForClaim(final Claim claim) {
			final StringBuffer queryString = new StringBuffer();
			queryString.append(" SELECT taskInstance ");
			queryString.append(" FROM TaskInstance taskInstance, Claim claim ");
			queryString.append(" where taskInstance.isOpen = true  ");
			queryString.append(" and taskInstance.claimId = claim.id and claim.id = :claimId");
			return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session.createQuery(queryString.toString())
					.setParameter("claimId", claim.getId()).list();
				}
			});
		}

        /**
         * This API appends the thirdPartyClause to claim count query if needed
         * @param //isThirdPartyApplicable
         * @return
         */
	    private String formClaimTasksCountQuery(WorkListCriteria criteria){
	    	StringBuffer COUNT_CLAIM_TASKNAMES_QUERY = new StringBuffer("SELECT COUNT(ti.id_) AS col_0_0_, task.name_ AS col_1_0_ "
                        + "FROM jbpm_taskinstance ti, jbpm_task task,"
                        + "claim claim WHERE "
                        + "claim.business_unit_info IN (:buNames) "
                        + "AND ti.task_ =task.id_ "
                        + "AND ti.isopen_ = 1 "
                        + "AND task.name_ IN (:taskNames) "
                        + "AND ti.claim_Id = claim.id AND").append(createWhereSQLClauseBasedOnUser(criteria)).append("group by task.name_");
	    	return COUNT_CLAIM_TASKNAMES_QUERY.toString();
	    }
	    
	    private String getRecoveryClaimTaskCountQuery() {
	    	StringBuffer query = new StringBuffer("select count(distinct recClaim) " +
	    			"from TaskInstance taskInstance, RecoveryClaim recClaim " +
	    			"where taskInstance.task.name = (:taskName) " +
	    			"and taskInstance.isOpen = true " +
	    			"and recClaim.id = taskInstance.claimId");// +
	    			//"and taskInstance.actorId = :actorId");
	    	return query.toString();
	    }
	    
	    private String getUserRecClaimTaskCountQuery() {
	    	StringBuffer query = new StringBuffer("select count(distinct recClaim) " +
	    			"from TaskInstance taskInstance, RecoveryClaim recClaim " +
	    			"where taskInstance.task.name = (:taskName) " +
	    			"and taskInstance.isOpen = true " +
	    			"and recClaim.id = taskInstance.claimId " +
	    			"and taskInstance.actorId = :actorId");
	    	return query.toString();
	    }
	    
	    private Long getRecoveryClaimTaskCount(final WorkListCriteria criteria, final String taskName) {
	    	return (Long) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = null;
					if(criteria.getUser().hasRole("masterSupplier") || criteria.getUser().isInternalUser()) {
						query = session.createQuery(getRecoveryClaimTaskCountQuery());
					} else {
						query = session.createQuery(getUserRecClaimTaskCountQuery());
						query.setString("actorId", criteria.getUser().getName());
					}
					query.setString("taskName", taskName);
					return (query.uniqueResult());
				}
			});
	    }
	    
     private String createWhereSQLClauseBasedOnUser(WorkListCriteria criteria){
        if(AdminConstants.INTERNAL.equals(criteria.getUser().getUserType())
                        || AdminConstants.ENTERPRISE_USER.equals(criteria.getUser().getUserType()))
		{
            if(criteria.getUserGroup() != null && criteria.getUserGroup().size() >0){
                return " ti.actorId_ in (:actorId) ";
            }else{
                return " (ti.actorId_ = :actorId) ";
            }

		}
		else 
		{    if(isThirdPartyApplicable(criteria))
	        	return " ( claim.for_Dealer = :dealerId "
                                + " or (claim.for_Dealer not in (:dealerIds) )" 
                                + " and ( claim.for_Dealer in ("
                                + "select tp.id from Third_Party tp "
                                + "inner join service_provider sp on tp.id = sp.id "
                                + "inner join organization o on tp.id = o.id "
                                + "inner join party p on tp.id = p.id where p.d_active = 1) )"
                                + "and ( claim.filed_By in " +
				" (select ou.id from Organization org "
                                + "inner join party p on org.id = p.id "
                                + "inner join org_user_belongs_to_orgs oubto on org.id = oubto.belongs_to_organizations "
                                + "inner join org_user ou on oubto.org_user = ou.id "
                                + "where org.id = :dealerId and p.d_active = 1) ) )";
	        else
		        	return "(claim.for_Dealer = :dealerId )";			
	        	}
		}		
    /**
         * This API appends the thirdPartyClause to part return count query if needed
         * @param //isThirdPartyApplicable
         * @return
         */
	    private String formPartReturnTasksCountQuery(WorkListCriteria criteria){

	    StringBuffer COUNT_PARTRETURN_TASKNAMES_QUERY = new StringBuffer("SELECT COUNT(distinct claim), ti.task.name" +
	         " FROM TaskInstance ti , Claim claim "+
	         " WHERE ti.isOpen = true"+
	         " AND ti.task.name in (:taskNames)"+
	         " AND ti.claimId = claim.id and ").append(createWhereClauseBasedOnUser(criteria)).append("group by ti.task.name");

	    return COUNT_PARTRETURN_TASKNAMES_QUERY.toString();
	    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
		public List<TaskInstance> getAllOpenTasks(final TaskCriteria criteria) {
			final String queryString = getAllOpenTasksQuery(criteria);
			return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					Query query = session.createQuery(queryString);
					for(String taskName : criteria.getParams().keySet()){
						query.setParameterList(taskName, criteria.getParams().get(taskName));
					}
					return query.list();
				}
			});
		}

		private String getAllOpenTasksQuery(TaskCriteria criteria) {
			StringBuilder queryString = new StringBuilder();
			queryString.append(" SELECT taskInstance ");
			queryString.append(" FROM TaskInstance taskInstance, Claim claim ");
			queryString.append(" where taskInstance.isOpen = true  ");
			queryString.append(" and taskInstance.claimId = claim.id ");
			if(criteria.getParams().containsKey("taskNames")){
				queryString.append(" and taskInstance.name in (:taskNames) ");
			}
			if(criteria.getParams().containsKey("inStates")){
				queryString.append(" and claim.activeClaimAudit.state in (:inStates)  ");
			} else if(criteria.getParams().containsKey("notInStates")){
				queryString.append(" and claim.activeClaimAudit.state not in (:notInStates)  ");
			}
			queryString.append(" and ( ");
			queryString.append(getBuConfigDaysFilterCriteria(criteria.getDaysBUMapConsideredForDenying(), criteria.getBuWiseFilterColumns()));
			queryString.append("  ) ");
			return queryString.toString();
		}

		private String getBuConfigDaysFilterCriteria(Map<String, List<Object>> daysBUMapConsideredForDenying, Map<String, String> buWiseFilterColumns){
			boolean isNotFirstElement = Boolean.FALSE;
			StringBuilder buConfigValueConcatString = new StringBuilder();			
			for (String key : daysBUMapConsideredForDenying.keySet()) {
				if (isNotFirstElement) {
					buConfigValueConcatString.append(" or ");
				}
				buConfigValueConcatString
				.append(" (claim.businessUnitInfo = '");
				buConfigValueConcatString.append(key);
				buConfigValueConcatString.append("' and ");
				if(buWiseFilterColumns.containsKey("ALL")){
					buConfigValueConcatString.append(buWiseFilterColumns.get("ALL"));
				} else {
					buConfigValueConcatString.append(buWiseFilterColumns.get(key));
				}
				buConfigValueConcatString.append(" + (");
				buConfigValueConcatString.append(daysBUMapConsideredForDenying
						.get(key).get(0));
				buConfigValueConcatString.append(") <= sysdate) ");
				isNotFirstElement = true;
			}
			return buConfigValueConcatString.toString();
		}
		private String getAllOpenTasksQueryForEmail(TaskCriteria criteria,int daysForEmailTriggering) {
			StringBuilder queryString = new StringBuilder();
			queryString.append(" SELECT taskInstance ");
			queryString.append(" FROM TaskInstance taskInstance, Claim claim ");
			queryString.append(" where taskInstance.isOpen = true  ");
			queryString.append(" and taskInstance.claimId = claim.id ");
			if(criteria.getParams().containsKey("taskNames")){
				queryString.append(" and taskInstance.name in (:taskNames) ");
			}
			if(criteria.getParams().containsKey("inStates")){
				queryString.append(" and claim.activeClaimAudit.state in (:inStates)  ");
			} else if(criteria.getParams().containsKey("notInStates")){
				queryString.append(" and claim.activeClaimAudit.state not in (:notInStates)  ");
			}
			queryString.append(" and ( ");
			queryString.append(getBuConfigDaysFilterCriteriaForEmail(criteria.getPendingOverDueDaysBUMapForEmail(), criteria.getBuWiseFilterColumns()));
			queryString.append("  ) ");
			return queryString.toString();
		}
		
		
		
		private String getBuConfigDaysFilterCriteriaForEmail(Map<String, List<Object>> daysBUMapConsideredForEmail, Map<String, String> buWiseFilterColumns){
			boolean isNotFirstElement = Boolean.FALSE;
			StringBuilder buConfigValueConcatString = new StringBuilder();			
			for (String key : daysBUMapConsideredForEmail.keySet()) {
				if (isNotFirstElement) {
					buConfigValueConcatString.append(" or ");
				}
				buConfigValueConcatString
				.append(" claim.businessUnitInfo = '");
				buConfigValueConcatString.append(key);
				buConfigValueConcatString.append("' and ");
			if (buWiseFilterColumns.containsKey("ALL")) {
				buConfigValueConcatString.append("TRUNC("
						+ buWiseFilterColumns.get("ALL"));
			} else {
				buConfigValueConcatString.append("TRUNC("
						+ buWiseFilterColumns.get(key));
			}
			buConfigValueConcatString.append(") + (");
			buConfigValueConcatString.append(daysBUMapConsideredForEmail
						.get(key).get(0));
				buConfigValueConcatString.append(") = TRUNC(sysdate)) ");
				isNotFirstElement = true;
			}
			return buConfigValueConcatString.toString();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public List<TaskInstance> getAllTasksForEmailTriggerring(
				final TaskCriteria criteria,int daysForEmailTriggering) {
			final String queryString = getAllOpenTasksQueryForEmail(criteria,daysForEmailTriggering);
			return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					Query query = session.createQuery(queryString);
					for(String taskName : criteria.getParams().keySet()){
						query.setParameterList(taskName, criteria.getParams().get(taskName));
					}
					return query.list();
				}
			});
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public List<TaskInstance> getAllRecoveryClaimTasksForEmailTriggerring(
				final TaskCriteria criteria,final int daysForEmailTriggering) {
			final String queryString = getAllOpenRecoveryClaimTasksQueryForEmail(criteria,daysForEmailTriggering);
			return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					Query query = session.createQuery(queryString);
					for(String taskName : criteria.getParams().keySet()){
						query.setParameterList(taskName, criteria.getParams().get(taskName));
					}
					return query.list();
				}
			});
		}

    private String getAllOpenRecoveryClaimTasksQueryForEmail(
				TaskCriteria criteria, int daysForEmailTriggering) {
			StringBuilder queryString = new StringBuilder();
			queryString.append(" SELECT taskInstance ");
			queryString.append(" FROM TaskInstance taskInstance, RecoveryClaim claim ");
			queryString.append(" where taskInstance.isOpen = true  ");
			queryString.append(" and taskInstance.claimId = claim.id ");
			if(criteria.getParams().containsKey("taskNames")){
				queryString.append(" and taskInstance.name in (:taskNames) ");
			}
			if(criteria.getParams().containsKey("inStates")){
				queryString.append(" and claim.recoveryClaimState in (:inStates)  ");
			} else if(criteria.getParams().containsKey("notInStates")){
				queryString.append(" and claim.recoveryClaimState not in (:notInStates)  ");
			}
			queryString.append(" and ( ");
			queryString.append(getBuConfigDaysFilterCriteriaForEmail(criteria.getPendingOverDueDaysBUMapForEmail(), criteria.getBuWiseFilterColumns()));
			queryString.append("  ) ");
			return queryString.toString();
		}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<TaskInstance> getAllFinalResponseRecoveryClaimTasksForEmailTriggerring(
			final TaskCriteria criteria, final int daysForEmailTriggering){
    	final String queryString = getAllOpenFinalResponseRecoveryClaimTasksQueryForEmail(criteria,daysForEmailTriggering);
		return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for(String taskName : criteria.getParams().keySet()){
					query.setParameterList(taskName, criteria.getParams().get(taskName));
				}
				return query.list();
			}
		});
    }

	private String getAllOpenFinalResponseRecoveryClaimTasksQueryForEmail(
			TaskCriteria criteria, int daysForEmailTriggering) {
		StringBuilder queryString = new StringBuilder();
		queryString.append(" SELECT taskInstance ");
		queryString.append(" FROM TaskInstance taskInstance, BasePartReturn partReturn, RecoveryClaim claim");
		queryString.append(" where taskInstance.isOpen = true  ");
		if(criteria.getParams().containsKey("taskNames")){
			queryString.append(" and taskInstance.name in (:taskNames) ");
		}
		if(criteria.getParams().containsKey("inStates")){
			queryString.append(" and claim.recoveryClaimState in (:inStates)  ");
		} if(criteria.getParams().containsKey("notInStates")){
			queryString.append(" and claim.recoveryClaimState not in (:notInStates)  ");
		}
		queryString.append("and taskInstance.partReturnId = partReturn.id ");
		queryString.append("and taskInstance.claimId = claim.id ");
		queryString.append(" and ( ");
		queryString.append(getBuConfigDaysFilterCriteriaWithDateRange(criteria.getPendingOverDueDaysBUMapForEmail(), criteria.getBuWiseFilterColumns()));
		queryString.append("  ) ");
		return queryString.toString();
	}

	public List<User> findAvailableUsersBelongingToRole(String roleName,User loggedInUser) {
        List<User> userList = getHibernateTemplate().find(
                "select u from User u, Role r ,UserBUAvailability uba where uba.orgUser = u " +
                        "and uba.role = r and r in elements(u.roles) and r.name=?", roleName);
        Set<User> userSet = new HashSet<User>(userList);
        Set<User> ncrUsers=new HashSet<User>();
        for(User user:userList)
        {
        	if(user.hasRole(Role.NCR_PROCESSOR))
        	   {
                ncrUsers.add(user);
                userSet.remove(user);
               }            	    	     
        }
        if(loggedInUser.hasRole(Role.NCR_PROCESSOR))
          {
        	return new ArrayList<User>(ncrUsers);
          }
        else
         {
        	return new ArrayList<User>(userSet);
         }
    }

	public List<TaskInstance> getAllOpenTasksInWpraInbox(final TaskCriteria criteria) {

		final String queryString = getAllOpenTasksQueryForWpra(criteria);
		return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for(String taskName : criteria.getParams().keySet()){
					query.setParameterList(taskName, criteria.getParams().get(taskName));
				}
				return query.list();
			}
		});
	
	}

	private String getAllOpenTasksQueryForWpra(TaskCriteria criteria) {

		StringBuilder queryString = new StringBuilder();
		queryString.append(" SELECT taskInstance ");
		queryString.append(" FROM TaskInstance taskInstance, PartReturn partReturn, Claim claim ,Wpra wpra");
		queryString.append(" where taskInstance.isOpen = true  ");
		if(criteria.getParams().containsKey("taskNames")){
			queryString.append(" and taskInstance.name in (:taskNames) ");
		}
		queryString.append("and taskInstance.partReturnId = partReturn.id ");
		queryString.append("and taskInstance.claimId = claim.id ");
		queryString.append("and  partReturn.wpra= wpra.id ");
		queryString.append(" and ( ");
		queryString.append(getBuConfigDaysFilterCriteriaWithDateRange(criteria.getDaysBUMapConsideredForDenying(), criteria.getBuWiseFilterColumns()));
		queryString.append("  ) ");
		return queryString.toString();
	
	}

	public List<TaskInstance> getAllOpenTasksInSupplierPartShippedInbox(
			final TaskCriteria criteria) {
		final String queryString = getAllOpenTasksQueryForSupplierPartShipped(criteria);
		return (List<TaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for(String taskName : criteria.getParams().keySet()){
					query.setParameterList(taskName, criteria.getParams().get(taskName));
				}
				return query.list();
			}
		});
	}

	private String getAllOpenTasksQueryForSupplierPartShipped(
			TaskCriteria criteria) {
		StringBuilder queryString = new StringBuilder();
		boolean isNotFirstElement = Boolean.FALSE;
		queryString.append(" SELECT taskInstance ");
		queryString.append(" FROM TaskInstance taskInstance, RecoveryClaim claim ");
		queryString.append(" where taskInstance.isOpen = true ");
		if(criteria.getParams().containsKey("taskNames")){
			queryString.append(" and taskInstance.task.name in (:taskNames) ");
		}
		queryString.append("and taskInstance.claimId = claim.id ");
		queryString.append(" and ( ");
        queryString.append(getBuConfigDaysFilterCriteriaWithDateRange(criteria.getDaysBUMapConsideredForDenying(), criteria.getBuWiseFilterColumns()));
        queryString.append("  ) ");
		return queryString.toString();
		

	}

    private String getBuConfigDaysFilterCriteriaWithDateRange(Map<String, List<Object>> daysBUMapConsideredForDenying, Map<String, String> buWiseFilterColumns){
        boolean isNotFirstElement = Boolean.FALSE;
        StringBuilder buConfigValueConcatString = new StringBuilder();
        for (String key : daysBUMapConsideredForDenying.keySet()) {
            if (isNotFirstElement) {
                buConfigValueConcatString.append(" or ");
            }
            buConfigValueConcatString
                    .append(" (claim.businessUnitInfo = '");
            buConfigValueConcatString.append(key);
            buConfigValueConcatString.append("' and ");
            if(buWiseFilterColumns.containsKey("ALL")){
                buConfigValueConcatString.append("TRUNC("+buWiseFilterColumns.get("ALL"));
            } else {
                buConfigValueConcatString.append("TRUNC("+buWiseFilterColumns.get(key));
            }
            buConfigValueConcatString.append(") + (");
            buConfigValueConcatString.append(daysBUMapConsideredForDenying
                    .get(key).get(0));
            buConfigValueConcatString.append(") <= sysdate) ");
            isNotFirstElement = true;
        }
        return buConfigValueConcatString.toString();
    }
    
    public TaskInstance getOpenOnHoldTaskForRecoveryClaim(final RecoveryClaim recoveryClaim){
    	final String queryString = getOpenOnHoldTaskQueryForRecoveryClaim();
		return (TaskInstance) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				query.setParameter("taskName", WorkflowConstants.ON_HOLD_FOR_PART_RETURN);
				query.setParameter("recoveryClaimId", recoveryClaim.getId());
				return query.uniqueResult();
			}
		});
    }
    
    private String getOpenOnHoldTaskQueryForRecoveryClaim(){
    	StringBuilder queryString = new StringBuilder();
    	queryString.append(" SELECT taskInstance");
		queryString.append(" FROM TaskInstance taskInstance, RecoveryClaim recClaim");
		queryString.append(" where taskInstance.isOpen = true");
		queryString.append(" and taskInstance.task.name = :taskName");
		queryString.append(" and taskInstance.claimId = :recoveryClaimId");
        return queryString.toString();
    }

}

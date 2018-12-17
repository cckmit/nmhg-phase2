package tavant.twms.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.CollectionUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;

public class BuConfigSchedulerTaskServiceImpl implements BuConfigSchedulerTaskService {
	private static Logger logger = Logger.getLogger(BuConfigSchedulerTaskServiceImpl.class.getName());
	
	private ConfigParamService configParamService;
	private EventService eventService;
	private WorkListItemService workListItemService;
	private SendEmailService sendEmailService;
	private String externalUrlForEmail;
	private String fromAddress;
	private String emailPendingOverDueDaysExceedTemplate;

	public String getEmailPendingOverDueDaysExceedTemplate() {
		return emailPendingOverDueDaysExceedTemplate;
	}

	public void setEmailPendingOverDueDaysExceedTemplate(
			String emailPendingOverDueDaysExceedTemplate) {
		this.emailPendingOverDueDaysExceedTemplate = emailPendingOverDueDaysExceedTemplate;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	public void executeTasks(){
		populateDummyAuthentication();
        try{
		    partsReturnClaimCrossedOverDueDays();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
		    draftClaimCrossedDueDays();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
            forwardedClaimCrossedOverDueDays();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
		try{
		    dsmRepliedClaimsCrossedOverDueDays();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
		    denyShipmentGeneratedClaimsCrossedWindowPeriodDays();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
            autoReplyOnDaysPastDueForInternalUsers();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
		/*try{
            cleanUpWpraGeneratedForParts();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
		    cleanUpSupplierPartsShipped();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }*/

	}

    public void executeCleanupForWpraAndSupPrtShippedTasks(){
        populateDummyAuthentication();
        try{
            cleanUpWpraGeneratedForParts();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
        try{
            cleanUpSupplierPartsShipped();
        }catch (Exception ex){
            logger.error(ex.fillInStackTrace());
        }
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void cleanUpSupplierPartsShipped() {
        logger.error("Initiating Supplier shipped parts scheduler");
		List taskNames = new ArrayList();
		taskNames.add(WorkflowConstants.SUPPLIER_PARTS_SHIPPED);

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();
		params.put("taskNames", taskNames);

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add(WorkflowConstants.PARTS_SHIPPED);

		TaskCriteria criteria = createTaskCriteria(
				ConfigName.DAYS_TO_CLEAN_UP_SUPPLIER_PARTS_SHIPPED.getName(),
				params, endTransitions, null, null,
				"Supplier Parts Shipped crossed the number of days within prescribed limit.",
				filterColumns);
		processJobForSupplierPartsShippedInbox(criteria);
	}

	private void processJobForSupplierPartsShippedInbox(TaskCriteria criteria) {

		try {
			List<TaskInstance> resultingTaskInstances = this.workListItemService
					.getAllOpenTasksInSupplierPartShippedInbox(criteria);

            logger.error("Supplier shipped parts scheduler. Got " + resultingTaskInstances.size() +" records to clean");

			if (!CollectionUtils.isEmpty(resultingTaskInstances)) {
				for (TaskInstance taskInstance : resultingTaskInstances) {

					if (!CollectionUtils.isEmpty(criteria.getIgnoreTasks())
							&& criteria.getIgnoreTasks().contains(
									taskInstance.getName())) {
						continue;
					}

					for (String endTransition : criteria.getEndTransitions()) {
						if (!hasTransitionByName(taskInstance, endTransition)) {
							continue;
						}
                        logger.error("Ending task instance "+ taskInstance.getId());
						workListItemService.endTaskWithTransition(taskInstance,
								endTransition);
					}
				}
			}
		} catch (NoValuesDefinedException e) {
			logger.error(" No Bu Config Param values defined for config param "
					+ criteria.getConfigParam() + " for task : "
					+ e.getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			logger.error("Error occurred in performing task "
					+ ex.getStackTrace()[1].getMethodName()
					+ " while executing " + criteria.getEndTransitions()
					+ " transition on supplier part shipped : "
					+ ex.getMessage());
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void cleanUpWpraGeneratedForParts() {

		List taskNames = new ArrayList();
		taskNames.add(WorkflowConstants.GENERATED_WPRA);

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();
		params.put("taskNames", taskNames);

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("endWpra");

		TaskCriteria criteria = createTaskCriteria(
				ConfigName.DAYS_TO_CLEAN_UP_WPRA_GENERATED_FOR_PARTS.getName(),
				params, endTransitions, null, null,
				"Wpra crossed the number of days within prescribed limit.",
				filterColumns);
		processJobForWpraGeneratedInbox(criteria);

	}

	private void processJobForWpraGeneratedInbox(TaskCriteria criteria) {

		try {
			List<TaskInstance> resultingTaskInstances = this.workListItemService
					.getAllOpenTasksInWpraInbox(criteria);

            if(resultingTaskInstances.size() == 0)
                logger.error("No record to end wpra");

			if (!CollectionUtils.isEmpty(resultingTaskInstances)) {
				for (TaskInstance taskInstance : resultingTaskInstances) {

                    logger.error("Ending WPRA task instance : " + taskInstance.getId());
					if (!CollectionUtils.isEmpty(criteria.getIgnoreTasks())
							&& criteria.getIgnoreTasks().contains(
									taskInstance.getName())) {
						continue;
					}

					for (String endTransition : criteria.getEndTransitions()) {
						if (!hasTransitionByName(taskInstance, endTransition)) {
                            logger.error("Task instance id: " + taskInstance.getId() + "does not have transition "+endTransition +" ignoring...");
							continue;
						}
						workListItemService.endTaskWithTransition(taskInstance,
								endTransition);
					}
				}
			}
		} catch (NoValuesDefinedException e) {
			logger.error(" No Bu Config Param values defined for config param "
					+ criteria.getConfigParam() + " for task : "
					+ e.getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			logger.error("Error occurred in performing task "
					+ ex.getStackTrace()[1].getMethodName()
					+ " while executing " + criteria.getEndTransitions()
					+ " transition on Wpra : " + ex.getMessage());
		}
	}

	private void partsReturnClaimCrossedOverDueDays() {
		Map<String, RejectionReason> defaultRejectionReasonMap = getDefaultRejectionReason(ConfigName.DEFAULT_REJECTION_REASON_FOR_OVERDUE_PARTS
				.getName());
		
		if(defaultRejectionReasonMap == null)
		{
			logger.error("partsReturnClaimCrossedOverDueDays API: Claims are not denied owing to defaultRejectionReasonMap null");
			return;
		}

		List states = new ArrayList();
		states.add(ClaimState.ACCEPTED_AND_CLOSED);
		states.add(ClaimState.DENIED_AND_CLOSED);
		states.add(ClaimState.DENIED);
		states.add(ClaimState.PENDING_PAYMENT_SUBMISSION);
		states.add(ClaimState.PENDING_PAYMENT_RESPONSE);

		List taskNames = new ArrayList();
		taskNames.add("Overdue Parts");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("notInStates", states);
		params.put("taskNames", taskNames);
		
		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("Deny");
		
		Set<String> ignoreTasks = new HashSet<String>();
		ignoreTasks.add("Due Parts");
		
		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");
		
		TaskCriteria criteria = createTaskCriteria(ConfigName.DAYS_FOR_WAITING_FOR_PART_RETURNS_CLAIM_DENIED.getName(),
				params, 
				endTransitions,
				ignoreTasks,defaultRejectionReasonMap,
				"Claim Denied. Parts were not shipped within prescribed limit",
				filterColumns);
		Set<Long> allClaims = processJob(criteria);
		for (Long claimId : allClaims) {
			eventService.createEvent("partReturn",
					EventState.CLAIM_DENIED_FOR_LACK_OF_PART_RETURNS, claimId);
		}
	}
	

	private void draftClaimCrossedDueDays() {
		List states = new ArrayList();
		states.add(ClaimState.DRAFT);

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("inStates", states);

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("Delete Draft");

		String dateField = null;
		Map<String, String> buWiseFilterColumns = new HashMap<String, String>();
		Map<String, List<Object>> dateForDraftDeletion = configParamService.getValuesForAllBUs(ConfigName.DATE_TOUSER_FOR_DRAFTCLAIM_DELETION
				.getName());

		for(String eachBu : dateForDraftDeletion.keySet()){
			dateField = (String)dateForDraftDeletion.get(eachBu).get(0);
			if("filedOn".equalsIgnoreCase(dateField)){
				buWiseFilterColumns.put(eachBu, " claim.filedOnDate ");
			} else if("updatedOn".equalsIgnoreCase(dateField)){
				buWiseFilterColumns.put(eachBu, " claim.lastUpdatedOnDate ");
			}
		}

		TaskCriteria criteria = createTaskCriteria(ConfigName.DAYS_FOR_WAITING_FOR_PART_RETURNS_CLAIM_DENIED.getName(),
				params, endTransitions, null, null, "Draft Claim Deleted. No action taken within prescribed limit", buWiseFilterColumns);
		processJob(criteria);
	}

	private void forwardedClaimCrossedOverDueDays() {
		Map<String, RejectionReason> defaultRejectionReasonMap = getDefaultRejectionReason(ConfigName.DEFAULT_REJECTION_REASON_FOR_FORWARDED_CLAIM
				.getName());

		if(defaultRejectionReasonMap == null){
			logger.error("forwardedClaimCrossedOverDueDays API: Claims are not denied owing to defaultRejectionReasonMap");
			return;
		}

		List states = new ArrayList();
		states.add(ClaimState.FORWARDED);

		List taskNames = new ArrayList();
		taskNames.add("Overdue Parts");
		taskNames.add("Forwarded");
		taskNames.add("Due Parts");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("inStates", states);
		params.put("taskNames", taskNames);

		Set<String> ignoreTasks = new HashSet<String>();
		ignoreTasks.add("Overdue Parts");
		ignoreTasks.add("Due Parts");

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put(AdminConstants.NMHGEMEA, "taskInstance.create");
		filterColumns.put(AdminConstants.NMHGAMER, "claim.activeClaimAudit.repairDate");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("DenyOnNoReply");

		TaskCriteria criteria = createTaskCriteria(ConfigName.DAYS_FOR_FORWARDED_CLAIM_DENIED.getName(),
				params, 
				endTransitions,
				ignoreTasks,
				defaultRejectionReasonMap,
				"Claim Denied. Was not responded to within prescribed limit.",
				filterColumns);
		processJob(criteria);
	}

	private void dsmRepliedClaimsCrossedOverDueDays() {

		List states = new ArrayList();
		states.add(ClaimState.SERVICE_MANAGER_RESPONSE);

		List taskNames = new ArrayList();
		taskNames.add("Service Manager Response");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("inStates", states);
		params.put("taskNames", taskNames);

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("Delete");


		TaskCriteria criteria = createTaskCriteria(ConfigName.DENY_DSM_REPLIED_CLAIMS_CROSSED_WINDOW_PERIOD_DAYS.getName(),
				params, 
				endTransitions,
				null,
				null,
				"Claim Deleted. Was not responded to within prescribed limit.",
				filterColumns);
		processJob(criteria);
	}
	
	private void denyShipmentGeneratedClaimsCrossedWindowPeriodDays() {
		Map<String, RejectionReason> defaultRejectionReasonMap = getDefaultRejectionReason(null);

		if(defaultRejectionReasonMap == null){
			logger.error("denyShipmentGeneratedClaimsCrossedWindowPeriodDays API: Claims are not denied owing to defaultRejectionReasonMap or daysBUMapConsideredForDenying null");
			return;
		}

		List states = new ArrayList();

		states.add(ClaimState.ACCEPTED_AND_CLOSED);
		states.add(ClaimState.DENIED_AND_CLOSED);
		states.add(ClaimState.DENIED);
		states.add(ClaimState.PENDING_PAYMENT_SUBMISSION);
		states.add(ClaimState.PENDING_PAYMENT_RESPONSE);

		List taskNames = new ArrayList();
		taskNames.add("Shipment Generated");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("notInStates", states);
		params.put("taskNames", taskNames);

		Set<String> ignoreTasks = new HashSet<String>();
		ignoreTasks.add("Overdue Parts");
		ignoreTasks.add("Due Parts");

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("Deny");

		TaskCriteria criteria = createTaskCriteria(ConfigName.DENY_SHIPMENT_GENERATED_CLAIMS_CROSSED_WINDOW_PERIOD_DAYS.getName(),
				params, 
				endTransitions,
				ignoreTasks,
				defaultRejectionReasonMap,
				"Claim Denied. Parts were not shipped within prescribed limit.",
				filterColumns);
		Set<Long> allClaims = processJob(criteria);
		for (Long claimId : allClaims) {
			eventService.createEvent("partReturn",
					EventState.CLAIM_DENIED_FOR_LACK_OF_PART_RETURNS, claimId);
		}
	}
	
	private void autoReplyOnDaysPastDueForInternalUsers() {

		Map<String, RejectionReason> defaultRejectionReasonMap = getDefaultRejectionReason(null);

		if(defaultRejectionReasonMap == null){
			logger.error("denyShipmentGeneratedClaimsCrossedWindowPeriodDays API: Claims are not denied owing to defaultRejectionReasonMap or daysBUMapConsideredForDenying null");
			return;
		}

		List states = new ArrayList();
		states.add(ClaimState.ADVICE_REQUEST);
		states.add(ClaimState.CP_REVIEW);

		List taskNames = new ArrayList();
		taskNames.add("Advice Request");
		taskNames.add("CP Review");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("inStates", states);
		params.put("taskNames", taskNames);

		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");

		List<String> endTransitions = new ArrayList<String>();
		endTransitions.add("Advice");
		endTransitions.add("Review");

		TaskCriteria criteria = createTaskCriteria(ConfigName.DAYS_FOR_INTERNAL_AGEING_PERIOD.getName(),
				params, 
				endTransitions,
				null,
				defaultRejectionReasonMap,
				"Auto reply generated by the system due to time window elapse.",
				filterColumns);
		processJob(criteria);
	}

	private Set<Long> processJob(TaskCriteria criteria) {

		Set<Long> processedClaims = new HashSet<Long>();
		try	{
			List<TaskInstance> resultingTaskInstances = this.workListItemService
					.getAllOpenTasks(criteria);

			if(!CollectionUtils.isEmpty(resultingTaskInstances)){
				for (TaskInstance taskInstance : resultingTaskInstances) {

					if(!CollectionUtils.isEmpty(criteria.getIgnoreTasks()) &&
							criteria.getIgnoreTasks().contains(taskInstance.getName())){
						continue;
					}

					Claim claim = (Claim) taskInstance.getContextInstance()
							.getVariable("claim");
                    //No need to set claim data if it is not satisfying ending condition
                    if((criteria.getConfigParam().equals(ConfigName.DENY_SHIPMENT_GENERATED_CLAIMS_CROSSED_WINDOW_PERIOD_DAYS.getName()) && !claim.isOpen()) || !checkForAMERForwardedClaims(criteria, claim) ){
                        continue;
                    }
					claim.setInternalComment(criteria.getInternalComment());
					claim.setExternalComment(claim.getInternalComment());
					if(criteria.getRejectionReasonMap() != null){
						 List<RejectionReason> tempRejectionList = new ArrayList<RejectionReason>();
						 tempRejectionList.add(criteria.getRejectionReasonMap().get(claim.getBusinessUnitInfo().getName()));
						claim.setRejectionReasons(tempRejectionList);
					}
					if(hasTransitionByName(taskInstance, "Review")){
						claim.setCpReviewed(Boolean.TRUE);
					}
					processedClaims.add(claim.getId());
					if (criteria.getConfigParam().equals(ConfigName.DENY_SHIPMENT_GENERATED_CLAIMS_CROSSED_WINDOW_PERIOD_DAYS.getName())) {
						List<TaskInstance> openTasks = workListItemService.findAllClaimSubmissionOpenTasksForClaim(claim.getId());
						if(claim.isOpen()){
							denyShipmentGeneratedClaimsCrossingWindow(claim,openTasks);		
						}
					} else {
						for(String endTransition : criteria.getEndTransitions()){
							if (!hasTransitionByName(taskInstance, endTransition)) {
								continue;
							}
							if(checkForAMERForwardedClaims(criteria, claim)) {
								workListItemService.endTaskWithTransition(taskInstance,
										endTransition);
							}
						}
					}
				}
			}
		} catch (NoValuesDefinedException e) {
			logger.error(" No Bu Config Param values defined for config param "+criteria.getConfigParam()+" for task : "+e.getStackTrace()[1].getMethodName());
		} catch(Exception ex){
			logger.error("Error occurred in performing task "+ ex.getStackTrace()[1].getMethodName()+" while executing "+ criteria.getEndTransitions()+" transition on claim : "+ ex.getMessage());
		}
		return processedClaims;		
	}
	
	// This API returns true for All Config Params other than DAYS_FOR_FORWARDED_CLAIM_DENIED, or BU is EMEA, so that other jobs are not affected
	// In case of AMER Forwarded claim, returns true if document present  
	private boolean checkForAMERForwardedClaims(TaskCriteria criteria, Claim claim) {
		if (criteria.getConfigParam().equals(ConfigName.DAYS_FOR_FORWARDED_CLAIM_DENIED.getName()) 
				 && claim.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
			return !documentsAvailable(claim);
		}
		return true;
	}
	
	private boolean documentsAvailable(Claim claim) {
		if(claim.isCmsAuthCheck()
				|| claim.getCmsTicketNumber() != null
				|| installationCommentsAvailable(claim)) {
			return true;
		}
		return false;
	}
	
	private boolean installationCommentsAvailable(Claim claim) {
		if (claim.getItemReference().isSerialized()) {
			return claim.getItemReference().getReferredInventoryItem().getInventoryCommentExists();
		}
		return false;
	}
	
	private void denyShipmentGeneratedClaimsCrossingWindow(Claim claim, List<TaskInstance> openTasks) {
		for (TaskInstance openTask : openTasks) {
			try {
                if(hasTransitionByName(openTask, "Deny")){
                    claim.setInternalComment("Deny Shipment Generated Claims which croses Window Period Days");
                    claim.setExternalComment("Deny Shipment Generated Claims which croses Window Period Days");
                    workListItemService.endTaskWithTransition(openTask, "Deny");
                }

			} catch (Exception e) {
				logger.error(
						"Error when denying Shipment Generated Claims which croses Window Period Days",
						e);
			}
		}
	}
	
	private TaskCriteria createTaskCriteria(String configParam, 
			Map<String, List<Object>> params, 
			List<String> endTransitions, 
			Set<String> ignoreTasks, 
			Map<String, RejectionReason> defaultRejectionReasonMap, 
			String internalComment, 
			Map<String, String> filterColumns){
		TaskCriteria criteria = new TaskCriteria();
		criteria.setConfigParam(configParam);
		criteria.setEndTransitions(endTransitions);
		criteria.setBuWiseFilterColumns(filterColumns);
		criteria.setIgnoreTasks(ignoreTasks);
		criteria.setInternalComment(internalComment);
		criteria.setParams(params);
		criteria.setRejectionReasonMap(defaultRejectionReasonMap);
		return criteria;
	}
	
	private Map<String, RejectionReason> getDefaultRejectionReason(
			String defaultRejectionReasonToUse) {
		String configNameParam = defaultRejectionReasonToUse;
		if (defaultRejectionReasonToUse == null) {
			configNameParam = ConfigName.DEFAULT_REJECTION_REASON.getName();
		}

		List<ConfigValue> configValues = configParamService.getValuesForConfigParam(configNameParam);
				
		Map<String, RejectionReason> businessUnitRejectionReasonMap = new HashMap<String, RejectionReason>();
		if (configValues == null
				|| CollectionUtils.isEmpty(configValues)) {			
			return null;
		}
		ConfigParam configParam = configValues.get(0).getConfigParam();
		for (ConfigValue configValue : configValues) {
			try {
				RejectionReason rejectionReason = (RejectionReason) configParamService
						.findObjectForLovId(Long.parseLong(configValue
								.getValue()), Class.forName(configParam
								.getType()));
				businessUnitRejectionReasonMap.put(configValue
						.getBusinessUnitInfo().getName(), rejectionReason);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return businessUnitRejectionReasonMap;
	}
	
	private boolean hasTransitionByName(TaskInstance taskInstance,
			String transitionName) {
        boolean isTransitionAvailable = false;
        List<Transition> transitions = taskInstance.getAvailableTransitions();
		for (Transition transition : transitions) {
			if (transitionName.equals(transition.getName())){
                isTransitionAvailable=true;
                break;
            }
		}
		return isTransitionAvailable;
	}
	
	private boolean isClaimSetContainsClaim(Set<Claim> claimSet,Claim claim){
   	 for(Claim clm :  claimSet){
   		 if(clm.getClaimNumber().equalsIgnoreCase(claim.getClaimNumber())){
   			 return true;
   		 }
   	 }
   	 return false;
   	 
    }
	
	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
	
	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getExternalUrlForEmail() {
		return externalUrlForEmail;
	}

	public void setExternalUrlForEmail(String externalUrlForEmail) {
		this.externalUrlForEmail = externalUrlForEmail;
	}	
}

package tavant.twms.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.WorkListService;

public class SupplierFinalResponseEmailSchedulerImpl implements
		SupplierFinalResponseEmailScheduler {

	private static Logger logger = Logger
			.getLogger(SupplierFinalResponseEmailSchedulerImpl.class
					.getName());
	private ConfigParamService configParamService;
	private WorkListItemService workListItemService;
	private SendEmailService sendEmailService;
	private String externalUrlForEmail;

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	public String getExternalUrlForEmail() {
		return externalUrlForEmail;
	}

	public void setExternalUrlForEmail(String externalUrlForEmail) {
		this.externalUrlForEmail = externalUrlForEmail;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	private String fromAddress;
	private String emailSupplierFinalResponseTemplate;

	private WorkListService workListService;

	public void executeTasks() {
		populateDummyAuthentication();
		emailTriggeringForSupplierFinalResponse();
	}

	public WorkListService getWorkListService() {
		return workListService;
	}

	public void setWorkListService(WorkListService workListService) {
		this.workListService = workListService;
	}

	
	public String getEmailSupplierFinalResponseTemplate() {
		return emailSupplierFinalResponseTemplate;
	}

	public void setEmailSupplierFinalResponseTemplate(
			String emailSupplierFinalResponseTemplate) {
		this.emailSupplierFinalResponseTemplate = emailSupplierFinalResponseTemplate;
	}

	private void emailTriggeringForSupplierFinalResponse() {
		List states = new ArrayList();
		states.add(RecoveryClaimState.ACCEPTED);
		states.add(RecoveryClaimState.AUTO_CLOSED);
		states.add(RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED);
		states.add(RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED);
		states.add(RecoveryClaimState.REJECTED);
		
		List inStates = new ArrayList();
		inStates.add(RecoveryClaimState.ON_HOLD_FOR_PART_RETURN);
		

		List taskNames = new ArrayList();
		taskNames.add("Confirm Dealer Part Returns");
		taskNames.add("Confirm Part Returns");

		Map<String, List<Object>> params = new HashMap<String, List<Object>>();		
		params.put("inStates", inStates);
		params.put("notInStates", states);
		params.put("taskNames", taskNames);
		Set<String> ignoreTasks = new HashSet<String>();
		List<String> endTransitions = new ArrayList<String>();
		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");
		int daysForEmailTriggering =0;
		if(StringUtils.hasText(ConfigName.DAYS_PENDING_IN_FINAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName())){
		  daysForEmailTriggering = Integer.parseInt(configParamService.getStringValue(ConfigName.DAYS_PENDING_IN_FINAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName()));
		}
		TaskCriteria criteriaForSupplierFinalResponse = createTaskCriteria(ConfigName.DAYS_PENDING_IN_FINAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName(),
		params, 
		endTransitions,
		ignoreTasks,
		"Email Tiggering for Supplier to take action before Final Response Period",
		filterColumns);
		Set<RecoveryClaim> claimsInFinalResponse = processCriteriaForEmailTriggering(criteriaForSupplierFinalResponse,daysForEmailTriggering); 
		if(claimsInFinalResponse!=null && claimsInFinalResponse.size() > 0){
			Map<Map<String,String>,List<String>> mapBySupplier = new HashMap<Map<String,String>, List<String>>();
		    List <String> claimIdList = new ArrayList<String>();
			for (RecoveryClaim recoveryClaim : claimsInFinalResponse) {
				if(recoveryClaim!=null){
	        	 if(claimIdList.contains(recoveryClaim.getRecoveryClaimNumber()))
	        	 continue;
	        	 claimIdList.add(recoveryClaim.getRecoveryClaimNumber());
				 StringBuilder emailDetailsForFinalResponse = new StringBuilder();
				 emailDetailsForFinalResponse.append("\n").append("Recovery ClaimNumber=").append(recoveryClaim.getRecoveryClaimNumber());
                	 Map<String,String> dealerMap= new HashMap<String, String>();
                	 List<String> listOfClaimDetails=new ArrayList<String>();
                	 User claimAssignee = recoveryClaim.getRecoveryClaimAudits().get(recoveryClaim.getRecoveryClaimAudits().size()-1).getD().getLastUpdatedBy();
                	 if(claimAssignee!=null){
                	 dealerMap.put(claimAssignee.getName(),claimAssignee.getEmail());
                	 if(!CollectionUtils.isEmpty(mapBySupplier.get(dealerMap))){
                     listOfClaimDetails = mapBySupplier.get(dealerMap);
                	 }
                	 listOfClaimDetails.add("\n"+emailDetailsForFinalResponse.toString());
                	 mapBySupplier.put(dealerMap,listOfClaimDetails);
				}
			}
		}
		    Set<Entry<Map<String, String>, List<String>>> entrySet = mapBySupplier.entrySet();
			Map<String,Object> paramMap = new HashMap<String, Object>();
			try{
        	for(Map.Entry<Map<String,String>, List<String>> entry : entrySet){
        		  String toEmail = null;
        		  if(entry.getKey()!=null){
        		  Map<String, String> dealerMap = entry.getKey();
          		  for(String dealerDetail: dealerMap.keySet()){
        		  toEmail=	dealerMap.get(dealerDetail);
        		  paramMap.put("userName", dealerDetail);
        		  paramMap.put("url", externalUrlForEmail);
            	  paramMap.put("emailDetailsForFinalResponse",org.apache.commons.lang.StringUtils.join(entry.getValue().toArray(),","));
            	  if(StringUtils.hasText(toEmail)){
            		  String subject = "Final Response Period - No Action Taken";
            	   sendEmailService.sendEmail(fromAddress,toEmail.trim(),subject,emailSupplierFinalResponseTemplate,paramMap);
            	   }
        		   }
        		  }
        	}
		}
			catch(Exception e){
                logger.error("Final Response Period - crossed: Exception in sending emails" + e.getMessage());
			}
	}
		
		
	}

	private Set<RecoveryClaim> processCriteriaForEmailTriggering(
			TaskCriteria criteriaForSupplierFinalResponse,
			int daysForEmailTriggering) {
		Set<RecoveryClaim> processedClaims = new HashSet<RecoveryClaim>();
		try {
			List<TaskInstance> resultingTaskInstances = this.workListItemService
					.getAllTasksForSupplierFinalResponsePeriod(
							criteriaForSupplierFinalResponse,
							daysForEmailTriggering);

			if (!CollectionUtils.isEmpty(resultingTaskInstances)) {
				for (TaskInstance taskInstance : resultingTaskInstances) {
					RecoveryClaim recoveryClaim = (RecoveryClaim) taskInstance
							.getContextInstance().getVariable("recoveryClaim");
					if (!isClaimSetContainsClaim(processedClaims, recoveryClaim)) {
						processedClaims.add(recoveryClaim);
					}
				}
			}
		} catch (NoValuesDefinedException e) {
			logger.error(" No Bu Config Param values defined for config param "
					+ criteriaForSupplierFinalResponse.getConfigParam()
					+ " for task : " + e.getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			logger.error("Error occurred in performing task "
					+ ex.getStackTrace()[1].getMethodName()
					+ " while executing "
					+ criteriaForSupplierFinalResponse.getEndTransitions()
					+ " transition on claim : " + ex.getMessage());
		}
		return processedClaims;
	}

	private TaskCriteria createTaskCriteria(String configParam,
			Map<String, List<Object>> params, List<String> endTransitions,
			Set<String> ignoreTasks,

			String internalComment, Map<String, String> filterColumns) {
		TaskCriteria criteria = new TaskCriteria();
		criteria.setConfigParam(configParam);
		//criteria.setEndTransitions(endTransitions);
		criteria.setBuWiseFilterColumns(filterColumns);
		criteria.setIgnoreTasks(ignoreTasks);
		criteria.setInternalComment(internalComment);
		criteria.setParams(params);
		// criteria.setRejectionReasonMap(defaultRejectionReasonMap);
		return criteria;
	}

	private boolean isClaimSetContainsClaim(Set<RecoveryClaim> processedClaims,
			RecoveryClaim recoveryClaim) {
		for (RecoveryClaim clm : processedClaims) {
			if (clm.getRecoveryClaimNumber().equalsIgnoreCase(
					recoveryClaim.getRecoveryClaimNumber())) {
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

}

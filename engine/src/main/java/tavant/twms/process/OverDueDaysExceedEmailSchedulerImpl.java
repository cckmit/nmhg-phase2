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
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;

public class OverDueDaysExceedEmailSchedulerImpl implements OverDueDaysExceedEmailScheduler {
	
	private static Logger logger = Logger.getLogger(OverDueDaysExceedEmailSchedulerImpl.class.getName());
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

	public String getEmailPendingOverDueDaysExceedTemplate() {
		return emailPendingOverDueDaysExceedTemplate;
	}

	public void setEmailPendingOverDueDaysExceedTemplate(
			String emailPendingOverDueDaysExceedTemplate) {
		this.emailPendingOverDueDaysExceedTemplate = emailPendingOverDueDaysExceedTemplate;
	}

	private String fromAddress;
	private String emailPendingOverDueDaysExceedTemplate;
	
	public void executeTasks() {
		populateDummyAuthentication();
		emailTriggeringForOverDueDaysExceed();
		
	}

	private void emailTriggeringForOverDueDaysExceed() {
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
		Set<String> ignoreTasks = new HashSet<String>();
		List<String> endTransitions = new ArrayList<String>();
		Map<String, String> filterColumns = new HashMap<String, String>();
		filterColumns.put("ALL", "taskInstance.create");
		int daysForEmailTriggering =0;
		if(StringUtils.hasText(ConfigName.DAYS_PENDING_IN_OVERDUE_STATUS_FOR_EMAIL_TRIGGERING.getName())){
		  daysForEmailTriggering = Integer.parseInt(configParamService.getStringValue(ConfigName.DAYS_PENDING_IN_OVERDUE_STATUS_FOR_EMAIL_TRIGGERING.getName()));
		}
		TaskCriteria criteriaForPendingOverdueDaysForEmail = createTaskCriteria(ConfigName.DAYS_PENDING_IN_OVERDUE_STATUS_FOR_EMAIL_TRIGGERING.getName(),
		params, 
		endTransitions,
		ignoreTasks,
		"Parts crossed the no of days in OverDue Status with prescribed limit",
		filterColumns);
		Set<Claim> claimsInOverdueStatus = processCriteriaForEmailTriggering(criteriaForPendingOverdueDaysForEmail,daysForEmailTriggering); 
		if(claimsInOverdueStatus!=null && claimsInOverdueStatus.size() > 0){
			Map<Map<String,String>,List<String>> mapWpraGroupByDealer = new HashMap<Map<String,String>, List<String>>();
		    List <String> claimIdList = new ArrayList<String>();
			for (Claim claim : claimsInOverdueStatus) {
				if(claim!=null){
	        	 if(claimIdList.contains(claim.getClaimNumber()))
	        	 continue;
	        	 claimIdList.add(claim.getClaimNumber());
				 List<OEMPartReplaced> allOEMPartsReplaced = claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced();
				 StringBuilder emailDetailsForPendingOverDue = new StringBuilder();
				 emailDetailsForPendingOverDue.append("\n").append("ClaimNumber=").append(claim.getClaimNumber());
				 if(!CollectionUtils.isEmpty(allOEMPartsReplaced)){
	      			for(OEMPartReplaced oemPartReplaced : allOEMPartsReplaced){
	      				 if(oemPartReplaced.getItemReference()!=null && oemPartReplaced.getItemReference().getReferredItem()!=null
	      				 && oemPartReplaced.getStatus()!=null && !oemPartReplaced.getStatus().getStatus().equals(PartReturnStatus.PART_SHIPPED.getStatus()) &&	StringUtils.hasText(oemPartReplaced.getItemReference().getReferredItem().getNumber())){
	      					 emailDetailsForPendingOverDue.append(" ").append(" ").append("PartNumber=").append(oemPartReplaced.getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand())); 
	      			  }
	      		    }	 
	      		   }
                	 Map<String,String> dealerMap= new HashMap<String, String>();
                	 List<String> listOfClaimDetails=new ArrayList<String>();
                	 dealerMap.put(claim.getFiledBy().getName(),claim.getFiledBy().getEmail());
                	 if(!CollectionUtils.isEmpty(mapWpraGroupByDealer.get(dealerMap))){
                     listOfClaimDetails = mapWpraGroupByDealer.get(dealerMap);
                	 }
                	 listOfClaimDetails.add("\n"+emailDetailsForPendingOverDue.toString());
                	 mapWpraGroupByDealer.put(dealerMap,listOfClaimDetails);
				}
			}
		    Set<Entry<Map<String, String>, List<String>>> entrySet = mapWpraGroupByDealer.entrySet();
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
            	  paramMap.put("emailDetailsForPendingOverDue",org.apache.commons.lang.StringUtils.join(entry.getValue().toArray(),","));
            	  if(StringUtils.hasText(toEmail)){
            		  String subject = "Overdue - No Action Taken";
            		  if(configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
            			  subject = "WPRA Generated - " + subject;
            		  }
            	   sendEmailService.sendEmail(fromAddress,toEmail.trim(),subject,emailPendingOverDueDaysExceedTemplate,paramMap);
            	   }
        		   }
        		  }
        	}
		}
			catch(Exception e){
                logger.error("WPRA Generated - Overdue: Exception in sending emails" + e.getMessage());
			}
	}
		
		
	}
	private Set<Claim> processCriteriaForEmailTriggering(
			TaskCriteria criteriaForPendingOverdueDaysForEmail,int daysForEmailTriggering) {
		Set<Claim> processedClaims = new HashSet<Claim>();
		try	{
			List<TaskInstance> resultingTaskInstances = this.workListItemService
					.getAllTasksForPendingOverDue(criteriaForPendingOverdueDaysForEmail,daysForEmailTriggering);

			if(!CollectionUtils.isEmpty(resultingTaskInstances)){
				for (TaskInstance taskInstance : resultingTaskInstances) {
					Claim claim = (Claim) taskInstance.getContextInstance()
							.getVariable("claim");
					claim.setInternalComment(criteriaForPendingOverdueDaysForEmail.getInternalComment());
					claim.setExternalComment(claim.getInternalComment());
					if(criteriaForPendingOverdueDaysForEmail.getRejectionReasonMap() != null){
						 List<RejectionReason> tempRejectionList = new ArrayList<RejectionReason>();
						 tempRejectionList.add(criteriaForPendingOverdueDaysForEmail.getRejectionReasonMap().get(claim.getBusinessUnitInfo().getName()));
						claim.setRejectionReasons(tempRejectionList);
					}
					if(hasTransitionByName(taskInstance, "Review")){
						claim.setCpReviewed(Boolean.TRUE);
					}
					if(!isClaimSetContainsClaim(processedClaims,claim)){
					   processedClaims.add(claim);
					}
				}
			}
		} catch (NoValuesDefinedException e) {
			logger.error(" No Bu Config Param values defined for config param "+criteriaForPendingOverdueDaysForEmail.getConfigParam()+" for task : "+e.getStackTrace()[1].getMethodName());
		} catch(Exception ex){
			logger.error("Error occurred in performing task "+ ex.getStackTrace()[1].getMethodName()+" while executing "+ criteriaForPendingOverdueDaysForEmail.getEndTransitions()+" transition on claim : "+ ex.getMessage());
		}
		return processedClaims;		
	}
	private TaskCriteria createTaskCriteria(String configParam, 
			Map<String, List<Object>> params, 
			List<String> endTransitions, 
			Set<String> ignoreTasks, 
			
			String internalComment, 
			Map<String, String> filterColumns){
		TaskCriteria criteria = new TaskCriteria();
		criteria.setConfigParam(configParam);
		criteria.setEndTransitions(endTransitions);
		criteria.setBuWiseFilterColumns(filterColumns);
		criteria.setIgnoreTasks(ignoreTasks);
		criteria.setInternalComment(internalComment);
		criteria.setParams(params);
		//criteria.setRejectionReasonMap(defaultRejectionReasonMap);
		return criteria;
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


}

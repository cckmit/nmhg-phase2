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
package tavant.twms.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import org.springframework.transaction.support.TransactionTemplate;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.jbpm.WorkflowConstants;

/**
 * @author vineeth.varghese
 * @date Jul 13, 2006
 */
public class ClaimProcessServiceImpl implements ClaimProcessService {
    public static final String CLAIM_PROCESSING = "ClaimSubmission";

    private static final Logger logger = Logger.getLogger(ClaimProcessServiceImpl.class);

    private ClaimService claimService;
    
    private ProcessService processService;

    private WorkListItemService workListItemService;

    private ConfigParamService configParamService;

    private SecurityHelper securityHelper;

    private PartReturnProcessingService partReturnProcessingService;
    
    private TransactionTemplate transactionTemplate;

    public ProcessInstance startClaimProcessing(Claim claim) {        
        return processService.startProcess(CLAIM_PROCESSING, createProcessVariables(claim));
    }
    
    public ProcessInstance startClaimProcessingWithTransition(Claim claim, String transition) {
        return processService.startProcessWithTransition(CLAIM_PROCESSING, 
                createProcessVariables(claim), transition);        
    }
    
    
    //Not sure we need this method to deal with Hibernate Proxy
    private ProcessVariables createProcessVariables(Claim claim) {
        ProcessVariables processVariables = new ProcessVariables();
        // TODO - this is a hack to fix an issue with jBPM, needs to be fixed.
        if (claim instanceof HibernateProxy) {
            claim = (Claim) ((HibernateProxy) claim)
            .getHibernateLazyInitializer().getImplementation();
        }
        claim.getServiceInformation().getServiceDetail();
        processVariables.setVariable("claim", claim);
        processVariables.setVariable("isClaimDenied", Boolean.FALSE);
        processVariables.setVariable("isPartCorrected","false");
        //processVariables.setVariable("isThroughWPRA",String.valueOf(configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())));
        if (logger.isInfoEnabled()) {
            logger.info("Claim class " + claim.getClass());
        }
        return processVariables;
    }
    /*
     * Need to refactor this. !! 
     * 
     */
    public ProcessInstance startClaimProcessingForReopenClaims(Claim claim, String transition) {
        if (ClaimState.ACCEPTED_AND_CLOSED.equals(claim.getState())) {
            TaskInstance openTask = workListItemService.findTaskForClaimWithTaskName(claim.getId(),
                                                                                WorkflowConstants.PENDING_REC_INITIATION);
            if (openTask != null) {
                workListItemService.endTaskWithTransition(openTask, "goToEnd");
            }
        }
        return processService.startProcessWithTransition(CLAIM_PROCESSING, 
        		createProcessVariablesForReopenClaims(claim), transition);        
    }
    
    private ProcessVariables createProcessVariablesForReopenClaims(Claim claim) {
        ProcessVariables processVariables = new ProcessVariables();
        // TODO - this is a hack to fix an issue with jBPM, needs to be fixed.
        if (claim instanceof HibernateProxy) {
            claim = (Claim) ((HibernateProxy) claim)
            .getHibernateLazyInitializer().getImplementation();
        }
        claim.getServiceInformation().getServiceDetail();
        processVariables.setVariable("claim", claim);
        if(claim.getState().equals(ClaimState.ACCEPTED_AND_CLOSED)){
        	   processVariables.setVariable("isClaimDenied", Boolean.FALSE);
        }else if(claim.getState().equals(ClaimState.DENIED_AND_CLOSED)){
        	   processVariables.setVariable("isClaimDenied", Boolean.TRUE);
        }
        processVariables.setVariable("isPartCorrected","false");
        if (logger.isInfoEnabled()) {
            logger.info("Claim class " + claim.getClass());
        }
        return processVariables;
    }

    public void performActionForPartsShippedNotReceieved() {
        securityHelper.populateSystemUser();
        Map<String, RejectionReason> defaultRejectionReasonMap = partReturnProcessingService.getDefaultRejectionReason(null);
        Map<String, List<Object>> valuesForAllBUs = configParamService.getValuesForAllBUs(ConfigName.ACTION_FOR_PARTSSHIIPED_NOTRECEIVED.getName());
        Map<String, List<Object>> daysForActingOnClaim = configParamService.getValuesForAllBUs(ConfigName.DAYS_FOR_PARTSSHIIPED_NOTRECEIVED.getName());
        if (defaultRejectionReasonMap == null) {
            logger.error("performActionForPartsShippedNotReceieved API: defaultRejectionReasonMap is null and hence claims are not denied");
        }
        if (valuesForAllBUs == null) {
            logger.error("performActionForPartsShippedNotReceieved API: BU COnfig Param for  ACTION_FOR_PARTSSHIIPED_NOTRECEIVED is null and hence claims are not denied");
        }
        boolean isNotFirstElement = Boolean.FALSE;
		StringBuffer buConfigValueConcatString = new StringBuffer();
		for (String key : daysForActingOnClaim.keySet()) {
			if (isNotFirstElement) {
				buConfigValueConcatString.append(" or ");
			}
			buConfigValueConcatString.append(" (claim.businessUnitInfo = '");
			buConfigValueConcatString.append(key);
			buConfigValueConcatString.append("' and taskInstance.create + (");
			buConfigValueConcatString.append(daysForActingOnClaim.get(
					key).get(0));
			buConfigValueConcatString.append(") <= sysdate) ");
			isNotFirstElement = Boolean.TRUE;
		}
            List<Claim> claims = claimService.findClaimsWithPartsShipped(buConfigValueConcatString.toString());
            if (claims != null && !claims.isEmpty()){
                for (Claim claim : claims) {
                    String buname = claim.getBusinessUnitInfo().getName();
                    Object buValue = null;
                    List<Object> buConfigParamValues = valuesForAllBUs.get(buname);
                    if (buConfigParamValues != null && buConfigParamValues.size() > 0) {
                        buValue = buConfigParamValues.get(0);
                    }
                    if (buValue == null) {
                        continue;
                    }
                    boolean toBeDenied = "Deny Claim".equals(buValue.toString());
                    boolean moveToShippedNotReceived = "MoveToShippedNotReceivedInbox".equals(buValue.toString());
                    List<TaskInstance> openTasks = workListItemService.findAllClaimSubmissionOpenTasksForClaim(claim.getId());
                    for (TaskInstance openTask : openTasks) {
                        if (!claim.getPrtShpNtrcvd()) {
                            try {
                                claim.setPrtShpNtrcvd(Boolean.TRUE);
                                if (toBeDenied) {
                                    claim.setInternalComment("Claim was denied due to failure to return the part(s) within predefined time limit.");
                                    claim.setExternalComment("Claim was denied due to failure to return the part(s) within predefined time limit.");
                                    List<RejectionReason> tempRejectionList = new ArrayList<RejectionReason>();
                                    tempRejectionList.add(defaultRejectionReasonMap.get(buname));
                                    claim.setRejectionReasons(tempRejectionList);
                                    workListItemService.endTaskWithTransition(openTask, "Deny");
                                } else if(moveToShippedNotReceived){
                                    workListItemService.endTaskWithTransition(openTask, "MoveToShippedNotReceivedInbox");
                                } else {
                                	claim.setPrtShpNtrcvd(Boolean.FALSE);
                                }
                            }
                            catch (Exception e) {
                                logger.error("Error from performActionForPartsShippedNotReceieved API ", e);
                                claim.setPrtShpNtrcvd(Boolean.FALSE);
                            }
                        }
                    }
                }
            }
    }

    public void reopenClaim(Claim claimToBeReopened) {
        claimToBeReopened.setInternalComment("Claim re-opened to adjust minimum labor.");
        claimToBeReopened.setExternalComment("Claim re-opened to adjust minimum labor.");
        try {
            claimService.updatePaymentInformation(claimToBeReopened);
        } catch (PaymentCalculationException e) {
            logger.debug("Error Updating Payment for Claim :" + claimToBeReopened.getClaimNumber(), e);
        }
        claimToBeReopened.setState(ClaimState.REOPENED);
        claimToBeReopened.setState(ClaimState.ACCEPTED);
    }
    
    public void updateBOMOnClaimAcceptance() {
        /**
         * Get all claims that have update_bom flag = true
         */
            securityHelper.populateSystemUser();
            
            List<Long> claimIds = this.claimService.findClaimsForBOMUpdation();
            if (claimIds != null && !claimIds.isEmpty()) {
                for (final Long claimId : claimIds) {
                    try{
                        claimService.updateBOMPartOnPartOffCoverage(claimId, null);
                    }catch(Exception e){
                        logger.error("Error in updating BOM for claim id : " + claimId, e);
                    }
                }
            }
    }

    public ProcessInstance loadClaimProcess(Long processId) {
        return processService.findProcess(processId);
    }

    public void stopClaimProcessing(Claim claim) {
        workListItemService.cancelAllOpenTasksForClaim(claim.getId());
        claimService.deactivateClaim(claim);
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
}

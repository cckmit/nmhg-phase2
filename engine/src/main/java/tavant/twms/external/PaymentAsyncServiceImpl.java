package tavant.twms.external;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.RecoveryPayment;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.process.ClaimProcessService;

import java.util.ArrayList;
import java.util.List;

public class PaymentAsyncServiceImpl implements PaymentAsyncService {

	private static final Logger logger = Logger
			.getLogger(PaymentAsyncServiceImpl.class);

	WorkListItemService workListItemService;

	ClaimService claimService;

    ClaimProcessService claimProcessService;
	
	RecoveryClaimService recoveryClaimService;
	
	CampaignService campaignService;

	IntegrationBridge integrationBridge;

	boolean isExternalServiceEnabled;

	boolean isSupplierDebitSubmissionEnabled;

	private SecurityHelper securityHelper;
	
	private static String CR = "CR";
		
	private static String DR = "DR";

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

	public void startAsyncPayment(Claim claim) {
		try {

			if (this.isExternalServiceEnabled) {
				this.integrationBridge.sendClaim(claim);
			}
//			claim.setState(ClaimState.PENDING_PAYMENT_RESPONSE);
		} catch (RuntimeException ex) {
			logger.error("Claim # " + claim.getClaimNumber()
					+ " could not be sent.", ex);
			throw ex;
		}
	}

	public void startSupplierRecoveryAsyncPayment(RecoveryClaim recoveryClaim) {
		try {

			if (isSupplierDebitSubmissionEnabled) {
				integrationBridge.sendRecoveryClaim(recoveryClaim);
			}

			if(recoveryClaim.getRecoveryClaimState().equals(RecoveryClaimState.READY_FOR_DEBIT)){
				recoveryClaim.setRecoveryClaimState(RecoveryClaimState.WAIT_FOR_DEBIT);
			}else if(recoveryClaim.getRecoveryClaimState().equals(RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED)){
				recoveryClaim.setRecoveryClaimState(RecoveryClaimState.WAIT_FOR_NO_RESPONSE_AUTO_DEBIT);
			}else if(recoveryClaim.getRecoveryClaimState().equals(RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED)){
				recoveryClaim.setRecoveryClaimState(RecoveryClaimState.WAIT_FOR_DISPUTED_AUTO_DEBIT);
			}

		} catch (RuntimeException ex) {
			logger.error("RecoveryClaim Debit For Claim# " + recoveryClaim.getClaim().getClaimNumber()
					+ " could not be sent.", ex);
			throw ex;
		}
	}

	public void syncCreditMemo(CreditMemo creditMemo) {
	        if(logger.isDebugEnabled())
	        {
        		logger.debug("Received credit memo # "
        				+ creditMemo.getCreditMemoNumber() + " for claim # "
        				+ creditMemo.getClaimNumber());
	        }

	    securityHelper.populateSystemUser();
	    
	    if (creditMemo.getRecoveryClaim() == null) {
			Claim claim = this.claimService.findClaimByNumber(creditMemo
					.getClaimNumber());
            if (claim != null) {
                if (ClaimState.PENDING_PAYMENT_RESPONSE.equals(claim.getState())
                        || ClaimState.PENDING_PAYMENT_SUBMISSION.equals(claim.getState())) {
                    syncPaymentForClaim(claim, creditMemo);
                }else {
                    throw new RuntimeException("CN005:"+ claim.getState());
                }               
            } else {
                throw new RuntimeException("CN001:");
            }
		} else {
			RecoveryClaim recoveryClaim = this.recoveryClaimService
					.findRecoveryClaim(creditMemo.getRecoveryClaim().getId());
			if(Boolean.TRUE.equals(recoveryClaim.getContract().getOfflineDebitEnabled())){
				syncOfflineDebitDetailsForRecClaim(recoveryClaim, creditMemo);
			}else{
				syncPaymentForRecClaim(recoveryClaim, creditMemo);
			}
		}
	    
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void syncPaymentForClaim(Claim claim, CreditMemo creditMemo) {
		TaskInstance taskInstance = workListItemService.findTaskForClaimWithTaskName(claim.getId(),
						                                                WorkflowConstants.PENDING_PAYMENT_RESPONSE);
        claim.getPayment().addActiveCreditMemo(creditMemo);
        if (claim.getClaimDenied()) {
            claim.setState(ClaimState.DENIED_AND_CLOSED);
        } else {
            claim.setState(ClaimState.ACCEPTED_AND_CLOSED);
            updateCampaignNotificationStateToComplete(claim);
        }
        takeTransition(taskInstance, "goToIsReopenNeeded");
	}

    private void updateCampaignNotificationStateToComplete(Claim claim) {
		if(claim.getCampaign()!=null){
			List<InventoryItem> items = new ArrayList<InventoryItem>();
			for (ClaimedItem claimedItem : claim.getClaimedItems()) {
				items.add(claimedItem.getItemReference()
						.getReferredInventoryItem());
			}
			campaignService.updateCampaignNotifications(items, claim,
					claim.getCampaign(),CampaignNotification.COMPLETE);
		}
		
	}

	private void takeTransition(TaskInstance taskInstance, String takenTransition) {
        List transitions = taskInstance.getAvailableTransitions();
        Assert.notEmpty(transitions, "No Transitions can be taken from ["+ taskInstance + "]");
        if(logger.isDebugEnabled())
        {
            logger.debug("Available transitions are [" + transitions + "]");
        }
        boolean transitionAvailable = checkIfTransitionIsAvailable(transitions, takenTransition);
        Assert.isTrue(transitionAvailable, "Transition [" + takenTransition + "] is not one of the available transitions");
		if(logger.isInfoEnabled()) {
        		logger.info("Taking the transition [" + takenTransition + "] on task instance [" + taskInstance + "]");
		}
        workListItemService.endTaskWithTransition(taskInstance, takenTransition);
    }

    private boolean checkIfTransitionIsAvailable(List transitions, String transition) {
        for (Object t : transitions) {
            if (transition.equals(((Transition) t).getName())) {
                return true;
            }
        }
        return false;
    }

    private void syncPaymentForRecClaim(RecoveryClaim recClaim,
			CreditMemo creditMemo) {
		TaskInstance taskInstance = this.workListItemService
				.findTaskForRecClaimWithTaskName(recClaim.getId(),
						WorkflowConstants.WAIT_FOR_DEBIT);
		RecoveryPayment recoveryPayment = recClaim.getRecoveryPayment();
		recoveryPayment.addActiveCreditMemo(creditMemo);
		recoveryPayment.setPreviousPaidAmount(recoveryPayment.getPreviousPaidAmount());
		workListItemService.endTaskWithTransition(taskInstance, "GoToCheckDebitState");
		List<RecoveryClaimAudit> recoveryClaimAudits = recClaim.getRecoveryClaimAudits();
		int auditSize = recoveryClaimAudits.size();
		Long auditId = recoveryClaimAudits.get(auditSize - 1).getId();
		recoveryClaimService.createRecClaimPaymentAudits(recClaim, Clock.now(), auditId);
	}

	private void syncOfflineDebitDetailsForRecClaim(RecoveryClaim recClaim,
			CreditMemo creditMemo) {
		TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(recClaim.getId(), "Ready For Debit");
		RecoveryPayment recoveryPayment = recClaim.getRecoveryPayment();
		recoveryPayment.addActiveCreditMemo(creditMemo);
		recoveryPayment.setPreviousPaidAmount(recoveryPayment.getPreviousPaidAmount());
		workListItemService.endTaskWithTransition(taskInstance, "goToCheckOfflineDebitState");
		List<RecoveryClaimAudit> recoveryClaimAudits = recClaim.getRecoveryClaimAudits();
		int auditSize = recoveryClaimAudits.size();
		Long auditId = recoveryClaimAudits.get(auditSize - 1).getId();
		recoveryClaimService.createRecClaimPaymentAudits(recClaim, Clock.now(), auditId);
	}

	public void startCreditMemoPayment(Claim claim) {
		createDummyCreditMemo(claim);
	}

	private CreditMemo createDummyCreditMemo(Claim claim) {
        CreditMemo creditMemo = new CreditMemo();
        CalendarDate today = Clock.today();
        creditMemo.setClaimNumber(claim.getClaimNumber());
        creditMemo.setCreditMemoDate(today);
		int memoAudit = claim.getClaimAudits().size()+1;
		creditMemo.setCreditMemoNumber(claim.getWorkOrderNumber()+"_"+memoAudit);
        Money actualAllocatedAmt = claim.getPayment().getCreditMemoAmount();
        creditMemo.setTaxAmount(actualAllocatedAmt.times(0));
        creditMemo.setPaidAmount(actualAllocatedAmt);
        creditMemo.setPaidAmountErpCurrency(actualAllocatedAmt);
        if (actualAllocatedAmt != null && actualAllocatedAmt.isNegative()) {
			creditMemo.setCrDrFlag(CR);
		} else {
			creditMemo.setCrDrFlag(DR);
		}
        claim.getPayment().addActiveCreditMemo(creditMemo);
        if (claim.getClaimDenied()) {
            claim.setState(ClaimState.DENIED_AND_CLOSED);
        } else {
            claim.setState(ClaimState.ACCEPTED_AND_CLOSED);
        }        
        return creditMemo;
    }

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setExternalServiceEnabled(boolean isExternalServiceEnabled) {
		this.isExternalServiceEnabled = isExternalServiceEnabled;
	}


	public void setSupplierDebitSubmissionEnabled(
			boolean isSupplierDebitSubmissionEnabled) {
		this.isSupplierDebitSubmissionEnabled = isSupplierDebitSubmissionEnabled;
	}

	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

    public void setClaimProcessService(ClaimProcessService claimProcessService) {
        this.claimProcessService = claimProcessService;
    }
    
	public CampaignService getCampaignService() {
		return campaignService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}
}
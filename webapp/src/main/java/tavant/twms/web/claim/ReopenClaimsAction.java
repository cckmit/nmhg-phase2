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
package tavant.twms.web.claim;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import java.math.BigDecimal;
import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.classic.Validatable;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONArray;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignStatus;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.*;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.*;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibility;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibilityService;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnAudit;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.web.admin.campaign.CampaignStatistics;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class ReopenClaimsAction extends ClaimsAction implements
		Preparable, Validatable {

	private static final Logger logger = Logger.getLogger(ClaimsAction.class);

	private String id;
	
	private Claim claim;

	private String processorTakenTransition;

	private ValidationResults messages;

	private ClaimProcessService claimProcessService;

	private Boolean reopenRecoveryClaim;

	private List<Contract> contracts = new ArrayList<Contract>();

	private List<String> mandatedComments = new ArrayList<String>();

	private Campaign campaign;

    private List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalledList = new ArrayList<HussmanPartsReplacedInstalled>();

    private ReplacedInstalledPartsService replacedInstalledPartsService;

    private CostCategoryRepository costCategoryRepository;
    
    private List<OEMPartReplaced> initialReplacedParts = new ArrayList<OEMPartReplaced>();
  
    private List<OEMPartReplaced> initialOEMReplacedParts = new ArrayList<OEMPartReplaced>();  
    
    private Map<Long, String> technicians = new HashMap<Long, String>();
    
    private List<String> policyCodes = new ArrayList<String>();

	

	private AdditionalLaborEligibilityService additionalLaborService;

	private List<String> reasonsList = new ArrayList<String>();
	
	private String claimState;

	public static final String SUPPLIER_RECOVERY = "SupplierRecovery";	
	
	public String getClaimState() {
		return claimState;
	}

	public void setClaimState(String claimState) {
		this.claimState = claimState;
	}

	public List<String> getReasonsList() {
		return reasonsList;
	}

	public void setReasonsList(List<String> reasonsList) {
		this.reasonsList = reasonsList;
	}

	public void setAdditionalLaborService(
			AdditionalLaborEligibilityService additionalLaborService) {
		this.additionalLaborService = additionalLaborService;
	}

	public String submit() {
		return processClaim();
	}
	
	public String notifyProcessorIfDealerShippedPart() {
		JSONArray details=new JSONArray();
		try {
			int daysAfterPartShippedForNotification = Integer
					.parseInt(getConfigParamService()
							.getStringValue(
									ConfigName.DAYS_FOR_REOPEN_CLAIM_NOIFICATION_AFTER_PART_SHIPPED
											.getName()));
			boolean isDealerShippedPart = false;
			boolean isPartReturnExceedsDays =false;
			Set<OEMPartReplaced> removedParts = partReturnService.getAllReplacedParts(claim);
			for(OEMPartReplaced eachRemovedPart:removedParts){
				for(PartReturnAudit eachAudit:eachRemovedPart.getPartReturnAudits()){
					if(eachAudit.getPrStatus().equalsIgnoreCase("Part Shipped") && eachAudit.getD().getLastUpdatedBy() !=null 
							&& eachAudit.getD().getLastUpdatedBy().hasRole(Role.DEALER)){
						isDealerShippedPart = true;
						if(eachAudit.getD().getUpdatedOn()!=null && Clock.today().isAfter(
								 eachAudit.getD().getUpdatedOn()
								.plusDays(daysAfterPartShippedForNotification))){ //part shipped date is after xxxxxxx days of today
							isPartReturnExceedsDays = true;
							break;
						}
					}
				}
			}
			
			boolean isClaimStateAcceptedClosed = claim.getState().getState()
					.equalsIgnoreCase(ClaimState.ACCEPTED_AND_CLOSED.getState()) ? true
					: false;

		
			if (isClaimStateAcceptedClosed
					&& isDealerShippedPart
					&& daysAfterPartShippedForNotification > 0
					&& isPartReturnExceedsDays) {
				details.put(true);
				jsonString = details.toString();
				return "notifyAdmin";
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO Auto-generated catch block
		}
		return "notifyAdmin";
	}

	public String reopen() {
		populateClaim();
		if(this.claim.getState() == ClaimState.REOPENED) {
			addActionError("error.common.simultaneousAction");
			return "alreadyReopened";
		}
		//TODO
		/*setPartForCrossReference(claim);*/
		setConfiguredCostCategories(claim);
			claim.setCreditDate(null);
		this.claim.reopen(getReopenRecoveryClaim());
		this.claimProcessService.startClaimProcessingForReopenClaims(this.claim,WorkflowConstants.REOPEN);
		loadTaskWithTaskName("Processor Review");
		this.claimService.updateOEMPartInformation(claim, getInitialReplacedParts());
        //Don't trigger it now. It will happen when processor takes any action on it
        //this.partReturnProcessingService.startPartReturnProcessForAllParts(claim);
        setQtyForUIView(claim);
        this.preparePartReturn(claim);
        setTotalLaborHoursForUiView(claim);
        if(claim.getServiceInformation().getServiceDetail().getLaborPerformed()!=null){
			for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()){
				if(labor.getServiceProcedure()!=null && labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)){
					setRoundUpLaborDetail(labor);
				}
			}
		}
        if(claim != null && this.claim.getLatestRecoveryClaim() != null && this.claim.getRecoveryClaims().size() > 0 ){
			User recoveryClaimAssignee = this.workListService
					.getCurrentAssigneeForRecClaim(this.claim
							.getLatestRecoveryClaim().getId());
			if (recoveryClaimAssignee != null
					&& recoveryClaimAssignee.getName() != null) {
				this.claim
						.getLatestRecoveryClaim()
						.setCurrentAssignee(
								getDisplayNameForRecoveryClaimAssignedTo(recoveryClaimAssignee));
			}
		}
        if(getLoggedInUser().hasRole("processor"))
        {
        	populateApplicablePolicyCodes(this.claim);
        			
        }
        List<InventoryItem> items = new ArrayList<InventoryItem>();
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			items.add(claimedItem.getItemReference()
					.getReferredInventoryItem());
		}
        if(claim!=null && claim.getCampaign()!=null){
        	campaignService.updateCampaignNotifications(items, claim,
					claim.getCampaign(),CampaignNotification.INPROCESS);
        }
        
        claim.setStateMandate(claimSubmissionUtil.getStateMandate(claim));
        claimSubmissionUtil.getDeductableAmount(claim);
		if(getReopenRecoveryClaim()){
			TaskInstance taskInstance = workListItemService
					.findTaskForRecClaimWithTaskName(this.claim.getLatestRecoveryClaim().getId(),
							WorkflowConstants.CLOSED);
			this.claim.getLatestRecoveryClaim().setLoggedInUser(getLoggedInUser());
			if (taskInstance != null) {
				workListItemService.endTaskWithTransition(taskInstance,
						"Reopen");
			}
		}
		return SUCCESS;
	}
	
	
	private void populateApplicablePolicyCodes(Claim claimDetail) {
		if (!claimDetail.isOfType(ClaimType.CAMPAIGN) && !claimDetail.isNcr() && !claimDetail.isNcrWith30Days()) {
			List<String> codes = claimSubmissionUtil
					.fetchApplicablePolicyCodesForClaim(claimDetail);
			if (codes != null && codes.size() > 0) {
				this.policyCodes = codes;
			}
			this.policyCodes.add(POLICY);
		}
		if(!claimDetail.isOfType(ClaimType.CAMPAIGN) && claimDetail.getClaimedItems() != null && claimDetail.getClaimedItems().size()>0){
			ClaimedItem claimedItem = claimDetail.getClaimedItems().get(0);
			if(null != claimedItem.getItemReference() && claimedItem.getItemReference().isSerialized()){
				InventoryItem referredInventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
				if(null != referredInventoryItem)				{
					if(InventoryType.STOCK.getType().equalsIgnoreCase(referredInventoryItem.getType().getType())
							|| (null !=referredInventoryItem.getDeliveryDate() && claimDetail.getRepairDate().isBefore(referredInventoryItem.getDeliveryDate().nextDay()))){
						this.policyCodes.add(STOCK);
					}				}
			}
		}
	}

	private String getDisplayNameForRecoveryClaimAssignedTo(User recoveryClaimAssignee){
		StringBuffer displayName = new StringBuffer();
		displayName.append(recoveryClaimAssignee.getFirstName());
		displayName.append(" ");
		displayName.append(recoveryClaimAssignee.getLastName());
		displayName.append(" (");
		displayName.append(recoveryClaimAssignee.getName());
		displayName.append(')');
		return displayName.toString();
	}
	
	public String transferClaimFromSearchResult() {
		List<String> taskNames = new ArrayList<String>();
		taskNames.add(ClaimState.PROCESSOR_REVIEW.getState());
		taskNames.add(ClaimState.REJECTED_PART_RETURN.getState());
		taskNames.add(ClaimState.REOPENED.getState());
		taskNames.add(ClaimState.ON_HOLD.getState());
		taskNames.add(ClaimState.ON_HOLD_FOR_PART_RETURN.getState());
		taskNames.add(ClaimState.REPLIES.getState());
		taskNames.add(ClaimState.TRANSFERRED.getState());
		TaskInstance taskInstance = this.workListItemService
				.findTaskForClaimWithTaskNames(this.claim.getId(), taskNames);

		/*if (taskInstance.getActorId().equals(getLoggedInUser().getName())) {
			addActionError("transfer.error.tome", this.claim.getClaimNumber());
			return SUCCESS;
		}*/
		this.workListItemService.endTaskWithReassignment(taskInstance,
					"Transfer", getLoggedInUser().getName());
			addActionMessage("transfer.success.tome", this.claim.getClaimNumber());
			return SUCCESS;

	}

	public String transferClaimtoDSM() {
		List<String> taskNames = new ArrayList<String>();
		taskNames.add(ClaimState.SERVICE_MANAGER_REVIEW.getState());
		TaskInstance taskInstance = this.workListItemService
				.findTaskForClaimWithTaskNames(this.claim.getId(), taskNames);
		if (taskInstance.getActorId().equals(this.task.getTransferTo())) {
			addActionError("transfer.error.tome", this.claim.getClaimNumber());
		} else if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equalsIgnoreCase(taskInstance
				.getName())
				&& !StringUtils.hasText(this.task.getTransferTo())) {
			addActionMessage("transfer.validation.message", this.claim
					.getClaimNumber());
		} else if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equalsIgnoreCase(taskInstance
				.getName())) {
			this.workListItemService.endTaskWithReassignment(taskInstance,
					"Transfer", this.task.getTransferTo());
			addActionMessage("transfer.success", this.claim.getClaimNumber());
		}
		return SUCCESS;
	}

	public String resubmissionDetail() {
		populateClaim();
		setQtyForUIView(claim);
		setTotalLaborHoursForUiView(claim);
		this.claim.setAppealed(Boolean.TRUE);
		
		if(null != claim.getRejectionReasons() && claim.getRejectionReasons().size()>0){
			for(RejectionReason rejectionReason : claim.getRejectionReasons()){
				reasonsList.add(rejectionReason.getDescription());				
			}			
			claimState="Deny Reasons";
		}else if(null != claim.getRequestInfoFromUser() && claim.getRequestInfoFromUser().size()>0){
			for(RequestInfoFromUser requestInfoFromUser : claim.getRequestInfoFromUser()){
				reasonsList.add(requestInfoFromUser.getDescription());
			}
			claimState="Requesting Information For";
		}else if(null != claim.getPutOnHoldReasons() && claim.getPutOnHoldReasons().size()>0){
			for(PutOnHoldReason putOnHoldReason : claim.getPutOnHoldReasons()){
				reasonsList.add(putOnHoldReason.getDescription());
			}
			claimState="Put On Hold Reasons";
		}
		//Deductible and state mandate changes		
		Money deductible=claimSubmissionUtil.getDeductableAmount(claim);		
		if(claim.getPayment()!=null)
			claim.getPayment().setDeductibleAmount(deductible);		
		claim.setStateMandate(claimSubmissionUtil.getStateMandate(claim));
		
		return SUCCESS;
	}

	private void populateClaim() {
		Assert.hasText(this.claimId, "Claim Id not set !!");
		this.claim = this.claimService.findClaim(new Long(this.claimId));
		claim.setCreditDate(null);//nullifying the previously populated credit dates
	}

	public String resubmit() throws PaymentCalculationException {
		populateClaim();
		if(this.claim.getState() == ClaimState.APPEALED) {
			addActionError("error.common.simultaneousAction");
			return "alreadyAppealed";
		}
		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledParts(claim);
		}
		claim.setInternalComment(null);
		//State Mandate Changes
		Payment payment=this.claim.getPayment();
		if(this.claim.getStateMandate()!=null)
		{
			if(payment!=null)
				payment.setStateMandateActive(true);
		}
		else
		{
			if(payment!=null)
				payment.setStateMandateActive(false);
		}
		
		 claimSubmissionUtil.computePayment(this.claim, null);
			if (claim.getActiveClaimAudit().getIsPriceFetchDown()&&isErrorMessageShowOnEPODown()) {
				addActionError("label.common.epo.system.down");
				return INPUT;
			}
			else if (displayWarningIfPartPricesDifferent()
					&& isLoggedInUserAnInternalUser()&&(claim.getPaymentForDealerAudit()!=null&&
					claim.getPayment()!=null)){
				LineItemGroup oemPart=claim.getPayment().getLineItemGroup(Section.OEM_PARTS);
				Money acceptedAmount=null;
				Money requestedAmount=claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS).getGroupTotal();
				if(claim.getPayment().getTotalAcceptStateMdtChkbox()!=null&&claim.getPayment().getTotalAcceptStateMdtChkbox())
				{
					acceptedAmount=	oemPart.getGroupTotalStateMandateAmount();
				}
				else				
				{
					acceptedAmount=	oemPart.getAcceptedTotal();
				}	
				if(!requestedAmount.equals(acceptedAmount))
				{
				addActionWarning(
						"label.common.warning.differentPrices.pricefetch",
						"label.common.warning.differentPrices.pricefetch",
						new String[]{requestedAmount.toString(),
								acceptedAmount.toString()});
				}
			}
			else if(claim.getActiveClaimAudit().getIsPriceFetchReturnZero() != null
					&& claim.getActiveClaimAudit().getIsPriceFetchReturnZero() && claim.getActiveClaimAudit().isPriceZero()){
				String[] zeroPricedPars=claim.getActiveClaimAudit().getPriceFetchErrorMessage().split("#");
				addActionWarning("label.common.epo.system.has.return.zero.values.resubmit",zeroPricedPars[1]);
			}
		//this.claim.getPayment().setPreviousPaidAmount(
				//this.claim.getPayment().getTotalAmount());
		this.claim.setAppealed(Boolean.TRUE);
		this.claim.setRejectionReasons(null);
		this.setRequestInfoFromDealerList(null);
		this.setPutOnHoldReasonList(null);
		this.claim.setAcceptanceReason(null);
        this.claimService.updateOEMPartInformation(this.claim,getInitialReplacedParts());
        
     
        //Don't triger it here, that will be taken care once processor takes any action    Refer: NMHGSLMS-170
       // this.partReturnProcessingService
         //           .startPartReturnProcessForAllParts(claim);
        this.claimProcessService.startClaimProcessingForReopenClaims(this.claim,WorkflowConstants.APPEALS);
        loadTaskWithTaskName(WorkflowConstants.APPEALS);
		// loadTaskWithTaskName("Appeals");
		addActionMessage("message.newClaim.appeal", this.claim.getClaimNumber());

        //End all part return process for dealer
        checkAndEndPartReturnProcessForDealer();
        //change the status during resubmit for FPI claims
        if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(this.claim.getType().getType())){
            List<InventoryItem> items = new ArrayList<InventoryItem>();
            for (ClaimedItem claimedItem : claim.getClaimedItems()) {
                items.add(claimedItem.getItemReference()
                        .getReferredInventoryItem());
            }
            campaignService.updateCampaignNotifications(items, claim,
                            claim.getCampaign(),CampaignNotification.INPROCESS);
        }
		return SUCCESS;
	}

	private String processClaim() {
		StringBuffer messageKey = new StringBuffer(50);
		String takenTransition = this.task.getTakenTransition();
		String formattedTransition = StringUtils.uncapitalize(StringUtils
				.trimAllWhitespace(takenTransition));
		messageKey.append("message.newClaim.").append(formattedTransition)
				.append("Success");
		//TODO
		/*prepareOEMPartCrossRef(this.claim)*/;
		if (!"Delete Draft".equals(this.task.getTakenTransition()) 
				&& isMatchReadApplicable() &&  claim.getClaimedItems().get(0).getItemReference()
				.isSerialized() && 
				 !(ClaimType.CAMPAIGN.getType().equals(claim.getType()
						.getType()))) {
			computeMatchReadScore(claim);
		}		
		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledParts(claim);
		}
		claim.setCreditDate(null);//nullifying previously added credit dates
		if (task.getTakenTransition() != null
				&& "Accept".equalsIgnoreCase(task.getTakenTransition())
				&& claim.getItemReference().isSerialized()) {
			this.claimService.checkAdjustmentForRndUpLaborOnClaim(claim,getRoundUpLaborDetail());
		}	
		setTotalQtyForReplacedParts(claim);
		if(claim.getServiceInformation().getServiceDetail().getLaborPerformed()!=null){
			for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()){
				if(labor.getEmptyAdditionalHours()!=null && labor.getEmptyAdditionalHours()){
					labor.setAdditionalLaborHours(null);
				}
			}
		}
		setReasonsList(claim);
		setTotalLaborHoursForClaim(claim);
		if(task.isPartsClaim())
		{
			if(getConfigParamService()
				.getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName()))
		setPolicyOnClaimedParts(claim);
		}
		else
		{
		setPolicyOnClaimedItems(claim);
		}
		// jbpm is again calling compute payment method and setting payment in claim object
		// for only deny claims due to this last/latest audit values are wrong.
		// Fixing jbpm issue causes regression hence to ensure compute payment
		// will get call only below condition for deny claim
        isComputationRequired(claim);
        if (!("Deny".equalsIgnoreCase(this.task.getTakenTransition())) &&  claim.isCanUpdatePayment()) {
			computePayment(this.claim);
			if(checkForPaymentSystemErrors(this.claim,null,true)){
				return INPUT;	
			}
		}
		if (isProcessorReview()  &&
                null != task.getTakenTransition() && (task.getTakenTransition().equalsIgnoreCase("Accept") || task.getTakenTransition().equalsIgnoreCase("Hold")
                                                      || task.getTakenTransition().equalsIgnoreCase("Transfer"))) {
			this.claimService.updateOEMPartInformation(this.claim, getInitialReplacedParts());
            List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, getInitialReplacedParts());
            if (!removedParts.isEmpty()) {
                partReturnProcessingService.endTasksForParts(removedParts);
                //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                //and prepare due parts inbox too.
                if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                    partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                }
            }
            if(!claim.isNcr()){
                this.partReturnProcessingService
					.startPartReturnProcessForAllParts(this.claim);
            }

            for (OEMPartReplaced removedPart : removedParts) {
                removedPart.setPartReturns(new ArrayList<PartReturn>());
            }
		}
        else if(isProcessorReview()){
            this.claimService.updateOEMPartInformation(this.claim, getInitialReplacedParts());
            List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, getInitialReplacedParts());
            if (!removedParts.isEmpty()) {
                partReturnProcessingService.endTasksForParts(removedParts);
                //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                //and prepare due parts inbox too.
                if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                    partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                }
            }

            for (OEMPartReplaced removedPart : removedParts) {
                removedPart.setPartReturns(new ArrayList<PartReturn>());
            }
        }
		
		//Logic for initiate part return for dealer
        initiatePartReturnToDealer(task);
        
		boolean warning = ClaimState.DRAFT.getState().equalsIgnoreCase(
				claim.getState().getState());
		if (task.getSeekAdviceFrom() != null) {
			this.task.getTask().setVariable("dsmAdvisor",
					task.getSeekAdviceFrom());
		}
		if (task.getSeekReviewFrom() != null) {
			this.task.getTask().setVariable("cpAdvisor",
					task.getSeekReviewFrom());
		}		
		this.taskViewService.submitTaskView(this.task);
		addActionMessage(messageKey.toString(), this.claim.getClaimNumber());
		setWarningRequired(warning);
		List<InventoryItem> items = new ArrayList<InventoryItem>();
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			items.add(claimedItem.getItemReference()
					.getReferredInventoryItem());
		}
        if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(this.task.getClaim().getType().getType())){
            if (claim.getId() != null && claim.getCampaign() != null
                    && !claim.getState().equals(ClaimState.DENIED)
                    && !claim.getState().equals(ClaimState.ACCEPTED)) {
                if(takenTransition.equalsIgnoreCase(TransitionTaken.DENY.getTransitionTaken())){
                    campaignService.updateCampaignNotifications(items, claim,
                            claim.getCampaign(),CampaignNotification.PENDING);
                }else{
                    campaignService.updateCampaignNotifications(items, claim,
                        claim.getCampaign(),CampaignNotification.INPROCESS);
                }
            }
            else if (!claim.getPayment().isPaymentToBeMade()
                    && !claim.getState().equals(ClaimState.DENIED)
                    && claim.getActiveClaimAudit().getState()
                            .equals(ClaimState.ACCEPTED)) {
                campaignService.updateCampaignNotifications(items, claim,
                        claim.getCampaign(),CampaignNotification.COMPLETE);
            }
        }
		//Fix for SLMS-2043
        if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)){
        	PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        	if(partsClaim.getPartInstalled() && 
        			(partsClaim.getCompetitorModelBrand() == null || partsClaim.getCompetitorModelBrand().isEmpty())){
        		if(claim.getHoursInService() != null && claim.getHoursOnTruck() != null){
        			claim.setHoursOnPart(claim.getHoursInService().subtract(claim.getHoursOnTruck()));
        		}
        	}
        }
		return SUCCESS;
	}
	
	private void loadTaskWithTaskName(String taskName) {
		TaskInstance taskInstance = this.workListItemService
				.findTaskForClaimWithTaskName(new Long(getClaimId()), taskName);
		setTask(new TaskView(taskInstance));
		setId(new Long(this.task.getTaskId()).toString());
	}

	/**
	 * Dummy method which is used to show the active policies. Jsp's will
	 * directly call method {@link #getActivePolicies(Claim)}
	 */
	public String showActivePolicies() {
		return SUCCESS;
	}
	
	public String getSelectedJobsJsonString() {
		return getSelectedJobsJSON();
	}

	protected void validateRepairDateForCampaign(Claim claim){
		if(ClaimType.CAMPAIGN.getType().equals(claim.getType().getType())){
			if(claim.getRepairDate() != null)
			{
				CalendarDate campaignActiveFrom = null;
				CalendarDate campaignActiveTill = null;
				if(this.campaign != null)
				{
					campaignActiveFrom = this.campaign.getFromDate();
					campaignActiveTill = this.campaign.getTillDate();
				}
				else if (claim.getCampaign() != null)
				{
					campaignActiveFrom = claim.getCampaign().getFromDate();
					campaignActiveTill = claim.getCampaign().getTillDate();
				}
				if(claim.getRepairDate().isBefore(campaignActiveFrom) ||
						claim.getRepairDate().isAfter(campaignActiveTill) )
				{
					addActionError("error.newClaim.invalidRepairDateForCampaign");
				}
			}
		}
	}
	
	@Override
	public void validate() {
		
		//validateTechnician(claim);
		// to check payment only when not a draft claim. Changes done for issue HUSS-863
		checkPayment(claim);
		validateMandatedComments(this.claim);
		if(this.claim.getLoaScheme() != null && eligibleLOAProcessors.size()==0){
			eligibleLOAProcessors = claim.getLoaScheme().getEligibleLOAProcessorList();
		}
		validateDocumentTypeForAttachment(claim);
		validateAccountabilityCode(claim);
		if (this.task!=null && "Accept".equalsIgnoreCase(this.task.getTakenTransition()) && claim.getLoaScheme() != null && !eligibleLOAProcessors.contains(getLoggedInUser().getName())) {
			addActionError("error.claim.currentUserNotEligibleToAccept");
		}
		if (this.task!=null &&  "Transfer".equalsIgnoreCase(this.task.getTakenTransition()) && claim.getLoaScheme() != null && getNextLOAProcessor()== null) {
			addActionError("error.claim.nextLOAUserNotAvailable");
		}		
		if (!ClaimType.CAMPAIGN.getType().equals(claim.getType().getType()) && this.task!=null && (("Accept".equalsIgnoreCase(this.task.getTakenTransition()) || ("Re-process".equalsIgnoreCase(this.task.getTakenTransition()))) && org.apache.commons.lang.StringUtils.isEmpty((this.task.getClaim().getPolicyCode()))) && !this.task.getClaim().isNcr() && !this.task.getClaim().isNcrWith30Days() && !amerClaimAcceptedEarlier(this.task.getClaim())) {
			addActionError("error.claim.invalidPolicyCode");
		}
		validateActionToBeTaken(claim);
		validateRepairDateForCampaign(claim);
    	if(ClaimType.MACHINE.getType().equals(claim.getType().getType()) 
    			&& !(claim.getClaimedItems().get(0).getItemReference().isSerialized()) 
    			&& claim.getPurchaseDate() == null){
    		addActionError("error.newClaim.purchasedateRequired");
    	}
    	if(ClaimType.PARTS.getType().equals(claim.getType().getType())){ 
    		PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
    		if((!partsClaim.getPartInstalled().booleanValue()) && 
    				(!claim.getPartItemReference().isSerialized() && claim.getPurchaseDate() == null)){
    			addActionError("error.newClaim.purchasedateRequired");
    		}else if(!claim.getPartItemReference().isSerialized() && partsClaim.getPartInstalled().booleanValue() && claim.getInstallationDate() == null){
    			addActionError("error.newClaim.warrantyStartDateRequired");
    		}
    	} 
    	
    	if (claim.getFailureDate() != null && claim.getPurchaseDate() != null
				&& claim.getFailureDate().isBefore(claim.getPurchaseDate())) {
			addActionError("error.newClaim.invalidPurchaseDuration");
		}
        if(isSourceWarehouseToBeCaptured(claim) && 
        	(!claim.getPartItemReference().isSerialized()&& claim.getSourceWarehouse()==null)){ 
            addActionError("error.newClaim.sourceWarehouseRequired");
        }
        if(isSellingEntityToBeCaptured(claim) && 
        		(!claim.getPartItemReference().isSerialized()&& claim.getSellingEntity()==null)){
            addActionError("error.newClaim.sellingEntityRequired");
        }
        
        validateNonOEMPartNumbers();
    }

	private void validateNonOEMPartNumbers() {
		if (claim.getServiceInformation().getServiceDetail()
				.getNonOEMPartsReplaced() != null) {
			for (NonOEMPartReplaced nonOEMPartReplaced : claim
					.getServiceInformation().getServiceDetail()
					.getNonOEMPartsReplaced()) {
				try {
					if (getCatalogService()
							.findItemByItemNumberOwnedByManuf(
									nonOEMPartReplaced.getDescription())
							.getOwnedBy().getId() == 1) {
						addActionError("error.claim.oemPartInNonOemSection",
								nonOEMPartReplaced.getDescription());
					}
				} catch (CatalogException e) {
					logger.error(e);
				}
			}
		}
	}
	
	public boolean checkActionForCP(Claim claim){
		if(isProcessorReview()
				&& (ClaimState.REPLIES.equals(this.task.getClaim().getState()) &&
						"Accept".equalsIgnoreCase(this.task.getTakenTransition()) && claim.getCpReviewed() && (claim
						  .getAcceptanceReasonForCp() == null || (claim
						  .getAcceptanceReasonForCp() != null && !StringUtils
						  .hasText(claim.getAcceptanceReasonForCp()
						  .getDescription()))))){
			if (claim.getPayment() != null	&& !claim.getPayment().getLineItemGroups().isEmpty()) {
				List<LineItemGroup> lineItemGroups = claim.getPayment().getLineItemGroups();
				for (LineItemGroup lineItemGroup : lineItemGroups) {
					if ((lineItemGroup.getPercentageAcceptedForAdditionalInfo(AdditionalPaymentType.ACCEPTED_FOR_CP))
							.doubleValue() >0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private boolean isProcessorAccepting() {
		return (this.task != null && isProcessorReview() && "Accept"
				.equalsIgnoreCase(this.task.getTakenTransition()));
	}

	
	@SuppressWarnings("deprecation")
	public String validateClaim(){
		if (hasActionErrors() || hasFieldErrors()) {
			addActionError("error.newClaim.errorsInSubmit");
			return INPUT;
		}		
		
		if (task!=null && task.getTakenTransition() != null
				&& "Accept".equalsIgnoreCase(task.getTakenTransition())
				&& claim.getItemReference().isSerialized()) {
			this.claimService.checkAdjustmentForRndUpLaborOnClaim(claim,getRoundUpLaborDetail());
		}
		claim.setCreditDate(null);
		setActionErrors(claimSubmissionUtil.validateReplacedInstalledParts(claim,isProcessorReview(), hasActionErrors()));
		setTotalQtyForReplacedParts(claim);
		validateClaimForLaborDetail(claim);
		setTotalLaborHoursForClaim(claim);
		validateForAccountabilityCode(this.claim);		
		claimSubmissionUtil.setPolicyForClaim(claim);
		if(!claim.isFoc()
				&& (ClaimState.DRAFT.getState().equalsIgnoreCase(this.claim.getState().getState())
					|| ClaimState.FORWARDED.getState().equalsIgnoreCase(this.claim.getState().getState())
					|| ClaimState.SERVICE_MANAGER_RESPONSE.getState().equalsIgnoreCase(this.claim.getState().getState()))){
		this.messages = this.ruleAdministrationService
				.executeClaimEntryValidationRules(this.claim);
		setValidationResultAsActionMessage();
		}
		// HUSS-275. Scenario: Claim sent for CP Review.
		//					   Gets Auto replied due to lapse of window period
		//					   Processor should not be able to accept the CP part.
		//				       Can be accepted with 0 CP amount though.
		//					   Any other action can be taken.
		if(isCPAdvisorEnabled()){
			setActionErrors(claimSubmissionUtil.validateForNoCPOnAutoReply(this.claim, task.getTakenTransition()));
		}
		
		if ((this.messages != null && this.messages.hasErrors()) || hasActionErrors()) {
			return INPUT;
		}
		computePayment(this.claim);
		if (isBuConfigAMER() && task.getTakenTransition() != null
				&& task.getTakenTransition().equalsIgnoreCase(
						UserClusterService.ACTION_ACCEPT)
				&& (this.claim.getPayment().getTotalAmount().isNegative() || this.claim
						.getPayment().getTotalAmount().isZero()))
		{
			addActionError("error.payment.negativeAmount");
			return INPUT;
		}
		if(checkForPaymentSystemErrors(this.claim,null,false)){
			return INPUT;	
		}
		validateProcessorApprovalLimit(this.claim);
		if (allowedStateForLOA(this.claim.getState()))
			validateAllowedProcessClaim(this.claim);
		if (hasActionErrors() || hasFieldErrors()) {
			//addActionError("error.newClaim.errorsInSubmit");
			return INPUT;
		}
		
		this.claimService.updateOEMPartInformation(this.claim, null);
		populateProcessorTransition();

		return SUCCESS;
	}

	private void validateForAccountabilityCode(Claim theClaim) {
		if (isProcessorReview()
				&& this.task.getTakenTransition().equalsIgnoreCase("Accept")) {
			AccountabilityCode accountabilityCode = theClaim
					.getAccountabilityCode();
			Boolean supplierPartRecoverable = theClaim.getServiceInformation()
					.isSupplierPartRecoverable();
			if (accountabilityCode == null
					|| accountabilityCode.getCode().equalsIgnoreCase("null")
					|| accountabilityCode.getCode() == null
					|| accountabilityCode.getCode().length() == 0) {
				addActionError("Please select the Review Responsibility for the claim");
			} 
//			else {
//				if (accountabilityCode.getCode().equalsIgnoreCase("SUP")
//						&& (supplierPartRecoverable == null || !supplierPartRecoverable
//								.booleanValue())) {
//					addActionError("Supplier is accountable for the claim. Please mark the claim for supplier recovery.");
//				}
//			}
			if (supplierPartRecoverable != null
					&& supplierPartRecoverable.booleanValue()) {
				if (theClaim.getServiceInformation().getContract() == null) {
					addActionError("The causal part does not have any applicable contract. Please ensure the causal part is covered under a valid contract before marking for recovery");
				}
				else
				{
					if(!ClaimType.CAMPAIGN.getType().equalsIgnoreCase(theClaim.getType().getType())){
						boolean validContract =false;
						SelectedBusinessUnitsHolder.setSelectedBusinessUnit(theClaim.getBusinessUnitInfo().getName());
						List<Contract> applicableContracts = this.contractService
						.findContract(theClaim, theClaim.getServiceInformation().getCausalPart(), true);
	
						for(Contract contract:applicableContracts)
						{
							if(contract.getName().equals(theClaim.getServiceInformation().getContract().getName()))
							{
								validContract=true;
								break;
							}
						}
						if(!validContract)
						{
							addActionError("The causal part does not have any applicable contract. Please ensure the causal part is covered under a valid contract before marking for recovery");
						}
					}
				}

			}

			if (doesClaimContainRejectedPart(theClaim)) {
				addActionWarning("This claim has rejected part returns.");
			}
		}
	}

	private void triggerPartReturnsIfProcessorSubmit(Claim claim) {
		this.partReturnProcessingService
				.startPartReturnProcessForAllParts(claim);
	}

	@SuppressWarnings("unchecked")
	public List<ClaimAudit> getClaimAudits() {
		List result=new ArrayList<ClaimAudit>();
		if ((this.task != null) && (this.task.getClaim() != null)) {
			 result = new ArrayList(this.task.getClaim().getClaimAudits());
		}else if(claim!=null && claim.getClaimAudits()!=null){
			result=new ArrayList(claim.getClaimAudits());
		}
			Collections.reverse(result);
			return result;
	}


	public List<String> getMandatedComments() {
		return this.mandatedComments;
	}

	public void setMandatedComments(List<String> mandatedComments) {
		this.mandatedComments = mandatedComments;
	}

	public Claim getClaim() {
		return this.claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

    public Claim getClaimDetail() {
        return claim;
    }

    public String getJSONifiedAttachmentList() {
		try {
			List<Document> attachments = new ArrayList<Document>();
			if(this.task!=null && this.task.getClaim()!=null){
				attachments = this.task.getClaim().getAttachments();
			}else if(claim!=null){
				attachments = claim.getAttachments();
			}
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}

	public String getProcessorTakenTransition() {
		return this.processorTakenTransition;
	}

	public void setProcessorTakenTransition(String processorTakenTransition) {
		this.processorTakenTransition = processorTakenTransition;
	}

	public ValidationResults getMessages() {
		return this.messages;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public void prepare(){
		List<Country> countries = this.msaService.getCountryList();
		for (Country country : countries) {
			this.countryList.put(country.getCode(), country.getName());
		}
		this.countriesFromMSA = this.msaService.getCountriesFromMSA();
		if (logger.isDebugEnabled()) {
			logger.debug("Preparing ...");
		}

		this.paymentConditions = this.partReturnService.findAllPaymentConditions();
		if (StringUtils.hasText(getClaimId())) {
			this.claim = this.claimService.findClaim(new Long(getClaimId()));
			if (this.claim.getState() == ClaimState.REOPENED) {
				loadTaskWithTaskName("Processor Review");
			}
		} else if (StringUtils.hasText(getId())) {
			this.task = this.taskViewService.getTaskView(Long
					.parseLong(getId()));
			this.claim = this.task.getClaim();
		}

		//added for populating business unit info
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		selectedBusinessUnit  = claim.getBusinessUnitInfo().getName();
		//end of new change
		isThirdPartyUser(claim);
		populateBusinessUnitConfigParameters();
		initialReplacedParts.addAll(getInitialOEMReplacedParts());
        preparePartReturn(this.claim);
        if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledPartsDisplay(claim);
		} else {
			setRowIndex(0);
		}
		if (this.selectedContract != null
				&& this.selectedContract.getId() != null) {
			claim.getServiceInformation().setContract(
					this.contractService.findContract(this.selectedContract
							.getId()));
		}
		displayServicingLocationWithBrand(claim);	
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	public Boolean getReopenRecoveryClaim() {
		return Boolean.TRUE.equals(this.reopenRecoveryClaim);
	}

	public void setReopenRecoveryClaim(Boolean reopenRecoveryClaim) {
		this.reopenRecoveryClaim = reopenRecoveryClaim;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

		        
	public List<HussmanPartsReplacedInstalled> getHussmanPartsReplacedInstalledList() {
		return hussmanPartsReplacedInstalledList;
	}

	public void setHussmanPartsReplacedInstalledList(
			List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalledList) {
		this.hussmanPartsReplacedInstalledList = hussmanPartsReplacedInstalledList;
	}

	public ReplacedInstalledPartsService getReplacedInstalledPartsService() {
		return replacedInstalledPartsService;
	}

	public void setReplacedInstalledPartsService(
			ReplacedInstalledPartsService replacedInstalledPartsService) {
		this.replacedInstalledPartsService = replacedInstalledPartsService;
	}

    //Check and end part return for dealer process.

    public void checkAndEndPartReturnProcessForDealer(){
        //find if the claim has some removed parts
        List<OEMPartReplaced> oemPartsReplaced = this.claim.getServiceInformation().getServiceDetail().getReplacedParts();
        if(oemPartsReplaced.size() > 0){
            this.partReturnProcessingService.endAllDealerRequestPartReturnTaskForClaim(this.claim.getId());
            for(OEMPartReplaced part : oemPartsReplaced){
                //Fix for NUll Pointer while there is no part return information available , re-submit claim
                if(null != part.getPartReturn() &&  returnStatusMap().contains(part.getPartReturn().getStatus())){
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED.getStatus(),part.getPartReturns().size()));
                    this.partReturnService.updatePartStatus(part);
                    getPartReplacedService().updateOEMPartReplaced(part);
                }
             }
        }
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }
    
    public List<String> getPolicyCodes() {
		return policyCodes;
	}

	public void setPolicyCodes(List<String> policyCodes) {
		this.policyCodes = policyCodes;
	}

	public BigDecimal additionalLaborHoursUpdated(BigDecimal additionalLaborHrs) {
		BigDecimal noOfClaimedItems = new BigDecimal(getApprovedClaimedItems(this.claim));
		if (isLoggedInUserADealer()
				&& !ClaimState.DRAFT.getState().equals(
						this.claim.getState().getState())) {
			return additionalLaborHrs.divide(noOfClaimedItems);
		} else {
			return additionalLaborHrs;
		}
	}
	
	public boolean isEligibleForAdditionalLaborDetails(Claim claim) {
        boolean isAdditionalLaborDetailsToDisplay = false;
        List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
        AdditionalLaborEligibility additionalLaborEligibility = additionalLaborService.findAddditionalLabourEligibility();
        if (additionalLaborEligibility != null) {
            serviceProviders = additionalLaborEligibility.getServiceProviders();
        }
        if (claim.getServiceInformation() != null && claim.getServiceInformation().getServiceDetail() != null
                && claim.getServiceInformation().getServiceDetail().getLaborPerformed() != null
                && claim.getServiceInformation().getServiceDetail().getLaborPerformed().size() > 0) {
            for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
                if ((labor.getAdditionalLaborHours() != null && labor.getAdditionalLaborHours().signum() == 1) || (serviceProviders.isEmpty()) ||
                        (!serviceProviders.isEmpty() && serviceProviders.contains(claim.getForDealer()))) {
                    isAdditionalLaborDetailsToDisplay = true;
                    break;
                }
            }
        }
        return isAdditionalLaborDetailsToDisplay;
    }
	
	public List<String> displayJobCodeDescription() {
		List<String> jobCodeDescription = new ArrayList<String>();
		if (task.getClaim().getServiceInformation() != null
				&& task.getClaim().getServiceInformation().getServiceDetail() != null
				&& !task.getClaim().getServiceInformation().getServiceDetail()
						.getLaborPerformed().isEmpty())
			for (LaborDetail laborPerformed : task.getClaim()
					.getServiceInformation().getServiceDetail()
					.getLaborPerformed()) {
				jobCodeDescription.add(laborPerformed.getServiceProcedure()
						.getDefinedFor().getJobCodeDescription());
			}
		return jobCodeDescription;
	}
	
	private ProcessVariables createProcessVariablesForReopenRecClaims(
			RecoveryClaim recoveryClaim) {
		ProcessVariables processVariables = new ProcessVariables();

		if (recoveryClaim instanceof HibernateProxy) {
			recoveryClaim = (RecoveryClaim) ((HibernateProxy) recoveryClaim)
					.getHibernateLazyInitializer().getImplementation();
		}
		processVariables.setVariable("recoveryClaim", recoveryClaim);
		return processVariables;
	}	
	
}
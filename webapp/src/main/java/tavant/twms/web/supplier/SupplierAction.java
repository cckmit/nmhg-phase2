package tavant.twms.web.supplier;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.email.EmailMessageRepository;
import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

import com.domainlanguage.money.Money;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class SupplierAction extends AbstractSupplierActionSupport implements
		Preparable {

	private final Logger logger = Logger.getLogger(SupplierAction.class);

	private Shipment shipment;

	private String transition;

	public static final String FATAL_ERROR = "fatalError";
	
	private final String CANADA_COUNTRY_CODE="CA";

	private WorkListItemService workListItemService;

	private DomainRepository domainRepository;

	private PartReplacedService partReplacedService;

	private Supplier supplier;

	private User user;

	private PartReturnAudit partReturnAudit;

	private RecoveryClaimAudit recoveryClaimAudit;

	private Dealership dealership;

	private DocumentService documentService;

    private EmailMessageRepository emailMessageRepository;

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return this.workListService.getSupplierRecoveryClaimBasedView(criteria);
	}

	// These two fields are only used for the popup window which shows
	// RecoveryAmountDetails
	private Long partId;

	public String preview() {
		fetchClaimView();
		return SUCCESS;
	}

	public String detail() {
		// fetchSuppliers();
		fetchClaimView();
		if(!fetchedRecoveryClaim.getRecoveryClaimInfo().getRecoverableParts().isEmpty() && isBuConfigAMER()){
			for(RecoverablePart recPart :  getRecoveryClaim().getRecoveryClaimInfo().getRecoverableParts()){
				if (null != recPart.getOemPart().getActivePartReturn()
						&& recPart.getOemPart().getActivePartReturn()
								.getStatus()
								.equals(PartReturnStatus.PART_TO_BE_SHIPPED)
						&& recPart.isSupplierReturnNeeded())
					addActionWarning("message.supplier.partShipped");
			}
		}
		return SUCCESS;
	}

	@Override
	protected String getAlias() {
		return "recoveryClaim";
	}

	public String submitPreview() {
		/*
		 * RecoveryClaim recoveryClaim = null; try { recoveryClaim =
		 * getRecoveryClaim(); } catch (Exception e) {
		 * logger.error("Exception EX : " + e); return FATAL_ERROR; }
		 */
		if (getRecoveryClaim().getRecoveryClaimAcceptanceReason() != null
				&& getRecoveryClaim().getRecoveryClaimAcceptanceReason()
						.getCode() == null) {
			getRecoveryClaim().setRecoveryClaimAcceptanceReason(null);
		}
		if (getRecoveryClaim().getRecoveryClaimRejectionReason() != null
				&& getRecoveryClaim().getRecoveryClaimRejectionReason()
						.getCode() == null) {
			getRecoveryClaim().setRecoveryClaimRejectionReason(null);
		}

        //Disable mail notification
        if(getLoggedInUser().hasRole("supplier"))
        {
            List<NotificationMessage> emailMessageList= emailMessageRepository.getAllPendingRecoveryEmailMessage(getRecoveryClaim().getId().toString());
            for(NotificationMessage message : emailMessageList){
                HashMap<String, Object> paramMap = message.getParameterMap();
                String recId = paramMap.get("recClaimId") != null ? paramMap.get("recClaimId").toString() : null;
                if(recId != null && recId.equalsIgnoreCase(getRecoveryClaim().getId().toString())){
                    message.setMessageState(MessageState.CANCELLED);
                    emailMessageRepository.updateEmailMessage(message);
                }
            }
        }

		updateClaimAndPerformTransition();
        if(hasActionErrors()){
            return INPUT;
        }
		
		if (WorkflowConstants.ACCEPT.equals(this.transition)) {
			addActionMessage("message.sra.claimsAccepted");
		} else if(WorkflowConstants.REJECT.equalsIgnoreCase(this.transition)){
			addActionMessage("message.sra.claimsDisputed");
		} else if(WorkflowConstants.ON_HOLD_FOR_PART_RETURN.equalsIgnoreCase(this.transition)){
			addActionMessage("message.sra.claimsMarkedOnHold");
		}
        //CR 1071 auto-confirm part return if supplier taking action from on hold from part return inbox
        //pending action item: Need to end task instances which are on the way, waiting for client approval
        if( getRecoveryClaim().getBusinessUnitInfo().getName().equalsIgnoreCase("AMER")){
            List<TaskInstance> partReturnInstances = new ArrayList<TaskInstance>();
            List<TaskInstance> claimPartReturnInstances = new ArrayList<TaskInstance>();
            partReturnInstances.addAll(supplierRecoveryWorkListDao.getConfirmPartReturnsInstances(getRecoveryClaim()));
            claimPartReturnInstances = supplierRecoveryWorkListDao.getConfirmDealerPartReturnsInstances(getRecoveryClaim().getClaim());
            List<TaskInstance> partReturnShipmentGeneratedInstances = supplierRecoveryWorkListDao.getPartReturnShipmentInstances(getRecoveryClaim().getClaim());
            List<TaskInstance> partRecoveryShipmentGeneratedInstances = supplierRecoveryWorkListDao.getRecoveryShipmentGeneratedInstances(getRecoveryClaim());

            partReturnInstances.addAll(claimPartReturnInstances);
            this.workListItemService.endAllTasksWithTransition(partReturnInstances, "Received");
            this.workListItemService.endAllTasksWithTransition(partReturnShipmentGeneratedInstances, "autoConfirmed");
            this.workListItemService.endAllTasksWithTransition(partRecoveryShipmentGeneratedInstances, "autoConfirmed");

            claimPartReturnInstances.addAll(partReturnShipmentGeneratedInstances);

            List<Long> oemIds = new ArrayList<Long>();
            for(TaskInstance instance : claimPartReturnInstances){
                BasePartReturn partReturn = (BasePartReturn) instance.getVariable("partReturn");
                if(partReturn.getOemPartReplaced() != null){
                    oemIds.add(partReturn.getOemPartReplaced().getId());
                }
            }

            Map<Long, OEMPartReplaced> uniqueValues = new HashMap<Long, OEMPartReplaced>();
            for(TaskInstance instance : claimPartReturnInstances){
                BasePartReturn partReturn = (BasePartReturn) instance.getVariable("partReturn");
                if(partReturn.getOemPartReplaced() != null && uniqueValues.get(partReturn.getOemPartReplaced().getId()) == null){
                    uniqueValues.put(partReturn.getOemPartReplaced().getId(), partReturn.getOemPartReplaced());
                }
            }

            for(OEMPartReplaced part : uniqueValues.values()){
                int count = Collections.frequency(oemIds, part.getId());
                part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus(),count));
                getPartReturnService().updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);
            }

        }

		return SUCCESS;
	}

	/**
	 * This method is directly called in the jsp to get the claim for a
	 * OEMPartReplaced
	 * 
	 * @param oemPartReplaced
	 * @return
	 */
	public Claim getClaimForOEMPartReplaced(OEMPartReplaced oemPartReplaced) {
		return this.partReplacedService
				.getClaimForOEMPartReplaced(oemPartReplaced);
	}

	private void prepareClaimView() {
		if (getId() != null) {
			Assert.hasText(getId(), "Id should not be empty for fetch");
			fetchedRecoveryClaim = getRecoveryClaimService().findRecoveryClaim(
					Long.parseLong(getId()));
			if (fetchedRecoveryClaim != null) {
				SelectedBusinessUnitsHolder
						.setSelectedBusinessUnit(fetchedRecoveryClaim
								.getBusinessUnitInfo().getName());
			}
		}
	}

	private void fetchClaimView() {
		if (fetchedRecoveryClaim == null)
			prepareClaimView();
		setRecoveryClaim(fetchedRecoveryClaim);
		getRecoveryClaim().setLoggedInUser(getLoggedInUser());
		getRecoveryClaim().setCurrentAssignee(
				fetchCurrentClaimAssignee(fetchedRecoveryClaim.getId()));
		setClaim(getRecoveryClaim().getClaim());
	}
	
	public boolean routingRequired(){
		Boolean routingRequired = configParamService.getBooleanValue(ConfigName.SUPPLIER_PART_RETURN_ROUTING_REQUIRED.getName());
		if(routingRequired){
			Currency recClaimCurrency = recoveryClaim.getAcceptedAmount().breachEncapsulationOfCurrency();
			Money thresholdAmount = new Money(
					configParamService.getBigDecimalValue(ConfigName.RECOVERY_CLAIM_THRESHOLD_AMOUNT.getName())
						.setScale(recClaimCurrency.getDefaultFractionDigits()),
						recClaimCurrency);
			Map<Object, Object> countries = configParamService.getKeyValuePairOfObjects(
					ConfigName.RECOVERY_CLAIM_ROUTING_DEALER_COUNTRIES.getName());
			if((countries != null && countries.containsKey(recoveryClaim.getClaim().getForDealer().getAddress().getCountry()))
					|| recoveryClaim.getAcceptedAmount().isLessThan(thresholdAmount) ){
				return true;
			}
		}
		return false;
	}

	private void updateClaimAndPerformTransition() {
		/* No updation reqd in oemparts so commentin Api Rt now */
		getRecoveryClaim().setLoggedInUser(getLoggedInUser());
		// partReplacedService.updateOEMPartReplaced(getClaim().getServiceInformation().getOemPartReplaced());
		if(routingRequired()){
			startRecoveryRoutingFlow();
		} else if(isReturnThroughDealerDirectly()){
            startPartReturnForRecoveryClaim();
        } else {
			startSupplyPartReturnForClaim();
		}
		/*//updating the return location and triggering part return for canadian dealers
		if(isClaimFiledByCanadianDealer(getRecoveryClaim().getClaim().getFiledBy().getId())){
			updatePartReturnLocation();
			startPartReturnProcessForPart();
		}*/

		if (WorkflowConstants.ACCEPT.equals(this.transition)) {
			updatePayment();
		}

		if (!"".equals(this.transition)) {
			TaskInstance taskInstance = this.workListItemService
					.findTaskForRecClaimWithTaskName(
							getRecoveryClaim().getId(), getTaskName());
            if(taskInstance == null){
                addActionError("error.common.simultaneousAction");
            }else{
			this.workListItemService.endTaskWithTransition(taskInstance,
					this.transition);
            }
		}
	}

	private void updatePartReturnLocation() {		
		List<OEMPartReplaced> replacedParts = getRecoveryClaim().getClaim().getServiceInformation().getServiceDetail().getReplacedParts();
		Location location = claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation()); 
		 for (OEMPartReplaced removedPart : replacedParts) {                	
               removedPart.setPartReturns(new ArrayList<PartReturn>());                	           	
               if(removedPart.isReturnDirectlyToSupplier()){  	                	   
            	    removedPart.setReturnDirectlyToSupplier(false);                  		                 	   			           				           	
               }	               
               if(null != removedPart.getPartReturn()){
            	   removedPart.getPartReturn().setReturnLocation(location);    
               }                
               partReplacedService.updateOEMPartReplaced(removedPart);              
            }            			          
	}

	private void startPartReturnProcessForPart() {
		List<RecoverablePart> recoverableParts = getRecoveryClaim().getRecoveryClaimInfo().getRecoverableParts();
		for (RecoverablePart recoverablePart : recoverableParts) {    
        	if(recoverablePart.isSupplierReturnNeeded()){
        		this.partReturnProcessingService.startPartReturnProcessForPart(getRecoveryClaim().getClaim(),recoverablePart.getOemPart());
        	}	           		
        }  
	}
	
	public Shipment getShipment() {
		return this.shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public String getTransition() {
		return this.transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

	public WorkListItemService getWorkListItemService() {
		return this.workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public DomainRepository getDomainRepository() {
		return this.domainRepository;
	}

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	public Long getPartId() {
		return this.partId;
	}

	public void setPartId(Long partId) {
		this.partId = partId;
	}

	public PartReplacedService getPartReplacedService() {
		return this.partReplacedService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public void setPartReplacedService(PartReplacedService partReplacedService) {
		this.partReplacedService = partReplacedService;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<Shipment>(inboxItems, pageSpecification,
				noOfPages);
	}

    private boolean checkReturnInitiatedBySupplier(){
        boolean partSelected = false;
        List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
        for(RecoverablePart part : recoverableParts){
            if(part.isInitiatedBySupplier()) {
                partSelected = true;
                break;
            }
        }
        return partSelected;
    }

	@Override
	public void validate() {
		if (!StringUtils.hasText(this.transition)) {
			addActionError("error.supplier.requiredAction");
		}else{
			if ("Dispute/Reject".equals(this.transition) || "Reject".equals(this.transition)) {
				this.transition = "Reject";
				if(isBuConfigAMER() && checkReturnInitiatedBySupplier()){
					addActionError("error.supplier.PartRequestedOnDispute");
				}
			}else if(this.transition.equalsIgnoreCase(WorkflowConstants.REQUEST_FOR_PART)){
				setTransition(WorkflowConstants.ON_HOLD_FOR_PART_RETURN);
				if(!checkReturnInitiatedBySupplier()){
					addActionError("error.supplier.noPartRequested");
				}
			}
			TaskInstance taskInstance = workListItemService
					.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
							getTaskName());
            if(taskInstance == null){
                addActionError("error.common.simultaneousAction");
            }
			else if(taskInstance != null && !taskInstance.getTask().getTaskNode().hasLeavingTransition(this.transition)){
				addActionError("error.transition.noLeavingTransition");
			}
		}
		if ((WorkflowConstants.REJECT.equals(this.transition) || WorkflowConstants.ACCEPT
				.equals(this.transition))
				&& !StringUtils.hasText(getRecoveryClaim().getExternalComments())) {
			addActionError("error.supplier.requiredReason");
		}
		if (WorkflowConstants.ACCEPT.equals(this.transition)
				&& (getRecoveryClaim().getRecoveryClaimAcceptanceReason() == null || !StringUtils
						.hasText(getRecoveryClaim()
								.getRecoveryClaimAcceptanceReason().getCode()))) {
			addActionError("error.supplier.requiredAcceptanceReason");
		}
		if (WorkflowConstants.REJECT.equals(this.transition)
				&& (getRecoveryClaim().getRecoveryClaimRejectionReason() == null || !StringUtils
						.hasText(getRecoveryClaim()
								.getRecoveryClaimRejectionReason().getCode()))) {
			addActionError("error.supplier.requiredRejectionReason");
		}
		validateDocumentTypeForTheAttachment();

		if (hasActionErrors())
			fetchClaimView();
	}
	
	private boolean partRequestedForReturn(){
		boolean partSelected = false;
		List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
		for(RecoverablePart part : recoverableParts){
			if(part.isSupplierReturnNeeded() && part.getSupplierPartReturns().isEmpty()
					&& part.getOemPart().isReturnDirectlyToSupplier()
					&& part.getOemPart().getPartReturns().isEmpty()) {
				partSelected = true;
				break;
			}
		}
		return partSelected;
	}

	public void prepare() throws Exception {
		prepareClaimView();
	}

	public Supplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String previewDetailView() throws IOException, JSONException {
		if (getId() != null) {
			if (this.logger.isInfoEnabled()) {
				this.logger.info("The supplier id to be viewed is: " + getId());
			}
			if (this.supplier == null) {
				this.supplier = new HibernateCast<Supplier>()
						.cast(this.orgService.getPartyById(new Long(getId())));
			}
			return SUCCESS;
		}
		return displayFieldError("emptysupplierNumber",
				"error.supplier.supplierMandatory");
	}

	public List<String> getAllTransitions(RecoveryClaim recClaim)
			throws JSONException {
		List<String> allTransitions = new ArrayList<String>();
		if(isBuConfigAMER()){
			allTransitions.add("Reject");
		} else {
			allTransitions.add("Dispute/Reject");
		}
		allTransitions.add("Accept");
		if(getTaskName().equals(WorkflowConstants.NEW)){
			allTransitions.add("Request For Part");
		}
		return allTransitions;
	}

	public boolean isDisputeValid() {
		List<RecoveryClaimAudit> recoveryClaimAudits = getRecoveryClaim()
				.getRecoveryClaimAudits();
		int noOfDisputesDone = 0;
		boolean canDispute = true;
		Long maxDisputeAllowed = null;

		// added by arindam for restricting the disputation number
		maxDisputeAllowed = this.configParamService
				.getLongValue(ConfigName.MAXIMUM_DISPUTATION_ALLOWED.getName());
		for (RecoveryClaimAudit recAudit : getRecoveryClaim()
				.getRecoveryClaimAudits()) {
			if (RecoveryClaimState.REJECTED.getState().equals(
					recAudit.getRecoveryClaimState().getState())) {
				noOfDisputesDone++;
			}
		}
		if (noOfDisputesDone >= maxDisputeAllowed) {
			canDispute = false;
		}
		return canDispute;
	}

	public String getSwimlaneRole() {
		Assert.hasText(getId(), "Id should not be empty for fetch");
		TaskInstance taskInstance = workListItemService
				.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
						getTaskName());
		return taskInstance.getSwimlaneInstance().getName();
	}

	private String displayFieldError(String errorId, String errorMessage) {
		addFieldError(errorId, errorMessage);
		return INPUT;
	}

	// CR TKTSA-817
	public String detailForClaim() {
		fetchClaimDetail();
		return SUCCESS;
	}

	private void fetchClaimDetail() {
		if (fetchedRecoveryClaim == null)
			prepareToFetchClaimFromPartNumber();
		setRecoveryClaim(fetchedRecoveryClaim);
		if (getRecoveryClaim() != null) {
			getRecoveryClaim().setLoggedInUser(getLoggedInUser());
			getRecoveryClaim().setCurrentAssignee(
					fetchCurrentClaimAssignee(fetchedRecoveryClaim.getId()));
			setClaim(getRecoveryClaim().getClaim());
			setId(getRecoveryClaim().getId().toString());
		}
	}

	private void prepareToFetchClaimFromPartNumber() {
		if (getId() != null) {
			Assert.hasText(getId(), "Id should not be empty for fetch");
			Map<String, Object> params = new LinkedHashMap<String, Object>(3);
			params.put("partNumber", getId());
			params.put("taskName", this.getTaskName());
			params.put("actorId", getLoggedInUser().getName());
			fetchedRecoveryClaim = getRecoveryClaimService()
					.findRecoveryClaimByPartNumber(params);
			if (fetchedRecoveryClaim != null) {
				SelectedBusinessUnitsHolder
						.setSelectedBusinessUnit(fetchedRecoveryClaim
								.getBusinessUnitInfo().getName());
			}
		}
	}

	public String getSwimlaneRoleForPartView() {
		Assert.hasText(getId(), "Id should not be empty for fetch");
		TaskInstance taskInstance = workListItemService
				.findTaskForRecClaimWithTaskName(
						new Long(fetchedRecoveryClaim.getId()).longValue(),
						getTaskName());
		return taskInstance.getSwimlaneInstance().getName();
	}

	public boolean isDealerComments(PartReturnAudit partReturnAudit) {
		
			if (partReturnAudit.getD().getLastUpdatedBy().hasRole(ROLE_DEALER)) {
				return true;
			}
		return false;
	}
	
	private void validateDocumentTypeForTheAttachment(){
    	List<Document> attachments = getRecoveryClaim().getAttachments(); 
    	attachments.removeAll(Collections.singleton(null));
    	if(attachments != null){
    		for(Document doc : attachments){
        		if(doc.getDocumentType() == null){
        			addActionError("error.selectDocumentType");
        		}
        	}
    	}
    	
    }

	public RecoveryClaimAudit getRecoveryClaimAudit() {
		return recoveryClaimAudit;
	}

	public void setRecoveryClaimAudit(RecoveryClaimAudit recoveryClaimAudit) {
		this.recoveryClaimAudit = recoveryClaimAudit;
	}
	
	public boolean isPartAvailableForReturn(){
		boolean partAvailable = false;
		List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
		for(RecoverablePart part : recoverableParts){
			if(part.isSupplierPartReturnModificationAllowed()){
				partAvailable = true;
				break;
			}
		}
		return partAvailable;
	}

    public void setEmailMessageRepository(EmailMessageRepository emailMessageRepository) {
        this.emailMessageRepository = emailMessageRepository;
    }
}

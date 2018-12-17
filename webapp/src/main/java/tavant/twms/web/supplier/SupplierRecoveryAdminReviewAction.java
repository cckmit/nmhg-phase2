package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONException;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.*;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 *
 * @author dinesh.kk
 *
 */
@SuppressWarnings("serial")
public class SupplierRecoveryAdminReviewAction extends AbstractSupplierActionSupport implements Preparable,ServletRequestAware {

    private static Logger logger = Logger.getLogger(SupplierRecoveryAdminReviewAction.class);

    private WorkListItemService workListItemService;

    private ClaimRepository claimRepository;
    
    public static final String FATAL_ERROR = "fatalError";

    private String transitionTaken;

    private List<Long> partsNotToBeShown = new ArrayList<Long>();

    private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;
    
    private DocumentService documentService;
    
    private List<Boolean> sharedWithSupplier = new ArrayList<Boolean>();
    
    private List<Boolean> sharedWithDealer = new ArrayList<Boolean>();
    
    boolean flag=false;
    
	public List<Boolean> getSharedWithSupplier() {
		return sharedWithSupplier;
	}

	public void setSharedWithSupplier(List<Boolean> sharedWithSupplier) {
		this.sharedWithSupplier = sharedWithSupplier;
	}

	public List<Boolean> getSharedWithDealer() {
		return sharedWithDealer;
	}

	public void setSharedWithDealer(List<Boolean> sharedWithDealer) {
		this.sharedWithDealer =sharedWithDealer;
	}

    private boolean initiateReturnRequestFromSupplier;

    public boolean isInitiateReturnRequestFromSupplier() {
        return initiateReturnRequestFromSupplier;
    }

    public void setInitiateReturnRequestFromSupplier(boolean initiateReturnRequestFromSupplier) {
        this.initiateReturnRequestFromSupplier = initiateReturnRequestFromSupplier;
    }

    @Override
    protected String getAlias() {
    	return "recoveryClaim";
    }

    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return workListService.getSupplierRecoveryClaimBasedView(criteria);
    }

    public void prepare() {
    	prepareClaimView();
    }
    
    public String preview() {
    	fetchClaimView();

    	return SUCCESS;
    }

    public String detail() {
    	fetchClaimView();
    	return SUCCESS;
    }

    private void fetchDetailView() {
        fetchClaimView();
    }
    
    public String getSwimlaneRole(){
    	Assert.hasText(getId(), "Id should not be empty for fetch");
        TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
                getTaskName());
        return taskInstance.getSwimlaneInstance().getName();
    }
    
    private void prepareClaimView() {
    	if(getId() != null && getTaskName() != null) {
	        Assert.hasText(getId(), "Id should not be empty for fetch");
	        TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
	                getTaskName());
            if(taskInstance != null){
	            fetchedRecoveryClaim = (RecoveryClaim) taskInstance.getVariable("recoveryClaim");
            }
	        if(fetchedRecoveryClaim != null){
	        	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(fetchedRecoveryClaim.getBusinessUnitInfo().getName());
	        }
	        else{
		        if(!flag){
                    if(getActionUrl() == null || (getActionUrl() != null && !(getActionUrl().equalsIgnoreCase("supplierRecoveryAdminReview_submit") || getActionUrl().equalsIgnoreCase("notForRecoveryResponse_submit")))){
		                addActionError("error.common.simultaneousAction");
		                flag=true;
                    }
		        }
		      }
	       }
    	}


    private void fetchClaimView() {
    	if(fetchedRecoveryClaim == null)
    		prepareClaimView();
        if(fetchedRecoveryClaim != null){
            setRecoveryClaim(fetchedRecoveryClaim);
            getRecoveryClaim().setLoggedInUser(getLoggedInUser());
            setClaim(getRecoveryClaim().getClaim());
            if (!getRecoveryClaim().getRecoveryClaimInfo()
                    .getRecoverableParts().isEmpty()
                    && isBuConfigAMER()) {
                for (RecoverablePart recPart : getRecoveryClaim()
                        .getRecoveryClaimInfo().getRecoverableParts()) {
                    if (null != recPart.getOemPart().getActivePartReturn()
                            && recPart.getOemPart().getActivePartReturn().getStatus()
                                    .equals(PartReturnStatus.PART_TO_BE_SHIPPED)
                            && recPart.isSupplierReturnNeeded())
                        addActionWarning("message.supplier.partShipped");
                }
            }
        }
    }

    public String submit() {

    TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
            getTaskName());
    if(taskInstance == null){
        addActionError("error.common.simultaneousAction");
        if(getActionUrl() != null && (getActionUrl().equalsIgnoreCase("supplierRecoveryAdminReview_submit") || getActionUrl().equalsIgnoreCase("notForRecoveryResponse_submit"))){
            return SUCCESS;
        }
        return INPUT;
    }

	if (getRecoveryClaim().getRecoveryClaimAcceptanceReason() != null
				&& getRecoveryClaim().getRecoveryClaimAcceptanceReason().getCode() == null) {
			getRecoveryClaim().setRecoveryClaimAcceptanceReason(null);
		}

        if(this.transitionTaken.equalsIgnoreCase("On Hold") || this.transitionTaken.equalsIgnoreCase("Accept") || this.transitionTaken.equalsIgnoreCase("Transfer") || this.transitionTaken.equalsIgnoreCase("Send To Supplier")){
            if(isReturnThroughDealerDirectly()){
                startPartReturnForRecoveryClaim();
            }else{
                startSupplyPartReturnForClaim();
            }

        }

        if(flagFor2ndRecovery){
            getRecoveryClaim().setFlagForAnotherRecovery(true);
            getRecoveryClaim().getD().setActive(false);
            RecoveryInfo info = getRecoveryClaim().getClaim().getRecoveryInfo();
            info.getD().setActive(false);
            info.setWarrantyClaim(null);
            UserComment comment = new UserComment();
            comment.setComment(getText("message.reference.old.warranty.claim",this.getRecoveryClaim().getClaim().getClaimNumber()));
            info.getComments().add(comment);
            this.recoveryInfoService.saveUpdate(info);

            //part return handle should go there
            //dativate the recovery claim -- filter from recovery claim
            //getRecoveryClaimService().updateRecoveryClaim(getRecoveryClaim());
            //deactivate recovery info
            //end task instance
        }

        getRecoveryClaim().setLoggedInUser(getLoggedInUser());
        
        //setEligibilityToShare();

        //if transition taken is can not recover and initiate return from supplier is true then initiate return request for shipped parts.
        if("Cannot Recover".equals(transitionTaken) && initiateReturnRequestFromSupplier){
            //Get the recoverable parts and initiate return.
            boolean isContinue = initiatePartRetReqFromSupplier(this.getRecoveryClaim());
            if(!isContinue){
                return INPUT;
            }
        }
        if (transitionTaken != null) {
            if (WorkflowConstants.ACCEPT.equals(transitionTaken)) {
                updatePayment();
            }

            else if ("Transfer".equals(transitionTaken)) {
				workListItemService.endTaskWithReassignment(taskInstance, transitionTaken, getTransferToUser().getName());
			}else if("Cannot Recover".equalsIgnoreCase(transitionTaken) && flagFor2ndRecovery){
                try {
                    workListItemService.endTaskWithTransition(taskInstance,
                            "toEnd");
                } catch (Exception e) {
                    logger.error("Error while transitioning the recovery claim", e);
                    return INPUT;
                }
            }
            else {
				try {
					workListItemService.endTaskWithTransition(taskInstance, transitionTaken);
				} catch (Exception e) {
					addActionError("error.supplier.noSupplier");
					logger.error("Error occurs while updating recovery claim to transition : " + transitionTaken, e);
					return INPUT;
				}
			}
        }
        if ("On Hold".equals(transitionTaken))
            addActionMessage("message.sra.claimsMarkedOnHold");
        else if ("Send To Supplier".equals(transitionTaken))
            addActionMessage("message.sra.claimsMarkedForRecovery");
        else if ("Cannot Recover".equals(transitionTaken))
            addActionMessage("message.sra.claimsMarkedCannotRecover");
        else if ("Not For Recovery".equals(transitionTaken))
            addActionMessage("error.sra.claimNotForRecovery");
        else if ("Accept".equals(transitionTaken)) {
            addActionMessage("message.sra.claimsAccepted");
        }
        else if ("Transfer".equals(transitionTaken)) {
			addActionMessage("message.sra.claimTransferred");
		}
        return SUCCESS;
    }

    public boolean initiatePartRetReqFromSupplier(RecoveryClaim recoveryClaim){

        List<RecoverablePart> partShippedToSupplier = new ArrayList<RecoverablePart>();
        for(RecoverablePart part: recoveryClaim.getRecoveryClaimInfo().getRecoverableParts()){
            if((null != part.getStatus() && (part.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus()) ||
                    part.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED.getStatus())))
                    || (null != part.getOemPart().getStatus() && part.getOemPart().getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus()))){
                //Initiate return, return location is optional. due date and everything is optional
                //Variables required recovery claim and recoverable part +plus supplier part return
                partShippedToSupplier.add(part);

            }
        }

        Location location = getWarehouseService().getDefaultReturnLocation(getConfigParamService().getStringValue(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName()));
        if(location == null){
            addActionError("error.message.default.location.missing");
            return false;
        }else{
            getPartReturnProcessingService().startRecoveryPartReturnProcessFromSupplier(partShippedToSupplier, recoveryClaim, getWarehouseService().getDefaultReturnLocation(getConfigParamService().getStringValue(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName())));
        }
        return true;
    }

    public WorkListItemService getWorkListItemService() {
        return workListItemService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
        this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
    }

    public ClaimRepository getClaimRepository() {
        return claimRepository;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Long> getPartsNotToBeShown() {
        return partsNotToBeShown;
    }

    public void setPartsNotToBeShown(List<Long> partsNotToBeShown) {
        this.partsNotToBeShown = partsNotToBeShown;
    }

    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

    private RecoveryInfoService recoveryInfoService;

    public RecoveryInfoService getRecoveryInfoService() {
        return recoveryInfoService;
    }

    public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
        this.recoveryInfoService = recoveryInfoService;
    }

    boolean flagFor2ndRecovery = false;

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("supplierRecoveryAdminReview_submit".equals(actionName)||"notForRecoveryResponse_submit".equals(actionName)) {
            validateTransitionsTaken();
            fetchDetailView();
            if (!StringUtils.isNotBlank(getRecoveryClaim().getComments())) {
                addActionError("error.supplier.requiredReason");
            }
            if (WorkflowConstants.ACCEPT.equals(this.transitionTaken) && (getRecoveryClaim().getRecoveryClaimAcceptanceReason() == null || StringUtils.isBlank(getRecoveryClaim().getRecoveryClaimAcceptanceReason().getCode()))) {
                addActionError("error.supplier.requiredAcceptanceReason");
            }
            if(WorkflowConstants.TRANSFER.equals(this.transitionTaken) && this.getTransferToUser()==null)
            	addActionError("error.supplier.requiredTransferToUser");
            if ((WorkflowConstants.SEND_TO_SUPPLIER.equals(this.transitionTaken )|| WorkflowConstants.ACCEPT.equals(this.transitionTaken))
					&& getRecoveryClaim().getSupplierUserForRecoveryClaim() == null) {
				 addActionError("error.supplier.noSupplier");
			}
			if(WorkflowConstants.CANNOT_RECOVER.equals(this.transitionTaken)
					&& (getRecoveryClaim().getRecoveryClaimCannotRecoverReason() == null || StringUtils
					.isBlank(getRecoveryClaim().getRecoveryClaimCannotRecoverReason().getCode()))) {
				addActionError("error.supplier.requiredCannotRecoverReason");
			}

            if(WorkflowConstants.CANNOT_RECOVER.equals((this.transitionTaken)) &&
                    getRecoveryClaim().getRecoveryClaimCannotRecoverReason() != null && getRecoveryClaim().getRecoveryClaimCannotRecoverReason().getCode().equalsIgnoreCase(Constants.FLAG_FOR_2ND_RECOVERY)){
                if(getRecoveryClaim().getClaim().getRecoveryInfo().getCausalPartRecovery() != null && !getRecoveryClaim().getClaim().getRecoveryInfo().getCausalPartRecovery().isCausalPartRecovery()){
                    addActionError("error.recovery.info.causal.part.recovery");
                }else{
                    for(RecoverablePart part : getRecoveryClaim().getRecoveryClaimInfo().getRecoverableParts()){
                        if((part.isSupplierReturnNeeded() || (part.getOemPart().isReturnDirectlyToSupplier()))){
                            if(part.getStatus() != null && part.getStatus().ordinal() <PartReturnStatus.PART_RECEIVED.ordinal() ||
                                    (part.getOemPart().isReturnDirectlyToSupplier() && part.getOemPart().getStatus().ordinal()<PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal())){
                                // If part has not being received back to NMHG will show warning.
                                addActionError("error.part.on.the.way");
                            }
                            //or if part is with supplier ensure it is being asked
                            else if(isRequestForPartBackFromSupplierCheckboxRequired() && !initiateReturnRequestFromSupplier){
                                addActionError("error.message.request.for.part.from.supplier");
                            }
                            //practically it will never go to this block
                            else if(part.getStatus() != null && part.getStatus().ordinal() >= PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_AND_INSPECTED.ordinal() && part.getStatus().ordinal() < PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED.ordinal()){
                                flagFor2ndRecovery = true;
                            }/*else{
                                flagFor2ndRecovery = true;
                            }*/
                        }else{
                            flagFor2ndRecovery = true;
                        }
                    }
                }
            }
			validateDocumentTypeForTheAttachment();
        }
    }

    /**
     *
     * @return
     */
    private void validateTransitionsTaken() {
        boolean isvalid = false;
        if (transitionTaken != null && StringUtils.isNotBlank(transitionTaken)) {
            isvalid = true;
        }
        if (!isvalid) {
            addActionError("error.sra.noActionTaken");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<InboxItem>(inboxItems, pageSpecification, noOfPages);
    }

    /**
     * This method returns the transitions based on the Supplier Part Return
     * Status.
     *
     * @param recClaim
     * @return
     * @throws JSONException
     */
    public List<String> getAllTransitions(RecoveryClaim recClaim) throws JSONException {
        List<String> allTransitions = new ArrayList<String>();
        if (!RecoveryClaimState.ACCEPTED.equals(recClaim.getRecoveryClaimState())) {
            allTransitions.add("On Hold");
            allTransitions.add("Cannot Recover");
            allTransitions.add(getText("label.supplier.transfer").trim());
            allTransitions.add("Send To Supplier");
            allTransitions.add("Not For Recovery");
            allTransitions.add("Accept");
        } else if (!RecoveryClaimState.REJECTED.equals(recClaim.getRecoveryClaimState())) {
            allTransitions.add("Closed Recovered");
        }
        return allTransitions;
    }

    public String getTransitionTaken() {
        return transitionTaken;
    }

    public void setTransitionTaken(String transitionTaken) {
        this.transitionTaken = transitionTaken;
    }
    
    public boolean isCommentsExist(Claim claim) {
		for (ClaimAudit claimAudit : claim.getClaimAudits()) {

			if (!StringUtils.isEmpty(claimAudit.getInternalComments()) && !claimAudit.getUpdatedBy().hasRole(Role.SYSTEM)) {
				return true;
			}
		}
		return false;
	}
    
    private void setEligibilityToShare() {
		List<Document> attachments = getRecoveryClaim().getAttachments();
		Boolean shareSupplier = true;
		Boolean shareDealer = true;
		if (attachments != null) {
			for (Document doc : attachments) {
				try {
						
						if(sharedWithSupplier.size() < attachments.indexOf(doc)+1)
							shareSupplier = false;
						else
							shareSupplier = sharedWithSupplier.get(attachments.indexOf(doc));
						if(sharedWithDealer.size() < attachments.indexOf(doc)+1)
							shareDealer = false;
						else
							shareDealer = sharedWithDealer.get(attachments.indexOf(doc));
						doc.setIsSharedWithSupplier(shareSupplier);
						doc.setIsEligibilityToShare(shareSupplier);
						doc.setIsSharedWithDealer(shareDealer);
						/*List<Document> claimDocs = getRecoveryClaim().getClaim().getAttachments();
						if(!claimDocs.contains(doc))
							getRecoveryClaim().getClaim().getAttachments().add(doc);*/
						documentService.save(doc);
					} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

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
    
    public boolean isRequestForPartBackFromSupplierCheckboxRequired(){
        if(!getConfigParamService().getBooleanValue(ConfigName.REQUEST_FOR_PART_FROM_SUPPLIER_ON_VR_CLAIM_REJECTION.getName())){
            return false;
        }else{
            for(RecoverablePart part: getRecoveryClaim().getRecoveryClaimInfo().getRecoverableParts()){
                if(part.getOemPart().isPartScrapped()){
                    return false;
                }
               else if((null != part.getStatus() && (part.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus()) ||
                        part.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED.getStatus())))
                        || (null != part.getOemPart().getStatus() && part.getOemPart().getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus()))){
                    //Initiate return, return location is optional. due date and everything is optional
                    //Variables required recovery claim and recoverable part +plus supplier part return
                    return true;

                }
            }
        }

    	return false;
    }
    
}

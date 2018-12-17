package tavant.twms.web.supplier;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.UserComment;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

/**
 * Created by deepak.patel on 7/2/14.
 */
public class SupplierPartsInspection extends AbstractSupplierActionSupport{
	
	public static final String TRANSITION = "transition";
	
	private List<Document> attachments;

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

    private RecoveryInfoService recoveryInfoService;

    public RecoveryInfoService getRecoveryInfoService() {
        return recoveryInfoService;
    }

    public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
        this.recoveryInfoService = recoveryInfoService;
    }

    private WorkListItemService workListItemService;

    boolean flagForAnotherRecovery = true;
	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return workListService.getPartInspectorInspectView(criteria);
	}

	@Override
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<InboxItem>(inboxItems, pageSpecification, noOfPages);
	}
	
	private void validateAttachments(){
    	if(attachments != null){
    		attachments.removeAll(Collections.singleton(null));
    		for(Document attachment : attachments){
    			if(attachment.getDocumentType() == null){
    				addActionError("error.supplierPartsReceipt.attachmentTypeMandatory");
    			}
    		}
    	}
    }
	
	private void validateComments(){
		if(!StringUtils.hasText(comments)){
			addActionError("error.supplierPartsReceipt.commentsMandatory");
		}
	}
	
	private void validateRecoverablePartsBeans(){
		for (RecoverablePartsBean bean : getUiRecoverablePartsBeans()) {
			if(bean.isSelected()){
				String partNumber = bean.getRecoverablePart().getOemPart()
						.getItemReference().getUnserializedItem()
						.getAlternateNumber();
				if(bean.getAccepted() > 0 && !StringUtils.hasText(bean.getAcceptanceCause())){
						addActionError("error.supplierPartsReceipt.acceptanceReasonMandatory", partNumber);
				}
				if(bean.getRejected() > 0 && !StringUtils.hasText(bean.getFailureCause())){
						addActionError("error.supplierPartsReceipt.rejectionReasonMandatory", partNumber);
				}
				if(bean.getAccepted() < 0 || bean.getRejected() < 0){
					addActionError("error.supplierPartsReceipt.negativeValueInInspect", partNumber);
				}
				if(bean.getAccepted() + bean.getRejected() != bean.getReceived()){
					addActionError("error.supplierPartsReceipt.countMisMatchInInspect", partNumber);
				}
				if(bean.isReturnToDealer() && bean.getAccepted() == 0){
					addActionError("error.returnToDealer.noPartAccepted", partNumber);
				}
				if(bean.isReturnToSupplier() && bean.getRejected() == 0){
					addActionError("error.returnToSupplier.noPartRejected", partNumber);
				}
			}
		}
	}
	
	public String submit(){
    	validateAttachments();
    	validateComments();
    	validateRecoverablePartsBeans();
    	if(hasErrors()){
    		generatePreview();
    		setUserSpecifiedQuantity();
    		return INPUT;
    	}
    	
    	for (RecoverablePartsBean partReplacedBean : getUiRecoverablePartsBeans()) {
            if (partReplacedBean.isSelected()) {
                RecoverablePart part = partReplacedBean.getRecoveryPartTaskBeans().get(0).getRecoverablePart();
                PartReturnAction inspectAction1 = new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED.getStatus(),
                        partReplacedBean.getAccepted());
                PartReturnAction inspectAction2 = new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED.getStatus(),
                        partReplacedBean.getRejected());
                PartReturnAction inspectAction3 = new PartReturnAction(PartReturnStatus.PARTS_NOT_RECEIVED_FROM_SUPPLIER.getStatus(),
                        partReplacedBean.getDidNotReceive());

                if(StringUtils.hasText(partReplacedBean.getAcceptanceCause())){
                	ListOfValues acceptReason=lovRepository.findByCode(SupplierPartAcceptanceReason.class.getSimpleName(), partReplacedBean.getAcceptanceCause());
                    part.setStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED,getComments(),inspectAction1,inspectAction2,inspectAction3,acceptReason.getDescription(),null);
                }
                if(StringUtils.hasText(partReplacedBean.getFailureCause())){
                    ListOfValues rejectReason=lovRepository.findByCode(SupplierPartRejectionReason.class.getSimpleName(), partReplacedBean.getFailureCause());
                    part.setStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED,getComments(),inspectAction1,inspectAction2,inspectAction3,rejectReason.getDescription(),null);
                    if(part.getOemPart().isReturnDirectlyToSupplier()){
                        part.getOemPart().setAskBackFromSupplier(true);
                        part.getOemPart().setActedBy(getLoggedInUser().getName());
                        part.getOemPart().setTempLocationSetupForSupplier(part.getRetrunLocationObjectForSupplier());
                    }
                }

                if(partReplacedBean.isScrap()){
                    part.getOemPart().setPartScrapped(true);
                    part.getOemPart().setScrapDate(new Date());
                    PartReturnAction scrapAction = new PartReturnAction(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED.getStatus()
                            ,partReplacedBean.getAccepted());
                    part.setStatus(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED,getComments(),scrapAction,null,null);
                }

                if(flagForAnotherRecovery) {
                    for (PartReturn partReturn : part.getOemPart().getPartReturns()){
                        partReturn.setActionTaken(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER);
                        partReturn.setReturnLocation(part.getRetrunLocationObjectForSupplier());
                    }
                    part.getOemPart().setPartAction1(new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER.getStatus(),
                            partReplacedBean.getReceive()));
                    part.getOemPart().setReturnDirectlyToSupplier(false);
                    part.getOemPart().setStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER);
                    getPartReplacedService().updateOEMPartReplaced(part.getOemPart());
                }
            }
        }
    	
    	List<RecoveryPartTaskBean> selectedBeans = getSelectedPartTaskBeans();
    	
    	for(RecoverablePartsBean partBean : getUiRecoverablePartsBeans()){
            if(partBean.isReturnToDealer()){
                initiateReturnToDealer(partBean);
            }
        }

        if(flagForAnotherRecovery) {
            flagForAnotherRecovery();
        }
    	
    	if(attachments != null){
        	RecoveryClaim recClaim = recoveryClaimService.findRecoveryClaim(new Long(getId()));
        	recClaim.getAttachments().clear();
        	recClaim.getAttachments().addAll(attachments);
        	recoveryClaimService.updateRecoveryClaim(recClaim);
        }
    	
    	for(RecoveryPartTaskBean instance : selectedBeans){
    		getWorkListItemService().endTaskWithTransition(instance.getTask(), instance.getTransitionTaken());
    		if(instance.isScrap()){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED);
    		}else if(instance.getActionTaken().equals(WorkflowConstants.ACCEPT)){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED);
    		}else if(instance.getActionTaken().equals(WorkflowConstants.REJECT)){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED);
    		}
    	}
    	
    	addActionMessage("message.itemStatus.updated");
    	return SUCCESS;
	}

    private void flagForAnotherRecovery(){
        //second recovery flag goes here
        if(getRecoveryClaim().getRecoveryClaimState().equals(RecoveryClaimState.CLOSED_UNRECOVERED) && getRecoveryClaim().getRecoveryClaimCannotRecoverReason().getCode().equalsIgnoreCase(Constants.FLAG_FOR_2ND_RECOVERY)){
            TaskInstance taskInstance = this.workListItemService.findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
                    "Closed");
            getRecoveryClaim().setFlagForAnotherRecovery(true);
            getRecoveryClaim().getD().setActive(false);
            RecoveryInfo info = getRecoveryClaim().getClaim().getRecoveryInfo();
            info.getD().setActive(false);
            info.setWarrantyClaim(null);
            UserComment comment = new UserComment();
            comment.setComment(getText("message.reference.old.warranty.claim",this.getRecoveryClaim().getClaim().getClaimNumber()));
            info.getComments().add(comment);
            this.recoveryInfoService.saveUpdate(info);


            workListItemService.endTaskWithTransition(taskInstance,
                    "toEnd");
        }
    }
	
	private void setUserSpecifiedQuantity() {
		for (RecoverablePartsBean partReplacedBean : getRecoverablePartsBeans()) {
			for (RecoverablePartsBean uiPartReplacedBean : this.getUiRecoverablePartsBeans()) {
				if (partReplacedBean.getRecoverablePart().getId() == uiPartReplacedBean.getRecoverablePart().getId()) {
					partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
					partReplacedBean.setScrap(uiPartReplacedBean.isScrap());
					partReplacedBean.setAcceptanceCause(uiPartReplacedBean.getAcceptanceCause());
					partReplacedBean.setFailureCause(uiPartReplacedBean.getFailureCause());
					partReplacedBean.setAccepted(uiPartReplacedBean.getAccepted());
					partReplacedBean.setRejected(uiPartReplacedBean.getRejected());
					partReplacedBean.setReturnToDealer(uiPartReplacedBean.isReturnToDealer());
					partReplacedBean.setReturnToSupplier(uiPartReplacedBean.isReturnToSupplier());
				}
			}
		}
	}
	
	@Override
    protected void processPartTaskBean(RecoveryPartTaskBean partTaskBean) {
        partTaskBean.getSupplierPartReturn().setWarehouseLocation(
                partTaskBean.getWarehouseLocation());

            //check the transition taken
            if(partTaskBean.isReturnToDealer()){
                //Dude you have to trigger it manually. Return to dealer is a separate process and not in my territory
                //probably do the job for inspection set the result and trigger return
                partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
                partTaskBean.setTransitionTaken("toEnd");
                this.flagForAnotherRecovery = false;
                //TODO Code to initiate part return to dealer
                //Steps Get the OemPartsReplaced .. set the required data

            }
            else if(partTaskBean.isScrap()){
                partTaskBean.getTask().setVariable(TRANSITION, "Scrapped");
                partTaskBean.setTransitionTaken("Mark for Scrap");
                this.flagForAnotherRecovery = false;
            }
            else if(partTaskBean.isReturnToSupplier()){
                partTaskBean.getTask().setVariable(TRANSITION, "Send To Supplier");
                partTaskBean.setTransitionTaken("Send To Supplier");
                this.flagForAnotherRecovery = false;
            }
            else{
                 partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
                 partTaskBean.setTransitionTaken("toEnd");
            }
    }

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
	
	
}

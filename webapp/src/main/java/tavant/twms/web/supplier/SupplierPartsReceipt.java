package tavant.twms.web.supplier;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

import java.util.*;

/**
 * Created by deepak.patel on 7/2/14.
 */
public class SupplierPartsReceipt extends AbstractSupplierActionSupport{

    public static final String TRANSITION = "transition";

    private TaskViewService taskViewService;
    
    private List<Document> attachments;

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

    public TaskViewService getTaskViewService() {
        return taskViewService;
    }

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return workListService.getPartReceiverReceiptView(criteria);
    }

    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<InboxItem>(inboxItems, pageSpecification, noOfPages);
    }
    
    public Set<String> getWareHouses(String code) {
		return warehouseService.findByWarehouseCode(code).getWarehouseBins();
	}

    private RecoveryInfoService recoveryInfoService;

    public RecoveryInfoService getRecoveryInfoService() {
        return recoveryInfoService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
        this.recoveryInfoService = recoveryInfoService;
    }

    private WorkListItemService workListItemService;

    boolean flagForAnotherRecovery = true;

    //Setting up the transition for next processing. Required for supplier part receipt and inspection
    @Override
    protected void processPartTaskBean(RecoveryPartTaskBean partTaskBean) {
        partTaskBean.getSupplierPartReturn().setWarehouseLocation(
                partTaskBean.getWarehouseLocation());

        //Do the job for part receipt only -- that is for inspect or did not receive
        if (MARK_FOR_INSPECTION.equals(partTaskBean.getActionTaken()))
            partTaskBean.getTask().setVariable(TRANSITION,
                    "Send for Inspection");
        else if (MARK_NOT_RECEIVED.equals(partTaskBean.getActionTaken()))
        {
           partTaskBean.getTask().setVariable(TRANSITION, "Part Not Received");
        }

        //okay, now time to do the inspection.... there are multiple actions
        //1. send to dealer
        //2. send to supplier
        //3. scrap
        //Update the accept and reject reason
        else if (ACCEPT.equals(partTaskBean.getActionTaken())
                || REJECT.equals(partTaskBean.getActionTaken())) {
            //check the transition taken
            if(partTaskBean.isReturnToDealer()){
                //Dude you have to trigger it manually. Return to dealer is a separate process and not in my territory
                //probably do the job for inspection set the result and trigger return
                partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
                this.flagForAnotherRecovery = false;
                //TODO Code to initiate part return to dealer
                //Steps Get the OemPartsReplaced .. set the required data

            }
            else if(partTaskBean.isScrap()){
                partTaskBean.getTask().setVariable(TRANSITION, "Scrapped");
                this.flagForAnotherRecovery = false;
            }
            else if(partTaskBean.isReturnToSupplier()){
                partTaskBean.getTask().setVariable(TRANSITION, "Send To Supplier");
                this.flagForAnotherRecovery = false;
            }
            else{
                 partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
            }
            //updatePartsAfterInspection(partTaskBean);
        }
    }

   /* private void updatePartsAfterInspection(RecoveryPartTaskBean partTaskBean) {
        List<SupplierPartReturn> partsAccepted = new ArrayList<SupplierPartReturn>();
        List<SupplierPartReturn> partsRejected = new ArrayList<SupplierPartReturn>();
        if (partTaskBean.getActionTaken() == ACCEPT)
            partsAccepted.add(partTaskBean.getSupplierPartReturn());
        else if (partTaskBean.getActionTaken() == REJECT || partTaskBean.getActionTaken() == MARK_NOT_RECEIVED ) {
            partsRejected.add(partTaskBean.getSupplierPartReturn());
        }
        if (StringUtils.startsWithIgnoreCase(partTaskBean.getActionTaken(),
                ACCEPT)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().acceptSupplierPartAfterInspection(partsAccepted,partTaskBean.getAcceptanceCause());

        } else if (StringUtils.startsWithIgnoreCase(partTaskBean
                .getActionTaken(), REJECT)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().rejectSupplierPartAfterInspection(partsRejected,partTaskBean.getFailureCause());
        }
    }*/

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
    
	private void validateRecoverablePartsBeans() {
		for (RecoverablePartsBean bean : getUiRecoverablePartsBeans()) {
			if(bean.isSelected()){
			String partNumber = bean.getRecoverablePart().getOemPart()
					.getItemReference().getUnserializedItem()
					.getAlternateNumber();
			if (bean.getReceive() < 0 || bean.getDidNotReceive() < 0) {
				addActionError("error.supplierPartsReceipt.negativeValue", partNumber);
			}
			if (bean.getReceive() + bean.getDidNotReceive() != bean
					.getShipped()) {
				addActionError("error.supplierPartsReceipt.countMismatch", partNumber);
			}
			if (!StringUtils.hasText(bean.getWarehouseLocation())) {
				addActionError("error.supplierPartsReceipt.locationMandatory", partNumber);
			}
			if(bean.isToBeInspected()){
				if(bean.getAccepted() > 0 && !StringUtils.hasText(bean.getAcceptanceCause())){
						addActionError("error.supplierPartsReceipt.acceptanceReasonMandatory", partNumber);
				}
				if(bean.getRejected() > 0 && !StringUtils.hasText(bean.getFailureCause())){
						addActionError("error.supplierPartsReceipt.rejectionReasonMandatory", partNumber);
				}
				if(bean.getAccepted() < 0 || bean.getRejected() < 0){
					addActionError("error.supplierPartsReceipt.negativeValueInInspect", partNumber);
				}
				if(bean.getAccepted() + bean.getRejected() != bean.getReceive()){
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
	}
	
	private void validateComments(){
		if(!StringUtils.hasText(comments)){
			addActionError("error.supplierPartsReceipt.commentsMandatory");
		}
	}
    
    //TODO temp method requires implementation
    public String submit(){
    	validateAttachments();
    	validateRecoverablePartsBeans();
    	validateComments();
    	if(hasErrors()){
    		generatePreview();
    		setUserSpecifiedQuantity();
    		return INPUT;
    	}
        List<RecoveryPartTaskBean> selectedBeans = getSelectedPartTaskBeans();
        //List<SupplierPartReturn> parts = getPartReturnsFromPartTaskBeans(selectedBeans);
        getShipment().setReceiptDate(new Date());

        //Okay now the impt part, update the recovery history
        for (RecoverablePartsBean partReplacedBean : getUiRecoverablePartsBeans()) {
            if (partReplacedBean.isSelected()) {
                RecoverablePart part = partReplacedBean.getRecoveryPartTaskBeans().get(0).getRecoverablePart();
                PartReturnAction action1 = new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_AND_MARKED_FOR_INSPECTION.getStatus()
                        ,partReplacedBean.getReceive());
                PartReturnAction action2 = new PartReturnAction(PartReturnStatus.PARTS_NOT_RECEIVED_FROM_SUPPLIER.getStatus()
                        ,partReplacedBean.getDidNotReceive());
                part.setStatus(PartReturnStatus.PARTS_RECEIVED_AND_MARKED_FOR_INSPECTION,getComments(),action1,action2,null);
                part.setReceivedFromSupplier(partReplacedBean.getReceive());

                if (partReplacedBean.isToBeInspected()) {
                    PartReturnAction inspectAction1 = new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED.getStatus(),
                            partReplacedBean.getAccepted());
                    PartReturnAction inspectAction2 = new PartReturnAction(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED.getStatus(),
                            partReplacedBean.getRejected());
                    PartReturnAction inspectAction3 = new PartReturnAction(PartReturnStatus.PARTS_NOT_RECEIVED_FROM_SUPPLIER.getStatus(),
                            partReplacedBean.getDidNotReceive());

                    if(StringUtils.hasText(partReplacedBean.getAcceptanceCause()))
                    {
                        ListOfValues acceptReason=lovRepository.findByCode(SupplierPartAcceptanceReason.class.getSimpleName(), partReplacedBean.getAcceptanceCause());
                        part.setStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED,getComments(),inspectAction1,inspectAction2,inspectAction3,acceptReason.getDescription(),null);
                    }
                    if(StringUtils.hasText(partReplacedBean.getFailureCause()))
                    {
                        ListOfValues rejectReason=lovRepository.findByCode(SupplierPartRejectionReason.class.getSimpleName(), partReplacedBean.getFailureCause());
                        part.setStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED,getComments(),inspectAction1,inspectAction2,inspectAction3,rejectReason.getDescription(),null);
                        if(part.getOemPart().isReturnDirectlyToSupplier()){
                            part.getOemPart().setAskBackFromSupplier(true);
                            part.getOemPart().setActedBy(getLoggedInUser().getName());
                            part.getOemPart().setTempLocationSetupForSupplier(part.getRetrunLocationObjectForSupplier());
                        }
                        for(SupplierPartReturn supplierPartReturn : part.getSupplierPartReturns()){
                            if(part.getOemPart().isReturnDirectlyToSupplier()){
                                supplierPartReturn.setReturnLocation(part.getOemPart().getActivePartReturn().getReturnLocation());
                            }else{
                                supplierPartReturn.setReturnLocation(part.getOriginalRetrunLocationObjectForSupplier());
                            }
                        }

                    }

                    if(partReplacedBean.isScrap()){
                        part.getOemPart().setPartScrapped(true);
                        part.getOemPart().setScrapDate(new Date());
                        part.getOemPart().setStatus(PartReturnStatus.PART_MARKED_AS_SCRAPPED);
                        PartReturnAction scrapAction = new PartReturnAction(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED.getStatus()
                                ,partReplacedBean.getAccepted());
                        part.setStatus(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED,getComments(),scrapAction,null,null);

                    }
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

        for(RecoverablePartsBean partBean : getUiRecoverablePartsBeans()){
            if(partBean.isReturnToDealer()){
                initiateReturnToDealer(partBean);
            }
        }

        if(flagForAnotherRecovery) {
            flagForAnotherRecovery();
        }

        taskViewService.submitAllTaskInstances(getTasksForReceived(selectedBeans), transitionTaken);
        taskViewService.submitAllTaskInstances(getTasksForNotReceived(selectedBeans), transitionTaken);
        if(attachments != null){
        	RecoveryClaim recClaim = recoveryClaimService.findRecoveryClaim(new Long(getId()));
        	recClaim.getAttachments().clear();
        	recClaim.getAttachments().addAll(attachments);
        	recoveryClaimService.updateRecoveryClaim(recClaim);
        }
        
        for(RecoveryPartTaskBean instance : selectedBeans){
    		if(instance.isScrap()){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED);
    		}else if(instance.getActionTaken().equals(WorkflowConstants.ACCEPT)){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED);
    		}else if(instance.getActionTaken().equals(WorkflowConstants.REJECT)){
    			instance.getSupplierPartReturn().setBasePartReturnStatus(PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED);
    		}
    	}
        
        return resultingView();
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
            //part return handle should go there
            //dativate the recovery claim -- filter from recovery claim
            //getRecoveryClaimService().updateRecoveryClaim(getRecoveryClaim());
            //deactivate recovery info
            //end task instance
        }
    }
    
	private void setUserSpecifiedQuantity() {
		for (RecoverablePartsBean partReplacedBean : getRecoverablePartsBeans()) {
			for (RecoverablePartsBean uiPartReplacedBean : this.getUiRecoverablePartsBeans()) {
				if (partReplacedBean.getRecoverablePart().getId() == uiPartReplacedBean.getRecoverablePart().getId()) {
					partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
					partReplacedBean.setReceive(uiPartReplacedBean.getReceive());
					partReplacedBean.setDidNotReceive(uiPartReplacedBean.getDidNotReceive());
					partReplacedBean.setToBeInspected(uiPartReplacedBean.isToBeInspected());
					partReplacedBean.setScrap(uiPartReplacedBean.isScrap());
					partReplacedBean.setWarehouseLocation(uiPartReplacedBean.getWarehouseLocation());
					if (uiPartReplacedBean.isToBeInspected()) {
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
	}

    public List<TaskInstance> getTasksForReceived(
            List<RecoveryPartTaskBean> partTaskBeans) {
        List<TaskInstance> tasks = new ArrayList<TaskInstance>();
        for (RecoveryPartTaskBean partTaskBean : partTaskBeans) {
            if (MARK_FOR_INSPECTION.equals(partTaskBean.getActionTaken())
                    || ACCEPT.equals(partTaskBean.getActionTaken())
                    || REJECT.equals(partTaskBean.getActionTaken()))
                tasks.add(partTaskBean.getTask());
        }
        return tasks;
    }

    public List<TaskInstance> getTasksForNotReceived(
            List<RecoveryPartTaskBean> partTaskBeans) {
        List<TaskInstance> tasks = new ArrayList<TaskInstance>();
        for (RecoveryPartTaskBean partTaskBean : partTaskBeans) {
            if (MARK_NOT_RECEIVED.equals(partTaskBean.getActionTaken()))
                tasks.add(partTaskBean.getTask());
        }
        return tasks;
    }

    /*public List<SupplierPartReturn> getPartReturnsFromPartTaskBeans(
            List<RecoveryPartTaskBean> partTaskBeans) {
        List<SupplierPartReturn> parts = new ArrayList<SupplierPartReturn>();
        for (RecoveryPartTaskBean partTaskBean : partTaskBeans) {
            parts.add(partTaskBean.getSupplierPartReturn());
        }
        return parts;
    }*/

    private PartReturnService partReturnService;

    public PartReturnService getPartReturnService() {
        return partReturnService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

}

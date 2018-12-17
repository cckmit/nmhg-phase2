package tavant.twms.web.supplier;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.shipment.ContractShipmentService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.claim.ClaimsAction;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

import java.util.*;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by deepak.patel on 25/1/14.
 */
public class DuePartForSupplierToNMHG extends AbstractSupplierActionSupport {
	
    private TaskViewService taskViewService;
    
    private static final Logger logger = Logger.getLogger(DuePartForSupplierToNMHG.class);

    private ContractShipmentService contractShipmentService;
    
    private RecoveryClaimService recoveryClaimService;
    
    private SendEmailService sendEmailService;

    public ContractShipmentService getContractShipmentService() {
        return contractShipmentService;
    }

    public void setContractShipmentService(ContractShipmentService contractShipmentService) {
        this.contractShipmentService = contractShipmentService;
    }


    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return workListService.getSupplierRecoveryDistinctRecoveryClaimList(criteria);
    }

    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<InboxItem>(inboxItems, pageSpecification, noOfPages);
    }

    @Override
    protected String getAlias() {
        return "recoveryClaim";
    }
    
	public void validateShipment() {
	
			List<RecoveryPartTaskBean> partTasks = getSelectedPartTaskBeans();
			if (partTasks.size() == 0) {
				addActionError("error.partReturnConfiguration.noPartSelected");
			}
			if (!hasActionErrors()) {
				validateData();
			}			

			/*
			 * if(!hasActionErrors() && !receiversSetForWarehouse()){
			 * addActionError("error.NoReceiversPresent.ForWarehouse"); }			 */
	
	
	}
	public void validateData() {
		for (RecoverablePartsBean partReplacedBean : getUiRecoverablePartsBeans()) {
			if (partReplacedBean.isSelected()) {
			/*	if (partReplacedBean.getRecoveryPartTaskBeans() != null
						&& (isTaskShipmentGeneratedToNMHG()))*/
                String partNumber = partReplacedBean.getRecoverablePart().getOemPart().getItemReference().getReferredItem().getAlternateNumber();
				if (partReplacedBean.getRecoveryPartTaskBeans() != null)
                    if ((partReplacedBean.getCannotShip() < 0 || partReplacedBean
                            .getShip() < 0)) {
                        addActionError("error.message.partReturn.nonzero.quantity");
                    }
					else if ((partReplacedBean.getCannotShip() == 0 && partReplacedBean
							.getShip() == 0)) {
						addActionError("error.supplier.part.return.less.ship.quantity.less",partNumber);
					}else if (partReplacedBean.getCannotShip()
							+ partReplacedBean.getShip() < partReplacedBean
							.getRecoveryPartTaskBeans().size()) {
						addActionError("error.supplier.part.return.less.ship.quantity.less",partNumber);
					}else if (partReplacedBean.getCannotShip()
							+ partReplacedBean.getShip() > partReplacedBean
							.getRecoveryPartTaskBeans().size()) {
						addActionError("error.supplier.part.return.less.ship.quantity.excess", partNumber);
					}
				}
		}
	}

    //TODO temp method only needs to change
    public String submit(){
    	validateShipment();
    	if (hasActionErrors() || hasFieldErrors()) {
			generatePreview();
            setUserSpecifiedQuantity();
			return INPUT;

		}
        //List<TaskInstance> taskInstances = getTaskInstancesForShipper(getId());
        List<RecoveryPartTaskBean> recoveryPartTaskBeans = getSelectedPartTaskBeans();
        List<TaskInstance> selectedPartTaskBeans = new ArrayList<TaskInstance>();
        List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();
        for(RecoveryPartTaskBean recoveryPartTaskBean : recoveryPartTaskBeans){
            if(recoveryPartTaskBean.getTriggerStatus().equals(PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT)){
                //Add to shipment generate task
                selectedPartTaskBeans.add(recoveryPartTaskBean.getTask());
                supplierPartReturns.add(recoveryPartTaskBean.getSupplierPartReturn());
            }
        }

        //If shipment is already generated and parts available in Shipment generated inbox  for the parts then no need to generate again.
        Shipment shipment  = findShipmentNumberIfalreadyGenerated();
        if(shipment == null && supplierPartReturns.size()>0){
            Location location = getWarehouseService().getDefaultReturnLocation(getConfigParamService().getStringValue(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName()));
            shipment = this.contractShipmentService.generateSupplierContractShipment(supplierPartReturns,location);
        }

        //Shipment is null means no parts has been selected for ship or all marked as can not ship...
        //Note no shipment will be generated if all parts marked as can not ship.
        if(shipment != null){
            shipment.addSupplierPartReturns(supplierPartReturns);
        }

        List<TaskInstance> canNotShippedList = new ArrayList<TaskInstance>();
        for(RecoveryPartTaskBean recoveryPartTaskBean : recoveryPartTaskBeans){
            if(recoveryPartTaskBean.getTriggerStatus().equals(PartReturnTaskTriggerStatus.TO_BE_ENDED)){
                //Add to shipment generate task
                canNotShippedList.add(recoveryPartTaskBean.getTask());
            }
        }
        
        boolean supplierCannotShipNotification = false;

        //Okay now update recovery history
        for(RecoverablePartsBean recoverablePartsBean: getUiRecoverablePartsBeans()){
            if(recoverablePartsBean.isSelected()){
                RecoverablePart recPart = recoverablePartsBean.getRecoveryPartTaskBeans().get(0).getRecoverablePart();
                PartReturnAction action1 = new PartReturnAction(PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER.getStatus()
                        ,recoverablePartsBean.getShip());
                PartReturnAction action2 = new PartReturnAction(PartReturnStatus.PARTS_MARKED_AS_CAN_NOT_SHIPPED_BY_SUPPLIER.getStatus()
                        ,recoverablePartsBean.getCannotShip());
                if(recoverablePartsBean.getCannotShip()>0)
                	supplierCannotShipNotification = true;
                recPart.setStatus(PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER,getComments(),action1,action2,null, shipment);
            }
        }
        
        if(supplierCannotShipNotification)
        	try{
        		notifyRecoveryProcessor();
        	}catch(Exception e){
        		logger.error("Exception while sending mail to Notify Supplier cannot ship parts",e);
        	}

        taskViewService.submitAllTaskInstances(selectedPartTaskBeans,transitionTaken);
        taskViewService.submitAllTaskInstances(canNotShippedList, "Can not ship");

        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }
    
    private void notifyRecoveryProcessor(){
    	RecoveryClaim recoveryClaimForMail = recoveryClaimService.findRecoveryClaim(getRecoveryClaim().getId());
    	
        List<User> recoveryProcessors = orgService.findAllAvailableRecoveryProcessors();

        for(User recProssor : recoveryProcessors){
	    	HashMap<String,Object> paramMap = new LinkedHashMap<String, Object>();
	    	paramMap.put("userName", recProssor.getCompleteName());
	    	paramMap.put("supplierName", recoveryClaimForMail.getContract().getSupplier().getName());
	    	paramMap.put("recoveryClaimNumber", recoveryClaimForMail.getRecoveryClaimNumber());
	    	paramMap.put("supplierNumber", recoveryClaimForMail.getContract().getSupplier().getSupplierNumber());
	    	paramMap.put("returnDate", recoveryClaimForMail.getActiveRecoveryClaimAudit().getD().getUpdatedOn().toString("MM-dd-yyyy"));
	    	paramMap.put("notifyDate", CalendarUtil.convertToCalendarDate(new Date()).toString("MM-dd-yyyy"));
	    	paramMap.put("url", applicationSettings.getExternalUrlForEmail());
	   		sendEmailService.sendEmail(applicationSettings.getFromAddress(), recProssor.getEmail(), "Supplier Marked Cannot Ship Non-warranty Item", applicationSettings.getEmailCannotShipTemplate(), paramMap);
        }
    }

    private void setUserSpecifiedQuantity() {
        for (RecoverablePartsBean partReplacedBean : getRecoverablePartsBeans()) {
            for (RecoverablePartsBean uiPartReplacedBean : this.getUiRecoverablePartsBeans()) {
                if (partReplacedBean.getRecoverablePart().getId() == uiPartReplacedBean.getRecoverablePart().getId()) {
                    partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
                    partReplacedBean.setShip(uiPartReplacedBean.getShip());
                    partReplacedBean.setCannotShip(uiPartReplacedBean.getCannotShip());
                }
            }
        }
    }

    private Shipment findShipmentNumberIfalreadyGenerated(){
        List<TaskInstance> listOfTaskInstanceWithShipmentGenerated = getSupplierRecoveryWorkListDao().getAllTaskInstancesForRecoveryClaim(getRecoveryClaim().getId(), WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG);
        //Group by recovery claim
        Map<Long, TaskInstance> recoveryClaims = new HashMap<Long, TaskInstance>();
        for(TaskInstance instance : listOfTaskInstanceWithShipmentGenerated){
            RecoveryClaim recoveryClaim = (RecoveryClaim) instance.getVariable("recoveryClaim");
            recoveryClaims.put(recoveryClaim.getId(), instance);
        }
        if(recoveryClaims.keySet().contains(getRecoveryClaim().getId())){
            //Find the shipment
            TaskInstance task = recoveryClaims.get(getRecoveryClaim().getId());
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) task.getVariable("supplierPartReturn");
            return supplierPartReturn.getSupplierShipment();
        }
        return null;
    }

    public TaskViewService getTaskViewService() {
        return taskViewService;
    }

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}
}

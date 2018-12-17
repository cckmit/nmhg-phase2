package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListDao;
import tavant.twms.worklist.WorkListItemService;

@SuppressWarnings("serial")
public class RoutedToNMHGAction extends AbstractSupplierActionSupport {

	private List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();

	private List<RecoverablePartsBean> recoverablePartsBeans = new ArrayList<RecoverablePartsBean>();
	
	private WorkListItemService workListItemService;
	
	private PartReturnService partReturnService;
	
	private WorkListDao workListDao;

	public void setWorkListDao(WorkListDao workListDao) {
		this.workListDao = workListDao;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public List<RecoverablePartsBean> getRecoverablePartsBeans() {
		return recoverablePartsBeans;
	}

	public void setRecoverablePartsBeans(
			List<RecoverablePartsBean> recoverablePartsBeans) {
		this.recoverablePartsBeans = recoverablePartsBeans;
	}

	private List<Boolean> returnToSupplier;

	public List<Boolean> getReturnToSupplier() {
		return returnToSupplier;
	}

	public void setReturnToSupplier(List<Boolean> returnToSupplier) {
		this.returnToSupplier = returnToSupplier;
	}

	public List<TaskInstance> getTaskInstances() {
		return taskInstances;
	}

	public void setTaskInstances(List<TaskInstance> taskInstances) {
		this.taskInstances = taskInstances;
	}

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return this.supplierRecoveryWorkListDao
				.getRoutedPartReturnRequests(criteria);
	}

	@Override
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<RecoveryClaim>(inboxItems, pageSpecification,
				noOfPages);
	}

	@Override
	protected String getAlias() {
		if (getInboxViewId() != null
				&& getInboxViewId().equals(DEFAULT_VIEW_ID)) {
			return null;
		} else {
			return "recoveryClaim";
		}
	}

	private void prepareBeans() {
		for(TaskInstance taskInstance : taskInstances){
			RecoverablePartsBean recoverablePartsBean = new RecoverablePartsBean();
			recoverablePartsBean.setTaskInstance(taskInstance);
			recoverablePartsBean.setSupplierPartReturn((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
			recoverablePartsBeans.add(recoverablePartsBean);
		}
	}
	
	public String detail() {
		setRecoveryClaim(recoveryClaimService.findRecoveryClaim(Long
				.valueOf(getId())));
		taskInstances = getRoutedTaskInstancesForRecClaimId(Long
				.valueOf(getId()));
		prepareBeans();
		return SUCCESS;
	}

	public String submit() {
		for (RecoverablePartsBean partBean : recoverablePartsBeans) {
			if (partBean.isPartSelected()) {
				if (this.transitionTaken .equals(WorkflowConstants.ROUTED_PART_RETURN_ACCEPTED)) {
					RecoverablePart recoverablePart = partBean.getSupplierPartReturn().getRecoverablePart();
					if (recoverablePart
							.getOemPart().isPartReturnsPresent()) {
						partBean.getSupplierPartReturn().setReturnLocation(partBean.getSupplierReturnLocation());
						if(isPartInWarehouse(partBean.getSupplierPartReturn())){
							partBean.getSupplierPartReturn().setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
							workListItemService.endTaskWithTransition(partBean.getTaskInstance(), "Generate Shipment");
						} else {
							partBean.getSupplierPartReturn().setStatus(PartReturnStatus.AWAITING_SHIPMENT_TO_WAREHOUSE);
							workListItemService.endTaskWithTransition(partBean.getTaskInstance(), "Awaiting Shipment to Warehouse");
						}
					} else{
						PartReturn partReturn = new PartReturn();
						partReturn.setDueDays(getConfigParamService().getLongValue(
								ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName()).intValue());
						partReturn.setOemPartReplaced(recoverablePart.getOemPart());
						recoverablePart.getOemPart().setPartToBeReturned(true);
	                    recoverablePart.getOemPart().setPartReturn(partReturn);
                        //Set the default payment condition
                        PaymentCondition condition = new PaymentCondition();
                        condition.setCode("PAY");
                        condition.setDescription("Pay without Part Return");
                        partReturn.setPaymentCondition(condition);
						if(partBean.isReturnToOEM()){
                            recoverablePart.getOemPart().setReturnDirectlyToSupplier(false);
		                    partReturn.setReturnLocation(partBean.getOemReturnLocation());
		                    partReturnService.updateExistingPartReturns(recoverablePart.getOemPart(), getRecoveryClaim().getClaim());
							partReturnProcessingService.startPartReturnProcessForPart(
									getRecoveryClaim().getClaim(), recoverablePart.getOemPart());
						} else {
		                    partReturn.setReturnLocation(partBean.getSupplierReturnLocation());
                            recoverablePart.getOemPart().setReturnDirectlyToSupplier(true);
                            //Put a hack here for canadian dealers...
                            // there will be 2 return request for canadian dealers part return
                            // 1. dealer --> nmhg default warehouse and nmhg to supplier through recovery
                            boolean isDealerCanadian = isClaimFiledByCanadianDealer(getRecoveryClaim().getClaim().getForDealer().getId());
                            Location defaultNMHGWareHouseLocation = claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation());
                            if(isDealerCanadian){
                                //2 return required -- dealer -> nmhg, nmhg-> supplier scheduler
                                //initiate the dealer --> nmhg data -- > return location change
                                partReturn.setReturnLocation(defaultNMHGWareHouseLocation);
                                //initiate recovery return
                                recoverablePart.setSupplierReturnNeeded(true);
                                partReturnProcessingService.startRecoveryPartReturnProcessForRecPart(recoverablePart,this.recoveryClaim, partBean.getSupplierReturnLocation());
                                //Make direct return to false
                                recoverablePart.getOemPart().setReturnDirectlyToSupplier(false);

                            }
		                    partReturnService.updateExistingPartReturns(recoverablePart.getOemPart(), getRecoveryClaim().getClaim());
							partReturnProcessingService.startPartReturnProcessForPart(
									getRecoveryClaim().getClaim(), recoverablePart.getOemPart());
							workListItemService.endTaskWithTransition(partBean.getTaskInstance(), "toEnd");
						}
					}
				} else {
					partBean.getSupplierPartReturn().setStatus(PartReturnStatus.SUPPLIER_PART_RETURN_REJECTED);
					workListItemService.endTaskWithTransition(partBean.getTaskInstance(), "toEnd");
					TaskInstance onHoldTask = workListDao.getOpenOnHoldTaskForRecoveryClaim(getRecoveryClaim());
					getRecoveryClaim().setExternalComments(getText("message.supplierRouting.rejected"));
					getRecoveryClaim().setComments(getText("message.supplierRouting.rejected"));
					if(onHoldTask != null){
						workListItemService.endTaskWithTransition(onHoldTask, "Reject");
					}
				} 
			}
		}
		addActionMessage("message.itemStatus.updated");
		return SUCCESS;
	}

	private List<TaskInstance> getRoutedTaskInstancesForRecClaimId(
			Long recClaimId) {
		return supplierRecoveryWorkListDao
				.getRoutedTaskInstancesForRecClaimId(recClaimId);
	}
	
	private boolean isPartInWarehouse(SupplierPartReturn supplierPartReturn) {
		return supplierPartReturn.getRecoverablePart().getOemPart().isPartInWarehouse();		
	}

}

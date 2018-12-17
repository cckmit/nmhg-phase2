package tavant.twms.web.supplier;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.partsreturn.OEMPartReplacedBean;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.domainlanguage.time.CalendarDate;

/**
 * Created by Ajitkumar.singh on 25/1/14.
 */
public class ShipmentGeneratedForSupplierToNMHG extends
		AbstractSupplierActionSupport {

	private static final long serialVersionUID = 1L;
	private TaskViewService taskViewService;	
	private Integer hour;
	private Integer minute;
	//private Date shipmentDate;

	public TaskViewService getTaskViewService() {
		return taskViewService;
	}

	public void setTaskViewService(TaskViewService taskViewService) {
		this.taskViewService = taskViewService;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

    /*public Date getShipmentDate() {
            return shipmentDate;
        }

        public void setShipmentDate(Date shipmentDate) {
            this.shipmentDate = shipmentDate;
        }
    */
	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return workListService.getSupplierRecoveryDistinctRecoveryClaimList(criteria);
	}

	@Override
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<InboxItem>(inboxItems, pageSpecification,
				noOfPages);
	}

	@Override
	protected String getAlias() {
		return "recoveryClaim";
	}
	

	public String submit() {
        List<RecoveryPartTaskBean> recoveryPartTaskBeans = getSelectedPartTaskBeans();
        List<TaskInstance> selectedPartTaskBeans = new ArrayList<TaskInstance>();
       // List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();
        for(RecoveryPartTaskBean recoveryPartTaskBean : recoveryPartTaskBeans){
            if(recoveryPartTaskBean.getTriggerStatus().equals(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED)){
                //Add to shipment generate task
                selectedPartTaskBeans.add(recoveryPartTaskBean.getTask());
             //   supplierPartReturns.add(recoveryPartTaskBean.getSupplierPartReturn());
            }
        }
	   
		if(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG.equals(getTaskName()) ){
            Carrier carrier = getCarrierRepository().findCarrierById(Long
    					.parseLong(getCarrierId()));
             getShipment().setCarrier(carrier);
             getShipment().setTrackingId(getTrackingNumber());
             //getShipment().setShipmentDate(shipmentDate);
        }

        //Update Part Recovery History
        for(RecoverablePartsBean recoverablePartsBean: getUiRecoverablePartsBeans()){
            if(recoverablePartsBean.isSelected()){
                RecoverablePart recPart = recoverablePartsBean.getRecoveryPartTaskBeans().get(0).getRecoverablePart();
                PartReturnAction action1 = new PartReturnAction(PartReturnStatus.PARTS_SHIPPED_BY_SUPPLIER_TO_NMHG.getStatus()
                        ,recoverablePartsBean.getShipmentGenerated());
                recPart.setStatus(PartReturnStatus.PARTS_SHIPPED_BY_SUPPLIER_TO_NMHG,getComments(),action1,null,null,getShipment());
            }
        }

        taskViewService.submitAllTaskInstances(selectedPartTaskBeans, "Parts Shipped fork");
		addActionMessage("message.itemStatus.updated");
		return SUCCESS;
	}

	@Override
	public void validate() {

       	if (getShipment().getShipmentDate() == null) {
			addActionError("error.partReturnConfiguration.shipmentDateIsRequired");
		}
		if ((getShipment().getShipmentDate() != null)
				|| (getTaskName().equalsIgnoreCase(
						WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG))) {
			if (hour == -1) {
				addActionError("error.field.hour");
			}
			if (minute == -1) {
				addActionError("error.field.minute");
			}
			if (hour != null && hour != -1) {
				this.getShipment().getShipmentDate().setHours(hour);
			} else {
				this.getShipment().getShipmentDate().setHours(00);
			}
			if (minute != null && minute != -1) {
				this.getShipment().getShipmentDate().setMinutes(minute);
			} else {
				this.getShipment().getShipmentDate().setMinutes(00);
			}

			// for any flow if shipment date is there it should be today or
			// future date
			// For EMEA, part return is through CEVA. For Amer, it is not. Hence using the config to identify AMER BU
			// This validation is not required for AMER -- SLMSPROD-1480
		 if (isShipmentThroughCEVA() && getShipment().getShipmentDate().getTime() < new Date().getTime() && getShipment().getShipmentDate().getDate() != new Date().getDate()) {
				addActionError("error.partReturnConfiguration.shipmentDateInPast");
			}
		}	
		if (hasActionErrors() || hasFieldErrors()) {
			generatePreview();
            setUserSpecifiedQuantity();
		}
	}

    private void setUserSpecifiedQuantity() {
        for (RecoverablePartsBean partReplacedBean : getRecoverablePartsBeans()) {
            for (RecoverablePartsBean uiPartReplacedBean : this.getUiRecoverablePartsBeans()) {
                if (partReplacedBean.getRecoverablePart().getId() == uiPartReplacedBean.getRecoverablePart().getId()) {
                    partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
                    partReplacedBean.setShipmentGenerated(uiPartReplacedBean.getShipmentGenerated());
                    partReplacedBean.setCannotShip(uiPartReplacedBean.getCannotShip());
                }
            }
        }
    }

    public boolean isShipmentThroughCEVA() {
        return getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
    }

}

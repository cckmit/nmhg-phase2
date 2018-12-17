package tavant.twms.web.supplier;

import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;

/**
 * Created by deepak.patel on 30/1/14.
 */
public class RecoveryPartTaskBean {

    private boolean selected = false;

    private TaskInstance task;

    private String actionTaken;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private PartReturnTaskTriggerStatus triggerStatus = PartReturnTaskTriggerStatus.TO_BE_TRIGGERED;

    private boolean toBeInspected = false;

    private String warehouseLocation;

    private int inspected;

    private String failureCause;

    private String acceptanceCause;

    private boolean returnToDealer;
    private boolean scrap;
    private boolean returnToSupplier;
    
    private String transitionTaken;
    
    public String getTransitionTaken() {
		return transitionTaken;
	}

	public void setTransitionTaken(String transitionTaken) {
		this.transitionTaken = transitionTaken;
	}

	public RecoveryPartTaskBean() {
    }

    public RecoveryPartTaskBean(TaskInstance task) {
        this.task = task;
    }

    public Claim getClaim() {
        return (Claim) task.getVariable("claim");
    }

    // For Recovery Claim Part Return
    public RecoveryClaim getRecoveryClaim() {
        return (RecoveryClaim) task.getVariable("recoveryClaim");
    }

    public OEMPartReplaced getOemPart() {
        PartReturn partReturn = (PartReturn) task.getVariable("partReturn");
        return partReturn != null
                ? partReturn.getOemPartReplaced()
                : new OEMPartReplaced();
    }


    public RecoverablePart getRecoverablePart() {
        SupplierPartReturn supplierPartReturn = (SupplierPartReturn) task.getVariable("supplierPartReturn");
        return supplierPartReturn != null
                ? supplierPartReturn.getRecoverablePart()
                : new RecoverablePart();
    }

    public SupplierPartReturn getSupplierPartReturn() {
        return (SupplierPartReturn) task.getVariable("supplierPartReturn");
    }

    public TaskInstance getTask() {
        return task;
    }

    public void setTask(TaskInstance task) {
        this.task = task;
    }

    public PartReturnTaskTriggerStatus getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(PartReturnTaskTriggerStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public boolean isToBeInspected() {
        return toBeInspected;
    }

    public void setToBeInspected(boolean toBeInspected) {
        this.toBeInspected = toBeInspected;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public int getInspected() {
        return inspected;
    }

    public void setInspected(int inspected) {
        this.inspected = inspected;
    }

    public String getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(String failureCause) {
        this.failureCause = failureCause;
    }

    public String getAcceptanceCause() {
        return acceptanceCause;
    }

    public void setAcceptanceCause(String acceptanceCause) {
        this.acceptanceCause = acceptanceCause;
    }

    public boolean isReturnToDealer() {
        return returnToDealer;
    }

    public void setReturnToDealer(boolean returnToDealer) {
        this.returnToDealer = returnToDealer;
    }

    public boolean isScrap() {
        return scrap;
    }

    public void setScrap(boolean scrap) {
        this.scrap = scrap;
    }

    public boolean isReturnToSupplier() {
        return returnToSupplier;
    }

    public void setReturnToSupplier(boolean returnToSupplier) {
        this.returnToSupplier = returnToSupplier;
    }
}

package tavant.twms.web.supplier;

import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepak.patel on 28/1/14.
 */
public class RecoverablePartsBean {

    int toBeShipped = 0;
    int shipped = 0;
    int cannotBeShipped = 0;
    int ship = 0;
    int cannotShip = 0;
    int shipmentGenerated = 0;
    int receive = 0;
    int didNotReceive = 0;
    int received = 0;
    int notReceived = 0;
    int inspected = 0;
    int totalNoOfParts = 0;
    private boolean selected = true;
    RecoverablePart recoverablePart;
    private List<RecoveryPartTaskBean> recoveryPartTaskBeans = new ArrayList<RecoveryPartTaskBean>(100);
    private RecoveryClaim recClaim;
    RecoveryPartTaskBean recoveryPartTaskBean;
    private int qtyForShipment;
    private String warehouseLocation;
    private boolean toBeInspected = false;
    private int accepted = 0;
    private int rejected = 0;
    private String failureCause;
    private String acceptanceCause;
    private boolean returnToDealer;
    private boolean scrap;
    private boolean returnToSupplier = false;
    private boolean returnToOEM = false;
    private TaskInstance taskInstance;
    private SupplierPartReturn supplierPartReturn;
    private Location oemReturnLocation;
    private Location supplierReturnLocation;
    private boolean partSelected = false;

    public boolean isPartSelected() {
		return partSelected;
	}

	public void setPartSelected(boolean partSelected) {
		this.partSelected = partSelected;
	}

	public Location getOemReturnLocation() {
		return oemReturnLocation;
	}

	public void setOemReturnLocation(Location oemReturnLocation) {
		this.oemReturnLocation = oemReturnLocation;
	}

	public Location getSupplierReturnLocation() {
		return supplierReturnLocation;
	}

	public void setSupplierReturnLocation(Location supplierReturnLocation) {
		this.supplierReturnLocation = supplierReturnLocation;
	}

	public SupplierPartReturn getSupplierPartReturn() {
		return supplierPartReturn;
	}

	public void setSupplierPartReturn(SupplierPartReturn supplierPartReturn) {
		this.supplierPartReturn = supplierPartReturn;
	}

	public boolean isReturnToOEM() {
		return returnToOEM;
	}

	public void setReturnToOEM(boolean returnToOEM) {
		this.returnToOEM = returnToOEM;
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public RecoverablePartsBean() {
    }

    public RecoverablePartsBean(RecoveryPartTaskBean recoveryPartTaskBean) {
        this.recoveryPartTaskBean = recoveryPartTaskBean;
        this.recoveryPartTaskBeans.add(this.recoveryPartTaskBean);
        this.recoverablePart = recoveryPartTaskBean.getRecoverablePart();
        this.recClaim = recoveryPartTaskBean.getRecoveryClaim();
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

    public void setRecClaim(RecoveryClaim recClaim) {
        this.recClaim = recClaim;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public boolean isToBeInspected() {
        return toBeInspected;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public void setToBeInspected(boolean toBeInspected) {
        this.toBeInspected = toBeInspected;
    }

    public RecoveryPartTaskBean getRecoveryPartTaskBean() {
        return recoveryPartTaskBean;
    }

    public void setRecoveryPartTaskBean(RecoveryPartTaskBean recoveryPartTaskBean) {
        this.recoveryPartTaskBean = recoveryPartTaskBean;
    }

    public RecoveryClaim getRecClaim() {
        return recClaim;
    }

    public void setRecClaimId(RecoveryClaim recClaimId) {
        this.recClaim = recClaimId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getToBeShipped() {
        return toBeShipped;
    }

    public void setToBeShipped(int toBeShipped) {
        this.toBeShipped = toBeShipped;
    }

    public int getShipped() {
        return shipped;
    }

    public void setShipped(int shipped) {
        this.shipped = shipped;
    }

    public int getCannotBeShipped() {
        return cannotBeShipped;
    }

    public void setCannotBeShipped(int cannotBeShipped) {
        this.cannotBeShipped = cannotBeShipped;
    }

    public int getShip() {
        return ship;
    }

    public void setShip(int ship) {
        this.ship = ship;
    }

    public int getCannotShip() {
        return cannotShip;
    }

    public void setCannotShip(int cannotShip) {
        this.cannotShip = cannotShip;
    }

    public int getShipmentGenerated() {
        return shipmentGenerated;
    }

    public void setShipmentGenerated(int shipmentGenerated) {
        this.shipmentGenerated = shipmentGenerated;
    }

    public int getReceive() {
        return receive;
    }

    public void setReceive(int receive) {
        this.receive = receive;
    }

    public int getDidNotReceive() {
        return didNotReceive;
    }

    public void setDidNotReceive(int didNotReceive) {
        this.didNotReceive = didNotReceive;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getNotReceived() {
        return notReceived;
    }

    public void setNotReceived(int notReceived) {
        this.notReceived = notReceived;
    }

    public int getInspected() {
        return inspected;
    }

    public void setInspected(int inspected) {
        this.inspected = inspected;
    }

    public int getTotalNoOfParts() {
        return totalNoOfParts;
    }

    public void setTotalNoOfParts(int totalNoOfParts) {
        this.totalNoOfParts = totalNoOfParts;
    }

    public RecoverablePart getRecoverablePart() {
        return recoverablePart;
    }

    public void setRecoverablePart(RecoverablePart recoverablePart) {
        this.recoverablePart = recoverablePart;
    }

    public List<RecoveryPartTaskBean> getRecoveryPartTaskBeans() {
        return recoveryPartTaskBeans;
    }

    public void setRecoveryPartTaskBeans(List<RecoveryPartTaskBean> recoveryPartTaskBeans) {
        this.recoveryPartTaskBeans = recoveryPartTaskBeans;
    }

    public int getQtyForShipment() {
        return qtyForShipment;
    }

    public void setQtyForShipment(int qtyForShipment) {
        this.qtyForShipment = qtyForShipment;
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
}

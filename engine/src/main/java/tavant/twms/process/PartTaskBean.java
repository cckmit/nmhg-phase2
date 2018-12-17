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
package tavant.twms.process;

import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.InspectionStatus;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.ReceiptStatus;
import tavant.twms.domain.partreturn.ShipmentStatus;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.inventory.InventoryItem;

/**
 * @author vineeth.varghese
 * @date Feb 9, 2007
 */
public class PartTaskBean {

	// TODO ; Removed the TaskWrapper inheritance - multiplication of getClaim
	// etc. Fix it.

	private String number;

	private boolean selected = false;

	private String failureCause;

	private String acceptanceCause;

	private String warehouseLocation;

	private String actionTaken;
    
	private boolean toBeInspected = false;
	
	private Contract selectedContract;
	  
	  
	public int getInspected() {
		return inspected;
	}

	public void setInspected(int inspected) {
		this.inspected = inspected;
	}

	private TaskInstance task;
	
	private ShipmentStatus shipmentStatus;
	
	private ReceiptStatus receiptStatus;
	
	private InspectionStatus inspectionStatus;
	

	private Item partOffPartNumber;
	
	private InventoryItem partOffSerialNumber;
	

	
	private int inspected;
	
	private OEMPartReplaced newOEMPart;

	public PartTaskBean() {
	}

	public PartTaskBean(TaskInstance task) {
		this.task = task;
		this.number = (new Long(task.getId())).toString();
	}

	public Claim getClaim() {
		return (Claim) task.getVariable("claim");
	}

	// For Recovery Claim Part Return
	public RecoveryClaim getRecoveryClaim() {
		return (RecoveryClaim) task.getVariable("recoveryClaim");
	}
	
	

	public OEMPartReplaced getNewOEMPart() {
		return newOEMPart;
	}

	public void setNewOEMPart(OEMPartReplaced newOEMPart) {
		this.newOEMPart = newOEMPart;
	}

	public OEMPartReplaced getPart() {
		PartReturn partReturn = (PartReturn) task.getVariable("partReturn");
		return partReturn != null
				? partReturn.getOemPartReplaced()
				: new OEMPartReplaced();
	}

	public OEMPartReplaced getSupplierPart() {
		OEMPartReplaced supplierPart = (OEMPartReplaced) task
				.getVariable("supplierPart");
		return supplierPart != null ? supplierPart : new OEMPartReplaced();

	}

	public PartReturn getPartReturn() {
		return (PartReturn) task.getVariable("partReturn");
	}

	public long getId() {
		return task.getId();
	}

	public TaskInstance getTask() {
		return this.task;
	}

	public void setTask(TaskInstance task) {
		this.task = task;
	}

	public void setNumber(String number) {
		if (getTask() != null) {
			throw new IllegalStateException("Cannot set task id");
		}
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}

	public void setShipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}

	public ReceiptStatus getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(ReceiptStatus receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getWarehouseLocation() {
		return warehouseLocation;
	}

	public void setWarehouseLocation(String warehouseLocation) {
		this.warehouseLocation = warehouseLocation;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getAcceptanceCause() {
		return acceptanceCause;
	}

	public void setAcceptanceCause(String acceptanceCause) {
		this.acceptanceCause = acceptanceCause;
	}
	
	
	
	
	public boolean isToBeInspected() {
		return toBeInspected;
	}

	public void setToBeInspected(boolean toBeInspected) {
		this.toBeInspected = toBeInspected;
	}

	public InspectionStatus getInspectionStatus() {
		return inspectionStatus;
	}

	public void setInspectionStatus(InspectionStatus inspectionStatus) {
		this.inspectionStatus = inspectionStatus;
	}
	public Item getPartOffPartNumber() {
		return partOffPartNumber;
	}

	public void setPartOffPartNumber(Item partOffPartNumber) {
		this.partOffPartNumber = partOffPartNumber;
	}

	public InventoryItem getPartOffSerialNumber() {
		return partOffSerialNumber;
	}

	public void setPartOffSerialNumber(InventoryItem partOffSerialNumber) {
		this.partOffSerialNumber = partOffSerialNumber;
	}
	
	public boolean isIncorrectPartReturned(){
		if (this.selected) {
			return (this.partOffPartNumber !=null &&
					this.partOffPartNumber != this.getPart().getItemReference().getUnserializedItem())
					|| ( this.partOffPartNumber == this.getPart().getItemReference().getUnserializedItem() && 
							this.partOffSerialNumber != this.getPart().getItemReference().getReferredInventoryItem());
		} else
			return false;
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setSelectedContract(Contract selectedContract) {
		this.selectedContract = selectedContract;
	}

}


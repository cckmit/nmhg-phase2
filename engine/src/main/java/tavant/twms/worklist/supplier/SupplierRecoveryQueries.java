/**
 * 
 */
package tavant.twms.worklist.supplier;

import tavant.twms.jbpm.WorkflowConstants;

/**
 * @author kannan.ekanath
 * 
 */
public interface SupplierRecoveryQueries {

	final String SUPPLIER_RECOVERY_CLAIM_VIEW_QUERY = "from TaskInstance taskInstance, "
		+ "RecoveryClaim recoveryClaim "
		+ "where taskInstance.isOpen = true  "
		+ "and taskInstance.claimId = recoveryClaim.id "
		//+ "and recoveryClaim.claim = claim.id "
		+ "and taskInstance.task.name= :taskName "
		+ "and taskInstance.actorId = :actorId ";
	
	final String ALL_ACTORS_REC_CLAIM_VIEW_QUERY = "from TaskInstance taskInstance, "
			+ "RecoveryClaim recoveryClaim "
			+ "where taskInstance.isOpen = true  "
			+ "and taskInstance.claimId = recoveryClaim.id "
			+ "and taskInstance.task.name= :taskName ";
	
    //CR TKTSA-817 
	final String SUPPLIER_RECOVERY_PART_VIEW_QUERY = "from TaskInstance taskInstance, "
		+ "RecoveryClaim recoveryClaim join recoveryClaim.recoveryClaimInfo.recoverableParts as recoverableParts join recoverableParts.supplierPartReturns as supplierPartReturns, "
		+ "Claim claim join claim.claimedItems as claimedItems "
		+ "where taskInstance.isOpen = true  "
		+ "and taskInstance.claimId = recoveryClaim.id "
		+ "and recoveryClaim.claim = claim.id "
		+ "and taskInstance.task.name= :taskName "
		+ "and taskInstance.actorId = :actorId ";
	
	final String SUPPLIER_CLAIM_VIEW_QUERY = "from RecoveryClaim recoveryClaim, TaskInstance ti "
		+ "where ti.isOpen = true "
		+ "and ti.claimId = recoveryClaim.id "
		+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";

	final String PART_SHIPPER_SHIPMENT_AND_CLAIM_VIEW_QUERY_BARCODE_ENABLED = "from TaskInstance ti, "
		+ "SupplierPartReturn supplierPartReturn inner join supplierPartReturn.supplierShipment shipment "
		+ "where ti.isOpen = true and ti.partReturnId = supplierPartReturn.id "
		+ "and ((supplierPartReturn.recoverablePart.oemPart in (select pr.oemPartReplaced from BasePartReturn pr,Warehouse w,User u " +
				"where pr.returnLocation=w.location " +
				"and u.name= :actorId " +
				"and u in elements(w.partShippers))) or ti.actorId = :actorId) "
		+ "and ti.task.name= :taskName ";
	
	final String SUPPLIER_CONFIRM_PARTRETURNS_VIEW_QUERY = "from TaskInstance taskInstance, RecoveryClaim recoveryClaim, "
		+ "SupplierPartReturn supplierPartReturn, Shipment shipment "
		+ "where taskInstance.isOpen = true and taskInstance.partReturnId = supplierPartReturn.id and shipment.id=supplierPartReturn.supplierShipment "		
		+ "and taskInstance.claimId = recoveryClaim.id and taskInstance.task.name= :taskName and taskInstance.actorId = :actorId ";
	
	final String MASTER_SUPPLIER_CONFIRM_PARTRETURNS_VIEW_QUERY = "from TaskInstance taskInstance, RecoveryClaim recoveryClaim, "
			+ "SupplierPartReturn supplierPartReturn, Shipment shipment "
			+ "where taskInstance.isOpen = true and taskInstance.partReturnId = supplierPartReturn.id and shipment.id=supplierPartReturn.supplierShipment "		
			+ "and taskInstance.claimId = recoveryClaim.id and taskInstance.task.name= :taskName ";

	final String SUPPLIER_RESPONSE_VIEW_QUERY = "from "
		+ "TaskInstance ti, RecoveryClaim recoveryClaim "
		+ "where ti.isOpen = true and ti.claimId = recoveryClaim.id "
		// + "and part in
		// elements(claim.serviceInformation.serviceDetail.oemPartsReplaced)
		// "
		+ "and ti.task.name= :taskName and ti.actorId = :actorId ";

	final String PREVIEW_TASK_LIST_FOR_SUPPLIER_QUERY = "select ti from "
			+ "RecoveryClaim recoveryClaim, TaskInstance ti "
			+ "where ti.isOpen = true " + "and ti.claimId = recoveryClaim.id "
			+ "and recoveryClaim.contract.supplier.id = :supplierId "
			+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";
	
	final String PREVIEW_TASK_LIST_FOR_ADDPARTS = "select ti from "
		+ "BasePartReturn partReturn, TaskInstance ti, SupplierPartReturn spr "
		+ "where ti.isOpen = true " 
		+ "and ti.partReturnId = partReturn.id and spr.id = partReturn.id "
		+ "and partReturn.returnLocation  = :locationId "
		+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";

	final String PREVIEW_TASK_LIST_FOR_DUE_PART_QUERY = "select ti from "
		+ "RecoveryClaim recoveryClaim, TaskInstance ti "
		+ "where ti.isOpen = true " + "and ti.claimId = recoveryClaim.id "
		+ "and ti.claimId = :id "
		+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";
	
	final String PREVIEW_TASK_LIST_FOR_SUPPLIER_PARTS_RECEIPT_QUERY = "select ti from "
			+ "TaskInstance ti "
			+ "where ti.isOpen = true "
			+ "and ti.claimId = :id "
			+ "and ti.task.name= :taskName ";

	final String DUE_PART_VIEW_QUERY = "from TaskInstance task, "
		+ "RecoveryClaim recoveryClaim ,Claim claim "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = recoveryClaim.id and "
		+ "task.actorId = :actorId and "
		+ "recoveryClaim.claim = claim ";

	final String PREVIEW_TASK_LIST_FOR_SUPPLIER_LOCATION_QUERY = "select ti from "
		+ "RecoveryClaim recoveryClaim, TaskInstance ti, "
		+ "SupplierPartReturn supplierPartReturn "
		+ "where ti.isOpen = true "
		+ "and ti.claimId = recoveryClaim.id "
		+ "and ti.partReturnId = supplierPartReturn.id "
//		+ "and ti.actorId = :actorId "
		+ "and recoveryClaim.id= :recClaimId "
		+ "and ti.task.name= :taskName ";

	final String PREVIEW_TASK_LIST_FOR_SHIPMENT_QUERY = "select ti from "
			+ "OEMPartReplaced part, TaskInstance ti, HibernateLongInstance vi, ModuleInstance mi "
			+ "where ti.isOpen = true " + "and ti.taskMgmtInstance = mi "
			+ "and mi.processInstance = vi.processInstance "
			+ "and vi.name = 'supplierPart' " + "and vi.class = 'H' "
			+ "and vi.value.id = part.id "
			+ "and part.supplierShipment.id = :shipmentId "
			+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";

	final String PREVIEW_TASK_LIST_SHIPMENT_CLAIM_QUERY = "select ti from "
		+ "TaskInstance ti, RecoveryClaim recoveryClaim "
		+ "where ti.isOpen = true "
		+ "and ti.claimId = recoveryClaim.id "
		// + "and part in
		// elements(claim.serviceInformation.serviceDetail.oemPartsReplaced)
		// "
		+ "and recoveryClaim.id = :claimId "
		+ "and recoveryClaim.claim.serviceInformation.oemPartReplaced.supplierShipment.id = :shipmentId "
		+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";

	final String PREVIEW_TASK_LIST_SUPPLIER_CLAIM_QUERY = "select ti from "
		+ "TaskInstance ti, RecoveryClaim recoveryClaim "
		+ "where ti.isOpen = true " + "and ti.claimId = recoveryClaim.id "
		+ "and recoveryClaim.id = :claimId "
		+ "and ti.task.name= :taskName " + "and ti.actorId = :actorId ";
	
	final String PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY_AT_BARCODE_LEVEL = "from TaskInstance taskInstance, "
		+ "RecoveryClaim recoveryClaim ,Claim claim join claim.claimedItems as claimedItems , " 
		+ "OEMPartReplaced opr ,BasePartReturn basePartReturn, SupplierPartReturn supplierPartReturn "
		+ "where taskInstance.isOpen = true "
		+ "and recoveryClaim.claim = claim "
		+ "and taskInstance.claimId = recoveryClaim.id "
		+ "and taskInstance.partReturnId = basePartReturn.id "
		+ "and basePartReturn.id = supplierPartReturn.id "
		+ "and supplierPartReturn.recoverablePart.oemPart = opr "
		+ "and taskInstance.task.name= :taskName "
		+ "and taskInstance.actorId = :actorId ";

	final String NOT_SHIPPED_PARTS_FOR_PART = "select task from TaskInstance task, "
			+ "HibernateLongInstance var, "
			+ "ModuleInstance mi, "
			+ "OEMPartReplaced part "
			+ "where task.isOpen = true and "
			+ "task.task.name in ( '"
			+ WorkflowConstants.DUE_PARTS_TASK
			+ "', '"
			+ WorkflowConstants.SHIPMENT_GENERATED
			+ "' , '"
			+ WorkflowConstants.AWAITING_SHIPMENT
			+ "', '"
			+ WorkflowConstants.AWAITING_SHIPMENT_TO_WAREHOUSE
			+ "' , ' "
			+ WorkflowConstants.PART_RECEIVED_SCHEDULER
			+ "') "
			+ "and "
			+ "task.taskMgmtInstance = mi and "
			+ "mi.processInstance = var.processInstance and "
			+ "var.class = 'H' and "
			+ "var.value.id = part.id and "
			+ "var.name = 'supplierPart' and "
			+ " part.id = :partId";

	final String NOT_SHIPPED_PARTS_FOR_RECOVERY_CLAIM = "select task from TaskInstance task, "
		+ "RecoveryClaim recoveryClaim "
		+ "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED
		+ "' , '"
		+ WorkflowConstants.AWAITING_SHIPMENT
		+ "', '"
		+ WorkflowConstants.SUPPLIER_PARTS_CLAIMED
		+ "' , ' "
		+ WorkflowConstants.AWAITING_SHIPMENT_TO_WAREHOUSE
		+ "' , ' "
		+ WorkflowConstants.PART_RECEIVED_SCHEDULER
		+ "' , ' "
		+ WorkflowConstants.PART_NOT_IN_WAREHOUSE
		+ "') "
		+ "and task.claimId = recoveryClaim.id "
		+ "and recoveryClaim.id = :recClaimId";

	final String DUE_PARTS_NOT_GENERATED_FOR_CLAIM = "select task from TaskInstance task, "
		+ "RecoveryClaim recoveryClaim "
		+ "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.AWAITING_SHIPMENT
		+ "', '"
		+ WorkflowConstants.PART_NOT_IN_WAREHOUSE
		+ "' ) "
		+ "and task.claimId = recoveryClaim.id";
	
	final String TASKS_LIST_FOR_SUPPLIER_PART_RETURNS = "select task from TaskInstance task, SupplierPartReturn spr "
		+ "where task.isOpen = true "
		+ "and task.task.name = :taskName "
		+ "and task.partReturnId = spr.id "
		+ "and spr in (:supplierPartReturns) ";

	
	final String TASK_FOR_SUPPLIER_PART_RETURN = "select task from TaskInstance task, SupplierPartReturn spr "
		+ "where task.isOpen = true "
		+ "and task.task.name = :taskName "
		+ "and task.partReturnId = spr.id "
		+ "and spr = (:supplierPartReturn) ";
	
	final String RECOVERED_PARTS_DETAIL_QUERY = "select distinct supplierPartReturn.recoverablePart from "
		+ "RecoveryClaim recoveryClaim, TaskInstance ti, "
		+ "SupplierPartReturn supplierPartReturn "
		+ "where ti.isOpen = true " 
		+ "and ti.claimId = recoveryClaim.id " + "and ti.claimId = :id "
		+ "and ti.partReturnId = supplierPartReturn.id "
		+ "and ti.task.name= :taskName " 
		+ "and ti.actorId = :actorId ";
	
	final String SUPPLIER_PART_RETURNS_FOR_OEMPART = "select supplierPartReturn from TaskInstance task, "
		+ "SupplierPartReturn supplierPartReturn , RecoveryClaim recoveryClaim, "
		+ "where task.isOpen = true "
		+ "and task.task.name = '" + WorkflowConstants.AWAITING_SHIPMENT_TO_WAREHOUSE + "' "
		+ "and task.claimId = recoveryClaim.id  "
		+ "and task.partReturnId = supplierPartReturn.id "
		+ "and recoveryClaim.claim = :claim "
		+ "and recoveryClaim.contract = :contract "
		+ "and supplierPartReturn.recoverablePart.oemPart = :oemPart ";

    final String PART_SHIPPER_SUPPLIER_LOCATION_VIEW_QUERY = "from TaskInstance taskInstance, "
            + "RecoveryClaim recoveryClaim ,Claim claim, "
            + "BasePartReturn basePartReturn, SupplierPartReturn supplierPartReturn "
            + "where taskInstance.isOpen = true "
            + "and recoveryClaim.claim = claim "
            + "and taskInstance.claimId = recoveryClaim.id "
            + "and taskInstance.partReturnId = basePartReturn.id "
            + "and basePartReturn.id = supplierPartReturn.id "
            + "and taskInstance.task.name= :taskName "
            //+ "and supplierPartReturn.recoverablePart.oemPart = opr "
            + "and ((supplierPartReturn.recoverablePart.oemPart in (select pr.oemPartReplaced from BasePartReturn pr,Warehouse w,User u "
            + "where pr.returnLocation=w.location "
            + "and u.name= :actorId and u in elements(w.partShippers))) "
            + "or taskInstance.actorId = :actorId )";

    final String PART_SHIPPER_RECOVERY_CLAIM_VIEW_QUERY = "from TaskInstance taskInstance, "
            + "RecoveryClaim recoveryClaim ,Claim claim, "
            + "BasePartReturn basePartReturn, SupplierPartReturn supplierPartReturn "
            + "where taskInstance.isOpen = true "
            + "and recoveryClaim.claim = claim "
            + "and taskInstance.claimId = recoveryClaim.id "
            + "and taskInstance.partReturnId = basePartReturn.id "
            + "and basePartReturn.id = supplierPartReturn.id "
            + "and taskInstance.task.name= :taskName "
            + "and ((supplierPartReturn.recoverablePart.oemPart in (select pr.oemPartReplaced from BasePartReturn pr,Warehouse w,User u "
            + "where pr.returnLocation=w.location "
            + "and u.name= :actorId and u in elements(w.partShippers))) or taskInstance.actorId = :actorId)";


    final String SHIPMENT_LIST_FOR_SUPPLIER_PART_RETURNS = "select distinct(shipment) from TaskInstance task, SupplierPartReturn spr, "
            + "Shipment shipment where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.claimId = :recClaimId "
            + "and task.partReturnId = spr.id "
            + "and spr.supplierShipment = shipment.id ";

    final String TASK_LIST_FOR_SUPPLIER_PART_RETURNS = "select distinct(task) from TaskInstance task, SupplierPartReturn spr, "
            + "Shipment shipment where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.claimId = :recClaimId "
            + "and task.partReturnId = spr.id "
            + "and spr.supplierShipment = shipment.id ";


    final String PART_RECEIVER_RECOVERY_VIEW_QUERY = "from TaskInstance taskInstance, "
            + "RecoveryClaim recoveryClaim ,Claim claim, "
            + "BasePartReturn basePartReturn, SupplierPartReturn supplierPartReturn, Shipment shipment "
            + "where taskInstance.isOpen = true "
            + "and recoveryClaim.claim = claim "
            + "and taskInstance.claimId = recoveryClaim.id "
            + "and taskInstance.partReturnId = basePartReturn.id "
            + "and basePartReturn.id = supplierPartReturn.id "
            + "and taskInstance.task.name= :taskName and shipment.id=supplierPartReturn.supplierShipment "
            //+ "and supplierPartReturn.recoverablePart.oemPart = opr "
            + "and supplierPartReturn.recoverablePart.oemPart in (select pr.oemPartReplaced from BasePartReturn pr,Warehouse w,User u "
            + "where pr.returnLocation=w.location "
            + "and u.name= :actorId and u in elements(w.recievers)) ";
    // + "and taskInstance.actorId = :actorId ";

    final String PART_INSPECTOR_RECOVERY_VIEW_QUERY = "from TaskInstance taskInstance, "
            + "RecoveryClaim recoveryClaim ,Claim claim, "
            + "BasePartReturn basePartReturn, SupplierPartReturn supplierPartReturn, Shipment shipment "
            + "where taskInstance.isOpen = true "
            + "and recoveryClaim.claim = claim "
            + "and taskInstance.claimId = recoveryClaim.id "
            + "and taskInstance.partReturnId = basePartReturn.id "
            + "and basePartReturn.id = supplierPartReturn.id "
            + "and taskInstance.task.name= :taskName and shipment.id=supplierPartReturn.supplierShipment "
            //+ "and supplierPartReturn.recoverablePart.oemPart = opr "
            + "and supplierPartReturn.recoverablePart.oemPart in (select pr.oemPartReplaced from BasePartReturn pr,Warehouse w,User u "
            + "where pr.returnLocation=w.location "
            + "and u.name= :actorId and u in elements(w.inspectors)) ";
    // + "and taskInstance.actorId = :actorId ";
    
    final String ROUTED_TO_NMHG_QUERY = "from TaskInstance taskInstance, RecoveryClaim recoveryClaim " +
    		"where taskInstance.task.name = :taskName and taskInstance.isOpen = true and recoveryClaim.id = taskInstance.claimId";
    
    final String ROUTED_TASKS_FOR_REC_CLAIM = "from TaskInstance taskInstance where taskInstance.task.name = :taskName " +
    		"and taskInstance.isOpen = true and taskInstance.claimId = :recClaimId";
    
    final String REC_CLAIM_TASK_INSTANCE_ASSIGNED_TO = " from TaskInstance taskInstance, RecoveryClaim recoveryClaim " +
    		"where taskInstance.task.name = :taskName and taskInstance.isOpen = true and recoveryClaim.id = taskInstance.claimId " +
    		"and taskInstance.actorId = :actorId ";

    final String PART_RETURN_INSTANCES = "select task from TaskInstance task, "
            + "RecoveryClaim recoveryClaim "
            + "where task.isOpen = true "
            + "and task.task.name = '" + WorkflowConstants.CONFIRM_PART_RETURNS + "' "
            + "and task.claimId = recoveryClaim.id "
            + "and recoveryClaim.id = :recClaimId ";

    final String CONFIRM_DEALER_PART_RETURN_INSTANCES = "select task from TaskInstance task, "
            + "Claim claim "
            + "where task.isOpen = true "
            + "and task.task.name = '" + WorkflowConstants.CONFIRM_DEALER_PART_RETURNS + "' "
            + "and task.claimId = claim.id "
            + "and claim.id = :claimId ";

    final String SUPPLIER_SHIPMENT_GENERATED_INSTANCES = "select task from TaskInstance task, "
            + "RecoveryClaim recoveryClaim "
            + "where task.isOpen = true "
            + "and task.task.name = '" + WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED + "' "
            + "and task.claimId = recoveryClaim.id "
            + "and recoveryClaim.id = :recClaimId ";

    final String SHIPMENT_GENERATED_INSTANCES = "select task from TaskInstance task, "
            + "Claim claim, BasePartReturn bp, OEMPartReplaced opr "
            + "where task.isOpen = true "
            + "and task.task.name = '" + WorkflowConstants.SHIPMENT_GENERATED + "' "
            + "and task.claimId = claim.id "
            + "and claim.id = :claimId and  bp.id=task.partReturnId and bp.oemPartReplaced = opr.id and opr.returnDirectlyToSupplier = true";

    final String TASK_INSTANCE_FOR_TO_BE_SHIPPED_PARTS_FROM_NMHG = "select task from TaskInstance task, SupplierPartReturn partReturn "
            + "where task.isOpen = true and "
            + "task.task.name = :taskName and "
            + "task.partReturnId = partReturn.id "
            + "and partReturn.recoverablePart in ( :tobeShippedParts ) ";

    final String TASK_INSTANCE_FOR_TO_BE_SHIPPED_PARTS_FROM_NMHG_FOR_SUPPLIER = "select task from TaskInstance task, SupplierPartReturn partReturn "
            + "where task.isOpen = true and "
            + "task.task.name = :taskName and "
            + "task.partReturnId = partReturn.id "
            + "and partReturn.recoverablePart =  :tobeShippedParts";
}


/**
 * 
 */
package tavant.twms.worklist.partreturn;

import tavant.twms.jbpm.WorkflowConstants;

/**
 * @author kannan.ekanath
 * 
 */
public interface PartReturnQueries {
	
		//+ "and ( claim.forDealer = :dealerId or (:dealerId = -1 and task.actorId = :actorId ) "
		//+ "or (claim.forDealer not in (:dealerIds) " +
		//	" and claim.forDealer in (select tp from ThirdParty tp) " +
		//" and claim.filedBy in " +
		//" (select users from Organization org join org.users users where org= :dealerId))) ";
	
	//The above where clause is moved to PartReturnWorkListDaoImpl as we dnt need all the 3 clauses always. We can add them conditionally
	//based on if the user is internal or third party applicable dealer or a normal dealer
	
	final String LOCATION_VIEW_FROM_CLAUSE = "from TaskInstance task, "
			+ "BasePartReturn pr, " + "Location location ";
	
	final String LOCATION_VIEW_WHERE_CLAUSE = "where task.isOpen = true and "
			+ "task.task.name = :taskName "
			+ "and task.partReturnId = pr.id and "
			+ "(pr.class != tavant.twms.domain.supplier.SupplierPartReturn ) "
			+ "and pr.returnLocation = location.id ";
	
	final String LOCATION_VIEW_FOR_DEALER_QUERY = LOCATION_VIEW_FROM_CLAUSE + ", Claim claim " 
			+ LOCATION_VIEW_WHERE_CLAUSE + " and task.claimId = claim.id ";
	

	final String LOCATION_VIEW_QUERY = LOCATION_VIEW_FROM_CLAUSE
			+ LOCATION_VIEW_WHERE_CLAUSE
			+ " and task.actorId = :actorId ";
	
	final String PREVIEW_LOCATION_QUERY = "select task " + LOCATION_VIEW_FROM_CLAUSE + ", Claim claim "
	+ LOCATION_VIEW_WHERE_CLAUSE + " and location.id = :id and task.claimId = claim.id";
	
	
	final String CLAIM_VIEW_QUERY_COMMON = "from TaskInstance task, Claim claim "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id  ";
	
	
	final String CLAIM_VIEW_QUERY = CLAIM_VIEW_QUERY_COMMON + " and task.actorId = :actorId ";
	
	final String PREVIEW_CLAIM_QUERY = "select task " + CLAIM_VIEW_QUERY_COMMON
		+ " and claim.id = :id ";
	
	final String PREVIEW_CLAIM_QUERY_FOR_INSPECTION = "select task " //+ CLAIM_VIEW_QUERY_COMMON
		+ "from TaskInstance task, "
		+ "Claim claim, BasePartReturn pr "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id "
		+ "and task.partReturnId = pr.id "
		+ "and pr.returnLocation in ( select w.location from Warehouse w,User u " +
				"where u in elements (w.inspectors) and u.name = :actorId )"
		+ " and claim.id = :id ";
	
	final String PREVIEW_CLAIM_QUERY_FOR_RECEIPT = "select task " 
		+ "from TaskInstance task, "
		+ "Claim claim, BasePartReturn pr "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id and "
		+ "task.partReturnId = pr.id "
		+ "and pr.returnLocation in ( select w.location from Warehouse w,User u " +
				"where u in elements (w.recievers) and u.name = :actorId )"
		+ " and claim.id = :id ";
	
	final String SHIPMENT_VIEW_FROM_CLAUSE = "from TaskInstance task, "
			+ "BasePartReturn pr, Shipment shipment ";
		
		final String SHIPMENT_VIEW_WHERE_CLAUSE = "where task.isOpen = true and task.task.name = :taskName "
			+ "and pr.shipment = shipment.id "
			+ "and task.partReturnId = pr.id ";
		
		final String SHIPMENT_VIEW_FOR_DEALER_QUERY = SHIPMENT_VIEW_FROM_CLAUSE + ", Claim claim "
			+ SHIPMENT_VIEW_WHERE_CLAUSE + " and task.claimId = claim.id ";
	
	final String MASTER_SUPPLIER_CONFIRM_DEALER_PARTRETURNS_VIEW_QUERY = "from TaskInstance taskInstance, PartReturn partReturn, "
			+ "RecoveryClaim recoveryClaim, Claim claim, Shipment shipment "
			+ "where taskInstance.isOpen = true and taskInstance.partReturnId = partReturn.id and shipment.id=partReturn.shipment "
			+ "and taskInstance.claimId = claim.id and claim.id = recoveryClaim.claim "
			+ "and taskInstance.task.name= :taskName ";
	
	final String SUPPLIER_CONFIRM_DEALER_PARTRETURNS_VIEW_QUERY = "from TaskInstance taskInstance, PartReturn partReturn, "
			+ "RecoveryClaim recoveryClaim, Claim claim, Shipment shipment "
			+ "where taskInstance.isOpen = true and taskInstance.partReturnId = partReturn.id and shipment.id=partReturn.shipment "
			+ "and taskInstance.claimId = claim.id and claim.id = recoveryClaim.claim "
			+ "and taskInstance.task.name= :taskName and taskInstance.actorId = :actorId ";
	
	final String SHIPMENT_VIEW_QUERY = SHIPMENT_VIEW_FROM_CLAUSE 
		+ SHIPMENT_VIEW_WHERE_CLAUSE
		+ " and task.actorId = :actorId ";
	
	final String PREVIEW_SHIPMENT_QUERY = "select task " 
		+ SHIPMENT_VIEW_FROM_CLAUSE //+ SHIPMENT_VIEW_WHERE_CLAUSE
		+ "where task.isOpen = true and task.task.name = :taskName "
		+ "and pr.shipment = shipment.id "
		+ "and task.partReturnId = pr.id "
		+ " and shipment.id = :id ";
	
	final String PREVIEW_SHIPMENT_QUERY_PRINT = "select task " 
			+ SHIPMENT_VIEW_FROM_CLAUSE //+ SHIPMENT_VIEW_WHERE_CLAUSE
			+ "where  task.task.name = :taskName "
			+ "and pr.shipment = shipment.id "
			+ "and task.partReturnId = pr.id "
			+ " and shipment.id = :id ";
	
	final String DUE_PARTS_RECEIPT_SHIPMENT_VIEW_QUERY = "from TaskInstance task, "
		+ "BasePartReturn pr, Shipment shipment "
		+ "where task.isOpen = true and task.task.name = :taskName "
		+ "and task.partReturnId = pr.id "
		+ "and pr.shipment = shipment.id "
		+ "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.recievers) )";
	
	final String DUE_PARTS_RECEIPT_CLAIM_VIEW_QUERY = "from TaskInstance task, "
		+ "Claim claim, BasePartReturn pr "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id "
		+ "and task.partReturnId=pr.id "
		+ "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.recievers) )";

	final String DUE_PARTS_INSPECTION_SHIPMENT_VIEW_QUERY = "from TaskInstance task, "
		+ "BasePartReturn pr, Shipment shipment "
		+ "where task.isOpen = true and task.task.name = :taskName "
		+ "and pr.shipment = shipment.id "
		+ "and task.partReturnId = pr.id "
		+ "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.inspectors) )";
	
	final String DUE_PARTS_INSPECTION_CLAIM_VIEW_QUERY = "from TaskInstance task, "
		+ "Claim claim, BasePartReturn pr "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id and " 
		+ "task.partReturnId=pr.id "
		+ "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.inspectors) )";
	
	
	
	/*
	final String LOCATION_VIEW_QUERY = "from TaskInstance task, "
			+ "HibernateLongInstance var, " + "ModuleInstance mi, "
			+ "BasePartReturn pr, " + "Location location "
			+ "where task.isOpen = true and "
			+ "task.task.name = :taskName and "
			+ "task.taskMgmtInstance = mi and "
			+ "mi.processInstance = var.processInstance and "
			+ "var.class = 'H' and "
			+ "task.actorId = :actorId and var.value.id = pr.id and "
			+ "var.name = 'partReturn' and "
			+ "(pr.class != tavant.twms.domain.supplier.SupplierPartReturn ) "
			+ "and pr.returnLocation = location.id ";
	
	final String CLAIM_VIEW_QUERY = "from TaskInstance task, "
			+ "HibernateLongInstance var, " + "ModuleInstance mi, Claim claim "
			+ "where task.isOpen = true and "
			+ "task.task.name = :taskName and "
			+ "task.taskMgmtInstance = mi and "
			+ "mi.processInstance = var.processInstance and "
			+ "var.class = 'H' and " + "task.actorId = :actorId and "
			+ "var.value.id = claim.id and " + "var.name = 'claim' ";

	final String SHIPMENT_VIEW_QUERY = "from TaskInstance task, HibernateLongInstance var, "
			+ "ModuleInstance mi, BasePartReturn pr, Shipment shipment "
			+ "where task.isOpen = true and task.task.name = :taskName "
			+ "and task.taskMgmtInstance = mi and mi.processInstance = var.processInstance "
			+ "and var.class = 'H' and task.actorId = :actorId "
			+ "and (pr.shipment = shipment.id OR pr.oemPartReplaced.supplierShipment = shipment.id)"
			+ "and var.value.id = pr.id and var.name = 'partReturn' ";
	
	final String PREVIEW_LOCATION_QUERY = "select task " + LOCATION_VIEW_QUERY
			+ "and location.id = :id ";
	
	final String PREVIEW_CLAIM_QUERY = "select task " + CLAIM_VIEW_QUERY
			+ " and claim.id = :id ";

	final String PREVIEW_SHIPMENT_QUERY = "select task " + SHIPMENT_VIEW_QUERY
			+ " and shipment.id = :id ";

	*/
	
	final String DUE_AND_OVERDUE_PARTS_FOR_LOCATION = "select task from TaskInstance task, "
		+ "BasePartReturn pr, "
		+ "Shipment obj, Claim claim "
		+ "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.DUE_PARTS_TASK
		+ "', '"
		+ WorkflowConstants.OVERDUE_PARTS_TASK
		+ "' ) and "
		+ "task.partReturnId = pr.id and "
		+ "pr.returnLocation = obj.destination.id "
		+ " and obj.id = :shipmentId "
		+ " and task.claimId = claim.id";

	final String NOT_SHIPPED_PARTS_FOR_CLAIM = "select task from TaskInstance task, Claim claim "
		+ "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.DUE_PARTS_TASK
		+ "', '"
		+ WorkflowConstants.OVERDUE_PARTS_TASK
		+ "', '"
		+ WorkflowConstants.SHIPMENT_GENERATED
		+ "' , '"
        + WorkflowConstants.WPRA_TO_BE_GENERATED
        + "' , '"
        + WorkflowConstants.PREPARE_DUE_PARTS
        + "' , '"
        + WorkflowConstants.CEVA_TRACKING
        + "' ) and "
		+ "task.claimId = claim.id and "
		+ "claim.id = :claimId";

    final String TASK_INSTANCE_FOR_REMOVED_PARTS = "select task from TaskInstance task, PartReturn partReturn "
        + "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.DUE_PARTS_TASK
		+ "', '"
		+ WorkflowConstants.OVERDUE_PARTS_TASK
		+ "','"
		+ WorkflowConstants.THIRD_PARTY_DUE_PARTS_TASK
		+ "','"
		+ WorkflowConstants.SHIPMENT_GENERATED
        + "' , '"
        + WorkflowConstants.WPRA_TO_BE_GENERATED
        + "' , '"
        + WorkflowConstants.GENERATED_WPRA
        + "' , '"
        + WorkflowConstants.PREPARE_DUE_PARTS
        + "', '"
        + WorkflowConstants.CEVA_TRACKING
		+"') and "   
		+ "task.partReturnId = partReturn.id "
        + "and partReturn.oemPartReplaced in ( :removedParts ) ";

    final String PART_RETURN_TIMER_FOR_TASK_INSTANCE = "SELECT timer from Timer timer where taskInstance = :taskInstance" ;

    //Added for Rejected Part Return

    final String REJECTED_PARTS_QUERY = "select distinct task from TaskInstance task, Claim claim "
		+ "where task.isOpen = true and "
		+ "task.task.name = :taskName and "
		+ "task.claimId = claim.id  ";

   final String PART_SHIPPER_LOCATION_VIEW_FROM_CLAUSE = "from TaskInstance task, "
			+ "ServiceProvider serviceprovider, BasePartReturn pr ";

    final String PART_SHIPPER_LOCATION_VIEW_WHERE_CLAUSE = "where task.isOpen = true and "
			+ "task.task.name = :taskName "
			+ "and claim.forDealer.id = serviceprovider.id "
			+ "and task.partReturnId = pr.id and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.partShippers) )";
    
    final String PART_RETURN_DEALER_WHERE_CLAUSE = " and claim.forDealer.id=:id ";

   final String LOCATION_VIEW_FOR_PART_SHIPPER_QUERY = PART_SHIPPER_LOCATION_VIEW_FROM_CLAUSE + ", Claim claim "
			+ PART_SHIPPER_LOCATION_VIEW_WHERE_CLAUSE + " and task.claimId = claim.id ";

   final String PREVIEW_DEALER_LOCATION_QUERY = "select task " + PART_SHIPPER_LOCATION_VIEW_FROM_CLAUSE + ", Claim claim "
	+ PART_SHIPPER_LOCATION_VIEW_WHERE_CLAUSE + " and task.claimId = claim.id";

	final String PREPARE_DUE_PARTS_QUERY = " from Claim claim "
			+ "join claim.activeClaimAudit claimAudit "
			+ "join claimAudit.serviceInformation si "
			+ "join si.serviceDetail sd "
			+ "join sd.hussmanPartsReplacedInstalled hpri "
			+ "join hpri.replacedParts opr "
			+ "join opr.partReturns bprs "
			+ "join bprs.returnLocation location "
			+ "where bprs.status = 'PART_TO_BE_SHIPPED' "
			+ "and claimAudit.state in ('PROCESSOR_REVIEW', 'SUBMITTED', 'ON_HOLD') ";

    final String PROCESSOR_WPRA_VIEW_FROM_CLAUSE = "from TaskInstance task, "
			+ "Wpra wpra, PartReturn pr";
    
    final String PROCESSOR_WPRA_VIEW_FROM_CLAUSE_DEALERSHIP = "from TaskInstance task, "
			+ "Wpra wpra, PartReturn pr";

    final String PROCESSOR_WPRA_VIEW_WHERE_CLAUSE = "where task.isOpen = true and "
			+ "task.task.name = :taskName "
			+ "and  pr.wpra= wpra.id "
            + "and task.partReturnId = pr.id";

    final String WPRA_VIEW_FOR_PROCESSOR_QUERY = PROCESSOR_WPRA_VIEW_FROM_CLAUSE + ", Claim claim "
			+ PROCESSOR_WPRA_VIEW_WHERE_CLAUSE + " and task.claimId = claim.id ";
    
    
    final String PROCESSOR_WPRA_VIEW_WHERE_CLAUSE_BY_ACTOR_ID = "where task.isOpen = true and "
			+ "task.task.name = :taskName "
			+ "and  pr.wpra= wpra.id "
            + "and task.partReturnId = pr.id "
            + "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.recievers) )";

    final String WPRA_VIEW_FOR_PROCESSOR_QUERY_BY_ACTOR_ID = PROCESSOR_WPRA_VIEW_FROM_CLAUSE + ", Claim claim "
			+ PROCESSOR_WPRA_VIEW_WHERE_CLAUSE_BY_ACTOR_ID + " and task.claimId = claim.id ";
    
    
    final String PROCESSOR_WPRA_VIEW_WHERE_CLAUSE_BY_DEALERSHIP = "where task.isOpen = true and "
			+ "task.task.name = :taskName "
			+ "and  pr.wpra= wpra.id "
            + "and task.partReturnId = pr.id "
            + "and wpra.shippedBy = :dealerId";

    final String WPRA_VIEW_FOR_PROCESSOR_QUERY_BY_DEALERSHIP = PROCESSOR_WPRA_VIEW_FROM_CLAUSE_DEALERSHIP + ", Claim claim "
			+ PROCESSOR_WPRA_VIEW_WHERE_CLAUSE_BY_DEALERSHIP + " and task.claimId = claim.id";
    
   /* final String WPRA_SEARCH_FOR_PROCESSOR_QUERY ="from Wpra wpra, BasePartReturn pr" + ", Shipment shipment "
    		+ "where pr.wpra= wpra.id and pr.shipment = shipment.id ";*/
    
   /* final String WPRA_SEARCH_FOR_PROCESSOR_QUERY ="from Wpra wpra ,PartReturn as pr ,Shipment as shipment "
	+ "where pr.wpra= wpra.id and pr.shipment = shipment.id ";*/
    
   //NotWorking
    final String WPRA_SEARCH_FOR_PROCESSOR_QUERY ="from Wpra as wpra,PartReturn as pr right outer join pr.shipment as shipment where pr.shipment = shipment.id  "
    	+ "and pr.wpra= wpra.id";
    //working not for outer
   /* final String WPRA_SEARCH_FOR_PROCESSOR_QUERY ="from Wpra as wpra left outer join PartReturn as pr,pr.shipment as shipment "
	+ "where pr.wpra= wpra.id and pr.shipment = shipment.id ";*/
    

	//final String WPRA_SEARCH_FOR_PROCESSOR_QUERY = "select * from wpra wpra, part_return pr, shipment shipment where pr.wpra= wpra.id and pr.shipment = shipment.id(+)";
    
    
    final String WPRA_VIEW_FROM_CLAUSE = "from TaskInstance task, "
		+ "BasePartReturn pr, Wpra wpra ";

    final String PREVIEW_WPRA_QUERY = "select task "
		+ WPRA_VIEW_FROM_CLAUSE
		+ "where task.isOpen = true and  task.task.name = :taskName "
		+ "and pr.wpra = wpra.id "
		+ "and task.partReturnId = pr.id "
		+ " and wpra.id = :id ";
    
    final String PREVIEW_WPRA_QUERY_PRINT = "select task "
    		+ WPRA_VIEW_FROM_CLAUSE
    		+ "where  task.task.name = :taskName "
    		+ "and pr.wpra = wpra.id "
    		+ "and task.partReturnId = pr.id "
    		+ " and wpra.id = :id ";
        
    
    
    final String PREVIEW_WPRA_QUERY_BY_ACTOR_ID = "select task "
    		+ WPRA_VIEW_FROM_CLAUSE
    		+ "where task.isOpen = true and task.task.name = :taskName "
    		+ "and pr.wpra = wpra.id "
    		+ "and task.partReturnId = pr.id "
    		+ "and task.actorId = :actorId "
    		+ " and wpra.id = :id ";
    
    final String PREVIEW_WPRA_QUERY_BY_DEALERSHIP = "select task "
    		+ WPRA_VIEW_FROM_CLAUSE
    		+ "where task.isOpen = true and task.task.name = :taskName "
    		+ "and pr.wpra = wpra.id "
    		+ "and task.partReturnId = pr.id "
    		+ "and wpra.shippedBy = :dealerId "
    		+ " and wpra.id = :id ";


   final String TASK_INSTANCE_FOR_PREPARE_DUE_PARTS = "select task from TaskInstance task, PartReturn partReturn "
        + "where task.isOpen = true and "
		+ "task.task.name = 'Prepare Due Parts'  and "
		+ "task.partReturnId = partReturn.id "
        + "and partReturn.oemPartReplaced in ( :removedParts ) ";

    final String TASK_INSTANCE_FOR_PREPARE_DUE_PARTS_AND_WPRA = "select task from TaskInstance task, PartReturn partReturn "
        + "where task.isOpen = true and "
		+ "task.task.name in ( '"
		+ WorkflowConstants.WPRA_TO_BE_GENERATED
		+ "', '"
		+ WorkflowConstants.PREPARE_DUE_PARTS
		+"') and "
		+ "task.partReturnId = partReturn.id "
        + "and partReturn.oemPartReplaced in ( :removedParts ) ";

    final String TASK_INSTANCE_FOR_WPRA = "select task from TaskInstance task "
        + "where task.isOpen = true and "
		+ "task.task.name = 'Required Parts From Dealer' ";
    
    final String TASK_INSTANCE_FOR_WPRA_BETWEEN_DATE = "select task from TaskInstance task "
        + "where task.isOpen = true and "
		+ "task.task.name = 'Required Parts From Dealer' and "
		+ "task.create between to_date(:startDate,'DD-MON-YY') AND to_date(:endDate,'DD-MON-YY')";

    final String TASK_INSTANCE_FOR_PREPAREDUEPART = "select task from TaskInstance task "
        + "where task.isOpen = true and "
		+ "task.task.name = 'Prepare Due Parts' ";
    
    final String TASK_INSTANCE_FOR_PREPAREDUEPART_BETWEEN_DATE= "select task from TaskInstance task "
        + "where task.isOpen = true and "
		+ "task.task.name = 'Prepare Due Parts' and "
		+ "task.create between to_date(:startDate,'DD-MON-YY') AND to_date(:endDate,'DD-MON-YY')";

    final String TASK_FOR_SUPPLIER_DEALER_PART_RETURN = "select task from TaskInstance task, PartReturn pr "
            + "where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and pr = (:partReturn) ";

    final String TASK_FOR_SHIPMENT_GENERATED_WPRA_VIEW = "from TaskInstance task, PartReturn pr, Claim claim, Wpra wpra "
            + "where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and task.claimId = claim.id "
            + "and pr.wpra = wpra.id ";

    final String SHIPMENT_GENERATED_TASK_INSTANCE_FOR_WPRA = "select task from TaskInstance task, Shipment s, PartReturn pr "
            + "where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and pr.shipment = s.id "
            + "and pr.wpra.id = (:wpra)";

    final String LOCATION_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY ="from TaskInstance task, ServiceProvider serviceprovider, Claim claim, "
            + "BasePartReturn pr "
            + "where task.isOpen = true and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and claim.forDealer.id = serviceprovider.id "
            + "and task.claimId = claim.id "
            + "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.partShippers) )";

    final String CLAIM_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY ="from TaskInstance task, ServiceProvider serviceprovider, Claim claim, "
            + "BasePartReturn pr "
            + "where task.isOpen = true and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and task.claimId = claim.id "
            + "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.partShippers) )";

    final String SHIPMENT_VIEW_FOR_PART_SHIPPER_TODEALER_QUERY ="from TaskInstance task, Claim claim, Shipment shipment, "
            + "BasePartReturn pr "
            + "where task.isOpen = true "
            + "and task.task.name = :taskName "
            + "and task.partReturnId = pr.id "
            + "and shipment.id = pr.shipment "
            + "and task.claimId = claim.id "
            + "and pr.returnLocation in (select w.location from Warehouse w,User u where u.name=:actorId and u in elements(w.partShippers) )";

    final String TASK_INSTANCE_FOR_CLAIMED_PART_RECEIPT_AND_DEALER_PART_SHIPPED = "select task from TaskInstance task, PartReturn partReturn "
            + "where task.isOpen = true and "
            + "task.task.name in ( '"
            + WorkflowConstants.CLAIMED_PARTS_RECEIPT
            + "', '"
            + WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED
            +"') and "
            + "task.partReturnId = partReturn.id "
            + "and partReturn.oemPartReplaced in ( :receivedParts ) ";


    final String TASK_INSTANCE_FOR_NOT_COLLECTED_PARTS = "select task from TaskInstance task "
            + "where task.isOpen = true and "
            + "task.task.name in ( '"
            + WorkflowConstants.CLAIMED_PARTS_RECEIPT
            + "', '"
            + WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED
            +"') and " +
            "sysdate - :daysAllowed > task.create";

    final String TASK_INSTANCE_FOR_OEM_PART_FOR_REJECTED_PARTS_INBOX_FOR_DEALER = "select task from TaskInstance task, PartReturn partReturn "
            + "where task.isOpen = true and "
            + "task.task.name = '"
            + WorkflowConstants.REJETCTED_PARTS_INBOX
            +"' and "
            + "task.partReturnId = partReturn.id "
            + "and partReturn.oemPartReplaced = :oemPart";

    final String TASK_INSTANCE_FOR_PARTS = "select task from TaskInstance task "
            + "where task.isOpen = true and "
            + "task.task.name = :taskName  and "
            + "task.partReturnId in (:partReturnsIds) ";
            //+ "and partReturn.oemPartReplaced in ( :receivedParts ) ";
}

	
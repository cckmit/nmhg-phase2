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
package tavant.twms.jbpm;

/**
 * Any string that is hardcoded in the workflow definition file has to be
 * defined here.
 * 
 * When we change task names etc in the workflow, this would be the *only* place
 * to be fixed
 * 
 * @author kannan.ekanath
 * 
 */
public interface WorkflowConstants {

	/**
	 * Task Names here
	 */
	String PROCESSOR_REVIEW_TASK_NAME = "Processor Review";
	String CLAIM_FAILURE_REPORTS = "Claim Failure Reports";
	String SEND_TO_SUPPLIER = "Send To Supplier";
	String SUPPLIER_RECOVERY_TASK_NAME = "For Recovery";
	String NEW = "New";
	String SUPPLIER_PARTS_CLAIMED = "Supplier Parts Claimed";
	String DUE_PARTS_TASK = "Due Parts";
	String OVERDUE_PARTS_TASK = "Overdue Parts";
	String SRA_REVIEW = "Supplier Response";
	String SHIPMENT_FROM_DEALER_TASK = "Shipment from Dealer";
	String AWAITING_SHIPMENT = "Awaiting Shipment";
	String AWAITING_SUPPLIER_RESPONSE = "Awaiting Supplier Response";
	String PENDING_PAYMENT_RESPONSE = "PaymentWaitTask";
	String PENDING_PART_RETURN = "PartsReturnScheduler";
	String SUPPLIER_IN_PROGRESS_CLAIMS_TASK_NAME = "In Progress";
	String SUPPLIER_DISPUTED_CLAIMS_TASK_NAME = "Disputed";
	String DUE_PARTS_RECEIPT = "Due Parts Receipt";
	String DUE_PARTS_INSPECTION = "Due Parts Inspection";
	String PARTS_SHIPPED = "Parts Shipped";
	String WAIT_FOR_DEBIT = "WaitForDebit";
	String CONFIRM_PART_RETURNS = "Confirm Part Returns";
	String NOT_FOR_RECOVERY = "Not For Recovery";
	String NOT_FOR_RECOVERY_REQUEST = "Not For Recovery Request";
	String NOT_FOR_RECOVERY_RESPONSE = "Not For Recovery Response";
	String PART_NOT_IN_WAREHOUSE = "Part Not In Warehouse";
	String SUPPLIER_SHIPMENMT_GENERATED = "Supplier Shipment Generated";
	String SHIPMENT_GENERATED = "Shipment Generated";
	String AWAITING_SHIPMENT_TO_WAREHOUSE = "Awaiting Shipment To WareHouse";
	String PART_RECEIVED_SCHEDULER = "PartsReceivedScheduler";
	String ON_HOLD = "On Hold";
	String SUPPLIER_ACCEPTED = "Supplier Accepted";
	String ACCEPTED = "Accepted";
	String REOPENED = "Reopened";
	String CLOSED = "Closed";
	String ON_HOLD_FOR_PART_RETURN="On Hold For Part Return";
	String THIRD_PARTY_DUE_PARTS_TASK = "Third Party Due Parts";
	String DRAFT_CLAIM = "Draft Claim";
	String READY_FOR_DEBIT = "Ready For Debit";
	String DEBITED ="Debited";
	String WAITING_FOR_LABOR = "WaitingForLabor";
	String SERVICE_MANAGER_REVIEW = "Service Manager Review";
	String SERVICE_MANAGER_RESPONSE = "Service Manager Response";
	String ADVICE_REQUEST = "Advice Request";
	String CP_REVIEW = "CP Review";
	String FORWARDED_INTERNALLY = "Forwarded Internally";
	String REJECTED_PART_RETURN = "Rejected Part Return";
	String REPLIES = "Replies";
	String TRANSFERRED = "Transferred";
	String FORWARDED = "Forwarded";
	String FORWARDED_EXTERNALLY = "Forwarded Externally";
	String PART_SHIPPED_NOT_RECEIVED = "Part Shipped Not Received";
	String SUPPLIER_PARTS_SHIPPED = "Supplier Parts Shipped";
	String FOR_RECOVERY = "For Recovery";
	String SUPPLIER_RESPONSE ="Supplier Response";
	String REOPENED_CLAIMS = "Reopened Claims";
	String WNTY_CLAIM_REOPENED = "Wnty Claim Reopened";
    String PENDING_REC_INITIATION = "Pending Recovery Initiation";
    String REJETCTED_PARTS_INBOX = "Rejected Parts";
    String DEALER_REQUESTED_PART = "Dealer Requested Part";
    String GENERATE_SHIPMENT_FOR_DEALER ="Generate Shipment For Dealer";
    String SHIPMENT_GENERATED_FOR_DEALER="Shipment Generated For Dealer";
    String PREPARE_DUE_PARTS = "Prepare Due Parts";
    String SUBMIT_SHIPMENT = "SubmitShipment";
    String WPRA_TO_BE_GENERATED ="Required Parts From Dealer";
    String GENERATED_WPRA ="WPRA Generated For Parts";
    String CLAIMED_PARTS_RECEIPT = "Claimed Parts Receipt";
    String DEALER_REQUESTED_PARTS_SHIPPED  = "Dealer Requested Parts Shipped";
    String CANNOT_RECOVER = "Cannot Recover";
    String CONFIRM_DEALER_PART_RETURNS = "Confirm Dealer Part Returns";
    String CEVA_TRACKING = "CEVA Tracking";
    String FORWARD_TO_DEALER="Forward to Dealer";
    String PART_FOR_RETURN_TO_NMHG="Parts for Return To NMHG";
    String SHIPMENT_GENERATED_TO_NMHG = "Shipment Generated To NMHG";
    String PARTS_SHIPPED_TO_NMHG = "Parts Shipped to NMHG";
    String SUPPLIER_PARTS_RECEIPT = "Supplier Parts Receipt";
    String SUPPLIER_PARTS_INSPECTION = "Supplier Parts Inspection";
    String ROUTED_TO_NMHG = "Routed to NMHG";
    String ROUTED_PART_RETURN_ACCEPTED = "Routed Part Return Accepted";
    String ROUTED_PART_RETURN_REJECTED = "Routed Part Return Rejected";

	

	/**
	 * Transition names here
	 */
	String ACCEPT = "Accept";
	String REJECT = "Reject";
	String PART_RETURN_REQUEST="Part Return Request";
	String REOPEN = "Reopen";
	String RECEIVED = "Received";
	String NOT_RECEIVED = "Not Received";
	String SUBMIT = "Submit";
	String SEND_TO_DEALER = "Send To Dealer";
	String GENERATE_SHIPMENT = "Generate Shipment";
	String REMOVE = "Remove";
	String UPDATE = "Update";
	String APPEALS = "Appeals";
	String AUTO_DEBIT = "AutoDebit";
	String TRANSFER = "Transfer";
	String MOV_TO_PENDING_REC_INITIATION = "MovetoPendingRecoveryInitiation";
    String MOVE_TO_DUE_PARTS_FROM_WPRA="moveToWPRAGeneratedForPartsFork";
    String PART_RECEIVED_FROM_SUPPLIER = "Join After Receive";
    String REQUEST_FOR_PART = "Request For Part";
    String PART_RETURN_REQUESTED="Part Return Requested";
}

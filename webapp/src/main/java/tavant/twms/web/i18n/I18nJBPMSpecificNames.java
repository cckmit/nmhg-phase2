package tavant.twms.web.i18n;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.jbpm.WorkflowConstants;



@SuppressWarnings("serial")
public interface I18nJBPMSpecificNames {
	
	public static final Map<String, String> NAMES_AND_KEY = new HashMap<String, String>() {
        {
            put(WorkflowConstants.ACCEPTED, "label.jbpm.task.accepted");
            put(WorkflowConstants.DRAFT_CLAIM,"label.jbpm.task.draft.claim");
            put(WorkflowConstants.WAIT_FOR_DEBIT,"label.jbpm.task.wait.debit");
            put(WorkflowConstants.CLOSED, "label.jbpm.task.closed");
            put(WorkflowConstants.REOPENED,"label.jbpm.task.reopened");
            put(WorkflowConstants.DUE_PARTS_TASK,"label.jbpm.task.due.parts");
            put(WorkflowConstants.THIRD_PARTY_DUE_PARTS_TASK, "label.jbpm.task.third.party.due.parts");
            put(WorkflowConstants.OVERDUE_PARTS_TASK,"label.jbpm.task.overdue.parts");
            put(WorkflowConstants.SHIPMENT_GENERATED,"label.jbpm.task.shipment.generated");
            put(WorkflowConstants.PARTS_SHIPPED, "label.jbpm.taks.parts.shipped");
            put(WorkflowConstants.DUE_PARTS_RECEIPT,"label.jbpm.task.due.parts.receipt");
            put(WorkflowConstants.DUE_PARTS_INSPECTION,"label.jbpm.task.due.parts.inspection");
            put(WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME, "label.jbpm.task.processor.review");
            put(WorkflowConstants.CLAIM_FAILURE_REPORTS, "label.jbpm.task.claim.failure.reports");
            put(WorkflowConstants.APPEALS,"label.jbpm.task.appeals");
            put(WorkflowConstants.ON_HOLD,"label.jbpm.task.on.hold");
            put(WorkflowConstants.ON_HOLD_FOR_PART_RETURN, "label.jbpm.task.on.hold.part.return");
            put(WorkflowConstants.PART_NOT_IN_WAREHOUSE,"label.jbpm.task.part.not.warehouse");
            put(WorkflowConstants.AWAITING_SHIPMENT,"label.jbpm.task.due.awaiting.shipment");
            put(WorkflowConstants.AWAITING_SHIPMENT_TO_WAREHOUSE, "label.jbpm.task.awaiting.shipment.warehouse");
            put(WorkflowConstants.AWAITING_SUPPLIER_RESPONSE,"label.jbpm.task.awaiting.supplier.response");
            put(WorkflowConstants.SUPPLIER_PARTS_CLAIMED,"label.jbpm.task.supplier.parts.claimed");
            put(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED, "label.jbpm.task.supplier.shipment.generated");
            put(WorkflowConstants.CONFIRM_PART_RETURNS,"label.jbpm.task.due.confirm.part.returns");
            put(WorkflowConstants.NOT_FOR_RECOVERY,"label.jbpm.task.due.not.for.recovery");
            put(WorkflowConstants.NOT_FOR_RECOVERY_REQUEST, "label.jbpm.task.not.for.recovery.request");
            put(WorkflowConstants.NOT_FOR_RECOVERY_RESPONSE,"label.jbpm.task.not.for.recovery.response");
            put(WorkflowConstants.SUPPLIER_ACCEPTED,"label.jbpm.task.supplier.accepted");
            put(WorkflowConstants.NEW, "label.jbpm.task.new");
            put(WorkflowConstants.READY_FOR_DEBIT,"label.jbpm.task.ready.for.debit");
            put(WorkflowConstants.DEBITED,"label.jbpm.task.debited");
            put(WorkflowConstants.WAITING_FOR_LABOR, "label.jbpm.task.waiting.labor");
            put(WorkflowConstants.SERVICE_MANAGER_RESPONSE,"label.jbpm.task.service.manager.response");
            put(WorkflowConstants.SERVICE_MANAGER_REVIEW,"label.jbpm.task.service.manager.review");
            put(WorkflowConstants.ADVICE_REQUEST, "label.jbpm.task.advice.request");
            put(WorkflowConstants.CP_REVIEW,"label.jbpm.task.due.cp.review");
            put(WorkflowConstants.FORWARDED_INTERNALLY,"label.jbpm.task.forwarded.internally");
            put(WorkflowConstants.REJECTED_PART_RETURN,"label.jbpm.task.rejected.part.return");
            put(WorkflowConstants.REPLIES,"label.jbpm.task.due.replies");
            put(WorkflowConstants.TRANSFERRED, "label.jbpm.task.transferred");
            put(WorkflowConstants.FORWARDED,"label.jbpm.task.forwarded");
            put(WorkflowConstants.FORWARDED_EXTERNALLY,"label.jbpm.task.forwarded.externally");
            put(WorkflowConstants.PART_SHIPPED_NOT_RECEIVED,"label.jbpm.task.part.shipped.not.received");
            put(WorkflowConstants.SUPPLIER_PARTS_SHIPPED,"label.jbpm.task.supplier.parts.shipped");
            put(WorkflowConstants.FOR_RECOVERY, "label.jbpm.task.for.recovery");
            put(WorkflowConstants.SUPPLIER_RESPONSE,"label.jbpm.task.supplier.response");
            put(WorkflowConstants.REOPENED_CLAIMS,"label.jbpm.task.reopened.claims");
            put(WorkflowConstants.SUPPLIER_DISPUTED_CLAIMS_TASK_NAME,"label.jbpm.task.disputed");
            put(WorkflowConstants.REJETCTED_PARTS_INBOX,"label.jbpm.task.rejected.parts");
            put(WorkflowConstants.PREPARE_DUE_PARTS,"label.jbpm.task.prepare.due.parts");
            put(WorkflowConstants.CLAIMED_PARTS_RECEIPT,"label.jbpm.task.Claimed.due.Receipt");
            put(WorkflowConstants.WPRA_TO_BE_GENERATED,"label.jbpm.task.required.parts.from.dealer");
            put(WorkflowConstants.GENERATED_WPRA,"label.jbpm.task.wpra.generated.for.parts");
            put(WorkflowConstants.DEALER_REQUESTED_PART,"label.jbpm.task.dealer.requested.part");
            put(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER,"label.jbpm.task.shipment.generated.for.dealer");
            put(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED,"label.jbpm.task.dealer.requested.parts.shipped");
            put(WorkflowConstants.CONFIRM_DEALER_PART_RETURNS,"label.jbpm.task.confirm.dealer.part.returns");
            put(WorkflowConstants.CEVA_TRACKING,"label.jbpm.task.ceva.tracking");
            //Supplier return flow inbox
            put(WorkflowConstants.PART_FOR_RETURN_TO_NMHG,"label.jbpm.task.part.for.return.to.nmhg");
            put(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG,"label.jbpm.task.shipment.generated.to.nmhg");
            put(WorkflowConstants.PARTS_SHIPPED_TO_NMHG,"label.jbpm.task.parts.shipped.to.nmhg");
            put(WorkflowConstants.SUPPLIER_PARTS_RECEIPT,"label.jbpm.task.supplier.parts.receipt");
            put(WorkflowConstants.SUPPLIER_PARTS_INSPECTION,"label.jbpm.task.supplier.parts.inspect");
        }
    };
   
    public String getDisplayValue(String key);

}

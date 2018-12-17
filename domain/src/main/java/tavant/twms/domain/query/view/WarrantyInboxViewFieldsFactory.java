package tavant.twms.domain.query.view;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.ClaimFolderNames;
import tavant.twms.domain.common.Constants;

public class WarrantyInboxViewFieldsFactory extends InboxViewFieldsFactory {

    public WarrantyInboxViewFieldsFactory() {
        super();
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.DRAFT_CLAIM, new ClaimInboxViewFields(ClaimFolderNames.DRAFT_CLAIM));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.WAITING_FOR_LABOR, new ClaimInboxViewFields(ClaimFolderNames.WAITING_FOR_LABOR));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.FORWARDED, new ClaimInboxViewFields(ClaimFolderNames.FORWARDED));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.SERVICE_MANAGER_RESPONSE, new ClaimInboxViewFields(ClaimFolderNames.SERVICE_MANAGER_RESPONSE));

        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.FORWARDED_EXTERNALLY, new ClaimInboxViewFields(ClaimFolderNames.FORWARDED_EXTERNALLY));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.DUE_PARTS, new ClaimInboxViewFields(ClaimFolderNames.DUE_PARTS));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.ADVICE_REQUEST, new ClaimInboxViewFields(ClaimFolderNames.ADVICE_REQUEST));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.CP_REVIEW, new ClaimInboxViewFields(ClaimFolderNames.CP_REVIEW));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.ON_HOLD_FOR_PART_RETURN, new ClaimInboxViewFields(ClaimFolderNames.ON_HOLD_FOR_PART_RETURN));

        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.DUE_PARTS_INSPECTION, new ClaimInboxViewFields(ClaimFolderNames.DUE_PARTS_INSPECTION));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.OVERDUE_PARTS, new ClaimInboxViewFields(ClaimFolderNames.OVERDUE_PARTS));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PROCESSOR_REVIEW, new ClaimInboxViewFields(ClaimFolderNames.PROCESSOR_REVIEW));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PENDING_AUTHORIZATION, new ClaimInboxViewFields(ClaimFolderNames.PENDING_AUTHORIZATION));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.CLOSED_CLAIM, new ClaimInboxViewFields(ClaimFolderNames.CLOSED_CLAIM));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PARTS_SHIPPED, new ClaimInboxViewFields(ClaimFolderNames.PARTS_SHIPPED));

        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.APPEALS, new ClaimInboxViewFields(ClaimFolderNames.APPEALS));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PARTSRETURNSCHEDULER, new ClaimInboxViewFields(ClaimFolderNames.PARTSRETURNSCHEDULER));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.ON_HOLD, new ClaimInboxViewFields(ClaimFolderNames.ON_HOLD));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.REJECTED_PART_RETURN, new ClaimInboxViewFields(ClaimFolderNames.REJECTED_PART_RETURN));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.TRANSFERRED, new ClaimInboxViewFields(ClaimFolderNames.TRANSFERRED));

        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PAYMENTWAITTASK, new ClaimInboxViewFields(ClaimFolderNames.PAYMENTWAITTASK));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.SHIPMENTGENERATED, new ClaimInboxViewFields(ClaimFolderNames.SHIPMENTGENERATED));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.FORWARDEDINTERNALLY, new ClaimInboxViewFields(ClaimFolderNames.FORWARDEDINTERNALLY));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.REPLIES, new ClaimInboxViewFields(ClaimFolderNames.REPLIES));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.DUEPARTSRECEIPT, new ClaimInboxViewFields(ClaimFolderNames.DUEPARTSRECEIPT));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.SERVICEMANAGERREVIEW, new ClaimInboxViewFields(ClaimFolderNames.SERVICEMANAGERREVIEW));
        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.PART_SHIPPED_NOT_RECEIVED, new ClaimInboxViewFields(ClaimFolderNames.PART_SHIPPED_NOT_RECEIVED));

        inboxViewFields.put(BusinessObjectModelFactory.CLAIM_SEARCHES + ClaimFolderNames.SEARCH, new ClaimInboxViewFields(ClaimFolderNames.SEARCH));

        inboxViewFields.put(BusinessObjectModelFactory.INVENTORY_SEARCHES + "STOCK", new InventoryInboxViewFields("STOCK"));
        inboxViewFields.put(BusinessObjectModelFactory.INVENTORY_SEARCHES + "RETAIL", new InventoryInboxViewFields("RETAILED"));
        inboxViewFields.put(BusinessObjectModelFactory.INVENTORY_SEARCHES + "VINTAGE_STOCK", new InventoryInboxViewFields("STOCK"));
        inboxViewFields.put(BusinessObjectModelFactory.PART_RETURN_SEARCHES, new PartReturnInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.PART_RETURN_FOLDERS, new PartReturnFoldersInboxViewFields());

        //CR TKTSA-923
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.NEW, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.ACCEPTED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.DISPUTED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.CONFIRM_PART_RETURNS, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.REOPENED_CLAIMS, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.FOR_RECOVERY, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.AWAITING_SUPPLIER_RESPONSE, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.NOT_FOR_RECOVERY_REQUEST, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.NOT_FOR_RECOVERY_RESPONSE, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.ON_HOLD, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.READY_FOR_DEBIT, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.SUPPLIER_ACCEPTED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.SUPPLIER_RESPONSE, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.TRANSFERRED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.AWAITING_SHIPMENT, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.AWAITING_SHIPMENT_TO_WAREHOUSE, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.PART_NOT_IN_WAREHOUSE, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.SUPPLIER_PARTS_CLAIMED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.SUPPLIER_PARTS_SHIPPED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS + Constants.SUPPLIER_SHIPMENT_GENERATED, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.NOT_FOR_RECOVERY, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.REOPENED, new SupplierRecoveryInboxViewFields());
        //Recovery processor part received inbox
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.SUPPLIER_PARTS_RECEIPT, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.SUPPLIER_PARTS_INSPECTION, new SupplierRecoveryInboxViewFields());
        
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.ON_HOLD_FOR_PART_RETURN, new SupplierRecoveryInboxViewFields());
        inboxViewFields.put(BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS+Constants.ROUTED_TO_NMHG, new SupplierRecoveryInboxViewFields());
    }
}

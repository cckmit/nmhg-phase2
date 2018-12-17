package tavant.twms.domain.query.view;

import java.util.HashMap;
import java.util.Map;

public class SupplierRecoveryInboxViewFields extends InboxViewFields{
    private Map<String, InboxField> supplierRecoveryFields = new HashMap<String, InboxField>();

    SupplierRecoveryInboxViewFields() {
	 supplierRecoveryFields.put("id", new InboxField("id", "Number", "id", true, false, true, false, 0));
	 supplierRecoveryFields.put("recoveryClaimNumber", new InboxField("recoveryClaimNumber", "string", "columnTitle.common.recClaimNo",true, true, true, true));
         supplierRecoveryFields.put("totalRecoveredAmount", new InboxField("totalRecoveredAmount", "Money", "columnTitle.supplier.recoveryAmount",false, false,10));
         //supplierRecoveryFields.put("totalRecoveredCost", new InboxField("totalRecoveredCost", "Money", "columnTitle.supplier.recoveryAmount",false, false,10));
         supplierRecoveryFields.put("d.createdOn", new InboxField("d.createdOn", "date", "columnTitle.newClaim.createdOn",true, true,10));
         supplierRecoveryFields.put("updatedDate", new InboxField("updatedDate", "date", "label.inboxView.lastModifiedStatusDate",true, true,10));
         supplierRecoveryFields.put("recoveryClaimState.state", new InboxField("recoveryClaimState.state", "string", "columnTitle.common.status",false, false,15));
         supplierRecoveryFields.put("returnLocation", new InboxField("returnLocation", "string", "columnTitle.partShipperPartsClaimed.location",false, false,10));
         supplierRecoveryFields.put("businessUnitInfo.name", new InboxField("businessUnitInfo.name", "string", "label.common.businessUnitName",false, false,10));
         supplierRecoveryFields.put("claim.type", new InboxField("claim.type", "string", "columnTitle.common.claimType",true, true,10));
         supplierRecoveryFields.put("contract.name", new InboxField("contract.name", "string", "label.common.contract",true, true,10));
         supplierRecoveryFields.put("claim.itemReference.unserializedItem.product.name", new InboxField("claim.itemReference.unserializedItem.product.name", "string", "columnTitle.common.product",false, false,10));
         supplierRecoveryFields.put("claim.itemReference.unserializedItem.model.name", new InboxField("claim.itemReference.unserializedItem.model.name", "string", "columnTitle.common.model",false, false,18));
         supplierRecoveryFields.put("contract.supplier.supplierNumber", new InboxField("contract.supplier.supplierNumber", "string", "columnTitle.supplier.supplierNumber",true, true,20));
         supplierRecoveryFields.put("contract.supplier.name", new InboxField("contract.supplier.name", "string", "columnTitle.partSource.supplier_name",true, true,10));
         supplierRecoveryFields.put("claim.partReturnStatus", new InboxField("claim.partReturnStatus", "string", "label.inboxView.claimPartReturnStatus", false, false, 15));
         supplierRecoveryFields.put("partRecoveryStatus", new InboxField("partRecoveryStatus", "string", "label.supplier.part.recovery", false, false, 15));

    }
//
    @Override
    protected Map<String, InboxField> getInboxFieldsForAllUsers() {
        return supplierRecoveryFields;
    }

    @Override
    protected Map<String, InboxField> getInboxFieldsForDealer() {
	return null;
    }   
    
    public Map<String, InboxField> getInboxFields() {
	return supplierRecoveryFields;
    }
    
    public InboxField getField(String key) {
        Map<String, InboxField> inboxFieldsMap = getInboxFields();
        if (inboxFieldsMap.containsKey(key))
            return inboxFieldsMap.get(key);
        else
            throw new RuntimeException("There is no InboxField defined for the specified key["+key+"]");
    }
    
}

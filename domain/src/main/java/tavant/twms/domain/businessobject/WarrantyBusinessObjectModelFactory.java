package tavant.twms.domain.businessobject;

import java.util.HashSet;
import java.util.Set;

public class WarrantyBusinessObjectModelFactory extends BusinessObjectModelFactory {

    public WarrantyBusinessObjectModelFactory() {
        super();
        IBusinessObjectModel businessObjectForRules = new RuleBusinessObjectModel();
        contextWiseBusinessObjectsModel.put(CLAIM_DUPLICACY_RULES, new ClaimDuplicacyRulesBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(CLAIM_RULES, new ClaimRulesBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(POLICY_RULES, businessObjectForRules);
        contextWiseBusinessObjectsModel.put(CONTRACT_APPLICABILITY_RULES, businessObjectForRules);
        contextWiseBusinessObjectsModel.put(ENTRY_VALIDATION_RULES, businessObjectForRules);
        contextWiseBusinessObjectsModel.put(CLAIM_PROCESSOR_ROUTING, businessObjectForRules);
        contextWiseBusinessObjectsModel.put(REC_CLAIM_PROCESSOR_ROUTING, businessObjectForRules);
        contextWiseBusinessObjectsModel.put(CLAIM_SEARCHES, new ClaimSearchBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(INVENTORY_SEARCHES, new InventoryItemBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(PART_RETURN_SEARCHES, new PartReturnSearchBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(RECOVERY_CLAIM_SEARCHES, new RecoveryClaimSearchBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(ITEM_SEARCHES, new ItemSearchBusinessObjectModel());
        contextWiseBusinessObjectsModel.put(BRAND_ITEM_SEARCHES, new ItemSearchBusinessObjectModel(true));
        contextWiseBusinessObjectsModel.put(AMER_INVENTORY_SEARCHES, new InventoryItemBusinessObjectModel(true));
    }

    public Set<String> listAllRuleContexts() {
        final Set<String> allContexts = new HashSet<String>();
        allContexts.add(CLAIM_RULES);
        allContexts.add(POLICY_RULES);
        allContexts.add(CONTRACT_APPLICABILITY_RULES);
        allContexts.add(ENTRY_VALIDATION_RULES);
        allContexts.add(CLAIM_PROCESSOR_ROUTING);
        allContexts.add(REC_CLAIM_PROCESSOR_ROUTING);
        return allContexts;
    }

    public Set<String> listAllSearchContexts() {
        final Set<String> allContexts = new HashSet<String>();
        allContexts.add(CLAIM_SEARCHES);
        allContexts.add(INVENTORY_SEARCHES);
        allContexts.add(PART_RETURN_SEARCHES);
        allContexts.add(RECOVERY_CLAIM_SEARCHES);
        allContexts.add(ITEM_SEARCHES);
        return allContexts;
    }
}

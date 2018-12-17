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
package tavant.twms.domain.businessobject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author roopali.agrawal
 */

public abstract class BusinessObjectModelFactory {

    public static final String CLAIM_DUPLICACY_RULES = "ClaimDuplicacyRules";

    public static final String CLAIM_RULES = "ClaimRules";

    public static final String CLAIM_SEARCHES = "ClaimSearches";

    public static final String CLAIM_PROCESSOR_ROUTING = "ClaimProcessorRouting";

    public static final String CONTRACT_APPLICABILITY_RULES = "ContractApplicabilityRules";

    public static final String ENTRY_VALIDATION_RULES = "EntryValidationRules";

    public static final String INVENTORY_SEARCHES = "InventorySearches";

    public static final String PART_RETURN_SEARCHES = "PartReturnSearches";

    public static final String RECOVERY_CLAIM_SEARCHES = "RecoveryClaimSearches";

    public static final String POLICY_RULES = "PolicyRules";

    public static final String REC_CLAIM_PROCESSOR_ROUTING = "recClaimProcessorRouting";

    public static final String ITEM_SEARCHES = "ItemSearches";

    public static final String BRAND_ITEM_SEARCHES = "BrandItemSearches";

    public static final String PART_RETURN_FOLDERS = "PartReturnFolders";

    public static final String SUPPLIER_RECOVERY_FOLDERS = "SupplierRecoveryFolders";
    
    public static final String AMER_INVENTORY_SEARCHES = "AMERInventorySearches";

    private static BusinessObjectModelFactory instance;

    protected final Map<String, IBusinessObjectModel> contextWiseBusinessObjectsModel = new HashMap<String, IBusinessObjectModel>();

    protected BusinessObjectModelFactory() {
        instance = this;
    }

    public static BusinessObjectModelFactory getInstance() {
        return instance;
    }

    public IBusinessObjectModel getBusinessObjectModel(String context) {
        if (!contextWiseBusinessObjectsModel.containsKey(context)) {
            throw new IllegalArgumentException(
                    "No Business object is available for context[" + context
                            + "]");
        }
        return contextWiseBusinessObjectsModel.get(context);
    }

    public abstract Set<String> listAllRuleContexts();

    public abstract Set<String> listAllSearchContexts();

}

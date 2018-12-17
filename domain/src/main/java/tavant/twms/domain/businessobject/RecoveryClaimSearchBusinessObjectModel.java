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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.QueryDomainType;
import tavant.twms.domain.rules.Type;
import tavant.twms.domain.supplier.contract.Contract;

/**
 *
 * @author roopali.agrawal
 *
 */
public class RecoveryClaimSearchBusinessObjectModel extends AbstractBusinessObjectModel {

    private static Logger logger = LogManager.getLogger(RecoveryClaimSearchBusinessObjectModel.class);

    DomainType domainType;

    private final DomainTypeSystem domainTypeSystem;

    @Override
    public Set<DomainType> getDomainTypes() {
        Set<DomainType> set = new HashSet<DomainType>();
        set.add(this.domainType);
        return set;
    }

    public RecoveryClaimSearchBusinessObjectModel() {
        this.domainTypeSystem = new DomainTypeSystem();
        this.domainType = recoveryClaim();
        discoverPathsToFields(this.domainType, "recoveryClaim");
    }

    protected DomainType recoveryClaim() {
        String typeName = RecoveryClaim.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType recoveryClaim = new DomainType(/* Domain term */"Recovery Claim", /*
                                                                         * Unique
                                                                         * Type
                                                                         * Name
                                                                         */typeName);
            // Simple Fields.
            recoveryClaim.simpleField("label.recoveryClaim.recoveryClaimState", "recoveryClaimState", Type.STRING);
            recoveryClaim.simpleField("label.claim.claimNumber", "claim.claimNumber", Type.STRING);
            recoveryClaim.simpleField("label.recoveryClaim.recoveryClaimNumber", "recoveryClaimNumber", Type.STRING);
            recoveryClaim.simpleField("label.claim.historicalClaimNumber", "claim.histClmNo", Type.STRING);
            recoveryClaim.simpleField("label.recoveryClaim.documentNumber", "documentNumber", Type.STRING);
            recoveryClaim.simpleField("label.common.lastUpdatedDate", "updatedDate", Type.DATE);
            
            recoveryClaim.queryTemplate("label.supplier.acceptanceReason",
					"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"recoveryClaim.recoveryClaimAcceptanceReason.i18nLovTexts",
					"{alias}");
            
            recoveryClaim.queryTemplate("label.supplier.rejectionReason",
					"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"recoveryClaim.recoveryClaimRejectionReason.i18nLovTexts",
					"{alias}");
            
            recoveryClaim.queryTemplate("label.common.causalPartDescription",
					"recoveryClaim.recoveryClaimInfo.causalPartRecovery=true and {alias}.locale= 'USER_LOCALE' and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"recoveryClaim.claim.activeClaimAudit.serviceInformation.causalPart.i18nItemTexts",
					"{alias}");
            
            recoveryClaim.queryTemplate("label.common.causalPartNumber",
					"recoveryClaim.recoveryClaimInfo.causalPartRecovery=true and lower({alias}.number)",
					Type.STRING, true, FunctionField.Types.ONE_TO_ONE.getBaseType(),
					"recoveryClaim.claim.activeClaimAudit.serviceInformation.causalPart",
					"{alias}");
            
            recoveryClaim.queryTemplate("label.common.replacedPartNumber",
					"recoveryClaim.recoveryClaimInfo.causalPartRecovery=false and lower({alias}.oemPart.itemReference.unserializedItem.number)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"recoveryClaim.recoveryClaimInfo.recoverableParts",
					"{alias}");

            recoveryClaim.simpleField("label.common.businessUnit", "claim.businessUnitInfo",Type.STRING);

            // One To One Associations.
            recoveryClaim.oneToOne("label.common.serviceProvider", "claim.forDealer", dealership());
            recoveryClaim.oneToOne("label.common.contract", "contract",
					contract());
            this.domainTypeSystem.registerDomainType(recoveryClaim);
            return recoveryClaim;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    @Override
    public DomainTypeSystem getDomainTypeSystem() {
        return this.domainTypeSystem;
    }

    public String getExpressionForDomainType(String typeName) {
        String expression = null;
        if ("RecoveryClaim".equals(typeName)) {
            expression = "recoveryClaim";
        }
        return expression;
    }

    public String getTopLevelTypeName() {
        return "RecoveryClaim";
    }

    public String getTopLevelAlias() {
        return "recoveryClaim";
    }

    protected DomainType supplier() {
		String typeName = Supplier.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType supplier = new QueryDomainType(
			/* Domain term */typeName, /*
										 * Unique Type Name
										 */typeName);
			supplier.simpleField("label.common.name", "name", Type.STRING);
			supplier.simpleField("label.supplier.supplierNumber", "supplierNumber", Type.STRING);
			supplier.simpleField("label.campaign.preferredLocationType",
					"preferredLocationType", Type.STRING);
			domainTypeSystem.registerDomainType(supplier);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

    protected DomainType dealership() {
		String typeName = ServiceProvider.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new QueryDomainType(
			/* Domain term */typeName, /*
										 * Unique Type Name
										 */typeName);
			dealership.simpleField("label.common.name", "name", Type.STRING);
			dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
			dealership.simpleField("label.common.preferredCurrency",
					"preferredCurrency.code", Type.STRING);
			domainTypeSystem.registerDomainType(dealership);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

    protected DomainType item() {
		String typeName = Item.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType item = new QueryDomainType(/* Domain term */typeName, /*
																				 * Unique
																				 * Type
																				 * Name
																				 */typeName);
			item.simpleField("label.common.itemDescription", "description", Type.STRING);
			item.simpleField("item.number", "number", Type.STRING);
			item.simpleField("label.common.model", "model.name", Type.STRING);
			item.simpleField("product", "product.name", Type.STRING);

			domainTypeSystem.registerDomainType(item);
			// return item;
		}
		return domainTypeSystem.getDomainType(typeName);

	}

    protected DomainType contract() {
		String typeName = Contract.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType contract = new QueryDomainType(/* Domain term */typeName, /*
																				 * Unique
																				 * Type
																				 * Name
																				 */typeName);
			contract.simpleField("label.common.name", "name", Type.STRING);
			contract.oneToOne("label.supplier.supplierTitle", "supplier", supplier());
			domainTypeSystem.registerDomainType(contract);
			// return item;
		}
		return domainTypeSystem.getDomainType(typeName);

	}

}

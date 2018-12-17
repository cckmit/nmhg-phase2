/*
 *   Copyright (c)2007 Tavant Technologies
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
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.QueryDomainType;
import tavant.twms.domain.rules.Type;
/**
 *
 * @author roopali.agrawal
 *
 */
public class PartReturnSearchBusinessObjectModel extends AbstractBusinessObjectModel {

	private static Logger logger = LogManager.getLogger(PartReturnSearchBusinessObjectModel.class);

	DomainType domainType;
	DomainType domainTypeForClaim;

	public PartReturnSearchBusinessObjectModel() {
		this.domainTypeSystem = new DomainTypeSystem();
		this.domainType = partReturn();
		this.domainTypeForClaim = claim();
		discoverPathsToFields(this.domainType, "partReturn");
		discoverPathsToFields(this.domainTypeForClaim, "claim");

	}

	protected DomainType partReturn() {
		String typeName = PartReturn.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType partReturn = new DomainType(typeName,typeName);
			partReturn.simpleField("label.common.dueDate", "dueDate",Type.DATE);
			partReturn.simpleField("label.campaign.paymentCondition", "paymentCondition.description",Type.STRING);
			partReturn.simpleField("label.common.quantity", "oemPartReplaced.numberOfUnits",Type.INTEGER);
			partReturn.simpleField("label.campaign.location", "returnLocation.code",Type.STRING);
			partReturn.simpleField("label.inventory.unitPrice", "oemPartReplaced.pricePerUnit.amount",Type.BIGDECIMAL);
			partReturn.simpleField("label.common.status", "status",Type.ENUM);
			partReturn.simpleField("label.campaign.warehouse", "warehouseLocation",Type.STRING);
			partReturn.simpleField("label.partReturn.failureReason", "inspectionResult.failureReason.code",Type.STRING);
			//One To One Associations.
			//partReturn.oneToOne("Claim", null, claim());
			partReturn.oneToOne("label.inventory.shipment", "shipment", shipment());
			partReturn.oneToOne("label.claim.part", "oemPartReplaced.itemReference", itemReference());
		    this.domainTypeSystem.registerDomainType(partReturn);
		}
		return this.domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType claim() {
        String typeName = Claim.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType claim = new DomainType(/* Domain term */"Claim", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            // Simple Fields.
            claim.simpleField("label.common.causedby", "activeClaimAudit.serviceInformation.causedBy.name",
                    Type.STRING);
            claim.simpleField("label.claim.claimType", "type", Type.STRING);
            claim.simpleField("label.claim.claimNumber", "claimNumber", Type.STRING);
            claim.simpleField("label.claim.historicalClaimNumber", "histClmNo", Type.STRING);
            claim.simpleField("label.claim.claimDate", "filedOnDate", Type.DATE);
            claim.simpleField("label.common.conditionFound", "activeClaimAudit.conditionFound", Type.STRING);
            claim.simpleField("label.common.dateFailure", "activeClaimAudit.failureDate", Type.DATE);
            claim.simpleField("label.common.dateInstall", "activeClaimAudit.installationDate",
                    Type.DATE);
            claim.simpleField("label.common.dateRepair", "activeClaimAudit.repairDate", Type.DATE);
            claim.simpleField("label.common.repairStartDate", "activeClaimAudit.repairStartDate", Type.DATE);
            /*claim.simpleField("Failure Code", "serviceInformation.faultCode",
                    Type.STRING);*/
            claim.simpleField("label.common.faultFound", "activeClaimAudit.serviceInformation.faultFound.name",
                    Type.STRING);
            claim.simpleField("label.common.hoursInService", "hoursInService", Type.INTEGER);
            claim.simpleField("label.common.otherComments", "activeClaimAudit.otherComments", Type.STRING);
            claim.simpleField("label.common.probableCause", "activeClaimAudit.probableCause", Type.STRING);
            claim.simpleField("label.common.smrReason", "activeClaimAudit.reasonForServiceManagerRequest",
                    Type.STRING);
            claim.simpleField("label.common.smrRequest", "serviceManagerRequest",
                    Type.STRING);
            claim.simpleField("label.common.workPerformed", "activeClaimAudit.workPerformed", Type.STRING);
            claim.simpleField("label.common.businessUnit", "businessUnitInfo",Type.STRING);

            // One To One Associations.
//          One To One Associations.
			claim.oneToOne("label.common.serviceProvider", "forDealer", dealership());
			claim.oneToOne("label.inventory.inventoryItem",
					"itemReference.referredInventoryItem", inventoryItem());
			claim.oneToOne("label.common.item", "itemReference.referredItem", item());
			claim.oneToOne("label.failure.failureCode", "activeClaimAudit.serviceInformation.faultCodeRef",
                    failureCode());
			claim.oneToOne("label.claim.applicablePolicy", "applicablePolicy", policy());
			claim.oneToOne("label.common.causalPart", "activeClaimAudit.serviceInformation.causalPart",
					item());

            this.domainTypeSystem.registerDomainType(claim);
            return claim;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

	private DomainType failureCode() {
    	String typeName = FaultCode.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType faultCode = new QueryDomainType("Fault Code", typeName);
            faultCode.simpleField("label.failure.faultCodeString", "definition.code", Type.STRING);
            this.domainTypeSystem.registerDomainType(faultCode);
            return faultCode;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
	}

	protected DomainType policy() {
		String typeName = Policy.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType policy = new QueryDomainType("Policy", typeName);

			policy.simpleField("label.common.hoursCovered",
					"policyDefinition.coverageTerms.serviceHoursCovered",
					Type.INTEGER);
			policy
					.simpleField(
							"label.common.itemCondition",
							"policyDefinition.availability.itemCondition.itemCondition",
							Type.STRING);
			policy.simpleField("label.common.monthsCoveredFromDelivery",
					"policyDefinition.coverageTerms.monthsCoveredFromDelivery",
					Type.INTEGER);
			policy.simpleField("label.shipment.monthsCoveredFromShipment",
					"policyDefinition.coverageTerms.monthsCoveredFromShipment",
					Type.INTEGER);
			policy.simpleField("label.common.ownershipState",
					"policyDefinition.availability.ownershipState.name",
					Type.STRING);
			policy.simpleField("label.managePolicy.code", "policyDefinition.code",
								Type.STRING);
						policy.simpleField("label.managePolicy.name", "policyDefinition.description",
					Type.STRING);
			policy.simpleField("label.policy.planPrice",
					"policyDefinition.availability.price.amount", Type.BIGDECIMAL);
			policy.simpleField("label.policy.transferFees",
					"policyDefinition.transferDetails.transferFee.amount", Type.BIGDECIMAL);
			policy.simpleField("label.policy.transferable",
					"policyDefinition.transferDetails.transferable",
					Type.BOOLEAN);
			policy.simpleField("label.warranty.warrantyType",
					"policyDefinition.warrantyType.type", Type.STRING);

			this.domainTypeSystem.registerDomainType(policy);
			return policy;
		} else {
			return this.domainTypeSystem.getDomainType(typeName);
		}
	}
	protected DomainType itemReference() {
		String typeName = Item.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType item = new QueryDomainType(/* Domain term */typeName, /*
																		 * Unique
																		 * Type
																		 * Name
																		 */typeName);
			item.simpleField("label.common.serialNumber", "referredInventoryItem.serialNumber", Type.STRING);
			//todo-need to verify following fields.
			item.simpleField("item.number", "unserializedItem.number", Type.STRING);
			item.simpleField("label.common.itemDescription", "unserializedItem.description", Type.STRING);
			item.simpleField("label.common.model", "unserializedItem.model", Type.STRING);


			this.domainTypeSystem.registerDomainType(item);
			// return item;
		}
		return this.domainTypeSystem.getDomainType(typeName);

	}


	protected DomainType item() {
		String typeName = Item.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType item = new QueryDomainType(/* Domain term */typeName, /*
																		 * Unique
																		 * Type
																		 * Name
																		 */typeName);
			item.simpleField("label.common.itemDescription", "description", Type.STRING);
			item.simpleField("label.itemNumber", "number", Type.STRING);
			item.simpleField("label.common.model", "model.description", Type.STRING);
			item.simpleField("label.common.product", "product.groupCode", Type.STRING);

			this.domainTypeSystem.registerDomainType(item);
			// return item;
		}
		return this.domainTypeSystem.getDomainType(typeName);

	}

	protected DomainType dealership() {
		String typeName = ServiceProvider.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new QueryDomainType(/* Domain term */"Dealer", /*
																				 * Unique
																				 * Type
																				 * Name
																				 */typeName);
			dealership.simpleField("label.common.name", "name", Type.STRING);
			dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
			dealership.simpleField("label.common.preferredCurrency",
					"preferredCurrency.code", Type.STRING);
			this.domainTypeSystem.registerDomainType(dealership);
		}
		return this.domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType shipment() {
		String typeName = Shipment.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new QueryDomainType(typeName,typeName);
			dealership.simpleField("label.partReturnConfiguration.shipmentNumber", "id", Type.LONG);
			dealership.simpleField("label.partReturnConfiguration.carrier","carrier.name", Type.STRING);
			dealership.simpleField("label.partReturnConfiguration.trackingNumber", "trackingId", Type.STRING);
			this.domainTypeSystem.registerDomainType(dealership);
		}
		return this.domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType inventoryItem() {
		String typeName = InventoryItem.class.getSimpleName();
		if (!this.domainTypeSystem.isKnown(typeName)) {
			DomainType inventoryItem = new QueryDomainType(
			/* Domain term */"Inventory Item",
			/** Unique Type Name */
			typeName);
			inventoryItem.simpleField("label.common.dateOfDelivery", "deliveryDate",
					Type.DATE);
			inventoryItem.simpleField("label.inventory.dateOfShipment"/* Domain name */,
					"shipmentDate"/*
									 * field name, also the property expression
									 */, Type.DATE /*
									 * the type name of the field, either a
									 * build-in type or a domain type for
									 * one-to-one and one-to-many associations
									 */);
			inventoryItem.simpleField("label.common.serialNumber", "serialNumber",Type.STRING);
			this.domainTypeSystem.registerDomainType(inventoryItem);
		}
		return this.domainTypeSystem.getDomainType(typeName);
	}



	private final DomainTypeSystem domainTypeSystem;// =new DomainTypeSystem();

	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set=new HashSet<DomainType>();
		set.add(this.domainType);
		set.add(this.domainTypeForClaim);
		return set;
	}

	@Override
	public DomainTypeSystem getDomainTypeSystem() {
		return this.domainTypeSystem;
	}

	@Override
	public String getExpressionForDomainType(String typeName) {
		String expression = null;
		if (typeName.equals("PartReturn")) {
            expression = "partReturn";
        }
		if (typeName.equals("Claim")) {
            expression = "claim";
        }

		return expression;

	}

	public String getTopLevelTypeName() {
		return "PartReturn";
	}

	public String getTopLevelAlias() {
		return "partReturn";
	}

}

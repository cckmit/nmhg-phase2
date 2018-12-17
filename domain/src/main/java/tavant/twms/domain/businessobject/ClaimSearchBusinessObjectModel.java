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

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.common.DocumentType;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.QueryDomainType;
import tavant.twms.domain.rules.Type;
import tavant.twms.domain.common.Document;

/**
 *
 * @author roopali.agrawal
 *
 */
public class ClaimSearchBusinessObjectModel extends AbstractBusinessObjectModel {

	private static Logger logger = LogManager
			.getLogger(ClaimSearchBusinessObjectModel.class);

	DomainType domainType;

	private final DomainTypeSystem domainTypeSystem;

	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set = new HashSet<DomainType>();
		set.add(domainType);
		return set;
	}

	public ClaimSearchBusinessObjectModel() {
		domainTypeSystem = new DomainTypeSystem();
		domainType = claim();
		discoverPathsToFields(domainType, "claim");
	}

	protected DomainType claim() {
		String typeName = Claim.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType claim = new DomainType(/* Domain term */"Claim", /*
																		 * Unique
																		 * Type
																		 * Name
																		 */typeName);
			// Simple Fields.
			claim.simpleField("label.claim.claimType", "clmTypeName", Type.STRING);
			claim.simpleField("label.claim.partNumber", "partItemReference.referredItem.number", Type.STRING);
			claim.simpleField("label.claim.partSerialNumber", "partItemReference.referredInventoryItem.serialNumber", Type.STRING);
			claim.simpleField("label.claim.claimState", "activeClaimAudit.state", Type.ENUM);
			claim.simpleField("label.claim.claimNumber", "claimNumber", Type.STRING);
			claim.simpleField("label.claim.historicalClaimNumber", "histClmNo", Type.STRING.toLowerCase());
			claim.simpleField("label.viewClaim.warrantyOrderClaim", "warrantyOrder", Type.BOOLEAN);
			claim.simpleField("label.claim.claimDate", "filedOnDate", Type.DATE);
			claim.simpleField("label.common.lastModified", "lastUpdatedOnDate", Type.DATE);
			claim.simpleField("label.common.conditionFound", "activeClaimAudit.conditionFound", Type.STRING);
			claim.simpleField("label.common.dateFailure", "activeClaimAudit.failureDate", Type.DATE);
			claim.simpleField("label.common.dateInstall", "activeClaimAudit.installationDate",
					Type.DATE);
			claim.simpleField("label.newClaim.commercialPolicy", "commercialPolicy",  Type.BOOLEAN);
			claim.simpleField("label.common.dateRepair", "activeClaimAudit.repairDate", Type.DATE);
			claim.simpleField("label.common.repairStartDate", "activeClaimAudit.repairStartDate", Type.DATE);
			claim.simpleField("label.common.faultFound", "activeClaimAudit.serviceInformation.faultFound.name",
					Type.STRING);
			claim.simpleField("label.common.otherComments", "activeClaimAudit.otherComments", Type.STRING);
			claim.simpleField("label.common.assignTo","activeClaimAudit.assignToUser.name", Type.STRING);
			claim.simpleField("label.common.smrRequest", "serviceManagerRequest",
					Type.BOOLEAN);
			claim.simpleField("label.common.supplierRecovery", "supplierRecovery",
					Type.BOOLEAN);
			claim.simpleField("label.common.workPerformed", "activeClaimAudit.workPerformed", Type.STRING);
			claim.simpleField("label.common.workOrderNumber", "activeClaimAudit.workOrderNumber", Type.STRING);
			claim.simpleField("label.common.businessUnit", "businessUnitInfo",Type.STRING);
            claim.simpleField("label.claim.invoiceNumber", "activeClaimAudit.invoiceNumber", Type.STRING);
            claim.simpleField("label.common.authNumber", "authNumber", Type.STRING);
            
            // One To One Associations.
			claim.oneToOne("label.common.serviceProvider", "forDealer", dealership());
			claim.oneToOne("label.common.incidentals","activeClaimAudit.serviceInformation.serviceDetail",
					travelDetail());
			claim.oneToOne("label.failure.failureCode", "activeClaimAudit.serviceInformation.faultCodeRef",
					failureCode());
			claim.oneToOne("label.bussinessUnit.nmhgCausalPart", "activeClaimAudit.serviceInformation.causalPart",
					item());
			claim.oneToOne("label.claimSearch.campaign",
					"campaign", campaign());
			// claim.oneToOne("Claim Payment", "payment", payment());
			/*
			 * TODO : This clubcarPartsReplaced should be applicable only for BUs other than Hussmann.
			 * Hence need to introduce a BU condition based on which the filtering is to be done. :)
			 */
			claim.oneToMany("label.bussinessUnit.clubCarPartsReplaced",
					"activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced",
					oemPartReplaced());
			claim.oneToMany("label.bussinessUnit.clubCarPartsReplaced1",
					"activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts",
					oemReplacedParts());
			claim.oneToMany("label.bussinessUnit.clubCarPartsReplaced2",
					"activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts",
					oemInstalledParts());
    		claim.oneToMany("label.recoveryClaim.outsidePartsReplaced",
					"activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced",
					nonOEMPartReplaced());
/*			claim.oneToMany("label.recoveryClaim.outsidePartsInstalled",
					"serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts",
					nonOEMPartInstalled());
*/			claim.oneToMany("label.recoveryClaim.serviceDetails",
					"activeClaimAudit.serviceInformation.serviceDetail.laborPerformed",
					laborDetail());
			claim.oneToMany("label.claim.claimedItems", "claimedItems", claimedItem());
			claim.oneToMany("label.claimSearch.attachment", "activeClaimAudit.attachments", attachments());
			
			/*claim.oneToMany("label.search.payment",
					"payment.lineItemGroups", lineItemGroup());
			claim.queryTemplate("label.bussinessUnit.clubCarPartsAmount",
					"{alias}.forLineItemGroup.name='OEM Parts' and {alias}.groupTotal.amount",
					Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "claim.payment.lineItemGroups.lineItemGroupAudits",
					"{alias}");
			claim
					.queryTemplate(
							"label.bussinessUnit.totalClubCarPartsAmount",
							"{alias}.name='OEM Parts' and {alias}.acceptedTotal.amount",
							Type.BIGDECIMAL, true,
							FunctionField.Types.ONE_TO_MANY.getBaseType(),
							"claim.payment.lineItemGroups", "{alias}");
			claim.queryTemplate("label.common.labor",
					"{alias}.name='Labor' and {alias}.groupTotal.amount",
					Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "claim.payment.lineItemGroups",
					"{alias}");
			claim.queryTemplate("label.common.totalLabor",
					"{alias}.name='Labor' and {alias}.acceptedTotal.amount",
					Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "claim.payment.lineItemGroups",
					"{alias}");
			claim.queryTemplate("label.common.travel",
					"{alias}.name='Travel' and {alias}.groupTotal.amount",
					Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "claim.payment.lineItemGroups",
					"{alias}");*/

            claim.queryTemplate("label.common.problemPartNumber",
                    "{alias}.brand = claim.brand and {alias}.itemNumber",
                    Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
                    "claim.activeClaimAudit.serviceInformation.causalPart.brandItems",
                    "{alias}");

			claim.queryTemplate("label.common.smrReason",
					"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"claim.reasonForServiceManagerRequest.i18nLovTexts",
					"{alias}");
			
			claim.queryTemplate("label.claimSearch.creditDebitMemo",
					"{alias}.creditMemoDate",
					Type.DATE, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"claim.activeClaimAudit.payment.activeCreditMemo",
					"{alias}");
			
			claim.queryTemplate("label.claimSearch.memoNumber",
					"{alias}.creditMemoNumber",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"claim.activeClaimAudit.payment.activeCreditMemo",
					"{alias}");
			
			
			claim.queryTemplate("label.viewClaim.Supplier",
					"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"claim.suppliers.i18nLovTexts",
					"{alias}");
			/*
			 * claim.queryTemplate("Total Travel", "{alias}.name='Travel' and
			 * {alias}.acceptedTotal.amount", Type.BIGDECIMAL, true,
			 * FunctionField.Types.ONE_TO_MANY .getBaseType(),
			 * "claim.payment.lineItemGroups", "{alias}");
			 */
			/*claim
					.queryTemplate(
							"label.inventory.itemFreightDuty",
							"{alias}.name='Item Freight Duty' and {alias}.groupTotal.amount",
							Type.BIGDECIMAL, true,
							FunctionField.Types.ONE_TO_MANY.getBaseType(),
							"claim.payment.lineItemGroups", "{alias}");*/
			/*
			 * claim .queryTemplate( "Total Item Freight Duty",
			 * "{alias}.name='Item Freight Duty' and
			 * {alias}.acceptedTotal.amount", Type.BIGDECIMAL, true,
			 * FunctionField.Types.ONE_TO_MANY.getBaseType(),
			 * "claim.payment.lineItemGroups", "{alias}");
			 */
			/*claim.queryTemplate("label.campaign.meals",
					"{alias}.name='Meals' and {alias}.groupTotal.amount",
					Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "claim.payment.lineItemGroups",
					"{alias}");*/
			/*
			 * claim.queryTemplate("Total Meals", "{alias}.name='Meals' and
			 * {alias}.acceptedTotal.amount", Type.BIGDECIMAL, true,
			 * FunctionField.Types.ONE_TO_MANY .getBaseType(),
			 * "claim.payment.lineItemGroups", "{alias}");
			 */
			/*claim
					.queryTemplate(
							"label.claim.claimAmount",
							"{alias}.name='Claim Amount' and {alias}.groupTotal.amount",
							Type.BIGDECIMAL, true,
							FunctionField.Types.ONE_TO_MANY.getBaseType(),
							"claim.payment.lineItemGroups", "{alias}");*/
			claim
					.queryTemplate(
							"label.claim.requestClaimAmount",
							"{alias}.name='Claim Amount' and {alias}.baseAmount.amount",
							Type.BIGDECIMAL, true,
							FunctionField.Types.ONE_TO_MANY.getBaseType(),
							"claim.activeClaimAudit.payment.lineItemGroups", "{alias}");

			claim
					.queryTemplate(
							"label.bussinessUnit.numberofClubCarPartReplaced",
							"size(claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);

/*			claim
					.queryTemplate(
							"label.bussinessUnit.numberofClubCarReplacedParts",
							"size(claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);
*/
/*		claim
					.queryTemplate(
							"label.inventory.NumberofOEMInstalledParts",
							"size(claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);
*/
			claim
					.queryTemplate(
							"label.bussinessUnit.numberofNONClubCarPartReplaced",
							"size(claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);

/*			claim
					.queryTemplate(
							"label.bussinessUnit.numberofNONClubCarPartInstalled",
							"size(claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);
*/
			claim
					.queryTemplate(
							"label.claim.numberOfServiceDetails",
							"size(claim.activeClaimAudit.serviceInformation.serviceDetail.laborPerformed)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType(), null, null);

			claim.queryTemplateForAggregateFunctions(
					"label.claim.sumofAdditionalLaborHours",
					"sum({alias}.additionalLaborHours)", Type.LONG, true,
					FunctionField.Types.ONE_TO_MANY.getBaseType(),
					"claim.activeClaimAudit.serviceInformation.serviceDetail.laborPerformed",
					"claim.id", "{alias}");

			/*
			 * claim.queryTemplateForAggregateFunctions("Sum of Suggested Labor
			 * Hours","sum({alias}.serviceProcedure.suggestedLabourHours)",
			 * Type.LONG, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
			 * "claim.serviceInformation.serviceDetail.laborPerformed","claim.id","{alias}");
			 */
			claim
					.queryTemplateForAggregateFunctions(
						"label.bussinessUnit.sumofClubCarPartsReplacedQuantity",
						"sum({alias}.numberOfUnits)", Type.LONG, true,
						FunctionField.Types.ONE_TO_MANY.getBaseType(),
						"claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced",
						"claim.id", "{alias}");

/*			claim
					.queryTemplateForAggregateFunctions(
						"label.bussinessUnit.sumofClubCarReplacedPartsQuantity",
						"sum({alias}.numberOfUnits)", Type.LONG, true,
						FunctionField.Types.ONE_TO_MANY.getBaseType(),
						"claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts",
						"claim.id", "{alias}");
			claim
					.queryTemplateForAggregateFunctions(
						"label.bussinessUnit.sumofClubCarInstalledPartsQuantity",
						"sum({alias}.numberOfUnits)", Type.LONG, true,
						FunctionField.Types.ONE_TO_MANY.getBaseType(),
						"claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts",
						"claim.id", "{alias}");
*/

			claim
					.queryTemplateForAggregateFunctions(
							"label.bussinessUnit.sumofNONClubCarPartsReplacedQuantity",
							"sum({nonoemalias}.numberOfUnits)",
							Type.LONG,
							true,
							FunctionField.Types.ONE_TO_MANY.getBaseType(),
							"claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced",
							"claim.id", "{nonoemalias}");
/*			claim
				.queryTemplateForAggregateFunctions(
						"label.bussinessUnit.sumofNONClubCarInstalledPartsQuantity",
						"sum({nonoemalias}.numberOfUnits)",
						Type.LONG,
						true,
						FunctionField.Types.ONE_TO_MANY.getBaseType(),
						"claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts",
						"claim.id", "{nonoemalias}");
*/
			claim
					.queryTemplate(
							"label.laborType.laborSplit.count",
							"size(claim.activeClaimAudit.serviceInformation.serviceDetail.laborSplit)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
								.getBaseType(), null, null);
			claim
			.queryTemplate(
					"label.common.claimSearch.noAttached",
					"size(claim.activeClaimAudit.attachments)",
					Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
						.getBaseType(), null, null);
			
			domainTypeSystem.registerDomainType(claim);
			return claim;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	private DomainType failureCode() {
		String typeName = FaultCode.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType faultCode = new QueryDomainType("Fault Code", typeName);
			faultCode.simpleField("Fault Code String", "definition.code",
					Type.STRING);
			domainTypeSystem.registerDomainType(faultCode);
			return faultCode;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	protected DomainType policy() {
		String typeName = Policy.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType policy = new QueryDomainType("Policy", typeName);

			policy.simpleField("Hours Covered",
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
					"policyDefinition.availability.price.amount",
					Type.BIGDECIMAL);
			policy.simpleField("label.policy.transferFees",
					"policyDefinition.transferDetails.transferFee.amount",
					Type.BIGDECIMAL);
			policy.simpleField("label.policy.transferable",
					"policyDefinition.transferDetails.transferable",
					Type.BOOLEAN);
			policy.simpleField("label.warranty.warrantyType",
					"policyDefinition.warrantyType.type", Type.STRING);

			domainTypeSystem.registerDomainType(policy);
			return policy;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	protected DomainType oemPartReplaced() {
		String typeName = OEMPartReplaced.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType oemPartReplaced = new QueryDomainType(
					"Club Car Part Replaced", typeName);
			oemPartReplaced.simpleField("label.common.dueDate", "activePartReturn.dueDate",
					Type.DATE);
			oemPartReplaced.simpleField("label.campaign.paymentCondition",
					"activePartReturn.paymentCondition.code", Type.STRING);
			oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
					Type.INTEGER);
			oemPartReplaced.simpleField("label.common.returnlocation",
					"activePartReturn.warehouseLocation", Type.STRING);
			oemPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit.amount",
					Type.BIGDECIMAL);
			oemPartReplaced.oneToOne("label.common.partReplaced",
					"itemReference.referredItem", item());
			oemPartReplaced.oneToOne("label.businessUnit.clubCarDealerPartReplaced",
					"oemDealerPartReplaced", item());

			domainTypeSystem.registerDomainType(oemPartReplaced);
			return oemPartReplaced;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}

	}

	protected DomainType oemReplacedParts() {
		String typeName = OEMPartReplaced.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType oemPartReplaced = new QueryDomainType(
					"Club Car Replaced Parts", typeName);
			oemPartReplaced.simpleField("label.common.dueDate", "activePartReturn.dueDate",
					Type.DATE);
			oemPartReplaced.simpleField("label.campaign.paymentCondition",
					"activePartReturn.paymentCondition.code", Type.STRING);
			oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
					Type.INTEGER);
			oemPartReplaced.simpleField("label.common.returnlocation",
					"activePartReturn.warehouseLocation", Type.STRING);
			oemPartReplaced.oneToOne("label.common.partReplaced",
					"itemReference.referredItem", item());
			oemPartReplaced.oneToOne("label.businessUnit.clubCarDealerPartReplaced",
					"oemDealerPartReplaced", item());

			domainTypeSystem.registerDomainType(oemPartReplaced);
			return oemPartReplaced;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}

	}

	protected DomainType oemInstalledParts() {
		String typeName = InstalledParts.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType oemPartReplaced = new QueryDomainType(
					"Club Car Installed Parts", typeName);
			oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
					Type.INTEGER);
			oemPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit.amount",
					Type.BIGDECIMAL);
			oemPartReplaced.oneToOne("label.common.partReplaced",
					"itemReference.referredItem", item());
			oemPartReplaced.oneToOne("label.businessUnit.clubCarDealerPartReplaced",
					"oemDealerPartReplaced", item());

			domainTypeSystem.registerDomainType(oemPartReplaced);
			return oemPartReplaced;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}

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
			item.simpleField("label.common.itemNumber", "number", Type.STRING);
			item.simpleField("label.common.model", "model.description", Type.STRING);
			item.simpleField("label.common.product", "product.groupCode", Type.STRING);

			domainTypeSystem.registerDomainType(item);
			// return item;
		}
		return domainTypeSystem.getDomainType(typeName);

	}

	protected DomainType dealership() {
		String typeName = Dealership.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new QueryDomainType(
			/* Domain term */"Dealer", /*
										 * Unique Type Name
										 */typeName);
			dealership.simpleField("label.common.name", "name", Type.STRING);
			dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
			dealership.simpleField("label.common.preferredCurrency",
					"preferredCurrency", Type.STRING);
			dealership.simpleField("label.common.owner.customerclassification", "customerClassification", Type.STRING);
			dealership.simpleField("label.common.MarketingGroupCode", "marketingGroup", Type.STRING);
			domainTypeSystem.registerDomainType(dealership);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType inventoryItem() {
		String typeName = InventoryItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType inventoryItem = new QueryDomainType(
			/* Domain term */"Inventory Item",
			/** Unique Type Name */
			typeName);
			inventoryItem.simpleField("label.common.serialNumber", "serialNumber",
					Type.STRING);
            //Added  factory order number for Hussmann IR-HUS-INV-CR02
			inventoryItem.simpleField("label.common.factoryOrderNumber", "factoryOrderNumber",
					Type.STRING);
			inventoryItem.simpleField("label.common.dateOfDelivery", "deliveryDate",
					Type.DATE);
			inventoryItem.simpleField("Build Date", "builtOn", Type.DATE);

			inventoryItem.simpleField("label.inventory.dateOfShipment"/* Domain name */,
					"shipmentDate"/*
									 * field name, also the property expression
									 */, Type.DATE /*
									 * the type name of the field, either a
									 * build-in type or a domain type for
									 * one-to-one and one-to-many associations
									 */);
            inventoryItem.oneToOne("label.common.item", "ofType", item());
            //inventoryItem.oneToOne("label.inventory.manufacturingSiteInventory", "manufacturingSiteInventory", manufacturingSite());
            /*inventoryItem.queryTemplate("label.inventory.manufacturingSite",
                    "{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
                    Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
                    "inventoryItem.manufacturingSiteInventory.i18nLovTexts",
                "{alias}");*/

			domainTypeSystem.registerDomainType(inventoryItem);
		}
		return domainTypeSystem.getDomainType(typeName);
	}


	private DomainType manufacturingSite() {
        String typeName = ListOfValues.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType item = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            item.simpleField("label.common.code", "code", Type.STRING);
            item.simpleField("Description", "description", Type.STRING);
            item.simpleField("label.common.state", "state", Type.STRING);
            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }


	protected DomainType travelDetail() {
        String typeName = ServiceDetail.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType serviceDetail = new DomainType("Travel Rates", typeName);
            serviceDetail.simpleField("label.newClaim.perDiem", "perDiem.amount", Type.BIGDECIMAL);
            serviceDetail.simpleField("label.newClaim.rentalCharges", "rentalCharges.amount", Type.BIGDECIMAL);
            serviceDetail.simpleField("label.newClaim.parkingToll", "parkingAndTollExpense.amount", Type.BIGDECIMAL);
            serviceDetail.simpleField("label.campaign.travelDistancemiles", "travelDetails.distance", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelHours", "travelDetails.hours", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelLocation", "travelDetails.location", Type.STRING);
            serviceDetail.simpleField("label.campaign.travelTrips", "travelDetails.trips", Type.INTEGER);
            serviceDetail.simpleField("label.newClaim.additionalTravelHrs", "travelDetails.additionalHours", Type.INTEGER);

            this.domainTypeSystem.registerDomainType(serviceDetail);
            return serviceDetail;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }



	protected DomainType nonOEMPartReplaced() {
		String typeName = NonOEMPartReplaced.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType nonOEMPartReplaced = new QueryDomainType(
					"Outside Part Replaced", typeName);
			nonOEMPartReplaced.simpleField("label.common.description", "description",
					Type.STRING);
			nonOEMPartReplaced
					.simpleField("label.common.itemNumber", "number", Type.STRING);
			nonOEMPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
					Type.INTEGER);
			nonOEMPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit.amount",
					Type.BIGDECIMAL);
			nonOEMPartReplaced.simpleField("label.recoveryClaim.numberofOutsidePartsReplaced",
					"numberOfUnits", Type.INTEGER);
			domainTypeSystem.registerDomainType(nonOEMPartReplaced);
			return nonOEMPartReplaced;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	protected DomainType nonOEMPartInstalled() {
		String typeName = InstalledParts.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType nonOEMPartReplaced = new QueryDomainType(
					"Outside Part Installed", typeName);
			nonOEMPartReplaced.simpleField("label.common.description", "description",
					Type.STRING);
			nonOEMPartReplaced
					.simpleField("label.common.itemNumber", "number", Type.STRING);
			nonOEMPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
					Type.INTEGER);
			nonOEMPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit.amount",
					Type.BIGDECIMAL);
			nonOEMPartReplaced.simpleField("label.recoveryClaim.numberofOutsidePartsReplaced",
					"numberOfUnits", Type.INTEGER);
			domainTypeSystem.registerDomainType(nonOEMPartReplaced);
			return nonOEMPartReplaced;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	protected DomainType laborDetail() {
		String typeName = LaborDetail.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType laborDetail = new QueryDomainType("Service Details",
					typeName);
			laborDetail.simpleField("label.common.additionalLaborHours",
					"additionalLaborHours", Type.INTEGER);
			laborDetail.simpleField("label.common.additionalLaborReason",
					"reasonForAdditionalHours", Type.STRING);
			// laborDetail.oneToOne("Service Procedure", "serviceProcedure",
			// serviceProcedure());
			laborDetail.simpleField("label.common.jobCode",
					"serviceProcedure.definition.code", Type.STRING);
			laborDetail.simpleField("label.campaign.suggestedLaborHours",
					"serviceProcedure.suggestedLabourHours", Type.BIGDECIMAL);
			domainTypeSystem.registerDomainType(laborDetail);
			return laborDetail;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}

	/*
	 * protected DomainType serviceProcedure() { String typeName =
	 * ServiceProcedure.class.getSimpleName(); if
	 * (!domainTypeSystem.isKnown(typeName)) { DomainType jobCode = new
	 * QueryDomainType(typeName, typeName); jobCode.simpleField("Job Code",
	 * "definition.code", Type.STRING); jobCode.simpleField("Suggested Labor
	 * Hours", "suggestedLabourHours", Type.BIGDECIMAL);
	 * domainTypeSystem.registerDomainType(jobCode); return jobCode; } else {
	 * return domainTypeSystem.getDomainType(typeName); } }
	 */

	@Override
	public DomainTypeSystem getDomainTypeSystem() {
		// TODO Auto-generated method stub
		return domainTypeSystem;
	}

	public String getExpressionForDomainType(String typeName) {
		String expression = null;
		if ("Claim".equals(typeName)) {
			expression = "claim";
		}
		return expression;
	}

	public String getTopLevelTypeName() {
		return "Claim";
	}

	public String getTopLevelAlias() {
		return "claim";
	}

	protected DomainType claimedItem() {
		String typeName = ClaimedItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType claimedItem = new DomainType("Claimed Item", typeName);
			claimedItem.simpleField("label.common.hoursInService",
					"hoursInService", Type.INTEGER);
			claimedItem.simpleField("label.common.vinNumber", "vinNumber", Type.STRING);
			claimedItem.oneToOne("label.claim.applicablePolicy", "applicablePolicy",
					policy());
			claimedItem.oneToOne("label.inventory.inventoryItem",
					"itemReference.referredInventoryItem", inventoryItem());
			domainTypeSystem.registerDomainType(claimedItem);
			return claimedItem;
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType lineItemGroup() {
		String typeName = LineItemGroup.class.getSimpleName();
		if(!domainTypeSystem.isKnown(typeName)){
			DomainType lineItemGroup = new DomainType("Line Item Group", typeName);
			domainTypeSystem.registerDomainType(lineItemGroup);
			return lineItemGroup;
		}else{
			return domainTypeSystem.getDomainType(typeName);
		}
	}
	
	  public DomainType attachments() {
		  String typeName = Document.class.getSimpleName();
		  if (!domainTypeSystem.isKnown(typeName)) {
		  DomainType document = new DomainType("Document", typeName);
		  document.oneToOne("documentType", "documentType", document());
		  domainTypeSystem.registerDomainType(document);
		  return document;
		  }
		  return domainTypeSystem.getDomainType(typeName);
		  }
	  
	  public DomainType document() {
		  String typeName = DocumentType.class.getSimpleName();
		  if (!domainTypeSystem.isKnown(typeName)) {
		  DomainType documentType = new DomainType("Document Type", typeName);
		  documentType.simpleField("description", "description",Type.STRING );
		  domainTypeSystem.registerDomainType(documentType);
		  return documentType;
		  }
		  return domainTypeSystem.getDomainType(typeName);
		  }
	  
	protected DomainType campaign() {
		String typeName = Campaign.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType campaign = new QueryDomainType("Campaign", typeName);
			campaign.simpleField("label.claimSearch.campaignCode", "code",
					Type.STRING);

			domainTypeSystem.registerDomainType(campaign);
			return campaign;
		}
		return domainTypeSystem.getDomainType(typeName);

	}
}

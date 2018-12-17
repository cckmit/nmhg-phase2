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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.claim.JobDefinition;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MatchReadInfo;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.IsAReturnWatchedPart;
import tavant.twms.domain.rules.IsAReviewWatchedPart;
import tavant.twms.domain.rules.IsAWatchedDealership;
import tavant.twms.domain.rules.Predicate;
import tavant.twms.domain.rules.QueryDomainType;
import tavant.twms.domain.rules.Type;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.common.Document;
/**
 *
 * @author roopali.agrawal
 */
public abstract class AbstractRulesBusinessObjectModel extends
		AbstractBusinessObjectModel {
	private static Logger logger = LogManager
			.getLogger(AbstractRulesBusinessObjectModel.class);

	protected DomainTypeSystem domainTypeSystem;

	public static List<String> buSpecificKeyNames = new ArrayList<String>() {{
		add("label.bussinessUnit.clubCarDealerCausalPart");
		add("label.bussinessUnit.clubCarPartsReplaced");
		add("label.bussinessUnit.clubCarPartsAmount");
		add("label.bussinessUnit.totalClubCarPartsAmount");
		add("label.bussinessUnit.numberofClubCarPartReplaced");
		add("label.bussinessUnit.numberofNONClubCarPartReplaced");
		add("label.bussinessUnit.sumofClubCarPartsReplacedQuantity");
		add("label.bussinessUnit.sumofNONClubCarPartsReplacedQuantity");
		add("label.businessUnit.clubCarDealerPartReplaced");
		add("label.businessUnit.totalClubCarPartsClaimed");
		add("label.businessUnit.clubCarPartsClaimedCurrency");
		add("label.businessUnit.nonClubCarPartsClaimedCurrency");
		add("label.businessUnit.clubCarPartsClaimedCurrency");
		add("label.inventory.NumberofOEMInstalledParts");
		add("label.bussinessUnit.numberofClubCarReplacedParts");
		add("label.bussinessUnit.sumofNONClubCarInstalledPartsQuantity");
		add("label.inventory.sumofOEMReplacedPartsQuantity");
		add("label.newClaim.oEMPartReplacedInstalled");
		add("label.bussinessUnit.sumofClubCarInstalledPartsQuantity");
		add("label.bussinessUnit.numberofNONClubCarPartInstalled");
		


	}};

	protected DomainType claim() {
		String typeName = Claim.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType claim = new DomainType(/* Domain term */"Claim", /*
																		 * Unique
																		 * Type
																		 * Name
																		 */typeName);
			// Simple Fields.		
			claim.simpleField("label.common.causedby", "serviceInformation.causedBy.name",
					Type.STRING);
			claim.simpleField("label.claim.claimType", "clmTypeName", Type.STRING);
			claim.simpleField("label.rules.serializedPartsClaim", "partItemReference.serialized", Type.BOOLEAN);
			claim.simpleField("label.claim.ncr", "ncr", Type.BOOLEAN);
			claim.simpleField("label.claim.ncrWith30Days", "ncrWith30Days", Type.BOOLEAN);
			claim.simpleField("label.viewClaim.warrantyOrderClaim", "warrantyOrder", Type.BOOLEAN);
			claim.simpleField("label.claim.partSerialNumber", "partItemReference.referredInventoryItem.serialNumber", Type.STRING);
            claim.simpleField("label.claim.claimState", "activeClaimAudit.state.state", Type.STRING);
            claim.simpleField("label.viewClaim.workOrderNumber", "activeClaimAudit.workOrderNumber", Type.STRING);
            claim.simpleField("label.common.conditionFound", "conditionFound", Type.STRING);
			claim.simpleField("label.claim.claimNumber", "claimNumber", Type.STRING);
			claim.simpleField("label.claim.dealerClaimNumber", "histClmNo", Type.STRING);
			claim.simpleField("label.claim.claimDate", "filedOnDate", Type.DATE);
			claim.simpleField("label.common.dateFailure", "failureDate", Type.DATE);
			claim.simpleField("label.common.dateInstall", "installationDate",	
					Type.DATE);
			claim.simpleField("label.claim.lateFeeWaiver", "activeClaimAudit.isLateFeeApprovalRequired", Type.BOOLEAN);
			claim.simpleField("label.extendedwarrantyplan.dateOfPurchase", "purchaseDate", Type.DATE);
			claim.simpleField("label.common.dateRepair", "repairDate", Type.DATE);
            claim.simpleField("label.common.repairStartDate", "repairStartDate", Type.DATE);
            claim.simpleField("label.common.brandName", "brand", Type.STRING);
            claim.simpleField("label.viewClaim.emission", "emission", Type.BOOLEAN);
            claim.oneToMany("label.newClaim.attachment", "attachments", documentType());
			claim.oneToOne("label.rules.technicianEMEA", "activeClaimAudit.serviceInformation.serviceDetail.technician",	
					getTechnicianEMEA());
			//as per NMHGSLMS-449
			//as Technician is  free text field for AMER using 'serviceTechnician' as technician id
			claim.simpleField("label.rules.technicianIdAMER", "activeClaimAudit.serviceInformation.serviceDetail.serviceTechnician",Type.STRING);
			claim.oneToOne("label.rules.technicianLocaleAMER", "activeClaimAudit.serviceInformation.serviceDetail.technician",getTechnicianLocaleForAMER());
			/*
			 * claim.simpleField("Failure Code", "serviceInformation.faultCode",
			 * Type.STRING);
			 */
			
			claim.simpleField("label.newClaim.commercialPolicy", "commercialPolicy",  Type.BOOLEAN);
			 
			claim.simpleField("label.common.faultFound", "serviceInformation.faultFound.nameInEnglish",
					Type.STRING);
			claim.simpleField("label.common.otherComments", "otherComments", Type.STRING);			
			claim.simpleField("label.common.smrReason", "reasonForServiceManagerRequest.ruleContextDescription",
					Type.STRING);
			claim.simpleField("label.lov.claimCompetitorModel", "competitorModelDescription", Type.STRING);
			claim.simpleField("label.common.smrRequest", "serviceManagerRequest",
					Type.BOOLEAN);
			claim.simpleField("label.common.AuthCheck", "cmsAuthCheck",
					Type.BOOLEAN);
			claim.simpleField("label.common.authNumber", "authNumber",
					Type.STRING);
			claim.simpleField("label.viewClaim.cmsTicket", "cmsTicketNumber",
					Type.STRING);
			claim.simpleField("label.common.workPerformed", "workPerformed", Type.STRING);
			claim.simpleField("label.common.businessUnit", "businessUnitInfo.name",Type.STRING);
            claim.simpleField("label.common.diffOfLaborAskedAndLaborAllowed", "serviceInformation.serviceDetail.diffOfLaborAskedAndLaborAllowed",Type.BIGDECIMAL);

            // One To One Associations.
            claim.oneToOne("label.claim.part", "partItemReference.referredItem", item());
			claim.oneToOne("label.common.serviceProvider", "forDealerShip", dealership());
			claim.oneToOne("label.common.customer", "itemReference.referredInventoryItem.latestWarranty.customer", customer());
			claim.oneToOne("label.common.incidentals","serviceInformation.serviceDetail",
					travelDetail());
			claim.oneToOne("label.failure.failureCode", "serviceInformation.faultCodeRef",
					failureCode());
			claim.oneToOne("label.common.causalPart", "serviceInformation.causalPart",
					item());				
			 			claim.oneToOne("label.claim.claimPayment", "payment", payment());
			claim.oneToOne("label.common.matchReadInfo", "matchReadInfo", matchReadInfo());
			claim.oneToOne("label.supplier.supplierTitle", "serviceInformation.contract.supplier",
					supplier());
			
			claim.oneToOne("label.claim.sourceWarehouse", "sourceWarehouse", sourceWarehouse());
			
			
			// One To Many Associations.
			/*claim.oneToMany("label.bussinessUnit.clubCarPartsReplaced",
					"serviceInformation.serviceDetail.oemPartsReplaced",					
					oemPartReplaced());			
			claim.oneToMany("label.inventory.oemReplacedParts", 
					"serviceInformation.serviceDetail.replacedParts", 
					oemPartReplaced());
			claim.oneToMany("label.inventory.oemInstalledParts", 
					"serviceInformation.serviceDetail.installedParts", 
					oemInstalledParts());*/
			claim.oneToMany("label.newClaim.oEMPartReplacedInstalled",
					"activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled",
					hussmanPartsReplacedInstalled());
			
			claim.oneToMany("label.section.nonReplacedParts", 
					"serviceInformation.serviceDetail.nonOEMPartsReplaced", 
					nonOEMPartReplaced());		
			
			//claim.oneToMany("label.recoveryClaim.outsidePartsReplaced",
				//	"serviceInformation.serviceDetail.nonOEMPartsReplaced",
					//nonOEMPartReplaced());
			/*claim.oneToMany("label.recoveryClaim.miscPartsReplaced",
					"serviceInformation.serviceDetail.miscPartsReplaced", nonOEMPartReplaced());*/

			claim.oneToMany("label.recoveryClaim.serviceDetails",
					"serviceInformation.serviceDetail.laborPerformed",
					laborDetail());
			claim.oneToMany("label.claim.claimedItems", "claimedItems", claimedItem());
			
			claim.oneToOne("label.claim.filedBy", "filedBy", claimFiledBy());
			
						
			claim
					.functionField(
							"label.businessUnit.totalClubCarPartsClaimed",
							"totalOemPartsAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			claim
					.functionField(
							"label.bussinessUnit.clubCarPartsAmount",
							"oemPartsAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			
			claim
					.functionField(
							"label.businessUnit.totalNonClubCarPartsClaimed",
							"totalNonOemPartsAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			claim
					.functionField(
							"label.common.totalLabor",
							"totalLaborAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			claim
					.functionField(
							"label.common.labor",
							"laborAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			claim
					.functionField(
							"label.common.travel",
							"totalTravelAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			
			claim
					.functionField(
							"label.section.travelByDistance",
							"travelByDistanceAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());			
			claim
					.functionField(
							"label.section.travelByHours",
							"travelByHoursAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			claim
					.functionField(
							"label.section.freight",
							"itemFreightDutyAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			
			claim
					.functionField(
							"label.section.meals",
							"mealsAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());
			
			claim
					.functionField(
							"label.section.perDiem",
							"perDiemAmount({contextExpression}.payment)",
							Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());

                        claim.functionField(
                            "label.businessUnit.totalMiscellaneousAmountClaimed",
                            "totalMiscellaneousAmount({contextExpression}.payment)",
                            Type.MONEY, true, FunctionField.Types.ONE_TO_ONE.getBaseType());

			claim
					.functionField(
							"label.bussinessUnit.numberofClubCarPartReplaced",
							"claim.serviceInformation.serviceDetail.oemPartsReplaced.size",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.bussinessUnit.numberofClubCarReplacedParts",
							"numberOfReplacedParts({contextExpression}.serviceInformation.serviceDetail." +
							"hussmanPartsReplacedInstalled)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.inventory.NumberofOEMInstalledParts",
							"numberOfHussmannInstalledParts({contextExpression}.serviceInformation.serviceDetail." +
							"hussmanPartsReplacedInstalled)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
						"label.recoveryClaim.numberofOutsideReplacedParts",
						"numberOfNonHussmanInstalledParts({contextExpression}.serviceInformation.serviceDetail." +
						"hussmanPartsReplacedInstalled)",
						Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType());
			
			claim
					.functionField(
							"label.recoveryClaim.numberofOutsidePartsReplaced",
							"claim.serviceInformation.serviceDetail.nonOEMPartsReplaced.size",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.bussinessUnit.sumofClubCarPartsReplacedQuantity",
							"sumOfIntegers({contextExpression}.serviceInformation."
									+ "serviceDetail.oemPartsReplaced.{numberOfUnits})",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.inventory.sumofOEMReplacedPartsQuantity",
							"sumOfHussmannReplacedPartsQuantity({contextExpression}.serviceInformation."
									+ "serviceDetail.hussmanPartsReplacedInstalled)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.bussinessUnit.sumofClubCarInstalledPartsQuantity",
							"sumOfHussmannInstalledPartsQuantity({contextExpression}.serviceInformation."
									+ "serviceDetail.hussmanPartsReplacedInstalled)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.recoveryClaim.sumOfOutsidePartsReplacedQuantity",
							"sumOfIntegers({contextExpression}.serviceInformation."
									+ "serviceDetail.nonOEMPartsReplaced.{numberOfUnits})",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.recoveryClaim.sumOfOutsidePartsReplacedQuantity1",
							"sumOfNONHussmannPartsInstalledQuantity({contextExpression}.serviceInformation."
									+ "serviceDetail.hussmanPartsReplacedInstalled)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim
					.functionField(
							"label.claim.numberOfServiceDetails",
							"claim.serviceInformation.serviceDetail.laborPerformed.size",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim.functionField("label.claim.sumOfSuggestedLaborHours",
					"sumOfIntegers({contextExpression}.serviceInformation."
							+ "serviceDetail.laborPerformed.{serviceProcedure"
							+ ".suggestedLabourHours})", Type.INTEGER, true,
					FunctionField.Types.ONE_TO_MANY.getBaseType());

			claim.functionField("label.claim.sumofAdditionalLaborHours",
					"sumOfDecimals({contextExpression}.serviceInformation."
							+ "serviceDetail.laborPerformed."
							+ "{additionalLaborHours})", Type.INTEGER, true,
					FunctionField.Types.ONE_TO_MANY.getBaseType());

			claim
					.functionField(
							"label.campaign.averageSpeed",
							"averageSpeed({contextExpression}.serviceInformation.serviceDetail.travelDetails)",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());

			claim.functionField("label.claim.totalClaimAmountExcludingTax",
					"totalClaimedAmtExcludingTax({contextExpression}.payment)",
					Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
							.getBaseType());
			
			claim.functionField("label.claim.claimCurrentAmount",
					"totalCurrentClaimAmount({contextExpression}.payment)",
					Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
							.getBaseType());
			
			claim.functionField("label.claim.claimCostAmount",
					"totalClaimCostAmount({contextExpression}.payment)",
					Type.MONEY, true, FunctionField.Types.ONE_TO_ONE
							.getBaseType());

			claim
					.functionField(
							"label.claim.totalClaimAmountCurrency",
							"claim.payment.totalAmount.breachEncapsulationOfCurrency().currencyCode",
							Type.STRING, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());

			claim
					.functionField(
							"label.claim.numberOfJobCodesClaim",
							"{contextExpression}.serviceInformation.serviceDetail.laborPerformed.size",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());

		
			claim
					.functionField(
							"label.supplier.manualReviewSupplierContract",
							"isManualReviewRequiredForSupplierContract({contextExpression})",
							Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE
									.getBaseType());

            claim.functionField("label.exchange.isExchangeRateSetupForRepairDate",
                    "isExchangeRateSetupForRepairDate({contextExpression})",
                    Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
            

            claim.functionField("label.reports.acr.isPublished",
					"isACRPublished({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim
					.functionField(
						"label.multiple.jobcode.same.subcomponent",
						"isMultiJobCodeSameSubComponent({contextExpression})",
						Type.BOOLEAN , true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.owned.by",
					"isOwnedByFilingUser({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.any.pending.field.modifications",
					"anyPendingFieldModifications({contextExpression})",Type.STRING , true, 
					FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim
					.functionField(
							"label.laborType.laborSplit.count",
							"claim.serviceInformation.serviceDetail.laborSplit.size",
							Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY
									.getBaseType());
			claim.functionField("label.rules.claim", "isPartsClaimWithoutHost({contextExpression})", 
								Type.BOOLEAN, true,FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.repeated.jobCode",
					"isJobCodeAlreadyIncluded({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.rules.competitorModel",
					"isPartsClaimOnCompetitorModel({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim.oneToMany("label.bussinessUnit.faultCodeAttributes", "serviceInformation.faultClaimAttributes", claimAttributes());
			claim.oneToMany("label.bussinessUnit.partAttributes", "serviceInformation.partClaimAttributes", claimAttributes());
			claim.oneToMany("label.bussinessUnit.claimAttributes", "claimAdditionalAttributes", claimAttributes());
			claim.functionField("label.rules.isClaimingDealerInActive",
					"isClaimingDealerInActive({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.rules.isCausalPartModelSameAsInventoryItemModel",
					"isCausalPartModelSameAsInventoryItemModel({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.rules.isReplacedPartModelSameAsInventoryItemModel",
					"isReplacedPartModelSameAsInventoryItemModel({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());

			claim.functionField("label.rules.isMultipleSuppliersAssociatedWithClaim",
					"isMultipleSuppliersAssociatedWithClaim({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			
			claim.functionField("label.rules.isFaultCodeAndJobCodeOfExactMatch",
					"isFaultCodeAndJobCodeOfExactMatch({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());

			claim.functionField("label.rules.isReplacedSerialNumberSameAsOtherClaim",
					"isReplacedSerialNumberSameAsOtherClaim({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());

			claim.functionField("label.rules.isInstalledSerialNumberSameAsOtherClaim",
					"isInstalledSerialNumberSameAsOtherClaim({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			// Added for NMHGSMLS-577: RTM-165 - START
			claim.functionField(
					"label.rules.additionalTravelHours",
					"additionalTravelHours({contextExpression})",
					Type.BIGDECIMAL, true,
					FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim.simpleField("label.rules.customerAddressChanged", "serviceInformation.serviceDetail.travelDetails.travelAddressChanged", Type.BOOLEAN);
			claim.functionField(
					"label.rules.travelTrip",
					"travelTrips({contextExpression}.serviceInformation.serviceDetail.travelDetails)",
					Type.INTEGER, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim.functionField(
					"label.rules.causalAndClaimBrand",
					"isCasualPartBrandSameOfClaimBrand({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim.functionField(
					"label.rules.installedAndClaimBrand",
					"isInstalledPartBrandSameOfClaimBrand({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			claim.functionField(
					"label.rules.removedAndClaimBrand",
					"isRemovedPartBrandSameOfClaimBrand({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
			// Added for NMHGSMLS-577: RTM-165 - END
			// Added for NMHGSLMS-576: RTM-156,682
			claim.simpleField("label.rules.manualAuditRequired", "manualReviewConfigured", Type.BOOLEAN);
			//technician certification rule
			claim.functionField("label.technician.verifyTechnicianCertified",
					"isTechCertifiedRule({contextExpression})",
					Type.BOOLEAN, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
            domainTypeSystem.registerDomainType(claim);
			return claim;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType failureCode() {
		String typeName = FaultCode.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType faultCode = new DomainType("Fault Code", typeName);
			faultCode.simpleField("label.failure.faultCodeString", "definition.code",
					Type.STRING);
			domainTypeSystem.registerDomainType(faultCode);
			return faultCode;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType contract() {
		String typeName = Contract.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType policy = new DomainType("Contract", typeName);
			domainTypeSystem.registerDomainType(policy);
			return policy;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType policy() {
		String typeName = Policy.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType policy = new DomainType("Policy", typeName);

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
					"policyDefinition.availability.price", Type.MONEY);
			policy.simpleField("label.policy.transferFees",
					"policyDefinition.transferDetails.transferFee", Type.MONEY);
			policy.simpleField("label.policy.transferable",
					"policyDefinition.transferDetails.transferable",
					Type.BOOLEAN);
			policy.simpleField("label.warranty.warrantyType",
					"policyDefinition.warrantyType.type", Type.STRING);

			domainTypeSystem.registerDomainType(policy);
			return policy;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType payment() {
		String typeName = Payment.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType payment = new DomainType("Payment", typeName);
			payment.simpleField("label.claim.claimAmount", "claimedAmount", Type.MONEY);
			payment.simpleField("label.claim.totalClaimAmount", "totalAmount",
							Type.MONEY);
			domainTypeSystem.registerDomainType(payment);
			return payment;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType travelDetail() {
        String typeName = ServiceDetail.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType serviceDetail = new DomainType("Incidentals", typeName);
            serviceDetail.oneToOne("label.newClaim.itemFreightDutyInvoicePresent", "freightDutyInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.mealsInvoicePresent", "mealsInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.perDiemInvoicePresent", "perDiemInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.parkingAndTollInvoicePresent", "parkingAndTollInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.rentalChargesInvoicePresent", "rentalChargesInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.localPurchaseExpensePresent","localPurchaseExpense.id", document());
            serviceDetail.oneToOne("label.newClaim.tollsInvoicePresent", "tollsInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.otherFreightDutyInvoicePresent", "otherFreightDutyInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.transportationInvoicePresent", "transportationInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.othersInvoicePresent", "othersInvoice.id", document());
            serviceDetail.oneToOne("label.newClaim.handlingFeeInvoicePresent", "handlingFeeInvoice.id", document());
            serviceDetail.simpleField("label.newClaim.perDiem", "perDiem", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.rentalCharges", "rentalCharges", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.parkingToll", "parkingAndTollExpense", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.otherFreightDuty", "itemFreightAndDuty", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.tolls", "tollsExpense", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.localPurchase", "localPurchaseExpense", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.meals", "mealsExpense", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.itemFreightDuty", "itemFreightAndDuty", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.others", "othersExpense", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.otherFreightDuty", "otherFreightDutyExpense", Type.MONEY);
            serviceDetail.simpleField("label.section.handlingFee", "handlingFee", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.transportation", "transportationAmt", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.isInvoiceAvailable", "invoiceAvailable", Type.BOOLEAN);
            serviceDetail.simpleField("label.campaign.travelDistancemiles", "travelDetails.distance", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelHours", "travelDetails.hours", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelLocation", "travelDetails.location", Type.STRING);
            serviceDetail.simpleField("label.campaign.travelTrips", "travelDetails.trips", Type.INTEGER);
            serviceDetail.simpleField("label.newClaim.additionalTravelHrs", "travelDetails.additionalHours", Type.INTEGER);
            serviceDetail.simpleField("label.newClaim.additionalTravelDistance", "travelDetails.additionalDistance", Type.INTEGER);
            serviceDetail.simpleField("label.travel.distance.charge", "travelDetails.distanceCharge", Type.MONEY);
            serviceDetail.simpleField("label.travel.time.charge", "travelDetails.timeCharge", Type.MONEY);
            serviceDetail.simpleField("label.travel.trip.charge", "travelDetails.tripCharge", Type.MONEY);
            this.domainTypeSystem.registerDomainType(serviceDetail);
            return serviceDetail;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }
	
	protected DomainType getTechnicianEMEA(){
		String typeName = User.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType technician = new DomainType("Technician", typeName);
            return technician;
        }
        return domainTypeSystem.getDomainType(typeName);
	}
	//as per NMHGSLMS-449
	protected DomainType getTechnicianLocaleForAMER(){
		String typeName = User.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType technician = new DomainType("Technician", typeName);                                 
            technician.simpleField("label.manageProfile.locale", "locale.language", Type.STRING);	
            domainTypeSystem.registerDomainType(technician);
            return technician;
        }
        return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType laborDetail() {
		String typeName = LaborDetail.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType laborDetail = new DomainType("Service Details", typeName);
			laborDetail.simpleField("label.common.additionalLaborHours",
					"additionalLaborHours", Type.INTEGER);
			laborDetail.simpleField("label.common.additionalLaborReason",
					"reasonForAdditionalHours", Type.STRING);
			laborDetail.oneToOne("label.common.jobCode", "serviceProcedure",serviceProcedure());
			laborDetail.simpleField("label.campaign.laborHours", "hoursSpent", Type.BIGDECIMAL);
			laborDetail.simpleField("label.manageRates.labourRate", "laborRate", Type.MONEY);
			laborDetail.oneToMany("label.bussinessUnit.jobCodeAttributes", "claimAttributes", claimAttributes());
            domainTypeSystem.registerDomainType(laborDetail);
			return laborDetail;
		}

		return domainTypeSystem.getDomainType(typeName);
	}
	protected DomainType customer() {
		String typeName = Customer.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType customer = new DomainType("Customer", typeName);
			customer.simpleField("label.common.customerName",
					"name", Type.STRING);
            domainTypeSystem.registerDomainType(customer);
			return customer;
		}

		return domainTypeSystem.getDomainType(typeName);
	}
	protected DomainType serviceProcedure() {
		String typeName = ServiceProcedure.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType jobCode = new DomainType("Job Code", typeName);
			jobCode.simpleField("label.common.jobCodeString", "definition.code",
					Type.STRING);
			jobCode.simpleField("label.campaign.suggestedLaborHours",
					"suggestedLabourHours", Type.DOUBLE);
						domainTypeSystem.registerDomainType(jobCode);
			return jobCode;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType job() {
		String typeName = Job.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType job = new DomainType("Job Performed", typeName);
			job.simpleField("label.campaign.standardLaborHours", "definition.hoursSpent",
					Type.INTEGER);
			domainTypeSystem.registerDomainType(job);
			return job;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType jobDefinition() {
		String typeName = JobDefinition.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType jobDefinition = new DomainType("Job Definition",
					typeName);
			jobDefinition.simpleField("label.campaign.standardLaborHours", "hoursSpent",
					Type.INTEGER);
			domainTypeSystem.registerDomainType(jobDefinition);
			return jobDefinition;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType nonOEMPartReplaced() {
		String typeName = NonOEMPartReplaced.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType nonOEMPartReplaced = new DomainType(
					"Outside Part Replaced", typeName);
			nonOEMPartReplaced.simpleField("label.common.description", "description",
					Type.STRING);
			nonOEMPartReplaced
					.simpleField("columnTitle.common.itemNumber", "miscItem.partNumber", Type.STRING);
			
			/*nonOEMPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit",
					Type.MONEY);*/
			
			nonOEMPartReplaced.simpleField("label.rules.unitPrice", "pricePerUnit.breachEncapsulationOfAmount()",
					Type.BIGDECIMAL);

			nonOEMPartReplaced.simpleField("Total Amount", "totalAmount",
					Type.BIGDECIMAL);
			
			nonOEMPartReplaced.simpleField("label.recoveryClaim.qtyOutsidePartsReplaced",
					"numberOfUnits", Type.INTEGER);
			nonOEMPartReplaced.oneToOne("label.newClaim.invoice", "invoice.id", document());
			domainTypeSystem.registerDomainType(nonOEMPartReplaced);
			return nonOEMPartReplaced;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType document() {
		String typeName = Document.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType document = new DomainType("Invoice", typeName);
			document.simpleField("label.common.attachment.size",
					"size", Type.INTEGER);			
			domainTypeSystem.registerDomainType(document);
			return document;
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType documentType() {
		String typeName = Document.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType documentType = new DomainType("documentType", typeName);
			documentType.simpleField("label.common.attachment.type",
					"documentType.description", Type.STRING);
			documentType.simpleField("label.common.attachment.size",
					"size", Type.INTEGER);
			domainTypeSystem.registerDomainType(documentType);
			return documentType;
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType oemPartReplaced() {
			String typeName = OEMPartReplaced.class.getSimpleName();
			if (!domainTypeSystem.isKnown(typeName)) {
				DomainType oemPartReplaced = new DomainType(
				/* Domain term */"OEM Part Replaced",
				/** Unique Type Name */
				typeName);
				oemPartReplaced.simpleField("label.common.dueDate", "ruleContextActivePartReturn.dueDate",
						Type.DATE);
				oemPartReplaced.simpleField("label.campaign.paymentCondition",
						"ruleContextActivePartReturn.paymentCondition.description", Type.STRING);
				oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits",
						Type.INTEGER);
				oemPartReplaced.simpleField("label.common.returnlocation",
						"ruleContextActivePartReturn.warehouseLocation", Type.STRING);
				oemPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit",
						Type.MONEY);
				oemPartReplaced.simpleField("label.component.serialNumber", "itemReference.referredItem.serialNumber", Type.STRING);
				oemPartReplaced.simpleField("label.common.dateCode", "dateCode", Type.STRING);
				oemPartReplaced.oneToOne("label.common.partReplaced",
						"itemReference.referredItem", item());
				oemPartReplaced.oneToOne("label.common.warehouseLabelofReturnLocation",
						"returnLocation", warehouseLocation());
				
				domainTypeSystem.registerDomainType(oemPartReplaced);
				return oemPartReplaced;
			}

			return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType hussmanPartsReplacedInstalled(){
		String typeName = HussmanPartsReplacedInstalled.class.getSimpleName();
		if(!domainTypeSystem.isKnown(typeName)){
			DomainType hussmanPartsReplacedInstalled = new QueryDomainType("label.newClaim.oEMPartReplacedInstalled",
															typeName);
			hussmanPartsReplacedInstalled.oneToMany("label.inventory.oemReplacedParts", 
					"replacedParts", 
					oemPartReplaced());
			hussmanPartsReplacedInstalled.oneToMany("label.inventory.oemInstalledParts", 
					"hussmanInstalledParts", 
					oemInstalledParts());
		/*	hussmanPartsReplacedInstalled.oneToMany("label.section.nonReplacedParts", 
					"nonHussmanInstalledParts", 
					nonOemInstalledParts());*/
			domainTypeSystem.registerDomainType(hussmanPartsReplacedInstalled);
			return hussmanPartsReplacedInstalled;
		}else {
			return domainTypeSystem.getDomainType(typeName);
		}
	}


	protected DomainType oemInstalledParts() {
		String typeName = InstalledParts.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType installedParts = new QueryDomainType(
					"Oem Installed Parts", typeName);
			installedParts.simpleField("label.component.serialNumber", "serialNumber",
					Type.STRING);
			installedParts.simpleField("columnTitle.common.quantity", "numberOfUnits",
					Type.INTEGER);
			installedParts.simpleField("label.newClaim.unitPrice", "pricePerUnit.breachEncapsulationOfAmount()",
					Type.BIGDECIMAL);
			installedParts.simpleField("label.newClaim.unitCostPrice", "costPricePerUnit",
					Type.MONEY);
			installedParts.simpleField("label.common.dateCode", "dateCode", Type.STRING);
			installedParts.oneToOne("label.common.item",
					"item", item());			
			domainTypeSystem.registerDomainType(installedParts);
			return installedParts;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}

	}
	
	protected DomainType nonOemInstalledParts() {
		String typeName = "nonOem"+InstalledParts.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType installedParts = new QueryDomainType(
					"Non Oem Installed Parts", typeName);
			installedParts.simpleField("label.quantity", "numberOfUnits",Type.INTEGER);
			installedParts.simpleField("label.common.price", "pricePerUnit.breachEncapsulationOfAmount() * numberOfUnits",Type.BIGDECIMAL);
			installedParts.simpleField("label.common.partNumber", "partNumber", Type.STRING);
			installedParts.simpleField("columnTitle.common.description", "description", Type.STRING);
			installedParts.oneToOne("label.newClaim.invoice", "invoice", document());
			domainTypeSystem.registerDomainType(installedParts);
			return installedParts;
		} else {
			return domainTypeSystem.getDomainType(typeName);
		}

	}

	protected DomainType inventoryItem() {
		String typeName = InventoryItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType inventoryItem = new DomainType(
			/* Domain term */"Inventory Item",
			/** Unique Type Name */
			typeName);
			inventoryItem.simpleField("Date of Delivery", "deliveryDate",
					Type.DATE);
			inventoryItem.simpleField("label.inventory.dateOfShipment"/* Domain name */,
					"shipmentDate"/*
									 * field name, also the property expression
									 */, Type.DATE /*
									 * the type name of the field, either a
									 * build-in type or a domain type for
									 * one-to-one and one-to-many associations
									 */);
			inventoryItem.simpleField("label.common.dateOfBuild", "builtOn", Type.DATE);

			inventoryItem.oneToOne("label.inventory.manufacturingSiteInventory", "manufacturingSiteInventory", manufacturingSite());

			inventoryItem.simpleField("label.common.serialNumber", "serialNumber",
					Type.STRING);
			inventoryItem.simpleField("label.common.factoryOrderNumber", "factoryOrderNumber",
					Type.STRING);
			inventoryItem.simpleField("label.common.registrationDate", "registrationDate",
					Type.DATE);

			inventoryItem.simpleField("label.warrantyAdmin.itemType", "type.type", Type.STRING);
						
			inventoryItem.oneToOne("item", "ofType", item());
			
			inventoryItem.oneToOne("columnTitle.warrantyId", "warranty", warranty());          
            
            inventoryItem.simpleField("label.invItem.itemCondition", "conditionType.itemCondition", Type.STRING);

            inventoryItem.oneToMany("label.invItem.reportAnswers", "reportAnswers", reportAnswer());
            inventoryItem.simpleField("label.invItem.isWarrantyPending", "pendingWarranty", Type.BOOLEAN);
            inventoryItem.simpleField("label.invItem.isDisclaimer", "isDisclaimer", Type.BOOLEAN);
            inventoryItem.simpleField("label.invItem.latestWarrantyType", "latestWarranty.transactionType.trnxTypeKey", Type.STRING);
            inventoryItem.simpleField("label.invItem.inventoryComment", "inventoryCommentExists", Type.BOOLEAN);

            inventoryItem.oneToOne("label.invItem.currentOwner", "currentOwner", party());
            inventoryItem.oneToOne("label.invItem.sourceWarehouse", "sourceWarehouse", sourceWarehouse());
            inventoryItem.oneToMany("label.common.campnotification", "campaignNotifications", campaignNotification());

            inventoryItem.functionField(
					"label.common.salesOrderNumber",
					"getSalesOrderNumber()",
					Type.STRING, false, FunctionField.Types.ONE_TO_ONE
							.getBaseType());

            domainTypeSystem.registerDomainType(inventoryItem);
		}
		return domainTypeSystem.getDomainType(typeName);
	}


    protected DomainType reportAnswer() {
		String typeName = CustomReportAnswer.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType reportAnswer = new DomainType("Report Answer", typeName);
            reportAnswer.oneToOne("label.reportAns.customReport","customReport",customReport());
			domainTypeSystem.registerDomainType(reportAnswer);
			return reportAnswer;
		}
		return domainTypeSystem.getDomainType(typeName);
	}

    protected DomainType customReport() {
		String typeName = CustomReport.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType customReport = new DomainType("CustomReport", typeName);
            customReport.simpleField("label.customReport.name", "name",Type.STRING);
            customReport.simpleField("label.customReport.published", "published", Type.BOOLEAN);
            customReport.oneToOne("label.customReport.reportType", "reportType", reportType());
			domainTypeSystem.registerDomainType(customReport);
			return customReport;
		}

		return domainTypeSystem.getDomainType(typeName);
	}

    private DomainType reportType() {
        String typeName = ListOfValues.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType item = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            item.simpleField("label.common.code", "code", Type.STRING);
            item.simpleField("label.common.description", "description", Type.STRING);
            item.simpleField("label.common.state", "state", Type.STRING);
            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }

    protected DomainType sourceWarehouse() {
		String typeName = SourceWarehouse.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType sourceWarehouse = new DomainType("SourceWarehouse", typeName);
            sourceWarehouse.simpleField("label.common.name", "name",Type.STRING);
            sourceWarehouse.simpleField("label.common.code", "code",Type.STRING);
            domainTypeSystem.registerDomainType(sourceWarehouse);
			return sourceWarehouse;
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
            item.simpleField("label.common.description", "description", Type.STRING);
            item.simpleField("label.common.state", "state", Type.STRING);
            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }


	protected DomainType inventoryTransaction() {
		String typeName = InventoryTransaction.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType inventoryTransaction = new DomainType(
			/* Domain term */"Inventory Transaction", /*
														 * Unique Type Name
														 */
			typeName);
			inventoryTransaction.simpleField("label.warrantyAdmin.transactionDate",
					"transactionDate", Type.DATE);
			inventoryTransaction.oneToOne("label.common.seller", "seller", party());
			inventoryTransaction.oneToOne("label.common.buyer", "buyer", party());
			inventoryTransaction.simpleField("label.inventory.salesOrderNumber",
					"salesOrderNumber", Type.STRING);
			inventoryTransaction.simpleField("label.inventory.invoiceDate", "invoiceDate",
					Type.DATE);
			// Avoid back-pointer to inventory item as it introduces cyclic call
			// sequences in this class.
			// An error like this would result in stack-overflow at runtime.
			domainTypeSystem.registerDomainType(inventoryTransaction);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType item() {
		String typeName = Item.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType item = new DomainType("Item", typeName) {
				@Override
				public void setDefaultPredicates() {
					super.setDefaultPredicates();
					Set<Class<? extends Predicate>> predicates = supportedPredicates();
					predicates.add(IsAReturnWatchedPart.class);
					predicates.add(IsAReviewWatchedPart.class);
				}
			};
			item.simpleField("label.common.itemDescription", "ruleContextDescription", Type.STRING);
			item.simpleField("label.common.itemNumberForCausalPart", "number", Type.STRING);
			item.oneToOne("label.common.model", "model", model());
			item.simpleField("label.common.product", "product.name", Type.STRING);
			item.simpleField("label.common.itaTruckClass", "product.isPartOf.groupCode", Type.STRING);
			item.simpleField("Status", "status", Type.STRING);
			item.simpleField("Item Type", "itemType", Type.STRING);
			item.oneToMany("label.common.brandItems", "brandItems", brandItem());
			domainTypeSystem.registerDomainType(item);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType brandItem() {
		String typeName = BrandItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType brandItem = new DomainType("BrandItem", typeName);
			brandItem.simpleField("label.common.itemNumber", "itemNumber", Type.STRING);
			brandItem.simpleField("label.common.brandName", "brand", Type.STRING);
			domainTypeSystem.registerDomainType(brandItem);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType party() {
		String typeName = Party.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType party = new DomainType(/* Domain term */"Party", /*
																		 * Unique
																		 * Type
																		 * Name
																		 */typeName);
			party.simpleField("label.common.name", "name", Type.STRING);
                        party.simpleField("label.common.type", "type",
                                Type.STRING);
			domainTypeSystem.registerDomainType(party);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType dealership() {
		String typeName = ServiceProvider.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new DomainType(/* Domain term */"Dealer", /*
																				 * Unique
																				 * Type
																				 * Name
																				 */typeName) {
				@Override
				public void setDefaultPredicates() {
					super.setDefaultPredicates();
					Set<Class<? extends Predicate>> predicates = supportedPredicates();
					predicates.add(IsAWatchedDealership.class);
				}
			};
			dealership.simpleField("label.common.name", "name", Type.STRING);
			dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
			dealership.simpleField("label.common.preferredCurrency",
					"preferredCurrency.currencyCode", Type.STRING);
			dealership.simpleField("label.common.type", "type", Type.STRING);
			dealership.simpleField("label.common.customerclassification", "customerClassification", Type.STRING) ;
			dealership.simpleField("country", "address.country", Type.STRING) ;
			domainTypeSystem.registerDomainType(dealership);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	@Override
	public DomainTypeSystem getDomainTypeSystem() {
		// TODO Auto-generated method stub
		return domainTypeSystem;
	}

	protected DomainType claimedItem() {
		String typeName = ClaimedItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType claimedItem = new DomainType("Claimed Item", typeName);
			claimedItem.simpleField("label.common.hoursOnTruck",
					"hoursInService", Type.INTEGER);
			claimedItem.simpleField("label.common.serializedClaim",
                            "itemReference.serialized", Type.BOOLEAN);
			claimedItem.oneToOne("label.claim.applicablePolicy", "applicablePolicy",
					policy());
			claimedItem.oneToOne("label.inventory.inventoryItem",
					"itemReference.referredInventoryItem", inventoryItem());
            claimedItem.oneToOne("label.item.unserializedItemModel",
                            "itemReference.model", model());
            claimedItem.simpleField("label.common.vinNumber", "vinNumber", Type.STRING);
            claimedItem.oneToMany("label.bussinessUnit.claimedItemAttributes", "claimAttributes", claimAttributes());
            //claimedItem.oneToOne("columnTitle.common.product","itemReference.unserializedItem.product", model());
            claimedItem.simpleField("label.item.unserializedItemProduct", "itemReference.model.isPartOf.name",Type.STRING);            
			domainTypeSystem.registerDomainType(claimedItem);
			return claimedItem;
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	protected DomainType matchReadInfo() {
		String typeName = MatchReadInfo.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType matchReadInfo = new DomainType("Match Read Info",
					typeName);
			matchReadInfo.simpleField("label.common.matchReadScore", "score", Type.LONG);
			domainTypeSystem.registerDomainType(matchReadInfo);
			return matchReadInfo;
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	

	protected DomainType supplier() {
		String typeName = Supplier.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType supplier = new DomainType("Supplier", typeName);
			supplier.simpleField("label.common.name", "name", Type.STRING);
			supplier.simpleField("label.common.suppliernumber", "supplierNumber", Type.STRING);
			domainTypeSystem.registerDomainType(supplier);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	
	protected DomainType campaign() {
		String typeName = Campaign.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType campaign = new DomainType("Campaign", typeName);
			campaign.simpleField("label.campaign.code", "code", Type.STRING);
			campaign.simpleField("label.common.startDate", "fromDate", Type.DATE);
			campaign.simpleField("label.common.endDate", "tillDate", Type.DATE);
			campaign.simpleField("label.campaign.classCode", "campaignClass.code", Type.STRING);
			campaign.oneToMany("label.newClaim.document", "attachments", campaignDocument());
			campaign.oneToMany("label.common.campaignLabels","labels", campaignLabels());
			domainTypeSystem.registerDomainType(campaign);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType campaignDocument() {
		String typeName = "Campaign"+Document.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType document = new DomainType("CampaignDocument", typeName);
			document.simpleField("label.customReportAnswer.mandatory", "mandatory", Type.BOOLEAN);
			domainTypeSystem.registerDomainType(document);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType claimFiledBy() {
		String typeName = User.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType user = new DomainType("User", typeName);
			user.simpleField("label.manageProfile.locale", "locale.language", Type.STRING);			
			domainTypeSystem.registerDomainType(user);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType warranty(){
		String typeName = Warranty.class.getSimpleName();
		if(!domainTypeSystem.isKnown(typeName)){
			DomainType warranty = new DomainType("columnTitle.warrantyId", typeName);
			warranty.simpleField("columnTitle.common.warrantyStartDate", "startDate", Type.DATE);
			warranty.simpleField("label.warrantyAdmin.customerType", "customerType", Type.STRING);
			warranty.oneToOne("label.marketInfo", "marketingInformation", marketingInformation());
			domainTypeSystem.registerDomainType(warranty);
			return warranty;
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	private DomainType marketingInformation(){
		String typeName = MarketingInformation.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType marketingInformation = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);            
            marketingInformation.simpleField("label.defineSearch.contractCode", "contractCode.contractCode", Type.STRING);
            marketingInformation.simpleField("label.defineSearch.internalInstallType", "internalInstallType.internalInstallType", Type.STRING);
            domainTypeSystem.registerDomainType(marketingInformation);
        }
        return domainTypeSystem.getDomainType(typeName);
	}
	
    private DomainType model() {
        String typeName = ItemGroup.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType itemGroup = new DomainType(/* Domain term */"Model", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            itemGroup.simpleField("label.common.name", "name", Type.STRING);
            itemGroup.simpleField("label.common.groupCode", "groupCode", Type.STRING);
            itemGroup.simpleField("label.common.description", "description", Type.STRING);                            
            //itemGroup.simpleField("columnTitle.common.product","isPartOf.isPartOf.name",Type.STRING);
            itemGroup.functionField(
					"columnTitle.common.product",
					"getProductNameForUnserializedItem()",
					Type.STRING, false, FunctionField.Types.ONE_TO_ONE
							.getBaseType());
            domainTypeSystem.registerDomainType(itemGroup);
        }
        return domainTypeSystem.getDomainType(typeName);
    }

    public DomainType claimAttributes() {
        String typeName = ClaimAttributes.class.getSimpleName();
        DomainType claimAttributes = new DomainType(/* Domain term */typeName, /*
                                                                 * Unique TypeR
                                                                 * Name
                                                                 */typeName);
        if (!domainTypeSystem.isKnown(typeName)) {
            domainTypeSystem.registerDomainType(claimAttributes);
        }
        return claimAttributes;
    }
    
    public DomainType campaignNotification() {
        String typeName = CampaignNotification.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType claimedItem = new DomainType("Campaign Notification", typeName);
            claimedItem.simpleField("label.common.notificationStatus", "notificationStatus", Type.STRING);
            claimedItem.oneToOne("label.campaign.campaign","campaign",campaign());
            domainTypeSystem.registerDomainType(claimedItem);
            return claimedItem;
        }
        return domainTypeSystem.getDomainType(typeName);
    }
    public DomainType  campaignLabels(){
    	 String typeName = Label.class.getSimpleName();	
    	  if (!domainTypeSystem.isKnown(typeName)) {
              DomainType labels = new DomainType("Label", typeName);
              labels.simpleField("label.common.labelName", "name", Type.STRING);
              domainTypeSystem.registerDomainType(labels);
              return labels;
          }
          return domainTypeSystem.getDomainType(typeName);
    	
    }  
    public DomainType warehouseLocation() {
		String typeName = Location.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType location = new DomainType("location", typeName);
			domainTypeSystem.registerDomainType(location);
			return location;
		}
		return domainTypeSystem.getDomainType(typeName);
    }
}

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

package tavant.twms.domain.common;

public enum ListOfValuesType {

    SmrReason("SMRREASON"),
    AcceptanceReason("ACCEPTANCEREASON"),
    AcceptanceReasonForCP("ACCEPTANCEREASONFORCP"),
    AccountabilityCode("ACCOUNTABILITYCODE"),
    FailureReason("FAILUREREASON"),
    PartAcceptanceReason("PARTACCEPTANCEREASON"),
    RecoveryClaimAcceptanceReason("RECOVERYCLAIMACCEPTANCEREASON"),
    RecoveryClaimRejectionReason("RECOVERYCLAIMREJECTIONREASON"),
    RejectionReason("REJECTIONREASON"),
    ManufacturingSiteInventory("MANUFACTURINGSITEINVENTORY"),
    CampaignClass("CAMPAIGNCLASS"),
    SellingEntity("SELLINGENTITY"),
    ReportType("REPORTTYPE"),
    Oem("OEM"),
    ClaimCompetitorModel("CLAIMCOMPETITORMODEL"),
    FieldModificationInventoryStatus("FIELDMODIFICATIONINVENTORYSTATUS"),
    DocumentType("DOCUMENTTYPE"),
    Suppliers("SUPPLIERS"),
    RecoveryClaimCannotRecoverReason("RECOVERYCLAIMCANNOTRECOVERREASON"),
    UnitDocumentType("UNITDOCUMENTTYPE"),
    AdditionalComponentsSubType("ADDITIONALCOMPONENTSUBTYPE"),
    AdditionalComponentsType("ADDITIONALCOMPONENTTYPE"),
    LaborRateType("LABORRATETYPE"),
    ReviewResposibility("REVIEWRESPONSIBILITY"),
    RecoveryClaimDocumentType("RECOVERYDOCUMENTTYPE"),
    PutOnHoldReason("PUTONHOLDREASON"),
    RequestInfoFromUser("REQUESTINFOFROMUSER"),
    SupplierPartAcceptanceReason("SUPPLIERPARTACCEPTANCEREASON"),
    SupplierPartRejectionReason("SUPPLIERPARTREJECTIONREASON"),
    DiscountType("DISCOUNTTYPE"),
    // Fleet LoVs
    CallType("CALLTYPE"),
    CausedByCodes("CAUSEDBYCODES"),
    ClaimAcceptanceReason("CLAIMACCEPTANCEREASON"),
    ClaimRejectionReason("CLAIMREJECTIONREASON"),
    ClaimReview("CLAIMREVIEW"),
    ComponentCodes("COMPONENTCODES"),
    DealerFailure("DEALERFAILURE"),
    EquipmentCondition("EQUIPMENTCONDITION"),
    EquipmentDescription("EQUIPMENTDESCRIPTION"),
    EquipmentFuelType("EQUIPMENTFUELTYPE"),
    EquipmentModels("EQUIPMENTMODELS"),
    FailureLocations("FAILURELOCATIONS"),
    FaultFound("FAULTFOUND"),
    FleetClaimAcceptanceReason("FLEETCLAIMACCEPTANCEREASON"),
    FleetRecommendation("FLEETRECOMMENDATION"),
    FleetClaimRejectionReason("FLEETCLAIMREJECTIONREASON"),
    MiscellaneousCosts("MISCELLANEOUSCOSTS"),
    QuoteApprovalReason("QUOTEAPPROVALREASON"),
    QuoteDenyingReason("QUOTEDENYINGREASON"),
    QuoteReplacementRecommendation("QUOTEREPLACEMENTRECOMMENDATION"),
    ReviewResponsibility("REVIEWRESPONSIBILITY"),
    CustomerRejectionReason("CUSTOMERREJECTIONREASON")
    ;

    private String type;

    private ListOfValuesType(String type) {
        this.type = type;
    }

    public static ListOfValuesType typeFor(String type) {
        if (SmrReason.type.equals(type)) {
            return SmrReason;
        } else if (AcceptanceReason.type.equals(type)) {
            return AcceptanceReason;
        } else if (AcceptanceReasonForCP.type.equals(type)) {
            return AcceptanceReasonForCP;
        } else if (AccountabilityCode.type.equals(type)) {
            return AccountabilityCode;
        } else if (FailureReason.type.equals(type)) {
            return FailureReason;
        } else if (PartAcceptanceReason.type.equals(type)) {
            return PartAcceptanceReason;
        } else if (RecoveryClaimAcceptanceReason.type.equals(type)) {
            return RecoveryClaimAcceptanceReason;
        } else if (RecoveryClaimRejectionReason.type.equals(type)) {
            return RecoveryClaimRejectionReason;
        } else if (RejectionReason.type.equals(type)) {
            return RejectionReason;
        } else if (ManufacturingSiteInventory.type.equals(type)) {
            return ManufacturingSiteInventory;
        } else if (CampaignClass.type.equals(type)) {
            return CampaignClass;
        } else if (SellingEntity.type.equals(type)) {
            return SellingEntity;
        } else if (ReportType.type.equals(type)) {
            return ReportType;
        } else if (Oem.type.equals(type)) {
            return Oem;
        } else if (ClaimCompetitorModel.type.equals(type)) {
            return ClaimCompetitorModel;
        } else if (FieldModificationInventoryStatus.type.equals(type)) {
            return FieldModificationInventoryStatus;
        } else if (DocumentType.type.equals(type)) {
            return DocumentType;
        } else if (UnitDocumentType.type.equals(type)) {
            return UnitDocumentType;
        } else if (RecoveryClaimDocumentType.type.equals(type)) {
            return RecoveryClaimDocumentType;
        } else if (Suppliers.type.equals(type)) {
            return Suppliers;
        } else if (RecoveryClaimCannotRecoverReason.type.equals(type)) {
            return RecoveryClaimCannotRecoverReason;
        } else if (AdditionalComponentsType.type.equals(type)) {
            return AdditionalComponentsType;
        } else if (AdditionalComponentsSubType.type.equals(type)) {
            return AdditionalComponentsSubType;
        } else if (LaborRateType.type.equals(type)) {
            return LaborRateType;
        } else if (ReviewResposibility.type.equals(type)) {
            return ReviewResposibility;
        }else if(PutOnHoldReason.type.equals(type)) {
        	return PutOnHoldReason;
        }else if(RequestInfoFromUser.type.equals(type)) {
        	return RequestInfoFromUser;
        }else if(DiscountType.type.equals(type)) {
        	return DiscountType;
            // Fleet LoVs
        } else if (CallType.type.equals(type)) {
            return CallType;
        } else if (CausedByCodes.type.equals(type)) {
            return CausedByCodes;
        } else if (ClaimAcceptanceReason.type.equals(type)) {
            return ClaimAcceptanceReason;
        } else if (ClaimRejectionReason.type.equals(type)) {
            return ClaimRejectionReason;
        } else if (ClaimReview.type.equals(type)) {
            return ClaimReview;
        } else if (ComponentCodes.type.equals(type)) {
            return ComponentCodes;
        } else if (DealerFailure.type.equals(type)) {
            return DealerFailure;
        } else if (EquipmentCondition.type.equals(type)) {
            return EquipmentCondition;
        } else if (EquipmentDescription.type.equals(type)) {
            return EquipmentDescription;
        } else if (EquipmentFuelType.type.equals(type)) {
            return EquipmentFuelType;
        } else if (EquipmentModels.type.equals(type)) {
            return EquipmentModels;
        } else if (FailureLocations.type.equals(type)) {
            return FailureLocations;
        } else if (FaultFound.type.equals(type)) {
            return FaultFound;
        } else if (FleetRecommendation.type.equals(type)) {
            return FleetRecommendation;
        } else if (MiscellaneousCosts.type.equals(type)) {
            return MiscellaneousCosts;
        } else if (QuoteApprovalReason.type.equals(type)) {
            return QuoteApprovalReason;
        } else if (QuoteDenyingReason.type.equals(type)) {
            return QuoteDenyingReason;
        } else if (QuoteReplacementRecommendation.type.equals(type)) {
            return QuoteReplacementRecommendation;
        } else if (ReviewResponsibility.type.equals(type)) {
            return ReviewResponsibility;
        }else if(FleetClaimRejectionReason.type.equals(type)){
        	return FleetClaimRejectionReason;
        }else if(FleetClaimAcceptanceReason.type.equals(type)){ 
        	return FleetClaimAcceptanceReason;
        }else if(CustomerRejectionReason.type.equals(type)){ 
        	return CustomerRejectionReason;
        }else {
            throw new IllegalArgumentException("Cannot understand the List Of Value Type");
        }
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}

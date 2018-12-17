package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;
/**
 * The method returns the error codes and error messages.
 * @author TWMSUSER
 */
public class DealerInterfaceErrorConstants {
    public final static String SUCESS = "message.common.success";

    public final static String FAILURE = "dealerAPI.failure";
    
    public final static String STATUS_FAILURE = "FAILURE";

    public final static String DAPI01 = "DAPI01";

    public final static String DAPI02 = "DAPI02";

    public final static String DAPI03 = "DAPI03";
    
    public final static String DAPI04 = "DAPI04";
    
    public final static String DAPI05 = "DAPI05";
    
    public final static String DAPI06 = "DAPI06";
    
    public final static String DAPI07 = "DAPI07";
    
    public final static String DAPI08 = "DAPI08";
    
    public final static String USH01 = "USH01";
    
    public final static String USH02 = "USH02";
	
    public final static String DAI999 = "DAI999";
    
 
    
    public final static String	CSA91 = "CSA91";
    public final static String CSA115 = "CSA115";
    public final static String	CSA77 = "CSA77";
    public final static String	CSA119 = "CSA119";
    public final static String	CSA76 = "CSA76";
    public final static String	CSA12 = "CSA12";
    public final static String	CSA03 = "CSA03";
    public final static String	CSA65 = "CSA65";
    public final static String	CSA27 = "CSA27";
    public final static String	CSA94 = "CSA94";
    public final static String	CSA98 = "CSA98";
    public final static String	CSA109 = "CSA109";
    public final static String	CSA123 = "CSA123";
    public final static String	CSA133 = "CSA133";
    
    
//New Error codes for Claim Submssion
    
    public final static String	CSA001 = "CSA001";
    
    public final static String	CSA002 = "CSA002";
    
    public final static String	CSA003 = "CSA003";
    
    public static final String CSA004 = "CSA004";
    
    public static final String CSA005 = "CSA005";
    
    public static final String CSA006 = "CSA006";
    
    public static final String CSA007 = "CSA007";
    
    public static final String CSA008 = "CSA008";
    
    public static final String CSA009 = "CSA009";
    
    public static final String CSA010 = "CSA010";
    
    public static final String CSA011 = "CSA011";
    
    public static final String CSA012 = "CSA012";
    
    public static final String CSA013 = "CSA013";
    
    public static final String CSA014 = "CSA014";
    
    public static final String CSA015 = "CSA015";
    
    public static final String CSA016 = "CSA016";
    
    public static final String CSA017 = "CSA017";
    
    public static final String CSA018 = "CSA018";
    
    public static final String CSA019 = "CSA019";
    
    public static final String CSA020 = "CSA020";
    
    public static final String CSA021 = "CSA021";
    
    public static final String CSA022 = "CSA022";
    
    public static final String CSA023 = "CSA023";
    
    public static final String CSA024 = "CSA024";
    
    public static final String CSA025 = "CSA025";
    
    public static final String CSA026 = "CSA026";
    
    public static final String CSA027 = "CSA027";
    
    public static final String CSA028 = "CSA028";
    
    public static final String CSA029 = "CSA029";
    
    public static final String CSA030 = "CSA030";
    
    public static final String CSA031 = "CSA031";
    
    public static final String CSA032 = "CSA032";
    
    public static final String CSA033 = "CSA033";
    
    public static final String CSA034 = "CSA034";
    
    public static final String CSA035 = "CSA035";
    
    public static final String CSA036 = "CSA036";
    
    public static final String CSA037 = "CSA037";
    
    public static final String CSA038 = "CSA038";
    
    public static final String CSA039 = "CSA039";
    
    public static final String CSA040 = "CSA040";
    
    public static final String CSA041 = "CSA041";
    
    public static final String CSA042 = "CSA042";
    
    public static final String CSA043 = "CSA043";
    
    public static final String CSA044 = "CSA044";
    
    public static final String CSA045 = "CSA045";
    
    public static final String CSA046 = "CSA046";
    
    public static final String CSA047 = "CSA047";
    
    public static final String CSA048 = "CSA048";
    
    public static final String CSA049 = "CSA049";
    
    public static final String CSA050 = "CSA050";
    
    public static final String CSA051 = "CSA051";
    
    public static final String CSA052 = "CSA052";
    
    public static final String CSA053 = "CSA053";
    
    public static final String CSA054 = "CSA054";
    
    public static final String CSA055 = "CSA055";
    
    public static final String CSA056 = "CSA056";
    
    public static final String CSA057 = "CSA057";
    
    public static final String CSA058 = "CSA058";
    
    public static final String CSA059 = "CSA059";
    
    public static final String CSA060 = "CSA060";
    
    public static final String CSA061 = "CSA061";
    
    public static final String CSA062 = "CSA062";
    
    public static final String CSA063 = "CSA063";
    
    public static final String CSA064 = "CSA064";
    
    public static final String CSA065 = "CSA065";
    
    public static final String CSA066 = "CSA066";
    
    public static final String CSA067 = "CSA067";
    
    public static final String CSA068 = "CSA068";
    
    public static final String CSA069 = "CSA069";
    
	public static final String CSA070 = "CSA070";
    
    public static final String CSA071 = "CSA071";
    
    public static final String CSA072 = "CSA072";
    
    public static final String CSA073 = "CSA073";
    
    public static final String CSA074 = "CSA074";
    
    public static final String CSA075 = "CSA075";
    
    public static final String CSA076 = "CSA076";
    
    public static final String CSA077 = "CSA077";
    
    public static final String CSA078 = "CSA078";
    
    public static final String CSA079 = "CSA079";
    
    public static final String CSA080 = "CSA080";
    
    public static final String CSA081 = "CSA081";
    
    public static final String CSA082 = "CSA082";
    
    public static final String CSA083 = "CSA083";
    
    public static final String CSA084 = "CSA084";
    
    public static final String CSA085 = "CSA085";
    
    public static final String CSA086 = "CSA086";
    
    public static final String CSA087 = "CSA087";
    
    public static final String CSA088 = "CSA088";
    
    public static final String CSA089 = "CSA089";
    
    public static final String CSA090 = "CSA090";
    
    public static final String CSA091 = "CSA091";
    
    public static final String CSA092 = "CSA092";
    
    public static final String CSA093 = "CSA093";
    
    public static final String CSA094 = "CSA094";
    
    public static final String CSA095 = "CSA095";
    
    public static final String CSA096 = "CSA096";
    
    public static final String CSA097 = "CSA097";
    
    public static final String CSA098 = "CSA098";
    
    public static final String CSA099 = "CSA099";
    
    public static final String CSA0100 = "CSA0100";
    
    public static final String CSA0101 = "CSA0101";
    
    public static final String CSA0102 = "CSA0102";
    
    public static final String CSA0103 = "CSA0103";
    
    public static final String CSA0104 = "CSA0104";
    
    public static final String CSA0105 = "CSA0105"; 
    
    public static final String CSA0122 = "CSA0122";
    
    public static final String CSA0123 = "CSA0123";
    
    public static final String CSA0124 = "CSA0124";
    
    public static final String CSA0125 = "CSA0125";
    
    public static final String CSA0126 = "CSA0126";
    
    public static final String CSA0127 = "CSA0127";
    
    public static final String CSA0128 = "CSA0128";
    
    public static final String CSA0129 = "CSA0129";
    
    public static final String CSA0130 = "CSA0130";
    
    public static final String CSA0131 = "CSA0131";
    
    public static final String CSA0132 = "CSA0132";
    
    public static final String CSA0133 = "CSA0133";
    
    public static final String CSA0134 = "CSA0134";
    
    public static final String CSA0135 = "CSA0135";
    
    public static final String CSA0136 = "CSA0136";
    
    public static final String CSA0137 = "CSA0137";
    
    public static final String CSA0138 = "CSA0138";
    
    public static final String CSA0139 = "CSA0139";
    
    public static final String CSA0140 = "CSA0140";
    
    public static final String CSA0141 = "CSA0141";
    
    public static final String CSA0142 = "CSA0142";
    
    public static final String CSA0143 = "CSA0143";
    
    public static final String CSA0144 = "CSA0144";
    
    public static final String CSA0145 = "CSA0145";
    
    public static final String CSA0146 = "CSA0146";
    
    public static final String CSA0147 = "CSA0147";
    
    public static final String CSA0148 = "CSA0148";
    
    public static final String CSA0149 = "CSA0149";
    
    public static final String CSA0150 = "CSA0150";
    
    public static final String CSA0151 = "CSA0151";
    
    public static final String CSA0152 = "CSA0152";
    
    public static final String CSA0153 = "CSA0153";
    
	
    private final static Map<String, String> errorCodeMap = new HashMap<String, String>();

	

 
    private I18nDomainTextReader i18nDomainTextReader;
    
    static {
    	
    	/***
    	 * Error codes for Claim Submssion
    	 */
    	 errorCodeMap.put(CSA001, "claim.submission.Bu.is.required");
    	 errorCodeMap.put(CSA002, "claim.submission.dealerNumber.is.required");
    	 errorCodeMap.put("CSA003" , "error.newClaim.repairStartDateRequired");
    	 errorCodeMap.put("CSA004" , "error.newClaim.repairDateRequired");
    	 errorCodeMap.put("CSA005" , "error.newClaim.claim.type.required");
    	 errorCodeMap.put("CSA006" , "error.newClaim.repairStartDateIsAfterEndDate");
    	 errorCodeMap.put("CSA007" , "dealerAPI.claimSubmission.invalidPartsClaim.InvalidPartNumber");
    	 errorCodeMap.put("CSA008" , "error.newClaim.invalidInventoryItem.item");
    	 errorCodeMap.put("CSA009" , "error.newClaim.noItemGroupExists");
    	 errorCodeMap.put("CSA010" , "error.newClaim.noPartExists");
    	 errorCodeMap.put("CSA011" , "error.newClaim.competitor.model.serila.number.required");
    	 errorCodeMap.put("CSA012" , "error.newClaim.competitor.model.brand.required");
    	 errorCodeMap.put("CSA013" , "dealerAPI.claimSubmission.invalidInventoryItemNumber");
    	 errorCodeMap.put("CSA014" , "dealerAPI.claimSubmission.user.inventory.model.invalid");
    	 errorCodeMap.put("CSA015" , "dealerAPI.claimSubmission.invalidMachineClaimFields");
    	 errorCodeMap.put("CSA016" , "dealerAPI.claimSubmission.user.inventory.does.not.exist");
    	 errorCodeMap.put("CSA017" , "error.newClaim.dealer.not.allowed.file.claim");
    	 errorCodeMap.put("CSA018" , "error.newClaim.claimSubmission.fieldModificationClaim.invalidCampaignCode"); 
    	 errorCodeMap.put("CSA019" , "error.newClaim.claimSubmission.fieldModificationClaim.campaignCodeRequired");  
    	 errorCodeMap.put("CSA020" , "error.newClaim.claimSubmission.fieldModificationClaim.invalidSerialNumber" );
    	 errorCodeMap.put("CSA021" , "error.newClaim.claimSubmission.fieldModificationClaim.campaignCodeNotApplicableOnInventory");
    	 errorCodeMap.put("CSA022" , "error.newClaim.claimSubmission.fieldModificationClaim.campaignClaimAlreadyFiledOnInventory");
    	 errorCodeMap.put("CSA023" , "error.newClaim.serialNoRequired");
    	 errorCodeMap.put("CSA024", "error.newClaim.failureDateRequired");
    	 errorCodeMap.put("CSA025" , "error.newClaim.claimSubmission.invalidFailureDate");
    	 errorCodeMap.put("CSA026" , "error.newClaim.serviceHrsRequired");
    	 errorCodeMap.put("CSA027" , "error.newClaim.invalidFailureDate");
    	 errorCodeMap.put("CSA028" , "error.newClaim.invalidStartdate");
    	 errorCodeMap.put("CSA029" , "error.newClaim.invalienddate");
    	 errorCodeMap.put("CSA030" , "error.newClaim.invalidRepairDateForCampaign");
    	 errorCodeMap.put("CSA031" , "error.managePolicy.hoursInServiceRequired");
    	 errorCodeMap.put("CSA032" , "error.ncrClaim.unAuthorizedDealer");
    	 errorCodeMap.put("CSA033" , "error.newClaim.ncrClaimWith30Days");
    	 errorCodeMap.put("CSA034" , "error.newClaim.ncrClaim.daysCheck");
    	 errorCodeMap.put("CSA035" , "error.newClaim.smrClaim.is.not.allowed.batchclaim");
    	 errorCodeMap.put("CSA036" , "error.newClaim.smrReasonRequired");
    	 errorCodeMap.put("CSA037" , "error.newClaim.smrReason.not.valid");
    	 errorCodeMap.put("CSA038" , "error.newClaim.purchasedateRequired");
    	 errorCodeMap.put("CSA039" , "error.newClaim.invalidPurchaseDuration");
    	 errorCodeMap.put("CSA040" , "error.newClaim.dateCodeRequired");
    	 errorCodeMap.put("CSA041" , "error.newClaim.casual.partNumberRequired");
    	 errorCodeMap.put("CSA042" , "error.newClaim.brand.Required");
    	 errorCodeMap.put("CSA043" , "error.newClaim.warrantyStartDateRequired");
    	 errorCodeMap.put("CSA044" , "error.newClaim.invalidInstallationDuration");
    	 errorCodeMap.put("CSA045" , "error.newClaim.serviceHrsGreater");
    	 errorCodeMap.put("CSA046" , "error.newClaim.failureDateBeforeDeliveryDate");
    	 errorCodeMap.put("CSA047" , "error.newClaim.partNumberRequired");
    	 errorCodeMap.put("CSA048" , "error.newClaim.information.required");
    	 errorCodeMap.put("CSA049" , "error.newClaim.workorderNoRequired");
    	 errorCodeMap.put("CSA050" , "error.newClaim.serviceLocationNotUnique");
    	 errorCodeMap.put("CSA051" , "error.newClaim.invalidServicingLocation");
    	 errorCodeMap.put("CSA052" , "error.newClaim.servicingLocationRequired");
    	 errorCodeMap.put("CSA053" , "error.newClaim.causalPartNoRequired");
    	 errorCodeMap.put("CSA054" , "error.newClaim.invalid.Causal.Part.number");
    	 errorCodeMap.put("CSA055" , "error.integration.partsClaim.standardWarrantyPeriodExists");
    	 errorCodeMap.put("CSA056" , "error.newClaim.invalidCausalPart"); 
    	 errorCodeMap.put("CSA057" , "error.newClaim.part.number.casualpart.number.is.diffrent");
    	 errorCodeMap.put("CSA058" , "error.newClaim.conditionFoundRequired");
    	 errorCodeMap.put("CSA059" , "error.newClaim.workPerformedRequired");
    	 errorCodeMap.put("CSA060" , "error.newClaim.faultloaction.required");
    	 errorCodeMap.put("CSA061" , "error.newClaim.faultFoundRequired");
    	 errorCodeMap.put("CSA062" , "error.newClaim.faultCode.not.valid");
    	 errorCodeMap.put("CSA063" , "error.newClaim.faultFound.not.valid");
    	 errorCodeMap.put("CSA064" , "error.newClaim.rootCauseRequired");
    	 errorCodeMap.put("CSA065" , "error.newClaim.CausedBy.value.Required");
    	 errorCodeMap.put("CSA066" , "error.newClaim.CausedBy.value.invalid");
    	 errorCodeMap.put("CSA067" , "error.newClaim.customerinformation.required");
    	 errorCodeMap.put("CSA068" , "error.newClaim.customerinformation.customer.name.required");
    	 errorCodeMap.put("CSA069" , "error.newClaim.customerinformation.customer.address.required");
    	 errorCodeMap.put("CSA070" , "error.newClaim.customerinformation.customer.city.required");
    	 errorCodeMap.put("CSA071" , "error.newClaim.customerinformation.customer.state.required");
    	 errorCodeMap.put("CSA072" , "error.newClaim.customerinformation.customer.zip.required");
    	 errorCodeMap.put("CSA073" , "error.newClaim.faultfound.not.required");
    	 errorCodeMap.put("CSA074" , "error.newClaim.casuedby.not.required");
    	 errorCodeMap.put("CSA075" , "error.newClaim.multiple.job.code.are.not.allowed");
    	 errorCodeMap.put("CSA076" , "error.newClaim.failure.structure.required");
    	 errorCodeMap.put("CSA077" , "error.newClaim.invalid.job.code");
    	 errorCodeMap.put("CSA078" , "error.newClaim.AdditionHours.not.allowed");
    	 errorCodeMap.put("CSA079", "error.newClaim.invalidJobCodes");
    	 errorCodeMap.put("CSA080" , "error.claim.laborHours");
    	 errorCodeMap.put("CSA081" , "error.claim.replacedPartNumberInvalid");
    	 errorCodeMap.put("CSA082" , "error.claim.replacedQuantityNull");
    	 errorCodeMap.put("CSA083" , "error.claim.replacedQuantityInvalid");
    	 errorCodeMap.put("CSA084" , "error.claim.installedPartNumberInvalid");
    	 errorCodeMap.put("CSA085" , "error.claim.oemPartReplacedSerialized");
    	 errorCodeMap.put("CSA086" , "error.claim.oemPartReplacedInstalledNonSerialized");
    	 errorCodeMap.put("CSA087" , "error.claim.oemPartReplacedInstalledNull");
    	 errorCodeMap.put("CSA088" , "error.claim.oemPartReplacedInstalledInstallDate");
    	 errorCodeMap.put("CSA089" , "error.claim.replacedOnOtherClaim");
    	 errorCodeMap.put("CSA090" , "error.claim.replacedOnNotOnInventory");
    	 errorCodeMap.put("CSA091" , "invalid.fieldvalue.claim.serviceInformation.serviceDetail.miscPartsReplaced.numberOfUnits.miscItemConfig");
    	 errorCodeMap.put("CSA092" , "error.newClaim.invalidRentalCharges");
    	 errorCodeMap.put("CSA093" , "error.newClaim.invalidTolls");
    	 errorCodeMap.put("CSA094" , "error.newClaim.invalidLocalPurchase");
    	 errorCodeMap.put("CSA095" , "error.newClaim.invalidParkingCost");
    	 errorCodeMap.put("CSA096" , "error.newClaim.invalidPerDiem");
    	 errorCodeMap.put("CSA097" , "error.newClaim.invalidItemFieghtDuty");
    	 errorCodeMap.put("CSA098" , "error.newClaim.invalidMealsHours");
    	 errorCodeMap.put("CSA099" , "error.newClaim.invalidOtherExpense");
    	 errorCodeMap.put("CSA0100" , "error.newClaim.invalidOtherFreightDutyExpense");
    	 errorCodeMap.put("CSA0101" , "error.newClaim.technicianNotUnique");
    	 errorCodeMap.put("CSA0102" , "error.newClaim.alarmCode");
    	 errorCodeMap.put("CSA0103" , "error.newClaim.alarmcode.invalid");
    	 errorCodeMap.put("CSA0104" , "alarmcode.duplicate");
    	 errorCodeMap.put("CSA0105" , "error.claim.serviceProcedure.duplicateJobCode");
    	 errorCodeMap.put("CSA0106" , "error.newClaim.negativeLaborHours");
    	 errorCodeMap.put("CSA0107" , "error.newClaim.negativeAdditionHours");
    	 errorCodeMap.put("CSA0108" , "error.newClaim.reasonForAdditionHours");
    	 errorCodeMap.put("CSA0109" , "error.claim.thirdPartyLaborRateNull");
    	 errorCodeMap.put("CSA0110" , "error.claim.invalidThirdPartyLaborRate");
    	 errorCodeMap.put("CSA0111" , "error.additionalAttributes.Mandatory");
    	 errorCodeMap.put("CSA0112" , "error.additionalAttributes.inventory.invalidValue");
    	 errorCodeMap.put("CSA0113" , "error.additionalAttributes.causalPart.invalidValue");
    	 errorCodeMap.put("CSA0114" , "error.additionalAttributes.faultCode.invalidValue");
    	 errorCodeMap.put("CSA0115" , "error.additionalAttributes.jobCode.invalidValue");
    	 errorCodeMap.put("CSA0116" , "error.additionalAttributes.invalidValue");
    	 errorCodeMap.put("CSA0117" , "error.newClaim.invalidOemParts");
    	 errorCodeMap.put("CSA0118" , "error.claim.duplicateOEMPartsInstalled");
    	 errorCodeMap.put("CSA0119" , "error.claim.duplicateOEMPartsReplaced");
    	 errorCodeMap.put("CSA0120" , "error.newClaim.invalidMiscPart");
    	 errorCodeMap.put("CSA0121" , "error.newclaim.label.common.epo.system.down");
    	 errorCodeMap.put("CSA0122" , "error.newclaim.failure.structure.does.not.exist");
    	 errorCodeMap.put("CSA0123" , "error.claim.replacedPartNumberNull");
    	 errorCodeMap.put("CSA0124" , "error.claim.installed.part.null");
    	 errorCodeMap.put("CSA0125" , "error.claim.Undefined.error");
    	 errorCodeMap.put("CSA0128" , "error.claim.machine.claim.feild.values.required");
    	 errorCodeMap.put("CSA0129" , "error.claim.non.serilized.not.allowed");
    	 errorCodeMap.put("CSA0130" , "error.claim.misc.parts.not.allowed");
    	 errorCodeMap.put("CSA0131" , "error.claim.logged.in.user.is.notdealer");
    	 errorCodeMap.put("CSA0132" , "error.newClaim.invalidNonOEMDescription");
    	 errorCodeMap.put("CSA0133" , "error.newClaim.invalidLabor");
    	 errorCodeMap.put("CSA0134" , "error.newClaim.invaliddealerNumber");
    	 errorCodeMap.put("CSA0135" , "error.newClaim.end.customer.information.required");
    	 errorCodeMap.put("CSA0136" , "error.newClaim.travel.distance.does.not.match");
    	 errorCodeMap.put("CSA0137" , "error.newClaim.travel.hours.does.not.match");
    	 errorCodeMap.put("CSA0138" , "error.newClaim.google.maps.error.while.calculating.travel");
    	 errorCodeMap.put("CSA0139" , "error.newClaim.additionalHoursReasonRequired");
    	 errorCodeMap.put("CSA0140" , "error.newClaim.techncian.id.required");
    	 errorCodeMap.put("CSA0141" , "error.newClaim.invalidTransportation");
    	 errorCodeMap.put("CSA0142" , "error.newClaim.invalidHandlingFee");
    	 errorCodeMap.put("CSA0143" , "error.claim.mktgGroupCodeLookupNoResult");
    	 errorCodeMap.put("CSA0144" , "error.claim.marketingGroupCodeLookup.integration");
    	 errorCodeMap.put("CSA0145" , "error.claim.google.map.doesnot.return.distance");
    	 errorCodeMap.put("CSA0146" , "error.claim.force.to.draft.set.to.true");
    	 errorCodeMap.put("CSA0147" , "error.claim.logged.in.user.not.dealer");
    	 errorCodeMap.put("CSA0148", "error.newClaim.failureDate.invalid");
    	 errorCodeMap.put("CSA0149", "error.newClaim.reapirenddate.invalid");
    	 errorCodeMap.put("CSA0150", "error.newClaim.reapirsatrtdate.invalid");
    	 errorCodeMap.put("CSA0151", "error.newClaim.misc.part.quantity.invalid");
    	 errorCodeMap.put("CSA0151", "error.newClaim.non.oem.part.quantity.invalid");
    	 errorCodeMap.put("CSA0153", "error.fleet.claim.not.allowed");
    	
        /**
         * General Error Codes
         */
        errorCodeMap.put(DAPI01, "dealerAPI.invalidRequest");
        errorCodeMap.put(DAPI02, "dealerAPI.errorOccured");
        errorCodeMap.put(DAPI03, "dealerAPI.userNotAuthenticated");
        errorCodeMap.put(DAPI04, "dealerAPI.currentlyActiveOrganizationRequired");
        errorCodeMap.put(DAPI05, "dealerAPI.requestHeaderMissing");
        errorCodeMap.put(DAPI06, "dealerAPI.badCredentials");
        errorCodeMap.put(DAPI07, "dealerAPI.requestUsernameMissing");
        errorCodeMap.put(DAPI08, "dealerAPI.requestPasswordMissing");
       
        
        
        /**
         * Unit Service History related
         */
        errorCodeMap.put(USH01, "dealerAPI.unitservicehistory.inventoryItemNotFound");        
        errorCodeMap.put(USH02, "dealerAPI.unitservicehistory.userNotAuthorized");
        
        
       
        errorCodeMap.put("CSA03" , "error.newClaim.invalidRepairDate");
        errorCodeMap.put("CSA12" , "error.newClaim.invalidBaseModelEmpty");
        errorCodeMap.put("CSA13" , "error.newClaim.selectDealer");
        errorCodeMap.put("CSA15" , "message.error.invoiceNumberMandatory");
        errorCodeMap.put("CSA17" , "error.newClaim.invalidItemNumberEmpty");
        errorCodeMap.put("CSA19" , "error.newClaim.serviceHrsInvalid");
        errorCodeMap.put("CSA20" , "error.newClaim.itemRequired");
        errorCodeMap.put("CSA21" , "error.newClaim.partSerialNumberRequired");
        errorCodeMap.put("CSA25" , "error.newClaim.invalidCampaignCode");
        errorCodeMap.put("CSA27" , "error.newClaim.causalPartNoRequired");
        errorCodeMap.put("CSA28" , "error.newClaim.sellingEntityRequired");
        errorCodeMap.put("CSA36" , "error.partReturnConfiguration.returnLocationRequired");
        errorCodeMap.put("CSA37" , "error.partReturnConfiguration.paymentConditionRequired");
        errorCodeMap.put("CSA38" , "error.partReturnConfiguration.dueDays");
        errorCodeMap.put("CSA39" , "error.claim.selectAtleastOneOfInstallParts");
        errorCodeMap.put("CSA40" , "error.claim.installedHussmannPartNumberNull");
        errorCodeMap.put("CSA41" , "error.claim.hussmannInstalledQuantityNull");
        errorCodeMap.put("CSA42" , "error.claim.hussmannInstalledQuantityInvalid");
        errorCodeMap.put("CSA43" , "error.claim.installedNonHussmannPartNumberNull");
        errorCodeMap.put("CSA44" , "error.claim.nonHussmannInstalledQuantityNull");
        errorCodeMap.put("CSA45" , "error.claim.nonHussmannInstalledQuantityInvalid");
        errorCodeMap.put("CSA46" , "error.claim.priceNull");
        errorCodeMap.put("CSA49" , "message.claim.selectAccountabilityCode");
        errorCodeMap.put("CSA50" , "error.claim.supplierAccountability");
        errorCodeMap.put("CSA51" , "error.claim.causalPartRecovery");
        errorCodeMap.put("CSA52" , "error.newClaim.jobCodes.invalidError");
        errorCodeMap.put("CSA62" , "error.additionalAttributes.part.invalidValue");
        errorCodeMap.put("CSA63" , "message.scrap.machineScrapped");      
        errorCodeMap.put("CSA65" , "dealerAPI.claimSubmission.invalidTechnician");
        errorCodeMap.put("CSA76" , "dealerAPI.claimSubmission.invalidPartsClaim.InvalidPartSerialNumber");
        errorCodeMap.put("CSA77" , "dealerAPI.claimSubmission.invalidPartsClaimFields");
        errorCodeMap.put("CSA94" , "dealerAPI.claimSubmission.invalidOwnerInfo");
        errorCodeMap.put("CSA98" , "invalid.fieldvalue.claim.hoursOnPart");
        errorCodeMap.put("CSA102" , "error.claim.oemPartReplacedInstalledQty");
        errorCodeMap.put("CSA105" , "error.claim.installedPartOnOtherClaim");
        errorCodeMap.put("CSA106" , "error.claim.installedPartOnAnotherUnit");
        errorCodeMap.put("CSA107" , "error.claim.selectAtleastOneOfReplacedParts");
        errorCodeMap.put("CSA109" , "dealerAPI.claimSubmission.invalidHoursInService");
        errorCodeMap.put("CSA110" , "dealerAPI.claim.draftForFailureReport"); 
        
        
       
        errorCodeMap.put("CSA119" , "dealerAPI.claimSubmission.invalidUnit");
       
        
        errorCodeMap.put("CSA123" , "dealerAPI.claimSubmission.user.org.null");
        errorCodeMap.put("CSA133" , "error.newClaim.servicing.location.required");
        

        errorCodeMap.put("error.majorComponent.enterUnitOrCustomer", "MCR111");
        errorCodeMap.put("error.majorComponent.invalidInstaller", "MCR112");  
        errorCodeMap.put("error.majorComponent.PartNumberInvalidForMC", "MCR113");
        errorCodeMap.put("error.majorComponent.majorCompWithSNoAndPartNumberCombinationExits", "MCR114");   
        errorCodeMap.put("error.majorComponent.deliveryDateCannotBeInFuture", "MCR115");   
        errorCodeMap.put("error.majorComponent.invalidAddBookTypeForInst", "MCR116");   
        errorCodeMap.put("error.majorComponent.invalidNonCertInst", "MCR117");   
        errorCodeMap.put("error.majorComponent.invalidCertifiedInstaller", "MCR118");   
        errorCodeMap.put("error.majorComponent.enterCertOrNonCertInst", "MCR119");  
        errorCodeMap.put("error.majorComponent.invalidAddBookTypeForCust", "MCR120");   
        errorCodeMap.put("error.majorComponent.invalidCustomer", "MCR121");   
        errorCodeMap.put("error.majorComponent.invalidUnitSerialNumber", "MCR122");

        /**
         * WarrantyRegistration Error codes
         */
        
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidItemSerialNoAndItemNo", "WRE01");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidDealerNumber", "WRE02");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidInstallingDealer", "WRE03");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidOEMDescription", "WRE04");       									   
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidCustomerId", "WRE05");
        errorCodeMap.put("dealerAPI.warrantyRegistration.futureDeliveryDate", "WRE06");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidHoursInService", "WRE07");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.partItemNoInvalid", "WRE08");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidOperatorId", "WRE09");
        errorCodeMap.put("dealerAPI.warrantyRegistration.customertypeNotAllowed", "WRE10");
        errorCodeMap.put("dealerAPI.warrantyRegistration.operatortypeNotAllowed", "WRE11");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.inventoryAlreadyFilledOnWarranty", "WRE12");
        errorCodeMap.put("dealerAPI.warrantyRegistration.inventoryNotInDealersStock", "WRE13");
        errorCodeMap.put("dealerAPI.warrantyRegistration.inventoryOnPendingWarranty", "WRE14");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateCannotBeInFuture", "WRE15");
        errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateBeforeShipment", "WRE16");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateInvalid", "WRE17");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateBeforeInstallation", "WRE18");
        errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateCannotBeInFuture", "WRE19");
        errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateBeforeShipment", "WRE20");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateInvalid", "WRE21");
        errorCodeMap.put("dealerAPI.warrantyRegistration.scrap.machineScrapped", "WRE22");       
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidTransactionType", "WRE28");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidMarketType", "WRE29");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidCompetitorMake", "WRE30");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidCompetitionType", "WRE31");        
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidModelNo", "WRE32");
        errorCodeMap.put("dealerAPI.warrantyRegistration.errorInSavingWarrantyDraft", "WRE33");
        errorCodeMap.put("dealerAPI.warrantyRegistration.partItemNoAlreadyRetailed", "WRE34");
        errorCodeMap.put("dealerAPI.warrantyRegistration.common.duplicatePart", "WRE35");
        errorCodeMap.put("dealerAPI.warrantyRegistration.partInstallationDateCannotBeInFuture", "WRE36");
        errorCodeMap.put("dealerAPI.warrantyRegistration.partNumberMandatory", "WRE37");
        errorCodeMap.put("dealerAPI.warrantyRegistration.partSerialNumberMandatory", "WRE38");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidPart", "WRE39");
        errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateCannotBeforeBuildDate", "WRE40");      
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidProductMarketingAttributeInfo", "WRE42");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidProductMarketingAttributeValue", "WRE43");
        errorCodeMap.put("dealerAPI.warrantyRegistration.applicablePoliciesNotAvialable", "WRE44");
        errorCodeMap.put("dealerAPI.warrantyRegistration.invalidIfPreviousOwner", "WRE45");
        errorCodeMap.put("dealerAPI.warrantyRegistration.noOfMonthsRequired", "WRE46");
        errorCodeMap.put("dealerAPI.warrantyRegistration.noOfYearsRequired", "WRE47");  
        
    }
    
    public String getErrorMessage(final String errorCode) {
        String messageKey = errorCodeMap.get(errorCode);
        return getPropertyMessage(messageKey);
    }

	public String getErrorCodeFromKey(final String errorMessageKey) {
		for (Map.Entry<String, String> entry : errorCodeMap.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(errorMessageKey)) {
				return entry.getKey();
			}
		}
		return null;
	}
    public String getErrorCode(final String errorMessageKey) {

    	String errorCode = null;
    
    	if (errorMessageKey.startsWith("dealerAPI.warrantyRegistration")) {
    		errorCode = errorCodeMap.get(errorMessageKey);	
    		
    	} else {    	
    		errorCode = errorCodeMap.get(errorMessageKey);    		
    	}
    	
    	if(StringUtils.hasText(errorCode)) {
    		errorCode = DAI999;
		}
    	return errorCode;
    }
    
    public String getPropertyMessage(final String messageKey) {
        return i18nDomainTextReader.getProperty(messageKey);
    }
    
    public String getPropertyMessageFromErrorCode(final String errorCode) {
		if(StringUtils.hasText(errorCode)){
			return i18nDomainTextReader.getProperty(errorCodeMap.get(errorCode).trim());
		}
		return null;
	}
    
    public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
        this.i18nDomainTextReader = i18nDomainTextReader;
    }
    
    public String getPropertyMessage(final String errorMessageKey, String[] args) {
    	return i18nDomainTextReader.getText(errorCodeMap.get(errorMessageKey), args);
    }
    public String getPropertyMessageValue(final String errorMessageKey, String[] args) {
    	return i18nDomainTextReader.getText(errorMessageKey, args);
    }

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}
}
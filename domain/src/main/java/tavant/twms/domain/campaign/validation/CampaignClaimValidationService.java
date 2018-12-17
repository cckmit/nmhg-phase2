package tavant.twms.domain.campaign.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignLaborDetail;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignServiceDetail;
import tavant.twms.domain.campaign.CampaignTravelDetail;
import tavant.twms.domain.campaign.HussPartsToReplace;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;

import com.domainlanguage.money.Money;

public class CampaignClaimValidationService {

	private Claim claim;

	private Campaign campaign;

	private CampaignAssignmentService campaignAssignmentService;

	private CurrencyExchangeRateRepository currencyExchangeRateRepository;
	
	private ConfigParamService configParamService;
	private boolean partsReplacedInstalledSectionVisible;
	private boolean canBUPartBeReplacedByNonBUPart;


	public CampaignClaimValidationService() {
	}

	private boolean compareNonOemParts(Currency currency) {
		List<NonOEMPartToReplace> campaignNonOems = new ArrayList<NonOEMPartToReplace>();
		if(partsReplacedInstalledSectionVisible && canBUPartBeReplacedByNonBUPart){
			for(HussPartsToReplace hussPartsToReplace : campaign.getHussPartsToReplace()){
				campaignNonOems.addAll(hussPartsToReplace.getNonOEMpartsToReplace());
			}
		} else {
			campaignNonOems = campaign.getNonOEMpartsToReplace();
		}
		List<PartReplaced> claimNonOems = claim.getServiceInformation().getServiceDetail().getNonOEMPriceFetchParts();
		boolean isValid = true;

		//Check number non-oem parts in campaign and claim
		if (campaignNonOems.size()!= claimNonOems.size()){
			return isValid = false;
		}

		for (int i = 0; i < campaignNonOems.size(); i++) {
			NonOEMPartToReplace campaignNonOemPart = campaignNonOems.get(i);
			boolean foundMatchingNonOemPart = false;
			for(int j = 0; j < claimNonOems.size(); j++){
				PartReplaced claimNonOemPart = claimNonOems.get(j);
				if (claim.getServiceInformation().getServiceDetail().isNonOEMPartReplaced(claimNonOemPart)) {
					if(StringUtils.trimTrailingWhitespace(((NonOEMPartReplaced)claimNonOemPart).getDescription())
							.equalsIgnoreCase(StringUtils.trimTrailingWhitespace(campaignNonOemPart.getDescription()))							
						&& ((NonOEMPartReplaced)claimNonOemPart).getNumberOfUnits().intValue() == campaignNonOemPart.getNoOfUnits().intValue()){
						foundMatchingNonOemPart = true;
					} 
				} else {
					if(StringUtils.trimTrailingWhitespace(((InstalledParts)claimNonOemPart).getPartNumber())
							.equalsIgnoreCase(StringUtils.trimTrailingWhitespace(campaignNonOemPart.getDescription()))){
						foundMatchingNonOemPart = true;
					}
				}
			}
			if(!foundMatchingNonOemPart){
				return foundMatchingNonOemPart;
			}
		}
		Money totalCampaignNonOemCost = null;
		
		if(partsReplacedInstalledSectionVisible && canBUPartBeReplacedByNonBUPart){
			totalCampaignNonOemCost = calculateTotalCostForCampaignNonOemsHussmannParts(campaignNonOems, currency);
		} else {
			totalCampaignNonOemCost = calculateTotalCostForCampaignNonOems(campaignNonOems,currency);
		}
		
		Money totalClaimNonOemCost = calculateTotalCostForClaimNonOems(claimNonOems);


        if (totalCampaignNonOemCost == null && totalClaimNonOemCost != null) {
            return isValid = false;
        }
        if (totalCampaignNonOemCost != null && totalClaimNonOemCost == null) {
            return isValid = false;
        }
        if(totalCampaignNonOemCost != null && totalClaimNonOemCost != null
        		&& !totalClaimNonOemCost.equals(totalCampaignNonOemCost)){
        	isValid=false;
        }
        return isValid;
	}

	public boolean compareOemParts() {
		boolean isValid = true;
		if(partsReplacedInstalledSectionVisible){
			List<OEMPart> campaignRemovedParts = new ArrayList<OEMPart>();
			List<OEMPart> campaignInstalledParts = new ArrayList<OEMPart>();
			List<OEMPart> claimRemovedParts = new ArrayList<OEMPart>();
			List<OEMPart> claimInstalledParts = new ArrayList<OEMPart>();
			for(HussPartsToReplace hussPartsToReplace : campaign.getHussPartsToReplace()){
				List<OEMPartToReplace> removedParts = hussPartsToReplace.getRemovedParts();
				List<OEMPartToReplace> installedParts = hussPartsToReplace.getInstalledParts();
				campaignRemovedParts.addAll(populateCampaignParts(removedParts));
				campaignInstalledParts.addAll(populateCampaignParts(installedParts));
			}
				 
			for(HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : 
				 claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()){
				 List<OEMPartReplaced> replacedParts = hussmanPartsReplacedInstalled.getReplacedParts();
				 List<InstalledParts> hussmanInstalledParts = hussmanPartsReplacedInstalled.getHussmanInstalledParts();
				 claimRemovedParts.addAll(populateClaimParts(replacedParts));
				 claimInstalledParts.addAll(populateClaimInstalledParts(hussmanInstalledParts));
			}
			if(campaignRemovedParts.size() != claimRemovedParts.size()){
				isValid = false;
				return isValid;
			}
			if(campaignInstalledParts.size() != claimInstalledParts.size()){
				isValid = false;
				return isValid;
			}	 
			for(int i=0; i< claimRemovedParts.size(); i++){
				boolean foundMatching = false;
				for(int j=0; j<campaignRemovedParts.size(); j++){
					if(campaignRemovedParts.get(j).equals(claimRemovedParts.get(i))){
						foundMatching = true;
					}
				}
				if(!foundMatching){
					return isValid = false;
				}
			}
			
			for(int i=0; i< claimInstalledParts.size(); i++){
				boolean foundMatching = false;
				for(int j=0; j<campaignInstalledParts.size(); j++){
					if(campaignInstalledParts.get(j).equals(claimInstalledParts.get(i))){
						foundMatching = true;
					}
				}
				if(!foundMatching){
					return isValid = false;
				}
			}
			return isValid;
		}else{
			 List<OEMPartToReplace> campaignOems = campaign.getOemPartsToReplace();
			 List<OEMPartReplaced> claimOems = claim.getServiceInformation().getServiceDetail().getOEMPartsReplaced();
			 if (campaignOems.size() != claimOems.size()) {
				isValid = false;
				return isValid;
			 }
			 List<OEMPart> campaignParts = populateCampaignParts(campaignOems);
			 List<OEMPart> claimParts = populateClaimParts(claimOems);
			 for (int i = 0; i < claimParts.size(); i++) {
				 boolean foundMatching = false;
				 for(int j = 0; j < campaignParts.size(); j++){
					 if (campaignParts.get(j).equals(claimParts.get(i))){
						foundMatching = true;
					 }
				 }
				 if(!foundMatching){
					return isValid = false;
				 }
			 }
			 return isValid;
		}
	}

	public boolean compareLaborDetails() {
		List<CampaignLaborDetail> campaignLaborDetails = campaign.getCampaignServiceDetail().getCampaignLaborLimits();
		List<LaborDetail> claimLaborDetails = claim.getServiceInformation().getServiceDetail().getLaborPerformed();
		boolean isValid = true;
		if (campaignLaborDetails.size() != claimLaborDetails.size()) {
			isValid = false;
			return isValid;
		}
		Collections.sort(campaignLaborDetails, CAMPAIGN_SORT);
		Collections.sort(claimLaborDetails, CLAIM_SORT);

		for(int i=0; i<claimLaborDetails.size(); i++){
			boolean foundMatching = false;
			for(int j=0; j<campaignLaborDetails.size(); j++){
				if(claimLaborDetails.get(i).getServiceProcedure().getDefinition().getCode()
						.equals(campaignLaborDetails.get(j).getServiceProcedureDefinition().getCode())) {
					foundMatching = true;
				}
				if((claimLaborDetails.get(i).getAdditionalLaborHours()!=null && claimLaborDetails.get(i).getAdditionalLaborHours().floatValue()!= 0f)) {
					return isValid = false;
				}
			}
			if(!foundMatching){
				return isValid = false;
			}
		}
		return isValid;
	}

	public boolean compareTravelDetails() {
		CampaignTravelDetail campaignTravelDetail = campaign.getCampaignServiceDetail().getTravelDetails();
		TravelDetail claimTravelDetail = claim.getServiceInformation().getServiceDetail().getTravelDetails();
		boolean isValid = true;
		if(campaignTravelDetail != null && claimTravelDetail == null){
			return false;
		}else if(campaignTravelDetail == null && claimTravelDetail != null){
			return isValid;
		}
		

		BigDecimal campaignTravelDistance = campaignTravelDetail.getDistance();
		BigDecimal campaignTravelHours = campaignTravelDetail.getHours();
		BigDecimal claimTravelDistance = claimTravelDetail.getDistance();
		BigDecimal claimTravelHours =BigDecimal.ZERO;
		if(!org.apache.commons.lang.StringUtils.isEmpty(claimTravelDetail.getHours()))
			claimTravelHours= new BigDecimal(claimTravelDetail.getHours());
		Integer claimTravelTrips = claimTravelDetail.getTrips();
		Integer campaignTravelTrips = campaignTravelDetail.getTrips();
		BigDecimal campaignAdditionalHours=campaignTravelDetail.getAdditionalHours();
		BigDecimal claimAdditionalHours = claimTravelDetail.getAdditionalHours();

		//if claim distance is greater than campaign validation fails
		if(claimTravelDistance != null && campaignTravelDistance != null && claimTravelDistance.compareTo(campaignTravelDistance) != 0)
			isValid = false;

		if((claimTravelDistance != null && (claimTravelDistance.compareTo(BigDecimal.ZERO)!=0) && campaignTravelDistance == null ) || (campaignTravelDistance != null && claimTravelDistance == null)){
			isValid = false;
		}
		
		if(claimTravelDistance != null && campaignTravelDistance != null && campaignTravelDetail.getUom() != null &&
				(!(campaignTravelDetail.getUom().equalsIgnoreCase(claimTravelDetail.getUom())))){
			isValid = false;
		}

		//if claim travel hours is greater than campaign validation fails
		if(claimTravelHours != null && campaignTravelHours != null && claimTravelHours.compareTo(campaignTravelHours)!= 0){
			isValid = false;
		}

		if((claimTravelHours != null && (claimTravelHours.compareTo(BigDecimal.ZERO)!=0) && campaignTravelHours == null ) || (campaignTravelHours != null && claimTravelHours == null)){
			isValid = false;
		}

		if( claimTravelTrips !=null && campaignTravelTrips!=null &&(claimTravelTrips.intValue() != campaignTravelTrips.intValue())){
			isValid = false;
		}
		
		if(claimAdditionalHours != null && campaignAdditionalHours != null && claimAdditionalHours.compareTo(campaignAdditionalHours)!= 0){
			isValid = false;
		}

		if((claimAdditionalHours != null && (claimAdditionalHours.compareTo(BigDecimal.ZERO)!=0) && campaignAdditionalHours == null ) || (campaignAdditionalHours != null && claimAdditionalHours == null)){
			isValid = false;
		}

		return isValid;
	}

	public boolean compareIncidentals(Claim claim){
    	boolean isValid=true;
    	Currency currency = claim.getCurrencyForCalculation();
    	CampaignServiceDetail campaignServiceDetail = campaign.getCampaignServiceDetail();
    	tavant.twms.domain.claim.ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
    	Money perDiem = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.PER_DIEM_COST_CATEGORY_CODE,currency);
    	Money freightDuty = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.FREIGHT_DUTY_CATEGORY_CODE,currency);
    	Money localPurchase = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE,currency); 
    	Money meals = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.MEALS_HOURS_COST_CATEGORY_CODE,currency);
    	Money otherFreight = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE,currency);
    	Money others = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.OTHERS_CATEGORY_CODE,currency);
    	Money parking = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.PARKING_COST_CATEGORY_CODE,currency);
    	Money rentalCharges =campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.RENTAL_CHARGES_COST_CATEGORY_CODE,currency);
    	Money tolls = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(
    			CostCategory.TOLLS_COST_CATEGORY_CODE,currency);
		Money handlingFee = campaignServiceDetail
				.getCampaignPriceForSectionAndCurrency(
						CostCategory.HANDLING_FEE_CODE, currency);
		Money transportation = campaignServiceDetail.getCampaignPriceForSectionAndCurrency(CostCategory.TRANSPORTATION_COST_CATEGORY_CODE, currency);
    	if(claim.getPerDiemConfig() != null && claim.getPerDiemConfig()){
	    	if((perDiem == null && serviceDetail.getPerDiem() != null
	    			&& (serviceDetail.getPerDiem().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (serviceDetail.getPerDiem() == null && perDiem != null) 
	    	    	|| (perDiem.compareTo(serviceDetail.getPerDiem())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getItemDutyConfig() != null && claim.getItemDutyConfig()){
	    	if((freightDuty == null && serviceDetail.getItemFreightAndDuty() != null 
	    			&& (serviceDetail.getItemFreightAndDuty().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (freightDuty != null && serviceDetail.getItemFreightAndDuty() == null)
	    			|| (freightDuty != null && freightDuty.compareTo(serviceDetail.getItemFreightAndDuty())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getLocalPurchaseConfig() != null && claim.getLocalPurchaseConfig()){
	    	if((localPurchase == null && serviceDetail.getLocalPurchaseExpense() != null
	    			&& (serviceDetail.getLocalPurchaseExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (localPurchase != null && serviceDetail.getLocalPurchaseExpense() == null) 
	    			|| (localPurchase != null && localPurchase.compareTo(serviceDetail.getLocalPurchaseExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getMealsConfig() != null && claim.getMealsConfig()){
	    	if((meals == null && serviceDetail.getMealsExpense() != null
	    			&& (serviceDetail.getMealsExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			||(serviceDetail.getMealsExpense() == null && meals != null)  
	    			|| (meals != null && meals.compareTo(serviceDetail.getMealsExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getOtherFreightDutyConfig() != null && claim.getOtherFreightDutyConfig()){
	    	if((otherFreight == null && serviceDetail.getOtherFreightDutyExpense() != null
	    			&& (serviceDetail.getOtherFreightDutyExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (serviceDetail.getOtherFreightDutyExpense() == null && otherFreight != null) 
	    			|| (otherFreight != null && otherFreight.compareTo(serviceDetail.getOtherFreightDutyExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getOthersConfig() != null && claim.getOthersConfig()){
	    	if((others == null && serviceDetail.getOthersExpense() != null
	    			&& (serviceDetail.getOthersExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			||(serviceDetail.getOthersExpense() == null && others != null) 
	    			|| (others != null && others.compareTo(serviceDetail.getOthersExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getParkingConfig() != null && claim.getParkingConfig()){
	    	if((parking == null && serviceDetail.getParkingAndTollExpense() != null
	    			&& (serviceDetail.getParkingAndTollExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			||(serviceDetail.getParkingAndTollExpense() == null && parking != null)
	    			||(parking != null && parking.compareTo(serviceDetail.getParkingAndTollExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	
    	if(claim.getRentalChargesConfig() != null && claim.getRentalChargesConfig()){
	    	if((rentalCharges == null && serviceDetail.getRentalCharges() != null
	    			&& (serviceDetail.getRentalCharges().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			||(serviceDetail.getRentalCharges() == null && rentalCharges != null)
	    			||(rentalCharges != null && rentalCharges.compareTo(serviceDetail.getRentalCharges())!=0)){
	    		return isValid = false;
	    	}
    	}
    	if(claim.getTollsConfig() != null && claim.getTollsConfig()){
	    	if((tolls == null && serviceDetail.getTollsExpense() != null
	    			&& (serviceDetail.getTollsExpense().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (serviceDetail.getTollsExpense() == null && tolls != null) 
	    			|| (tolls != null && tolls.compareTo(serviceDetail.getTollsExpense())!=0)){
	    		return isValid = false;
	    	}
    	}
    	if(claim.getHandlingFeeConfig() != null && claim.getHandlingFeeConfig()){
	    	if((handlingFee == null && serviceDetail.getHandlingFee() != null
	    			&& (serviceDetail.getHandlingFee().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0)) 
	    			|| (serviceDetail.getHandlingFee() == null && handlingFee != null) 
	    			|| (handlingFee != null && handlingFee.compareTo(serviceDetail.getHandlingFee())!=0)){
	    		return isValid = false;
	    	}
    	}
    	if(claim.getTransportation() != null && claim.getTransportation()){
    		if((transportation == null && serviceDetail.getTransportationAmt() != null
    				&& (serviceDetail.getTransportationAmt().breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO) != 0))
    				|| (serviceDetail.getTransportationAmt() == null && transportation != null)
    				|| (transportation != null && transportation.compareTo(serviceDetail.getTransportationAmt()) != 0)){
    			return isValid = false;
    		}
    	}
    	return isValid;
    }

	public boolean compareMiscPartsDetails(Currency currency){
		List<NonOEMPartToReplace> campaignMiscOems = campaign.getMiscPartsToReplace();
		List<NonOEMPartReplaced> claimMiscParts = claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced();
		boolean isValid = true;

		//Check number non-oem parts in campaign and claim
		if(campaignMiscOems == null && claimMiscParts!=null){
			return isValid = false;
		}
		
		if(campaignMiscOems != null && claimMiscParts == null){
			return isValid = false;
		}

		if (campaignMiscOems.size() != claimMiscParts.size()){
			return isValid = false;
		}

		List<OEMPart> campaignParts = populateCampaignMiscParts(campaignMiscOems);
		List<OEMPart> claimParts = populateClaimMiscParts(claimMiscParts);

		for (int i = 0; i < claimParts.size(); i++) {
			boolean foundMatching = false;
			for(int j = 0; j<campaignParts.size(); j++){
				if (campaignParts.get(j).equals(claimParts.get(i))){
					foundMatching = true;
				}
			}
			if(!foundMatching){
				return isValid = false;
			}
		}

		return isValid;
	}

	private List<OEMPart> populateClaimParts(List<OEMPartReplaced> claimOems) {
		List<OEMPart> oemParts = new ArrayList<OEMPart>();
		for (OEMPartReplaced replaced : claimOems) {
			OEMPart oemPart = new OEMPart();
			oemPart.setNoOfUnits(replaced.getNumberOfUnits());
			oemPart.setSerialNumber(replaced.getItemReference()
					.getUnserializedItem().getNumber());
			oemParts.add(oemPart);
		}
		return oemParts;
	}
	
	private List<OEMPart> populateClaimInstalledParts(List<InstalledParts> claimOems) {
		List<OEMPart> oemParts = new ArrayList<OEMPart>();
		for (InstalledParts replaced : claimOems) {
			OEMPart oemPart = new OEMPart();
			oemPart.setNoOfUnits(replaced.getNumberOfUnits());
			oemPart.setSerialNumber(replaced.getItem().getNumber());
			oemParts.add(oemPart);
		}
		return oemParts;
	}
	
	private List<OEMPart> populateCampaignParts(
			List<OEMPartToReplace> campaignOems) {
		List<OEMPart> oemParts = new ArrayList<OEMPart>();
		for (OEMPartToReplace parts : campaignOems) {
			OEMPart oemPart = new OEMPart();
			oemPart.setNoOfUnits(parts.getNoOfUnits());
			oemPart.setSerialNumber(parts.getItem().getNumber());
			oemParts.add(oemPart);
		}
		return oemParts;
	}

	private List<OEMPart> populateCampaignMiscParts(
			List<NonOEMPartToReplace> campaignMiscParts) {
		List<OEMPart> oemParts = new ArrayList<OEMPart>();
		for (NonOEMPartToReplace parts : campaignMiscParts) {
			OEMPart oemPart = new OEMPart();
			oemPart.setNoOfUnits(parts.getNoOfUnits());
			oemPart.setSerialNumber(parts.getMiscItem().getPartNumber());
			oemParts.add(oemPart);
		}
		return oemParts;
	}

	private List<OEMPart> populateClaimMiscParts(
			List<NonOEMPartReplaced> campaignMiscParts) {
		List<OEMPart> oemParts = new ArrayList<OEMPart>();
		for (NonOEMPartReplaced parts : campaignMiscParts) {
			OEMPart oemPart = new OEMPart();
			oemPart.setNoOfUnits(parts.getNumberOfUnits());
			oemPart.setSerialNumber(parts.getMiscItem().getPartNumber());
			oemParts.add(oemPart);
		}
		return oemParts;
	}


	private Money calculateTotalCostForClaimNonOems(
			List<PartReplaced> claimNonOems) {
		Money totalCost = null;
		for (PartReplaced replace : claimNonOems) {
			if (replace.getPricePerUnit() != null) {
                Money cost = replace.getPricePerUnit().times(replace.getNumberOfUnits());
                totalCost = totalCost == null ? cost : totalCost.plus(cost);
			}
		}
		return totalCost;
	}

	private Money calculateTotalCostForCampaignNonOems(List<NonOEMPartToReplace> campaignNonOems,
														Currency currency) {
		Money totalCost = null;
		for (NonOEMPartToReplace replace : campaignNonOems) {
			if(replace.getCampaignSectionPrice() != null && !replace.getCampaignSectionPrice().isEmpty()) {
				Money nonOemSectionPrice = replace.getCampaignSectionPriceForCurrency(currency);
                if(nonOemSectionPrice!=null){
					totalCost = totalCost == null ? nonOemSectionPrice.times(replace.getNoOfUnits())
	                		: totalCost.plus(nonOemSectionPrice.times(replace.getNoOfUnits()));
                }
            }
		}
		return totalCost;
	}
	
	private Money calculateTotalCostForCampaignNonOemsHussmannParts(List<NonOEMPartToReplace> campaignNonOems,
			Currency currency) {
		Money totalCost = null;
		for (NonOEMPartToReplace replace : campaignNonOems) {
			if (replace.getPricePerUnit() != null) {
				totalCost = totalCost == null ? replace.getPricePerUnit().times(replace.getNoOfUnits()) 
						: totalCost.plus(replace.getPricePerUnit().times(replace.getNoOfUnits()));
			}
		}
		return totalCost;
	}



	private class OEMPart {
		private String serialNumber;

		private Integer noOfUnits;

		public Integer getNoOfUnits() {
			return noOfUnits;
		}

		public void setNoOfUnits(Integer noOfUnits) {
			this.noOfUnits = noOfUnits;
		}

		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final OEMPart other = (OEMPart) obj;
			if (noOfUnits == null) {
				if (other.noOfUnits != null)
					return false;
			} else if (!noOfUnits.equals(other.noOfUnits))
				return false;
			if (serialNumber == null) {
				if (other.serialNumber != null)
					return false;
			} else if (!serialNumber.equals(other.serialNumber))
				return false;
			return true;
		}

	}

	public boolean isClaimWithinCampaignLimits(Claim claim) {
		setClaimAndCampaign(claim);
		partsReplacedInstalledSectionVisible = configParamService.getBooleanValue(
				ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName());	
		canBUPartBeReplacedByNonBUPart =configParamService.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
		return compareNonOemParts(claim.getCurrencyForCalculation())
				&& compareOemParts()
				&& compareLaborDetails()
				&& compareTravelDetails()
				&& compareMiscPartsDetails(claim.getCurrencyForCalculation())
				&& compareIncidentals(claim);
	}

	private Comparator<CampaignLaborDetail> CAMPAIGN_SORT =
        new Comparator<CampaignLaborDetail>() {
            public int compare(CampaignLaborDetail e1, CampaignLaborDetail e2) {
                return e1.getServiceProcedureDefinition().getCode().toLowerCase().compareTo(e2.getServiceProcedureDefinition().getCode().toLowerCase());
            }
        };

    private Comparator<LaborDetail> CLAIM_SORT =
         new Comparator<LaborDetail>() {
             public int compare(LaborDetail e1, LaborDetail e2) {
                 return e1.getServiceProcedure().getDefinition().getCode().toLowerCase().compareTo(e2.getServiceProcedure().getDefinition().getCode().toLowerCase());
             }
        };

	private void setClaimAndCampaign(Claim claim) {
		this.claim = claim;
		if(claim.getId() == null) {
			this.campaign = claim.getCampaign();
		} else {
			CampaignNotification campaignNotification = campaignAssignmentService.findNotificationForClaim(claim);
			if(campaignNotification == null) {
				this.campaign = claim.getCampaign();
			} else {
				this.campaign = campaignNotification.getCampaign();
			}
		}
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public CurrencyExchangeRateRepository getCurrencyExchangeRateRepository() {
		return currencyExchangeRateRepository;
	}

	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}

	public boolean isCanBUPartBeReplacedByNonBUPart() {
		return canBUPartBeReplacedByNonBUPart;
	}

	public void setCanBUPartBeReplacedByNonBUPart(boolean canBUPartBeReplacedByNonBUPart) {
		this.canBUPartBeReplacedByNonBUPart = canBUPartBeReplacedByNonBUPart;
	}
	
}

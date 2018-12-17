/**
 * 
 */
package tavant.twms.web.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.configuration.ConfigName;

/**
 * @author mritunjay.kumar
 * 
 */
public class PrintClaimObject {
	private Claim claim;
	private List<ClaimPaymentObject> payment;
	private boolean internalUser;
	private boolean showPaymentInfo;
	private String showWarningMessage ;
	private String jobCodeDescription ;
	private boolean showPercentageAccepted;
	private String gifName;
	private String buTitle;
	private Boolean advisorEnabled;
	private String ownerName;
	private String ownerCity;
	private String ownerState;
	private String ownerZipCode;
	private String ownerCountry;
	private Boolean isOwnerInfoDisplayNeeded = Boolean.FALSE;
	private Boolean isOEMReplacedPartPresent = Boolean.FALSE;
	private Boolean showServiceProcedureSection;
	private Boolean showEquipmentInfoSection = Boolean.FALSE;
	private Boolean isPartReplacedInstalledSectionVisible;
	private Boolean isRootCauseVisible;
	private Boolean buPartReplacedByNonBUPart;
	private Boolean isAlarmCodesSectionVisible;
	private Boolean isNonOEMPartsSectionVisible;
	private Boolean isSmrClaimAllowedForDealer;
	private Boolean isTechnicianEnabled;
	private Boolean isChecklaborPerformed;
	private boolean stateMondateEnabled;
	private String warrantyAmount;
	private String cpAmount;	
	private String  partInstalledOn;
	private List<ManageDocumentObject> documentObjects;
	private List<WaraningMessageObject> warningMessages;
	private List<JobCodes> codes;
	private String reasonsList;
	private String policyApplied;
	private String itemFreightDuty;
	private String itemFreightDutyFileName;
	private String mealsExpense;
	private String mealsExpenseFileName;
	private String perDiem;
	private String perDiemFileName;
	private String parkingAndTollExpense;
	private String parkingAndTollExpenseFileName;
	private String rentalCharges;
	private String rentalChargesFileName;
	private String localPurchaseExpense;
	private String localPurchaseExpenseFileName;
	private String tollsExpense;
	private String tollsExpenseFileName;
	private String otherFreightDutyExpense;
	private String otherFreightDutyExpenseFileName;
	private String transportationAmt;
	private String transportationAmtFileName;
	private String handlingFee;
	private String handlingFeeFileName;
//	  private List<WaraningMessageObject> messages;
	  private String disclaimerInfo;

	private Boolean newPaymentSectionVisible;
	
	public Boolean getIsTechnicianEnabled() {
		return isTechnicianEnabled;
	}

	public void setIsTechnicianEnabled(Boolean isTechnicianEnabled) {
		this.isTechnicianEnabled = isTechnicianEnabled;
	}

	public Boolean getIsSmrClaimAllowedForDealer() {
		return isSmrClaimAllowedForDealer;
	}

	public void setIsSmrClaimAllowedForDealer(Boolean isSmrClaimAllowedForDealer) {
		this.isSmrClaimAllowedForDealer = isSmrClaimAllowedForDealer;
	}

	private boolean laborSplitEnabled;
	private boolean laborSplitOption;
	private Boolean dealerJobNumberEnabled;
	
	
	public boolean isLaborSplitEnabled() {
		return laborSplitEnabled;
	}

	public void setLaborSplitEnabled(boolean laborSplitEnabled) {
		this.laborSplitEnabled = laborSplitEnabled;
	}

	public boolean isLaborSplitOption() {
		return laborSplitOption;
	}

	public void setLaborSplitOption(boolean laborSplitOption) {
		this.laborSplitOption = laborSplitOption;
	}

	public Boolean getIsPartReplacedInstalledSectionVisible() {
		return isPartReplacedInstalledSectionVisible;
	}

	public void setIsPartReplacedInstalledSectionVisible(
			Boolean isPartReplacedInstalledSectionVisible) {
		this.isPartReplacedInstalledSectionVisible = isPartReplacedInstalledSectionVisible;
	}

	public String getBuTitle() {
		return buTitle;
	}

	public void setBuTitle(String buTitle) {
		this.buTitle = buTitle;
	}

	public String getGifName() {
		return gifName;
	}

	public void setGifName(String gifName) {
		this.gifName = gifName;
	}

	public boolean isShowPercentageAccepted() {
		return showPercentageAccepted;
	}

	public void setShowPercentageAccepted(boolean showPercentageAccepted) {
		this.showPercentageAccepted = showPercentageAccepted;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public boolean isInternalUser() {
		return internalUser;
	}

	public void setInternalUser(boolean internalUser) {
		this.internalUser = internalUser;
	}

	public boolean isShowPaymentInfo() {
		return showPaymentInfo;
	}

	public void setShowPaymentInfo(boolean showPaymentInfo) {
		this.showPaymentInfo = showPaymentInfo;
	}

	public List<ClaimPaymentObject> getPayment() {
		return payment;
	}

	public void setPayment(List<ClaimPaymentObject> payment) {
		this.payment = payment;
	}

	public Boolean getNewPaymentSectionVisible() {
		return newPaymentSectionVisible;
	}



	public void setNewPaymentSectionVisible(Boolean newPaymentSectionVisible) {
		this.newPaymentSectionVisible = newPaymentSectionVisible;
	}

	public Boolean getAdvisorEnabled() {
		return advisorEnabled;
	}

	public void setAdvisorEnabled(Boolean advisorEnabled) {
		this.advisorEnabled = advisorEnabled;
	}

	public Boolean getIsRootCauseVisible() {
		return isRootCauseVisible;
	}

	public void setIsRootCauseVisible(Boolean isRootCauseVisible) {
		this.isRootCauseVisible = isRootCauseVisible;
	}

	public String getOwnerCity() {
		return ownerCity;
	}

	public void setOwnerCity(String ownerCity) {
		this.ownerCity = ownerCity;
	}

	public String getOwnerCountry() {
		return ownerCountry;
	}

	public void setOwnerCountry(String ownerCountry) {
		this.ownerCountry = ownerCountry;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerState() {
		return ownerState;
	}

	public void setOwnerState(String ownerState) {
		this.ownerState = ownerState;
	}

	public String getOwnerZipCode() {
		return ownerZipCode;
	}

	public void setOwnerZipCode(String ownerZipCode) {
		this.ownerZipCode = ownerZipCode;
	}
	public Boolean getIsOwnerInfoDisplayNeeded() {
		return isOwnerInfoDisplayNeeded;
	}

	public void setIsOwnerInfoDisplayNeeded(Boolean isOwnerInfoDisplayNeeded) {
		this.isOwnerInfoDisplayNeeded = isOwnerInfoDisplayNeeded;
	}

	public Boolean getIsOEMReplacedPartPresent() {
		return isOEMReplacedPartPresent;
	}

	public void setIsOEMReplacedPartPresent(Boolean isOEMReplacedPartPresent) {
		this.isOEMReplacedPartPresent = isOEMReplacedPartPresent;
	}

	public Boolean getBuPartReplacedByNonBUPart() {
		return buPartReplacedByNonBUPart;
	}

	public void setBuPartReplacedByNonBUPart(Boolean buPartReplacedByNonBUPart) {
		this.buPartReplacedByNonBUPart = buPartReplacedByNonBUPart;
	}

	public Boolean getIsAlarmCodesSectionVisible() {
		return isAlarmCodesSectionVisible;
	}

	public void setIsAlarmCodesSectionVisible(Boolean isAlarmCodesSectionVisible) {
		this.isAlarmCodesSectionVisible = isAlarmCodesSectionVisible;
	}	
	
	
	public Boolean getShowServiceProcedureSection() {
		return showServiceProcedureSection;
	}

	public void setShowServiceProcedureSection(Boolean showServiceProcidureSection) {
		this.showServiceProcedureSection = showServiceProcidureSection;
	}

	public Boolean getShowEquipmentInfoSection() {
		return showEquipmentInfoSection;
	}

	public void setShowEquipmentInfoSection(Boolean showEquipmentInfoSection) {
		this.showEquipmentInfoSection = showEquipmentInfoSection;
	}

	public Boolean getIsNonOEMPartsSectionVisible() {
		return isNonOEMPartsSectionVisible;
	}

	public void setIsNonOEMPartsSectionVisible(Boolean isNonOEMPartsSectionVisible) {
		this.isNonOEMPartsSectionVisible = isNonOEMPartsSectionVisible;
	}

	public Boolean getDealerJobNumberEnabled() {
		return dealerJobNumberEnabled;
	}

	public void setDealerJobNumberEnabled(Boolean isDealerJobNumberEnabled) {
		this.dealerJobNumberEnabled = isDealerJobNumberEnabled;
	}

	public boolean isStateMondateEnabled() {
		return stateMondateEnabled;
	}

	public void setStateMondateEnabled(boolean stateMondateEnabled) {
		this.stateMondateEnabled = stateMondateEnabled;
	}

	public String getWarrantyAmount() {
		return warrantyAmount;
	}

	public void setWarrantyAmount(String warrantyAmount) {
		this.warrantyAmount = warrantyAmount;
	}

	public String getCpAmount() {
		return cpAmount;
	}

	public void setCpAmount(String cpAmount) {
		this.cpAmount = cpAmount;
	}

	public List<ManageDocumentObject> getDocumentObjects() {
		return documentObjects;
	}

	public void setDocumentObjects(List<ManageDocumentObject> documentObjects) {
		this.documentObjects = documentObjects;
	}

	public Boolean getIsChecklaborPerformed() {
		return isChecklaborPerformed;
	}

	public void setIsChecklaborPerformed(Boolean isChecklaborPerformed) {
		this.isChecklaborPerformed = isChecklaborPerformed;
	}

	

	
	public String getShowWarningMessage() {
		return showWarningMessage;
	}

	public void setShowWarningMessage(String showWarningMessage) {
		this.showWarningMessage = showWarningMessage;
	}


	public String getDisclaimerInfo() {
		return disclaimerInfo;
	}

	public void setDisclaimerInfo(String disclaimerInfo) {
		this.disclaimerInfo = disclaimerInfo;
	}

	public List<WaraningMessageObject> getWarningMessages() {
		return warningMessages;
	}

	public void setWarningMessages(List<WaraningMessageObject> warningMessages) {
		this.warningMessages = warningMessages;
	}

	public String getJobCodeDescription() {
		return jobCodeDescription;
	}

	public void setJobCodeDescription(String jobCodeDescription) {
		this.jobCodeDescription = jobCodeDescription;
	}

	public String getPartInstalledOn() {
		return partInstalledOn;
	}

	public void setPartInstalledOn(String partInstalledOn) {
		this.partInstalledOn = partInstalledOn;		
	}

	/**
	 * @return the codes
	 */
	public List<JobCodes> getCodes() {
		return codes;
	}

	/**
	 * @param codes the codes to set
	 */
	public void setCodes(List<JobCodes> codes) {
		this.codes = codes;
	}

	/**
	 * @return the policyApplied
	 */
	public String getPolicyApplied() {
		return policyApplied;
	}

	/**
	 * @param policyApplied the policyApplied to set
	 */
	public void setPolicyApplied(String policyApplied) {
		this.policyApplied = policyApplied;
	}

	/**
	 * @return the reasonsList
	 */
	public String getReasonsList() {
		return reasonsList;
	}

	/**
	 * @param reasonsList the reasonsList to set
	 */
	public void setReasonsList(String reasonsList) {
		this.reasonsList = reasonsList;
	}

	/**
	 * @return the itemFreightDuty
	 */
	public String getItemFreightDuty() {
		return itemFreightDuty;
	}

	/**
	 * @param itemFreightDuty the itemFreightDuty to set
	 */
	public void setItemFreightDuty(String itemFreightDuty) {
		this.itemFreightDuty = itemFreightDuty;
	}

	/**
	 * @return the itemFreightDutyFileName
	 */
	public String getItemFreightDutyFileName() {
		return itemFreightDutyFileName;
	}

	/**
	 * @param itemFreightDutyFileName the itemFreightDutyFileName to set
	 */
	public void setItemFreightDutyFileName(String itemFreightDutyFileName) {
		this.itemFreightDutyFileName = itemFreightDutyFileName;
	}

	/**
	 * @return the mealsExpense
	 */
	public String getMealsExpense() {
		return mealsExpense;
	}

	/**
	 * @param mealsExpense the mealsExpense to set
	 */
	public void setMealsExpense(String mealsExpense) {
		this.mealsExpense = mealsExpense;
	}

	/**
	 * @return the mealsExpenseFileName
	 */
	public String getMealsExpenseFileName() {
		return mealsExpenseFileName;
	}

	/**
	 * @param mealsExpenseFileName the mealsExpenseFileName to set
	 */
	public void setMealsExpenseFileName(String mealsExpenseFileName) {
		this.mealsExpenseFileName = mealsExpenseFileName;
	}

	/**
	 * @return the perDiem
	 */
	public String getPerDiem() {
		return perDiem;
	}

	/**
	 * @param perDiem the perDiem to set
	 */
	public void setPerDiem(String perDiem) {
		this.perDiem = perDiem;
	}

	/**
	 * @return the perDiemFileName
	 */
	public String getPerDiemFileName() {
		return perDiemFileName;
	}

	/**
	 * @param perDiemFileName the perDiemFileName to set
	 */
	public void setPerDiemFileName(String perDiemFileName) {
		this.perDiemFileName = perDiemFileName;
	}

	/**
	 * @return the parkingAndTollExpense
	 */
	public String getParkingAndTollExpense() {
		return parkingAndTollExpense;
	}

	/**
	 * @param parkingAndTollExpense the parkingAndTollExpense to set
	 */
	public void setParkingAndTollExpense(String parkingAndTollExpense) {
		this.parkingAndTollExpense = parkingAndTollExpense;
	}

	/**
	 * @return the parkingAndTollExpenseFileName
	 */
	public String getParkingAndTollExpenseFileName() {
		return parkingAndTollExpenseFileName;
	}

	/**
	 * @param parkingAndTollExpenseFileName the parkingAndTollExpenseFileName to set
	 */
	public void setParkingAndTollExpenseFileName(
			String parkingAndTollExpenseFileName) {
		this.parkingAndTollExpenseFileName = parkingAndTollExpenseFileName;
	}

	/**
	 * @return the rentalCharges
	 */
	public String getRentalCharges() {
		return rentalCharges;
	}

	/**
	 * @param rentalCharges the rentalCharges to set
	 */
	public void setRentalCharges(String rentalCharges) {
		this.rentalCharges = rentalCharges;
	}

	/**
	 * @return the rentalChargesFileName
	 */
	public String getRentalChargesFileName() {
		return rentalChargesFileName;
	}

	/**
	 * @param rentalChargesFileName the rentalChargesFileName to set
	 */
	public void setRentalChargesFileName(String rentalChargesFileName) {
		this.rentalChargesFileName = rentalChargesFileName;
	}

	/**
	 * @return the localPurchaseExpense
	 */
	public String getLocalPurchaseExpense() {
		return localPurchaseExpense;
	}

	/**
	 * @param localPurchaseExpense the localPurchaseExpense to set
	 */
	public void setLocalPurchaseExpense(String localPurchaseExpense) {
		this.localPurchaseExpense = localPurchaseExpense;
	}

	/**
	 * @return the localPurchaseExpenseFileName
	 */
	public String getLocalPurchaseExpenseFileName() {
		return localPurchaseExpenseFileName;
	}

	/**
	 * @param localPurchaseExpenseFileName the localPurchaseExpenseFileName to set
	 */
	public void setLocalPurchaseExpenseFileName(
			String localPurchaseExpenseFileName) {
		this.localPurchaseExpenseFileName = localPurchaseExpenseFileName;
	}

	/**
	 * @return the tollsExpense
	 */
	public String getTollsExpense() {
		return tollsExpense;
	}

	/**
	 * @param tollsExpense the tollsExpense to set
	 */
	public void setTollsExpense(String tollsExpense) {
		this.tollsExpense = tollsExpense;
	}

	/**
	 * @return the tollsExpenseFileName
	 */
	public String getTollsExpenseFileName() {
		return tollsExpenseFileName;
	}

	/**
	 * @param tollsExpenseFileName the tollsExpenseFileName to set
	 */
	public void setTollsExpenseFileName(String tollsExpenseFileName) {
		this.tollsExpenseFileName = tollsExpenseFileName;
	}

	/**
	 * @return the otherFreightDutyExpense
	 */
	public String getOtherFreightDutyExpense() {
		return otherFreightDutyExpense;
	}

	/**
	 * @param otherFreightDutyExpense the otherFreightDutyExpense to set
	 */
	public void setOtherFreightDutyExpense(String otherFreightDutyExpense) {
		this.otherFreightDutyExpense = otherFreightDutyExpense;
	}

	/**
	 * @return the otherFreightDutyExpenseFileName
	 */
	public String getOtherFreightDutyExpenseFileName() {
		return otherFreightDutyExpenseFileName;
	}

	/**
	 * @param otherFreightDutyExpenseFileName the otherFreightDutyExpenseFileName to set
	 */
	public void setOtherFreightDutyExpenseFileName(
			String otherFreightDutyExpenseFileName) {
		this.otherFreightDutyExpenseFileName = otherFreightDutyExpenseFileName;
	}

	/**
	 * @return the transportationAmt
	 */
	public String getTransportationAmt() {
		return transportationAmt;
	}

	/**
	 * @param transportationAmt the transportationAmt to set
	 */
	public void setTransportationAmt(String transportationAmt) {
		this.transportationAmt = transportationAmt;
	}

	/**
	 * @return the transportationAmtFileName
	 */
	public String getTransportationAmtFileName() {
		return transportationAmtFileName;
	}

	/**
	 * @param transportationAmtFileName the transportationAmtFileName to set
	 */
	public void setTransportationAmtFileName(String transportationAmtFileName) {
		this.transportationAmtFileName = transportationAmtFileName;
	}

	/**
	 * @return the handlingFee
	 */
	public String getHandlingFee() {
		return handlingFee;
	}

	/**
	 * @param handlingFee the handlingFee to set
	 */
	public void setHandlingFee(String handlingFee) {
		this.handlingFee = handlingFee;
	}

	/**
	 * @return the handlingFeeFileName
	 */
	public String getHandlingFeeFileName() {
		return handlingFeeFileName;
	}

	/**
	 * @param handlingFeeFileName the handlingFeeFileName to set
	 */
	public void setHandlingFeeFileName(String handlingFeeFileName) {
		this.handlingFeeFileName = handlingFeeFileName;
	}



	
}

package tavant.twms.web.print;

import java.util.List;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;

import com.domainlanguage.money.Money;

/**
 * @author ravikumar.gowri
 *
 */
public class PrintRecoveryClaimObject {
	private Claim claim;
	private RecoveryClaim recoveryClaim;
	private String gifName;
	private String buTitle;
	private String dealerOrSp;
	private String formatedFailureDate;
	private String formatedRepairDate;
	private String formatedWarrantyStartDate;
	private String formatedWarrantyEndDate;
	private String formatedfiledOnDate;
	private String formatedPurchaseDate;
	private String formatedInstallationDate;
	private String formattedRecoveryClaimDate;
	private String ownerName;
	private String ownerCity;
	private String ownerState;
	private String ownerZipCode;
	private String ownerCountry;
	private Boolean isOwnerInfoDisplayNeeded = Boolean.FALSE;
	private Boolean showEquipmentInfoSection = Boolean.FALSE;
	private String unserializedItemProduct;
	private Boolean showServiceProcedureSection= Boolean.FALSE;
	private boolean laborSplitEnabled=false;
	private boolean laborSplitOption=false;
	private Boolean isOEMReplacedPartPresent = Boolean.FALSE;
	private boolean isNonOEMPartsSectionVisible=false;
	private Boolean buPartReplacedByNonBUPart=Boolean.FALSE;
	private Boolean isPartReplacedInstalledSectionVisible=Boolean.FALSE;
	private Boolean isRootCauseVisible=Boolean.FALSE;
	private boolean internalUser=false;
	private boolean displayCPFlagOnClaimPgOne=false;
//	private boolean isBarcodeEnabled=false;
	private String claimProcessedAs;
	private boolean isClaimAssigneeShownToDealer=false;
	private List<RecoveryCostLineObject> recoveryCostLineItems;
	private boolean hasSupplierRole=false;
	private String datePatternForLoggedInUser;
	private String loggedInUserId;
	private String printDate;
	private Boolean isDealerJobNumberEnabled;
	private Boolean supplierView;
	private Money actualValueForTravel;
	private Money actualValueForMiscellenousExpension;
	private boolean showTravelSection;
	private boolean showMiscellaneousExpenseSection;
	private String formatedRepairStartDate;
	private boolean sectionHide=Boolean.TRUE;
	private String nmhgFooterGif;
	private String addressInfo;	private String unserializedItemGroupCode;
	private List<ManageDocumentObject> documentObjects;
	private String faultLocationNameWithCode;
	private List<JobCodes> codes;
	
	public String getUnserializedItemGroupCode() {
		return unserializedItemGroupCode;
	}

	public void setUnserializedItemGroupCode(String unserializedItemGroupCode) {
		this.unserializedItemGroupCode = unserializedItemGroupCode;
	}

	public String getNmhgFooterGif() {
		return nmhgFooterGif;
	}

	public void setNmhgFooterGif(String nmhgFooterGif) {
		this.nmhgFooterGif = nmhgFooterGif;
	}

	public boolean getSectionHide() {
		return sectionHide;
	}

	public void setSectionHide(boolean sectionHide) {
		this.sectionHide = sectionHide;
	}

	public boolean getShowTravelSection() {
		return showTravelSection;
	}

	public void setShowTravelSection(boolean showTravelSection) {
		this.showTravelSection = showTravelSection;
	}

	public Money getActualValueForTravel() {
		return actualValueForTravel;
	}

	public void setActualValueForTravel(Money actualValueForTravel) {
		this.actualValueForTravel = actualValueForTravel;
	}

	public Boolean getSupplierView() {
		return supplierView;
	}

	public void setSupplierView(Boolean supplierView) {
		this.supplierView = supplierView;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public RecoveryClaim getRecoveryClaim() {
		return recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}

	public String getGifName() {
		return gifName;
	}

	public void setGifName(String gifName) {
		this.gifName = gifName;
	}

	public String getBuTitle() {
		return buTitle;
	}

	public void setBuTitle(String buTitle) {
		this.buTitle = buTitle;
	}

	public String getDealerOrSp() {
		return dealerOrSp;
	}

	public void setDealerOrSp(String dealerOrSp) {
		this.dealerOrSp = dealerOrSp;
	}

	public String getFormatedFailureDate() {
		return formatedFailureDate;
	}

	public void setFormatedFailureDate(String formatedFailureDate) {
		this.formatedFailureDate = formatedFailureDate;
	}

	public String getFormatedRepairDate() {
		return formatedRepairDate;
	}

	public void setFormatedRepairDate(String formatedRepairDate) {
		this.formatedRepairDate = formatedRepairDate;
	}

	public String getFormatedWarrantyStartDate() {
		return formatedWarrantyStartDate;
	}

	public void setFormatedWarrantyStartDate(String formatedWarrantyStartDate) {
		this.formatedWarrantyStartDate = formatedWarrantyStartDate;
	}

	public String getFormatedWarrantyEndDate() {
		return formatedWarrantyEndDate;
	}

	public void setFormatedWarrantyEndDate(String formatedWarrantyEndDate) {
		this.formatedWarrantyEndDate = formatedWarrantyEndDate;
	}

	public String getFormatedfiledOnDate() {
		return formatedfiledOnDate;
	}

	public void setFormatedfiledOnDate(String formatedfiledOnDate) {
		this.formatedfiledOnDate = formatedfiledOnDate;
	}

	public String getFormatedPurchaseDate() {
		return formatedPurchaseDate;
	}

	public void setFormatedPurchaseDate(String formatedPurchaseDate) {
		this.formatedPurchaseDate = formatedPurchaseDate;
	}

	public String getFormatedInstallationDate() {
		return formatedInstallationDate;
	}

	public void setFormatedInstallationDate(String formatedInstallationDate) {
		this.formatedInstallationDate = formatedInstallationDate;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerCity() {
		return ownerCity;
	}

	public void setOwnerCity(String ownerCity) {
		this.ownerCity = ownerCity;
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

	public String getOwnerCountry() {
		return ownerCountry;
	}

	public void setOwnerCountry(String ownerCountry) {
		this.ownerCountry = ownerCountry;
	}

	public Boolean getIsOwnerInfoDisplayNeeded() {
		return isOwnerInfoDisplayNeeded;
	}

	public void setIsOwnerInfoDisplayNeeded(Boolean isOwnerInfoDisplayNeeded) {
		this.isOwnerInfoDisplayNeeded = isOwnerInfoDisplayNeeded;
	}

	public Boolean getShowEquipmentInfoSection() {
		return showEquipmentInfoSection;
	}

	public void setShowEquipmentInfoSection(Boolean showEquipmentInfoSection) {
		this.showEquipmentInfoSection = showEquipmentInfoSection;
	}

	public String getUnserializedItemProduct() {
		return unserializedItemProduct;
	}

	public void setUnserializedItemProduct(String unserializedItemProduct) {
		this.unserializedItemProduct = unserializedItemProduct;
	}

	public Boolean getShowServiceProcedureSection() {
		return showServiceProcedureSection;
	}

	public void setShowServiceProcedureSection(Boolean showServiceProcedureSection) {
		this.showServiceProcedureSection = showServiceProcedureSection;
	}

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

	public Boolean getIsOEMReplacedPartPresent() {
		return isOEMReplacedPartPresent;
	}

	public void setIsOEMReplacedPartPresent(Boolean isOEMReplacedPartPresent) {
		this.isOEMReplacedPartPresent = isOEMReplacedPartPresent;
	}

	public Boolean getIsNonOEMPartsSectionVisible() {
		return isNonOEMPartsSectionVisible;
	}

	public void setIsNonOEMPartsSectionVisible(Boolean isNonOEMPartsSectionVisible) {
		this.isNonOEMPartsSectionVisible = isNonOEMPartsSectionVisible;
	}

	public Boolean getBuPartReplacedByNonBUPart() {
		return buPartReplacedByNonBUPart;
	}

	public void setBuPartReplacedByNonBUPart(Boolean buPartReplacedByNonBUPart) {
		this.buPartReplacedByNonBUPart = buPartReplacedByNonBUPart;
	}

	public Boolean getIsPartReplacedInstalledSectionVisible() {
		return isPartReplacedInstalledSectionVisible;
	}

	public void setIsPartReplacedInstalledSectionVisible(
			Boolean isPartReplacedInstalledSectionVisible) {
		this.isPartReplacedInstalledSectionVisible = isPartReplacedInstalledSectionVisible;
	}

	public Boolean getIsRootCauseVisible() {
		return isRootCauseVisible;
	}

	public void setIsRootCauseVisible(Boolean isRootCauseVisible) {
		this.isRootCauseVisible = isRootCauseVisible;
	}

	public boolean isInternalUser() {
		return internalUser;
	}

	public void setInternalUser(boolean internalUser) {
		this.internalUser = internalUser;
	}

	public boolean isDisplayCPFlagOnClaimPgOne() {
		return displayCPFlagOnClaimPgOne;
	}

	public void setDisplayCPFlagOnClaimPgOne(boolean displayCPFlagOnClaimPgOne) {
		this.displayCPFlagOnClaimPgOne = displayCPFlagOnClaimPgOne;
	}

//	public boolean isBarcodeEnabled() {
//		return isBarcodeEnabled;
//	}
//
//	public void setBarcodeEnabled(boolean isBarcodeEnabled) {
//		this.isBarcodeEnabled = isBarcodeEnabled;
//	}

	public String getClaimProcessedAs() {
		return claimProcessedAs;
	}

	public void setClaimProcessedAs(String claimProcessedAs) {
		this.claimProcessedAs = claimProcessedAs;
	}

	public boolean isClaimAssigneeShownToDealer() {
		return isClaimAssigneeShownToDealer;
	}

	public void setClaimAssigneeShownToDealer(boolean isClaimAssigneeShownToDealer) {
		this.isClaimAssigneeShownToDealer = isClaimAssigneeShownToDealer;
	}

	public List<RecoveryCostLineObject> getRecoveryCostLineItems() {
		return recoveryCostLineItems;
	}

	public void setRecoveryCostLineItems(
			List<RecoveryCostLineObject> recoveryCostLineItems) {
		this.recoveryCostLineItems = recoveryCostLineItems;
	}

	public boolean isHasSupplierRole() {
		return hasSupplierRole;
	}

	public void setHasSupplierRole(boolean hasSupplierRole) {
		this.hasSupplierRole = hasSupplierRole;
	}

	public String getDatePatternForLoggedInUser() {
		return datePatternForLoggedInUser;
	}

	public void setDatePatternForLoggedInUser(String datePatternForLoggedInUser) {
		this.datePatternForLoggedInUser = datePatternForLoggedInUser;
	}

	public String getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(String loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	
	public Boolean getDealerJobNumberEnabled() {
		return isDealerJobNumberEnabled;
	}

	public void setDealerJobNumberEnabled(Boolean isDealerJobNumberEnabled) {
		this.isDealerJobNumberEnabled = isDealerJobNumberEnabled;
	}
	
	public String getFormattedRecoveryClaimDate() {
		return formattedRecoveryClaimDate;
	}

	public void setFormattedRecoveryClaimDate(String formattedRecoveryClaimDate) {
		this.formattedRecoveryClaimDate = formattedRecoveryClaimDate;
	}
	

	public String getFormatedRepairStartDate() {
		return formatedRepairStartDate;
	}

	public void setFormatedRepairStartDate(String formatedRepairStartDate) {
		this.formatedRepairStartDate = formatedRepairStartDate;
	}
	
	public boolean getShowMiscellaneousExpenseSection() {
		return showMiscellaneousExpenseSection;
	}

	public void setShowMiscellaneousExpenseSection(
			boolean showMiscellaneousExpenseSection) {
		this.showMiscellaneousExpenseSection = showMiscellaneousExpenseSection;
	}

    //	public Money getActualValueForMiscellenousExpension() {
    //		return actualValueForMiscellenousExpension;
    //	}
    //
    //	public void setActualValueForMiscellenousExpension(
    //			Money actualValueForMiscellenousExpension) {
    //		this.actualValueForMiscellenousExpension = actualValueForMiscellenousExpension;
    //	}
    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

	public List<ManageDocumentObject> getDocumentObjects() {
		return documentObjects;
	}

	public void setDocumentObjects(List<ManageDocumentObject> documentObjects) {
		this.documentObjects = documentObjects;
	}

	
	public String getFaultLocationNameWithCode() {
		return faultLocationNameWithCode;
	}

	public void setFaultLocationNameWithCode(String faultLocationNameWithCode) {
		this.faultLocationNameWithCode = faultLocationNameWithCode;
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

	

    
}

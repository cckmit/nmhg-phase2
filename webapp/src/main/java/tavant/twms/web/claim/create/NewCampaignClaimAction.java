package tavant.twms.web.claim.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignSectionPrice;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.claimsubmission.FieldModificationClaimSubmissionUtil;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.InstanceOfUtil;

@SuppressWarnings("serial")
public class NewCampaignClaimAction extends AbstractNewClaimAction {
	private CampaignClaim claim;

	private Campaign campaign;

	private CampaignAssignmentService campaignAssignmentService;

	private FailureStructureService failureStructureService;

	private String fromPendingCampaign;

	private List<Campaign> campaigns;

	private CampaignService campaignService;

	private List<ClaimedItem> tempList = new ArrayList<ClaimedItem>();

	private boolean multiSerialPerClaimAllowedFlag;

	private Boolean isThirdParty = false;

	private Boolean thirdPartyType;

	private String thirdPartyName;

	private boolean partsReplacedInstalledSectionVisible;

	private boolean buPartReplaceableByNonBUPart;

	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;

	private FieldModificationClaimSubmissionUtil fieldModificationClaimSubmissionUtil;

	private Boolean dealerNumberSelected;

	private CampaignNotification campaignNotification;
	
	private String serialNumberForCampaignCodes;

	public boolean isPartsReplacedInstalledSectionVisible() {
		return partsReplacedInstalledSectionVisible;
	}

	public void setFieldModificationClaimSubmissionUtil(
			FieldModificationClaimSubmissionUtil fieldModificationClaimSubmissionUtil) {
		this.fieldModificationClaimSubmissionUtil = fieldModificationClaimSubmissionUtil;
	}

	public void setPartsReplacedInstalledSectionVisible(
			boolean partsReplacedInstalledSectionVisible) {
		this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
	}

	public Boolean getThirdPartyType() {
		return thirdPartyType;
	}

	public void setThirdPartyType(Boolean thirdPartyType) {
		this.thirdPartyType = thirdPartyType;
	}

	public Boolean getIsThirdParty() {
		return isThirdParty;
	}

	public void setIsThirdParty(Boolean isThirdParty) {
		this.isThirdParty = isThirdParty;
	}

	public String initializeClaim() {
		return SUCCESS;
	}

	@SuppressWarnings("static-access")
	public String saveDraft() {
		
		if (getConfigParamService().getBooleanValue(
				ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName())) {
			if (null != claim.getSerialNumber())
				validateMarketingGroupCodes(claim);
			if (hasActionErrors())
				return INPUT;
		}
		partsReplacedInstalledSectionVisible = getConfigParamService()
				.getBooleanValue(
						ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
								.getName());
		buPartReplaceableByNonBUPart = getConfigParamService().getBooleanValue(
				ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
		if(claim!=null && claim.getBrand()==null)
        {
        	if(claim.getItemReference().getReferredInventoryItem()!=null)
        	{
        		claim.setBrand(claim.getItemReference().getReferredInventoryItem().getBrandType());
        	}
        }
		if (claim.getId() == null) {
			populateClaimFromCampaign(claim);
		}
		
		// Get the campaign notification, There should be one existing
		InventoryItem mInventoryItem = claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem(); // Get the first inventory item
		Campaign mCampaign = claim.getCampaign() != null ? claim.getCampaign() : campaign;
		CampaignNotification mCampaignNotification = campaignService.getNotificationForItemwithCampaign(mInventoryItem, mCampaign);
		
		// If already a claim exists, then show the error
		// Also we check if the claim exists, then it should be same as we have (This will be true if claim is in draft state)
		if (mCampaignNotification != null && mCampaignNotification.getClaim() != null
				&& (mCampaignNotification.getClaim().getId() != claim.getId())
				) {
			addActionError("error.fpiClaim.alreadyFiled");
			return INPUT;
		}
		
		String returnString = saveDraft(claim);
		if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class,
				claim.getForDealer())) {
			Boolean isThirdPartyLogin = orgService
					.isThirdPartyDealerWithLogin(claim.getForDealer().getId());
			if (getLoggedInUser().getBelongsToOrganization().isDealer()
					&& !(isThirdPartyLogin)) {
				isThirdParty = true;
			} else if (getLoggedInUser().getBelongsToOrganization().getName()
					.equalsIgnoreCase("OEM")
					&& !(isThirdPartyLogin)) {
				isThirdParty = true;
			}
		}
		if (NONE.equals(returnString)) {
			return NONE;
		} else {
			try {
				List<InventoryItem> items = new ArrayList<InventoryItem>();
				for (ClaimedItem claimedItem : claim.getClaimedItems()) {
					items.add(claimedItem.getItemReference()
							.getReferredInventoryItem());
				}
				if (claim.getId() != null && claim.getCampaign() != null) {
					campaignService.updateCampaignNotifications(items, claim,
							claim.getCampaign(),campaignNotification.PENDING);
				} else {
					campaignService.updateCampaignNotifications(items, claim,
							campaign,campaignNotification.PENDING);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return NONE;
			}
			return returnString;
		}
	}

	public CampaignClaim getClaim() {
		return claim;
	}

	public void setClaim(CampaignClaim claim) {
		this.claim = claim;
	}

	@Override
	public void validate() {
		super.validate();
		checkAuthNumber(getClaim());
		if(getClaim().getForDealer().getServiceProviderNumber() == null){
       	 addActionError("error.newClaim.selectDealer");
       }
		setActionErrors(fieldModificationClaimSubmissionUtil.validate(
				this.claim, this.campaign, tempList));
	}

	private boolean isPriceConfiguredForNonOemPartsOnCampaign(
			Campaign campaign, ServiceProvider forDealer) {
		Currency preferredCurrency = forDealer.getPreferredCurrency();
		for (NonOEMPartToReplace replace : campaign.getNonOEMpartsToReplace()) {
			boolean isPriceConfigured = false;
			for (CampaignSectionPrice campaignNonOem : replace
					.getCampaignSectionPrice()) {
				if (preferredCurrency.getCurrencyCode().equalsIgnoreCase(
						campaignNonOem.getPricePerUnit()
								.breachEncapsulationOfCurrency()
								.getCurrencyCode())) {
					isPriceConfigured = true;
					break;
				}

			}
			if (!isPriceConfigured)
				return false;
		}
		return true;
	}

	private void populateClaimFromCampaign(CampaignClaim claim) {
		ServiceInformation serviceInformation = new ServiceInformation();
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceInformation.setServiceDetail(serviceDetail);
		claim.setServiceInformation(serviceInformation);
		fieldModificationClaimSubmissionUtil.populateReplacedPartsFromCampaign(
				campaign, claim, partsReplacedInstalledSectionVisible,
				buPartReplaceableByNonBUPart);
		if (!serviceDetail.getNonOEMPartsReplaced().isEmpty()
				&& !isPriceConfiguredForNonOemPartsOnCampaign(campaign,
						claim.getForDealer())) {
			addActionWarning("message.campaign.nonoem.notConfigured");
		}
		fieldModificationClaimSubmissionUtil.populateMiscPartsFromCampaign(
				campaign, claim);
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			claimedItem.setClaim(claim);
		}
		setPolicyOnClaimedItems(claim);
		fieldModificationClaimSubmissionUtil.populateLaborDetailsFromCampaign(
				campaign, claim);
		fieldModificationClaimSubmissionUtil
				.populateMiscellaneousDetailsFromCampaign(campaign, claim);

		claim.setForDealerShip(claim.getForDealer());
		claim.setCampaign(campaign);
		User loggedInUser = getLoggedInUser();
		claim.setFiledBy(loggedInUser);
		claim.setLastModifiedBy(loggedInUser);
		Date currentDate=new Date();
		claim.setLastModifiedOn(currentDate);
		claim.setLastUpdatedBy(loggedInUser);
		claim.setLastUpdatedOnDate(currentDate);
	}

	@SuppressWarnings("unused")
	private List<InstalledParts> fetchNonHussmannInstalledParts() {
		List<NonOEMPartToReplace> partsToReplace = campaign
				.getNonOEMpartsToReplace();
		List<InstalledParts> partsReplaced = new ArrayList<InstalledParts>();
		for (NonOEMPartToReplace replace : partsToReplace) {
			InstalledParts nonHussmannPartInstalled = new InstalledParts();
			nonHussmannPartInstalled.setNumberOfUnits(replace.getNoOfUnits());
			nonHussmannPartInstalled.setDescription(replace.getDescription());
			nonHussmannPartInstalled.setPricePerUnit(replace.getPricePerUnit());
			partsReplaced.add(nonHussmannPartInstalled);
		}
		return partsReplaced;
	}

	private List<HussmanPartsReplacedInstalled> fetchHussmannPartsReplacedInstalled() {
		List<OEMPartToReplace> partsToReplace = campaign.getOemPartsToReplace();
		List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = new ArrayList<HussmanPartsReplacedInstalled>();
		List<OEMPartReplaced> partsReplaced;
		List<InstalledParts> hussmannInstalledPartsList;
		List<InstalledParts> nonHussmannInstalledPartsList;
		HussmanPartsReplacedInstalled hussReplacedInstalled = new HussmanPartsReplacedInstalled();
		for (OEMPartToReplace replace : partsToReplace) {
			partsReplaced = new ArrayList<OEMPartReplaced>();
			OEMPartReplaced oemPartReplaced = fetchHussmannReplacedParts(replace);
			partsReplaced.add(oemPartReplaced);
			hussReplacedInstalled.setReplacedParts(partsReplaced);
			hussmannInstalledPartsList = new ArrayList<InstalledParts>();
			InstalledParts hussmannInstalledParts = fetchHussmannInstalledParts(replace);
			hussmannInstalledPartsList.add(hussmannInstalledParts);
			hussReplacedInstalled
					.setHussmanInstalledParts(hussmannInstalledPartsList);
		}
		List<NonOEMPartToReplace> nonOemPartToReplace = campaign
				.getNonOEMpartsToReplace();
		for (NonOEMPartToReplace nonOemPart : nonOemPartToReplace) {
			InstalledParts installedNonHussmannPart = fetchNonHussmannInstalledParts(nonOemPart);
			nonHussmannInstalledPartsList = new ArrayList<InstalledParts>();
			nonHussmannInstalledPartsList.add(installedNonHussmannPart);
			hussReplacedInstalled
					.setNonHussmanInstalledParts(nonHussmannInstalledPartsList);
		}
		hussmanPartsReplacedInstalled.add(hussReplacedInstalled);
		return hussmanPartsReplacedInstalled;
	}

	private OEMPartReplaced fetchHussmannReplacedParts(
			OEMPartToReplace replacedPart) {
		OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
		oemPartReplaced.setNumberOfUnits(replacedPart.getNoOfUnits());
		oemPartReplaced.setItemReference(new ItemReference(replacedPart
				.getItem()));
		oemPartReplaced.setShippedByOem(replacedPart.isShippedByOem());
		return oemPartReplaced;
	}

	private InstalledParts fetchHussmannInstalledParts(
			OEMPartToReplace installedPart) {
		InstalledParts oemPartReplaced = new InstalledParts();
		oemPartReplaced.setNumberOfUnits(installedPart.getNoOfUnits());
		oemPartReplaced.setItem(installedPart.getItem());
		return oemPartReplaced;
	}

	private InstalledParts fetchNonHussmannInstalledParts(
			NonOEMPartToReplace nonOEMPartToReplace) {
		InstalledParts nonOEMInstalled = new InstalledParts();
		nonOEMInstalled.setPartNumber(nonOEMPartToReplace.getDescription());
		nonOEMInstalled.setDescription(nonOEMPartToReplace.getDescription());
		nonOEMInstalled.setNumberOfUnits(nonOEMPartToReplace.getNoOfUnits());
		nonOEMInstalled.setPricePerUnit(nonOEMPartToReplace.getPricePerUnit());
		return nonOEMInstalled;
	}

	public CampaignAssignmentService getCampaignAssignmentService() {
		return campaignAssignmentService;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public List<ClaimType> getClaimTypes() {
		List<ClaimType> tempClaimTypes = new ArrayList<ClaimType>();
		List<ClaimType> claimTypes = new ArrayList<ClaimType>();
		tempClaimTypes = getClaimService().fetchAllClaimTypesForBusinessUnit();
		claimTypes.add(ClaimType.CAMPAIGN);
		for (ClaimType claimType : tempClaimTypes) {
			if (!ClaimType.CAMPAIGN.getType().equals(claimType.getType())) {
				claimTypes.add(ClaimType.getUIDisplayName(claimType.getType()));
			}
		}

		if (thirdPartyType != null && thirdPartyType) {
			for (int i = 0; i < claimTypes.size(); i++) {
				if ((claimTypes.get(i)).getType().contains("Parts")) {
					claimTypes.remove(i);
				}
			}
		}
		return claimTypes;
	}

	public String getFromPendingCampaign() {
		return fromPendingCampaign;
	}

	public void setFromPendingCampaign(String fromPendingCampaign) {
		this.fromPendingCampaign = fromPendingCampaign;
	}

	public String getCampaignCodes() throws ItemNotFoundException {
		campaigns = campaignService.getCampaignsForInventoryItemById(serialNumberForCampaignCodes);
		return SUCCESS;
	}

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public String setForMultiCampaignClaim() {

		populateMultiSerialPerClaimSubmissionAllowedFlag();
		if (claim != null && claim.getClaimedItems().size() > 0)
			for (Iterator<ClaimedItem> iterator = claim.getClaimedItems()
					.iterator(); iterator.hasNext();) {
				ClaimedItem claimedItem = (ClaimedItem) iterator.next();
				if (claimedItem.getItemReference().getReferredInventoryItem() == null) {
					iterator.remove();
				}
			}
		return SUCCESS;
	}

	public String getEquipmentDetails() throws IOException {
		List<InventoryItem> selectedItems = getSelectedInventoryItems();
		selectedMultipleInventories(selectedItems, claim);
		if (wereIncompatibleInventoriesSelected(this.claim.getClaimedItems())) {
			return sendValidationResponse();
		} else {
			if (claim.getClaimedItems() != null
					&& claim.getClaimedItems().size() > 0) {
				claim.setForMultipleItems(true);
			}
			return SUCCESS;
		}
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public List<ClaimedItem> getTempList() {
		return tempList;
	}

	public void setTempList(List<ClaimedItem> tempList) {
		this.tempList = tempList;
	}

	/*
	 * This method checks the validation for Multiple Inventory Claim It checks
	 * whether all the selected Inventories are of same model. The model is set
	 * to 1st inventory model and then iterated through loop to check the
	 * consistency of the selections. Note: We *do not* necessarily validate all
	 * the items, because we stop the validation at the very first invalid item
	 * that we encounter.
	 */
	private boolean wereIncompatibleInventoriesSelected(
			List<ClaimedItem> claimedItems) {
		boolean incompatibleInventoriesSelected = false;

		if (claimedItems != null && claimedItems.size() > 1) {
			InventoryItem firstInventoryItem = claimedItems.get(0)
					.getItemReference().getReferredInventoryItem();
			String firstModelName = firstInventoryItem.getOfType().getModel()
					.getName();
			int claimedItemsCount = claimedItems.size();
			for (int i = 1; i < claimedItemsCount; i++) {
				ClaimedItem claimedItem = claimedItems.get(i);
				InventoryItem currentInventoryItem = claimedItem
						.getItemReference().getReferredInventoryItem();
				if (!firstModelName.equals(currentInventoryItem.getOfType()
						.getModel().getName())) {
					incompatibleInventoriesSelected = true;
					break;
				}
			}
		}

		return incompatibleInventoriesSelected;
	}

	public void populateMultiSerialPerClaimSubmissionAllowedFlag() {
		Boolean isMultiSerialPerClaimAllowed = getConfigParamService()
				.getBooleanValue("isMultipleSerialsPerClaimAllowed");
		if (isMultiSerialPerClaimAllowed != null) {
			this.multiSerialPerClaimAllowedFlag = isMultiSerialPerClaimAllowed
					.booleanValue();
		} else {
			this.multiSerialPerClaimAllowedFlag = false;
		}
	}

	public boolean isMultiSerialPerClaimAllowedFlag() {
		return multiSerialPerClaimAllowedFlag;
	}

	public void setMultiSerialPerClaimAllowedFlag(
			boolean multiSerialPerClaimAllowedFlag) {
		this.multiSerialPerClaimAllowedFlag = multiSerialPerClaimAllowedFlag;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public InventoryScrapTransactionXMLConverter getInventoryScrapTransactionXMLConverter() {
		return inventoryScrapTransactionXMLConverter;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
	}

	public Boolean getDealerNumberSelected() {
		return dealerNumberSelected;
	}

	public void setDealerNumberSelected(Boolean dealerNumberSelected) {
		this.dealerNumberSelected = dealerNumberSelected;
	}

	public CampaignNotification getCampaignNotification() {
		return campaignNotification;
	}

	public void setCampaignNotification(
			CampaignNotification campaignNotification) {
		this.campaignNotification = campaignNotification;
	}

	public boolean isBuPartReplaceableByNonBUPart() {
		return buPartReplaceableByNonBUPart;
	}

	public void setBuPartReplaceableByNonBUPart(
			boolean buPartReplaceableByNonBUPart) {
		this.buPartReplaceableByNonBUPart = buPartReplaceableByNonBUPart;
	}

	public String getSerialNumberForCampaignCodes() {
		return serialNumberForCampaignCodes;
	}

	public void setSerialNumberForCampaignCodes(String serialNumberForCampaignCodes) {
		this.serialNumberForCampaignCodes = serialNumberForCampaignCodes;
	}

	
}

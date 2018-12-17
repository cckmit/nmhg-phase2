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

package tavant.twms.web.claim.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.claimsubmission.MachineClaimSubmissionUtil;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;
import com.opensymphony.xwork2.Preparable;

/**
 * @author binil.thomas
 */
@SuppressWarnings("serial")

public abstract class NewSerializedClaimAction extends AbstractNewClaimAction implements Preparable {
    private final Logger logger = Logger.getLogger(NewSerializedClaimAction.class);

    private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
    
    private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;

    private AttributeAssociationService attributeAssociationService;

    private List<ClaimedItem> tempList = new ArrayList<ClaimedItem>();

    private Boolean isThirdParty = false;

    private Boolean thirdPartyType;

    private boolean partsReplacedInstalledSectionVisible;
    private String thirdPartyName;

    private InventoryItem inventoryItem;

    private Boolean dealerNumberSelected;

    private MachineClaimSubmissionUtil machineClaimSubmissionUtil;

    public abstract Claim getClaimDetail();
    
    public boolean isPartsReplacedInstalledSectionVisible() {
        return partsReplacedInstalledSectionVisible;
    }

    public void setMachineClaimSubmissionUtil(MachineClaimSubmissionUtil machineClaimSubmissionUtil) {
        this.machineClaimSubmissionUtil = machineClaimSubmissionUtil;
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

    @SuppressWarnings("deprecation")
    public void prepare() throws Exception {
        if (getClaimDetail() != null && !CollectionUtils.isEmpty(getClaimDetail().getClaimedItems())) {
            getClaimDetail().getClaimedItems().removeAll(Collections.singleton(null));
        }
        if (getClaimDetail() != null && getClaimDetail().getItemReference() != null) {
            if (getClaimDetail().getItemReference().getReferredInventoryItem() != null) {
                setSelectedBusinessUnit(getClaimDetail().getItemReference().getReferredInventoryItem().getBusinessUnitInfo()
                        .getName());
            } else if (getClaimDetail().getItemReference().getReferredItem() != null
                    && getClaimDetail().getItemReference().getReferredItem().getModel() != null) {
                setSelectedBusinessUnit(getClaimDetail().getItemReference().getReferredItem().getModel()
                        .getBusinessUnitInfo().getName());
            }
        }
    }

    public String saveDraft() {
		if (getConfigParamService().getBooleanValue(
				ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName())) {
			if (null != getClaimDetail().getSerialNumber()) 
					validateMarketingGroupCodes(getClaimDetail());
			if (hasActionErrors())
				return INPUT;
		}
		
		/*  SLMSPROD-1043 - Fix Start */
		
		if (getConfigParamService().getBooleanValue(
				ConfigName.VALIDATE_INV_PERMISSION_FOR_CLAIMS_FILED_BY_INTERNAL_USERS.getName())
				&& isLoggedInUserAnInternalUser()) {
			
			InventoryItem selectedInvItem = getClaimDetail().getForItem();
			ServiceProvider selectedDealer = getClaimDetail().getForDealer();
			
			// Following method takes care of both STOCK as well as RETAIL trucks
			Organization retailedDealer = selectedInvItem.getRetailedDealer();
			
			if (selectedDealer.getId().equals(retailedDealer.getId()) == false) {
				addActionError(getText("error.claim.invBelongsToAnotherDealer", 
						new String[] {selectedInvItem.getSerialNumber(), 
									  selectedDealer.getDisplayName()}));
				
				getClaimDetail().setForDealer(null); // reset so that can select again
				getClaimDetail().setItemReference(null); // reset so that can select again
				
				return INPUT;
			}
			
		}
		
		/*  SLMSPROD-1043 - Fix End */

        partsReplacedInstalledSectionVisible = getConfigParamService()
                .getBooleanValue(
                        ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
                                .getName());
        getClaimDetail().setForMultipleItems(getClaimDetail().getClaimedItems().size() > 1);

        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, getClaimDetail()
                .getForDealer())) {
            Boolean isThirdPartyLogin = orgService.isThirdPartyDealerWithLogin(getClaimDetail()
                    .getForDealer().getId());
            if (getLoggedInUser().getBelongsToOrganization().isDealer()
                    && !(isThirdPartyLogin)) {
                isThirdParty = true;
            } else if (getLoggedInUser().getBelongsToOrganization()
                    .getName().equalsIgnoreCase("OEM")
                    && !(isThirdPartyLogin)) {
                isThirdParty = true;
            }
        }

        for (ClaimedItem claimedItem : getClaimDetail().getClaimedItems()) {
            claimedItem.setClaim(getClaimDetail());
        }
        if ((!StringUtils.hasText(getForSerialized()) || TRUE.equals(getForSerialized())) && !isEligibleForClaim()) {
            return NONE;
        }
        if(getClaimDetail().getBrand()==null)
        {
        	if(getClaimDetail().getItemReference().getReferredInventoryItem()!=null)
        	{
        		getClaimDetail().setBrand(getClaimDetail().getItemReference().getReferredInventoryItem().getBrandType());
        	}
        }
        return saveDraft(getClaimDetail());
    }

    @Override
    public void validate() {
        super.validate();
        checkAuthNumber(getClaimDetail());
        validateNCRWith30DaysForDealer(getClaimDetail());
        if(getClaimDetail().getForDealer().getServiceProviderNumber()==null){
        	 addActionError("error.newClaim.selectDealer");
        }
        setActionErrors(machineClaimSubmissionUtil.validateMachineClaim(getClaimDetail(), getFieldErrors(),
                tempList, getForSerialized()));
    }
   
    private void validateNCRWith30DaysForDealer(Claim claim){
    	if(isLoggedInUserADealer() && claim != null && claim.isNcrWith30Days()){
    		if (true == getAllowed30DayNcrClasses().isEmpty()) {
    			addActionError("error.ncrClaim.unAuthorizedDealer");
    		}
    	}
    }
    
    public InventoryItem getInventoryItemForNonSerializedSerialNumber(String serialNumber, String model) {
        try {
            return getInventoryService().findSerializedItem(serialNumber, model);
        } catch (ItemNotFoundException exception) {
            this.logger.error("No inventory item exists for the given serial number [" + serialNumber + "] and" +
                    " model [" + model + "]");
            return null;
        }
    }

    public void setInventoryScrapTransactionXMLConverter(
            InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
        this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
    }

    public void setInventoryStolenTransactionXMLConverter(
            InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter) {
        this.inventoryStolenTransactionXMLConverter = inventoryStolenTransactionXMLConverter;
    }
    /*
     * If the selected inventories in a multiClaim are of different types the
     * the response is directly set over here else it displays the normal
     * equipment page
     */
    public String getEquipmentDetails() throws IOException {
        List<InventoryItem> selectedItems = getSelectedInventoryItems();
        selectedMultipleInventories(selectedItems, getClaimDetail());
        if (wereIncompatibleInventoriesSelected(getClaimDetail().getClaimedItems())) {
            return sendValidationResponse();
        } else {
            if (getClaimDetail().getClaimedItems() != null && getClaimDetail().getClaimedItems().size() > 0) {
                getClaimDetail().setForMultipleItems(true);
            }
            return SUCCESS;
        }
    }


    /*
     * This method checks the validation for Multiple Inventory Claim It checks
     * whether all the selected Inventories are of same model,year and belongs
     * to the same end customer. The three parameters of the 1st inventory is
     * set to different variables and the iterated through loop to check the
     * consistency of the selections. Note: We *do not* necessarily validate all
     * the items, because we stop the validation at the very first invalid item
     * that we encounter.
     */
    private boolean wereIncompatibleInventoriesSelected(List<ClaimedItem> claimedItems) {
        boolean incompatibleInventoriesSelected = false;

        if (claimedItems != null && claimedItems.size() > 1) {
            long firstEndCustomerId = -1;
            InventoryItem firstInventoryItem = claimedItems.get(0).getItemReference().getReferredInventoryItem();
            List<InventoryTransaction> txHistory = firstInventoryItem.getTransactionHistory();
            Collections.sort(txHistory);
            String firstModelName = firstInventoryItem.getOfType().getModel().getName();
            CalendarInterval firstShipmentYear = firstInventoryItem.getShipmentDate().year();
            firstEndCustomerId = txHistory.get(0).getBuyer().getId();
            int claimedItemsCount = claimedItems.size();

            for (int i = 1; i < claimedItemsCount; i++) {
                ClaimedItem claimedItem = claimedItems.get(i);
                InventoryItem currentInventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
                List<InventoryTransaction> currentItemTranx = currentInventoryItem.getTransactionHistory();
                Collections.sort(currentItemTranx);
                if (firstEndCustomerId != -1) {
                    if (!firstModelName.equals(currentInventoryItem.getOfType().getModel().getName())
                            || !firstShipmentYear.equals(currentInventoryItem.getShipmentDate().year())
                            || firstEndCustomerId != currentItemTranx.get(0).getBuyer().getId()) {
                        incompatibleInventoriesSelected = true;
                        break;
                    }
                }
            }
        }

        return incompatibleInventoriesSelected;
    }

    public List<String> getInventoryTypes() {
        List<String> inventoryTypes = new ArrayList<String>();
        inventoryTypes.add(InventoryType.STOCK.getType());
        inventoryTypes.add(InventoryType.RETAIL.getType());
        return inventoryTypes;
    }

    private boolean isEligibleForClaim() {
        for (ClaimedItem claimedItem : getClaimDetail().getClaimedItems()) {
            InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
            CalendarDate scrapDate = null;
            CalendarDate unScrapDate = null;
            CalendarDate stolenDate = null;
            CalendarDate unStolenDate = null;
            if (inventoryItem != null && inventoryItem.getInventoryItemAttrVals() != null) {
                if (inventoryItem.getInventoryItemAttrVals().size() > 0) {
                    for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem.getInventoryItemAttrVals()) {
                        if (AttributeConstants.SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
                            InventoryScrapTransaction scrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
                                    .convertXMLToObject(inventoryItemAttrVal.getValue());
                            scrapDate = scrap.getDateOfScrapOrUnscrap();
                        }
                        if (AttributeConstants.UN_SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
                            InventoryScrapTransaction unScrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
                                    .convertXMLToObject(inventoryItemAttrVal.getValue());
                            unScrapDate = unScrap.getDateOfScrapOrUnscrap();
                        }
                        if (scrapDate != null && unScrapDate != null) {
                            if ((claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem.getClaim().getRepairDate().equals(scrapDate))
                                    && (claimedItem.getClaim().getRepairDate().isBefore(unScrapDate) || claimedItem.getClaim().getRepairDate().equals(unScrapDate))
                                    && !scrapDate.equals(unScrapDate)) {
                                addActionError("message.scrap.machineScrapped", inventoryItem.getSerialNumber());
                                return false;
                            } else {
                                scrapDate = null;
                                unScrapDate = null;
                            }
                        }

                        if (AttributeConstants.STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
                            InventoryStolenTransaction stolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
                                    .convertXMLToObject(inventoryItemAttrVal.getValue());
                            stolenDate = stolen.getDateOfStolenOrUnstolen();
                        }
                        if (AttributeConstants.UN_STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
                            InventoryStolenTransaction unStolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
                                    .convertXMLToObject(inventoryItemAttrVal.getValue());
                            unStolenDate = unStolen.getDateOfStolenOrUnstolen();
                        }
                        if (stolenDate != null && unStolenDate != null) {
                            if ((claimedItem.getClaim().getRepairDate().isAfter(stolenDate) || claimedItem.getClaim().getRepairDate().equals(stolenDate))
                                    && (claimedItem.getClaim().getRepairDate().isBefore(unStolenDate) || claimedItem.getClaim().getRepairDate().equals(unStolenDate))
                                    && !stolenDate.equals(unStolenDate)) {
                                addActionError("message.stole.machineStolen", inventoryItem.getSerialNumber());
                                return false;
                            } else {
                                stolenDate = null;
                                unStolenDate = null;
                            }
                        }
                    
                    }
                    if (scrapDate != null && unScrapDate == null
                            && (claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem.getClaim().getRepairDate().equals(scrapDate))) {
                        addActionError("message.scrap.machineScrapped", inventoryItem.getSerialNumber());
                        addActionError("message.scrap.claim", scrapDate.toString("MM/dd/yyyy"));
                        return false;
                    }
                    if (stolenDate != null && unStolenDate == null
                            && (claimedItem.getClaim().getRepairDate().isAfter(stolenDate) || claimedItem.getClaim().getRepairDate().equals(stolenDate))) {
                        addActionError("message.stole.machineStolen", inventoryItem.getSerialNumber());
                        addActionError("message.stole.claim", stolenDate.toString("MM/dd/yyyy"));
                        return false;
                    }
                }
                
            }
        }
        return true;
    }

    public void setAttributeAssociationService(AttributeAssociationService attributeAssociationService) {
        this.attributeAssociationService = attributeAssociationService;
    }

    @SuppressWarnings("deprecation")
    public List<ClaimType> getClaimTypes() {
        List<ClaimType> tempClaimTypes = new ArrayList<ClaimType>();
        List<ClaimType> claimTypes = new ArrayList<ClaimType>();
        if ((getClaimDetail() != null && getClaimDetail().getItemReference() != null && getClaimDetail().getItemReference().getReferredInventoryItem() == null)
                && this.inventoryItem != null) {
            getClaimDetail().getItemReference().setReferredInventoryItem(this.inventoryItem);
        }
        if (getClaimDetail() != null && getClaimDetail().getItemReference() != null &&
                getClaimDetail().getItemReference().getReferredInventoryItem() != null) {
            InventoryItem invItem = getClaimDetail().getItemReference().getReferredInventoryItem();
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(invItem.getBusinessUnitInfo().getName());
            setSelectedBusinessUnit(invItem.getBusinessUnitInfo().getName());
        }

        tempClaimTypes = getClaimService().fetchAllClaimTypesForBusinessUnit();
        claimTypes.add(ClaimType.MACHINE);
        for (ClaimType claimType : tempClaimTypes) {
            if (!ClaimType.MACHINE.getType().equals(claimType.getType())) {
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

    public boolean isItemNumberDisplayRequired() {
        return getConfigParamService().getBooleanValue(ConfigName.IS_ITEM_NUMBER_DISPLAY_REQUIRED
                .getName());
    }

    public List<ClaimedItem> getTempList() {
        return tempList;
    }

    public void setTempList(List<ClaimedItem> tempList) {
        this.tempList = tempList;
    }

    public String setClaimBusinessUnitInfo() {
        return SUCCESS;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Boolean getDealerNumberSelected() {
        return dealerNumberSelected;
    }

    public void setDealerNumberSelected(Boolean dealerNumberSelected) {
        this.dealerNumberSelected = dealerNumberSelected;
    }
    
}

package tavant.twms.web.claim.create;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.catalog.MiscItemRate;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.alarmcode.AlarmCodeService;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.claimsubmission.GoogleMapDistanceCalculater;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemSource;
import tavant.twms.domain.inventory.InventoryItemUtil;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import org.apache.commons.lang.math.NumberUtils;

@SuppressWarnings("serial")
public class ClaimFormListingAction extends I18nActionSupport {
	private InventoryService inventoryService;
	private CatalogService catalogService;
	private ClaimService claimService;
	private FailureStructureService failureStructureService;
	private ContractService contractService;
	private CampaignService campaignService;
	private AlarmCodeService alarmCodeService;
	private InventoryItemUtil inventoryItemUtil;

	private static final Logger logger = Logger.getLogger(ClaimFormListingAction.class.getName());

	private String jsonString;
	private String number;
	private String thirdPartyName;

	private String faultFound;
	private Organization forDealer;
	private String causalPart;
	private String dealerName;
	private String contractId;
	private String claimNumber;
	private String  servicingLocationAddress;
	private String  travelLocationAddress;
	
	private ConfigParamService configParamService;

	private AttributeAssociationService attributeAssociationService;

	List<FailureCauseDefinition> possibleCauses;

	private static JSONArray EMPTY_OEM_DETAIL;
	private static JSONArray EMPTY_TRANSPORTATION_DETAIL;

	private MiscellaneousItemConfigService miscellaneousItemConfigService;
	private DealershipRepository dealershipRepository;

	private String selectedBusinessUnit;

	private static final String CASUAL_PART_ITEM_TYPE = "causalpart";

	private static final String REPLACED_PART_ITEM_TYPE = "replacedpart";

	private static final String PARTS_CLAIM_ITEM_TYPE = "partsClaimItemType";

	private static final String CLAIM_ID = "claimId";

	private String modelName;
	private CalendarDate claimRepairDate;
	private CalendarDate purchaseDate;
	private CalendarDate installationDate;
	private UomMappingsService uomMappingsService;
    private WarehouseService warehouseService;
    private InventoryItem claimedItem;
    private InventoryItem serializedPart;
    private boolean isPartsClaim;
    private String claimId;
    private String claimBrand;
    private boolean activeParts;
    private ClaimSubmissionUtil claimSubmissionUtil;
    private boolean dealerBrandsOnly = false;

    private Claim claim;

    public boolean isDealerBrandsOnly() {
        return dealerBrandsOnly;
    }

    public void setDealerBrandsOnly(boolean dealerBrandsOnly) {
        this.dealerBrandsOnly = dealerBrandsOnly;
    }

    private String claimType;

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    static {
		EMPTY_OEM_DETAIL = new JSONArray();
		EMPTY_OEM_DETAIL.put("-");
		EMPTY_OEM_DETAIL.put("-");
		EMPTY_OEM_DETAIL.put("-");
		EMPTY_OEM_DETAIL.put("-");
		EMPTY_OEM_DETAIL.put("-");
	}
    
    static {
 		EMPTY_TRANSPORTATION_DETAIL = new JSONArray();
 		EMPTY_TRANSPORTATION_DETAIL.put(BigDecimal.ZERO);
 		
 	}

    public String listSerialNumbers() {
        return getSerialNumbersStartingWith(getSearchPrefix());
    }

    public String listSerialNumbersForCampaign() {
    	String dealerNumber = null;
    	if(isLoggedInUserADealer()){
    		dealerNumber = ((ServiceProvider)getLoggedInUser().getBelongsToOrganization()).getServiceProviderNumber();
    		return getSerialNumbersStartingWithForCampaign(getSearchPrefix(),dealerNumber);
    	}
    	else{
    		return getSerialNumbersStartingWithForCampaignForAdmin(getSearchPrefix());
    	}
    }

    public String listCampaignsForDealer() {
        return getAllCampaignsForDealer(getSearchPrefix());
    }

    public String listItemNumbers() {
        return  getItemNumbersStartingWith(getSearchPrefix());
    }

    public String listModelNumbers() {
        return getModelNumbersStartingWith(getSearchPrefix());
    }

    public String listDealers() {
        return getDealersWhoseNameStartingWith(getSearchPrefix());
    }

    public String listDealerNumbers(){
    	return getDealersWhoseNumberStartingWith(getSearchPrefix());
    }

    public String listDealerNumbersWithValueDealerName(){
    	return getDealersWithValueDealerNameStartingWith(getSearchPrefix());
    }

    String getDealersWhoseNameStartingWith(String prefix) {
    	List<ServiceProvider> dealers = orgService.findDealersWhoseNameStartsWith(getSearchPrefix(), 0, 10);
        return generateAndWriteComboboxJson(dealers,"id","name");
    }

    @SuppressWarnings("null")
    String getDealersWhoseNumberStartingWith(String prefix) {
    	List<ServiceProvider> dealers = orgService.findDealersWhoseNumberStartingWith(getSearchPrefix(), 0, 10);
        return generateAndWriteComboboxJson(dealers,"id","dealerNumber");
    }

    String getDealersWithValueDealerNameStartingWith(String prefix) {
        List<ServiceProvider>  dealers = orgService.findDealersWhoseNumberStartingWith(getSearchPrefix(), 0, 10);
        return generateAndWriteComboboxJson(dealers,"name","dealerNumber");
    }

    String getSerialNumbersStartingWith(String prefix) {

        //If Claim type is parts, Just send the item type as Machine for serial number search.
        String itemType = ClaimType.PARTS.getType().equals(claimType) ? "MACHINE" : claimType;
        if (StringUtils.hasText(prefix)) {
            if (getLoggedInUsersOrganization().isNationalAccount()) {
                Long orgId = getLoggedInUser().getBelongsToOrganization().getId();
                List<InventoryItem> items =
                        inventoryService.findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(prefix.toUpperCase(), claimType, 0, 10, orgId);
                return generateAndWriteComboboxJsonForSerialNumber(items, "id", "serialNumber");
            } else {
                List<InventoryItem> items;
                List<String> brands = new ArrayList<String>();
                if(isDealerBrandsOnly()){
                    brands = getListOfSupportedBrands();
                }else{
                    brands = getAllBrandTypes();
                }
                if(brands==null ||brands.isEmpty())
                {
                  items=inventoryService.findInventoryItemsWhoseSerialNumbersStartWith(prefix.toUpperCase(), itemType, 0, 10);
                }
                else
                {
                	items=inventoryService.findInventoryItemsWhoseSerialNumbersStartWithAndBrand(prefix.toUpperCase(), itemType, 0, 10,brands);
                }
                if (isLoggedInUserADealer() || isLoggedInUserAnEnterpriseDealer()|| isLoggedInUserAnAdmin()) {
                    Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
                            .getKeyValuePairOfObjects(ConfigName.WARRANTY_CONFIG_CUSTOMER_TYPES_ALLOWED_IN_QUICK_SEARCH
                                    .getName());
                    for (Iterator<InventoryItem> iterator = items.iterator(); iterator.hasNext(); ) {
                        InventoryItem item = iterator.next();
                        if (!InventoryItemCondition.CONSIGNMENT.equals(item.getConditionType())
                                && (InventoryType.STOCK.equals(item.getType())||InventoryType.RETAIL.equals(item.getType()))) {
                            if(isLoggedInUserAnAdmin()){
                                if(!dealerName.equals(item.getCurrentOwner().getId().toString()))
                                    iterator.remove();
                            }
                            else{
                                if (!claimSubmissionUtil.hasUserPermissionOnInventory(item)) {
                                    if (!inventoryService.isInvItemOwnedByAllowedCustomerType(item, keyValueOfCustomerTypes)) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                }
                return generateAndWriteComboboxJsonForSerialNumber(items, "id", "serialNumber");
            }
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
    }

    //Config param check to display all brand types
    private boolean showAllBrands(){
        return configParamService.getBooleanValue(ConfigName.DISPLAY_ALL_BRANDS.getName());
    }

    //Getting all bran types for dealer specific for emea it's the claim brand only
    private List<String> getListOfSupportedBrands(){
        List<String> brands = new ArrayList<String>();
        if(StringUtils.hasText(this.claimBrand)){
            brands.add(this.claimBrand);
        }
        else if(forDealer!=null)
        {
            brands.add(((Dealership)forDealer).getBrand());
            brands.add(BrandType.UTILEV.getType());
        }
        return brands;
    }

    //Get all the brands -- claim page 2 will be using this
    //whola how to get its claim page 2 or not -- added parameter isdealerbrand only -- add only on page 1 things solved.
    public List<String> getAllBrandTypes(){
        List<String> brandTypes = new ArrayList<String>();
        if(showAllBrands()){
            for(BrandType brandType : BrandType.values()){
                brandTypes.add(brandType.getType());
            }

        }else if(StringUtils.hasText(this.claimBrand)){
            brandTypes.add(this.claimBrand);
        }

        if(brandTypes.size()==0 && this.claim != null && this.claim.getBrand() != null){
            brandTypes.add(this.claim.getBrand());
        }

        return brandTypes;
    }

    public String getSerializedPartsForPartsClaim(String prefix) {
    	 if (StringUtils.hasText(prefix)) {
    		 List<InventoryItem> inventoryItems;
    		 if(claimBrand==null || claimBrand.isEmpty())
    		 {
             inventoryItems =
            	 inventoryService.findAllSerializedSerialNumbersStartingWith(prefix.toUpperCase(),InventoryItemSource.MAJORCOMPREGISTRATION, 0, 10);
    		 }
    		 else
    		 {
    		inventoryItems = inventoryService.findAllSerializedSerialNumbersStartingWithAndBrand(prefix.toUpperCase(), 0, 10,claimBrand);
    		 }
          return generateAndWriteComboboxJsonForSerialNumber(inventoryItems,"id","serialNumber");
         } else {
             return generateAndWriteEmptyComboboxJson();
         }
    }

	public String listSerializedReplacedPart() {
        String searchPrefix = getSearchPrefix();
        if (StringUtils.hasText(searchPrefix)) {
			if (isPartsClaim && serializedPart != null) {
				if (claimedItem != null) {
					List<InventoryItem> inventoryItems = inventoryService
							.findInventoryItemCompositionForInvItem(
                                    searchPrefix, claimedItem.getId());
					return generateAndWriteComboboxJsonForSerialNumber(
							inventoryItems, "id", "serialNumber");
				} else {
					List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
					inventoryItems.add(serializedPart);
					return generateAndWriteComboboxJsonForSerialNumber(
							inventoryItems, "id", "serialNumber");
				}
			} else if (!isPartsClaim && claimedItem != null) {
				List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
				logger.debug("Claim ID:" + claimId);
				try {
					claim = claimService.findClaim(Long.parseLong(claimId));
				} catch (NumberFormatException ex) {
					logger.error("Error while generating JSON", ex);
				}

				// Find the list of parts (union of all parts on the BOM of all
				// selected serial numbers).
				if (claim != null && claim.getClaimedItems() != null)
					for (ClaimedItem claimedItem : claim.getClaimedItems()) {
						List<InventoryItemComposition> inventoryItemCompositionList = claimedItem
								.getItemReference().getReferredInventoryItem()
								.getComposedOf();
						for (InventoryItemComposition inventoryItemComposition : inventoryItemCompositionList) {
                            InventoryItem inventoryItemCompositionPart = inventoryItemComposition.getPart();
                            if (inventoryItemCompositionPart.getSerialNumber()
                                    .toLowerCase().startsWith(searchPrefix.toLowerCase())) {
                                inventoryItems.add(inventoryItemCompositionPart);
                            }
						} // End inventoryItem composition loop.
					} // End claimedItems loop.
				return generateAndWriteComboboxJsonForSerialNumber(
						inventoryItems, "id", "serialNumber");
			}
		}
		return generateAndWriteEmptyComboboxJson();
	}

    String getSerialNumbersStartingWithForCampaign(String prefix, String dealerNumber) {
        if (StringUtils.hasText(prefix)) {
            List<InventoryItem> inventoryItems =
            	campaignService.findInvItemsStartWithForCampaignClaims(prefix.toUpperCase(), dealerNumber);
            return generateAndWriteComboboxJsonForSerialNumber(inventoryItems,"id","serialNumber");
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
    }

    String getSerialNumbersStartingWithForCampaignForAdmin(String prefix) {
        if (StringUtils.hasText(prefix)) {
            List<InventoryItem> inventoryItems =
            	campaignService.findInvItemsStartWithForCampaignClaimsForAdmin(prefix.toUpperCase());
            return generateAndWriteComboboxJsonForSerialNumber(inventoryItems,"id","serialNumber");
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
    }

    String getAllCampaignsForDealer(String prefix) {
        if (StringUtils.hasText(prefix)) {
      	    List<Campaign> campaigns = campaignService.getCampaignsByCode(prefix.toUpperCase());
            return generateAndWriteComboboxJson(campaigns,"id","code");
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
    }

    String getItemNumbersStartingWith(String prefix) {
        if (StringUtils.hasText(prefix)) {
            List<Item> items = catalogService.findItemsWhoseNumbersStartWith(prefix.toUpperCase(), 0, 10);
            return generateAndWriteComboboxJson(items, "number");
       } else {
            return generateAndWriteEmptyComboboxJson();
       }
    }

    String getModelNumbersStartingWith(String prefix) {
        List<ItemGroup> models = new ArrayList<ItemGroup>();
        if (StringUtils.hasText(prefix)) {
            models = catalogService.findModelsWhoseNumbersStartWith(prefix.toUpperCase(), 0, 10);
        }
        return generateAndWriteComboboxJson(models, "id","name");
    }

    String getOemPartItemNumbersStartingWith(String prefix, String configParamType, boolean onlyServicePart) {
        if (StringUtils.hasText(prefix)) {
            List<String> brands;

            if(isDealerBrandsOnly()){
                brands = getListOfSupportedBrands();
            }else{
                brands = getAllBrandTypes();
            }

        	if(brands != null && !brands.isEmpty())
        	{
        		List<BrandItem>brandItems=getOEMPartBrandItemNumbers(prefix, configParamType, onlyServicePart, claimBrand);
        		return generateAndWriteComboboxJsonForBrandItem(brandItems);
        	}
        	else
        	{
        	 List<Item> items =getOEMPartItemNumbers(prefix,configParamType, onlyServicePart);
            return generateAndWriteComboboxJsonForItem(items,"alternateNumber");
        	}
        }else{
            return generateAndWriteEmptyComboboxJson();
        }
    }

    String getOemPartItemNumbersStartingWith(String prefix, String configParamType,String keyProperty) {
        if (StringUtils.hasText(prefix)) {
        	 List<Item> items =getOEMPartItemNumbers(prefix,configParamType, false);
            return writeJsonResponse(generateComboboxJson(items,keyProperty,"alternateNumber"));
        }else{
            return generateAndWriteEmptyComboboxJson();
        }
    }

	List<Item> getOEMPartItemNumbers(String prefix, String configParamType, boolean onlyServicePart) {
		List<Item> items = new ArrayList<Item>();
		{
			List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);
			if (items == null || items.isEmpty()) {
				items = catalogService.fetchManufParts(selectedBusinessUnit, prefix.toUpperCase(), 10, itemGroups, onlyServicePart);
			}
		}
		return items;
	}

	List<BrandItem> getOEMPartBrandItemNumbers(String prefix, String configParamType, boolean onlyServicePart,String brand) {
		List<BrandItem> brandItems = new ArrayList<BrandItem>();

        List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);
        List<String> brands = new ArrayList<String>();
        if(isDealerBrandsOnly()){
           brands = getListOfSupportedBrands();
        }else{
            brands = getAllBrandTypes();
        }
        brandItems = catalogService.fetchManufBrandParts(selectedBusinessUnit, prefix.toUpperCase(), 10, itemGroups, onlyServicePart,brands, activeParts);

        return brandItems;
	}

    public String listCausalPartNos() {
        List<Item> possibleParts = new ArrayList<Item>();
        List<Object> itemGroups = getItemGroupsFromBUConfig(CASUAL_PART_ITEM_TYPE);
        List<String> brands = new ArrayList<String>();
        if(isDealerBrandsOnly()){
            brands = getListOfSupportedBrands();
        }else{
            brands = getAllBrandTypes();
        }
        if(brands!=null &&!brands.isEmpty())
        {
            List<BrandItem> possibleBrandParts = new ArrayList<BrandItem>();
            /*boolean isADualDealer = false;
            if(isLoggedInUserADealer()){
                isADualDealer = orgService.checkLoggedInDealerForDualBrand(getLoggedInUsersDealership().getId())==null?false:true;
            }else{
                isADualDealer = orgService.checkLoggedInDealerForDualBrand(this.claim.getForDealer().getId())==null?false:true;
            }*/


            possibleBrandParts = catalogService.fetchManufBrandParts(selectedBusinessUnit,getSearchPrefix(),10, itemGroups, false, brands, activeParts);
            return generateAndWriteComboboxJsonForBrandItem(possibleBrandParts);
        }
        else
        {
        possibleParts = catalogService.fetchManufParts(selectedBusinessUnit,getSearchPrefix(),10, itemGroups, false);
        return generateAndWriteComboboxJsonForItem(possibleParts,"alternateNumber");
        }
    }

    private String generateAndWriteComboboxJsonForBrandItem(List<BrandItem> matchingObjects) {
        return writeJsonResponse(generateComboboxJsonForBrandItem(matchingObjects));
    }

    private String generateComboboxJsonForBrandItem(List<BrandItem> matchingObjects) {

        JSONArray itemsArray = new JSONArray();

        try {
            List<Long> brandItemNumbers = new ArrayList<Long>();
            List<String> duplicateBrandItemNumbers = new ArrayList<String>();
            for (BrandItem matchingObject : matchingObjects) {
                brandItemNumbers.add(matchingObject.getId());
            }

            for (BrandItem matchingObject : matchingObjects) {

                String brandItemNumber = matchingObject.getItemNumber();
                String brandId = String.valueOf(matchingObject.getId());

                int count = CollectionUtils.cardinality(matchingObject.getId(), brandItemNumbers);
                if (count == 1) {
                    addToJsonArray(itemsArray, brandItemNumber, brandId);
                    continue;
                }
                if (!duplicateBrandItemNumbers.contains(brandItemNumber)) {
                    // count > 1.. duplicates found

                    // If there are two NMHG part numbers associated with a brand part number always use the active part number.
                    // If both are active use the NMHG part that has both brand part numbers associated with it.

                    // Step 1 - Identify duplicate Brand Items
                    List<BrandItem> duplicates = new ArrayList<BrandItem>();
                    for (BrandItem brandItem : matchingObjects) {
                        if (brandItemNumber.equals(brandItem.getItemNumber())) {
                            duplicates.add(brandItem);
                        }
                    }

                    // Step 2 - Check if only one of them is ACTIVE. In that case use that.
                    int activeCount = 0;
                    BrandItem brandItem = null;
                    for (BrandItem duplicate : duplicates) {
                        if ("ACTIVE".equals(duplicate.getItem().getStatus())) {
                            brandItem = duplicate;
                            ++activeCount;
                        }
                    }

                    if (activeCount != 1) {
                        // More that one matching NMHG part is active.
                        brandItem = null;

                        // Step 3 - Find the NMHG part that has both brand part numbers associated with it.
                        if (activeCount > 1) {
                            int countOfNmhgPartWithBothBrandPartNumbers = 0;
                            for (BrandItem duplicate : duplicates) {
                                if ("ACTIVE".equals(duplicate.getItem().getStatus())) {
                                    if (catalogService.findById(duplicate.getItem().getId()).getBrandItems().size() > 1) {
                                        ++countOfNmhgPartWithBothBrandPartNumbers;
                                        brandItem = duplicate;
                                    }
                                }
                            }
                            if (countOfNmhgPartWithBothBrandPartNumbers > 1) {
                                // More than one NMHG part that has both brand part numbers associated with it.
                                brandItem = null;
                            }
                        }
                    }

                    if (brandItem == null) {
                        // We have reached here.. which means one of the following is TRUE
                        // a) All parts are INACTIVE
                        // b) More than one part is ACTIVE and they have more than one brand part number associated with it

                        // Step 4 - Find the brand part number that matches the NMHG part number
                        for (BrandItem duplicate : duplicates) {
                            if (activeCount == 0 || "ACTIVE".equals(duplicate.getItem().getStatus())) {
                                if (duplicate.getItemNumber().equals(duplicate.getItem().getNumber())) {
                                    brandItem = duplicate;
                                }
                            }
                        }
                    }

                    if (brandItem != null) {
                        brandItemNumber = brandItem.getItemNumber();
                        //itemNumber = brandItem.getItem().getNumber();
                        duplicateBrandItemNumbers.add(brandItemNumber);
                        addToJsonArray(itemsArray, brandItemNumber, brandId);
                    } else {
                        if (activeCount == 0 || "ACTIVE".equals(matchingObject.getItem().getStatus())) {
                            // Couldn't resolve duplicate. Display the brand item number along with the description
                            brandItemNumber = brandItemNumber + "(" + matchingObject.getItem().getDescription() + ")";
                            addToJsonArray(itemsArray, brandItemNumber, brandId);
                        }
                    }
                }
            }

            JSONObject comboJson = new JSONObject();
            comboJson.put("identifier", "key");
            comboJson.put("items", itemsArray);

            return comboJson.toString();
        } catch (Exception e) {
            logger.error("Exception while json'ifying combobox data :", e);
            throw new RuntimeException("Exception while json'ifying combobox data :", e);
        }
    }

    private void addToJsonArray(JSONArray itemsArray, String brandItemNumber, String itemNumber) throws JSONException {
        itemsArray.put(
                new JSONObject()
                        .put("key", itemNumber)
                        .put("label", brandItemNumber));
    }

    @SuppressWarnings("unchecked")
	public String listCausedBy() {
        List<FailureCauseDefinition> possibleCauses = failureStructureService.findCausedByOptionsById(number, faultFound);
        Collections.sort(possibleCauses,new Comparator()
        {
            public int compare(Object obj1 , Object obj2){
                FailureCauseDefinition failure1 = (FailureCauseDefinition)obj1;
                FailureCauseDefinition failure2 = (FailureCauseDefinition)obj2;
                return failure1.getDescription().compareTo(failure2.getDescription());
            }
        });
        return generateAndWriteComboboxJson(possibleCauses,"id","name");
    }


    public String listRootCause() {
        List<FailureRootCauseDefinition> possibleRootCauses = failureStructureService.findRootCauseOptionsByModel(number, faultFound);
        Collections.sort(possibleRootCauses);
        return generateAndWriteComboboxJson(possibleRootCauses,"id","name");
    }



    public String listContracts() throws CatalogException {
    	String contractName = getSearchPrefix().toLowerCase();
        Claim claim = claimService.findClaim(Long.parseLong(number));
        //Start: We need to populate contract based on claim Repair,Installation 
        //and purchase date which could be updated by Processor or DSM on UI
        if(this.claimRepairDate != null){
        	claim.setRepairDate(this.claimRepairDate);
        }
        if(this.purchaseDate != null){
        	claim.setPurchaseDate(this.purchaseDate);
        }
        if(this.installationDate != null){
        	claim.setInstallationDate(this.installationDate);
        }
        // End
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        List<Contract> applicableContracts = null;
        //Modified to fix TWMS4.3-656 issue 
        if(claim.getCampaign() != null && ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType())){
            if(claim.getCampaign().getContract() != null){
        	Contract contract = contractService.findContract(claim.getCampaign().getContract().getId());
        	applicableContracts = new ArrayList<Contract>();
        	applicableContracts.add(contract);
            }
        }else{
        	Item item= catalogService.findItemOwnedByManuf(causalPart);
        	applicableContracts = contractService.findContract(claim, item, true);
        }
        if(applicableContracts != null){
            List<Contract> selectedContracts=new ArrayList<Contract>();
            for (Contract contract : applicableContracts) {
                if (contract.getName().toLowerCase().startsWith(contractName)) {
                    selectedContracts.add(contract);
                }
            }
            return generateAndWriteComboboxJson(selectedContracts, "id", "name");
        }
        return generateAndWriteEmptyComboboxJson();
    }

    public String listAllContracts() throws CatalogException {
    	String contractName = null;
    	List<Contract> contracts = new ArrayList<Contract>();
    	if(StringUtils.hasText(getSearchPrefix())){
            contractName = getSearchPrefix().toLowerCase();
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getSecurityHelper().getWarrantyAdminBusinessUnit());
    	    contracts = contractService.findAllContracts(contractName,0,10);
    	}
    	return generateAndWriteComboboxJson(contracts, "id", "name");

    }


    public String getInfoForContract() throws JSONException{
        // fix for TWMS4.3-682
        long contactIdAsLong = NumberUtils.toLong(contractId, -1);
        if (contactIdAsLong != -1) {
            Contract contract = contractService.findContract(contactIdAsLong);
            JSONArray details = new JSONArray();
            JSONObject contractInfo = new JSONObject();
            if (contract != null && contract.getPhysicalShipmentRequired()) {
                contractInfo.append("physicalShipment", "true");
            }
            if (contract != null && contract.getCollateralDamageToBePaid()) {
                contractInfo.append("collateral", "true");
            }
            // changes for TWMS4.1-547
            if (contract != null && contract.getSupplier() != null
                    && contract.getSupplier().getName() != null) {
                contractInfo.append("supplierName", contract.getSupplier().getName());
            }
            if (contract != null && contract.getSupplier() != null
                    && contract.getSupplier().getSupplierNumber() != null) {
                contractInfo.append("supplierNumber", contract.getSupplier().getSupplierNumber());
            }
            if (contract != null && contract.getName() != null) {
                contractInfo.append("contractName", contract.getName());
            }
            // end
            details.put(contractInfo);
            jsonString = details.toString();
        }
        return SUCCESS;

    }



    public String listOemPartSerialNumbers() {
        return getSerialNumbersStartingWith(getSearchPrefix());
    }

	public String getSerializedOemPartDetails() {
		JSONArray details;

        try {
			InventoryItem item = inventoryService.findInvItemByIdWithoutInactiveFilter(number);
			details = new JSONArray();
			details.put(item.getOfType().getDescription());
			details.put(item.getOfType().getNumber());
			details.put("-");
			details.put("-");
			details.put("-");
		} catch (ItemNotFoundException e) {
			details = EMPTY_OEM_DETAIL;
		}
		jsonString = details.toString();
		return SUCCESS;
	}

	public String getAllMiscPartsForDealer(){
		List<MiscellaneousItem> miscItem = miscellaneousItemConfigService.findMiscellanousPartForDealer(forDealer.getId(), getSearchPrefix());
		if(miscItem != null && !miscItem.isEmpty())
			return generateAndWriteComboboxJson(miscItem,"partNumber");
		else
                        // modified to fix TWMS4.3-664
			return generateAndWriteEmptyComboboxJson();
	}

	public String getAllMiscParts(){
		List<MiscellaneousItem> miscItem = miscellaneousItemConfigService.findMiscellanousParts(getSearchPrefix());
		if(miscItem != null && !miscItem.isEmpty())
			return generateAndWriteComboboxJson(miscItem,"partNumber");
		else
                        // modified to fix TWMS4.3-664
			return generateAndWriteEmptyComboboxJson();
	}


	public String getMiscellaneousItemDetails(){

		    JSONArray details;
		    ServiceProvider dealer = this.dealershipRepository.findByDealerId(forDealer.getId());
        	MiscellaneousItemConfiguration miscellaneousItemConfig = this.miscellaneousItemConfigService.
        	findMiscellanousPartConfigurationForDealerAndMiscPart(forDealer.getId(), number);

        	details = new JSONArray();
        	if(miscellaneousItemConfig !=null){
				details.put(miscellaneousItemConfig.getMiscellaneousItem().getPartNumber());
				details.put(miscellaneousItemConfig.getMiscellaneousItem().getDescription());
				if(dealer != null && dealer.getPreferredCurrency()!= null){
					MiscItemRate miscItemRate = miscellaneousItemConfig.getMiscItemRateForCurrency(
							dealer.getPreferredCurrency());
					Money unitPrice = miscItemRate.getRate();
                	String baseUomString = miscellaneousItemConfig.getUom().getName();
                	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(miscellaneousItemConfig.getMiscellaneousItem().getBusinessUnitInfo().getName());
                    if(!ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)){
                    	UomMappings uomMapping = uomMappingsService.findUomMappingForBaseUom(baseUomString);
                        if(uomMapping != null ){
                        	unitPrice = unitPrice.dividedBy(uomMapping.getMappingFraction());
                        }
                    }
					details.put(unitPrice.breachEncapsulationOfAmount());
					details.put(unitPrice.breachEncapsulationOfCurrency());
				}else{
					details.put("-");
					details.put("-");
				}
				details.put(miscellaneousItemConfig.getUom().getType());
				details.put(miscellaneousItemConfig.getId());
				details.put(miscellaneousItemConfig.getTresholdQuantity());
        	} else{
				details.put("-");
				details.put("-");
				details.put("-");
				details.put("-");
				details.put("-");
				details.put("-");
				details.put("-");
        	}
		jsonString = details.toString();

		return SUCCESS;
	}

	public String getMiscellaneousItemDetailsForCampaign(){
	    JSONArray details;
    	MiscellaneousItemConfiguration miscellaneousItemConfig = this.miscellaneousItemConfigService.
    												findMiscellanousPartConfigurationForMiscPart(number);
    	details = new JSONArray();
    	if(miscellaneousItemConfig !=null){
			details.put(miscellaneousItemConfig.getMiscellaneousItem().getPartNumber());
			details.put(miscellaneousItemConfig.getMiscellaneousItem().getDescription());
			details.put(miscellaneousItemConfig.getUom().getType());
			details.put(miscellaneousItemConfig.getId());
			details.put(miscellaneousItemConfig.getTresholdQuantity());
			if(miscellaneousItemConfig.getMiscItemRateForCurrency(Currency.getInstance("USD")) != null){
				MiscItemRate miscItemRate = miscellaneousItemConfig.getMiscItemRateForCurrency(
						Currency.getInstance("USD"));
				Money unitPrice = miscItemRate.getRate();
            	String baseUomString = miscellaneousItemConfig.getUom().getName();
                if(!ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)){
                	UomMappings uomMapping = uomMappingsService.findUomMappingForBaseUom(baseUomString);
                    if(uomMapping != null ){
                    	unitPrice = unitPrice.dividedBy(uomMapping.getMappingFraction());
                    }
                }
				details.put(unitPrice.breachEncapsulationOfAmount());
				details.put(unitPrice.breachEncapsulationOfCurrency());
			}else{
				details.put("-");
				details.put("-");
			}

    	} else{
			details.put("-");
			details.put("-");
			details.put("-");
			details.put("-");
			details.put("-");
			details.put("-");
			details.put("-");
    	}
		jsonString = details.toString();

		return SUCCESS;
	}

    public String listOemPartItemNumbers() {
        return getOemPartItemNumbersStartingWith(getSearchPrefix(), REPLACED_PART_ITEM_TYPE, false);
    }

    public String listActiveOemPartItemNumbers() {
    	// To show all inactive parts on a claim
        //activeParts = true;
        return getOemPartItemNumbersStartingWith(getSearchPrefix(), REPLACED_PART_ITEM_TYPE, false);
    }

    public String listOemServicePartItemNumbers() {
        return getOemPartItemNumbersStartingWith(getSearchPrefix(), REPLACED_PART_ITEM_TYPE, true);
    }

    public String listOemPartItemNumbersWithLabel() {
        return getOemPartItemNumbersStartingWith(getSearchPrefix(), REPLACED_PART_ITEM_TYPE,"id");
    }

    public String listItemNumbersForPartsClaim() {
        return getOemPartItemNumbersStartingWith(getSearchPrefix(), PARTS_CLAIM_ITEM_TYPE, false);
    }

    public String listSerializedPartsForPartsClaim() {
        return getSerializedPartsForPartsClaim(getSearchPrefix());
    }

	public String getUnserializedOemPartDetails() throws JSONException {
		JSONArray details;

        try {
            details = new JSONArray();

            Claim claim = this.claimService.findClaim(Long.parseLong(claimNumber));
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());

            Item item = this.catalogService.findItemOwnedByManuf(number);
            details.put(item.getDescription());
            details.put(1, "-");
            details.put("-");
            details.put("-");
            details.put("-");
            details.put(2, item.getNumber());
        } catch (CatalogException e) {
            details = EMPTY_OEM_DETAIL;
        }
        jsonString = details.toString();
        return SUCCESS;
    }

    public String getUnserializedBrandPartDetails() throws JSONException {
        JSONArray details;

        try {
            details = new JSONArray();

            BrandItem brandItem = this.catalogService.findBrandItemById(Long.parseLong(number));
            details.put(brandItem.getItem().getDescription());
            details.put(1, "-");
            details.put("-");
            details.put("-");
            details.put("-");
            details.put(2, brandItem.getItem().getNumber());
        } catch (Exception e) {
            details = EMPTY_OEM_DETAIL;
        }
        jsonString = details.toString();
        return SUCCESS;
    }
    
    public String getTranspotationRate() throws JSONException {
        JSONArray details= new JSONArray();
        try { 
        	BigDecimal transporationRate=this.configParamService.getBigDecimalValue(ConfigName.TRANSPORTATION_RATE_PER_LOADED_MILE.getName());
        	 details.put(transporationRate);    
        } catch (Exception e) {        	
       	 details.put(EMPTY_TRANSPORTATION_DETAIL);       	 
       	logger.error("getTranspotationRate",e);
       }
       jsonString = details.toString();
       return SUCCESS;
   }
            
    
    public String getTranspotationAmount() throws JSONException {
        JSONArray details= new JSONArray();

        try {   
            
            Map<BigDecimal, BigDecimal> distanceInSecMeters=getDistanceInSecMeters();
    		Set<BigDecimal> keys = distanceInSecMeters.keySet();
			BigDecimal timeInSec=null;
			BigDecimal distanceInMeters=null;
			for(BigDecimal key:keys)
			{
				timeInSec=key;
				distanceInMeters=distanceInSecMeters.get(key);
			}
            BigDecimal distanceInMile=GoogleMapDistanceCalculater.convertDistanceInMiles(distanceInMeters);
			BigDecimal distanceInHrs=GoogleMapDistanceCalculater.convertTravelSecsInHrs(timeInSec);            
			BigDecimal transporationRate=this.configParamService.getBigDecimalValue(ConfigName.TRANSPORTATION_RATE_PER_LOADED_MILE.getName());
			BigDecimal transpotationAmount=distanceInMile.multiply(transporationRate).multiply(BigDecimal.valueOf(2));
			Money transpotation=Money.valueOf(transpotationAmount, this.claim.getCurrencyForCalculation());          
            details.put(transpotationAmount);          
            details.put(distanceInMile.toString());    
            details.put(distanceInHrs.toString());   
            details.put(transpotation); 
          //fix for SLMSPROD-1392
            details.put(distanceInHrs.toString().replace('.', ':')); 
        } catch (Exception e) {        	
        	 details.put(EMPTY_TRANSPORTATION_DETAIL);
        	 details.put(EMPTY_TRANSPORTATION_DETAIL);
        	 details.put(EMPTY_TRANSPORTATION_DETAIL);
        	logger.error("getTranspotationAmount, error occured while calculating distance using google map, hence returning zero hours and distance", e);
        	logger.error(e.getStackTrace());
        }
        jsonString = details.toString();
        return SUCCESS;
    }

    public Map<BigDecimal, BigDecimal> getDistanceInSecMeters() throws Exception{

    	//Address fromAddress = getServicingLocationAddress();
    	/*Address toAddress = null;
    	InventoryItem inventoryItem=claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem();
    	if(inventoryItem!=null&&!inventoryItem.getType().equals(InventoryType.STOCK))
    	{    	
    		if (claim.getClaimedItems().get(0).getItemReference().isSerialized()) {
    			toAddress = inventoryItem.getOwnedBy().getAddress();
    		} else {
    			toAddress = claim.getOwnerInformation();
    		}
    	}
    	if(toAddress==null)
    	{
    		toAddress=fromAddress;
    	}*/
    	String fromAddress1 =getServicingLocationAddress();
    	/*String toAddress1 = getDealerLocationForGoogleMap(toAddress);*/
    	String toAddress1=getTravelLocationAddress();
    	Map<BigDecimal, BigDecimal> distanceInSecMeters = null;
    	
    		distanceInSecMeters = GoogleMapDistanceCalculater
    				.calculateDistance(fromAddress1, toAddress1);  	
    
    	return distanceInSecMeters;
    }

public String getDealerLocationForGoogleMap(
        Address organizationAddress) {
 StringBuilder sb = new StringBuilder();
 if (organizationAddress.getAddressLine1() != null
              && org.apache.commons.lang.StringUtils.isNotBlank(organizationAddress.getAddressLine1())) {
        String addressLine1 = organizationAddress.getAddressLine1();
        sb.append(addressLine1);
 }
 if (organizationAddress.getAddressLine2() != null
              && org.apache.commons.lang.StringUtils
                            .isNotBlank(organizationAddress.getAddressLine2())) {
        String addressLine2 = organizationAddress.getAddressLine2();
        sb.append(",").append(addressLine2);
 }
 if (organizationAddress.getCity() != null
              && org.apache.commons.lang.StringUtils.isNotBlank(organizationAddress.getCity())) {
        String city = organizationAddress.getCity();
        sb.append(",").append(city);
 }
 if (organizationAddress.getState() != null
              && org.apache.commons.lang.StringUtils.isNotBlank(organizationAddress.getState())) {
        String state = organizationAddress.getState();
        sb.append(",").append(state);
 }
 if (organizationAddress.getZipCode() != null
              && org.apache.commons.lang.StringUtils.isNotBlank(organizationAddress.getZipCode())) {
        String zip = organizationAddress.getZipCode();
        sb.append(",").append(zip);
 }
 if (organizationAddress.getCountry() != null
              && org.apache.commons.lang.StringUtils.isNotBlank(organizationAddress.getCountry())) {
        String country = organizationAddress.getCountry();
        sb.append(",").append(country);
 }
 return sb.toString();
}

    public String getUnserializedOemPartInfo() throws JSONException {
		JSONArray details;
            details = new JSONArray();
            Item item;
            try {
            	item = this.catalogService.findById(Long.parseLong(number));
                details.put(item.getDescription());
            } catch (Exception e) {
                details = EMPTY_OEM_DETAIL;
            }
        jsonString = details.toString();
        return SUCCESS;
    }

	public String findServiceProvider() throws JSONException {
		JSONArray details = new JSONArray();
		try {
        	ServiceProvider thirdPartyDealer = orgService.findServiceProviderByName(thirdPartyName);
        	List<OrganizationAddress> servicingLocations =
        		orgService.getAddressesForOrganization(thirdPartyDealer);
        	if (CollectionUtils.isNotEmpty(servicingLocations))
        	{
        		OrganizationAddress servicingLocation = servicingLocations.iterator().next();
        		details.put(servicingLocation.getLocation());
        		details.put(servicingLocation.getId());
        	}
        	else
        	{
    			details.put("-"); // Empty since it dint find anything
        		details.put("-");
        	}
		}
		catch (Exception e) {
			details.put("-"); // Empty due to exception
			details.put("-");
		}
		jsonString = details.toString();
		return SUCCESS;
	}

	public String  listParts() {
		return getOemPartItemNumbersStartingWith(getSearchPrefix());

	}
	public String  listMajorComponents() {
		return getMajorComponentsStartingWith(getSearchPrefix());

	}

	String getOemPartItemNumbersStartingWith(String prefix) {
        try {
            List<Item> items = new ArrayList<Item>();
            if (StringUtils.hasText(prefix)) {
            	String currentSelectedBusinessUnit = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
            	//Commented for perf fix instead we will use the new Native SQL query
                //items = catalogService.findParts(prefix.toUpperCase());

            	//since all types of parts should come we have a list of all the types created.
            	List<Object> itemGroupSet = new ArrayList<Object>(3);
            	itemGroupSet.add("PARTS");
            	itemGroupSet.add("KIT");
            	itemGroupSet.add("OPTION");
            	items = catalogService.fetchManufParts(currentSelectedBusinessUnit, prefix, 10, itemGroupSet, false);
            }
            return generateAndWriteComboboxJson(items,"id","number");
        } catch (Exception e) {
            logger.error("Error while generating JSON",e);
            throw new RuntimeException("Error while generating JSON", e);
        }

    }

	String getMajorComponentsStartingWith(String prefix) {
        try {
            List<Item> items = new ArrayList<Item>();
            if (StringUtils.hasText(prefix)) {
            	items = catalogService.findAllItemsWithPurposeWarrantyCoverage("Warranty Coverage",prefix, 0, 10);
            }
            return generateAndWriteComboboxJson(items,"number","number");
        } catch (Exception e) {
            logger.error("Error while generating JSON",e);
            throw new RuntimeException("Error while generating JSON", e);
        }

    }

    public String getCausalPartDetails() throws JSONException {
        JSONArray details;
//        fix for TWMS4.3-711
        long numberAsLong = NumberUtils.toLong(number, -1);
        if(numberAsLong != -1){
            try {
                details = new JSONArray();
                Item item;
                BrandItem brandItem = this.catalogService.findBrandItemById(Long.parseLong(causalPart));
                item = brandItem.getItem();
                details.put(item.getDescription());

                //Performance fix: firing other attribute related query only if the BU has one or more attributes
                if(this.attributeAssociationService.isAnyAttributeConfiguredForBU())
                {
                	Claim claim = this.claimService.findClaim(numberAsLong);
                    SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
                    List<AdditionalAttributes> additionalAttributes = this.attributeAssociationService.findAttributesForItem(item
                            .getId(), claim.getType());
                  //perf fix: look for contracts only if any attribute is defined for supplier at all
        			if(attributeAssociationService.isAnyAttributeConfiguredForSupplier())
        			{
	                    List<Contract> applicableContracts = this.contractService.findContract(
	                            claim, item, true);
	                    if (applicableContracts != null && applicableContracts.size() == 1) {
	                        Supplier supplier = applicableContracts.get(0).getSupplier();
	                        List<AdditionalAttributes> supplierAttributes = this.attributeAssociationService
	                                .findAttributesForSupplier(supplier.getId(), claim.getType());
	                        if (supplierAttributes != null && !supplierAttributes.isEmpty()) {
	                            additionalAttributes.addAll(supplierAttributes);
	                        }
	                    }
        			}
                    if (additionalAttributes != null && !additionalAttributes.isEmpty()) {
                        details.put(1, true);
                    } else {
                        details.put(1, "-");
                    }
                }
                details.put(2,item.getNumber());
                details.put(3,brandItem.getBrand());
                details.put("-");
                details.put("-");
                details.put("-");
            } catch (Exception e) {
                details = EMPTY_OEM_DETAIL;
            }
        }
        else
            details = EMPTY_OEM_DETAIL;
        jsonString = details.toString();
        return SUCCESS;
    }

	public String getDescriptionForPart() throws JSONException {
            JSONArray details=new JSONArray();
            try {
            Item item = this.catalogService.findItemOwnedByManuf(number);
                    details.put(item.getDescription());
            } catch (CatalogException e) {
                //Find brand part if supplier
                try{
                    BrandItem brandItem = this.catalogService.findBrandItemById(NumberUtils.toLong(number, -1));
                    details.put(brandItem.getItem().getDescription());
                    details.put(2, brandItem.getItem().getNumber());
                }catch(Exception e1){
                    details = EMPTY_OEM_DETAIL;
                }
            }
            jsonString = details.toString();
            return SUCCESS;
	}

	public String getDescriptionForBrandPart() throws JSONException {
        JSONArray details=new JSONArray();
        try {
        Item item = this.catalogService.findItemByBrandPartNumber(number,claimBrand);
                details.put(item.getDescription());
        } catch (CatalogException e) {
                details = EMPTY_OEM_DETAIL;
        }
        jsonString = details.toString();
        return SUCCESS;
}

	public String getPartAndDescriptionForInventoryItem() throws JSONException {
        JSONArray details=new JSONArray();

        InventoryItem inventoryItem = this.inventoryService.findInventoryItem(inventoryId);
                details.put(inventoryItem.getOfType().getDescription());
                details.put(inventoryItem.getOfType().getNumber());

        jsonString = details.toString();
        return SUCCESS;
}

	public String getInventoryForMC() throws JSONException {
		JSONArray details = new JSONArray();
		InventoryItem inventoryItem = this.inventoryService.findInventoryItemForMajorComponent(inventoryId);
		if(inventoryItem!=null)
		{
		details.put(inventoryItem.getId());
		details.put(inventoryItem.getSerialNumber());
		}
		else
		{
		details.put("");
		details.put("");
		}
		jsonString = details.toString();
		return SUCCESS;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public void setFaultFound(String faultFound) {
		this.faultFound = faultFound;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public Organization getForDealer() {
		return forDealer;
	}

	public void setForDealer(Organization forDealer) {
		this.forDealer = forDealer;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}

	public String getCausalPart() {
		return causalPart;
	}

	public void setCausalPart(String causalPart) {
		this.causalPart = causalPart;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getServicingLocationAddress() {
		return servicingLocationAddress;
	}

	public void setServicingLocationAddress(String servicingLocationAddress) {
		this.servicingLocationAddress = servicingLocationAddress;
	}

	public String getTravelLocationAddress() {
		return travelLocationAddress;
	}

	public void setTravelLocationAddress(String travelLocationAddress) {
		this.travelLocationAddress = travelLocationAddress;
	}

	private List<Object> getItemGroupsFromBUConfig(String configParamType) {

        List<Object> itemGroup = null;

        if (configParamType.equals(REPLACED_PART_ITEM_TYPE)) {
            // if Replaced part section
            itemGroup = this.configParamService
                    .getListofObjects(ConfigName.REPLACED_ITEMS_ON_CLAIM_CONFIGURATION
                            .getName());

        } else if (configParamType.equals(CASUAL_PART_ITEM_TYPE)) {
            // if causal part section
            itemGroup = this.configParamService
                    .getListofObjects(ConfigName.CAUSAL_ITEMS_ON_CLAIM_CONFIGURATION
                            .getName());
        } else if (configParamType.equals(PARTS_CLAIM_ITEM_TYPE)) {
            // if causal part section
            itemGroup = this.configParamService
                    .getListofObjects(ConfigName.PARTSCLAIM_ITEMTYPE_ON_CLAIM_CONFIGURATION
                            .getName());
        }

        return toUpper(itemGroup);
	}

	private List<Object> toUpper(List<Object> itemGroups){
		List<Object> toUpperItemGroups = new ArrayList<Object>();

		for(Object itemGroup : itemGroups){
			String itGroup = (String)itemGroup;
			toUpperItemGroups.add(itGroup.toUpperCase());
		}
		return toUpperItemGroups;

	}

	public String listItemNumbersForNonSerializedClaim() {
    	List<Item> items=new ArrayList<Item>();
    	String itemNumber = getSearchPrefix();
    	if(StringUtils.hasText(itemNumber)){
    		items = catalogService.findItemNumbersForNonSerializedClaim(itemNumber,claimType,0,15);
    	}

        return generateAndWriteComboboxJsonForItem(items,"alternateNumber");
	}

    public String getItemDetails() throws JSONException {
        JSONArray details;
        try {
            details = new JSONArray();
            if (StringUtils.hasText(number)) {
                Item item = this.catalogService.findItemOwnedByManuf(number);
                details.put(0, item.getDescription());
                details.put(1, item.getModel().getName());
                details.put(2, item.getModel().getId());
            } else {
                details = EMPTY_OEM_DETAIL;
            }
        } catch (CatalogException e) {
            details = EMPTY_OEM_DETAIL;
        }
        jsonString = details.toString();
        return SUCCESS;
    }

    public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public String getModelName() {
		return modelName;
    }

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public CalendarDate getClaimRepairDate() {
		return claimRepairDate;
	}

	public void setClaimRepairDate(CalendarDate claimRepairDate) {
		this.claimRepairDate = claimRepairDate;
	}

	public CalendarDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(CalendarDate installationDate) {
		this.installationDate = installationDate;
	}

	public CalendarDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(CalendarDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public UomMappingsService getUomMappingsService() {
		return uomMappingsService;
	}

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}

    public String listLocations() {
        try {
            List<Location> locations = warehouseService.findWarehouseLocationsStartingWith(getSearchPrefix());
            return generateAndWriteComboboxJson(locations,"id","code");
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    @Required
    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

	public InventoryItem getClaimedItem() {
		return claimedItem;
	}

	public void setClaimedItem(InventoryItem claimedItem) {
		this.claimedItem = claimedItem;
	}

	public InventoryItem getSerializedPart() {
		return serializedPart;
	}

	public void setSerializedPart(InventoryItem serializedPart) {
		this.serializedPart = serializedPart;
	}

	public void setAlarmCodeService(AlarmCodeService alarmCodeService) {
		this.alarmCodeService = alarmCodeService;
	}

	public boolean isPartsClaim() {
		return isPartsClaim;
	}

	public void setIsPartsClaim(boolean isPartsClaim) {
		this.isPartsClaim = isPartsClaim;
	}

	private Long productId;
	private Long inventoryId;
	/*private Claim claim;*/
	private ItemGroup itemGroup;
	public String  listAlarmCodes() {
		try {
			List<AlarmCode> alarmCode = alarmCodeService.findAllAlarmCodeOfProductWithNameLike(getSearchPrefix(), itemGroup, 0, 10);
			return generateAndWriteComboboxJson(alarmCode,"id","code");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}


	public ItemGroup getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(ItemGroup itemGroup) {
		this.itemGroup = itemGroup;
	}

	private boolean isNotNull(Object object) {
		return object == null ? false : true;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getAlarmCodeDescription() throws JSONException {
		JSONArray details;
		try {
			details = new JSONArray();
			if (StringUtils.hasText(number)) {
				AlarmCode code = this.alarmCodeService.alarmCodeByCode(number);
				details.put(code.getDescription());
				details.put(code.getId());
			} else {
				details.put("----");
				details.put("");
			}
		} catch (Exception e) {
			details = new JSONArray();
			details.put("----");
			details.put("");
		}
		jsonString = details.toString();
		return SUCCESS;
	}

	 @SuppressWarnings("unchecked")
	public String listCausedByForModel() {
	        List<FailureCauseDefinition> possibleCauses = failureStructureService.findCausedByOptionsForModelById(number, faultFound);
	        Collections.sort(possibleCauses,new Comparator()
	        {
	            public int compare(Object obj1 , Object obj2){
	                FailureCauseDefinition failure1 = (FailureCauseDefinition)obj1;
	                FailureCauseDefinition failure2 = (FailureCauseDefinition)obj2;
	                return failure1.getDescription().compareTo(failure2.getDescription());
	            }
	        });
	        return generateAndWriteComboboxJson(possibleCauses, "id","name");
	    }
    public String listDealerBrands() {
	        List<String> brands = new ArrayList<String>();
	       if(forDealer!=null)
	       {
	    	   brands.add(((Dealership)forDealer).getBrand());
	    	   brands.add(BrandType.UTILEV.getType());
	       }
	           return generateAndWriteComboboxJson(brands);
	       }

    public String getInventoryBrand(){
        JSONArray details=new JSONArray();
        if(inventoryId != null){
            InventoryItem inventoryItem = this.inventoryService.findInventoryItem(inventoryId);
            if(inventoryItem != null){
                details.put(inventoryItem.getBrandType());
            }
        }

        jsonString = details.toString();
        return SUCCESS;
    }
    public ClaimSubmissionUtil getClaimSubmissionUtil() {
        return claimSubmissionUtil;
    }

    public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
        this.claimSubmissionUtil = claimSubmissionUtil;
    }

	public InventoryItemUtil getInventoryItemUtil() {
		return inventoryItemUtil;
	}

	public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
		this.inventoryItemUtil = inventoryItemUtil;
	}

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

	public String getClaimBrand() {
		return claimBrand;
	}

	public void setClaimBrand(String claimBrand) {
		this.claimBrand = claimBrand;
	}

    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getJsonBrandItemForItemNumber(){
        List<BrandItem> brandItems = new ArrayList<BrandItem>();
        for(BrandItem brandItem : this.item.getBrandItems()){
            if(brandItem.getBrand().equalsIgnoreCase(this.claimBrand)){
                brandItems.add(brandItem);
            }
        }
        return generateAndWriteComboboxJsonForBrandItem(brandItems);
    }
 }

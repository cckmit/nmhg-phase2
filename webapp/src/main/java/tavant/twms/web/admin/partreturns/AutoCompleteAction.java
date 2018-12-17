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
package tavant.twms.web.admin.partreturns;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.actions.TwmsActionSupport;

import java.util.ArrayList;
import java.util.List;
import static tavant.twms.domain.catalog.ItemGroup.PRODUCT;

/**
 * @author Kiran.Kollipara
 * 
 */
@SuppressWarnings("serial")
public class AutoCompleteAction extends TwmsActionSupport {

    private static Logger logger=Logger.getLogger(AutoCompleteAction.class);

    private String jsonString;

    private CatalogService catalogService;
    private DealerGroupService dealerGroupService;
    private ItemGroupService itemGroupService;
    
    private String itemCriterion;
    private String dealer;
    private String productType;
    private String dealerCriterion;
    private String partCriterion;
    private String locationPrefix;
    private String dealerGroupName;
    private String itemGroupName;
    private WarehouseService warehouseService;
    private MiscellaneousItemConfigService miscellaneousItemConfigService;
    private WarrantyUtil warrantyUtil ;
   

	public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public String getItemGroupName() {
        return this.itemGroupName;
    }

    public void setItemGroupName(String itemGroupName) {
        this.itemGroupName = itemGroupName;
    }

    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    //  Autocomplete methods
    public String listDealers() {
        try {
            List<String> names = this.orgService.findDealerNamesStartingWith(getSearchPrefix(), 0,
                    10);
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    public String listProducts() {
        try {
            List<String> names = new ArrayList<String>(); 
            List<ItemGroup>	products = this.catalogService
                    .listAllProductCodesMatchingName(getSearchPrefix());
            if(products != null)
            	for(ItemGroup itemGroup :products){
            		if(itemGroup.getItemGroupDescription()!= null)
            			names.add(itemGroup.getItemGroupDescription());
            	}
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }
   
    public String listSeries() {
        try {
            List<ItemGroup> names = new ArrayList<ItemGroup>(); 
            List<ItemGroup>	products = this.catalogService
                    .listAllProductsMatchingGroupCode(getSearchPrefix());
            if(products != null)
            	for(ItemGroup itemGroup :products){
            		if(itemGroup.getGroupCode()!= null)
            			names.add(itemGroup);
            	}
            return generateAndWriteComboboxJson(names,"id","groupCode");
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }
    public String  listProductNameAndIds() {
		try {
			
			List<ItemGroup> itemGroups = catalogService.listAllProductCodesMatchingName(getSearchPrefix());
			return generateAndWriteComboboxJson(itemGroups,"id","itemGroupDescription");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
    
    public String listDealer() {
        List<String> names = this.orgService.findDealerNamesStartingWith(this.dealer, 0, 5);
        List<String> dealerNames = this.dealerGroupService.findGroupsWithNameStartingWith(
                this.dealer, new PageSpecification(0, 5), AdminConstants.PART_RETURNS_PURPOSE);
        for (String dealerName : dealerNames) {
            names.add(dealerName);
        }
        JSONArray listOfEntries = new JSONArray();
        for (String name : names) {
            JSONArray oneEntry = new JSONArray();
            oneEntry.put(name).put(name);
            listOfEntries.put(oneEntry);// needs the format [key, value]
        }
        this.jsonString = listOfEntries.toString();
        return SUCCESS;
    }

    public String listProductsAndModels() {
        try {
            List<String> names = this.catalogService.findProductsAndModelsWhoseNameStartsWith(
                    productType, 0, 10);
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    public String listItemCriterions() {
        try {
            List<String> names = this.catalogService.findItemNumbersStartingWith(this.partCriterion, 0,
                    10);
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    } 
    
    public String listItemsStartingWith(){
    	try {
            List<Item> items = this.catalogService.findItemsWhoseNumbersStartWith(getSearchPrefix(), 0,
                    10);
             	return generateAndWriteComboboxJson(items,"id","number");
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    	
    }

    public String listItemCriterion() {
        List<String> names = this.catalogService.findItemNumbersStartingWith(this.itemCriterion, 0,
                5);
        List<String> itemGroups = this.itemGroupService.findGroupsWithNameStartingWith(
                this.itemCriterion, new PageSpecification(0, 5),
                AdminConstants.PART_RETURNS_PURPOSE);
        for (String item : itemGroups) {
            names.add(item);
        }

        JSONArray listOfEntries = new JSONArray();
        for (String name : names) {
            JSONArray oneEntry = new JSONArray();
            oneEntry.put(name).put(name);
            listOfEntries.put(oneEntry);// needs the format [key, value]
        }
        this.jsonString = listOfEntries.toString();
        return SUCCESS;
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

    public String listItemGroupsForPartReturns() {
        return getItemGroupNames(AdminConstants.PART_RETURNS_PURPOSE);
    }
    
    public String listItemGroupsForFailureReports() {
        List<ItemGroup> itemsInGroups = itemGroupService.findItemGroupsByPurposeStartingWith(
                                    AdminConstants.FAIURE_REPORT_PURPOSE,getSearchPrefix(),new PageSpecification(0,10));
        return generateAndWriteComboboxJson(itemsInGroups,"id","name");
    }

    public String listItemGroupsForItemPrices() {
        return getItemGroupNames(AdminConstants.ITEM_PRICE_PURPOSE);
    }

    private String getItemGroupNames(String purpose) {
        try {
            List<String> names = this.itemGroupService.findGroupsWithNameStartingWith(
                    getSearchPrefix(), new PageSpecification(0, 10), purpose);
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    public String listDealerGroupsInDealerRates() {
        return getDealerGroupNames(AdminConstants.DEALER_RATES_PURPOSE);
    }

    public String listDealerGroupsInPartReturn() {
         return getDealerGroupNames(AdminConstants.PART_RETURNS_PURPOSE);
    }

    public String  listDealerGroupsInModifiers() {
        return getDealerGroupNames(AdminConstants.MODIFIERS_PURPOSE);
    }
   
    
	public String listMiscellaneousItems() {
		try {
			List<String> names = miscellaneousItemConfigService.findAllPartNumbersStartingWith(
					getSearchPrefix());
			return generateAndWriteComboboxJson(names);
		} catch (Exception e) {			
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
	
	
	public String listAllMiscellaneousItemConfigs(){
		try {
			List<MiscellaneousItemConfiguration> names = miscellaneousItemConfigService.findAllMiscellaneousItemConfigs();
			return generateAndWriteComboboxJson(names,"miscellaneousItem.partNumber");
		} catch (Exception e) {			
			throw new RuntimeException("Error while generating JSON", e);
		}		
		
	}
    

    private String getDealerGroupNames(String purpose) {
        try {
            List<String> names = this.dealerGroupService.findGroupsWithNameStartingWith(
                    getSearchPrefix(), new PageSpecification(0, 10), purpose);
            return generateAndWriteComboboxJson(names);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }

    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public String getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(String dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    public String getJsonString() {
        return this.jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getLocationPrefix() {
        return this.locationPrefix;
    }

    public void setLocationPrefix(String locationPrefix) {
        this.locationPrefix = locationPrefix;
    }

    public String getPartCriterion() {
        return this.partCriterion;
    }

    public void setPartCriterion(String partCriterion) {
        this.partCriterion = partCriterion;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public String getDealer() {

        return this.dealer;
    }

    public void setDealer(String dealer) {

        this.dealer = dealer;
    }

    public String getItemCriterion() {

        return this.itemCriterion;
    }

    public void setItemCriterion(String itemCriterion) {

        this.itemCriterion = itemCriterion;
    }

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}
    
    public String listDealerNamesAndIds() {
        List<ServiceProvider> dealers = orgService.findDealersWhoseNameStartsWith(getSearchPrefix(), 0, 10);
        return writeJsonResponse(generateComboboxJson(dealers, "id","name"));
    }
    
    public String listDealerNumbersAndIds() {
        List<ServiceProvider> dealers = orgService.findDealersWhoseNumberStartingWith(getSearchPrefix(), 0, 10);
        return writeJsonResponse(generateComboboxJson(dealers,"id","dealerNumber"));
    }
    
    public String  findItemGroupsForGroupType() {
		try {
			List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();;
			if (StringUtils.hasText(getSearchPrefix())) {
					 itemGroups = itemGroupService.findItemGroupForNameAndType(searchPrefix, "PART CLASS");
			}
			return generateAndWriteComboboxJson(itemGroups,"id","name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

    public WarrantyUtil getWarrantyUtil() {
        return warrantyUtil;
    }

    public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
        this.warrantyUtil = warrantyUtil;
    }
}

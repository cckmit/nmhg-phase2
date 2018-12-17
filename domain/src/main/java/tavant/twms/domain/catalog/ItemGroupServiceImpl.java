/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.catalog;

import static tavant.twms.domain.common.AdminConstants.ITEM_REVIEW_WATCHLIST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ItemGroupServiceImpl extends GenericServiceImpl<ItemGroup, Long, Exception> implements
        ItemGroupService {

    private ItemGroupRepository itemGroupRepository;

    private ItemSchemeRepository itemSchemeRepository;

    public void updateItemGroup(ItemGroup itemGroup){
        itemGroupRepository.update(itemGroup);
    }

    @Override
    public GenericRepository<ItemGroup, Long> getRepository() {
        return itemGroupRepository;
    }

    public ItemGroup findGroupContainingItem(Item item, ItemScheme scheme) {
        return itemGroupRepository.findGroupContainingItem(item, scheme);
    }

    public List<ItemGroup> findItemGroupsFromScheme(ItemScheme itemScheme) {
        return itemGroupRepository.findItemGroupsFromScheme(itemScheme);
    }

    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }

    public PageResult<ItemGroup> findPage(ListCriteria listCriteria, ItemScheme itemScheme) {
        return itemGroupRepository.findPage(listCriteria, itemScheme);
    }

    public ItemGroup findItemGroupByName(String name, ItemScheme itemScheme) {
        return itemGroupRepository.findItemGroupByName(name, itemScheme);
    }
    
	public ItemGroup findItemGroupByName(String name) {
		return itemGroupRepository.findItemGroupByName(name);
	}

    public ItemGroup findItemGroupByCode(String code) {
        return itemGroupRepository.findItemGroupByCode(code);
    }
    
    public ItemGroup findItemGroupByCodeAndType(String code, String groupType){ 
    	return itemGroupRepository.findItemGroupByCodeAndType(code, groupType);
    }
    public List<ItemGroup> findItemGroupByCodeAndTypeForPartsItemSync(String code,
    		String groupType) {
    	return itemGroupRepository.findItemGroupByCodeAndTypeForParts(code, groupType);
    }
    
    public ItemGroup findItemGroupByCodeAndTypeIncludeInactive(String code, String groupType){ 
    	return itemGroupRepository.findItemGroupByCodeAndTypeIncludeInactive(code, groupType);
    }
    
    public ItemGroup findItemGroupForModel(String productCode, String modelCode){
    	return itemGroupRepository.findItemGroupForModel(productCode, modelCode);
    }

    public List<ItemGroup> findGroupsByNameAndDescription(ItemScheme scheme, String name,
            String description) {
        return itemGroupRepository.findGroupsByNameAndDescription(scheme, name, description);
    }

    public void setItemSchemeRepository(ItemSchemeRepository itemSchemeRepository) {
        this.itemSchemeRepository = itemSchemeRepository;
    }

    public void createItemGroupForProductStructure(ItemGroup itemGroup) {
        itemGroup.setScheme(getSchemeForProductStructure());
        itemGroupRepository.save(itemGroup);
    }
    
    public void updateTreeInfo(String groupCode, String itemScheme, String buName)
    {
    	itemGroupRepository.updateTreeInfo(groupCode, itemScheme, buName);
    }

    public void updateItemGroupForProductStructure(ItemGroup itemGroup) {
        itemGroup.setScheme(getSchemeForProductStructure());
        itemGroupRepository.update(itemGroup);
    }

    private ItemScheme getSchemeForProductStructure() {
        ItemScheme scheme = itemSchemeRepository.findSchemeForPurpose("PRODUCT STRUCTURE");
        return scheme;
    }

    public ItemGroup findByNameAndPurpose(String name, String purpose) {
        return itemGroupRepository.findByNameAndPurpose(name, purpose);
    }

    public List<String> findGroupsWithNameStartingWith(String name,
            PageSpecification pageSpecification, String purpose) {
        return itemGroupRepository.findGroupsWithNameStartingWith(name, pageSpecification, purpose);
    }

    public List<ItemGroup> findDescendentsOf(ItemGroup itemGroup) {
        return itemGroupRepository.findDescendentsOf(itemGroup);
    }

    public List<ItemGroup> findGroupsForGroupType(String groupType) {
        return itemGroupRepository.findGroupsForGroupType(groupType);
    }
  

    public List<ItemGroup> findModelsForProduct(ItemGroup itemGroup) {
        List<ItemGroup> itemGroupList = new ArrayList<ItemGroup>();
        itemGroupList = itemGroupRepository.findModelsForProduct(itemGroup);
		Collections.sort(itemGroupList);
		return itemGroupList;
    }

    public ItemGroup findItemGroupForWatchedItem(Item item, String itemWatchlistType) {
        return itemGroupRepository.findItemGroupsForWatchedItem(item, itemWatchlistType);
    }

    public List<ItemGroup> findProductForNameAndType(String userEntry, String groupType) {
        return itemGroupRepository.findGroupsByNameAndType(userEntry, groupType);
    }
    
    public List<ItemGroup> findProductForCodeAndType(String userEntry, String groupType) {
        return itemGroupRepository.findGroupsByCodeAndType(userEntry, groupType);
    }

    public boolean isPartInReturnWatchList(Item item) {
        ItemGroup itemGroup = findItemGroupForWatchedItem(item, AdminConstants.PART_RETURNS_PURPOSE);
        if (itemGroup != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPartInReviewWatchList(Item item) {
        ItemGroup itemGroup = findItemGroupForWatchedItem(item, ITEM_REVIEW_WATCHLIST);
        if (itemGroup != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPartInReturnWatchList(Collection<Item> items, boolean forEach) {
        boolean watchedPart = false;
        for (Item item : items) {
            watchedPart = isPartInReturnWatchList(item);
            if (forEach && !watchedPart) {
                return false;
            }
            if (!forEach && watchedPart) {
                return true;
            }
        }
        return watchedPart;
    }

    public boolean isPartInReviewWatchList(Collection<Item> items, boolean forEach) {
        boolean watchedPart = false;
        for (Item item : items) {
            watchedPart = isPartInReviewWatchList(item);
            if (forEach && !watchedPart) {
                return false;
            }
            if (!forEach && watchedPart) {
                return true;
            }
        }
        return watchedPart;
    }

    public Map<Item, ItemGroup> findItemGroupMapForWatchedItems(Collection<Item> items,
            String itemWatchlistType) {
        Map<Item, ItemGroup> itemGroupItemMap = new HashMap<Item, ItemGroup>();
        for (Item item : items) {
            ItemGroup itemGroup = findItemGroupForWatchedItem(item, itemWatchlistType);
            if (itemGroup != null) {
                itemGroupItemMap.put(item, itemGroup);
            }
        }
        return itemGroupItemMap;

    }
    
    public boolean isInPartReturnDefinition(Item item) {
        List<PartReturnDefinition> returnDefinitions = itemGroupRepository.findPartReturnDefinitionForItem(item);
        if (returnDefinitions != null && !returnDefinitions.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    public List<ItemGroup> findItemGroupsByPurposes(List<String> purposes){
    	return itemGroupRepository.findItemGroupsByPurposes(purposes);
    }
    
    public ItemGroup findItemGroupsById(Long id){
    	return itemGroupRepository.findItemGroupsById(id);
    }

	public PageResult<ItemGroup> findPageForModels(ListCriteria productCriteria) {
		return itemGroupRepository.fetchPageForModels(productCriteria);
	}

	public void saveMachineUrlForModel(ItemGroup model) {
		itemGroupRepository.updateMachineUrlForModel(model);
	}
	
	public ItemGroup findItemGroupByType(final String groupType) {
		return itemGroupRepository.findItemGroupByType(groupType);
	}

    public List<ItemGroup> findAllModelForLabel(final String labelName){
        return itemGroupRepository.findAllModelForLabel(labelName);
    }

    public List<ItemGroup> findItemGroupForNameAndType(String userEntry, String groupType) {
        return itemGroupRepository.findGroupsByNameAndType(userEntry, groupType);
    }
    
    public ItemGroup findItemGroupByCodeAndIsPartOf(final String code,ItemGroup isPartOf){
    	return itemGroupRepository.findItemGroupByCodeAndIsPartOf(code, isPartOf);
    }
    
    
    public List<ItemGroup> listAllProductsAndModelsMatchingName(final String partialName,final List<String> itemGroupTypes,final PageSpecification pageSpecification){
    	return itemGroupRepository.listAllProductsAndModelsMatchingName(partialName,itemGroupTypes,pageSpecification);
    }
    
    public List<ItemGroup> findItemGroupsByPurposeStartingWith(String purpose,String searchPrefix,PageSpecification pageSpecification){
    	return itemGroupRepository.findItemGroupsByPurposeStartingWith(purpose,searchPrefix,pageSpecification);
    }

    public ItemGroup findModelForProduct(ItemGroup itemGroup, String modelCode) {
        return itemGroupRepository.findModelForProduct(itemGroup, modelCode);
    }

    public ItemGroup findProductFamilyForProduct(ItemGroup product, String productFamilyCode) {
        return itemGroupRepository.findProductFamilyForProduct(product, productFamilyCode);
    }
    
    public ItemGroup findProductFamilyForProductType(ItemGroup productType, String productFamilyCode){
    	return itemGroupRepository.findProductFamilyForProductType(productType, productFamilyCode);
    }

    public ItemGroup findItemGroupForItem(ItemGroup itemGroup, Item item,String purpose){
        return itemGroupRepository.findItemGroupForItem(itemGroup,item,purpose);
    }
        
   public ItemGroup  findProductOfModel(ItemGroup itemGroup){
        	return itemGroupRepository.findProductOfModel(itemGroup);
   }
   
   public boolean isItemPresentInItemGroupWithName(Item item,String itemGroupName){
	   ItemGroup itemGroup =  itemGroupRepository.isItemPresentInItemGroupWithName(item, itemGroupName);
	   if(itemGroup != null)
		   return true;
	   else
		   return false;
   }
   
   public List<Option> findOptionsList(String searchPrefix,int pageSize,int pageNumber) {
		return itemGroupRepository.findOptionsList(searchPrefix,pageSize,pageNumber);
	}
   
	public List<Option> findOptionsList(String brandType,String searchPrefix, int pageSize,int pageNumber){
		return itemGroupRepository.findOptionsList(brandType, searchPrefix, pageSize, pageNumber);

	}


public List<ItemGroup> findItemGroupForNameAndTypeAndBrand(String userEntry,
		String groupType, String brandType) {
	return itemGroupRepository.findGroupsByNameAndTypeAndBrand(userEntry, groupType,brandType);
}

public List<ItemGroup> findModelForNameAndTypeAndBrand(String userEntry,
		String groupType, String brandType){
	return itemGroupRepository.findModelByNameAndTypeAndBrand(userEntry, groupType,brandType);
        
}

public List<ItemGroup> listGroupCodeBasedOnGroupType(){
	return itemGroupRepository.findGroupCodeBasedOnGroupType();
}

}
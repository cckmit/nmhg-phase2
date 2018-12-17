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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.inventory.Option;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface ItemGroupService extends GenericService<ItemGroup, Long, Exception> {

    @Transactional(readOnly = false)
    public void updateItemGroup(ItemGroup itemGroup);

    public List<ItemGroup> findItemGroupsFromScheme(ItemScheme itemScheme);

    public ItemGroup findGroupContainingItem(Item item, ItemScheme scheme);

    public PageResult<ItemGroup> findPage(ListCriteria listCriteria, ItemScheme itemScheme);

    public ItemGroup findItemGroupByName(String name, ItemScheme itemScheme);

    public ItemGroup findItemGroupByName(String name);
    
    public ItemGroup findItemGroupByCode(String code);
    
    public ItemGroup findItemGroupByCodeAndType(String code, String groupType);
    
    public ItemGroup findItemGroupByCodeAndTypeIncludeInactive(final String code, final String groupType);

    List<ItemGroup> findGroupsByNameAndDescription(ItemScheme scheme, String name,
            String description);

    public void createItemGroupForProductStructure(ItemGroup itemGroup);

    public void updateItemGroupForProductStructure(ItemGroup itemGroup);

    public ItemGroup findByNameAndPurpose(String name, String purpose);

    public List<String> findGroupsWithNameStartingWith(String name,
            PageSpecification pageSpecification, String purpose);

    public List<ItemGroup> findDescendentsOf(ItemGroup itemGroup);

    public List<ItemGroup> findGroupsForGroupType(String groupType);

    List<ItemGroup> findModelsForProduct(ItemGroup itemGroup);

    public ItemGroup findItemGroupForWatchedItem(Item item, String itemWatchlistType);

    public boolean isPartInReturnWatchList(Item item);

    public boolean isPartInReviewWatchList(Item item);

    public boolean isPartInReturnWatchList(Collection<Item> items, boolean forEach);

    public boolean isPartInReviewWatchList(Collection<Item> items, boolean forEach);

    public boolean isInPartReturnDefinition(Item item);

    public Map<Item, ItemGroup> findItemGroupMapForWatchedItems(Collection<Item> items,
            String itemWatchlistType);

    public List<ItemGroup> findProductForNameAndType(String userEntry, String groupType);

    public List<ItemGroup> findProductForCodeAndType(String userEntry, String groupType);

    public List<ItemGroup> findItemGroupsByPurposes(List<String> purposes);
    
    public List<ItemGroup> findItemGroupsByPurposeStartingWith(String purpose,String searchPrefi,PageSpecification pageSpecification);

    @Transactional(readOnly=false)
    public void updateTreeInfo(String groupCode, String itemScheme, String buName);


    public ItemGroup findItemGroupsById(Long id);

	public PageResult<ItemGroup> findPageForModels(ListCriteria criteria);

    @Transactional(readOnly=false)
	public void saveMachineUrlForModel(ItemGroup model);
    
    public ItemGroup findItemGroupByType(final String groupType);

    public List<ItemGroup> findAllModelForLabel(String labelName);

    public List<ItemGroup> findItemGroupForNameAndType(String userEntry, String groupType);
    
    public ItemGroup findItemGroupByCodeAndIsPartOf(final String code,ItemGroup isPartOf);
    
    public List<ItemGroup> listAllProductsAndModelsMatchingName(final String partialName,final List<String> itemGroupTypes,PageSpecification pageSpecification);

    public ItemGroup findModelForProduct(ItemGroup product, String modelCode);

    public ItemGroup findProductFamilyForProduct(ItemGroup product, String productFamilyCode);
    
    public ItemGroup findProductFamilyForProductType(ItemGroup productType, String productFamilyCode);

    public ItemGroup findItemGroupForItem(ItemGroup itemGroup, Item item,String purpose);
    
    public ItemGroup  findProductOfModel(ItemGroup itemGroup);
    
    public boolean isItemPresentInItemGroupWithName(Item item,String itemGroupName);
    public ItemGroup findItemGroupForModel(final String productCode, final String modelCode);

	public List<ItemGroup> findItemGroupForNameAndTypeAndBrand(String string,
			String product, String brandType);

	public List<ItemGroup> findModelForNameAndTypeAndBrand(String string,
			String model, String brandType);
	
	public List<Option> findOptionsList(String searchPrefix, int pageSize,int pageNumber);
	
	public List<Option> findOptionsList(String brandType, String searchPrefix, int pageSize,int pageNumbers);
	
	public List<ItemGroup> listGroupCodeBasedOnGroupType();

	public List<ItemGroup> findItemGroupByCodeAndTypeForPartsItemSync(
			String partsCode, String model);
	
}
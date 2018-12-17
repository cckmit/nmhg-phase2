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

import java.util.List;

import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface ItemGroupRepository extends GenericRepository<ItemGroup, Long> {

    public List<ItemGroup> findItemGroupsFromScheme(ItemScheme itemScheme);

    public ItemGroup findGroupContainingItem(Item item, ItemScheme scheme);

    public PageResult<ItemGroup> findPage(ListCriteria listCriteria, ItemScheme itemScheme);

    public ItemGroup findItemGroupByName(String name, ItemScheme itemScheme);

    public ItemGroup findItemGroupByName(String name);

    public ItemGroup findItemGroupByCode(String code);
    
    public ItemGroup findItemGroupByCodeAndType(String code, String groupType); 

    public List<ItemGroup> findGroupsByNameAndDescription(ItemScheme scheme, String name,
            String description);

    public ItemGroup findByNameAndPurpose(String name, String purpose);

    public List<String> findGroupsWithNameStartingWith(String name,
            PageSpecification pageSpecification, String purpose);

    public List<ItemGroup> findDescendentsOf(ItemGroup itemGroup);

    public List<ItemGroup> findGroupsForGroupType(String groupType);
    

    List<ItemGroup> findModelsForProduct(ItemGroup itemGroup);

    public ItemGroup findItemGroupsForWatchedItem(Item item, String itemWatchlistType);

    public List<ItemGroup> findGroupsByNameAndType(String userEntry, String groupType);

    public List<PartReturnDefinition> findPartReturnDefinitionForItem(Item item);

    public List<ItemGroup> findGroupsByCodeAndType(String userEntry, String groupType);
    
    public ItemGroup findItemGroupByCodeAndTypeIncludeInactive(final String code, final String groupType);

    public List<ItemGroup> findItemGroupsByPurposes(List<String> purposes);

    public void updateTreeInfo(final String groupCode, final String itemSchemem, final String buName);

    public ItemGroup findItemGroupsById(Long id);

	public PageResult<ItemGroup> fetchPageForModels(ListCriteria productCriteria);

	public void updateMachineUrlForModel(ItemGroup model);
	
	public ItemGroup findItemGroupByType(final String groupType);

    public List<ItemGroup> findAllModelForLabel(String labelName);
    
    public ItemGroup findItemGroupByCodeAndIsPartOf(final String code,ItemGroup isPartOf);

    public List<ItemGroup> listAllProductsAndModelsMatchingName(final String name,final List<String> itemGroupTypes,PageSpecification pageSpecification);
    public ItemGroup findModelForProduct(final ItemGroup itemGroup, final String modelCode);
    
    public List<ItemGroup>  findItemGroupsByPurposeStartingWith(final String purpose,final String searchPrefix,PageSpecification pageSpecification);

    public ItemGroup findProductFamilyForProduct(final ItemGroup itemGroup, final String productFamilyCode);
    
    public ItemGroup findProductFamilyForProductType(ItemGroup productType, String productFamilyCode);

    public ItemGroup findItemGroupForItem(final ItemGroup itemGroup, final Item item,final String purpose);
    
    public ItemGroup  findProductOfModel(final ItemGroup itemGroup);
    
    public ItemGroup isItemPresentInItemGroupWithName(Item item, String itemGroupName);

    public ItemGroup findItemGroupForModel(final String productCode, final String modelCode);
 

	public List<Option> findOptionsList(String searchPrefix, int pageSize,int pageNumber);
	
	public List<Option> findOptionsList(String brandType, String searchPrefix, int pageSize,int pageNumber);
	
    public List<Option> findOptionDescriptionList(String searchPrefix, int pageSize,int pageNumber);
	
	public List<Option> findOptionDescriptionList(String brandType, String searchPrefix, int pageSize,int pageNumber);
 

	public List<ItemGroup> findGroupsByNameAndTypeAndBrand(String userEntry,
			String groupType, String brandType);

	public List<ItemGroup> findModelByNameAndTypeAndBrand(String userEntry,
			String groupType, String brandType);
	
	public List<Item> findItemsAtAllLevelForGroup(final Long id);
	
	public List<ItemGroup> findGroupCodeBasedOnGroupType();

	public List<ItemGroup> findItemGroupByCodeAndTypeForParts(String code,
			String groupType);
 
}
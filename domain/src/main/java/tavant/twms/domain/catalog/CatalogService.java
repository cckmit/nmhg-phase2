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
package tavant.twms.domain.catalog;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author kamal.govindraj
 */
@Transactional(readOnly = true)
public interface CatalogService {

	@Transactional(readOnly = false)
	void createItem(Item item);

	@Transactional(readOnly = false)
	void updateItem(Item item);
	
	@Transactional(readOnly = false)
	void createBrandItem(BrandItem item);
	
	@Transactional(readOnly = false)
	void updateBrandItem(BrandItem brandItem);
	
	@Transactional(readOnly = false)
	void createSupersessionItem(SupersessionItem item);

	Item findItemOwnedByManuf(String itemNumber) throws CatalogException;
	
	ItemGroup findSeriesByGroupCode(String seriesGroupCode);

	public List<String> findItemNumbersStartingWith(String partialItemNumber, int pageNumber, int pageSize);

	public Item findItemByItemNumber(String itemNumber) throws CatalogException;
	
	public List<Item> findItemsWhoseNumbersStartWith(String partialItemNumber, int pageNumber, int pageSize);

	public List<ItemGroup> findModelsWhoseNumbersStartWith(String partialModelName, int pageNumber, int pageSize);

	public List<Item> findParts(final String itemNameOrNumber);

	public List<Item> findParts(final String itemNameOrNumber, final List<Object> itemGroup);

	public ItemGroup findItemGroup(Long id);

	public List<ItemGroup> findAllItemGroups();

	public ItemGroup findItemGroupByName(String name);

	public List<ItemGroup> findAllItemModels();

    public List<ItemGroup> findAllItemProductsAndModels(String partialItemGroup, int pageNumber, int pageSize);

	List<String> findItemGroupsWithNameLike(String partialProductName, int pageNumber, int pageSize);

	List<ItemGroup> findAllItemGroupsWithNameLike(String partialProductName, int pageNumber, int pageSize);

	List<Item> findAllItemsWithPurposeWarrantyCoverage(String purpose, String name, int firstResult, int pageMaxResult);

	public Item findItemWithPurposeWarrantyCoverage(String purpose, String partItemNumber);

	public PageResult<Item> findItemsWithNumberAndDescriptionLike(String number, String description, ListCriteria lc);

	public List<String> findProductsAndModelsWhoseNameStartsWith(String name, int firstResult, int pageMaxResult);

	public ItemGroup findProductOrModelWhoseNameIs(String name);

	public List<Item> findOEMDealerParts(String itemNameOrNumber, Organization organization, List<Object> itemGroup);

	public Item findPartForOEMDealerPart(Item toItem, final Organization organization);

	public Item findOEMDealerPartForPart(Item fromItem, final Organization organization);

	public Item findById(Long id);
	
	public BrandItem findBrandItemById(Long id);
    
    public List<Item> findByIds(Collection<Long> ids);

	public PageResult<Item> findItems(ListCriteria criteria, Organization organization);

	public PageResult<Item> findAllItemsMatchingQuery(Long domainPredicateId, ListCriteria listCriteria,
			Organization organization);

	@Transactional(readOnly = false)
	public void deleteItem(Item item);

	public List<Item> findItemsWithModelName(String modelName);

	public Item findSupplierItem(String itemNumber, Long supplierId);

	public List<String> findItemGroupsOfTypeWithNameLike(String partialProductName, String itemGroupType,
			int pageNumber, int pageSize);

	public Item findItemByItemNumberOwnedByManuf(String itemNumber) throws CatalogException;
	
	public Item findItemByItemNumberOwnedByManuf(final String itemNumber, final String itemType) throws CatalogException;
	
	public Item findItemByItemNumberOwnedByManufAndProduct(String itemNumber,ItemGroup product) throws CatalogException;

	public BrandItem findBrandByItemIdAndBrand(Item item, String brand);
	
	public List<BrandItem> fetchItemBrands(final Item item);
	
	public Item findItemByItemNumberAndPurposeOwnedByServiceProvider(String purpose, String itemNumber, Long ownedBy)
			throws CatalogException;

	public Item findPartForOEMDealerPartUsingItemNumber(String toItemNumber, Organization organization);

	public ResultSet findItems(PageSpecification pageSpecification);

	public Long findItemCount();

	public void createPartsCatalogWorkSheet(PageSpecification pageSpecification, HSSFSheet sheet, HSSFCellStyle style)
			throws Exception;

	public Item findItemByItemNumberOwnedByServiceProvider(String itemNumber, Long ownedById) throws CatalogException;

	public List<ItemUOMTypes> findAllUoms();

	public List<Item> findItemsByAlternateItemNumber(String alternateItemNumber, Long ownedById);

	public List<Item> fetchManufParts(final String businessUnit, final String partNumber, final int fetchSize,
			List<Object> itemGroups, boolean onlyServiceParts);

	public List<Item> findItemNumbersByModelName(String modelName, String itemNumber);

	public PageResult<ItemGroup> findAllModelsWithCriteria(ListCriteria listCriteria);

	public List<Item> findItemNumbersForNonSerializedClaim(final String itemNumber,final String itemType, final int pageNumber,
			final int pageSize);

	public List<ItemGroup> listAllProductCodesMatchingName(String partialProductName);

	public ItemGroup findModelByModelName(final String modelName) throws CatalogException;

    public boolean isOEMDealerPartExistForPart(Item fromItem,
            Organization organization);

    public String findOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
            boolean isNumber, Organization forClaim, Organization loggedInUserOrganization, Class party);
	
    	List<Item> findItemsWithNumberLike(String number);

   public PageResult<Item> findItemsForItemGroup(long itemGroup, ListCriteria criteria);        

    public ItemGroup findItemGroupByProductOrModelName(String name,String itemGroupType);
    
    public List<BrandItem> fetchManufBrandParts(final String businessUnit, final String partNumber, final int fetchSize,
    		List<Object> itemGroups, boolean onlyServiceParts, List<String> brands, boolean activeParts);

	public BrandItem findBrandItemByName(String name);
	
	public List<BrandItem> findBrandItemsByName(String number,String name);
	
	public PageResult<Item> findAllItemsForContract(Contract contract, ListCriteria listCriteria);
	
	public String findSupersessionItem(Item item);

	Item findItemByBrandPartNumber(String number, String claimBrand) throws CatalogException;
	
	public BrandItem fetchManufBrandPartsForBrandPartNumber(final String businessUnit, final String partNumber,
			List<Object> itemGroups,String brand);

	public Item fetchManufPartsForPartNumber(final String businessUnit,
			final String partNumber, List<Object> itemGroups);
	
	public List<ItemGroup> listAllProductsMatchingGroupCode(String partialProductCode);
	
	public List<BrandItem> fetchBrandItemsForbrandPartNumber(final String businessUnit, final String partNumber,
			List<Object> itemGroups);

	List<BrandItem> fetchManufBrandPartsForBrandPartNumberForAMER(String name,
			String upperCase, List<Object> itemGroups, List<String> brands);
	
	public ItemGroup findItemGroupByDescription(String description);
}

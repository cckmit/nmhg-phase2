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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.query.HibernateQuery;
import tavant.twms.domain.query.HibernateQueryGenerator;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.supplier.ItemMappingRepository;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.security.SecurityHelper;

public class CatalogServiceImpl implements CatalogService {


    private static int ROW_MAX_NUMBER = 5;

    private final Logger logger = Logger.getLogger(this.getClass());

    private CatalogRepository catalogRepository;

    private ItemMappingRepository itemMappingRepository;
    
    private BrandItemRepository brandItemRepository;
    
    private SupersessionItemRepository supersessionItemRepository;

	private PredicateAdministrationService predicateAdministrationService;

    private SecurityHelper securityHelper;

    public Item findById(Long id) {
        return catalogRepository.findById(id);
    }

    public void createItem(Item item) {
        catalogRepository.save(item);
    }

    public void updateItem(Item item) {
        catalogRepository.update(item);
    }
    
    public void createBrandItem(BrandItem brandItem) {
    	brandItemRepository.save(brandItem);
    }
    
    public void updateBrandItem(BrandItem brandItem) {
      brandItemRepository.update(brandItem);
    }

    public Item findItemOwnedByManuf(String itemNumber) throws CatalogException {
        Item foundItem = catalogRepository.findItemByItemNumberOwnedByManuf(itemNumber);
        if (foundItem != null) {
            return foundItem;
        } else {
            throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
        }
    }
    
    public ItemGroup findSeriesByGroupCode(String seriesGroupCode){
    	return catalogRepository.findSeriesByGroupCode(seriesGroupCode);
    }

    public Item findItemByItemNumberOwnedByManuf(String itemNumber) throws CatalogException {
        Item foundItem = catalogRepository.findItemByItemNumberOwnedByManuf(itemNumber);
        if (foundItem != null) {
            return foundItem;
        } else {
            throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
        }
    }
    
    public Item findItemByItemNumber(String itemNumber) throws CatalogException {
        Item foundItem = catalogRepository.findItemByItemNumber(itemNumber);
        if (foundItem != null) {
            return foundItem;
        } else {
            throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
        }
    }
    
      
    public Item findItemByItemNumberOwnedByManuf(String itemNumber,String itemType) throws CatalogException {
        Item foundItem = catalogRepository.findItemByItemNumberOwnedByManuf(itemNumber,itemType);
        if (foundItem != null) {
            return foundItem;
        } else {
            throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
        }
    }
    
    public Item findItemByItemNumberOwnedByManufAndProduct(String itemNumber,ItemGroup product) throws CatalogException{
    	 Item foundItem = catalogRepository.findItemByItemNumberOwnedByManufAndProduct(itemNumber,product);
         if (foundItem != null) {
             return foundItem;
         } else {
             throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
         }
    }
    
    

	public BrandItem findBrandByItemIdAndBrand(Item item, String brand)
		{
		      return brandItemRepository.findBrandByItemIdAndBrand(item,brand);
	    }
	
	public List<BrandItem> fetchItemBrands(final Item item)
	{
		return brandItemRepository.fetchItemBrands(item);
	}

    public Item findItemByItemNumberOwnedByServiceProvider(String itemNumber, Long ownedBy) throws CatalogException {
        Item foundItem = catalogRepository.findItemByItemNumberOwnedByServiceProvider(itemNumber, ownedBy);
        if (foundItem != null) {
            return foundItem;
        } else {
            throw new CatalogException("Item with number " + itemNumber + " doesn't exist");
        }
    }
    
	public Item findItemByItemNumberAndPurposeOwnedByServiceProvider(String purpose, String itemNumber, Long ownedBy)
			throws CatalogException {
		Item foundItem = catalogRepository.findItemByItemNumberAndPurposeOwnedByServiceProvider(purpose, itemNumber,
				ownedBy);
		if (foundItem != null) {
			return foundItem;
		} else {
			throw new CatalogException("Item with number " + itemNumber + " with purpose " + purpose + "doesn't exist");
		}
	}

    public List<String> findItemNumbersStartingWith(String partialItemNumber, int pageNumber,
                                                    int pageSize) {
        return catalogRepository.findItemNumbersStartingWith(partialItemNumber, pageNumber,
                pageSize);
    }

    public List<Item> findItemsWhoseNumbersStartWith(String partialItemNumber, int pageNumber,
                                                     int pageSize) {
        return catalogRepository.findItemsWhoseNumbersStartWith(partialItemNumber, pageNumber,
                pageSize);
    }

    public PageResult<Item> findItems(ListCriteria criteria, Organization organization) {
        return catalogRepository.findItems(criteria, organization);
    }

    public List<Item> findItemsWithModelName(String modelName) {
        return catalogRepository.findItemsWithModelName(modelName);
    }

    public List<ItemGroup> findModelsWhoseNumbersStartWith(String partialModelName, int pageNumber,
                                                           int pageSize) {
        return catalogRepository.findModelsWhoseNumbersStartWith(partialModelName, pageNumber,
                pageSize);
    }

    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<Item> findParts(String itemNameOrNumber) {
        return catalogRepository.findParts(itemNameOrNumber);
    }

    public List<Item> findParts(String itemNameOrNumber, List<Object> itemGroup) {
        return catalogRepository.findParts(itemNameOrNumber, itemGroup);
    }

    public ItemGroup findItemGroup(final Long id) {
        return catalogRepository.findItemGroup(id);
    }

    public List<ItemGroup> findAllItemGroups() {
        return catalogRepository.findAllItemGroups();
    }

    public List<ItemGroup> findAllItemModels() {
        return catalogRepository.findAllItemModels();
    }

    public List<ItemGroup> findAllItemProductsAndModels(String partialItemGroup ,int pageNumber,
                                                      int pageSize) {
           return catalogRepository.findAllItemProductsAndModels(partialItemGroup, pageNumber , pageSize);
       }


    public ItemGroup findItemGroupByName(String name) {
        return catalogRepository.findItemGroupByName(name);
    }

    public List<String> findItemGroupsWithNameLike(String partialProductName, int pageNumber,
                                                   int pageSize) {
        return catalogRepository.findItemGroupsWithNameLike(partialProductName, pageNumber,
                pageSize);
    }
    
    
    
    public List<ItemGroup> findAllItemGroupsWithNameLike(String partialProductName, int pageNumber,
            int pageSize) {
        return catalogRepository.findAllItemGroupsWithNameLike(partialProductName, pageNumber,
                pageSize);
    }
    public List<Item> findAllItemsWithPurposeWarrantyCoverage(String purpose,String partialProductName, int pageNumber,
            int pageSize) {
        return catalogRepository.findAllItemsWithPurposeWarrantyCoverage(purpose,partialProductName, pageNumber,
                pageSize);
    }
    
    public Item findItemWithPurposeWarrantyCoverage(String purpose, String partItemNumber) {
        return catalogRepository.findItemWithPurposeWarrantyCoverage(purpose, partItemNumber);
    }
  
    
    public List<ItemGroup> listAllProductCodesMatchingName(String partialProductName) {
        return catalogRepository.listAllProductCodesMatchingName(partialProductName);
    }
    
    public List<ItemGroup> listAllProductsMatchingGroupCode(String partialProductName) {
        return catalogRepository.findAllProductsMatchingGroupCode(partialProductName);
    }

    public PageResult<Item> findItemsWithNumberAndDescriptionLike(String number, String description, ListCriteria lc) {
        return catalogRepository.findItemsWithNumberAndDescriptionLike(number, description, lc);
    }

    public List<String> findProductsAndModelsWhoseNameStartsWith(String name, int firstResult,
                                                                 int pageMaxResult) {
        return catalogRepository.findProductsAndModelsWhoseNameStartsWith(name, firstResult,
                pageMaxResult);
    }

    public ItemGroup findProductOrModelWhoseNameIs(String name) {
        return catalogRepository.findProductOrModelWhoseNameIs(name);
    }

    public List<Item> findOEMDealerParts(String itemNameOrNumber, Organization organization, List<Object> itemGroup) {
        return catalogRepository.findOEMDealerParts(itemNameOrNumber, organization, itemGroup);
    }

    public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
        this.itemMappingRepository = itemMappingRepository;
    }

    public Item findPartForOEMDealerPart(Item toItem, final Organization organization) {
        return itemMappingRepository.findPartForOEMDealerPart(toItem, organization);
    }

    public Item findPartForOEMDealerPartUsingItemNumber(String toItemNumber, Organization organization) {
        return itemMappingRepository.findPartForOEMDealerPartUsingItemNumber(toItemNumber, organization);
    }

    public Item findOEMDealerPartForPart(Item fromItem, final Organization organization) {
        return itemMappingRepository.findOEMDealerPartForPart(fromItem, organization);
    }

    public void setPredicateAdministrationService(
            PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }


    @Required
    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public PageResult<Item> findAllItemsMatchingQuery(Long domainPredicateId,
                                                      ListCriteria listCriteria, Organization organization) {
        DomainPredicate predicate = predicateAdministrationService.findById(domainPredicateId);
        HibernateQueryGenerator generator;
       if( organization.isDealer())
       {
        generator = new HibernateQueryGenerator(
                BusinessObjectModelFactory.BRAND_ITEM_SEARCHES);
       }
       else
       {
    	   generator = new HibernateQueryGenerator(
                   BusinessObjectModelFactory.ITEM_SEARCHES); 
       }
        generator.visit(predicate);
        HibernateQuery query = generator.getHibernateQuery();
//        if (organization instanceof ServiceProvider) {
//            query = prepareItemSearchQueryForItemMapping(query, organization);
//        }
        String sortString = listCriteria.getSortCriteriaString();
        PageSpecification pageSpecification = listCriteria.getPageSpecification();
        String queryWithoutSelect = query.getQueryWithoutSelect();
        if( organization.isDealer())
        {
        	queryWithoutSelect=queryWithoutSelect.concat( " and brandItem.item.ownedBy =  " + securityHelper.getOEMOrganization().getId());
        }
        else 
        {
        	queryWithoutSelect=queryWithoutSelect.concat( " and item.ownedBy =  " + securityHelper.getOEMOrganization().getId());	
        }
        
        
        if (listCriteria.isFilterCriteriaSpecified()) {
            String filter = listCriteria.getParamterizedFilterCriteria();
            queryWithoutSelect = queryWithoutSelect + " and (" + filter + " )";
        }
        queryWithoutSelect = queryWithoutSelect.replaceAll("USER_LOCALE", securityHelper.getLoggedInUser().getLocale().toString());
        QueryParameters params = new QueryParameters(query.getParameters(), listCriteria
                .getTypedParameterMap());
        return catalogRepository.findItemsUsingDynamicQuery(queryWithoutSelect, sortString, query
                .getSelectClause(), pageSpecification, params);
    }

    public void deleteItem(Item item) {
        catalogRepository.delete(item);
    }

    private HibernateQuery prepareItemSearchQueryForItemMapping(HibernateQuery query, Organization organization) {
        String queryWithoutSelect = query.getQueryWithoutSelect();
        StringBuffer tempQueryWithoutSelect = new StringBuffer(queryWithoutSelect);


    	String filterItemMappingForOEMPart=  "and not exists(select itemMapping.fromItem"
									           + " from Item innerItem, ItemMapping itemMapping, Item fromItem"
									           + " where innerItem = itemMapping.toItem"
									           + " and itemMapping.fromItem = item"
									           + " and fromItem = itemMapping.fromItem"
									           + " and fromItem.ownedBy.id = " + securityHelper.getOEMOrganization().getId() 									           
									           + " and innerItem.ownedBy.id = " + organization.getId() + ")";

        String filterItemMappingForSupplierPart = "and not exists(select itemMapping.toItem"
                + " from ItemMapping itemMapping, Supplier supplier"
                + " where itemMapping.toItem = item"
                + " and itemMapping.toItem.ownedBy = supplier)";

        String filterItemMappingForOtherOEMDealerPart = "and not exists(select itemMapping.toItem"
                + " from ItemMapping itemMapping, ServiceProvider dealership"
                + " where itemMapping.toItem = item"
                + " and itemMapping.toItem.ownedBy = dealership"
                + " and itemMapping.toItem.ownedBy.id !=" + organization.getId() + ")";

        tempQueryWithoutSelect = tempQueryWithoutSelect.append(filterItemMappingForOEMPart)
                .append(filterItemMappingForSupplierPart)
                .append(filterItemMappingForOtherOEMDealerPart);
        query.setQueryWithoutSelect(tempQueryWithoutSelect.toString());
        return query;
    }

    public Item findSupplierItem(String itemNumber, Long supplierId) {
        return catalogRepository.findSupplierItem(itemNumber, supplierId);
    }

    public List<String> findItemGroupsOfTypeWithNameLike(
            String partialProductName, String itemGroupType,
            int pageNumber, int pageSize) {
        return catalogRepository.findItemGroupsOfTypeWithNameLike(partialProductName, itemGroupType, pageNumber, pageSize);
    }

    public List<ItemUOMTypes> findAllUoms() {
    	List<ItemUOMTypes> allUOMs = new ArrayList<ItemUOMTypes>();    	
    	for (ItemUOMTypes eachUOM : ItemUOMTypes.values()) {
    		allUOMs.add(eachUOM);       
    	}
    	Collections.sort(allUOMs);
        return allUOMs;
    }
    

    public ResultSet findItems(PageSpecification pageSpecification) {
        return catalogRepository.findItems(pageSpecification);
    }

    public Long findItemCount() {
        return catalogRepository.findItemCount().longValue();
    }

    public void createPartsCatalogWorkSheet(
            PageSpecification pageSpecification, HSSFSheet sheet,
            HSSFCellStyle style) throws Exception {
        ResultSet itemRs = findItems(pageSpecification);
        int rowNumber = 0;
        int columnNumber = 0;
        HSSFRow row = sheet.createRow((short) rowNumber++);
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(1));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(2));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(3));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(4));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(5));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(6));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(7));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(8));
        row.createCell((short) columnNumber++).setCellValue(
                itemRs.getMetaData().getColumnName(9));
        try {
            while (itemRs.next()) {
                columnNumber = 0;
                if (rowNumber == ROW_MAX_NUMBER)
                    rowNumber = 0;
                row = sheet.createRow((short) rowNumber++);
                HSSFCell cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("PartNumber"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("PartDescription"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("DealerActualPrice"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("ElitePrice"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("PartWeight"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("DimensionPackageLength"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("DimensionPackageWidth"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("DimensionPackageHeight"));
                cell.setCellStyle(style);
                cell = row.createCell((short) columnNumber++);
                cell.setCellValue(itemRs.getString("DimensionUOM"));
                cell.setCellStyle(style);
            }
        } catch (SQLException e) {
            itemRs.close();
            logger.error(
                    "Exception in CatalogServiceImpl.createPartsCatalogWorkSheet()"
                            + e.getMessage(), e);
        } finally {
            if (itemRs != null) {
                try {
                    itemRs.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }


    public List<Item> findItemsByAlternateItemNumber(String alternateItemNumber, Long ownedById) {
        return catalogRepository.findItemsByAlternateItemNumber(alternateItemNumber, ownedById);

    }

    public List<Item> fetchManufParts(final String businessUnit, final String partNumber, final int fetchSize, List<Object> itemGroups, boolean onlyServiceParts) {
        return itemMappingRepository.fetchManufParts(businessUnit, partNumber, fetchSize, itemGroups, onlyServiceParts);
    }
    
    public Item fetchManufPartsForPartNumber(final String businessUnit, final String partNumber,List<Object> itemGroups) {
        return itemMappingRepository.fetchManufPartsUsingPartNumber(businessUnit, partNumber, itemGroups);
    }

    public List<Item> findItemNumbersByModelName(
            String modelName, String itemNumber) {
        return catalogRepository.findItemNumbersByModelName(modelName, itemNumber);
    }
    
    public List<Item> findItemNumbersForNonSerializedClaim(final String itemNumber,String itemType,final int pageNumber,
			final int pageSize) {
        return catalogRepository.findItemNumbersForNonSerializedClaim(itemNumber,itemType,pageNumber,pageSize);
    }

    public PageResult<ItemGroup> findAllModelsWithCriteria(ListCriteria listCriteria) {
        return catalogRepository.findAllModelsWithCriteria(listCriteria);
    }
    
    public ItemGroup findModelByModelName(final String modelName) throws CatalogException {
    	ItemGroup itemGroup = catalogRepository.findModelByModelName(modelName);
        if (itemGroup == null) {
       	 throw new CatalogException("Item Group with model name " + modelName + " doesn't exist");
        }
		return itemGroup; 
    }

    public boolean isOEMDealerPartExistForPart(Item fromItem,
            Organization organization) {
        //TODO
/*		Item item = findOEMDealerPartForPart(fromItem,
				organization);
		if (item != null) {
			return true;
		}*/
        return false;
    }

    public String findOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
            boolean isNumber, Organization forDealer, Organization loggedInUserOrganization, Class party) {
    	
    	if (oemDealerItem != null
                && isOEMDealerPartExistForPart(item, forDealer)) {
            if (InstanceOfUtil.isInstanceOfClass(party, loggedInUserOrganization)) {
                if (isNumber) {
                    return oemDealerItem.getDuplicateAlternateNumber() ? item
                            .getNumber() : item.getAlternateNumber()  ;
                } else {
                    return oemDealerItem.getDescription();
                }
            } else {
                if (isNumber) {
                    return (oemDealerItem.getDuplicateAlternateNumber() ? item
                            .getNumber() : item.getAlternateNumber())
                            + "(" + oemDealerItem.getNumber() + ")";
                } else {
                    return item.getDescription() + "("
                            + oemDealerItem.getDescription() + ")";
                }
            }
        } else {
            if (isNumber) {
                return item.getDuplicateAlternateNumber() ? item
                            .getNumber() : item.getAlternateNumber();
            } else {
                return item.getDescription();
            }
        }
    }
    public List<Item> findItemsWithNumberLike(String number) {
        return catalogRepository.findItemsWithNumberLike(number);
    }
    
    public PageResult<Item> findItemsForItemGroup(long itemGroup, ListCriteria criteria){
        return catalogRepository.findItemsForItemGroup(itemGroup, criteria);
    }
    
    public List<Item> findByIds(Collection<Long> ids){
        return catalogRepository.findByIds(ids);
    }

    public ItemGroup findItemGroupByProductOrModelName(String name, String itemGroupType){
        return catalogRepository.findItemGroupByProductOrModelName(name,itemGroupType);
    }
    
    public void setBrandItemRepository(BrandItemRepository brandItemRepository) {
		this.brandItemRepository = brandItemRepository;
	}

	public void createSupersessionItem(SupersessionItem item) {
		supersessionItemRepository.save(item);
	}
	

	public void setSupersessionItemRepository(
			SupersessionItemRepository supersessionItemRepository) {
		this.supersessionItemRepository = supersessionItemRepository;
	}
	public List<BrandItem> fetchManufBrandParts(final String businessUnit, final String partNumber, final int fetchSize,
			List<Object> itemGroups, boolean onlyServiceParts, List<String> brands, boolean activeParts) {
		// TODO Auto-generated method stub
		return itemMappingRepository.fetchManufBrandParts(businessUnit, partNumber, fetchSize, itemGroups, onlyServiceParts, brands, activeParts);
	}
	public BrandItem fetchManufBrandPartsForBrandPartNumber(final String businessUnit, final String partNumber,
			List<Object> itemGroups,String brand) {
		return itemMappingRepository.fetchManufBrandPartswithBarndItemNumber(businessUnit, partNumber, itemGroups, brand);
	}
	public List<BrandItem> fetchBrandItemsForbrandPartNumber(
			String businessUnit, String partNumber, List<Object> itemGroups) {
		// TODO Auto-generated method stub
		return itemMappingRepository.fetchBrandItemsForbrandPartNumber(businessUnit, partNumber, itemGroups);
	}

	public BrandItem findBrandItemByName(String name) {
		// TODO Auto-generated method stub
		return brandItemRepository.findBrandItemByName(name);
	}
	public List<BrandItem> findBrandItemsByName(String number,String name) {
		// TODO Auto-generated method stub
		return brandItemRepository.findBrandItems(number,name);
	}

	public BrandItem findBrandItemById(Long id) {
		// TODO Auto-generated method stub
		return catalogRepository.findBrandItemById(id);
	}
	
	public PageResult<Item> findAllItemsForContract(Contract contract,
			ListCriteria listCriteria) {
		return catalogRepository
				.findAllItemsForContract(contract, listCriteria);
	}

	public String findSupersessionItem(Item item) {
		return supersessionItemRepository.findSuppreSupersessionItem(item);
	}
	
	public BrandItem findUniqueBrandItemByNumberAndBrand(
			 String itemNumber,  String brand) {
		return brandItemRepository.findUniqueBrandItemByNumberAndBrand(
				itemNumber, brand);
	}

	public Item findItemByBrandPartNumber(String number, String brand) throws CatalogException{
		 Item foundItem = catalogRepository.findItemByBrandPartNumber(number,brand);
	        if (foundItem != null) {
	            return foundItem;
	        } else {
	            throw new CatalogException("Item with number " + number + " doesn't exist");
	        }
	}

	public List<BrandItem> fetchManufBrandPartsForBrandPartNumberForAMER(String businessUnit,
			String partNumber, List<Object> itemGroups, List<String> brands) {
			return itemMappingRepository.fetchManufBrandPartswithBarndItemNumber(businessUnit, partNumber, itemGroups, brands);
	}
	
    public ItemGroup findItemGroupByDescription(String description) {
        return catalogRepository.findItemGroupByDescription(description);
    }

}

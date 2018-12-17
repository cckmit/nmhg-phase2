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
package tavant.twms.domain.supplier;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepository;

public interface ItemMappingRepository extends GenericRepository<ItemMapping, Long> {

    public List<ItemMapping> findItemMappingsForPart(Item item, CalendarDate buildDate);
    
    public ItemMapping findItemMappingForOEMandSupplierItem(Item oemItem, String supplierItemNumber, Supplier supplier);

    public ItemMapping findItemMappingForOEMItem(Item item, Supplier supplier,
            CalendarDate buildDate);

    public Item findOEMItemForSupplierItem(Item supplierItem);

    public Item findPartForOEMDealerPart(final Item toItem, final Organization organization);

    public Item findOEMDealerPartForPart(final Item fromItem, final Organization organization);

    public void updateItemMappings(List<ItemMapping> itemMappingList);

    public List<ItemMapping> findItemMappingForSupplier(Supplier supplier);
    
    public ItemMapping findItemMappingForSupplier(final Item item, final Supplier supplier);

    public List<ItemMapping> findItemMappingsItem(Item item);
    
    public Item findPartForOEMDealerPartUsingItemNumber(String toItemNumber,Organization organization);
    
    public List<Item> fetchManufParts(final String businessUnit, final String partNumber, final int fetchSize, List<Object> itemGroups, boolean onlyServicePart);
    
    public List<BrandItem> fetchManufBrandParts(String businessUnit,
			String partNumber, int fetchSize, List<Object> itemGroups,
			boolean onlyServiceParts, List<String> brands, boolean activeParts);
    
    public List<Item> findSupplierItemsForOEMItem(Item OEMItem);
    
    public void deleteSupplierItemLocation(final Long id);
    
	public BrandItem fetchManufBrandPartswithBarndItemNumber(
			final String businessUnit, final String partNumber,
			final List<Object> itemGroups, final String brand);

	public Item fetchManufPartsUsingPartNumber(final String businessUnit, final String partNumber,final List<Object> itemGroups);

	public List<BrandItem> fetchBrandItemsForbrandPartNumber(
			String businessUnit, String partNumber, List<Object> itemGroups);

	public List<BrandItem> fetchManufBrandPartswithBarndItemNumber(
			String businessUnit, String partNumber, List<Object> itemGroups,
			List<String> brands);
}

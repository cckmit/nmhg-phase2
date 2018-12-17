package tavant.twms.domain.supplier;

import java.util.List;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;

public class ItemMappingServiceImpl extends GenericServiceImpl<ItemMapping, Long, RuntimeException>
        implements ItemMappingService {

    ItemMappingRepository itemMappingRepository;

    CatalogService catalogService;
    
    SupplierItemLocationRepository supplierItemLocationRepository;

    @Override
    public GenericRepository<ItemMapping, Long> getRepository() {
        return itemMappingRepository;
    }

    public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
        this.itemMappingRepository = itemMappingRepository;
    }

    public void updateItemMappings(List<ItemMapping> itemMappingList, Supplier supplier, boolean isNewSupplier)
            throws CatalogException {
    	
    	if(!isNewSupplier) {
    		List<ItemMapping> oldItemMappingList = findItemMappingForSupplier(supplier);

    		if(oldItemMappingList != null && oldItemMappingList.size() > 0){
    			boolean flag = true;
    			for (ItemMapping oldItemMapping : oldItemMappingList) {
    				flag = true;
    				
    				if(itemMappingList != null && itemMappingList.size() > 0){
	    				
    					for (ItemMapping itemMapping : itemMappingList) {
	    					if(oldItemMapping.getId().equals(itemMapping.getId())) {
	    						flag = false;
	    						break;
	    					}
	    				}
    				}
    				if(flag) {
    					delete(oldItemMapping);
    				}
    			}
    		}
    	}
		if (itemMappingList != null && itemMappingList.size() > 0) {
			for (ItemMapping itemMapping : itemMappingList) {
				Item toItem = itemMapping.getToItem();
				Item fromItem = catalogService.findItemOwnedByManuf(itemMapping
						.getFromItem().getNumber());
				itemMapping.setFromItem(fromItem);
				if (toItem.getId() == null) {
					String supplierItemNumber = toItem.getNumber();
					/*if (toItem.getNumber().trim()
							.equalsIgnoreCase(fromItem.getNumber().trim())) {
						supplierItemNumber = createSuffixedPartNumber(supplier,
								supplierItemNumber);
					}*/
/*					SelectedBusinessUnitsHolder
							.setSelectedBusinessUnit(fromItem
									.getBusinessUnitInfo().getName());
*/					Item supplierItem = null;
					try {
						supplierItem = catalogService
								.findItemByItemNumberOwnedByServiceProvider(
										supplierItemNumber, supplier.getId());
						mergeSupplierItem(supplierItem,
								toItem, supplier);
						catalogService.updateItem(supplierItem);
					} catch (CatalogException e) {
						supplierItem = fromItem.cloneMe();
						supplierItem.setNumber(supplierItemNumber);
						if (!supplierItemNumber.contains("#")) {
							supplierItem.setAlternateNumber(supplierItemNumber);
						}
						supplierItem.setOwnedBy(supplier);
						mergeSupplierItem(supplierItem,
								toItem, supplier);
						catalogService.createItem(supplierItem);
					}

					Item persistedItem = catalogService
							.findItemByItemNumberOwnedByServiceProvider(
									supplierItem.getNumber(), supplier.getId());
					itemMapping.setToItem(persistedItem);
				}
			}
			itemMappingRepository.updateItemMappings(itemMappingList);
		}
    }

	private void mergeSupplierItem(Item supplierItem, Item toItem,
			Supplier supplier) {
		supplierItem.setName(toItem.getDescription());
		supplierItem.setDescription(toItem.getDescription());
		supplierItem.setMake(supplier.getName());
		//supplierItem.setSupplierItemLocations(toItem.getSupplierItemLocations());
	}
    
	private String createSuffixedPartNumber(Supplier supplier,
			String supplierItemNumber) {
		String supplierName = supplier.getName();
		if(supplierName.length() > 3){
			supplierItemNumber = supplierItemNumber + "-" + supplierName.substring(0, 3).toUpperCase() + "-" + supplier.getSupplierNumber();
		}else{
			supplierItemNumber = supplierItemNumber + "-" + supplierName.toUpperCase() + "-" + supplier.getSupplierNumber();
		}
		return supplierItemNumber;
	}

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public List<ItemMapping> findItemMappingForSupplier(Supplier supplier) {
        return itemMappingRepository.findItemMappingForSupplier(supplier);
    }

    public List<ItemMapping> findItemMappingForItem(Item item) {
        return itemMappingRepository.findItemMappingsItem(item);
    }
    
    public ItemMapping findItemMappingForOEMItem(Item item, Supplier supplier, CalendarDate buildDate) {
		return itemMappingRepository.findItemMappingForOEMItem(item, supplier, buildDate);
	}
    
    public ItemMapping findItemMappingForOEMandSupplierItem(Item oemItem, String supplierItemNumber, Supplier supplier){
    	return itemMappingRepository.findItemMappingForOEMandSupplierItem(oemItem,supplierItemNumber,supplier);
    }
    
    public void deleteSupplierItemLocation(final Long id){
    	 itemMappingRepository.deleteSupplierItemLocation(id);
    }

	public SupplierItemLocationRepository getSupplierItemLocationRepository() {
		return supplierItemLocationRepository;
	}

	public void setSupplierItemLocationRepository(
			SupplierItemLocationRepository supplierItemLocationRepository) {
		this.supplierItemLocationRepository = supplierItemLocationRepository;
	}
	
}
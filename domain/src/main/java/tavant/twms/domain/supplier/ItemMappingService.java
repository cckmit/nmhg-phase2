package tavant.twms.domain.supplier;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericService;

public interface ItemMappingService extends GenericService<ItemMapping, Long, RuntimeException> {

    @Transactional(readOnly = false)
    public void updateItemMappings(List<ItemMapping> itemMappingList, Supplier supplier, boolean isNewSupplier)
            throws CatalogException;

    public List<ItemMapping> findItemMappingForSupplier(Supplier supplier);

    public List<ItemMapping> findItemMappingForItem(Item item);
    
    public ItemMapping findItemMappingForOEMItem(Item item, Supplier supplier,
            CalendarDate buildDate);
    
    public ItemMapping findItemMappingForOEMandSupplierItem(Item oemItem, String supplierItemNumber, Supplier supplier);
    
    public void deleteSupplierItemLocation(final Long id);
    
}
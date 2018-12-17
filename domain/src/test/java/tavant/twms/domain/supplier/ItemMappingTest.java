package tavant.twms.domain.supplier;

import java.util.List;

import org.hibernate.HibernateException;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.DomainRepositoryTestCase;

public class ItemMappingTest extends DomainRepositoryTestCase {

    ItemMappingRepository itemMappingRepository;

    public void testMappingFind() {
        Item item = (Item) getSession().load(Item.class, new Long(1));
        CalendarDate buildate = Clock.today();
        List<ItemMapping> mappings = itemMappingRepository.findItemMappingsForCausalPart(item,
                buildate);
        assertEquals(4, mappings.size());
        assertEquals("PRTVLV1-NW", mappings.get(0).getToItem().getNumber());
        assertEquals("PRTVLV1-TD", mappings.get(1).getToItem().getNumber());
        assertEquals("PRTVLV1-BY", mappings.get(2).getToItem().getNumber());
        assertEquals("PRTVLV1-ASG", mappings.get(3).getToItem().getNumber());

        // find for an item that doesnt have a mapping
        item = (Item) getSession().load(Item.class, new Long(20));
        assertTrue(itemMappingRepository.findItemMappingsForCausalPart(item, buildate).isEmpty());
    }

    public void testMappingFindWithSupplier() {
        Supplier supplier = (Supplier) getSession().load(Supplier.class, new Long(34));
        Item item = (Item) getSession().load(Item.class, new Long(1));
        CalendarDate buildate = Clock.today();
        ItemMapping itemMapping = itemMappingRepository.findItemMappingForOEMItem(item, supplier,
                buildate);
        assertEquals("PRTVLV1-TD", itemMapping.getToItem().getNumber());

        // look for non existing supplier
        item = (Item) getSession().load(Item.class, new Long(20));
        try {
            itemMappingRepository.findItemMappingForOEMItem(item, supplier, buildate);
            fail("dint threw exception");
        } catch (HibernateException e) {
            // pass
        }
    }

    public void testOEMItemForSupplierItem() {
        Item supplierItem = (Item) getSession().load(Item.class, new Long(2));
        // Load Northwind item
        assertEquals("PRTVLV1-NW", supplierItem.getNumber());
        Item oemItem = itemMappingRepository.findOEMItemForSupplierItem(supplierItem);
        assertEquals("PRTVLV1", oemItem.getNumber());
    }

    public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
        this.itemMappingRepository = itemMappingRepository;
    }
}

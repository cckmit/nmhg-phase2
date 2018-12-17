package tavant.twms.inventory;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.infra.IntegrationTestCase;

public class BomTest extends IntegrationTestCase {
    private InventoryService inventoryService;

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void testCreatingInventoryItemComposition() throws ItemNotFoundException {
        login("sedinap");
        InventoryItem machine = inventoryService.findInventoryItemBySerialNumber("A4304977");
        InventoryItem part = new InventoryItem();
        part.setHoursOnMachine(new Long (24));
        part.setSerialNumber("123321");
        part.setOwnershipState(machine.getOwnershipState());
        part.setType(InventoryType.RETAIL);
        part.setOfType(machine.getOfType());
        part.setConditionType(InventoryItemCondition.NEW);
        part.setCurrentOwner(machine.getCurrentOwner());
        part.setLatestBuyer(machine.getLatestBuyer());
        part.setBusinessUnitInfo(machine.getBusinessUnitInfo());
        part.setManufacturingSiteInventory(machine.getManufacturingSiteInventory());
        part.setSourceWarehouse(machine.getSourceWarehouse());
        part.setFactoryOrderNumber(machine.getFactoryOrderNumber());
        part.setBuiltOn(machine.getBuiltOn());
        part.setDeliveryDate(machine.getDeliveryDate());
        part.setFactoryOrderNumber(machine.getFactoryOrderNumber());
        part.setShipmentDate(machine.getShipmentDate());
        part.setSerializedPart(true);

        machine.include(part);

        inventoryService.createInventoryItem(part);
        inventoryService.updateInventoryItem(machine);
        assertNotNull(part.getId());
        flushAndClear();
        machine = inventoryService.findInventoryItem(part.getId());
    }

    public void testRetrievingInvItemCompForInventory() throws ItemNotFoundException {
        InventoryItem dummyInventoryItem = inventoryService.findInventoryItemBySerialNumber("A4304977");
    }
}
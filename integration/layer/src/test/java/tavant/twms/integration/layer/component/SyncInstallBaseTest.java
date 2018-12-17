package tavant.twms.integration.layer.component;

import static tavant.twms.integration.layer.util.CalendarUtil.convertToCalendarDate;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import tavant.oagis.ShipmentScheduleDocumentDTO;
import tavant.oagis.SyncShipmentScheduleDocumentDTO;
import tavant.oagis.ShipmentScheduleDocumentDTO.ShipmentSchedule;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;

public class SyncInstallBaseTest extends IntegrationRepositoryTestCase {

    InventoryService inventoryService;

    SyncInstallBase syncInstallBase;

    public void testSyncNewInventoryItem() throws IOException, XmlException, ItemNotFoundException {
    	ShipmentSchedule dto = getInventoryDTO("/installbasesync/InstallBaseRequest-DROPSHIPMENT.xml");
        syncInventory(dto);
        flushAndClear();
        ShipmentSchedule dto2 = getInventoryDTO("/installbasesync/InstallBaseRequest-DROPSHIPMENT2.xml");
        syncInventory(dto2);
        InventoryItem invFromDB = inventoryService.findSerializedItem(dto.getLine().getItem().getUserArea().getSerialNumber());
        verifyInvFromDB(invFromDB, dto, 1);
        flushAndClear();
    }


    private ShipmentSchedule getInventoryDTO(String invXmlPath) throws XmlException, IOException {
    	SyncShipmentScheduleDocumentDTO syncShipmentScheduleDocumentDTO = SyncShipmentScheduleDocumentDTO.Factory.parse(
        		SyncInstallBaseTest.class.getResourceAsStream(invXmlPath));
        return syncShipmentScheduleDocumentDTO.getSyncShipmentSchedule().getDataArea().getShipmentSchedule();
    }

    private void syncInventory(ShipmentSchedule dto) throws XmlException, IOException {
    	syncInstallBase.sync(dto);
    }

    private void verifyInvFromDB(InventoryItem invFromDB, ShipmentSchedule dto, int noOfInvTxs) throws ItemNotFoundException {
        assertEquals(invFromDB.getOfType().getNumber(), dto.getLine().getItem().getItemId().getId());
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


	public void setSyncInstallBase(SyncInstallBase syncInstallBase) {
		this.syncInstallBase = syncInstallBase;
	}


}

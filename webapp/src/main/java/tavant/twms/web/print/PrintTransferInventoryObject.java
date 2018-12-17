package tavant.twms.web.print;

import tavant.twms.domain.inventory.InventoryItem;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Jun 4, 2009
 * Time: 3:14:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrintTransferInventoryObject {
    private InventoryItem inventoryItem;
    private String deliveryDate;
    private String shipmentDate;
    private String installationDate;
    private String vinNumber;
    private String fleetNumber;

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}

	public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public String getFleetNumber() {
		return fleetNumber;
	}

	public void setFleetNumber(String fleetNumber) {
		this.fleetNumber = fleetNumber;
	}	
    
}

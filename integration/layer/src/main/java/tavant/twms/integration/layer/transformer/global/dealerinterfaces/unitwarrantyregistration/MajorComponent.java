package tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitwarrantyregistration;

import java.util.Calendar;

import tavant.twms.domain.catalog.Item;

public class MajorComponent {
	private String serialNumber;
	private Item item;
	private Calendar installationDate;
	
	public MajorComponent(String serialNumber, Item item, Calendar installationDate) {
		this.serialNumber = serialNumber;
		this.item = item;
		this.installationDate = installationDate;
	}
	
	public final String getSerialNumber() {
		return serialNumber;
	}

	public final Item getItem() {
		return item;
	}
	
	public final Calendar getInstallationDate() {
		return installationDate;
	}
	
}

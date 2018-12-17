package tavant.twms.web.print;

public class AttachmentObject {
	
	private String type;
	private String manufacturer;
	private String model;
	private String serialNumber;
	
	public AttachmentObject(String type, String manufacturer, String model,
			String serialNumber) {
		super();
		this.type = type;
		this.manufacturer = manufacturer;
		this.model = model;
		this.serialNumber = serialNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}

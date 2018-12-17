package tavant.twms.web.print;

public class MajorComponentsObject {

	private String type;
	
	private String description;
	
	private String serialNumber;

	public MajorComponentsObject(String type, String description,
			String serialNumber) {
		super();
		this.type = type;
		this.description = description;
		this.serialNumber = serialNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}

package tavant.twms.web.print;

public class CarriageObject {
	
	private String partNumber;
	private String serialNumber;
	private String dateCode;
	private String subType;
	
	
	public CarriageObject(String partNumber, String serialNumber,
			String dateCode, String subType) {
		super();
		this.partNumber = partNumber;
		this.serialNumber = serialNumber;
		this.dateCode = dateCode;
		this.subType = subType;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDateCode() {
		return dateCode;
	}
	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}

}

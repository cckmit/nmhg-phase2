package tavant.twms.domain.policy;

public class ExtWarrantyPurchase {
	
	private String NotificationType;
	
	private String invoiceDate;
	
	private String invoiceNumber;
	
	private String salesOrderLineNumber;
	
	private String salesOrderNumber;
	
	private String warrantyPlanCode;
	
	private String warrantyItemNumber;
	
	private String equipmentItemNumber;
	
	private String serialNumber;
	
	private String dealerNumber;

    private String buName;

    public String getNotificationType() {
		return NotificationType;
	}

	public void setNotificationType(String notificationType) {
		NotificationType = notificationType;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getSalesOrderLineNumber() {
		return salesOrderLineNumber;
	}

	public void setSalesOrderLineNumber(String salesOrderLineNumber) {
		this.salesOrderLineNumber = salesOrderLineNumber;
	}

	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	public String getWarrantyPlanCode() {
		return warrantyPlanCode;
	}

	public void setWarrantyPlanCode(String warrantyPlanCode) {
		this.warrantyPlanCode = warrantyPlanCode;
	}

	public String getWarrantyItemNumber() {
		return warrantyItemNumber;
	}

	public void setWarrantyItemNumber(String warrantyItemNumber) {
		this.warrantyItemNumber = warrantyItemNumber;
	}

	public String getEquipmentItemNumber() {
		return equipmentItemNumber;
	}

	public void setEquipmentItemNumber(String equipmentItemNumber) {
		this.equipmentItemNumber = equipmentItemNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }
}

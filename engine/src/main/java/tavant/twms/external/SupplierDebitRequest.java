package tavant.twms.external;

import com.domainlanguage.money.Money;

public class SupplierDebitRequest {

	private String lineContext;
	
	private String warrantyClaimNumber;
	
	private String recoveryItemNumber;
	
	private String warrantyClaimType;
	
	private String warrantySerialNumber;
	
	private String batchSourceName;
	
	private String supplierNumber;
	
	private Money totalAmount;

	public String getLineContext() {
		return lineContext;
	}

	public void setLineContext(String lineContext) {
		this.lineContext = lineContext;
	}

	public String getWarrantyClaimNumber() {
		return warrantyClaimNumber;
	}

	public void setWarrantyClaimNumber(String warrantyClaimNumber) {
		this.warrantyClaimNumber = warrantyClaimNumber;
	}

	public String getRecoveryItemNumber() {
		return recoveryItemNumber;
	}

	public void setRecoveryItemNumber(String recoveryItemNumber) {
		this.recoveryItemNumber = recoveryItemNumber;
	}

	public String getWarrantyClaimType() {
		return warrantyClaimType;
	}

	public void setWarrantyClaimType(String warrantyClaimType) {
		this.warrantyClaimType = warrantyClaimType;
	}

	public String getWarrantySerialNumber() {
		return warrantySerialNumber;
	}

	public void setWarrantySerialNumber(String warrantySerialNumber) {
		this.warrantySerialNumber = warrantySerialNumber;
	}

	public String getBatchSourceName() {
		return batchSourceName;
	}

	public void setBatchSourceName(String batchSourceName) {
		this.batchSourceName = batchSourceName;
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
	
}

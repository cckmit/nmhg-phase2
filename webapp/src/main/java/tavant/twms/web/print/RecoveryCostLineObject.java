package tavant.twms.web.print;

import com.domainlanguage.money.Money;

public class RecoveryCostLineObject {
	private String costElement;
	private Money warrantyClaimValue;
	private Money contractValue;
	private Money actualValue;
	private Money askedValue;
	private boolean bold=false;
	RecoveryCostLineObject(){
		super();
	}
	public String getCostElement() {
		return costElement;
	}
	public void setCostElement(String costElement) {
		this.costElement = costElement;
	}
	public Money getWarrantyClaimValue() {
		return warrantyClaimValue;
	}
	public void setWarrantyClaimValue(Money warrantyClaimValue) {
		this.warrantyClaimValue = warrantyClaimValue;
	}
	public Money getContractValue() {
		return contractValue;
	}
	public void setContractValue(Money contractValue) {
		this.contractValue = contractValue;
	}
	public Money getActualValue() {
		return actualValue;
	}
	public void setActualValue(Money actualValue) {
		this.actualValue = actualValue;
	}
	public Money getAskedValue() {
		return askedValue;
	}
	public void setAskedValue(Money askedValue) {
		this.askedValue = askedValue;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
}

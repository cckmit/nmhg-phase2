package tavant.twms.domain.policy;

import com.domainlanguage.money.Money;

public class ExtWarrantyPlan {
	
	private String planCode;
	
	private String planItemNumber;
	
	private Money amount;
	
	private Money taxAmount;

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getPlanItemNumber() {
		return planItemNumber;
	}

	public void setPlanItemNumber(String planItemNumber) {
		this.planItemNumber = planItemNumber;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public Money getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Money taxAmount) {
		this.taxAmount = taxAmount;
	}

}

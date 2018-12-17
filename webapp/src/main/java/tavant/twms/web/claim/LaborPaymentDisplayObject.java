package tavant.twms.web.claim;

import java.math.BigDecimal;

import com.domainlanguage.money.Money;

public class LaborPaymentDisplayObject {
	
	private String laborType;
	
	private BigDecimal laborHrs;
	
	private BigDecimal multiplicationValue;
	
	private Money amount;

	public Money getAmount() {
		return amount;
	}
	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public BigDecimal getLaborHrs() {
		return laborHrs;
	}
	public void setLaborHrs(BigDecimal laborHrs) {
		this.laborHrs = laborHrs;
	}

	public String getLaborType() {
		return laborType;
	}
	public void setLaborType(String laborType) {
		this.laborType = laborType;
	}

	public BigDecimal getMultiplicationValue() {
		return multiplicationValue;
	}
	public void setMultiplicationValue(BigDecimal multiplicationValue) {
		this.multiplicationValue = multiplicationValue;
	}
}

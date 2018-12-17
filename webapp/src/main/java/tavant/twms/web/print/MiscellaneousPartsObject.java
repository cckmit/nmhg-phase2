package tavant.twms.web.print;

import com.domainlanguage.money.Money;

public class MiscellaneousPartsObject {
	
	private String description;
	
	private Money pricePerUnit;
	
	private Integer numberOfUnits;
	
	private Money actualValue;
	
	private boolean showRecoveryClaim=true;

	public boolean getShowRecoveryClaim() {
		return showRecoveryClaim;
	}

	public void setShowRecoveryClaim(boolean showRecoveryClaim) {
		this.showRecoveryClaim = showRecoveryClaim;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Money getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(Money pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public Integer getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(Integer numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public Money getActualValue() {
		return actualValue;
	}

	public void setActualValue(Money actualValue) {
		this.actualValue = actualValue;
	}
}

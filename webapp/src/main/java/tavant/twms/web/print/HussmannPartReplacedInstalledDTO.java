package tavant.twms.web.print;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.domainlanguage.money.Money;

import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;

public class HussmannPartReplacedInstalledDTO {
	
	private String partNumber;
	 
	private String partToBeReturned;
	
	private String returnLocation;
	
	private Integer quantity;
	
	private Money pricePerUnit;
	
	private String pricePerUnitCurrencyValue;
	
	
	private String standardCostCurrencyValue;
	
	
	private String description;
	
	private Money dealerNetPrice;
	
	private Money standardCost;
	
	
	private String identifier;
	
	private String serialNumber;
	
	private String paymentCondition;
	
	private String dateCode;

	public HussmannPartReplacedInstalledDTO() {
		
	}
	
	public HussmannPartReplacedInstalledDTO( OEMPartReplaced replacedParts,String brand) {
		if(replacedParts.getItemReference().isSerialized()){
		  this.partNumber =replacedParts.getBrandItem().getItemNumber();
		  this.description = replacedParts.getItemReference().getReferredInventoryItem().getOfType().getDescription();
		}else{
			this.partNumber = replacedParts.getBrandItem().getItemNumber();
			this.description = replacedParts.getItemReference().getReferredItem().getDescription();
		}
		this.quantity = replacedParts.getNumberOfUnits();
		this.dateCode = replacedParts.getDateCode();
		 this.standardCost=replacedParts.getCostPricePerUnit();
		   this.standardCostCurrencyValue = replacedParts.getCostPricePerUnit().breachEncapsulationOfCurrency().getCurrencyCode().toString()+replacedParts.getCostPricePerUnit().breachEncapsulationOfAmount().toString();
		if(replacedParts.getSerialNumber() != null && !StringUtils.isEmpty(replacedParts.getSerialNumber()))
		   this.serialNumber = replacedParts.getSerialNumber();
		this.partToBeReturned = replacedParts.isPartToBeReturned() == true ? "Yes":"No";
		if (this.partToBeReturned == "Yes" && !CollectionUtils.isEmpty(replacedParts.getPartReturns())) {
			this.returnLocation = replacedParts.getPartReturns().get(0).getReturnLocation()==null ? "--"
					: replacedParts.getPartReturns().get(0).getReturnLocation().getCode();
			// this.returnLocation =
			// replacedParts.getPartReturn().getReturnLocation().getCode();

		}
		if (!CollectionUtils.isEmpty(replacedParts.getPartReturns())&&
				replacedParts.getPartReturns().get(0).getPaymentCondition()!=null
				&& !StringUtils.isEmpty( replacedParts.getPartReturns().get(0)
					.getPaymentCondition().getDescription())) {
			this.paymentCondition = replacedParts.getPartReturns().get(0)
					.getPaymentCondition().getDescription();
		}
		this.identifier = "Removed Parts";
	}
	
	public HussmannPartReplacedInstalledDTO( InstalledParts installedParts, boolean isHussmann, boolean iscanBUPartBeReplacedByNonBUPart,String brand) {
		this.quantity = installedParts.getNumberOfUnits();
		this.dateCode = installedParts.getDateCode();
		this.pricePerUnitCurrencyValue=installedParts.getPricePerUnit().breachEncapsulationOfCurrency().getCurrencyCode().toString() +installedParts.getPricePerUnit().breachEncapsulationOfAmount().toString();
        this.dealerNetPrice=installedParts.getCostPricePerUnit();
        this.standardCost=installedParts.getCostPricePerUnit();
        this.standardCostCurrencyValue = installedParts.getCostPricePerUnit().breachEncapsulationOfCurrency().getCurrencyCode().toString()+installedParts.getCostPricePerUnit().breachEncapsulationOfAmount().toString();
		if(installedParts.getSerialNumber() != null && !StringUtils.isEmpty(installedParts.getSerialNumber()))
		   this.serialNumber = installedParts.getSerialNumber();
		if( isHussmann){
			this.partNumber = installedParts.getBrandItem().getItemNumber();
			this.identifier = "Installed Parts";
			this.description =installedParts.getBrandItem().getItem().getDescription();
		} else {
			if(iscanBUPartBeReplacedByNonBUPart){
				this.partNumber = installedParts.getPartNumber();
				this.identifier = "Non Hussmann Parts";
				this.description = installedParts.getDescription();
			}
		}
		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPartNumber() {
		return partNumber;	
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	
	public String getReturnLocation() {
		return returnLocation;
	}

	public void setReturnLocation(String returnLocation) {
		this.returnLocation = returnLocation;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	public String getPartToBeReturned() {
		return partToBeReturned;
	}

	public void setPartToBeReturned(String partToBeReturned) {
		this.partToBeReturned = partToBeReturned;
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
	
	public String getPaymentCondition() {
		return paymentCondition;
	}

	public void setPaymentCondition(String paymentCondition) {
		this.paymentCondition = paymentCondition;
	}

	/**
	 * @return the dealerNetPrice
	 */
	public Money getDealerNetPrice() {
		return dealerNetPrice;
	}

	/**
	 * @param dealerNetPrice the dealerNetPrice to set
	 */
	public void setDealerNetPrice(Money dealerNetPrice) {
		this.dealerNetPrice = dealerNetPrice;
	}

	/**
	 * @return the standardCost
	 */
	public Money getStandardCost() {
		return standardCost;
	}

	/**
	 * @param standardCost the standardCost to set
	 */
	public void setStandardCost(Money standardCost) {
		this.standardCost = standardCost;
	}

	/**
	 * @return the pricePerUnitCurrencyValue
	 */
	public String getPricePerUnitCurrencyValue() {
		return pricePerUnitCurrencyValue;
	}

	/**
	 * @param pricePerUnitCurrencyValue the pricePerUnitCurrencyValue to set
	 */
	public void setPricePerUnitCurrencyValue(String pricePerUnitCurrencyValue) {
		this.pricePerUnitCurrencyValue = pricePerUnitCurrencyValue;
	}

	/**
	 * @return the standardCostCurrencyValue
	 */
	public String getStandardCostCurrencyValue() {
		return standardCostCurrencyValue;
	}

	/**
	 * @param standardCostCurrencyValue the standardCostCurrencyValue to set
	 */
	public void setStandardCostCurrencyValue(String standardCostCurrencyValue) {
		this.standardCostCurrencyValue = standardCostCurrencyValue;
	}

	

	
}

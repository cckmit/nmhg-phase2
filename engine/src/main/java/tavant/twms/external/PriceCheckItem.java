package tavant.twms.external;

import tavant.twms.domain.catalog.ItemUOMTypes;

import com.domainlanguage.money.Money;

public class PriceCheckItem {

    //For both request and response
	private String partNumber;

	private int quantity;

    private ItemUOMTypes uom;

    //Only for price request
    private String manufacturingLocation;

    //Only for cost request
    private String supplierNumber;

    private String currencyCode;

    //For price and cost response
    private String statusCode;

    private String errorMessage;

    //Only for price response
    private Money listPrice;
	
	private Money adjustedListPrice;
	
	//Only for cost response
    private Money standardCost;
	
	private Money materialCost;

    public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Money getListPrice() {
		return listPrice;
	}

	public void setListPrice(Money listPrice) {
		this.listPrice = listPrice;
	}

	public Money getMaterialCost() {
		return materialCost;
	}

	public void setMaterialCost(Money materialCost) {
		this.materialCost = materialCost;
	}

	public Money getStandardCost() {
		return standardCost;
	}

	public void setStandardCost(Money standardCost) {
		this.standardCost = standardCost;
	}

	public Money getAdjustedListPrice() {
		return adjustedListPrice;
	}

	public void setAdjustedListPrice(Money adjustedListPrice) {
		this.adjustedListPrice = adjustedListPrice;
	}
	
	public ItemUOMTypes getUom() {
		return uom;
	}

	public void setUom(ItemUOMTypes uom) {
		this.uom = uom;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public String getManufacturingLocation() {
        return manufacturingLocation;
    }

    public void setManufacturingLocation(String manufacturingLocation) {
        this.manufacturingLocation = manufacturingLocation;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}

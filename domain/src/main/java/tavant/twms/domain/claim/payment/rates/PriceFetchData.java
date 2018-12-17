package tavant.twms.domain.claim.payment.rates;

import com.domainlanguage.money.Money;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;

public class PriceFetchData {
	
	private Item item;
	
	private BrandItem brandItem;
	
	private Money itemPrice;
	
	private Money listPrice;
	
	private Money materialPrice;
	
	private Money standardCost;
	
	private Money adjustedListPrice;
	
	private Item supplierItem;

    private Integer quantity;
    
    private Boolean priceUpdated = Boolean.FALSE;
    
    private Boolean isInstalledPart = Boolean.FALSE;

    public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Money getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Money itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Money getListPrice() {
		return listPrice;
	}

	public void setListPrice(Money listPrice) {
		this.listPrice = listPrice;
	}

	public Money getMaterialPrice() {
		return materialPrice;
	}

	public void setMaterialPrice(Money materialPrice) {
		this.materialPrice = materialPrice;
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

	public Item getSupplierItem() {
		return supplierItem;
	}

	public void setSupplierItem(Item supplierItem) {
		this.supplierItem = supplierItem;
	}

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

	public BrandItem getBrandItem() {
		return brandItem;
	}

	public void setBrandItem(BrandItem brandItem) {
		this.brandItem = brandItem;
	}

	public Boolean getPriceUpdated() {
		return priceUpdated;
	}

	public void setPriceUpdated(Boolean priceUpdated) {
		this.priceUpdated = priceUpdated;
	}

	public Boolean getIsInstalledPart() {
		return isInstalledPart;
	}

	public void setIsInstalledPart(Boolean isInstalledPart) {
		this.isInstalledPart = isInstalledPart;
	}
}

/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 *   
 *   @author shraddha.nanda
 */
package tavant.twms.domain.claim;

import java.math.BigDecimal;

import javax.persistence.*;

import com.domainlanguage.money.Money;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.customReports.CustomReportAnswer;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name="INSTALLED_PARTS")
public class InstalledParts extends PartReplaced {
	
	// item object for the OEM parts.
	@ManyToOne(cascade = { CascadeType.ALL },fetch=FetchType.LAZY)
	@JoinColumn(name="item")
	private Item item ;
	
	// partnumber,price and description fields are used only for non OEM parts.
	private String partNumber;
	
	private String serialNumber;
	
	//Adding as part of SLMS-776
	private String dateCode;
	
	private String description;
	
	private BigDecimal price;
	
	@ManyToOne(fetch = FetchType.LAZY)
    private Item supplierItem;
	
	@ManyToOne(fetch = FetchType.LAZY)
    private Item oemDealerPartReplaced;
	
	private Boolean isHussmanPart;
	
	private Boolean priceUpdated = Boolean.FALSE;
	
    /**
     * This flag when set would set the price of the OEM Part replace to 0 as
     * the part would have been shipped already by the OEM and hence no cost
     * against the part could be claimed. Applicable to Campaign Claims. This
     * flag would be set at the time of campaign setup.
     */
    private boolean shippedByOem;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( {org.hibernate.annotations.CascadeType.ALL})
    private CustomReportAnswer customReportAnswer;
	
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL })
    private Document invoice;    
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public Boolean getIsHussmanPart() {
		return isHussmanPart;
	}

	public void setIsHussmanPart(Boolean isHussmanPart) {
		this.isHussmanPart = isHussmanPart;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@SuppressWarnings("static-access")
	public Money getCostPrice() {
		Money priceAmount = Money.valueOf(price, GlobalConfiguration.getInstance().getBaseCurrency());
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		if (this.numberOfUnits == null || this.price == null) {
			return globalConfiguration.zeroInBaseCurrency();
		}
		return this.numberOfUnits != null ? priceAmount.times(this.numberOfUnits)
				: globalConfiguration.zeroInBaseCurrency();
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@Transient
    public boolean isPartRecoveredFromSupplier(Contract contract, Item causalItem, ClaimType type) {
        if(type != null && ClaimType.CAMPAIGN.getType().equalsIgnoreCase(type.getType())){
        	return true;
        }
		return (causalItem != null && causalItem.getId() == this.item.getId())
                || (contract != null && contract.getCollateralDamageToBePaid() != null && contract
                        .getCollateralDamageToBePaid());
    }

	public Item getSupplierItem() {
		return supplierItem;
	}

	public void setSupplierItem(Item supplierItem) {
		this.supplierItem = supplierItem;
	}

	public Item getOemDealerPartReplaced() {
		return oemDealerPartReplaced;
	}

	public void setOemDealerPartReplaced(Item oemDealerPartReplaced) {
		this.oemDealerPartReplaced = oemDealerPartReplaced;
	}
	
	public boolean isPartBelongsToSupplier(Contract contract) {
        return this.supplierItem != null
                && this.supplierItem.getOwnedBy().getId() == contract.getSupplier().getId();
    }

	public boolean isShippedByOem() {
		return shippedByOem;
	}

	public void setShippedByOem(boolean shippedByOem) {
		this.shippedByOem = shippedByOem;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

    public CustomReportAnswer getCustomReportAnswer() {
        return customReportAnswer;
    }

    public void setCustomReportAnswer(CustomReportAnswer customReportAnswer) {
        this.customReportAnswer = customReportAnswer;
    }
    
    public InstalledParts clone(){
    	InstalledParts installedParts = new InstalledParts(); 
    	installedParts.setCostPricePerUnit(this.costPricePerUnit);
    	installedParts.setD(d);
    	installedParts.setDescription(description);
    	installedParts.setInventoryLevel(inventoryLevel);
    	installedParts.setIsHussmanPart(isHussmanPart);
    	installedParts.setItem(item);
    	installedParts.setMaterialCost(materialCost);
    	installedParts.setNumberOfUnits(numberOfUnits);
    	installedParts.setOemDealerPartReplaced(oemDealerPartReplaced);
    	installedParts.setPrice(price);
    	installedParts.setPricePerUnit(pricePerUnit);
    	installedParts.setReadOnly(readOnly);
    	installedParts.setSerialNumber(serialNumber);
    	installedParts.setShippedByOem(shippedByOem);
    	installedParts.setSupplierItem(supplierItem);
        installedParts.setBrandItem(brandItem);
    	return installedParts;
    }

	public Document getInvoice() {
		return invoice;
	}

	public void setInvoice(Document invoice) {
		this.invoice = invoice;
	}    
	
	@Transient
	public Money getUomAdjustedPricePerUnit(){
		if(pricePerUnit==null)
			return Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
		if(this.uomMapping !=null && this.uomMapping.getMappingFraction() !=null && this.pricePerUnit != null){
			return this.pricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
		} else {
			return this.pricePerUnit;
		}
		
	}
    @ManyToOne(fetch=FetchType.LAZY,optional=true)
	protected UomMappings uomMapping;
	
	public UomMappings getUomMapping() {
		return uomMapping;
	}

	public void setUomMapping(UomMappings uomMapping) {
		this.uomMapping = uomMapping;
	}
	
	@Transient
	public Money getUomAdjustedCostPrice(String costPriceType){
		if(CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType)){
			if(this.uomMapping !=null && this.uomMapping.getMappingFraction() !=null && this.materialCost!= null){
				return this.materialCost.dividedBy(this.uomMapping.getMappingFraction());
			} else {
				return this.materialCost;
			}
		}else if(CompensationTerm.STARDARD_COST.equalsIgnoreCase(costPriceType)){
			if(this.uomMapping !=null && this.uomMapping.getMappingFraction() !=null && this.costPricePerUnit != null){
				return this.costPricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
			} else {
				return this.costPricePerUnit;
			}
		}else {
			if(this.uomMapping !=null && this.uomMapping.getMappingFraction() !=null && this.costPricePerUnit != null){
				return this.costPricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
			} else {
				return this.costPricePerUnit;
			}
		}
		
	}

    public Money cost() {
        Money priceWithUomCorrection = super.cost();
        if (this.uomMapping != null && !priceWithUomCorrection.isZero()) {
            priceWithUomCorrection = priceWithUomCorrection.dividedBy(this.uomMapping.getMappingFraction().doubleValue());
        }
        return priceWithUomCorrection;
    }

	public String getDateCode() {
		return dateCode;
	}

	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}

	public Boolean getPriceUpdated() {
		return priceUpdated;
	}

	public void setPriceUpdated(Boolean priceUpdated) {
		this.priceUpdated = priceUpdated;
	}

   /* private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }*/

    @ManyToOne(cascade = { CascadeType.ALL },fetch=FetchType.LAZY)
    @JoinColumn(name="brand_item")
    private BrandItem brandItem ;

    public BrandItem getBrandItem() {
        return brandItem;
    }

    public void setBrandItem(BrandItem brandItem) {
        this.brandItem = brandItem;
    }
}

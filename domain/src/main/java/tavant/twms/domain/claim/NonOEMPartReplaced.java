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
 */
package tavant.twms.domain.claim;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.common.Document;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Table(name = "NON_OEM_PART_REPLACED")
public class NonOEMPartReplaced extends PartReplaced {
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.EVICT, org.hibernate.annotations.CascadeType.LOCK, org.hibernate.annotations.CascadeType.REPLICATE, org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    private Document invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private MiscellaneousItemConfiguration miscItemConfig;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private MiscellaneousItem miscItem;
    
    @Transient
    private String totalAmount;
    
    @Transient
    private String pricePerUnitCurrency;


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Document getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Document invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("description",
                this.description).toString();
    }

    public MiscellaneousItemConfiguration getMiscItemConfig() {
        return miscItemConfig;
    }

    public void setMiscItemConfig(MiscellaneousItemConfiguration miscItemConfig) {
        this.miscItemConfig = miscItemConfig;
    }

    public MiscellaneousItem getMiscItem() {
        return miscItem;
    }

    public void setMiscItem(MiscellaneousItem miscItem) {
        this.miscItem = miscItem;
    }

    public NonOEMPartReplaced clone() {
        NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
        nonOEMPartReplaced.setDescription(description);
        nonOEMPartReplaced.setInvoice(invoice);
        nonOEMPartReplaced.setMiscItem(miscItem);
        nonOEMPartReplaced.setMiscItemConfig(miscItemConfig);
        nonOEMPartReplaced.setCostPricePerUnit(costPricePerUnit);
        nonOEMPartReplaced.setInventoryLevel(inventoryLevel);
        nonOEMPartReplaced.setMaterialCost(materialCost);
        nonOEMPartReplaced.setNumberOfUnits(numberOfUnits);
        nonOEMPartReplaced.setPricePerUnit(pricePerUnit);
        nonOEMPartReplaced.setReadOnly(readOnly);
        return nonOEMPartReplaced;

    }
    
	public BigDecimal getTotalAmount(){
		
		return this.pricePerUnit.times(this.numberOfUnits).breachEncapsulationOfAmount();
		
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the pricePerUnitCurrency
	 */
	public String getPricePerUnitCurrency() {
		return this.pricePerUnit.breachEncapsulationOfCurrency().toString()+this.pricePerUnit.breachEncapsulationOfAmount().toString();
	}

	/**
	 * @param pricePerUnitCurrency the pricePerUnitCurrency to set
	 */
	public void setPricePerUnitCurrency(String pricePerUnitCurrency) {
		this.pricePerUnitCurrency = pricePerUnitCurrency;
	}

}

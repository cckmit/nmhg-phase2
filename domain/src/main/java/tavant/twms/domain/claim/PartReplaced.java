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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.ExcludeConversion;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PartReplaced implements AuditableColumns{
	@Id
	@GeneratedValue(generator = "PartReplaced")
	@GenericGenerator(name = "PartReplaced", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_PartReplaced"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
	protected Long id;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "price_per_unit_amt"),
			@Column(name = "price_per_unit_curr") })
	protected Money pricePerUnit;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "cost_price_per_unit_amt"),
			@Column(name = "cost_price_per_unit_curr") })
	@ExcludeConversion		
	protected Money costPricePerUnit;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "material_cost_amt"),
			@Column(name = "material_cost_curr") })
	@ExcludeConversion
	protected Money materialCost;

	protected Integer numberOfUnits;

	@Column(nullable = false)
	protected Boolean inventoryLevel=Boolean.FALSE;
	
	/**
	 * This parameter has been added to ensure that processor/dealer cannot remove parts
	 * currently used by foc claims of hussmann where parts are synced in from hussmann after market
	 * system which should not be removed
	 * 
	 */
	
	protected Boolean readOnly = Boolean.FALSE;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();


	/**
	 * @return the id
	 */

	public Long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the numberOfUnits
	 */
	public Integer getNumberOfUnits() {
		return this.numberOfUnits;
	}

	/**
	 * @param numberOfUnits
	 *            the numberOfUnits to set
	 */
	public void setNumberOfUnits(Integer numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	/**
	 * @return the pricePerUnit
	 */
	public Money getPricePerUnit() {
		return this.pricePerUnit;
	}

	/**
	 * @param pricePerUnit
	 *            the pricePerUnit to set
	 */
	public void setPricePerUnit(Money pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public Money costAtCP(String costPriceType) {
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		if (this.numberOfUnits == null || (this.costPricePerUnit == null && this.materialCost == null)) {
			return globalConfiguration.zeroInBaseCurrency();
		}
		if(CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType)){
			return this.numberOfUnits != null ? this.materialCost.times(this.numberOfUnits)
					: globalConfiguration.zeroInBaseCurrency();
		}else if(CompensationTerm.STARDARD_COST.equalsIgnoreCase(costPriceType)){
			return this.numberOfUnits != null ? this.costPricePerUnit.times(this.numberOfUnits)
					: globalConfiguration.zeroInBaseCurrency();
		}else{
			// TODO: this is a hack. The null check needs to be ridden of.
			return this.numberOfUnits != null ? this.costPricePerUnit.times(this.numberOfUnits)
					: globalConfiguration.zeroInBaseCurrency();
		}
	}

	public Money cost() {
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		if (this.numberOfUnits == null || this.pricePerUnit == null) {
			return globalConfiguration.zeroInBaseCurrency();
		}
		// TODO: this is a hack. The null check needs to be ridden of.
		return this.numberOfUnits != null ? this.pricePerUnit.times(this.numberOfUnits)
				: globalConfiguration.zeroInBaseCurrency();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.id).append(
				"price per unit", this.pricePerUnit).append("number of units",
				this.numberOfUnits).toString();
	}

	public Money getCostPricePerUnit() {
		return this.costPricePerUnit;
	}

	public void setCostPricePerUnit(Money costPrice) {
		this.costPricePerUnit = costPrice;
	}

	public Boolean getInventoryLevel() {
		return this.inventoryLevel;
	}

	public void setInventoryLevel(Boolean inventoryLevel) {
		this.inventoryLevel = inventoryLevel;
	}

	public Money getMaterialCost() {
		return this.materialCost;
	}

	public void setMaterialCost(Money materialCost) {
		this.materialCost = materialCost;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	
    public Money moneyValueOf(double amount) {
        return Money.valueOf(amount, GlobalConfiguration.getInstance().getBaseCurrency());
    }

}

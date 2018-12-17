/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.supplier;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

/**
 * @author kannan.ekanath
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class CostLineItem implements AuditableColumns, BUSpecificSectionNames{

    @Id
    @GeneratedValue(generator = "CostLineItem")
	@GenericGenerator(name = "CostLineItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "COST_LINE_ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "cost_amt"), @Column(name = "cost_curr") })
    private Money actualCost;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "contract_cost_amt"), @Column(name = "contract_cost_curr") })
    private Money costAfterApplyingContract;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "recovered_cost_amt"),
            @Column(name = "recovered_cost_curr") })
    private Money recoveredCost;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "supplier_cost_amt"),
            @Column(name = "supplier_cost_curr") })
    private Money supplierCost;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CostLineItem() {
        // for hibernate
    }

    public CostLineItem(Section section, Money actualCost) {
        this.section = section;
        this.actualCost = actualCost;
    }
    
    public CostLineItem(Section section, Money actualCost, Money supplierCost) {
    	this.section = section;
    	this.actualCost = actualCost;
    	this.supplierCost = supplierCost;
    }

    public Money getActualCost() {
        return this.actualCost;
    }

    public void setActualCost(Money actualCost) {
        this.actualCost = actualCost;
    }

    public Section getSection() {
        return this.section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Money getCostAfterApplyingContract() {
        return this.costAfterApplyingContract;
    }

    public void setCostAfterApplyingContract(Money costAfterApplyingContract) {
        this.costAfterApplyingContract = costAfterApplyingContract;
    }

    public Money getRecoveredCost() {
        return this.recoveredCost;
    }

    public void setRecoveredCost(Money recoveredCost) {
        this.recoveredCost = recoveredCost;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("section", this.section)
                .append("actualCost", this.actualCost)
                .append("costAfterApplyingContract",this.costAfterApplyingContract)
                .append("recoveredCost", this.recoveredCost)
                .append("supplierCost", this.supplierCost)
                .toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Money getSupplierCost() {
		return supplierCost;
	}

	public void setSupplierCost(Money supplierCost) {
		this.supplierCost = supplierCost;
	}
	
	public String getMessageKey(String sectionName){
        return NAMES_AND_KEY.get(sectionName);
    }

}

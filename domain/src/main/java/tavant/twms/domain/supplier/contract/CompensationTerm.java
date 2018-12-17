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
package tavant.twms.domain.supplier.contract;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.security.AuditableColumns;

/**
 * @author kannan.ekanath
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@PropertiesWithNestedCurrencyFields( { "recoveryFormula" })
public class CompensationTerm implements AuditableColumns{

    public static final String DEALER_NET_PRICE = "Dealer Net Price";

    public static final String COST_PRICE = "Cost Price";

    public static final String MATERIAL_COST = "MaterialCost";
    
    public static final String STARDARD_COST = "StandardCost";

    public static final String STD_SUPPLIER_LABOR_RATE = "Standard Supplier Rate";

    public static final String STD_DEALER_LABOR_RATE = "Standard Dealer Rate";

    public static final String SPL_SUPPLIER_LABOR_RATE = "Special Supplier Rate";

    public static final String SPL_DEALER_LABOR_RATE = "Special Dealer Rate";

    @Id
    @GeneratedValue(generator = "CompensationTerm")
    @GenericGenerator(name = "CompensationTerm", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "COMPENSATION_TERM_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;

    private String priceType;

    @Transient
    private boolean covered;

    @Transient
    private boolean stdLaborCovered;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private RecoveryFormula recoveryFormula;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CompensationTerm() {
        // for hibernate
    }

    public CompensationTerm(Section section, RecoveryFormula recoveryFormula) {
        this.section = section;
        this.recoveryFormula = recoveryFormula;
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

    public RecoveryFormula getRecoveryFormula() {
        return this.recoveryFormula;
    }

    public void setRecoveryFormula(RecoveryFormula recoveryFormula) {
        this.recoveryFormula = recoveryFormula;
    }

    public String getPreparedRecoveryFormula() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.recoveryFormula.getPercentageOfCost());
        if (Section.OEM_PARTS.equals(this.getSection().getName())) {
            buf.append("% of Part Cost(" + this.priceType + ")");
        }else if(Section.LABOR.equals(this.getSection().getName())){
        	if(SPL_DEALER_LABOR_RATE.equals(this.priceType)){
        		buf.append("% of Dealer Rate * "+this.recoveryFormula.getNoOfHours());
        		return buf.toString();
        	}else if(SPL_SUPPLIER_LABOR_RATE.equals(this.priceType)){
        		buf.append("% of Supplier Rate at "+this.recoveryFormula.getSupplierRate()+" * "+this.recoveryFormula.getNoOfHours());
        		return buf.toString();
        	}else if(STD_SUPPLIER_LABOR_RATE.equals(this.priceType)){
        		buf.append("% of Labor at Supplier Rate of "+this.recoveryFormula.getSupplierRate());
        	}else if(STD_DEALER_LABOR_RATE.equals(this.priceType)){
        		buf.append("% of Labor at Dealer Rate ");
        	}
        }else {
            buf.append("% of Total Cost element share");
        }
        if (this.recoveryFormula.getAddedConstant() != null) {
            buf.append(" + ");
            buf.append(this.recoveryFormula.getAddedConstant());
        }
        if (this.recoveryFormula.getMaximumAmount() != null) {
            buf.append(" subject to maximum of ");
            buf.append(this.recoveryFormula.getMaximumAmount());
        }
        return buf.toString();
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public boolean isStdLaborCovered() {
        return stdLaborCovered;
    }

    public void setStdLaborCovered(boolean stdLaborCovered) {
        this.stdLaborCovered = stdLaborCovered;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

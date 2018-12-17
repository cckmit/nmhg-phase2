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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.supplier.CostLineItem;
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
public class RecoveryFormula implements AuditableColumns{

    private static final Logger logger = Logger.getLogger(RecoveryFormula.class);

    @Id
    @GeneratedValue(generator = "RecoveryFormula")
	@GenericGenerator(name = "RecoveryFormula", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "RECOVERY_FORMULA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Range(min = 0, message = "Not a valid percentage value")
    private Double percentageOfCost;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "constant_amt"), @Column(name = "constant_curr") })
    private Money addedConstant;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "supplier_rate", nullable = true),
            @Column(name = "supplier_rate_curr", nullable = true) })
    private Money supplierRate;

    private Double noOfHours = 0.0;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "minimum_amount_amt"),
            @Column(name = "minimum_amount_curr") })
    private Money minimumAmount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "max_amount_amt"), @Column(name = "max_amount_curr") })
    private Money maximumAmount;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public RecoveryFormula() {
        super();
    }

    public RecoveryFormula(Double percentageOfCost, Money addedConstant, Money minimumAmount,
            Money maximumAmount) {
        super();
        this.percentageOfCost = percentageOfCost;
        this.addedConstant = addedConstant;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
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

    public Money getAddedConstant() {
        return this.addedConstant;
    }

    public void setAddedConstant(Money addedConstant) {
        this.addedConstant = addedConstant;
    }

    public Money getMaximumAmount() {
        return this.maximumAmount;
    }

    public void setMaximumAmount(Money maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Money getMinimumAmount() {
        return this.minimumAmount;
    }

    public void setMinimumAmount(Money minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Double getPercentageOfCost() {
        return this.percentageOfCost;
    }

    public void setPercentageOfCost(Double percentageOfCost) {
        this.percentageOfCost = percentageOfCost;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("percentageOfCost", this.percentageOfCost).append(
                "addedConstant", this.addedConstant).append("minimumAmount", this.minimumAmount)
                .append("maximumAmount", this.maximumAmount).toString();
    }

    public void applyFormulaToItem(CostLineItem cli) {
        /**
         * The recovered cost should be also set the cost after applying
         * contract to start with
         */
        if (!cli.getActualCost().isZero()) {
            cli.setCostAfterApplyingContract(apply(cli.getSupplierCost()));
            cli.setRecoveredCost(cli.getCostAfterApplyingContract());
        } else {
            cli.setRecoveredCost(cli.getSupplierCost());
            cli.setCostAfterApplyingContract(cli.getSupplierCost());
        }

    }

    public Money apply(Money money) {
        if (logger.isDebugEnabled()) {
            logger.debug("Applying formula to [" + money + "]");
        }
        Money percentageCost = money.times((double) this.percentageOfCost / (double) 100);
		Money fixedCost = this.addedConstant != null
				&& percentageCost!=null && this.addedConstant.breachEncapsulationOfCurrency().equals(
						percentageCost.breachEncapsulationOfCurrency()) ? percentageCost
				.plus(this.addedConstant) : percentageCost;
				
		if (logger.isDebugEnabled()) {
            logger.debug("After applying percentage [" + fixedCost + "]");
        }
        if (this.minimumAmount != null && fixedCost!=null && this.minimumAmount.breachEncapsulationOfCurrency().equals(fixedCost.breachEncapsulationOfCurrency())) {
            fixedCost = fixedCost.isLessThan(this.minimumAmount) ? this.minimumAmount : fixedCost;
            if (logger.isDebugEnabled()) {
                logger.debug("After applying minimum limit");
            }
        }
        if (this.maximumAmount != null && fixedCost!=null && this.maximumAmount.breachEncapsulationOfCurrency().equals(fixedCost.breachEncapsulationOfCurrency())) {
            fixedCost = fixedCost.isGreaterThan(this.maximumAmount) ? this.maximumAmount
                    : fixedCost;
        }
        return fixedCost;
    }

    public Money getSupplierRate() {
        return supplierRate;
    }

    public void setSupplierRate(Money supplierRate) {
        this.supplierRate = supplierRate;
    }

    public Double getNoOfHours() {
        return noOfHours;
    }

    public void setNoOfHours(Double noOfHours) {
        this.noOfHours = noOfHours;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

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
package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.ExcludeConversion;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class LineItem implements AuditableColumns{

    public static final String TOTAL_COST_LINE_ITEM_PREFIX = "Total cost of ";

    @Id
    @GeneratedValue(generator = "LineItem")
    @GenericGenerator(name = "LineItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "SEQ_LineItem"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Column(nullable = false)
    private String name;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "amt", nullable = false),
            @Column(name = "curr", nullable = false) })
    private Money value;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "cp_amt", nullable = false),
            @Column(name = "cp_curr", nullable = false) })
    @ExcludeConversion
    private Money cpValue;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "claimed_amt", nullable = true),
            @Column(name = "claimed_curr", nullable = true) })
    private Money claimedValue;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "dlr_claimed_amt", nullable = true),
            @Column(name = "dlr_claimed_curr", nullable = true) })
    private Money dlrClaimedValue;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "display_amt", nullable = true),
            @Column(name = "display_curr", nullable = true) })
    private Money displayAmount;

    @Column(nullable = false)
    private Double modifierPercentage;
    
    private Double SMandateModifierPercent;
    
    private Double percentageConfigured;
    private Double percentageConfiguredSMandate;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "accepted_amt", nullable = false),
			@Column(name = "accepted_curr", nullable = false) })
    private Money acceptedCost;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "state_mandate_amt", nullable = false),
			@Column(name = "state_mandate_curr", nullable = false) })
    private Money stateMandateAmount;
    
    private BigDecimal percentageAcceptance = BigDecimalFactory.bigDecimalOf(100);
    

    @Column(name = "line_item_level")
    private int level;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private PaymentVariable paymentVariable;
    
    private Boolean isFlatRate = Boolean.FALSE;;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Money getValue() {
        return this.value;
    }

    public void setValue(Money value) {
        this.value = value;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Double getModifierPercentage() {
        return this.modifierPercentage;
    }

    public void setModifierPercentage(Double modifierPercentage) {
        this.modifierPercentage = modifierPercentage;
    }
   
    public Money getAcceptedCost() {
		return acceptedCost;
	}

	public void setAcceptedCost(Money acceptedCost) {
		this.acceptedCost = acceptedCost;
	}

	public Money getStateMandateAmount() {
		return stateMandateAmount;
	}

	public void setStateMandateAmount(Money stateMandateAmount) {
		this.stateMandateAmount = stateMandateAmount;
	}

	public BigDecimal getPercentageAcceptance() {
		return percentageAcceptance;
	}

	public void setPercentageAcceptance(BigDecimal percentageAcceptance) {
		this.percentageAcceptance = percentageAcceptance;
	}

	public String prettyPrint(int leftIndent, int totalLength) {
        StringBuffer buf = new StringBuffer();
        StringBuffer indent = new StringBuffer();
        StringBuffer padding = new StringBuffer();
        for (int i = 0; i < leftIndent; i++) {
            buf.append(' ');
        }

        buf.append(indent);
        buf.append(this.name);

        int rightPadding = (totalLength - leftIndent - this.name.length());
        for (int i = 0; i < rightPadding; i++) {
            padding.append(' ');
        }

        buf.append(padding);
        buf.append(this.value);
        buf.append('\n');
        return buf.toString();
    }

	public Money getCpValue() {
		return cpValue == null ? getValue() : cpValue;
	}

	public void setCpValue(Money cpValue) {
		this.cpValue = cpValue;
	}

	public Money getClaimedValue() {
		return claimedValue;
	}

	public void setClaimedValue(Money claimedValue) {
		this.claimedValue = claimedValue;
	}

	public Money getDlrClaimedValue() {
		return dlrClaimedValue;
	}

	public void setDlrClaimedValue(Money dlrClaimedValue) {
		this.dlrClaimedValue = dlrClaimedValue;
	}

	public Money getDisplayAmount() {
		return displayAmount;
	}

	public void setDisplayAmount(Money displayAmount) {
		this.displayAmount = displayAmount;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public PaymentVariable getPaymentVariable() {
        return paymentVariable;
    }

    public void setPaymentVariable(PaymentVariable paymentVariable) {
        this.paymentVariable = paymentVariable;
    }

	public Boolean getIsFlatRate() {
		return isFlatRate;
	}

	public void setIsFlatRate(Boolean isFlatRate) {
		this.isFlatRate = isFlatRate;
	}	
	
	public Double getSMandateModifierPercent() {
		return SMandateModifierPercent;
	}

	public void setSMandateModifierPercent(Double sMandateModifierPercent) {
		SMandateModifierPercent = sMandateModifierPercent;
	}

	public Double getPercentageConfigured() {
		return percentageConfigured;
	}

	public void setPercentageConfigured(Double percentageConfigured) {
		this.percentageConfigured = percentageConfigured;
	}

	public Double getPercentageConfiguredSMandate() {
		return percentageConfiguredSMandate;
	}

	public void setPercentageConfiguredSMandate(Double percentageConfiguredSMandate) {
		this.percentageConfiguredSMandate = percentageConfiguredSMandate;
	}

	@Override
	public LineItem clone(){
		LineItem item = new LineItem();
		item.setClaimedValue(this.claimedValue);
		item.setCpValue(this.cpValue);
		item.setDisplayAmount(this.displayAmount);
		item.setDlrClaimedValue(this.dlrClaimedValue);
		item.setIsFlatRate(this.isFlatRate);
		item.setLevel(this.level);
		item.setModifierPercentage(this.modifierPercentage);
		item.setName(this.name);
		item.setPaymentVariable(this.paymentVariable);
		item.setValue(this.value);
		item.setVersion(0);
		item.setAcceptedCost(this.acceptedCost);
		item.setSMandateModifierPercent(this.SMandateModifierPercent);
		item.setPercentageConfigured(this.percentageConfigured);
		item.setPercentageConfiguredSMandate(this.percentageConfiguredSMandate);
		item.setStateMandateAmount(this.stateMandateAmount);
		item.setPercentageAcceptance(this.percentageAcceptance);		
		
		return item;
	}

    public void clear(Money zero) {
        this.claimedValue = zero;
        this.cpValue = zero;
        this.displayAmount = zero;
        this.dlrClaimedValue = zero;
        this.modifierPercentage = BigDecimalFactory.bigDecimalOf(0).doubleValue();
        this.value = zero;
        this.acceptedCost=zero;
        this.stateMandateAmount=zero;
        this.percentageAcceptance=BigDecimal.ZERO;
        this.SMandateModifierPercent=BigDecimalFactory.bigDecimalOf(0).doubleValue();
        this.percentageConfigured=BigDecimalFactory.bigDecimalOf(0).doubleValue();
        this.percentageConfiguredSMandate=BigDecimalFactory.bigDecimalOf(0).doubleValue();
        
    }
    
    public void setStateMandate(Claim claim, Money SMandateCost,String costCategoryName)
    {
		Money stateMandateAmt = Money.valueOf(BigDecimal.ZERO,
				claim.getCurrencyForCalculation());	
		boolean isGoodWillPolicy = claim.isGoodWillPolicy();
		if (claim.getStateMandate() != null) {
			if (costCategoryName.equals(Section.OEM_PARTS)
					|| claim.getStateMandate()
							.isStateMandateApplyForCostCategory(
									costCategoryName) && !isGoodWillPolicy) {
				this.setStateMandateAmount(SMandateCost);
			} else {
				this.setStateMandateAmount(stateMandateAmt);
			}
		} else {
			this.setStateMandateAmount(stateMandateAmt);
		}
	}
}

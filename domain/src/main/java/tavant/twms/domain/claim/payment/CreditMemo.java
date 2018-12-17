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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * A credit memo entity is attached to a payment entity indicating that the
 * payment has been done. The total tax amount is stored here however, the
 * individual taxes like VAT, Service Tax have to be shown in the individual
 * LineItemGroup
 *
 * TODO: LineItemGroup should have a tax field.
 *
 * @author kannan.ekanath
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class CreditMemo implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "CreditMemo")
	@GenericGenerator(name = "CreditMemo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CREDIT_MEMO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String creditMemoNumber;

    private String claimNumber;

    @OneToOne(fetch = FetchType.LAZY)
    private RecoveryClaim recoveryClaim;

   	@Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate creditMemoDate;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "tax_amount_amt"), @Column(name = "tax_amount_curr") })
    private Money taxAmount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "paid_amount_amt"), @Column(name = "paid_amount_curr") })
    private Money paidAmount;
    
    //Store amounts received in ERP currency 
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "tax_amount_erp_amt"), @Column(name = "tax_amount_erp_curr") })
    private Money taxAmountErpCurrency;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "paid_amount_erp_amt"), @Column(name = "paid_amount_erp_curr") })
    private Money paidAmountErpCurrency;
    
    /**
     * The currency in which a vendor makes a payment may not be same as their default. If the currency
     * paid is different (or same) this property captures the information. 
     * 
     * This property was added as a part of fix for SLMSPROD-593
     */
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "credit_amount_amt"), @Column(name = "credit_amount_curr") })
    private Money creditAmount;


    private String crDrFlag;
    
	@SuppressWarnings("unused")
	@Transient
	private String formattedCreditMemoDate;
	
	@Column(nullable = true, length = 4000)
	private String creditMemoComments; 

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CalendarDate getCreditMemoDate() {
        return this.creditMemoDate;
    }

    public void setCreditMemoDate(CalendarDate creditMemoDate) {
        this.creditMemoDate = creditMemoDate;
    }

    public String getCreditMemoNumber() {
        return this.creditMemoNumber;
    }

    public void setCreditMemoNumber(String creditMemoNumber) {
        this.creditMemoNumber = creditMemoNumber;
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

    public Money getTaxAmount() {
        return this.taxAmount;
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getClaimNumber() {
        return this.claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public RecoveryClaim getRecoveryClaim() {
        return recoveryClaim;
    }

    public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
        this.recoveryClaim = recoveryClaim;
    }

	public Money getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Money paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getCrDrFlag() {
		return crDrFlag;
	}

	public void setCrDrFlag(String crDrFlag) {
		this.crDrFlag = crDrFlag;
	}

   	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Money getTaxAmountErpCurrency() {
		return taxAmountErpCurrency;
	}

	public void setTaxAmountErpCurrency(Money taxAmountErpCurrency) {
		this.taxAmountErpCurrency = taxAmountErpCurrency;
	}

	public Money getPaidAmountErpCurrency() {
		return paidAmountErpCurrency;
	}

	public void setPaidAmountErpCurrency(Money paidAmountErpCurrency) {
		this.paidAmountErpCurrency = paidAmountErpCurrency;
	}
	
	public Money getCreditAmount() {
		return creditAmount;
	}
	
	public void setCreditAmount(Money creditAmount) {
		this.creditAmount = creditAmount;
	}
	
	//Intraduced for TKTSA-964
	public String getFormattedCreditMemoDate() {
		return this.creditMemoDate.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
	}

	public void setFormattedCreditMemoDate(String formattedCreditMemoDate) {
		this.formattedCreditMemoDate = formattedCreditMemoDate;
	}

	public String getCreditMemoComments() {
		return creditMemoComments;
	}

	public void setCreditMemoComments(String creditMemoComments) {
		this.creditMemoComments = creditMemoComments;
	}
	
}

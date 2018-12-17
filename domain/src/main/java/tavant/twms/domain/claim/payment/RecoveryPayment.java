/*
 *   Copyright (c) 2007 Tavant Technologies
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

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

/**
 * @author pradipta.a
 */

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name = "Recovery_Payment")
@PropertiesWithNestedCurrencyFields( { "activeCreditMemo", "previousCreditMemos" })
public class RecoveryPayment implements AuditableColumns{

    private static final Logger logger = Logger.getLogger(RecoveryPayment.class);

    @Id
    @GeneratedValue(generator = "Payment")
    @GenericGenerator(name = "Payment", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "SEQ_Payment"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private RecoveryClaim forRecoveryClaim;

    @Version
    private int version;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "contract_amount_amt"),
            @Column(name = "contract_amount_curr") })
    private Money contractAmount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "total_rec_amount_amt"),
            @Column(name = "total_rec_amount_curr") })
    private Money totalRecoveryAmount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "previous_paid_amount_amt"),
            @Column(name = "previous_paid__amount_curr") })
    private Money previousPaidAmount;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private CreditMemo activeCreditMemo;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "rec_pmt_prev_cred_memos")
    private List<CreditMemo> previousCreditMemos;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public RecoveryPayment() {
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        this.contractAmount = Money.valueOf(0, baseCurrency);
        this.totalRecoveryAmount = Money.valueOf(0, baseCurrency);
        this.previousPaidAmount = Money.valueOf(0, baseCurrency);
        previousCreditMemos = new ArrayList<CreditMemo>();
    }

    public void addActiveCreditMemo(CreditMemo newCreditMemo) {
        if (activeCreditMemo != null) {
            previousCreditMemos.add(activeCreditMemo);
        }
        this.activeCreditMemo = newCreditMemo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecoveryClaim getForRecoveryClaim() {
        return forRecoveryClaim;
    }

    public void setForRecoveryClaim(RecoveryClaim forRecoveryClaim) {
        this.forRecoveryClaim = forRecoveryClaim;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Money getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(Money contractAmount) {
        this.contractAmount = contractAmount;
    }

    public Money getTotalRecoveryAmount() {
        return totalRecoveryAmount;
    }

    public void setTotalRecoveryAmount(Money totalRecoveryAmount) {
        this.totalRecoveryAmount = totalRecoveryAmount;
    }

    public Money getPreviousPaidAmount() {
        return previousPaidAmount;
    }

    public void setPreviousPaidAmount(Money previousPaidAmount) {
        this.previousPaidAmount = previousPaidAmount;
    }

    public CreditMemo getActiveCreditMemo() {
        return activeCreditMemo;
    }

    public void setActiveCreditMemo(CreditMemo activeCreditMemo) {
        this.activeCreditMemo = activeCreditMemo;
    }

    public List<CreditMemo> getPreviousCreditMemos() {
        return previousCreditMemos;
    }

    public void setPreviousCreditMemos(List<CreditMemo> previousCreditMemos) {
        this.previousCreditMemos = previousCreditMemos;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

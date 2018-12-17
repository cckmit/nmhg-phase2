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
package tavant.twms.domain.common;

import java.math.MathContext;
import java.util.Currency;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.hibernate.annotations.Cascade;

import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
public class GlobalConfiguration implements AuditableColumns{
    private Currency baseCurrency;
    private MathContext mathContext;
    private ThreadLocal<Currency> claimCurrency = new ThreadLocal<Currency>();
    private static GlobalConfiguration _instance = new GlobalConfiguration();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    public static GlobalConfiguration getInstance() {
        return _instance;
    }
    
    public Currency getBaseCurrency() {
        return (claimCurrency.get() != null) ? claimCurrency.get() : baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
    
    private GlobalConfiguration() {
        baseCurrency = Currency.getInstance("USD");
        mathContext = MathContext.DECIMAL32;        
    }

    public MathContext getMathContext() {
        return mathContext;
    }

    public boolean isInBaseCurrency(Money money) {
        return baseCurrency.equals( money.breachEncapsulationOfCurrency());
    }
    
    public Money zeroInBaseCurrency() {
        if (claimCurrency.get() != null) {
            return Money.valueOf(0.0D, claimCurrency.get());
        } else {
            return Money.valueOf(0.0D, baseCurrency);
        }
    }

    public void setClaimCurrency(Currency claimCurrency) {
        this.claimCurrency.set(claimCurrency);
    }

    public void removeClaimCurrency() {
        this.claimCurrency.remove();
    }

    public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}



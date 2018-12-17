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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

/**
 * Money value represented in both base currency as well as natural currency.
 * The base currency representation is persisted solely for reporting requirements.
 * programmers must use the natural currency. where required.

 * @author radhakrishnan.j
 */
@Embeddable
public class MoneyValue {
    @Type(type="tavant.twms.infra.MoneyUserType")
    @Columns(columns = {
            @Column(name="amt",nullable=true),
            @Column(name="curr",nullable=true)})    
    private Money naturalCurrency;
    
    @Type(type="tavant.twms.infra.MoneyUserType")
    @Columns(columns = {
            @Column(name="amt",nullable=false),
            @Column(name="curr",nullable=false)})    
    private Money baseCurrency = Money.valueOf(0.0D,GlobalConfiguration.getInstance().getBaseCurrency());

    public Money getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Money baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Money getNaturalCurrency() {
        if( naturalCurrency==null ) {
            return baseCurrency;
        }
        return naturalCurrency;
    }

    public void setNaturalCurrency(Money naturalCurrency) {
        if( GlobalConfiguration.getInstance().isInBaseCurrency(naturalCurrency) ) {
            this.baseCurrency = naturalCurrency;
        } else {
            this.naturalCurrency = naturalCurrency;
        }
    }
}

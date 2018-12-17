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
package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

/**
 * @author aniruddha.chaturvedi
 */
@Embeddable
public class CurrencyWiseTravelAndLaborCharge {
    
    private Currency currency;

    private BigDecimal laborAmount;

    private BigDecimal perKmAmount;

    private BigDecimal perMileAmount;

    private BigDecimal perTripAmount;

    private BigDecimal perHourAmount;
    
    public CurrencyWiseTravelAndLaborCharge() {
        this.currency = Currency.getInstance("USD");
        this.laborAmount = new BigDecimal(0.0);
        this.perKmAmount = new BigDecimal(0.0);
        this.perMileAmount = new BigDecimal(0.0);
        this.perTripAmount = new BigDecimal(0.0);
        this.perHourAmount = new BigDecimal(0.0);
    }

    public CurrencyWiseTravelAndLaborCharge(Currency currency, BigDecimal laborAmount,
            BigDecimal perKmAmount, BigDecimal perMileAmount, BigDecimal perTripAmount,
            BigDecimal perHourAmount) {
        this.currency = currency;
        this.laborAmount = laborAmount;
        this.perKmAmount = perKmAmount;
        this.perMileAmount = perMileAmount;
        this.perTripAmount = perTripAmount;
        this.perHourAmount = perHourAmount;
    }

    @Type(type="org.hibernate.type.CurrencyType")
    @Column(nullable=false)
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Type(type="org.hibernate.type.BigDecimalType")
    @Column(nullable=false)
    public BigDecimal getPerHourAmount() {
        return perHourAmount;
    }

    public void setPerHourAmount(BigDecimal perHourAmount) {
        this.perHourAmount = perHourAmount;
    }

    @Type(type="org.hibernate.type.BigDecimalType")
    @Column(nullable=false)
    public BigDecimal getPerKmAmount() {
        return perKmAmount;
    }

    public void setPerKmAmount(BigDecimal perKmAmount) {
        this.perKmAmount = perKmAmount;
    }

    @Type(type="org.hibernate.type.BigDecimalType")
    @Column(nullable=false)
    public BigDecimal getPerMileAmount() {
        return perMileAmount;
    }

    public void setPerMileAmount(BigDecimal perMileAmount) {
        this.perMileAmount = perMileAmount;
    }

    @Type(type="org.hibernate.type.BigDecimalType")
    @Column(nullable=false)
    public BigDecimal getPerTripAmount() {
        return perTripAmount;
    }

    public void setPerTripAmount(BigDecimal perTripAmount) {
        this.perTripAmount = perTripAmount;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("Currency is [").append(currency).append(
                "], Labor Charge is [").append(laborAmount).append("], Amount per Km is [").append(
                perKmAmount).append("], Amount per Mile is [").append(perMileAmount).append(
                "], Amount per Trip is [").append(perTripAmount).append("], Amount per Hour is [").append(
                perHourAmount).append("].").toString();
    }
    
    

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((currency == null) ? 0 : currency.hashCode());
        result = PRIME * result + ((laborAmount == null) ? 0 : laborAmount.hashCode());
        result = PRIME * result + ((perHourAmount == null) ? 0 : perHourAmount.hashCode());
        result = PRIME * result + ((perKmAmount == null) ? 0 : perKmAmount.hashCode());
        result = PRIME * result + ((perMileAmount == null) ? 0 : perMileAmount.hashCode());
        result = PRIME * result + ((perTripAmount == null) ? 0 : perTripAmount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CurrencyWiseTravelAndLaborCharge other = (CurrencyWiseTravelAndLaborCharge) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (laborAmount == null) {
            if (other.laborAmount != null)
                return false;
        } else if (!laborAmount.equals(other.laborAmount))
            return false;
        if (perHourAmount == null) {
            if (other.perHourAmount != null)
                return false;
        } else if (!perHourAmount.equals(other.perHourAmount))
            return false;
        if (perKmAmount == null) {
            if (other.perKmAmount != null)
                return false;
        } else if (!perKmAmount.equals(other.perKmAmount))
            return false;
        if (perMileAmount == null) {
            if (other.perMileAmount != null)
                return false;
        } else if (!perMileAmount.equals(other.perMileAmount))
            return false;
        if (perTripAmount == null) {
            if (other.perTripAmount != null)
                return false;
        } else if (!perTripAmount.equals(other.perTripAmount))
            return false;
        return true;
    }

    @Type(type="org.hibernate.type.BigDecimalType")
    @Column(nullable=false)     
    public BigDecimal getLaborAmount() {
        return laborAmount;
    }

    public void setLaborAmount(BigDecimal laborAmount) {
        this.laborAmount = laborAmount;
    }
}

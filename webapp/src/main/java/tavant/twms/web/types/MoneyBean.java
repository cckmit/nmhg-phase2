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
package tavant.twms.web.types;

import com.domainlanguage.money.Money;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * The equivalent class for Money to be used at the web layer for ease in
 * handling the Money values.
 * 
 * @author aniruddha.chaturvedi
 * 
 */
public class MoneyBean implements Comparable<MoneyBean>{
    private String currencyCode;

    private BigDecimal value;

    public MoneyBean() {
    }
    
    public MoneyBean(Money fromMoney) {
        currencyCode = fromMoney.breachEncapsulationOfCurrency().getCurrencyCode();
        value = fromMoney.breachEncapsulationOfAmount();
    }

    public MoneyBean(String currencyCode, BigDecimal value) {
        this.value = value;
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal decimal) {
        this.value = decimal;
    }

    public Money getModel() {
        return Money.valueOf(getValue().setScale(2, BigDecimal.ROUND_HALF_UP), Currency.getInstance(getCurrencyCode()));
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("currencyCode = [").append(currencyCode).append("], value = [").append(value)
                .append("].");
        return buff.toString();
    }

    public int compareTo(MoneyBean o) {
        if( equals(o) ) return 0;
        return getCurrencyCode().compareTo(o.getCurrencyCode());
    }
}

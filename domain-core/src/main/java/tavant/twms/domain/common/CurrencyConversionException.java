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

import java.text.MessageFormat;
import java.util.Currency;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
public class CurrencyConversionException extends Exception {
    private CurrencyExchangeRate exchangeRate;
    
    private Money money;

    private Currency toCurrency;

    private CalendarDate asOfDate;

    private ReasonCode reasonCode;
    
    private static String messageFormat = "Failed to convert from {0} to {1} as on {3} using {2}. Reason {4}";
    
    public CurrencyConversionException(CurrencyExchangeRate exchangeRate,Money money, Currency toCurrency, CalendarDate asOfDate,
            ReasonCode reasonCode) {
        this.exchangeRate = exchangeRate;
        this.money = money;
        this.toCurrency = toCurrency;
        this.asOfDate = asOfDate;
        this.reasonCode = reasonCode;
    }

    public CalendarDate getAsOfDate() {
        return asOfDate;
    }

    public Money getMoney() {
        return money;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }
    
    @Override
    public String getMessage() {
        return MessageFormat.format(messageFormat,money,toCurrency,exchangeRate,asOfDate,reasonCode);
    }

    public static enum ReasonCode {
        INCOMPATIBLE_CURRENCY,
        CONVERSION_RATE_NOT_FOUND_FOR_DATE
    }
}

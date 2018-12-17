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
package tavant.twms.domain.claim;

import java.util.Currency;
import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
public interface CurrencyConversionAdvice {

    public Object convertFromNaturalToBaseCurrency(Claim claim);

    public Object convertFromBaseToNaturalCurrency(Claim claim);
    
    public Object convertFromNaturalToBaseCurrencyForRecClaim(RecoveryClaim recClaim);
    
    public Money convertMoneyFromNaturalToBaseCurrency(Money valueToConvert, Claim claim);
    
    public Object convertCurrencyOnlyFromNaturalToBase(Claim claim);

    public Object convertCurrencyOnlyFromBaseToNatural(Claim claim);
    
    public Money convertMoneyFromBaseToNaturalCurrency(Money valueToConvert, CalendarDate asOfDate, Currency naturalCurrency);
    
    public String getCurrencyForERPInteractions(Claim claim);
        
    public Money convertMoneyUsingAppropriateConFactor(Money valueToConvert,CalendarDate asOfDate, Currency naturalCurrency); 
}

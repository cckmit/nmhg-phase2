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

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.infra.GenericRepository;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public interface CurrencyExchangeRateRepository extends GenericRepository<CurrencyExchangeRate,Long> {
    
	public CurrencyExchangeRate findCurrencyExchangeRate(Currency fromCurrency,Currency toCurrency);
    
    public CurrencyConversionFactor findConversionFactor(Currency fromCurrency,Currency toCurrency,CalendarDate asOfDate);

    @Deprecated
	public List<Currency> findAllCurrencies();  
    
    
    public CurrencyExchangeRate findCurrencyExchangeRateForSync(Currency fromCurrency, Currency toCurrency);

	public CurrencyConversionFactor findConversionFactorForSync(Currency fromCurrency, Currency toCurrency, CalendarDate asOfDate);
}

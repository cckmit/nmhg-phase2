package tavant.twms.domain.common;


import java.util.Currency;
import java.util.List;

import tavant.twms.infra.GenericService;

import com.domainlanguage.time.CalendarDate;

public interface CurrencyExchangeRateService extends GenericService<CurrencyExchangeRate, Long, Exception>{
	
	public CurrencyExchangeRate findCurrencyExchangeRate(Currency fromCurrency, Currency toCurrency);
	
	public CurrencyConversionFactor findConversionFactor(Currency fromCurrency,Currency toCurrency,CalendarDate asOfDate);
	
	@Deprecated
	public List<Currency> findAllCurrencies();
	
    public CurrencyExchangeRate findCurrencyExchangeRateForSync(Currency fromCurrency, Currency toCurrency);

	public CurrencyConversionFactor findConversionFactorForSync(Currency fromCurrency, Currency toCurrency, CalendarDate asOfDate);
}

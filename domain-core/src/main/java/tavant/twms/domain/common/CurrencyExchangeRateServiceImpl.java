package tavant.twms.domain.common;



import java.util.Currency;
import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class CurrencyExchangeRateServiceImpl extends
GenericServiceImpl<CurrencyExchangeRate, Long, Exception> implements
CurrencyExchangeRateService {
	
	CurrencyExchangeRateRepository currencyExchangeRateRepository;
	
	 @Override
	    public GenericRepository<CurrencyExchangeRate, Long> getRepository() {
	        return this.currencyExchangeRateRepository;
	    }

	public CurrencyExchangeRateRepository getCurrencyExchangeRateRepository() {
		return currencyExchangeRateRepository;
	}

	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}
	 
	public CurrencyExchangeRate findCurrencyExchangeRate(Currency fromCurrency, Currency toCurrency){
		return this.currencyExchangeRateRepository.findCurrencyExchangeRate(fromCurrency, toCurrency);
	}
	
	public CurrencyConversionFactor findConversionFactor(Currency fromCurrency,Currency toCurrency,CalendarDate asOfDate){
		return this.currencyExchangeRateRepository.findConversionFactor(fromCurrency, toCurrency, asOfDate);
	}

	public List<Currency> findAllCurrencies() {
		return currencyExchangeRateRepository.findAllCurrencies();
	}
	
    public CurrencyExchangeRate findCurrencyExchangeRateForSync(Currency fromCurrency, Currency toCurrency){
    	return this.currencyExchangeRateRepository.findCurrencyExchangeRateForSync(fromCurrency, toCurrency);
    }

	public CurrencyConversionFactor findConversionFactorForSync(Currency fromCurrency, Currency toCurrency, CalendarDate asOfDate)
	{
		return this.currencyExchangeRateRepository.findConversionFactorForSync(fromCurrency, toCurrency, asOfDate);
	}
	
}

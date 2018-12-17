package tavant.twms.integration.layer.util;

import java.util.Currency;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.integration.layer.util.CalendarUtil;

import java.util.Calendar;

public class CurrencyConvertor {
	
	
	public Money convertToBaseCurrency(Currency fromCurrency,Money toBeConverted){
		
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        CalendarDate currentDate = CalendarUtil.convertToCalendarDate(Calendar.getInstance());
        
        CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                .findConversionFactor(fromCurrency,baseCurrency,currentDate);
        if (conversionFactor == null) {
            throw new RuntimeException("Exchcange rate not defined for ("
                    + fromCurrency + "," + baseCurrency + ") for [" +currentDate
                    + "]");
        }
        Money convertedValue = null;
        try {
			convertedValue = conversionFactor.convert(toBeConverted, currentDate);
		} catch (CurrencyConversionException e) {
			throw new RuntimeException(e);
		}
		return convertedValue;
		
	}
	
	public Money convertToNaturalCurrency(Currency toCurrency,Money toBeConverted){
		
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        CalendarDate currentDate = CalendarUtil.convertToCalendarDate(Calendar.getInstance());
        
        CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                .findConversionFactor(toCurrency,baseCurrency,currentDate);
        if (conversionFactor == null) {
            throw new RuntimeException("Exchcange rate not defined for ("
                    + toCurrency + "," + baseCurrency + ") for [" +currentDate
                    + "]");
        }
        Money convertedValue = null;
        try {
			convertedValue = conversionFactor.reverseConvert(toBeConverted, currentDate);
		} catch (CurrencyConversionException e) {
			throw new RuntimeException(e);
		}
		return convertedValue;
		
	}
	
    protected CurrencyExchangeRateRepository currencyExchangeRateRepository;
    
    @Required
    public void setCurrencyExchangeRateRepository(
            CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

}

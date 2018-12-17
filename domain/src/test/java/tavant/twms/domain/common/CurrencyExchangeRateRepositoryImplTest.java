package tavant.twms.domain.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.TimeZone;

import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class CurrencyExchangeRateRepositoryImplTest extends DomainRepositoryTestCase {
    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }
    
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;
    
    public void setCurrencyExchangeRateRepository(CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    private CurrencyExchangeRate entity;
    private Currency USD = Currency.getInstance("USD");
    private Currency EUR = Currency.getInstance("EUR");
    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
//        super.setUpInTxnRollbackOnFailure();
        entity = new CurrencyExchangeRate();
        entity.setFromCurrency(USD);
        entity.setToCurrency(EUR);
    }



    public void testSave() throws DurationOverlapException {
        
        CalendarDate today = Clock.today();
        CalendarDate yesterDay = today.previousDay();
        CalendarDate tomorrow = today.nextDay();
        
        MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
        BigDecimal yesterdaysRate = new BigDecimal(1.0002,mathContext);
        BigDecimal todaysRate = new BigDecimal(1.0034,mathContext);
        BigDecimal tomorrowsRate = new BigDecimal(1.0034,mathContext);        
        entity.set(yesterdaysRate, new CalendarDuration(yesterDay,yesterDay));
        entity.set(todaysRate, new CalendarDuration(today,today));        
        entity.set(tomorrowsRate, new CalendarDuration(tomorrow,tomorrow));
        
        currencyExchangeRateRepository.save(entity);
        assertNotNull(entity.getId());
        flushAndClear();
        
        entity = currencyExchangeRateRepository.findById(entity.getId());
        assertNotNull(entity);
        assertEquals(yesterdaysRate,entity.getValueAsOf(yesterDay));
        assertEquals(todaysRate,entity.getValueAsOf(today));
        assertEquals(tomorrowsRate,entity.getValueAsOf(tomorrow));
        assertNull(entity.getValueAsOf(yesterDay.previousDay()));
        assertNull(entity.getValueAsOf(tomorrow.nextDay()));
        
        CurrencyExchangeRate _USD_to_EUR = currencyExchangeRateRepository.findCurrencyExchangeRate(USD,EUR);
        assertNotNull(_USD_to_EUR);
        assertNotNull(_USD_to_EUR.getValueAsOf(yesterDay));
        assertNotNull(_USD_to_EUR.getValueAsOf(today));
        assertNotNull(_USD_to_EUR.getValueAsOf(tomorrow));
    }

    public void testUpdate() throws DurationOverlapException {
        CalendarDate today = Clock.today();
        CalendarDate yesterDay = today.previousDay();
        CalendarDate tomorrow = today.nextDay();
        
        BigDecimal yesterdaysRate = new BigDecimal(1.0002).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal todaysRate = new BigDecimal(1.0034).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal tomorrowsRate = new BigDecimal(1.0034).setScale(4, RoundingMode.HALF_EVEN);        
        entity.set(yesterdaysRate, new CalendarDuration(yesterDay,yesterDay));
        entity.set(todaysRate, new CalendarDuration(today,today));        
        entity.set(tomorrowsRate, new CalendarDuration(tomorrow,tomorrow));
        
        currencyExchangeRateRepository.save(entity);
        assertNotNull(entity.getId());
//        flushAndClear();
        flush();
        
        entity = currencyExchangeRateRepository.findById(entity.getId());
        assertNotNull(entity);

        BigDecimal newValue = new BigDecimal(1.0004).setScale(4,RoundingMode.HALF_EVEN);
        entity.set(newValue,new CalendarDuration(yesterDay,yesterDay));
        currencyExchangeRateRepository.update(entity);
//        flushAndClear();
        flush();
        
        entity = currencyExchangeRateRepository.findById(entity.getId());
        assertEquals(newValue,entity.getValueAsOf(yesterDay));
    }

    public void testSameCurrencyComparison() throws CurrencyConversionException {
        Currency usd1 = Currency.getInstance("USD");
        CurrencyExchangeRate sameRate = currencyExchangeRateRepository.findCurrencyExchangeRate(USD, usd1);
        Money money = Money.dollars(100);
        assertEquals(money, sameRate.convert(money, Clock.today()));
    }
}

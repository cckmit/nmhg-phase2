package tavant.twms.domain.common;

import java.util.Currency;
import java.util.TimeZone;

import junit.framework.TestCase;
import tavant.twms.infra.BigDecimalFactory;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class CurrencyExchangeRateTest extends TestCase {
    static {
        Clock.setDefaultTimeZone( TimeZone.getDefault() );
        Clock.timeSource();
    }
    
    private CurrencyExchangeRate fixture;
    private Currency USD = Currency.getInstance("USD");
    private Currency EUR = Currency.getInstance("EUR");
    
    private CalendarDuration firstQuarter = new CalendarDuration(CalendarDate.date(2006,1,1),CalendarDate.date(2006,3,31));
    private CalendarDuration secondQuarter = new CalendarDuration(CalendarDate.date(2006,4,1),CalendarDate.date(2006,6,30));
    
    @Override
    protected void setUp() throws Exception {
        fixture = new CurrencyExchangeRate(USD,EUR);
        fixture.set(BigDecimalFactory.bigDecimalOf(3.1415926535D), firstQuarter);
        fixture.set(BigDecimalFactory.bigDecimalOf(1.0592D), secondQuarter);
    }

    public void testConvertMoneyCalendarDate() throws CurrencyConversionException {
        Money amountInDollars = Money.dollars(10000000);
        Money valueInQ1 = Money.euros(31415930.00D);
        Money valueInQ2 = Money.euros(10592000D);        
        CalendarDate someDayInQ1 = CalendarDate.date(2006,2, 2);
        CalendarDate someDayInQ2 = CalendarDate.date(2006,5, 2);        
        assertEquals(valueInQ1,fixture.convert(amountInDollars, someDayInQ1));
        assertEquals(valueInQ2,fixture.convert(amountInDollars, someDayInQ2));
        
        assertEquals(amountInDollars,fixture.reverseConvert(valueInQ1,someDayInQ1));
        assertEquals(amountInDollars,fixture.reverseConvert(valueInQ2,someDayInQ2));
    }

    public void testSameCurrencyExchangeRate() throws CurrencyConversionException {
        assertEquals(Money.dollars(100),CurrencyExchangeRate.sameCurrencyExchangeRate(USD).convert(Money.dollars(100), CalendarDate.date(2009,1, 1)));
    }

    public void testConvertMoneyCalendarDate_SameCurrency() throws CurrencyConversionException {
        CurrencyConversionException ex = null;
        try {
            assertEquals(Money.euros(100.0D),fixture.convert(Money.valueOf(100.0D,Currency.getInstance("EUR")), CalendarDate.date(2006,2, 2)));
        } catch (CurrencyConversionException e) {
            ex = e;
        }
        assertNull(ex);
    }
    
    public void testConvertMoneyCalendarDate_IncompatibleCurrency() {
        CurrencyConversionException ex = null;
        try {
            assertEquals(Money.euros(104.59D),fixture.convert(Money.valueOf(100,Currency.getInstance("INR")), CalendarDate.date(2006,2, 2)));
        } catch (CurrencyConversionException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertEquals(EUR,ex.getToCurrency());
        assertEquals(CalendarDate.date(2006,2,2),ex.getAsOfDate());
    }

    public void testReverseConvertMoneyCalendarDate_IncompatibleCurrency() {
        CurrencyConversionException ex = null;
        try {
            assertEquals(Money.dollars(100.0D),fixture.reverseConvert(Money.valueOf(104.59D,Currency.getInstance("INR")), CalendarDate.date(2006,2, 2)));
        } catch (CurrencyConversionException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertEquals(USD,ex.getToCurrency());
        assertEquals(CalendarDate.date(2006,2,2),ex.getAsOfDate());
    }
    
    public void testReverseConvertMoneyCalendarDate() {
        CurrencyConversionException ex = null;
        try {
            assertEquals(Money.dollars(31.83D),fixture.reverseConvert(Money.valueOf(100.0D,Currency.getInstance("EUR")), CalendarDate.date(2006,2, 2)));
        } catch (CurrencyConversionException e) {
            ex = e;
        }
        assertNull(ex);
    }    

    public void testReverseConvertMoneyCalendarDate_SameCurrency() {
        CurrencyConversionException ex = null;
        try {
            assertEquals(Money.dollars(100.0D),fixture.reverseConvert(Money.valueOf(100.0D,Currency.getInstance("USD")), CalendarDate.date(2006,2, 2)));
        } catch (CurrencyConversionException e) {
            ex = e;
        }
        assertNull(ex);
    }    
    
    
    public void testConversionRoundTrip() throws CurrencyConversionException {
        Money valueInDollars = Money.dollars(BigDecimalFactory.bigDecimalOf(1000000000.01D));
        Money valueInEuros = fixture.convert(valueInDollars,firstQuarter.getFromDate());
        Money valueBackInDollars = fixture.reverseConvert(valueInEuros, firstQuarter.getFromDate());
        assertEquals(valueInDollars,valueBackInDollars);
    }
    
    public void testConvertZero() throws CurrencyConversionException {
        Money convertedValue = fixture.convert(Money.valueOf(0.0D, USD),firstQuarter.getFromDate());
        assertEquals(Money.valueOf(0.0D,EUR),convertedValue);
    }
    
    public void testReverseConvertZero() throws CurrencyConversionException {
        Money convertedValue = fixture.reverseConvert(Money.valueOf(0.0D, EUR),firstQuarter.getFromDate());
        assertEquals(Money.valueOf(0.0D,USD),convertedValue);
    }    
}

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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import javax.validation.constraints.NotNull;
import org.springframework.util.Assert;

import tavant.twms.infra.BigDecimalFactory;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
public class CurrencyExchangeRate extends TimeBoundValues<BigDecimal, CurrencyConversionFactor> {
    @Id
    @GeneratedValue(generator = "CurrencyExchangeRate")
	@GenericGenerator(name = "CurrencyExchangeRate", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CURR_EXCH_RATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @NotNull
    private Currency fromCurrency;

    @NotNull
    private Currency toCurrency;

    private static Logger logger = LogManager.getLogger(CurrencyExchangeRate.class);

    // FIX-ME: This needs to be an embeddable.
    // FIX-ME: In the case of an entity having a collection of elements,
    // hibernate's handling
    // FIX-ME: of changes to the collection is not understood properly. Atleast
    // it doesn't
    // FIX-ME: fit-in with current understanding. This problem occurs with
    // 3.2.1.ga

    // @CollectionOfElements(fetch=FetchType.LAZY)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    // @AttributeOverride(name="element.value",column=@Column(name="conversion_rate"))
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private SortedSet<CurrencyConversionFactor> rates = new TreeSet<CurrencyConversionFactor>();

    // for frameworks
    public CurrencyExchangeRate() {
    }

    // for programmers
    public CurrencyExchangeRate(Currency fromCurrency, Currency toCurrency) {
        Assert.notNull(fromCurrency, " from currency cannot be null");
        Assert.notNull(toCurrency, " to currency cannot be null");
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    @Override
    public SortedSet<CurrencyConversionFactor> getEntries() {
        return this.rates;
    }

    @Override
    public CurrencyConversionFactor newTimeBoundValue(BigDecimal value, CalendarDuration forDuration) {
        return new CurrencyConversionFactor(value, forDuration);
    }

    public Currency getFromCurrency() {
        return this.fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Currency getToCurrency() {
        return this.toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        buf.append("fromCurrency").append('=').append(this.fromCurrency);
        buf.append(", ");
        buf.append("toCurrency").append('=').append(this.toCurrency);
        buf.append('\n');
        buf.append(super.toString());
        return buf.toString();
    }

    public Money convert(Money value, CalendarDate asOfDate) throws CurrencyConversionException {
        Assert.notNull(value, "Money cannot be null");
        Assert.notNull(asOfDate, "Date cannot be null");
        Currency sourceCurrency = value.breachEncapsulationOfCurrency();
        if (sourceCurrency.equals(this.toCurrency)) {
            return value;
        } else if (!sourceCurrency.equals(this.fromCurrency)) {
            throw new CurrencyConversionException(this, value, this.toCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.INCOMPATIBLE_CURRENCY);
        } else if (value.isZero()) {
            return Money.valueOf(0.0D, this.toCurrency);
        }
        BigDecimal valueInFromCurrency = value.breachEncapsulationOfAmount();
        CurrencyConversionFactor conversionFactor = getEntryAsOf(asOfDate);
        if (logger.isDebugEnabled()) {
            logger.debug("convert(" + value + "," + asOfDate + ") to [" + this.toCurrency
                    + "] exchange rate is " + this);
        }

        if (conversionFactor == null) {
            throw new CurrencyConversionException(this, value, this.toCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.CONVERSION_RATE_NOT_FOUND_FOR_DATE);
        }
        BigDecimal conversionFactorValue = conversionFactor.getValue();
        conversionFactorValue = BigDecimalFactory.bigDecimalOf(conversionFactorValue.doubleValue());
        MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
        BigDecimal convertedValue = valueInFromCurrency
                .multiply(conversionFactorValue, mathContext);
        convertedValue = BigDecimalFactory.bigDecimalOf(convertedValue.doubleValue());
        return Money.valueOf(convertedValue, this.toCurrency, RoundingMode.HALF_EVEN.ordinal());
    }

    public Money reverseConvert(Money value, CalendarDate asOfDate)
            throws CurrencyConversionException {
        Assert.notNull(value, "Money cannot be null");
        Assert.notNull(asOfDate, "Date cannot be null");
        Currency sourceCurrency = value.breachEncapsulationOfCurrency();

        if (sourceCurrency.equals(this.fromCurrency)) {
            return value;
        } else if (!sourceCurrency.equals(this.toCurrency)) {
            throw new CurrencyConversionException(this, value, this.fromCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.INCOMPATIBLE_CURRENCY);
        } else if (value.isZero()) {
            return Money.valueOf(0.0D, this.fromCurrency);
        }

        BigDecimal valueInToCurrency = value.breachEncapsulationOfAmount();
        CurrencyConversionFactor conversionFactor = getEntryAsOf(asOfDate);
        if (logger.isDebugEnabled()) {
            logger.debug("reverseConvert(" + value + "," + asOfDate + ") to [" + this.fromCurrency
                    + "] exchange rate is " + this);
        }

        if (conversionFactor == null) {
            throw new CurrencyConversionException(this, value, this.toCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.CONVERSION_RATE_NOT_FOUND_FOR_DATE);
        }

        BigDecimal conversionFactorValue = conversionFactor.getValue();
        conversionFactorValue = BigDecimalFactory.bigDecimalOf(conversionFactorValue.doubleValue());
        MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
        if (logger.isDebugEnabled()) {
            logger.debug(valueInToCurrency + " divided by " + conversionFactorValue);
        }
        BigDecimal convertedValue = valueInToCurrency.divide(conversionFactorValue, mathContext);
        convertedValue = BigDecimalFactory.bigDecimalOf(convertedValue.doubleValue());
        return Money.valueOf(convertedValue, this.fromCurrency, RoundingMode.HALF_EVEN.ordinal());
    }

    public static CurrencyExchangeRate sameCurrencyExchangeRate(Currency aCurrency) {
        return new CurrencyExchangeRate(aCurrency, aCurrency);
    }

	@Override
	public CurrencyConversionFactor newTimeBoundValueModifier(BigDecimal value,
			CalendarDuration forDuration, Boolean isFlatRate) {
		// TODO Auto-generated method stub
		return null;
	};

}

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
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
public class CurrencyConversionFactor extends TimeBoundValue<BigDecimal> {

    @Id
    @GeneratedValue(generator = "CurrencyConversionFactor")
	@GenericGenerator(name = "CurrencyConversionFactor", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CURR_CONV_FCTR_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    @SuppressWarnings("unused")
    private Long id;

    @Version
    private int version;

    @NotNull
    @Column(columnDefinition = "numeric(19,6)")
    // @Check(name="positive_conversion_rate",constraints="factor > 0.000001")
    private BigDecimal factor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CurrencyExchangeRate parent;

    public CurrencyConversionFactor() {
    }

    public CurrencyConversionFactor(BigDecimal value, CalendarDuration aDuration) {
        super(aDuration);
        Assert.notNull(value, "conversion factor cannot be null");
        this.factor = value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public BigDecimal getValue() {
        return this.factor;
    }

    @Override
    public void setValue(BigDecimal newValue) {
        this.factor = newValue;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (CurrencyExchangeRate) parent;
    }

    public Money convert(Money value, CalendarDate asOfDate) throws CurrencyConversionException {
        Assert.notNull(value, "Money cannot be null");
        Assert.notNull(asOfDate, "Date cannot be null");
        Assert.isTrue(getDuration().includes(asOfDate), "Date not within range");
        Currency sourceCurrency = value.breachEncapsulationOfCurrency();
        Currency toCurrency = this.parent.getToCurrency();
        Currency fromCurrency = this.parent.getFromCurrency();
        if (sourceCurrency.equals(toCurrency)) {
            return value;
        } else if (!sourceCurrency.equals(fromCurrency)) {
            throw new CurrencyConversionException(this.parent, value, toCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.INCOMPATIBLE_CURRENCY);
        } else if (value.isZero()) {
            return Money.valueOf(0.0D, toCurrency);
        }
        BigDecimal valueInFromCurrency = value.breachEncapsulationOfAmount();

        this.factor = BigDecimalFactory.bigDecimalOf(this.factor.doubleValue());
        MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
        BigDecimal convertedValue = valueInFromCurrency.multiply(this.factor, mathContext);
        convertedValue = BigDecimalFactory.bigDecimalOf(convertedValue.doubleValue());
        return Money.valueOf(convertedValue, toCurrency, RoundingMode.HALF_EVEN.ordinal());
    }

    public Money reverseConvert(Money value, CalendarDate asOfDate)
            throws CurrencyConversionException {
        Assert.notNull(value, "Money cannot be null");
        Assert.notNull(asOfDate, "Date cannot be null");
        Assert.isTrue(getDuration().includes(asOfDate), "Date not within range");

        Currency sourceCurrency = value.breachEncapsulationOfCurrency();
        Currency toCurrency = this.parent.getToCurrency();
        Currency fromCurrency = this.parent.getFromCurrency();

        if (sourceCurrency.equals(fromCurrency)) {
            return value;
        } else if (!sourceCurrency.equals(toCurrency)) {
            throw new CurrencyConversionException(this.parent, value, fromCurrency, asOfDate,
                    CurrencyConversionException.ReasonCode.INCOMPATIBLE_CURRENCY);
        } else if (value.isZero()) {
            return Money.valueOf(0.0D, fromCurrency);
        }

        BigDecimal valueInToCurrency = value.breachEncapsulationOfAmount();
        this.factor = BigDecimalFactory.bigDecimalOf(this.factor.doubleValue());
        MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
        BigDecimal convertedValue = valueInToCurrency.divide(this.factor, mathContext);
        convertedValue = BigDecimalFactory.bigDecimalOf(convertedValue.doubleValue());
        return Money.valueOf(convertedValue, fromCurrency, RoundingMode.HALF_EVEN.ordinal());
    }
}

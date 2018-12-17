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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ognl.OgnlException;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRate;
import tavant.twms.domain.common.CurrencyFieldValue;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 * 
 */
public class ClaimCurrencyConversionAdvice extends AbstractCurrencyConversionAdvice implements
        CurrencyConversionAdvice {
    /*
     * 
     * (non-Javadoc)
     * @see tavant.twms.domain.claim.ClaimAdvice#claimAdvice(tavant.twms.domain.claim.Claim)
     */
	
	private static String USD = "usd";
	private static String EUR = "eur";
	private static String DEALER_CURRENCY = "dealersCurrency";
	private ConfigParamService configParamService;
    public Object convertFromNaturalToBaseCurrency(Claim claim) {
        try {
            Set<CurrencyFieldValue> currencyFieldValues = this.currencyFieldCollector
                    .collectCurrencyFieldValuesOf(claim);
            CalendarDate repairDate = claim.getRepairDate();
            for (CurrencyFieldValue currencyField : currencyFieldValues) {
                Money toBeConverted = currencyField.getFieldValue();
                if (toBeConverted != null) {
                    Currency naturalCurrency = toBeConverted.breachEncapsulationOfCurrency();
                    Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(naturalCurrency, baseCurrency, repairDate);
                    if (conversionFactor == null) {
                        return Money.valueOf(0.0, naturalCurrency);
                    }
                    Money convertedValue = conversionFactor.convert(toBeConverted, repairDate);
                    currencyField.setFieldValue(convertedValue);
                }
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", e);
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", ex);
        }
        return null;
    }
    
    public Object convertFromNaturalToBaseCurrencyForRecClaim(RecoveryClaim recClaim) {
        try {
            Set<CurrencyFieldValue> currencyFieldValues = this.currencyFieldCollector
                    .collectCurrencyFieldValuesOf(recClaim);
            CalendarDate repairDate = recClaim.getClaim().getRepairDate();
            for (CurrencyFieldValue currencyField : currencyFieldValues) {
                Money toBeConverted = currencyField.getFieldValue();
                if (toBeConverted != null) {
                    Currency naturalCurrency = toBeConverted.breachEncapsulationOfCurrency();
                    Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(naturalCurrency, baseCurrency, repairDate);
                    if (conversionFactor == null) {
                        return Money.valueOf(0.0, baseCurrency);
                    }
                    Money convertedValue = conversionFactor.convert(toBeConverted, repairDate);
                    currencyField.setFieldValue(convertedValue);
                }
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Failed to convert currencies for claim [" + recClaim + "]", e);
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + recClaim + "]", ex);
        }
        return null;
    }
    
    public Money convertMoneyFromNaturalToBaseCurrency(Money valueToConvert, Claim claim) {
        try {
            CalendarDate repairDate = claim.getRepairDate();
                if (valueToConvert != null) {
                    Currency naturalCurrency = valueToConvert.breachEncapsulationOfCurrency();
                    Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
                    if (baseCurrency.getCurrencyCode().equals(naturalCurrency.getCurrencyCode())) {
                        return valueToConvert;
                    }
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(naturalCurrency, baseCurrency, repairDate);
                    CurrencyConversionFactor reverseFactor = this.currencyExchangeRateRepository
                    .findConversionFactor(baseCurrency,naturalCurrency, repairDate);
                    if (conversionFactor  == null && reverseFactor ==null) {
                        return Money.valueOf(0.0, baseCurrency);
                    }
                    Money convertedValue =conversionFactor == null ? reverseFactor.reverseConvert(valueToConvert, repairDate) : 
                    	conversionFactor.convert(valueToConvert, repairDate);
                    /**
                     *    To fix null pointer when conversionFactor is null                 
                     */
                    BigDecimal factor = (null == conversionFactor || conversionFactor.getValue()== null)?reverseFactor.getValue():conversionFactor.getValue();
                    
                    if(!claim.getClaimAudits().isEmpty()){
                    	ClaimAudit latestClaimAudit=claim.getActiveClaimAudit();
                    	latestClaimAudit.setExchangeRate(factor);
                    	latestClaimAudit.setUpdatedTime(new Date());
                    	claim.setActiveClaimAudit(latestClaimAudit);
                    }
                    return convertedValue;
            }
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", ex);
        }
        return null;
    }
    
    public Money convertMoneyUsingAppropriateConFactor(Money valueToConvert,CalendarDate asOfDate, Currency naturalCurrency){
        try {
                if (valueToConvert != null) {
                    Currency baseCurrency = valueToConvert.breachEncapsulationOfCurrency();
                    if (baseCurrency.getCurrencyCode().equals(naturalCurrency.getCurrencyCode())) {
                        return valueToConvert;
                    }
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(baseCurrency,naturalCurrency, asOfDate) ;
                    CurrencyConversionFactor reverseFactor = this.currencyExchangeRateRepository
                    .findConversionFactor(naturalCurrency,baseCurrency, asOfDate);
                    if (conversionFactor  == null && reverseFactor ==null) {
                        return Money.valueOf(0.0, naturalCurrency);
                    }
                    Money convertedValue = conversionFactor == null ? reverseFactor.reverseConvert(valueToConvert, asOfDate) : 
                    	conversionFactor.convert(valueToConvert, asOfDate);
                    return convertedValue;
            }
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("CN004:", ex);
        }
        return null;
    }
    
    public Money convertMoneyFromBaseToNaturalCurrency(Money valueToConvert, CalendarDate asOfDate, Currency naturalCurrency) {
        try {
                if (valueToConvert != null) {
                    Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
                    if (baseCurrency.getCurrencyCode().equals(naturalCurrency.getCurrencyCode())) {
                        return valueToConvert;
                    }
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(naturalCurrency, baseCurrency, asOfDate);
                    if (conversionFactor == null) {
                        return Money.valueOf(0.0, naturalCurrency);
                    }
                    Money convertedValue = conversionFactor.reverseConvert(valueToConvert, asOfDate);
                    return convertedValue;
            }
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies", ex);
        }
        return null;
    }

    public Object convertFromBaseToNaturalCurrency(Claim claim) {
        try {
            Set<CurrencyFieldValue> currencyFieldValues = this.currencyFieldCollector
                    .collectCurrencyFieldValuesOf(claim);
            Currency naturalCurrency = claim.getCurrencyForCalculation();
            CalendarDate repairDate = claim.getRepairDate();
            for (CurrencyFieldValue currencyField : currencyFieldValues) {
                Money toBeConverted = currencyField.getFieldValue();
                if (toBeConverted != null) {
                    Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
                    CurrencyConversionFactor conversionFactor = this.currencyExchangeRateRepository
                            .findConversionFactor(naturalCurrency, baseCurrency, repairDate);
                    if (conversionFactor == null) {
                        return Money.valueOf(0.0, naturalCurrency);
                    }
                    Money convertedValue = conversionFactor.reverseConvert(toBeConverted,
                                                                           repairDate);
                    currencyField.setFieldValue(convertedValue);
                }
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", e);
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", ex);
        }
        return null;
    }

    public Object convertCurrencyOnlyFromNaturalToBase(Claim claim) {
        try {
            Set<CurrencyFieldValue> currencyFieldValues = this.currencyFieldCollector
                    .collectCurrencyFieldValuesOf(claim);
            CalendarDate repairDate = claim.getRepairDate();
            for (CurrencyFieldValue currencyField : currencyFieldValues) {
                Money toBeConverted = currencyField.getFieldValue();
                if (toBeConverted != null) {
                	CurrencyExchangeRate exchangeRate = CurrencyExchangeRate.sameCurrencyExchangeRate(claim.getCurrencyForCalculation());
                	exchangeRate.setToCurrency(GlobalConfiguration.getInstance().getBaseCurrency());
                    CurrencyConversionFactor conversionFactor = new CurrencyConversionFactor();
                    conversionFactor.setParent(exchangeRate);
                    conversionFactor.setValue(new BigDecimal(1));
                    CalendarDuration duration = new CalendarDuration(CalendarDate.date(1900, 1, 1),repairDate.plusMonths(12));
                    conversionFactor.setDuration(duration);
                    Money convertedValue = conversionFactor.convert(toBeConverted, repairDate);
                    currencyField.setFieldValue(convertedValue);
                }
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", e);
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", ex);
        }
        return null;
    }
    
    public Money convertMoneyCurrencyOnlyFromNaturalToBase(Money toBeConverted) {
        try {
                if (toBeConverted != null) {
                	CurrencyExchangeRate exchangeRate = CurrencyExchangeRate.sameCurrencyExchangeRate(toBeConverted.breachEncapsulationOfCurrency());
                	exchangeRate.setToCurrency(GlobalConfiguration.getInstance().getBaseCurrency());
                    CurrencyConversionFactor conversionFactor = new CurrencyConversionFactor();
                    conversionFactor.setParent(exchangeRate);
                    conversionFactor.setValue(new BigDecimal(1));
                    CalendarDuration duration = new CalendarDuration(CalendarDate.date(1900, 1, 1),Clock.today().plusMonths(12));
                    conversionFactor.setDuration(duration);
                    Money convertedValue = conversionFactor.convert(toBeConverted, Clock.today().plusMonths(-1));
                    return convertedValue;
                }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to convert currencies for Money", ex);
        }
        return null;
    }
    
    public Object convertCurrencyOnlyFromBaseToNatural(Claim claim) {
        try {
            Set<CurrencyFieldValue> currencyFieldValues = this.currencyFieldCollector
                    .collectCurrencyFieldValuesOf(claim);
            CalendarDate repairDate = claim.getRepairDate();
            for (CurrencyFieldValue currencyField : currencyFieldValues) {
                Money toBeConverted = currencyField.getFieldValue();
                if (toBeConverted != null) {
                	CurrencyExchangeRate exchangeRate = CurrencyExchangeRate.sameCurrencyExchangeRate(GlobalConfiguration.getInstance().getBaseCurrency());
                	exchangeRate.setFromCurrency(claim.getCurrencyForCalculation());
                    CurrencyConversionFactor conversionFactor = new CurrencyConversionFactor();
                    conversionFactor.setParent(exchangeRate);
                    conversionFactor.setValue(new BigDecimal(1));
                    CalendarDuration duration = new CalendarDuration(CalendarDate.date(1900, 1, 1),repairDate.plusMonths(12));
                    conversionFactor.setDuration(duration);
                    Money convertedValue = conversionFactor.reverseConvert(toBeConverted,
                                                                           repairDate);
                    currencyField.setFieldValue(convertedValue);
                }
            }
        } catch (OgnlException e) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", e);
        } catch (CurrencyConversionException ex) {
            throw new RuntimeException("Failed to convert currencies for claim [" + claim + "]", ex);
        }
        return null;
    }
    
    public String getCurrencyForERPInteractions(Claim claim) {
    	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        List<Object> configValues = configParamService.getListofObjects(ConfigName.ERP_CURRENCY.getName());
        if (configValues == null || !(configValues.get(0) instanceof String) || configValues.get(0) == null) {
            return claim.getCurrencyForCalculation().getCurrencyCode();
        }
        String erpCurrency = (String) configValues.get(0);
        if (USD.equals(erpCurrency) || EUR.equals(erpCurrency)) {
            return erpCurrency.toUpperCase();
        } else if (DEALER_CURRENCY.equalsIgnoreCase(erpCurrency)) {
            return claim.getCurrencyForCalculation().getCurrencyCode();
        }

        return claim.getCurrencyForCalculation().getCurrencyCode();
    }
    
   

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
    
    
}

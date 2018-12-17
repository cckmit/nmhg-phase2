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
package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CurrencyConversionException;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.policy.Policy;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.RelevanceScoreComputerService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public class TravelRatesAdminServiceImpl extends GenericServiceImpl<TravelRates, Long, Exception> implements
        TravelRatesAdminService {
	private static Logger logger = LogManager.getLogger(TravelRatesAdminServiceImpl.class);
	
	private static final String TRAVEL_RATE_WEIGHTS ="TRAVEL RATE WEIGHTS";
	
    private TravelRatesRepository travelRatesRepository;
    
	private CurrencyExchangeRateRepository currencyExchangeRateRepository;
	
	private RelevanceScoreComputerService relevanceScoreComputerService;
	
	/**
	 * @param currencyExchangeRateRepository the currencyExchangeRateRepository to set
	 */
	@Required
	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}

	public TravelRate findTravelRate(Claim claim, Policy policy,String customerType) {
		Criteria priceCriteria = claim.priceCriteriaForTravelExpense(policy);
		Currency dealerCurrency = claim.getCurrencyForCalculation();
		CalendarDate repairDate = claim.getRepairDate();
		return findTravelRate(priceCriteria, repairDate,
				dealerCurrency,customerType);
	}
	
	public TravelRate findTravelRate(Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency,String customerType) {
		List<TravelRate> applicableTravelRates = travelRatesRepository.findTravelRateConfiguration(withCriteria,asOfDate,customerType);
		if (applicableTravelRates == null || applicableTravelRates.size() == 0) {
			return null;
		}
		
		Currency preferredCurrency = withCriteria.getDealerCriterion().getDealer().getPreferredCurrency();
		TravelRate travelRate = findTravelRate(applicableTravelRates, preferredCurrency);
        
		return travelRate;
		/*Currency preferredCurrency = withCriteria.getDealer().getPreferredCurrency();
		List<TravelRateValues> travelRateValuesList = travelRate.getTravelRateValues();
		TravelRateValues travelRateValues = new TravelRateValues();
		for(TravelRateValues rate:travelRateValuesList)
		{
			if(rate.getDistanceRate().breachEncapsulationOfCurrency().getCurrencyCode()==
				preferredCurrency.getCurrencyCode())
			{
				travelRateValues.setId(rate.getId());
				travelRateValues.setDistanceRate(rate.getDistanceRate());
				travelRateValues.setHourlyRate(rate.getHourlyRate());
				travelRateValues.setTripRate(rate.getTripRate());
			}
				
		}
	
		
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		Currency baseCurrency = globalConfiguration.getBaseCurrency();
		CurrencyConversionFactor conversionFactor = currencyExchangeRateRepository
				.findConversionFactor(preferredCurrency, inCurrency,asOfDate);
		if (conversionFactor == null) {
			logger.error("Failed to find exchange rate from currency ["
					+ baseCurrency + "]  to currency [" + inCurrency + "]");
		}
		try {
			Money convertedValue = null;
			
			if(travelRateValues.getDistanceRate() != null)
			{
				convertedValue = conversionFactor.reverseConvert(travelRateValues.getDistanceRate(), asOfDate);
			    travelRateValues.setDistanceRate(convertedValue);
			}
			
			if(travelRateValues.getHourlyRate() != null)
			{
				convertedValue = conversionFactor.reverseConvert(travelRateValues.getHourlyRate(), asOfDate);
				travelRateValues.setHourlyRate(convertedValue);
			}	
			
			if(travelRateValues.getTripRate() != null)
			{
				convertedValue = conversionFactor.reverseConvert(travelRateValues.getTripRate(), asOfDate);
				travelRateValues.setTripRate(convertedValue);
			}
			travelRateValuesList.clear();
			travelRateValuesList.add(travelRateValues);
			travelRate.setTravelRateValues(travelRateValuesList);
			return travelRate;
		} catch (CurrencyConversionException e) {
			logger.error("Failed to convert price to currency [" + inCurrency
					+ "]", e);
			throw new RuntimeException(e);
		}*/
	}
    
	private TravelRateValues getTraveRateValueForPreferedCurrency(TravelRate travelRate, Currency preferredCurrency)
	{
		List<TravelRateValues> travelRateValuesList = travelRate.getTravelRateValues();
		TravelRateValues travelRateValues = new TravelRateValues();
		for(TravelRateValues rate:travelRateValuesList) {
			if(preferredCurrency.getCurrencyCode().equalsIgnoreCase(rate.getCurrencyCode())) {
				travelRateValues.setId(rate.getId());
				travelRateValues.setDistanceRate(rate.getDistanceRate());
				travelRateValues.setHourlyRate(rate.getHourlyRate());
				travelRateValues.setTripRate(rate.getTripRate());
			}
		}
		return 	travelRateValues;
	}

        /**
         *This method returns a travel rate with travelRateValues list containing only one element
         * that is of user preferred currency.
         */
	private TravelRate findTravelRate(List<TravelRate> applicableTravelRates, Currency preferredCurrency) {
		TravelRate travelRate = new TravelRate();
        boolean foundTripRate = false;
        boolean foundTravelRate = false;
        boolean foundDistanceRate = false;
        TravelRateValues travelRateValues = new TravelRateValues();
        for(TravelRate rate : applicableTravelRates) {
        	TravelRateValues values = getTraveRateValueForPreferedCurrency(rate,preferredCurrency);
        	if(!foundDistanceRate) {
        		if(values.getDistanceRate()!=null) {
        			travelRateValues.setDistanceRate(values.getDistanceRate());
        			travelRate.setValueIsDistanceFlatRate(rate.getValueIsDistanceFlatRate());
        			foundDistanceRate = true;
        		}
        	}
        	if(!foundTravelRate) {
        		if(values.getHourlyRate()!=null) {
        			travelRateValues.setHourlyRate(values.getHourlyRate());
        			travelRate.setValueIsHourFlatRate(rate.getValueIsHourFlatRate());
        			foundTravelRate = true;
        		}
        	}
        	if(!foundTripRate) {
        		if(values.getTripRate()!=null) {
        			travelRateValues.setTripRate(values.getTripRate());
        			travelRate.setValueIsTripFlatRate(rate.getValueIsTripFlatRate());
        			foundTripRate = true;
        		}
        	}
        }
//        if(!foundDistanceRate)
//        	travelRateValues.setDistanceRate(Money.valueOf(0.0D, preferredCurrency));
//        if(!foundTravelRate)
//        	travelRateValues.setHourlyRate(Money.valueOf(0.0D, preferredCurrency));
//        if(!foundTripRate)
//        	travelRateValues.setTripRate(Money.valueOf(0.0D, preferredCurrency));
        travelRate.getTravelRateValues().add(travelRateValues);
        return travelRate;
	}
	
    public boolean isUnique(TravelRates price) {
        boolean isUnique = false;
        Criteria forCriteria = price.getForCriteria();
        TravelRates example = null;
        example = travelRatesRepository.findByCriteria(forCriteria,price);
        if (example == null || same(price, example)) {
            isUnique = true;
        }
        return isUnique;
    }

    private boolean same(TravelRates source, TravelRates target) {
        return source.getId() != null 
                && target.getId() != null 
                && source.getId().compareTo(target.getId()) == 0;
    }

    @Override
    public void save(TravelRates entity) {
      long relevanceScore =  relevanceScoreComputerService.computeRelevanceScore(TRAVEL_RATE_WEIGHTS, entity);
      entity.getForCriteria().setRelevanceScore(relevanceScore);
      this.travelRatesRepository.save(entity);
    }

    @Override
    public void update(TravelRates entity) {
    	long relevanceScore = relevanceScoreComputerService.computeRelevanceScore(TRAVEL_RATE_WEIGHTS, entity);
    	entity.getForCriteria().setRelevanceScore(relevanceScore);
        this.travelRatesRepository.update(entity);
    }
    
    /**
     * @param travelRatesRepository the travelRatesRepository to set
     */
    @Required
    public void setTravelRatesRepository(TravelRatesRepository travelRatesRepository) {
        this.travelRatesRepository = travelRatesRepository;
    }

    /* (non-Javadoc)
     * @see tavant.twms.infra.GenericServiceImpl#getRepository()
     */
    @Override
    public GenericRepository<TravelRates, Long> getRepository() {
        return travelRatesRepository;
    }

	/**
	 * Accepts information about travel rates for a particular duration.
	 */
	public void saveOrUpdateTravelRates(TravelRates travelRates) {
		// TODO Method signature needs to be modified or approved by Radha.
		// TODO Also, a real implementation is needed here.
		logger.warn("I am an empty method waiting to be implemented.");
	}
	
	public void setRelevanceScoreComputerService(
			RelevanceScoreComputerService relevanceScoreComputerService) {
		this.relevanceScoreComputerService = relevanceScoreComputerService;
	}

	@Override
	public void delete(TravelRates travelRates) {
		for (TravelRate rate : travelRates.getRates()) {
			rate.getD().setActive(Boolean.FALSE);
        }
		travelRates.getD().setActive(Boolean.FALSE);
		super.update(travelRates);
	}
	
}
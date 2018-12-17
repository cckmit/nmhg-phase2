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
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
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
public class LaborRatesAdminServiceImpl extends
		GenericServiceImpl<LaborRates, Long, Exception> implements
		LaborRatesAdminService {
	private LaborRatesRepository laborRatesRepository;
	
	private LaborHistoryRepository laborHistoryRepository;
	private LaborRateValues  laborRateValues;
	
	public LaborHistoryRepository getLaborHistoryRepository() {
		return laborHistoryRepository;
	}

	private CurrencyExchangeRateRepository currencyExchangeRateRepository;
	
	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;
	
	private RelevanceScoreComputerService relevanceScoreComputerService;
	
	private static final String LABOR_RATE_WEIGHTS ="LABOR RATE WEIGHTS";

	/**
	 * @param laborRatesRepository
	 *            the laborRatesRepository to set
	 */
	@Required
	public void setLaborRatesRepository(
			LaborRatesRepository laborRatesRepository) {
		this.laborRatesRepository = laborRatesRepository;
	}

	@Required
	public void setLaborHistoryRepository(
			LaborHistoryRepository laborHistoryRepository) {
		this.laborHistoryRepository = laborHistoryRepository;
	}

	/**
	 * @param currencyExchangeRateRepository
	 *            the currencyExchangeRateRepository to set
	 */
	@Required
	public void setCurrencyExchangeRateRepository(
			CurrencyExchangeRateRepository currencyExchangeRateRepository) {
		this.currencyExchangeRateRepository = currencyExchangeRateRepository;
	}
	
	
	public Money findLaborRate(Claim claim, Policy policy,String customerType) {
		Criteria priceCriteria = claim.priceCriteriaForLaborExpense(policy);
		Currency dealerCurrency = claim.getCurrencyForCalculation();
		CalendarDate repairDate = claim.getRepairDate();
		return findLaborRate(priceCriteria, repairDate,dealerCurrency,customerType);
	}

	public Money findLaborRate(Criteria withCriteria,
			CalendarDate asOfDate, Currency inCurrency,String customerType) {
			LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(withCriteria,asOfDate,customerType);
			if (laborRate == null) {
			return null;
	}
		
			Currency preferredCurrency = withCriteria.getDealer().getPreferredCurrency();
			Money priceInOEMCurrency = Money.valueOf(new BigDecimal(0.00),preferredCurrency,2);
			List<LaborRateValues> laborRateValuesList = laborRate.getLaborRateValues();
			for(LaborRateValues laborRateValues:laborRateValuesList)	{
				if(laborRateValues.getRate().breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(preferredCurrency.getCurrencyCode())){
				priceInOEMCurrency = laborRateValues.getRate();
				}
			}
			Money laborCost =  priceInOEMCurrency;
		
			return laborCost;
		/*GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		Currency baseCurrency = globalConfiguration.getBaseCurrency();
		CurrencyConversionFactor conversionFactor = currencyExchangeRateRepository
				.findConversionFactor(priceInOEMCurrency.breachEncapsulationOfCurrency(), inCurrency, asOfDate);
		if (conversionFactor == null) {
			logger.error("Failed to find exchange rate from currency ["
					+ preferredCurrency + "]  to currency [" + inCurrency + "]");
		}
		try {
			return conversionFactor.convert(priceInOEMCurrency, asOfDate);
		} catch (CurrencyConversionException e) {
			logger.error("Failed to convert price to currency [" + inCurrency
					+ "]", e);
			throw new RuntimeException(e);
		}*/
		}

		private static Logger logger = LogManager
			.getLogger(ItemPriceAdminServiceImpl.class);

		public boolean isUnique(LaborRates price) {
			boolean isUnique = false;
			Criteria forCriteria = price.getForCriteria();		
			LaborRates example = null;
			example = laborRatesRepository.findByCriteria(forCriteria,price);
			if (example == null || same(price, example)) {
			isUnique = true;
			}
		return isUnique;
	}

	private boolean same(LaborRates source, LaborRates target) {
		return source.getId() != null && target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0;
	}
	

	/**
	 * Accepts information about labor rates for a particular duration.
	 */
	public void saveLaborRates(LaborRates laborRates,String comments) throws Exception{
			long score =  relevanceScoreComputerService.computeRelevanceScore(LABOR_RATE_WEIGHTS, laborRates);
			laborRates.getForCriteria().setRelevanceScore(score);
             
			List<LaborRate> laborRateList= laborRates.getRates();
			LaborRateAudit laborRateAuditObj = new LaborRateAudit();
			for(LaborRate laborRate:laborRateList){
	        
	    	   	List<LaborRateValues> laborRateValues = laborRate.getLaborRateValues();
	    	   	for(LaborRateValues laborRateValue:laborRateValues){
	        	if(laborRateValue.getRate().breachEncapsulationOfCurrency()!=null)
	        	{
	        		LaborRateRepairDateAudit laborRateRepairDateAudit=new LaborRateRepairDateAudit();
	        		laborRateRepairDateAudit.setRate(laborRateValue.getRate());
	        		laborRateRepairDateAudit.setDuration(laborRate.getDuration());
	        		laborRateAuditObj.getLaborRateRepairDateAudits().add(laborRateRepairDateAudit);
	        	}
	        }
	    	   	laborRateAuditObj.setClaimType(laborRates.getForCriteria().getClaimType().getType());
	    	   	laborRateAuditObj.setWarrantyType(laborRates.getForCriteria().getWarrantyType());
                if(null != laborRates.getForCriteria().getProductType()){
                    laborRateAuditObj.setProductName(laborRates.getForCriteria().getProductType().getName());
                }
		    	laborRateAuditObj.setDealerCriterion(laborRates.getCriteria().getDealerCriterion());
		    	laborRateAuditObj.setCustomerType(laborRates.getCustomerType());
		    	laborRateAuditObj.setComments(comments);
	  }
	 laborRates.getLaborRateAudits().add(laborRateAuditObj);  
     save(laborRates);        
	}
    
	public void updateLaborRates(LaborRates laborRates,String comments)throws Exception {
	      long score =  relevanceScoreComputerService.computeRelevanceScore(LABOR_RATE_WEIGHTS, laborRates);
	      laborRates.getForCriteria().setRelevanceScore(score);
	      List<LaborRate> laborRateList= laborRates.getRates();
	      LaborRateAudit laborRateAuditObj = new LaborRateAudit();
	        for(LaborRate laborRate:laborRateList){
	           if(laborRate.getD().isActive()){
                   List<LaborRateValues> laborRateValues = laborRate.getLaborRateValues();
                   for(LaborRateValues laborRateValue:laborRateValues){
                       if(laborRateValue.getRate().breachEncapsulationOfCurrency()!=null)
                       {
                            LaborRateRepairDateAudit laborRateRepairDateAudit=new LaborRateRepairDateAudit();
                            laborRateRepairDateAudit.setRate(laborRateValue.getRate());
                            laborRateRepairDateAudit.setDuration(laborRate.getDuration());
                            laborRateAuditObj.getLaborRateRepairDateAudits().add(laborRateRepairDateAudit);
                       }
                   }
                   laborRateAuditObj.setClaimType(laborRates.getForCriteria().getClmTypeName());
                   laborRateAuditObj.setWarrantyType(laborRates.getForCriteria().getWarrantyType());
                   laborRateAuditObj.setProductName(laborRates.getForCriteria().getProductName());
                   laborRateAuditObj.setDealerCriterion(laborRates.getCriteria().getDealerCriterion());
                   laborRateAuditObj.setCustomerType(laborRates.getCustomerType());
                   laborRateAuditObj.setComments(comments);
               }
	       }
	       laborRates.getLaborRateAudits().add(laborRateAuditObj);
	       update(laborRates);
	}
	
	
	@Override
	public GenericRepository<LaborRates, Long> getRepository() {
		return laborRatesRepository;
	}
	
	public void setRelevanceScoreComputerService(
			RelevanceScoreComputerService relevanceScoreComputerService) {
		this.relevanceScoreComputerService = relevanceScoreComputerService;
	}

	public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}
	
	@Override
	public void delete(LaborRates laborRates) {
		for (LaborRate rate : laborRates.getRates()) {
			rate.getD().setActive(Boolean.FALSE);
        }
		laborRates.getD().setActive(Boolean.FALSE);
		this.update(laborRates);
	}

	
}
/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.claim.payment.rates.TravelRatesAdminService;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.policy.Policy;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public abstract class TravelPaymentComputer extends AbstractPaymentComponentComputer {
	private static Logger logger = LogManager.getLogger(TravelPaymentComputer.class);
	private TravelRatesAdminService travelRatesAdminService;
	
	 public Money computeBaseAmount(PaymentContext paymentContext) {
        Claim claim = paymentContext.getClaim();
        Policy applicablePolicy = paymentContext.getPolicy();        
        TravelRate travelRate = computeBySection(claim, applicablePolicy);
        Money travelRateAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());       
        Money baseAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());        
        TravelDetail travelDetail = claim.getServiceInformation().getServiceDetail().getTravelDetails();
        if (travelRate != null) {
            travelRateAmount = getTravelRateBasedOnSection(travelRate, claim.getCurrencyForCalculation());           
        }
        baseAmount = computeTravelBasedOnSectionAndRate(travelDetail, travelRate, claim.getCurrencyForCalculation(),claim);    
        travelRateAmount = convertAmountIfUnitOfMeasureInKMs(claim, travelDetail, travelRateAmount);
        paymentContext.setRate(travelRateAmount); 	
	
        return baseAmount; 
    }

    public TravelRate computeBySection(Claim forClaim, Policy policy) {

        TravelDetail travelDetail = forClaim.getServiceInformation().getServiceDetail().getTravelDetails();
        if (travelDetail == null) {
            return null;
        }

        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        CalendarDate repairDate = forClaim.getRepairDate();
        Criteria priceCriteriaForTravelExpense = forClaim.priceCriteriaForTravelExpense(policy);
        String customerType = forClaim.getCustomerType();
		TravelRate travelRate = this.travelRatesAdminService.findTravelRate(priceCriteriaForTravelExpense, repairDate, baseCurrency, customerType);
        if( logger.isDebugEnabled()) {
            logger.debug(" travel charge is "+travelRate);
        }
        return travelRate;
    }

    protected Money convertAmountIfUnitOfMeasureInKMs(Claim claim, TravelDetail travelDetail, Money travelRateAmount) {
        return travelRateAmount;
    }

    protected TravelRateValues getTravelRateValueForPreferredCurrency(TravelRate travelRate, Currency preferredCurrency)
    {
        List<TravelRateValues> travelRateValuesList = travelRate.getTravelRateValues();
        TravelRateValues travelRateValues = new TravelRateValues();
        for (TravelRateValues rate : travelRateValuesList) {
            Money tempRate = null;
            if (rate.getDistanceRate() != null) {
                tempRate = rate.getDistanceRate();
            } else if (rate.getHourlyRate() != null) {
                tempRate = rate.getHourlyRate();

            } else if (rate.getTripRate() != null) {
                tempRate = rate.getTripRate();
            }
            if (tempRate != null &&
                    tempRate.breachEncapsulationOfCurrency().getCurrencyCode()
                            .equalsIgnoreCase(preferredCurrency.getCurrencyCode())) {
                travelRateValues.setId(rate.getId());
                Money distanceRate = rate.getDistanceRate();
                travelRateValues.setDistanceRate(distanceRate);
                Money hourlyRate = rate.getHourlyRate();
                travelRateValues.setHourlyRate(hourlyRate);
                Money tripRate = rate.getTripRate();
                travelRateValues.setTripRate(tripRate);
            }
        }
        return travelRateValues;
    }
    
    public void setTravelRatesAdminService(TravelRatesAdminService travelRatesAdminService) {
		this.travelRatesAdminService = travelRatesAdminService;
	}
    
    public abstract Money getTravelRateBasedOnSection(TravelRate travelRate, Currency preferredCurrency);

    public abstract Money computeTravelBasedOnSectionAndRate(TravelDetail travelDetail,TravelRate travelRate, Currency preferredCurrency,Claim claim);
}

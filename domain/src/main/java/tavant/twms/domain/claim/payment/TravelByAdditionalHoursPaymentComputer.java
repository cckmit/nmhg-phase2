package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class TravelByAdditionalHoursPaymentComputer extends TravelPaymentComputer {

    public Money getTravelRateBasedOnSection(TravelRate travelRate, Currency preferredCurrency) {
        return getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency).getHourlyRate();
    }

    public Money computeTravelBasedOnSectionAndRate(TravelDetail travelDetail, TravelRate travelRate,
                                                    Currency preferredCurrency,Claim claim) {
    	Money perHourInBaseCurrency=Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
    	if(travelRate!=null)
    	{
        TravelRateValues travelRateValues = getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency);
        perHourInBaseCurrency = travelRateValues.getHourlyRate();
    	}
        travelDetail.setTimeCharge(perHourInBaseCurrency);
        Money travelCost = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
        Money rate = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
        Money travelAcceptedCost = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
        LineItemGroup lineItemGroup=claim.getPayment().createLineItemGroup(Section.ADDITIONAL_TRAVEL_HOURS);  
        BigDecimal acceptedHrs=BigDecimal.ZERO;
        if(claim.getState().equals(ClaimState.DRAFT)||claim.getState().equals(ClaimState.FORWARDED))
  		{     	  
    	  if(travelDetail.getAdditionalHours()!=null)
    	  {
    		  lineItemGroup.setAcceptedQtyHrs(travelDetail.getAdditionalHours().toString());
    		  lineItemGroup.setAskedQtyHrs(travelDetail.getAdditionalHours().toString());
    	  }
    	  else
    	  {
    		  lineItemGroup.setAcceptedQtyHrs(acceptedHrs.toString());  
    		  lineItemGroup.setAskedQtyHrs(acceptedHrs.toString());
    	  }
    	  
  		}
        else
        {
        	if(lineItemGroup.getAcceptedQtyHrs()==null)
        	{
        		if(travelDetail.getAdditionalHours()!=null)
        		{
        			lineItemGroup.setAcceptedQtyHrs(travelDetail.getAdditionalHours().toString());
        		}

        		else
        		{
        			lineItemGroup.setAcceptedQtyHrs(acceptedHrs.toString());  

        		}
        	}
        }
        if (perHourInBaseCurrency == null) {
            return travelCost;
        }
        if (travelDetail.getTimeCharge() != null) {
            travelCost = Money.valueOf(0.0D, travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            rate=Money.valueOf(travelDetail.getTimeCharge().breachEncapsulationOfAmount(), travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            travelAcceptedCost = Money.valueOf(0.0D, travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            if (travelRate!=null&&travelRate.getValueIsHourFlatRate() != null && travelRate.getValueIsHourFlatRate() && travelDetail.getAdditionalHours() != null && travelDetail.getAdditionalHours().doubleValue() > 0) {
                travelCost = travelCost.plus(travelDetail.getTimeCharge());
                travelAcceptedCost = travelCost.plus(travelDetail.getTimeCharge());
            } else {
                if (travelDetail.getAdditionalHours() != null) {
                    travelCost = travelCost.plus(travelDetail.getTimeCharge().times(travelDetail.getAdditionalHours()));
                    acceptedHrs=new BigDecimal(lineItemGroup.getAcceptedQtyHrs());
            		travelAcceptedCost = travelAcceptedCost.plus(travelDetail.getTimeCharge().times(acceptedHrs));
                }
            }
        }
        if(travelDetail.getAdditionalHours()!=null)
        {
        	
        	if(!claim.getPayment().isFlatAmountApplied())
        	{
        		travelAcceptedCost=travelAcceptedCost.times(lineItemGroup.getPercentageAcceptance()).dividedBy(100.00);
        	}
        }
   
        if(!claim.getPayment().isFlatAmountApplied())
    	{
        lineItemGroup.setAcceptedTotal(travelAcceptedCost);  
    	}
        lineItemGroup.setStateMandate(claim,travelAcceptedCost,Section.ADDITIONAL_TRAVEL_HOURS);
        lineItemGroup.setRate(rate);    
        lineItemGroup.setStateMandateRate(rate); 
        return travelCost;
    }
}

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
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;

public class TravelByTripPaymentComputer extends TravelPaymentComputer {

    public Money getTravelRateBasedOnSection(TravelRate travelRate, Currency preferredCurrency) {
        return getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency).getTripRate();
    }

    public Money computeTravelBasedOnSectionAndRate(TravelDetail travelDetail, TravelRate travelRate,
                                                    Currency preferredCurrency,Claim claim) {
    	Money perTripInBaseCurrency=Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
    	if(travelRate!=null)
    	{
        TravelRateValues travelRateValues = getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency);
         perTripInBaseCurrency = travelRateValues.getTripRate();
    	}
        travelDetail.setTripCharge(perTripInBaseCurrency);
        BigDecimal acceptedQty=BigDecimal.ZERO;
        Money travelCost = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());      
        LineItemGroup lineItemGroup=claim.getPayment().createLineItemGroup(Section.TRAVEL_BY_TRIP);  
            	  
        if(travelDetail.getTrips()!=null)
        {        	
        	lineItemGroup.setAskedQtyHrs(travelDetail.getTrips().toString());    
        	lineItemGroup.setAcceptedQtyHrs(travelDetail.getTrips().toString());  
        }
        else
        {
        	
        	lineItemGroup.setAskedQtyHrs(acceptedQty.toString());
        	lineItemGroup.setAcceptedQtyHrs(acceptedQty.toString());  
        }      
        travelCost=Money.valueOf(BigDecimal.ZERO,claim.getCurrencyForCalculation());
        lineItemGroup.setAcceptedTotal(travelCost);        
        lineItemGroup.setStateMandate(claim,travelCost,Section.TRAVEL_BY_TRIP);
        return travelCost;
    }
}

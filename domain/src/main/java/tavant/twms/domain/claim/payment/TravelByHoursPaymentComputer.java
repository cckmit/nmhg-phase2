package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class TravelByHoursPaymentComputer extends TravelPaymentComputer {

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
        LineItemGroup lineItemGroup=claim.getPayment().createLineItemGroup(Section.TRAVEL_BY_HOURS);  
        LineItemGroup travelByTrip=claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_TRIP);    	
        BigDecimal acceptedHrs=BigDecimal.ZERO;
        BigDecimal travelHour=BigDecimal.ZERO;
        
		if (travelDetail!=null&&!StringUtils.isEmpty(travelDetail.getHours())) {
			travelHour = new BigDecimal(travelDetail.getHours());
			/*if (travelByTrip != null) {
				if (travelDetail.getTrips() != null) {
					travelHour=travelHour.multiply(new BigDecimal(travelDetail.getTrips()*2));
				}
			}*/
		}
		lineItemGroup.setAskedQtyHrs(travelHour.toString());  
        if(claim.getState().equals(ClaimState.DRAFT)||claim.getState().equals(ClaimState.FORWARDED))
  		{   	    	 
    		  lineItemGroup.setAcceptedQtyHrs(travelHour.toString());    	  
  		}
        else
        {
        	if(lineItemGroup.getAcceptedQtyHrs()==null)
        	{        			 	 
        			lineItemGroup.setAcceptedQtyHrs(travelDetail.getHours().toString());  		     		
        	}
        }
        if (perHourInBaseCurrency == null) {
            return travelCost;
        }

        if (travelDetail.getTimeCharge() != null) {
        	rate= Money.valueOf(travelDetail.getTimeCharge().breachEncapsulationOfAmount(), travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            travelCost = Money.valueOf(0.0D, travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            travelAcceptedCost = Money.valueOf(0.0D, travelDetail.getTimeCharge().breachEncapsulationOfCurrency());
            if (travelRate!=null&&travelRate.getValueIsHourFlatRate() != null && travelRate.getValueIsHourFlatRate() &&!StringUtils.isEmpty(travelDetail.getHours()) && Double.parseDouble(travelDetail.getHours()) > 0) {
                travelCost = travelCost.plus(travelDetail.getTimeCharge());
                travelAcceptedCost  = travelCost.plus(travelDetail.getTimeCharge());
            } else {
                if (travelDetail.getHours() != null) {                	                	
                	Money travelAmount=getTravelAmount(travelHour,travelDetail.getTimeCharge());
                    travelCost = travelCost.plus(travelAmount);                  
                    acceptedHrs=new BigDecimal(lineItemGroup.getAcceptedQtyHrs());
                    Money AcceptedAmount=getTravelAmount(acceptedHrs,travelDetail.getTimeCharge());
            		travelAcceptedCost =travelAcceptedCost.plus(AcceptedAmount);
                }
            }
        }
        if(travelByTrip!=null)
        {
        	Integer travelTrip=new Integer(1);
        	Integer acceptedTravelTrip=new Integer(1);
        	if(travelDetail.getTrips()!=null)
        	{
        	 travelTrip=travelDetail.getTrips();
        	} 
        	if(travelTrip.equals(0))
        	{
        	travelCost=Money.valueOf(BigDecimal.ZERO,claim.getCurrencyForCalculation());
        	}
        	if(travelByTrip.getAcceptedQtyHrs()!=null)
        	{        		
        		acceptedTravelTrip=Integer.parseInt(travelByTrip.getAcceptedQtyHrs()); 
        		if(acceptedTravelTrip.equals(0))            	
        			travelAcceptedCost=Money.valueOf(BigDecimal.ZERO,claim.getCurrencyForCalculation());        		
        	}        	
        }
       
        if(travelDetail.getHours()!=null)
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
        lineItemGroup.setStateMandate(claim,travelAcceptedCost,Section.TRAVEL_BY_HOURS);
        lineItemGroup.setRate(rate);    
        lineItemGroup.setStateMandateRate(rate); 
        return travelCost;
    }   
    
    Money getTravelAmount(BigDecimal travelHour,Money rate)
    {
    	String[] hours=travelHour.toString().split("\\.");
    	Integer hour = new Integer(0);
    	Integer minute = new Integer(0);
    	if(hours.length > 0){
        	hour=Integer.parseInt(hours[0]);
        	if(hours.length > 1)
        		minute=Integer.parseInt(hours[1]);
    	}
    	Money ratePerMinute=rate.dividedBy(60);
    	Money totalAmount=rate.times(hour);
    	totalAmount=totalAmount.plus(ratePerMinute.times(minute));
    	return totalAmount;    	
    }
    
}

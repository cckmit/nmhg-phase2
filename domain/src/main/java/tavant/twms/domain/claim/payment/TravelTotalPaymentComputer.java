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
import java.math.RoundingMode;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.infra.BigDecimalFactory;

import com.domainlanguage.money.Money;

/**
 * @author ajitkumar.singh
 * 
 */
public class TravelTotalPaymentComputer extends AbstractPaymentComponentComputer {
	private static Logger logger = LogManager.getLogger(TravelTotalPaymentComputer.class);

	public Money computeBaseAmount(PaymentContext ctx) {    
		Claim claim=ctx.getClaim(); 	

		Money totalTravelBaseAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()); 
		Money totalTravelGroupAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()); 
		Money totalTravelStateMandateAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()); 
		Money totalTravelAcceptedAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());  
		BigDecimal totalAskedQty=BigDecimal.ZERO;
		BigDecimal totalacceptedQty=BigDecimal.ZERO;
		LineItemGroup travelByTrip=claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_TRIP);    	
		LineItemGroup travelByHour=claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_HOURS);
		LineItemGroup additionalTravelHours=claim.getPayment().getLineItemGroup(Section.ADDITIONAL_TRAVEL_HOURS);
		LineItemGroup travel=claim.getPayment().createLineItemGroup(ctx.getSectionName());  
	/*	if(travelByTrip!=null)
		{    	
			totalTravelBaseAmount=totalTravelBaseAmount.plus(travelByTrip.getBaseAmount());
			totalTravelStateMandateAmount=totalTravelStateMandateAmount.plus(travelByTrip.getGroupTotalStateMandateAmount());
			totalTravelAcceptedAmount=totalTravelAcceptedAmount.plus(travelByTrip.getAcceptedTotal()); 
			if(travelByTrip.getAskedQtyHrs()!=null)
				totalAskedQty=totalAskedQty.add(new BigDecimal(Double.parseDouble(travelByTrip.getAskedQtyHrs())));
				if(travelByTrip.getAcceptedQtyHrs())
				{
				totalacceptedQty=totalacceptedQty.add(new BigDecimal(Double.parseDouble(travelByTrip.getAcceptedQtyHrs())));
				}
			
		}*/
		if(travelByHour!=null)
		{    		
			totalTravelBaseAmount=totalTravelBaseAmount.plus(travelByHour.getBaseAmount());
			totalTravelGroupAmount=totalTravelGroupAmount.plus(travelByHour.getGroupTotal());
			totalTravelStateMandateAmount=totalTravelStateMandateAmount.plus(travelByHour.getGroupTotalStateMandateAmount());
			if(travelByHour.getAcceptedTotal()!=null)
				totalTravelAcceptedAmount=totalTravelAcceptedAmount.plus(travelByHour.getAcceptedTotal()); 
			if(StringUtils.hasText(travelByHour.getAskedQtyHrs()))
				totalAskedQty=totalAskedQty.add(new BigDecimal(travelByHour.getAskedQtyHrs()));
			if(StringUtils.hasText(travelByHour.getAcceptedQtyHrs()))
			{
				totalacceptedQty=totalacceptedQty.add(new BigDecimal(travelByHour.getAcceptedQtyHrs()));
			}
			
		}
		if(additionalTravelHours!=null)
		{    		
			totalTravelBaseAmount=totalTravelBaseAmount.plus(additionalTravelHours.getBaseAmount());
			totalTravelGroupAmount=totalTravelGroupAmount.plus(additionalTravelHours.getGroupTotal());
			totalTravelStateMandateAmount=totalTravelStateMandateAmount.plus(additionalTravelHours.getGroupTotalStateMandateAmount());
			if(additionalTravelHours.getAcceptedTotal()!=null)
				totalTravelAcceptedAmount=totalTravelAcceptedAmount.plus(additionalTravelHours.getAcceptedTotal()); 
			if(additionalTravelHours.getAskedQtyHrs()!=null)
				totalAskedQty=totalAskedQty.add(new BigDecimal(additionalTravelHours.getAskedQtyHrs()));
			if(additionalTravelHours.getAcceptedQtyHrs()!=null && !additionalTravelHours.getAcceptedQtyHrs().isEmpty())
			{
				totalacceptedQty=totalacceptedQty.add(new BigDecimal(additionalTravelHours.getAcceptedQtyHrs()));
			}
			
		}	
		travel.setAcceptedTotal(totalTravelAcceptedAmount);		
		travel.setAskedQtyHrs(totalAskedQty.toString());  			
		travel.setAcceptedQtyHrs(totalacceptedQty.toString());
		travel.setStateMandate(claim,totalTravelStateMandateAmount,ctx.getSectionName());
		travel.setGroupTotal(totalTravelGroupAmount);

		logger.info(" total travel cost is "+totalTravelBaseAmount);
		return totalTravelBaseAmount;             

	}
}


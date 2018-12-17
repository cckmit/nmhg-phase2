package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class HandlingFeeComputer extends AbstractPaymentComponentComputer{

	 public Money computeBaseAmount(PaymentContext ctx) {
		    Claim claim=ctx.getClaim();
			LineItemGroup lineItemGroup=claim.getPayment().createLineItemGroup(ctx.getSectionName());  
	        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
	        Money handlingFee = serviceDetail.getHandlingFee();
	        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
	        if( handlingFee==null ) {
	        	handlingFee = Money.valueOf(0,baseCurrency);
	        }	  
	        lineItemGroup.setStateMandate(claim,handlingFee,ctx.getSectionName());
	        lineItemGroup.setPercentageAcceptance(BigDecimal.ZERO);
	        return handlingFee;
	    }

}

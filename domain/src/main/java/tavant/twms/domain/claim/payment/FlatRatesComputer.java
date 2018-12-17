package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.Currency;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class FlatRatesComputer extends AbstractPaymentComponentComputer {
	private static Logger logger = LogManager.getLogger(FlatRatesComputer.class);
	private String costCategoryCode;
	public static final String DEDUCTIBLE="Deductible";   
	
    public Money computeBaseAmount(PaymentContext ctx) {
    	Claim claim=ctx.getClaim();
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
       /* Money expense = getExpense(serviceDetail);
        if( expense==null ) {
        	Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
            expense = Money.valueOf(0,baseCurrency);
        }
        return expense;*/
        
        Money totalOtherBaseAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()); 
        Money totalOtherGroupAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()); 
		Money totalOtherAcceptedAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		 Money stateMandateAmt = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		
		LineItemGroup itemFreightDuty=claim.getPayment().getLineItemGroup(Section.ITEM_FREIGHT_DUTY);    	
		LineItemGroup transpotation=claim.getPayment().getLineItemGroup(Section.TRANSPORTATION_COST);
		LineItemGroup handlingFee=claim.getPayment().getLineItemGroup(Section.HANDLING_FEE);
		LineItemGroup deductible=null;
		LineItemGroup others=claim.getPayment().createLineItemGroup(ctx.getSectionName());  
		if(itemFreightDuty!=null)
		{    	
			totalOtherBaseAmount=totalOtherBaseAmount.plus(itemFreightDuty.getBaseAmount());
			totalOtherGroupAmount=totalOtherGroupAmount.plus(itemFreightDuty.getGroupTotal());
			stateMandateAmt=stateMandateAmt.plus(itemFreightDuty.getGroupTotalStateMandateAmount());
			totalOtherAcceptedAmount=totalOtherAcceptedAmount.plus(itemFreightDuty.getAcceptedTotal()); 			
		}
		if(transpotation!=null)
		{    		
			totalOtherBaseAmount=totalOtherBaseAmount.plus(transpotation.getBaseAmount());
			totalOtherGroupAmount=totalOtherGroupAmount.plus(transpotation.getGroupTotal());
			stateMandateAmt=stateMandateAmt.plus(transpotation.getGroupTotalStateMandateAmount());
			totalOtherAcceptedAmount=totalOtherAcceptedAmount.plus(transpotation.getAcceptedTotal()); 			
		}
		if(handlingFee!=null)
		{    		
			totalOtherBaseAmount=totalOtherBaseAmount.plus(handlingFee.getBaseAmount());
			totalOtherGroupAmount=totalOtherGroupAmount.plus(handlingFee.getGroupTotal());
			stateMandateAmt=stateMandateAmt.plus(handlingFee.getGroupTotalStateMandateAmount());
			totalOtherAcceptedAmount=totalOtherAcceptedAmount.plus(handlingFee.getAcceptedTotal());			
		}	
		Money deductibleAmount = claim.getPayment().getDeductibleAmount();
		//Money deductibleAcceptedAmount=deductibleAmount;
		if (deductibleAmount != null) {
			deductible = claim.getPayment().createLineItemGroup(DEDUCTIBLE);
			/*if (!claim.getState().equals(ClaimState.DRAFT)) {
				deductible = claim.getPayment().createLineItemGroup(DEDUCTIBLE);				
			} else {
				deductibleAmount=Money.valueOf(BigDecimal.ZERO,
						claim.getCurrencyForCalculation());				
			}*/
			if(claim.getState().equals(ClaimState.DRAFT))
			{
				deductibleAmount=Money.valueOf(BigDecimal.ZERO,
						claim.getCurrencyForCalculation());			
			}
		} else {

			deductible = claim.getPayment().getLineItemGroup(DEDUCTIBLE);
			if (deductible != null) {
				deductibleAmount=Money.valueOf(BigDecimal.ZERO,
						claim.getCurrencyForCalculation());
				//deductibleAcceptedAmount=deductibleAmount;
			}
		}		
		if(deductible!=null)
		{
		deductible.setBaseAmount(deductibleAmount);
		deductible.setGroupTotal(deductibleAmount);
		deductible.setAcceptedTotal(deductibleAmount);
		deductible.setStateMandate(claim, deductibleAmount, DEDUCTIBLE);		
		totalOtherBaseAmount = totalOtherBaseAmount
				.minus(deductibleAmount);
		totalOtherGroupAmount=totalOtherGroupAmount.minus(deductibleAmount);
		totalOtherAcceptedAmount = totalOtherAcceptedAmount
				.minus(deductibleAmount);
		stateMandateAmt = stateMandateAmt.minus(deductibleAmount);	
		}
		
		others.setGroupTotal(totalOtherGroupAmount);
		others.setAcceptedTotal(totalOtherAcceptedAmount);
		others.setStateMandate(claim,stateMandateAmt,ctx.getSectionName());  	

		logger.info(" total other cost is "+totalOtherBaseAmount);
		return totalOtherBaseAmount;             

    }
    
    /*private Money getExpense(ServiceDetail serviceDetail) {
    	if(costCategoryCode.equals(CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE))
    		return serviceDetail.getLocalPurchaseExpense();
    	else if(costCategoryCode.equals(CostCategory.TOLLS_COST_CATEGORY_CODE))
    		return serviceDetail.getTollsExpense();
    	else if(costCategoryCode.equals(CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE))
    		return serviceDetail.getOtherFreightDutyExpense();
    	else if(costCategoryCode.equals(CostCategory.OTHERS_CATEGORY_CODE))
    		return serviceDetail.getOthersExpense();
    	return null;
    }*/
    
    public void setCostCategoryCode(String costCategoryCode) {
    	this.costCategoryCode = costCategoryCode;
    }

}

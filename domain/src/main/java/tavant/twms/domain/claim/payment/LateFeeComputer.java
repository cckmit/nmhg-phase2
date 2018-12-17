package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.policy.WarrantyType;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class LateFeeComputer  extends AbstractPaymentComponentComputer {
	
	private static final int addDays60 =60,addDays90=90,addDays120=120;
	
	public Money computeBaseAmount(PaymentContext ctx) {
		  Claim claim=ctx.getClaim();
		  Payment payment = ctx.getClaim().getPayment();
		  Money lateFee = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		  Money lateFeeOnAcceptedAmount = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		  Money lateFeeSMandate = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		  Money acceptedLateFee= Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		  Money zeroAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
		  Money deductibleAmount=payment.getDeductibleAmount();
		  BigDecimal lateFeePercentage=BigDecimal.ZERO;
		  LineItemGroup lineItemGrp = null;
		  LineItemGroup lineItemGrpTotal = claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM);
			  lineItemGrp=claim.getPayment().createLineItemGroup(Section.LATE_FEE);
			  if((claim.getRepairDate().isBefore(claim.getFiledOnDate())) || (claim.getRepairDate().equals(claim.getFiledOnDate()))){
					CalendarDate after60Days = claim.getRepairDate().plusDays(addDays60);
					CalendarDate after90Days = claim.getRepairDate().plusDays(addDays90);
					CalendarDate after120Days = claim.getRepairDate().plusDays(addDays120);
					Money totalAmount=claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM).getGroupTotal();
					Money totalAcceptedAmount=claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM).getAcceptedTotal();
					Money totalStateMandateAmount=claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM).getGroupTotalStateMandateAmount();
					if(claim.getState().equals(ClaimState.DRAFT))
					{
						if(claim.getPayment().getDeductibleAmount()!=null)
						{
							totalAmount=totalAmount.minus(deductibleAmount);
							totalAcceptedAmount=totalAcceptedAmount.minus(deductibleAmount);
							totalStateMandateAmount=totalStateMandateAmount.minus(deductibleAmount);
						}
					}
					if((claim.getFiledOnDate().isBefore(after60Days) || (claim.getFiledOnDate().equals(after60Days)))){ // checks for if filed date is 60 days or less,after repair date 
						lateFee = totalAmount.times(new BigDecimal(0.00));
						lateFeeOnAcceptedAmount=totalAcceptedAmount.times(new BigDecimal(0.00));
						lateFeeSMandate=totalStateMandateAmount.times(new BigDecimal(0.00));
					}else if(claim.isLateFeeEnabledFrom61to90days()){
						if((claim.getFiledOnDate().isBefore(after90Days) && claim.getFiledOnDate().isAfter(after60Days)) || (claim.getFiledOnDate().equals(after90Days))){ // checks if filed date is between 61-90 days after repair date
							lateFeePercentage=claim.getLateFeeValueFrom61to90days();
							lateFee = totalAmount.times(claim.getLateFeeValueFrom61to90days().divide(new BigDecimal(100.00)));
							lateFeeOnAcceptedAmount=totalAcceptedAmount.times(claim.getLateFeeValueFrom61to90days().divide(new BigDecimal(100.00)));
							lateFeeSMandate=totalStateMandateAmount.times(claim.getLateFeeValueFrom61to90days().divide(new BigDecimal(100.00)));
						}
					}
					if(claim.isLateFeeEnabledFrom91to120days()){
						if((claim.getFiledOnDate().isBefore(after120Days) && claim.getFiledOnDate().isAfter(after90Days)) || (claim.getFiledOnDate().equals(after120Days))){ // checks if filed date is between 91-120 days after repair date
							lateFeePercentage=claim.getLateFeeValueFrom91to120days();
							lateFee = totalAmount.times(claim.getLateFeeValueFrom91to120days().divide(new BigDecimal(100.00)));
							lateFeeOnAcceptedAmount=totalAcceptedAmount.times(claim.getLateFeeValueFrom91to120days().divide(new BigDecimal(100.00)));
							lateFeeSMandate=totalStateMandateAmount.times(claim.getLateFeeValueFrom91to120days().divide(new BigDecimal(100.00)));
						}
					}
					if(claim.getFiledOnDate().isAfter(after120Days))
					{
						lateFee=totalAmount;
						lateFeeOnAcceptedAmount=totalAcceptedAmount;
						lateFeeSMandate=totalStateMandateAmount;
						lateFeePercentage=new BigDecimal(100);
					}
					//lineItemGrp.setBaseAmount(lateFee);
				}	
			  lateFee= lateFee.negated();			  
			  lateFeeSMandate=lateFeeSMandate.negated();
			  lateFeeOnAcceptedAmount=lateFeeOnAcceptedAmount.negated();
			  payment.setLateFee(lateFee);
			  payment.setAcceptedLateFee(lateFeeOnAcceptedAmount);
			  payment.setStateMandateLateFee(lateFeeSMandate);
			  //Changes to deduct amount from total
			  if(claim.getState().equals(ClaimState.DRAFT))
			  {
				  lineItemGrp.setAcceptedTotal(zeroAmount);
				  lineItemGrp.setStateMandate(claim, zeroAmount, Section.LATE_FEE);
				  lateFee=zeroAmount;		
				  lateFeeSMandate=zeroAmount;	
				  lateFeeOnAcceptedAmount=zeroAmount;	
			  }
			  else
			  {
				  	lineItemGrpTotal.setBaseAmount(lineItemGrpTotal.getBaseAmount().plus(lateFee));
			  		lineItemGrpTotal.setGroupTotal(lineItemGrpTotal.getGroupTotal().plus(lateFee));
			  		//lineItemGrpTotal.setStateMandate(claim,lineItemGrpTotal.getGroupTotalStateMandateAmount().plus(lateFee),ctx.getSectionName());
			  		if(!claim.getPayment().isFlatAmountApplied())
			  		{
			  			acceptedLateFee=lateFeeOnAcceptedAmount.times(lineItemGrp.getPercentageAcceptance()).dividedBy(100.00);
			  			lateFeeSMandate=lateFeeSMandate.times(lineItemGrp.getPercentageAcceptance()).dividedBy(100.00);
			  			/*lineItemGrpTotal.setAcceptedTotal(lineItemGrpTotal.getAcceptedTotal().plus(acceptedLateFee));*/
			  			 lineItemGrp.setAcceptedTotal(acceptedLateFee);
			  			 lineItemGrp.setStateMandate(claim, lateFeeSMandate, Section.LATE_FEE);
			  			//lineItemGrpTotal.setAcceptedTotal(lineItemGrpTotal.getAcceptedTotal().plus(lateFee));	
			  		}
			  		else
			  		{	
			  		lineItemGrp.setStateMandate(claim, lineItemGrp.getAcceptedTotal(), Section.LATE_FEE);	
			  		//lineItemGrpTotal.setAcceptedTotal(lineItemGrpTotal.getAcceptedTotal().plus(lineItemGrp.getAcceptedTotal()));	
			  		}
			  		lineItemGrpTotal.setAcceptedTotal(lineItemGrpTotal.getAcceptedTotal().plus(lineItemGrp.getAcceptedTotal()));
			  		//lineItemGrp.setStateMandate(claim, lineItemGrp.getAcceptedTotal(), Section.LATE_FEE);
			  		lineItemGrpTotal.setStateMandate(claim, lineItemGrpTotal.getGroupTotalStateMandateAmount().plus(lineItemGrp.getGroupTotalStateMandateAmount()), Section.LATE_FEE);
			  }	
			 
			 /* lineItemGrp.setStateMandate(claim, lateFeeSMandate, Section.LATE_FEE);
			  lineItemGrp.setAcceptedTotal(acceptedLateFee);*/
			  lineItemGrp.setPercentageApplicable(lateFeePercentage);
			  //lineItemGrp.setStateMandate(claim, lineItemGrp.getAcceptedTotal(), Section.LATE_FEE);
			 /* lineItemGrpTotal.setStateMandate(claim, lineItemGrpTotal.getGroupTotalStateMandateAmount().plus(lineItemGrp.getAcceptedTotal()), Section.TOTAL_CLAIM);*/
		/*	  lineItemGrpTotal.setAcceptedTotalForCpOfTotalClaimSection(claim);*/
			  if(!payment.isFlatAmountApplied())
			  {
				  if(lineItemGrpTotal.getGroupTotal().breachEncapsulationOfAmount().intValue()!=0)
					  lineItemGrpTotal.setPercentageAcceptance(lineItemGrpTotal.getAcceptedTotal().breachEncapsulationOfAmount().multiply(new BigDecimal(100)).divide(lineItemGrpTotal.getGroupTotal().breachEncapsulationOfAmount(),RoundingMode.HALF_UP));
			  }
			  payment.setAmountSelected(lineItemGrpTotal,claim);	
			  lineItemGrpTotal.setAcceptedForWarrantyAfterLateFee(claim);
			  //lineItemGrpTotal.setAcceptedCpTotal(lineItemGrpTotal.getAcceptedTotalForCp()); // This is for displaying cost price at total level
			  lineItemGrpTotal.setTotalCreditAmountForTotal();
			  payment.setLateFeeCreditAmount(lineItemGrp);			  
			  payment.setTotalAmount(lineItemGrpTotal.getAcceptedTotal());
			  ctx.getClaim().setDisbursedAmountAfterLateFee();
	        return lateFee;
	}

}



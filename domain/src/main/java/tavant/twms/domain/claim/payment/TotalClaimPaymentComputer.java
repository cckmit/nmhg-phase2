package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;


public class TotalClaimPaymentComputer implements PaymentComponentComputer {

	private final String BUSINESS_UNIT_AMER="AMER";
    private ModifierService modifierService;
    private SecurityHelper securityHelper;

    public void compute(PaymentContext ctx) throws PaymentCalculationException {
        PaymentSection totalClaimSection = ctx.getPaymentDefinition().getSectionForName(Section.TOTAL_CLAIM);
        List<PaymentSection> sections = new ArrayList<PaymentSection>(
                ctx.getPaymentDefinition().getPaymentSections());
        if (totalClaimSection != null) {
            sections.remove(totalClaimSection);
        }
        Payment payment = ctx.getClaim().getPayment();
        LineItemGroup summationGroup = payment.createLineItemGroup(Section.TOTAL_CLAIM);
        summationGroup.setModifierAmount(null);
        summationGroup.setBaseAmount(payment.getLineItemGroupsGroupTotal());
        summationGroup.setGroupTotalStateMandateAmount(payment.getLineItemGroupsStateMandateTotal());
        summationGroup.setAcceptedCpTotal(payment.getLineItemGroupsAcceptedTotalAtCP());
        summationGroup.setRate(ctx.getRate());
        modifierService.applyModifiers(ctx.getClaim(), summationGroup, ctx.getPaymentSection());
        summationGroup.setGroupTotal();
       /* summationGroup.setAcceptedTotal();*/
        summationGroup.setAcceptedTotal(payment.getLineItemGroupsAcceptedTotal());
        if(payment.isFlatAmountApplied())
        {
        	summationGroup.setPercentageAcceptance(BigDecimalFactory.bigDecimalOf(0));
        }else
        {
        	if(summationGroup.getGroupTotal().breachEncapsulationOfAmount().intValue()!=0)
        		summationGroup.setPercentageAcceptance(summationGroup.getAcceptedTotal().breachEncapsulationOfAmount().multiply(new BigDecimal(100)).divide(summationGroup.getGroupTotal().breachEncapsulationOfAmount(),RoundingMode.HALF_UP));
        	else
        	{
        	summationGroup.setPercentageAcceptance(BigDecimalFactory.bigDecimalOf(0));
        	}
        }
      /*  if(!isBuConfigAMER())
        {
        setPercentageForCPCalculationOfTotalClaimSection(summationGroup);
        }
        else
        {*/
       
        //}       
        //To check total button
        // Mark checkBox auto Selected based on max amount or amount that processor has selected
        payment.setAmountSelected(summationGroup,ctx.getClaim());	
        summationGroup.setAcceptedTotalForCpOfTotalClaimSection(ctx.getClaim());
        summationGroup.setAcceptedCpTotal(summationGroup.getAcceptedTotalForCp()); // This is for displaying cost price at total level
        payment.setTotalAmount(summationGroup.getAcceptedTotal());
        applyProratedAmount(payment);
        payment.setTotalCreditAmount();
        ctx.getClaim().setDisbursedAmount();
        
        
       
       // payment.sortLineItemBasedOnDisplayPosition(ctx);
			
        //To update total claimed amount
		//summationGroup.setAcceptedTotalForCpOfTotalClaimSection(payment);     
      
        
    }

    private void setPercentageForCPCalculationOfTotalClaimSection(LineItemGroup lineItemGroup) {
    	lineItemGroup.setPercentageAcceptanceForCp(lineItemGroup.getPercentageAcceptanceForCp());
       	lineItemGroup.setPercentageAcceptanceForWnty(lineItemGroup.getPercentageAcceptance());
       	lineItemGroup.setAcceptedTotalForWnty();
        lineItemGroup.setAcceptedTotalForCpOfTotalClaimSection();
    }

    private void applyProratedAmount(Payment payment) {
        BigDecimal lineItemGrpSum = new BigDecimal(0);
        List<LineItemGroup> lineItemGroups = payment.getLineItemGroups();
        for (LineItemGroup lineItemGroup : lineItemGroups) {
            if (lineItemGroup.getName() != null && !lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM)&& !lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL)&& !lineItemGroup.getName().equalsIgnoreCase(Section.OTHERS)) {
                lineItemGrpSum = lineItemGrpSum.add(lineItemGroup.getAcceptedTotal()
                        .breachEncapsulationOfAmount());
            }
        }
        LineItemGroup claimAmtLineItemGroup = payment
                .getLineItemGroup(Section.TOTAL_CLAIM);
        Currency currency = claimAmtLineItemGroup.getAcceptedTotal().breachEncapsulationOfCurrency();
        Money LineItemGroupAmt = Money.valueOf(lineItemGrpSum, currency);
        Money totalProratedAmount = claimAmtLineItemGroup.getModifierAmount();
 /*       if (claimAmtLineItemGroup.getPercentageAcceptance()
                .doubleValue() != new BigDecimal(100).doubleValue()) {
            if (totalProratedAmount != null) {
                totalProratedAmount = totalProratedAmount
                        .plus((claimAmtLineItemGroup.getAcceptedTotal().
                                minus(claimAmtLineItemGroup.getGroupTotal())));
            } else {
                totalProratedAmount = claimAmtLineItemGroup.getAcceptedTotal().
                        minus(claimAmtLineItemGroup.getGroupTotal());
            }
        }*/
        if (totalProratedAmount != null) {
            for (LineItemGroup lineItemGroup : lineItemGroups) {
                if (lineItemGroup.getName() != null && !lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM)&& !lineItemGroup.getName().equalsIgnoreCase(Section.TRAVEL)&& !lineItemGroup.getName().equalsIgnoreCase(Section.OTHERS)) {
                    calculateProratedAmount(lineItemGroup, LineItemGroupAmt,
                            totalProratedAmount, currency, claimAmtLineItemGroup.getPercentageAcceptance());
                    LineItemGroupAmt = LineItemGroupAmt.minus(lineItemGroup.getAcceptedTotal());
                    if (lineItemGroup.getProratedAmount() != null) {
                        totalProratedAmount = totalProratedAmount.minus(lineItemGroup.getProratedAmount());
                    }
                }
            }
        }
    }

    private void calculateProratedAmount(LineItemGroup lineItemGroup,
                                         Money lineItemGroupSum, Money totalProratedAmount,
                                         Currency currency, BigDecimal percentage) {
        BigDecimal lineItemGroupTotal = lineItemGroup.getAcceptedTotal()
                .breachEncapsulationOfAmount();
        BigDecimal proratedAmount = totalProratedAmount
                .breachEncapsulationOfAmount();
        BigDecimal lineItemSum = lineItemGroupSum.breachEncapsulationOfAmount();
        if (lineItemSum.intValue() != 0 && lineItemGroupTotal.intValue() != 0) {
            BigDecimal proratedAmt = lineItemGroupTotal.divide(lineItemSum, 10,
                    BigDecimal.ROUND_CEILING).multiply(proratedAmount);
            lineItemGroup.setProratedAmount(Money.valueOf(proratedAmt,
                    currency, BigDecimal.ROUND_HALF_EVEN));
            totalProratedAmount = totalProratedAmount.minus(lineItemGroup
                    .getProratedAmount());
            // This is required if negative modifier is specified at claim
            // amount level
            if (percentage.doubleValue() == new BigDecimal(100).doubleValue()
                    && totalProratedAmount.isNegative()) {
                lineItemGroup.setProratedAmount(totalProratedAmount);
            }
        }
    }
    
/*    public void setAmountSelected(LineItemGroup summationGroup,Claim claim)
    {
    	if(summationGroup.getAcceptedTotal().isLessThan(summationGroup.getGroupTotalStateMandateAmount())&&claim.getPayment().getTotalAcceptanceChkbox().equals(true))
    	{
    		claim.getPayment().setTotalAcceptStateMdtChkbox(false);
    	}
    	else if(summationGroup.getGroupTotalStateMandateAmount().isLessThan(summationGroup.getAcceptedTotal())&&claim.getPayment().getTotalAcceptStateMdtChkbox().equals(true))
    	{
    		claim.getPayment().setTotalAcceptanceChkbox(false);
    	}
    	else
    	{
    		int result=summationGroup.getAcceptedTotal().compareTo(summationGroup.getGroupTotalStateMandateAmount());
    		if(result==0||result==1)
    		{
    			claim.getPayment().setTotalAcceptanceChkbox(true);
    			claim.getPayment().setTotalAcceptStateMdtChkbox(false);
    		}
    		else					
    		{
    			claim.getPayment().setTotalAcceptStateMdtChkbox(true);
    			claim.getPayment().setTotalAcceptanceChkbox(false);
    		}					
    	}

    }*/


    public void setModifierService(ModifierService modifierService) {
        this.modifierService = modifierService;
    }

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	public BusinessUnit getCurrentBusinessUnit() {
		return this.securityHelper.getDefaultBusinessUnit();
	}
/*	public boolean isBuConfigAMER() {
		return getCurrentBusinessUnit().getName().equals(BUSINESS_UNIT_AMER);
	}	*/
   }

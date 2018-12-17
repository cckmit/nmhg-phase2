package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.domainlanguage.money.Money;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.policy.Policy;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.SecurityHelper;

public abstract class AbstractPaymentComponentComputer implements PaymentComponentComputer {

	public static final String POLICY = "POLICY";
	private final String BUSINESS_UNIT_AMER="AMER";
    private ModifierService modifierService;
    private SecurityHelper securityHelper;

    public void setModifierService(ModifierService modifierService) {
        this.modifierService = modifierService;
    }

    public void compute(PaymentContext ctx) {
        Claim claim = ctx.getClaim();
        Money baseAmount = computeBaseAmount(ctx);
        LineItemGroup lineItemGroup = claim.getPayment().createLineItemGroup(ctx.getSectionName());
        lineItemGroup.setBaseAmount(baseAmount);
        lineItemGroup.setAcceptedCpTotal(ctx.getAcceptedCpTotal());
        lineItemGroup.setRate(ctx.getRate());
        if(!lineItemGroup.getName().equals(Section.LATE_FEE)){
       /* lineItemGroup.getModifiers().clear();*/
          	modifierService.applyModifiers(claim, lineItemGroup, ctx.getPaymentSection());
        }
     	
          	lineItemGroup.setGroupAndAcceptedTotal(claim,lineItemGroup);        	

			lineItemGroup.setAcceptedTotalForCpClaimSection(claim.getPayment());
	
        
        
    }   
	public BusinessUnit getCurrentBusinessUnit() {
		return this.securityHelper.getDefaultBusinessUnit();
	}
	public boolean isBuConfigAMER(Claim forClaim) {
		return forClaim.getBusinessUnitInfo().getName().equals(BUSINESS_UNIT_AMER);
	}	

    public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public ModifierService getModifierService() {
		return modifierService;
	}

	public abstract Money computeBaseAmount(PaymentContext ctx);
}

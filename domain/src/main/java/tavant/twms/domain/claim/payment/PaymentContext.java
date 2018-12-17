package tavant.twms.domain.claim.payment;

import com.domainlanguage.money.Money;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.policy.Policy;

import java.math.BigDecimal;

public class PaymentContext {

    private Claim claim;
    private PaymentDefinition paymentDefinition;
    private Policy policy;
    private String sectionName;
    private Money acceptedCpTotal;
    private Money rate;
  
    private Money acceptedWrAmount;
    private Money ModifierAcceptedCpTotal;
    private Money wrModifierAcceptedTotal;
    private BigDecimal modifierAcceptencePercent;
    private BigDecimal cpModifierAcceptencePercent;
    private BigDecimal wrAcceptencePercent;
    private BigDecimal cpAcceptencePercent;
 
    
    public PaymentContext() {
    }

    public PaymentContext(Claim claim, PaymentDefinition paymentDefinition, Policy policy, String sectionName) {
        this.claim = claim;
        this.paymentDefinition = paymentDefinition;
        this.policy = policy;
        this.sectionName = sectionName;
        this.acceptedCpTotal = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());        
        
        this.acceptedWrAmount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
        this.ModifierAcceptedCpTotal=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
        this.wrModifierAcceptedTotal=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
        modifierAcceptencePercent=new BigDecimal(0);
        cpModifierAcceptencePercent=new BigDecimal(0);
         wrAcceptencePercent=new BigDecimal(0);
        cpAcceptencePercent=new BigDecimal(0);
     
    }

    public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public PaymentDefinition getPaymentDefinition() {
		return paymentDefinition;
	}

	public void setPaymentDefinition(PaymentDefinition paymentDefinition) {
		this.paymentDefinition = paymentDefinition;
	}

	public PaymentSection getPaymentSection() {
		return paymentDefinition.getSectionForName(sectionName);
	}

    public Money getAcceptedCpTotal() {
        return acceptedCpTotal;
    }

    public void setAcceptedCpTotal(Money acceptedCpTotal) {
        this.acceptedCpTotal = acceptedCpTotal;
    }

    public Money getRate() {
        return rate;
    }

    public void setRate(Money rate) {
        this.rate = rate;
    }

    public void reset() {
        this.acceptedCpTotal = Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
        this.rate = null;
    }

	public Money getAcceptedWrAmount() {
		return acceptedWrAmount;
	}

	public void setAcceptedWrAmount(Money acceptedWrAmount) {
		this.acceptedWrAmount = acceptedWrAmount;
	}

	public Money getModifierAcceptedCpTotal() {
		return ModifierAcceptedCpTotal;
	}

	public void setModifierAcceptedCpTotal(Money modifierAcceptedCpTotal) {
		ModifierAcceptedCpTotal = modifierAcceptedCpTotal;
	}

	public Money getWrModifierAcceptedTotal() {
		return wrModifierAcceptedTotal;
	}

	public void setWrModifierAcceptedTotal(Money wrModifierAcceptedTotal) {
		this.wrModifierAcceptedTotal = wrModifierAcceptedTotal;
	}

	public BigDecimal getModifierAcceptencePercent() {
		return modifierAcceptencePercent;
	}

	public void setModifierAcceptencePercent(BigDecimal modifierAcceptencePercent) {
		this.modifierAcceptencePercent = modifierAcceptencePercent;
	}

	public BigDecimal getCpModifierAcceptencePercent() {
		return cpModifierAcceptencePercent;
	}

	public void setCpModifierAcceptencePercent(
			BigDecimal cpModifierAcceptencePercent) {
		this.cpModifierAcceptencePercent = cpModifierAcceptencePercent;
	}

	public BigDecimal getWrAcceptencePercent() {
		return wrAcceptencePercent;
	}

	public void setWrAcceptencePercent(BigDecimal wrAcceptencePercent) {
		this.wrAcceptencePercent = wrAcceptencePercent;
	}

	public BigDecimal getCpAcceptencePercent() {
		return cpAcceptencePercent;
	}

	public void setCpAcceptencePercent(BigDecimal cpAcceptencePercent) {
		this.cpAcceptencePercent = cpAcceptencePercent;
	}	
    
    }

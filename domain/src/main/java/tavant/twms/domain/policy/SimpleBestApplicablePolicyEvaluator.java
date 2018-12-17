/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

/**
 * @author vineeth.varghese
 */
public class SimpleBestApplicablePolicyEvaluator implements BestApplicablePolicyEvaluator {

    private static Logger logger = LogManager.getLogger(SimpleBestApplicablePolicyEvaluator.class);

    private PaymentService paymentService;
    private final String TERMINATED="Terminated";

    private RuleExecutionTemplate ruleExecutionTemplate;

    public ApplicablePolicy findBestApplicablePolicy(ClaimedItem claimedItem,
    		List<? extends Policy> policies) throws PolicyException {
    	Policy bestPolicy = null;
    	boolean coverageExpired=true;
    	List<Policy> applicablePolicies = new ArrayList<Policy>();
    	ApplicablePolicy applicablePolicy = null;
    	Claim claim = claimedItem.getClaim();
    	sort(policies);             
    	for (Policy policy : policies) {
    		boolean notApplicable = !policy.isApplicable(claim, this.ruleExecutionTemplate);
    		Integer serviceHoursCovered = null;
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit())
    			.getServiceHoursCovered();
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<PolicyDefinition>().cast(policy).getCoverageTerms())
    			.getServiceHoursCovered();
    		}
    		if(policy.getWarrantyType().getType().equals(WarrantyType.POLICY.getType()) && claimedItem.getItemReference()!=null){
    			for(RegisteredPolicy registeredPolicy:claimedItem.getItemReference().getReferredInventoryItem().getWarranty().getPolicies()){
    				if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)&&registeredPolicy.getPolicyDefinition().
    						equals((new HibernateCast<RegisteredPolicy>().cast(policy).getPolicyDefinition()))){
    					coverageExpired = !policy.covers(claimedItem, registeredPolicy.getWarrantyPeriod(),  registeredPolicy.getLatestPolicyAudit().getServiceHoursCovered());
    				}
    			}
    		}
    		else{
        		 coverageExpired = !policy.covers(claimedItem, serviceHoursCovered);
    		}
    		if (notApplicable || coverageExpired) {
    			continue;
    		}
    		applicablePolicies.add(policy);
    	}
    	if(applicablePolicies!=null && !applicablePolicies.isEmpty()){
    		bestPolicy=applicablePolicies.get(0);
    	}
    	for (Policy policy : applicablePolicies) {
    		if(policy.getPolicyDefinition().getPriority().longValue()
    				<=bestPolicy.getPolicyDefinition().getPriority().longValue()){
    			bestPolicy = policy;
    		}
    	}
    	if(bestPolicy!=null){
			CalendarDuration duration = null;
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, bestPolicy)) {
    			applicablePolicy = new ApplicablePolicy(new HibernateCast<RegisteredPolicy>().cast(bestPolicy));
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, bestPolicy)) {
    			PolicyDefinition policy = new HibernateCast<PolicyDefinition>().cast(bestPolicy);
    			if(ClaimType.PARTS.getType().equals(claim.getType().getType()))
    			{
    			duration = policy.computeWarrantyPeriod(claim);
    			applicablePolicy = new ApplicablePolicy(policy, duration);
    			}
    			else
    			{
    	  			if(policy.getWarrantyType().getType().equals(WarrantyType.POLICY.getType()) && claimedItem.getItemReference()!=null){
            			for(RegisteredPolicy registeredPolicy:claimedItem.getItemReference().getReferredInventoryItem().getWarranty().getPolicies()){
            				if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)&&registeredPolicy.getPolicyDefinition().
            						equals((new HibernateCast<RegisteredPolicy>().cast(policy).getPolicyDefinition()))){
            					 duration = registeredPolicy.getWarrantyPeriod();
            				}
            			}
    	  			}
            	else{
            		duration = policy.computeWarrantyPeriod(claimedItem);
            		}
        		applicablePolicy = new ApplicablePolicy(policy, duration);	
    			}
    			
    		}
    		
    	}

    	return applicablePolicy;

    }

    public ApplicablePolicy findBestApplicablePolicy(Claim claim,
    		List<? extends Policy> policies) throws PolicyException {
    	if(policies == null){
    		return null;
    	}
    	Policy bestPolicy = null;
    	List<Policy> applicablePolicies = new ArrayList<Policy>();
    	ApplicablePolicy applicablePolicy = null;    	
    	sort(policies);             
    	for (Policy policy : policies) {
    		boolean notApplicable = !policy.isApplicable(claim, this.ruleExecutionTemplate);
    		Integer serviceHoursCovered = null;
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit())
    			.getServiceHoursCovered();
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<PolicyDefinition>().cast(policy).getCoverageTerms())
    			.getServiceHoursCovered();
    		}
    		boolean coverageExpired = !policy.covers(claim, serviceHoursCovered);
    		if (notApplicable || coverageExpired) {
    			continue;
    		}
    		applicablePolicies.add(policy);
    	}
    	if(applicablePolicies!=null && !applicablePolicies.isEmpty()){
    		bestPolicy=applicablePolicies.get(0);
    	}
    	for (Policy policy : applicablePolicies) {
    		if(policy.getPolicyDefinition().getPriority().longValue()
    				<=bestPolicy.getPolicyDefinition().getPriority().longValue()){
    			bestPolicy = policy;
    		}
    	}
    	if(bestPolicy!=null){
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, bestPolicy)) {
    			applicablePolicy = new ApplicablePolicy(new HibernateCast<RegisteredPolicy>().cast(bestPolicy));
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, bestPolicy)) {
    			PolicyDefinition policy = new HibernateCast<PolicyDefinition>().cast(bestPolicy);
    			CalendarDuration duration = policy.computeWarrantyPeriod(claim);
    			applicablePolicy = new ApplicablePolicy(policy, duration);
    		}
    		
    	}

    	return applicablePolicy;

    }
    public List<String> findApplicablePolicesCodes(Claim claim,
    		List<? extends Policy> policies) throws PolicyException {
    	List<String> policyCodes = new ArrayList<String>();
    	if(policies != null && !policies.isEmpty()){
    	sort(policies);
    	for (Policy policy : policies) {
    		boolean notApplicable = !policy.isApplicable(claim, this.ruleExecutionTemplate);
    		Integer serviceHoursCovered = null;
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit())
    			.getServiceHoursCovered();
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<PolicyDefinition>().cast(policy).getCoverageTerms())
    			.getServiceHoursCovered();
    		}
    		boolean coverageExpired = !policy.covers(claim, serviceHoursCovered);
    		if (notApplicable || coverageExpired) {
    			continue;
    		}
    		
    		if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)){
				if(!(new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit()).getStatus().equalsIgnoreCase(TERMINATED)){		    			
					policyCodes.add(policy.getCode());
				}
    	    }else{
    	    	policyCodes.add(policy.getCode());
    	    }
    	}
    	}
    	return policyCodes;
    }
    
    void sort(List<? extends Policy> policies) {
        // Fix for 132559 you need to order the policies in some way
    	if(policies != null && !policies.isEmpty()){
        Collections.sort(policies, new Comparator<Policy>() {
            public int compare(Policy o1, Policy o2) {
                if (o1.getId() != null && o2.getId() != null) {
                    return o1.getId().compareTo(o2.getId());
                } else {
                    return 0;
                }
            }
        });
    	}
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setRuleExecutionTemplate(RuleExecutionTemplate ruleExecutionTemplate) {
        this.ruleExecutionTemplate = ruleExecutionTemplate;
    }

	public List<String> findApplicablePolicyCodes(ClaimedItem claimedItem,
			List<? extends Policy> policies) {
		List<String> policyCodes = new ArrayList<String>();   
		boolean coverageExpired=true;
		if(policies != null && !policies.isEmpty()){
    	sort(policies); 
    	Claim claim = claimedItem.getClaim();
    	sort(policies);             
    	for (Policy policy : policies) {
    		boolean notApplicable = !policy.isApplicable(claim, this.ruleExecutionTemplate);
    		Integer serviceHoursCovered = null;
    		if (InstanceOfUtil.isInstanceOfClass( RegisteredPolicy.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit())
    			.getServiceHoursCovered();
    		} else if (InstanceOfUtil.isInstanceOfClass( PolicyDefinition.class, policy)) {
    			serviceHoursCovered = (new HibernateCast<PolicyDefinition>().cast(policy).getCoverageTerms())
    			.getServiceHoursCovered();
    		}
    		if(policy.getWarrantyType().getType().equals(WarrantyType.POLICY.getType())){
    			for(RegisteredPolicy registeredPolicy:claimedItem.getItemReference().getReferredInventoryItem().getWarranty().getPolicies()){
    				if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)&&registeredPolicy.getPolicyDefinition().
    						equals((new HibernateCast<RegisteredPolicy>().cast(policy).getPolicyDefinition()))){
    					coverageExpired = !policy.covers(claimedItem, registeredPolicy.getWarrantyPeriod(), registeredPolicy.getLatestPolicyAudit().getServiceHoursCovered());
    				}
    			}
    		}
    		else{
        		 coverageExpired = !policy.covers(claimedItem, serviceHoursCovered);
    		}
    		if (notApplicable || coverageExpired) {
    			continue;
    		}
    		if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)){
				if(!(new HibernateCast<RegisteredPolicy>().cast(policy).getLatestPolicyAudit()).getStatus().equalsIgnoreCase(TERMINATED)){		    			
					policyCodes.add(policy.getCode());
				}
    	    }else{
    	    	policyCodes.add(policy.getCode());
    	    }
    	}
		}
		return policyCodes;
	}

}

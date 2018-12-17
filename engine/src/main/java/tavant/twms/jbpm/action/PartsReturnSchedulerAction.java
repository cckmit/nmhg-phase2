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
package tavant.twms.jbpm.action;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.claim.payment.PaymentServiceImpl;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnServiceImpl;
import tavant.twms.domain.partreturn.PayOnlyWithInspectionEvaluator;
import tavant.twms.domain.partreturn.PayOnlyWithReturnEvaluator;
import tavant.twms.domain.partreturn.PayWithNoReturnEvaluator;
import tavant.twms.domain.partreturn.PaymentConditionEvaluator;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.worklist.WorkListItemService;

@SuppressWarnings("serial")
public class PartsReturnSchedulerAction extends Action {

	private BeanLocator beanLocator = new BeanLocator();
	
	
	String transition;
	

	public void execute(ExecutionContext executionContext) throws Exception {
		Claim claim = (Claim) executionContext.getVariable("claim");
		PartReturnService partReturnService = (PartReturnService) beanLocator.lookupBean("partReturnService");
		RuleAdministrationService ruleAdministrationService = (RuleAdministrationService) beanLocator.lookupBean("ruleAdministrationService");
		ClaimService claimService = (ClaimService) beanLocator.lookupBean("claimService");
		PaymentService paymentService = (PaymentService) beanLocator.lookupBean("paymentService");
		boolean canMakePmtDecision = claim.isNcr() ? true : partReturnService.canMakePaymentDecision(claim);
		if (canMakePmtDecision && claim.isPaymentRecalculationRequired()) {
			executionContext.setVariable("isPartCorrected","true");
			paymentService.calculatePaymentForClaim(claim,null);
			claim.setPaymentRecalculationRequired(false);
			if(claimService.acceptedClaimFailsRulesAfterPartOff(claim)){
				executionContext.getTaskInstance().end("sendForManualReview");
			}else if(ruleAdministrationService.processingRuleFailsAfterPartOff(claim)){
				executionContext.getTaskInstance().end("goToRuleExecutionDueToPartCorrection");
			}else{
				executionContext.getTaskInstance().end(getTransition());
			}
		} else if (canMakePmtDecision) {
			executionContext.getTaskInstance().end(getTransition());
		}

	}

	public String getTransition() {
		return transition;		
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

}

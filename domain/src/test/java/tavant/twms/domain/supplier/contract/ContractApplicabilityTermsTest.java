/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.supplier.contract;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.IsNotSet;
import tavant.twms.domain.rules.IsTrue;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.infra.DomainRepositoryTestCase;

/**
 * @author kannan.ekanath
 * 
 */
public class ContractApplicabilityTermsTest extends DomainRepositoryTestCase {

	RuleExecutionTemplate ruleExecutionTemplate;

	DomainPredicate travelDetailPredicate, smrPredicate;

	DomainRule travelDetailRule, smrRule;

	Claim claim;

	@Override
	protected void setUpInTxnRollbackOnFailure() throws Exception {
		super.setUpInTxnRollbackOnFailure();
		travelDetailPredicate = new DomainPredicate();
		String contractRulesContext = "ContractApplicabilityRules";
		travelDetailPredicate.setContext(contractRulesContext);
		travelDetailPredicate.setName("Travel details not set.");

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails",
				BusinessObjectModelFactory.CLAIM_RULES);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
		travelDetailPredicate.setPredicate(isNotSet);
		travelDetailRule = new DomainRule();
		travelDetailRule.setPredicate(travelDetailPredicate);

		smrPredicate = new DomainPredicate();
		smrPredicate.setContext(contractRulesContext);
		smrPredicate.setName("Claim Type is Machine");

		DomainSpecificVariable smrVariable = new DomainSpecificVariable(
				Claim.class, "claim.serviceManagerRequest",
				BusinessObjectModelFactory.CLAIM_RULES);
		IsTrue equals = new IsTrue(smrVariable);
		smrPredicate.setPredicate(equals);
		smrRule = new DomainRule();
		smrRule.setPredicate(smrPredicate);

		claim = new MachineClaim();
		ServiceInformation serviceInformation = new ServiceInformation();
		claim.setServiceInformation(serviceInformation);
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceInformation.setServiceDetail(serviceDetail);
	}

	public void testContractApplicableTermsSuccessAndFailure() {
		Contract contract = new Contract();
		contract.addApplicabilityTerm(travelDetailRule);

		assertTrue(contract.isRulesEvaluationSuccess(ruleExecutionTemplate,
				claim));

		// Now actually set the travel details then the rule execution should
		// fail
		claim.getServiceInformation().getServiceDetail().setTravelDetails(
				new TravelDetail());
		assertFalse(contract.isRulesEvaluationSuccess(ruleExecutionTemplate,
				claim));
	}

	public void testContractTermsMultipleRules() {
		Contract contract = new Contract();
		contract.addApplicabilityTerm(travelDetailRule);
		contract.addApplicabilityTerm(smrRule);
		claim.setServiceManagerRequest(true);

		// now all rules must pass
		assertTrue(contract.isRulesEvaluationSuccess(ruleExecutionTemplate,
				claim));

		// set one of them to fail
		claim.setServiceManagerRequest(false);
		assertFalse(contract.isRulesEvaluationSuccess(ruleExecutionTemplate,
				claim));
	}

	public void setRuleExecutionTemplate(
			RuleExecutionTemplate ruleExecutionTemplate) {
		this.ruleExecutionTemplate = ruleExecutionTemplate;
	}
}

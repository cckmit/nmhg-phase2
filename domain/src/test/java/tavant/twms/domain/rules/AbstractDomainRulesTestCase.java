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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: May 17, 2007
 * Time: 11:15:50 PM
 */

package tavant.twms.domain.rules;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.infra.DomainRepositoryTestCase;

import java.util.*;

public class AbstractDomainRulesTestCase extends DomainRepositoryTestCase {
	private static Logger logger = LogManager
			.getLogger(AbstractDomainRulesTestCase.class);

	private static final String CLAIM_RULES_CONTEXT = "ClaimRules";
	private static final DomainRuleAction MANUAL_REVIEW_ACTION = new DomainRuleAction(
			"Send for manual review", ClaimState.MANUAL_REVIEW.getState(),
			CLAIM_RULES_CONTEXT);
	private static final DomainRuleAction ON_HOLD_ACTION = new DomainRuleAction(
			"Put on hold", ClaimState.ON_HOLD.getState(), CLAIM_RULES_CONTEXT);
	private static final DomainRuleAction REJECT_ACTION = new DomainRuleAction(
			"Reject", ClaimState.REJECTED.getState(), CLAIM_RULES_CONTEXT);
	private RuleExecutionTemplate ruleExecutionTemplate;
	protected Claim claimFixture;
	private DomainRule domainRuleFixture;

	public void setRuleExecutionTemplate(
			RuleExecutionTemplate ruleExecutionTemplate) {
		this.ruleExecutionTemplate = ruleExecutionTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tavant.twms.infra.AbstractRepositoryTestCase#setUpInTxnRollbackOnFailure()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void setUpInTxnRollbackOnFailure() throws Exception {
		claimFixture = new MachineClaim();
		domainRuleFixture = new DomainRule();
		domainRuleFixture.setContext(CLAIM_RULES_CONTEXT);
	}

	protected DomainSpecificVariable getClaimFieldVariable(String fieldName) {
		return new DomainSpecificVariable(Claim.class, "claim." + fieldName,
				BusinessObjectModelFactory.CLAIM_RULES);
	}

	protected Constant getStringConstant(String value) {
		return getConstant(value, "string");
	}

	protected Constant getIntegerConstant(String value) {
		return getConstant(value, "integer");
	}

	protected Constant getDateConstant(String value) {
		return getConstant(value, "date");
	}

	protected Constant getMoneyConstant(String value) {
		return getConstant(value, "money");
	}

	private Constant getConstant(String value, String type) {
		return new Constant(value, type);
	}

	protected void prepareRuleForManualReview(String ruleName,
			Predicate predicate) {
		prepareRuleForTest(ruleName, predicate, MANUAL_REVIEW_ACTION);
	}

	protected void prepareRuleForTest(String ruleName, Predicate predicate,
			DomainRuleAction domainRuleAction) {
		domainRuleFixture.setDescription(ruleName);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName(ruleName);
		dra.setAction(domainRuleAction);
		domainRuleFixture.getRuleAudits().add(dra);


		DomainPredicate domainPredicate = new DomainPredicate(ruleName,
				predicate);
		domainRuleFixture.setPredicate(domainPredicate);
	}

	protected void executeAndAssertRuleFailed() {
		executeAndAssertRuleStatus(true);
	}

	protected void executeAndAssertRulePassed() {
		executeAndAssertRuleStatus(false);
	}

	protected void executeAndAssertRuleStatus(Boolean ruleShouldFail) {
		claimFixture.setState(ClaimState.SUBMITTED);
		String claimStateBeforeExecution = claimFixture.getState().getState();

		Callback executionResult = evaluateRuleCondition();

		assertEquals(ruleShouldFail, executionResult.getResult());

		Map<DomainRule, Map<Boolean, String>> fullResults = executionResult.getResults();
		Set<Map.Entry<DomainRule, Map<Boolean, String>>> entrySet = fullResults.entrySet();
		assertEquals(1, entrySet.size());

		Map.Entry<DomainRule, Map<Boolean, String>> entry = entrySet.iterator().next();
		Map<Boolean, String> clmFailedMsgMap = entry.getValue();
		Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator().next();
		assertEquals(ruleShouldFail, clmFailedCheck);
		assertEquals(domainRuleFixture, entry.getKey());

		if (ruleShouldFail) {
			validateClaimStateChange(executionResult, claimStateBeforeExecution);
		} else {
			assertEquals(0, executionResult.getActionResultHolder().size());
		}
	}

	private void validateClaimStateChange(Callback executionResult,
			String oldState) {
		boolean isAlreadyRejected = "rejected".equals(oldState);
		boolean isAlreadyOnHold = "on hold".equals(oldState);

		String stateToBeSet = domainRuleFixture.getAction().getState();

		Set<Map.Entry<String, Object>> actionResults = executionResult
				.getActionResultHolder().entrySet();

		if (("on hold".equals(stateToBeSet) && isAlreadyRejected)
				|| (("manual review".equals(stateToBeSet) && (isAlreadyRejected || isAlreadyOnHold)))) {

			assertEquals(0, actionResults.size());
			return;
		}

		assertEquals(1, actionResults.size());

		Map.Entry<String, Object> actionResult = actionResults.iterator()
				.next();
		assertEquals(DomainRuleAction.CLAIM_STATE_KEY, actionResult.getKey());

		assertEquals(domainRuleFixture.getAction().getState(), actionResult
				.getValue());
	}

	private Callback evaluateRuleCondition() {

		final Map<String, Object> txnContext = new HashMap<String, Object>();
		txnContext.put("claim", claimFixture);
		txnContext.put(DomainRuleAction.CLAIM_STATE_KEY, claimFixture
				.getState().getState());

		Callback callback = new Callback(domainRuleFixture, txnContext);
		ruleExecutionTemplate.executeRules(callback);
		return callback;
	}

	private static class Callback extends RuleExecutionCallback {
		private final Map<String, Object> context;

		private Map<DomainRule, Map<Boolean, String>> results;

		private final DomainRule rule;
		private final HashMap<String, Object> actionResultHolder = new HashMap<String, Object>();

		public Callback(DomainRule rule, Map<String, Object> context) {
			this.context = context;
			this.rule = rule;
		}

		public Map<String, Object> getActionResultHolder() {
			return actionResultHolder;
		}

		public List<DomainRule> getRulesForExecution() {
			List<DomainRule> rules = new ArrayList<DomainRule>();
			rules.add(rule);
			return rules;
		}

		public Map<String, Object> getTransactionContext() {
			return context;
		}

		public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
			this.results = results;
		}

		/**
		 * @return the results
		 */
		public Map<DomainRule, Map<Boolean, String>> getResults() {
			return results;
		}

		/**
		 * @return the result
		 */
		public Boolean getResult() {
			Map<Boolean, String> clmFailedMsgMap = results.get(rule);
			return clmFailedMsgMap.keySet().iterator().next();
		}

	}
}

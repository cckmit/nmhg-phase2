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

import java.util.*;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleExecutionCallback;

/**
 * @author kannan.ekanath
 * 
 */
public class ContractRuleExecutionCallBack extends RuleExecutionCallback {

	private final Contract contract;

	private final Claim claim;

	private Map<DomainRule, Map<Boolean, String>> results = new HashMap<DomainRule, Map<Boolean, String>>();

	public ContractRuleExecutionCallBack(Claim claim, Contract contract) {
		this.claim = claim;
		this.contract = contract;
	}

	public Map<String, Object> getActionResultHolder() {
		return new HashMap<String, Object>();
	}

	public List<DomainRule> getRulesForExecution() {
		return contract.getApplicabilityTerms();
	}

	public Map<String, Object> getTransactionContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("claim", claim);
		context.put("contract", contract);
		return context;
	}

	public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
		this.results = results;
	}

	public boolean haveAllRulesPassed() {
		if (!results.isEmpty()) {
			for (Map.Entry<DomainRule, Map<Boolean, String>> entry : results.entrySet()) {
				Map<Boolean, String> clmFailedMsgMap = entry.getValue();
				Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator()
						.next();
				if (!clmFailedCheck) {
					return false;
				}
			}
		}
		return true;
	}
}

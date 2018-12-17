package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.DomainRepositoryTestCase;

public class RuleExecutionTemplateTest extends DomainRepositoryTestCase {
	private RuleExecutionTemplate ruleExecutionTemplate;

	private OrgService orgService;

	@Required
	public void setRuleExecutionTemplate(
			RuleExecutionTemplate ruleExecutionTemplate) {
		this.ruleExecutionTemplate = ruleExecutionTemplate;
	}

	@Required
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void testExecuteRules() {
		final String ruleName = "Check if Service Provider is in Watch list";

		final Set<DomainRule> rules = new HashSet<DomainRule>(1);

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class, "claim.forDealer",
				BusinessObjectModelFactory.CLAIM_RULES);
		// A dealer is in watch list.
		IsAWatchedDealership isAWatchedDealership = new IsAWatchedDealership(
				domainSpecificVariable);
		final DomainRule domainRule = new DomainRule();
		domainRule.setRuleNumber(Integer.MAX_VALUE);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName(ruleName);		
		domainRule.getRuleAudits().add(dra);
		
				
		DomainPredicate domainPredicate = new DomainPredicate(
				"Service Provider is in Watch list", isAWatchedDealership);
		domainRule.setPredicate(domainPredicate);
		rules.add(domainRule);

		ruleExecutionTemplate.executeRules(new RuleExecutionCallback() {

			public Set<DomainRule> getRulesForExecution() {
				return rules;
			}

			public Map<String, Object> getTransactionContext() {
				Map<String, Object> context = new HashMap<String, Object>(1);

				MachineClaim claim = new MachineClaim();
				claim.setForDealerShip(orgService.findDealerByNumber("FER-302"));
				context.put("claim", claim);

				return context;

			}

			public Map<String, Object> getActionResultHolder() {
				return new HashMap<String, Object>(2);
			}

			public void setRuleEvaluationResult(
					Map<DomainRule, Map<Boolean, String>> results) {
				Map<Boolean, String> clmFailedMsgMap = results.get(domainRule);
				Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator()
						.next();
				assertTrue(clmFailedCheck);
			}
		});
	}
}

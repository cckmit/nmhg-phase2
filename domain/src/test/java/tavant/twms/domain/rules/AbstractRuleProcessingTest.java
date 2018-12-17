package tavant.twms.domain.rules;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_RULES;
import static tavant.twms.domain.claim.ClaimState.SUBMITTED;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RuleFailure;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.DomainRepositoryTestCase;

public abstract class AbstractRuleProcessingTest extends
		DomainRepositoryTestCase {

	private RuleAdministrationService ruleAdministrationService;

	protected OrgService orgService;

	protected String context = CLAIM_RULES;

	protected static final Constant CONSTANT_TRUE = new Constant("true",
			"boolean");

	protected static final Constant CONSTANT_FALSE = new Constant("false",
			"boolean");

	public AbstractRuleProcessingTest() {
		super();
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	protected void executeAndAssertRulePassed(Claim aClaim) {
		String finalState = this.ruleAdministrationService
				.executeClaimAutoProcessingRules(aClaim);
		assertEquals(0, aClaim.getRuleFailures().size());
		// If there are no rule failures, the finalState may not be null as
		// there is a check
		// for supplier recovery contracts. This forces a manual review in case
		// of none or multiple contracts.
		// This would be further handled as a part of rule failure and would
		// then be changed accordingly.
		// Modifying the test case till then.
		// assertNull(finalState);
		assertNull(finalState);
		// assertEquals("manual review", finalState);
		// Changes end.
		assertEquals(ClaimState.SUBMITTED, aClaim.getState());

	}

	protected void executeAndAssertRuleFailed(String ruleFailureMessage,
			Claim aClaim, DomainRuleAction action) {
		String finalState = this.ruleAdministrationService
				.executeClaimAutoProcessingRules(aClaim);
		assertEquals(action.getState(), finalState);
		assertEquals(ClaimState.SUBMITTED, aClaim.getState());

		SortedSet<RuleFailure> ruleFailures = aClaim.getRuleFailures();
		assertEquals(1, ruleFailures.size());

		RuleFailure ruleFailure = ruleFailures.iterator().next();
		assertEquals(this.context, ruleFailure.getFailedRuleSet());

/*		Set<String> actualFailedRules = ruleFailure.getFailedRules();
		assertEquals(1, actualFailedRules.size());

		assertTrue(actualFailedRules.iterator().next().indexOf(
				ruleFailureMessage) != -1);
				*/
	}

	protected DomainRuleAction getManualReviewAction() {
		DomainRuleAction action = new DomainRuleAction();
		action.setState("manual review");
		action.setName("Send Claim For Manual Review");
		action.setContext("ClaimRules");
		return action;
	}

	protected DomainRule createBaseRule(DomainPredicate predicate,
			DomainRuleAction action) throws RuleAdministrationException {
		DomainRule rule = new DomainRule();
		rule.setRuleNumber(Integer.MAX_VALUE);
//		rule.setName(predicate.getName());
//		rule.setFailureMessage(predicate.getName());
		rule.setPredicate(predicate);
		rule.setContext(this.context);
//		rule.setAction(action);

		this.ruleAdministrationService.save(rule);

		return rule;
	}

	protected Claim createSubmittedMachineClaimWithMaxId() {
		Claim aClaim = new MachineClaim();
		aClaim.setState(ClaimState.SUBMITTED);
		aClaim.setId(Long.MAX_VALUE);
		aClaim.setUpdated(false);
		Item causalPart = new Item();
		causalPart.setId(1L);
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setCausalPart(causalPart);
		serviceInformation.setId(1L);
		aClaim.setServiceInformation(serviceInformation);

		causalPart.setNumber("PRTVLV1-NW");
		flushAndClear();
		return aClaim;
	}

	protected void resetClaim(Claim claim) {
		claim.setState(SUBMITTED);
		claim.getRuleFailures().clear();
	}

	protected DomainSpecificVariable getClaimDsv(String fieldName) {
		return new DomainSpecificVariable(Claim.class, fieldName, this.context);
	}

	protected Constant constantString(String value) {
		return new Constant(value, "string");
	}

	protected Constant constantNumber(int value) {
		return new Constant(String.valueOf(value), "integer");
	}

	protected void addBlankOemPartsToClaim(Claim claim, int numParts) {
		ServiceDetail serviceDetail = new ServiceDetail();
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceDetail(serviceDetail);
		claim.setServiceInformation(serviceInformation);

		for (int i = 0; i < numParts; i++) {
			ItemReference itemReference = new ItemReference(new Item());
			serviceDetail.addOEMPartReplaced(new OEMPartReplaced(itemReference,
					1));
		}
	}

	protected OEMPartReplaced getNthOemPartForClaim(Claim claim, int index) {
		ServiceDetail serviceDetail = claim.getServiceInformation()
				.getServiceDetail();
		List<OEMPartReplaced> oemPartsReplaced = serviceDetail
				.getOemPartsReplaced();

		return oemPartsReplaced.get(index);
	}

	protected Item getItemForOemPart(OEMPartReplaced oemPart) {
		return oemPart.getItemReference().getReferredItem();
	}

	protected ForEachOf createForEachPredicateOnOemPartItem(Predicate predicate) {
		return (ForEachOf) createPredicateOnOemPartItem(true, predicate);
	}

	protected ForAnyOf createForAnyPredicateOnOemPartItem(Predicate predicate) {
		return (ForAnyOf) createPredicateOnOemPartItem(false, predicate);
	}

	protected AbstractCollectionUnaryPredicate createPredicateOnOemPartItem(
			boolean isEach, Predicate predicate) {

		All corePredicate = new All();
		corePredicate.setForOneToOne(true);
		DomainSpecificVariable oemPartReplacedDsv = new DomainSpecificVariable(
				OEMPartReplaced.class, "itemReference.referredItem", this.context);
		corePredicate.setOneToOneVariable(oemPartReplacedDsv);
		corePredicate.addPredicate(predicate);

		All wrappingPredicate = new All();
		wrappingPredicate.addPredicate(corePredicate);

		DomainSpecificVariable oemPartsReplacedDsv = getClaimDsv("claim.serviceInformation.serviceDetail.oemPartsReplaced");

		AbstractCollectionUnaryPredicate forAnyOrEachItemOf = (isEach) ? new ForEachOf()
				: new ForAnyOf();

		forAnyOrEachItemOf.setCollectionValuedVariable(oemPartsReplacedDsv);
		forAnyOrEachItemOf.setConditionToBeSatisfied(wrappingPredicate);

		return forAnyOrEachItemOf;
	}
}
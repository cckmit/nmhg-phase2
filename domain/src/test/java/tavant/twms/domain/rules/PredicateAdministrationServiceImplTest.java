package tavant.twms.domain.rules;

import java.util.List;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class PredicateAdministrationServiceImplTest extends
		DomainRepositoryTestCase {

	private PredicateAdministrationService predicateAdministrationService;
	private DomainPredicateRepository domainPredicateRepository;
	private DomainRuleRepository domainRuleRepository;
	DomainRule domainRule;
	DomainPredicate domainPredicate;

	/**
	 * @param domainPredicateRepository
	 *            the domainPredicateRepository to set
	 */
	public void setDomainPredicateRepository(
			DomainPredicateRepository domainPredicateRepository) {
		this.domainPredicateRepository = domainPredicateRepository;
	}

	public void setDomainRuleRepository(
			DomainRuleRepository domainRuleRepository) {
		this.domainRuleRepository = domainRuleRepository;
	}

	/**
	 * @param predicateAdministrationService
	 *            the predicateAdministrationService to set
	 */
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	@Override
	protected void setUpInTxnRollbackOnFailure() throws Exception {
		super.setUpInTxnRollbackOnFailure();
		domainPredicate = new DomainPredicate();
		domainPredicate.setContext("ClaimProcessingRules");
		domainPredicate.setName("Travel details not set.");

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails",
				BusinessObjectModelFactory.CLAIM_RULES);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
		domainPredicate.setPredicate(isNotSet);

		domainRule = new DomainRule();
		domainRule.setContext(domainPredicate.getContext());
		domainRule.setRuleNumber(Integer.MAX_VALUE);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName(domainPredicate.getName());		
		domainRule.getRuleAudits().add(dra);
		domainRule.setPredicate(domainPredicate);

		domainRuleRepository.save(domainRule);

		assertNotNull(domainRule.getId());
		assertNotNull(domainPredicate.getId());

		flush();
	}

	public void testFindAll() {
		assertEquals(2, predicateAdministrationService.findAll().size());
	}

	public void testFindByName() throws Exception {
		List<DomainPredicate> inventoryRules = predicateAdministrationService
				.findPredicatesByName("details", "ClaimProcessingRules");
		assertEquals(1, inventoryRules.size());
	}

	public void testFindAllInContext() throws Exception {
		PageResult<DomainPredicate> findAll = predicateAdministrationService
				.findAllRulesInContext("ClaimProcessingRules",
						new PageSpecification());
		assertFalse(findAll.getResult().isEmpty());
	}

	public void testUpdate() throws Exception {
		DomainPredicate reloadedDomainPredicate = domainPredicateRepository
				.findById(domainPredicate.getId());
		reloadedDomainPredicate.setName("Travel details not set....");
		predicateAdministrationService.update(reloadedDomainPredicate);
		flushAndClear();
	}

	public void testFindRulesUsingPredicate() {
		List<DomainRule> rulesUsingPredicate = predicateAdministrationService
				.findRulesUsingPredicate(domainPredicate);

		assertEquals(1, rulesUsingPredicate.size());

		DomainRule rule = rulesUsingPredicate.get(0);

		assertNotNull(rule);
		assertEquals(domainRule, rule);
	}

	public void testFindClashingPredicatesForDuplicatePredicate() {
		DomainPredicate testPredicate = new DomainPredicate();
		testPredicate.setName(domainPredicate.getName());
		testPredicate.setContext(domainPredicate.getContext());
		testPredicate.setPredicate(domainPredicate.getPredicate());

		List<DomainPredicate> clashingPredicates = predicateAdministrationService
				.findClashingPredicates(testPredicate);
		assertEquals(1, clashingPredicates.size());

		DomainPredicate clashingPredicate = clashingPredicates.get(0);
		assertEquals(domainPredicate, clashingPredicate);
	}

	public void testFindClashingPredicatesForAllButNameSame() {
		DomainPredicate testPredicate = new DomainPredicate();

		testPredicate.setName("Foo");
		testPredicate.setContext(domainPredicate.getContext());
		testPredicate.setPredicate(domainPredicate.getPredicate());

		assertEquals(1, predicateAdministrationService.findClashingPredicates(
				testPredicate).size());
	}

	public void testIsDuplicatePredicateForAllButContextSame() {
		DomainPredicate testPredicate = new DomainPredicate();

		testPredicate.setName(domainPredicate.getName());
		testPredicate.setContext("Foo");
		testPredicate.setPredicate(domainPredicate.getPredicate());

		assertEquals(1, predicateAdministrationService.findClashingPredicates(
				testPredicate).size());
	}

	public void testIsDuplicatePredicateForAllButCorePredicateSame() {
		DomainPredicate testPredicate = new DomainPredicate();

		testPredicate.setName(domainPredicate.getName());
		testPredicate.setContext(domainPredicate.getContext());

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class, "claim.claimedItems.hoursInService",
				BusinessObjectModelFactory.CLAIM_RULES);
		Predicate differentPredicate = new Equals(domainSpecificVariable,
				new Constant("1", "integer"));

		testPredicate.setPredicate(differentPredicate);

		assertEquals(1, predicateAdministrationService.findClashingPredicates(
				testPredicate).size());
	}
}

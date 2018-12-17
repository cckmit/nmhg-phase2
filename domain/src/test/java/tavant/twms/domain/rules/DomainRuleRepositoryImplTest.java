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
 * Date: Mar 2, 2007
 * Time: 2:30:56 PM
 */

package tavant.twms.domain.rules;

import java.util.List;

import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class DomainRuleRepositoryImplTest extends DomainRepositoryTestCase {
	private DomainRuleRepository domainRuleRepository;
	private DomainPredicateRepository domainPredicateRepository;

	/**
	 * @param domainRuleRepository
	 *            the domainRuleRepository to set
	 */
	public void setDomainRuleRepository(
			DomainRuleRepository domainRuleRepository) {
		this.domainRuleRepository = domainRuleRepository;
	}

	/**
	 * @param domainPredicateRepository
	 *            the domainPredicateRepository to set
	 */
	public void setDomainPredicateRepository(
			DomainPredicateRepository domainPredicateRepository) {
		this.domainPredicateRepository = domainPredicateRepository;
	}

	public void testFindByNameForNonExistentRuleName() throws Exception {
		List<DomainRule> list = domainRuleRepository.findByNameInContext(
				"Foo Bar", "ClaimRules");
		assertEquals(0, list.size());
	}

	public void testFindByNameForExistingRuleName() throws Exception {

		DomainPredicate domainPredicate = new DomainPredicate();
		String claimProcessingRules = "ClaimProcessingRules";
		domainPredicate.setContext(claimProcessingRules);
		domainPredicate.setName("Travel details not set.");

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails",
				claimProcessingRules);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
		domainPredicate.setPredicate(isNotSet);

		domainPredicateRepository.save(domainPredicate);
		assertNotNull(domainPredicate.getId());

		List<DomainPredicate> findByNameInContext = domainPredicateRepository
				.findByNameInContext("details", claimProcessingRules);
		assertEquals(1, findByNameInContext.size());
		assertEquals("Travel details not set.", findByNameInContext.get(0)
				.getName());

		DomainRule domainRule = new DomainRule();
		domainRule.setRuleNumber(Integer.MAX_VALUE);
		domainRule.setContext(claimProcessingRules);
		domainRule.setPredicate(domainPredicate);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName("Test Rule");
		dra.setFailureMessage("Test Rule Failed Buddy!!!");
		domainRule.getRuleAudits().add(dra);
		domainRuleRepository.save(domainRule);
//		flush();
		List<DomainRule> list = domainRuleRepository.findByNameInContext(
				"Test Rule", claimProcessingRules);
		for (DomainRule rule : list) {
			assertNotNull(rule.getFailureMessage());
		}
		assertEquals(1, list.size());
	}
	
	public void testFindAllInContext_BUFilter()
	{
		DomainPredicate domainPredicate = new DomainPredicate();
		String claimProcessingRules = "ClaimProcessingRules";
		domainPredicate.setContext(claimProcessingRules);
		domainPredicate.setName("Travel details not set.");

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails",
				claimProcessingRules);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
		domainPredicate.setPredicate(isNotSet);

		domainPredicateRepository.save(domainPredicate);
		assertNotNull(domainPredicate.getId());

		List<DomainPredicate> findByNameInContext = domainPredicateRepository
				.findByNameInContext("details", claimProcessingRules);
		assertEquals(1, findByNameInContext.size());
		assertEquals("Travel details not set.", findByNameInContext.get(0)
				.getName());

		DomainRule domainRule = new DomainRule();
		domainRule.setRuleNumber(Integer.MAX_VALUE);
		domainRule.setContext(claimProcessingRules);
		domainRule.setPredicate(domainPredicate);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName("Test Rule");
		dra.setFailureMessage("Test Message");
		domainRule.getRuleAudits().add(dra);
		domainRuleRepository.save(domainRule);
		flush();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(0);
		pageSpecification.setPageSize(10);
		PageResult<DomainRule> domainRuleList=domainRuleRepository.findAllInContext("ClaimProcessingRules", pageSpecification);
		assertEquals(1, domainRuleList.getResult().size());
	}
	
	public void testCRUD_I18n()
	{
		DomainPredicate domainPredicate = new DomainPredicate();
		String claimProcessingRules = "ClaimProcessingRules";
		domainPredicate.setContext(claimProcessingRules);
		domainPredicate.setName("Travel details not set.");

		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class,
				"claim.serviceInformation.serviceDetail.travelDetails",
				claimProcessingRules);
		IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
		domainPredicate.setPredicate(isNotSet);

		domainPredicateRepository.save(domainPredicate);
		assertNotNull(domainPredicate.getId());
		DomainRule domainRule = new DomainRule();
		domainRule.setRuleNumber(Integer.MAX_VALUE);
		domainRule.setContext(claimProcessingRules);
		domainRule.setPredicate(domainPredicate);
		DomainRuleAudit dra = new DomainRuleAudit();
		dra.setName("Test Rule");
		dra.setFailureMessage("Test Message");
		domainRule.getRuleAudits().add(dra);
		domainRuleRepository.save(domainRule);
		flush();
		DomainRule entity  = domainRuleRepository.findById(domainRule.getId());
		assertNotNull(entity.getFailureMessage());
		assertEquals(domainRule.getFailureMessage(), entity.getFailureMessage());
		
	}
}

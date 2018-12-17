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
 * Date: Mar 7, 2007
 * Time: 1:11:37 PM
 */

package tavant.twms.domain.rules;

import java.util.Currency;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.orgmodel.Dealership;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("unused")
public class RuleAdministrationServiceImplTest extends
		AbstractRuleProcessingTest {

	public void disable_testExecuteClaimAutoProcessingRulesForBelongsTo()
			throws Exception {

		DomainSpecificVariable dealerDsv = getClaimDsv("claim.forDealer");
		Constant frequentClaimantsList = constantString("Frequent Claimants");
		BelongsTo dealerBelongsTo = new BelongsTo(dealerDsv,
				frequentClaimantsList);
		dealerBelongsTo.setMethodName("isBusinessObjectInNamedCategory");

		DomainPredicate predicate = new DomainPredicate("Frequent Claimant",
				dealerBelongsTo);
		predicate.setContext("ClaimRules");

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Dealership dealer = orgService.findDealerById(7L);
		Claim aClaim = createSubmittedMachineClaimWithMaxId();
		aClaim.setForDealerShip(dealer);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		dealer = orgService.findDealerById(25L);
		aClaim.setForDealerShip(dealer);

		executeAndAssertRulePassed(aClaim);
	}

	public void testExecuteClaimAutoProcessingRulesForDuplicacy()
			throws RuleAdministrationException {

		DomainSpecificVariable causalPartNumber = getClaimDsv("claim.serviceInformation.causalPart.number");
		IsSameAs sameCausalPartNumber = new IsSameAs(causalPartNumber);
		All corePredicate = new All();
		corePredicate.setQueryPredicate(true);
		corePredicate.addPredicate(sameCausalPartNumber);

		DomainPredicate predicate = new DomainPredicate(
				"Same Causal Part Number", corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

//		Item causalPart = new Item();
//
//		ServiceInformation serviceInformation = new ServiceInformation();
//		serviceInformation.setCausalPart(causalPart);
//
//		aClaim.setServiceInformation(serviceInformation);
//
//		causalPart.setNumber("PRTVLV1-NW");

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		aClaim.getServiceInformation().getCausalPart().setNumber(String.valueOf(Integer.MAX_VALUE));
//		causalPart.setNumber(String.valueOf(Integer.MAX_VALUE));

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForDateEquals()
			throws RuleAdministrationException {
		DomainSpecificVariable dsv = getClaimDsv("claim.failureDate");
		Constant failureDate = new Constant("1/4/2007", "date");
		Equals failureDateEquals = new Equals(dsv, failureDate);

		All corePredicate = new All();
		corePredicate.addPredicate(failureDateEquals);

		DomainPredicate predicate = new DomainPredicate(
				"Failure Date equals 1/4/2007", corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();
		aClaim.setFailureDate(CalendarDate.date(2007, 1, 4));

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		aClaim.setFailureDate(CalendarDate.date(2010, 1, 1));

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForPreferredCurrencyEquals()
			throws RuleAdministrationException {
		DomainSpecificVariable dsv = getClaimDsv("claim.forDealer.preferredCurrency.currencyCode");
		Constant preferredCurrency = constantString("USD");
		Equals preferredCurrencyEquals = new Equals(dsv, preferredCurrency);

		All corePredicate = new All();
		corePredicate.addPredicate(preferredCurrencyEquals);

		DomainPredicate predicate = new DomainPredicate(
				"Dealer's Preferred Currency equals USD", corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		Dealership dealer = new Dealership();
		dealer.setPreferredCurrency(Currency.getInstance("USD"));

		aClaim.setForDealerShip(dealer);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		dealer.setPreferredCurrency(Currency.getInstance("EUR"));

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForItemConditionEquals()
			throws RuleAdministrationException {
		DomainSpecificVariable dsv = getClaimDsv("claim.forDealer.preferredCurrency.currencyCode");
		Constant preferredCurrency = constantString("USD");
		Equals preferredCurrencyEquals = new Equals(dsv, preferredCurrency);

		All corePredicate = new All();
		corePredicate.addPredicate(preferredCurrencyEquals);

		DomainPredicate predicate = new DomainPredicate(
				"Dealer's Preferred Currency equals USD", corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		Dealership dealer = new Dealership();
		dealer.setPreferredCurrency(Currency.getInstance("USD"));

		aClaim.setForDealerShip(dealer);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		dealer.setPreferredCurrency(Currency.getInstance("EUR"));

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForCausalPartItemDescrAndCausedByContains()
			throws RuleAdministrationException {
		DomainSpecificVariable causedByDsv = getClaimDsv("claim.serviceInformation.causedBy");
		Constant causedBy = constantString("CRIMP");
		Contains causedByContains = new Contains(causedByDsv, causedBy);

		DomainSpecificVariable itemDescriptionDsv = getClaimDsv("claim.serviceInformation.causalPart.description");
		Constant itemDescription = constantString("Valve");
		Contains itemDescriptionContains = new Contains(itemDescriptionDsv,
				itemDescription);

		All corePredicate = new All();
		corePredicate.addPredicate(itemDescriptionContains);
		corePredicate.addPredicate(causedByContains);

		DomainPredicate predicate = new DomainPredicate(
				"Causal Part's Description contains Valve and Caused By contains CRIMP",
				corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setId(1L);
		Item causalPart = new Item();
		causalPart.setId(1L);
		causalPart.setDescription("Some Valve thing");
		serviceInformation.setCausalPart(causalPart);

		serviceInformation.setCausedBy("Some CRIMP thing");

		aClaim.setServiceInformation(serviceInformation);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		// Check case insensitivity.
		serviceInformation.setCausedBy("Some cRiMp thing");
		causalPart.setDescription("Some VAlvE thing");

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		// Check non matching.
		serviceInformation.setCausedBy("foo bar");
		causalPart.setDescription("fee fum");

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForSmrRequest()
			throws RuleAdministrationException {
		DomainSpecificVariable dsv = getClaimDsv("claim.serviceManagerRequest");
		Equals serviceManagerRequestEquals = new Equals(dsv, CONSTANT_TRUE);

		All corePredicate = new All();
		corePredicate.addPredicate(serviceManagerRequestEquals);

		DomainPredicate predicate = new DomainPredicate(
				"Service Manager Request is true", corePredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		aClaim.setServiceManagerRequest(true);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		aClaim.setServiceManagerRequest(false);

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForItemNumberContains()
			throws RuleAdministrationException {
		DomainSpecificVariable dsv = new DomainSpecificVariable(
				OEMPartReplaced.class, "itemReference.referredItem.number",
				context);
		Constant itemNumber = constantString("mc-cougar-50-hz-1");
		Contains itemNumberContains = new Contains(dsv, itemNumber);

		ForAnyOf forAnyItemOf = createForAnyPredicateOnOemPartItem(itemNumberContains);

		All rootPredicate = new All();
		rootPredicate.addPredicate(forAnyItemOf);

		DomainPredicate predicate = new DomainPredicate(
				"Any replaced OEM Part's item number contains mc-cougar-50-hz-1",
				rootPredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		addBlankOemPartsToClaim(aClaim, 1);
		OEMPartReplaced oemPart = getNthOemPartForClaim(aClaim, 0);
		Item item = getItemForOemPart(oemPart);
		item.setNumber("mc-cougar-50-hz-1");

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		item.setNumber("mc-cougar-foo-bar");

		executeAndAssertRulePassed(aClaim);
	}

	/**
	 * For bug #130259
	 * 
	 * @throws RuleAdministrationException
	 */
	public void testExecuteClaimAutoProcessingRulesForNumOemPartsEquals()
			throws RuleAdministrationException {
		DomainSpecificVariable numOemPartsDsv = getClaimDsv("claim.serviceInformation.serviceDetail.oemPartsReplaced.size");

		Equals numOemPartsReplacedEquals = new Equals(numOemPartsDsv,
				constantNumber(2));

		All rootPredicate = new All();
		rootPredicate.addPredicate(numOemPartsReplacedEquals);

		DomainPredicate predicate = new DomainPredicate(
				"Number of replaced OEM Parts equals 2", rootPredicate);
		predicate.setContext(context);

		DomainRuleAction action = getManualReviewAction();

		createBaseRule(predicate, action);

		Claim aClaim = createSubmittedMachineClaimWithMaxId();

		addBlankOemPartsToClaim(aClaim, 2);

		executeAndAssertRuleFailed(predicate.getName(), aClaim, action);

		resetClaim(aClaim);

		addBlankOemPartsToClaim(aClaim, 3);

		executeAndAssertRulePassed(aClaim);

		resetClaim(aClaim);

		executeAndAssertRulePassed(aClaim);
	}

}

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
package tavant.twms.domain.claim.payment.definition;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import junit.framework.TestCase;
import tavant.twms.domain.category.ApplicablePolicyCategory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.DomainTestHelper;

import java.util.List;

/**
 * @author kannan.ekanath
 *
 */
public class PolicyCriteriaTest extends TestCase {

	Label label;
	
	PolicyDefinition stdPolicy, nonStdPolicy;
	
	PolicyCriteria specificPolicyCriteria;
	
	@Override
	protected void setUp() throws Exception {
		label = new Label();
		label.setName("Label1");
		stdPolicy = new PolicyDefinition();
		nonStdPolicy = new PolicyDefinition();
		stdPolicy.getLabels().add(label);
		specificPolicyCriteria = new PolicyCriteria();
		specificPolicyCriteria.setClaimType("Machine");
		specificPolicyCriteria.setLabel(label);
	}

	public void testSuitabilityScores() {
		//policy matches but claim type does not match
		Claim claim = new PartsClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);

        claimedItem.setApplicablePolicy(stdPolicy);
		assertEquals(-1, specificPolicyCriteria.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
		
		//all criteria matches
		claim = new MachineClaim();
        claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);
        
        claimedItem.setApplicablePolicy(stdPolicy);
		assertEquals(12, specificPolicyCriteria.getSuitabilityScore(claim,claimedItem.getApplicablePolicy()));
		
		//no criteria matches
		claim = new PartsClaim();
		claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);

        claimedItem.setApplicablePolicy(nonStdPolicy);
		assertEquals(-1, specificPolicyCriteria.getSuitabilityScore(claim,claimedItem.getApplicablePolicy()));
	}
	
	public void testGenericCriteria() {
		PolicyCriteria policyCriteria = new PolicyCriteria();
		//dont set anything here. Still the score must be 2
		Claim claim = new PartsClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);
        claimedItem.setApplicablePolicy(stdPolicy);
		assertEquals(2, policyCriteria.getSuitabilityScore(claim,claimedItem.getApplicablePolicy()));
	}

}

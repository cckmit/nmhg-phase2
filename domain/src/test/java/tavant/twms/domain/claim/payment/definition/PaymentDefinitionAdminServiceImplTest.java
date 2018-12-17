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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyAudit;

import java.util.Arrays;
import java.util.List;

/**
 * @author kannan.ekanath
 *
 */
public class PaymentDefinitionAdminServiceImplTest extends MockObjectTestCase {

    public PaymentDefinition definition1, definition2, definition3;

    PolicyDefinition stdPolicy;

    Label shortTerm = new Label("ShortTerm");

    @Override
    protected void setUp() throws Exception {
        this.definition1 = new PaymentDefinition();
        this.definition2 = new PaymentDefinition();
        this.definition3 = new PaymentDefinition();
        PolicyCriteria stdPolicyCriteria, nonStdPolicyCriteria, fittingCriteria;
        Label label = new Label();
        label.setName("Label1");
        this.stdPolicy = new PolicyDefinition();
        this.stdPolicy.getLabels().add(label);
        stdPolicyCriteria = new PolicyCriteria();
        stdPolicyCriteria.setClaimType("Machine");
        stdPolicyCriteria.setLabel(label);

        nonStdPolicyCriteria = new PolicyCriteria();
        nonStdPolicyCriteria.setClaimType(""); // covers all types
        nonStdPolicyCriteria.setLabel(null); // covers all categories

        fittingCriteria = new PolicyCriteria();
        fittingCriteria.setClaimType("");
        fittingCriteria.setLabel(this.shortTerm);
        this.definition1.setCriteria(stdPolicyCriteria);
        this.definition2.setCriteria(nonStdPolicyCriteria);
        this.definition3.setCriteria(fittingCriteria);

    }

    public void testFindBestPaymentDefinition() {
        PaymentDefinitionAdminServiceImpl service = new PaymentDefinitionAdminServiceImpl();
        Claim claim = new PartsClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);

        service.setPaymentDefinitionRepository(getPaymentDefinitionRepositoryMock());
        // Score = -1 since claim type mismatch
        assertEquals(-1, this.definition1.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        // Score = 2 (1 for all claims 1 for all policies)
        assertEquals(2, this.definition2.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        // Score = -1 since policy label mismatch
        assertEquals(-1, this.definition3.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        claim = new MachineClaim();
        RegisteredPolicy policy = new RegisteredPolicy();
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinition.getLabels().add(this.shortTerm);
        policy.setPolicyDefinition(policyDefinition);
        policy.getPolicyAudits().add(new RegisteredPolicyAudit());
        claimedItem.setApplicablePolicy(policy);
        // Score = 9 (both label and covers all claim types)
        assertEquals(9, this.definition3.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        assertEquals(2, this.definition2.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        assertEquals(-1, this.definition1.getSuitabilityScore(claim, claimedItem.getApplicablePolicy()));
        assertEquals(this.definition3, service.findBestPaymentDefinition(claim, claimedItem.getApplicablePolicy()));
    }

    private PaymentDefinitionRepository getPaymentDefinitionRepositoryMock() {
        Mock repository = mock(PaymentDefinitionRepository.class);
        List<PaymentDefinition> definitions = Arrays.asList(this.definition1, this.definition2, this.definition3);
        repository.expects(once()).method("findAll").will(returnValue(definitions));
        return (PaymentDefinitionRepository) repository.proxy();
    }
}

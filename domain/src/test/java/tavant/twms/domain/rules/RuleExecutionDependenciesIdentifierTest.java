package tavant.twms.domain.rules;

import junit.framework.TestCase;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;

public class RuleExecutionDependenciesIdentifierTest extends TestCase {

	public void testVisitMethodInvocationTarget() {
		RuleExecutionDependenciesIdentifier fixture = new RuleExecutionDependenciesIdentifier();
		DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(
				Claim.class, "claim.serviceInformation.causalPart",
				BusinessObjectModelFactory.CLAIM_RULES);
		IsAReturnWatchedPart isAWatchedPart = new IsAReturnWatchedPart(
				domainSpecificVariable);

		isAWatchedPart.accept(fixture);
		assertEquals(1, fixture.getRequiredDependencies().size());
		assertEquals("itemGroupService", fixture.getRequiredDependencies()
				.iterator().next());
	}

}

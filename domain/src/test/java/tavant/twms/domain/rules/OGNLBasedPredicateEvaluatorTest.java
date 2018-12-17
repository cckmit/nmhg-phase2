package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;

public class OGNLBasedPredicateEvaluatorTest extends TestCase {

    public void testEvaluatePredicate() {
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.type.type", BusinessObjectModelFactory.CLAIM_RULES);
        Equals equals = new Equals(domainSpecificVariable, new Constant("MACHINE", Type.STRING));
        OGNLBasedPredicateEvaluator fixture = new OGNLBasedPredicateEvaluator();

        Map<String, Object> context = new HashMap<String, Object>();
        MachineClaim machineClaim = new MachineClaim();
        PartsClaim partsClaim = new PartsClaim();
        context.put("claim", partsClaim);
        assertFalse(fixture.evaluatePredicate(equals, context));
        assertFalse(fixture.getParsedExpressions().isEmpty());
        context.put("claim", machineClaim);
        assertTrue(fixture.evaluatePredicate(equals, context));
    }

}

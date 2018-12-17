package tavant.twms.jbpm.decision.claim;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.RecoveryClaim;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: May 25, 2010
 * Time: 1:40:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class IsReopenNeeded implements DecisionHandler {

    private BeanLocator beanLocator = new BeanLocator();

    public String decide(ExecutionContext executionContext) throws Exception {
        Claim claim = (Claim)executionContext.getVariable("claim");
        ClaimService claimService = (ClaimService) beanLocator.lookupBean("claimService");
        if (claim != null && claim.getItemReference().isSerialized()) {
            Claim claimToBeReopened = claimService.reopenClaimForLaborRndUpOnCreditSubmission(claim);
            if (claimToBeReopened != null) {
                return "Yes";
            } else {
                return "No";
            }
        } else {
            return "No";
        }
    }
}

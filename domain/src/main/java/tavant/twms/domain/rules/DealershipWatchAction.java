/**
 * 
 */
package tavant.twms.domain.rules;

import java.util.Map;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;

/**
 * @author mritunjay.kumar
 * 
 */
public class DealershipWatchAction implements PredicateEvaluationAction {
	private DomainSpecificVariable domainVariable;

	public String performAction(Map<String, Object> evaluationContext) {
		StringBuffer watchMessage = new StringBuffer();
		if (domainVariable == null) {
			throw new IllegalStateException(
					"domainVariable cannot be null in DealerWatchAction");
		}
		ServiceProvider dealership = null;
		DealerGroupService service = (DealerGroupService) evaluationContext
				.get("dealerGroupService");
		Claim claim = (Claim) evaluationContext.get("claim");
		if (domainVariable.getAccessedFromType().equals("Claim")
				&& domainVariable.getFieldName().equals("claim.forDealer")) {
			dealership = claim.getForDealer();

			DealerGroup dealerGroupForWatchedDealership = service
					.findDealerGroupsForWatchedDealership(dealership);

			if (dealerGroupForWatchedDealership != null) {
				watchMessage = new StringBuffer(
						"This Dealer was found in the watch list: ");
				watchMessage.append(dealership.getDealerNumber()).append("(");
				watchMessage.append("Group:");
				watchMessage.append(dealerGroupForWatchedDealership.getName());
				watchMessage.append(")");
			}
		}
		return watchMessage.toString();
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

}

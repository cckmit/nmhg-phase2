/**
 * 
 */
package tavant.twms.web.admin.rules;

import java.util.List;

import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.rules.AssignmentRuleAction;
import tavant.twms.infra.HibernateCast;

/**
 * @author fatima.marneni
 *
 */
@SuppressWarnings("serial")
public class ManageDSMAdvisorRoutingRules extends ManageDomainRules {
    
	private List<UserCluster> userClusters;
	
	private UserClusterService userClusterService;
	
	private String result;
	
	private UserCluster userCluster;
	
	@Override
	protected void fetchAndSetActions() {
    	userClusters = userClusterService.findUserClustersByPurpose(
    			CreateProcessorRoutingRules.DSM_ADVISOR_ASSIGNMENT_PURPOSE);
    	
    	action = rule.getAction();
    	userCluster = (new HibernateCast<AssignmentRuleAction>().cast(action)).getUserCluster();
    }
	
	public List<UserCluster> getUserClusters() {
		return userClusters;
	}

	public void setUserClusters(List<UserCluster> userClusters) {
		this.userClusters = userClusters;
	}

	public void setUserClusterService(UserClusterService userClusterService) {
		this.userClusterService = userClusterService;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public UserCluster getUserCluster() {
		return userCluster;
	}

	public void setUserCluster(UserCluster userCluster) {
		this.userCluster = userCluster;
	}
}

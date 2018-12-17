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
public class ManageDSMRoutingRules extends ManageDomainRules {
    
	private List<UserCluster> userClusters;
	
	private UserClusterService userClusterService;
	
	private String result;
	
	private UserCluster userCluster;
	
	@Override
	protected void fetchAndSetActions() {
    	userClusters = userClusterService.findUserClustersByPurpose(
    			CreateProcessorRoutingRules.DSM_ASSIGNMENT_PURPOSE);
    	
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

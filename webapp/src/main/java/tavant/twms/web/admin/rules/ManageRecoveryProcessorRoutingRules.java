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

import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.rules.AssignmentRuleAction;
import tavant.twms.infra.HibernateCast;

import java.util.List;

public class ManageRecoveryProcessorRoutingRules extends ManageDomainRules {

    private List<UserCluster> userClusters;

    private UserClusterService userClusterService;

    private String result;

    private UserCluster userCluster;

    // populated from the ruleadmin.xml
    // TODO : add to ruleadmin.xml for the update case.... Need to fix the update scenario
    // Fixed the update scenario.-20th Oct
    private String ruleContext;

    @Override
    protected void fetchAndSetActions() {
        userClusters = userClusterService
                .findUserClustersByPurpose(CreateProcessorRoutingRules.REC_CLAIM_ASSIGNMENT_PURPOSE);

        action = rule.getAction();
        if(getActionId()!=null && action !=null )
        {

        	userCluster = userClusterService.findByNameAndPurpose(actionId,CreateProcessorRoutingRules.REC_CLAIM_ASSIGNMENT_PURPOSE);
        	action = getRuleAdministrationService().findAssignmentRuleActionByUserGroupNameAndState(
					getActionId(), result, action.getContext());

			if (action == null) {				
				action = new AssignmentRuleAction(userCluster, rule.getContext());
				action.setState(result);
			}   
			action.setName(getActionId());
        }
        else
        {
        	userCluster = (new HibernateCast<AssignmentRuleAction>().cast(action)).getUserCluster();          	
        }
       
        
    }

    public void setUserClusterService(UserClusterService userClusterService) {
        this.userClusterService = userClusterService;
    }

    public List<UserCluster> getUserClusters() {
        return userClusters;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setRuleContext(String context) {
        this.ruleContext = context;
    }

    public UserCluster getUserCluster() {
        return userCluster;
    }

    public void setUserCluster(UserCluster userCluster) {
        this.userCluster = userCluster;
    }

    public String getResult() {
        return result;
    }

}

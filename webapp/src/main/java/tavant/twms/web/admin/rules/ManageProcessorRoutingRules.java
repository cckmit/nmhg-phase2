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

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.common.Constants;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.loa.LimitOfAuthoritySchemeService;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.rules.AssignmentRuleAction;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.infra.HibernateCast;

public class ManageProcessorRoutingRules extends ManageDomainRules {

	private List<UserCluster> userClusters;

	private UserClusterService userClusterService;

	private String result;
	
	private UserCluster userCluster;

	private RuleAdministrationService ruleAdminService;

	private LimitOfAuthoritySchemeService loaService;

	private List<LimitOfAuthorityScheme> loaSchemes = new ArrayList<LimitOfAuthorityScheme>();

	// populated from the ruleadmin.xml
	// TODO : add to ruleadmin.xml for the update case....  Need to fix the update scenario.
	private String ruleContext;
	
    @Override
	protected void fetchAndSetActions() {
    	action = rule.getAction();
    	if (getActionId() != null && action != null) {
    		
    		String ctxt = rule.getContext();
    		if(ctxt == null)
    			ctxt = getContext();

			if (RuleAdministrationService.PROCESSOR_ROUTING_RULE_CATEGORY.equals(rule.getAction().getContext()))
			{
				userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.CLAIM_ASSIGNMENT_PURPOSE);
				userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.CLAIM_ASSIGNMENT_PURPOSE);
			} 
			else if (RuleAdministrationService.DSM_ROUTING_RULE_CATEGORY.equals(ctxt))
			{
				userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.DSM_ASSIGNMENT_PURPOSE);
				userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.DSM_ASSIGNMENT_PURPOSE);
			}
			else if (RuleAdministrationService.DSM_ADVISOR_ROUTING_RULE_CATEGORY.equals(ctxt)) 
			{
				userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.DSM_ADVISOR_ASSIGNMENT_PURPOSE);
				userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.DSM_ADVISOR_ASSIGNMENT_PURPOSE);
			}
			else if (RuleAdministrationService.REC_PROCESSOR_ROUTING_RULE_CATEGORY.equals(ctxt)) 
			{
				userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.REC_CLAIM_ASSIGNMENT_PURPOSE);
				userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.REC_CLAIM_ASSIGNMENT_PURPOSE);
			}
			else if (RuleAdministrationService.CP_ADVISOR_ROUTING_RULE_CATEGORY.equals(ctxt)) 
			{
				userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.CP_ADVISOR_ASSIGNMENT_PURPOSE);
				userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.CP_ADVISOR_ASSIGNMENT_PURPOSE);
			}
		
			if (result.equalsIgnoreCase(Constants.RULE_ADMIN_ASSIGN_TO_LOA_SCHEME)) {
				action = getRuleAdministrationService().findAssignmentRuleActionByLOASchemeStateAndContext(getActionId(), result, action.getContext());
				if (action == null) {
					LimitOfAuthorityScheme loaScheme = loaService
							.findByName(getActionId());
					action = new AssignmentRuleAction(rule.getAction()
							.getContext(), loaScheme);
					action.setState(result);
					action.setName(loaScheme.getName());
				}
			} else {
				  action = getRuleAdministrationService().findAssignmentRuleActionByUserGroupNameAndState(getActionId(), result,action.getContext());
					if (action == null) {
						action = new AssignmentRuleAction(userCluster, rule.getAction().getContext());
						action.setState(result);
					}
				  action.setName(getActionId());
			}
		    	
		} else {
					userCluster = (new HibernateCast<AssignmentRuleAction>().cast(action)).getUserCluster();
					userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.CLAIM_ASSIGNMENT_PURPOSE);
			  }
    	
    	loaSchemes = loaService.findAll();
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

	public void setRuleAdminService(RuleAdministrationService ruleAdminService) {
		this.ruleAdminService = ruleAdminService;
	}

	public void setLoaService(LimitOfAuthoritySchemeService loaService) {
		this.loaService = loaService;
	}

	public List<LimitOfAuthorityScheme> getLoaSchemes() {
		return loaSchemes;
	}

}

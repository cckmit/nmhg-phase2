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
package tavant.twms.jbpm.assignment;

import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_RULES_STATE;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_NOT_ASSIGN_RULES_STATE;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_TO_LOA_SCHEME;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.rules.RuleAdministrationService;

/**
 * TODO : Need to abstract this class to make it extensible. This class just
 * takes care of claim assignment for now.
 * 
 */
public class AssignmentRuleExecutor {

	private RuleAdministrationService ruleAdministrationService;

	private OrgService orgService;

	private List<String> getAssignedToListFromValidationResults(ValidationResults validationResults) {
		Map map = validationResults.getAssignStateToUserGroupMap();
		Set<String> assignTo = (Set<String>) map
		.get(RULE_ADMIN_ASSIGN_TO_LOA_SCHEME);
		
		List<String> assignToList = new ArrayList<String>();
		if(assignTo != null && assignTo.size() >0)
			assignToList.addAll(assignTo);
		else{
			assignTo = (Set<String>) map
			.get(RULE_ADMIN_ASSIGN_RULES_STATE);
			if(assignTo != null)
				assignToList.addAll(assignTo);
		}
		return assignToList;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> fetchEligibleProcessorsUsingAssignmentRules(Claim claim) {
		ValidationResults ruleResults = ruleAdministrationService
				.executeAssignmentRules(claim);
		return getAssignedToListFromValidationResults(ruleResults);
	}

	@SuppressWarnings("unchecked")
	public List<String> fetchEligibleRecProcessorsUsingAssignmentRules(
			RecoveryClaim recoveryClaim) {
		ValidationResults ruleResults = ruleAdministrationService
				.executeRecoveryAssignmentRules(recoveryClaim);
		Map map = ruleResults.getAssignStateToUserGroupMap();
		Set<UserCluster> assignTo = (Set<UserCluster>) map
				.get(RULE_ADMIN_ASSIGN_RULES_STATE);
		Set<UserCluster> notAssignTo = (Set<UserCluster>) map
				.get(RULE_ADMIN_NOT_ASSIGN_RULES_STATE);

		if (assignTo == null || assignTo.size() == 0) {
			return null;
		} else if (notAssignTo != null && notAssignTo.size() > 0) {
			removeNotAssignedToGroupFromTheAssignTo(assignTo, notAssignTo);
		}
		Role role = orgService.findRoleByName(Role.RECOVERYPROCESSOR);
		return getAllUsersFromClusterByRole(role, assignTo, recoveryClaim.getClaim().getBusinessUnitInfo());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> fetchEligibleDSMsUsingAssignmentRules(Claim claim){
		ValidationResults ruleResults = ruleAdministrationService
				.executeDSMRules(claim);
		return getAssignedToListFromValidationResults(ruleResults);
	}

	@SuppressWarnings("unchecked")
	public List<String> fetchEligibleDSMAdvisorsUsingAssignmentRules(Claim claim){
		ValidationResults ruleResults = ruleAdministrationService
				.executeDSMAdvisorRules(claim);
		return getAssignedToListFromValidationResults(ruleResults);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> fetchEligibleCPAdvisorsUsingAssignmentRules(Claim claim){
		ValidationResults ruleResults = ruleAdministrationService
				.executeCPAdvisorRules(claim);
		Map map = ruleResults.getAssignStateToUserGroupMap();
		Set<UserCluster> assignTo = (Set<UserCluster>) map
				.get(RULE_ADMIN_ASSIGN_RULES_STATE);
		Set<UserCluster> notAssignTo = (Set<UserCluster>) map
				.get(RULE_ADMIN_NOT_ASSIGN_RULES_STATE);
		
		if (assignTo == null || assignTo.size() == 0) {
			return null;
		} else if (notAssignTo != null && notAssignTo.size() > 0) {
			removeNotAssignedToGroupFromTheAssignTo(assignTo, notAssignTo);
		}
		Role role = orgService.findRoleByName(Role.CP_ADVISOR);
		return getAllUsersFromClusterByRole(role, assignTo,claim.getBusinessUnitInfo());
	}	

	// Assumes there can't be overlapping users in a UserCluster.
	List<String> getAllUsersFromClusterByRole(Role role,
			Set<UserCluster> userGroups, BusinessUnitInfo businessUnitInfo) {
		List<String> result = new ArrayList<String>();
		for (UserCluster cluster : userGroups) {
			for (User user : cluster.getIncludedUsers()) {
				if (user.getRoles().contains(role) && businessUnitInfo!=null && 
						user.isAvailableForBU(businessUnitInfo.getName(), role.getName())) {
					result.add(user.getName());
				}
			}
		}
		return result;
	}

	// Assumes there can't be overlapping users in a UserCluster.
	private void removeNotAssignedToGroupFromTheAssignTo(
			Set<UserCluster> assignTo, Set<UserCluster> notAssignTo) {
		for (UserCluster cluster : notAssignTo) {
			if (assignTo.contains(cluster)) {
				assignTo.remove(cluster);
			}
		}
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

}

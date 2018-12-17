package tavant.twms.domain.rules.group;

import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_RULES_STATE;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_TO_LOA_SCHEME;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_NOT_ASSIGN_RULES_STATE;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.loa.LimitOfAuthorityLevel;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public abstract class AssignmentRuleGroupExecutionCallback extends RuleGroupExecutionCallback {

	private String assignableRole;
	private boolean lastExecutionSuccessful;
	
	public boolean isLastExecutionSuccessful() {
		return lastExecutionSuccessful;
	}

	public void setLastExecutionSuccessful(boolean lastExecutionSuccessful) {
		this.lastExecutionSuccessful = lastExecutionSuccessful;
	}

	public AssignmentRuleGroupExecutionCallback(Map<String, Object> context,
			Map<String, Object> resultMap,
			String assignableRole) {
		super(context, resultMap);
		this.assignableRole = assignableRole;
	}
	
	protected boolean isUserAvailable(User user) {
		String businessUnitName = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
		if (user.isAvailableForBU(businessUnitName, assignableRole)) {
			return true;
		}
		return false;
	}

	/*
	 * When there are a set of valid users from the previous rule group execution
	 * only those users will be considered for the results of the current rule group
	 */
	protected void setValidationResults(Set<UserCluster> assignedToList,Set<UserCluster> notAssignedToList, LimitOfAuthorityScheme assignedToLOAScheme, ValidationResults validationResults ) {
		Set<String> assignedTo = (Set<String>)validationResults.getAssignStateToUserGroupMap().get(RULE_ADMIN_ASSIGN_RULES_STATE);
		Set<String> notAssignedTo = (Set<String>)validationResults.getAssignStateToUserGroupMap().get(RULE_ADMIN_NOT_ASSIGN_RULES_STATE);
		Set<String> assignedToLOA = (Set<String>)validationResults.getAssignStateToUserGroupMap().get(RULE_ADMIN_ASSIGN_TO_LOA_SCHEME);
		if(assignedTo == null)
			assignedTo = new HashSet<String>();
		if(assignedToLOA == null)
			assignedToLOA = new HashSet<String>();
		if(notAssignedTo == null)
			notAssignedTo = new HashSet<String>();
		for(UserCluster cluster : notAssignedToList) {
			if(null != cluster){
			for(User user : cluster.getIncludedUsers()) {
				if(!notAssignedTo.contains(user.getName()))
					notAssignedTo.add(user.getName());
			}}
		}
		boolean narrowDownResult = !assignedTo.isEmpty();
		Set<String> userList = new HashSet<String>();
		for(UserCluster cluster : assignedToList) {
			if(null != cluster){
			for(User user : cluster.getIncludedUsers()) {
				if(!notAssignedTo.contains(user.getName()) && !userList.contains(user.getName())
					&& (!narrowDownResult || (narrowDownResult && assignedTo.contains(user.getName()))))
					if(isUserAvailable(user))
						userList.add(user.getName());
			}
			}
		}
		Set<String> loaUserList = new HashSet<String>();
		if (null != assignedToLOAScheme) {
			int previousLevel = 0;
			List<LimitOfAuthorityLevel> levels = assignedToLOAScheme.getLoaLevels();
			Collections.sort(levels);
			for (LimitOfAuthorityLevel level : levels) {
				if (previousLevel == level.getLoaLevel()) {
					if (isUserAvailable(level.getLoaUser())) {
						loaUserList.add(level.getLoaUser().getName());
					}
				} else if (previousLevel == 0 && previousLevel < level.getLoaLevel()) {
					previousLevel = level.getLoaLevel();
					if (isUserAvailable(level.getLoaUser())) {
						loaUserList.add(level.getLoaUser().getName());
					}
				} else {
					if (loaUserList.size() == 0) {
						previousLevel = level.getLoaLevel();
						if (isUserAvailable(level.getLoaUser())) {
							loaUserList.add(level.getLoaUser().getName());
						}
					} else {
						break;
					}
				}
			}
		}
		if(userList.size() == 0 && loaUserList == null) {
			userList = assignedTo;
			loaUserList = assignedToLOA;
			setLastExecutionSuccessful(false);
		} else {
			setLastExecutionSuccessful(true);
		}
		validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_ASSIGN_RULES_STATE, userList);
		validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_ASSIGN_TO_LOA_SCHEME, loaUserList);
		validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_NOT_ASSIGN_RULES_STATE, notAssignedTo);
	}
	
	public abstract int getAssignableCount();
	
}

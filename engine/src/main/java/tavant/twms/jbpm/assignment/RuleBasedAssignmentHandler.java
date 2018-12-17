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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.jbpm.infra.ContextVariableProvider;
import tavant.twms.security.SecurityHelper;


/**
 * @author vineeth.varghese
 * @date Aug 22, 2006
 */
@SuppressWarnings("serial")
public class RuleBasedAssignmentHandler implements AssignmentHandler {

	private static final Logger logger = Logger
			.getLogger(RuleBasedAssignmentHandler.class);

	private final BeanLocator beanLocator = new BeanLocator();

	private List<String> variables;

	private String ruleSet;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jbpm.taskmgmt.def.AssignmentHandler#assign(org.jbpm.taskmgmt.exe.Assignable,
	 *      org.jbpm.graph.exe.ExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	public void assign(final Assignable assignable, ExecutionContext context)
			throws Exception {
		if (ruleSet.equals("DsmAssignment")) {
			assignDSM(assignable,context);
			return;
		}
		if (ruleSet.equals("RecoveryProcessorAssignment")) {
			assignRecoveryProcessor(assignable, context);
			return;
		}

		if (ruleSet.equals("ProcessorAssignment")) {
			assignWarrantyProcessor(assignable, context);
			return;
		}
	}
	private void assignDSM(final Assignable assignable, ExecutionContext context){
		Collection objectList = new ContextVariableProvider(context)
		        .getExplodedContextVariables(variables);
		Assert.notEmpty(objectList, "The claim to be assigned cannot be null");
		Claim claim = (Claim) objectList.iterator()
				.next();
		AssignmentRuleExecutor assignmentRuleExecutor = (AssignmentRuleExecutor) beanLocator
				.lookupBean("assignmentRuleExecutor");
		OrgService orgService = (OrgService) beanLocator.lookupBean("orgService");		
		
		List<String> eligibleProcessors = assignmentRuleExecutor
				.fetchEligibleDSMsUsingAssignmentRules(claim);

		if (eligibleProcessors == null || eligibleProcessors.size() == 0) {
			User defaulfUser = orgService.findDefaultUserBelongingToRoleForSelectedBU(claim.getBusinessUnitInfo().getName(), Role.DSM);
			if (defaulfUser!=null && !"".equalsIgnoreCase(defaulfUser.getName()))
				assignable.setActorId(defaulfUser.getName());
			else
				assignDefaultDSM(assignable);
		} else {
			assignable.setActorId(getUserWithLeastLoad(eligibleProcessors));
		}		
	}

	private void assignRecoveryProcessor(final Assignable assignable,
			ExecutionContext context) {
		Collection objectList = new ContextVariableProvider(context)
				.getExplodedContextVariables(variables);
		Assert.notEmpty(objectList, "The claim to be assigned cannot be null");
		RecoveryClaim recoveryClaim = (RecoveryClaim) objectList.iterator()
				.next();
		AssignmentRuleExecutor assignmentRuleExecutor = (AssignmentRuleExecutor) beanLocator
				.lookupBean("assignmentRuleExecutor");
		OrgService orgService = (OrgService) beanLocator.lookupBean("orgService");

		List<String> eligibleProcessors = assignmentRuleExecutor
				.fetchEligibleRecProcessorsUsingAssignmentRules(recoveryClaim);
		if (eligibleProcessors == null || eligibleProcessors.size() == 0) {
			User defaulfUser = orgService.findDefaultUserBelongingToRoleForSelectedBU(recoveryClaim.getBusinessUnitInfo().getName(), Role.RECOVERYPROCESSOR);
			if (defaulfUser!=null && !"".equalsIgnoreCase(defaulfUser.getName()))
				assignable.setActorId(defaulfUser.getName());
			else
				assignDefaultRecProcessor(assignable);
		} else {
			assignable.setActorId(getUserWithLeastLoad(eligibleProcessors));
		}
	}

	private void assignWarrantyProcessor(final Assignable assignable, ExecutionContext context) {
		Collection objectList = new ContextVariableProvider(context).getExplodedContextVariables(variables);
		Assert.notEmpty(objectList, "The claim to be assigned cannot be null");
		Claim claim = (Claim) objectList.iterator().next();
		AssignmentRuleExecutor assignmentRuleExecutor = (AssignmentRuleExecutor) beanLocator
				.lookupBean("assignmentRuleExecutor");
		SecurityHelper securityHelper = (SecurityHelper) beanLocator.lookupBean("securityHelper");
		OrgService orgService = (OrgService) beanLocator.lookupBean("orgService");
		if (claim.getState().equals(ClaimState.REOPENED)) {
			assignable.setActorId(securityHelper.getLoggedInUser().getName());
		} else {
			List<String> eligibleProcessors = assignmentRuleExecutor.fetchEligibleProcessorsUsingAssignmentRules(claim);
			if (eligibleProcessors == null || eligibleProcessors.size() == 0) {
				User defaulfUser = orgService.findDefaultUserBelongingToRoleForSelectedBU(claim.getBusinessUnitInfo()
						.getName(), Role.PROCESSOR);
				if (defaulfUser != null && !"".equalsIgnoreCase(defaulfUser.getName()))
					assignable.setActorId(defaulfUser.getName());
				else
					assignDefaultProcessor(assignable);
			} else {
				assignable.setActorId(getUserWithLeastLoad(eligibleProcessors));
			}
		}

	}

	String getUserWithLeastLoad(List<String> eligibleProcessors) {
		LoadBalancingService loadBalancingService = (LoadBalancingService) beanLocator
				.lookupBean("loadBalancingService");
		List<String> sortedUsers = loadBalancingService
				.findUsersSortedByLoad(eligibleProcessors);

		// The first condition usually occurs only in the initial stages or when
		// a new user is added.
		if (sortedUsers == null
				|| sortedUsers.size() < eligibleProcessors.size()) {
			return findAnUnassignedUser(sortedUsers, eligibleProcessors);
		} else {
			return sortedUsers.get(0); // The first one will be the least
			// loaded.
		}
	}

	String findAnUnassignedUser(List<String> usersWithTasks,
			List<String> eligibleUsers) {
		if (usersWithTasks == null)
			return eligibleUsers.get(0);

		for (String eligibleUser : eligibleUsers) {
			if (!usersWithTasks.contains(eligibleUser)) {
				return eligibleUser;
			}
		}
		return null; // this case shouldn't arise !
	}

	private void assignDefaultProcessor(Assignable assignable) {
		assignable.setActorId("processor");
	}

	private void assignDefaultRecProcessor(Assignable assignable) {
		assignable.setActorId("sra");
	}
	
	private void assignDefaultDSM(Assignable assignable) {
		assignable.setActorId("dsm");
	}

	public String getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

}

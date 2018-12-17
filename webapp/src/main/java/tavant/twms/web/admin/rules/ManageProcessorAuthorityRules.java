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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.rules.AssignmentRuleAction;
import tavant.twms.domain.rules.DomainRuleAction;
import tavant.twms.domain.rules.DomainRuleAudit;
import tavant.twms.domain.rules.RuleAdministrationException;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.infra.HibernateCast;

/**
 * @author jhulfikar.ali
 *
 */

@SuppressWarnings("serial")
public class ManageProcessorAuthorityRules extends ManageDomainRules {

	private UserClusterService userClusterService;
	
	private String result;

	private SortedMap<String, String> availableActionsOnClaim;
	
	private List<String> availableActionsOnRule;
    
    private List<UserCluster> userClusters;

    private UserCluster userCluster;

	@Override
	public void prepare() throws Exception {
		super.prepare();
    	if (availableActionsOnClaim==null)
    		availableActionsOnClaim = new TreeMap<String, String>();
    	availableActionsOnClaim.put(UserClusterService.ACTION_PUT_ON_HOLD,UserClusterService.ACTION_PUT_ON_HOLD); //Put on hold
    	availableActionsOnClaim.put(UserClusterService.ACTION_FORWARDED_TO_DEALER, UserClusterService.ACTION_REQUEST_INFORMATION_FROM_DEALER); //Request information from dealer
    	availableActionsOnClaim.put(UserClusterService.ACTION_REQUEST_FOR_ADVICE, UserClusterService.ACTION_REQUEST_FOR_ADVICE); //Request for advice 
    	availableActionsOnClaim.put(UserClusterService.ACTION_TRANSFER_TO, UserClusterService.ACTION_TRANSFER_TO); //Transfer to 
    	availableActionsOnClaim.put(UserClusterService.ACTION_ACCEPT, UserClusterService.ACTION_ACCEPT); //Accept 
    	availableActionsOnClaim.put(UserClusterService.ACTION_DENY, UserClusterService.ACTION_DENY); //Deny
    	availableActionsOnClaim.put(UserClusterService.ACTION_RE_PROCESS, UserClusterService.ACTION_RE_PROCESS); //Re-process
    	availableActionsOnClaim.put(UserClusterService.ACTION_REQUEST_REVIEW_FROM_CP, UserClusterService.ACTION_REQUEST_REVIEW_FROM_CP); //Request review from CP

    	//availableActionsOnClaim.add(UserClusterService.ACTION_FORWARDED_TO_DEALER); //Forwarded To Dealer
    	//availableActionsOnClaim.add("");

    	userClusters = userClusterService.findUserClustersByPurpose(CreateProcessorRoutingRules.PROCESSOR_AUTHORITY);
    	if (rule != null && rule.getRuleAudits() != null
				&& !rule.getRuleAudits().isEmpty())
    	{
    		if (availableActionsOnRule==null)
    			availableActionsOnRule = new ArrayList<String>();
    		DomainRuleAudit domainRuleAudit = (DomainRuleAudit) rule.getRuleAudits().get(rule.getRuleAudits().size() - 1); // To get the latest one
    		DomainRuleAction domainRuleAction = domainRuleAudit.getAction();
    		if (domainRuleAction!=null && domainRuleAction.getState()!=null)
    			availableActionsOnRule.addAll(trimSpacesFromList(Arrays.asList(domainRuleAction.getState().split(","))));
    	}
	}

    protected void fetchAndSetActions() {
    	if(rule!=null) {
    		if (getActions()==null)
    			setActions(new ArrayList<DomainRuleAction>());
            getActions().addAll(getRuleAdministrationService()
									.findDomainRuleActionsByContext(
											RuleAdministrationService.PROCESSOR_AUTHORITY_RULE_CATEGORY));
        } else {
        	getActions().addAll(getRuleAdministrationService().findAllDomainRuleActions());
        }

        if (rule != null && rule.getRuleAudits() != null
				&& !rule.getRuleAudits().isEmpty()) {        	
        	action = ((DomainRuleAudit) rule.getRuleAudits().get(rule.getRuleAudits().size() - 1)).getAction();            
            if(action != null)
            	userCluster = (new HibernateCast<AssignmentRuleAction>().cast(action)).getUserCluster();           
        }
    }
    
    @Override
    public String updateDomainRule() throws RuleAdministrationException {
    	DomainRuleAction resultAction = getRuleAdministrationService().findAssignmentRuleActionByUserGroupNameAndState(
                getActionId(), getResult(), getContext());
    	if (resultAction == null) {
            resultAction = new AssignmentRuleAction(userCluster, getContext());
    	}
    	if (resultAction != null) {
    		action = resultAction;
        	action.setState(result);
        	if (getActionId() != null && action != null) {
        		userCluster = userClusterService.findByNameAndPurpose(getActionId(),CreateProcessorRoutingRules.PROCESSOR_AUTHORITY);
        		((AssignmentRuleAction)action).setUserCluster(userCluster);
    			action.setName(getActionId());
    		}
    	}
    	return super.updateDomainRule();
    }
    
	public SortedMap<String, String> getAvailableActionsOnClaim() {
		return availableActionsOnClaim;
	}

	public void setAvailableActionsOnClaim(SortedMap<String, String> availableActionsOnClaim) {
		this.availableActionsOnClaim = availableActionsOnClaim;
	}

	public List<UserCluster> getUserClusters() {
		return userClusters;
	}

	public void setUserClusters(List<UserCluster> userClusters) {
		this.userClusters = userClusters;
	}

	public UserClusterService getUserClusterService() {
		return userClusterService;
	}

	public void setUserClusterService(UserClusterService userClusterService) {
		this.userClusterService = userClusterService;
	}

	public List<String> getAvailableActionsOnRule() {
		return availableActionsOnRule;
	}

	public void setAvailableActionsOnRule(List<String> availableActionsOnRule) {
		this.availableActionsOnRule = availableActionsOnRule;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	private List<String> trimSpacesFromList(List<String> listToTrim) {
		if (listToTrim==null || listToTrim.isEmpty())
			return new ArrayList<String>();
		List<String> trimmedList = new ArrayList<String>(listToTrim.size());
		for (Iterator<String> iter = listToTrim.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if (StringUtils.hasText(element))
				trimmedList.add(element.trim());
		}		
		return trimmedList;
	}

	public UserCluster getUserCluster() {
		return userCluster;
	}

	public void setUserCluster(UserCluster userCluster) {
		this.userCluster = userCluster;
	}
	
}

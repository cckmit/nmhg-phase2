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

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Constants;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.loa.LimitOfAuthoritySchemeService;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.rules.AssignmentRuleAction;
import tavant.twms.domain.rules.DomainRuleAction;

@SuppressWarnings("serial")
public class CreateProcessorRoutingRules extends CreateDomainRule {

	public static final String CLAIM_ASSIGNMENT_PURPOSE = "Claim Assignment";

    public static final String REC_CLAIM_ASSIGNMENT_PURPOSE = "Recovery Claim Assignment";
    
    public static final String DSM_ASSIGNMENT_PURPOSE = "DSM Assignment";
    
    public static final String DSM_ADVISOR_ASSIGNMENT_PURPOSE = "DSM Advisor Assignment";
    
    public static final String PROCESSOR_AUTHORITY = "Processor Authority";
	
	public static final String CP_ADVISOR_ASSIGNMENT_PURPOSE = "CP Advisor Assignment";

    private UserClusterService userClusterService;
    
    private LimitOfAuthoritySchemeService loaService;

    private List<UserCluster> userClusters;
        
    private List<LimitOfAuthorityScheme> loaSchemes = new ArrayList<LimitOfAuthorityScheme>();

    private String purpose;
    
    private String selectedAssignedTo;
    
    private String selectedUserCluster;

    private List<String> availableActionsOnClaim;

    @Override
	public void prepare() throws Exception {
		super.prepare();
    	if (availableActionsOnClaim==null)
    		availableActionsOnClaim = new ArrayList<String>();
    	availableActionsOnClaim.add(UserClusterService.ACTION_PUT_ON_HOLD); //Put on hold
    	availableActionsOnClaim.add(UserClusterService.ACTION_REQUEST_INFORMATION_FROM_DEALER); //Request information from dealer
    	availableActionsOnClaim.add(UserClusterService.ACTION_REQUEST_FOR_ADVICE); //Request for advice 
    	availableActionsOnClaim.add(UserClusterService.ACTION_TRANSFER_TO); //Transfer to 
    	availableActionsOnClaim.add(UserClusterService.ACTION_ACCEPT); //Accept 
    	availableActionsOnClaim.add(UserClusterService.ACTION_DENY); //Deny
    	availableActionsOnClaim.add(UserClusterService.ACTION_RE_PROCESS); //Re-process
    	availableActionsOnClaim.add(UserClusterService.ACTION_REQUEST_REVIEW_FROM_CP); //Request review from CP
    	
	}

	@Override
    @SuppressWarnings("unused")
    public String createRule() {
        if (!userClusterService.doUserClustersExistForPurpose(purpose) && loaService.findAll().isEmpty()) {
            return handleUserGroupsLOASchemesNotConfigured();
        }
        return super.createRule();
    }

    @SuppressWarnings("deprecation")
    public String searchPredicates() {
        super.searchPredicates();
        userClusters = userClusterService.findUserClustersByPurpose(purpose);
        loaSchemes = loaService.findAll();

        if (userClusters.isEmpty() && loaSchemes.isEmpty()) {
            return handleUserGroupsLOASchemesNotConfigured();
        }
        
        return SUCCESS;
    }

    private String handleUserGroupsLOASchemesNotConfigured() {
        addActionError("error.manageBusinessCondition.userGroupsLOASchemesNotSetup");
        return SETUP;
    }

    @Override
    // TODO: Need to refactor this...
    protected DomainRuleAction fetchAction() {
    	DomainRuleAction resultAction = null;
		if (selectedAssignedTo.equalsIgnoreCase(Constants.RULE_ADMIN_ASSIGN_TO_LOA_SCHEME)) {
			resultAction = getRuleAdministrationService().findAssignmentRuleActionByLOASchemeStateAndContext(selectedUserCluster, selectedAssignedTo, context);

			if (resultAction == null) {
				LimitOfAuthorityScheme loaScheme = loaService.findByName(selectedUserCluster);
				resultAction = new AssignmentRuleAction(context, loaScheme);
				resultAction.setState(selectedAssignedTo);
				resultAction.setName(loaScheme.getName());
			}
		} else {

			 resultAction = getRuleAdministrationService().findAssignmentRuleActionByUserGroupNameAndState(selectedUserCluster, selectedAssignedTo,context);

			if (resultAction == null) {
				UserCluster userCluster = userClusterService.findByNameAndPurpose(selectedUserCluster, purpose);
				resultAction = new AssignmentRuleAction(userCluster, context);
				resultAction.setState(selectedAssignedTo);
				resultAction.setName(userCluster.getName());
			}
		}
        return resultAction;
    }
    
    @Override
	public void validate() {    	
    	if (!StringUtils.hasText(selectedAssignedTo)) {
			addActionError("error.manageBusinessRule.actionRequired");
		}
    	if (!StringUtils.hasText(selectedUserCluster)) {
			addActionError("error.manageBusinessRule.userGroupRequired");
		}
    	super.validate();
    }

    public void setUserClusterService(UserClusterService userClusterService) {
        this.userClusterService = userClusterService;
    }

    public List<UserCluster> getUserClusters() {
        return userClusters;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

	public String getSelectedAssignedTo() {
		return selectedAssignedTo;
	}

	public void setSelectedAssignedTo(String selectedAssignedTo) {
		this.selectedAssignedTo = selectedAssignedTo;
	}

	public String getSelectedUserCluster() {
		return selectedUserCluster;
	}

	public void setSelectedUserCluster(String selectedUserCluster) {
		this.selectedUserCluster = selectedUserCluster;
	}

	public List<String> getAvailableActionsOnClaim() {
		return availableActionsOnClaim;
	}

	public void setAvailableActionsOnClaim(List<String> availableActionsOnClaim) {
		this.availableActionsOnClaim = availableActionsOnClaim;
	}

	public LimitOfAuthoritySchemeService getLoaService() {
		return loaService;
	}

	public List<LimitOfAuthorityScheme> getLoaSchemes() {
		return loaSchemes;
	}

	public void setLoaSchemes(List<LimitOfAuthorityScheme> loaSchemes) {
		this.loaSchemes = loaSchemes;
		
	}


	public void setLoaService(LimitOfAuthoritySchemeService loaService) {
		this.loaService = loaService;
	}
	
}

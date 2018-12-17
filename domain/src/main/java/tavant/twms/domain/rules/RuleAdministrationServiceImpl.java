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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Mar 1, 2007
 * Time: 7:54:13 PM
 */

package tavant.twms.domain.rules;

import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_RULES_STATE;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_NOT_ASSIGN_RULES_STATE;
import static tavant.twms.domain.common.Constants.RULE_ADMIN_ASSIGN_TO_LOA_SCHEME;
import static tavant.twms.domain.rules.DomainRuleAction.CLAIM_STATE_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.validation.CampaignClaimValidationService;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.FailedRuleDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RuleFailure;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AccountabilityCode;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.ListOfValuesType;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.partreturn.InspectionResult;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.rules.group.AssignmentRuleGroupExecutionCallback;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.domain.rules.group.RuleGroupAdministrationService;
import tavant.twms.domain.rules.group.RuleGroupExecutionCallback;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 */
public class RuleAdministrationServiceImpl extends
        GenericServiceImpl<DomainRule, Long, RuleAdministrationException> implements
        RuleAdministrationService {

	private DomainRuleRepository domainRuleRepository;

    private DomainRuleActionRepository domainRuleActionRepository;

    private AssignmentRuleActionRepository assignmentRuleActionRepository;

    private RuleExecutionTemplate ruleExecutionTemplate;

    private CampaignClaimValidationService campaignClaimValidationService;

    private RuleXMLConverter ruleXMLConverter;

    private SecurityHelper securityHelper;

    private ContractService contractService;

    private MiscellaneousItemConfigService miscellaneousItemConfigService;

    private LovRepository lovRepository;

    private RuleGroupAdministrationService ruleGroupAdministrationService;

    private ConfigParamService configParamService;

    @Required
    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    @Required
    public void setRuleXMLConverter(RuleXMLConverter ruleXMLConverter) {
        this.ruleXMLConverter = ruleXMLConverter;
    }

    @Required
    public void setDomainRuleRepository(DomainRuleRepository domainRuleRepository) {
        this.domainRuleRepository = domainRuleRepository;
    }

    @Required
    public void setDomainRuleActionRepository(DomainRuleActionRepository domainRuleActionRepository) {
        this.domainRuleActionRepository = domainRuleActionRepository;
    }

    public void setAssignmentRuleActionRepository(
            AssignmentRuleActionRepository assignmentRuleActionRepository) {
        this.assignmentRuleActionRepository = assignmentRuleActionRepository;
    }

    public void setRuleExecutionTemplate(RuleExecutionTemplate ruleExecutionTemplate) {
        this.ruleExecutionTemplate = ruleExecutionTemplate;
    }

    @Override
    public GenericRepository<DomainRule, Long> getRepository() {
        return domainRuleRepository;
    }

    public List<DomainRule> findRulesByName(String name, String forRuleEditorContext) {
        return domainRuleRepository.findByNameInContext(name, forRuleEditorContext,
                doesContextUseRuleGroup(forRuleEditorContext));
    }

    public List<DomainRule> findRulesByContext(String category) {
        return domainRuleRepository.findByContext(category, doesContextUseRuleGroup(category));
    }
    
    private List<DomainRule> findProcessorAuthorityRules(String category, User processor) {
        return domainRuleRepository.findProcessorAuthorityRules(category, processor);
    }

    public List<DomainRuleAction> findAllDomainRuleActions() {
        return domainRuleActionRepository.findAll();
    }

    public List<DomainRuleAction> findDomainRuleActionsByContext(String context) {
        return domainRuleActionRepository.findByContext(context);
    }

    public List<DomainRuleAction> findDomainRuleActionsByName(String actionName) {
        return domainRuleActionRepository.findByName(actionName);
    }

    public void saveDomainRuleAction(DomainRuleAction domainRuleAction) {
        domainRuleActionRepository.save(domainRuleAction);
    }

    public ValidationResults executeDSMRules(Claim claim){
    	final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeAssignmentRuleGroup(DSM_ROUTING_RULE_CATEGORY, context, resultMap, Role.DSM);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    public ValidationResults executeCPAdvisorRules(Claim claim){
    	final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);

        // assuming only one rule category for the processor routing
        // rules. Else make it a list.
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeAssignmetRuleCategory(CP_ADVISOR_ROUTING_RULE_CATEGORY, context, resultMap);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    public ValidationResults executeDSMAdvisorRules(Claim claim){
    	final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeAssignmentRuleGroup(DSM_ADVISOR_ROUTING_RULE_CATEGORY, context, resultMap, Role.DSM_ADVISOR);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

	public ValidationResults executeAssignmentRules(final Claim claim) {
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);

        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeAssignmentRuleGroup(PROCESSOR_ROUTING_RULE_CATEGORY, context, resultMap, Role.PROCESSOR);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    public ValidationResults executeRecoveryAssignmentRules(final RecoveryClaim recoveryClaim) {
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("recoveryClaim", recoveryClaim);
        context.put("claim", recoveryClaim.getClaim());
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);

        // assuming only one rule category for the processor routing
        // rules. Else
        // make it a list.
        return executeAssignmetRuleCategory(REC_PROCESSOR_ROUTING_RULE_CATEGORY, context, resultMap);
    }

    public ValidationResults executeClaimEntryValidationRules(final Claim claim) {
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);

        // assuming only one rule category for the entry level
        // validations. Else
        // make it a list.
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeRuleCategory(ENTRY_VALIDATION_RULE_CATEGORY, context, resultMap);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    public ValidationResults executeProcessorAuthorityRules(Claim claim, User processor){
    	final Map<String, Object> context = new HashMap<String, Object>();
        context.put("claim", claim);
        context.put("processor", processor);
        final Map<String, Object> resultMap = new HashMap<String, Object>(1);

        // assuming only one rule category for the processor authority
        // rules. Else
        // make it a list.
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            return executeAuthorityRuleCategory(PROCESSOR_AUTHORITY_RULE_CATEGORY, context, resultMap);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    @SuppressWarnings("unchecked")
    public String executeClaimAutoProcessingRules(final Claim claim) {
        final Map<String, Object> context = new HashMap<String, Object>();
        //Added for NMHGSLMS-576: RTM-156, 682
        claim.setManualReviewConfigured(isAuditClaim(claim));
        context.put("claim", claim);

        final Map<String, Object> resultMap = new HashMap<String, Object>(1);
        RejectionReason defaultRejectionReason=(RejectionReason)configParamService.getListOfValues(
                ConfigName.DEFAULT_REJECTION_REASON.getName()).get(0);

        ValidationResults validationResults = null;
        Claim aClaim = (Claim) context.get("claim");

        /*
         * Resubmitted claim shuould always be forced for manual review. It
         * basically only needs to run through the assignment rules and not the
         * processing rules. Incase the resubmit was earlier automatically
         * processed, with the same set of rules running, the claim would never
         * reach a processor. Hence forcing a manual review.
         */
        if(ClaimState.FORWARDED.equals(aClaim.getState())){
        	aClaim.getRuleFailures().clear();
        }
        if (isReopenedOrAppealedClaim(aClaim)) {
            aClaim.getRuleFailures().clear();
            if(forManualReview(aClaim)){
            	return CLAIM_STATE_MANUAL_REVIEW;
            }else{
            	return "on hold for part return";
            }
        }

        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            validationResults = executeRuleGroupCategory(CLAIM_PROCESSING_RULE_CATEGORY, context, resultMap);
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
        List<String> failedRules = validationResults.getErrors();
        //list for failure msq in en_US if claim gets rejected
        List<String> defaultFailedRulesMsgs = validationResults.getDefaultErrorMsgInUS();
        int indexOfdefaultFailedRulesMsgs = 0;

        if (failedRules.size() > 0) {
            RuleFailure ruleFailure = new RuleFailure(CLAIM_PROCESSING_RULE_CATEGORY);
            Boolean flag = false; // has to be removed once rule priority will come into the picture
            for (String failedRule : failedRules) {
                if (failedRule != null && !"".equals(failedRule)) {

                    FailedRuleDetail ruleDetail = new FailedRuleDetail();
                    int pos1 = failedRule.indexOf(")", 1);
                    int pos2 = failedRule.lastIndexOf("[");
                    if (pos2 > -1) {
                        ruleDetail.setRuleMsg(failedRule.substring(pos1 + 1, pos2));
                        if (defaultFailedRulesMsgs != null && indexOfdefaultFailedRulesMsgs < defaultFailedRulesMsgs.size())
                            ruleDetail.setDefaultRuleMsgInUS(defaultFailedRulesMsgs.get(indexOfdefaultFailedRulesMsgs));
                    }
                    ruleDetail.setRuleNumber(failedRule.substring(1, pos1));
                    ruleDetail.setRuleAction(failedRule.substring(pos2 + 1, failedRule.length() - 1));

                    if ("Reject Claim".equals(ruleDetail.getRuleAction()) && !flag) {
                        flag = true;
                        List<DomainRule> domainRule = findRulesByRuleNumber(ruleDetail.getRuleNumber());
                        List<RejectionReason> tempRejectionList = new ArrayList<RejectionReason>();
                        for (DomainRule rule : domainRule) {
                            if (rule.getRejectionReason() != null) {
                                RejectionReason rejectionReason = (RejectionReason)
                                        lovRepository.findById("RejectionReason", rule.getRejectionReason().getId());                                
                                tempRejectionList.add(rejectionReason);
                                aClaim.setRejectionReasons(tempRejectionList);
                            }else{
                                aClaim.setRejectionReasons(tempRejectionList);
                            }
                        }
                    }else{
                    	if(!flag){ //if not rejected, then set rejection reason to null
                        	aClaim.setRejectionReasons(null); 
                    	}
                    }
                    ruleFailure.getFailedRules().add(ruleDetail);
                    indexOfdefaultFailedRulesMsgs++;
                }
            }
            aClaim.addRuleFailure(ruleFailure);
        }
        
        String claimStateToReturn = (String) resultMap.get(CLAIM_STATE_KEY);

        if (aClaim.isOfType(ClaimType.CAMPAIGN) && !claimRejected(claimStateToReturn) && !aClaim.getManualReviewConfigured()) {
            if (!campaignClaimValidationService.isClaimWithinCampaignLimits(aClaim)) {
                return CLAIM_STATE_MANUAL_REVIEW;
            }
        }

        if (!CLAIM_STATE_MANUAL_REVIEW.equalsIgnoreCase(claimStateToReturn) && !FORWARD.equalsIgnoreCase(claimStateToReturn) &&
        		manualReviewRequiredBasedOnMiscPartThresholdQty(aClaim))
        	return CLAIM_STATE_MANUAL_REVIEW;
        
        if(claimStateToReturn == null) {
            claim.setAccountabilityCode((AccountabilityCode)this.configParamService
            		.getListOfValues("default" + ListOfValuesType.AccountabilityCode.name()).get(0));
            claim.setAcceptanceReason((AcceptanceReason)this.configParamService
            		.getListOfValues("default" + ListOfValuesType.AcceptanceReason.name()).get(0));
        }

        return claimStateToReturn;
    }

	private boolean manualReviewRequiredBasedOnMiscPartThresholdQty(Claim claim) {
		Boolean isMiscPartThresholdQty = Boolean.FALSE;

		if (claim!=null && claim.getServiceInformation() != null
				&& claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced() != null
				&& !claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced().isEmpty())
		{
			for (Iterator<NonOEMPartReplaced> iterator = claim.getServiceInformation().getServiceDetail()
							.getMiscPartsReplaced().iterator(); iterator.hasNext();) {
				NonOEMPartReplaced miscPartsReplaced = (NonOEMPartReplaced) iterator.next();
				if (miscPartsReplaced!=null && miscPartsReplaced.getMiscItemConfig()!=null)
				{
					// If the configured Treshold Quantity is less than the quantity replaced by 
					// dealer then we should force the claim for 'Manual Review'.
					if (miscPartsReplaced.getMiscItemConfig().getTresholdQuantity() 
							< miscPartsReplaced.getNumberOfUnits())
						return Boolean.TRUE;
				}
			}
		}
		return isMiscPartThresholdQty;
	}

	private boolean isReopenedOrAppealedClaim(Claim aClaim) {
		return ClaimState.APPEALED.equals(aClaim.getState()) || ClaimState.REOPENED.equals(aClaim.getState()) ||
                Boolean.TRUE.equals(aClaim.getAppealed()) || Boolean.TRUE.equals(aClaim.getReopened());
	}

 public boolean forManualReview(Claim claim) {
		List<OEMPartReplaced> parts = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
		for (OEMPartReplaced part : parts) {
			List<PartReturn> partReturns = part.getPartReturns();
			if (partReturns == null || partReturns.size() == 0) {
				continue;
			}
			for (PartReturn partReturn : partReturns) {
				InspectionResult inspectionResult = partReturn
						.getInspectionResult();
				if (inspectionResult == null
						&& !PartReturnStatus.CANNOT_BE_SHIPPED
								.equals(partReturn.getStatus())) {
					return false;
				}
			}
		}
		return true;
	}

    private boolean claimRejected(String claimStateToReturn) {
        return ("rejected".equals(claimStateToReturn));
    }

    public PageResult<DomainRule> findAllRulesInContext(String context, PageSpecification pageSpecification) {
        return domainRuleRepository.findAllInContext(context, doesContextUseRuleGroup(context), pageSpecification);
    }

    private ValidationResults executeRuleCategory(final String ruleCategory,
            final Map<String, Object> context, final Map<String, Object> resultMap) {
        // validationResults.errors is used to store all error priority rule failures for both the entry validation
        // rules categories as well as Claim Rules categories. validationResults.warnings is used to store only
        // warning priority rule failures for entry validation rules categories only.
        final ValidationResults validationResults = new ValidationResults();
        Claim claim = (Claim)context.get("claim");
          SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
          ruleExecutionTemplate.executeRules(new RuleExecutionCallback(context, resultMap) {
            public List<DomainRule> getRulesForExecution() {
                return findRulesByContext(ruleCategory);
            }

            public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
                generateValidationResults(results, validationResults);
            }
        });

        return validationResults;
    }

    private ValidationResults executeRuleGroupCategory(final String ruleContext, final Map<String, Object> context,
                                                  final Map<String, Object> resultMap) {
        // validationResults.errors is used to store all error priority rule failures for both the entry validation
        // rules categories as well as Claim Rules categories. validationResults.warnings is used to store only
        // warning priority rule failures for entry validation rules categories only.
        final ValidationResults validationResults = new ValidationResults();
        //TKTSA-1162 Added BU filtering based on the claim BU
        Claim claim = (Claim)context.get("claim");
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());

        this.ruleExecutionTemplate.executeRuleGroups(new RuleGroupExecutionCallback(context, resultMap) {
            public List<DomainRuleGroup> getRuleGroupsForExecution() {
                return ruleGroupAdministrationService.findRuleGroupsForContext(ruleContext);
            }

            public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
                generateValidationResults(results, validationResults);
            }
        });

        return validationResults;
    }

    private void generateValidationResults(Map<DomainRule, Map<Boolean, String>> results,
                                           ValidationResults validationResults) {
        if (!results.isEmpty()) {
            for (Map.Entry<DomainRule, Map<Boolean, String>> entry : results.entrySet()) {
                Map<Boolean, String> clmFailedMsgMap = entry.getValue();
                Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator().next();
                if (clmFailedCheck) { //
                    if (entry.getKey() != null && entry.getKey().getRuleAudits() != null &&
                            !entry.getKey().getRuleAudits().isEmpty() &&
                            entry.getKey().getRuleAudits().iterator().next() != null &&
                            WARNED_RULES_STATE.equals(entry.getKey().getAction().getState())) {
                        validationResults.addWarningMessage(clmFailedMsgMap.get(clmFailedCheck));
                    } else { // this includes the "error" state of entry validation rules as well.
                        validationResults.addErrorMessage(clmFailedMsgMap.get(clmFailedCheck));
                        validationResults.addDefalutErrorMsgInUS(entry.getKey().getFailureMessageInUS());
                    }
                }
            }
        }
    }

    private ValidationResults executeAssignmentRuleGroup(final String ruleCategory,
            final Map<String, Object> context, final Map<String, Object> resultMap,final String assignableRole) {
    	final ValidationResults validationResults = new ValidationResults();
      final  Claim claim = (Claim)context.get("claim");     
          SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        ruleExecutionTemplate.executeAssignmentRuleGroups(
        		new AssignmentRuleGroupExecutionCallback(context,resultMap,assignableRole) {
        	public List<DomainRuleGroup> getRuleGroupsForExecution() {
                return ruleGroupAdministrationService.findRuleGroupsForContext(ruleCategory);
            }

            public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
                if (!results.isEmpty()) {
                	Set<UserCluster> assignedToList = new HashSet<UserCluster>();
                    Set<UserCluster> notAssignedToList = new HashSet<UserCluster>();
                    LimitOfAuthorityScheme assignedToLOAScheme = null;
                    for (Map.Entry<DomainRule, Map<Boolean, String>> entry : results.entrySet()) {
                        Map<Boolean, String> clmFailedMsgMap = entry.getValue();
                        Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator().next();
                        if (clmFailedCheck) {
                            AssignmentRuleAction action = (AssignmentRuleAction) entry.getKey()
                                    .getAction();
                            if (action.getState().equals(RULE_ADMIN_ASSIGN_RULES_STATE)) {
                                assignedToList.add(action.getUserCluster());
                            } else if (action.getState().equals(RULE_ADMIN_ASSIGN_TO_LOA_SCHEME)) {
                            	assignedToLOAScheme=action.getLoaScheme();
                            	claim.setLoaScheme(action.getLoaScheme());
                            } else {
                                notAssignedToList.add(action.getUserCluster());
                            }
                        }
                    }
                    setValidationResults(assignedToList,notAssignedToList,assignedToLOAScheme,validationResults);
                } else {
                	setLastExecutionSuccessful(false);
                }
            }

			@Override
			public int getAssignableCount() {
				Set<String> assignedTo = validationResults.getAssignStateToUserGroupMap().get(RULE_ADMIN_ASSIGN_RULES_STATE);
				Set<String> assignedToLOA = validationResults.getAssignStateToUserGroupMap().get(RULE_ADMIN_ASSIGN_TO_LOA_SCHEME);
				if(assignedToLOA == null || assignedToLOA.size() == 0){
					if(assignedTo == null || assignedTo.size() ==0){
						return 0;
					} else {
						return assignedTo.size();
					}
				} else {
					return assignedToLOA.size();
				}
			}
        });
        return validationResults;
    }
    
    
    private ValidationResults executeAssignmetRuleCategory(final String ruleCategory,
            final Map<String, Object> context, final Map<String, Object> resultMap) {
        final ValidationResults validationResults = new ValidationResults();
        Claim claim = (Claim)context.get("claim");
          SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
             ruleExecutionTemplate.executeRules(new RuleExecutionCallback(context,resultMap) {
            public List<DomainRule> getRulesForExecution() {
                return findRulesByContext(ruleCategory);
            }

            public Map<String, Object> getTransactionContext() {
                return context;
            }

            public Map<String, Object> getActionResultHolder() {
                return resultMap;
            }

            // TODO: Needs refactoring !!!
            public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
                if (!results.isEmpty()) {
                    Set<UserCluster> assignedToList = new HashSet<UserCluster>();
                    Set<UserCluster> notAssignedToList = new HashSet<UserCluster>();
                    for (Map.Entry<DomainRule, Map<Boolean, String>> entry : results.entrySet()) {
                        Map<Boolean, String> clmFailedMsgMap = entry.getValue();
                        Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator().next();
                        if (clmFailedCheck) {
                            AssignmentRuleAction action = (AssignmentRuleAction) entry.getKey()
                                    .getAction();
                            if (action.getState().equals(RULE_ADMIN_ASSIGN_RULES_STATE)) {
                                assignedToList.add(action.getUserCluster());
                            } else {
                                notAssignedToList.add(action.getUserCluster());
                            }
                        }
                    }
                    validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_ASSIGN_RULES_STATE,
                            assignedToList);
                    validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_NOT_ASSIGN_RULES_STATE,
                            notAssignedToList);
                }
            }
        });
        return validationResults;
    }

    private ValidationResults executeAuthorityRuleCategory(final String ruleCategory,
            final Map<String, Object> context, final Map<String, Object> resultMap) {
        final ValidationResults validationResults = new ValidationResults();
        Claim claim = (Claim)context.get("claim");
          SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        ruleExecutionTemplate.executeRules(new RuleExecutionCallback(context,resultMap) {
            public List<DomainRule> getRulesForExecution() {
            	/*List<DomainRule> rules = findRulesByContext(ruleCategory);
            	List<DomainRule> processorRules = new ArrayList<DomainRule>();
            	User processor = (User)context.get("processor");
            	for(DomainRule rule : rules) {
            		UserCluster cluster = ((AssignmentRuleAction)rule.getAction()).getUserCluster();
            		if(cluster.getIncludedUsers().contains(processor))
            			processorRules.add(rule);
            	}
                return processorRules;*/
            	return findProcessorAuthorityRules(ruleCategory, (User)context.get("processor"));
            }

            public Map<String, Object> getTransactionContext() {
                return context;
            }

            public Map<String, Object> getActionResultHolder() {
                return resultMap;
            }

            @SuppressWarnings("unchecked")
			public void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results) {
                if (!results.isEmpty()) {
                    Set<UserCluster> assignedToList = new HashSet<UserCluster>();
                    Set<String> topAllowedActions = new HashSet<String>(10);
                    int intersectIter = 0;
                    for (Map.Entry<DomainRule, Map<Boolean, String>> entry : results.entrySet()) {
                        Map<Boolean, String> clmAuthorizedActionsRuleMap = entry.getValue();
                        Boolean clmAuthorizedActionsRuleCheck = clmAuthorizedActionsRuleMap.keySet().iterator().next();
                        validationResults.addWarningMessage(clmAuthorizedActionsRuleMap.get(clmAuthorizedActionsRuleCheck));
                        if (clmAuthorizedActionsRuleCheck) {
                            AssignmentRuleAction action = (AssignmentRuleAction) entry.getKey().getAction();
                            assignedToList.add(action.getUserCluster());
                            // Actions should be intersected ones
                            if (intersectIter>0)
                            	topAllowedActions = new HashSet(CollectionUtils.intersection(topAllowedActions,
                            			trimSpacesFromList(Arrays.asList(action.getState().split(",")))));
                            else
                            	topAllowedActions = trimSpacesFromList(Arrays.asList(action.getState().split(",")));
                            intersectIter++;
                        }
                    }
                    Claim claim = (Claim) context.get("claim");
                    claim.addAllowedActionsList(topAllowedActions);
                    validationResults.getAssignStateToUserGroupMap().put(RULE_ADMIN_ASSIGN_RULES_STATE,
                            assignedToList);
                }
            }

        });
        return validationResults;
    }

	private Set<String> trimSpacesFromList(List<String> listToTrim) {
		if (listToTrim==null || listToTrim.isEmpty())
			return new HashSet<String>();
		Set<String> trimmedList = new HashSet<String>(listToTrim.size());
		for (Iterator<String> iter = listToTrim.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if (StringUtils.hasText(element))
				trimmedList.add(element.trim());
		}
		return trimmedList;
	}

	public DomainRuleAction findDomainRuleActionById(Long id) {
        return domainRuleActionRepository.findById(id);
    }

    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(
            String userGroupName, String result) {
        return assignmentRuleActionRepository.findAssignmentRuleActionByUserGroupNameAndState(
                userGroupName, result);
    }
    
    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(
    		String userGroupName, String result, String context) {
        return assignmentRuleActionRepository.findAssignmentRuleActionByUserGroupNameAndState(
                userGroupName, result,context);
    }
    
    public AssignmentRuleAction findAssignmentRuleActionByLOASchemeStateAndContext(String loaScheme, String result,String context) {
        return assignmentRuleActionRepository.findAssignmentRuleActionByLOASchemeStateAndContext(loaScheme, result,context);
    }

    public AssignmentRuleAction findAssignmentRuleActionById(Long id) {
        return assignmentRuleActionRepository.findById(id);
    }

    public void setCampaignClaimValidationService(
            CampaignClaimValidationService campaignClaimValidationService) {
        this.campaignClaimValidationService = campaignClaimValidationService;
    }

    void setAuditForRule(DomainRule domainRule,DomainRuleAudit audit) {
		if (null == domainRule.getRejectionReason()) {
			audit.setRuleSnapshotAsString(ruleXMLConverter
					.convertObjectToXML(domainRule));
		}
        audit.setCreatedBy(securityHelper.getLoggedInUser());
        audit.getD().setInternalComments(domainRule.getD().getInternalComments());
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        audit.setCreatedOn(Clock.now());
        audit.setCreatedTime(new Date());
        domainRule.getRuleAudits().add(audit);
    }

    public void saveRule(DomainRule domainRule,DomainRuleAudit ruleAudit) {
        setAuditForRule(domainRule,ruleAudit);
        domainRule.updateOgnlExpression();
        domainRuleRepository.save(domainRule);
    }

    public void updateRule(DomainRule domainRule,DomainRuleAudit ruleAudit) {
        setAuditForRule(domainRule,ruleAudit);
        domainRule.updateOgnlExpression();
        domainRuleRepository.update(domainRule);
    }

    public List<DomainRule> findRulesByRuleNumber(String number) {
        return domainRuleRepository.findByRuleNumber(new Integer(number));
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public PageResult<DomainRule> findAllRulesInContext(String context, ListCriteria listCriteria) {
        return domainRuleRepository.findAllInContext(context, listCriteria, doesContextUseRuleGroup(context));
    }

    public boolean doesContextUseRuleGroup(String context) {
        return CONTEXTS_USING_RULE_GROUP.contains(context);
    }

    public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void updateRule(DomainRule domainRule) {
		domainRuleRepository.update(domainRule);
	}

    public void setRuleGroupAdministrationService(RuleGroupAdministrationService ruleGroupAdministrationService) {
        this.ruleGroupAdministrationService = ruleGroupAdministrationService;
    }

	public MiscellaneousItemConfigService getMiscellaneousItemConfigService() {
		return miscellaneousItemConfigService;
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }
    
    
	public boolean processingRuleFailsAfterPartOff(Claim claim) {
		boolean ruleFails = false;
		List<String> ruleNumbers = null;
		ValidationResults validationResults = null;
		if (!CollectionUtils.isEmpty(claim.getRuleFailures())) {
			ruleNumbers = new ArrayList<String>();
			for (RuleFailure ruleFailure : claim.getRuleFailures()) {
				for (FailedRuleDetail failureDetail : ruleFailure.getFailedRules()) {
					ruleNumbers.add(failureDetail.getRuleNumber());
				}
			}
		}
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("claim", claim);
		final Map<String, Object> resultMap = new HashMap<String, Object>(1);
		try {
			GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
			validationResults = executeRuleGroupCategory(CLAIM_PROCESSING_RULE_CATEGORY, context, resultMap);
		} finally {
			GlobalConfiguration.getInstance().removeClaimCurrency();//need to understand why this ishere.
		}
		List<String> failedRules = validationResults.getErrors();
		for (String failedRule : failedRules) {
			int pos1 = failedRule.indexOf(")", 1);
			String ruleNumber = failedRule.substring(1, pos1);
			if (ruleNumbers != null && !ruleNumbers.contains(ruleNumber)) {
				return true;
			} else if (ruleNumbers == null) {
				return true;
			}
		}
 
		return ruleFails;
	}
	
	/**
	 * @param claim
	 * @return
	 * This method is for finding X number of claim
	 * Added for NMHGSLMS-576: RTM-156, 682
	 */
	private Boolean isAuditClaim(Claim claim) {
		if (claim != null && claim.getClaimNumber() != null
				&& !claim.getClaimNumber().isEmpty()) {
			int manualRevCount = configParamService.getLongValue(
					ConfigName.FLAG_FOR_MANUAL_REVIEW_CLAIM.getName())
					.intValue();
			String number = claim.getClaimNumber().substring(1);
			if (manualRevCount != 0) {
				if (Integer.parseInt(number) % manualRevCount == 0) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}

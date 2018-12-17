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
 * Time: 7:53:37 PM
 */

package tavant.twms.domain.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 */
@Transactional(readOnly = true)
public interface RuleAdministrationService extends
        GenericService<DomainRule, Long, RuleAdministrationException> {

    public static final List<String> CONTEXTS_USING_RULE_GROUP = new ArrayList<String>() {
        {
            add(CLAIM_PROCESSING_RULE_CATEGORY);
            add(PROCESSOR_ROUTING_RULE_CATEGORY);
            add(DSM_ROUTING_RULE_CATEGORY);
            add(DSM_ADVISOR_ROUTING_RULE_CATEGORY);
        }
    };
    
    public List<DomainRule> findRulesByName(String name, String forRuleEditorContext);

    public List<DomainRule> findRulesByContext(String category);

    public List<DomainRuleAction> findAllDomainRuleActions();

    public DomainRuleAction findDomainRuleActionById(Long id);

    public AssignmentRuleAction findAssignmentRuleActionById(Long id);

    public List<DomainRuleAction> findDomainRuleActionsByName(String actionName);

    public List<DomainRuleAction> findDomainRuleActionsByContext(String context);

    public void saveDomainRuleAction(DomainRuleAction domainRuleAction);

    public ValidationResults executeClaimEntryValidationRules(Claim claim);

    public String executeClaimAutoProcessingRules(Claim claim);

    public ValidationResults executeAssignmentRules(Claim claim);
    
    public ValidationResults executeDSMRules(Claim claim);
    
    public ValidationResults executeDSMAdvisorRules(Claim claim);
    
    public ValidationResults executeCPAdvisorRules(Claim claim);

    public ValidationResults executeRecoveryAssignmentRules(RecoveryClaim recoveryClaim);
    
    public ValidationResults executeProcessorAuthorityRules(Claim claim, User processor);
   
    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(
            String userGroupName, String result);
    
    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(
            String userGroupName, String result, String context);

    public static final String CLAIM_PROCESSING_RULE_CATEGORY ="ClaimRules";

    public static final String ENTRY_VALIDATION_RULE_CATEGORY = "EntryValidationRules";

    public static final String PROCESSOR_ROUTING_RULE_CATEGORY = "ClaimProcessorRouting";
    
    public static final String REC_PROCESSOR_ROUTING_RULE_CATEGORY = "recClaimRouting";
    
    public static final String DSM_ROUTING_RULE_CATEGORY = "DSMRouting";
    
    public static final String DSM_ADVISOR_ROUTING_RULE_CATEGORY = "DSMAdvisorRouting";

    public static final String PROCESSOR_AUTHORITY_RULE_CATEGORY = "ProcessorAuthority";
       
    public static final String CP_ADVISOR_ROUTING_RULE_CATEGORY = "CPAdvisorRouting";
    
    public static final String WARNED_RULES_STATE = "warning";

    public static final String CLAIM_STATE_MANUAL_REVIEW = "manual review";
    
    public static final String FORWARD="Forwarded";

    public PageResult<DomainRule> findAllRulesInContext(String context,
            PageSpecification pageSpecification);

    @Transactional(readOnly = false)
    public void saveRule(DomainRule domainRule,DomainRuleAudit ruleAudit);

    @Transactional(readOnly = false)
    public void updateRule(DomainRule domainRule);

    @Transactional(readOnly = false)
    public void updateRule(DomainRule domainRule,DomainRuleAudit ruleAudit);

    public List<DomainRule> findRulesByRuleNumber(String number);
    
    public PageResult<DomainRule> findAllRulesInContext(String context,
            ListCriteria listCriteria);

    public boolean doesContextUseRuleGroup(String context);
    
    public AssignmentRuleAction findAssignmentRuleActionByLOASchemeStateAndContext(String loaScheme, String result,String context);
    
    public boolean processingRuleFailsAfterPartOff(Claim claim);
    
}

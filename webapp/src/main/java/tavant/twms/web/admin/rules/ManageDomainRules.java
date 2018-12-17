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

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.I18NDomainRuleDescription;
import tavant.twms.domain.catalog.I18NDomainRuleText;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.DomainRuleAction;
import tavant.twms.domain.rules.DomainRuleAudit;
import tavant.twms.domain.rules.DomainRuleRepository;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.RuleAdministrationException;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.domain.rules.group.RuleGroupAdministrationService;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author aniruddha.chaturvedi
 */
@SuppressWarnings("serial") 
public class ManageDomainRules extends I18nActionSupport implements Preparable,
        Validateable {
	private static Logger logger = Logger.getLogger(ManageDomainRules.class);
    private String id;
    protected DomainRule rule;
    private RuleAdministrationService ruleAdministrationService;
    private PredicateAdministrationService predicateAdministrationService;
    private DomainRuleRepository domainRuleRepository;
	private ProductLocaleService productLocaleService;
    private List<DomainRuleAction> actions;
    protected DomainRuleAction action;
    protected DomainRuleGroup ruleGroup;
    protected String actionId;
    private List<DomainRuleGroup> ruleGroupsInContext;
    private RuleGroupAdministrationService ruleGroupAdministrationService;
    private String context="ClaimRules";
    private List<DomainRule> domainRules;
    private List<ListOfValues> listOfRejectionReasons;
    private Long rejectedReason;
    private LovRepository lovRepository;
    private String selectedAction;
    private String ruleId;
    protected DomainRuleAudit ruleAudit;
    private String activeFlag;
    private String ruleAuditName;
    private String ruleAuditFailureMessage;
    
    public boolean doesContextUseRuleGroup() {
        return ruleAdministrationService.doesContextUseRuleGroup(context);
    }

    public String showDomainRule() {
		ruleId = String.valueOf(rule.getId());
		return SUCCESS;
	}
	
    public String updateDomainRule() throws RuleAdministrationException {
    	DomainRuleAudit newRuleAudit = new DomainRuleAudit();
    	newRuleAudit.setStatus(activeFlag);    	
    	
        if(doesContextUseRuleGroup()) {
            rule.setRuleGroup(ruleGroup);            
        }

        // action already set in the prepare method.
        newRuleAudit.setAction(action);
    	
    	/**
    	 * This is being used because the trigger is for updating domain rule from
    	 * domain rule audit "update_domain_rule_status" is being fired late
    	 */
    	rule.setStatus(this.activeFlag);    	
    	return saveI18NMessages(newRuleAudit);
    }

    public String populateDomainRuleOgnlExpression() throws RuleAdministrationException {
        domainRules = ruleAdministrationService.findAll();
        for (DomainRule domainRule : domainRules) {
            if (domainRule.getOgnlExpression() == null && domainRule.getPredicate().getPredicateAsXML() != null) {
                domainRule.updateOgnlExpression();
                ruleAdministrationService.update(domainRule);
            }
        }
        return SUCCESS;
    }
    

    private String saveI18NMessages(DomainRuleAudit newRuleAudit) {
		/*prepareDomainRuleText();*/
		if(hasActionErrors()) {
			return INPUT;
		}
		newRuleAudit.setName(getRuleAuditName());
		newRuleAudit.setFailureMessage(getRuleAuditFailureMessage());
		prepareDomainRuleTextForEnLocale(newRuleAudit);
		ruleAdministrationService.updateRule(rule, newRuleAudit);
		rule = ruleAdministrationService.findById(Long.parseLong(ruleId));
    	selectedAction = rule.getAction().getName();
    	setLastestRuleAudit();
		addActionMessage("message.manageBusinessRule.updateSuccess");		
        return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private void prepareDomainRuleText() {
		List<ProductLocale> locales = productLocaleService.findAll();
		rule.getDomainRuleTexts().clear();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			I18NDomainRuleText domainRuleText = new I18NDomainRuleText();
			domainRuleText.setFailureDescription(ruleAudit.getFailureMessage());
			domainRuleText.setLocale(i18nLocale);
			rule.getDomainRuleTexts().add(domainRuleText);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prepareDomainRuleDesc() {
		List<ProductLocale> locales = productLocaleService.findAll();
		rule.getDomainRuleDesc().clear();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			I18NDomainRuleDescription domainRuleDesc = new I18NDomainRuleDescription();
			domainRuleDesc.setRuleDescription(ruleAudit.getName());
			domainRuleDesc.setLocale(i18nLocale);
			rule.getDomainRuleDesc().add(domainRuleDesc);
		}
	}

	private void prepareDomainRuleTextForEnLocale(DomainRuleAudit newRuleAudit) {
	   
	    for (I18NDomainRuleText I18NDomainRuleText : rule.getDomainRuleTexts()) {
		if(I18NDomainRuleText.getLocale().equalsIgnoreCase("en_US")){
		       I18NDomainRuleText.setFailureDescription(newRuleAudit.getFailureMessage());
		     
		       }		
	    }
	}
	
	private void prepareDomainRuleDescForEnLocale(DomainRuleAudit newRuleAudit) {
		   
	    for (I18NDomainRuleDescription I18NDomainRuleDesc : rule.getDomainRuleDesc()) {
		if(I18NDomainRuleDesc.getLocale().equalsIgnoreCase("en_US")){
		       I18NDomainRuleDesc.setRuleDescription(newRuleAudit.getName());
		       
		 }		
	    }
	}

    public void prepare() throws Exception {
        Long idToBeUsed = null;
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if (rule != null && rule.getId() != null) {
            idToBeUsed = rule.getId();
        }

        if (idToBeUsed != null) {
            rule = ruleAdministrationService.findById(idToBeUsed);
        }
        selectedAction = rule.getAction().getName();
        setLastestRuleAudit();
        // the fetchAndSetActions needs to be called after the rule is set. It is used in the 
        // child class.
        fetchAndSetActions();
        fetchAllRejectionReason();

        if(rule != null && rule.getRuleGroup() != null) {
            ruleGroupsInContext =
                    ruleGroupAdministrationService.findRuleGroupsForContext(rule.getRuleGroup().getContext());
        }
    }

    private void setLastestRuleAudit() {
    	this.ruleAudit = rule.getRuleAudits().get(rule.getRuleAudits().size() - 1);
    }
    // Need to be overridden in child classes.
    protected void fetchAndSetActions() {
    	if(rule!=null) {
            DomainRuleGroup ruleGroup = rule.getRuleGroup();
            String context = ruleGroup != null ? ruleGroup.getContext() : rule.getContext();
            actions = ruleAdministrationService.findDomainRuleActionsByContext(context);
        } else {
    		actions=ruleAdministrationService.findAllDomainRuleActions();
        }

        if (actionId != null) {
        	this.selectedAction = getActionName(actions, actionId);
            action = ruleAdministrationService.findDomainRuleActionById(Long.parseLong(actionId));
			if ("Reject Claim".equals(action.getName())) {
				RejectionReason rejectionReason = fetchRejectionReason();
				rule.setRejectionReason(rejectionReason);
			} else {
				rule.setRejectionReason(null);
			}
        }    	
    }

    private String getActionName(List<DomainRuleAction> actions, String actionIdStr){
    	String name = "";
    	if(actions != null && actions.size() > 0){
    		for(DomainRuleAction action : actions){
    			
    			if(action.getId().equals(Long.valueOf(actionIdStr))){
    				name = action.getName();
    				break;
    			}
    		}
    	}
    	return name;
    }
    
    protected void fetchAllRejectionReason() {
        listOfRejectionReasons = lovRepository.findAllActive("RejectionReason");
    }
    
    @Override
    public void validate() {
        if (!StringUtils.hasText(getRuleAuditName())) {
            addActionError("error.manageBusinessRule.nameRequired");
        }

        if (!StringUtils.hasText(getRuleAuditFailureMessage())) {
            addActionError("error.manageBusinessRule.failureMessageRequired");
        }
        
        if (!StringUtils.hasText(rule.getD().getInternalComments())) {
            addActionError("error.manageBusinessRule.commentRequired");
        }
        if(hasActionErrors()){
        	ruleAudit.setName(getRuleAuditName());
        	ruleAudit.setFailureMessage(getRuleAuditFailureMessage());
        }
    }

    public void setRuleAdministrationService(RuleAdministrationService ruleAdministrationService) {
        this.ruleAdministrationService = ruleAdministrationService;
    }
    
	public RuleAdministrationService getRuleAdministrationService() {
		return ruleAdministrationService;
	}
    
    public DomainRule getRule() {
        return rule;
    }

    public void setRule(DomainRule rule) {
        this.rule = rule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DomainRuleAction getAction() {
        return action;
    }

    public void setAction(DomainRuleAction action) {
        this.action = action;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public List<DomainRuleAction> getActions() {
        return actions;
    }

    public void setActions(List<DomainRuleAction> actions) {
        this.actions = actions;
    }

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public List<ListOfValues> getListOfRejectionReasons() {
		return listOfRejectionReasons;
	}

	public void setListOfRejectionReasons(List<ListOfValues> listOfRejectionReasons) {
		this.listOfRejectionReasons = listOfRejectionReasons;
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return predicateAdministrationService;
	}

	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}
	
	private RejectionReason fetchRejectionReason(){
        if (rejectedReason != null) {
            return (RejectionReason) lovRepository.findById("RejectionReason", rejectedReason);
        } else {
            return null;
        }
    }

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public Long getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(Long rejectedReason) {
		this.rejectedReason = rejectedReason;
	}
	
	public DomainRuleAudit getRuleAudit() {
		return ruleAudit;
	}

	public void setRuleAudit(DomainRuleAudit ruleAudit) {
		this.ruleAudit = ruleAudit;
	}
	
	 public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}

	public DomainRuleRepository getDomainRuleRepository() {
		return domainRuleRepository;
	}

	public void setDomainRuleRepository(DomainRuleRepository domainRuleRepository) {
		this.domainRuleRepository = domainRuleRepository;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}


    public List<DomainRuleGroup> getRuleGroupsInContext() {
        return ruleGroupsInContext;
    }

    public void setRuleGroupsInContext(List<DomainRuleGroup> ruleGroupsInContext) {
        this.ruleGroupsInContext = ruleGroupsInContext;
    }

    public void setRuleGroupAdministrationService(RuleGroupAdministrationService ruleGroupAdministrationService) {
        this.ruleGroupAdministrationService = ruleGroupAdministrationService;
    }

    public DomainRuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(DomainRuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

    public List<DomainRule> getDomainRules() {
        return domainRules;
    }

	public String getRuleAuditName() {
		return ruleAuditName;
	}

	public void setRuleAuditName(String ruleAuditName) {
		this.ruleAuditName = ruleAuditName;
	}

	public String getRuleAuditFailureMessage() {
		return ruleAuditFailureMessage;
	}

	public void setRuleAuditFailureMessage(String ruleAuditFailureMessage) {
		this.ruleAuditFailureMessage = ruleAuditFailureMessage;
	}

}

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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.I18NDomainRuleDescription;
import tavant.twms.domain.catalog.I18NDomainRuleText;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.rules.*;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.domain.rules.group.RuleGroupAdministrationService;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 * @author shraddha.nanda
 * 
 */
@SuppressWarnings("serial")
public class CreateDomainRule extends I18nActionSupport implements Validateable, Preparable {
	private static Logger logger = Logger.getLogger(CreateDomainRule.class);
	protected String name;
	private String ruleName;
	private String failureMessage;
	protected String context;
	protected List<DomainPredicate> predicates;
	private String id;
	private String ruleId;
	private List<DomainRuleAction> actions;
	private List<ListOfValues> listOfRejectionReasons;
	private List<ProductLocale> locales;
	private PredicateAdministrationService predicateAdministrationService;
	private RuleAdministrationService ruleAdministrationService;
	private ProductLocaleService productLocaleService;
	// Used in the UI to print a helpful message if no matching predicates were
	// found via a search.
	private boolean searchProcessed;
	private String ruleNumber;
	private Long rejectedReason;
	private String selectedAction;
	private LovRepository lovRepository;
	private DomainRule domainRule;
	private DomainRuleRepository domainRuleRepository;
	private I18NDomainRuleText iDomainRuleText;
	private I18NDomainRuleDescription iDomainRuleDescription;
	
	private Set<I18NDomainRuleDescription> i18NRuleDescSet;
	private String locale_I18nDomainRule;
	private Set<I18NDomainRuleText> i18NFailureSet;
	private List<String> localizedFailureMessages;
	private String predicateid;
    protected DomainRuleGroup ruleGroup;
    private RuleGroupAdministrationService ruleGroupAdministrationService;
    private List<DomainRuleGroup> ruleGroupsInContext;

    public DomainRuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(DomainRuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public void setRuleGroupAdministrationService(RuleGroupAdministrationService ruleGroupAdministrationService) {
        this.ruleGroupAdministrationService = ruleGroupAdministrationService;
    }

    public List<DomainRuleGroup> getRuleGroupsInContext() {
        return ruleGroupsInContext;
    }

    public String getPredicateid() {
		return predicateid;
	}

	public void setPredicateid(String predicateid) {
		this.predicateid = predicateid;
	}
	
	public I18NDomainRuleDescription getiDomainRuleDescription() {
		return iDomainRuleDescription;
	}

	public void setiDomainRuleDescription(
			I18NDomainRuleDescription iDomainRuleDescription) {
		this.iDomainRuleDescription = iDomainRuleDescription;
	}


	@SuppressWarnings("unused")
	public String createRule() {
		getId();

        if(doesContextUseRuleGroup() && ruleGroupsInContext.isEmpty()) {
            addActionError(getText("error.manageBusinessRule.noRuleGroupsConfigured"));
            return ERROR;
        }

        return SUCCESS;
	}

    public boolean doesContextUseRuleGroup() {
        return ruleAdministrationService.doesContextUseRuleGroup(context);
    }

    public String setUpForCreateI18NMessages() {
		locales = productLocaleService.findAll();
		domainRule = domainRuleRepository.findById(Long.parseLong(this.ruleId));
		return SUCCESS;
	}

	public String saveI18NMessages() {
		domainRule = domainRuleRepository.findById(Long.parseLong(this.ruleId));
		prepareDomainRuleText();
		if(hasActionErrors()) {
			return INPUT;
		}
		ruleAdministrationService.updateRule(domainRule);
		addActionMessage("message.manageBusinessRule.i18nCreateSuccess");
		return SUCCESS;
	}
	
	
	public String saveI18NRuleDescription() {
		domainRule = domainRuleRepository.findById(Long.parseLong(this.ruleId));
		prepareDomainRuleDesc();
		if(hasActionErrors()) {
			return INPUT;
		}
		ruleAdministrationService.updateRule(domainRule);
		addActionMessage("message.manageBusinessRuleDesc.i18nCreateSuccess");
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private void prepareDomainRuleText() {
		locales = productLocaleService.findAll();
		domainRule.getDomainRuleTexts().clear();
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedFailureMessages_" + locale.getLocale());
			if(localizedMessages[0].equals("")) {
				if(i18nLocale.equals(EN_US)) {
				addActionError("error.manageBusinessRule.failureMessageUS");
				}
			}
			else {
			I18NDomainRuleText domainRuleText = new I18NDomainRuleText();
			domainRuleText.setFailureDescription(localizedMessages[0]);
			domainRuleText.setLocale(i18nLocale);
			domainRule.getDomainRuleTexts().add(domainRuleText);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prepareDomainRuleDesc() {
		locales = productLocaleService.findAll();
		domainRule.getDomainRuleDesc().clear();
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			
			String localizedRuleDesc[] = (String[]) params
					.get("localizedRuleDesc_" + locale.getLocale());
			if(localizedRuleDesc[0].equals("")) {
				if(i18nLocale.equals(EN_US)) {
				addActionError("error.manageBusinessRule.failureMessageUS");
				}
			}
			else {
				I18NDomainRuleDescription domainRuleText = new I18NDomainRuleDescription();
			
			domainRuleText.setRuleDescription(localizedRuleDesc[0]);
			domainRuleText.setLocale(i18nLocale);
			domainRule.getDomainRuleDesc().add(domainRuleText);
			}
		}
	}
	
	
	
	@SuppressWarnings("deprecation")
	public String searchPredicates() {
		actions = ruleAdministrationService
				.findDomainRuleActionsByContext(context);
		
		predicates = predicateAdministrationService
				.findNonSearchPredicatesByName(name);
		
		searchProcessed = true;
		return SUCCESS;
	}

	public String saveRule() {
		DomainRuleAction action = fetchAction();
		DomainRule rule = new DomainRule();
		DomainPredicate pred = predicateAdministrationService.findById(Long.parseLong(id));
		rule.setPredicate(pred);		

        if(doesContextUseRuleGroup()) {
            ruleGroup.setContext(context);
            rule.setRuleGroup(ruleGroup);

            Long rulePriority =
                    ruleGroupAdministrationService.findNextAvailableRulePriorityForRuleGroup(ruleGroup.getId());
            rule.setPriority(rulePriority);
        } else {
            rule.setContext(context);
        }

        if(context.equalsIgnoreCase("ClaimRules"))
		{		    
			if("Reject Claim".equals(action.getName()))
			{
				    RejectionReason rejectionReason= fetchRejectionReason();
					rule.setRejectionReason(rejectionReason);
			}
		}
		rule.setRuleNumber(new Integer(ruleNumber));
		rule.setStatus(DomainRuleAudit.ACTIVE);
		I18NDomainRuleText domainRuleText = new I18NDomainRuleText();
		I18NDomainRuleDescription domainRuleDesc = new I18NDomainRuleDescription();
		domainRuleText.setFailureDescription(failureMessage);
		domainRuleDesc.setRuleDescription(ruleName);
		domainRuleText.setLocale(EN_US);
		domainRuleDesc.setLocale(EN_US);
		rule.getDomainRuleDesc().add(domainRuleDesc);
		rule.getDomainRuleTexts().add(domainRuleText);
		DomainRuleAudit ruleAudit = new DomainRuleAudit();
		ruleAudit.setAction(action);
		ruleAudit.setName(ruleName);
		ruleAudit.setStatus(DomainRuleAudit.ACTIVE);
		ruleAudit.setFailureMessage(failureMessage);
		rule.getRuleAudits().add(ruleAudit);
		ruleAdministrationService.saveRule(rule,ruleAudit);
		addActionMessage("message.manageBusinessRule.createSuccess");
		ruleId = String.valueOf(rule.getId());
		return SUCCESS;
	}

	@Override
	public void validate() {
		if (!StringUtils.hasText(id)) {
			addActionError("error.manageBusinessRule.expressionRequired");
		}

		if (!StringUtils.hasText(ruleName)) {
			addActionError("error.manageBusinessRule.nameRequired");
		}

		if (!StringUtils.hasText(failureMessage)) {
			addActionError("error.manageBusinessRule.failureMessageRequired");
		}

		validateRuleNumber();

		if (hasActionErrors()) {
			setPredicateid(id);
			searchPredicates();
		}
	}

	// Only getters and setters follow..
	public List<DomainRuleAction> getActions() {
		return actions;
	}

	public void setActions(List<DomainRuleAction> actions) {
		this.actions = actions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public List<DomainPredicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<DomainPredicate> predicates) {
		this.predicates = predicates;
	}

	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getRuleNumber() {
		return ruleNumber;
	}

	public void setRuleNumber(String ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	// This shall be overridden if required in sub classes.
	@SuppressWarnings("unchecked")
	protected DomainRuleAction fetchAction() {
		return ruleAdministrationService.findDomainRuleActionById(new Long(selectedAction));
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return predicateAdministrationService;
	}

	public RuleAdministrationService getRuleAdministrationService() {
		return ruleAdministrationService;
	}

	public boolean isSearchProcessed() {
		return searchProcessed;
	}

	public void validateRuleNumber() {
		try {
			Integer.parseInt(ruleNumber);
		} catch (NumberFormatException nfe) {
			addActionError("error.manageBusinessRule.invalidRuleNumber");
			return;
		}
		if (Integer.parseInt(ruleNumber) <= 0) {
			addActionError("error.manageBusinessRule.invalidRuleNumber");
		}
		List<DomainRule> domainRules = ruleAdministrationService
				.findRulesByRuleNumber(ruleNumber);
		if (domainRules != null && domainRules.size() > 0) {
			addActionError("error.manageBusinessRule.ruleNumberAlreadyExists");
		}
	}

	public List<ListOfValues> getListOfRejectionReasons() {
        if (listOfRejectionReasons != null) {
            return listOfRejectionReasons;
        } else {
            return lovRepository.findAllActive("RejectionReason");
        }
    }

	public void setListOfRejectionReasons(List<ListOfValues> listOfRejectionReasons) {
		this.listOfRejectionReasons = listOfRejectionReasons;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
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

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(
			ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public DomainRule getDomainRule() {
		return domainRule;
	}

	public void setDomainRule(DomainRule domainRule) {
		this.domainRule = domainRule;
	}

	public DomainRuleRepository getDomainRuleRepository() {
		return domainRuleRepository;
	}

	public void setDomainRuleRepository(
			DomainRuleRepository domainRuleRepository) {
		this.domainRuleRepository = domainRuleRepository;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public I18NDomainRuleText getIDomainRuleText() {
		return iDomainRuleText;
	}

	public void setIDomainRuleText(I18NDomainRuleText domainRuleText) {
		iDomainRuleText = domainRuleText;
	}
	
	public String getLocale_I18nDomainRule() {
		return locale_I18nDomainRule;
	}

	public void setLocale_I18nDomainRule(String locale_I18nDomainRule) {
		this.locale_I18nDomainRule = locale_I18nDomainRule;
	}

	public Set<I18NDomainRuleText> getI18NFailureSet() {
		return i18NFailureSet;
	}

	public void setI18NFailureSet(Set<I18NDomainRuleText> failureSet) {
		i18NFailureSet = failureSet;
	}

	public List<String> getLocalizedFailureMessages() {
		return localizedFailureMessages;
	}

	public void setLocalizedFailureMessages(List<String> localizedFailureMessages) {
		this.localizedFailureMessages = localizedFailureMessages;
	}

	public String getContext() {
		return context;
	}
	//Check for internationaliztion of Rules 
	public boolean checkForI18N(){
		DomainRuleAction action = fetchAction();
		if("Reject Claim".equals(action.getName()) && "ClaimRules".equals( context )  || "EntryValidationRules".equals( context )){
			return true;
		}
		return false;
	}

    public void prepare() throws Exception {
        if(StringUtils.hasText(context) && doesContextUseRuleGroup()) {
            ruleGroupsInContext = ruleGroupAdministrationService.findRuleGroupsForContext(context);
        }
    }

}

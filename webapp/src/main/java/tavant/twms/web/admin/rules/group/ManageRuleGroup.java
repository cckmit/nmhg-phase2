package tavant.twms.web.admin.rules.group;

import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.domain.rules.group.RuleGroupAdministrationService;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.List;

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 * Date: 7 Oct, 2008
 * Time: 3:35:54 AM
 */
public class ManageRuleGroup extends I18nActionSupport {

    private String context;
    private DomainRuleGroup ruleGroup;
    private RuleGroupAdministrationService ruleGroupAdministrationService;
    private List<DomainRuleGroup> allRuleGroupsForContext;
    
    public String setupRuleGroupCreation() throws Exception {
        return SUCCESS;
    }

    public String saveRuleGroup() throws Exception {
        Long priority = ruleGroupAdministrationService.findNextAvailableRuleGroupPriorityForContext(context);
        ruleGroup.setPriority(priority);
        ruleGroup.setContext(context);
        ruleGroup.setStatus(TWMSWebConstants.STATUS_ACTIVE);
        ruleGroup.getD().setActive(true);
        ruleGroupAdministrationService.save(ruleGroup);

        addActionMessage(getText("message.manageBusinessRuleGroup.ruleGroupCreated",
                new String[] {ruleGroup.getName()}));
        return SUCCESS;
    }

	public String updateRuleGroup() throws Exception {
		ruleGroupAdministrationService.updateRuleGroup(ruleGroup);
		addActionMessage(getText(
				"message.manageBusinessRuleGroup.ruleGroupUpdated",
				new String[] { ruleGroup.getName() }));
		return SUCCESS;
	}

  	public String deactivateRuleGroup() throws Exception {
    	
    	ruleGroup.setStatus(TWMSWebConstants.STATUS_INACTIVE);
    	ruleGroup.getD().setActive(false);
    	
    	ruleGroupAdministrationService.update(ruleGroup);

        addActionMessage(getText("message.manageBusinessRuleGroup.ruleGroupDeactivated",
                new String[] {ruleGroup.getName()}));
        return SUCCESS;
    }
    
    public String activateRuleGroup() throws Exception {
    	
    	ruleGroup.setStatus(TWMSWebConstants.STATUS_ACTIVE);
    	ruleGroup.getD().setActive(true);
    	
    	ruleGroupAdministrationService.update(ruleGroup);

        addActionMessage(getText("message.manageBusinessRuleGroup.ruleGroupActivated",
                new String[] {ruleGroup.getName()}));
        return SUCCESS;
    }

    public String setupRuleGroupsPrioritizaton() {
        allRuleGroupsForContext = ruleGroupAdministrationService.findRuleGroupsForContextOrderedByPriority(context);
        return SUCCESS;
    }

    public String rePrioritizeRuleGroups() throws Exception {
        for (DomainRuleGroup ruleGroup : allRuleGroupsForContext) {
            ruleGroupAdministrationService.update(ruleGroup);    
        }

        addActionMessage(getText("message.manageBusinessRuleGroup.ruleGroupPrioritiesUpdated"));

        return SUCCESS;
    }

    public DomainRuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(DomainRuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public RuleGroupAdministrationService getRuleGroupAdministrationService() {
        return ruleGroupAdministrationService;
    }

    public void setRuleGroupAdministrationService(RuleGroupAdministrationService ruleGroupAdministrationService) {
        this.ruleGroupAdministrationService = ruleGroupAdministrationService;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    // not used by the action. For use by commonDelete.jsp to autohide row.
    public Long getId() {
        return ruleGroup.getId();
    }

    public List<DomainRuleGroup> getAllRuleGroupsForContext() {
        return allRuleGroupsForContext;
    }

    public void setAllRuleGroupsForContext(List<DomainRuleGroup> allRuleGroupsForContext) {
        this.allRuleGroupsForContext = allRuleGroupsForContext;
    }
}

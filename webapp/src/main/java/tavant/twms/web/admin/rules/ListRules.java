/**
 * 
 */
package tavant.twms.web.admin.rules;

import org.apache.log4j.Logger;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.DomainRuleAction;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ListRules extends SummaryTableAction {

	public static final Logger logger = Logger.getLogger(ListRules.class);

	private RuleAdministrationService ruleAdministrationService;

	private String context;

    private boolean doesContextUseRuleGroup() {
        return ruleAdministrationService.doesContextUseRuleGroup(context);
    }

    @Override
	protected PageResult<?> getBody() {
		return ruleAdministrationService.findAllRulesInContext(context,
				getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn(
				"columnTitle.manageBusinessRule.ruleNumber", "ruleNumber", 8,
				"string", true, false, false, false));
		
		//code added for new request
		header.add(new SummaryTableColumn(
				"label.manageBusinessRule.failureRoledescription", "name",
				27, "string",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		header.add(new SummaryTableColumn(
				"columnTitle.manageBusinessRule.expression", "predicate.name",
				30, "string", true, false, false, false));
		
		header.add(new SummaryTableColumn(
				"columnTitle.manageBusinessRule.businessAction", "action.actionName",
				15, "string", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		header.add(new SummaryTableColumn(
				"columnTitle.manageBusinessRule.history.status", "status",
				10, "string"));


        if(doesContextUseRuleGroup()) {
            header.add(new SummaryTableColumn("columnTitle.manageBusinessRule.ruleGroup", "ruleGroup.name", 10,
                    "string"));
        }
        
        header.add(new SummaryTableColumn("columnTitle.common.id", "id", 0,
				"string", false, true, true, false));
		return header;
	}

	// TODO - this is probably not needed, verify and remove
	@Override
	protected BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {
			@Override
			public Object getProperty(String propertyPath, Object root) {
				if ("action.name".equals(propertyPath)) {
					DomainRuleAction action = ((DomainRule) root).getAction();
					return (action == null) ? "" : action.getName();
				}
				return super.getProperty(propertyPath, root);
			}
		};
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
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
package tavant.twms.web.admin.rules.group;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.rules.group.RuleGroupAdministrationService;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Preparable;

/**
 * @author vikas.sasidharan
 *
 */
public class ListRuleGroups extends SummaryTableAction implements Preparable {

    public static final Logger logger = Logger.getLogger(ListRuleGroups.class);

    private RuleGroupAdministrationService ruleGroupAdministrationService;

    private RuleAdministrationService ruleAdministrationService;

	private DomainRuleGroup ruleGroup;

    private String context;
    
	@Override
	protected PageResult<?> getBody() {
        return ruleGroupAdministrationService.listAllRuleGroupsByContext(context, getCriteria());
    }

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.name", "name", 30, "string", true,
                false, false, false));
        header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.priority", "priority", 10, "Number"));
        if(RuleAdministrationService.CLAIM_PROCESSING_RULE_CATEGORY.equals(context)) {
        	header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.stopOnSuccess",
	                "stopRuleProcessingOnSuccess", 25, "Boolean"));
	        header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.stopOnFirstSuccess",        		
	                "stopRuleProcessingOnFirstSuccess", 20, "Boolean"));
        }else {
        	header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.stopOnNoResult",
	                "stopRuleProcessingOnNoResult", 25, "Boolean"));
	        header.add(new SummaryTableColumn("columnTitle.manageBusinessRuleGroup.stopOnMultipleResult",        		
	                "stopRuleProcessingOnMultipleResult", 20, "Boolean"));
        }
        header.add(new SummaryTableColumn(
				"columnTitle.manageBusinessRule.history.status", "status",
				10, "string"));
        header.add(new SummaryTableColumn("columnTitle.common.id", "id", 0, "string", "id", false, true, true, false));
        return header;
	}

    public String viewRuleGroup() {
        return SUCCESS;
    }

    public void setRuleGroupAdministrationService(RuleGroupAdministrationService ruleGroupAdministrationService) {
        this.ruleGroupAdministrationService = ruleGroupAdministrationService;
    }

	public void setContext(String context) {
		this.context = context;
	}

    public String getContext() {
        return context;
    }

    public DomainRuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(DomainRuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    /**
     * This method is called to allow the action to prepare itself.
     *
     * @throws Exception thrown if a system level exception occurs.
     */
    public void prepare() throws Exception {
        String id = getId();
        
        if(StringUtils.hasText(id)) {
            ruleGroup = ruleGroupAdministrationService.findById(Long.parseLong(id));
        }
    }
    
    public RuleAdministrationService getRuleAdministrationService() {
		return ruleAdministrationService;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}
    
    private boolean doesContextUseRuleGroup() {
        return ruleAdministrationService.doesContextUseRuleGroup(context);
    }

}
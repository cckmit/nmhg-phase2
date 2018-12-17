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
package tavant.twms.web.rules;

import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class ListExpressions extends SummaryTableAction {
    private String context;
    private PredicateAdministrationService predicateAdministrationService;
    
    @Override
	protected PageResult<?> getBody() {
    	
        return predicateAdministrationService.findAllNonSearchPredicates(
                getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn("columnTitle.manageBusinessCondition.name",
                "name",100, "string"));
        header.add(new SummaryTableColumn("columnTitle.manageBusinessCondition.expression",
                "id", 0, "string", true, true, true, false));
        
        return header;
	}

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setPredicateAdministrationService(
            PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }

}

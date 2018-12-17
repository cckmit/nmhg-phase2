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
package tavant.twms.web.admin.partreturns;

import tavant.twms.domain.partreturn.PartReturnDefinitionRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.admin.ListCriteriaAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ListPartReturnDefinitionsAction extends ListCriteriaAction {

    private PartReturnDefinitionRepository partReturnDefinitionRepository;

    @Override
    protected String getAlias() {
        return "config";
    }

    @Override
    protected PageResult<?> getBody() {
        return this.partReturnDefinitionRepository.findPage("from PartReturnDefinition "
            + getAlias(), getCriteria());

    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        List<SummaryTableColumn> criteriaHeader = super
                .getCriteriaHeader("Part Return Configuration");
        header.add(new SummaryTableColumn("Id", "id", 0, "String", "id", false, true, true, false));
        header.add(new SummaryTableColumn("", "label", 0, "String", "itemCriterion.identifier",
                true, false, true, false));
        header.add(new SummaryTableColumn("columnTitle.common.itemCriterion",
                "itemCriterion.itemIdentifier", 20, "String"));
        header.addAll(criteriaHeader);
        header.add(new SummaryTableColumn("columnTitle.common.status", "status", 10, "String"));
        return header;
    }

    public void setPartReturnDefinitionRepository(
            PartReturnDefinitionRepository partReturnDefinitionRepository) {
        this.partReturnDefinitionRepository = partReturnDefinitionRepository;
    }
}
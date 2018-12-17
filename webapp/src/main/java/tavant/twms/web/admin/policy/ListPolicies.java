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
package tavant.twms.web.admin.policy;

import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.util.ArrayList;
import java.util.List;
import tavant.twms.infra.ListCriteria;

/**
 * @author Radhakrishnan
 * 
 */
@SuppressWarnings("serial")
public class ListPolicies extends SummaryTableAction {
	private PolicyDefinitionService policyDefinitionService;

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.managePolicy.planCode",
				"code", 15, "string", "code", false, false, false, false));
		header.add(new SummaryTableColumn("columnTitle.managePolicy.planName",
				"description", 25, "string"));
		if(isBuConfigAMER())
			header.add(new SummaryTableColumn(
					"columnTitle.managePolicy.warrantyType", "warrantyType.displayValue",
					10, "string", true));
		else
			header.add(new SummaryTableColumn(
					"columnTitle.managePolicy.warrantyType", "warrantyType.type",
					10, "string"));
		header.add(new SummaryTableColumn(
				"columnTitle.managePolicy.activeFrom",
				"availability.duration.fromDate", 9, "date"));
		header.add(new SummaryTableColumn(
				"columnTitle.managePolicy.activeTill",
				"availability.duration.tillDate", 9, "date"));
		header.add(new SummaryTableColumn("label.common.nomsPolicyOptionCode",
				"nomsPolicyOptionCode", 8, "string", "nomsPolicyOptionCode", false, false, false, false));
		header.add(new SummaryTableColumn("columnTitle.common.status",
				"inActive", 7, "string", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		header.add(new SummaryTableColumn("columnTitle.managePolicy.priority",
				"priority", 7, "String"));
		header.add(new SummaryTableColumn(
				"columnTitle.managePolicy.policyDefinitionNo", "id", 0,
				"string", "id", false, true, true, false));
		// Dummy Column for the accordionLabel
		header.add(new SummaryTableColumn(
				"accordionLabel.managePolicy.policyDefinition", 
				"code", 0, "string", "code", true, false, true, false));
		header.add(new SummaryTableColumn("", "imageCol", 0, IMAGE,
				"labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		return header;
	}

    @Override
        protected ListCriteria getListCriteria(){
            return new ListCriteria(){
            @Override
                protected boolean isNumberProperty(String propertyName){
                    return "policyDefinition.priority".equals(propertyName);
                }
            };
        }

	@Override
	protected PageResult<?> getBody() {
		return policyDefinitionService.findAllPolicyDefinitions(getCriteria());
	}

	@Override
	protected String getAlias() {
		return "policyDefinition";
	}

    @Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver() {

			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object object = super.getProperty(propertyPath, root);
				if (propertyPath.equals("inActive")) {
					boolean isInactive = (Boolean) object;
					if (isInactive) {
						object = getText("label.common.inactive");
					} else {
						object = getText("label.common.active");
					}
				}
				return object;
			}
		};
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

}

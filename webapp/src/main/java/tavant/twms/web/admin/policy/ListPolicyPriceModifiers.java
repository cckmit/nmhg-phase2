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

import tavant.twms.domain.policy.PolicyRatesRepository;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class ListPolicyPriceModifiers extends SummaryTableAction {
	private PolicyRatesRepository policyRatesRepository;

	private Map<String, String> defaultValues;

	public ListPolicyPriceModifiers() {
		super();

		defaultValues = new HashMap<String, String>();
		defaultValues.put("forCriteria.dealerCriterion.identifier",
				getText("label.common.allDealers"));
		defaultValues.put("forCriteria.warrantyType",
				getText("label.common.allWarrantyTypes"));
		defaultValues.put("forCriteria.productType.name",
				getText("label.common.allProductTypes"));
		defaultValues.put("forCriteria.warrantyRegistrationType",
				getText("label.common.all"));
		defaultValues.put("forCriteria.customerState", 
				getText("label.common.allCustomerStates"));
	}

	@Override
	protected PageResult<?> getBody() {
		return policyRatesRepository.findPage("from PolicyRates " + getAlias(),
				getCriteria());
	}

	@Override
	protected String getAlias() {
		return "config";
			}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.managePolicy.policyModifier",
				"id", 0, "String", "id", true, true, true, false));
		header.add(new SummaryTableColumn("columnTitle.common.warrantyType",
				"forCriteria.wntyTypeName",20, "String"));
		header.add(new SummaryTableColumn("columnTitle.common.dealerCriterion",
				"forCriteria.identifier", 20, "String"));
		header.add(new SummaryTableColumn("columnTitle.common.products",
				"forCriteria.productName", 20, "String"));
		header.add(new SummaryTableColumn("columnTitle.managePolicy.registrationType",
				"forCriteria.warrantyRegistrationType.type", 20, "String"));
		header.add(new SummaryTableColumn("columnTitle.managePolicy.customerState",
				"forCriteria.customerState", 20, "String"));
		return header;
	}

	@Override
	protected BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {

			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object object = super.getProperty(propertyPath, root);
				if (object == null) {
					object = defaultValues.get(propertyPath);
				}
				return object;
			}

		};
	}

	public void setPolicyRatesRepository(
			PolicyRatesRepository policyRatesRepository) {
		this.policyRatesRepository = policyRatesRepository;
	}

}

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
package tavant.twms.web.admin.payment;

import tavant.twms.domain.claim.payment.rates.AdministeredItemPriceRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.admin.ListCriteriaAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class ListItemPriceModifiers extends ListCriteriaAction {

	private AdministeredItemPriceRepository administeredItemPriceRepository;

	@Override
	protected String getAlias() {
		return "config";
	}

	@Override
	protected PageResult<?> getBody() {
		return administeredItemPriceRepository.findPage("from AdministeredItemPrice " + getAlias(), getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.common.id",
				"id", 0, "String", "id", false, true, true, false));
		header.add(new SummaryTableColumn("",
				"label", 0, "String", "itemCriterion.item.number", true, false,	true, false));
		header.add(new SummaryTableColumn("label.manageRates.itemNumberGroup",
				"itemCriterion.itemIdentifier", 50,"String"));
		if (isBuConfigAMER())
			header.add(new SummaryTableColumn(
					"columnTitle.common.warrantyType",
					"forCriteria.wntyTypeName", 50, "String",true));
		else
			header.add(new SummaryTableColumn(
					"columnTitle.common.warrantyType",
					"forCriteria.wntyTypeName", 50, "String"));
		return header;
	}

	public void setAdministeredItemPriceRepository(AdministeredItemPriceRepository administeredItemPriceRepository) {
		this.administeredItemPriceRepository = administeredItemPriceRepository;
	}
}
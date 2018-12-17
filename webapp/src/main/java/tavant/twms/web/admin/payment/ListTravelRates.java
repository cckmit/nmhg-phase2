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

import tavant.twms.domain.claim.payment.rates.TravelRatesRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.admin.ListCriteriaAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.List;

/**
 * 
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class ListTravelRates extends ListCriteriaAction {

	private TravelRatesRepository travelRatesRepository;

	@Override
	protected String getAlias() {
		return "config";
	}

	@Override
	protected PageResult<?> getBody() {
		return travelRatesRepository.findPage("from TravelRates " + getAlias(), getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = super.getCriteriaHeader("Travel Rate Price List");
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
				"id", 0, "String", "id", true, true, true, false));
 		tableHeadData.add(new SummaryTableColumn(
                "columnTitle.common.customer", "customerType", 20, "String"));		
		return tableHeadData;
	}

	public void setTravelRatesRepository(TravelRatesRepository travelRatesRepository) {
		this.travelRatesRepository = travelRatesRepository;
	}
}
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
package tavant.twms.web.admin.shippers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class ListShippers extends SummaryTableAction {

	protected static Logger logger = LogManager.getLogger(ListShippers.class);
	private CarrierRepository carrierRepository;

	public void setCarrierRepository(CarrierRepository carrierRepository) {
		this.carrierRepository = carrierRepository;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		 List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
	        tableHeadData.add(new SummaryTableColumn("columnTitle.manageShippers.shipperName",
	        		"name", 20, "string", true, false, false, false));
	        tableHeadData.add(new SummaryTableColumn("columnTitle.manageShippers.shipperDesc",
	        		"description", 20, "string"));
	        tableHeadData.add(new SummaryTableColumn("columnTitle.manageShippers.shipperUrl",
	        		"url", 10, "string"));
	        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
	        		"id",0, "String", false, true, true, false));
	        return tableHeadData;
	}

	@Override
	protected PageResult<?> getBody() {
		PageResult<Carrier> pageResult = carrierRepository.findPage(
				"from Carrier carrier", getCriteria());
		return pageResult;
	}
}

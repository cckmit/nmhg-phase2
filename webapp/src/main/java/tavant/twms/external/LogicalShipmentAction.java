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
package tavant.twms.external;

import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.process.LogicalShipmentService;
import tavant.twms.web.actions.TwmsActionSupport;

import java.util.List;

public class LogicalShipmentAction extends TwmsActionSupport {

	private List<Shipment> shipments;
	private LogicalShipmentService logicalShipmentService;
	
	
	public String execute() {
		return SUCCESS;
	}
	
	public String shipPart() {
		shipments = logicalShipmentService.generateAllLogicalShipments();
		return SUCCESS;
	}
	
	public String shipDealerPart() {
		shipments = logicalShipmentService.generateAllDealerLogicalShipments();
		return SUCCESS;
	}

	public void setLogicalShipmentService(
			LogicalShipmentService logicalShipmentService) {
		this.logicalShipmentService = logicalShipmentService;
	}

	public List<Shipment> getShipments() {
		return shipments;
	}
	
	
}

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
package tavant.twms.domain.campaign;

import org.springframework.core.style.ToStringCreator;

import tavant.twms.infra.ListCriteria;

public class CampaignItemsSearchCriteria extends ListCriteria {

	private String serialNumber;

	private String status;

	private String assignedDealerId;

	public CampaignItemsSearchCriteria() {
		super();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("serialNumber", serialNumber)
				.append("status", status).append("assignedDealerName",
						assignedDealerId).toString();
	}

	public String getAssignedDealerId() {
		return assignedDealerId;
	}

	public void setAssignedDealerId(String assignedDealerId) {
		this.assignedDealerId = assignedDealerId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.policy;

import java.sql.Types;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.claim.Criteria;

/**
 * @author kiran.sg
 */
@Embeddable
public class PolicyRatesCriteria extends Criteria {
	private String customerState;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.policy.WarrantyRegistrationType"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private WarrantyRegistrationType warrantyRegistrationType;
	
	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	public WarrantyRegistrationType getWarrantyRegistrationType() {
		return warrantyRegistrationType;
	}

	public void setWarrantyRegistrationType(
			WarrantyRegistrationType warrantyRegistrationType) {
		this.warrantyRegistrationType = warrantyRegistrationType;
	}
}
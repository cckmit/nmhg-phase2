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
package tavant.twms.domain.inventory;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import tavant.twms.domain.claim.Claim;

@Embeddable
public class ItemReplacementReason {
	@ManyToOne(fetch = FetchType.LAZY)
	private Claim claim;

	/**
	 * @return the claim
	 */
	public Claim getClaim() {
		return claim;
	}

	/**
	 * @param claim the claim to set
	 */
	public void setClaim(Claim claim) {
		this.claim = claim;
	}
}
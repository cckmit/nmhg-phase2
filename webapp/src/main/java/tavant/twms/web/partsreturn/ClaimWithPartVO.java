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
package tavant.twms.web.partsreturn;

/**
 * @author kuldeep.patil
 *
 */
public class ClaimWithPartVO {
	private PartReturnVO part;
	private ClaimVO claim;
	public PartReturnVO getPart() {
		return part;
	}
	public void setPart(PartReturnVO part) {
		this.part = part;
	}
	public ClaimVO getClaim() {
		return claim;
	}
	public void setClaim(ClaimVO claim) {
		this.claim = claim;
	}
}

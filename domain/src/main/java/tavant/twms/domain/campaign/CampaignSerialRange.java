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
package tavant.twms.domain.campaign;


public class CampaignSerialRange {

	private String fromSerialNumber;

	private String toSerialNumber;

	private String patternToApply;
	
	private String attachOrDelete;
	
	
	public String getAttachOrDelete() {
		return attachOrDelete;
	}

	public void setAttachOrDelete(String attachOrDelete) {
		this.attachOrDelete = attachOrDelete;
	}

	public String getFromSerialNumber() {
		return fromSerialNumber;
	}

	public void setFromSerialNumber(String fromSerialNumber) {
		this.fromSerialNumber = fromSerialNumber;
	}

	public String getToSerialNumber() {
		return toSerialNumber;
	}

	public void setToSerialNumber(String toSerialNumber) {
		this.toSerialNumber = toSerialNumber;
	}

	public String getPatternToApply() {
		return patternToApply;
	}

	public void setPatternToApply(String patternToApply) {
		this.patternToApply = patternToApply;
	}

}

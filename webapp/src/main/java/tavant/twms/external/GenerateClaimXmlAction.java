/*
 *   Copyright (c) 2007 Tavant Technologies
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

import java.util.ArrayList;
import java.util.List;

import tavant.twms.integration.layer.component.BuildClaimXml;
//import tavant.twms.integration.layer.component.CheckCreditSubmissionAmount;
import tavant.twms.web.actions.TwmsActionSupport;

@SuppressWarnings("serial")
public class GenerateClaimXmlAction extends TwmsActionSupport {

	private BuildClaimXml buildClaimXml;
	
	//private CheckCreditSubmissionAmount checkCreditSubmissionAmount;  

	private String claimNumber;
	
	List<String> claimNumbers = new ArrayList<String>();

	public String execute() {
		return SUCCESS;
	}

	public void generateClaimAsXml() throws Exception {
		buildClaimXml.buildXml(this.claimNumber);
	}

/*	public String displayMismatchedClaims(){
		checkCreditSubmissionAmount.fetchMismatchedAmountClaims();
		return SUCCESS;
	}*/
	
	public void setBuildClaimXml(BuildClaimXml buildClaimXml) {
		this.buildClaimXml = buildClaimXml;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

/*	public void setCheckCreditSubmissionAmount(
			CheckCreditSubmissionAmount checkCreditSubmissionAmount) {
		this.checkCreditSubmissionAmount = checkCreditSubmissionAmount;
	}*/

	public List<String> getClaimNumbers() {
		return claimNumbers;
	}

	public void setClaimNumbers(List<String> claimNumbers) {
		this.claimNumbers = claimNumbers;
	}

}

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

package tavant.twms.domain.download;

import com.domainlanguage.time.CalendarDate;

/**
 * @author jhulfikar.ali
 *
 */
public class ReportSearchBean {

	private String delimiter;
	
	private CalendarDate fromDate;
	
	private CalendarDate toDate;
	
	private String submitOrCreditOrUpdateDate;
	
	private String claimStatus;
	
	private String dealerNumber;
	
	private String businessUnitName;
	
	private boolean allDealerSelected;
	
	private String customerType;

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public CalendarDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(CalendarDate fromDate) {
		this.fromDate = fromDate;
	}

	public CalendarDate getToDate() {
		return toDate;
	}

	public void setToDate(CalendarDate toDate) {
		this.toDate = toDate;
	}

	public String getSubmitOrCreditOrUpdateDate() {
		return submitOrCreditOrUpdateDate;
	}

	public void setSubmitOrCreditOrUpdateDate(String submitOrCreditOrUpdateDate) {
		this.submitOrCreditOrUpdateDate = submitOrCreditOrUpdateDate;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	public boolean isAllDealerSelected() {
		return allDealerSelected;
	}

	public void setAllDealerSelected(boolean allDealerSelected) {
		this.allDealerSelected = allDealerSelected;
	}

	public String getBusinessUnitName() {
		return businessUnitName;
	}

	public void setBusinessUnitName(String businessUnitName) {
		this.businessUnitName = businessUnitName;
	}
	
}

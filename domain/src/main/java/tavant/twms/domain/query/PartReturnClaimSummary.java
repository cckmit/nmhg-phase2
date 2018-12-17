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

package tavant.twms.domain.query;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
/**
 * DTO class used for part return search result.This special object is required because current model
 * doesn't support reference to claim object from part return.
 * @author roopali.agrawal
 *
 */
public class PartReturnClaimSummary {
	private PartReturn partReturn;
	private Claim claim;
	//todo-this is a temp variable required for SummaryTableAction.Need to remove once SummaryTableAction starts
	//supporting multiple id columns.
	private String dummyId;

	public PartReturnClaimSummary() {
	}

	public PartReturnClaimSummary(PartReturn pr,Claim clm){
		this.partReturn=pr;
		this.claim=clm;
	}
	public Claim getClaim() {
		return claim;
	}
	public void setClaim(Claim claim) {
		this.claim = claim;
	}
	public PartReturn getPartReturn() {
		return partReturn;
	}
	public void setPartReturn(PartReturn partReturn) {
		this.partReturn = partReturn;
	}
	//todo-temp
	public String getDummyId() {
		return partReturn.getId()+"and"+claim.getId();
	}
	public void setDummyId(String dummyId) {
		//this.dummyId = dummyId;
	}

	private Long claimId;
	private Long partReturnId;
	private String claimNumber;
	private String partNumber;
	private String locationCode;
	private PartReturnStatus status;
	private String shipmentNumber;
    private String wpraNumber;
    private String dealerName;
    private ClaimAudit claimAudit;
    private CalendarDate dueDate;

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}
	public void setPartReturnId(Long partReturnId) {
		this.partReturnId = partReturnId;
	}
	public String getDetailId() {
		return this.partReturnId+"and"+this.claimId;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getClaimNumber() {
		return this.claimNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getPartNumber() {
		return this.partNumber;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public String getLocationCode() {
		return this.locationCode;
	}

	public void setStatus(PartReturnStatus status) {
		this.status = status;
	}
	public PartReturnStatus getStatus() {
		return this.status;
	}

	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}
	public String getShipmentNumber() {
		return this.shipmentNumber;
	}

    public String getWpraNumber() {
        return wpraNumber;
    }

    public void setWpraNumber(String wpraNumber) {
        this.wpraNumber = wpraNumber;
    }

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	public ClaimAudit getClaimAudit() {
		return claimAudit;
	}

	public void setClaimAudit(ClaimAudit claimAudit) {
		this.claimAudit = claimAudit;
	}

	public CalendarDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(CalendarDate dueDate) {
		this.dueDate = dueDate;
	}
	

}

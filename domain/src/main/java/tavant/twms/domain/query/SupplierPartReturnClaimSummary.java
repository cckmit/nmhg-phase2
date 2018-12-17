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

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.SupplierPartReturn;
/**
 * DTO class used for supplier part return search result.This special object is required because current model
 * doesn't support reference to claim object from supplier part return.
 * @author amritha.k
 *
 */
public class SupplierPartReturnClaimSummary {
	private SupplierPartReturn supplierPartReturn;
	private Claim claim;
	private String recoveryClaimNumber;
	private Long recoveryClaimId;
	//todo-this is a temp variable required for SummaryTableAction.Need to remove once SummaryTableAction starts
	//supporting multiple id columns. 
	
	public SupplierPartReturnClaimSummary() {
	}
	
	public SupplierPartReturnClaimSummary(SupplierPartReturn pr,Claim clm){
		this.supplierPartReturn=pr;
		this.claim=clm;
	}
	public Claim getClaim() {
		return claim;
	}
	public void setClaim(Claim claim) {
		this.claim = claim;
	}
	public SupplierPartReturn getSupplierPartReturn() {
		return supplierPartReturn;
	}
	
	public void setSupplierPartReturn(SupplierPartReturn supplierPartReturn) {
		this.supplierPartReturn = supplierPartReturn;
	}

	public String getRecoveryClaimNumber() {
		return recoveryClaimNumber;
	}

	public void setRecoveryClaimNumber(String recoveryClaimNumber) {
		this.recoveryClaimNumber = recoveryClaimNumber;
	}

	public Long getRecoveryClaimId() {
		return recoveryClaimId;
	}

	public void setRecoveryClaimId(Long recoveryClaimId) {
		this.recoveryClaimId = recoveryClaimId;
	}
	
	
	
	
}

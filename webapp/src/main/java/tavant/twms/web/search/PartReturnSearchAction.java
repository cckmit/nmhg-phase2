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

package tavant.twms.web.search;

import org.apache.struts2.interceptor.ServletRequestAware;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class PartReturnSearchAction extends SummaryTableAction implements
		ServletRequestAware {
	PartReturnService partReturnService;

	ClaimService claimService;

	String domainPredicateId;

	String savedQueryId;

	private String contextName;

	private HttpServletRequest httpRequest;

	private PartReturnClaimSummary partReturnClaimSummary;

	public boolean isPageReadOnly() {
		return false;
	}
	
	public PartReturnSearchAction() {
		contextName = BusinessObjectModelFactory.PART_RETURN_SEARCHES;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getDomainPredicateId() {
		return domainPredicateId;
	}

	public void setDomainPredicateId(String domainPredicateId) {
		this.domainPredicateId = domainPredicateId;
	}

	public String getSavedQueryId() {
		return savedQueryId;
	}

	public void setSavedQueryId(String savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public PartReturnService getPartReturnService() {
		return partReturnService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public void setServletRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpServletRequest getServletRequest() {
		return httpRequest;
	}

	public String detail() throws Exception {
		if (id != null) {
			// todo-this is a temp arrangement required for SummaryTableAction.
			// Need to remove once SummaryTableAction starts
			// supporting multiple id columns.

			StringTokenizer tokenizer = new StringTokenizer(id, "and");
			String partId = tokenizer.nextToken();
			String claimId = tokenizer.nextToken();
			Claim clm = claimService.findClaim(Long.parseLong(claimId));
			PartReturn partReturn = partReturnService.findPartReturn(Long
					.parseLong(partId));
			Collections.reverse(partReturn.getOemPartReplaced().getPartReturnAudits());
			partReturnClaimSummary = new PartReturnClaimSummary(partReturn, clm);
		}
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		PageResult<?> obj = null;
		if (domainPredicateId != null && !("".equals(domainPredicateId.trim()))) {
			obj = partReturnService.findAllPartReturnsMatchingQuery(Long
					.parseLong(domainPredicateId), getCriteria());
		}		
		return obj;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			savedQueryId = getServletRequest().getAttribute("savedQueryId")
					.toString();
		}
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			domainPredicateId = getServletRequest().getAttribute(
					"domainPredicateId").toString();
		}
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.dueParts.part_no",
				"dummyId", 20, true, true, true, false));

		header.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.partNumber",
				"partReturn.oemPartReplaced.itemReference.unserializedItem.number", 16));
		header.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.returnLocation",
				"partReturn.returnLocation.code", 16, "string"));
		header.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.claimNo",
				"claim.claimNumber", 16, "string"));
		header.add(new SummaryTableColumn("columnTitle.common.status",
				"partReturn.status.status", 16, "string"));
		header.add(new SummaryTableColumn("columnTitle.common.shipment_no",
				"partReturn.shipment.transientId", 16, "string"));
		header.add(new SummaryTableColumn("columnTitle.common.dealerName",
                "claim.forDealer.name", 16, "string"));
		return header;
		
	}

	public PartReturnClaimSummary getPartReturnClaimSummary() {
		return partReturnClaimSummary;
	}

	public void setPartReturnClaimSummary(
			PartReturnClaimSummary partReturnClaimSummary) {
		this.partReturnClaimSummary = partReturnClaimSummary;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

    public boolean showScrapButton(){
        if(null != getPartReturnClaimSummary().getPartReturn() && !PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus().equals(getPartReturnClaimSummary().getPartReturn().getStatus())
                && (getPartReturnClaimSummary().getPartReturn().getStatus().equals(PartReturnStatus.PART_ACCEPTED)
                ||  getPartReturnClaimSummary().getPartReturn().getStatus().equals(PartReturnStatus.PART_REJECTED)
                ||  getPartReturnClaimSummary().getPartReturn().getStatus().equals(PartReturnStatus.PART_RECEIVED))
                && isAllRecoveryClaimsClosed()
                && (null != getPartReturnClaimSummary().getPartReturn().getOemPartReplaced() && !getPartReturnClaimSummary().getPartReturn().getOemPartReplaced().isPartScrapped()) ){
            return true;
        }
        return false;
    }

    public boolean isAllRecoveryClaimsClosed(){
        List<RecoveryClaim> supplierRecoveryClaims = getPartReturnClaimSummary().getClaim() != null ? getPartReturnClaimSummary().getClaim().getRecoveryClaims() : new ArrayList<RecoveryClaim>();
        for(RecoveryClaim recoveryClaim : supplierRecoveryClaims){
            if (!recoveryClaim.getRecoveryClaimState().getState().toUpperCase().contains(AdminConstants.RECOVERY_CLAIM_STATE_CLOSED.toUpperCase())
                    || recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED.getState())
                    || recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED.getState())
                    || recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NOT_FOR_RECOVERY.getState())
                    || recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NOT_FOR_RECOVERY_DISPUTED.getState())
                    || recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.REJECTED.getState())) {
                return false;
            }
        }
        return true;
    }

}

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
package tavant.twms.domain.query.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.claim.ClaimFolderNames;
/**
 *
 * @author roopali.agrawal, Hari Krishna Y D
 *
 */
public class ClaimInboxViewFields extends InboxViewFields {
    private Map<String, InboxField> claimFields = new HashMap<String, InboxField>();

    private Map<String, InboxField> claimFieldsForDealer = new HashMap<String, InboxField>();
    
    ClaimInboxViewFields(String folderName) {
    	if(ClaimFolderNames.SEARCH.equalsIgnoreCase(folderName))
    		setupClaimFields(folderName, true);
    	else 
    		setupClaimFields(folderName, false);
    }

    private void setupClaimFields(String folderName, boolean isPredefinedSearch) { 
    	String prefix = isPredefinedSearch ? "" : "claim.";
    	
    	claimFields.put(prefix+"id", new InboxField(prefix+"id", "Number", "label.claimInboxView.claimId",true,false,true,false,10));
    	claimFields.put(prefix+"filedOnDate", new InboxField(prefix+"filedOnDate", "date", "label.inboxView.claimDate",10));
        claimFields.put(prefix+"histClmNo", new InboxField(prefix+"histClmNo", "string", "label.claim.historicalClaimNumber", true, true,12));

        claimFields.put(prefix+"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber",
        		new InboxField(prefix+"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", prefix+"causalPartBrandItemNumber", "string", "columnTitle.newClaim.causalPart",10));
        claimFields.put(prefix+"activeClaimAudit.serviceInformation.causalPart.number",
        		new InboxField(prefix+"activeClaimAudit.serviceInformation.causalPart.number", "string", "columnTitle.nmhgCausalPart",10));
        claimFields.put("enum:ClaimState:"+prefix+"activeClaimAudit.state", new InboxField("enum:ClaimState:"+prefix+"activeClaimAudit.state",prefix+"activeClaimAudit.state", "string", "label.inboxView.claimStatus", 10));
        claimFields.put(prefix+"clmTypeName", new InboxField(prefix+"clmTypeName", "string", "label.inboxView.claimType", true, true,8));
        claimFields.put(prefix+"activeClaimAudit.conditionFound", new InboxField(prefix+"activeClaimAudit.conditionFound", "string", "label.inboxView.condFound"));
        claimFields.put(prefix+"activeClaimAudit.workPerformed", new InboxField(prefix+"activeClaimAudit.workPerformed", "string", "label.inboxView.workPerf"));
        claimFields.put(prefix+"activeClaimAudit.serviceInformation.faultCode", new InboxField(prefix+"activeClaimAudit.serviceInformation.faultCode", "string", "columnTitle.newClaim.failureCode"));
        claimFields.put(prefix+"activeClaimAudit.serviceInformation.causedBy.name", new InboxField(prefix+"activeClaimAudit.serviceInformation.causedBy.name", "string", "label.newClaim.causedBy"));
        claimFields.put(prefix+"activeClaimAudit.failureDate", new InboxField(prefix+"activeClaimAudit.failureDate", "date", "label.inboxView.failureDate",10));
        claimFields.put(prefix+"activeClaimAudit.installationDate", new InboxField(prefix+"activeClaimAudit.installationDate", "date",
                "label.common.installationDate", true,10));
        claimFields.put(prefix+"activeClaimAudit.repairDate", new InboxField(prefix+"activeClaimAudit.repairDate", "date", "label.inboxView.repairDate", true, true,10));
        claimFields.put(prefix+"activeClaimAudit.otherComments", new InboxField(prefix+"activeClaimAudit.otherComments", "string",
        "label.inboxView.otherComments"));
        //claimFields.put(prefix+"probableCause", new InboxField(prefix+"probableCause", "string", "label.inboxView.probableCause"));
        claimFields.put(prefix+"forDealer.name", new InboxField(prefix+"forDealer.name", "string", "label.inboxView.servProviderName"));
        claimFields.put(prefix+"forDealer.dealerNumber", new InboxField(prefix+"forDealer.dealerNumber",
                "string", "label.inboxView.servProviderNumber",6));
        claimFields.put(prefix+"serialNumber", new InboxField(
		        prefix+"serialNumber", "string", "label.inboxView.serialNumber",false,false,10));
        claimFields.put(prefix+"buildDate", new InboxField(prefix+"buildDate", "date", "label.inboxView.buildDate",false,false,10));
        claimFields.put(prefix+"manufacturingSite.description", new InboxField(prefix+"manufacturingSite.description", "string", "label.inboxView.manufacturingSite",false,false,10));
		claimFields.put(prefix+"model", new InboxField(
		        prefix+"model", "string", "label.inboxView.model",false,false,10));
		claimFields.put(prefix+"activeClaimAudit.workOrderNumber", new InboxField(
				prefix+"activeClaimAudit.workOrderNumber", "string", "label.inboxView.workOrderNumber"));
		claimFields.put(prefix+"lastUpdatedOnDate", new InboxField(
                prefix+"lastUpdatedOnDate", "date", "label.inboxView.lastUpdtDate", 10));
		claimFields.put(prefix+"lastModifiedClaimStatusDate", new InboxField(
                prefix+"lastModifiedClaimStatusDate", "date", "label.inboxView.lastModifiedStatusDate", false,false,10));
		claimFields.put(prefix+"hoursInService", new InboxField(prefix+"hoursInService", "string", "label.common.hoursOnTruck",false,false));
		claimFields.put(prefix+"ncr", new InboxField(prefix+"ncr", "string", "NCR",false,true));
		claimFields.put(prefix+"ncrWith30Days", new InboxField(prefix+"ncrWith30Days", "string", "30Day NCR",false,true));
		claimFields.put(prefix+"hoursOnPart", new InboxField(prefix+"hoursOnPart", "string", "label.common.hoursOnPart"));
		claimFields.put(prefix+"businessUnitInfo", new InboxField(prefix+"businessUnitInfo", "string", "label.inboxView.businessUnit"));
		claimFields.put(prefix+"campaign.code", new InboxField(prefix+"campaign.code", "string", "columnTitle.campaign.code"));
		String assignToUserName = prefix + "activeClaimAudit.assignToUser.getAssignToUserName()";
		claimFields.put(assignToUserName, new InboxField(assignToUserName,
				"string", "label.inboxView.assignTo",false,false));
		claimFields.put(prefix+"forDealer.getMarketingGroup()", new InboxField(prefix+"forDealer.getMarketingGroup()", "string", "label.common.MarketingGroupCode",false,false));
		
			claimFields.put(prefix+"reasonForServiceManagerRequest.description", new InboxField(
			        prefix+"reasonForServiceManagerRequest.description", "string", "label.inboxView.smrReason",false,false));
			
		if(folderName.equalsIgnoreCase(ClaimFolderNames.TRANSFERRED)){
				claimFields.put(prefix+"activeClaimAudit.isLateFeeApprovalRequired", new InboxField(prefix+"activeClaimAudit.isLateFeeApprovalRequired","boolean","label.claim.lateFeeWaiver"));
			}
		
		if(!folderName.equalsIgnoreCase(ClaimFolderNames.SERVICE_MANAGER_RESPONSE) &&
				!folderName.equalsIgnoreCase(ClaimFolderNames.DRAFT_CLAIM)) {
			claimFields.put(prefix+"activeClaimAudit.payment.activeCreditMemo.creditMemoNumber", new InboxField(
	                prefix+"activeClaimAudit.payment.activeCreditMemo.creditMemoNumber", "string", "label.inboxView.creditMemo",false,false));
	        claimFields.put(prefix+"activeClaimAudit.payment.activeCreditMemo.creditMemoDate", new InboxField(
	                prefix+"activeClaimAudit.payment.activeCreditMemo.creditMemoDate", "date", "label.inboxView.creditDate", false, false,10));
	        claimFields.put(prefix+"activeClaimAudit.payment.claimedAmount", new InboxField(
					prefix+"activeClaimAudit.payment.claimedAmount", "Money", "label.inboxView.amountAsked",false,false));
			claimFields.put(prefix+"activeClaimAudit.payment.activeCreditMemo.paidAmount.abs()", new InboxField(
					prefix+"activeClaimAudit.payment.activeCreditMemo.paidAmount.abs()", "Money", "label.inboxView.amountCredited",false,false));
			claimFields.put(prefix+"activeClaimAudit.payment.totalAmount", new InboxField(
					prefix+"activeClaimAudit.payment.totalAmount", "Money", "label.inboxView.amountAccepted",false,false));
			claimFields.put(prefix+"activeClaimAudit.acceptanceReason.description", new InboxField(
	                prefix+"activeClaimAudit.acceptanceReason.description", "string", "label.inboxView.accpReason",false,false));
			claimFields.put(prefix+"suppliers.description", new InboxField(
			        prefix+"suppliers.description", "string", "label.inboxView.supplier",false,false));
			claimFields.put(prefix+"activeClaimAudit.accountabilityCode.description", new InboxField(
	                prefix+"activeClaimAudit.accountabilityCode.description", "string", "label.claimSearch.accountabilityCodes",false,false));
			claimFields.put(prefix+"activeClaimAudit.getRejectionReasonForInboxView()", new InboxField(
	                prefix+"activeClaimAudit.getRejectionReasonForInboxView()", "string", "label.inboxView.rejectionReason",false,false));
			claimFields.put(prefix+"partReturnStatus", new InboxField(
	                prefix+"partReturnStatus", "string", "label.inboxView.claimPartReturnStatus",false,false));
		}

		if(folderName.equalsIgnoreCase(ClaimFolderNames.REPLIES))
			claimFields.put(prefix+"latestAudit.updatedBy.name", new InboxField(
					prefix+"latestAudit.updatedBy.name", "string", "label.inboxView.replyFrom",false,false));
		if(folderName.equalsIgnoreCase(ClaimFolderNames.TRANSFERRED))
			claimFields.put(prefix+"latestAudit.updatedBy.name", new InboxField(
					prefix+"latestAudit.updatedBy.name", "string", "label.inboxView.transferFrom",false,false));
		if(folderName.equalsIgnoreCase(ClaimFolderNames.FORWARDED))
			claimFields.put(prefix+"latestAudit.updatedBy.name", new InboxField(
					prefix+"latestAudit.updatedBy.name", "string", "label.inboxView.forwardedBy",false,false));

		//Fields which appear in the summary table by default are made hidden and default sort is set to true.  
		//So that they cannot be duplicated but appear in the sort columns list while creating/editing a view.
		if(folderName.equalsIgnoreCase(ClaimFolderNames.DRAFT_CLAIM)) {
			claimFields.get(prefix+"activeClaimAudit.workOrderNumber").setHidden(true);
			claimFields.get(prefix+"activeClaimAudit.workOrderNumber").setAllowDefaultSort(true);
			claimFields.get(prefix+"filedOnDate").setHidden(true);
			claimFields.get(prefix+"filedOnDate").setAllowDefaultSort(true);
		}else {
			claimFields.put(prefix+"claimNumber", new InboxField(prefix+"claimNumber", "date", "label.inboxView.claimNumber",true,true,true,true,10));
			if (folderName.equalsIgnoreCase(ClaimFolderNames.WAITING_FOR_LABOR)) {
				claimFields.get(prefix+"activeClaimAudit.workOrderNumber").setHidden(true);
				claimFields.get(prefix+"activeClaimAudit.workOrderNumber").setAllowDefaultSort(true);
			}
		}

		claimFieldsForDealer.putAll(claimFields);
    	claimFieldsForDealer.remove(prefix+"forDealer.name");
        claimFieldsForDealer.remove(prefix+"forDealer.dealerNumber");
        claimFieldsForDealer.remove(prefix+"latestAudit.updatedBy.name");
        claimFieldsForDealer.remove(prefix+"activeClaimAudit.acceptanceReason.description");
        claimFieldsForDealer.remove(prefix+"activeClaimAudit.rejectionReason.description");
        claimFieldsForDealer.remove(prefix+"buildDate");
        claimFieldsForDealer.remove(prefix+"suppliers.description");
        claimFieldsForDealer.remove(prefix+"activeClaimAudit.accountabilityCode.description");
        claimFieldsForDealer.remove(prefix+"manufacturingSite.description");
        claimFieldsForDealer.remove(prefix+"lastUpdatedOnDate");
//        claimFieldsForDealer.remove(prefix+"ncr");
//        claimFieldsForDealer.remove(prefix+"ncrWith30Days");
        claimFieldsForDealer.remove(prefix+"activeClaimAudit.serviceInformation.causalPart.number");
        //claimFieldsForDealer.get("enum:ClaimState:"+prefix+"state").setAllowSort(false);
    }
    
    @Override
    protected Map<String, InboxField> getInboxFieldsForAllUsers() {
        return claimFields;
    }

    @Override
    protected Map<String, InboxField> getInboxFieldsForDealer() {
        return claimFieldsForDealer;
    }

    public List<String> getFieldsNotAvailableForSort(){
    	List<String> exclude=new ArrayList<String>();
//        exclude.add("claim.payment.totalClaimAmount");
    	return exclude;
    }

}

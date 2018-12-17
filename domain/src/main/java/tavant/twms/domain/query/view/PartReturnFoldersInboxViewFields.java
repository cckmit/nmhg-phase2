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

import tavant.twms.security.SecurityHelper;

public class PartReturnFoldersInboxViewFields extends InboxViewFields {
    private Map<String, InboxField> partReturnFields = new HashMap<String, InboxField>();

    private Map<String, InboxField> partReturnFieldsForDealer = new HashMap<String, InboxField>();
    
    private Map<String, InboxField> partReturnFieldsForSupplier = new HashMap<String, InboxField>();

    PartReturnFoldersInboxViewFields() 
    {
    	partReturnFields.put("part.numberOfUnits", new InboxField(
                "part.numberOfUnits", "string", "Quantity"));    	
    	partReturnFields.put("part.partReturn.returnLocation.code", new InboxField(
                "part.partReturn.returnLocation.code", "string", "Return Location"));
    	partReturnFields.put("part.partReturn.returnStatus.status", new InboxField("part.partReturn.returnStatus.status",
				"string", "Return Status"));
    	/*partReturnFields.put("part.sraComment", new InboxField("part.sraComment",
				"string", "Sra Comment")); */   	
    	partReturnFields.put("part.activePartReturn.dueDate", new InboxField("part.activePartReturn.dueDate","date", "Due Date"));
    	partReturnFields.put("part.pricePerUnit", new InboxField(
                "part.pricePerUnit", "string", "Unit Price"));
    	
    	partReturnFields.put("part.partReturn.warehouseLocation",
    				new InboxField("part.partReturn.warehouseLocation","string", "Bin Location"));
        /*partReturnFields.put("part.supplier", new InboxField("part.supplier", "string",    "Supplier"));*/
        
        
          
         partReturnFields.put("part.partReturn.inspectionResult", new InboxField(
                "part.partReturn.inspectionResult", "string", "InspectionResult"));

        //Claim Fields
        partReturnFields.put("claim.activeClaimAudit.otherComments", new InboxField("claim.activeClaimAudit.otherComments", "string",
        "Additional Details"));
		partReturnFields.put("claim.applicablePolicy.policyDefinition.code", new InboxField(
		        "claim.applicablePolicy.policyDefinition.code", "string", "Applicable Policy Code"));
		partReturnFields.put("claim.applicablePolicy.policyDefinition.description", new InboxField(
		        "claim.applicablePolicy.policyDefinition.description", "string", "Applicable Policy Name"));
		partReturnFields.put("claim.applicablePolicy.priority", new InboxField(
		        "claim.applicablePolicy.priority", "number", "Applicable Policy Priority"));        
		partReturnFields.put("claim.applicablePolicy.policyDefinition.warrantyType.type", new InboxField(
		        "claim.applicablePolicy.policyDefinition.warrantyType.type", "string", "Applicable Policy WarrantyType"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.causalPart.unserializedItem.description", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.causalPart.unserializedItem.description", "string", "Causal Part Description"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.causalPart.unserializedItem.number", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.causalPart.unserializedItem.number", "string", "Causal Part Number"));
		partReturnFields.put("claim.activeClaimAudit.probableCause", new InboxField("claim.activeClaimAudit.probableCause", "string", "Cause"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.causedBy", new InboxField("claim.activeClaimAudit.serviceInformation.causedBy", "string", "Caused By"));
		partReturnFields.put("claim.filedOnDate", new InboxField("claim.filedOnDate", "date", "Claim Date"));
		partReturnFields.put("claim.claimNumber", new InboxField("claim.claimNumber", "string", "Claim Number"));        
		partReturnFields.put("claim.activeClaimAudit.state", new InboxField("claim.activeClaimAudit.state", "string", "Claim Status"));
		partReturnFields.put("claim.clmTypeName", new InboxField("claim.clmTypeName", "string", "Claim Type"));
		partReturnFields.put("claim.activeClaimAudit.conditionFound", new InboxField("claim.activeClaimAudit.conditionFound", "string", "Complaint"));
		partReturnFields.put("claim.activeClaimAudit.workPerformed", new InboxField("claim.activeClaimAudit.workPerformed", "string", "Correction"));
		partReturnFields.put("claim.itemReference.referredInventoryItem.deliveryDate", new InboxField(
		        "claim.itemReference.referredInventoryItem.deliveryDate", "date", "Delivery Date"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code", "string", "Failure Code"));
		partReturnFields.put("claim.activeClaimAudit.failureDate", new InboxField("claim.activeClaimAudit.failureDate", "date", "Failure Date"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.faultFound", new InboxField("claim.activeClaimAudit.serviceInformation.faultFound", "string", "Fault Found"));
		partReturnFields.put("claim.hoursInService", new InboxField("claim.hoursInService", "number",
		        "Machine Hours"));
		partReturnFields.put("claim.activeClaimAudit.installationDate", new InboxField("claim.activeClaimAudit.installationDate", "date",
		        "Part Fitted Date"));
		//partReturnFields.put("claim.state", new InboxField("claim.state", "string", "Internal Claim Status"));
		partReturnFields.put("claim.itemReference.referredInventoryItem.invoiceDate", new InboxField(
		        "claim.itemReference.referredInventoryItem.invoiceDate", "date", "Invoice Date"));
		
		partReturnFields.put("claim.itemReference.unserializedItem.description", new InboxField(
		        "claim.itemReference.unserializedItem.description", "string", "Item Description"));
		partReturnFields.put("claim.itemReference.unserializedItem.number", new InboxField(
		        "claim.itemReference.unserializedItem.number", "string", "Item Number"));
		partReturnFields.put("claim.itemReference.unserializedItem.model", new InboxField(
		        "claim.itemReference.unserializedItem.model", "string", "Model"));
		partReturnFields.put("claim.mileage", new InboxField(
		        "claim.mileage", "string", "Mileage(in miles)"));
        partReturnFields.put("claim.mileageInKms", new InboxField(
		        "claim.mileageInKms", "string", "Mileage(in kms)"));
		partReturnFields.put("claim.itemReference.unserializedItem.productType.name", new InboxField(
		        "claim.itemReference.unserializedItem.productType.name", "string", "Product"));
		partReturnFields.put("claim.activeClaimAudit.repairDate", new InboxField("claim.activeClaimAudit.repairDate", "date", "Repair Date"));
		partReturnFields.put("claim.itemReference.referredInventoryItem.serialNumber", new InboxField(
		        "claim.itemReference.referredInventoryItem.serialNumber", "string", "Serial Number"));
		
		partReturnFields.put("claim.forDealer.name", new InboxField("claim.forDealer.name", "string", "Service Provider Name"));
		partReturnFields.put("claim.forDealer.dealerNumber", new InboxField("claim.forDealer.dealerNumber",
		        "string", "Service Provider Number"));
		partReturnFields.put("claim.forDealer.preferredCurrency", new InboxField(
		        "claim.forDealer.preferredCurrency", "string", "Service Provider Preferred Currency"));
		
		partReturnFields.put("claim.activeClaimAudit.reasonForServiceManagerRequest.description", new InboxField(
		        "claim.activeClaimAudit.reasonForServiceManagerRequest.description", "string", "SMR Reason"));
		partReturnFields.put("claim.serviceManagerRequest", new InboxField("claim.serviceManagerRequest", "string", "SMR Request"));
		partReturnFields.put("claim.totalClaimAmount", new InboxField("claim.totalClaimAmount", "string",
		"Total Claim Amount"));
		partReturnFields.put("claim.totalLabor", new InboxField("claim.totalLabor", "string", "Total Labor"));
		partReturnFields.put("claim.totalOEMPartsAmount", new InboxField("claim.totalOEMPartsAmount", "string",
		"Total ESG Parts Amount"));
		partReturnFields.put("claim.totalTravel", new InboxField("claim.totalTravel", "string", "Total Travel"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distanceInKM", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distanceInKM", "number", "Travel Distance(in kms)"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distanceInMiles", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distanceInMiles", "number", "Travel Distance(in miles)"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.hours", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.hours", "number", "Travel Hours"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.location", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.location", "string",
		        "Travel Location"));
		partReturnFields.put("claim.activeClaimAudit.serviceInformation.faultCodeRef.treadBucket", new InboxField(
		        "claim.activeClaimAudit.serviceInformation.faultCodeRef.treadBucket", "string", "Tread Bucket"));
		
		
		partReturnFields.put("claim.itemReference.referredInventoryItem.warrantyStartDate", new InboxField(
		        "claim.itemReference.referredInventoryItem.warrantyStartDate", "date",
		        "Warranty Start Date"));
		partReturnFields.put("claim.activeClaimAudit.workOrderNumber", new InboxField("claim.activeClaimAudit.workOrderNumber", "string",
		"Dealer Job Number"));     

    	partReturnFieldsForSupplier.putAll(partReturnFields);
        partReturnFieldsForDealer.putAll(partReturnFields);
        
        partReturnFieldsForDealer.remove("part.partReturn.warehouseLocation");

        partReturnFieldsForSupplier.remove("part.activePartReturn.dueDate");
        partReturnFieldsForSupplier.remove("part.pricePerUnit");
        partReturnFieldsForSupplier.remove("claim.filedOnDate");
    }

    public Map<String, InboxField> getInboxFields() {
        if (new SecurityHelper().getLoggedInUser().hasOnlyRole("dealer"))
            return getInboxFieldsForDealer();
        else if(new SecurityHelper().getLoggedInUser().hasOnlyRole("supplier"))
        	return getInboxFieldsForSupplier();
        else	
            return getInboxFieldsForAllUsers();
    }
    
    @Override
    protected Map<String, InboxField> getInboxFieldsForAllUsers() {
        // TODO Auto-generated method stub
        return partReturnFields;
    }

    @Override
    protected Map<String, InboxField> getInboxFieldsForDealer() {
        // TODO Auto-generated method stub
        return partReturnFieldsForDealer;
    }
    
    protected Map<String, InboxField> getInboxFieldsForSupplier() 
    {
        return partReturnFieldsForSupplier;
    }
    
    public List<String> getFieldsNotAvailableForSort(){
    	List<String> exclude=new ArrayList<String>();
    	exclude.add("claim.totalClaimAmount");
    	exclude.add("claim.totalLabor");
    	exclude.add("claim.totalTravel");
    	exclude.add("claim.totalOEMPartsAmount");
    	return exclude;
    }

}


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

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class PartReturnInboxViewFields extends InboxViewFields {
    private Map<String, InboxField> partReturnFields = new HashMap<String, InboxField>();
    
    private Map<String, InboxField> partReturnFieldsForDealer = new HashMap<String, InboxField>();
    
    private Map<String, InboxField> partReturnFieldsForSupplier = new HashMap<String, InboxField>();

    PartReturnInboxViewFields() 
    {
    	partReturnFields.put("warehouseLocation", new InboxField("warehouseLocation","string", "Bin Location"));
    	partReturnFields.put("dueDate", new InboxField("dueDate","date", "Due Date", true, false));
    	partReturnFields.put("inspectionResult.failureReason.description", new InboxField("inspectionResult.failureReason.description","string", "Internal Inspection Result", false));
    	
    	partReturnFields.put("oemPartReplaced.numberOfUnits", new InboxField("oemPartReplaced.numberOfUnits", "string", "Quantity"));
    	partReturnFields.put("returnLocation.code", new InboxField("returnLocation.code", "string", "Return Location", false, false));
    	partReturnFields.put("returnStatus.status", new InboxField("returnStatus.status",	"string", "Return Status", false, false));    	
    	partReturnFields.put("sraComment", new InboxField("sraComment", "string", "Sra Comment"));
    	
    	partReturnFields.put("supplierPartReturn.rejectionReason.description", new InboxField("supplierPartReturn.rejectionReason.description", "string", "Supplier Action Reason", false));
    	partReturnFields.put("supplierPartReturn.inspectionResult.description", new InboxField("supplierPartReturn.inspectionResult.description", "string", "Supplier Inspection Result"));
        partReturnFields.put("supplier.name", new InboxField("supplier.name", "string",    "Supplier Name"));
        partReturnFields.put("supplier.supplierNumber", new InboxField("supplier.supplierNumber", "string",    "Supplier Number"));
    	partReturnFields.put("oemPartReplaced.pricePerUnit", new InboxField("oemPartReplaced.pricePerUnit", "string", "Unit Price"));

    	
        partReturnFields.put("claim.activeClaimAudit.otherComments", new InboxField("claim.activeClaimAudit.otherComments", "string",   "Additional Details"));
        partReturnFields.put("claim.applicablePolicy.policyDefinition.code", new InboxField(
                "claim.applicablePolicy.policyDefinition.code", "string", "Applicable Policy Code", false));
        partReturnFields.put("claim.applicablePolicy.policyDefinition.description", new InboxField(
                "claim.applicablePolicy.policyDefinition.description", "string", "Applicable Policy Name", false));
        partReturnFields.put("claim.applicablePolicy.policyDefinition.priority", new InboxField(
                "claim.applicablePolicy.policyDefinition.priority", "number", "Applicable Policy Priority", false));
        partReturnFields.put("claim.applicablePolicy.policyDefinition.warrantyType.type", new InboxField(
                "claim.applicablePolicy.policyDefinition.warrantyType.type", "string", "Applicable Policy Warranty type", false));
        
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.causalPart.description", new InboxField(
                "claim.activeClaimAudit.serviceInformation.causalPart.description", "string", "Causal Part Description", false));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", new InboxField(
                "claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", "string", "Causal Part Number", false));
        
        partReturnFields.put("claim.activeClaimAudit.probableCause", new InboxField("claim.activeClaimAudit.probableCause", "string", "Cause"));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.causedBy", new InboxField("claim.activeClaimAudit.serviceInformation.causedBy", "string", "Caused By"));
    	partReturnFields.put("claim.filedOnDate", new InboxField("claim.filedOnDate", "date", "Claim Date"));
        partReturnFields.put("claim.claimNumber", new InboxField("claim.claimNumber", "string", "Claim Number"));
        partReturnFields.put("claim.externalState", new InboxField("claim.externalState", "string", "Claim Status"));
        partReturnFields.put("claim.clmTypeName", new InboxField("claim.clmTypeName", "string", "Claim Type"));
        partReturnFields.put("claim.activeClaimAudit.conditionFound", new InboxField("claim.activeClaimAudit.conditionFound", "string", "Complaint"));
        partReturnFields.put("claim.activeClaimAudit.workPerformed", new InboxField("claim.activeClaimAudit.workPerformed", "string", "Correction"));
        partReturnFields.put("claim.itemReference.referredInventoryItem.deliveryDate",
        		new InboxField("claim.itemReference.referredInventoryItem.deliveryDate", "date", "Delivery Date"));
 	   
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code", new InboxField(
                "claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code", "string", "Failure Code"));
        partReturnFields.put("claim.activeClaimAudit.failureDate", new InboxField("claim.activeClaimAudit.failureDate", "date", "Failure Date"));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.faultFound", new InboxField("claim.activeClaimAudit.serviceInformation.faultFound", "string", "Fault Found"));
        partReturnFields.put("claim.hoursInService", new InboxField("claim.hoursInService", "number", "Machine Hours"));
        partReturnFields.put("claim.activeClaimAudit.installationDate", new InboxField("claim.activeClaimAudit.installationDate", "date","Part Fitted Date"));
        partReturnFields.put("claim.itemReference.referredInventoryItem.shipmentDate", new InboxField(
                "claim.itemReference.referredInventoryItem.shipmentDate", "date","Invoice Date")); 
        
        partReturnFields.put("oemPartReplaced.itemReference.unserializedItem.description", new InboxField(
                "oemPartReplaced.itemReference.unserializedItem.description", "string","Item Description")); 
        partReturnFields.put("oemPartReplaced.itemReference.unserializedItem.number", new InboxField(
                "oemPartReplaced.itemReference.unserializedItem.number", "string","Item Number"));
        
        partReturnFields.put("oemPartReplaced.itemReference.referredInventoryItem.mileage * 1.609344", new InboxField(
                "oemPartReplaced.itemReference.referredInventoryItem.mileage * 1.609344", "string","Mileage(in kms)"));
        partReturnFields.put("oemPartReplaced.itemReference.referredInventoryItem.mileage", new InboxField(
                "oemPartReplaced.itemReference.referredInventoryItem.mileage", "string","Mileage(in miles)"));
        partReturnFields.put("claim.itemReference.referredInventoryItem.model", new InboxField(        		
                "claim.itemReference.referredInventoryItem.model", "string","Model", false));
        partReturnFields.put("claim.itemReference.referredInventoryItem.productType.name", new InboxField(
                "claim.itemReference.referredInventoryItem.productType.name", "string","Product", false));
        
        partReturnFields.put("claim.activeClaimAudit.repairDate", new InboxField("claim.activeClaimAudit.repairDate", "date", "Repair Date"));
        partReturnFields.put("oemPartReplaced.itemReference.referredInventoryItem.serialNumber",
                new InboxField("oemPartReplaced.itemReference.referredInventoryItem.serialNumber", "string", "Serial Number", false, false));

        partReturnFields.put("claim.forDealer.name", new InboxField("claim.forDealer.name", "string", "Service Provider Name"));
        partReturnFields.put("claim.forDealer.dealerNumber", new InboxField("claim.forDealer.dealerNumber", "string", "Service Provider Number"));
        partReturnFields.put("claim.forDealer.preferredCurrency.code", new InboxField("claim.forDealer.preferredCurrency.code", "string", "Service Provider Preferred currency", false));

        partReturnFields.put("claim.activeClaimAudit.reasonForServiceManagerRequest", new InboxField(
                "claim.activeClaimAudit.reasonForServiceManagerRequest", "string", "SMR Reason"));
        
        partReturnFields.put("claim.sMRRequest", new InboxField("claim.sMRRequest", "string", "SMR Request", false));
        
        partReturnFields.put("claim.totalClaimAmount", new InboxField("claim.totalClaimAmount", "string",
        "Total Claim Amount", false));
        partReturnFields.put("claim.totalLabor", new InboxField("claim.totalLabor", "string", "Total Labor Amount", false));
        partReturnFields.put("claim.totalOEMPartsAmount", new InboxField("claim.totalOEMPartsAmount", "string",
        "Total OEM Parts Amount", false));
        partReturnFields.put("claim.totalTravel", new InboxField("claim.totalTravel", "string", "Total Travel Amount", false));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance", new InboxField(
                "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance", "number",
                "Total Travel Distance"));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance * 0.6", new InboxField(
                "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance * 0.6", "number",
                "Travel Distance (in kms)"));
        partReturnFields.put("claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance * 0.6", new InboxField(
                "claim.activeClaimAudit.serviceInformation.serviceDetail.travelDetails.distance * 0.6", "number",
                "Travel Distance (in miles)"));
        
        partReturnFieldsForDealer.put("dueDate", partReturnFields.get("dueDate"));
        partReturnFieldsForDealer.put("oemPartReplaced.numberOfUnits", partReturnFields.get("oemPartReplaced.numberOfUnits"));
        partReturnFieldsForDealer.put("returnLocation.code", partReturnFields.get("returnLocation.code"));
        partReturnFieldsForDealer.put("returnStatus.status", partReturnFields.get("returnStatus.status"));
        partReturnFieldsForDealer.put("oemPartReplaced.pricePerUnit", partReturnFields.get("oemPartReplaced.pricePerUnit"));
        partReturnFieldsForDealer.put("claim.filedOnDate", partReturnFields.get("claim.filedOnDate"));
        partReturnFieldsForDealer.put("claim.claimNumber", partReturnFields.get("claim.claimNumber"));
        
        
        partReturnFieldsForSupplier.put("oemPartReplaced.numberOfUnits", partReturnFields.get("oemPartReplaced.numberOfUnits"));
        partReturnFieldsForSupplier.put("returnLocation.code", partReturnFields.get("returnLocation.code"));
        partReturnFieldsForSupplier.put("returnStatus.status", partReturnFields.get("returnStatus.status"));
        partReturnFieldsForSupplier.put("sraComment", partReturnFields.get("sraComment"));               
        partReturnFieldsForSupplier.put("claim.claimNumber", partReturnFields.get("claim.claimNumber"));
        

    }

    public Map<String, InboxField> getInboxFields() 
    {        	
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

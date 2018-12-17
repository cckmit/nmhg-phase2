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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author roopali.agrawal
 *
 */
public class InventoryInboxViewFields extends InboxViewFields {
    private Map<String, InboxField> inventoryFields = new HashMap<String, InboxField>();
    
    private Map<String, InboxField> inventoryFieldsForDealer = new HashMap<String, InboxField>();

    InventoryInboxViewFields(String folderName) {
    	inventoryFields.put("builtOn", new InboxField("builtOn", "date", "label.inboxView.buildDate", true, true,10));
        inventoryFields.put("shipmentDate", new InboxField("shipmentDate", "date", "label.common.shipmentDate",true, true,10));  
        inventoryFields.put("hoursOnMachine", new InboxField("hoursOnMachine", "string","label.inboxView.hoursInService", true, false,10));
        inventoryFields.put("conditionType.itemCondition", new InboxField("conditionType.itemCondition", "string","label.inboxView.itemCond", true, false));
        inventoryFields.put("businessUnitInfo", new InboxField("businessUnitInfo", "string","label.inboxView.businessUnit", true, false));
		
        inventoryFields.put("ofType.product.itemGroupDescription", new InboxField("ofType.product.itemGroupDescription", "string", "label.common.seriesDescription",false,false));               
        /*inventoryFields.put("ofType.number", new InboxField("ofType.number", "string", "label.inboxView.itemNumber",false));*/
        inventoryFields.put("ofType.model.itemGroupDescription", new InboxField("ofType.model.itemGroupDescription", "string", "label.inboxView.model",false));        
        inventoryFields.put("ofType.model.description", new InboxField("ofType.model.description", "string", "label.common.description",false,false));
        inventoryFields.put("ofType.product.groupCode", new InboxField("ofType.product.groupCode", "string", "label.inboxView.product",false));

        // Fields For Current Owner        
        inventoryFields.put("currentOwner.name", 
        		new InboxField("currentOwner.name", "string", "label.inboxView.servProviderName",false,true));
        inventoryFields.put("getOwner().getServiceProviderNumber()", 
        		new InboxField("getOwner().getServiceProviderNumber()", "string", "label.inboxView.servProviderNumber", false, false));
        inventoryFields.put("getSalesOrderNumber()", 
                		new InboxField("getSalesOrderNumber()", "string", "label.inventory.salesOrderNumber", false)); 
        inventoryFields.put("marketingGroupCode", 
        		new InboxField("marketingGroupCode", "string", "label.inboxView.marketingGroupCode", false));    
        
        // Fields for Customer
    	if(folderName.equalsIgnoreCase("RETAILED"))
    	{
    		inventoryFields.put("deliveryDate", new InboxField("deliveryDate", "date", "label.inboxView.deliveryDate", true, true,10));
    		inventoryFields.put("latestBuyer.type", 
	        		new InboxField("latestBuyer.type", "string", "label.warrantyAdmin.customerType", false));
	        inventoryFields.put("latestBuyer.name", 
	        		new InboxField("latestBuyer.name", "string", "label.inboxView.endCustName", false));	        
	        inventoryFields.put("latestWarranty.addressForTransfer.addressLine", 
	    	        		new InboxField("latestWarranty.addressForTransfer.addressLine", "string", "label.inboxView.endCustAddress",false));
	        inventoryFields.put("latestWarranty.addressForTransfer.city", 
	        		new InboxField("latestWarranty.addressForTransfer.city", "string", "label.inboxView.endCustCity",false));
	        inventoryFields.put("latestWarranty.addressForTransfer.state", 
	        		new InboxField("latestWarranty.addressForTransfer.state", "string", "label.inboxView.endCustState", false));
	        inventoryFields.put("latestWarranty.addressForTransfer.country", 
	        		new InboxField("latestWarranty.addressForTransfer.country","string","label.inboxView.endCustCountry", false));
	        inventoryFields.put("latestWarranty.addressForTransfer.email", 
	    	        		new InboxField("latestWarranty.addressForTransfer.email","string","label.inboxView.endCustEmail", false));
	        inventoryFields.put("latestWarranty.addressForTransfer.phone", 
	    	        		new InboxField("latestWarranty.addressForTransfer.phone","string","label.inboxView.endCustPhone", false));
	        inventoryFields.put("latestWarranty.addressForTransfer.zipCode", 
	        		new InboxField("latestWarranty.addressForTransfer.zipCode", "string", "label.inboxView.endCustZip", false)); 
	        inventoryFields.put("vinNumber", new InboxField("vinNumber", "string", "label.common.vinId", true));
	        inventoryFields.put("wntyStartDate", new InboxField("wntyStartDate", "date", "columnTitle.common.warrantyStartDate",true, true,10));
	        inventoryFields.put("wntyEndDate", new InboxField("wntyEndDate", "date", "columnTitle.common.warrantyEndDate",true, true,10));
	        inventoryFields.put("latestWarranty.discountType.description", new InboxField("latestWarranty.discountType.description", "string", "columnTitle.common.discountType",false, false,10));
	        inventoryFields.put("discAuthorizationNumber", new InboxField("discAuthorizationNumber", "string", "columnTitle.common.discountNumber",false, false,10));
	        inventoryFields.put("discountPercent", new InboxField("discountPercent", "string", "columnTitle.common.discountPercentage",false, false,10));
    	}
    	if(folderName.equalsIgnoreCase("STOCK"))
    	{
    		inventoryFields.put("machineAge", new InboxField("machineAge", "string", "label.common.machineAge",false, false,10));    	
    	}
    	
    	inventoryFields.put("invoiceNumber", new InboxField("invoiceNumber", "string", "label.common.invoiceNumber",false, false,10));        
    	
        inventoryFieldsForDealer.putAll(inventoryFields);	   
        inventoryFieldsForDealer.remove("currentOwner.name");
        inventoryFieldsForDealer.remove("getOwner().getServiceProviderNumber()");
    }

    @Override
    protected Map<String, InboxField> getInboxFieldsForAllUsers() {
        return inventoryFields;
    }

    @Override
    protected Map<String, InboxField> getInboxFieldsForDealer() {
        return inventoryFieldsForDealer;
    }

}

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
package tavant.twms.domain.complaints;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.infra.DomainRepositoryTestCase;

public class ComplaintsRepositoryImplTest extends DomainRepositoryTestCase {

	private ComplaintsRepository complaintsRepository;
	
	private Complaint newComplaint;
	
	private InventoryItem inventoryItem;
	
	private InventoryService itemService;
	
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        inventoryItem = itemService.findSerializedItem("ABCD123456");
        
        // Test create
        newComplaint = new Complaint();
        newComplaint.setItemReference(new ItemReference(inventoryItem));
    }
    
    public void testCRUD() {
        Consumer consumer = new Consumer();
        consumer.setFirstName("Consumer 1");
        consumer.setAddress(new Address());
        newComplaint.setConsumer(consumer);
        newComplaint.setComplaintType("FieldReport");
        complaintsRepository.save(newComplaint);    	
    }

	public InventoryService getItemService() {
		return itemService;
	}

	public void setItemService(InventoryService itemService) {
		this.itemService = itemService;
	}

	public void setComplaintsRepository(ComplaintsRepository complaintsRepository) {
		this.complaintsRepository = complaintsRepository;
	}
	
}

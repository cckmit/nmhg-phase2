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
package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.dao.DataIntegrityViolationException;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.time.CalendarDate;

public class InventoryItemRepositoryImplIntegrationTest extends DomainRepositoryTestCase {

    OrgService orgService;

    CustomerService customerService;

    InventoryItemRepository inventoryItemRepository;

    CatalogService catalogService;
    
    InventoryService inventoryService;

    public void testCreate() throws CatalogException, ItemNotFoundException {
        InventoryItem inv = createInventoryItem("S001");
        InventoryTransaction invTx = createInventoryTx(inv);
        inv.getTransactionHistory().add(invTx);

        this.inventoryItemRepository.save(inv);
        flushAndClear();
        assertNotNull("Id should have been assigned", inv.getId());
        assertNotNull("Id should have been assigned", invTx.getId());

        InventoryItem invFromDB = this.inventoryItemRepository.findSerializedItem("S001");
        assertEquals(invFromDB.getId(), inv.getId());
        assertEquals(invFromDB.getTransactionHistory().get(
                invFromDB.getTransactionHistory().size() - 1).getId(), invTx.getId());
    }

    public void testCreateWithExistingSerialNumber() throws CatalogException {
        try {
            InventoryItem inv = createInventoryItem("ABCD123456");
            this.inventoryItemRepository.save(inv);
            flushAndClear();
            fail("Should have thrown a unique constraint violation");
        } catch (DataIntegrityViolationException e) {
            // FIX-ME: Spring misses to convert an exception and hibernate also
            // FIX-ME: throws different exceptions based on whether batch
            // updates
            // FIX-ME: are enabled or not. Hence the two extra try-catch blocks
        } catch (ConstraintViolationException e) {
        } catch (GenericJDBCException e) {
        }
    }

    public void testCreateWithExistingSerialNumberButDifferentItemCondition()
            throws CatalogException, ItemNotFoundException {

        InventoryItem inv = createInventoryItem("ABCD123456");
        inv.setConditionType(InventoryItemCondition.REFURBISHED);
        this.inventoryItemRepository.save(inv);
        flushAndClear();
        assertNotNull("Id should have been assigned", inv.getId());
    }

    private InventoryItem createInventoryItem(String serialNumber) throws CatalogException {
        InventoryItem inv = new InventoryItem();

        inv.setSerialNumber(serialNumber);
        inv.setOfType(this.catalogService.findItemOwnedByManuf("PRTVLV1"));
        inv.setRegistrationDate(CalendarDate.date(2005, 1, 6));
        inv.setDeliveryDate(CalendarDate.date(2005, 1, 6));
        inv.setType(new InventoryType("STOCK"));
        // TODO Fix this once the id field is removed from
        // InventoryItemCondition
        inv.setConditionType(InventoryItemCondition.NEW);
        return inv;
    }

    private InventoryTransaction createInventoryTx(InventoryItem inv) {
        InventoryTransaction invTx = new InventoryTransaction();

        invTx.setTransactionDate(CalendarDate.date(2005, 1, 6));
        invTx.setSeller(this.orgService.findDealerById(7L));
        invTx.setBuyer(this.customerService.findCustomerById(29L));
        invTx.setSalesOrderNumber("SO-1");
        invTx.setInvoiceNumber("IN-1");
        invTx.setInvoiceDate(CalendarDate.date(2005, 1, 6));
        invTx.setTransactedItem(inv);
        return invTx;
    }

    public void testUpdate() throws ItemNotFoundException {
        InventoryItem inv = this.inventoryItemRepository.findSerializedItem("ABCD123456");
        assertEquals(2, inv.getTransactionHistory().size());

        InventoryTransaction invTx3 = createInventoryTx(inv);
        inv.getTransactionHistory().add(invTx3);

        this.inventoryItemRepository.update(inv);
        flushAndClear();
        assertNotNull("Id should have been assigned", invTx3.getId());

        InventoryItem invFromDB = this.inventoryItemRepository.findSerializedItem("ABCD123456");
        assertEquals(3, invFromDB.getTransactionHistory().size());

    }

    public void testListInventoryItemConditionTypes() {

        List<InventoryItemCondition> itemConditionList = new ArrayList<InventoryItemCondition>();
        itemConditionList = this.inventoryItemRepository.listInventoryItemConditionTypes();
        assertEquals(2, itemConditionList.size());
    }

    public void testfindAllSerialNumbersStartingWith() throws ItemNotFoundException {
        List<String> result = this.inventoryItemRepository.findAllSerialNumbersStartingWith("A", 0,
                10);
        assertEquals(4, result.size());
        result = this.inventoryItemRepository.findAllSerialNumbersStartingWith("ABCD", 0, 10);
        assertEquals(3, result.size());
        result = this.inventoryItemRepository.findAllSerialNumbersStartingWith("XYZ", 0, 10);
        assertEquals(0, result.size());
        result = this.inventoryItemRepository.findAllSerialNumbersStartingWith("ABCD123456", 0, 10);
        assertEquals(1, result.size());
    }

    public void testFindSerializedItem() throws ItemNotFoundException {
        InventoryItem foundItem = this.inventoryItemRepository.findSerializedItem("ABCD123456");
        assertNotNull(foundItem);
        assertEquals(CalendarDate.date(2005, 1, 6), foundItem.getRegistrationDate());
        assertEquals("jack", foundItem.getOwnedBy().getName());
    }

    public void testOwnedBy() throws ItemNotFoundException {
        InventoryItem sockItem = this.inventoryItemRepository.findSerializedItem("PQXZ123458");
        assertEquals("A-L-L EQUIPMENT", sockItem.getOwnedBy().getName());
        assertEquals("A-L-L EQUIPMENT", sockItem.getDealer().getName());
    }

    /*
     * public void testfindAllInventoryItemsForDealer() { Dealership dealer =
     * this.orgService.findDealerById(new Long(7)); PageResult<InventoryItem>
     * result = this.inventoryItemRepository
     * .findAllInventoryItemsForDealer(dealer, new PageSpecification(0, 10));
     * assertEquals(1, result.getNumberOfPagesAvailable()); assertEquals(6,
     * result.getResult().size()); }
     */

    public void testfindAllInventoryItemsofTypeForDealer() {
        Dealership dealer = this.orgService.findDealerById(new Long(7));
        InventoryListCriteria criteria = new InventoryListCriteria();
        criteria.setDealer(dealer);
        criteria.setPageSpecification(new PageSpecification(0, 10));
        criteria.setType(new InventoryType("STOCK"));

        PageResult<InventoryItem> result = this.inventoryItemRepository
                .findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(1, result.getNumberOfPagesAvailable());
        assertEquals(3, result.getResult().size());

        criteria.setType(new InventoryType("RETAIL"));
        result = this.inventoryItemRepository.findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(1, result.getNumberOfPagesAvailable());
        assertEquals(3, result.getResult().size());

        criteria.addFilterCriteria("serial_number", "P");
        result = this.inventoryItemRepository.findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(1, result.getNumberOfPagesAvailable());
        assertEquals(1, result.getResult().size());

        criteria.addFilterCriteria("serial_number", "FF");
        result = this.inventoryItemRepository.findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(0, result.getResult().size());

        criteria.removeFilterCriteria();
        criteria.addSortCriteria("serial_number", false);
        result = this.inventoryItemRepository.findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(result.getResult().get(0).getSerialNumber(), "PQRS123458");

        criteria.removeFilterCriteria();
        criteria.setDraft(true);
        result = this.inventoryItemRepository.findAllInventoryItemsOfTypeForDealer(criteria);
        assertEquals(0, result.getResult().size());
    }

    public void testFindSerializedItemNonExistentItem() {
        try {
            this.inventoryItemRepository.findSerializedItem("ZZZZZZZZZ");
            fail("Should have thrown an item not found exception");
        } catch (ItemNotFoundException e) {
        }
    }

   public void _testfindAllInventoryItemsForMultiClaim(){
	 /*ListCriteria listCriteria = new ListCriteria();
	   listCriteria.addSortCriteria("serialNumber", true);
	   PageSpecification page = new PageSpecification();
	   page.setPageNumber(0);
	   listCriteria.setPageSpecification(page);
	   User dealer = this.orgService.findUserByName("bishop");
	   MultiInventorySearch search = new MultiInventorySearch();
	   search.setInventoryType("RETAIL");
	  // search.setSerialNumber("123458");
	   Customer cust =new Customer();
	   cust.setCompanyName("HP");
	   search.setCustomer(cust);
	   PageResult<InventoryItem> items = this.inventoryService.findAllInventoryItemsForMultiClaim
	   (search,dealer.getBelongsToOrganizations().getId(),listCriteria);
	   assertEquals(1, items.getResult().size());*/
   }
   
   public void testfindInventoryItemsForSerialNumbers() {      
    	   String serialNumbers="ABCD123456, ABCD123457, ABCD123458";
    	   serialNumbers=serialNumbers.replaceAll(" ","");
       	   String[] serialArray = serialNumbers.split(",");
    	   List<InventoryItem>items=inventoryService.findInventoryItemsForSerialNumbers(serialArray);
    	   assertEquals(3, items.size());          
   }

    public void testFindInventoryItemsBetweenSerialNumbers() throws CatalogException {
        InventoryItem item1 = createInventoryItem("CA1111");
        InventoryTransaction invTx = createInventoryTx(item1);
        item1.getTransactionHistory().add(invTx);
        this.inventoryItemRepository.save(item1);
        flush();

        InventoryItem item2 = createInventoryItem("CA1112");
        item2.getTransactionHistory().add(invTx);
        this.inventoryItemRepository.save(item2);
        flush();
	
        InventoryItem item3 = createInventoryItem("CA1113");
        item3.getTransactionHistory().add(invTx);
        this.inventoryItemRepository.save(item3);
        flush();

        InventoryItem item4 = createInventoryItem("CA1114");
        item4.getTransactionHistory().add(invTx);
        this.inventoryItemRepository.save(item4);
        flush();

        InventoryItem item5 = createInventoryItem("CA1115");
        item5.getTransactionHistory().add(invTx);
        this.inventoryItemRepository.save(item5);

        flushAndClear();

        List<InventoryItem> itemList = this.inventoryItemRepository
                .findInventoryItemsBetweenSerialNumbers("CA1113", "CA1115");
        assertEquals(3, itemList.size());

        itemList = this.inventoryItemRepository.findInventoryItemsBetweenSerialNumbers("CA1111",
                "CA1111");
        assertEquals(1, itemList.size());

        itemList = this.inventoryItemRepository.findInventoryItemsBetweenSerialNumbers("CA1115",
                "CA1111");
        assertEquals(0, itemList.size());
    }

    public void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}
    
}

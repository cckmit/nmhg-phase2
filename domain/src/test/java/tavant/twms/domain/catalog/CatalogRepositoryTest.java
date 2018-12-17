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
package tavant.twms.domain.catalog;

import java.util.List;
import java.util.Set;

import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.PageSpecification;

public class CatalogRepositoryTest extends DomainRepositoryTestCase {
    CatalogRepository catalogRepository;

    OrgService orgService;

    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public void testCreateItem() throws Exception {
        int initialGroupCount = this.catalogRepository.findAllItemGroups().size();
        assertNull(this.catalogRepository.findItem("11"));
        Dealership dealership = this.orgService.findDealerByName("Dealer 1"); 
        Item item = createItem("11");
        item.setOwnedBy(dealership);
        this.catalogRepository.save(item);
        int finalGroupCount = this.catalogRepository.findAllItemGroups().size();
        assertNotNull(this.catalogRepository.findItemByNumberAndParty("11", this.orgService.findDealerByName("Dealer 1")));
        // This may not be true if the ItemGroup didn't exist earlier
        assertEquals(initialGroupCount, finalGroupCount);

    }
    
    public void testFindAllItemGroupsOfType()
    {
    	String itemGroupType="MODEL";
    	List<ItemGroup> itemGroupList = catalogRepository.findAllItemGroupsOfType(itemGroupType); 
    	assertEquals(true, itemGroupList.size() > 0);
    }

    public void testUpdateItem() {
        String itemNumber = "MC-COUGAR-50-HZ-1";
        Item item = this.catalogRepository.findItem(itemNumber);
        assertNotNull("Item for test not found.", item);
        String newDescription = "Blessed with a new description";
        item.setDescription(newDescription);
        this.catalogRepository.update(item);
        assertEquals(newDescription, this.catalogRepository.findItem(itemNumber).getDescription());
    }

    private Item createItem(String number) throws GroupInclusionException {
        Item item = new Item();
        ItemGroup product = this.catalogRepository.findItemGroup(5L);
        ItemGroup model = this.catalogRepository.findItemGroup(14L);
        model.includeItem(item);
        item.setDescription("Took man to the moon.");
        item.setMake("NASA");
        item.setModel(model);
        item.setName("Apollo-11");
        item.setNumber(number);
        item.setProduct(product);
        return item;
    }

    public void testComposition() {
        Item equipment = this.catalogRepository.findItem("MC-COUGAR-50-HZ-1");
        assertNotNull(equipment);

        equipment.include(2, this.catalogRepository.findItem("PRTVLV1"));
        equipment.include(2, this.catalogRepository.findItem("PRTVLV2"));
        equipment.include(2, this.catalogRepository.findItem("PRTVLV3"));

        flushAndClear();

        Item _reloaded = this.catalogRepository.findItem("MC-COUGAR-50-HZ-1");
        assertNotNull(_reloaded);
        Set<ItemComposition> _parts = _reloaded.getParts();
        for (ItemComposition _part : _parts) {
            assertTrue(_reloaded.includes(_part.getItem()));
        }
    }

    public void testFindItem() {
        Item item = this.catalogRepository.findItem("PRTVLV1");
        assertNotNull(item);
        assertEquals("Valve", item.getModel().getName());
        assertEquals("Ingersoll Rand", item.getMake());
    }

    public void testFindItemNumbersStartingWith() {
        List<String> itemNumbers = this.catalogRepository.findItemNumbersStartingWith("PRTVLV", 0,
                10);
        assertNotNull(itemNumbers);
        assertEquals(3, itemNumbers.size());

        itemNumbers = this.catalogRepository.findItemNumbersStartingWith("ATTCH-UNIGY", 0, 10);
        assertEquals(9, itemNumbers.size());

        itemNumbers = this.catalogRepository.findItemNumbersStartingWith("JUNK", 0, 10);
        assertEquals(0, itemNumbers.size());
    }

    public void testFindItemsWithModelName(){
    	List<Item> items = this.catalogRepository.findItemsWithModelName("EXTD WARR REMAN");
    	assertNotNull(items);
    }
    public void testfindModelsWhoseNumbersStartWith() {
        List<ItemGroup> items = this.catalogRepository.findModelsWhoseNumbersStartWith("COU", 0, 10);
        assertNotNull(items);
    }
    
    public void testFindItemsWhoseNumbersStartWith() {
        List<Item> items = this.catalogRepository.findItemsWhoseNumbersStartWith("PRTVLV", 0, 10);
        assertNotNull(items);
        assertEquals(3, items.size());

        items = this.catalogRepository.findItemsWhoseNumbersStartWith("ATTCH-UNIGY", 0, 10);
        assertEquals(9, items.size());

        items = this.catalogRepository.findItemsWhoseNumbersStartWith("JUNK", 0, 10);
        assertEquals(0, items.size());
    }

    public void testFindParts() {
        // Testing if a valid query is generated
        this.catalogRepository.findParts("G");
    }

    public void testFindItemGroupsWithNameLike() {
        List<String> names = this.catalogRepository.findItemGroupsWithNameLike("U", 0, 10);
        assertEquals(4, names.size());
        assertEquals("UNIGY", names.get(0));
    }

    public void testFindItemGroupByCode() {
        ItemGroup ig = this.catalogRepository.findItemGroupByCode("ASG");
        assertNotNull("Desired ItemGroup not found.", ig);
    }

    public void testFindAllItemGroups() {
        List<ItemGroup> allItemGroups = this.catalogRepository.findAllItemGroups();
        assertEquals(true, allItemGroups.size()>0);
    }

    public void testAddFailureStructureToItemGroup() {
        ItemGroup ig = this.catalogRepository.findItemGroup(1L);
        assertEquals(0, ig.getFailureStructures().size());

        FailureStructure failureStructure = new FailureStructure();
        failureStructure.setId(1L);
        failureStructure.setName("Test");
        ig.addFailureStructure(failureStructure);
        this.catalogRepository.updateItemGroup(ig);

        ig = this.catalogRepository.findItemGroup(1L);
        assertEquals(1, ig.getFailureStructures().size());
        failureStructure = ig.getFailureStructures().iterator().next();
        assertEquals(1L, failureStructure.getId().longValue());
        assertEquals("Test", failureStructure.getName());
        assertEquals(ig, failureStructure.getForItemGroup());
    }

    public void testFindItemWithNumberAndDescriptionLike() {
        List<Item> items = this.catalogRepository.findItemsWithNumberAndDescruptionLike("MC", "");
        assertEquals(27, items.size());
        items = this.catalogRepository.findItemsWithNumberAndDescruptionLike("AT", "");
        assertEquals(9, items.size());
        assertTrue(items.contains(this.catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1")));
    }

    public void testSupplier() {
        assertTrue(this.catalogRepository.findItem("PRTHOSE1").isOwnedByOEM());
        // This part comes from a supplier
        assertFalse(this.catalogRepository.findSupplierItem("PRTVLV1-NW", null).isOwnedByOEM());
        assertEquals(3, this.catalogRepository.findItemsOwnedBy(new Long(31)).size());
    }

    public void testItemCompositionRelatedChecks() {
        Item equipmentType = this.catalogRepository.findItem("MC-COUGAR-50-HZ-1");

        // Machine parts.
        Item compressorType = this.catalogRepository.findItem("PRTCOMP20L1");
        Item controllerType = this.catalogRepository.findItem("PRTCONTAZ1");
        Item motorType = this.catalogRepository.findItem("PRTMOTOR6000RPMZ");

        assertTrue(equipmentType.includes(compressorType));
        assertTrue(equipmentType.includes(controllerType));
        assertTrue(equipmentType.includes(motorType));

        // Compressor parts.
        Item cylinderType = this.catalogRepository.findItem("PRTCYL80CC");
        Item pistonType = this.catalogRepository.findItem("PRTPISTON10MM");
        Item discType = this.catalogRepository.findItem("PRTDISC10MM");
        Item crankShaftType = this.catalogRepository.findItem("PRTCSHAFT10MM");

        assertTrue(compressorType.includes(cylinderType));
        assertTrue(compressorType.includes(pistonType));
        assertTrue(compressorType.includes(discType));
        assertTrue(compressorType.includes(crankShaftType));

        assertTrue(equipmentType.includes(cylinderType));
        assertTrue(equipmentType.includes(pistonType));
        assertTrue(equipmentType.includes(discType));
        assertTrue(equipmentType.includes(crankShaftType));

        // Controller Parts.
        Item motherboardType = this.catalogRepository.findItem("PRTMBINTEL505");
        Item _LCD_DisplayType = this.catalogRepository.findItem("PRTLCDDLG45");

        assertTrue(controllerType.includes(motherboardType));
        assertTrue(controllerType.includes(_LCD_DisplayType));

        assertTrue(equipmentType.includes(motherboardType));
        assertTrue(equipmentType.includes(_LCD_DisplayType));

        // Motor parts.
        Item solenoidType = this.catalogRepository.findItem("PRTSLND10");
        Item shaftType = this.catalogRepository.findItem("PRTSHAFT10");
        Item wiringType = this.catalogRepository.findItem("PRTWIRE10");
        Item axleType = this.catalogRepository.findItem("PRTAXLE10");
        Item connectorType = this.catalogRepository.findItem("PRTCONCTR10");

        assertTrue(motorType.includes(solenoidType));
        assertTrue(motorType.includes(shaftType));
        assertTrue(motorType.includes(wiringType));
        assertTrue(motorType.includes(axleType));
        assertTrue(motorType.includes(connectorType));

        assertTrue(equipmentType.includes(solenoidType));
        assertTrue(equipmentType.includes(shaftType));
        assertTrue(equipmentType.includes(wiringType));
        assertTrue(equipmentType.includes(axleType));
        assertTrue(equipmentType.includes(connectorType));

        // Cylinder parts
        Item casingType = this.catalogRepository.findItem("PRTCASE10");
        Item nutType = this.catalogRepository.findItem("PRTNUT10");
        Item boltType = this.catalogRepository.findItem("PRTBOLT10");
        Item washerType = this.catalogRepository.findItem("PRTWASHER10");

        assertTrue(cylinderType.includes(casingType));
        assertTrue(cylinderType.includes(nutType));
        assertTrue(cylinderType.includes(boltType));
        assertTrue(cylinderType.includes(washerType));

        assertTrue(equipmentType.includes(casingType));
        assertTrue(equipmentType.includes(nutType));
        assertTrue(equipmentType.includes(boltType));
        assertTrue(equipmentType.includes(washerType));

        // Piston parts
        Item disc10Type = this.catalogRepository.findItem("PRTDISC10I");
        assertTrue(pistonType.includes(disc10Type));
        assertTrue(pistonType.includes(nutType));
        assertTrue(pistonType.includes(boltType));
        assertTrue(pistonType.includes(washerType));
    }

    public void testFindProdutsWithNameStartingWith() {
        PageSpecification pageSpecification = new PageSpecification(0, 20);
        List<Item> items = this.catalogRepository.findProdutsWithNameStartingWith("pegasus",
                pageSpecification);
        assertNotNull(items);
        assertEquals(9, items.size());
        assertTrue(items.get(0).getName().toLowerCase().startsWith("pegasus"));
    }

}

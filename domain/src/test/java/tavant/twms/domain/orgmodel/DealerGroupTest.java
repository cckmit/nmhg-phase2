package tavant.twms.domain.orgmodel;

import junit.framework.TestCase;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;

public class DealerGroupTest extends TestCase {
    
    DealerGroup group;
    
    public DealerGroupTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        group = new DealerGroup();
        group.setName("name");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testSimpleItemGroup() throws GroupInclusionException, GroupHierarchyException {
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfDealers());
        
        //Add another group.
        DealerGroup group2 = new DealerGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        assertTrue(group.getConsistsOf().contains(group2));
        //Once one group is added isGroupOfItems should return false...
        assertFalse(group.isGroupOfDealers());
        assertTrue(group.isGroupOfGroups());
        
        //Remove Group.
        group.removeGroup(group2);
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfDealers());
        assertTrue(group.getConsistsOf().isEmpty());
        
        //Add Item.
        Dealership dealership = new Dealership();
        group.includeDealer(dealership);
        assertTrue(group.getIncludedDealers().contains(dealership));
        assertFalse(group.isGroupOfGroups());
        assertTrue(group.isGroupOfDealers());
        
        //Remove Item.
        group.removeDealer(dealership);
        assertTrue(group.getIncludedDealers().isEmpty());
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfDealers());        
    }
    
    public void testAdditionOfItemWhenChildGroupsPresent() throws Exception {
        
        //Add another group.
        DealerGroup group2 = new DealerGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        
        //Try adding an item.
        Dealership dealership = new Dealership();
        try {
            group.includeDealer(dealership);
            fail("Exception should have been thrown.");
        } catch (GroupInclusionException e) {
            assertTrue(group.getIncludedDealers().isEmpty());
            assertFalse(group.isGroupOfDealers());
        }
    }
    
    public void testAdditionOfGroupWhenChildItemsPresent() throws Exception {
        
        //Add an Item.
        Dealership dealership = new Dealership();
        group.includeDealer(dealership);
        
        //Try adding a Group.
        DealerGroup group2 = new DealerGroup();
        group2.setName("name2");
        try {
            group.includeGroup(group2);
            fail("An Exception should have been thrown.");
        } catch (GroupInclusionException e) {
            assertTrue(group.getConsistsOf().isEmpty());
            assertFalse(group.isGroupOfGroups());
        } 
        
    }
    
    public void testIncludingAGroupThatAlreadyHasAParent() throws Exception {
        
        //Add another group.
        DealerGroup group2 = new DealerGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        
        //Try including group2 in another Group.
        DealerGroup group3 = new DealerGroup();
        group3.setName("name3");
        try {
            group3.includeGroup(group2);
            fail("An Exception should have been thrown.");
        } catch (GroupHierarchyException e) {
            assertTrue(group3.getConsistsOf().isEmpty());
            assertTrue(group3.isGroupOfDealers());
        }
        
    }
    
    public void testFindTopMostParent() throws GroupInclusionException, GroupHierarchyException {
        DealerGroup g1 = new DealerGroup(), g2 = new DealerGroup(), g3 = new DealerGroup(), g4= new DealerGroup();
        g3.includeGroup(g4);
        g1.includeGroup(g2);
        g1.includeGroup(g3);
        
        //Test level 1
        assertEquals(g1, g1.findTopMostParent());
        
        //Test level 2
        assertEquals(g1, g2.findTopMostParent());
        assertEquals(g1, g3.findTopMostParent());
        
        //Test level 3
        assertEquals(g1, g4.findTopMostParent());
        
    }
}

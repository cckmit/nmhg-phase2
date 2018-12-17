package tavant.twms.domain.catalog;

import junit.framework.TestCase;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;

public class ItemGroupTest extends TestCase {
    
    ItemGroup group;

    public ItemGroupTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        group = new ItemGroup();
        group.setName("name");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testSimpleItemGroup() throws GroupInclusionException, GroupHierarchyException {
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfItems());
        
        //Add another group.
        ItemGroup group2 = new ItemGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        assertTrue(group.getConsistsOf().contains(group2));
        //Once one group is added isGroupOfItems should return false...
        assertFalse(group.isGroupOfItems());
        assertTrue(group.isGroupOfGroups());
        
        //Remove Group.
        group.removeGroup(group2);
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfItems());
        assertTrue(group.getConsistsOf().isEmpty());
        
        //Add Item.
        Item item = new Item();
        group.includeItem(item);
        assertTrue(group.getIncludedItems().contains(item));
        assertFalse(group.isGroupOfGroups());
        assertTrue(group.isGroupOfItems());
        
        //Remove Item.
        group.removeItem(item);
        assertTrue(group.getIncludedItems().isEmpty());
        assertTrue(group.isGroupOfGroups());
        assertTrue(group.isGroupOfItems());        
    }
    
    public void testAdditionOfItemWhenChildGroupsPresent() throws Exception {
        
        //Add another group.
        ItemGroup group2 = new ItemGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        
        //Try adding an item.
        Item item = new Item();
        try {
            group.includeItem(item);
            fail("Exception should have been thrown.");
        } catch (GroupInclusionException e) {
            assertTrue(group.getIncludedItems().isEmpty());
            assertFalse(group.isGroupOfItems());
        }
    }
    
    public void testAdditionOfGroupWhenChildItemsPresent() throws Exception {
        
        //Add an Item.
        Item item = new Item();
        group.includeItem(item);
        
        //Try adding a Group.
        ItemGroup group2 = new ItemGroup();
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
        ItemGroup group2 = new ItemGroup();
        group2.setName("name2");
        group.includeGroup(group2);
        
        //Try including group2 in another Group.
        ItemGroup group3 = new ItemGroup();
        group3.setName("name3");
        try {
            group3.includeGroup(group2);
            fail("An Exception should have been thrown.");
        } catch (GroupHierarchyException e) {
            assertTrue(group3.getConsistsOf().isEmpty());
            assertTrue(group3.isGroupOfItems());
        }
        
    }
    
    public void testFindTopMostParent() throws GroupInclusionException, GroupHierarchyException {
        ItemGroup g1 = new ItemGroup(), g2 = new ItemGroup(), g3 = new ItemGroup(), g4= new ItemGroup();
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

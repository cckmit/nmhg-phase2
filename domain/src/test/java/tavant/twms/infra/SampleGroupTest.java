package tavant.twms.infra;

import junit.framework.TestCase;

public class SampleGroupTest extends TestCase {

    public void testIncludeGroup() {
        SampleGroup fixture = new SampleGroup();
        fixture.setGroupName("parent");
        
        SampleGroup child = new SampleGroup();
        fixture.setGroupName("child");
        assertNull(child.getParent());
        assertFalse(fixture.includesGroup(child));
        
        fixture.includeGroup(child);
        assertEquals(fixture,child.getParent());
        assertTrue(fixture.includesGroup(child));
    }

    public void testExcludeGroup() {
        SampleGroup fixture = new SampleGroup();
        fixture.setGroupName("parent");
        
        SampleGroup child = new SampleGroup();
        fixture.setGroupName("child");
        assertNull(child.getParent());
        assertFalse(fixture.includesGroup(child));
        
        fixture.includeGroup(child);
        assertEquals(fixture,child.getParent());
        assertTrue(fixture.includesGroup(child));
        
        fixture.excludeGroup(child);
        
        assertNull(child.getParent());
        assertFalse(fixture.includesGroup(child));
        
    }

}

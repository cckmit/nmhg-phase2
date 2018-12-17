package tavant.twms.domain.orgmodel;

import java.util.Map;

import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.DomainRepositoryTestCase;

public class SupplierTest extends DomainRepositoryTestCase {
    
    OrgService orgService;
    
    public void testGetAllSuppliers() {
        assertEquals(4, orgService.findAllSuppliers().size());
    }
    public void testSupplierLocations() {
        //supplier is loaded with id 31
        Supplier supplier = (Supplier) getSession().load(Supplier.class, new Long(31));
        Map<String, Location> locations = supplier.getLocations();
        assertEquals(2, locations.size());
        assertTrue(locations.containsKey("BUSINESS"));
        assertTrue(locations.containsKey("RETAIL"));
        Location location1 = (Location) getSession().load(Location.class, new Long(4));
        Location location2 = (Location) getSession().load(Location.class, new Long(5));
        assertEquals(location1.getId(), locations.get("BUSINESS").getId());
        assertEquals(location2.getId(), locations.get("RETAIL").getId());
        
        //preferred location is 2
        assertEquals(location2, supplier.getPreferredLocation());
    }
    
    public void testSuppliers() {
        User user = (User) getSession().load(User.class, new Long(32));
        assertEquals("northwind", user.getName());
        //belongs to NORTH wind supplies organisation
        Organization org = user.getBelongsToOrganizations();
        assertTrue(org instanceof Supplier);
        assertEquals(31, org.getId().intValue());
        //user has the role of supplier
        assertTrue(user.hasRole("supplier"));
        
        user = (User) getSession().load(User.class, new Long(33));
        assertEquals("sra", user.getName());
        org = user.getBelongsToOrganizations();
        assertEquals(8, org.getId().intValue());
        //user has the role of supplier
        assertTrue(user.hasRole("sra"));
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}

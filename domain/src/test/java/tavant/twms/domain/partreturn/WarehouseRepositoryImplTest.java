package tavant.twms.domain.partreturn;

import java.util.List;

import tavant.twms.domain.orgmodel.Address;
import tavant.twms.infra.DomainRepositoryTestCase;

public class WarehouseRepositoryImplTest extends DomainRepositoryTestCase {
	private WarehouseRepository warehouseRepository;

	public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
		this.warehouseRepository = warehouseRepository;
	}
	
	public void testSimpleCreateAndCascades() {
		Address address = new Address();
		address.setAddressLine1("addressLine1");
		address.setCity("city");
		address.setState("state");
		address.setCountry("country");
		Warehouse warehouse = new Warehouse("code", address);
		warehouseRepository.save(warehouse);
		Long id = warehouse.getId();
		Warehouse warehouse2 = warehouseRepository.findById(id);
		assertEquals(warehouse, warehouse2);
		assertNotNull(warehouse2.getLocation().getId());
		assertNotNull(warehouse2.getLocation().getAddress().getId());
		warehouse.getLocation().getAddress().setCity("test");
		warehouseRepository.update(warehouse);
		warehouse2 = warehouseRepository.findById(id);
		assertEquals("test", warehouse2.getLocation().getAddress().getCity());
	}
	
	public void testFindByWarehouseCode() {
		Warehouse warehouse = warehouseRepository.findByWarehouseCode("IR_KEN");
		assertEquals(new Long("1"), warehouse.getId());
	}

    public void testFindWarehouseCodesStartingWith() {
    	List<String> codes = warehouseRepository.findWarehouseCodesStartingWith("IR");
    	assertEquals(1, codes.size());
    	assertTrue(codes.contains("IR_KEN"));
    }

    //Not testing for the receivers and the partShippers as it is the same logic.
	public void testGetInspectorAtLocation() {
		String inspectorAtLocation = warehouseRepository.getInspectorAtLocation(warehouseRepository.findById(1L).getLocation());
		assertEquals("testInspector", inspectorAtLocation);
	}	

}

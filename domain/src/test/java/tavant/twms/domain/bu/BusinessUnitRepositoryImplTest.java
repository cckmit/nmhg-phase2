package tavant.twms.domain.bu;

import java.util.List;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

public class BusinessUnitRepositoryImplTest extends DomainRepositoryTestCase{

	private BusinessUnitRepository businessUnitRepository;
	
	public BusinessUnitRepository getBusinessUnitRepository() {
		return businessUnitRepository;
	}

	public void setBusinessUnitRepository(
			BusinessUnitRepository businessUnitRepository) {
		this.businessUnitRepository = businessUnitRepository;
	}

	@SuppressWarnings("unused")
	public void testFindAllBusinessUnits()
	{
		 List<BusinessUnit> bus= businessUnitRepository.findAll();
		 assertEquals(4, bus.size());
	}
	
	public void testFindBusinessUnit()
	{
		BusinessUnit businessUnit= businessUnitRepository.findBusinessUnit("Bobcat");
		assertNotNull(businessUnit);
		assertEquals("Bobcat", businessUnit.getName());
	}
}

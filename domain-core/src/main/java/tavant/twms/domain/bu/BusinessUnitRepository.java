package tavant.twms.domain.bu;

import java.util.List;
import java.util.Set;

import tavant.twms.infra.GenericRepository;

public interface BusinessUnitRepository extends
		GenericRepository<BusinessUnit, String> {
	public List<BusinessUnit> findAllBusinessUnits();
	
	public BusinessUnit findBusinessUnit(String name);
	
	public DivisionBusinessUnitMapping findBusinessUnitForDivisionCode(final String divisionCode);

    public List<BusinessUnit> findBusinessUnitsForNames(final Set<String> buNames);

}

package tavant.twms.domain.bu;

import java.util.List;
import java.util.Set;

public interface BusinessUnitService {
	public List<BusinessUnit> findAllBusinessUnits();

	public BusinessUnit findBusinessUnit(String name);
	
	public DivisionBusinessUnitMapping findBusinessUnitForDivisionCode(final String divisionCode);

    public List<BusinessUnit> findBusinessUnitsForNames(final Set<String> buNames);
}

package tavant.twms.domain.bu;

import java.util.List;
import java.util.Set;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class BusinessUnitServiceImpl extends GenericServiceImpl implements
		BusinessUnitService {
	public BusinessUnitRepository businessUnitRepository;

	public BusinessUnitRepository getBusinessUnitRepository() {
		return businessUnitRepository;
	}

	public void setBusinessUnitRepository(
			BusinessUnitRepository businessUnitRepository) {
		this.businessUnitRepository = businessUnitRepository;
	}

	public List<BusinessUnit> findAllBusinessUnits() {
		return businessUnitRepository.findAll();
	}
	
	public BusinessUnit findBusinessUnit(String name)
	{
		return businessUnitRepository.findBusinessUnit(name);
	}
	
	public DivisionBusinessUnitMapping findBusinessUnitForDivisionCode(final String divisionCode){
		return businessUnitRepository.findBusinessUnitForDivisionCode(divisionCode);
	}

    public List<BusinessUnit> findBusinessUnitsForNames(final Set<String> buNames){
        return businessUnitRepository.findBusinessUnitsForNames(buNames);
    }

    @Override
	public GenericRepository getRepository() {
		return businessUnitRepository;
	}

}

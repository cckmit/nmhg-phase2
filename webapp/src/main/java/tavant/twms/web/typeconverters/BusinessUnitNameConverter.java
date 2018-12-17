package tavant.twms.web.typeconverters;

import java.util.ArrayList;
import java.util.Collection;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.ItemNotFoundException;

public class BusinessUnitNameConverter extends
		NamedDomainObjectConverter<BusinessUnitService, BusinessUnit> {

	public BusinessUnitNameConverter() {
		super("businessUnitService");
	}

	@Override
	public BusinessUnit fetchByName(String businessUnitName) {
		return getService().findBusinessUnit(businessUnitName);
	}

	@Override
	public String getName(BusinessUnit businessUnit) {
		return businessUnit.getDisplayName();
	}
	
	 @Override
	    public Collection<BusinessUnit> fetchByNames(String[] businessUnitNames)  {
	        if (businessUnitNames != null && businessUnitNames.length > 0) {
	            Collection<BusinessUnit> businessUnits = new ArrayList<BusinessUnit>();
	            for (int i = 0; i < businessUnitNames.length; i++) {
	                if (businessUnitNames[i] != null && !businessUnitNames[i].equals("")) {
	                	businessUnits.add(getService().findBusinessUnit(businessUnitNames[i]));
	                }
	            }
	            return businessUnits;
	        }
	        return null;
	    }

}

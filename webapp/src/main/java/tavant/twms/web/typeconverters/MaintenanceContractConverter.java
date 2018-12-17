package tavant.twms.web.typeconverters;


import org.springframework.util.StringUtils;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.policy.WarrantyService;

public class MaintenanceContractConverter extends NamedDomainObjectConverter<WarrantyService, MaintenanceContract> {
    public MaintenanceContractConverter() {
        super("warrantyService");
    }

    @Override
    public MaintenanceContract fetchByName(String name)  {
    	if (StringUtils.hasText(name) && !name.equals("null")) {
    		try {
    			  return getService().findMaintenanceContract(name);
            }
    		catch (Exception origEx) {
                TypeConversionException thrownEx = null;
                if (origEx instanceof TypeConversionException) {
                    thrownEx = (TypeConversionException) origEx;
                } else {
                    thrownEx = new TypeConversionException("Error converting " + name + " to maintenanceid" , origEx);
                }
                throw thrownEx;
            }
    	}
    	else {
            return null;
        }
    }

    @Override
    public String getName(MaintenanceContract maintenanceContract) {
        return maintenanceContract.getMaintenanceContract();
    }
}
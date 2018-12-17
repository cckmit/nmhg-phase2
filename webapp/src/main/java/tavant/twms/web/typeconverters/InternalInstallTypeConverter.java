package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.policy.WarrantyService;

public class InternalInstallTypeConverter extends NamedDomainObjectConverter<WarrantyService, InternalInstallType> {
    public InternalInstallTypeConverter() {
        super("warrantyService");
    }

    @Override
    public InternalInstallType fetchByName(String name)  {
    	if (StringUtils.hasText(name) && !name.equals("null")) {
    		try {
    			return getService().findInternalInstallType(name);
            }
    		catch (Exception origEx) {
                TypeConversionException thrownEx = null;
                if (origEx instanceof TypeConversionException) {
                    thrownEx = (TypeConversionException) origEx;
                } else {
                    thrownEx = new TypeConversionException("Error converting " + name + " to InternalInstallId" , origEx);
                }
                throw thrownEx;
            }
    	}
    	else {
            return null;
        }
        
    }

	@Override
	public String getName(InternalInstallType internalInstallType) throws Exception {
		return internalInstallType.getId().toString();
	}
}


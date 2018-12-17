package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.policy.WarrantyService;

public class ContractCodeConverter extends NamedDomainObjectConverter<WarrantyService, ContractCode> {
    public ContractCodeConverter() {
        super("warrantyService");
    }

    @Override
    public ContractCode fetchByName(String name)  {
    	if (StringUtils.hasText(name) && !name.equals("null")) {
    		try {
    			return getService().findContractCode(name);
            }
    		catch (Exception origEx) {
                TypeConversionException thrownEx = null;
                if (origEx instanceof TypeConversionException) {
                    thrownEx = (TypeConversionException) origEx;
                } else {
                    thrownEx = new TypeConversionException("Error converting " + name + " to contractId" , origEx);
                }
                throw thrownEx;
            }
    	}
    	else {
            return null;
        }
        
    }

    @Override
    public String getName(ContractCode contractCode) {
        return contractCode.getId().toString();
    }
}


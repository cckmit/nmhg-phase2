package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.policy.WarrantyService;

public class IndustryCodeConverter extends NamedDomainObjectConverter<WarrantyService, IndustryCode> {
    public IndustryCodeConverter() {
        super("warrantyService");
    }

    @Override
    public IndustryCode fetchByName(String name)  {
    	if (StringUtils.hasText(name) && !name.equals("null")) {
    		try {
    			 return getService().findIndustryCode(name);
            }
    		catch (Exception origEx) {
                TypeConversionException thrownEx = null;
                if (origEx instanceof TypeConversionException) {
                    thrownEx = (TypeConversionException) origEx;
                } else {
                    thrownEx = new TypeConversionException("Error converting " + name + " to industryId" , origEx);
                }
                throw thrownEx;
            }
    	}
    	else {
            return null;
        }
       
    }

    @Override
    public String getName(IndustryCode industryCode) {
        return industryCode.getIndustryCode();
    }
}
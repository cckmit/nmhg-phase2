package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import bsh.StringUtil;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;

public class FaultFoundConverter  extends NamedDomainObjectConverter <FailureStructureService, FailureTypeDefinition> {

    public FaultFoundConverter() {
        super("failureStructureService");
    }

    @Override
    public FailureTypeDefinition fetchByName(String faultFoundId) {
    	if(StringUtils.hasText(faultFoundId))
    	{	
    		return getService().findFaultFoundById(new Long(faultFoundId));
    	}
    	else 
    		return null;
    }

    @Override
    public String getName(FailureTypeDefinition faultFound) {
        return faultFound.getName();
    }
}

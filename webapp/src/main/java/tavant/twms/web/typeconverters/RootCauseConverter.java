package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;

public class RootCauseConverter extends NamedDomainObjectConverter <FailureStructureService, FailureRootCauseDefinition> {
    public RootCauseConverter() {
        super("failureStructureService");
    }

    /**
     * This class is obsolete, does not go the conversion any more
     */
    @Override
    public FailureRootCauseDefinition fetchByName(String rootCauseById) {
    	if(StringUtils.hasText(rootCauseById))
    	{
    		return getService().findRootCauseById(new Long(rootCauseById));
    	}
    	else
    	return null;
    }

    @Override
    public String getName(FailureRootCauseDefinition rootCause) {
        return rootCause.getName();
    }

}

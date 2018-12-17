package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;

public class CausedByConverter extends NamedDomainObjectConverter <FailureStructureService, FailureCauseDefinition> {

    public CausedByConverter() {
        super("failureStructureService");
    }

    /**
     * This class is obsolete, does not go the conversion any more
     */
    @Override
    public FailureCauseDefinition fetchByName(String causedById) {
    	if(StringUtils.hasText(causedById))
    	{
    		return getService().findCausedById(new Long(causedById));
    	}
    	else
    	return null;
    }

    @Override
    public String getName(FailureCauseDefinition causedBy) {
        return causedBy.getName();
    }
}

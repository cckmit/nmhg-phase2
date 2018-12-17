package tavant.twms.domain.bu;

import java.util.Date;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.infra.GenericRepository;

public interface ConfigValueRepository extends GenericRepository<ConfigValue, Long> {

    public Date getNextCreditSubmissionDate(final String buName);
    
}

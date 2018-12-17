package tavant.twms.domain.bu;

import java.util.Date;
import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.configuration.ConfigValue;

public interface ConfigValueService {
	
	@Transactional
	public ConfigValue findById(Long id);
	
	@Transactional
	public void save(ConfigValue configValue);

    @Transactional(readOnly=true)
    public Date getNextCreditSubmissionDate(String buName);
}

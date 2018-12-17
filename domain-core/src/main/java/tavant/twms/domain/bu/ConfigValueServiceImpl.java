package tavant.twms.domain.bu;

import java.util.Date;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.configuration.ConfigValue;

public class ConfigValueServiceImpl implements	ConfigValueService {
	
	private ConfigValueRepository configValueRepository;
	
	@Transactional
	public ConfigValue findById(Long id) {		
		return configValueRepository.findById(id);
	}
	
	@Transactional
	public void save(ConfigValue configValue) {
		configValueRepository.save(configValue);
	}

	@Required
	public void setConfigValueRepository(ConfigValueRepository configValueRepository) {
		this.configValueRepository = configValueRepository;
	}

    @Transactional(readOnly=true)
    public Date getNextCreditSubmissionDate(String buName){
        return configValueRepository.getNextCreditSubmissionDate(buName);
    }
}

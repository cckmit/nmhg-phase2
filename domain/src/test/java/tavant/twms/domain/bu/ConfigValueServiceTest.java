package tavant.twms.domain.bu;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.infra.DomainRepositoryTestCase;

public class ConfigValueServiceTest extends DomainRepositoryTestCase {

	private ConfigValueService configValueService;

	@Required
	public void setConfigValueService(ConfigValueService configValueService) {
		this.configValueService = configValueService;
	}

	public void testSaveConfigValue() {
		ConfigValue configValue = configValueService.findById(new Long(15));
		configValue.setValue("true");
		configValueService.save(configValue);
		ConfigValue updatedConfigValue = configValueService.findById(new Long(
				15));
		assertEquals(true, updatedConfigValue.getValue().equals("true"));
	}

}

package tavant.twms.domain.configuration;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface ConfigParamRepository extends GenericRepository<ConfigParam,Long> {
	
	public void save(ConfigParam buConfig);

	public void update(ConfigParam buConfig);
	
	public void delete(ConfigParam buConfig);
	
	public ConfigParam findConfig(String name);

    public ConfigParam reloadConfig(String name);
    
    public List<ConfigValue> getValuesForConfigParam(final String configParamName);
    
    public List<ConfigValue> getValuesForConfigParamByBU(final String configParamName,final String BUName);
	
}

package tavant.twms.domain.configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.bu.BusinessUnit;

@Transactional(readOnly = true)
public interface ConfigParamService {

	@Transactional(readOnly = false)
	void saveConfig(ConfigParam config);

	@Transactional(readOnly = false)
	void updateConfig(ConfigParam config);

	@Transactional(readOnly = false)
	void deleteConfig(ConfigParam config);

	public List<ConfigParam> findAll();

	public Boolean  getBooleanValue( String name);

	public Long  getLongValue(String name);

	public List<ListOfValues>  getListOfValues(String name);

	public List<Object>  getListofObjects(final String paramName);

	public String getStringValue(String paramName);
	
	public String getStringValueByBU(String paramName,String BUName);

	public Map<Object, Object> getKeyValuePairOfObjects(final String paramName);

	public ConfigParam getConfigParamByName(final String paramName);
	
	public Map<String, List<Object>> getValuesForAllBUs(String paramName);	
	
	public ListOfValues findObjectForLovCode(String value, Class toType);
	
	public ListOfValues findObjectForLovId(Long id, Class toType);
	
    public List<BusinessUnit> configApplicableForBuWithValue(String configName, String value);
    
    public List<ConfigValue> getValuesForConfigParam(final String configName);

	public List<ConfigValue> getConfiguredRolesToBeDisplayed(String bu);
	
	public BigDecimal getBigDecimalValue(String paramName);

}

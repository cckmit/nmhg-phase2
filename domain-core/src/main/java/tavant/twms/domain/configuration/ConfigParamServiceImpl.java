package tavant.twms.domain.configuration;

import java.math.BigDecimal;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.infra.DomainRepository;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public class ConfigParamServiceImpl implements ConfigParamService {

	private static Logger logger = LogManager
			.getLogger(ConfigParamServiceImpl.class);

	private ConfigParamRepository configParamRepository;

	private LovRepository lovRepository;

	private DomainRepository domainRepository;

     private BusinessUnitService businessUnitService;

    public ConfigParamRepository getConfigParamRepository() {
		return this.configParamRepository;
	}

	public void setConfigParamRepository(
			ConfigParamRepository configParamRepository) {
		this.configParamRepository = configParamRepository;
	}

	public void saveConfig(ConfigParam config) {
		this.configParamRepository.save(config);
	}

	public void updateConfig(ConfigParam config) {
		this.configParamRepository.update(config);
	}

	public void deleteConfig(ConfigParam config) {
		this.configParamRepository.delete(config);
	}

	public List<ConfigParam> findAll() {
		return this.configParamRepository.findAll();
	}

	public Boolean getBooleanValue(String paramName) {
		/*
		 * ConfigParam configParam =
		 * this.configParamRepository.findConfig(paramName); if
		 * (configParam==null || (configParam!=null &&
		 * CollectionUtils.isEmpty(configParam.getValues()))) { throw new
		 * NoValuesDefinedException(paramName); }
		 */
		return new Boolean(getStringValue(paramName));
	}

	public Long getLongValue(String paramName) {
		/*
		 * ConfigParam configParam =
		 * this.configParamRepository.findConfig(paramName); if
		 * (configParam==null || (configParam!=null &&
		 * CollectionUtils.isEmpty(configParam.getValues()))) { throw new
		 * NoValuesDefinedException(paramName); }
		 */
		return new Long(getStringValue(paramName));
	}

	public String getStringValue(String paramName) {
		List<ConfigValue> configValues = this.getValuesForConfigParam(paramName);
		if (configValues == null
				|| CollectionUtils.isEmpty(configValues)) {
			throw new NoValuesDefinedException(paramName);
		}
		//ESESA-1758
        for(ConfigValue configValue:configValues){
        	if(configValue.getConfigParamOption() != null) {
        		if ("true".equals(configValue.getConfigParamOption().getValue())) {        	
        			return configValue.getConfigParamOption().getValue();
        		}else
        			continue;
        	}else {
    			return configValues.get(0).getValue();
    		}
        }
/*		if (configValues.get(0).getConfigParamOption() != null) {
			return configValues.get(0).getConfigParamOption()
					.getValue();
		} */
        
        return configValues.get(0).getConfigParamOption().getValue();	
	}
	
	

	public Map<String, List<Object>> getValuesForAllBUs(String paramName) {
        String selectedBusinessUnit = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
        SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
		
		List<ConfigValue> configValues = this.getValuesForConfigParam(paramName);
		Map<String, List<Object>> buConfigValueMap = new HashMap<String, List<Object>>();
		if (configValues == null
				|| CollectionUtils.isEmpty(configValues)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
            throw new NoValuesDefinedException(paramName);
		}
		List<ConfigValue> allParamValues = configValues;
		for (ConfigValue configValue : allParamValues) {
			String buName = configValue.getBusinessUnitInfo().getName();
			if (configValue.getConfigParamOption() != null) {
				if (buConfigValueMap.containsKey(buName)) {
					buConfigValueMap.get(buName).add(
							configValue.getConfigParamOption().getValue());
				} else {
					List<Object> valuesForBu = new ArrayList<Object>();
					valuesForBu.add(configValue.getConfigParamOption()
							.getValue());
					buConfigValueMap.put(buName, valuesForBu);
				}
			} else {
				if (buConfigValueMap.containsKey(buName)) {
					buConfigValueMap.get(buName).add(configValue.getValue());
				} else {
					List<Object> valuesForBu = new ArrayList<Object>();
					valuesForBu.add(configValue.getValue());
					buConfigValueMap.put(buName, valuesForBu);
				}
			}
		}
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
        return buConfigValueMap;
	}

	public ConfigParam getConfigParamByName(final String paramName) {
		return this.configParamRepository.findConfig(paramName);
	}

	public List<ListOfValues> getListOfValues(String paramName) {
		List<ListOfValues> list = new ArrayList<ListOfValues>(5);		
		List<ConfigValue> configValues = this.getValuesForConfigParam(paramName);
		if (configValues == null
				|| CollectionUtils.isEmpty(configValues)) {
			return null;
		}
		ConfigParam configParam = configValues.get(0).getConfigParam();
		/**
		 * There is an open bug in hibernate in caching bi-directional
		 * inverse relation. Hence configParam on config value turns out null 
		 * when any updation happens and second level cache is referred there after
		 *  
		 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2350
		 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-5087
		 * 
		 * So doing look up by name if configParam is not found
		 */
		if(configParam == null){
			configParam = this.configParamRepository.findConfig(paramName);
		}
		try {
			for (ConfigValue configValue : configValues) {
				if (configValue.getActive()) {
					String value = null;
					if (configValue.getValue() != null) {
						value = configValue.getValue();
						list.add(findObjectForLovId(Long.parseLong(value),
								Class.forName(configParam.getType())));
					} else if (configValue.getConfigParamOption() != null) {
						value = configValue.getConfigParamOption().getValue();
						list.add(findObjectForLovCode(value, Class
								.forName(configParam.getType())));
					}
				}
			}
		} catch (ClassNotFoundException exception) {
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration with name" + paramName
						+ "has invalid type");
			}
		} catch (NumberFormatException formatException) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("Configuration with name"
								+ paramName
								+ "has invalid value. We are supposed to have only id's of Lov in value");
			}
		}
		return list;
	}

	public ListOfValues findObjectForLovId(Long id, Class toType) {
		Assert.notNull(this.lovRepository, "Lov repository is null");
		return this.lovRepository.findById(toType.getSimpleName(), id);
	}

	public ListOfValues findObjectForLovCode(String value, Class toType) {
		Assert.notNull(this.lovRepository, "Lov repository is null");
		return this.lovRepository.findByCode(toType.getSimpleName(), value);

	}

	public List<Object> getListofObjects(String paramName) {
		List<Object> list = new ArrayList<Object>(5);		
		List<ConfigValue> configValues = this.getValuesForConfigParam(paramName);
		if (configValues == null
				|| (CollectionUtils.isEmpty(configValues))) {
			return null;
		}
		ConfigParam configParam = configValues.get(0).getConfigParam();		
		
		if(configParam == null){
			configParam = this.configParamRepository.findConfig(paramName);
		}
		try {
			for (ConfigValue configVal : configValues) {
				String value = null;
				if (configVal.getConfigParamOption() != null) {
					value = configVal.getConfigParamOption().getValue();
				} else {
					value = configVal.getValue();
				}
				if (configVal.getActive()) {
					if (Class.forName(configParam.getType()) == String.class) {
						list.add(value);
					} else {
						list.add(findObjectForValue(value, Class
								.forName(configParam.getType())));
					}
				}
			}
		} catch (ClassNotFoundException exception) {
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration with name" + paramName
						+ "has invalid type");
			}
		}
		return list;
	}

	private Object findObjectForValue(String value, Class toType) {
		Assert.notNull(this.domainRepository, "Domain repository is null");
		return this.domainRepository.load(toType, new Long(value));

	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	public Map<Object, Object> getKeyValuePairOfObjects(String paramName) {
		Map<Object, Object> keyValue = new HashMap<Object, Object>();		
		List<ConfigValue> configValues = this.getValuesForConfigParam(paramName);
		ConfigParam configParam = null;
		if(!configValues.isEmpty()){
			configParam = configValues.get(0).getConfigParam();
		}
		if(configParam == null){
			configParam = this.configParamRepository.findConfig(paramName);
		}
		if (configParam == null
				|| (CollectionUtils.isEmpty(configValues))) {
			return null;
		}
		try {
			for (ConfigValue configVal : configValues) {
				String value = null;
				String key = null;
				if (configVal.getConfigParamOption() != null) {
					key = configVal.getConfigParamOption().getValue();
					value = configVal.getConfigParamOption().getDisplayValue();
				} else {
					key = configVal.getValue();
					value = configVal.getValue();
				}
				if (configVal.getActive()) {
					if (Class.forName(configParam.getType()) == String.class) {
						keyValue.put(key, value);
					} else {
						keyValue.put(findObjectForValue(value, Class
								.forName(configParam.getType())),
								findObjectForValue(value, Class
										.forName(configParam.getType())));
					}
				}
			}
		} catch (ClassNotFoundException exception) {
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration with name" + paramName
						+ "has invalid type");
			}
		}
		return keyValue;
	}

    public List<BusinessUnit> configApplicableForBuWithValue(String configName,String value){        
        List<ConfigValue> configValues = this.getValuesForConfigParam(configName);
        Set<String> buNames = new HashSet<String>();
        for (ConfigValue configValue : configValues) {
            if(value.equals(configValue.getConfigParamOption().getValue())){
                buNames.add(configValue.getBusinessUnitInfo().getName());
            }
        }
        return businessUnitService.findBusinessUnitsForNames(buNames);
    }

    public void setBusinessUnitService(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }
    
    public List<ConfigValue> getValuesForConfigParam(final String configName){
    	return configParamRepository.getValuesForConfigParam(configName);
    }
    
    public List<ConfigValue> getValuesForConfigParamByBU(final String configName,final String BUName){
    	return configParamRepository.getValuesForConfigParamByBU(configName,BUName);
    }

	public List<ConfigValue> getConfiguredRolesToBeDisplayed(String bu) {
		ConfigParam configParam = this.getConfigParamByName(ConfigName.ROLES_TO_BE_DISPLYED.getName());
		List<ConfigValue> configValues = new ArrayList<ConfigValue>();
		for(ConfigValue configValue:configParam.getValues())
		{
			if(bu.equals(configValue.getBusinessUnitInfo().getName()) && configValue.getD().isActive())
				configValues.add(configValue);
		}
		return configValues;
	}

	public String getStringValueByBU(String paramName, String BUName) {
		List<ConfigValue> configValues = this
				.getValuesForConfigParamByBU(paramName,BUName);
		if (configValues == null || CollectionUtils.isEmpty(configValues)) {
			throw new NoValuesDefinedException(paramName);
		}

		if (configValues.get(0).getConfigParamOption() != null) {
			return configValues.get(0).getConfigParamOption().getValue();
		} else {
			return configValues.get(0).getValue();
		}

	}
	
	public BigDecimal getBigDecimalValue(String paramName){
		//pattern matching for a decimal value, to avoid exceptions.
		return getStringValue(paramName).matches("^\\d+(\\.\\d{1,2})?$")?new BigDecimal(getStringValue(paramName)):new BigDecimal(0.00);
	}
}

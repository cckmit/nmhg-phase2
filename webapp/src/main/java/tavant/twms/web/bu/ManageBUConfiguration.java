package tavant.twms.web.bu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamOption;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ManageBUConfiguration extends ActionSupport {

	private static Logger logger = LogManager.getLogger(ManageBUConfiguration.class);
	ConfigParamService configParamService = null;
	List<ConfigParam> paramList = null;

    public static String LOGICAL_GROUP_CLAIMS="CLAIMS";
    public static String SECTION_HOME_PAGE="HOME_PAGE";
    public static String SECTION_CLAIM_INPUT="CLAIM_INPUT_PARAMETERS";
    public static String SECTION_CLAIM_SUBMIT="CLAIM_SUBMISSION";
    public static String SECTION_CLAIM_PROCESS="CLAIM_PROCESS";
    public static String SECTION_CLAIM_RETURN="CLAIM_RETURN_PART";
    public static String SECTION_CLAIM_FIELD="CLAIM_FIELD_MOD";
    public static String SECTION_CLAIM_FOC="CLAIM_FOC";
    public static String SECTION_CLAIM_DISLAY="CLAIM_DISPLAY";

    public static String LOGICAL_GROUP_INVENTORY="INVENTORY";
    public static String SECTION_INVENTORY_DR_ETR="INVENTORY_DR_ETR";
    public static String SECTION_INVENTORY_DR="INVENTORY_DR";
    public static String SECTION_INVENTORY_ETR="INVENTORY_ETR";
    public static String SECTION_INVENTORY_SEARCH="INVENTORY_SEARCH";

    public static String LOGICAL_GROUP_OTHERS="OTHERS";
    public static String SECTION_OTHERS_MODIFIERS="OTHERS_MODIFIERS";
    public static String SECTION_OTHERS_WARRANTY_PLAN="OTHERS_WARRANTY_PLAN";

    public static String LOGICAL_GROUP_RECOVERY="SUPPLIER_RECOVERY";
    
    private LovRepository lovRepository;
    
    public String execute() {
		prepareData();
		return SUCCESS;
	}
    
    /*
     * Here we prepare the config param if it LOV.
     * LOV's of the configparam.type are fetched 
     * and configparamoptions are created with the fetched LOVs   
     * */
    
    private void prepareParamForLOV(ConfigParam configParam){					
    	try {
    		List<String> selectedValues = new ArrayList<String>();
    		if(configParam.getValues()!=null){
    			for (ConfigValue cfgValue : configParam.getValues()) {
    				if(cfgValue.getValue() !=null){
    					selectedValues.add(String.valueOf(cfgValue.getValue()));
    				}
    			}
    			List<ConfigParamOption> lovConfigParamOptions = new ArrayList<ConfigParamOption>();
    			configParam.setParamOptions(lovConfigParamOptions);
    			List<ListOfValues> lovs;
    			lovs = lovRepository.findAllActive(Class.forName(configParam.getType()).getSimpleName());
    			for(ListOfValues lov :lovs){
    				ConfigParamOption paramOption = new ConfigParamOption();
    				paramOption.setId(lov.getId());
    				paramOption.setDisplayValue(lov.getDescription());
    				paramOption.setValue(lov.getCode());
    				lovConfigParamOptions.add(paramOption);
    			}
    			configParam.setDbParameterValues(selectedValues);
    		}
    	} catch (ClassNotFoundException e) {
    		// ignore
    	}
    }
    
    private void prepareParamForSelect(ConfigParam configParam){
    	if (configParam.getValues() != null
    			&& configParam.getValues().size() > 0) {
    		List<String> selectedValues = new ArrayList<String>();
    		for (ConfigValue cfgValue : configParam.getValues()) {
    			if(cfgValue.getConfigParamOption() !=null){
    				selectedValues.add(String.valueOf(cfgValue
    						.getConfigParamOption().getId()));
    			}
    		}
    		configParam.setDbParameterValues(selectedValues);
    	}

    }

	private void prepareData() {
		List<ConfigParam> pList = configParamService.findAll();
		paramList = new ArrayList<ConfigParam>();
		for (ConfigParam configParam : pList) {
				if (StringUtils.hasText(configParam.getParamDisplayType())) {
						if(!paramList.contains(configParam)){
							paramList.add(configParam);			
							if (configParam.getParamDisplayType().contains("select")									) {
								try {
									if(configParam.getType().contains("tavant.twms.domain.") && 
											Class.forName(configParam.getType()).newInstance() instanceof ListOfValues){
										prepareParamForLOV(configParam);
									}else{
										prepareParamForSelect(configParam);
									}
								} catch (InstantiationException e) {
									// ignore
								} catch (IllegalAccessException e) {
									// ignore
								} catch (ClassNotFoundException e) {
									// ignore
								}
							}
						}
				}
		}
	}
	
	private boolean validateShipmentDateForVintageStock(ConfigParam configParam){
		for(String value : configParam.getDbParameterValues()){
			try{
				if (false == StringUtils.hasText(value)) {
					// Value that came was empty or null
					return true;
				}
				
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				df.setLenient(false);
                Date parsedDate = df.parse(value);
				Calendar cal = Calendar.getInstance();
				cal.setTime(parsedDate);
				int parsedYear = cal.get(Calendar.YEAR);
				if(parsedYear > 9999){
					// Checking for this as setting a year greater than 9999 succeeds, but it
					// results in an exception. Technically year greater than 9999 is a valid year
					// and should be OK so this needs to be analysed. But as this feature would
					// be used occasionally, this may not be done
					return false;
				}
			}catch(ParseException pe){
				logger.error("Error while updating Business Configuration."
						+ " Parse Exception occured while parsing the config value for Shipment Date for Vintage Stock." 
						+ " Reason : Invalid date entered by the user. Date Format should be dd/MM/yyyy"
						+ " Value entered by the user : " + value);
				return false;
			}catch(NullPointerException npe){
				logger.error("Error while updating Business Configuration."
						+ " Null Pointer Exception occured while parsing the config value for Shipment Date for Vintage Stock."
						+ " Reason : No Value entered by the user for the Configuration Shipment Date for Vintage Stock");
				return false;
			}
		}
		return true;
	}

	public String save() {	
		
		List<ConfigParam> pList = configParamService.findAll();
		List<ConfigParam> updateList = new ArrayList<ConfigParam>(); 
		
		for (ConfigParam uiValue : paramList) {
			if(uiValue!=null){
				int index = pList.indexOf(uiValue);
				ConfigParam configParam =pList.get(index);
				configParam.setDbParameterValues(uiValue.getDbParameterValues());
				if(configParam.getName().equals(ConfigName.SHIPMENT_DATE_FOR_VINTAGE_STOCK.getName())){
					if(!validateShipmentDateForVintageStock(configParam)){
						addActionError(getText("error.buAdmin.vintageShipmentDateInvalid"));
						return ERROR;
					}
				}
				List<ConfigValue> valueList = new ArrayList<ConfigValue>();
				boolean hasDataChanged = false;
				if(isDataValid(configParam)){
					if (configParam.getParamDisplayType().equals("textbox")) {
						hasDataChanged = populateConfigValueForTextbox(configParam, valueList);
					} else if (configParam.getParamDisplayType().equals("radio")) {
						hasDataChanged =  populateConfigValuesForRadio(configParam, valueList);
					} else if (configParam.getParamDisplayType().contains("select")) {
						hasDataChanged =  populateConfigValuesForSelectType(configParam,
								valueList);
					}			        
				}
				if(hasDataChanged){
					configParam.getValues().clear();
			        configParam.getValues().addAll(valueList);
			        updateList.add(configParam);
				}
		
		}
		}
		
		for (ConfigParam configParam : updateList) {
			configParamService.updateConfig(configParam);
		}
		
		return SUCCESS;
	}
	
	private boolean populateConfigValuesForLOVType(ConfigParam configParam,
			List<ConfigValue> valueList) {
		List<String> selectedOptions=new ArrayList<String>();
		
		if(configParam.getDbParameterValues()!=null && configParam.getDbParameterValues().size()>0){			
			for (String optionId : configParam.getDbParameterValues()) {				
				selectedOptions.add(optionId);
				ConfigValue cfval = new ConfigValue();
				cfval.setActive(Boolean.TRUE);
				cfval.setValue(optionId);
				valueList.add(cfval);
			}			
		}
		
       int dbOptionsSize = 0;      
       if(configParam.getValues()!=null && configParam.getValues().size()>0){
    	   dbOptionsSize = configParam.getValues().size();
       }
		
       if(dbOptionsSize != selectedOptions.size()){
    	   return true;
       } else{
    	   
    	   for (ConfigValue value : configParam.getValues()) {
			 if(!selectedOptions.contains(value.getValue())){
				return true; 
			 }    		   
    	   }    	   
    	   return false;
       }
	}

	private boolean populateConfigValuesForSelectType(ConfigParam configParam,
			List<ConfigValue> valueList) {		
		try {
			if(configParam.getType().contains("tavant.twms.domain.")
					&& Class.forName(configParam.getType()).newInstance() instanceof ListOfValues){
				return populateConfigValuesForLOVType( configParam, valueList);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<ConfigParamOption> selectedOptions=new ArrayList<ConfigParamOption>();
		
		if(configParam.getDbParameterValues()!=null && configParam.getDbParameterValues().size()>0){
			
			for (String optionId : configParam.getDbParameterValues()) {
				Long id = Long.parseLong(optionId);
				ConfigParamOption selectedOption = null;
				for (ConfigParamOption option : configParam.getParamOptions()) {
					if(id.longValue() == option.getId().longValue()){
						selectedOption = option;
						break;
					}					
				}				
				if(selectedOption == null){
					logger.error("Error for param id ="+ configParam.getId());
				} else{
						selectedOptions.add(selectedOption);
						ConfigValue cfval = new ConfigValue();
						cfval.setActive(Boolean.TRUE);
						cfval.setConfigParamOption(selectedOption);
						valueList.add(cfval);
				}								

			}			
		}
		
       int dbOptionsSize = 0;      
       if(configParam.getValues()!=null && configParam.getValues().size()>0){
    	   dbOptionsSize = configParam.getValues().size();
       }
		
       if(dbOptionsSize != selectedOptions.size()){
    	   return true;
       } else{
    	   
    	   for (ConfigValue value : configParam.getValues()) {
			 if(!selectedOptions.contains(value.getConfigParamOption())){
				return true; 
			 }    		   
    	   }
    	   
    	   return false;
       }
       
       
       			
	}

	private boolean populateConfigValuesForRadio(ConfigParam configParam,
			List<ConfigValue> valueList) {
		
		Assert.isTrue(configParam.getParamOptions()!=null,"No Param options mapped for configParam id ="+configParam.getId());
		Assert.isTrue(configParam.getParamOptions().size() > 0,"No Param options mapped for configParam id ="+configParam.getId());
		// this can be removed later
		Assert.isTrue(configParam.getParamOptions().size() == 2,"Exactly 2 options have to be mapped for configParam id ="+configParam.getId());
		
		String uiValue = null;
		if(configParam.getDbParameterValues()!=null){				
			Assert.isTrue(configParam.getDbParameterValues().size() == 1);				
            uiValue = configParam.getDbParameterValues().iterator().next();
		}
		
		if(uiValue!=null){

			ConfigValue dbValue = null;
			if(configParam.getValues()!=null && configParam.getValues().size()>0){
				/*Assert.isTrue(configParam.getValues().size()<=1,"Multiple values are set for config param of radio type and id ="+configParam.getId());*/
				if(configParam.getValues().size()>1){
					logger.error("Multiple values are set for config param of radio type and id ="+configParam.getId());
				}
				dbValue= configParam.getValues().iterator().next();			
			}			
			
			ConfigParamOption selectedOption = null;
			for (ConfigParamOption configParamOption : configParam
					.getParamOptions()) {
				if (configParamOption.getValue().equalsIgnoreCase(uiValue)) {
					selectedOption = configParamOption;
					break;
				}
			}
			Assert.isTrue(selectedOption!=null,"Improper options mapped for configParam id ="+configParam.getId());
			
			if(dbValue==null){
				ConfigValue cfValue = new ConfigValue();
				cfValue.setActive(Boolean.TRUE);
				cfValue.setConfigParamOption(selectedOption);
				valueList.add(cfValue);
                   return true;				
			} else if (dbValue.getConfigParamOption().getId().longValue() != selectedOption.getId().longValue()){
				ConfigValue cfValue = new ConfigValue();
				cfValue.setActive(Boolean.TRUE);
				cfValue.setConfigParamOption(selectedOption);
				valueList.add(cfValue);
                 return true;						
			}
			
		
		}
		
		return false;
	}

	private boolean populateConfigValueForTextbox(ConfigParam configParam,
			List<ConfigValue> valueList) {
		String uiValue = null;
		if(configParam.getDbParameterValues()!=null){				
			Assert.isTrue(configParam.getDbParameterValues().size() == 1);				
            uiValue = configParam.getDbParameterValues().iterator().next();
		}
		
		
		ConfigValue dbValue = null;
		if(configParam.getValues()!=null && configParam.getValues().size()>0){
			Assert.isTrue(configParam.getValues().size()==1);
			dbValue= configParam.getValues().iterator().next();			
		}
		if(StringUtils.hasText(uiValue)){
			// check if value is changed
			if(dbValue == null){
				ConfigValue cfValue = new ConfigValue();
				cfValue.setActive(Boolean.TRUE);
				cfValue.setValue(org.apache.commons.lang.StringUtils.trim(uiValue));
				valueList.add(cfValue);
				return true;
			} else if (!dbValue.getValue().equals(org.apache.commons.lang.StringUtils.trim(uiValue))){
				ConfigValue cfValue = new ConfigValue();
				cfValue.setActive(Boolean.TRUE);
				cfValue.setValue(org.apache.commons.lang.StringUtils.trim(uiValue));
				valueList.add(cfValue);
				return true;
			}
		} else if (dbValue != null){
			ConfigValue cfValue = new ConfigValue();
			cfValue.setActive(Boolean.TRUE);
			cfValue.setValue("");
			valueList.add(cfValue);
			return true;
		}
		
		return false;
	}

    
    public boolean isDataValid(ConfigParam param) {
	    boolean isValid = false;
		if (param.getDbParameterValues() != null && param.getDbParameterValues().size() > 0) {
			for (String val : param.getDbParameterValues()) {
				if (!org.apache.commons.lang.StringUtils.isBlank(val)) {
					isValid = true;
					break;
				}
			}
			//return true when no values are selected - fix for NMHGSLMS-1260
		}else{
			if(param.getParamDisplayType().contains("select") && null == param.getDbParameterValues() && null != param.getValues() && param.getValues().size()>0){				
				   isValid= true;
				}			
		}		
		return isValid;
    }

	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public List<ConfigParam> getParamList() {
		return paramList;
	}

	public void setParamList(List<ConfigParam> paramList) {
		this.paramList = paramList;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

}
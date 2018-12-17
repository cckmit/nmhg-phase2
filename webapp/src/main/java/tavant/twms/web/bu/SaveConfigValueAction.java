package tavant.twms.web.bu;

import org.acegisecurity.context.SecurityContextHolder;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import tavant.twms.domain.bu.ConfigValueService;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.model.OrgAwareUserDetails;
import tavant.twms.taglib.ConfigDisplayComponent;

import com.opensymphony.xwork2.Action;

public class SaveConfigValueAction implements Action {

	private long id;

	private String value;
	private String jsonString;

	private ConfigValueService configValueService;
	private MessageSource messageSource;
	private SecurityHelper securityHelper;

	@Required
	public void setConfigValueService(ConfigValueService configValueService) {
		this.configValueService = configValueService;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String execute() throws Exception {
		ConfigValue configValue = configValueService
				.findById(new Long(getId()));
		User user = securityHelper.getLoggedInUser();
		/*ConfigParam param = configValue.getConfigParam();*/
		ConfigParam param = null;
		String type = param.getType();
		
		if (ConfigDisplayComponent.BOOLEAN_INPUT.equalsIgnoreCase(type)) {
			configValue.setValue(value);			
		}else if (ConfigDisplayComponent.NUMBER_INPUT.equalsIgnoreCase(type)) {
			try {
				Long.parseLong(value);
				configValue.setValue(value);
			} catch (NumberFormatException ne) {
				JSONArray oneEntry = new JSONArray();
				oneEntry.put(messageSource.getMessage("errorMessage.invalidInput.number",null, 
						user.getLocale()));
				jsonString = oneEntry.toString();
			}
		}else if(ConfigDisplayComponent.LIST_INPUT.equalsIgnoreCase(type)){
			configValue.setActive(Boolean.parseBoolean(value));			
		}
		configValueService.save(configValue);
		return SUCCESS;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}	

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}	
}

package tavant.twms.web.bu;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import tavant.twms.domain.bu.LogicalGroup;
import tavant.twms.domain.bu.LogicalGroupService;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;

import com.opensymphony.xwork2.Action;

@SuppressWarnings("serial")
public class ListBuConfigurationsAction implements Action{
	
	private List<LogicalGroup> logicalGroups ;	
	
	public LogicalGroupService logicalGroupService;
	
	public ConfigParamService configParamService;
	
	public List<ConfigParam> configParamList = new ArrayList<ConfigParam>(); 

	public String execute() throws Exception {
		// Load the list of business unit configurations
		List<LogicalGroup> lgroups = getLogicalGroupService().findAll();
		if (lgroups != null && !lgroups.isEmpty()) {
			logicalGroups = new ArrayList<LogicalGroup>();
			logicalGroups.addAll(lgroups);
		}
		return SUCCESS;
	}

	public List<LogicalGroup> getLogicalGroups() {
		return logicalGroups;
	}

	public void setLogicalGroups(List<LogicalGroup> logicalGroups) {
		this.logicalGroups = logicalGroups;
	}

	public LogicalGroupService getLogicalGroupService() {
		return logicalGroupService;
	}

	public void setLogicalGroupService(LogicalGroupService logicalGroupService) {
		this.logicalGroupService = logicalGroupService;
	}

	public void setServletContext(ServletContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

}

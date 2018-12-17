/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;

import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.web.actions.TwmsActionSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class ManageDealerGroups extends TwmsActionSupport implements Preparable {
    
    private static final String DEALER = "dealer";
    private static final String GROUP = "group";
    
    private String id;

    private DealerGroupService dealerGroupService;
    private DealerSchemeService dealerSchemeService;

    private DealerGroup dealerGroup;
    private DealerScheme dealerScheme;

    private Set<ServiceProvider> includedDealers;
    private Set<DealerGroup> includedGroups;
    
    private String buttonLabel;
    private String sectionTitle;
    
    private ConfigParamService configParamService;
	private Boolean dealerGroupConfigParam;
	private static final String DEALER_GROUP_CODE = "dealerGroupCode";


    public void prepare() throws Exception {
        buttonLabel = getText("button.manageGroup.updateDealerGroup");
        sectionTitle = getText("title.manageGroup.updatingDealerGroup");
        
        Long idToBeUsed = null;
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if ((dealerGroup != null) && (dealerGroup.getId() != null)) {
            idToBeUsed = dealerGroup.getId();
        }
        if (idToBeUsed != null) {
            dealerGroup = dealerGroupService.findById(idToBeUsed);
        }
        if (dealerGroup != null && dealerGroup.getId() != null) {
            dealerScheme = dealerGroup.getScheme();
            sectionTitle = getText("title.manageGroup.dealerGroup") + " - " + dealerGroup.getName() + " - " + dealerGroup.getDescription()+ " - " + dealerGroup.getCode();
        }
        /*if(dealerGroupConfigParam == null)
                dealerGroupConfigParam = this.configParamService.getBooleanValue(DEALER_GROUP_CODE);*/
    }
    
    public String showGroup() {
        if (dealerGroup.isGroupOfDealers()) {
            includedDealers = new HashSet<ServiceProvider>();
            for (ServiceProvider aDealer : dealerGroup.getIncludedDealers()) {
            	if(aDealer.getD().isActive())
            		includedDealers.add(aDealer);
            }
            return DEALER;  
        } else {
            includedGroups = new HashSet<DealerGroup>();
            includedGroups.addAll(dealerGroup.getConsistsOf());
            return GROUP;
        }
    }
    
    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String tableTitle) {
        this.sectionTitle = tableTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DealerGroup getDealerGroup() {
        return dealerGroup;
    }

    public void setDealerGroup(DealerGroup dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

    public DealerScheme getDealerScheme() {
        return dealerScheme;
    }

    public void setDealerScheme(DealerScheme dealerScheme) {
        this.dealerScheme = dealerScheme;
    }

    public Set<ServiceProvider> getIncludedDealers() {
        return includedDealers;
    }

    public void setIncludedDealers(Set<ServiceProvider> includedDealers) {
        this.includedDealers = includedDealers;
    }

    public Set<DealerGroup> getIncludedGroups() {
        return includedGroups;
    }

    public void setIncludedGroups(Set<DealerGroup> includedGroups) {
        this.includedGroups = includedGroups;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setDealerSchemeService(DealerSchemeService dealerSchemeService) {
        this.dealerSchemeService = dealerSchemeService;
    }

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public DealerSchemeService getDealerSchemeService() {
		return dealerSchemeService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public Boolean getDealerGroupConfigParam() {
		return dealerGroupConfigParam;
	}

	public void setDealerGroupConfigParam(Boolean dealerGroupConfigParam) {
		this.dealerGroupConfigParam = dealerGroupConfigParam;
	}

    
}

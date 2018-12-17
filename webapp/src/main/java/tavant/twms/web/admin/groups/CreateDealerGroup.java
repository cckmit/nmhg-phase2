/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.DealerScheme;
import tavant.twms.domain.orgmodel.DealerSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class CreateDealerGroup extends I18nActionSupport implements Validateable, Preparable{

    private String id;

    private String dealerSchemeId;

    private DealerGroup dealerGroup;
    private DealerScheme dealerScheme;
    
    private DealerGroupService dealerGroupService;
    private DealerSchemeService dealerSchemeService;
    
    private ConfigParamService configParamService;
	private Boolean dealerGroupConfigParam;
	private static final String DEALER_GROUP_CODE = "dealerGroupCode";

    
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

	public void prepare() throws Exception {
        Long idToBeUsed = null;
        if(dealerSchemeId!=null){
            id=dealerSchemeId;
        }
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if ((dealerScheme != null) && (dealerScheme.getId() != null)) {
            idToBeUsed = dealerScheme.getId();
        }
        if (idToBeUsed != null) {
            dealerScheme = dealerSchemeService.findById(idToBeUsed);
        } 
        /*if(dealerGroupConfigParam == null)
              dealerGroupConfigParam = this.configParamService.getBooleanValue(DEALER_GROUP_CODE);*/
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(dealerGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyDealerGroupName");
        } else {
            DealerGroup group = dealerGroupService.findDealerGroupByName(dealerGroup.getName(), dealerScheme);
            if (group != null) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(dealerGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForDealerGroup");
        }
        //if(dealerGroupConfigParam){
        	/*if (StringUtils.isBlank(dealerGroup.getCode())) {
                addActionError("error.manageGroup.nonEmptyCodeForDealerGroup");
            }
            else {
                DealerGroup groupCode = dealerGroupService.findDealerGroupByCode(dealerGroup.getCode(), dealerScheme);
                if (groupCode != null) {
                    addActionError("error.manageGroup.duplicateGroupCodeForScheme");
                }
            }	*/
        //}
        
        addActionError("error.manageGroup.nonEmptyDealerSet");
    }

    public String setUpForCreate() {
        dealerGroup = new DealerGroup();
        return SUCCESS;
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

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setDealerSchemeService(DealerSchemeService dealerSchemeService) {
        this.dealerSchemeService = dealerSchemeService;
    }

    public String getDealerSchemeId() {
        return dealerSchemeId;
    }

    public void setDealerSchemeId(String dealerSchemeId) {
        this.dealerSchemeId = dealerSchemeId;
    }
}
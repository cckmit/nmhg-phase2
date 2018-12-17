/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.DealerScheme;
import tavant.twms.domain.orgmodel.DealerSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kiran.Kollipara
 * 
 */

@SuppressWarnings("serial")
public class ManageDealerGroup extends I18nActionSupport implements Preparable, Validateable {

    private String id;

    private String dealerSchemeId;

    private DealerGroup dealerGroup;
    private DealerScheme dealerScheme;

    private String groupName;
    private String groupDescription;
    private String grouptype;

    private List<DealerGroup> groups;
    private List<DealerGroup> availableGroups = new ArrayList<DealerGroup>();
    private Set<DealerGroup> includedGroups;

    private List<String> includedGroupNames = new ArrayList<String>();
    
    private DealerGroupService dealerGroupService;
    private DealerSchemeService dealerSchemeService;

    private String buttonLabel;
    private String sectionTitle;
    
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
        buttonLabel = getText("button.manageGroup.createDealerGroup");
        sectionTitle = getText("label.manageGroup.createNewGroup");
        
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
        if (dealerGroup != null && dealerGroup.getId() != null) {        	
            buttonLabel = getText("button.manageGroup.updateDealerGroup");
            sectionTitle = getText("title.manageGroup.dealerGroup") + " - " + dealerGroup.getName() + " - " + dealerGroup.getDescription()+ " - " + dealerGroup.getCode();
           
			for(DealerGroup includedDealerGroup : dealerGroup.getConsistsOf()){
				includedGroupNames.add(includedDealerGroup.getName());
			}
        }
       /* if(dealerGroupConfigParam == null)
                     dealerGroupConfigParam = this.configParamService.getBooleanValue(DEALER_GROUP_CODE);*/
    }

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("search_groups_for_dealergroup".equals(actionName)) {
            if (StringUtils.isBlank(groupName) && StringUtils.isBlank(groupDescription)) {
                addActionError("error.manageGroup.nameOrDescriptionRequired");
            }
        } else {
            validateOthers();
        }
        if (hasActionErrors()) {
            try {
                add();
            } catch (CatalogException e) {
            }
        }
    }

    public String manage() throws CatalogException {
        if(includedGroupNames != null) {
            includedGroups = new HashSet<DealerGroup>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(dealerGroupService.findDealerGroupByName(groupName, dealerScheme));
            }
        }
        return SUCCESS;
    }
    
    public String search() throws CatalogException {
        groups = dealerGroupService.findGroupsByNameAndDescription(dealerScheme, groupName,  groupDescription);
        
        // Removed parent to avoid cycilic tree.
        if (dealerGroup != null && dealerGroup.getId() != null) {
            DealerGroup group = dealerGroupService.findById(dealerGroup.getId());
            groups.remove(group.findTopMostParent());    
        }
        availableGroups.addAll(groups);
        
        for (DealerGroup group : groups) {
            if ((group.getIsPartOf() != null)
                    && (dealerGroup.getId() == null || group.getIsPartOf().getId().compareTo(dealerGroup.getId()) != 0)) {
                availableGroups.remove(group);
            }
        }

        if (includedGroupNames != null) {
            includedGroups = new HashSet<DealerGroup>();
            for (String groupName : includedGroupNames) {
                DealerGroup owner = dealerGroupService.findDealerGroupByName(groupName, dealerScheme);
                includedGroups.add(owner);
            }
        }        
        return SUCCESS;
    }

    public String add() throws CatalogException {
        if (includedGroupNames != null) {
            includedGroups = new HashSet<DealerGroup>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(dealerGroupService.findDealerGroupByName(groupName, dealerScheme));
            }
        }
        return SUCCESS;
    }

    public String save() throws Exception {
        if (dealerGroup != null && dealerGroup.getId() != null) {
            return update();
        } else {
            String name = dealerGroup.getName();
            String description = dealerGroup.getDescription();
            String code = dealerGroup.getCode();
            dealerGroup = dealerScheme.createDealerGroup(name, description, code);
            for (String groupName : includedGroupNames) {
                dealerGroup.includeGroup(dealerGroupService.findDealerGroupByName(groupName, dealerScheme));
            }
            dealerGroupService.save(dealerGroup);
            addActionMessage("message.manageGroup.dealerGroupCreateSuccess", dealerGroup.getName());
        }
        return SUCCESS;
    }

    private String update() throws Exception, GroupInclusionException, GroupHierarchyException {
        String name = dealerGroup.getName();
        String description = dealerGroup.getDescription();
        String code =dealerGroup.getCode();
        dealerGroup = dealerGroupService.findById(dealerGroup.getId());
        
        dealerGroup.setName(name);
        dealerGroup.setDescription(description);
        dealerGroup.setCode(code);
        
        Set<DealerGroup> preIncludedGroups = new HashSet<DealerGroup>();
        preIncludedGroups.addAll(dealerGroup.getConsistsOf());
        for (DealerGroup aGroup : preIncludedGroups) {
            if (includedGroupNames.contains(aGroup.getName())) {
                includedGroupNames.remove(aGroup.getName());
            } else {
                dealerGroup.removeGroup(aGroup);
            }
        }

        for (String groupName : includedGroupNames) {
            dealerGroup.includeGroup(dealerGroupService.findDealerGroupByName(groupName, dealerScheme));
        }
        includedGroups = dealerGroup.getConsistsOf();
        dealerGroupService.update(dealerGroup);
        addActionMessage("message.manageGroup.dealerGroupUpdateSuccess");
        return INPUT;
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

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGrouptype() {
        return grouptype;
    }

    public void setGrouptype(String grouptype) {
        this.grouptype = grouptype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIncludedGroupNames() {
        return includedGroupNames;
    }

    public void setIncludedGroupNames(List<String> includedGroupNames) {
        this.includedGroupNames = includedGroupNames;
    }

    // ************************ Private Methods **************************//
    private void validateOthers() {
        if (StringUtils.isBlank(dealerGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyDealerGroupName");
        } else {
            DealerGroup group = dealerGroupService.findDealerGroupByName(dealerGroup.getName(), dealerScheme);
            if (group != null && !checkSame(group, dealerGroup)) {
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
            }*/	
        //}
        
        if ((includedGroupNames == null) || (includedGroupNames.isEmpty())) {
            addActionError("error.manageGroup.nonEmptyDealerSet");
        }
    }
    
    private boolean checkSame(DealerGroup source, DealerGroup target) {
        if (target.getId() != null && source.getId().compareTo(target.getId()) == 0) {
            return true;
        }
        return false;
    }

    public List<DealerGroup> getAvailableGroups() {
        return availableGroups;
    }

    public void setAvailableGroups(List<DealerGroup> availableGroups) {
        this.availableGroups = availableGroups;
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

    public List<DealerGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<DealerGroup> groups) {
        this.groups = groups;
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

    public String getDealerSchemeId() {
        return dealerSchemeId;
    }

    public void setDealerSchemeId(String dealerSchemeId) {
        this.dealerSchemeId = dealerSchemeId;
    }
}
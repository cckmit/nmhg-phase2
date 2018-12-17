/**
 * 
 */
package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.orgmodel.UserScheme;
import tavant.twms.domain.orgmodel.UserSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ManageUserGroup extends I18nActionSupport implements Preparable, Validateable {

    private String id;
    private String userSchemeId;

    private UserCluster userGroup;
    private UserScheme userScheme;

    private String groupName;
    private String groupDescription;
    private String grouptype;

    private List<UserCluster> groups;
    private List<UserCluster> availableGroups = new ArrayList<UserCluster>();
    private Set<UserCluster> includedGroups;

    private List<String> includedGroupNames = new ArrayList<String>();
    
    private UserClusterService userClusterService;
    private UserSchemeService userSchemeService;

    private String buttonLabel;
    private String sectionTitle;

    public void prepare() throws Exception {
        buttonLabel = getText("button.manageGroup.createUserGroup");
        sectionTitle = getText("label.manageGroup.createNewGroup");
        
        Long idToBeUsed = null;
        if (userSchemeId != null) {
            id = userSchemeId;
        }
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if ((userScheme != null) && (userScheme.getId() != null)) {
            idToBeUsed = userScheme.getId();
        }
        if (idToBeUsed != null) {
            userScheme = userSchemeService.findById(idToBeUsed);
        }
        if (userGroup != null && userGroup.getId() != null) {
            buttonLabel = getText("button.manageGroup.updateUserGroup");
            sectionTitle = getText("title.manageGroup.userGroup") + " - " + userGroup.getName() + " - " + userGroup.getDescription();
            for(UserCluster includedUserGroup : userGroup.getConsistsOf()){
            	includedGroupNames.add(includedUserGroup.getName());
            }
        }
    }

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("search_groups_for_usergroup".equals(actionName)) {
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
            includedGroups = new HashSet<UserCluster>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(userClusterService.findUserClusterByName(groupName, userScheme));
            }
        }
        return SUCCESS;
    }
    
    public String search() throws CatalogException {
        groups = userClusterService.findClustersByNameAndDescription(userScheme, groupName,  groupDescription);
        
        // Removed parent to avoid cycilic tree.
        if (userGroup != null && userGroup.getId() != null) {
            UserCluster group = userClusterService.findById(userGroup.getId());
            groups.remove(group.findTopMostParent());    
        }
        availableGroups.addAll(groups);
        
        for (UserCluster group : groups) {
            if ((group.getIsPartOf() != null)
                    && (userGroup.getId() == null || group.getIsPartOf().getId().compareTo(userGroup.getId()) != 0)) {
                availableGroups.remove(group);
            }
        }

        if (includedGroupNames != null) {
            includedGroups = new HashSet<UserCluster>();
            for (String groupName : includedGroupNames) {
                UserCluster owner = userClusterService.findUserClusterByName(groupName, userScheme);
                includedGroups.add(owner);
            }
        }        
        return SUCCESS;
    }

    public String add() throws CatalogException {
        if (includedGroupNames != null) {
            includedGroups = new HashSet<UserCluster>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(userClusterService.findUserClusterByName(groupName, userScheme));
            }
        }
        return SUCCESS;
    }

    public String save() throws Exception {
        if (userGroup != null && userGroup.getId() != null) {
            return update();
        } else {
            String name = userGroup.getName();
            String description = userGroup.getDescription();
            userGroup = userScheme.createUserCluster(name, description);
            for (String groupName : includedGroupNames) {
                userGroup.includeGroup(userClusterService.findUserClusterByName(groupName, userScheme));
            }
            userClusterService.save(userGroup);
            addActionMessage("message.manageGroup.userGroupCreateSuccess", userGroup.getName());
        }
        return SUCCESS;
    }

    private String update() throws Exception, GroupInclusionException, GroupHierarchyException {
        String name = userGroup.getName();
        String description = userGroup.getDescription();
        userGroup = userClusterService.findById(userGroup.getId());
        
        userGroup.setName(name);
        userGroup.setDescription(description);
        
        Set<UserCluster> preIncludedGroups = new HashSet<UserCluster>();
        preIncludedGroups.addAll(userGroup.getConsistsOf());
        for (UserCluster aGroup : preIncludedGroups) {
            userGroup.removeGroup(aGroup);
        }
        userClusterService.update(userGroup);
        
        for (String groupName : includedGroupNames) {
            userGroup.includeGroup(userClusterService.findUserClusterByName(groupName, userScheme));
        }
        includedGroups = new HashSet<UserCluster>();
        includedGroups.addAll(userGroup.getConsistsOf());
        userClusterService.update(userGroup);
        addActionMessage("message.manageGroup.userGroupUpdateSuccess");
        return INPUT;
    }
    
    // ************************ Private Methods **************************//
    private void validateOthers() {
        if (StringUtils.isBlank(userGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyUserGroupName");
        } else {
            UserCluster group = userClusterService.findUserClusterByName(userGroup.getName(), userScheme);
            if (group != null && !checkSame(group, userGroup)) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(userGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForUserGroup");
        }
        if ((includedGroupNames == null) || (includedGroupNames.isEmpty())) {
            addActionError("error.manageGroup.nonEmptyUserSet");
        }
    }
    
    private boolean checkSame(UserCluster source, UserCluster target) {
        if (target.getId() != null && source.getId().compareTo(target.getId()) == 0) {
            return true;
        }
        return false;
    }

	public List<UserCluster> getAvailableGroups() {
		return availableGroups;
	}

	public void setAvailableGroups(List<UserCluster> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
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

	public List<UserCluster> getGroups() {
		return groups;
	}

	public void setGroups(List<UserCluster> groups) {
		this.groups = groups;
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

	public Set<UserCluster> getIncludedGroups() {
		return includedGroups;
	}

	public void setIncludedGroups(Set<UserCluster> includedGroups) {
		this.includedGroups = includedGroups;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public UserCluster getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserCluster userGroup) {
		this.userGroup = userGroup;
	}

	public UserScheme getUserScheme() {
		return userScheme;
	}

	public void setUserScheme(UserScheme userScheme) {
		this.userScheme = userScheme;
	}

	public void setUserClusterService(UserClusterService userClusterService) {
		this.userClusterService = userClusterService;
	}

	public void setUserSchemeService(UserSchemeService userSchemeService) {
		this.userSchemeService = userSchemeService;
	}

    public String getUserSchemeId() {
        return userSchemeId;
    }

    public void setUserSchemeId(String userSchemeId) {
        this.userSchemeId = userSchemeId;
    }
}
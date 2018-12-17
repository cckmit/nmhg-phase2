/**
 * 
 */
package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.Preparable;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.web.actions.TwmsActionSupport;

import java.util.ArrayList;
import java.util.Collections;

import java.util.HashSet;
import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ManageUserGroups extends TwmsActionSupport implements Preparable {
    
    private static final String USER = "user";
    private static final String GROUP = "group";
    
    private String id;

    private UserClusterService userClusterService;
    private UserSchemeService userSchemeService;

    private UserCluster userGroup;
    private UserScheme userScheme;

    private ArrayList<User> includedUsers;
    private Set<UserCluster> includedGroups;
    
    private String buttonLabel;
    private String sectionTitle;

    public void prepare() throws Exception {
        buttonLabel = getText("button.manageGroup.updateUserGroup");
        sectionTitle = getText("title.manageGroup.updatingUserGroup");
        
        Long idToBeUsed = null;
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if ((userGroup != null) && (userGroup.getId() != null)) {
            idToBeUsed = userGroup.getId();
        }
        if (idToBeUsed != null) {
            userGroup = userClusterService.findById(idToBeUsed);
        }
        if (userGroup != null && userGroup.getId() != null) {
            userScheme = userGroup.getScheme();
            sectionTitle = getText("title.manageGroup.userGroup") + " - " + userGroup.getName() + " - " + userGroup.getDescription();
        }
    }
    
    public String showGroup() {
        if (userGroup.isClusterOfUsers()) {
            includedUsers = new ArrayList<User>();
            for (User aUser : userGroup.getIncludedUsers()) {
                includedUsers.add(aUser);
            }
            Collections.sort(includedUsers, User.SORT_BY_COMPLETE_NAME);
            return USER;  
        } else {
            includedGroups = new HashSet<UserCluster>();
            includedGroups.addAll(userGroup.getConsistsOf());
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

	public Set<UserCluster> getIncludedGroups() {
		return includedGroups;
	}

	public void setIncludedGroups(Set<UserCluster> includedGroups) {
		this.includedGroups = includedGroups;
	}

	public ArrayList<User> getIncludedUsers() {
		return includedUsers;
	}

	public void setIncludedUsers(ArrayList<User> includedUsers) {
		this.includedUsers = includedUsers;
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
    
}
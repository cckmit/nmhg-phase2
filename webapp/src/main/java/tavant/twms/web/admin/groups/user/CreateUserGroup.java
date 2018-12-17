/**
 * 
 */
package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.orgmodel.UserScheme;
import tavant.twms.domain.orgmodel.UserSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class CreateUserGroup extends I18nActionSupport implements Validateable, Preparable{

    private String id;
    private String userSchemeId;

    private UserCluster userGroup;
    private UserScheme userScheme;
    
    private UserClusterService userClusterService;
    private UserSchemeService userSchemeService;
    
    public void prepare() throws Exception {
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
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(userGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyUserGroupName");
        } else {
            UserCluster group = userClusterService.findUserClusterByName(userGroup.getName(), userScheme);
            if (group != null) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(userGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForUserGroup");
        }
        addActionError("error.manageGroup.nonEmptyUserSet");
    }

    public String setUpForCreate() {
        userGroup = new UserCluster();
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
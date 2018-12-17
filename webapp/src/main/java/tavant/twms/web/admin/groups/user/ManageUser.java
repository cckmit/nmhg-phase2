/**
 *
 */
package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
import tavant.twms.domain.admin.RoleService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author aniruddha.chaturvedi
 */
public class ManageUser extends I18nActionSupport implements Preparable, Validateable {

    private static Logger logger = Logger.getLogger(ManageUser.class);

    private String id;
    private String userSchemeId;

    private UserCluster userGroup;
    private UserScheme userScheme;

    private String userName;

    private List<User> users;
    private List<User> availableUsers = new ArrayList<User>();
    private Set<User> includedUsers;

    private Set<String> includedUserNames = new HashSet<String>();

    private UserClusterService userClusterService;
    private UserSchemeService userSchemeService;

    private String buttonLabel;
    private String sectionTitle;

    private Map<String, String> organisations = new HashMap<String, String>();

    private User user;

    private List<Role> allRoles = new ArrayList<Role>();

    private List<Role> rolesToAssign = new ArrayList<Role>();

    private List<Role> rolesToRemove = new ArrayList<Role>();

    private RoleService roleService;

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
            for (User includedUser : userGroup.getIncludedUsers()) {
                includedUserNames.add(includedUser.getName());
            }
        }
    }

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("search_users_for_usergroup".equals(actionName)) {
            if (StringUtils.isBlank(userName)) {
                addActionError("error.manageGroup.userNameRequired");
            }
        } else {
            validateOthers();
        }
        if (hasActionErrors()) {
            add();
        }
    }

    public String previewRolesToUser() {
        user = orgService.findUserById(Long.parseLong(getId()));
        Set<Role> sortedRoles = new TreeSet<Role>(new Role.RoleComparator());
        sortedRoles.addAll(user.getRoles());
        user.setRoles(sortedRoles);
        getUserOrganisations();
        return SUCCESS;
    }

    public String showRolesToUser() {
        return SUCCESS;
    }

    public String showUser() {
        if (isLoggedInUserAnInternalUser() && orgService.isInternalUser(user)) {
            allRoles = roleService.getAllRoles(null);
        } else if (orgService.isInternalUser(user)) {   // external user cannot access details of internal user
            user = null;
            return SUCCESS;
        } else if (!user.getUserType().equals(AdminConstants.SUPPLIER_USER) && orgService.isDealer(user)) {
            showRoles(RoleType.DEALER);
        }

        allRoles.removeAll(user.getRoles());
        Set<Role> sortedRoles = new TreeSet<Role>(new Role.RoleComparator());
        sortedRoles.addAll(user.getRoles());
        user.setRoles(sortedRoles);

        getUserOrganisations();
        return SUCCESS;
    }

    private void showRoles(RoleType roleType) {
        List<RoleType> roleTypeList = new ArrayList<RoleType>();
        roleTypeList.add(roleType);
        allRoles = roleService.getAllRoles(roleTypeList);
    }

    private void getUserOrganisations() {
        for (Organization organisation : user.getBelongsToOrganizations()) {
            if (organisation instanceof Dealership) {
                organisations.put(organisation.getName(), ((Dealership) organisation).getCompanyType() != null ? ((Dealership) organisation).getCompanyType() : "");
            } else {
                organisations.put(organisation.getName(), organisation.getType());
            }
        }
    }

    public String updateUserRoles() {
        rolesToAssign.removeAll(Collections.singleton(null));
        rolesToRemove.removeAll(Collections.singleton(null));
        // internal user can assign any role to other internal users
        if (isLoggedInUserAnInternalUser() && orgService.isInternalUser(user)) {
            user.getRoles().removeAll(rolesToRemove);
            user.getRoles().addAll(rolesToAssign);
        }
        // external user cannot update details of internal user
        // TODO user can be redirected to Unauthorized page
        else if (orgService.isInternalUser(user)) {
            user = null;
            return INPUT;

        }
        // internal roles cannot be assigned to external user
        // TODO user can be redirected to Unauthorized page
        else if (Role.containsRoleType(rolesToAssign, RoleType.INTERNAL)) {
            user = null;
            return INPUT;
        } else
        // TODO user association check
        {
            user.getRoles().removeAll(rolesToRemove);
            user.getRoles().addAll(rolesToAssign);
        }
        if (user.getRoles().size() == 0) {
            addActionError("error.manageGroup.nonEmptyRoleSet");
            return INPUT;
        }
        try {
            orgService.updateUser(user);
        } catch (Exception e) {
            logger.equals(e.getMessage() + e.getStackTrace());
        }
        addActionMessage("message.userrole.update.success");
        return INPUT;

    }

    public String manage() {
        if (includedUserNames != null) {
            includedUsers = new HashSet<User>();
            for (String aUserName : includedUserNames) {
                includedUsers.add(orgService.findUserByName(aUserName));
            }
        }
        return SUCCESS;
    }

    public String search() {

        List<String> foundUserNames = orgService.findUsersWithNameLikeOfType(userName, Constants.USER_TYPE_INTERNAL);
        users = new ArrayList<User>();
        for (String aName : foundUserNames) {
            users.add(orgService.findUserByName(aName));
        }
        Collections.sort(users, User.SORT_BY_COMPLETE_NAME);
        availableUsers.addAll(users);
        for (User user : users) {
            UserCluster ownerGroup = userClusterService.findClusterContainingUser(user, userScheme);
            if (ownerGroup != null && (userGroup.getId() == null || ownerGroup.getId().compareTo(userGroup.getId()) != 0)) {
                availableUsers.remove(user);
            }
        }

        if (userGroup != null && userGroup.getId() != null)

            includedUsers = getAlreadyincludedUsers(userGroup);

        return SUCCESS;
    }

    public String add() {
        includedUsers = new HashSet<User>();
        if (userGroup != null && userGroup.getId() != null)
            includedUsers = getAlreadyincludedUsers(userGroup);

        if (includedUserNames != null) {
            for (String aUserName : includedUserNames) {
                if (includedUsers == null || (includedUsers != null && !getAlreadyIncludedUserNames(includedUsers).contains(aUserName)))
                    includedUsers.add(orgService.findUserByName(aUserName));
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
            includedUsers = new HashSet<User>();
            for (String aUserName : includedUserNames) {
                includedUsers.add(orgService.findUserByName(aUserName));
            }
            userGroup.setIncludedUsers(includedUsers);
            userClusterService.save(userGroup);
            addActionMessage("message.manageGroup.userGroupCreateSuccess", userGroup.getName());
        }
        return SUCCESS;
    }

    private String update() throws Exception {
        String name = userGroup.getName();
        String description = userGroup.getDescription();
        userGroup = userClusterService.findById(userGroup.getId());

        userGroup.setName(name);
        userGroup.setDescription(description);

        includedUsers = getAlreadyincludedUsers(userGroup);

        if (includedUserNames != null && includedUserNames.size() > includedUsers.size()) {
            for (String aUserName : includedUserNames) {
                if (!getAlreadyIncludedUserNames(includedUsers).contains(aUserName))
                    includedUsers.add(orgService.findUserByName(aUserName));
            }
        } else {
            for (String aUserName : getAlreadyIncludedUserNames(includedUsers)) {
                if (!includedUserNames.contains(aUserName))
                    includedUsers.remove(orgService.findUserByName(aUserName));
            }
        }

        userGroup.setIncludedUsers(includedUsers);
        userClusterService.update(userGroup);
        addActionMessage("message.manageGroup.userGroupUpdateSuccess");
        return SUCCESS;
    }

    private Set<User> getAlreadyincludedUsers(UserCluster userGroup) {
        includedUsers = new HashSet<User>();
        for (User aUser : userGroup.getIncludedUsers()) {
            includedUsers.add(aUser);
        }
        return includedUsers;
    }

    private Set<String> getAlreadyIncludedUserNames(Set<User> includedUsers) {
        Set<String> includedUserNames = new HashSet<String>();
        for (User user : includedUsers) {
            includedUserNames.add(user.getName());
        }
        return includedUserNames;
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
        if ((includedUserNames == null) || (includedUserNames.isEmpty())) {
            addActionError("error.manageGroup.nonEmptyUserSet");
        }
    }

    private boolean checkSame(UserCluster source, UserCluster target) {
        if (target.getId() != null && source.getId().compareTo(target.getId()) == 0) {
            return true;
        }
        return false;
    }

    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getIncludedUserNames() {
        return includedUserNames;
    }

    public void setIncludedUserNames(Set<String> includedUserNames) {
        this.includedUserNames = includedUserNames;
    }

    public Set<User> getIncludedUsers() {
        return includedUsers;
    }

    public void setIncludedUsers(Set<User> includedUsers) {
        this.includedUsers = includedUsers;
    }

    public UserCluster getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserCluster userGroup) {
        this.userGroup = userGroup;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public Map<String, String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Map<String, String> organisations) {
        this.organisations = organisations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<Role> allRoles) {
        this.allRoles = allRoles;
    }

    public List<Role> getRolesToAssign() {
        return rolesToAssign;
    }

    public void setRolesToAssign(List<Role> rolesToAssign) {
        this.rolesToAssign = rolesToAssign;
    }

    public List<Role> getRolesToRemove() {
        return rolesToRemove;
    }

    public void setRolesToRemove(List<Role> rolesToRemove) {
        this.rolesToRemove = rolesToRemove;
    }
}
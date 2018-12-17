package tavant.twms.web.admin.roles;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ValidationAware;
import org.apache.log4j.Logger;
import tavant.twms.domain.admin.RoleService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class AddUsersToRole extends SummaryTableAction implements
        ValidationAware, Preparable {

    public AddUsersToRole() {
        super();
    }

    private static Logger logger = Logger.getLogger(AddUsersToRole.class);

    private RoleService roleService;

    private Role role;

    private Set<User> users = new HashSet<User>();

    private List<User> userList = new ArrayList<User>();

    private Set<User> usersToAdd = new HashSet<User>();

    public String detail() {
        role = roleService.findRoleById(Long.parseLong(getId()));
        return SUCCESS;
    }

    private void fetchUsersBasedOnRole() {
        role = roleService.findRoleById(Long.parseLong(getId()));
        if (isLoggedInUserADealer()) {
            userList = orgService.findUsersBelongingToRoleAndOrgForDisplay(getLoggedInUsersDealership(), role.getName());
        } else {
            userList = this.orgService.findUsersListBelongingToRoleForDisplay(role.getName());
        }
    }

    public String previewUsersToRole() {
        fetchUsersBasedOnRole();
        return SUCCESS;
    }

    public String listUsers() {
        List<User> users = new ArrayList<User>();
        if (isLoggedInUserAnInternalUser()) {
            users = getUsersForInternalUser(users);
        } else if (isLoggedInUserADealer()) {
            users = getUsersForDealer();
        }
        return generateAndWriteComboboxJson(users, "id", "name");
    }

    private List<User> getUsersForDealer() {
        return orgService.findUsersForDealerWithNameLike(
                getLoggedInUsersDealership().getId(), getSearchPrefix());
    }

    private List<User> getUsersForInternalUser(List<User> users) {
        if (role != null) {
            if (RoleType.INTERNAL.equals(role.getRoleType())) {
                users = orgService.findUsersWithNameLike(getSearchPrefix(), "INTERNAL");
            } else if (RoleType.DEALER.equals(role.getRoleType())) {
                users = getUsersForRole(RoleType.DEALER);
            }
        } else {
            users = orgService.findUsersWithNameLike(getSearchPrefix());
        }
        return users;
    }

    private List<User> getUsersForRole(RoleType roleType) {
        List<RoleType> roleTypeList = new ArrayList<RoleType>();
        roleTypeList.add(roleType);
        roleTypeList.add(RoleType.INTERNAL);
        return orgService.findUsersLikeBelongingToRoles(getSearchPrefix(), roleTypeList);
    }

    @Override
    protected PageResult<?> getBody() {
        if (isLoggedInUserAnInternalUser()) {
            return roleService.findAllRoles(getCriteria());
        } else {
            List<RoleType> roleTypeList = new ArrayList<RoleType>();
            roleTypeList.add(RoleType.DEALER);
            return roleService.fetchAllRolesByRoleType(getCriteria(),
                    roleTypeList);
        }
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {

        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("", "id", 0, "Number", "id",
                false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.roles.roleName",
                "name", 50, "string", "name", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn(
                "columnTitle.roles.roleDescription", "description", 50,
                "string", false, false, false, false));
/*        tableHeadData.add(new SummaryTableColumn(
                "columnTitle.roles.roleType", "roleType", 30,
                "string", "roleType.type", false, false, false, false));*/
        return tableHeadData;
    }

    public String updateRole() {
        // internal roles can only be assigned by internal users and to internal users only
        if (role.getRoleType() == RoleType.INTERNAL) {
            if (!isLoggedInUserAnInternalUser()) {
                addActionError("message.role.update.only.internal.user.updates.internal.role");
                return SUCCESS;
            }
            for (User user : usersToAdd) {
                if (!orgService.isInternalUser(user)) {
                    addActionError("message.role.update.only.internal.user.addedto.internal.roles");
                    return SUCCESS;
                }
                user.getRoles().add(role);
            }

        }
        // external users can assign external roles to external users only
        //and internal users can assign them to all users
        else {
            // TODO user association check
            if (!isLoggedInUserAnInternalUser()) {
                for (User user : usersToAdd) {
                    if (orgService.isInternalUser(user)) {
                        addActionError("message.role.update.only.external.user.addedto.external.roles");
                        return SUCCESS;
                    }
                    user.getRoles().add(role);
                }
            } else {
                for (User user : usersToAdd) {
                    user.getRoles().add(role);
                }
            }
        }
        try {
            List<User> usersList = new ArrayList<User>();
            usersList.addAll(usersToAdd);
            orgService.updateAllUser(usersList);
            addActionMessage("message.role.update.success");
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
            addActionError("message.role.update.error");
        }

        return SUCCESS;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Set<User> getUsersToAdd() {
        return usersToAdd;
    }

    public void setUsersToAdd(Set<User> usersToAdd) {
        this.usersToAdd = usersToAdd;
    }

    public void prepare() {
        this.setPreviewVisible(Boolean.FALSE.booleanValue());

    }

}

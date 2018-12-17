package tavant.twms.web.admin.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.admin.FunctionalArea;
import tavant.twms.domain.admin.FunctionalMapping;
import tavant.twms.domain.admin.Permission;
import tavant.twms.domain.admin.RoleService;
import tavant.twms.domain.admin.SubjectArea;
import tavant.twms.domain.admin.SubjectAreaFunctionalMapping;
import tavant.twms.domain.admin.UserAction;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleCategory;
import tavant.twms.domain.orgmodel.RoleType;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ValidationAware;

@SuppressWarnings("serial")
public class ManageRoles extends SummaryTableAction implements ValidationAware,
        Preparable {

    private static Logger logger = Logger.getLogger(ManageRoles.class);

    private RoleService roleService;

    private Role role;

    private Map<RoleType, String> roleTypeMap = new HashMap<RoleType, String>();

    private List<FunctionalMapping> funcMapping;

    private List<SubjectAreaFunctionalMapping> subjectAreas;

    private Map<String, UserAction> actionsMaster = new HashMap<String, UserAction>();

    public String detail() {
        populateActionMaster();
        role = roleService.findRoleById(Long.parseLong(getId()));
        subjectAreas = populateSubjectAreaMapping(role);
        return SUCCESS;
    }

    public String preview() {
        populateActionMaster();
        role = roleService.findRoleById(Long.parseLong(getId()));
        subjectAreas = populateSubjectAreaMapping(role);
        return SUCCESS;
    }

    @Override
    protected PageResult<?> getBody() {
        return roleService.fetchAllRolesByRoleType(getCriteria(), new ArrayList<RoleType>(Arrays.asList(RoleType.INTERNAL)));
    }

    public List<SubjectAreaFunctionalMapping> populateSubjectAreaMapping(
            Role role) {

        List<SubjectArea> subjectAreas = this.roleService.getAllSubjectAreas();
        List<SubjectAreaFunctionalMapping> subjectAreasMappingList = new ArrayList<SubjectAreaFunctionalMapping>();

        for (SubjectArea subjectArea : subjectAreas) {
            SubjectAreaFunctionalMapping safM = new SubjectAreaFunctionalMapping();
            safM.setSubjectArea(subjectArea);
            Map<FunctionalArea, Map<String, UserAction>> functionalMappingsMap = new Hashtable<FunctionalArea, Map<String, UserAction>>();
            if (subjectArea.getFunctionalAreas().size() > 0) {
                initializeMap(subjectArea, functionalMappingsMap);

                if (role != null) {
                    populatePermissions(role
                            .getPermissionForSubjectArea(subjectArea),
                            functionalMappingsMap);
                }
                safM.setFunctionalAreas(FunctionalMapping
                        .convertMapToList(functionalMappingsMap));
                subjectAreasMappingList.add(safM);
            }

        }
        return subjectAreasMappingList;
    }

    private void initializeMap(SubjectArea subjectArea,
                               Map<FunctionalArea, Map<String, UserAction>> functionalMappingsMap) {
        for (FunctionalArea element : subjectArea.getFunctionalAreas()) {
            functionalMappingsMap.put(element,
                    new HashMap<String, UserAction>());
        }
    }

    private void populatePermissions(List<Permission> permList,
                                     Map<FunctionalArea, Map<String, UserAction>> funcMappingsMap) {
        for (Permission permission : permList) {
            FunctionalArea functionalArea = permission.getFunctionalArea();
            Map<String, UserAction> permissionMap = funcMappingsMap
                    .get(functionalArea);
            permissionMap.put(permission.getAction().getAction(), permission
                    .getAction());
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
                "columnTitle.roles.roleType", "enum:RoleType:type", 12,
                "string","RoleType.type"));*/

        return tableHeadData;
    }

    public void prepare() {
        roleTypeMap.putAll(RoleType.addAllEnums());
    }

    public String createRoles() {
        populateActionMaster();
        subjectAreas = populateSubjectAreaMapping(null);
        return SUCCESS;
    }

    public String saveRole() {
    	role.setRoleType(RoleType.INTERNAL);
    	role.setRoleCategory(RoleCategory.WARRANTY);
        validateRole();
        if (hasActionErrors()) {
            populateActionMaster();
            return INPUT;
        }
        //role.removeWhiteSpacesFromFields();
        List<Permission> permissions = new ArrayList<Permission>();
        removeNulls(subjectAreas);
        for (SubjectAreaFunctionalMapping element : subjectAreas) {
            for (FunctionalMapping funcMapping : element.getFunctionalAreas()) {
                permissions.addAll(funcMapping.getPermissionList(element
                        .getSubjectArea()));
            }
        }
        role.setPermissions(permissions);
        role.setDisplayName(role.getDescription());
        roleService.createRole(role);
        addActionMessage("message.role.add.success");
        return SUCCESS;
    }

    /*
      * API to remove the object from the collection of additional coverage
      * options when the number of hours is not entered.
      */
    @SuppressWarnings("unchecked")
    private void removeNulls(Collection fromCollection) {
        Collection NULL = Collections.singleton(null);
        fromCollection.removeAll(NULL);
    }

    public String updateRole() {
        validateRole();
        if (hasActionErrors()) {
            populateActionMaster();
            return INPUT;
        }
        try {
            //role.removeWhiteSpacesFromFields();
            List<Permission> permissions = new ArrayList<Permission>();
            for (SubjectAreaFunctionalMapping element : subjectAreas) {
                for (FunctionalMapping funcMapping : element
                        .getFunctionalAreas()) {
                    permissions.addAll(funcMapping.getPermissionList(element
                            .getSubjectArea()));
                }
            }
            role.getPermissions().clear();
            role.getPermissions().addAll(permissions);
            role.setRoleCategory(RoleCategory.WARRANTY);
            role.setDisplayName(role.getDescription());
            roleService.updateRole(role);

            addActionMessage("message.role.update.success");
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            addActionError("message.role.update.error");
        }
        return SUCCESS;
    }

    private void populateActionMaster() {
        for (UserAction action : this.roleService.getAllActions()) {
            this.actionsMaster.put(action.getAction(), action);
        }
    }

    private void validateRole() {
        if (!StringUtils.hasText(role.getName())) {
            addActionError("error.role.noNameSpecified");
        }
        if (!StringUtils.hasText(role.getDescription())) {
            addActionError("error.role.noDescriptionSpecified");
        }
        if (role.getRoleType() == null) {
            addActionError("error.role.noTypeSpecified");
        }
        if (StringUtils.hasText(role.getName())) {
            if (roleService.findIfRoleExists(role.getName(), role.getId())) {
                addActionError("error.role.sameNameSpecified");
            }
        }
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

    public Map<RoleType, String> getRoleTypeMap() {
        return roleTypeMap;
    }

    public void setRoleTypeMap(Map<RoleType, String> roleTypeMap) {
        this.roleTypeMap = roleTypeMap;
    }

    public Map<String, UserAction> getActionsMaster() {
        return actionsMaster;
    }

    public void setActionsMaster(Map<String, UserAction> actionsMaster) {
        this.actionsMaster = actionsMaster;
    }

    public List<FunctionalMapping> getFuncMapping() {
        return funcMapping;
    }

    public void setFuncMapping(List<FunctionalMapping> funcMapping) {
        this.funcMapping = funcMapping;
    }

    public List<SubjectAreaFunctionalMapping> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(List<SubjectAreaFunctionalMapping> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }
    
    public boolean getPrimaryRole(){
    	return role.isPrimaryRole();
    }

}

/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageRole extends I18nActionSupport implements Preparable,
		Validateable {

	private static final long serialVersionUID = 1L;

	private String id;

	private RoleGroup roleGroup;

	private RoleScheme roleScheme;

	private String roleName;

	private List<Role> roles;

	private List<Role> availableRoles = new ArrayList<Role>();

	private List<Role> includedRoles;

	private Set<String> includedRoleNames;

	private RoleGroupService roleGroupService;

	private RoleSchemeService roleSchemeService;

	private String buttonLabel;

	private String sectionTitle;

	public void prepare() throws Exception {
		buttonLabel = getText("button.manageGroup.createRoleGroup");
		sectionTitle = getText("label.manageGroup.createNewGroup");

		Long idToBeUsed = null;
		if (id != null && !(id.trim().equals(""))) {
			idToBeUsed = Long.parseLong(id);
		} else if ((roleScheme != null) && (roleScheme.getId() != null)) {
			idToBeUsed = roleScheme.getId();
		}
		if (idToBeUsed != null) {
			roleScheme = roleSchemeService.findById(idToBeUsed);
		}
		if (roleGroup != null && roleGroup.getId() != null) {
			buttonLabel = getText("button.manageGroup.updateRoleGroup");
			sectionTitle = getText("title.manageGroup.roleGroup") + " - "
					+ roleGroup.getName() + " - " + roleGroup.getDescription();
		}
	}

	@Override
	public void validate() {
		String actionName = ActionContext.getContext().getName();
		if ("search_roles_for_rolegroup".equals(actionName)) {
			if (StringUtils.isBlank(roleName)) {
				addActionError("error.manageGroup.roleNameRequired");
			}
		} else {
			validateOthers();
		}
		if (hasActionErrors()) {
			add();
		}
	}

	public String manage() {
		if (includedRoleNames != null) {
			includedRoles = new ArrayList<Role>();
			for (String aRoleName : includedRoleNames) {
				includedRoles.add(orgService.findRoleByName(aRoleName));
			}
		}
		return SUCCESS;
	}

	public String search() {
		List<String> foundRoleNames = orgService.findRoleNamesStartingWith(
				roleName, 0, 10);
		roles = new ArrayList<Role>();
		for (String aName : foundRoleNames) {
			roles.add(orgService.findRoleByName(aName));
		}
		availableRoles.addAll(roles);
		for (Role role : roles) {
			RoleGroup ownerGroup = roleGroupService.findGroupContainingRole(
					role, roleScheme);
			if (ownerGroup != null
					&& (roleGroup.getId() == null || ownerGroup.getId()
							.compareTo(roleGroup.getId()) != 0)) {
				availableRoles.remove(role);
			}
		}

		if (includedRoleNames != null) {
			includedRoles = new ArrayList<Role>();
			for (String aRoleName : includedRoleNames) {
				includedRoles.add(orgService.findRoleByName(aRoleName));
			}
		}
		return SUCCESS;
	}

	public String add() {
		if (includedRoleNames != null) {
			includedRoles = new ArrayList<Role>();
			for (String aRoleName : includedRoleNames) {
				includedRoles.add(orgService.findRoleByName(aRoleName));
			}
		}
		return SUCCESS;
	}

	public String save() throws Exception {
		if (roleGroup != null && roleGroup.getId() != null) {
			return update();
		} else {
			String name = roleGroup.getName();
			String description = roleGroup.getDescription();
			roleGroup = roleScheme.createRoleGroup(name, description);
			includedRoles = new ArrayList<Role>();
			for (String aRoleName : includedRoleNames) {
				includedRoles.add(orgService.findRoleByName(aRoleName));
			}
			roleGroup.setIncludedRoles(includedRoles);
			roleGroupService.save(roleGroup);
			addActionMessage("message.manageGroup.roleGroupCreateSuccess",
					roleGroup.getName());
		}
		return SUCCESS;
	}

	private String update() throws Exception {
		String name = roleGroup.getName();
		String description = roleGroup.getDescription();
		roleGroup = roleGroupService.findById(roleGroup.getId());

		roleGroup.setName(name);
		roleGroup.setDescription(description);

		includedRoles = new ArrayList<Role>();
		for (String aRoleName : includedRoleNames) {
			includedRoles.add(orgService.findRoleByName(aRoleName));
		}
		roleGroup.setIncludedRoles(includedRoles);
		roleGroupService.update(roleGroup);
		addActionMessage("message.manageGroup.roleGroupUpdateSuccess");
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

	// ************************ Private Methods **************************//
	private void validateOthers() {
		if (StringUtils.isBlank(roleGroup.getName())) {
			addActionError("error.manageGroup.nonEmptyRoleGroupName");
		} else {
			RoleGroup group = roleGroupService.findRoleGroupByName(roleGroup
					.getName(), roleScheme);
			if (group != null && !checkSame(group, roleGroup)) {
				addActionError("error.manageGroup.duplicateGroupForScheme");
			}
		}
		if (StringUtils.isBlank(roleGroup.getDescription())) {
			addActionError("error.manageGroup.nonEmptyDescriptionForRoleGroup");
		}
		if ((includedRoleNames == null) || (includedRoleNames.isEmpty())) {
			addActionError("error.manageGroup.nonEmptyRoleSet");
		}
	}

	private boolean checkSame(RoleGroup source, RoleGroup target) {
		if (target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0) {
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Role> getAvailableRoles() {
		return availableRoles;
	}

	public void setAvailableRoles(List<Role> availableRoles) {
		this.availableRoles = availableRoles;
	}

	public Set<String> getIncludedRoleNames() {
		return includedRoleNames;
	}

	public void setIncludedRoleNames(Set<String> includedRoleNames) {
		this.includedRoleNames = includedRoleNames;
	}

	public List<Role> getIncludedRoles() {
		return includedRoles;
	}

	public void setIncludedRoles(List<Role> includedRoles) {
		this.includedRoles = includedRoles;
	}

	public RoleGroup getRoleGroup() {
		return roleGroup;
	}

	public void setRoleGroup(RoleGroup roleGroup) {
		this.roleGroup = roleGroup;
	}

	public RoleGroupService getRoleGroupService() {
		return roleGroupService;
	}

	public void setRoleGroupService(RoleGroupService roleGroupService) {
		this.roleGroupService = roleGroupService;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public RoleScheme getRoleScheme() {
		return roleScheme;
	}

	public void setRoleScheme(RoleScheme roleScheme) {
		this.roleScheme = roleScheme;
	}

	public RoleSchemeService getRoleSchemeService() {
		return roleSchemeService;
	}

	public void setRoleSchemeService(RoleSchemeService roleSchemeService) {
		this.roleSchemeService = roleSchemeService;
	}

}
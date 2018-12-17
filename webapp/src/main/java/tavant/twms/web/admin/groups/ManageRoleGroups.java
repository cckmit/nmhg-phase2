package tavant.twms.web.admin.groups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleGroup;
import tavant.twms.domain.orgmodel.RoleGroupService;
import tavant.twms.domain.orgmodel.RoleScheme;
import tavant.twms.domain.orgmodel.RoleSchemeService;
import tavant.twms.web.actions.TwmsActionSupport;

import com.opensymphony.xwork2.Preparable;

public class ManageRoleGroups extends TwmsActionSupport implements Preparable {

	private static final long serialVersionUID = 1L;

	private static final String ROLE = "role";

	private static final String GROUP = "group";

	private String id;

	private RoleGroupService roleGroupService;

	private RoleSchemeService roleSchemeService;

	private RoleGroup roleGroup;

	private RoleScheme roleScheme;

	private List<Role> includedRoles;

	private HashSet<RoleGroup> includedGroups;

	private String buttonLabel;

	private String sectionTitle;

	public void prepare() throws Exception {
		buttonLabel = getText("button.manageGroup.updateRoleGroup");
		sectionTitle = getText("title.manageGroup.updatingRoleGroup");

		Long idToBeUsed = null;
		if (id != null  && !(id.trim().equals(""))) {
			idToBeUsed = Long.parseLong(id);
		} else if ((roleGroup != null) && (roleGroup.getId() != null)) {
			idToBeUsed = roleGroup.getId();
		}
		if (idToBeUsed != null) {
			roleGroup = roleGroupService.findById(idToBeUsed);
		}
		if (roleGroup != null && roleGroup.getId() != null) {
			roleScheme = roleGroup.getScheme();
			sectionTitle = getText("title.manageGroup.roleGroup") + " - "
					+ roleGroup.getName() + " - " + roleGroup.getDescription();
		}
	}

	public String showGroup() {
		if (roleGroup.isGroupOfRoles()) {
			includedRoles = new ArrayList<Role>();
			for (Role aRole : roleGroup.getIncludedRoles()) {
				includedRoles.add(aRole);
			}
			return ROLE;
		} else {
			includedGroups = new HashSet<RoleGroup>();
			includedGroups.addAll(roleGroup.getConsistsOf());
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

	public HashSet<RoleGroup> getIncludedGroups() {
		return includedGroups;
	}

	public void setIncludedGroups(HashSet<RoleGroup> includedGroups) {
		this.includedGroups = includedGroups;
	}

}
package tavant.twms.web.admin.groups;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.orgmodel.RoleGroup;
import tavant.twms.domain.orgmodel.RoleGroupService;
import tavant.twms.domain.orgmodel.RoleScheme;
import tavant.twms.domain.orgmodel.RoleSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

public class CreateRoleGroup extends I18nActionSupport implements Validateable,
		Preparable {

	private static final long serialVersionUID = 1L;

	private String id;

	private RoleGroup roleGroup;

	private RoleScheme roleScheme;

	private RoleGroupService roleGroupService;

	private RoleSchemeService roleSchemeService;

	public void prepare() throws Exception {
		Long idToBeUsed = null;

		if (id != null && !(id.trim().equals(""))) {
			idToBeUsed = Long.parseLong(id);
		} else if ((roleScheme != null) && (roleScheme.getId() != null)) {
			idToBeUsed = roleScheme.getId();
		}
		if (idToBeUsed != null) {
			roleScheme = roleSchemeService.findById(idToBeUsed);
		}
	}

	@Override
	public void validate() {
		if (StringUtils.isBlank(roleGroup.getName())) {
			addActionError("error.manageGroup.nonEmptyRoleGroupName");
		} else {
			RoleGroup group = roleGroupService.findRoleGroupByName(roleGroup
					.getName(), roleScheme);
			if (group != null) {
				addActionError("error.manageGroup.duplicateGroupForScheme");
			}
		}
		if (StringUtils.isBlank(roleGroup.getDescription())) {
			addActionError("error.manageGroup.nonEmptyDescriptionForRoleGroup");
		}
		addActionError("error.manageGroup.nonEmptyRoleSet");
	}

	public String setUpForCreate() {
		roleGroup = new RoleGroup();
		return SUCCESS;
	}

	public String save() {
		return SUCCESS;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

}
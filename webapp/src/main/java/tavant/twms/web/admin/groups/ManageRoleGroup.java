/**
 * 
 */
package tavant.twms.web.admin.groups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.domain.orgmodel.RoleGroup;
import tavant.twms.domain.orgmodel.RoleGroupService;
import tavant.twms.domain.orgmodel.RoleScheme;
import tavant.twms.domain.orgmodel.RoleSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

public class ManageRoleGroup extends I18nActionSupport implements Preparable,
		Validateable {

	private static final long serialVersionUID = 1L;

	private String id;

	private RoleGroup roleGroup;

	private RoleScheme roleScheme;

	private String groupName;

	private String groupDescription;

	private String grouptype;

	private List<RoleGroup> groups;

	private List<RoleGroup> availableGroups = new ArrayList<RoleGroup>();

	private Set<RoleGroup> includedGroups;

	private List<String> includedGroupNames;

	private RoleGroupService roleGroupService;

	private RoleSchemeService roleSchemeService;

	private String buttonLabel;

	private String sectionTitle;

	public void prepare() throws Exception {
		buttonLabel = getText("button.manageGroup.createRoleGroup");
		sectionTitle = getText("label.manageGroup.createNewGroup");

		Long idToBeUsed = null;
		if (id != null && !(id .trim().equals(""))) {
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
		if ("search_groups_for_rolegroup".equals(actionName)) {
			if (StringUtils.isBlank(groupName)
					&& StringUtils.isBlank(groupDescription)) {
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
		if (includedGroupNames != null) {
			includedGroups = new HashSet<RoleGroup>();
			for (String groupName : includedGroupNames) {
				includedGroups.add(roleGroupService.findRoleGroupByName(
						groupName, roleScheme));
			}
		}
		return SUCCESS;
	}

	public String search() throws CatalogException {
		groups = roleGroupService.findGroupsByNameAndDescription(roleScheme,
				groupName, groupDescription);

		// Removed parent to avoid cycilic tree.
		if (roleGroup != null && roleGroup.getId() != null) {
			RoleGroup group = roleGroupService.findById(roleGroup.getId());
			groups.remove(group.findTopMostParent());
		}
		availableGroups.addAll(groups);

		for (RoleGroup group : groups) {
			if ((group.getIsPartOf() != null)
					&& (roleGroup.getId() == null || group.getIsPartOf()
							.getId().compareTo(roleGroup.getId()) != 0)) {
				availableGroups.remove(group);
			}
		}

		if (includedGroupNames != null) {
			includedGroups = new HashSet<RoleGroup>();
			for (String groupName : includedGroupNames) {
				RoleGroup owner = roleGroupService.findRoleGroupByName(
						groupName, roleScheme);
				includedGroups.add(owner);
			}
		}
		return SUCCESS;
	}

	public String add() throws CatalogException {
		if (includedGroupNames != null) {
			includedGroups = new HashSet<RoleGroup>();
			for (String groupName : includedGroupNames) {
				includedGroups.add(roleGroupService.findRoleGroupByName(
						groupName, roleScheme));
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
			for (String groupName : includedGroupNames) {
				roleGroup.includeGroup(roleGroupService.findRoleGroupByName(
						groupName, roleScheme));
			}
			roleGroupService.save(roleGroup);
			addActionMessage("message.manageGroup.roleGroupCreateSuccess",
					roleGroup.getName());
		}
		return SUCCESS;
	}

	private String update() throws Exception, GroupInclusionException,
			GroupHierarchyException {
		String name = roleGroup.getName();
		String description = roleGroup.getDescription();
		roleGroup = roleGroupService.findById(roleGroup.getId());

		roleGroup.setName(name);
		roleGroup.setDescription(description);

		Set<RoleGroup> preIncludedGroups = new HashSet<RoleGroup>();
		preIncludedGroups.addAll(roleGroup.getConsistsOf());
		for (RoleGroup aGroup : preIncludedGroups) {
			if (includedGroupNames.contains(aGroup.getName())) {
				includedGroupNames.remove(aGroup.getName());
			} else {
				roleGroup.removeGroup(aGroup);
			}
		}

		for (String groupName : includedGroupNames) {
			roleGroup.includeGroup(roleGroupService.findRoleGroupByName(
					groupName, roleScheme));
		}
		includedGroups = roleGroup.getConsistsOf();
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
			addActionError("error.manageGroup.nonEmptyDescriptionForROleGroup");
		}
		if ((includedGroupNames == null) || (includedGroupNames.isEmpty())) {
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

	public void setAvailableGroups(List<RoleGroup> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public void setGroups(List<RoleGroup> groups) {
		this.groups = groups;
	}

	public void setIncludedGroups(Set<RoleGroup> includedGroups) {
		this.includedGroups = includedGroups;
	}

}
package tavant.twms.web.admin.groups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeService;
import tavant.twms.domain.orgmodel.RoleScheme;
import tavant.twms.domain.orgmodel.RoleSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

public class ManageRoleSchemes extends I18nActionSupport implements Preparable,
		Validateable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private RoleScheme roleScheme;

	private Set<Purpose> selectedPurposes = new HashSet<Purpose>();

	private List<Purpose> availablePurposes;

	private RoleSchemeService roleSchemeService;

	private PurposeService purposeService;

	private List<String> purposeNames;

	public String showGroups() {
		availablePurposes = getListOfAvailablePurposes();
		name = roleScheme.getName();
		return SUCCESS;
	}

	public String createScheme() {
		roleScheme = new RoleScheme();
		availablePurposes = getListOfAvailablePurposes();
		return SUCCESS;
	}

	public String saveScheme() {
		if (roleScheme.getId() == null) {
			String name = roleScheme.getName().trim();
			roleScheme = new RoleScheme(name);
		} else {
			roleScheme.setName(name.trim());
		}
		roleScheme.setPurposes(selectedPurposes);
		try {
			if (roleScheme.getId() == null) {
				roleSchemeService.save(roleScheme);
				addActionMessage("message.manageGroup.saveSuccess");
				id = roleScheme.getId().toString();
				prepare();
			} else {
				roleSchemeService.update(roleScheme);
				addActionMessage("message.manageGroup.updateSuccess");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return showGroups();
	}
	@Override
	public void validate() {
		 if((name != null) && (name.trim().equals(""))){
	            addActionError("error.manageGroup.nonEmptyName");
	        } else if (roleScheme.getName().trim().equals("")) {
	            addActionError("error.manageGroup.nonEmptyName");
	        }
		 if((purposeNames == null) || (purposeNames.isEmpty())) {
	            addActionError("error.manageGroup.noPurposeChosen");
	        }
		 if(hasActionErrors()) {
	            if(name != null) {
	                showGroups();
	            } else {
	                availablePurposes = getListOfAvailablePurposes();
	            }
	        }
	}

	public void prepare() throws Exception {
		Long idToBeUsed = null;
		if (id != null) {
			idToBeUsed = Long.parseLong(id);
		} else if ((roleScheme != null) && (roleScheme.getId() != null)) {
			idToBeUsed = roleScheme.getId();
		}

		if (idToBeUsed != null) {
			roleScheme = roleSchemeService.findById(idToBeUsed);
		}

		if (purposeNames != null) {
			selectedPurposes = new HashSet<Purpose>();
			for (String name : purposeNames) {
				selectedPurposes.add(purposeService.findPurposeByName(name));
			}
		}
	}

	private List<Purpose> getListOfAvailablePurposes() {
		List<Purpose> allPurposes = purposeService.findAll();
		List<Purpose> employedPurposes = roleSchemeService
				.findEmployedPurposes();
		List<Purpose> result = new ArrayList<Purpose>(allPurposes);
		for (Purpose purpose : employedPurposes) {
			result.remove(purpose);
		}
		return result;
	}

	public List<Purpose> getAvailablePurposes() {
		return availablePurposes;
	}

	public void setAvailablePurposes(List<Purpose> availablePurposes) {
		this.availablePurposes = availablePurposes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPurposeNames() {
		return purposeNames;
	}

	public void setPurposeNames(List<String> purposeNames) {
		this.purposeNames = purposeNames;
	}

	public PurposeService getPurposeService() {
		return purposeService;
	}

	public void setPurposeService(PurposeService purposeService) {
		this.purposeService = purposeService;
	}

	public RoleSchemeService getRoleSchemeService() {
		return roleSchemeService;
	}

	public void setRoleSchemeService(RoleSchemeService roleSchemeService) {
		this.roleSchemeService = roleSchemeService;
	}

	public Set<Purpose> getSelectedPurposes() {
		return selectedPurposes;
	}

	public void setSelectedPurposes(Set<Purpose> selectedPurposes) {
		this.selectedPurposes = selectedPurposes;
	}

	public RoleScheme getRoleScheme() {
		return roleScheme;
	}

	public void setRoleScheme(RoleScheme roleScheme) {
		this.roleScheme = roleScheme;
	}

}

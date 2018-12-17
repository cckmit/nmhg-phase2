package tavant.twms.web.admin.groups.user;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeService;
import tavant.twms.domain.orgmodel.UserScheme;
import tavant.twms.domain.orgmodel.UserSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageUserSchemes extends I18nActionSupport implements Preparable, Validateable {

    private String id;
    private String name;
    
    private UserScheme userScheme;
    
    private Set<Purpose> selectedPurposes = new HashSet<Purpose>();
    private List<Purpose> availablePurposes;
    
    private UserSchemeService userSchemeService;
    private PurposeService purposeService;
    
    private List<String> purposeNames;
    private boolean preview;
    
    public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	/**
     * This Api essentially just shows the scheme details. 
     * @return
     */
    public String showGroups() {
        availablePurposes = getListOfAvailablePurposes();
        name = userScheme.getName();
        return SUCCESS;
    }
    
    /**
     * Creates a new UserScheme instance and shows in a jsp for the user to 
     * fill the relevant details.
     * @return
     */
    public String createScheme() {
        userScheme = new UserScheme();
        availablePurposes = getListOfAvailablePurposes();
        return SUCCESS;
    }    
    
    /**
     * Persists the userscheme. If the scheme already exists it gets updates 
     * else a new scheme gets added. 
     * @return
     */
    public String saveScheme() {
        if(userScheme.getId() == null) {
            String name = userScheme.getName().trim();
            userScheme = new UserScheme(name);
        } else {
            userScheme.setName(name.trim());
        }
        userScheme.setPurposes(selectedPurposes);
        try {
            if(userScheme.getId() == null) {
                userSchemeService.save(userScheme);
                addActionMessage("message.manageGroup.saveSuccess");
                id = userScheme.getId().toString();
                prepare();
            } else {
            	userSchemeService.update(userScheme);
            	addActionMessage("message.manageGroup.updateSuccess");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showGroups();
    }
    
    public void prepare() throws Exception {
        Long idToBeUsed = null;
        if(id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if((userScheme != null) && (userScheme.getId() != null)) {
            idToBeUsed = userScheme.getId();
        }
        
        if(idToBeUsed != null) {
            userScheme = userSchemeService.findById(idToBeUsed);
        }
        
        if(purposeNames != null) {
            selectedPurposes = new HashSet<Purpose>();
            for (String name : purposeNames) {
                selectedPurposes.add(purposeService.findPurposeByName(name));
            }
        }
    }
    
    @Override
    public void validate() {
        if((name != null) && (name.trim().equals(""))){
            addActionError("error.manageGroup.nonEmptyName");
        } else if (userScheme.getName().trim().equals("")) {
            addActionError("error.manageGroup.nonEmptyName");
        }
        
        if((name != null) && (!name.trim().equals(""))) {
            List<UserScheme> listOfSchemes = userSchemeService.findAll();
            for (UserScheme scheme : listOfSchemes) {
                boolean condition1 = name.trim().equals(scheme.getName().trim());
                boolean condition2 = (userScheme.getId().longValue() != scheme.getId().longValue()); 
                if((condition1) && (condition2) ) {
                    addActionError("error.manageGroup.duplicateSchemeName",
                    		new String[] {name});
                }
            }
        }
        
        if((userScheme.getName() != null) && (!userScheme.getName().trim().equals("")) && (userScheme.getId() == null)) {
            List<UserScheme> listOfSchemes = userSchemeService.findAll();
            for (UserScheme scheme : listOfSchemes) {
                if(userScheme.getName().trim().equals(scheme.getName().trim())) {
                    addActionError("error.manageGroup.schemeExists");
                }
            }
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
    
    private List<Purpose> getListOfAvailablePurposes() {
        List<Purpose> allPurposes = purposeService.findAll();
        List<Purpose> employedPurposes = userSchemeService.findEmployedPurposes();
        List<Purpose> result = new ArrayList<Purpose>(allPurposes);
        for (Purpose purpose : employedPurposes) {
            result.remove(purpose);
        }
        return result;
    }
    
    //Only getters and setters follow.
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

    public Set<Purpose> getSelectedPurposes() {
        return selectedPurposes;
    }

    public void setSelectedPurposes(Set<Purpose> selectedPurposes) {
        this.selectedPurposes = selectedPurposes;
    }

    public void setPurposeService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

	public UserScheme getUserScheme() {
		return userScheme;
	}

	public void setUserScheme(UserScheme userScheme) {
		this.userScheme = userScheme;
	}

	public void setUserSchemeService(UserSchemeService userSchemeService) {
		this.userSchemeService = userSchemeService;
	}

}
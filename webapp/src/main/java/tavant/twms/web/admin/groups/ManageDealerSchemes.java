package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeService;
import tavant.twms.domain.orgmodel.DealerScheme;
import tavant.twms.domain.orgmodel.DealerSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageDealerSchemes extends I18nActionSupport implements Preparable, Validateable {

    private String id;
    private String name;
    
    private DealerScheme dealerScheme;
    
    private Set<Purpose> selectedPurposes = new HashSet<Purpose>();
    private List<Purpose> availablePurposes;
    
    private DealerSchemeService dealerSchemeService;
    private PurposeService purposeService;
    
    private List<String> purposeNames;
    private boolean preview;
    
    public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	public String showGroups() {
        availablePurposes = getListOfAvailablePurposes();
        name = dealerScheme.getName();
        return SUCCESS;
    }
    
    public String createScheme() {
        dealerScheme = new DealerScheme();
        availablePurposes = getListOfAvailablePurposes();
        return SUCCESS;
    }    
    
    public String saveScheme() {
        if(dealerScheme.getId() == null) {
            String name = dealerScheme.getName().trim();
            dealerScheme = new DealerScheme(name);
        } else {
            dealerScheme.setName(name.trim());
        }
        dealerScheme.setPurposes(selectedPurposes);
        try {
            if(dealerScheme.getId() == null) {
                dealerSchemeService.save(dealerScheme);
                addActionMessage("message.manageGroup.saveSuccess");
                id = dealerScheme.getId().toString();
                prepare();
            } else {
            	dealerSchemeService.update(dealerScheme);
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
        } else if((dealerScheme != null) && (dealerScheme.getId() != null)) {
            idToBeUsed = dealerScheme.getId();
        }
        
        if(idToBeUsed != null) {
            dealerScheme = dealerSchemeService.findById(idToBeUsed);
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
        } else if (dealerScheme.getName().trim().equals("")) {
            addActionError("error.manageGroup.nonEmptyName");
        }
        
        if((name != null) && (!name.trim().equals(""))) {
            List<DealerScheme> listOfSchemes = dealerSchemeService.findAll();
            for (DealerScheme scheme : listOfSchemes) {
                boolean condition1 = name.trim().equals(scheme.getName().trim());
                boolean condition2 = (dealerScheme.getId().longValue() != scheme.getId().longValue()); 
                if((condition1) && (condition2) ) {
                    addActionError("error.manageGroup.duplicateSchemeName",
                    		new String[] {name});
                }
            }
        }
        
        if((dealerScheme.getName() != null) && (!dealerScheme.getName().trim().equals("")) && (dealerScheme.getId() == null)) {
            List<DealerScheme> listOfSchemes = dealerSchemeService.findAll();
            for (DealerScheme scheme : listOfSchemes) {
                if(dealerScheme.getName().trim().equals(scheme.getName().trim())) {
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
        List<Purpose> employedPurposes = dealerSchemeService.findEmployedPurposes();
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

    public DealerScheme getDealerScheme() {
        return dealerScheme;
    }

    public void setDealerScheme(DealerScheme dealerScheme) {
        this.dealerScheme = dealerScheme;
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

    public void setDealerSchemeService(DealerSchemeService dealerSchemeService) {
        this.dealerSchemeService = dealerSchemeService;
    }

}
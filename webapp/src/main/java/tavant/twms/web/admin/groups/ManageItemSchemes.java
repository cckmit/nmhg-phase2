package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import tavant.twms.domain.catalog.ItemScheme;
import tavant.twms.domain.catalog.ItemSchemeService;
import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeService;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageItemSchemes extends I18nActionSupport implements Preparable, Validateable {

    private String id;

    private String name;

    private ItemScheme itemScheme;

    private Set<Purpose> selectedPurposes = new HashSet<Purpose>();

    private List<Purpose> availablePurposes;

    private ItemSchemeService itemSchemeService;

    private PurposeService purposeService;

    private boolean preview;

    private List<String> purposeNames;

    public String showItemSchemes() {
        this.availablePurposes = getListOfAvailablePurposes();
        this.name = this.itemScheme.getName();
        return SUCCESS;
    }

    public String createScheme() {
        this.itemScheme = new ItemScheme();
        this.availablePurposes = getListOfAvailablePurposes();
        return SUCCESS;
    }

    public String saveScheme() throws Exception {
        if (this.itemScheme.getId() == null) {
            String name = this.itemScheme.getName().trim();
            this.itemScheme = new ItemScheme(name);
        } else {
            this.itemScheme.setName(this.name.trim());
        }
        this.itemScheme.setPurposes(this.selectedPurposes);
        if (this.itemScheme.getId() == null) {
            this.itemSchemeService.save(this.itemScheme);
            addActionMessage("message.manageGroup.saveSuccess");
            this.id = this.itemScheme.getId().toString();
            prepare();
        } else {
            this.itemSchemeService.update(this.itemScheme);
            addActionMessage("message.manageGroup.updateSuccess");
        }
        return showItemSchemes();
    }

    public void prepare() throws Exception {
        Long idToBeUsed = null;
        if (this.id != null) {
            idToBeUsed = Long.parseLong(this.id);
        } else if ((this.itemScheme != null) && (this.itemScheme.getId() != null)) {
            idToBeUsed = this.itemScheme.getId();
        }

        if (idToBeUsed != null) {
            this.itemScheme = this.itemSchemeService.findById(idToBeUsed);
        }

        if (this.purposeNames != null) {
            this.selectedPurposes = new HashSet<Purpose>();
            for (String name : this.purposeNames) {
                this.selectedPurposes.add(this.purposeService.findPurposeByName(name));
            }
        }
    }

    @Override
    public void validate() {
        if ((this.name != null) && (this.name.trim().equals(""))) {
            addActionError("error.manageGroup.nonEmptyName");
        } else if (this.itemScheme.getName().trim().equals("")) {
            addActionError("error.manageGroup.nonEmptyName");
        }

        if ((this.name != null) && (!this.name.trim().equals(""))) {
            List<ItemScheme> listOfSchemes = this.itemSchemeService.findAll();
            for (ItemScheme scheme : listOfSchemes) {
                boolean condition1 = this.name.trim().equals(scheme.getName().trim());
                boolean condition2 = (this.itemScheme.getId().longValue() != scheme.getId()
                        .longValue());
                if ((condition1) && (condition2)) {
                    addActionError("error.manageGroup.duplicateSchemeName",
                            new String[] { this.name });
                }
            }
        }

        if ((this.itemScheme.getName() != null) && (!this.itemScheme.getName().trim().equals(""))
                && (this.itemScheme.getId() == null)) {
            List<ItemScheme> listOfSchemes = this.itemSchemeService.findAll();
            for (ItemScheme scheme : listOfSchemes) {
                if (this.itemScheme.getName().trim().equals(scheme.getName().trim())) {
                    addActionError("error.manageGroup.schemeExists");
                }
            }
        }

        if ((this.purposeNames == null) || (this.purposeNames.isEmpty())) {
            addActionError("error.manageGroup.noPurposeChosen");
        }

        if (hasActionErrors()) {
            if (this.name != null) {
                showItemSchemes();
            } else {
                this.availablePurposes = getListOfAvailablePurposes();
            }
        }
    }

    private List<Purpose> getListOfAvailablePurposes() {
        List<Purpose> allPurposes = this.purposeService.findAll();
        List<Purpose> employedPurposes = this.itemSchemeService.findEmployedPurposes();
        List<Purpose> result = new ArrayList<Purpose>(allPurposes);
        for (Purpose purpose : employedPurposes) {
            result.remove(purpose);
        }
        return result;
    }

    public void setItemSchemeService(ItemSchemeService itemSchemeService) {
        this.itemSchemeService = itemSchemeService;
    }

    public ItemScheme getItemScheme() {
        return this.itemScheme;
    }

    public void setItemScheme(ItemScheme itemScheme) {
        this.itemScheme = itemScheme;
    }

    public List<Purpose> getAvailablePurposes() {
        return this.availablePurposes;
    }

    public void setAvailablePurposes(List<Purpose> availablePurposes) {
        this.availablePurposes = availablePurposes;
    }

    public Set<Purpose> getSelectedPurposes() {
        return this.selectedPurposes;
    }

    public void setSelectedPurposes(Set<Purpose> selectedPurposes) {
        this.selectedPurposes = selectedPurposes;
    }

    public List<String> getPurposeNames() {
        return this.purposeNames;
    }

    public void setPurposeNames(List<String> purposeNames) {
        this.purposeNames = purposeNames;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPurposeService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPreview() {
        return this.preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }
}
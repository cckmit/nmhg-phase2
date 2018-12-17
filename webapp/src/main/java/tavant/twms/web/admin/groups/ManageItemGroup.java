/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import tavant.twms.domain.catalog.*;
import tavant.twms.web.actions.TwmsActionSupport;

import java.util.HashSet;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ManageItemGroup extends TwmsActionSupport implements Preparable {
    
    private static final String ITEM = "item";
    private static final String GROUP = "group";
    
    private String id;
    private String itemSchemeId;

    private ItemGroupService itemGroupService;
    private ItemSchemeService itemSchemeService;

    private ItemGroup itemGroup;
    private ItemScheme itemScheme;

    private Set<Item> includedItems;
    private Set<ItemGroup> includedGroups;
    
    private String buttonLabel;
    private String sectionTitle;

    public void prepare() throws Exception {
        buttonLabel = getText("Create Item Group");
        sectionTitle = getText("title.manageGroup.creatingItemGroup");
        
        Long idToBeUsed = null;
        if(itemSchemeId!=null){
        	id=itemSchemeId;
        }
        if (StringUtils.hasText(id)) {
            idToBeUsed = Long.parseLong(id);
        } else if ((itemGroup != null) && (itemGroup.getId() != null)) {
            idToBeUsed = itemGroup.getId();
        }
        if (idToBeUsed != null) {
            itemGroup = itemGroupService.findById(idToBeUsed);
        }
        if (itemGroup != null && itemGroup.getId() != null) {
            itemScheme = itemGroup.getScheme();
            buttonLabel = "Update Item Group";
            sectionTitle = "Item Group - " + itemGroup.getName() + " - " + itemGroup.getDescription();
        }
    }
    
    public String showGroup() throws CatalogException {
        if (itemGroup.isGroupOfItems()) {
            return ITEM;  
        } else {
            includedGroups = new HashSet<ItemGroup>();
            includedGroups.addAll(itemGroup.getConsistsOf());
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

    public Set<ItemGroup> getIncludedGroups() {
        return includedGroups;
    }

    public void setIncludedGroups(Set<ItemGroup> includedGroups) {
        this.includedGroups = includedGroups;
    }

    public Set<Item> getIncludedItems() {
        return includedItems;
    }

    public void setIncludedItems(Set<Item> includedItems) {
        this.includedItems = includedItems;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public ItemScheme getItemScheme() {
        return itemScheme;
    }

    public void setItemScheme(ItemScheme itemScheme) {
        this.itemScheme = itemScheme;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public void setItemSchemeService(ItemSchemeService itemSchemeService) {
        this.itemSchemeService = itemSchemeService;
    }

	public String getItemSchemeId() {
		return itemSchemeId;
	}

	public void setItemSchemeId(String itemSchemeId) {
		this.itemSchemeId = itemSchemeId;
	}
}
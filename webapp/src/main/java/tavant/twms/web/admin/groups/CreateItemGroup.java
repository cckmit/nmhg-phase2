/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemScheme;
import tavant.twms.domain.catalog.ItemSchemeService;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class CreateItemGroup extends I18nActionSupport implements Validateable, Preparable{

    private String id;
    private String itemSchemeId;

    private ItemGroup itemGroup;
    private ItemScheme itemScheme;
    
    private ItemGroupService itemGroupService;
    private ItemSchemeService itemSchemeService;
    
    public void prepare() throws Exception {
        Long idToBeUsed = null;
        if(itemSchemeId!=null){
        	id=itemSchemeId;
        }
        if (id != null) {
            idToBeUsed = Long.parseLong(id);
        } else if ((itemScheme != null) && (itemScheme.getId() != null)) {
            idToBeUsed = itemScheme.getId();
        }
        if (idToBeUsed != null) {
            itemScheme = itemSchemeService.findById(idToBeUsed);
        }   
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(itemGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyItemGroupName");
        } else {
            ItemGroup group = itemGroupService.findItemGroupByName(itemGroup.getName(), itemScheme);
            if (group != null) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(itemGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForItemGroup");
        }
        addActionError("error.manageGroup.nonEmptyItemSet");
    }
    
    public String setUpForCreate() {
        itemGroup = new ItemGroup();
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
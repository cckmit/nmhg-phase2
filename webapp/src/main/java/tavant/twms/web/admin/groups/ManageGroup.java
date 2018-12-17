/**
 * 
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.catalog.*;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.*;

/**
 * @author Kiran.Kollipara
 * 
 */
@SuppressWarnings("serial")
public class ManageGroup extends I18nActionSupport implements Preparable, Validateable {

    private String id;
    private String itemSchemeId;

    private ItemGroup itemGroup;
    private ItemScheme itemScheme;

    private String groupName;
    private String groupDescription;
    private String grouptype;

    private List<ItemGroup> groups;
    private List<ItemGroup> availableGroups = new ArrayList<ItemGroup>();
    private Set<ItemGroup> includedGroups;

    private List<String> includedGroupNames = new ArrayList<String>();

    private ItemGroupService itemGroupService;
    private ItemSchemeService itemSchemeService;

    private String buttonLabel;
    private String sectionTitle;

    public void prepare() throws Exception {
        buttonLabel = getText("button.manageGroup.createItemGroup");
        sectionTitle = getText("title.manageGroup.creatingItemGroup");

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
        if (itemGroup != null && itemGroup.getId() != null) {
            buttonLabel = getText("button.manageGroup.updateItemGroup");
            sectionTitle = getText("title.manageGroup.itemGroup") + " - " + itemGroup.getName() + " - " + itemGroup.getDescription();
            for(ItemGroup includedItemGroup : itemGroup.getConsistsOf()){
            	includedGroupNames.add(includedItemGroup.getName());
            }
        }
    }

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("search_groups_for_itemgroup".equals(actionName)) {
            if (StringUtils.isBlank(groupName) && StringUtils.isBlank(groupDescription)) {
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
        if(includedGroupNames != null) {
            includedGroups = new HashSet<ItemGroup>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(itemGroupService.findItemGroupByName(groupName, itemScheme));
            }
        }
        return SUCCESS;
    }

    public String search() throws CatalogException {
        groups = itemGroupService.findGroupsByNameAndDescription(itemScheme, groupName,  groupDescription);

        // Removed parent to avoid cycilic tree.
        if (itemGroup != null && itemGroup.getId() != null) {
            ItemGroup group = itemGroupService.findById(itemGroup.getId());
            groups.remove(group.findTopMostParent());
        }
        availableGroups.addAll(groups);

        for (ItemGroup group : groups) {
            if (group.getIsPartOf() != null
                    && (itemGroup.getId() == null || group.getIsPartOf().getId().compareTo(itemGroup.getId()) != 0)) {
                availableGroups.remove(group);
            }
        }

        if (includedGroupNames != null) {
            includedGroups = new HashSet<ItemGroup>();
            for (String groupName : includedGroupNames) {
                ItemGroup owner = itemGroupService.findItemGroupByName(groupName, itemScheme);
                includedGroups.add(owner);
            }
        }
        return SUCCESS;
    }

    public String add() throws CatalogException {
        if (includedGroupNames != null) {
            includedGroups = new HashSet<ItemGroup>();
            for (String groupName : includedGroupNames) {
                includedGroups.add(itemGroupService.findItemGroupByName(groupName, itemScheme));
            }
        }
        return SUCCESS;
    }

    public String save() throws Exception {
        if (itemGroup != null && itemGroup.getId() != null) {
            return update();
        } else {
            String name = itemGroup.getName();
            String description = itemGroup.getDescription();
            itemGroup = itemScheme.createItemGroup(name, description);
            for (String groupName : includedGroupNames) {
                itemGroup.includeGroup(itemGroupService.findItemGroupByName(groupName, itemScheme));
            }
            itemGroupService.save(itemGroup);
            addActionMessage("message.manageGroup.itemGroupCreateSuccess", itemGroup.getName());
        }
        return SUCCESS;
    }

    private String update() throws Exception, GroupInclusionException, GroupHierarchyException {
        String name = itemGroup.getName();
        String description = itemGroup.getDescription();
        itemGroup = itemGroupService.findById(itemGroup.getId());

        itemGroup.setName(name);
        itemGroup.setDescription(description);

        Set<ItemGroup> preIncludedGroups = new HashSet<ItemGroup>();
        preIncludedGroups.addAll(itemGroup.getConsistsOf());
        for (ItemGroup aGroup : preIncludedGroups) {
            if (includedGroupNames.contains(aGroup.getName())) {
                includedGroupNames.remove(aGroup.getName());
            } else {
                itemGroup.removeGroup(aGroup);
            }
        }

        for (String groupName : includedGroupNames) {
            itemGroup.includeGroup(itemGroupService.findItemGroupByName(groupName, itemScheme));
        }
        includedGroups = itemGroup.getConsistsOf();
        itemGroupService.update(itemGroup);
        addActionMessage("message.manageGroup.itemUpdateSuccess");
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

    public List<ItemGroup> getAvailableGroups() {
        return availableGroups;
    }

    public void setAvailableGroups(List<ItemGroup> availableGroups) {
        this.availableGroups = availableGroups;
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

    public Set<ItemGroup> getIncludedGroups() {
        return includedGroups;
    }

    public void setIncludedGroups(Set<ItemGroup> includedGroups) {
        this.includedGroups = includedGroups;
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

    public List<ItemGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ItemGroup> groups) {
        this.groups = groups;
    }

    // ************************ Private Methods **************************//
    private void validateOthers() {
        if (StringUtils.isBlank(itemGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyItemGroupName");
        } else {
            ItemGroup group = itemGroupService.findItemGroupByName(itemGroup.getName(), itemScheme);
            if (group != null && !checkSame(group, itemGroup)) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(itemGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForItemGroup");
        }
        if ((includedGroupNames == null) || (includedGroupNames.isEmpty())) {
            addActionError("error.manageGroup.nonEmptyItemSet");
        }
    }

    private boolean checkSame(ItemGroup source, ItemGroup target) {
        if (target.getId() != null && source.getId().compareTo(target.getId()) == 0) {
            return true;
        }
        return false;
    }

	public String getItemSchemeId() {
		return itemSchemeId;
	}

	public void setItemSchemeId(String itemSchemeId) {
		this.itemSchemeId = itemSchemeId;
	}  
    
}
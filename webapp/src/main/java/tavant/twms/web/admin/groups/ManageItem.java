/**
 * 
 */
package tavant.twms.web.admin.groups;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemScheme;
import tavant.twms.domain.catalog.ItemSchemeService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.actions.AbstractGridAction;

/**
 * @author Kiran.Kollipara
 * 
 */
public class ManageItem extends AbstractGridAction implements Preparable, Validateable {

    private String id;
    
    private String itemSchemeId;

    private ItemGroup itemGroup;

    private ItemScheme itemScheme;

    String itemNumber;

    private String itemDescription;

    private List<Item> items;
    
    private List<Item> availableItems = new ArrayList<Item>();
    
    private List<Long> removedItems;

    private List<Long> addedItems;

    private ItemGroupService itemGroupService;

    private ItemSchemeService itemSchemeService;

    private CatalogService catalogService;
    
    private List<Item> includedItems = new ArrayList<Item>();

    private String buttonLabel;

    private String sectionTitle;
        
    private Integer pageNo = new Integer(0);
    
    private List<Integer> pageNoList = new ArrayList<Integer>();;

    public void prepare() throws Exception {
        this.buttonLabel = getText("button.manageGroup.createItemGroup");
        this.sectionTitle = getText("title.manageGroup.creatingItemGroup");

        Long idToBeUsed = null;
        if(itemSchemeId!=null){
        	id=itemSchemeId;
        }
        if (StringUtils.isNotBlank(this.id)) {
            idToBeUsed = Long.parseLong(this.id);
        } else if ((this.itemScheme != null) && (this.itemScheme.getId() != null)) {
            idToBeUsed = this.itemScheme.getId();
        }
        if (idToBeUsed != null) {
            this.itemScheme = this.itemSchemeService.findById(idToBeUsed);
        }
        if (this.itemGroup != null && this.itemGroup.getId() != null) {
            this.buttonLabel = getText("button.manageGroup.updateItemGroup");
            this.sectionTitle = getText("title.manageGroup.itemGroup") + " - "
                    + this.itemGroup.getName() + " - " + this.itemGroup.getDescription();
        }
    }

    @Override
    public void validate() {
        String actionName = ActionContext.getContext().getName();
        if ("search_items_for_itemgroup".equals(actionName)) {
            if (StringUtils.isBlank(this.itemNumber) && StringUtils.isBlank(this.itemDescription)) {
                addActionError("error.manageGroup.itemNumberOrItemDescriptionRequired");
            }
        } else {
            validateOthers();
        }
    }

    public String manage() throws CatalogException {
        return SUCCESS;
    }

    public String search() throws CatalogException {
        PageResult<Item> pageResult = this.catalogService.findItemsWithNumberAndDescriptionLike(itemNumber, itemDescription, getListCriteria());
        this.items = pageResult.getResult();
        this.availableItems.addAll(this.items);
        for (Item item : this.items) {
            ItemGroup ownerGroup = this.itemGroupService.findGroupContainingItem(item,this.itemScheme);
            if (ownerGroup != null){
                if(this.itemGroup.getId() == null || 
                        ownerGroup.getId().compareTo(this.itemGroup.getId()) != 0) {
                    this.availableItems.remove(item);
                }else if(itemGroup.getId() != null  && ownerGroup.getId().equals(itemGroup.getId())){
                    includedItems.add(item);
                }
            }
        }        
        for (int i=0;i<pageResult.getNumberOfPagesAvailable();i++){
	        	this.pageNoList.add(new Integer(i+1));
	    }
        return SUCCESS;
    }

   public String save() throws Exception {
        if (this.itemGroup != null && this.itemGroup.getId() != null) {
            return update();
        } else {
            String name = this.itemGroup.getName();
            String description = this.itemGroup.getDescription();
            this.itemGroup = this.itemScheme.createItemGroup(name, description);
            Set<Item> newItems = new HashSet<Item>();
            updatedItemMappings(newItems);
            this.itemGroup.setIncludedItems(newItems);
            this.itemGroupService.save(this.itemGroup);
            addActionMessage("message.manageGroup.itemGroupCreateSuccess", this.itemGroup.getName());
        }
        return SUCCESS;
    }

    private String update() throws CatalogException, Exception {
        String name = this.itemGroup.getName();
        String description = this.itemGroup.getDescription();
        this.itemGroup = this.itemGroupService.findById(this.itemGroup.getId());

        this.itemGroup.setName(name);
        this.itemGroup.setDescription(description);
        
        Set<Item> existingItems = this.itemGroup.getIncludedItems();
        updatedItemMappings(existingItems);
        this.itemGroupService.update(this.itemGroup);
        addActionMessage("message.manageGroup.itemUpdateSuccess");
        return SUCCESS;
    }
    
	public String getButtonLabel() {
        return this.buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public String getSectionTitle() {
        return this.sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public List<Item> getAvailableItems() {
        return this.availableItems;
    }

    public void setAvailableItems(List<Item> availableItems) {
        this.availableItems = availableItems;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemDescription() {
        return this.itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getItemNumber() {
        return this.itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public ItemScheme getItemScheme() {
        return this.itemScheme;
    }

    public void setItemScheme(ItemScheme itemScheme) {
        this.itemScheme = itemScheme;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public void setItemSchemeService(ItemSchemeService itemSchemeService) {
        this.itemSchemeService = itemSchemeService;
    }

    // ************************ Private Methods **************************//
    private void validateOthers() {
        if (StringUtils.isBlank(this.itemGroup.getName())) {
            addActionError("error.manageGroup.nonEmptyItemGroupName");
        } else {
            ItemGroup group = this.itemGroupService.findItemGroupByName(this.itemGroup.getName(),
                    this.itemScheme);
            if (group != null && !checkSame(group, this.itemGroup)) {
                addActionError("error.manageGroup.duplicateGroupForScheme");
            }
        }
        if (StringUtils.isBlank(this.itemGroup.getDescription())) {
            addActionError("error.manageGroup.nonEmptyDescriptionForItemGroup");
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

    public String removeItemsFromGroup() throws Exception{
        if(itemGroup != null && itemGroup.getId() != null){
            if(removedItems == null || removedItems.isEmpty()){
                addActionError("error.manageGroup.nonEmptyItemSet");
                return INPUT;
            }else{
                String name = this.itemGroup.getName();
                String description = this.itemGroup.getDescription();
                this.itemGroup = this.itemGroupService.findById(this.itemGroup.getId());
                this.itemGroup.setName(name);
                this.itemGroup.setDescription(description);
                Set<Item> existingItems = this.itemGroup.getIncludedItems();
                if(existingItems.size()>catalogService.findByIds(removedItems).size()){
                    existingItems.removeAll(catalogService.findByIds(removedItems));
                }else{
                	addActionError("error.manageGroup.nonEmptyItemSet");
                	return INPUT;
                }
                this.itemGroupService.update(this.itemGroup);
                addActionMessage("message.manageGroup.itemUpdateSuccess");
            }
        }
        return SUCCESS;
    }
    
    @Override
    protected PageResult<?> getBody() {
        if(itemGroup != null && itemGroup.getId() != null){// edit case
            return catalogService.findItemsForItemGroup(itemGroup.getId(), getCriteria());
        }
        return getEmptyPageResult();

    }

    @Override
    protected void transformRowData(Object result, JSONObject row) throws JSONException {
        Item item = (Item) result;
        row.putOpt("id", item.getId());
        row.putOpt("item.number", item.getNumber());
        row.putOpt("item.description", item.getDescription());
    }

    public List<Long> getAddedItems() {
        return addedItems;
    }

    public void setAddedItems(List<Long> addedItems) {
        this.addedItems = addedItems;
    }

    public List<Long> getRemovedItems() {
        return removedItems;
    }

    public void setRemovedItems(List<Long> removedItems) {
        this.removedItems = removedItems;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public List<Integer> getPageNoList() {
        return pageNoList;
    }

    public void setPageNoList(List<Integer> pageNoList) {
        this.pageNoList = pageNoList;
    }

    public List<Item> getIncludedItems() {
        return includedItems;
    }

    public void setIncludedItems(List<Item> includedItems) {
        this.includedItems = includedItems;
    }

    private void updatedItemMappings(Set<Item> existingItems) {
        if(this.addedItems != null && !this.addedItems.isEmpty()){
            List<Item> itemsToBeAdded = catalogService.findByIds(this.addedItems);
            existingItems.addAll(itemsToBeAdded); // add newly added items 
        }
    }

    private ListCriteria getListCriteria() {
        ListCriteria criteria = new ListCriteria();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(10);
		criteria.setPageSpecification(pageSpecification);   
        criteria.addSortCriteria("item.number", true);   
        criteria.setCaseSensitiveSort(true);
		return criteria;
    }
}   
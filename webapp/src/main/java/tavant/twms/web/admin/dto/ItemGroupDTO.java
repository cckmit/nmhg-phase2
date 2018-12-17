package tavant.twms.web.admin.dto;

import tavant.twms.domain.catalog.ItemGroup;

/**
 * @author kaustubhshobhan.b
 *
 */

public class ItemGroupDTO {

    private String itemCategory;

    private ItemGroup parentGroup;

    private String type;

    private String error;

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public ItemGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(ItemGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }




}

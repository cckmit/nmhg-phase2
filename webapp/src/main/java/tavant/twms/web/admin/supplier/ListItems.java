package tavant.twms.web.admin.supplier;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class ListItems extends SummaryTableAction {

    CatalogService catalogService;

    ItemMappingService itemMappingService;

    String id;

    List<ItemMapping> itemMappings = new ArrayList<ItemMapping>();

    @Override
    protected PageResult<?> getBody() {
        return catalogService.findItems(getCriteria(), getLoggedInUser().getBelongsToOrganization());
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("id", "id", 0, "String", "id", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("Item Number", "number", 15, "String", "number", true, false, false,
                false));
        tableHeadData.add(new SummaryTableColumn("Description", "description", 20, "String"));

        return tableHeadData;
    }

    public String preview() {
        if (id != null) {
            Item item = catalogService.findById(Long.parseLong(id));
            itemMappings = itemMappingService.findItemMappingForItem(item);
        }
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemMapping> getItemMappings() {
        return itemMappings;
    }

    public void setItemMappings(List<ItemMapping> itemMappings) {
        this.itemMappings = itemMappings;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setItemMappingService(ItemMappingService itemMappingService) {
        this.itemMappingService = itemMappingService;
    }

}

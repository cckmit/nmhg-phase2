package tavant.twms.web.actions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemComposition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;

/**
 * @author : janmejay.singh
 *         Date : Jun 21, 2007
 *         Time : 8:04:48 PM
 */
public class ItemBomAction extends TwmsActionSupport {

    public static final String NUMBER = "number",
                               DESCRIPTION = "description",
                               ITEMS = "items",
                               QUANTITY = "quantity",
                               SERIAL_NUMBER = "slNumber",
                               ITEM_NO = "itemNo",
                               SERIAL_NO = "serialNo";

    public static final String ITEM_TREE = "itemTree",
                               SERIAL_TREE = "serialTree";

    private String serialNo;
    private String itemNo;

    private String jsonString;

    private CatalogService catalogService;
    private InventoryService inventoryService;

    public String fetchItemBom() throws CatalogException, JSONException, ItemNotFoundException {
        if(serialNo != null) {
            jsonString = getSerializedResult(inventoryService.findSerializedItem(serialNo));
        } else if(itemNo != null) {
            jsonString = getSerializedResult(catalogService.findItemOwnedByManuf(itemNo));
        } else {
            throw new IllegalStateException("The action has NOT been passed either serialNo or itemNo, hence in a bad state.");
        }
        return SUCCESS;
    }

    private String getSerializedResult(InventoryItem inventoryItem) throws JSONException {
        return new JSONObject().put(ITEM_TREE, getSerializedJSONString(inventoryItem.getOfType()))
                               .put(SERIAL_TREE, getSerializedJSONString(inventoryItem))
                               .put(ITEM_NO, inventoryItem.getOfType().getNumber())
                               .put(SERIAL_NO, inventoryItem.getSerialNumber()).toString();
    }

    private String getSerializedResult(Item item) throws JSONException {
        return new JSONObject().put(ITEM_TREE, getSerializedJSONString(item))
                               .put(ITEM_NO, item.getNumber()).toString();
    }

    public JSONObject getSerializedJSONString(Item item) throws JSONException {
        return putWrapper(serialize(item));
    }

    public JSONObject getSerializedJSONString(InventoryItem inventoryItem) throws JSONException {
        return putWrapper(serialize(inventoryItem));
    }

    private JSONObject putWrapper(JSONObject tree) throws JSONException {
        return new JSONObject().put(ITEMS, new JSONArray().put(tree));
    }

    private JSONObject serialize(InventoryItem inventoryItem) throws JSONException {
        JSONObject node = new JSONObject();
        node.put(NUMBER, inventoryItem.getOfType().getNumber());
        node.put(SERIAL_NUMBER, inventoryItem.getSerialNumber());
        node.put(DESCRIPTION, inventoryItem.getOfType().getDescription());
        JSONArray items = new JSONArray();
        for(InventoryItemComposition childItemComposition : inventoryItem.getComposedOf()) {
            items.put(serialize(childItemComposition.getPart()));
        }
        node.put(ITEMS, items);
        return node;
    }

    private JSONObject serialize(ItemComposition itemComposition) throws JSONException {
        JSONObject node = populateItemDetails(itemComposition.getItem());
        node.put(QUANTITY, itemComposition.getQuantity());
        return node;
    }

    private JSONObject serialize(Item item) throws JSONException {
        JSONObject node = populateItemDetails(item);
        node.put(QUANTITY, 1);
        return node;
    }

    private JSONObject populateItemDetails(Item item) throws JSONException {
        JSONObject node = new JSONObject();
        node.put(NUMBER, item.getNumber());
        node.put(DESCRIPTION, item.getDescription());
        JSONArray items = new JSONArray();
        for(ItemComposition part : item.getParts()) {
            items.put(serialize(part));
        }
        node.put(ITEMS, items);
        return node;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    @Required
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Required
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public String getJsonString() {
        return jsonString;
    }
}

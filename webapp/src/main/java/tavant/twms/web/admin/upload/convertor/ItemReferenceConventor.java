/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.admin.upload.convertor;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

/**
 * @author vineeth.varghese
 * @date Jun 28, 2007
 */
public class ItemReferenceConventor implements Convertor {

    private static final String YES = "Yes";

    private CatalogService catalogService;
    private InventoryService inventoryService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /* (non-Javadoc)
     * @see tavant.twms.web.xls.reader.Convertor#convert(java.lang.Object)
     */
    public Object convert(Object object) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see tavant.twms.web.xls.reader.Convertor#convertWithDependency(java.lang.Object, java.lang.Object)
     */
    public Object convertWithDependency(Object object, Object dependency) {
        String number = (String)object;
        ItemReference itemReference;
        if (YES.equalsIgnoreCase((String)dependency)) {
            InventoryItem invItem;
            try {
                invItem = inventoryService.findSerializedItem(number);
            } catch (ItemNotFoundException e) {
                ConversionErrors.getInstance().addError("Unable to find Inventory Item with serial number["
                        + number + "]");
                invItem = new InventoryItem();
                invItem.setSerialNumber(number);
            }
            itemReference = new ItemReference(invItem);
        } else {
            Item item;
            try {
                item = catalogService.findItemOwnedByManuf(number);
            } catch (CatalogException e) {
                ConversionErrors.getInstance().addError("Unable to find Item with item number["
                        + number + "]");
                item = new Item();
                item.setNumber(number);
            }
            itemReference = new ItemReference(item);
        }
        // TODO Auto-generated method stub
        return itemReference;
    }

}

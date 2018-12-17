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

package tavant.twms.web.typeconverters;

import java.util.ArrayList;
import java.util.Collection;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import org.springframework.util.StringUtils;

public class InventoryItemConverter extends
        NamedDomainObjectConverter<InventoryService, InventoryItem> {

    public InventoryItemConverter() {
        super("inventoryService");
    }

    @Override
    public InventoryItem fetchByName(String serialNumber) throws ItemNotFoundException {
        if (StringUtils.hasText(serialNumber)) {
            return getService().findSerializedItem(serialNumber);
        } else {
            return null;
        }
    }

    @Override
    public String getName(InventoryItem item) {
        return item.getSerialNumber();
    }

    @Override
    public Collection<InventoryItem> fetchByNames(String[] serialNumbers) throws ItemNotFoundException {
        if (serialNumbers != null && serialNumbers.length > 0) {
            Collection<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
            for (int i = 0; i < serialNumbers.length; i++) {
                if (serialNumbers[i] != null && !serialNumbers[i].equals("")) {
                    inventoryItems.add(getService().findSerializedItem(serialNumbers[i]));
                }
            }
            return inventoryItems;
        }
        return null;
    }
}

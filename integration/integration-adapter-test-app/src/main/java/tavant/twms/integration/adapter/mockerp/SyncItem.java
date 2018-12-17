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

package tavant.twms.integration.adapter.mockerp;

import org.openapplications.oagis.x9.ApplicationAreaType;
import tavant.oagis.ItemDTO;
import tavant.oagis.SyncItemDTO;
import tavant.oagis.SyncItemDataAreaDTO;
import tavant.oagis.SyncItemDocumentDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SyncItem {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<Item> items = new ArrayList<Item>();
        for (SyncTracker syncTracker : syncTrackers) {
            items.add((Item) genericDao.findById(Item.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(items);
    }

    private String transform(List<Item> items) {
        SyncItemDocumentDTO syncItemDocumentDTO = SyncItemDocumentDTO.Factory.newInstance();
        createSyncItem(syncItemDocumentDTO, items);
        return syncItemDocumentDTO.toString();
    }

    private void createSyncItem(SyncItemDocumentDTO syncItemDocumentDTO, List<Item> items) {
        SyncItemDTO syncItemDTO = syncItemDocumentDTO.addNewSyncItem();
        createApplicationArea(syncItemDTO);
        createDataArea(syncItemDTO, items);
    }

    private void createApplicationArea(SyncItemDTO syncItemDTO) {
        ApplicationAreaType applicationArea = syncItemDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncItemDTO syncItemDTO, List<Item> items) {
        SyncItemDataAreaDTO dataArea = syncItemDTO.addNewDataArea();
        createSync(dataArea);
        createItems(items, dataArea);
    }

    private void createSync(SyncItemDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createItems(List<Item> items, SyncItemDataAreaDTO dataArea) {
        for (Item item : items) {
            ItemDTO itemDTO = dataArea.addNewItem();
            itemDTO.setBusinessId(""+item.getId());
            itemDTO.setItemNumber(item.getNumber());
            itemDTO.setName(item.getName());
            itemDTO.setDescription(item.getDescription());
            itemDTO.setMake(item.getMake());
            itemDTO.setModel(item.getModel());
            itemDTO.setItemGroupCode(item.getBelongsToItemGroup());
            itemDTO.setProductType(item.getProductType());
        }
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
}


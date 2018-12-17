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
import tavant.oagis.ItemGroupDTO;
import tavant.oagis.SyncItemGroupDTO;
import tavant.oagis.SyncItemGroupDataAreaDTO;
import tavant.oagis.SyncItemGroupDocumentDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SyncItemGroup {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
        for (SyncTracker syncTracker : syncTrackers) {
            itemGroups.add((ItemGroup) genericDao.findById(ItemGroup.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(itemGroups);
    }

    private String transform(List<ItemGroup> itemGroups) {
        SyncItemGroupDocumentDTO syncItemGroupDocumentDTO = SyncItemGroupDocumentDTO.Factory.newInstance();
        createSyncItemGroup(syncItemGroupDocumentDTO, itemGroups);
        return syncItemGroupDocumentDTO.toString();
    }

    private void createSyncItemGroup(SyncItemGroupDocumentDTO syncItemGroupDocumentDTO, List<ItemGroup> itemGroups) {
        SyncItemGroupDTO syncItemGroupDTO = syncItemGroupDocumentDTO.addNewSyncItemGroup();
        createApplicationArea(syncItemGroupDTO);
        createDataArea(syncItemGroupDTO, itemGroups);
    }

    private void createApplicationArea(SyncItemGroupDTO syncItemGroupDTO) {
        ApplicationAreaType applicationArea = syncItemGroupDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncItemGroupDTO syncItemGroupDTO, List<ItemGroup> itemGroups) {
        SyncItemGroupDataAreaDTO dataArea = syncItemGroupDTO.addNewDataArea();
        createSync(dataArea);
        createItemGroups(itemGroups, dataArea);
    }

    private void createSync(SyncItemGroupDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createItemGroups(List<ItemGroup> itemGroups, SyncItemGroupDataAreaDTO dataArea) {
        for (ItemGroup itemGroup : itemGroups) {
            ItemGroupDTO itemGroupDTO = dataArea.addNewItemGroup();
            itemGroupDTO.setBusinessId(""+itemGroup.getId());
            itemGroupDTO.setItemGroupName(itemGroup.getItemGroupName());
            itemGroupDTO.setItemGroupCode(itemGroup.getItemGroupCode());
            itemGroupDTO.setDescription(itemGroup.getDescription());
            itemGroupDTO.setItemGroupType(itemGroup.getItemGroupType());
            itemGroupDTO.setIsPartOf(itemGroup.getIsPartOf());
        }
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
}

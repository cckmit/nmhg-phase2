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
import tavant.oagis.InventoryDTO;
import tavant.oagis.PartyDTO;
import tavant.oagis.SyncInventoryDTO;
import tavant.oagis.SyncInventoryDataAreaDTO;
import tavant.oagis.SyncInventoryDocumentDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.*;

public class SyncInventory {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
        for (SyncTracker syncTracker : syncTrackers) {
            inventoryItems.add((InventoryItem) genericDao.findById(InventoryItem.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(inventoryItems);
    }

    private String transform(List<InventoryItem> inventoryItems) {
        SyncInventoryDocumentDTO syncInventoryDocumentDTO = SyncInventoryDocumentDTO.Factory.newInstance();
        createSyncInventory(syncInventoryDocumentDTO, inventoryItems);
        return syncInventoryDocumentDTO.toString();
    }

    private void createSyncInventory(SyncInventoryDocumentDTO syncInventoryDocumentDTO, List<InventoryItem> inventoryItems) {
        SyncInventoryDTO syncInventoryDTO = syncInventoryDocumentDTO.addNewSyncInventory();
        createApplicationArea(syncInventoryDTO);
        createDataArea(syncInventoryDTO, inventoryItems);
    }

    private void createApplicationArea(SyncInventoryDTO syncInventoryDTO) {
        ApplicationAreaType applicationArea = syncInventoryDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncInventoryDTO syncInventoryDTO, List<InventoryItem> inventoryItems) {
        SyncInventoryDataAreaDTO dataArea = syncInventoryDTO.addNewDataArea();
        createSync(dataArea);
        createInventories(inventoryItems, dataArea);
    }

    private void createSync(SyncInventoryDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createInventories(List<InventoryItem> inventoryItems, SyncInventoryDataAreaDTO dataArea) {
        for (InventoryItem inventoryItem : inventoryItems) {
            InventoryDTO inventoryDTO = dataArea.addNewInventory();
            inventoryDTO.setBusinessId(""+inventoryItem.getId());
            inventoryDTO.setSerialNumber(inventoryItem.getSerialNumber());
            inventoryDTO.setItemNumber(inventoryItem.getItemNumber());
            if ("STOCK".equals(inventoryItem.getType())) {
                inventoryDTO.setType(InventoryDTO.Type.STOCK);
            } else {
                inventoryDTO.setType(InventoryDTO.Type.RETAIL);
            }
            if ("NEW".equals(inventoryItem.getConditionType())) {
                inventoryDTO.setConditionType(InventoryDTO.ConditionType.NEW);
            } else {
                inventoryDTO.setConditionType(InventoryDTO.ConditionType.REFURBISHED);
            }
            inventoryDTO.setRegistrationDate(cal(inventoryItem.getRegistrationDate()));
            inventoryDTO.setDeliveryDate(cal(inventoryItem.getDeliveryDate()));
            inventoryDTO.setShipmentDate(cal(inventoryItem.getShipmentDate()));
            inventoryDTO.setHoursOnMachine(inventoryItem.getHoursOnMachine());

            InventoryDTO.InventoryTransaction invTxDTO = inventoryDTO.addNewInventoryTransaction();
            invTxDTO.setInvoiceDate(cal(inventoryItem.getInvoiceDate()));
            invTxDTO.setInvoiceNumber(inventoryItem.getInvoiceNumber());
            invTxDTO.setSalesOrderNumber(inventoryItem.getSalesOrderNumber());
            invTxDTO.setTransactionDate(cal(inventoryItem.getTransactionDate()));

            PartyDTO seller = invTxDTO.addNewSeller();
            seller.setType(PartyDTO.Type.OEM);

            PartyDTO buyer = invTxDTO.addNewBuyer();
            if ("Dealer".equals(inventoryItem.getBuyerType())) {
                buyer.setType(PartyDTO.Type.DEALER);
            } else if ("EndCustomer".equals(inventoryItem.getBuyerType())) {
                buyer.setType(PartyDTO.Type.END_CUSTOMER);
            } else if ("OEM".equals(inventoryItem.getBuyerType())) {
                buyer.setType(PartyDTO.Type.OEM);
            }
            buyer.setId(inventoryItem.getBuyer());
        }
    }

    private Calendar cal(Date date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = new GregorianCalendar(TimeZone.getDefault());
            calendar.setTime(date);
        }
        return calendar;
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
}

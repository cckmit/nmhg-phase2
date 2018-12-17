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
import org.springframework.transaction.annotation.Transactional;
import tavant.oagis.*;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.*;

public class SyncItemPrice {

    private GenericDao genericDao;

    @Transactional(readOnly = false)
    public String sync(List<SyncTracker> syncTrackers) {
        List<ItemPrice> itemPrices = new ArrayList<ItemPrice>();
        for (SyncTracker syncTracker : syncTrackers) {
            itemPrices.add((ItemPrice) genericDao.findById(ItemPrice.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(itemPrices);
    }

    private String transform(List<ItemPrice> itemPrices) {
        SyncItemPriceDocumentDTO getItemPriceDocumentDTO = SyncItemPriceDocumentDTO.Factory.newInstance();
        createSyncItemPrice(getItemPriceDocumentDTO, itemPrices);
        return getItemPriceDocumentDTO.toString();
    }

    private void createSyncItemPrice(SyncItemPriceDocumentDTO getItemPriceDocumentDTO, List<ItemPrice> itemPrices) {
        SyncItemPriceDTO getItemPriceDTO = getItemPriceDocumentDTO.addNewSyncItemPrice();
        createApplicationArea(getItemPriceDTO);
        createDataArea(getItemPriceDTO, itemPrices);
    }

    private void createApplicationArea(SyncItemPriceDTO getItemPriceDTO) {
        ApplicationAreaType applicationArea = getItemPriceDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncItemPriceDTO getItemPriceDTO, List<ItemPrice> itemPrices) {
        SyncItemPriceDataAreaDTO dataArea = getItemPriceDTO.addNewDataArea();
        createSync(dataArea);
        createItems(itemPrices, dataArea);
    }

    private void createSync(SyncItemPriceDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createItems(List<ItemPrice> itemPrices, SyncItemPriceDataAreaDTO dataArea) {
        for (ItemPrice itemPrice : itemPrices) {
            ItemPriceDTO itemPriceDTO = dataArea.addNewItemPrice();
            itemPriceDTO.setBusinessId(""+itemPrice.getId());
            itemPriceDTO.setItemNumber(itemPrice.getItemNumber());
            itemPriceDTO.setWarrantyType(itemPrice.getWarrantyType());

            DurationDTO durationDTO = itemPriceDTO.addNewForDuration();
            durationDTO.setFrom(cal(itemPrice.getFromDate()));
            durationDTO.setTo(cal(itemPrice.getToDate()));

            MoneyDTO moneyDTO = itemPriceDTO.addNewPrice();
            moneyDTO.setAmount(itemPrice.getAmount());
            moneyDTO.setCurrency(itemPrice.getCurrency());
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

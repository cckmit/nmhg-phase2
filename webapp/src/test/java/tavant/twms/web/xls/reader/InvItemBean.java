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
package tavant.twms.web.xls.reader;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.catalog.ItemReference;

import com.domainlanguage.time.CalendarDate;

/**
 * @author vineeth.varghese
 * @date Jun 5, 2007
 */
public class InvItemBean {

    Long serialNumber;
    Long itemNumber;
    Long usage;
    String dealer;
    CalendarDate date;
    ItemReference itemReference;
    List<ItemReference> itemReferences = new ArrayList<ItemReference>();

    List<FaultCodeBean> faultCodes = new ArrayList<FaultCodeBean>();

    public String getDealer() {
        return dealer;
    }
    public void setDealer(String dealer) {
        this.dealer = dealer;
    }
    public Long getUsage() {
        return usage;
    }
    public void setUsage(Long usage) {
        this.usage = usage;
    }
    public Long getItemNumber() {
        return this.itemNumber;
    }
    public void setItemNumber(Long itemNumber) {
        this.itemNumber = itemNumber;
    }
    public Long getSerialNumber() {
        return this.serialNumber;
    }
    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }
    public CalendarDate getDate() {
        return date;
    }
    public void setDate(CalendarDate date) {
        this.date = date;
    }
    public List<FaultCodeBean> getFaultCodes() {
        return faultCodes;
    }
    public void setFaultCodes(List<FaultCodeBean> faultCodes) {
        this.faultCodes = faultCodes;
    }
    public ItemReference getItemReference() {
        return itemReference;
    }
    public void setItemReference(ItemReference itemReference) {
        this.itemReference = itemReference;
    }
    public List<ItemReference> getItemReferences() {
        return itemReferences;
    }
    public void setItemReferences(List<ItemReference> itemReferences) {
        this.itemReferences = itemReferences;
    }

}

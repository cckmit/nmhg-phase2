/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.dto;

import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.ServiceProvider;

/**
 * @author kaustubhshobhan.b
 *
 */
public class InventoryDTO {

    private String serialNumber;

    private Item itemNumber;

    private ServiceProvider dealer;

    private Integer usage;

    private CalendarDate dateOfShipment;

    private String error;

    private String errorItemNumber;

    public Item getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Item itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public CalendarDate getDateOfShipment() {
        return dateOfShipment;
    }

    public void setDateOfShipment(CalendarDate dateOfShipment) {
        this.dateOfShipment = dateOfShipment;
    }

    public ServiceProvider getDealer() {
        return dealer;
    }

    public void setDealer(ServiceProvider dealer) {
        this.dealer = dealer;
    }

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorItemNumber() {
        return errorItemNumber;
    }

    public void setErrorItemNumber(String errorItemNumber) {
        this.errorItemNumber = errorItemNumber;
    }



}

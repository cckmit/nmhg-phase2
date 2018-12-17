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
package tavant.twms.domain.policy;


import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.infra.ListCriteria;

/**
 * InventoryList criteria object created to obtain task lists by passing in sort or filter criteria.
 * 
 */
public class WarrantyListCriteria extends ListCriteria {

    private WarrantyType type;
    
    private ServiceProvider dealer;

    private String transactionType;

    private WarrantyStatus status;

    private User filedBy;

    public ServiceProvider getDealer() {
        return dealer;
    }

    public void setDealer(ServiceProvider dealer) {
        this.dealer = dealer;
    }

    public WarrantyType getType() {
        return type;
    }

    public void setType(WarrantyType type) {
        this.type = type;
    }

    public WarrantyStatus getStatus() {
        return status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public User getFiledBy() {
        return filedBy;
    }

    public void setFiledBy(User filedBy) {
        this.filedBy = filedBy;
    }
}

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
package tavant.twms.domain.inventory;


import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.ListCriteria;

import com.domainlanguage.time.CalendarDate;

/**
 * InventoryList criteria object created to obtain task lists by passing in sort or filter criteria.
 * 
 */
public class InventoryListCriteria extends ListCriteria {

    private InventoryType type;
    
    private ServiceProvider dealer;
    
    private Organization loggedInUserOrg;
    
    private boolean draft;
    
    private User user;
    
    private CalendarDate vintageStockShipmentDate;
    
    private boolean vintageStock;

	public CalendarDate getVintageStockShipmentDate() {
		return vintageStockShipmentDate;
	}

	public void setVintageStockShipmentDate(CalendarDate vintageStockShipmentDate) {
		this.vintageStockShipmentDate = vintageStockShipmentDate;
	}

	public boolean isVintageStock() {
		return vintageStock;
	}

	public void setVintageStock(boolean vintageStock) {
		this.vintageStock = vintageStock;
	}
    
    public ServiceProvider getDealer() {
        return dealer;
    }

    public void setDealer(ServiceProvider dealer) {
        this.dealer = dealer;
    }

    public InventoryType getType() {
        return type;
    }

    public void setType(InventoryType type) {
        this.type = type;
    }

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Organization getLoggedInUserOrg() {
		return loggedInUserOrg;
	}

	public void setLoggedInUserOrg(Organization loggedInUserOrgs) {
		this.loggedInUserOrg = loggedInUserOrgs;
	}    
}
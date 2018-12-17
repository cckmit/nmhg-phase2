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
package tavant.twms.domain.watchlist;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.AccessType;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.inventory.InventoryItem;

/**
 * @author Radhakrishnan
 *
 */
public class WatchedWarrantyItem {
    Long id;
    
    InventoryItem warrantyItem;

    @Id @AccessType("field")
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    public InventoryItem getWarrantyItem() {
        return warrantyItem;
    }

    public void setWarrantyItem(InventoryItem warrantyItem) {
        this.warrantyItem = warrantyItem;
    }
    
    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("id", id)
            .toString();
    }    
}

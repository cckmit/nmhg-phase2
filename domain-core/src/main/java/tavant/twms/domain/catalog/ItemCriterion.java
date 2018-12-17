/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.catalog;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;


/**
 * @author radhakrishnan.j
 *
 */
@Embeddable
public class ItemCriterion {
    @ManyToOne(fetch=FetchType.LAZY)
    private Item item;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private ItemGroup itemGroup;
    
    private String itemIdentifier;
    
    //For frameworks
    public ItemCriterion() {
        
    }
    
    //For progammers
    public ItemCriterion(Item anItem) {
        Assert.notNull(anItem,"Item criterion does accept a null item");
        item = anItem;
    }
    
    public ItemCriterion(ItemGroup anItemGroup) {
        Assert.notNull(anItemGroup,"Item criterion cannot does a null item group");
        itemGroup = anItemGroup;
    }
    
    public boolean isGroupCriterion() {
        return itemGroup!=null;
    }
    
    /**
     * Gets the item number or the group name.
     * @return
     */
    public String getIdentifier() {
        if(isGroupCriterion()) {
            return itemGroup.getName();
        } else {
            return item.getNumber();
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

	public String getItemIdentifier() {
		return itemIdentifier;
	}

	public void setItemIdentifier(String itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}
}

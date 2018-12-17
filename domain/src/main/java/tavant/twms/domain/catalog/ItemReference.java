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
package tavant.twms.domain.catalog;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.AccessType;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.inventory.InventoryItem;

@Embeddable
@AccessType("field")
public class ItemReference {

    @ManyToOne(fetch = FetchType.LAZY)
    private Item referredItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private InventoryItem referredInventoryItem;

    /**
     * This ideally should have been transient. But making it transient
     * will mean that order by, filtering etc may not be performed on this.
     * <p/>
     * The unserializedItem will be same as referredItem if serialized is false
     * It will be referredInventoryItem.getOfType() if serialized is true.
     * <p/>
     * i.e, unserializedItem = serialized ? referredInventoryItem.getOfType() : referredItem;
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Item unserializedItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup model;

    @Column
    private boolean serialized;

    @Column
    private String unszdSlNo;

    public ItemReference() {
        super();
        //Bug 126279 : putting this at field level doesnt seem to work
        this.serialized = true;
    }

    public ItemReference(Item referredItem) {
        this.serialized = false;
        this.referredItem = referredItem;
        this.unserializedItem = referredItem;
        this.referredInventoryItem = null;
    }


    public ItemReference(InventoryItem referredInventoryItem) {
        this.serialized = true;
        this.referredInventoryItem = referredInventoryItem;
        this.referredItem = null;
        this.unserializedItem = referredInventoryItem.getOfType();
    }

    //do not give a setter for an unserialized Item. It doesnt make sense
    public Item getUnserializedItem() {
        return this.unserializedItem;
    }

    public boolean isSerialized() {
        return this.serialized;
    }

    public void setSerialized(boolean serialized) {
        this.serialized = serialized;
    }

    public InventoryItem getReferredInventoryItem() {
        return this.referredInventoryItem;
    }

    public void setReferredInventoryItem(InventoryItem referredInventoryItem) {
        this.referredInventoryItem = referredInventoryItem; 
        this.serialized = false;
        if (referredInventoryItem != null) {
        	this.serialized = true;
            this.referredItem = referredInventoryItem.getOfType();
            this.unserializedItem = referredInventoryItem.getOfType();
            this.model = referredInventoryItem.getOfType().getModel();
        }
    }

    public Item getReferredItem() {
        return this.referredItem;
    }

    public void setReferredItem(Item referredItem) {
        this.serialized = false;
        this.unserializedItem = referredItem;
        this.referredInventoryItem = null;
        this.referredItem = referredItem;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("serialized", this.serialized)
                .append("inventory item ", this.referredInventoryItem)
                .append("item", this.referredItem)
                .append("model", this.model)
                .toString();
    }

    public ItemGroup getModel() {
        return this.model;
    }

    public void setModel(ItemGroup model) {
        this.model = model;
    }

    public String getUnszdSlNo() {
        return this.unszdSlNo;
    }

    public void setUnszdSlNo(String unszdSlNo) {
        this.unszdSlNo = unszdSlNo;
    }

    public Item referenceForItem() {
        if (getReferredInventoryItem() != null) {
            return getReferredInventoryItem().getOfType();
        } else {
            return getReferredItem();
        }
    }

    public ItemReference clone() {
        ItemReference itemReference = new ItemReference();
        itemReference.setModel(model);
        itemReference.setReferredInventoryItem(referredInventoryItem);
        itemReference.setReferredItem(referredItem);
        itemReference.setSerialized(serialized);
        itemReference.setUnszdSlNo(unszdSlNo);
        return itemReference;
    }
}

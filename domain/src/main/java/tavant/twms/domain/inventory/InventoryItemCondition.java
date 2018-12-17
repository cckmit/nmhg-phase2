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

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class InventoryItemCondition implements AuditableColumns{

    public static final InventoryItemCondition NEW = new InventoryItemCondition("NEW");
    public static final InventoryItemCondition REFURBISHED = new InventoryItemCondition("REFURBISHED");
    public static final InventoryItemCondition SCRAP = new InventoryItemCondition("SCRAP");
    public static final InventoryItemCondition STOLEN = new InventoryItemCondition("STOLEN");
    public static final InventoryItemCondition BOTH = new InventoryItemCondition("BOTH");
    public static final InventoryItemCondition CONSIGNMENT = new InventoryItemCondition("CONSIGNMENT");
    public static final InventoryItemCondition PREMIUM_RENTAL = new InventoryItemCondition("PREMIUM_RENTAL");
    public static final InventoryItemCondition PREOWNED = new InventoryItemCondition("PREOWNED");


    @Id
    private String itemCondition;

    @Version
    private int version;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public InventoryItemCondition(String itemCondition) {
        super();
        this.itemCondition = itemCondition;
    }

    // for hibernate
    public InventoryItemCondition() {
        super();
    }

    public String getItemCondition() {
        return this.itemCondition;
    }

    public void setItemCondition(String condition) {
        this.itemCondition = condition;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    @Override
    public boolean equals(Object o) {
        return this == o || itemCondition.equals(((InventoryItemCondition) o).getItemCondition());
    }

    @Override
    public int hashCode() {
        return itemCondition.hashCode();
    }
}

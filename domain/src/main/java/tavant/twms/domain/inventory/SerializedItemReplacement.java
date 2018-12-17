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
package tavant.twms.domain.inventory;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.Claim;
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
public class SerializedItemReplacement implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "SerializedItemReplacement")
    @GenericGenerator(name = "SerializedItemReplacement", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "SEQ_SerializedItemReplacement"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "for_composition", insertable = false, updatable = false)
    private InventoryItemComposition forComposition;
   
    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem oldPart;

    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem newPart;

    @Embedded
    private ItemReplacementReason dueTo;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public SerializedItemReplacement() {
        super();
    }

    public SerializedItemReplacement(InventoryItem oldPart, InventoryItem newPart,
            ItemReplacementReason dueTo) {
        super();
        this.oldPart = oldPart;
        this.newPart = newPart;
        this.dueTo = dueTo;
    }

    /**
     * @return the dueTo
     */
    public ItemReplacementReason getDueTo() {
        return this.dueTo;
    }

    /**
     * @param dueTo the dueTo to set
     */
    public void setDueTo(ItemReplacementReason asPartOf) {
        this.dueTo = asPartOf;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the newPart
     */
    public InventoryItem getNewPart() {
        return this.newPart;
    }

    /**
     * @param newPart the newPart to set
     */
    public void setNewPart(InventoryItem newPart) {
        this.newPart = newPart;
    }

    /**
     * @return the oldPart
     */
    public InventoryItem getOldPart() {
        return this.oldPart;
    }

    /**
     * @param oldPart the oldPart to set
     */
    public void setOldPart(InventoryItem oldPart) {
        this.oldPart = oldPart;
    }

    /**
     * @return the forComposition
     */
    public InventoryItemComposition getForComposition() {
        return this.forComposition;
    }

    /**
     * @param forComposition the forComposition to set
     */
    public void setForComposition(InventoryItemComposition forComposition) {
        this.forComposition = forComposition;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

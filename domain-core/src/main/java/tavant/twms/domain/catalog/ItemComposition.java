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

import java.text.MessageFormat;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.util.Assert;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ItemComposition implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "ItemComposition")
    @GenericGenerator(name = "ItemComposition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "SEQ_ItemComposition"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Item item;

    private Integer quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Item partOf;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ItemComposition() {
        super();
    }

    public ItemComposition(Item item, Item partOf) {
        this(item, partOf, 1);
    }

    public ItemComposition(Item item, Item partOf, Integer quantity) {
        super();
        Assert.notNull(item, " Item not specified");
        Assert.notNull(partOf, " Item that this item is part of, is not specified");
        setItem(item);
        setPartOf(partOf);
        setQuantity(quantity);
    }

    /**
     * @param partOf the partOf to set
     */
    public void setPartOf(Item partOf) {
        this.partOf = partOf;
    }

    /**
     * @return the partOf
     */
    public Item getPartOf() {
        return this.partOf;
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
     * @return the item
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getForestName() {
        return "Item BOM";
    }

    /**
     * @param quantity the quantity to set
     */
 /*   public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }*/

    @Override
    public String toString() {
        String msgFormat = "(item={0}, quantity={1}, partOf={2})";// ,
        return MessageFormat.format(msgFormat, this.item, this.quantity, this.partOf);// ,nodeInfo);
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.Item;
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
public class WatchedPart implements AuditableColumns{
    Long id;

    @Version
    private int version;

    Item item;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Id
    @AccessType("field")
    @GeneratedValue
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

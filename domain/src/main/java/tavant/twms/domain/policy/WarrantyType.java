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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;

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
public class WarrantyType implements AuditableColumns{
    public static final WarrantyType STANDARD = new WarrantyType("STANDARD");

    public static final WarrantyType EXTENDED = new WarrantyType("EXTENDED");

    public static final WarrantyType POLICY = new WarrantyType("POLICY");

    @Id
    private String type;

    @Version
    private int version;
    
    private String displayValue;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // for hibernate
    public WarrantyType() {
        super();
    }

    public WarrantyType(String type) {
        super();
        this.type = type;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.type;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	
}

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
package tavant.twms.domain.failurestruct;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class TreadBucket implements AuditableColumns{
    @Id
    private String code;

    private String description;

    @Version
    private int version;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {// auto generated
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TreadBucket that = (TreadBucket) o;

        if (this.code != null ? !this.code.equals(that.code) : that.code != null)
            return false;
        if (this.description != null ? !this.description.equals(that.description)
                : that.description != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {// auto generated
        int result;
        result = (this.code != null ? this.code.hashCode() : 0);
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        return result;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

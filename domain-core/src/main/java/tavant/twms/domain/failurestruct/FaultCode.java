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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.common.Views;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author kamal.govindraj
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class FaultCode implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "FaultCode")
	@GenericGenerator(name = "FaultCode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAULT_CODE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @JsonView(value=Views.Internal.class)
    @ManyToOne(optional = false)
    private FaultCodeDefinition definition;

    @JsonView(value=Views.Internal.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private TreadBucket treadBucket;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate lastUpdatedDate;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public FaultCode() {

    }

    public FaultCode(FaultCodeDefinition definition) {
        this.definition = definition;
    }

    public FaultCodeDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(FaultCodeDefinition defintion) {
        this.definition = defintion;
    }

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

    public TreadBucket getTreadBucket() {
        return this.treadBucket;
    }

    public void setTreadBucket(TreadBucket treadBucket) {
        this.treadBucket = treadBucket;
    }

    public CalendarDate getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    public void setLastUpdatedDate(CalendarDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @Override
    public boolean equals(Object o) {// auto generated
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FaultCode faultCode = (FaultCode) o;

        if (this.definition != null ? !this.definition.equals(faultCode.definition)
                : faultCode.definition != null)
            return false;
        if (this.id != null ? !this.id.equals(faultCode.id) : faultCode.id != null)
            return false;
        if (this.treadBucket != null ? !this.treadBucket.equals(faultCode.treadBucket)
                : faultCode.treadBucket != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {// auto generated
        int result;
        result = (this.id != null ? this.id.hashCode() : 0);
        result = 31 * result + (this.definition != null ? this.definition.hashCode() : 0);
        result = 31 * result + (this.treadBucket != null ? this.treadBucket.hashCode() : 0);
        return result;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

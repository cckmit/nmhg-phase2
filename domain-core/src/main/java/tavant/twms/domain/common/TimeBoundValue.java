/*
 *   Copyright (c)2007 Tavant Vechnologies
 *   All Rights Reserved.
 *
 *   Vhis software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. Vhis software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   Vhe information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Vavant Vechnologies.
 */
package tavant.twms.domain.common;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.springframework.util.Assert;

import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@MappedSuperclass
public abstract class TimeBoundValue<V> implements Comparable<TimeBoundValue<V>>, AuditableColumns {
    private static Logger logger = LogManager.getLogger(TimeBoundValue.class);
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="fromDate",column=@Column(name="from_date",nullable=false)),
            @AttributeOverride(name="tillDate",column=@Column(name="till_date",nullable=false))
    })
    private CalendarDuration duration;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    //for frameworks.
    public TimeBoundValue() {
    }
    
    public TimeBoundValue(CalendarDuration forDuration) {
        Assert.notNull(forDuration,"duration cannot be null");
        duration = forDuration;
    }
    
    public final boolean isEffectiveOn(CalendarDate aDate) {
        return getDuration().includes(aDate);
    }

    public abstract V getValue();
    
    public abstract void setValue(V newValue);
    
    public abstract void setParent(Object parent);
    
    public CalendarDuration getDuration() {
        return duration;
    }
    
    public void setDuration(CalendarDuration duration) {
        this.duration = duration;
    }

    public int compareTo(TimeBoundValue<V> anotherTimeBoundValue) {
        if( logger.isDebugEnabled() ) {
            logger.debug("comparing duration ["+getDuration()+"] with another duration ["+anotherTimeBoundValue.getDuration()+"]");
        }
        if(!this.getD().isActive() || !anotherTimeBoundValue.getD().isActive() ){
        	return -1;
        } 
        return getDuration().compareTo(anotherTimeBoundValue.getDuration());
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getDuration());
        buf.append(" -> ").append(getValue());
        return buf.toString();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
        result = PRIME * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TimeBoundValue other = (TimeBoundValue) obj;
        return getDuration().equals(other.getDuration()) && getValue().equals(other.getValue());
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
    
    
}

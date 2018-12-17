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
package tavant.twms.domain.common;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;

import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * All Temporal Objects are sorted by their duration. Subclasses may choose to override this
 * behaviour.
 * 
 * @author vineeth.varghese
 * 
 */
@MappedSuperclass
public abstract class TimeBoundObject implements Comparable<TimeBoundObject>, AuditableColumns {

    private static Logger logger = LogManager.getLogger(TimeBoundObject.class);

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date", nullable = false)),
            @AttributeOverride(name = "tillDate", column = @Column(name = "till_date", nullable = false)) })
    private Duration duration;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public boolean isValidForDate(CalendarDate date) {
        return this.duration.includes(date);
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int compareTo(TimeBoundObject timeBoundObject) {
        if (logger.isDebugEnabled()) {
            logger.debug("comparing duration [" + getDuration() + "] with another duration ["
                    + timeBoundObject.getDuration() + "]");
        }

        return getDuration().compareTo(timeBoundObject.getDuration());
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

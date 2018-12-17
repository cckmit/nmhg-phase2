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
package tavant.twms.domain.common;

import com.domainlanguage.time.CalendarDate;
import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;

import tavant.twms.common.Views;
import tavant.twms.domain.orgmodel.User;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

/**
 * This is the entity that has the columns that are common to all the entities.
 * @author fatima.marneni
 *
 */
@SuppressWarnings("serial")
@Embeddable
@AccessType("field")
public class AuditableColEntity implements Serializable{
	
	@JsonView(value=Views.Public.class)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User lastUpdatedBy;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate createdOn;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate updatedOn;
    
    private Date createdTime;
    
    private Date updatedTime;
    
    private String internalComments;

    private Boolean active = Boolean.TRUE;

	public User getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(User lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public CalendarDate getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(CalendarDate createdOn) {
		this.createdOn = createdOn;
	}

	public CalendarDate getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(CalendarDate updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getInternalComments() {
		return internalComments;
	}

	public void setInternalComments(String internalComments) {
		this.internalComments = internalComments;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

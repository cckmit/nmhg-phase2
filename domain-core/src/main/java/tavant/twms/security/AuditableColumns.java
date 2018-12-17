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
package tavant.twms.security;

import tavant.twms.domain.common.AuditableColEntity;

import com.domainlanguage.time.CalendarDate;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * @author fatima.marneni
 *
 */
public interface AuditableColumns {
	
//	public Long getLastUpdatedBy();
//
//	public void setLastUpdatedBy(Long lastUpdatedBy);
//
//	public CalendarDate getCreatedOn();
//
//	public void setCreatedOn(CalendarDate createdOn);
//
//	public CalendarDate getUpdatedOn();
//
//	public void setUpdatedOn(CalendarDate updatedOn);
//
//	public String getInternalComments();
//
//	public void setInternalComments(String internalComments); 
	
	@JsonIgnore
	public AuditableColEntity getD();
	
	@JsonIgnore
	public void setD(AuditableColEntity auditableColEntity);
	
}

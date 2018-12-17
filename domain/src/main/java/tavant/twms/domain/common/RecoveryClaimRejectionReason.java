/*
 *   Copyright (c) 2007 Tavant Technologies
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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Table;

import tavant.twms.security.AuditableColumns;

/**
 * @author pradipta.a
 * 
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("RECOVERYCLAIMREJECTIONREASON")
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name="rec_clm_reject_reason")
public class RecoveryClaimRejectionReason extends ListOfValues implements AuditableColumns{
	
	public RecoveryClaimRejectionReason() {
		super();
	}
	
	public ListOfValuesType getType() {
		return ListOfValuesType.RecoveryClaimRejectionReason;
	}
}

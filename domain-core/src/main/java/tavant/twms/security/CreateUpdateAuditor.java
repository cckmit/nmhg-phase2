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


import org.acegisecurity.context.SecurityContextHolder;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.model.OrgAwareUserDetails;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("serial")
public class CreateUpdateAuditor implements PreInsertEventListener, PreUpdateEventListener {
    private static Logger logger = LogManager.getLogger(CreateUpdateAuditor.class);

    private SecurityHelper securityHelper;

    /**
     * @param securityHelper
     *            the securityHelper to set
     */
    @Required
    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public boolean onPreInsert(PreInsertEvent preInsertEvent) {
        Object entity = preInsertEvent.getEntity();
       if (entity instanceof AuditableColumns) {
        	if (logger.isDebugEnabled()) {
                logger.debug(" Entity [" + entity + "] has auditable columns ");
            }
        	AuditableColumns auditableCols = (AuditableColumns)entity;
        	
        	User loggedInUser = securityHelper.getLoggedInUser();
        	        	
        	CalendarDate today = Clock.today(); 
        	Date newDate = new Date();
        	auditableCols.getD().setCreatedOn(today);
        	auditableCols.getD().setLastUpdatedBy(loggedInUser);
        	auditableCols.getD().setUpdatedOn(today);
        	auditableCols.getD().setCreatedTime(newDate);
        	auditableCols.getD().setUpdatedTime(newDate);
        	
        	int propertyIndex = preInsertEvent.getPersister().getEntityMetamodel().getPropertyIndex("d");
        	preInsertEvent.getState()[propertyIndex] = auditableCols.getD();     	
        	
        	if (logger.isDebugEnabled()) {
                logger.debug(" Creation of [" + entity + "] audited columns. ");
            }
        }
           return false;
    }

    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof AuditableColumns) {
        	if (logger.isDebugEnabled()) {
                logger.debug(" Entity [" + entity + "] has auditable columns ");
            }
        	AuditableColumns auditableCols = (AuditableColumns)entity;
        	        	
        	User loggedInUser = securityHelper.getLoggedInUser();
        	        	
        	CalendarDate today = Clock.today();
        	
        	if(auditableCols.getD() == null){
        		AuditableColEntity auditableColEntity = new AuditableColEntity();
        		auditableColEntity.setUpdatedOn(today);
        		auditableColEntity.setUpdatedTime(new Date());
        		auditableColEntity.setLastUpdatedBy(loggedInUser);
        		auditableCols.setD(auditableColEntity);
        	}else{
	        	auditableCols.getD().setLastUpdatedBy(loggedInUser);
	        	auditableCols.getD().setUpdatedOn(today);
	        	auditableCols.getD().setUpdatedTime(new Date());
        	}
        	
        	int propertyIndex = event.getPersister().getEntityMetamodel().getPropertyIndex("d");
        	event.getState()[propertyIndex] = auditableCols.getD();
        	
        	if (logger.isDebugEnabled()) {
                logger.debug(" Creation of [" + entity + "] audited columns. ");
            }
        }
        return false;
    }
    
}

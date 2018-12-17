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

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("serial")
public class ClaimCreateUpdateAuditor implements PreInsertEventListener, PreUpdateEventListener {
    private static Logger logger = LogManager.getLogger(ClaimCreateUpdateAuditor.class);

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
        if (entity instanceof Auditable) {
            if (logger.isDebugEnabled()) {
                logger.debug(" Entity [" + entity + "] is auditable ");
            }
            Auditable auditable = (Auditable) entity;
            User loggedInUser = securityHelper.getLoggedInUser();
            auditable.setCreatedBy(loggedInUser);

            CalendarDate today = Clock.today();
            auditable.setCreatedOn(today);

            if (logger.isDebugEnabled()) {
                logger.debug(" Creation of [" + entity + "] audited. ");
            }
        }
        return false;
    }

    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof Auditable) {
            Auditable auditable = (Auditable) entity;
            User loggedInUser = securityHelper.getLoggedInUser();
            auditable.setLastModifiedBy(loggedInUser);

            //CalendarDate today = Clock.today();
            Date today=new Date();
            auditable.setLastModifiedOn(today);

            int propertyIndex = event.getPersister().getEntityMetamodel().getPropertyIndex("lastUpdatedOnDate");
        	event.getState()[propertyIndex] = today;
        	propertyIndex = event.getPersister().getEntityMetamodel().getPropertyIndex("lastUpdatedBy");
        	event.getState()[propertyIndex] = loggedInUser;
        	
            if (logger.isDebugEnabled()) {
                logger.debug(" Updation of [" + entity + "] audited. ");
            }
        }
        return false;
    }
    
}

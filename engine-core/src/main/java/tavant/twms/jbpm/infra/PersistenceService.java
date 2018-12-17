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
package tavant.twms.jbpm.infra;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jbpm.db.SchedulerSession;
import org.jbpm.persistence.JbpmPersistenceException;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;

import tavant.twms.security.SecurityHelper;

/**
 * Service for database persistence related functionality. It overrides the
 * getSession method of jbpm DbPersistenceService class, for returning the
 * session bound to the thread, if no session is bound it creates its own
 * session.
 *
 *
 */
public class PersistenceService extends DbPersistenceService {

    Logger logger = Logger.getLogger(PersistenceService.class);

    public PersistenceService(DbPersistenceServiceFactory persistenceServiceFactory) {
        super(persistenceServiceFactory);
    }

    SchedulerSession schedulerSession;
    private boolean mustSessionBeClosed = false;

    /**
     * Gets the session bound to the existing transaction, if no session is
     * present, opens a new session.
     *
     * @link org.hibernate.SessionFactoryImpl
     */
    @Override
    public Session getSession() {
        Session session = null;
        mustSessionBeClosed = false;
        if (getSessionFactory() != null) {
            // Need a better fix for this. This is a major HACK!!!
            try {
                session = getSessionFactory().getCurrentSession();                
            } catch (HibernateException e) {
                // Not logging the exception stacktrace as it just fills the log
                // file
                this.logger.debug("Unable to get Thread bound session due to [" + e.getMessage()
                        + "] so creating a new session");
                session = super.getSession();
                mustSessionBeClosed = true;
            }
        }
        return session;
    }

    @Override
    public SchedulerSession getSchedulerSession() {
        SecurityHelper securityHelper = new SecurityHelper();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            securityHelper.populateSystemUser();
        }
        if (this.schedulerSession == null) {
            Session session = super.getSession();
            mustSessionBeClosed = true;
            if (session != null) {
                this.schedulerSession = new SchedulerSession(session);
            }
        }
        return this.schedulerSession;
    }
    
    @Override
    public void close() {
		try{
			super.close();
		}
		catch (Exception ex){
        	try {
        		if ((mustSessionBeClosed) && (super.getSession() != null) && (super.getSession().isOpen())){
        			getSession().close();
        		}
            }catch (Exception e) {
                throw new JbpmPersistenceException("couldn\\\'t close hibernate session", e);
            }
            if (ex instanceof RuntimeException)
            	throw (RuntimeException)ex;
            else {
            	logger.error("unhandled checked exception in DbPersistenceService.close()", ex);
            }            	
		}		
	}    
}

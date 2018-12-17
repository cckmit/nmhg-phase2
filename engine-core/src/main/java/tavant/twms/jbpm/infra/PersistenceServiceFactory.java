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

import org.hibernate.SessionFactory;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Service;

/**
 * Overrides the openService method of jbpm DbPersistenceServiceFactory to
 * return the instance of PersistenceService class. If need to use
 * PersistenceService class, configure this factory in the jbpm configuration
 * file.
 * 
 */
public class PersistenceServiceFactory extends DbPersistenceServiceFactory {
    
    SessionFactory sessionFactory = null;

    public Service openService() {
        return new PersistenceService(this);
    }
    
    public synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            BeanLocator beanLocator = new BeanLocator();
            sessionFactory = 
                (SessionFactory)beanLocator.lookupBean("sessionFactory");
        }
        return sessionFactory;
    }
}

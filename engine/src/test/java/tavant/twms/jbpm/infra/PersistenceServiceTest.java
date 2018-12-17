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
import org.hibernate.classic.Session;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import tavant.twms.jbpm.infra.PersistenceService;
import tavant.twms.jbpm.infra.PersistenceServiceFactory;

/**
 * Test case for PersistenceService.
 *
 */
public class PersistenceServiceTest extends MockObjectTestCase {

	PersistenceService persistenceService;
	Mock sessionFactoryMock;
	Mock persistenceServiceFactoryMock;
	Mock session;

	public void setUp(){
		sessionFactoryMock = mock(SessionFactory.class);
		session = mock(Session.class);
		persistenceServiceFactoryMock = mock(PersistenceServiceFactory.class);		
	}
	public void testGetSession(){
		persistenceServiceFactoryMock.expects(once()).method("isTransactionEnabled").will(
				returnValue(false));		
		persistenceService = new PersistenceService((DbPersistenceServiceFactory) persistenceServiceFactoryMock.proxy());
		persistenceServiceFactoryMock.expects(atLeastOnce()).method("getSessionFactory").will(
				onConsecutiveCalls(returnValue(sessionFactoryMock.proxy()),returnValue(sessionFactoryMock.proxy())));
		sessionFactoryMock.expects(once()).method("getCurrentSession").will(returnValue((Session)session.proxy()));
		Session localSession = (Session) persistenceService.getSession();
		assertEquals(localSession, session.proxy());
	}
	
	public void testNullSessionFactory(){
		persistenceServiceFactoryMock.expects(once()).method("isTransactionEnabled").will(
				returnValue(false));		
		persistenceService = new PersistenceService((DbPersistenceServiceFactory) persistenceServiceFactoryMock.proxy());
		persistenceServiceFactoryMock.expects(atLeastOnce()).method("getSessionFactory").will(
				onConsecutiveCalls(returnValue(null),returnValue(sessionFactoryMock.proxy())));
		Session localSession = (Session) persistenceService.getSession();
		assertEquals(localSession, null);
	}

}

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

package tavant.twms.integration.layer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class IntegrationRepositoryTestCase extends
		AbstractTransactionalDataSourceSpringContextTests {

	private SessionFactory sessionFactory;

	@Override
	public String[] getConfigLocations() {
		return new String[] { "classpath:unittest-env-context.xml",
				"classpath*:app-context.xml", "classpath:test-context.xml" };
	}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return SessionFactoryUtils.getSession(sessionFactory, false);
	}

	protected void flushAndClear() {
		Session session = getSession();
		session.flush();
		session.clear();
	}
}

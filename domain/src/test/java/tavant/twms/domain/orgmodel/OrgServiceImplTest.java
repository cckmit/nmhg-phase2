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
package tavant.twms.domain.orgmodel;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class OrgServiceImplTest extends MockObjectTestCase {

	OrgServiceImpl fixture;

	Mock userRepositoryMock;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fixture = new OrgServiceImpl();
		userRepositoryMock = mock(UserRepository.class);
		fixture.setUserRepository((UserRepository) userRepositoryMock.proxy());
	}

	public void testFindUserById() {
		User user = new User();
		final Long USER_ID = new Long(1);
		userRepositoryMock.expects(once()).method("findById").with(eq(USER_ID))
				.will(returnValue(user));
		assertEquals(user, fixture.findUserById(USER_ID));
	}

	public void testFindUserByName() {
		User user = new User();
		final String USER_NAME = "ann";
		userRepositoryMock.expects(once()).method("findByName").with(
				eq(USER_NAME)).will(returnValue(user));
		assertEquals(user, fixture.findUserByName(USER_NAME));
	}

	public void testFindUsersBelongingToRole() {
		Set<User> users = new HashSet<User>();
		final String ROLE_NAME = "processor";
		userRepositoryMock.expects(once()).method("findUsersBelongingToRole")
				.with(eq(ROLE_NAME)).will(returnValue(users));
		assertEquals(users, fixture.findUsersBelongingToRole(ROLE_NAME));

	}

}

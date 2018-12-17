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

import junit.framework.TestCase;

public class UserTest extends TestCase {

	public void testHasAttribute() {
		User user = new User();
		Set<UserAttributeValue> userAttrVals = new HashSet<UserAttributeValue>();
		userAttrVals.add(new UserAttributeValue(new Attribute("Language"),
				"English"));
		userAttrVals.add(new UserAttributeValue(new Attribute("Language"),
				"German"));
		userAttrVals.add(new UserAttributeValue(new Attribute("Language"),
				"Spanish"));

		user.setUserAttrVals(userAttrVals);

		assertTrue(user.hasAttribute("Language", "English"));
		assertTrue(user.hasAttribute("Language", "Spanish"));
		assertFalse(user.hasAttribute("Language", "Hindi"));
		assertFalse(user.hasAttribute("SkillLevel", "Expert"));
	}
}

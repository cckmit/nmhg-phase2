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
package tavant.twms.domain.failurestruct;

import junit.framework.TestCase;
import tavant.twms.common.TWMSException;

/**
 * @author kamal.govindraj
 *
 */
public class NumericCodeGeneratorTest extends TestCase {
	private NumericCodeGenerator fixture = new NumericCodeGenerator();
	
	public void testNextCode() {
		assertEquals("001",fixture.nextCode(null));
		assertEquals("001",fixture.nextCode(""));
		assertEquals("001",fixture.nextCode("000"));
		assertEquals("010",fixture.nextCode("009"));
		assertEquals("100",fixture.nextCode("099"));
		try {
			fixture.nextCode(" ");
			fail("Should throws exception when code is invalid");
		} catch(TWMSException e) {
			assertNotNull(e.getMessage());
		}
		
		try {
			fixture.nextCode("999");
			fail("Should throws exception when code is invalid");
		} catch(TWMSException e) {
			assertNotNull(e.getMessage());
		}
		
	}

}

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
package tavant.twms.domain.claim;

import junit.framework.TestCase;

public class CriteriaTest extends TestCase {

    public void testNewCritria() {
        Criteria criteria = new Criteria();
        criteria.setClaimType("  ");
        criteria.setWarrantyType("STANDARD");

        assertNull(criteria.getClaimType());
        assertEquals(criteria.getWarrantyType(), "STANDARD");

        criteria = new Criteria();
        criteria.setClaimType("  ");
        criteria.setWarrantyType(null);

        assertNull(criteria.getClaimType());
        assertNull(criteria.getWarrantyType());
    }

    public void testReplaceEmptyStringsWithNullValues_EmptyString() {
        Criteria criteria = new Criteria();
        criteria.setClaimType(" ");
        criteria.setWarrantyType("  ");
        assertNull(criteria.getClaimType());
        assertNull(criteria.getWarrantyType());
    }

    public void testReplaceEmptyStringsWithNullValues_AlreadyNullValues() {
        Criteria criteria = new Criteria();
        assertNull(criteria.getClaimType());
        assertNull(criteria.getWarrantyType());
    }

    public void testReplaceEmptyStringsWithNullValues_NonEmptyValues() {
        Criteria criteria = new Criteria();
        criteria.setClaimType("Machine");
        criteria.setWarrantyType("STANDARD");
        assertEquals("Machine", criteria.getClaimType().getType());
        assertEquals("STANDARD", criteria.getWarrantyType());
    }
}

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
package tavant.twms.domain.claim.payment;

import junit.framework.TestCase;

public class CalculationContextTest extends TestCase {

    public void testMin() throws Exception {
        CalculationContext fixture = new CalculationContext();
        assertEquals(20.0D,fixture.executeStatement("minimum Of (20,30);"));
    }
    
    public void testMax() throws Exception {
        CalculationContext fixture = new CalculationContext();
        assertEquals(30.0D,fixture.executeStatement("maximum Of (20,30);"));
    }    
    
    public void testExecuteStatement() throws Exception  {
        CalculationContext fixture = new CalculationContext();
        fixture.addAmount(1200, "OEM Parts");
        fixture.addAmount(-0.1, "Discount Percentage");
        fixture.addAmount(10, "Max Travel Hours");        
        assertTrue(fixture.isAmountDefined("OEM Parts"));
        assertTrue(fixture.isAmountDefined("Discount Percentage"));        
        assertEquals(-110.0D,fixture.executeStatement("OEM Parts * Discount Percentage + minimum of (Max Travel Hours,30)"));
    }

    public void testAddGetAndIsAmountDefined() throws Exception {
        CalculationContext fixture = new CalculationContext();
        fixture.addAmount(1200, "OEM Parts");
        assertTrue(fixture.isAmountDefined("OEM Parts"));
        assertEquals(1200.0D,fixture.getAmount("OEM Parts"));
    }

}

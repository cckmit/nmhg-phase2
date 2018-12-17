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
package tavant.twms.web.xls.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import junit.framework.TestCase;

/**
 * @author vineeth.varghese
 * @date Jun 5, 2007
 */
public class UtilityTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    InputStream input;

    protected void setUp() throws Exception {
        super.setUp();
        String xml = "<excel>" +
                     "  <sheet sheetName=\"Machine\" type=\"tavant.twms.domain.claim.MachineClaim\" startRow=\"5\"> " +
                     "    <block>" +
                     "      <column-mapping column=\"3\" expression=\"referenceItem.referredInventoryItem\"/>" +
                     "    </block>" +
                     "    <block-break column=\"4\"/>" +
                     "    <sheet-end/>" +
                     "  </sheet>" +
                     "</excel>";
        input = new ByteArrayInputStream(xml.getBytes());
    }

    public void testGetReaderFromXML() throws Exception {
        Reader reader = Utility.getReaderFromXML(input);
        assertNotNull(reader);
    }

}

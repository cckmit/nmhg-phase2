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
package tavant.twms.infra;

import java.util.TimeZone;

import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

public class CalendarDateTest extends TestCase {

    public void testToString() {
        CalendarDate fixture = CalendarDate.date(2006, 9, 1);
        assertEquals("2006-09-01",fixture.toString("yyyy-MM-dd"));
        assertEquals("2006/09/01",fixture.toString("yyyy/MM/dd"));
        assertEquals("01/09/2006",fixture.toString("dd/MM/yyyy"));
        
        TimePoint timePoint = TimePoint.atGMT(2006, 9, 1, 8, 12, 15);
        assertEquals("2006-09-01-08:12", timePoint.toString("yyyy-MM-dd-hh:mm", TimeZone.getTimeZone("GMT")));
    }
}

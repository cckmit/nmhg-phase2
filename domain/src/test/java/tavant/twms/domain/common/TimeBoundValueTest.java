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
package tavant.twms.domain.common;

import java.util.TimeZone;

import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
public class TimeBoundValueTest extends TestCase {
    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }
    
    public void testIsEffectiveOn() {
        CalendarDate today = Clock.today();        
        CalendarDate yesterday = today.previousDay();        
        CalendarDate tomorrow = today.nextDay();
        CalendarDuration duration = new CalendarDuration(yesterday,tomorrow);
        TimeBoundInteger fixture = new TimeBoundInteger(10,duration);
        assertTrue(fixture.isEffectiveOn(yesterday));
        assertTrue(fixture.isEffectiveOn(today));
        assertTrue(fixture.isEffectiveOn(tomorrow));
        
        assertFalse(fixture.isEffectiveOn(yesterday.previousDay()));
        assertFalse(fixture.isEffectiveOn(tomorrow.nextDay()));
    }
    
    
}

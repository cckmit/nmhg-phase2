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
package tavant.twms.domain.common;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class CalendarDurationTest extends TestCase {
    static {
        Clock.setDefaultTimeZone( TimeZone.getDefault() );
        Clock.timeSource();
    }
    
    public void testIncludesCurrentDate_Yes() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate endsOn = Clock.today().plusMonths(4);        
        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertTrue(fixture.includesCurrentDate());
    }

    public void testIncludesCurrentDate_No() {
        CalendarDate startsFrom = Clock.today().plusMonths(4);
        CalendarDate endsOn = Clock.today().plusMonths(14);        
        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertFalse(fixture.includesCurrentDate());
    }    
    
    public void testIncludes_boundaryCondition_lowerBound_Yes() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate endsOn = Clock.today().plusMonths(4);        
        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertTrue(fixture.includes(startsFrom));
    }

    public void testIncludes_boundaryCondition_upperBound_Yes() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate endsOn = Clock.today().plusMonths(4);        
        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertTrue(fixture.includes(endsOn));
    }

    public void testIncludes_boundaryCondition_lowerBound_No() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate endsOn = Clock.today().plusMonths(4);        
        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertFalse(fixture.includes(startsFrom.previousDay()));
    }

    public void testIncludes_boundaryCondition_upperBound_No() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate endsOn = startsFrom.plusMonths(2);

        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertFalse(fixture.includes(endsOn.nextDay()));
    }
    
    
    public void testIncludes() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint = startsFrom.plusMonths(2);
        CalendarDate endsOn = midPoint.plusMonths(2);

        CalendarDuration fixture = new CalendarDuration(startsFrom,endsOn);
        assertTrue(fixture.includes(midPoint));
    }
    
    public void testContinuousDurations() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint = startsFrom.plusMonths(2);
        CalendarDate endsOn = midPoint.plusMonths(2);
        
        CalendarDuration fixture1 = new CalendarDuration(startsFrom,midPoint);
        CalendarDuration fixture2 = new CalendarDuration(midPoint.nextDay(),endsOn);
        assertTrue( CalendarDuration.continuousDurations(fixture1, fixture2));
        assertTrue( fixture1.isContinousWith(fixture2));
        assertFalse( CalendarDuration.durationsOverlap(fixture1, fixture2));
        assertFalse( fixture1.overlapsWith(fixture2));        
    }
    
    public void testDurationsWithGap() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint = startsFrom.plusMonths(2);
        CalendarDate endsOn = midPoint.plusMonths(4);
        
        CalendarDuration fixture1 = new CalendarDuration(startsFrom,midPoint);
        CalendarDuration fixture2 = new CalendarDuration(midPoint.plusMonths(1),endsOn);
        assertFalse( CalendarDuration.continuousDurations(fixture1, fixture2));
        assertFalse( fixture1.isContinousWith(fixture2));
        assertFalse( CalendarDuration.durationsOverlap(fixture1, fixture2));
        assertFalse( fixture1.overlapsWith(fixture2));        
    }
    
    public void testCompareTo() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint1 = startsFrom.plusMonths(2);
        CalendarDate midPoint2 = midPoint1.plusMonths(2);
        CalendarDate endsOn = midPoint2.plusMonths(2);

        
        CalendarDuration fixture1 = new CalendarDuration(startsFrom,midPoint1);
        CalendarDuration fixture2 = new CalendarDuration(midPoint1.plusDays(10),midPoint2);
        CalendarDuration fixture3 = new CalendarDuration(midPoint2.plusDays(10),endsOn);
        
        assertEquals(-1,fixture1.compareTo(fixture2));
        assertEquals(-1,fixture2.compareTo(fixture3));
        assertEquals(-1,fixture1.compareTo(fixture3));
        
        assertEquals(1,fixture2.compareTo(fixture1));
        assertEquals(1,fixture3.compareTo(fixture2));
        assertEquals(1,fixture3.compareTo(fixture1));        
        
    }
    
    public void testGetFirstMissingDuration() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint1 = startsFrom.plusMonths(2);
        CalendarDate midPoint2 = midPoint1.plusMonths(2);
        CalendarDate endsOn = midPoint2.plusMonths(2);

        
        CalendarDuration fixture1 = new CalendarDuration(startsFrom,midPoint1);
        CalendarDuration fixture2 = new CalendarDuration(midPoint1.plusDays(10),midPoint2);
        CalendarDuration fixture3 = new CalendarDuration(midPoint2.plusDays(10),endsOn);
        
        assertEquals(-1,fixture1.compareTo(fixture2));
        assertEquals(-1,fixture2.compareTo(fixture3));
        assertEquals(-1,fixture1.compareTo(fixture3));        
        
        SortedSet<CalendarDuration> fixtures = new TreeSet<CalendarDuration>();
        fixtures.add(fixture1);
        fixtures.add(fixture2);
        fixtures.add(fixture3);
        
        CalendarDuration expectedMissingDuration = new CalendarDuration(fixture1.getTillDate().nextDay(),fixture2.getFromDate().previousDay());
        CalendarDuration actualMissingDuration = CalendarDuration.getFirstMissingDuration(fixtures);
        assertEquals( expectedMissingDuration,actualMissingDuration);
    }
    
    public void testGetFirstOverlappingDuration() {
        CalendarDate startsFrom = Clock.today().plusMonths(-24);
        CalendarDate midPoint1 = startsFrom.plusMonths(2);
        CalendarDate midPoint2 = midPoint1.plusMonths(2);
        CalendarDate endsOn = midPoint1.plusMonths(2);

        CalendarDuration fixture1 = new CalendarDuration(startsFrom,midPoint1);
        CalendarDuration fixture2 = new CalendarDuration(midPoint1.plusDays(-10),midPoint2);
        CalendarDuration fixture3 = new CalendarDuration(midPoint2.plusDays(-10),endsOn);
        
        SortedSet<CalendarDuration> fixtures = new TreeSet<CalendarDuration>();
        fixtures.add(fixture1);
        fixtures.add(fixture2);
        fixtures.add(fixture3);
        
        assertEquals( fixture2,CalendarDuration.getFirstOverlappingDuration(fixtures));
    }    
    
    public void testEqualsNHashCode_true() {
        CalendarDate _today = Clock.today();
        CalendarDuration duration = new CalendarDuration(_today,_today);
        CalendarDuration duration2 = new CalendarDuration(_today,_today);
        assertEquals(duration,duration2);
        
        assertEquals(duration.hashCode(),duration2.hashCode());
        
        Set<CalendarDuration> set = new HashSet<CalendarDuration>();
        set.add(duration);
        assertTrue(set.contains(duration2));
    }
    
    public void testEqualsNHashCode_AllFalseScenarios() {
        CalendarDate _today = Clock.today();
        CalendarDuration duration = new CalendarDuration(_today,_today);
        CalendarDuration duration2 = new CalendarDuration(_today,_today);
        duration.setFromDate(null);
        assertNotSame(duration,duration2);
        
        duration.setFromDate(_today);
        duration.setTillDate(null);
        assertNotSame(duration,duration2);
        
        duration2.setTillDate(_today);
        duration2.setFromDate(null);
        assertNotSame(duration,duration2);
        
        duration2.setTillDate(null);
        duration2.setFromDate(_today);
        assertNotSame(duration,duration2);
        
        duration.setFromDate(_today.nextDay());
        assertNotSame(duration,duration2);
    }
    
    public void testIncludesAnotherInterval() {
        CalendarDate _today = Clock.today();
        CalendarDate _yday = _today.previousDay();
        CalendarDate _dayBeforeYday = _yday.previousDay();
        CalendarDate _tomorrow = _today.nextDay();
        CalendarDate _dayAfterTomorrow = _tomorrow.nextDay();
        
        CalendarDuration outerMost = new CalendarDuration(_dayBeforeYday,_dayAfterTomorrow);
        CalendarDuration sandwiched = new CalendarDuration(_yday,_tomorrow);
        CalendarDuration innerMost = new CalendarDuration(_today,_today);
        
        assertTrue( outerMost.includes(sandwiched));
        assertTrue( outerMost.includes(innerMost));
        assertTrue( sandwiched.includes(innerMost));
        assertTrue( innerMost.includes(innerMost));
        
        assertFalse( innerMost.includes(sandwiched));
        assertFalse( innerMost.includes(outerMost));
        assertFalse( sandwiched.includes(outerMost));
    }
}

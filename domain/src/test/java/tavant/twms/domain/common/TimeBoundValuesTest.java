package tavant.twms.domain.common;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class TimeBoundValuesTest extends TestCase {
    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }
    private CalendarDate today;
    private CalendarDate tomorrow;
    private CalendarDate yesterday;
    private CalendarDate dayAfterTomorrow;
    private CalendarDate dayBeforeYesterday;
    TimeBoundIntegers fixture;
    
    
    @Override
    protected void setUp() throws Exception {
        today = Clock.today();
        tomorrow = today.nextDay();
        dayAfterTomorrow = tomorrow.nextDay();
        yesterday = today.previousDay();
        dayBeforeYesterday = yesterday.previousDay();
        fixture = new TimeBoundIntegers();        
    }

    public void testSet() throws Exception {
        fixture.set(10, new CalendarDuration(today,today));
        assertEquals(1,fixture.getEntries().size());
        Iterator<TimeBoundInteger> entryIterator = fixture.getEntries().iterator();
        TimeBoundInteger timeBoundValue = entryIterator.next();
        assertEquals(new Integer(10),timeBoundValue.getValue());
        assertEquals(new CalendarDuration(today,today),timeBoundValue.getDuration());
        
        fixture.set(12,new CalendarDuration(tomorrow,tomorrow));
        fixture.set(13,new CalendarDuration(tomorrow,tomorrow));

        entryIterator = fixture.getEntries().iterator();
        entryIterator.next();
        timeBoundValue = entryIterator.next();        
        assertEquals(2,fixture.getEntries().size());
        assertEquals(new Integer(13),timeBoundValue.getValue());
        assertEquals(new CalendarDuration(tomorrow,tomorrow),timeBoundValue.getDuration());
    }

    public void testSet_ExistingDuration() throws Exception {
        fixture.set(10, new CalendarDuration(today,today));
        assertEquals(1,fixture.getEntries().size());
        
        Iterator<TimeBoundInteger> entryIterator = fixture.getEntries().iterator();
        TimeBoundInteger timeBoundValue = entryIterator.next();
        assertEquals(new Integer(10),timeBoundValue.getValue());
        assertEquals(new CalendarDuration(today,today),timeBoundValue.getDuration());
        
        fixture.set(12,new CalendarDuration(tomorrow,tomorrow));
        entryIterator = fixture.getEntries().iterator();
        entryIterator.next();
        timeBoundValue = entryIterator.next();        
        assertEquals(2,fixture.getEntries().size());
        assertEquals(new Integer(12),timeBoundValue.getValue());
        assertEquals(new CalendarDuration(tomorrow,tomorrow),timeBoundValue.getDuration());
        
        DurationOverlapException ex = null;
        try {
            fixture.set(13, new CalendarDuration(yesterday,tomorrow));
        } catch(DurationOverlapException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertEquals(new CalendarDuration(yesterday,tomorrow),ex.getOffendingDuration());
        
        SortedSet<CalendarDuration> offendedDurations = new TreeSet<CalendarDuration>();
        offendedDurations.add(new CalendarDuration(today,today));
        offendedDurations.add(new CalendarDuration(tomorrow,tomorrow));
        assertEquals(offendedDurations,ex.getOffendedDurations());
    }    
    
    public void testRemoveAllEntries_ForDurationThatCompletelySpansMultipleDurations() throws DurationOverlapException {
        fixture.set(10, new CalendarDuration(today,today));
        fixture.set(11, new CalendarDuration(tomorrow,tomorrow));
        
        fixture.removeEntriesFor(new CalendarDuration(dayBeforeYesterday,dayAfterTomorrow));
        assertNull( fixture.getEntryAsOf(today));
        assertNull( fixture.getEntryAsOf(tomorrow));
    }

    
    public void testRemoveAllEntries_ForDurationThatPartiallySpansMultipleDurations() throws DurationOverlapException {
        fixture.set(10, new CalendarDuration(today,today));
        fixture.set(11, new CalendarDuration(tomorrow,tomorrow));        
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        
        fixture.removeEntriesFor(new CalendarDuration(today,tomorrow));
        assertNull( fixture.getEntryAsOf(today));
        assertNull( fixture.getEntryAsOf(tomorrow));
        assertEquals(12,fixture.getValueAsOf(dayAfterTomorrow).intValue());
    }
    
    
    public void testRemoveAllEntries() throws DurationOverlapException {
        assertEquals(0,fixture.getEntries().size());
        fixture.set(10, new CalendarDuration(today,today));
        assertEquals(1,fixture.getEntries().size());
        fixture.set(11, new CalendarDuration(tomorrow,tomorrow));
        assertEquals(2,fixture.getEntries().size());
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        assertEquals(3,fixture.getEntries().size());
        fixture.removeAllEntries();
        assertEquals(0,fixture.getEntries().size());
    }

    public void testExistingDurationsSpannedBy() throws DurationOverlapException {
        assertEquals(0,fixture.getEntries().size());
        fixture.set(10, new CalendarDuration(today,today));
        assertEquals(1,fixture.getEntries().size());
        fixture.set(11, new CalendarDuration(tomorrow,tomorrow));
        assertEquals(2,fixture.getEntries().size());
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        assertEquals(3,fixture.getEntries().size());
        fixture.removeAllEntries();
        assertEquals(0,fixture.getEntries().size());
    }

    public void testHasAValueOn() throws DurationOverlapException {
        fixture.set(10, new CalendarDuration(today,today));
        fixture.set(11, new CalendarDuration(tomorrow,tomorrow));
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        assertTrue(fixture.hasAValueOn(today));
        assertTrue(fixture.hasAValueOn(tomorrow));
        assertTrue(fixture.hasAValueOn(dayAfterTomorrow));
        assertFalse(fixture.hasAValueOn(yesterday));
        assertFalse(fixture.hasAValueOn(dayAfterTomorrow.nextDay()));
    }

    public void testGetEntryAsOf() throws DurationOverlapException {
        fixture.set(11, new CalendarDuration(yesterday,tomorrow));
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        assertEquals(new TimeBoundInteger(11,new CalendarDuration(yesterday,tomorrow)),fixture.getEntryAsOf(yesterday));
        assertEquals(new TimeBoundInteger(11,new CalendarDuration(yesterday,tomorrow)),fixture.getEntryAsOf(today));
        assertEquals(new TimeBoundInteger(11,new CalendarDuration(yesterday,tomorrow)),fixture.getEntryAsOf(tomorrow));        
        assertEquals(new TimeBoundInteger(12,new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow)),fixture.getEntryAsOf(dayAfterTomorrow));
        assertNull(fixture.getEntryAsOf(dayBeforeYesterday));
        assertNull(fixture.getEntryAsOf(dayAfterTomorrow.nextDay()));
    }

    public void testGetValueAsOf() throws DurationOverlapException {
        fixture.set(11, new CalendarDuration(yesterday,tomorrow));
        fixture.set(12, new CalendarDuration(dayAfterTomorrow,dayAfterTomorrow));
        assertEquals(11,fixture.getValueAsOf(yesterday).intValue());
        assertEquals(11,fixture.getValueAsOf(today).intValue());
        assertEquals(11,fixture.getValueAsOf(tomorrow).intValue());        
        assertEquals(12,fixture.getValueAsOf(dayAfterTomorrow).intValue());
        assertNull(fixture.getValueAsOf(dayBeforeYesterday));
        assertNull(fixture.getValueAsOf(dayAfterTomorrow.nextDay()));
    }

}

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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 * 
 */
@Embeddable
public class CalendarDuration implements Comparable<CalendarDuration> {
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    @Column(nullable = false)
    private CalendarDate fromDate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    @Column(nullable = false)
    private CalendarDate tillDate;

    public CalendarDuration() {
    }

    public CalendarDuration(CalendarDate fromDate, CalendarDate tillDate) {
        super();
        Assert.notNull(fromDate, "start date for a duration cannot be null");
        Assert.notNull(tillDate, "end date for a duration cannot be null");
        if (fromDate.isAfter(tillDate)) {
            throw new IllegalArgumentException("Invalid date range, from date [" + fromDate
                    + "] is after till date [" + tillDate + "]");
        }
        this.fromDate = fromDate;
        this.tillDate = tillDate;
    }

    public CalendarDate getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(CalendarDate fromDate) {
        this.fromDate = fromDate;
    }

    public CalendarDate getTillDate() {
        return this.tillDate;
    }

    public void setTillDate(CalendarDate tillDate) {
        this.tillDate = tillDate;
    }

    public boolean includesCurrentDate() {
        return includes(Clock.today());
    }

    public boolean includes(CalendarDate aDate) {
        if ((this.fromDate == null) || (this.tillDate == null)) {
            return false;
        }
        return interval().includes(aDate);
    }

    /**
     * @return
     */
    private CalendarInterval interval() {
        return CalendarInterval.inclusive(this.fromDate, this.tillDate);
    }

    public boolean includes(CalendarDuration anotherDuration) {
        CalendarInterval thisInterval = interval();
        CalendarInterval otherInterval = anotherDuration.interval();
        boolean answer = thisInterval.covers(otherInterval);
        return answer;
    }

    @Override
    public String toString() {
        return new StringBuffer().append('[').append(this.fromDate).append(',').append(
                this.tillDate).append(']').toString();
    }

    public int compareTo(CalendarDuration otherDuration) {
        //for stable sorting.
        if (equals(otherDuration)) {
            return 0;
        }
        Assert.notNull(this.fromDate, "Invalid duration from date is not specified");
        Assert.notNull(otherDuration, "Duration to be compared with is not specified");
        Assert.notNull(otherDuration.fromDate,
                "Duration to be compared with is invalid. From date not specified");
        int compareTo = this.fromDate.previousDay().isBefore(otherDuration.fromDate) ? -1 : 1;
        return compareTo;
    }

    public static boolean durationsOverlap(CalendarDuration oneDuration,
            CalendarDuration otherDuration) {
        TreeSet<CalendarDuration> durations = new TreeSet<CalendarDuration>();
        durations.add(oneDuration);
        durations.add(otherDuration);
        Iterator<CalendarDuration> iterator = durations.iterator();
        CalendarDuration firstDuration = iterator.next();
        CalendarDuration secondDuration = iterator.next();
        if ((firstDuration.tillDate == null) || (secondDuration.fromDate == null)) {
            return false;
        }
        CalendarDate dayAfterFirstDuration = firstDuration.tillDate.nextDay();
        return dayAfterFirstDuration.isAfter(secondDuration.fromDate);
    }

    public static boolean continuousDurations(CalendarDuration oneDuration,
            CalendarDuration otherDuration) {
        TreeSet<CalendarDuration> durations = new TreeSet<CalendarDuration>();
        durations.add(oneDuration);
        durations.add(otherDuration);
        Iterator<CalendarDuration> iterator = durations.iterator();
        CalendarDuration firstDuration = iterator.next();
        CalendarDuration secondDuration = iterator.next();
        if ((firstDuration.tillDate == null) || (secondDuration.fromDate == null)) {
            return false;
        }
        CalendarDate dayAfterFirstDuration = firstDuration.tillDate.nextDay();
        boolean ifNextDuratonBeginsOnDayAfterFirstDuration = dayAfterFirstDuration
                .equals(secondDuration.fromDate);
        return ifNextDuratonBeginsOnDayAfterFirstDuration;
    }

    public boolean overlapsWith(CalendarDuration otherDuration) {
        return durationsOverlap(this, otherDuration);
    }

    public boolean isContinousWith(CalendarDuration otherDuration) {
        return continuousDurations(this, otherDuration);
    }

    public static CalendarDuration getFirstOverlappingDuration(
            SortedSet<CalendarDuration> aSetOfDurations) {
        if (!aSetOfDurations.isEmpty()) {
            Iterator<CalendarDuration> durationIterator = aSetOfDurations.iterator();
            CalendarDuration previousDuration = durationIterator.next();
            while (durationIterator.hasNext()) {
                CalendarDuration nextDuration = durationIterator.next();
                if (previousDuration.overlapsWith(nextDuration)) {
                    return nextDuration;
                }
            }
        }
        return null;
    }

    public static CalendarDuration getFirstMissingDuration(
            SortedSet<CalendarDuration> aSetOfDurations) {
        if (!aSetOfDurations.isEmpty()) {
            Iterator<CalendarDuration> durationIterator = aSetOfDurations.iterator();
            CalendarDuration previousDuration = durationIterator.next();
            CalendarDuration missingDuration = null;
            while (durationIterator.hasNext()) {
                CalendarDuration nextDuration = durationIterator.next();
                if (nextDuration.isContinousWith(previousDuration)
                        || nextDuration.overlapsWith(previousDuration)) {
                    continue;
                } else {
                    missingDuration = new CalendarDuration(
                            previousDuration.getTillDate().nextDay(), nextDuration.fromDate
                                    .previousDay());
                    return missingDuration;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.fromDate == null) ? 0 : this.fromDate.hashCode());
        result = PRIME * result + ((this.tillDate == null) ? 0 : this.tillDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalendarDuration other = (CalendarDuration) obj;
        if ((this.fromDate == null) || (this.tillDate == null)) {
            return false;
        }
        return this.fromDate.equals(other.fromDate) && this.tillDate.equals(other.tillDate);
    }

}

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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("serial")
public class DurationOverlapException extends Exception {
    private CalendarDuration offendingDuration;
    private SortedSet<CalendarDuration> offendedDurations = new TreeSet<CalendarDuration>();

    public DurationOverlapException(CalendarDuration duration,
            SortedSet<CalendarDuration> overlappedDurations) {
        super();
        this.offendingDuration = duration;
        this.offendedDurations.addAll(overlappedDurations);
    }

    public DurationOverlapException(CalendarDuration duration, CalendarDuration overlappedDuration) {
        super();
        this.offendingDuration = duration;
        this.offendedDurations.add(overlappedDuration);
    }

    public CalendarDuration getOffendingDuration() {
        return this.offendingDuration;
    }

    public void setOffendingDuration(CalendarDuration duration) {
        this.offendingDuration = duration;
    }

    public SortedSet<CalendarDuration> getOffendedDurations() {
        return this.offendedDurations;
    }

    public void setOffendedDurations(SortedSet<CalendarDuration> overlappedDurations) {
        this.offendedDurations = overlappedDurations;
    }

}

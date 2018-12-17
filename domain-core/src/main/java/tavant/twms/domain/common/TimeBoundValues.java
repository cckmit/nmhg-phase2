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

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
@MappedSuperclass
@Embeddable
public abstract class TimeBoundValues<V, TV extends TimeBoundValue<V>> {
    public void set(V value, CalendarDuration aDuration) throws DurationOverlapException {
        TV newValue = newTimeBoundValue(value, aDuration);
        newValue.setParent(this);
        SortedSet<CalendarDuration> durationsSpanned = existingDurationsSpannedBy(aDuration);
        if (durationsSpanned.isEmpty()) {
            getEntries().add(newValue);
        } else if ((durationsSpanned.size() == 1)
                && durationsSpanned.iterator().next().equals(aDuration)) {
            //This is a re-set call for an existing duration, just update the value.
            getEntryAsOf(aDuration.getFromDate()).setValue(value);
        } else {
            throw new DurationOverlapException(aDuration, durationsSpanned);
        }
    }
    
    public void setModifier(V value, CalendarDuration aDuration,
			boolean isFlatRate) throws DurationOverlapException {
		TV newValue = newTimeBoundValueModifier(value, aDuration, isFlatRate);
		newValue.setParent(this);
		SortedSet<CalendarDuration> durationsSpanned = existingDurationsSpannedBy(aDuration);
		if (durationsSpanned.isEmpty()) {
			getEntries().add(newValue);
		} else if ((durationsSpanned.size() == 1)
				&& durationsSpanned.iterator().next().equals(aDuration)) {
			// This is a re-set call for an existing duration, just update the
			// value.
			getEntryAsOf(aDuration.getFromDate()).setValue(value);
		} else {
			throw new DurationOverlapException(aDuration, durationsSpanned);
		}
	}
    
    public abstract TV newTimeBoundValueModifier(V value,
			CalendarDuration forDuration, Boolean isFlatRate);

    //Don't see this being used anywhere
    public void removeAllEntries() {
        getEntries().clear();
    }

    //Don't see this being used anywhere
    public void removeEntriesFor(CalendarDuration aDuration) {
        SortedSet<CalendarDuration> spannedDurations = existingDurationsSpannedBy(aDuration);
        for (CalendarDuration aSpannedDuration : spannedDurations) {
            CalendarDate fromDate = aSpannedDuration.getFromDate();
            TV anEntry = getEntryAsOf(fromDate);
            getEntries().remove(anEntry);
        }
    }

    /**
     * Get all the durations that are included within a given duration.
     * 
     * @param aDuration
     *                A given duration.
     * @return A non-empty orderd set of durations that are included within the given duration.
     */
    public SortedSet<CalendarDuration> existingDurationsSpannedBy(CalendarDuration aDuration) {
        SortedSet<TV> values = getEntries();
        SortedSet<CalendarDuration> spannedDurations = new TreeSet<CalendarDuration>();
        for (TV aTimeBoundEntry : values) {
            CalendarDuration probablySpannedDuration = aTimeBoundEntry.getDuration();
            if (aDuration.includes(probablySpannedDuration) && aTimeBoundEntry.getD().isActive()) {
                spannedDurations.add(probablySpannedDuration);
            }
        }

        if (hasAValueOn(aDuration.getFromDate())) {
            spannedDurations.add(getEntryAsOf(aDuration.getFromDate()).getDuration());
        }
        if (hasAValueOn(aDuration.getTillDate())) {
            spannedDurations.add(getEntryAsOf(aDuration.getTillDate()).getDuration());
        }

        return spannedDurations;
    }

    public boolean hasAValueOn(CalendarDate aDate) {
        return getEntryAsOf(aDate) != null;
    }

    public TV getEntryAsOf(CalendarDate aDate) {
        SortedSet<TV> timeBoundValues = getEntries();
        for (TV aTimeBoundEntry : timeBoundValues) {
            if (aTimeBoundEntry.isEffectiveOn(aDate) && aTimeBoundEntry.getD().isActive()) {
                return aTimeBoundEntry;
            }
        }
        return null;
    }

    public V getValueAsOf(CalendarDate aDate) {
        TV entryAsOf = getEntryAsOf(aDate);
        return entryAsOf == null ? null : entryAsOf.getValue();
    }

    public abstract SortedSet<TV> getEntries();

    public abstract TV newTimeBoundValue(V value, CalendarDuration forDuration);

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (TV eachTBV : getEntries()) {
            buf.append(eachTBV).append('\n');
        }
        return buf.toString();
    }

}

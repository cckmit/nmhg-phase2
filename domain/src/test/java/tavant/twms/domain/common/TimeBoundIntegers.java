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
public class TimeBoundIntegers extends TimeBoundValues<Integer,TimeBoundInteger> {
    private SortedSet<TimeBoundInteger> entries = new TreeSet<TimeBoundInteger>();
    
    @Override
    public SortedSet<TimeBoundInteger> getEntries() {
        return entries;
    }

    @Override
    public TimeBoundInteger newTimeBoundValue(Integer value, CalendarDuration forDuration) {
        return new TimeBoundInteger(value,forDuration);
    }
}

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
package typeconverters;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import tavant.twms.web.typeconverters.CalendarDateConverter;

import com.domainlanguage.time.CalendarDate;

public class CalendarDateConverterTest extends TestCase {

    CalendarDateConverter calendarDateConverter = new CalendarDateConverter();

    Map ctx = Collections.EMPTY_MAP;

    /**
     * Incompatible objects should be converted to null.
     * 
     */
    public void testConvertValueWithIncompatibleType() {

        Object convertedValue = calendarDateConverter.convertValue(ctx, new Integer(1), Boolean.class);

        assertNull(convertedValue);
    }

    /**
     * Null CalendarDate objects should be left untouched.
     * 
     */
    public void testConvertValueWithNullCalendarDate() {

        Object convertedValue = calendarDateConverter.convertValue(ctx, null, String.class);

        assertNull(convertedValue);
    }

    /**
     * Non null CalendarDate objects should be converted to 
     * formatted date string.
     * 
     */
    public void testConvertValueWithNonNullCalendarDate() {

        CalendarDate calendarDate = CalendarDate.date(2006, 9, 1);

        Object convertedValue = calendarDateConverter.convertValue(ctx, calendarDate, String.class);

        assertNotNull(convertedValue);
        assertTrue(convertedValue instanceof String);

        String formattedDate = (String) convertedValue;
        assertEquals("09/01/2006", formattedDate);
    }

    /**
     * Null String array should be converted to null.
     * 
     */
    public void testConvertValueWithNullDateString() {

        Object convertedValue = calendarDateConverter.convertValue(ctx, new String[] { null },
                CalendarDate.class);

        assertNull(convertedValue);
    }

    /**
     * Non null Date strings should be converted to CalendarDate objects.
     * 
     */
    public void testConvertValueWithNonNullDateString() {

        CalendarDate calendarDate = CalendarDate.date(2006, 9, 1);
        String dateString = "09/01/2006";

        Object convertedValue = calendarDateConverter.convertValue(ctx, new String[] { dateString },
                CalendarDate.class);

        assertNotNull(convertedValue);
        assertTrue(convertedValue instanceof CalendarDate);

        CalendarDate converterdCalendarDate = (CalendarDate) convertedValue;
        assertEquals(calendarDate, converterdCalendarDate);
    }
}

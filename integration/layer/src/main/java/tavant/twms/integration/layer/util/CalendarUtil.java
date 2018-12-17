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

package tavant.twms.integration.layer.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCalendar;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

public class CalendarUtil {

    private static Logger logger = Logger.getLogger(CalendarUtil.class
            .getName());

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private static final String DATE__TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    private static final String DATE_FORMAT_XLS="MM/dd/yyyy";

    public static CalendarDate convertToCalendarDate(Calendar calendar) {
        return (calendar == null) ? null : TimePoint.from(calendar)
                .calendarDate(TimeZone.getDefault());
    }

    public static Calendar convertToJavaCalendar(CalendarDate domainLanguageDate) {
        Calendar javaCalendarDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = sdf.parse(domainLanguageDate.toString(DATE_FORMAT));
            javaCalendarDate.setTime(date);
        } catch (ParseException e) {
            logger
                    .error(
                            "Failed to transform DSL calendar date to java calendar date.",
                            e);
        }
        return javaCalendarDate;
    }
    
    public static CalendarDate convertToCalendarDate(Date date) {
    	if(date != null) {
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(date);
    		return convertToCalendarDate(calendar);
    	}
    	return null;
	}

    public static Calendar convertDateToCalendar(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        return null;
    }
    
    public static CalendarDate convertToCalendarDate(String dataString) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			Date date = sdf.parse(dataString);
			calendar.setTime(date);
		} catch (ParseException e) {
			logger
					.error(
							"Failed to transform String date to java calendar date.",
							e);
		}
		return (calendar == null) ? null : TimePoint.from(calendar)
				.calendarDate(TimeZone.getDefault());

	}
    
    public static CalendarDate convertDateFromXLSToCalendarDate(String dataString) {
  		Calendar calendar = Calendar.getInstance();
  		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_XLS);
  		try {
  			Date date = sdf.parse(dataString);
  			calendar.setTime(date);
  		} catch (ParseException e) {
  			logger
  					.error(
  							"Failed to transform String date to java calendar date.",
  							e);
  		}
  		return (calendar == null) ? null : TimePoint.from(calendar)
  				.calendarDate(TimeZone.getDefault());

  	}
    // date format - dd-MMM-yyyy 
    public static CalendarDate convertStringToCalendarDate(String dataString){
    
    	Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			Date date = sdf.parse(dataString);
			calendar.setTime(date);
		} catch (ParseException e) {
			logger
					.error(
							"Failed to transform String date to java calendar date.",
							e);
		}
	
		return (calendar == null) ? null : TimePoint.from(calendar)
				.calendarDate(TimeZone.getDefault());
	
    }
    
    public static Date convertToJavaDate(CalendarDate domainLanguageDate){
    	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    	Date date;
		try {
			date = sdf.parse(domainLanguageDate.toString(DATE_FORMAT));
			CCDate ccDate = new CCDate(date.getTime());
			return ccDate;
		} catch (ParseException e) {
			return new CCDate();
		}
    	
	}
    
    public static XmlCalendar convertToXMLCalendar(CalendarDate domainLanguageDate){    	
		try {
			return new XmlCalendar(domainLanguageDate.toString(DATE_FORMAT));
		} catch (Exception e) {
			return new XmlCalendar(new CCDate());
		}
    	
	}
    
    public static Date incrementDate(final Date endDate, int days) {
        Calendar endDatecal = Calendar.getInstance();
        endDatecal.setTime(endDate);
        endDatecal.add(Calendar.DATE, days);
        Date incEndDate = endDatecal.getTime();
        return incEndDate;
    }
    
    public static Calendar convertToJavaCalendarInGMT(CalendarDate domainLanguageDate) {
        Calendar javaCalendarDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = sdf.parse(domainLanguageDate.toString(DATE_FORMAT));
            javaCalendarDate.setTime(date);
        } catch (ParseException e) {
            logger
                    .error(
                            "Failed to transform DSL calendar date to java calendar date.",
                            e);
        }
        javaCalendarDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        return javaCalendarDate;
    }
    
	public static String convertToDateToString(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date).toString();
	}
	public static String convertToDateTimeToString(Date date) {
		return new SimpleDateFormat(DATE__TIME_FORMAT).format(date).toString();
	}
                                  
}

package tavant.twms.domain.common;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.domainlanguage.time.CalendarDate;


public class CalendarIterator {
	public static final int LAST = -1;
	public static final int NEXT =1;
	private static final int START_OF_MONTH = 1;
	private static final int START_OF_WEEK = Calendar.getInstance(TimeZone.getDefault()).getFirstDayOfWeek();
	
	/**
	 * returns start of previous or next months based on direction (last/next) and 
	 * length (for e.g. 2 months prior to current month, here 2 is the length).
	 * @return
	 */
	public static CalendarDate getStartOfMonth(int direction, int length){
		Calendar calendar = GregorianCalendar.getInstance(); 
		calendar.add(Calendar.MONTH, direction*length);
		calendar.set(Calendar.DAY_OF_MONTH, START_OF_MONTH);
		return CalendarDate.date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, 
				calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * returns start of previous or next weeks based on direction (last/next) and 
	 * length (for e.g. 2 weeks prior to current week, here 2 is the length).
	 * @return
	 */
	public static CalendarDate getStartOfWeek(int direction, int length){
		Calendar calendar = GregorianCalendar.getInstance(); 
		calendar.add(Calendar.WEEK_OF_MONTH, direction*length);
		calendar.set(Calendar.DAY_OF_WEEK, START_OF_WEEK);
		return CalendarDate.date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, 
				calendar.get(Calendar.DAY_OF_MONTH));
	}

}

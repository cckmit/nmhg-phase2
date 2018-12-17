/**
 * 
 */
package tavant.twms.web.claim.upload;

import com.domainlanguage.time.CalendarDate;

public class DateHolder {
    private CalendarDate calendarDate;

    public CalendarDate getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(CalendarDate calendarDate) {
        this.calendarDate = calendarDate;
    }
    
    public String toString() {
        return calendarDate!=null ? calendarDate.toString(): "";
    }
}
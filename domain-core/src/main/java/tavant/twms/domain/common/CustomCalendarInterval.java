package tavant.twms.domain.common;

import tavant.twms.domain.rules.DateType;

import com.domainlanguage.time.CalendarInterval;

public class CustomCalendarInterval {
	
	/**
	 * returns CalendarInterval for previous/next calendar months/weeks 
	 * 
	 * @param durationLength
	 * @param durationType
	 * @param direction
	 * @return
	 */
	public static CalendarInterval getCalendarInterval(int durationLength, int durationType, int direction){
        switch (durationType) {
        //TODO either use constants instead of literals with case or switch to if/then        
        case 1:
        	return CalendarInterval.startingFrom(CalendarIterator.getStartOfWeek(direction, 
        			(direction==CalendarIterator.LAST) ? durationLength : 1), 
            		DateType.DurationType.getDurationForTypeAndLength(durationLength, durationType));
        case 2:
        	return CalendarInterval.startingFrom(CalendarIterator.getStartOfMonth(direction, 
        			(direction==CalendarIterator.LAST) ? durationLength : 1), 
            		DateType.DurationType.getDurationForTypeAndLength(durationLength, durationType));        	
        default:
            throw new IllegalArgumentException(
                    "Unknown duration type : " + durationType);
        }
	}
}

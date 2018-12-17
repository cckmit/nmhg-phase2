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
package tavant.twms.web.typeconverters;

import com.domainlanguage.time.CalendarDate;
import java.util.Map;
import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.dateutil.TWMSDateFormatUtil;

public class CalendarDateConverter extends DefaultTypeConverter {

    private Logger logger = Logger.getLogger(CalendarDateConverter.class);

    

    @Override
    public Object convertValue(Map ctx, Object obj, Class toType) {
    	
    	String datePattern = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
    	    	
        Object returnedValue = null;

        if (toType == CalendarDate.class) {
            String date = null;
            if (obj instanceof String[]) {
                date = ((String[]) obj)[0];
            } else if (obj instanceof String) {
                date = (String) obj;
            } else {
                date = obj.toString();
            }

            if(StringUtils.hasText(date)) {            	
                returnedValue = CalendarDate.from(date, datePattern);

                if (logger.isDebugEnabled()) {
                    logger.debug("Converted date string [" + date
                            + "] to CalendarDate [" + returnedValue + "]");
                }
            }
        } else if (toType == String.class) {

            CalendarDate calendarDate = (CalendarDate) obj;

            if(calendarDate != null) {
                returnedValue = calendarDate.toString(datePattern);

                if (logger.isDebugEnabled()) {
                    logger.debug("Converted CalendarDate [" + calendarDate
                            + "] to  date string [" + returnedValue + "]");
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring unsupported object type : ["
                        + toType + "]");
            }
        }

        return returnedValue;
    }

}
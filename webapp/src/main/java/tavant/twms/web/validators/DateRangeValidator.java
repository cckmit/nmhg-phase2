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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jan 17, 2007
 * Time: 8:00:12 PM
 */

package tavant.twms.web.validators;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;
import org.apache.log4j.Logger;

public class DateRangeValidator extends ExpressionValidator {

    private String startDateField;
    private String endDateField;

    private static final Logger logger =
            Logger.getLogger(DateRangeValidator.class);

    public String getStartDateField() {
        return startDateField;
    }

    public void setStartDateField(String startDateField) {
        this.startDateField = startDateField;
    }

    public String getEndDateField() {
        return endDateField;
    }

    public void setEndDateField(String endDateField) {
        this.endDateField = endDateField;
    }

    public void validate(Object object) throws ValidationException {

        // if the object itself is null - don't do comparison.
        if(object == null) {
            return;
        }

        Object startDateObj = getFieldValue(getStartDateField(), object);
        Object endDateObj = getFieldValue(getEndDateField(), object);

        // if either of the dates are null or not of type CalendarDate - don't do comparison.
        if (!(startDateObj instanceof CalendarDate && endDateObj instanceof CalendarDate)) {
            return;
        }

        CalendarDate startDate = (CalendarDate) startDateObj;
        CalendarDate endDate = (CalendarDate) endDateObj;

        // We can't use isBefore(...) since that would reject start date and
        // end dates falling on the same day.
        if (startDate.compareTo(endDate) > 0) {

            if (logger.isDebugEnabled()) {
                logger.debug("Date Range Validation failed for Start Date [" +
                        startDate + "] and End Date [" + endDate +
                        "] of object [" + object + "].");
            }

            addActionError(object);
        }
    }
}
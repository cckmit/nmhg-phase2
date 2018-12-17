package tavant.twms.integration.web.validators;

import java.util.Date;

import org.apache.log4j.Logger;


import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

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
        if (!(startDateObj instanceof Date && endDateObj instanceof Date)) {
            return;
        }

        Date startDate = (Date) startDateObj;
        Date endDate = (Date) endDateObj;

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

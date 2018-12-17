package tavant.twms.domain.campaign.validation;

import junit.framework.TestCase;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import tavant.twms.domain.common.CalendarDuration;

import com.domainlanguage.time.CalendarDate;

public class ExpressionValidatorTest extends TestCase {

    public void testExpressionValidatorForCalendarDate() {
        ValueObject valueObject = new ValueObject();
        valueObject.duration.setFromDate(CalendarDate.from("05/01/2007", "MM/dd/yyyy"));
        valueObject.duration.setTillDate(CalendarDate.from("05/02/2007", "MM/dd/yyyy"));
        ExpressionValidator validator = new ExpressionValidator();
        validator.setCondition("isValidDuration(duration.fromDate, duration.tillDate)");
        boolean isValid = validator.isValid(valueObject);
        assertTrue(isValid);
    }

    public void testExpressionValidatorForNullDuration() {
        ValueObject valueObject = new ValueObject();
        ExpressionValidator validator = new ExpressionValidator();
        validator.setCondition("isValidDuration(duration.fromDate, duration.tillDate)");
        boolean isValid = validator.isValid(valueObject);
        assertTrue(isValid);
    }
    
    public void testExpressionValidator() {
        ValueObject valueObject = new ValueObject();
        ExpressionValidator validator = new ExpressionValidator();
        valueObject.duration.setFromDate(CalendarDate.from("05/01/2007", "MM/dd/yyyy"));
        validator.setCondition("isValidDuration(duration.fromDate, duration.tillDate)");
        boolean isValid = validator.isValid(valueObject);
        assertFalse(isValid);
    }

    public void testOGNLAnnotation() {
        ValueObject valueObject = new ValueObject();
        valueObject.duration.setFromDate(CalendarDate.from("05/01/2007", "MM/dd/yyyy"));
        valueObject.duration.setTillDate(CalendarDate.from("05/02/2007", "MM/dd/yyyy"));
        valueObject.min = 1;
        
        InvalidValue[] values = validate(valueObject);
        assertEquals(values.length, 1);
        assertEquals(values[0].getMessage(), "Input does not match with criteria isValidRange()");
    }

    @Expressions( { @Expression("isValidRange()"), @Expression("duration.fromDate.isBefore(duration.tillDate)") })
    class ValueObject {
        Integer min;
        Integer max;
        CalendarDuration duration = new CalendarDuration();
        
        public CalendarDuration getDuration() {
            return duration;
        }

        public Integer getMax() {
            return max;
        }

        public Integer getMin() {
            return min;
        }
        public boolean isValidDuration(CalendarDate startDate, CalendarDate endDate) {
            if (startDate == null && endDate == null) {
                return true;
            } else if (startDate != null && endDate != null) {
                return !startDate.isAfter(endDate);
            } else {
                return false;
            }
        }
        public boolean isValidRange() {
            if (min == null && max == null) {
                return true;
            } else if (min != null && max != null) {
                return max >= min;
            } else {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static InvalidValue[] validate(ValueObject valueObject) {
        ClassValidator validator = new ClassValidator(ValueObject.class);
        return validator.getInvalidValues(valueObject);
    }
}
package tavant.twms.web.admin.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.common.CalendarDuration;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.validator.ValidationException;

public class ManageTravelRateTest extends XWorkTestCase {

    private final CalendarDate _START_DATE = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
    private final CalendarDate _END_DATE = CalendarDate.from("11/10/2007", "MM/dd/yyyy");

    private final Money _1069USD = Money.dollars(10.69D);

    @SuppressWarnings("unchecked")
    public void testValidationForConfigurations() {
        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(Collections.EMPTY_LIST);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertTrue(def.hasActionErrors());
        List<String> ae = (List<String>) def.getActionErrors();
        assertEquals(ae.size(), 1);
        assertEquals(ae.get(0), "error.manageRates.moreRateConfigRequired");
    }

    @SuppressWarnings("unchecked")
    public void testValidationForDuration() {
        List<TravelRate> travelRates = new ArrayList<TravelRate>();
        TravelRate travelRate = new TravelRate();
        TravelRateValues newValue = new TravelRateValues();
        newValue.setDistanceRate(_1069USD);
        newValue.setHourlyRate(_1069USD);
        newValue.setTripRate(_1069USD);
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(travelRates);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertTrue(def.hasActionErrors());
        List<String> ae = (List<String>) def.getActionErrors();
        assertEquals(ae.size(), 1);
        assertEquals(ae.get(0), "error.manageRates.durationNotSpecified");
    }

    @SuppressWarnings("unchecked")
    public void testValidationForDates() {
        List<TravelRate> travelRates = new ArrayList<TravelRate>();
        TravelRate travelRate = new TravelRate();
        travelRate.setDuration(new CalendarDuration());
        TravelRateValues newValue = new TravelRateValues();
        newValue.setDistanceRate(_1069USD);
        newValue.setHourlyRate(_1069USD);
        newValue.setTripRate(_1069USD);
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(travelRates);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertTrue(def.hasActionErrors());
        List<String> ae = (List<String>) def.getActionErrors();
        assertEquals(ae.size(), 2);
        assertEquals(ae.get(0), "error.manageRates.startDateNotSpecified");
        assertEquals(ae.get(1), "error.manageRates.endDateNotSpecified");
    }

    @SuppressWarnings("unchecked")
    public void testValidationForStartDate() {
        List<TravelRate> travelRates = new ArrayList<TravelRate>();
        TravelRate travelRate = new TravelRate();
        CalendarDuration duration = new CalendarDuration();
        duration.setFromDate(null);
        duration.setTillDate(_END_DATE);
        travelRate.setDuration(duration);
        TravelRateValues newValue = new TravelRateValues();
        newValue.setDistanceRate(_1069USD);
        newValue.setHourlyRate(_1069USD);
        newValue.setTripRate(_1069USD);
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        travelRate = new TravelRate();
        travelRate.setDuration(new CalendarDuration(_START_DATE, _END_DATE));
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(travelRates);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertTrue(def.hasActionErrors());
        List<String> ae = (List<String>) def.getActionErrors();
        assertEquals(ae.size(), 1);
        assertEquals(ae.get(0), "error.manageRates.startDateNotSpecified");
    }

    @SuppressWarnings("unchecked")
    public void testValidation() {
        List<TravelRate> travelRates = new ArrayList<TravelRate>();

        TravelRateValues newValue = new TravelRateValues();
        newValue.setDistanceRate(_1069USD);
        newValue.setHourlyRate(_1069USD);
        newValue.setTripRate(_1069USD);

        TravelRate travelRate = new TravelRate();
        travelRate.setDuration(new CalendarDuration(_START_DATE, _END_DATE));
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(travelRates);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertFalse(def.hasActionErrors());
    }

    @SuppressWarnings("unchecked")
    public void validationForRates() {
        List<TravelRate> travelRates = new ArrayList<TravelRate>();

        TravelRateValues newValue = new TravelRateValues();
        newValue.setDistanceRate(_1069USD);
        newValue.setHourlyRate(Money.dollars(-10.00D));
        newValue.setTripRate(Money.dollars(BigDecimal.ZERO));

        TravelRate travelRate = new TravelRate();
        travelRate.setDuration(new CalendarDuration(_START_DATE, _END_DATE));
        travelRate.setValue(newValue);
        travelRates.add(travelRate);

        DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
        ManageTravelRate def = new ManageTravelRate();
        def.setRates(travelRates);
        try {
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        assertTrue(def.hasActionErrors());
        List<String> ae = (List<String>) def.getActionErrors();
    }
}
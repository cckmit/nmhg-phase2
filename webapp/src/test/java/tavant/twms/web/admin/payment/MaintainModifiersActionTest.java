/**
 * 
 */
package tavant.twms.web.admin.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tavant.twms.domain.claim.payment.definition.modifiers.CriteriaBasedValue;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifier;
import tavant.twms.domain.common.CalendarDuration;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.validator.ValidationException;

/**
 * @author Kiran.Kollipara
 *
 */
public class MaintainModifiersActionTest extends XWorkTestCase {
    private final CalendarDate _START_DATE = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
    private final CalendarDate _END_DATE = CalendarDate.from("11/10/2007", "MM/dd/yyyy");

    public void testValidationForConfigurations() {
        validate(Collections.EMPTY_LIST, new String[] {"error.manageRates.moreConfigRequired"});
    }

    public void testValidationForDuration() {
        List<CriteriaBasedValue> travelRates = new ArrayList<CriteriaBasedValue>();
        CriteriaBasedValue newValue = new CriteriaBasedValue();
        newValue.setValue(10.0D);
        newValue.setParent(new PaymentModifier());
        travelRates.add(newValue);

        validate(travelRates, new String[] {"error.manageRates.durationNotSpecified"});
    }

    public void testValidationForDates() {
        List<CriteriaBasedValue> travelRates = new ArrayList<CriteriaBasedValue>();
        CriteriaBasedValue newValue = new CriteriaBasedValue();
        newValue.setParent(new PaymentModifier());
        newValue.setDuration(new CalendarDuration());
        newValue.setValue(10.0D);
        travelRates.add(newValue);

        validate(travelRates, new String[]{
                "error.manageRates.startDateNotSpecified",
                "error.manageRates.endDateNotSpecified"});
    }

    public void testValidationForPercentage() {
        List<CriteriaBasedValue> travelRates = new ArrayList<CriteriaBasedValue>();
        CriteriaBasedValue newValue = new CriteriaBasedValue();
        newValue.setParent(new PaymentModifier());
        newValue.setDuration(new CalendarDuration(_START_DATE, _END_DATE));
        travelRates.add(newValue);

        // any value >= -100 is a valid percentage
        newValue.setValue(-100.0D);
        validate(travelRates, new String[0]);
        newValue.setValue(0.0D);
        validate(travelRates, new String[0]);
        newValue.setValue(100.0D);
        validate(travelRates, new String[0]);
        newValue.setValue(200.0D);
        validate(travelRates, new String[0]);
        newValue.setValue(-100.1D);
        validate(travelRates, new String[]{"error.manageRates.invalidPercentage"});
    }

    @SuppressWarnings("unchecked")
    private void validate(List<CriteriaBasedValue> travelRates, String[] actionErrors) {
        MaintainModifiersAction def = new MaintainModifiersAction();
        def.setEntries(travelRates);
        try {
            DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
            validator.validate(def, null);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        if (actionErrors.length > 0) {
            assertTrue(def.hasActionErrors());
            List<String> ae = (List<String>) def.getActionErrors();
            assertEquals(ae.size(), actionErrors.length);
            for (int i = 0; i < actionErrors.length; i++) {
                assertEquals(ae.get(i), actionErrors[i]);
            }
        } else {
            assertFalse(def.hasActionErrors());
        }
    }
}

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
 * Time: 9:18:28 PM
 */

package tavant.twms.web.validators;

import java.util.Date;
import java.util.TimeZone;

import org.jmock.Mock;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public class DateRangeValidatorTest extends AbstractValidatorTest {

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    Mock plainVanillaInterfaceMock = mock(PlainVanillaInterface.class);
    DateRangeValidator fixture;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fixture = (DateRangeValidator) getValidator();
        fixture.setStartDateField("startDate");
        fixture.setEndDateField("endDate");
    }

    protected ValidatorSupport setupValidator() {
        return new DateRangeValidator();
    }

    private interface PlainVanillaInterface {
        CalendarDate getStartDate();
        CalendarDate getEndDate();
    }

    public void testValidateForNullObject() throws ValidationException {
        plainVanillaInterfaceMock.expects(never()).method("getStartDate");
        plainVanillaInterfaceMock.expects(never()).method("getEndDate");

        fixture.validate(null);
        assertNoActionErrors();
    }

    public void testValidateForNullStartDateAndEndDate() throws ValidationException {
        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(null));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(null));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoActionErrors();
    }

    public void testValidateForNonNullStartDateAndNullEndDate() throws ValidationException {
        CalendarDate today = Clock.today();
        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(today));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(null));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoActionErrors();
    }

    public void testValidateForNullStartDateAndNonNullEndDate() throws ValidationException {
        CalendarDate today = Clock.today();
        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(null));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(today));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoActionErrors();
    }

    public void testValidateForNonCalendarDateStartDateAndEndDate() throws ValidationException {
        Date today = new Date();
        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(today));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoActionErrors();
    }

     public void testValidateForStartDateBeforeEndDate() throws ValidationException {
        CalendarDate startDate = Clock.today();
        CalendarDate endDate = Clock.today().plusDays(1);

        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(startDate));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(endDate));

        fixture.validate(plainVanillaInterfaceMock.proxy());
         assertNoActionErrors();
     }

     public void testValidateForSameStartDateAndEndDate() throws ValidationException {
        CalendarDate startDate = Clock.today();
        CalendarDate endDate = Clock.today();

        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(startDate));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(endDate));

        fixture.validate(plainVanillaInterfaceMock.proxy());
         assertNoActionErrors();
     }

     public void testValidateForStartDateAfterEndDate() throws ValidationException {
        CalendarDate startDate = Clock.today().plusDays(1);
        CalendarDate endDate = Clock.today();         

        plainVanillaInterfaceMock.expects(once()).method("getStartDate").will(returnValue(startDate));
        plainVanillaInterfaceMock.expects(once()).method("getEndDate").will(returnValue(endDate));

        fixture.validate(plainVanillaInterfaceMock.proxy());
         assertActionErrorCount(1);
     }
}
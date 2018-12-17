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
 * Time: 10:22:19 PM
 */

package tavant.twms.web.validators;

import org.jmock.Mock;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public class LongRangeFieldValidatorTest extends AbstractValidatorTest {

    LongRangeFieldValidator fixture;
    Mock plainVanillaInterfaceMock = mock(PlainVanillaInterface.class);

    private interface PlainVanillaInterface {
        Long getSomeLongValue();
    }

    protected ValidatorSupport setupValidator() {
        return new LongRangeFieldValidator();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fixture = (LongRangeFieldValidator) getValidator();
        fixture.setFieldName("someLongValue");
    }

    public void testValidateForNullObject() throws ValidationException {
        plainVanillaInterfaceMock.expects(never()).method("getSomeLongValue");

        fixture.validate(null);
        assertNoFieldErrors();
    }

    public void testValidateForValueLessThanMin() throws ValidationException {
        fixture.setMin(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(-1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueEqualsMin() throws ValidationException {
        fixture.setMin(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(0L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueGreaterThanMin() throws ValidationException {
        fixture.setMin(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueLessThanMax() throws ValidationException {
        fixture.setMax(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(-1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueEqualsMax() throws ValidationException {
        fixture.setMax(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(0L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueGreaterThanMax() throws ValidationException {
        fixture.setMax(0L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueToLeftOfMinMaxRange() throws ValidationException {
        fixture.setMin(-1L);
        fixture.setMax(1L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(-10L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueSameAsMinOfMinMaxRange() throws ValidationException {
        fixture.setMin(-1L);
        fixture.setMax(1L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(-1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueBetweenMinMaxRange() throws ValidationException {
        fixture.setMin(-1L);
        fixture.setMax(1L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(0L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueSameAsMaxOfMinMaxRange() throws ValidationException {
        fixture.setMin(-1L);
        fixture.setMax(1L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(1L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueToRightOfMinMaxRange() throws ValidationException {
        fixture.setMin(-1L);
        fixture.setMax(1L);

        plainVanillaInterfaceMock.expects(once()).method("getSomeLongValue").will(returnValue(10L));

        fixture.validate(plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

}

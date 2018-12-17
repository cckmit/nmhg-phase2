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

import java.math.BigDecimal;

import org.jmock.Mock;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public class BigDecimalRangeFieldValidatorTest extends AbstractValidatorTest {

    BigDecimalRangeFieldValidator fixture;
    Mock plainVanillaInterfaceMock = mock(PlainVanillaInterface.class);

    private interface PlainVanillaInterface {
        BigDecimal getSomeBigDecimalValue();
    }

    @Override
	protected ValidatorSupport setupValidator() {
        return new BigDecimalRangeFieldValidator();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.fixture = (BigDecimalRangeFieldValidator) getValidator();
        this.fixture.setFieldName("someBigDecimalValue");
    }

    public void testValidateForNullObject() throws ValidationException {
        this.plainVanillaInterfaceMock.expects(never()).method("getSomeBigDecimalValue");

        this.fixture.validate(null);
        assertNoFieldErrors();
    }

    public void testValidateForValueLessThanMin() throws ValidationException {
        this.fixture.setMin(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.valueOf(-1)));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueEqualsMin() throws ValidationException {
        this.fixture.setMin(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ZERO));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueGreaterThanMin() throws ValidationException {
        this.fixture.setMin(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ONE));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueLessThanMax() throws ValidationException {
        this.fixture.setMax(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.valueOf(-1)));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueEqualsMax() throws ValidationException {
        this.fixture.setMax(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ZERO));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueGreaterThanMax() throws ValidationException {
        this.fixture.setMax(BigDecimal.ZERO);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ONE));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueToLeftOfMinMaxRange() throws ValidationException {
        this.fixture.setMin(BigDecimal.valueOf(-1));
        this.fixture.setMax(BigDecimal.ONE);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.valueOf(-10)));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

    public void testValidateForValueSameAsMinOfMinMaxRange() throws ValidationException {
        this.fixture.setMin(BigDecimal.valueOf(-1));
        this.fixture.setMax(BigDecimal.ONE);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.valueOf(-1)));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueBetweenMinMaxRange() throws ValidationException {
        this.fixture.setMin(BigDecimal.valueOf(-1));
        this.fixture.setMax(BigDecimal.ONE);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ZERO));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueSameAsMaxOfMinMaxRange() throws ValidationException {
        this.fixture.setMin(BigDecimal.valueOf(-1));
        this.fixture.setMax(BigDecimal.ONE);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.ONE));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertNoFieldErrors();
    }

    public void testValidateForValueToRightOfMinMaxRange() throws ValidationException {
        this.fixture.setMin(BigDecimal.valueOf(-1));
        this.fixture.setMax(BigDecimal.ONE);

        this.plainVanillaInterfaceMock.expects(once()).method("getSomeBigDecimalValue").will(returnValue(BigDecimal.valueOf(10)));

        this.fixture.validate(this.plainVanillaInterfaceMock.proxy());
        assertFieldErrorCount(1);
    }

}

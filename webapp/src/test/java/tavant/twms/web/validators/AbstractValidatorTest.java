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
 * Time: 10:05:10 PM
 */

package tavant.twms.web.validators;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;
import org.jmock.MockObjectTestCase;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractValidatorTest extends MockObjectTestCase {

    private ValidatorSupport validator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator = setupValidator();
        setupValidatorContext();
    }

    public ValidatorSupport getValidator() {
        return validator;
    }

    protected abstract ValidatorSupport setupValidator();

    @Override
    protected void tearDown() throws Exception {
        validator = null;
        super.tearDown();
   }

    protected void setupValidatorContext() {
        ValidationAware validationAware = new ActionSupport();
        validationAware.setActionErrors(new ArrayList());
        validationAware.setFieldErrors(new HashMap());

        DelegatingValidatorContext validatorContext =
                new DelegatingValidatorContext(validationAware);

        validator.setValidatorContext(validatorContext);
    }

    protected void assertNoActionErrors() {
        assertEquals(0, validator.getValidatorContext().getActionErrors().size());
    }

    protected void assertActionErrorCount(int errorCount) {
        assertEquals(errorCount, validator.getValidatorContext().getActionErrors().size());
    }

    protected void assertNoFieldErrors() {
        assertEquals(0, validator.getValidatorContext().getFieldErrors().size());
    }

    protected void assertFieldErrorCount(int errorCount) {
        assertEquals(errorCount, validator.getValidatorContext().getFieldErrors().size());
    }

}
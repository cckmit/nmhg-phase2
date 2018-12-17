/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import junit.framework.TestCase;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;

/**
 * @author radhakrishnan.j
 * 
 */
public class SimpleValidatorTest extends TestCase {
    public void testValidation() {
        SimpleValidator simpleValidator = new SimpleValidator();
        Constant constant = new Constant("Junk1", Type.STRING);
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,
                "claim.type.type", BusinessObjectModelFactory.CLAIM_RULES);
        Equals equals1 = new Equals(domainSpecificVariable, constant);

        constant = new Constant("Junk", Type.STRING);
        constant.setLiteral(null);
        Equals equals2 = new Equals(domainSpecificVariable, constant);

        And and = new And(equals1, equals2);

        Or or = new Or(equals1, and);

        And and2 = new And(or, equals1);

        and2.accept(simpleValidator);

        assertTrue(simpleValidator.getValidationContext().hasErrors());

    }
}

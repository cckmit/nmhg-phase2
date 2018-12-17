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
package tavant.twms.domain.supplier.contract;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;

/**
 * 
 * @author kannan.ekanath
 * 
 */
public class RecoveryFormulaTest extends TestCase {

    public void testFormulaApplication() {
        RecoveryFormula formula = new RecoveryFormula();
        formula.setPercentageOfCost(80);
        formula.setAddedConstant(Money.dollars(20));
        formula.setMaximumAmount(Money.dollars(10000));
        formula.setMinimumAmount(Money.dollars(30));

        assertEquals(Money.dollars(81.02), formula.apply(Money.dollars(76.28), 1));
        assertEquals(Money.dollars(486.83), formula.apply(Money.dollars(583.54), 1));

        // touch the ceiling
        assertEquals(formula.getMaximumAmount(), formula.apply(Money.dollars(10000000), 1));
        // touch the floor
        assertEquals(formula.getMinimumAmount(), formula.apply(Money.dollars(10), 1));
    }

    public void testFormulaApplicationWithZero() {
        RecoveryFormula formula = new RecoveryFormula();
        formula.setPercentageOfCost(80);
        formula.setAddedConstant(Money.dollars(20));
        formula.setMaximumAmount(Money.dollars(10000));

        assertEquals(Money.dollars(20), formula.apply(Money.dollars(0), 1));
    }
}

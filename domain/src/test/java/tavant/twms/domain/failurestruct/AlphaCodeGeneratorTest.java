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
 * Date: Dec 22, 2006
 * Time: 2:20:35 PM
 */

package tavant.twms.domain.failurestruct;

import junit.framework.TestCase;

public class AlphaCodeGeneratorTest extends TestCase {

    AlphaCodeGenerator fixture =
            new AlphaCodeGenerator();

    public void testGenerateAssemblyCodeForCodeSequenceHavingSomeNonAlphaChars() {

        try {
            fixture.nextCode("A0V^");
            fail("generateAssemblyCode didn't throw exception for code sequence " +
                    "having some non-alpha characters!");
        } catch (IllegalArgumentException e) {
            // Success.
        }
    }

    public void testGenerateAssemblyCodeForStartingUpperCaseCodeSequenceAndOneIteration() {
        assertEquals("AB", fixture.nextCode("AA"));
    }

    public void testGenerateAssemblyCodeForStartingLowerCaseCodeSequenceAndOneIteration() {
        assertEquals("ab", fixture.nextCode("aa"));
    }

    public void testGenerateAssemblyCodeForUpperCaseCodeSequenceAndOneIterationWithCarryOver() {
        assertEquals("BA", fixture.nextCode("AZ"));
    }

    public void testGenerateAssemblyCodeForLowerCaseCodeSequenceAndOneIterationWithCarryOver() {
        assertEquals("ba", fixture.nextCode("az"));
    }

    public void testGenerateAssemblyCodeForEndingUpperCaseCodeSequenceAndOneIteration() {
        assertEquals("AA", fixture.nextCode("ZZ"));
    }

    public void testGenerateAssemblyCodeForEndingLowerCaseCodeSequenceAndOneIteration() {
        assertEquals("aa", fixture.nextCode("zz"));
    }

    public void testGenerateAssemblyCodeForUpperCaseCodeSequenceAndTwentySixIterations() {
        String lastGeneratedCode = "AA";

        for (int i = 1; i <= 26; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("BA", lastGeneratedCode);
    }

    public void testGenerateAssemblyCodeForLowerCaseCodeSequenceAndTwentySixIterations() {
        String lastGeneratedCode = "aa";

        for (int i = 1; i <= 26; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("ba", lastGeneratedCode);
    }

    public void testGenerateAssemblyCodeForUpperCaseCodeSequenceAndOneLessThanFullCircleIteration() {
        int numIterations = (int) Math.pow(26, 2) - 1; // 2 = "AA".length()
        String lastGeneratedCode = "AA";

        for (int i = 1; i <= numIterations; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("ZZ", lastGeneratedCode);
    }

    public void testGenerateAssemblyCodeForLowerCaseCodeSequenceAndOneLessThanFullCircleIteration() {
        int numIterations = (int) Math.pow(26, 2) - 1; // 2 = "AA".length()
        String lastGeneratedCode = "aa";

        for (int i = 1; i <= numIterations; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("zz", lastGeneratedCode);
    }

    public void testGenerateAssemblyCodeForUpperCaseCodeSequenceAndFullCircleIteration() {
        int numIterations = (int) Math.pow(26, 2); // 2 = "AA".length()
        String lastGeneratedCode = "AA";

        for (int i = 1; i <= numIterations; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("AA", lastGeneratedCode);
    }

    public void testGenerateAssemblyCodeForLowerCaseCodeSequenceAndFullCircleIteration() {
        int numIterations = (int) Math.pow(26, 2); // 2 = "AA".length()
        String lastGeneratedCode = "aa";

        for (int i = 1; i <= numIterations; i++) {
            lastGeneratedCode = fixture.nextCode(lastGeneratedCode);
        }

        assertEquals("aa", lastGeneratedCode);
    }

}
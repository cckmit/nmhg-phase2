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
 * Time: 12:44:09 PM
 */

package tavant.twms.domain.failurestruct;

import org.apache.commons.lang.StringUtils;

public class AlphaCodeGenerator implements CodeGenerator {

    public String nextCode(String currentAssemblyCode) {

    	if (StringUtils.isEmpty(currentAssemblyCode)) {
    		return "AA";
    	}
    	
        if(!StringUtils.isAlpha(currentAssemblyCode)) {
            throw new IllegalArgumentException("Assembly Code contains one or " +
                    "more non-alphabetic characters!");
        }

        char[] assemblyCodeAsArray = currentAssemblyCode.toCharArray();

        return incrementCharSequenceWithCarryOver(assemblyCodeAsArray);
    }

    private String incrementCharSequenceWithCarryOver(char[] charSequence) {
        return incrementCharSequenceWithCarryOver(charSequence,
                charSequence.length - 1);
    }

    private String incrementCharSequenceWithCarryOver(char[] charSequence, int i) {

        if ((charSequence[i] == 'Z') || (charSequence[i] == 'z')) {

            if(charSequence[i] == 'Z') {
                charSequence[i] = 'A';
            } else {
                charSequence[i] = 'a';
            }

            if (i > 0) {
                incrementCharSequenceWithCarryOver(charSequence, i - 1);
            }
        } else {
            charSequence[i] += 1;
        }

        // exit point is (i==0)
        return String.valueOf(charSequence);
    }
}

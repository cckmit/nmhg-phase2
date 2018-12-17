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
package tavant.twms.infra;

/**
 * @author radhakrishnan.j
 * 
 */
public class BitSetValueComputer {
    public BitSetValueComputer() {
        super();
    }

    public long compute(boolean[] bits) {
        long value = 0;
        long weight = 1;
        for (int i = bits.length - 1; i > -1; i--, weight = weight * 2) {
            value += bits[i] ? weight : 0L;
        }
        return value;
    }
}

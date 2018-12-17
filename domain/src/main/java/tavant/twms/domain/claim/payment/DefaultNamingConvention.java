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
package tavant.twms.domain.claim.payment;

import tavant.twms.domain.claim.payment.definition.Section;

/**
 * @author radhakrishnan.j
 *
 */
public class DefaultNamingConvention implements PaymentAmountNamingConvention {

    public String getAmountName(CostCategory costCategory) {
        return costCategory.getName();
    }

    public String getAmountName(Section section) {
        return section.getName();
    }

    public String getTotalAmountName(CostCategory costCategory) {
        return getTotalAmountName(getAmountName(costCategory));
    }

    public String getTotalAmountName(Section section) {
        return getTotalAmountName(getAmountName(section));
    }

    public String getTotalAmountName(String amountName) {
        StringBuffer buf = new StringBuffer();
        buf.append("Total ").append(amountName);
        return buf.toString();

    }
}

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
package tavant.twms.domain.claim.payment;

import java.util.Currency;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 */
public class PerDiemComputer extends AbstractPaymentComponentComputer {
	 public Money computeBaseAmount(PaymentContext ctx) {
	    Claim claim=ctx.getClaim();
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
        Money perDiemExpense = serviceDetail.getPerDiem();
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        if (perDiemExpense == null) {
            perDiemExpense = Money.valueOf(0, baseCurrency);
        }
        return perDiemExpense;
    }

}

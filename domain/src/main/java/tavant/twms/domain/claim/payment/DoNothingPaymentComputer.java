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

import java.util.Currency;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class DoNothingPaymentComputer extends AbstractPaymentComponentComputer {
    private static Logger logger = LogManager.getLogger(DoNothingPaymentComputer.class);

    public Money computeBaseAmount(PaymentContext paymentContext) {
        logger.error("This payment computation not defined.");
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        Money baseAmt = Money.valueOf(0.0D,baseCurrency);
        return baseAmt;
    }

}

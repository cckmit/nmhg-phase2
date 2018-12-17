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
package tavant.twms.domain.partreturn;

/**
 * @author vineeth.varghese
 *
 */
public class PayOnlyWithReturnEvaluator implements PaymentConditionEvaluator {

    /* (non-Javadoc)
     * @see tavant.twms.domain.partreturn.PaymentConditionEvaluator#isEligibleForPayment(tavant.twms.domain.partreturn.PartReturn)
     */
    public boolean isEligibleForPayment(PartReturn partReturn) {
        return partReturn.isPartReceived();
    }

	public boolean canMakePaymentDecision(PartReturn partReturn) {
		return partReturn.isPartReceived();
		
	}

}

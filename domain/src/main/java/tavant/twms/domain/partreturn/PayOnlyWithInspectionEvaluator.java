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
public class PayOnlyWithInspectionEvaluator implements
		PaymentConditionEvaluator {

	public boolean isEligibleForPayment(PartReturn partReturn) {
		boolean isEligibleForPayment = false;
        if(partReturn.getOemPartReplaced() != null && partReturn.getOemPartReplaced().isReturnDirectlyToSupplier()) {
            return (PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal() <= partReturn.getStatus().ordinal());
        }
		else if (partReturn.isPartReceived()
				&& partReturn.getInspectionResult() != null) {
			isEligibleForPayment = partReturn.getInspectionResult()
					.isAccepted();
		}

		return isEligibleForPayment;
	}

	public boolean canMakePaymentDecision(PartReturn partReturn) {
        if(partReturn.getOemPartReplaced() != null && partReturn.getOemPartReplaced().isReturnDirectlyToSupplier()) {
            return (PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal() <= partReturn.getStatus().ordinal());
        }
		else{
            return partReturn.getInspectionResult() != null;
        }
	}

}

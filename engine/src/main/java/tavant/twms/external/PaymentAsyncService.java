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
package tavant.twms.external;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.payment.CreditMemo;

@Transactional(readOnly = true)
/**
 * This class is intended to call the integration class
 * @author kannan.ekanath
 */
public interface PaymentAsyncService {
    
    public void startAsyncPayment(Claim claim);
    
    @Transactional(readOnly = false)
    public void startCreditMemoPayment(Claim claim);
    
    @Transactional(readOnly = false)
    public void syncCreditMemo(CreditMemo creditMemo);
    
    public void startSupplierRecoveryAsyncPayment(RecoveryClaim recoveryClaim);
}

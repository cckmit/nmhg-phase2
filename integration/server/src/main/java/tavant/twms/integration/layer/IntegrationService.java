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
 *
 */

/**
 * Created Mar 5, 2007 5:57:41 PM
 * @author kapil.pandit
 */

package tavant.twms.integration.layer;

import java.util.Map;

import com.nmhg.syncitalyqanotification.MTSyncItalyQANotificationSLMSDocument;

public interface IntegrationService {

    public abstract Object syncOEMXReference(String bod);

    public abstract Object syncExtWarrantyDebitNotification(String bod);

    public abstract Object syncWarrantyClaimCreditNotification(String bod);

    public abstract Object syncItem(String bod);
    
    Object syncBatchClaim(String bod);

    public abstract Object syncUser(String bod);

    public abstract Object syncInstallBase(String bod);

    public abstract Object syncFocClaimDetails(String claimsXml);

    public abstract Object syncCustomer(String bod);

    public abstract Object syncSupplierDebitNotification(String bod);

    public abstract Object syncExtWarrantyPurchaseNotification(String bod);

    public abstract Object syncCurrencyExchangeRate(String bod);

    public abstract Object fetchFocOrderDetails(String orderNo);

    public abstract Object postFocOrderDetails(String orderXml);

    public abstract Object updateClaimStatePostSubmission(String bod);

    public abstract Object syncFailureCode(String bod);
    
    public abstract Map<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp> getClaimsForItalyClaimNotification(java.sql.Timestamp lastSchedulerTime);
    
    public int updateCreditSubmissionDate(String claimNumber);
    
	public abstract void updateCreditForAcceptedAndDeniedClaims(String buName);

     Object syncTechnicians(String bod);
	
	public abstract Object syncUnitBooking(String bod);
	
	public abstract void saveAndsendUnitTransaction();

}

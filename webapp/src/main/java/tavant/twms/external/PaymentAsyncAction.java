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

import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;

import org.springframework.util.Assert;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimRepository;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.web.actions.TwmsActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.timeutil.Clock;

public class PaymentAsyncAction extends TwmsActionSupport {

    ClaimRepository claimRepository;

    RecoveryClaimRepository recoveryClaimRepository;

    PaymentAsyncService paymentAsyncService;

    ClaimService claimService;

    String claimType;

    RecoveryClaimService recoveryClaimService;

    Collection claimsPending;

    Collection supplierRecoveryPendingClaims;

    SyncTrackerService syncTrackerService;

    Long id;

    @Override
    public String execute() {
        this.claimsPending = this.claimRepository.findAllClaimsInState(ClaimState.PENDING_PAYMENT_RESPONSE);
        this.claimsPending.addAll(this.claimRepository.findAllClaimsInState(ClaimState.PENDING_PAYMENT_SUBMISSION));
        this.supplierRecoveryPendingClaims = this.recoveryClaimService
                .findClaimInState(RecoveryClaimState.WAIT_FOR_DEBIT);
        this.supplierRecoveryPendingClaims.addAll(this.recoveryClaimService
                .findClaimInState(RecoveryClaimState.WAIT_FOR_DISPUTED_AUTO_DEBIT));
        this.supplierRecoveryPendingClaims.addAll(this.recoveryClaimService
                .findClaimInState(RecoveryClaimState.WAIT_FOR_NO_RESPONSE_AUTO_DEBIT));
        return SUCCESS;
    }

    public String syncPaymentMade() {
        Assert.notNull(this.id);
        Claim claim = this.claimRepository.find(this.id);
        if (ClaimState.PENDING_PAYMENT_SUBMISSION.equals(claim.getState())) {
            SyncTracker syncTracker = syncTrackerService.findByStatusSyncTypeAndUniqueIdValue(
                                               SyncStatus.TO_BE_PROCESSED.getStatus(), "Claim", claim.getClaimNumber());
            if (syncTracker != null) {
                syncTracker.setStatus(SyncStatus.CANCELLED);
                syncTracker.setUpdateDate(new Date());
                syncTrackerService.update(syncTracker);
            }
        }
        // For now allow for 10% TAX
        CreditMemo creditMemo = new CreditMemo();
        creditMemo.setClaimNumber(claim.getClaimNumber());
        creditMemo.setCreditMemoDate(Clock.today());
        // Since we can now have multiple credit memos per claim, using the
        // timestamp
        // to generate the numbers.
        creditMemo.setCreditMemoNumber(Clock.now().toString("hhmmss", TimeZone.getDefault()));
        Money actualAllocatedAmt = claim.getPayment().getCreditMemoAmount();
		// creditMemo.setCreditMemoNumber(40000 + id.intValue());
        creditMemo.setTaxAmount(actualAllocatedAmt.times(0));
        creditMemo.setPaidAmount(actualAllocatedAmt);
        creditMemo.setPaidAmountErpCurrency(actualAllocatedAmt);
        if (actualAllocatedAmt != null && actualAllocatedAmt.isNegative()) {
			creditMemo.setCrDrFlag("CR");
		} else {
			creditMemo.setCrDrFlag("DR");
		}
        this.paymentAsyncService.syncCreditMemo(creditMemo);
        return SUCCESS;
    }

    public String syncPaymentMadeForSRClaims() {
        Assert.notNull(this.id);
        RecoveryClaim recClaim = this.recoveryClaimRepository.find(this.id);
        // For now allow for 10% TAX
        CreditMemo creditMemo = new CreditMemo();
        creditMemo.setRecoveryClaim(recClaim);
        creditMemo.setClaimNumber(recClaim.getClaim().getClaimNumber());
        creditMemo.setCreditMemoDate(Clock.today());
        // Since we can now have multiple credit memos per claim, using the
        // timestamp
        // to generate the numbers.
        creditMemo.setCreditMemoNumber(Clock.now().toString("hhmmss", TimeZone.getDefault()));
        // creditMemo.setCreditMemoNumber(40000 + id.intValue());
        creditMemo.setTaxAmount(recClaim.getTotalRecoveredCost().times(0.1));
        creditMemo.setPaidAmount(recClaim.getTotalRecoveredCost());
        this.paymentAsyncService.syncCreditMemo(creditMemo);
        return SUCCESS;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List getClaimsPending() {
        return (List) this.claimsPending;
    }

    public void setClaimsPending(List claimsPending) {
        this.claimsPending = claimsPending;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
        this.paymentAsyncService = paymentAsyncService;
    }

    public List getSupplierRecoveryPendingClaims() {
        return (List) this.supplierRecoveryPendingClaims;
    }

    public void setSupplierRecoveryPendingClaims(List supplierRecoveryPendingClaims) {
        this.supplierRecoveryPendingClaims = supplierRecoveryPendingClaims;
    }

    public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
        this.recoveryClaimService = recoveryClaimService;
    }

    public String getClaimType() {
        return this.claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public void setRecoveryClaimRepository(RecoveryClaimRepository recoveryClaimRepository) {
        this.recoveryClaimRepository = recoveryClaimRepository;
    }

    public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
        this.syncTrackerService = syncTrackerService;
    }
}

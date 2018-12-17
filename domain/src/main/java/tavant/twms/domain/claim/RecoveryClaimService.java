/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.claim;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.SupplierPartReturnClaimSummary;
import tavant.twms.domain.supplier.RecoveryClaimCriteria;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import com.domainlanguage.time.TimePoint;

/**
 * @author pradipta.a
 */
public interface RecoveryClaimService {

    @Transactional(readOnly = false)
    public void updateRecoveryClaim(RecoveryClaim recoveryClaim);

    @Transactional(readOnly = true)
    public List<RecoveryClaim> findClaimInState(RecoveryClaimState state);

    @Transactional(readOnly = true)
    public boolean isPaymentMade(RecoveryClaim recoveryClaim);

    @Transactional(readOnly = true)
    public RecoveryClaim findRecoveryClaim(Long id);
    
    @Transactional(readOnly = true)
    public RecoveryClaim findRecoveryClaim(String recoveryClaimNumber); 

     
    public boolean checkAutoClose(RecoveryClaim recClaim);

    public PageResult<RecoveryClaim> findAllRecoveryClaimsMatchingQuery(Long domainPredicateId,
            ListCriteria listCriteria);

    public RecoveryClaim findRecoveryClaimForClaim(Long id);

    @Transactional(readOnly = false)
    public void updatePayment(RecoveryClaim recoveryClaim);
    
    @Transactional(readOnly = true)
    public RecoveryClaim findActiveRecoveryClaimForClaim(String warrantyClaimNumber);
    
    @Transactional(readOnly = true)
    public List<RecoveryClaim> findAllRecClaimsForMultiMaintainance(Long domainPredicateId,
            ListCriteria listCriteria);

	public PageResult<RecoveryClaim> findRecClaimsForPredefinedSearch(
			RecoveryClaimCriteria searchObj,ListCriteria listCriteria);

	public PageResult<SupplierPartReturnClaimSummary> findAllRecoveryClaimsMatchingCriteria(
			final PartRecoverySearchCriteria partRecoverySearchCriteria);

	public List<PartReturnStatus> findAllStatusForPartRecovery();
	
	public RecoveryClaim findActiveRecoveryClaimForClaimForOfflineDebit(String warrantyClaimNumber);

    public void createRecClaimPaymentAudits(RecoveryClaim recoveryClaim, TimePoint updatedOn, Long claimAuditId);

    public List<RecoveryClaim> findRecoveryClaimsForVRRDownload(PartRecoverySearchCriteria criteria, int pageNum, int recordsPerPage);

    public Long findRecClmsCountForVRRDownload(PartRecoverySearchCriteria criteria);
    
    public RecoveryClaim findRecoveryClaimByPartNumber(final Map<String,Object> params);
    
    public RecoveryClaim findByRecoveryClaimNumber(final String recoveryClaimNumber);
    
    public String findRecClaimFromPartReturn(final Long partReturnId);

}

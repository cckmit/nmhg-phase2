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

import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.SupplierPartReturnClaimSummary;
import tavant.twms.domain.supplier.RecoveryClaimCriteria;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

/**
 * @author pradipta.a
 */
public interface RecoveryClaimRepository extends GenericRepository<RecoveryClaim, Long> {
	
	String CONFIRM_DEALER_PART_RETURNS_TASK = "Confirm Dealer Part Returns";

    public List<RecoveryClaim> findClaimInState(RecoveryClaimState state);

    public RecoveryClaim find(Long id);
    
    public RecoveryClaim find(String recoveryClaimNumber);

    public PageResult<RecoveryClaim> findRecoveryClaimsUsingDynamicQuery(
            final String queryWithoutSelect, final String orderByClause, final String selectClause,
            PageSpecification pageSpecification, final QueryParameters paramsMap);

    public List<RecoveryClaim> findRecClaimForClaim(Long id);
    
    public RecoveryClaim findActiveRecoveryClaimForClaim(String warrantyClaimNumber);
    
    public List<RecoveryClaim> findAllRecClaimsForMultiMaintainance(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause,
			final QueryParameters params);
    
    public PageResult<RecoveryClaim> findRecClaimsForPredefinedSearch(RecoveryClaimCriteria recoveryClaimCriteria,ListCriteria listCriteria);

	public PageResult<SupplierPartReturnClaimSummary> findAllRecoveryClaimsMatchingCriteria(
			final PartRecoverySearchCriteria partRecoverySearchCriteria);

	public List<PartReturnStatus> findAllStatusForPartRecovery();

	public RecoveryClaim findActiveRecoveryClaimForClaimForOfflineDebit(String warrantyClaimNumber);

    public List<SupplierRecoveryPaymentAudit> fetchAllRecClaimPaymentAudits(final List<Long> recClaimAuditIds);

    public void updateAllRecClaimPaymentAudits(SupplierRecoveryPaymentAudit paymentAudit);
    
    public List<RecoveryClaim> findRecoveryClaimsForVRRDownload(PartRecoverySearchCriteria criteria,
            int pageNum, int recordsPerPage);

    public Long findRecClmsCountForVRRDownload(PartRecoverySearchCriteria criteria);
    
    public RecoveryClaim findRecoveryClaimByPartNumber(final Map<String,Object> params);
    
    public RecoveryClaim findByRecoveryClaimNumber(final String recoveryClaimNumber); 
    
    public String findRecClaimFromPartReturn(Long partReturnId);
}

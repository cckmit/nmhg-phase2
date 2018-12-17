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

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.ListCriteria;

/**
 * @author vineeth.varghese
 * 
 */
@Transactional(readOnly = true)
public interface PartReturnService {

    void updatePartReturns(OEMPartReplaced partReplaced, Claim claim);

    PartReturn findPartReturn(Long id);

    boolean isEligibleForPayment(Claim claim);

    @Transactional(readOnly = false)
    void updatePartReturnsForClaim(Claim claim, List<OEMPartReplaced> replacedParts);
    
    @Transactional(readOnly = false)
    void acceptPartAfterInspection(List<PartReturn> parts, String comments, String accepatanceCode);

    @Transactional(readOnly = false)
    void rejectPartAfterInspection(List<PartReturn> partReturns, String failureCode, String comments);

    @Transactional(readOnly = false)
    void save(PartReturnDefinition partReturnDefinition);

    @Transactional(readOnly = false)
    void update(PartReturnDefinition partReturnDefinition);

    @Transactional(readOnly = false)
    void delete(PartReturnDefinition partReturnDefinition);

    public List<PaymentCondition> findAllPaymentConditions();

    public PartReturnDefinition findPartReturnDefinitionById(Long id);

    boolean isUnique(PartReturnDefinition partReturnDefinition);

    @Transactional(readOnly = false)
    public void updatePartStatus(OEMPartReplaced partReplaced);

    public PageResult<PartReturnClaimSummary> findAllPartReturnsMatchingQuery(
            Long domainPredicateId, ListCriteria listCriteria);

    @Transactional(readOnly = false)
    public void updateBOMForOEMParts(Claim claim);

    PaymentCondition findPaymentCondition(String code);

    public void checkPaymentConditionConsistency(OEMPartReplaced partReplaced);

    public boolean canMakePaymentDecision(Claim claim);

    public void updateExistingPartReturns(OEMPartReplaced partReplaced, Claim claim);

    public boolean isPartShipped(OEMPartReplaced partReplaced);
    
    public boolean shouldClaimMoveToRejectedParts(Claim claim);
    
    public boolean doesClaimHaveRejectedParts(Claim claim);
    
    public PageResult<PartReturnClaimSummary> findAllClaimsMatchingCriteria(
    		final PartReturnSearchCriteria partReturnSearchCriteria);
   
    public List<Claim> getAllDraftClaims();
    
    public List<Claim> forwardedClaimCrossedOverDueDays(
			int forwardedOverdueDate);

    public String getWarrantyType(Claim claim);

    public Criteria populateCriteria(Claim claim, Item item, String warrantyType);
    
    public List<String> findAllStatusForPartReturn();

	public List<String> findAllLocationsForPartReturn();

    public List<Claim> fetchShipmentGeneratedClaimsCrossedWindowPeriodDays(int windowPeriodDays);

    @Transactional(readOnly = false)
    public void updatePartReturnConfiguration(PartReturnConfiguration partReturnConfiguration);

    public List<OEMPartReplaced> fetchRemovedParts(Claim claim, List<OEMPartReplaced> partsReplaced);
    
    public Boolean canClaimBeAccepted(Claim claim);

	public void update(PartReturn partReturn);
	
	@Transactional(readOnly = false)
	public void createPartReturnDefinitionAudit(PartReturnDefinition partReturnDefinition);
	
	public Set<OEMPartReplaced> getAllReplacedParts(Claim claim);

    /*@Transactional(readOnly = false)
    public void acceptSupplierPartAfterInspection(List<SupplierPartReturn> supplierPartReturns,
                                                  String accepatanceCode);

    @Transactional(readOnly = false)
    public void rejectSupplierPartAfterInspection(List<SupplierPartReturn> supplierPartReturns, String failureCode
    );*/

}

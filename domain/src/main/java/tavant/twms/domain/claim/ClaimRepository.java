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

package tavant.twms.domain.claim;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

/**
 * @author kamal.govindraj
 *
 */
public interface ClaimRepository extends GenericRepository<Claim, Long> {

	public void save(Claim newClaim);

	public void update(Claim claimToBeUpdated);
	public void updateInstalledParts(InstalledParts installedParts);
	public void updateLineItemGroup(LineItemGroup lineItemGroup);
	public void updatePayment(Payment payment);

	public Claim find(Long id);
	public Claim findClaimWithServiceInfoAttributes(Long id);

	public ClaimAudit findClaimAudit(Long id);

	public Collection<Claim> findAllPreviousClaimsForItem(Long invItemId);
	
	public Collection<Claim> findAllClaimsForItemFiledByDealer(String serialNumber,final ServiceProvider serviceProvider);

	public Collection<ClaimState> findAllClaimStates();

	public void delete(Claim claim);

	public Claim findClaimByNumber(String claimNumber);
	
	public Claim findClaimByNumber(final String claimNumber,
			final String dealerNumber);

	public void createClaimAudit(ClaimAudit claimAudit);
	
	public List<String> findTasksAssingedToUser(final String userId);

	public PageResult<Claim> findClaimsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final QueryParameters params);

	public Collection<Claim> findAllClaimsInState(ClaimState state);

	public Collection<Claim> findAllPreviousClaimsForClaimedItems(
			String serialNumber, Double hoursInService);

	public List<Claim> findAllClaimsForMultiMaintainance(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, final QueryParameters params);

	public List<Claim> findClaimsToRetryCreditSubmission();


	public PageResult<Claim> findClaimsUsingDynamicQuery(String claimNumber,
			PageSpecification pageSpecification);

	public PageResult<Claim> findAllClaimsMatchingCriteria(
			final ClaimSearchCriteria claimSearchCriteria);
	
	public PageResult<Claim> findAllHistClaimsMatchingCriteria(
			final ListCriteria listCriteria, ServiceProvider loggedInUser);
	
	public PageResult<Claim> getAllAcceptedClaimsMatchingCriteriaForDealer(
			final ListCriteria listCriteria, ServiceProvider loggedInUser);
	
	public PageResult<Claim> getAllPartShippedNotReceivedClaims(
			ListCriteria listCriteria, Long buConfigDays);
	
	public List<Claim> findClaimsWithPartsShipped(String buQueryAppended);

	public List<SyncTracker> findAllCreditSubmissionsAwaitingNotification();

	public List<ItemGroup> findProductTypes(final String itemGroupType);

	public String findClaimByPartReturnId(final Long partReturnId);

	public List<Claim> findClaimToReassign();

    public List<Claim> findSMRClaimToReassign();

	public List<Claim> findClaimsForRecovery();

	public List<Claim> findClaimsForRecovery(final int pageNumber,final int pageSize);

	public List<Claim> findClaimsForIds(List<Long> claimIds);
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart, final Claim claim);
	
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart);
	
	public Boolean isAnyOpenClaimWithPolicyOnInventoryItem(final InventoryItem inventoryItem,RegisteredPolicy policy);

	public boolean areOpenClaimsPresentForServiceProvider(final String serviceProviderNumber);

    public Collection<Claim> findAllPreviousClaimsForMajorComp(Long majorComInvId);
    
    public Boolean isAnyOpenClaimWithReplacedPart(final OEMPartReplaced oemPartReplaced,final Claim claim);
    
    public Boolean isAnyActiveClaimOnMajorComponent(final InventoryItem inventoryItem);
    
    public List<Long> findClaimsForBOMUpdation();

	public Long findAllClaimsCountForMultiClaimMaintenance(String queryWithoutSelect, QueryParameters params);
	
	public ClaimedItem findClaimedItem(final Long claimId, final Long inventoryId);

	public PageResult<Claim> findClaimsForRecovery(ListCriteria listCriteria);
	
	public Claim findLastFiledClaim();
	
	public List<Claim> getClaimsForItalyClaimNotification(Date recentTime);

	public List<ItemGroup> findProductTypesByBrand(String itemGroupType,
			String brandType);

	public List<ItemGroup> findModelTypesByBrand(String itemGroupType,
			String brandType);

    public Boolean isAnyClaimWithInstalledParts(final List<String> installedParts);
    

	public Long getCountOfPendingRecoveryClaims();
	
	public int updateCreditSubmissionDate(String claimNumber);


    
	public Boolean isAnyClaimWithReplacedParts(final List<String> replacedParts);

	public int updateCreditSubmissionDateForAcceptedOrDeniedClaims();

	public int updateEPOerrorMessagesDetails(Claim claim);
	
	public List<MarketingGroupsLookup> lookUpMktgGroupCodes(MarketingGroupsLookup lookup,boolean forProcessor);
	
	public Location getLocationForDefaultPartReturn(final String defaultPartReturnLocation);
	
}

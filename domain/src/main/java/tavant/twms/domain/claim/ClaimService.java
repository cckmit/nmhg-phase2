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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.RequiresCurrencyConversion;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportApplicablePart;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author kamal.govindraj
 *
 */
@Transactional(readOnly = true)
public interface ClaimService {

	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	public void createClaim(Claim claim);

	/**
	 * Takes in a partially filled in claim (header ) and initializes the rest
	 * of the content (mainly applicable for parts claim)
	 *
	 * @param claim
	 * @return
	 */
	public Claim initializeClaim(Claim claim);

	/**
	 * Save the changes to the claim to the database
	 *
	 * @param claim
	 */
	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	void updateClaim(Claim claim);
	
	
	
	
	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	void updateInstalledParts(InstalledParts installedParts);
	
	
	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	public void updateLineItemGroup(LineItemGroup lineItemGroup);
	

	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	public void updatePayment(Payment payment);


	/**
	 * Find the claim associated with the claim id
	 *
	 * @param id
	 * @return - null if the claim is not found
	 */
	Claim findClaim(Long id);
	
	Claim findClaimWithServiceInfoAttributes(Long id);

	/**
	 * Find the claimAudit associated with the claimAudit id
	 *
	 * @param id
	 * @return - null if the claim audit is not found
	 */
	ClaimAudit findClaimAudit(Long id);
	
	/**
	 * Find tasks assigned to User
	 *
	 * @param user
	 * @return
	 */

	public List<String> findTasksAssingedToUser(final String userId);

	/**
	 * Find claim by claim number
	 *
	 * @param claimNumber
	 * @return
	 */
	public Claim findClaimByNumber(String claimNumber);
	
	public Claim findClaimByNumber(final String claimNumber,
			final String dealerNumber);

	/**
	 * Find claim by claim number and dealer Number
	 *
	 * @param claimNumber
	 * @return
	 */

	/**
	 * Find all claims filed on the item with given serial number
	 * @param invItemId TODO
	 *
	 * @return
	 */
	public Collection<Claim> findAllPreviousClaimsForItem(Long invItemId);

	/**
	 * Find all possible claim states.
	 *
	 * @return
	 */
	public Collection<ClaimState> findAllClaimStates();

	/**
	 * Computes the payment information, sets it on the claim and persists it to
	 * the database. TODO maybe this should also happen transaparently whenever
	 * update is called.
	 *
	 * @param claim
	 */
	@Transactional(readOnly = false)
	@RequiresCurrencyConversion
	public void updatePaymentInformation(Claim claim)
			throws PaymentCalculationException;

	/**
	 * Update the part return & price information for each of the parts replaced
	 * TODO - should this method belong here? - need to find a better place for
	 * this
	 *
	 * @param claim
     * @param replacedParts
	 */
	public void updateOEMPartInformation(Claim claim, List<OEMPartReplaced> replacedParts);

	/**
	 * Deletes the claim. This method is meant to be used ONLY in order to
	 * delete a draft claim. This method will be removed later.
	 *
	 * @param claim
	 */
	@Transactional(readOnly = false)
	public void deleteClaim(Claim claim);

    @Transactional(readOnly = false)
	public void deactivateClaim(Claim claim);

	/**
	 * Creates audit information for the claim object.This method is invoked on
	 * every business state change of the claim.Intermediate(Non-business) claim
	 * states should be ignored.
	 *
	 * @param claim
	 * @param isExternal -
	 *            This audit information is visible to external users or not.
	 */
	@Transactional(readOnly = false)
	public void createClaimAudit(Object claim, boolean isExternal);

	public PageResult<Claim> findAllClaimsMatchingQuery(Long domainPredicateId,
			ListCriteria listCriteria);

	public PageResult<Claim> findAllClaimsForMultiMaintainance(
			Long domainPredicateId, ListCriteria listCriteria);

	public PageResult<Claim> findAllClaimsForMultiTransferReProcess(
			Long domainPredicateId, ListCriteria listCriteria, User user);

	public List<Claim> findClaimsForCreditSubmitRetry();

	public PageResult<Claim> findAllClaimsForQuickSearch(String claimNumber,
			ListCriteria listCriteria);

	public PageResult<Claim> findAllClaimsMatchingCriteria(
			final ClaimSearchCriteria claimSearchCriteria);
	
	public PageResult<Claim> findAllHistClaimsMatchingCriteria(
			ListCriteria listCriteria, ServiceProvider loggedInUser);
	
	public PageResult<Claim> getAllAcceptedClaimsMatchingCriteriaForDealer(
			ListCriteria listCriteria, ServiceProvider loggedInUser);
	
	public PageResult<Claim> getAllPartShippedNotReceivedClaims(
			ListCriteria listCriteria, Long buConfigDays);

	public List<Claim> findClaimsWithPartsShipped(String buQueryAppended);

	public List<ItemGroup> findProductTypes(String itemGroupType);

	public List<ItemGroup> findModelTypes(String itemGroupType);

    @Transactional(readOnly = false)
    public List<SyncTracker> findAllCreditSubmissionsAwaitingNotification();
    
    /**
     * @param partReturnId
     * @return
     */
    public String findClaimByPartReturnId(final Long partReturnId);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForInventory(Claim claim);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForInventory(Claim claim, boolean isSerialized);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForFaultCode(Claim claim, Long faultCodeId);

    public List<AdditionalAttributes> findAdditionalAttributesForFaultCode(Claim claim, Long faultCodeId);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForCausalPart(Claim claim, Item causalPart);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForClaim(Claim claim, Long smrReasonId);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForCausalPart(Claim claim, String partNumber);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForReplacedPart(Claim claim, OEMPartReplaced oemPartReplaced);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForReplacedPart(Claim claim, OEMPartReplaced oemPartReplaced, String partNumber);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForJobCode(Claim Claim);
    
    @Transactional(readOnly = true)
    public void prepareAttributesForJobCode(Claim claim, LaborDetail laborDetail, Long serviceProcId);
    
    public Payment getLatestManualCpReviewedPayment(Claim claim);

	public List<Claim> findClaimsForRecovery();
	
	public List<Claim> findClaimsForRecovery(int pageNumber, int pageSize);
	
	public List<Claim> findClaimsForIds(List<Long> claimIds);
	
	public void roundUpLaborOnClaim(Claim claim);
	
	public void checkMinRndUpAndComputePaymentForClaim(Claim claim);
	
	@Transactional(readOnly = false)
	public Claim reopenClaimForLaborRndUpOnCreditSubmission(Claim claim);
	public boolean areOpenClaimsPresentForServiceProvider(final String serviceProviderNumber);
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart, Claim claim);
	
	@Transactional(readOnly = false)
	public void updateBOMPartOnPartOffCoverage(Long claimId, Boolean isBOMUpdationNeeded);
	
	@Transactional(readOnly = false)
	public void autoAcceptedClaimPostActivities(Claim claim,String isPartCorrected);
	
	@Transactional(readOnly = false)
	public Claim reopenClaimForLaborRndUpOnClaimDenial(Claim claim);
		
	public void checkAdjustmentForRndUpLaborOnClaim(Claim claim,LaborDetail roundUpLaborDetail);
	
	public boolean hasRoundUpCode(Claim claim);
	
	public BigDecimal totalLaborHoursOnClaim(Claim claim);
	
	public BigDecimal getRoundUpHoursOnClaim(Claim claim);
	
	public List<Claim> getAcceptedClaimsInWindowPeriod(Claim claim);
	
	public BigDecimal getRoundUpHoursOnAllClaimsInWindowPeriod(List<Claim> allAcceptedClaimsInWindowPeriod);
	
	public BigDecimal getTotalHoursOnAllClaimsInWindowPeriod(List<Claim> allAcceptedClaimsInWindowPeriod);
	
	public Map<String, String[]> validateReplacedParts(HussmanPartsReplacedInstalled replacedInstalledPart, Claim claim);
	
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart);
	
	public Boolean isAnyOpenClaimWithPolicyOnInventoryItem(final InventoryItem inventoryItem,final RegisteredPolicy policy);

    public Boolean isAnyFailureReportPendingOnClaim(Claim claim);

    public List<CustomReport> fetchfailureReportsForItemsOnClaim(Claim claim);

    public boolean isCustomReportPartApplicableForItem(CustomReportApplicablePart customReportApplicablePart,Item item);

	public Collection<Claim> findAllPreviousClaimsForMajorComp(Long id);
	
	public Boolean isAnyOpenClaimWithReplacedPart(final OEMPartReplaced oemPartReplaced,final Claim claim);
	
	public Boolean isAnyActiveClaimOnMajorComponent(final InventoryItem inventoryItem);

	public Collection<Item> itemsApplicableOnReport(Claim claim, CustomReport customReport);
	
	public boolean reportAlreadyAnswered(Claim claim, CustomReport failureReport) ;
	
	public boolean acceptedClaimFailsRulesAfterPartOff(Claim claim);
	
	public List<Long> findClaimsForBOMUpdation();
	
	public Long findAllClaimsCountForMultiTransferReProcess(Long domainPredicateId, ListCriteria listCriteria, User user);
	
	public Long findAllClaimsCountForClaimAttributes(Long domainPredicateId, ListCriteria listCriteria);
	
	/*
	 * Finds the claimed item for a given claim and a serial number
	 */
	public ClaimedItem findClaimedItem(final Long claimId, final Long inventoryId);
	
	public PageResult<Claim> findClaimsForRecovery(ListCriteria listCriteria);

    public List<ClaimType> fetchAllClaimTypesForBusinessUnit();
    
    public List<ListOfValues> getLovsForClass(String className, Claim claim);
    
    public List<Claim> getClaimsForItalyClaimNotification(Date recentTime);
    
    public List<ItemGroup>findProductTypesByBrand(String itemGroupType, String brandType);
    
    public List<ItemGroup>findModelTypesByBrand(String itemGroupType, String brandType);
    
    public Long getCountOfPendingRecoveryClaims();
    
    public int updateCreditSubmissionDate(String claimNumber);

	public void updateCreditDateOfAcceptedAndDeniedClaims();

	public void updateEPOerrorMessagesDetails(Claim claim);
	
	public List<ItemGroup> listGroupCodeBasedOnGroupType();
	
	public List<MarketingGroupsLookup> lookUpMktgGroupCodes(MarketingGroupsLookup lookup,boolean forProcessor);

	public Location getLocationForDefaultPartReturn(String defaultPartReturnLocation);
	
}

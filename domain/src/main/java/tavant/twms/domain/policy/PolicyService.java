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
package tavant.twms.domain.policy;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@Transactional(readOnly = true)
public interface PolicyService {

    /**
     * Used primarily by Workflow.
     * @param claim
     * @return
     * @throws PolicyException
     */
    public void updateApplicablePolicies(Claim claim) throws PolicyException;

    public Policy findApplicablePolicy(ClaimedItem claimedItem) throws PolicyException;

    @Transactional(readOnly = true)
    public List<RegisteredPolicy> findRegisteredPolicies(InventoryItem inventoryItem)
            throws PolicyException;  

     /**
     * Finds Extended policies availalble for registration.
     *
     * @param inventoryItem
     * @param asOfDate
     * @return
     * @throws PolicyException
     */
    public List<PolicyDefinition> findExtendedPoliciesAvailableForRegistration(
            InventoryItem inventoryItem, CalendarDate asOfDate) throws PolicyException;

    /**
     * Find active policiey for an inventory item
     *
     * @param inventoryItem
     * @param claimedItem
     * @return
     * @throws PolicyException
     */
    public Collection<RegisteredPolicy> findActivePolicies(InventoryItem inventoryItem,
            ClaimedItem claimedItem) throws PolicyException;

    /**
     * Fi
     *
     * @param item
     * @param purchaseDate
     * @return
     */
    public Collection<PolicyDefinition> findActivePolicies(Item item, CalendarDate purchaseDate);

    /**
     * Finds policies available for registration including goodwill policies.
     *
     * @param inventoryItem
     * @return
     * @throws PolicyException
     */
    public List<PolicyDefinition> findPoliciesForAdminWarrantyReg(InventoryItem inventoryItem)
            throws PolicyException;

    /**
     * This method returns the policy fee for the Policy Definition.
     *
     * @param definition
     * @param criteria
     * @param asOfDate
     * @return
     */
    public Money getPolicyFeeForPolicyDefinition(PolicyDefinition definition,
            PolicyRatesCriteria criteria, CalendarDate asOfDate);

    /**
     * This method returns the tranfer fee for the Policy Definition.
     *
     * @param definition
     * @param criteria
     * @param asOfDate
     * @return
     */
    public Money getTransferFeeForPolicyDefinition(PolicyDefinition definition,
            PolicyRatesCriteria criteria, CalendarDate asOfDate);

    /**
     * This method returns Active Policies for a Claimed Item.
     * @param claimedItem
     * @return
     */
    public Collection<Policy> findActivePolicies(ClaimedItem claimedItem) throws PolicyException;
    public Policy findApplicablePolicy(Claim claim) throws PolicyException;

    /**
     * This method returns all available GoodWill Policies.
     */
    public List<PolicyDefinition> getAllGoodWillPolicies() throws PolicyException;
    
    public List<RegisteredPolicy> getPoliciesForWarranty(Warranty warranty);
    
    public List<PolicyDefinition> findGoodWillPoliciesForInventory(String serialNumber,CalendarDate asOfDate);

    public List<PolicyDefinition> findPoliciesAvailableWithPurposeWarrantyCoverage(Item item, CalendarDate today);

	public List<PolicyDefinition> findPoliciesAvailableForRegistration(
			InventoryItem inventoryItem, String addressBookType,
			ServiceProvider forDealer) throws PolicyException;
	
	public List<PolicyDefinition> findPoliciesUsingOptionCode(InventoryItem inventoryItem, String optionCode) throws PolicyException;
	
	public List<PolicyDefinition> findPoliciesAvailableForMajorCompRegistration(InventoryItem inventoryItem, CalendarDate calendarDate, String addressBookType,
			ServiceProvider certifiedDealer) throws PolicyException;
	
	 public List<PolicyDefinition> findGoodWillPoliciesForMajorComponent(String serialNumber,CalendarDate asOfDate);
	 
    public List<PolicyDefinition> findAllExtendedPoliciesAvailable(boolean isInternal);
    
    public List<RegisteredPolicy> filterPolicyByServiceProvider(List<RegisteredPolicy> policys,ServiceProvider serviceProvider);

	public List<String> findApplicablePolicesCodes(Claim theClaim);
	
	public List<PolicyDefinition> findPoliciesUsingAllOptionCodes(InventoryItem inventoryItem, List<String> optionCodes);

	public List<String> findApplicablePolicesCodes(
			ClaimedItem claimedItem);
	
	public List<PolicyDefinition> findInvisiblePolicies(InventoryItem inventoryItem,
            CalendarDate asOfDate, String customerType, ServiceProvider forDealer) throws PolicyException;
	
	public List<PolicyDefinition> findInvisiblePoliciesForAdminWarrantyReg(InventoryItem inventoryItem);
    
}
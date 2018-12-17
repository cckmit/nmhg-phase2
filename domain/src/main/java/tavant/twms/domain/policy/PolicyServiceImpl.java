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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.HibernateCast;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
public class PolicyServiceImpl implements PolicyService {
    private static Logger logger = LogManager.getLogger(PolicyServiceImpl.class);

    private WarrantyRepository warrantyRepository;

    private PolicyDefinitionRepository policyDefinitionRepository;

    private PolicyRatesRepository policyRatesRepository;

    private BestApplicablePolicyEvaluator bestApplicablePolicyEvaluator;
    
    private PolicyRepository policyRepository;
    

    
    public void setPolicyRepository(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}

	/**
     * @param policyRatesRepository
     */
    public void setPolicyRatesRepository(PolicyRatesRepository policyRatesRepository) {
        this.policyRatesRepository = policyRatesRepository;
    }

    public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
        this.policyDefinitionRepository = policyDefinitionRepository;
    }

    public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }
   
    
	private String getWarrantyOptionCode(InventoryItem inventoryItem) {
        if(inventoryItem.getOptions() != null && !inventoryItem.getOptions().isEmpty()){
            for (Option option : inventoryItem.getOptions()) {
                if ("W".equalsIgnoreCase(option.getSpecialOptionStatus()) && "A".equalsIgnoreCase(option.getActiveInactiveStatus())) {
                    return option.getOptionCode();
                }
            }
        }
		return null;
	}
	
	private List<String> getAllOptionCodes(InventoryItem inventoryItem) {
		List<Option> options = inventoryItem.getOptions();
		List<String> optionCodes = new ArrayList<String>();
		if(options != null && !options.isEmpty()) {
			for(Option option : options) {
				if("W".equalsIgnoreCase(option.getSpecialOptionStatus())
						&& "A".equalsIgnoreCase(option.getActiveInactiveStatus())) {
					optionCodes.add(option.getOptionCode());
				}
			}
		}
		return optionCodes;
	}
    
    public List<PolicyDefinition> findPoliciesAvailableForRegistration(InventoryItem inventoryItem, String customerType, ServiceProvider forDealer)
    throws PolicyException {
    	List<String> optionCodes = new ArrayList<String>();
    	if(!"AMER".equals(inventoryItem.getBusinessUnitInfo().getName())){
    		String warrantyOptionCode = getWarrantyOptionCode(inventoryItem);
    		if (warrantyOptionCode != null) {
    			optionCodes.add(warrantyOptionCode);
    		}
    	} else {
    		optionCodes.addAll(getAllOptionCodes(inventoryItem));
    	}
    	if(optionCodes != null && !optionCodes.isEmpty()){
    		Set<PolicyDefinition> policies = new HashSet<PolicyDefinition>();
            policies.addAll(findPoliciesUsingAllOptionCodes(inventoryItem, optionCodes));
            if(!"AMER".equals(inventoryItem.getBusinessUnitInfo().getName())){
                if(isExtendedPolicy(policies)){
                    List<PolicyDefinition> standardPolicies = findStandardPoliciesAvailableForRegistration(
                        inventoryItem, inventoryItem.getDeliveryDate(),
                        customerType, forDealer);
                    if (standardPolicies != null && standardPolicies.size() > 0) {
                        policies.addAll(standardPolicies);
                    }
                }
            }
		    ArrayList<PolicyDefinition> policieswithoutduplicates = new ArrayList<PolicyDefinition>();
		    policieswithoutduplicates.addAll(policies);   
    		return policieswithoutduplicates;
    	}else if(!"AMER".equals(inventoryItem.getBusinessUnitInfo().getName())) {
    		//This will return only standard policies
    		return findStandardPoliciesAvailableForRegistration(inventoryItem, inventoryItem.getDeliveryDate(), customerType, forDealer);
    	}
        return Collections.EMPTY_LIST;
    }
    
    
	private boolean isExtendedPolicy(Collection<PolicyDefinition> policies) {
		for (PolicyDefinition policyDefinition : policies) {
			if (WarrantyType.EXTENDED.getType()
					.equals(policyDefinition.getWarrantyType().getType())) {
				return true;
			}
		}
		return false;
	}
    
    public List<PolicyDefinition> findPoliciesUsingOptionCode(InventoryItem inventoryItem, String optionCode)
    	    throws PolicyException {
    	return this.policyDefinitionRepository.findPoliciesAvailableForRegistrationUsingOptionCode(inventoryItem, optionCode);
    }
    
    public List<PolicyDefinition> findPoliciesUsingAllOptionCodes(InventoryItem inventoryItem, List<String> optionCodes)
    	throws PolicyException {
    	List<PolicyDefinition> availablePolicies = this.policyDefinitionRepository.findPoliciesUsingAllOptionCodes(inventoryItem, optionCodes);
    	List<PolicyDefinition> eligiblePolicies = new ArrayList<PolicyDefinition>();
    	for(PolicyDefinition policy : availablePolicies) {
    		if(policy.isHoursOnMachineWithinCoverage(inventoryItem)) {
    			eligiblePolicies.add(policy);
    		}
    	}
    	return eligiblePolicies;
    }
   
    //Please do not use this method unless you need only standard warranty policies
    private List<PolicyDefinition> findStandardPoliciesAvailableForRegistration(InventoryItem inventoryItem,
            CalendarDate asOfDate, String customerType, ServiceProvider forDealer) throws PolicyException{
        List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository
                .findPoliciesForInventory(inventoryItem, asOfDate, customerType, forDealer,Boolean.FALSE);
        CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        return policiesAvailableOn(inventoryItem, offerredPolicies, deliveryDate);
    }
    
    //Please do not use this method unless you need only invisibleFilingDr warranty policies
    public List<PolicyDefinition> findInvisiblePolicies(InventoryItem inventoryItem,
            CalendarDate asOfDate, String customerType, ServiceProvider forDealer) throws PolicyException{
        List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository
                .findPoliciesForInventory(inventoryItem, asOfDate, customerType, forDealer,Boolean.TRUE);
        CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        return policiesAvailableOn(inventoryItem, offerredPolicies, deliveryDate);
    }
    
    public List<PolicyDefinition> findExtendedPoliciesAvailableForRegistration(
            InventoryItem inventoryItem, CalendarDate asOfDate) throws PolicyException {
        String serialNumber = inventoryItem.getSerialNumber();
        List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository
                .findExtendedPoliciesForInventory(serialNumber, asOfDate);
        CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        return policiesAvailableOn(inventoryItem, offerredPolicies, deliveryDate);
    }
    
    public List<PolicyDefinition> findAllExtendedPoliciesAvailable(
    		boolean isInternal) {
    	List<PolicyDefinition> availablePolicies = this.policyDefinitionRepository
    			.findAllExtendedPolicies(isInternal);
    	return availablePolicies;
    }

    public List<PolicyDefinition> getAllGoodWillPolicies() throws PolicyException{
        List<PolicyDefinition> goodWillPolicies = this.policyDefinitionRepository
        .getAllGoodWillPolicies();
        return goodWillPolicies;
    }

    public void updateApplicablePolicies(Claim claim) throws PolicyException {
        /*for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            claimedItem.setApplicablePolicy(findApplicablePolicy(claimedItem));
        }*/
    }

    public Policy findApplicablePolicy(ClaimedItem claimedItem) throws PolicyException {
        Claim claim = claimedItem.getClaim();
        if (claim.isOfType(ClaimType.CAMPAIGN)) {
            return null;
        }
        List<? extends Policy> policies = findPoliciesAvailable(claimedItem);
        if (policies == null || (policies != null && policies.size() == 0)) {
            return null;
        }
        Policy bestApplicablePolicy = this.bestApplicablePolicyEvaluator.findBestApplicablePolicy(claimedItem, policies);
        populateDetailsIfOutOfWarranty(bestApplicablePolicy, policies, claimedItem, claimedItem.getClaim());
        return bestApplicablePolicy;
    }

    public List<PolicyDefinition> findApplicablePoliciesForClaim(Claim claim) throws PolicyException {
    	List<? extends Policy> policies = null;
		List<PolicyDefinition> allPolicies = new ArrayList<PolicyDefinition>();
		if (claim.getPartItemReference().isSerialized()) {
			Warranty warranty = this.warrantyRepository.findBy(claim
					.getPartItemReference().getReferredInventoryItem());
				if(claim.getItemReference() !=null && claim.getItemReference().getReferredItem() !=null)
				allPolicies = fetchAllInvisiblePoliciesForItemNumber(claim.getItemReference().getReferredItem(), claim.getPurchaseDate());
			if (warranty != null) {
				policies = new ArrayList<Policy>(warranty.getPolicies());
				allPolicies.addAll(getApplicablePolicyDefinitions((List<RegisteredPolicy>) policies));
			}
		} else {
			if (claim.getInstallationDate() != null) {
				policies = findPoliciesAvailableWithPurposeWarrantyCoverage(claim.getPartItemReference()
						.getReferredItem(), claim.getInstallationDate());
				if(claim.getItemReference() !=null && claim.getItemReference().getReferredItem() !=null)
				allPolicies = fetchAllInvisiblePoliciesForItemNumber(claim.getItemReference().getReferredItem(),claim.getInstallationDate());
				allPolicies.addAll((Collection<? extends PolicyDefinition>) policies);

			} else {
				policies = findPoliciesAvailableWithPurposeWarrantyCoverage(claim.getPartItemReference()
						.getReferredItem(), claim.getPurchaseDate());
				if(claim.getItemReference() !=null && claim.getItemReference().getReferredItem() !=null)
				allPolicies = fetchAllInvisiblePoliciesForItemNumber(claim.getItemReference().getReferredItem(), claim.getPurchaseDate());
				allPolicies.addAll((Collection<? extends PolicyDefinition>) policies);
			}
		}
		if (allPolicies == null || allPolicies.size() == 0) {
			return null;
		}
		return allPolicies;
    }
    

	@SuppressWarnings("unchecked")
	public Policy findApplicablePolicy(Claim claim) throws PolicyException {
		List<PolicyDefinition> allPolicies = findApplicablePoliciesForClaim(claim);
		Policy bestApplicablePolicy = this.bestApplicablePolicyEvaluator
				.findBestApplicablePolicy(claim, allPolicies);
		populateDetailsIfOutOfWarranty(bestApplicablePolicy, allPolicies, null,
				claim);
		return bestApplicablePolicy;
	}
    


    private void populateDetailsIfOutOfWarranty(Policy bestApplicablePolicy, List<? extends Policy> policies, ClaimedItem claimedItem, Claim claim) {
        if (bestApplicablePolicy == null) {

            int maxServiceHoursCovered = 0;
            CalendarDate maxWarrantyEndDate = null;
            if(policies != null)
            for (Policy policy : policies) {

                Integer serviceHoursCovered = 0;
                CalendarDate warrantyEndDate = null;

                if (InstanceOfUtil.isInstanceOfClass(RegisteredPolicy.class, policy)) {

                    RegisteredPolicy p = new HibernateCast<RegisteredPolicy>().cast(policy);
                    serviceHoursCovered = (p.getLatestPolicyAudit()).getServiceHoursCovered();
                    warrantyEndDate = p.getWarrantyPeriod().getTillDate();

                } else if (InstanceOfUtil.isInstanceOfClass(PolicyDefinition.class, policy)) {

                    PolicyDefinition pd = new HibernateCast<PolicyDefinition>().cast(policy);
                    if (!pd.getInvisibleFilingDr()) {
                        serviceHoursCovered = (pd.getCoverageTerms()).getServiceHoursCovered();
                        if (claimedItem != null) {
                        	if(policy.getWarrantyType().getType().equals(WarrantyType.POLICY.getType()) && claimedItem.getItemReference()!=null){
                    			for(RegisteredPolicy RegisteredPolicy:claimedItem.getItemReference().getReferredInventoryItem().getWarranty().getPolicies()){
                    				if(RegisteredPolicy.getPolicyDefinition().equals(policy)){
                    					serviceHoursCovered = RegisteredPolicy.getLatestPolicyAudit().getServiceHoursCovered();
                    					warrantyEndDate = RegisteredPolicy.getWarrantyPeriod().getTillDate();
                    				}
                    			}
                    		}
                        	else{
                                warrantyEndDate = pd.computeWarrantyPeriod(claimedItem).getTillDate();
                        	}
                        } else {
                            warrantyEndDate = pd.computeWarrantyPeriod(claim).getTillDate();
                        }
                    }
                }

                if (serviceHoursCovered > maxServiceHoursCovered) {
                    maxServiceHoursCovered = serviceHoursCovered;
                }

                if (maxWarrantyEndDate == null) {
                    maxWarrantyEndDate = warrantyEndDate;
                } else if (warrantyEndDate != null && warrantyEndDate.isAfter(maxWarrantyEndDate)) {
                    maxWarrantyEndDate = warrantyEndDate;
                }
            }

            claim.setHoursCovered(maxServiceHoursCovered);
            claim.setCoverageEndDate(maxWarrantyEndDate);
        }
    }
    
    List<PolicyDefinition> findAllInvisiblePolicies(ClaimedItem claimedItem) {
    	InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
    	ItemReference itemReference = claimedItem.getItemReference();
    	PartsClaim partsClaim = null;
    	ItemGroup referredModel = null;
    	Claim claim = claimedItem.getClaim();
        if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)){
             partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        }
    	List<PolicyDefinition> invisiblePolicies = null;
    	if (claimedItem.getItemReference().isSerialized()) {
    		invisiblePolicies = fetchAllInvisiblePoliciesForItemNumber(inventoryItem.getOfType(),null);
    	} else if ((partsClaim!=null && !itemReference.isSerialized() && partsClaim.getPartInstalled())) {
    		referredModel = itemReference.getModel();
    		if(referredModel != null){
    			invisiblePolicies = fetchAllInvisiblePoliciesForModel(referredModel);
            }
    	} else {
            if (partsClaim != null) {
                return null;
            } else {
            	invisiblePolicies = fetchAllInvisiblePoliciesForItemNumber(itemReference.getReferredItem(),null);
            }
        }
    	return invisiblePolicies;
    }

    List<? extends Policy> findPoliciesAvailable(ClaimedItem claimedItem) {
        ItemReference itemReference = claimedItem.getItemReference();
        Claim claim = claimedItem.getClaim();
        List<? extends Policy> policies = null;
        ItemGroup referredModel = null;
        PartsClaim partsClaim = null;
        if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)){
             partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        }
        if (itemReference.isSerialized()) {
            InventoryItem inventoryItem = itemReference.getReferredInventoryItem();
            // No policy determination for STOCK inventory.
            if (inventoryItem.isInStock()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Policies are not available for stock inventory");
                }
                return null;
            }
            Warranty warranty = this.warrantyRepository.findBy(inventoryItem);
            if (warranty != null) {
                policies = new ArrayList<Policy>(warranty.getPolicies());
            } else {
            	//Performance fix. A retailed inventory should have coverage of its own. We should not look for applicable policy for a retailed inventory.
            	return null;
            }
        } else if((partsClaim!=null && !itemReference.isSerialized() && partsClaim.getPartInstalled())
                ||(partsClaim==null && !itemReference.isSerialized())){
            if (logger.isInfoEnabled()) {
                logger.info("Claim on non-serialized item");
            }
            referredModel = itemReference.getModel();
            CalendarDate asOfToday = Clock.today();
            if(referredModel!=null){
            	policies = findPoliciesAvailableOnModelNumber(referredModel, asOfToday);
            }
        }else {
            if (partsClaim != null) {
                return null;
            } else {
                policies = findPoliciesAvailable(itemReference.getReferredItem(), Clock.today());
            }
        }
        return policies;
    }
    
	List<PolicyDefinition> getApplicablePolicyDefinitions(
			List<RegisteredPolicy> policies) {
		List<PolicyDefinition> policyDefinitions = new ArrayList<PolicyDefinition>();
		for (RegisteredPolicy registedPolicy : policies) {
			policyDefinitions.add(registedPolicy.getPolicyDefinition());
		}
		return policyDefinitions;
	}
    
	
	// Method to Fetch Invisible Policies based on item number
    List<PolicyDefinition> fetchAllInvisiblePoliciesForItemNumber(Item item,CalendarDate date) {
    	List<PolicyDefinition> policies = new ArrayList<PolicyDefinition>();
    	if(date == null){
    		date = Clock.today();
    	}
    	 policies = this.policyDefinitionRepository.findInvisiblePoliciesForItem(item.getNumber(),date);
    	 return policies;
    }

    
 // Method to Fetch Invisible Policies based on  Model
    List<PolicyDefinition> fetchAllInvisiblePoliciesForModel(ItemGroup itemGroup) {
    	List<PolicyDefinition> policies = new ArrayList<PolicyDefinition>();
    	 policies = this.policyDefinitionRepository.findInvisiblePoliciesForProductCode(itemGroup.getName(), Clock.today());
    	 return policies;
    }
    
    /**
     * @param itemGroup
     * @param today
     * @return
     */
    private List<PolicyDefinition> findPoliciesAvailableOnModelNumber(ItemGroup productType ,CalendarDate today)
    {
    	// It's the same as of above API which fetches for the Serialized item
    	// Here we are just passing the model that is captured in Non Serialized Claim
    	return this.policyDefinitionRepository.findPoliciesForProduct(productType, today);
    }
    
    public List<RegisteredPolicy> findRegisteredPolicies(InventoryItem inventoryItem) {
        Warranty warranty = inventoryItem.getWarranty();
        return warranty != null ? new ArrayList(warranty.getPolicies()) : new ArrayList<RegisteredPolicy>();
    }

    /**
     * @param item
     * @param today
     * @return
     */
    private List<PolicyDefinition> findPoliciesAvailable(Item item, CalendarDate today) {
        ItemGroup productType = item.getProduct();

        // HACK-FIX for flow where a 'MachineClaim' refers to an item that is
        // not a machine. This is because our claim-creation UI doesn't guard
        // against
        // this possibility.
        if (productType == null) {
            return new ArrayList<PolicyDefinition>();
        }
        return this.policyDefinitionRepository.findPoliciesForProduct(productType, today);
    }

    public List<PolicyDefinition> findPoliciesAvailableWithPurposeWarrantyCoverage(Item item, CalendarDate today) {
        String itemNumber = item.getNumber();

        return this.policyDefinitionRepository.findApplicablePolicyForClaimedPart(itemNumber, today);
    }
    /**
     * @param inventoryItem
     * @param allPolicies
     * @return
     * @throws PolicyException
     */
    protected List<PolicyDefinition> policiesAvailableOn(InventoryItem inventoryItem,
            List<PolicyDefinition> allPolicies, CalendarDate aDate) throws PolicyException {
        List<PolicyDefinition> availableOnes = new ArrayList<PolicyDefinition>();
        for (PolicyDefinition aPolicyDefinition : allPolicies) {
            if (aPolicyDefinition.isAvailable(inventoryItem, aDate)) {
                availableOnes.add(aPolicyDefinition);
            }
        }
        return availableOnes;
    }

    public Collection<RegisteredPolicy> findActivePolicies(InventoryItem inventoryItem,
            ClaimedItem claimedItem) throws PolicyException {
        Warranty warranty = inventoryItem.getWarranty();
        if (warranty != null) {
            Set<RegisteredPolicy> allRegisteredPolicies = warranty.getPolicies();
            Set<RegisteredPolicy> activeRegisteredPolicies = new HashSet<RegisteredPolicy>();
            for (RegisteredPolicy registeredPolicy : allRegisteredPolicies) {
                if (registeredPolicy.covers(claimedItem, null)) {
                    /*
                     * if (registeredPolicy.getWarranty().getEndDate().isAfter(
                     * Clock.today())) {
                     */activeRegisteredPolicies.add(registeredPolicy);
                }
            }
            return activeRegisteredPolicies;
        } else {
            return new ArrayList<RegisteredPolicy>();
        }
    }

    public Collection<PolicyDefinition> findActivePolicies(Item item, CalendarDate purchaseDate) {
        return findPoliciesAvailable(item, purchaseDate);
    }

	@SuppressWarnings("unchecked")
	public List<PolicyDefinition> findPoliciesForAdminWarrantyReg(
			InventoryItem inventoryItem) throws PolicyException {
		if (!inventoryItem.getSerializedPart()
				&& inventoryItem.getWarranty() != null) {			
			Set<PolicyDefinition> appPolicyDefintions = new HashSet<PolicyDefinition>();
			//Needs to be re-factored - this will not be used for EMEA might be used in phase 2 for NA .
			ServiceProvider serviceProvider = new ServiceProvider();
			serviceProvider.setId(inventoryItem.getOwner().getId());
			appPolicyDefintions.addAll(this.findPoliciesAvailableForRegistration(inventoryItem,
					inventoryItem.getWarranty().getCustomerType(),serviceProvider));
			appPolicyDefintions.addAll(this.policyDefinitionRepository
					.findPoliciesForAdminWarrantyReg(inventoryItem)); //SLMSPROD-453 -- to display the available policies including goodwill policies
			appPolicyDefintions.removeAll(Collections.singleton(null));
			List<PolicyDefinition> applicablePolicies = new ArrayList<PolicyDefinition>(appPolicyDefintions);
			return applicablePolicies;
		} else { 
			return this.policyDefinitionRepository
					.findPoliciesForAdminWarrantyReg(inventoryItem);
		}
	}

	public List<PolicyDefinition> findInvisiblePoliciesForAdminWarrantyReg(InventoryItem inventoryItem){
		return this.policyDefinitionRepository
				.findInvisiblePoliciesForAdminWarrantyReg(inventoryItem);
	}
	
	public Money getPolicyFeeForPolicyDefinition(PolicyDefinition definition,
            PolicyRatesCriteria criteria, CalendarDate asOfDate) {
        Currency preferredCurrency = criteria.getDealer().getPreferredCurrency();
        List<PolicyFees> policyFeesList = definition.getRegistrationFees();
        Money policyFee = Money.valueOf(new BigDecimal(0.00),preferredCurrency,2);
        
        for(PolicyFees policyFees:policyFeesList){
        	if(policyFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(preferredCurrency.getCurrencyCode())){
        		policyFee = policyFees.getPolicyFee();
        	}
        }
        return policyFee;
    }

    public Money getTransferFeeForPolicyDefinition(PolicyDefinition definition,
            PolicyRatesCriteria criteria, CalendarDate asOfDate) {
        Currency preferredCurrency = criteria.getDealer().getPreferredCurrency();
        Money transferFee = Money.valueOf(new BigDecimal(0.00),preferredCurrency,2);
        List<PolicyFees> policyFeesList = definition.getTransferFees();
        for(PolicyFees policyFees:policyFeesList)
        {
        	if(policyFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(preferredCurrency.getCurrencyCode())){
        		transferFee = policyFees.getPolicyFee();
        	}
        }

        return transferFee;
    }

    public Collection<Policy> findActivePolicies(ClaimedItem claimedItem) throws PolicyException {
        ItemReference itemReference = claimedItem.getItemReference();
        Claim claim = claimedItem.getClaim();
        if (claim.isOfType(ClaimType.MACHINE) && itemReference.getReferredInventoryItem() != null) {
            return new ArrayList<Policy>(findActivePolicies(itemReference
                    .getReferredInventoryItem(), claimedItem));
        } else {
            return new ArrayList<Policy>(findActivePolicies(itemReference.getUnserializedItem(),
                claim.getInstallationDate()));
        }
    }

    public void setBestApplicablePolicyEvaluator(
            BestApplicablePolicyEvaluator bestApplicablePolicyEvaluator) {
        this.bestApplicablePolicyEvaluator = bestApplicablePolicyEvaluator;
    }

	public List<RegisteredPolicy> getPoliciesForWarranty(Warranty warranty) {
		return policyRepository.findPoliciesForWarranty(warranty);
	}
	
	public List<PolicyDefinition> findGoodWillPoliciesForInventory(String serialNumber,CalendarDate asOfDate){
		return policyDefinitionRepository.findGoodWillPoliciesForInventory(serialNumber,asOfDate);
	}

	public List<PolicyDefinition> findPoliciesAvailableForMajorCompRegistration(InventoryItem inventoryItem, CalendarDate asOfDate,
			String customerType, ServiceProvider forDealer)
			throws PolicyException {
		List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository
				.findPoliciesAvailableForMajorCompRegistration(inventoryItem, asOfDate,customerType, forDealer);
		List<PolicyDefinition> availableOnes = new ArrayList<PolicyDefinition>();
	        for (PolicyDefinition policyDefinition : offerredPolicies) {
	            if (policyDefinition.warrantyPeriodFor(inventoryItem) != null) {
	                availableOnes.add(policyDefinition);
	            }
	        }
		return availableOnes;
	}
	
	public List<PolicyDefinition> findGoodWillPoliciesForMajorComponent(String serialNumber,CalendarDate asOfDate){
		return policyDefinitionRepository.findGoodWillPoliciesForMajorComponent(serialNumber,asOfDate);
	}
	
	public List<RegisteredPolicy> filterPolicyByServiceProvider(List<RegisteredPolicy> policys,ServiceProvider serviceProvider){
		return policyRepository.filterPolicyByServiceProvider(policys, serviceProvider);
	}

	/**
	 * This is written only for fetching applicable policy codes for Processor.
	 */
	
	public List<String> findApplicablePolicesCodes(Claim claim) {
		return this.bestApplicablePolicyEvaluator.findApplicablePolicesCodes(
				claim, findApplicablePoliciesForClaim(claim));
	}
	
	/**
	 * This is written only for fetching applicable policy codes for Processor.
	 */
	public List<String> findApplicablePolicesCodes(ClaimedItem claimedItem) {
        Claim claim = claimedItem.getClaim();
        if (claim.isOfType(ClaimType.CAMPAIGN)) {
            return null;
        }
        List<? extends Policy> policies = findPoliciesAvailable(claimedItem);
        List<PolicyDefinition> invisiblePolicies = findAllInvisiblePolicies(claimedItem);
        if ((policies == null || (policies != null && policies.size() == 0))
        		&& (invisiblePolicies == null || invisiblePolicies.isEmpty())) {
            return null;
        }
        List<String> applicablePolicyCodes = new ArrayList<String>();
        applicablePolicyCodes = this.bestApplicablePolicyEvaluator.findApplicablePolicyCodes(claimedItem, policies);
        applicablePolicyCodes.addAll(this.bestApplicablePolicyEvaluator.findApplicablePolicyCodes(claimedItem, invisiblePolicies));
        return applicablePolicyCodes;
	}	
}

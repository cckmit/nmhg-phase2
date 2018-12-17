package tavant.twms.domain.warranty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.domainlanguage.money.Money;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyRatesCriteria;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SecurityHelper;

public class MajorCompRegUtil {

	private InventoryService inventoryService;

	private SecurityHelper securityHelper;

	private InventoryTransactionService invTransactionService;

	private PolicyService policyService;
	
	private ConfigParamService configParamService;
	
	private WarrantyService warrantyService;
	
	private WarrantyCoverageRequestService warrantyCoverageRequestService;
	
	private MSAService msaService;
	
	private Map<Object, Object> keyValueOfCustomerTypes=null;

	private final Logger logger = Logger.getLogger(MajorCompRegUtil.class.getName());

	public boolean checkInventoryItemWithSNoAndPartNumberCombinationExits(InventoryItem majorComponent) {
		InventoryItem majorComp = new InventoryItem();
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		try {
			majorComp = inventoryService.findItemBySerialNumberAndModelNumber(majorComponent.getSerialNumber(),
					majorComponent.getOfType());
		} catch (ItemNotFoundException e) {
			return false;
		}
		if (majorComp != null) {
			errorCodeMap.put("error.majorComponent.majorCompWithSNoAndPartNumberCombinationExits", null);
			return true;
		} else
			return false;
	}

	public void createInventoryItemsCompositionAndTransaction(InventoryItem majorComponent,
			InventoryItem inventoryItem, ServiceProvider dealer, Customer endCustomer) {
		InventoryTransaction newTransaction = this.createInventoryTransaction(majorComponent);
		if (inventoryItem != null) {
			newTransaction.setBuyer(inventoryItem.getLatestBuyer());
		} else {
			newTransaction.setBuyer(endCustomer);
		}
		newTransaction.setSeller(dealer);
		newTransaction.setOwnerShip(dealer);
		majorComponent.getTransactionHistory().add(newTransaction);
		majorComponent.setCurrentOwner(dealer);
		if (inventoryItem != null) {
			majorComponent.setLatestBuyer(inventoryItem.getLatestBuyer());
		} else {
			majorComponent.setLatestBuyer(endCustomer);
		}
		inventoryService.createMajorCompAndUpdateInvItemBOM(inventoryItem, majorComponent);
	}
	
	public InventoryTransaction createInventoryTransaction(InventoryItem majorComponent) {
		InventoryTransaction newTransaction = new InventoryTransaction();
		newTransaction.setTransactionOrder(new Long(1));
		newTransaction.setTransactedItem(majorComponent);
		newTransaction.setInvTransactionType(invTransactionService
				.getTransactionTypeByName(InvTransationType.DR.name()));
		newTransaction.setTransactionDate(Clock.today());
		newTransaction.setStatus(BaseDomain.ACTIVE);
		return newTransaction;
	}
	
	public AddressBookType getAddressBookType(String customerType) {

		AddressBookType addressBookType = null;

		// fetching list of available customer types to BU parameter
		if(this.keyValueOfCustomerTypes==null)		
			this.keyValueOfCustomerTypes=this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_DR.getName());
		
		// checking given customer type is in available list or not
		if (this.keyValueOfCustomerTypes.containsValue(customerType)) {
			if ("EndCustomer".equals(customerType)) {
				addressBookType = AddressBookType.ENDCUSTOMER;

			} else if ("DirectCustomer".equals(customerType)) {
				addressBookType = AddressBookType.DIRECTCUSTOMER;

			} else if ("InterCompany".equals(customerType)) {
				addressBookType = AddressBookType.INTERCOMPANY;

			} else if ("NationalAccount".equals(customerType)) {
				addressBookType = AddressBookType.NATIONALACCOUNT;

			} else if ("Dealer".equals(customerType)) {
				addressBookType = AddressBookType.DEALER;
			}else if (AddressBookType.FEDERAL_GOVERNMENT.getType().equalsIgnoreCase(customerType)){
				addressBookType =  AddressBookType.FEDERAL_GOVERNMENT;
	    	}else if (AddressBookType.STATE_GOVERNMENT.getType().equalsIgnoreCase(customerType)){
	    		addressBookType =  AddressBookType.STATE_GOVERNMENT;
	     	}else if (AddressBookType.COUNTY_GOVERNMENT.getType().equalsIgnoreCase(customerType)){
	     		addressBookType =  AddressBookType.COUNTY_GOVERNMENT;
	     	}else if (AddressBookType.CTV_GOVERNMENT.getType().equalsIgnoreCase(customerType)){
	     		addressBookType =  AddressBookType.CTV_GOVERNMENT;
	     	}else if (AddressBookType.HOMEOWNERS.getType().equalsIgnoreCase(customerType)){
	     		addressBookType =   AddressBookType.HOMEOWNERS;
	     	}else if (AddressBookType.BUSINESS.getType().equalsIgnoreCase(customerType)){
	     		addressBookType =   AddressBookType.BUSINESS;
	     	}else if (AddressBookType.REGIONAL_ACCOUNT.getType().equalsIgnoreCase(customerType)){
	     		addressBookType =   AddressBookType.REGIONAL_ACCOUNT;
	      	}
		}
		return addressBookType;
	}

	public AddressForTransfer populateAddressForTransfer(Address address) {
		AddressForTransfer addressForTransfer = new AddressForTransfer();
		addressForTransfer.setAddressLine(address.getAddressLine1());
		addressForTransfer.setAddressLine2(address.getAddressLine2());
		addressForTransfer.setAddressLine3(address.getAddressLine3());
		addressForTransfer.setCity(address.getCity());
		addressForTransfer.setState(address.getState());
		addressForTransfer.setCountry(address.getCountry());
		addressForTransfer.setZipCode(address.getZipCode());
		addressForTransfer.setEmail(address.getEmail());
		addressForTransfer.setPhone(address.getPhone());
		addressForTransfer.setSecondaryPhone(address.getSecondaryPhone());
		addressForTransfer.setContactPersonName(address.getContactPersonName());
		addressForTransfer.setCustomerContactTitle(address.getCustomerContactTitle());
		addressForTransfer.setCounty(address.getCounty());
		if(address.getCountyCodeWithName()!=null){
			addressForTransfer.setCountyCodeWithName(address.getCountyCodeWithName());
		}
		else if(address.getState()!=null && address.getCounty()!=null)
		{
			String countyName = msaService.findCountyNameByStateAndCode(address.getState(),address.getCounty());
			addressForTransfer.setCountyCodeWithName(address.getCounty()+"-"+countyName);
		}
		return addressForTransfer;
	}

	public List<RegisteredPolicy> fetchAvailablePoliciesForMajorComp(InventoryItem majorComponent,
			String addressBookTypeForNonCertifiedInstaller, ServiceProvider certifiedDealer, ServiceProvider dealer) {
		try {
			List<PolicyDefinition> policyDefinitions = this.policyService
					.findPoliciesAvailableForMajorCompRegistration(majorComponent, majorComponent.getDeliveryDate(),
							addressBookTypeForNonCertifiedInstaller, certifiedDealer);
			List<RegisteredPolicy> availablePolicies = createPoliciesForMajorComp(policyDefinitions, majorComponent,
					dealer);
			Collections.sort(availablePolicies);
			return availablePolicies;
		} catch (PolicyException pex) {
			logger.error("Error while fetching policies available for major component", pex);
			return null;
		}
	}
	
	public boolean createWarranty(InventoryItem majorComponent, InventoryItem unit, ServiceProvider certifiedInstaller,
			Customer nonCertifiedInstaller, AddressBookType addBookTypeForNonCertInst, Customer customer,
			ServiceProvider dealer) {
		boolean reducedCoverage = false;
		Warranty warranty = setWarrantyAttributesFromMajorComponent(majorComponent);
		if (certifiedInstaller != null && certifiedInstaller.getId() != null) {
			warranty.setCertifiedInstaller(certifiedInstaller);
		} else {
			warranty.setNonCertifiedInstaller(nonCertifiedInstaller);
		}
		if (unit != null) {
			Customer latestBuyer = new HibernateCast<Customer>().cast(majorComponent.getLatestBuyer());
			warranty.setCustomer(latestBuyer);
		} else {
			warranty.setCustomer(customer);
		}
		warranty.setForDealer(this.getLoggedInUsersDealership());
		String nonCertificationInstallerType = addBookTypeForNonCertInst != null ? addBookTypeForNonCertInst.getType()
				: "";
		WarrantyAudit warrantyAudit = new WarrantyAudit();
		warrantyAudit.setSelectedPolicies(this.fetchAvailablePoliciesForMajorComp(majorComponent,
				nonCertificationInstallerType, certifiedInstaller, dealer));
		warrantyAudit.setStatus(WarrantyStatus.ACCEPTED);
		warranty.getWarrantyAudits().add(warrantyAudit);
		try {
			this.warrantyService.createPoliciesForWarrantyForMajorCompReg(warranty, majorComponent);
		} catch (PolicyException e) {
			logger.error("Error when creating policies available for InventoryItem[slNo:"
					+ majorComponent.getSerialNumber() + "]", e);
		}
		if (warrantyCoverageRequestService.hasPoliciesWithReducedCoverage(warranty)) {
			WarrantyCoverageRequest wcr = warrantyCoverageRequestService
					.storeReducedCoverageInformationForInventory(majorComponent);
			if (wcr.getId() != null) {

				reducedCoverage = true;
			}
		}
		return reducedCoverage;
	}

	public Warranty setWarrantyAttributesFromMajorComponent(InventoryItem majorComponent) {
		Warranty warranty = new Warranty();
		warranty.setInventoryItem(majorComponent);
		warranty.setDeliveryDate(majorComponent.getDeliveryDate());
		warranty.setStatus(WarrantyStatus.ACCEPTED);
		warranty.setForTransaction(majorComponent.getLatestTransaction());
		warranty.setTransactionType(majorComponent.getLatestTransaction().getInvTransactionType());
		warranty.setAddressForTransfer(this.populateAddressForTransfer(majorComponent.getLatestBuyer()
				.getAddress()));
		warranty.setCustomerType(majorComponent.getLatestBuyer().getType());
		warranty.setFiledBy(this.securityHelper.getLoggedInUser());		
		return warranty;
	}
	
	private List<RegisteredPolicy> createPoliciesForMajorComp(List<PolicyDefinition> withDefinitions,
			InventoryItem forItem, ServiceProvider dealer) throws PolicyException {
		List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
		for (PolicyDefinition definition : withDefinitions) {
			String policyName = definition.getCode();
			if ((policyName == null) || (policyName.trim().equals(""))) {
				continue;
			}
			RegisteredPolicy policy = new RegisteredPolicy();
			policy.setPolicyDefinition(definition);			
			policy.setWarrantyPeriod(definition.warrantyPeriodFor(forItem));
			computePriceForPolicyForMajorComp(forItem, policy, dealer);
			policies.add(policy);
		}
		return policies;
	}

	private void computePriceForPolicyForMajorComp(InventoryItem forItem, RegisteredPolicy policy,
			ServiceProvider dealer) {
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
		DealerCriterion dealerCriterion = new DealerCriterion();
		dealerCriterion.setDealer(dealer);
		criteria.setDealerCriterion(dealerCriterion);
		criteria.setProductType(forItem.getOfType().getProduct());
		Money price = this.policyService.getPolicyFeeForPolicyDefinition(policy.getPolicyDefinition(), criteria,
				forItem.getDeliveryDate());
		policy.setPrice(price);
	}

	public ServiceProvider getLoggedInUsersDealership() {
		Organization organization = this.securityHelper.getLoggedInUser().getCurrentlyActiveOrganization();
		return (organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ? new HibernateCast<ServiceProvider>()
				.cast(organization)
				: null;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public void setWarrantyCoverageRequestService(WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}


}

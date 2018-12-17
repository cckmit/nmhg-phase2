package tavant.twms.domain.warranty;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.NationalAccount;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.ExtendedWarrantyNotification;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyRatesCriteria;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyRegistrationType;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;

import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class WarrantyUtil {

	private Logger logger = Logger.getLogger(WarrantyUtil.class);

	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
	
	private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;

	private InventoryService inventoryService;

	private OrgService orgService;

	private SecurityHelper securityHelper;

	private PolicyService policyService;
	
	private WarrantyService warrantyService;
	
	private InventoryTransactionService invTransactionService;
	
	private ConfigParamService configParamService;	
	
	private UserRepository userRepository;
	
	private CustomerService customerService;
	
	private AddressBookService addressBookService;
	
	private OrganizationRepository organizationRepository;
	
	private MSAService msaService;

	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public AddressBookService getAddressBookService() {
		return addressBookService;
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public final InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public final void setInvTransactionService(InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public final WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public final void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public User getLoggedInUser() {
		return this.securityHelper.getLoggedInUser();
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryScrapTransactionXMLConverter getInventoryScrapTransactionXMLConverter() {
		return inventoryScrapTransactionXMLConverter;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
	}
	
	public InventoryStolenTransactionXMLConverter getInventoryStolenTransactionXMLConverter() {
		return inventoryStolenTransactionXMLConverter;
	}

	public void setInventoryStolenTransactionXMLConverter(
			InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter) {
		this.inventoryStolenTransactionXMLConverter = inventoryStolenTransactionXMLConverter;
	}
	
	public boolean canPerformD2D() {
		return this.configParamService
		.getBooleanValue(ConfigName.PERFORM_D2D_ON_FILING_WR
				.getName());
	}
	
	public boolean isInstallingDealerEnabled() {
		return this.configParamService
		.getBooleanValue(ConfigName.ENABLE_DEALER_AND_INSTALLATION_DATE
				.getName());
	}

	public boolean validateDeliveryAndInstallationDate(CalendarDate deliveryDate, CalendarDate installationDate,
			InventoryItem inventoryItem, Map<String, String[]> errorCodeMap) {
		boolean isValid = true;	
		
		if (this.isInstallingDealerEnabled() && installationDate != null && installationDate.isBefore(inventoryItem.getBuiltOn())) {
			// The delivery date chosen cannot be a future date
			errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateCannotBeforeBuildDate", new String[]{inventoryItem.getSerialNumber()});		
		
			isValid = false;
		}
		
		if (this.isInstallingDealerEnabled() && installationDate != null && installationDate.isAfter(Clock.today())) {
			// The delivery date chosen cannot be a future date
			errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateCannotBeInFuture", new String[]{inventoryItem.getSerialNumber()});		
		
			isValid = false;
		}

		if (this.isInstallingDealerEnabled() && installationDate != null && installationDate.isBefore(inventoryItem.getShipmentDate())) {
			// The delivery date chosen by the user is before shipment date
			
			errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateBeforeShipment", new String[]{inventoryItem.getSerialNumber()});		
			
			isValid = false;
		}
		
		if (this.isInstallingDealerEnabled() && deliveryDate != null && deliveryDate.isBefore(installationDate)) {
			// The delivery date chosen by the user is before installation date
			errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateBeforeInstallation", new String[]{inventoryItem.getSerialNumber()});	
			
			isValid = false;
		} 
		
			if (deliveryDate != null && deliveryDate.isAfter(Clock.today())) {				
				// The delivery date chosen cannot be a future date
				errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateCannotBeInFuture", new String[]{inventoryItem.getSerialNumber()});	
				
				isValid = false;
			}

			if (deliveryDate != null && deliveryDate.isBefore(inventoryItem.getShipmentDate())) {
				// The delivery date chosen by the user is before shipment date
				errorCodeMap.put("dealerAPI.warrantyRegistration.deliveryDateBeforeShipment", new String[]{inventoryItem.getSerialNumber()});	
				isValid = false;
			}
			
			if (inventoryItem.getInventoryItemAttrVals() != null && !inventoryItem.getInventoryItemAttrVals().isEmpty()) {
				CalendarDate scrapDate = null;
				CalendarDate unScrapDate = null;
				CalendarDate stolenDate = null;
				CalendarDate unStolenDate = null;
				String previousItemCondition = null;
				int indexAtWhichAdded = inventoryItem.getInventoryItemAttrVals().size();
				if(indexAtWhichAdded!=0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getAttribute().getName().equalsIgnoreCase("StolenComments")){
					InventoryStolenTransaction stolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
							.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getValue());
					stolenDate = stolen.getDateOfStolenOrUnstolen();
					previousItemCondition = stolen.getPreviousItemCondition();
				}
				if(indexAtWhichAdded==0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded).getAttribute().getName().equalsIgnoreCase("StolenComments")){
						InventoryStolenTransaction stolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
								.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded).getValue());
						stolenDate = stolen.getDateOfStolenOrUnstolen();
						previousItemCondition = stolen.getPreviousItemCondition();
					}
				if(indexAtWhichAdded!=0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getAttribute().getName().equalsIgnoreCase("unStolenComments")){
						InventoryStolenTransaction unStolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
								.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getValue());
						unStolenDate = unStolen.getDateOfStolenOrUnstolen();
						previousItemCondition = unStolen.getPreviousItemCondition();
				}
				for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem.getInventoryItemAttrVals()) {
					if (AttributeConstants.SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
						InventoryScrapTransaction scrap = (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						scrapDate = scrap.getDateOfScrapOrUnscrap();
						previousItemCondition = scrap.getPreviousItemCondition();
					}
					if (AttributeConstants.UN_SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())) {
						InventoryScrapTransaction unScrap = (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						unScrapDate = unScrap.getDateOfScrapOrUnscrap();
					}
					if (deliveryDate != null && scrapDate != null && unScrapDate != null) {												
						if ((deliveryDate.isAfter(scrapDate) || deliveryDate.equals(scrapDate))
								&& (deliveryDate.isBefore(unScrapDate) || deliveryDate.equals(unScrapDate))
								&& !scrapDate.equals(unScrapDate)) {
							
							errorCodeMap.put("dealerAPI.warrantyRegistration.scrap.machineScrapped", new String[]{inventoryItem.getSerialNumber()});	
							
							isValid = false;
							break;
						}
					}
				//}	
					
				
				}
				if (scrapDate != null && unScrapDate == null) {
					if (deliveryDate!=null){					
						if(deliveryDate.isAfter(scrapDate) || deliveryDate.equals(scrapDate)) {
							errorCodeMap.put("dealerAPI.warrantyRegistration.scrap.machineScrapped", new String[]{inventoryItem.getSerialNumber()});
							isValid = false;
						}
					if (deliveryDate.isBefore(scrapDate)) {
							inventoryItem.setConditionType(new InventoryItemCondition(previousItemCondition));
						}
					}
				}
				if (deliveryDate != null && stolenDate != null && unStolenDate != null) {												
					if ((deliveryDate.isAfter(stolenDate) || deliveryDate.equals(stolenDate))
							&& (deliveryDate.isBefore(unStolenDate) || deliveryDate.equals(unStolenDate))
							&& !stolenDate.equals(unStolenDate)) {
						
						errorCodeMap.put("dealerAPI.warrantyRegistration.stole.machineStolen", new String[]{inventoryItem.getSerialNumber()});	
						
						isValid = false;
					}
				}
				if (stolenDate != null && unStolenDate == null) {
					if (deliveryDate!=null){
							if(deliveryDate.isAfter(stolenDate) || deliveryDate.equals(stolenDate)) {
								errorCodeMap.put("dealerAPI.warrantyRegistration.stole.machineStolen", new String[]{inventoryItem.getSerialNumber()});
								isValid = false;
							}
					if (deliveryDate.isBefore(stolenDate)) {
							inventoryItem.setConditionType(new InventoryItemCondition(previousItemCondition));
						}
					}
				}
			}
			return isValid;

	}
		
	
 public boolean validateStolenInventory(CalendarDate deliveryDate, CalendarDate installationDate1,
			InventoryItem inventoryItem, Map<String, String[]> errorCodeMap){
	 boolean isValid = true;	
	 if (inventoryItem.getInventoryItemAttrVals() != null && !inventoryItem.getInventoryItemAttrVals().isEmpty()) {
			CalendarDate stolenDate = null;
			CalendarDate unStolenDate = null;
			String previousItemCondition = null;
			int indexAtWhichAdded = inventoryItem.getInventoryItemAttrVals().size();
			if(indexAtWhichAdded!=0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getAttribute().getName().equalsIgnoreCase("StolenComments")){
				InventoryStolenTransaction stolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
						.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getValue());
				stolenDate = stolen.getDateOfStolenOrUnstolen();
				previousItemCondition = stolen.getPreviousItemCondition();
			}
			if(indexAtWhichAdded==0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded).getAttribute().getName().equalsIgnoreCase("StolenComments")){
					InventoryStolenTransaction stolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
							.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded).getValue());
					stolenDate = stolen.getDateOfStolenOrUnstolen();
					previousItemCondition = stolen.getPreviousItemCondition();
				}
			if(indexAtWhichAdded!=0 && inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getAttribute().getName().equalsIgnoreCase("unStolenComments")){
					InventoryStolenTransaction unStolen = (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
							.convertXMLToObject(inventoryItem.getInventoryItemAttrVals().get(indexAtWhichAdded-1).getValue());
					unStolenDate = unStolen.getDateOfStolenOrUnstolen();
					previousItemCondition = unStolen.getPreviousItemCondition();
			}
				if (deliveryDate != null && stolenDate != null && unStolenDate != null) {												
					if ((deliveryDate.isAfter(stolenDate) || deliveryDate.equals(stolenDate))
							&& (deliveryDate.isBefore(unStolenDate) || deliveryDate.equals(unStolenDate))
							&& !stolenDate.equals(unStolenDate)) {
						
						errorCodeMap.put("dealerAPI.warrantyRegistration.stole.machineStolen", new String[]{inventoryItem.getSerialNumber()});	
						
						isValid = false;
					}
				}
			if (stolenDate != null && unStolenDate == null) {
				if (deliveryDate!=null){
					if(deliveryDate.isAfter(stolenDate) || deliveryDate.equals(stolenDate)) {
						errorCodeMap.put("dealerAPI.warrantyRegistration.stole.machineStolen", new String[]{inventoryItem.getSerialNumber()});
						isValid = false;
					}
				if (deliveryDate.isBefore(stolenDate)) {
						inventoryItem.setConditionType(new InventoryItemCondition(previousItemCondition));
					}
				}
			}
		}
	 return isValid;
 }
	
	
	public boolean validateMajorComponents(InventoryItem inventoryItem, Map<String, String[]> errorCodeMap) {
		boolean isValid = true;

		List<InventoryItemComposition> invComposition = inventoryItem.getComposedOf();
		if (invComposition != null && invComposition.size() > 0) {
			invComposition.removeAll(Collections.singleton(null));
			inventoryItem.getComposedOf().removeAll(Collections.singleton(null));
			Set<String> serialNumber = new HashSet<String>();
			Set<String> partNumber = new HashSet<String>();
			for (InventoryItemComposition part : invComposition) {
				validateSerialNumbersForParts(part.getPart().getSerialNumber(), errorCodeMap);
				validatePartNumber(part.getPart(), errorCodeMap);
				validateInstallationDate(inventoryItem,part.getPart(), errorCodeMap);

				if ((part.getPart().getInstallationDate() != null || (part.getPart().getSerialNumber() != null && !part
						.getPart().getSerialNumber().equals("")))
						&& part.getPart().getOfType() != null) {
					validatePart(part.getPart(), inventoryItem, errorCodeMap);
					partNumber.add(part.getPart().getOfType().getNumber());
					serialNumber.add(part.getPart().getSerialNumber());
					/*if (serialNumber.contains(part.getPart().getSerialNumber())
							&& partNumber.contains(part.getPart().getOfType().getNumber())) {
						
						
						isValid = false;
						break;

					} else {*/
						partNumber.add(part.getPart().getOfType().getNumber());
						serialNumber.add(part.getPart().getSerialNumber());
					
				}
			}
		}
		return isValid;
	}

	public void validateSerialNumbersForParts(String serialNumber, Map<String, String[]> errorCodeMap) {
		
		serialNumber = serialNumber.trim();
		
		if (serialNumber.equals("")) {
			errorCodeMap.put("dealerAPI.warrantyRegistration.partSerialNumberMandatory", null);
		}
	}

	private void validatePartNumber(InventoryItem part, Map<String, String[]> errorCodeMap) {
		
		if (part.getOfType() == null || part.getOfType().getNumber() == null || part.getOfType().getNumber().equals("")) {			
			errorCodeMap.put("dealerAPI.warrantyRegistration.partNumberMandatory", new String[]{part.getSerialNumber()});
		}

	}

	private void validateInstallationDate(InventoryItem machine,InventoryItem part, Map<String, String[]> errorCodeMap) {
		
		CalendarDate partInstallationDate = part.getInstallationDate();
		
		if (partInstallationDate != null) {
			if (part.getInstallationDate().isAfter(Clock.today())) {
				errorCodeMap.put("dealerAPI.warrantyRegistration.partInstallationDateCannotBeInFuture", new String[]{part.getSerialNumber()});				
			}
			/*if(machine.getBuiltOn()!=null && part.getInstallationDate().isBefore(machine.getBuiltOn()))
					{
				errorCodeMap.put("dealerAPI.warrantyRegistration.partInstallationDateCannotBeAfterBuildDate", new String[]{part.getSerialNumber()});	
					}*/
		} else {
			errorCodeMap.put("dealerAPI.warrantyRegistration.installationDateNotEntered", new String[]{part.getSerialNumber()});
		}	
	}

	private boolean validatePart(InventoryItem part, InventoryItem machine, Map<String, String[]> errorCodeMap) {
		
		try {
			List<InventoryItem> parts = new ArrayList<InventoryItem>();
			for (InventoryItemComposition composition : machine.getComposedOf()) {
				parts.add(composition.getPart());
			}
			if(part.getId()==null)
			{
			getInventoryService().findItemBySerialNumberAndModelNumber(part.getSerialNumber(), part.getOfType());
			errorCodeMap.put("dealerAPI.warrantyRegistration.invalidPart", new String[]{part.getSerialNumber()});			
			}
			if (!parts.contains(part)) {
				errorCodeMap.put("dealerAPI.warrantyRegistration.invalidPart", new String[]{part.getSerialNumber()});
			}
			return true;

		} catch (Exception e) {
			logger.error("Error in validate part", e);
			return true;
		}
	}

	public boolean isLoggedInUserADealer() {
		return !getLoggedInUser().isInternalUser();
	}

	public ServiceProvider getLoggedInUsersDealership() {
		Organization organization = getLoggedInUser().getBelongsToOrganization();
		return (organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ? 
				new HibernateCast<ServiceProvider>().cast(organization) : null;
	}

	public ServiceProvider getForDealer() {

		ServiceProvider forDealer = null;

		if (isLoggedInUserADealer()) {
			forDealer = getLoggedInUsersDealership();
		}

		return forDealer;
	}

	public List<RegisteredPolicy> listApplicablePolicy(Warranty warranty) {

		List<RegisteredPolicy> availablePolicies = fetchAvailablePolicies(warranty.getForItem(), warranty
				.getCustomerType(), warranty.getForDealer(), false);
		
		List<RegisteredPolicy> extendedPolicies = fetchPurchasedExtendedPolicies(warranty.getForItem());
		for (RegisteredPolicy regPolicy : extendedPolicies) {
			if (!availablePolicies.contains(regPolicy))
				availablePolicies.add(regPolicy);
		}
		return availablePolicies;
	}

	public List<RegisteredPolicy> fetchPurchasedExtendedPolicies(InventoryItem inventoryItem) {
		List<PolicyDefinition> extendedWarranty = new ArrayList<PolicyDefinition>();
		List<RegisteredPolicy> registeredPolicy = new ArrayList<RegisteredPolicy>();
		List<ExtendedWarrantyNotification> extnWrntyList = warrantyService
				.findAllStagedExtnWntyPurchaseNotificationForInv(inventoryItem);
		for (ExtendedWarrantyNotification extendedWarrantyNotification : extnWrntyList) {
			extendedWarranty.add(extendedWarrantyNotification.getPolicy());
		}
		try {			
			registeredPolicy = createPolicies(getForDealer(),extendedWarranty, inventoryItem, false);			
		} catch (PolicyException pe) {			
			logger.error("Error in fetch purchased extended policies", pe);
		}
		return registeredPolicy;
	}	
	
	public List<RegisteredPolicy> fetchAvailablePolicies(InventoryItem inventoryItem, String addressBookType,
			ServiceProvider installingDealer, boolean computePrice) {
		try {

			
			List<PolicyDefinition> policyDefinitions = this.policyService.findPoliciesAvailableForRegistration(
					inventoryItem, addressBookType, installingDealer);

			// Remove the invalid Policies for BlackListedDealers
			if (isLoggedInUserADealer() && !isLoggedInUserAnAdmin() && !isLoggedInUserAnInvAdmin()) {
				for (Iterator<PolicyDefinition> policyIterator = policyDefinitions.iterator(); policyIterator.hasNext();) {
					PolicyDefinition policyDefinition = policyIterator.next();
					if (policyDefinition.isServiceProviderBlackListed(getLoggedInUsersDealership())) {
						policyIterator.remove();
					}
				}
			}
			List<RegisteredPolicy> availablePolicies = createPolicies(installingDealer,policyDefinitions, inventoryItem, computePrice);
			Collections.sort(availablePolicies);
			return availablePolicies;
		} catch (PolicyException pex) {
			logger.error("Error fetching policies available for InventoryItem[slNo:" + inventoryItem.getSerialNumber()
					+ "]", pex);			
			return null;
		}
	}	

	private boolean isLoggedInUserAnInvAdmin() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(), "inventoryAdmin");
	}

	private boolean isLoggedInUserAnAdmin() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(), "admin");
	}
	
	public List<RegisteredPolicy> createPolicies(ServiceProvider forDealer,List<PolicyDefinition> withDefinitions, InventoryItem forItem,
			boolean computePrice) throws PolicyException {
		List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
		for (PolicyDefinition definition : withDefinitions) {
			String policyName = definition.getCode();
			if ((policyName == null) || (policyName.trim().equals(""))) {
				continue; // ignore this policy definition
			}
			RegisteredPolicy policy = new RegisteredPolicy();
			policy.setPolicyDefinition(definition);
			policy.setWarrantyPeriod(definition.warrantyPeriodFor(forItem));

			if (computePrice) {
				computePriceForPolicy(forDealer,forItem, policy);
			}
			policies.add(policy);
		}
		if (policies.size() > 1) {
			Collections.sort(policies, new Comparator<RegisteredPolicy>() {
				public int compare(RegisteredPolicy p1, RegisteredPolicy p2) {
					if (p1.getPolicyDefinition().isAvailableByDefault()) {
						if (p2.getPolicyDefinition().isAvailableByDefault()) {
							return 0;
						} else {
							return -1;
						}
					} else {
						if (p2.getPolicyDefinition().isAvailableByDefault()) {
							return 1;
						} else {
							return 0;
						}
					}
				}
			});
		}
		return policies;
	}

	public void computePriceForPolicy(ServiceProvider forDealer,InventoryItem forItem, RegisteredPolicy policy) {
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
		DealerCriterion dealerCriterion = new DealerCriterion();
		if(forDealer ==null)
		forDealer = getForDealer();
		dealerCriterion.setDealer(forDealer);	
		criteria.setDealerCriterion(dealerCriterion);
		criteria.setProductType(forItem.getOfType().getProduct());
		criteria.setWarrantyRegistrationType(WarrantyRegistrationType.REGISTRATION);
		Money price = this.policyService.getPolicyFeeForPolicyDefinition(policy.getPolicyDefinition(), criteria,
				forItem.getDeliveryDate());
		policy.setPrice(price);
	}
		
	public String checkForInventoryAlreadyRetailedAndPendingWarranty(InventoryItem inventoryItem, Map<String, String[]> errorCodeMap) {
		
		if (inventoryItem != null && (inventoryItem.isRetailed() || inventoryItem.getPendingWarranty())) {
			if(inventoryItem.getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
				errorCodeMap.put("error.inventory.drsavedasdraft", new String[]{inventoryItem.getSerialNumber()});
			} else {
				errorCodeMap.put("dealerAPI.warrantyRegistration.inventoryAlreadyFilledOnWarranty", new String[]{inventoryItem.getSerialNumber()});
			}
			return AdminConstants.NONE;
		}		
		return AdminConstants.SUCCESS;
	}
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    /**
     * This method performs D2D transaction.
     * canPerformD2D is true only when the call is from InstallBaseSync
     * @param inventoryItem
     * @param serviceProvider
     * @param canPerformD2D
     * @return
     */
	public InventoryItem performD2D(InventoryItem inventoryItem, Organization serviceProvider, boolean canPerformD2D) {
		if (this.canPerformD2D() || canPerformD2D) {
			InventoryTransaction newTransaction = new InventoryTransaction();
			newTransaction.setTransactionOrder(new Long(inventoryItem.getTransactionHistory().size() + 1));
			Collections.sort(inventoryItem.getTransactionHistory());
			newTransaction.setTransactedItem(inventoryItem);
			inventoryItem.getTransactionHistory().get(0).setStatus(BaseDomain.INACTIVE);
			newTransaction.setSeller(inventoryItem.getTransactionHistory().get(0).getBuyer());
			newTransaction.setOwnerShip(serviceProvider);
			newTransaction.setBuyer(serviceProvider);
			newTransaction.setInvTransactionType(invTransactionService
					.getTransactionTypeByName(InvTransationType.DEALER_TO_DEALER.getTransactionType()));
			newTransaction.setTransactionDate(Clock.today());
			newTransaction.setStatus(BaseDomain.ACTIVE);
			inventoryItem.getTransactionHistory().add(newTransaction);
			inventoryItem.setLatestBuyer(serviceProvider);
			inventoryItem.setCurrentOwner(serviceProvider);
		}
			return inventoryItem;		
	}
	
	public List<User> getSalesPersonsForDealer(InventoryItem invitem, ServiceProvider serviceProvider){
		ServiceProvider dealerShip = getLoggedInUsersDealership();
		//TODO: need to handle internal, national users. Logic is not complete. Need to revisit
		if (getLoggedInUser().isInternalUser()) {
			
		}
		if (getLoggedInUsersDealership()!= null && getLoggedInUsersDealership().isEnterpriseDealer()) {
            Set<Long> dealerIds = new TreeSet<Long> ();
            if(!getLoggedInUsersDealership().getChildDealersIds().isEmpty()){
            	dealerIds.addAll(getLoggedInUsersDealership().getChildDealersIds());
            }
            else{
            	dealerIds.add(getLoggedInUsersDealership().getId());
            }
            return userRepository.findAssociatedUsersForDealers(dealerIds, "salesPerson");
            
        }else if(dealerShip != null) {
             return userRepository.findSalesPersonForDealer(dealerShip.getId());
         }
		 else if(invitem != null) {
             return userRepository.findSalesPersonForDealer(invitem.getOwner().getId());
         }
		 else if (serviceProvider != null) {
             return userRepository.findSalesPersonForDealer(serviceProvider.getId());
         }
		else {
             return userRepository.findSalesPersonForDealer(getLoggedInUser().getId());
         }
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    public Address getDealersAddress(Long serviceProviderId) {
        Address address = null;
        if (serviceProviderId != null)
                        address = this.orgService.getPartyById(serviceProviderId).getAddress();
        return address;
}

    
	@SuppressWarnings("unused")
	public String populateExistingCustomerType(Warranty warranty,
			Long transactionId, Map<Object, Object> customerTypes,
			String addressBookType) {
		Customer customer = null;
		if (warranty.getCustomer() != null
				&& InstanceOfUtil.isInstanceOfClass(Customer.class,
						warranty.getCustomer())) {
			if (warranty.getCustomerType().equals(
					AddressBookType.ENDCUSTOMER.getType())
					|| (isCustomerDetailsNeededForDR_Rental() && warranty
							.getCustomerType().equals(
									AddressBookType.DEALERRENTAL.getType())) || warranty.getCustomerType().equals(
											AddressBookType.DEMO.getType())) { // the customers for dealer rental is the same as End Customer,SLMSPROD-1174, demo should work in the same way as that of End customer
			customer = customerService.findCustomerById(warranty.getCustomer()
					.getId());
			}
		}
		else if(warranty.getCustomer() != null
				&& InstanceOfUtil.isInstanceOfClass(NationalAccount.class,
						warranty.getCustomer())){
			return AddressBookType.NATIONALACCOUNT
			.getType();
		}
	    else if(warranty.getCustomer() != null
				&& InstanceOfUtil.isInstanceOfClass(Dealership.class,
						warranty.getCustomer())){
	    	/*if(warranty.getCustomerType().equals("Demo"))
	    		return AddressBookType.DEMO.getType();*/
	    	/*else*/ if((!isCustomerDetailsNeededForDR_Rental() && warranty
					.getCustomerType().equals(
							AddressBookType.DEALERRENTAL.getType()))){
	    		return AddressBookType.DEALERRENTAL.getType();
	    	}
	   
		}
		if (warranty == null && transactionId != null) {
			warranty = this.warrantyService.findByTransactionId(transactionId);
		}
		if (addressBookService != null && warranty != null && customer != null
				&& customer.getAddresses() != null) {
			List<AddressBookAddressMapping> addressBookAddressMappings = addressBookService
					.getAddressBookAddressMappingByListOfAddresses(
							customer.getAddresses());

			for (AddressBookAddressMapping addressBookAddressMapping : addressBookAddressMappings) {
				if (AddressBookType.SELF.getType().equalsIgnoreCase(
						addressBookAddressMapping.getAddressBook().getType()
								.getType())) {

					if (customerTypes.containsValue(AddressBookType.DEALER
							.getType())) {
						return AddressBookType.DEALER.getType();
					} else if (customerTypes
							.containsValue(AddressBookType.DEALERRENTAL
									.getType())) {
						return AddressBookType.DEALERRENTAL.getType();
					} else if (customerTypes
							.containsValue(AddressBookType.DEMO
									.getType())) {
						return AddressBookType.DEMO.getType();
					}
				} else {
					if((isCustomerDetailsNeededForDR_Rental() && warranty
							.getCustomerType().equals(
									AddressBookType.DEALERRENTAL.getType()))){
						return AddressBookType.DEALERRENTAL.getType();
					} else if (warranty.getCustomerType().equals(AddressBookType.DEMO.getType())) {
						return AddressBookType.DEMO.getType();
					}
					else{
						return addressBookAddressMapping.getAddressBook().getType()
								.getType();
					}
					
				}
				return addressBookType;

			}

		}
		// in case of National/Government Account
		if (addressBookType == null && addressBookService != null
				&& warranty != null && customer != null
				&& customer.getAddresses() != null) {
			List<AddressBookAddressMapping> addressBookAddressMappings = addressBookService
					.getAddressBookAddressMappingByOrganizationAndListOfAddresses(
							customer.getAddresses(),
							organizationRepository.findByName("OEM"));

			for (AddressBookAddressMapping addressBookAddressMapping : addressBookAddressMappings) {
				if (AddressBookType.SELF.getType().equalsIgnoreCase(
						addressBookAddressMapping.getAddressBook().getType()
								.getType())) {

					if (customerTypes.containsValue(AddressBookType.DEALER
							.getType())) {
						return AddressBookType.DEALER.getType();
					} else if (customerTypes
							.containsValue(AddressBookType.DEALERRENTAL
									.getType())) {
						return AddressBookType.DEALERRENTAL.getType();
					} else if (customerTypes
							.containsValue(AddressBookType.DEMO
									.getType())) {
						return AddressBookType.DEMO.getType();
					}
				} else {

					return addressBookAddressMapping.getAddressBook().getType()
							.getType();
				}
				return addressBookType;
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
		addressForTransfer.setPhone(address.getPhone());
		addressForTransfer.setEmail(address.getEmail());
		addressForTransfer.setSecondaryPhone(address.getSecondaryPhone());
		addressForTransfer.setContactPersonName(address
						.getContactPersonName());
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
	
	public boolean isCustomerDetailsNeededForDR_Rental(){
		return this.configParamService
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
	}

	public void validateAdditionalComponents(InventoryItem inventoryItem,
			Map<String, String[]> errorCodeMap) {
		List<InventoryItemAdditionalComponents> invAddComposition = inventoryItem.getAdditionalComponents();
		StringBuffer descForPartNumber  = new StringBuffer();
		StringBuffer numberForDesc = new StringBuffer();
		boolean isDescBlank = false;
		boolean isPartNumberBlank = false;
		if (invAddComposition != null && invAddComposition.size() > 0) {
			invAddComposition.removeAll(Collections.singleton(null));
			inventoryItem.getAdditionalComponents().removeAll(Collections.singleton(null));
			for (InventoryItemAdditionalComponents part : invAddComposition) {
				if(!StringUtils.hasText(part.getType())){
					errorCodeMap.put("dealerAPI.warrantyRegistration.typeMandatory",null);
				}
				if(!StringUtils.hasText(part.getSubType())){
					errorCodeMap.put("dealerAPI.warrantyRegistration.subTypeMandatory",null);
				}
				if (StringUtils.hasText(part.getPartNumber()) && !StringUtils.hasText(part.getPartDescription())) {
					descForPartNumber.append(part.getPartNumber());
					descForPartNumber.append(",");
					isDescBlank = true;
				}
				if (!StringUtils.hasText(part.getPartNumber()) && StringUtils.hasText(part.getPartDescription())) {
					numberForDesc.append(part.getPartDescription());
					numberForDesc.append(",");
					isPartNumberBlank = true;
				}
			}

			if(isDescBlank){
				descForPartNumber.deleteCharAt(descForPartNumber.length()-1);
				errorCodeMap.put("dealerAPI.warrantyRegistration.partDescription", new String[]{descForPartNumber.toString()});
			}
			if(isPartNumberBlank){
				numberForDesc.deleteCharAt(numberForDesc.length()-1);
				errorCodeMap.put("dealerAPI.warrantyRegistration.partNumber", new String[]{numberForDesc.toString()});
			}
		}
	}

	public void validateInstallType(InventoryItem inventoryItem,
			MarketingInformation marketingInformation,
			Map<String, String[]> errorCodeMap) {
		if (!inventoryItem.getPreOrderBooking()
				&& marketingInformation.getContractCode() != null
				&& !marketingInformation.getContractCode().getContractCode()
						.equalsIgnoreCase("DEMO")) {
			errorCodeMap.put("error.preOrderBooking.warrantyNotAllowed",
					new String[] { inventoryItem.getSerialNumber() });
		}
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}
}
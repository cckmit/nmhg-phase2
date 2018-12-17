package tavant.twms.web.registerMajorComponent;

import java.util.ArrayList;

import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.inventory.ComponentAuditHistory;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.warranty.MajorCompRegUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.warranty.BaseManageWarrantyAction;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class RegisterMajorComponentAction extends BaseManageWarrantyAction implements Preparable, Validateable {

	private static Logger logger = LogManager.getLogger(RegisterMajorComponentAction.class);
	private String addressBookTypeForNonCertifiedInstaller;
	private String addressBookTypeForEndCustomer;
	private InventoryService inventoryService;
	private CatalogService catalogService;
	private AddressBookService addressBookService;
	private PolicyService policyService;
	private WarrantyService warrantyService;
	private InventoryTransactionService invTransactionService;
	private WarrantyCoverageRequestService warrantyCoverageRequestService;
	private InventoryItem inventoryItem;
	private InventoryItem majorComponent;
	private AddressForTransfer addressForTransferToNonCertifiedInstaller;
	private AddressForTransfer addressForTransferToendCustomer;
	private Address nonCertifiedInstallerAddress;
	private Address endCustomerAddress;
	private Organization dealerOrganization;
	private Customer nonCertifiedInstaller;
	private Customer endCustomer;
	private ServiceProvider certifiedDealer;
	private ServiceProvider forDealer;
	private boolean certifiedInstaller = true;
	private boolean standAloneParts;
	private boolean confirmMajorComRegistration;
	private boolean reductionInCoverage;
	private boolean isDealer = false;
	private boolean dealerNameSelected = true;
	private Long inventoryItemId;
	private Long majorCompInvItemId;
	private MajorCompRegUtil majorCompRegUtil;
	private String sequenceNumber;
	private List<ComponentAuditHistory> componentAuditList = new ArrayList<ComponentAuditHistory>();
	
	private List<RegisteredPolicy> availablePolicies = new ArrayList<RegisteredPolicy>();
	private InventoryItemComposition inventoryItemComposition;

	public void prepare() throws Exception {
		populateCustomerTypes();
		if (isLoggedInUserADealer()){
			isDealer = true;
			setForDealer(getLoggedInUsersDealership());
		}
		else if (forDealer != null)
			setForDealer(this.forDealer);
	}

	public String listUnitSerialNumbers() {
		List<InventoryItem> items = new ArrayList<InventoryItem>();
		if (StringUtils.hasText(getSearchPrefix())) {
			items = inventoryService.findAllRetailMachinesBySerialNumber(getSearchPrefix().toUpperCase(), 0, 10);
		} else {
			return generateAndWriteEmptyComboboxJson();
		}
		return generateAndWriteComboboxJsonForSerialNumber(items, "id", "serialNumber");
	}

	public String getOwnerInformation() {
		if (inventoryItem != null && inventoryItem.getId() != null)
			inventoryItem = this.inventoryService.findInventoryItem(inventoryItem.getId());
		return SUCCESS;
	}

	public String getCertifiedServiceProviderNames() {
		if (StringUtils.hasText(getSearchPrefix())) {
			try {
				List<ServiceProvider> serviceProviders = orgService.findCertifiedDealersWhoseNameStartsWith(
						getSearchPrefix(), 0, 10);
				return generateAndWriteComboboxJson(serviceProviders, "id", "name");
			} catch (Exception e) {
				logger.error("Error while generating JSON", e);
				throw new RuntimeException("Error while generating JSON", e);
			}
		} else {
			return generateAndWriteEmptyComboboxJson();
		}
	}

	public String showNonCertifiedInstaller() {
		addressForTransferToNonCertifiedInstaller = new AddressForTransfer();
		addressForTransferToNonCertifiedInstaller.setContactPersonName(nonCertifiedInstallerAddress
				.getContactPersonName());
		addressForTransferToNonCertifiedInstaller.setAddressLine(nonCertifiedInstallerAddress.getAddressLine1());
		addressForTransferToNonCertifiedInstaller.setAddressLine2(nonCertifiedInstallerAddress.getAddressLine2());
		addressForTransferToNonCertifiedInstaller.setAddressLine3(nonCertifiedInstallerAddress.getAddressLine3());
		addressForTransferToNonCertifiedInstaller.setCity(nonCertifiedInstallerAddress.getCity());
		addressForTransferToNonCertifiedInstaller.setState(nonCertifiedInstallerAddress.getState());
		addressForTransferToNonCertifiedInstaller.setCountry(nonCertifiedInstallerAddress.getCountry());
		addressForTransferToNonCertifiedInstaller.setZipCode(nonCertifiedInstallerAddress.getZipCode());
		addressForTransferToNonCertifiedInstaller.setPhone(nonCertifiedInstallerAddress.getPhone());
		addressForTransferToNonCertifiedInstaller.setEmail(nonCertifiedInstallerAddress.getEmail());
		addressForTransferToNonCertifiedInstaller.setSecondaryPhone(nonCertifiedInstallerAddress.getSecondaryPhone());

		AddressBookAddressMapping addressBookAddressMapping = addressBookService
				.getAddressBookAddressMappingByAddress(nonCertifiedInstallerAddress);
		if (addressBookAddressMapping != null) {
			addressForTransferToNonCertifiedInstaller.setType(addressBookAddressMapping.getType());
		}
		return SUCCESS;
	}

	public String showEndCustomerForMajorComponent() {
		addressForTransferToendCustomer = new AddressForTransfer();
		addressForTransferToendCustomer.setContactPersonName(endCustomerAddress.getContactPersonName());
		addressForTransferToendCustomer.setAddressLine(endCustomerAddress.getAddressLine1());
		addressForTransferToendCustomer.setAddressLine2(endCustomerAddress.getAddressLine2());
		addressForTransferToendCustomer.setAddressLine3(endCustomerAddress.getAddressLine3());
		addressForTransferToendCustomer.setCity(endCustomerAddress.getCity());
		addressForTransferToendCustomer.setState(endCustomerAddress.getState());
		addressForTransferToendCustomer.setCountry(endCustomerAddress.getCountry());
		addressForTransferToendCustomer.setZipCode(endCustomerAddress.getZipCode());
		addressForTransferToendCustomer.setPhone(endCustomerAddress.getPhone());
		addressForTransferToendCustomer.setEmail(endCustomerAddress.getEmail());
		addressForTransferToendCustomer.setSecondaryPhone(endCustomerAddress.getSecondaryPhone());
		addressForTransferToendCustomer.setCustomerContactTitle(endCustomerAddress.getCustomerContactTitle());
		addressForTransferToendCustomer.setCounty(endCustomerAddress.getCounty());
		if(endCustomerAddress.getCountyCodeWithName()!=null){
			addressForTransferToendCustomer.setCountyCodeWithName(endCustomerAddress.getCountyCodeWithName());
		}
		else if(endCustomerAddress.getState()!=null && endCustomerAddress.getCounty()!=null)
		{
			String countyName = msaService.findCountyNameByStateAndCode(endCustomerAddress.getState(),endCustomerAddress.getCounty());
			addressForTransferToendCustomer.setCountyCodeWithName(endCustomerAddress.getCounty()+"-"+countyName);
		}		
		AddressBookAddressMapping addressBookAddressMapping = addressBookService
				.getAddressBookAddressMappingByAddress(endCustomerAddress);
		if (addressBookAddressMapping != null) {
			addressForTransferToendCustomer.setType(addressBookAddressMapping.getType());
		}
		return SUCCESS;
	}

	public String confirmMajorCompRegistration() {

		this.availablePolicies =this.majorCompRegUtil.fetchAvailablePoliciesForMajorComp(this.majorComponent,this.addressBookTypeForNonCertifiedInstaller,this.certifiedDealer,this.forDealer);
		/*
		 * if(this.availablePolicies == null ||
		 * this.availablePolicies.isEmpty()){
		 * addActionError("error.transferPlan.noPlan"); return INPUT; }
		 */
		this.confirmMajorComRegistration = true;
		return SUCCESS;
	}

	public String getPolicyFeeTotalForMajorComp() {
		Double policyFeeSum = 0D;
		String currencySymbol = this.forDealer.getPreferredCurrency().getCurrencyCode();
		for (RegisteredPolicy selectedPolicy : this.availablePolicies) {
			policyFeeSum += selectedPolicy.getPrice().breachEncapsulationOfAmount().doubleValue();
		}
		return currencySymbol + " " + policyFeeSum;
	}

	public String registerMajorComponent() {
		if (SelectedBusinessUnitsHolder.getSelectedBusinessUnit() == null) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(majorComponent.getBusinessUnitInfo().getName());
		}
		this.majorCompRegUtil.createInventoryItemsCompositionAndTransaction(majorComponent, inventoryItem, this.forDealer,
				this.endCustomer);
		AddressBookType addressBookType = this.majorCompRegUtil
				.getAddressBookType(this.addressBookTypeForNonCertifiedInstaller);
		reductionInCoverage = this.majorCompRegUtil.createWarranty(majorComponent, inventoryItem, certifiedDealer,
				nonCertifiedInstaller, addressBookType, endCustomer,this.forDealer);
		if (reductionInCoverage) {
			addActionMessage("message.majorComponent.reducedCoverageInfo");
		}
		addActionMessage("message.majorComponent.majorComponentwarrantyRegisteredForItem", majorComponent
				.getSerialNumber());
		return SUCCESS;
	}

	@Override
	public void validate() {
		if (majorComponent.getSerialNumber() == null || majorComponent.getSerialNumber().trim().equals(""))
			addActionError("error.majorComponent.serialNumberMandatory");
		else if (majorComponent.getOfType() != null && majorComponent.getOfType().getNumber() != null) {
			checkInventoryItemWithSNoAndPartNumberCombinationExits(majorComponent);
		}
		if (majorComponent.getOfType() == null || majorComponent.getOfType().getNumber() == null
				|| majorComponent.getOfType().getNumber().equals(""))
			addActionError("error.majorComponent.partNumberMandatory");
		if (majorComponent.getDeliveryDate() == null) {
			addActionError("error.majorComponent.deliveryDateNotFound");
		} else {
			if (majorComponent.getDeliveryDate().isAfter(Clock.today()))
				addActionError("error.majorComponent.deliveryDateCannotBeInFuture");
			if(inventoryItem != null){
				if(majorComponent.getDeliveryDate().isBefore(inventoryItem.getShipmentDate()))
					addActionError("error.deliveryDateBeforeShipment", new String[]{inventoryItem.getSerialNumber()});
				if(majorComponent.getDeliveryDate().isBefore(inventoryItem.getDeliveryDate()))
					addActionError("error.majorComponent.deliveryDateBeforeDeliveryDateUnit",new String[]{inventoryItem.getSerialNumber()});					
			}
		}
		if (certifiedInstaller) {
			if (certifiedDealer.getId() == null) {
				addActionError("error.majorComponent.selectCertifiedInstaller");
			}
		} else {
			if (validateCustomerType(this.addressBookTypeForNonCertifiedInstaller))
				addActionError("error.majorComponent.addNonCertifiedCustomerType");
			if (validateCustomer(this.nonCertifiedInstaller))
				addActionError("error.majorComponent.addNonCertifiedCustomerInfo");
		}
		if (inventoryItem == null) {
			if (validateCustomerType(this.addressBookTypeForEndCustomer))
				addActionError("error.majorComponent.addEndCustomerType");
			if (validateCustomer(this.endCustomer))
				addActionError("error.majorComponent.addEndCustomerInfo");
		}
		if (getLoggedInUser().hasRole(Role.ADMIN) || getLoggedInUser().hasRole(Role.PROCESSOR)) {
			if (forDealer.getId() == null) {
				addActionError("error.majorComponent.addDealer");
			}
		}
	}

	private boolean validateCustomerType(String addressBookType) {
		if (addressBookType == null || "--Select--".equalsIgnoreCase(addressBookType) || addressBookType.equals("")) {
			return true;
		}
		return false;
	}

	private boolean validateCustomer(Customer customer) {
		if (customer == null) {
			return true;
		}
		return false;
	}

	private boolean checkInventoryItemWithSNoAndPartNumberCombinationExits(InventoryItem majorComponent) {
		InventoryItem majorComp = new InventoryItem();
		try {
			majorComp = inventoryService.findItemBySerialNumberAndModelNumber(majorComponent.getSerialNumber(),
					majorComponent.getOfType());
		} catch (ItemNotFoundException e) {
			return false;
		}
		if (majorComp != null) {
			addActionError("error.majorComponent.majorCompWithSNoAndPartNumberCombinationExits");
			return true;
		} else
			return false;
	}

	public String showReplacedPartsHistoryForMajorComp() {
		inventoryItemComposition = inventoryService.findInvItemCompForInvItemAndInvItemComposition(inventoryItemId,
				majorCompInvItemId);
		return SUCCESS;
	}

	public String showAuditHistoryForMajorComponent() {
		componentAuditList = inventoryService.findComponentAuditForInventoryAndSequenceNumber(inventoryItem, sequenceNumber);
		for(ComponentAuditHistory componentAuditHistory:componentAuditList){
			if("N".equalsIgnoreCase(componentAuditHistory.getTransactionType())){
				componentAuditHistory.setTransactionType("New");
			}else if("C".equalsIgnoreCase(componentAuditHistory.getTransactionType())){
				componentAuditHistory.setTransactionType("Change");
			}else if("D".equalsIgnoreCase(componentAuditHistory.getTransactionType())){
				componentAuditHistory.setTransactionType("Delete");
			}
		}
		return SUCCESS;
	}
	
	public String updateMajorCompFromEditPage() {
		if (majorComponent.getSerialNumber() == null || majorComponent.getSerialNumber().trim().equals("")) {
			addActionError("error.majorComponent.serialNumberMandatory");
			return INPUT;
		} else if (checkInventoryItemWithSNoAndPartNumberCombinationExits(this.majorComponent)) {
			return INPUT;
		} else {
			inventoryService.updateInventoryItem(majorComponent);
			addActionMessage("message.majorComponent.updateInventoryItem", majorComponent.getSerialNumber());
			return SUCCESS;
		}
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public AddressForTransfer getAddressForTransferToNonCertifiedInstaller() {
		return addressForTransferToNonCertifiedInstaller;
	}

	public void setAddressForTransferToNonCertifiedInstaller(
			AddressForTransfer addressForTransferToNonCertifiedInstaller) {
		this.addressForTransferToNonCertifiedInstaller = addressForTransferToNonCertifiedInstaller;
	}

	public AddressForTransfer getAddressForTransferToendCustomer() {
		return addressForTransferToendCustomer;
	}

	public void setAddressForTransferToendCustomer(AddressForTransfer addressForTransferToendCustomer) {
		this.addressForTransferToendCustomer = addressForTransferToendCustomer;
	}

	public Address getNonCertifiedInstallerAddress() {
		return nonCertifiedInstallerAddress;
	}

	public void setNonCertifiedInstallerAddress(Address nonCertifiedInstallerAddress) {
		this.nonCertifiedInstallerAddress = nonCertifiedInstallerAddress;
	}

	public AddressBookService getAddressBookService() {
		return addressBookService;
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public Organization getDealerOrganization() {
		return dealerOrganization;
	}

	public void setDealerOrganization(Organization dealerOrganization) {
		this.dealerOrganization = dealerOrganization;
	}

	public boolean isCertifiedInstaller() {
		return certifiedInstaller;
	}

	public void setCertifiedInstaller(boolean certifiedInstaller) {
		this.certifiedInstaller = certifiedInstaller;
	}

	public boolean isConfirmMajorComRegistration() {
		return confirmMajorComRegistration;
	}

	public void setConfirmMajorComRegistration(boolean confirmMajorComRegistration) {
		this.confirmMajorComRegistration = confirmMajorComRegistration;
	}

	public Customer getNonCertifiedInstaller() {
		return nonCertifiedInstaller;
	}

	public void setNonCertifiedInstaller(Customer nonCertifiedInstaller) {
		this.nonCertifiedInstaller = nonCertifiedInstaller;
	}

	public ServiceProvider getCertifiedDealer() {
		return certifiedDealer;
	}

	public void setCertifiedDealer(ServiceProvider certifiedDealer) {
		this.certifiedDealer = certifiedDealer;
	}

	public String getAddressBookTypeForNonCertifiedInstaller() {
		return addressBookTypeForNonCertifiedInstaller;
	}

	public void setAddressBookTypeForNonCertifiedInstaller(String addressBookTypeForNonCertifiedInstaller) {
		this.addressBookTypeForNonCertifiedInstaller = addressBookTypeForNonCertifiedInstaller;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public List<RegisteredPolicy> getAvailablePolicies() {
		return availablePolicies;
	}

	public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
		this.availablePolicies = availablePolicies;
	}

	public InventoryItem getMajorComponent() {
		return majorComponent;
	}

	public void setMajorComponent(InventoryItem majorComponent) {
		this.majorComponent = majorComponent;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public Address getEndCustomerAddress() {
		return endCustomerAddress;
	}

	public void setEndCustomerAddress(Address endCustomerAddress) {
		this.endCustomerAddress = endCustomerAddress;
	}

	public Customer getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(Customer endCustomer) {
		this.endCustomer = endCustomer;
	}

	public String getAddressBookTypeForEndCustomer() {
		return addressBookTypeForEndCustomer;
	}

	public void setAddressBookTypeForEndCustomer(String addressBookTypeForEndCustomer) {
		this.addressBookTypeForEndCustomer = addressBookTypeForEndCustomer;
	}

	public ServiceProvider getForDealer() {
		return forDealer;
	}

	public void setForDealer(ServiceProvider forDealer) {
		this.forDealer = forDealer;
	}

	public Long getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Long inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public InventoryItemComposition getInventoryItemComposition() {
		return inventoryItemComposition;
	}

	public void setInventoryItemComposition(InventoryItemComposition inventoryItemComposition) {
		this.inventoryItemComposition = inventoryItemComposition;
	}

	public Long getMajorCompInvItemId() {
		return majorCompInvItemId;
	}

	public void setMajorCompInvItemId(Long majorCompInvItemId) {
		this.majorCompInvItemId = majorCompInvItemId;
	}

	public WarrantyCoverageRequestService getWarrantyCoverageRequestService() {
		return warrantyCoverageRequestService;
	}

	public void setWarrantyCoverageRequestService(WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public boolean isReductionInCoverage() {
		return reductionInCoverage;
	}

	public void setReductionInCoverage(boolean reductionInCoverage) {
		this.reductionInCoverage = reductionInCoverage;
	}
	
	public void setMajorCompRegUtil(MajorCompRegUtil majorCompRegUtil) {
		this.majorCompRegUtil = majorCompRegUtil;
	}

	public boolean isDealer() {
		return isDealer;
	}

	public void setDealer(boolean isDealer) {
		this.isDealer = isDealer;
	}

	public boolean isDealerNameSelected() {
		return dealerNameSelected;
	}

	public void setDealerNameSelected(boolean dealerNameSelected) {
		this.dealerNameSelected = dealerNameSelected;
	}

	public boolean isStandAloneParts() {
		return standAloneParts;
	}

	public void setStandAloneParts(boolean standAloneParts) {
		this.standAloneParts = standAloneParts;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public List<ComponentAuditHistory> getComponentAuditList() {
		return componentAuditList;
	}

	public void setComponentAuditList(List<ComponentAuditHistory> componentAuditList) {
		this.componentAuditList = componentAuditList;
	}

}

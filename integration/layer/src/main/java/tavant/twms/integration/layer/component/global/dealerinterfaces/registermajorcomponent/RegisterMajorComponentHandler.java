package tavant.twms.integration.layer.component.global.dealerinterfaces.registermajorcomponent;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.warranty.MajorCompRegUtil;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.registermajorcomponent.RegisterMajorComponentTransformer;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import com.domainlanguage.timeutil.Clock;
import com.tavant.dealerinterfaces.majorcomponentregistration.majorcomponentregistrationrequest.MajorComponentRegistrationDocument;
import com.tavant.dealerinterfaces.majorcomponentregistration.majorcomponentregistrationrequest.MajorComponentRegistrationDocument.MajorComponentRegistration;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO.WarrantyRegistrationResponse;

public class RegisterMajorComponentHandler {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	private InventoryService inventoryService;

	private ApplicationSettingsHolder applicationSettings;

	private CatalogService catalogService;

	Map<String, String[]> errorCodeMap;

	private RegisterMajorComponentTransformer registerMajorComponentTransformer;

	private MajorCompRegUtil majorCompRegUtil;

	private OrgService orgService;

	private CustomerService customerService;

	private SecurityHelper securityHelper;

	private InventoryItem majorComponent = null;

	private InventoryItem unit = null;

	private Customer customer = null;

	private ServiceProvider certifiedInstaller = null;

	private Customer nonCertifiedInstaller = null;

	private AddressBookType addBookTypeForNonCertInst = null;

	private AddressBookType addBookTypeForEndCust = null;

	private WarrantyService warrantyService;

	private InventoryTransactionService invTransactionService;

	private WarrantyCoverageRequestService warrantyCoverageRequestService;

	public void registerMajorComponent(MajorComponentRegistrationDocument majorComponentRegistrationDocDTO,
			WarrantyRegistrationResponseDocumentDTO majorComponentRegistrationResDocDTO) {

		MajorComponentRegistration majorComponentRegistrationDTO = majorComponentRegistrationDocDTO
				.getMajorComponentRegistration();
		errorCodeMap = new HashMap<String, String[]>();
		majorComponent = new InventoryItem();
		unit = new InventoryItem();
		String businessUnitName = majorComponentRegistrationDTO.getBUName().toString();
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnitName);
		dealerInterfaceErrorConstants.getI18nDomainTextReader().setLoggedInUserLocale(businessUnitName);
		Map<String, String[]> resultMap = isValidMajorComponent(majorComponentRegistrationDTO);
		validateDeliveryDate(majorComponentRegistrationDTO, errorCodeMap);
		resultMap.putAll(errorCodeMap);
		if (resultMap.isEmpty()) {
			resultMap = isValidUnitOrCustomer(majorComponentRegistrationDTO);
			if (resultMap.isEmpty()) {
				resultMap = isValidInstaller(majorComponentRegistrationDTO);
			} else {
				this.registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationResDocDTO,
						resultMap, null);
			}
			WarrantyRegistrationResponse majorComponentResponse = WarrantyRegistrationResponse.Factory.newInstance();
			if (resultMap.isEmpty()) {
				this.majorCompRegUtil.createInventoryItemsCompositionAndTransaction(majorComponent, unit,
						this.majorCompRegUtil.getLoggedInUsersDealership(), this.customer);
				this.majorCompRegUtil.createWarranty(majorComponent, this.unit, this.certifiedInstaller,
						this.nonCertifiedInstaller, this.addBookTypeForNonCertInst, this.customer,
						this.majorCompRegUtil.getLoggedInUsersDealership());
				majorComponentResponse.setStatus(dealerInterfaceErrorConstants
						.getPropertyMessage(DealerInterfaceErrorConstants.SUCESS));
				majorComponentResponse.setTWMSURL(getApplicationSettings().getExternalUrl());
				majorComponentRegistrationResDocDTO.setWarrantyRegistrationResponse(majorComponentResponse);

			} else {
				this.registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationResDocDTO,
						resultMap, null);
			}

		} else {
			this.registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationResDocDTO, resultMap,
					null);
		}
	}

	private Map<String, String[]> isValidMajorComponent(MajorComponentRegistration majorComponentRegistrationDTO) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		String majorComponentSerialNumber = majorComponentRegistrationDTO.getMajorComponentSerialNumber();
		String majorComponentPartNumber = majorComponentRegistrationDTO.getMajorComponentItemNumber();
		Item item = getItembyItemNumberAndPurpose(majorComponentPartNumber);
		if (item == null) {
			errorCodeMap.put("error.majorComponent.PartNumberInvalidForMC", new String[] { majorComponentPartNumber });
		}
		if (errorCodeMap.isEmpty()) {
			majorComponent.setSerialNumber(majorComponentSerialNumber);
			majorComponent.setOfType(item);
			if (majorCompRegUtil.checkInventoryItemWithSNoAndPartNumberCombinationExits(majorComponent)) {
				errorCodeMap.put("error.majorComponent.majorCompWithSNoAndPartNumberCombinationExits", null);
			}
		}
		return errorCodeMap;
	}

	private void validateDeliveryDate(MajorComponentRegistration majorComponentRegistrationDTO,
			Map<String, String[]> errorCodeMap) {

		if (CalendarUtil.convertToCalendarDate(majorComponentRegistrationDTO.getDateOfDelivery())
				.isAfter(Clock.today())) {
			errorCodeMap.put("error.majorComponent.deliveryDateCannotBeInFuture", null);
		} else {
			majorComponent.setDeliveryDate(CalendarUtil.convertToCalendarDate(majorComponentRegistrationDTO
					.getDateOfDelivery()));
		}
	}

	private Map<String, String[]> isValidInstaller(MajorComponentRegistration majorComponentRegistrationDTO) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		this.certifiedInstaller = null;
		this.addBookTypeForNonCertInst = null;
		this.nonCertifiedInstaller = null;
		String certifiedInstallerName = majorComponentRegistrationDTO.getCertifiedInstallerName() != null ? majorComponentRegistrationDTO
				.getCertifiedInstallerName()
				: "";
		String nonCertifiedInstallerType = majorComponentRegistrationDTO.getNonCertifiedInstallerType() != null ? majorComponentRegistrationDTO
				.getNonCertifiedInstallerType()
				: "";
		String nonCertifiedInstalledId = majorComponentRegistrationDTO.getNonCertifiedInstallerId() != null ? majorComponentRegistrationDTO
				.getNonCertifiedInstallerId()
				: "";
		if (certifiedInstallerName != "")
			this.certifiedInstaller = getCertifiedInstaller(certifiedInstallerName);
		if (certifiedInstallerName.equals("") && nonCertifiedInstallerType != "") {
			this.addBookTypeForNonCertInst = this.majorCompRegUtil.getAddressBookType(nonCertifiedInstallerType);
			if (addBookTypeForNonCertInst == null) {
				errorCodeMap.put("error.majorComponent.invalidAddBookTypeForInst", null);
			} else {
				this.nonCertifiedInstaller = getCustomer(nonCertifiedInstalledId, addBookTypeForNonCertInst);
				if (this.nonCertifiedInstaller == null)
					errorCodeMap.put("error.majorComponent.invalidNonCertInst", null);
			}
		} else if (!certifiedInstallerName.equals("") && this.certifiedInstaller == null) {
			errorCodeMap.put("error.majorComponent.invalidCertifiedInstaller", null);
		}
		if ((this.certifiedInstaller != null && this.nonCertifiedInstaller != null)
				|| (this.certifiedInstaller == null && this.nonCertifiedInstaller == null)) {
			errorCodeMap.put("error.majorComponent.enterCertOrNonCertInst", null);
		}
		return errorCodeMap;
	}

	private Map<String, String[]> isValidUnitOrCustomer(MajorComponentRegistration majorComponentRegistrationDTO) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		this.unit = null;
		this.addBookTypeForEndCust = null;
		this.customer = null;
		String unitSerialNumber = majorComponentRegistrationDTO.getInventorySerialNumber() != null ? majorComponentRegistrationDTO
				.getInventorySerialNumber()
				: "";
		String unitItemNumber = majorComponentRegistrationDTO.getInventoryItemNumber() != null ? majorComponentRegistrationDTO
				.getInventoryItemNumber()
				: "";
		String customerType = majorComponentRegistrationDTO.getCustomerType() != null ? majorComponentRegistrationDTO
				.getCustomerType() : "";
		String customerId = majorComponentRegistrationDTO.getCustomerID() != null ? majorComponentRegistrationDTO
				.getCustomerID() : "";
		Item item = getItemByItemNumber(unitItemNumber);
		if (item != null && unitSerialNumber != "")
			this.unit = getInventoryItemBySerialNumberAndModelNumber(unitSerialNumber, item);
		if (unitSerialNumber.equals("") && customerType != "") {
			this.addBookTypeForEndCust = this.majorCompRegUtil.getAddressBookType(customerType);
			if (addBookTypeForEndCust == null) {
				errorCodeMap.put("error.majorComponent.invalidAddBookTypeForCust", null);
			} else {
				this.customer = getCustomer(customerId, addBookTypeForEndCust);
				if (this.customer == null)
					errorCodeMap.put("error.majorComponent.invalidCustomer", null);
			}
		} else if (!unitSerialNumber.equals("") && this.unit == null) {
			errorCodeMap.put("error.majorComponent.invalidUnitSerialNumber", null);
		}
		if ((this.unit != null && this.customer != null) || (this.unit == null && customer == null)) {
			errorCodeMap.put("error.majorComponent.enterUnitOrCustomer", null);
		}
		return errorCodeMap;
	}

	private ServiceProvider getCertifiedInstaller(String certifiedInstallerNumber) {
		ServiceProvider serviceProvider = null;
		if (StringUtils.isNotBlank(certifiedInstallerNumber)) {
			serviceProvider = orgService.findCertifiedDealerByNumber(certifiedInstallerNumber);
		}
		return serviceProvider;
	}

	private Customer getCustomer(String customerId, AddressBookType addressBookType) {
		Customer customer = null;

		if (StringUtils.isNotBlank(customerId) && addressBookType != null) {
			customer = customerService.findCustomerByCustomerIdAndDealer(customerId, securityHelper.getLoggedInUser()
					.getBelongsToOrganization(), addressBookType);
		}
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	private Item getItemByItemNumber(String itemNumber) {
		Item item = null;
		try {
			if (StringUtils.isNotBlank(itemNumber)) {
				item = catalogService.findItemByItemNumberOwnedByManuf(itemNumber);
			}
		} catch (CatalogException ce) {
			logger.error("Error in getItemByItemNumber", ce);
		}
		return item;
	}

	private Item getItembyItemNumberAndPurpose(String itemNumber) {

		Item item = null;
		try {
			if (StringUtils.isNotBlank(itemNumber)) {
				item = catalogService.findItemByItemNumberAndPurposeOwnedByServiceProvider(
						AdminConstants.WARRANTY_COVERAGE_PURPOSE, itemNumber, securityHelper.getOEMOrganization()
								.getId());
			}
		} catch (CatalogException ce) {
			logger.error("Error in getItemByItemNumber", ce);
		}
		return item;

	}

	private InventoryItem getInventoryItemBySerialNumberAndModelNumber(String serialNumber, Item item) {
		InventoryItem inventoryItem = null;

		if (StringUtils.isNotBlank(serialNumber) && item != null) {
			try {
				inventoryItem = inventoryService.findItemBySerialNumberAndModelNumberAndType(serialNumber, item,
						InventoryType.RETAIL);
			} catch (ItemNotFoundException e) {
				logger.error("Error fetching Inventory by Serial and Model Number", e);
			}
		}
		return inventoryItem;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public void setRegisterMajorComponentTransformer(RegisterMajorComponentTransformer registerMajorComponentTransformer) {
		this.registerMajorComponentTransformer = registerMajorComponentTransformer;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setDealerInterfaceErrorConstants(DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public void setMajorCompRegUtil(MajorCompRegUtil majorCompRegUtil) {
		this.majorCompRegUtil = majorCompRegUtil;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
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

	public WarrantyCoverageRequestService getWarrantyCoverageRequestService() {
		return warrantyCoverageRequestService;
	}

	public void setWarrantyCoverageRequestService(WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

}

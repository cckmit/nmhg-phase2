package tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitwarrantyregistration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.Oem;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationRepository;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.AdditionalMarketingInfo;
import tavant.twms.domain.policy.AdditionalMarketingInfoOptions;
import tavant.twms.domain.policy.AdditionalMarketingInfoService;
import tavant.twms.domain.policy.AdditionalMarketingInfoType;
import tavant.twms.domain.policy.CompetitionType;
import tavant.twms.domain.policy.CompetitorMake;
import tavant.twms.domain.policy.CompetitorModel;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.MarketType;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.SelectedAdditionalMarketingInfo;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.warranty.MajorCompRegUtil;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitservicehistory.UnitServiceHistoryTransformer;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.time.CalendarDate;
import com.tavant.dealerinterfaces.warrantyregistration.request.EachAttribute;
import com.tavant.dealerinterfaces.warrantyregistration.request.EachMajorComponent;
import com.tavant.dealerinterfaces.warrantyregistration.request.MajorComponentsType;
import com.tavant.dealerinterfaces.warrantyregistration.request.ProductMarketInformationType;
import com.tavant.dealerinterfaces.warrantyregistration.request.UnitWarrantyRegistrationRequestDocument;
import com.tavant.dealerinterfaces.warrantyregistration.request.UnitWarrantyRegistrationRequestDocument.UnitWarrantyRegistrationRequest;

import tavant.twms.domain.common.I18nDomainTextReader;

public class UnitWarrantyRegistrationTransformer {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	// for finding customers and operators
	private CustomerService customerService;

	// for finding Party(installing dealer)
	private OrgService orgService;

	// for inventory item service
	private InventoryService inventoryService;

	// for logged user
	private SecurityHelper securityHelper;

	// for lov repository (OEM object type)
	private LovRepository lovRepository;

	// for config service (list of customer types)

	private ConfigParamService configParamService;

	// for items
	private CatalogService catalogService;

	// warranty object
	private Warranty warranty;

	// WarrantyUtil
	private WarrantyUtil warrantyUtil;

	private ServiceProvider forDealer;

	private UnitServiceHistoryTransformer unitServiceHistoryTransformer;

	private InventoryItemRepository inventoryItemRepository;

	private AdditionalMarketingInfoService additionalMarketingInfoService;

	private OrganizationRepository organizationRepository;

	private MajorCompRegUtil majorCompRegUtil;

	private I18nDomainTextReader i18nDomainTextReader;

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public final MajorCompRegUtil getMajorCompRegUtil() {
		return majorCompRegUtil;
	}

	public final void setMajorCompRegUtil(MajorCompRegUtil majorCompRegUtil) {
		this.majorCompRegUtil = majorCompRegUtil;
	}

	public final OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	public final void setOrganizationRepository(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public final InventoryItemRepository getInventoryItemRepository() {
		return inventoryItemRepository;
	}

	public final void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	public final UnitServiceHistoryTransformer getUnitServiceHistoryTransformer() {
		return unitServiceHistoryTransformer;
	}

	public final void setUnitServiceHistoryTransformer(UnitServiceHistoryTransformer unitServiceHistoryTransformer) {
		this.unitServiceHistoryTransformer = unitServiceHistoryTransformer;
	}

	// inventoryTransactionService
	private InventoryTransactionService invTransactionService;

	// for fetching Competitor Model, Competitor Make, Competition Type, Market
	// Type
	private WarrantyService warrantyService;

	public WarrantyService getWarrantyService() {
		return this.warrantyService;
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

	public WarrantyUtil getWarrantyUtil() {
		return warrantyUtil;
	}

	public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
		this.warrantyUtil = warrantyUtil;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
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

	public final AdditionalMarketingInfoService getAdditionalMarketingInfoService() {
		return additionalMarketingInfoService;
	}

	public final void setAdditionalMarketingInfoService(AdditionalMarketingInfoService additionalMarketingInfoService) {
		this.additionalMarketingInfoService = additionalMarketingInfoService;
	}

	public UnitWarrantyRegistrationRequestDocument convertXMLtoRequestDTO(String soapInputXML) throws XmlException {

		UnitWarrantyRegistrationRequestDocument unitWarrantyRegistrationRequestDocument = null;

		if (!StringUtils.isBlank(soapInputXML)) {

			try {

				unitWarrantyRegistrationRequestDocument = UnitWarrantyRegistrationRequestDocument.Factory
						.parse(soapInputXML);
				
				// Create an XmlOptions instance and set the error listener.
				XmlOptions validateOptions = new XmlOptions();
				ArrayList errorList = new ArrayList();
				validateOptions.setErrorListener(errorList);
				
				if(!unitWarrantyRegistrationRequestDocument.validate(validateOptions)) {
				
					XmlError error = (XmlError) errorList.get(0);					
					throw new XmlException(error.getMessage());
				}				
			} catch (XmlException xe) {
				logger.error("Error in XML parsing or validation", xe);
				throw xe;
			}
		} else {
			throw new RuntimeException("input XML is empty");
		}

		return unitWarrantyRegistrationRequestDocument;
	}

	public Warranty validateAndPopulateWarrantyObject(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			Map<String, String[]> errorCodeMap) {

		InventoryItem inventoryItem = getInventoryItem(unitWarrantyRegistrationRequest);

		if (inventoryItem == null) {
			errorCodeMap.put("dealerAPI.warrantyRegistration.invalidItemSerialNoAndItemNo", null);
			return null;
		}

		// checking for already retailed and for pending warranty
		String msg = warrantyUtil.checkForInventoryAlreadyRetailedAndPendingWarranty(inventoryItem, errorCodeMap);
		if ("none".equalsIgnoreCase(msg)) {
			return null;
		}

		if (inventoryItem.getCurrentOwner().getId() != warrantyUtil.getLoggedInUser().getBelongsToOrganization()
				.getId()) {
			// Inventory item is not owned by the dealer, hence look up the BU
			// configuration
			if (!unitServiceHistoryTransformer.canViewStockInventoryItem(inventoryItem)) {
				errorCodeMap.put("dealerAPI.warrantyRegistration.inventoryNotInDealersStock", null);
				return null;
			}
		}

		// creating warranty object		
		warranty = new Warranty();

		// installing dealer
		ServiceProvider installingDealer = setInstallingDealer(unitWarrantyRegistrationRequest, errorCodeMap);

		if (installingDealer != null) {

			// setting inventory item
			warranty.setInventoryItem(inventoryItem);

			// setting hours in service
			Long hoursOnMachine = unitWarrantyRegistrationRequest.getHoursInService().longValue();
			inventoryItem.setHoursOnMachine(hoursOnMachine);

			// setting delivery and installation dates
			setDeliveryDateAndInstallationDate(unitWarrantyRegistrationRequest, inventoryItem, errorCodeMap);

			if (!errorCodeMap.isEmpty())
				return null;

			// setting major components
			setInventoryMajorComponents(unitWarrantyRegistrationRequest, errorCodeMap);

			setWarrantyInfo(unitWarrantyRegistrationRequest, inventoryItem, errorCodeMap);

			// draft status
			boolean isDraft = unitWarrantyRegistrationRequest.getForceToDraft();
			setDraftStatus(isDraft);

			// setting forDelaer and filedBy
			forDealer = warrantyUtil.getForDealer();
			warranty.setForDealer(forDealer);
			warranty.setFiledBy(warrantyUtil.getLoggedInUser());

			// fetching list of available customer types to BU parameter
			Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
					.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_DR.getName());

			// customer Type
			String customerType = unitWarrantyRegistrationRequest.getCustomerType();
			AddressBookType addressBookType = setCustomerTypeOrOperatorType(customerType, keyValueOfCustomerTypes,
					errorCodeMap, true);

			// customer Id
			String customerId = unitWarrantyRegistrationRequest.getCustomerID();

			if (addressBookType == AddressBookType.ENDCUSTOMER) {
				setCustomerOrOperator(customerId, (Organization) forDealer, addressBookType, errorCodeMap, true);
			} else {
				Organization organization = organizationRepository.findByName("OEM");
				setCustomerOrOperator(customerId, organization, addressBookType, errorCodeMap, true);
			}

			// operator Type
			String operatorType = unitWarrantyRegistrationRequest.getOperatorType();
			if (operatorType != null && StringUtils.isNotBlank(operatorType)) {
				addressBookType = setCustomerTypeOrOperatorType(operatorType, keyValueOfCustomerTypes, errorCodeMap,
						false);
			}

			// operator Id
			String operatorId = unitWarrantyRegistrationRequest.getOperatorId();
			if (operatorId != null && StringUtils.isNotBlank(operatorId)) {
				if (addressBookType == AddressBookType.ENDCUSTOMER) {
					setCustomerOrOperator(operatorId, (Organization) forDealer, addressBookType, errorCodeMap,
							false);
				} else {
					Organization organization = organizationRepository.findByName("OEM");
					setCustomerOrOperator(operatorId, organization, addressBookType, errorCodeMap, false);
				}
			}

			// address for transfer
			setAddressForTransfer();

			// marketing information
			warranty.setMarketingInformation(getMarketingInfo(unitWarrantyRegistrationRequest, errorCodeMap));

			// registration comments
			warranty.setRegistrationComments(unitWarrantyRegistrationRequest.getComments());

			// product marketing info
			validateAndsetProductMarketingInfo(unitWarrantyRegistrationRequest, errorCodeMap);

			// setting policy
			List<RegisteredPolicy> availablePolicies = this.warrantyUtil.fetchAvailablePolicies(warranty.getForItem(),
					warranty.getCustomerType(), warranty.getForDealer(), true);

			if (availablePolicies.size() >= 1) {
				WarrantyAudit warrantyAudit = new WarrantyAudit();
				warrantyAudit.setSelectedPolicies(availablePolicies);
				warrantyAudit.setExternalComments(warranty.getRegistrationComments());
				warranty.getWarrantyAudits().add(warrantyAudit);
			} else {
				errorCodeMap.put("dealerAPI.warrantyRegistration.applicablePoliciesNotAvialable",
						new String[] { warranty.getForItem().getSerialNumber() });
			}
		}
		warranty.getForItem().setLatestWarranty(warranty);
		return warranty;
	}

	private void validateAndsetProductMarketingInfo(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			Map<String, String[]> errorCodeMap) {

		boolean isProductMarketingInfoValid = false;

		ProductMarketInformationType productMarketInformationType = unitWarrantyRegistrationRequest
				.getProductMarketInformation();

		if (productMarketInformationType != null) {
			EachAttribute[] attributes = productMarketInformationType.getEachAttributeArray();

			if (attributes.length > 0) {

				ItemGroup itemGroup = warranty.getForItem().getOfType().getProduct();

				List<AdditionalMarketingInfo> additionalMarketingInfoList = additionalMarketingInfoService
						.getAdditionalMarketingInfoByAppProduct(itemGroup);

				for (EachAttribute eachAttribute : attributes) {

					String attributeName = eachAttribute.getAttributeName();
					String attributeValue = eachAttribute.getAttributeValue();
					if (StringUtils.isNotBlank(attributeName) && StringUtils.isNotBlank(attributeValue)) {
						boolean isAttributeMatched = false;
						for (AdditionalMarketingInfo additionalMarketingInfo : additionalMarketingInfoList) {

							// checking attribute name in list
							if (attributeName.equals(additionalMarketingInfo.getFieldName())) {
								isAttributeMatched = true;
								isProductMarketingInfoValid = false;

								if (additionalMarketingInfo.getInfoType() == AdditionalMarketingInfoType.DropDown) {

									List<AdditionalMarketingInfoOptions> additionalMarketingInfoOptionsList = additionalMarketingInfo
											.getOptions();

									for (AdditionalMarketingInfoOptions additionalMarketingInfoOptions : additionalMarketingInfoOptionsList) {

										if (attributeValue.equals(additionalMarketingInfoOptions.getOptionValue())) {
											isProductMarketingInfoValid = true;
											break;
										}
									}

								} else if (additionalMarketingInfo.getInfoType() == AdditionalMarketingInfoType.Number) {

									try {
										Integer.parseInt(attributeValue);
										isProductMarketingInfoValid = true;
									} catch (NumberFormatException nfe) {
										logger.error("Error in checking product attribute value: " + attributeValue);
									}

								} else if (additionalMarketingInfo.getInfoType() == AdditionalMarketingInfoType.FreeText) {
									isProductMarketingInfoValid = true;
								}

								if (isProductMarketingInfoValid) { // checking for product marketing info valid or not
									
									SelectedAdditionalMarketingInfo selectedAdditionalMarketingInfo = new SelectedAdditionalMarketingInfo();
									selectedAdditionalMarketingInfo.setAddtlMarketingInfo(additionalMarketingInfo);
									selectedAdditionalMarketingInfo.setValue(attributeValue);
									selectedAdditionalMarketingInfo.setForWarranty(warranty);

									warranty.getSelectedAddtlMktInfo().add(selectedAdditionalMarketingInfo);
									break;
								}
							}
						}
						if (!isAttributeMatched) {
							errorCodeMap.put("dealerAPI.warrantyRegistration.invalidProductMarketingAttributeInfo",
									new String[] { attributeName });
							break;
						}
						if (isAttributeMatched && !isProductMarketingInfoValid) {
							errorCodeMap.put("dealerAPI.warrantyRegistration.invalidProductMarketingAttributeValue",
									new String[] { attributeName, attributeValue });
							return;
						}
					} // string utils
				}
			}
		}
	}

	private ServiceProvider setInstallingDealer(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			Map<String, String[]> errorCodeMap) {
		ServiceProvider installingDealer = null;

		String dealerNumber = unitWarrantyRegistrationRequest.getInstallingDealer();
		installingDealer = orgService.findDealerByNumber(dealerNumber);

		if (installingDealer != null) {
			warranty.setInstallingDealer(installingDealer);

		} else {
			errorCodeMap.put("dealerAPI.warrantyRegistration.invalidDealerNumber", null);
		}

		return installingDealer;

	}

	private void setAddressForTransfer() {

		// setting for customer
		Party customer = warranty.getCustomer();
		if (customer != null) {
			this.warranty
					.setAddressForTransfer(this.majorCompRegUtil.populateAddressForTransfer(customer.getAddress()));
		}

		// setting for operator
		customer = warranty.getOperator();
		if (customer != null) {
			this.warranty.setOperatorAddressForTransfer(this.majorCompRegUtil.populateAddressForTransfer(customer
					.getAddress()));
		}
	}

	private void validateMajorComponentsAndSet(MajorComponentsType majorComponentType,
			Map<String, String[]> errorCodeMap) {

		InventoryItem inventoryItem = this.warranty.getForItem();

		EachMajorComponent[] majorComponentsArray = majorComponentType.getEachMajorComponentArray();

		boolean isPartNumbersValid = true;
		List<MajorComponent> majorComponents = new ArrayList<MajorComponent>(majorComponentsArray.length);
		for (EachMajorComponent majorComponent : majorComponentsArray) {

			String partItemNumber = majorComponent.getItemNumber();
			String partSerialNumber = majorComponent.getSerialNumber();
			Calendar partInstalledDate = majorComponent.getDateofInstallation();
			Item item = null;
			try {
				
				item = catalogService.findItemWithPurposeWarrantyCoverage("Warranty Coverage", partItemNumber);
				
				if (item == null) {
					errorCodeMap.put("dealerAPI.warrantyRegistration.partItemNoInvalid", new String[] { partItemNumber });
					isPartNumbersValid = false;
					
				} else {
					InventoryItem partInventoryItem = inventoryService.findItemBySerialNumberAndItemNumber(
							partSerialNumber, partItemNumber);

					if (partInventoryItem != null) {
						errorCodeMap.put("dealerAPI.warrantyRegistration.partItemNoAlreadyRetailed",
								new String[] { partSerialNumber, partItemNumber });
						isPartNumbersValid = false;
					}
				}							

			} catch (ItemNotFoundException infe) {

				majorComponents.add(new MajorComponent(partSerialNumber, item, partInstalledDate));
			}
		}
		InventoryItem partItem = null;
		if (isPartNumbersValid) {

			for (MajorComponent majorComponent : majorComponents) {
				partItem = new InventoryItem();

				partItem.setOfType(majorComponent.getItem());
				partItem.setSerialNumber(majorComponent.getSerialNumber());
				partItem.setInstallationDate(CalendarUtil.convertToCalendarDate(majorComponent.getInstallationDate()));

				InventoryItemComposition inventoryItemComposition = new InventoryItemComposition();
				inventoryItemComposition.setPart(partItem);
				inventoryItemComposition.setPartOf(inventoryItem);
				inventoryItem.getComposedOf().add(inventoryItemComposition);

				warrantyUtil.validateMajorComponents(inventoryItem, errorCodeMap);

			}
			majorComponents.clear();
		}
	}


	private InventoryItem getInventoryItem(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest) {
		InventoryItem inventoryItem = null;

		String invSerialNumber = unitWarrantyRegistrationRequest.getInventorySerialNumber();
		String invItemNumber = unitWarrantyRegistrationRequest.getInventoryItemNumber();

		try {

			inventoryItem = inventoryService.findItemBySerialNumberAndItemNumber(invSerialNumber, invItemNumber);

		} catch (ItemNotFoundException enfe) {
			logger.error("Error while retriving inventory item :: Serial NO:" + invSerialNumber + " - Item No: "
					+ invItemNumber);
		}
		return inventoryItem;
	}

	private void setDeliveryDateAndInstallationDate(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			InventoryItem inventoryItem, Map<String, String[]> errorCodeMap) {

		// validate delivery date and installation date
		CalendarDate dateOfDelivery = CalendarUtil.convertToCalendarDate(unitWarrantyRegistrationRequest
				.getDateOfDelivery());
		CalendarDate installationDate = CalendarUtil.convertToCalendarDate(unitWarrantyRegistrationRequest
				.getDateOfInstallation());

		boolean isDateofDeliveryAndDateOfInstallationValid = warrantyUtil.validateDeliveryAndInstallationDate(
				dateOfDelivery, installationDate, inventoryItem, errorCodeMap);

		if (isDateofDeliveryAndDateOfInstallationValid) {

			warranty.setDeliveryDate(dateOfDelivery);
			warranty.getForItem().setDeliveryDate(dateOfDelivery);
			warranty.setInstallationDate(installationDate);
		}
	}

	private void setWarrantyInfo(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			InventoryItem inventoryItem, Map<String, String[]> errorCodeMap) {

		String oemDesc = unitWarrantyRegistrationRequest.getOEM();

		Oem oemObj = getOemObj(oemDesc, "Oem");
		if (oemObj != null) {
			warranty.setOem(oemObj);
		} else if (org.springframework.util.StringUtils.hasText(oemDesc)) {
			errorCodeMap.put("dealerAPI.warrantyRegistration.invalidOEMDescription", null);
		}

		warranty.setOem(oemObj);
		warranty.setEquipmentVIN(unitWarrantyRegistrationRequest.getEquipmentVINID());
		warranty.setFleetNumber(unitWarrantyRegistrationRequest.getTruckTrailerNumber());
	}

	private void setInventoryMajorComponents(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			Map<String, String[]> errorCodeMap) {
		// validating major components and setting
		MajorComponentsType majorComponentType = unitWarrantyRegistrationRequest.getMajorComponents();
		if (majorComponentType != null) {
			validateMajorComponentsAndSet(majorComponentType, errorCodeMap);
		}
	}

	private Oem getOemObj(String oemDesc, String OemType) {

		ListOfValues listOfValue = null;
		if (StringUtils.isNotBlank(oemDesc)) {
			List<ListOfValues> listOfValues = lovRepository.findAllActive(OemType);
			for (ListOfValues lstOfValue : listOfValues) {
				if (oemDesc.equals(lstOfValue.getDescription())) {
					listOfValue = lstOfValue;
					break;
				}
			}
		}
		return (Oem) listOfValue;

	}

	private void setDraftStatus(boolean isDraft) {

		if (isDraft) { // if draft
			this.warranty.setDraft(true);
			this.warranty.setStatus(WarrantyStatus.DRAFT);			
		} else { // submitted

			this.warranty.setDraft(false);
		}
	}

	private AddressBookType setCustomerTypeOrOperatorType(String customerType,
			Map<Object, Object> keyValueOfCustomerTypes, Map<String, String[]> errorCodeMap, boolean isCustomerType) {

		AddressBookType addressBookType = null;

		// checking given customer type is in available list or not
		if (keyValueOfCustomerTypes.containsValue(customerType)) {

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

			// common to both customer type and operator type
			if (isCustomerType) {
				this.warranty.setCustomerType(addressBookType.getType());
			} else {
				this.warranty.setOperatorType(addressBookType.getType());
			}
		} else {

			// setting error code in errorCodesMap
			if (isCustomerType) {
				errorCodeMap.put("dealerAPI.warrantyRegistration.customertypeNotAllowed", null);
			} else {

				errorCodeMap.put("dealerAPI.warrantyRegistration.operatortypeNotAllowed", null);
			}
		}

		return addressBookType;

	}

	private void setCustomerOrOperator(String customerIdOrOperatorId, Organization org,
			AddressBookType addressBookType, Map<String, String[]> errorCodeMap, boolean isCustomerId) {

		Customer customerOrOperator = null;

		if (StringUtils.isNotBlank(customerIdOrOperatorId)) {

			customerOrOperator = customerService.findCustomerByCustomerIdAndDealer(customerIdOrOperatorId, org,
					addressBookType);

			if (customerOrOperator != null) {

				if (isCustomerId) {
					warranty.setCustomer(customerOrOperator);
				} else {
					warranty.setOperator(customerOrOperator);
				}

			} else {
				if (isCustomerId) {
					errorCodeMap.put("dealerAPI.warrantyRegistration.invalidCustomerId", null);
				} else {
					errorCodeMap.put("dealerAPI.warrantyRegistration.invalidOperatorId", null);
				}
			}
		}
	}

	private MarketingInformation getMarketingInfo(UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest,
			Map<String, String[]> errorCodesMap) {

		MarketingInformation marketingInformation = new MarketingInformation();

		// setting transaction type
		String trxType = unitWarrantyRegistrationRequest.getTransactionType().toString();
		setTransactionType(marketingInformation, trxType, errorCodesMap);		
				
		if ("Lease".equals(trxType) || "Long Term Rental".equals(trxType) || "Short Term Rental".equals(trxType)) {
			BigInteger noOfMonths = unitWarrantyRegistrationRequest.getNoOfMonths();
			if (noOfMonths != null) {
				marketingInformation.setMonths(noOfMonths.intValue());
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.noOfMonthsRequired", new String[]{trxType});
			}

			BigInteger noOfYears = unitWarrantyRegistrationRequest.getNoOfYears();
			if (noOfYears != null) {
				marketingInformation.setYears(noOfYears.intValue());
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.noOfYearsRequired", new String[]{trxType});
			}
		}

		// customer first time owner
		boolean customerFirstTimeOwner = unitWarrantyRegistrationRequest.getFirstTimeOwner();
		marketingInformation.setCustomerFirstTimeOwner(customerFirstTimeOwner);

		// setting marketing type
		String marketType = unitWarrantyRegistrationRequest.getMarketType();
		setMarketType(marketingInformation, marketType, errorCodesMap, customerFirstTimeOwner);

		if (!customerFirstTimeOwner) {

			String modelNo = unitWarrantyRegistrationRequest.getModelNo();
			setCompetitorModel(marketingInformation, modelNo, errorCodesMap);

			String competitionType = unitWarrantyRegistrationRequest.getCompetitionType();
			setCompetitionType(marketingInformation, competitionType, errorCodesMap);

			String competitorMake = unitWarrantyRegistrationRequest.getCompetitorMake();
			setCompetitorMake(marketingInformation, competitorMake, errorCodesMap);

			String ifPreviousOwner = unitWarrantyRegistrationRequest.getIfPreviousOwner();
			setIfPreviousOwner(marketingInformation, ifPreviousOwner, errorCodesMap);

		}

		return marketingInformation;
	}

	private void setIfPreviousOwner(MarketingInformation marketingInformation, String ifPreviousOwner,
			Map<String, String[]> errorCodesMap) {

		if (StringUtils.isNotBlank(ifPreviousOwner)) {

			String unknownOrNotProvided = i18nDomainTextReader
					.getProperty("label.wntyreg.prevowner.Unknown/NotProvided");
			String switchingToBU = i18nDomainTextReader.getProperty("label.wntyreg.prevowner.switch");
			String continuingWithBU = i18nDomainTextReader.getProperty("label.wntyreg.prevowner.continue");

			if (unknownOrNotProvided.equals(ifPreviousOwner)) {
				marketingInformation.setIfPreviousOwner(ifPreviousOwner);

			} else if (switchingToBU.equals(ifPreviousOwner)) {
				marketingInformation.setIfPreviousOwner(ifPreviousOwner);

			} else if (continuingWithBU.equals(ifPreviousOwner)) {
				marketingInformation.setIfPreviousOwner(ifPreviousOwner);

			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidIfPreviousOwner",
						new String[] { ifPreviousOwner });
			}

		} else {
			errorCodesMap
					.put("dealerAPI.warrantyRegistration.invalidIfPreviousOwner", new String[] { ifPreviousOwner });
		}

	}

	private void setTransactionType(MarketingInformation marketingInformation, String trxType,
			Map<String, String[]> errorCodesMap) {
		if (StringUtils.isNotBlank(trxType)) {

			TransactionType transactionType = warrantyService.findTransactionType(trxType);

			if (transactionType != null) {
				marketingInformation.setTransactionType(transactionType);
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidTransactionType", null);
			}
		} else {
			errorCodesMap.put("dealerAPI.warrantyRegistration.invalidTransactionType", null);
		}
	}

	private void setMarketType(MarketingInformation marketingInformation, String marketType,
			Map<String, String[]> errorCodesMap, boolean customerFirstTimeOwner) {
		if (StringUtils.isNotBlank(marketType)) {

			MarketType marketTypeObj = warrantyService.findMarketType(marketType);

			if (marketTypeObj != null) {
				marketingInformation.setMarketType(marketTypeObj);
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidMarketType", null);
			}
		} else {

			if (!customerFirstTimeOwner) {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidMarketType", null);
			}
		}

	}

	private void setCompetitorMake(MarketingInformation marketingInformation, String competitorMake,
			Map<String, String[]> errorCodesMap) {
		if (StringUtils.isNotBlank(competitorMake)) {
			CompetitorMake competitorMakeObj = warrantyService.findCompetitorMake(competitorMake);

			if (competitorMakeObj != null) {
				marketingInformation.setCompetitorMake(competitorMakeObj);
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidCompetitorMake", null);
			}
		} else {
			errorCodesMap.put("dealerAPI.warrantyRegistration.invalidCompetitorMake", null);
		}
	}

	private void setCompetitionType(MarketingInformation marketingInformation, String competitionType,
			Map<String, String[]> errorCodesMap) {
		if (StringUtils.isNotBlank(competitionType)) {
			CompetitionType competitionTypeObj = warrantyService.findCompetitionType(competitionType);

			if (competitionTypeObj != null) {
				marketingInformation.setCompetitionType(competitionTypeObj);
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidCompetitionType", null);
			}
		} else {
			errorCodesMap.put("dealerAPI.warrantyRegistration.invalidCompetitionType", null);
		}
	}

	private void setCompetitorModel(MarketingInformation marketingInformation, String modelNo,
			Map<String, String[]> errorCodesMap) {
		if (StringUtils.isNotBlank(modelNo)) {
			CompetitorModel competitorModel = warrantyService.findCompetitorModel(modelNo);
			if (competitorModel != null) {
				marketingInformation.setCompetitorModel(competitorModel);
			} else {
				errorCodesMap.put("dealerAPI.warrantyRegistration.invalidModelNo", null);
			}
		} else {
			errorCodesMap.put("dealerAPI.warrantyRegistration.invalidModelNo", null);
		}
	}
}
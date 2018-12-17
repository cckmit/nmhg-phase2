package tavant.twms.integration.layer.component.global;

import static tavant.twms.integration.layer.util.CalendarUtil.convertToCalendarDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.Components.Component;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.CustomerInfo;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.DieselTierWaiver.Disclaimers;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.DieselTierWaiver.Disclaimers.DisclaimerInformation;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.Options;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.PartGroups;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.SyncType;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.SourceWarehouseService;
import tavant.twms.domain.common.I18NLovText;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.ComponentAuditHistory;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.I18NDisclaimer;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemSource;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.inventory.PartGroup;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBook;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.AddressType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.OwnershipStateRepository;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.component.global.InstallBase.InstallBaseValidator;
import tavant.twms.integration.layer.constants.InstallBaseSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.integration.layer.util.LocaleUtil;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class GlobalInstallBaseSync {

	private static final Logger logger = Logger
			.getLogger(GlobalInstallBaseSync.class.getName());

	private CatalogService catalogService;

	private CustomerService customerService;

	private InventoryService inventoryService;

	private OrgService orgService;

	private InventoryTransactionService invTransactionService;

	private OwnershipStateRepository ownershipStateRepository;

	private TransactionTemplate transactionTemplate;

	private AddressBookService addressBookService;

	private LovRepository lovRepository;

	private SourceWarehouseService sourceWarehouseService;

	private WarrantyUtil warrantyUtil;

	private InstallBaseValidator installBaseValidator;

	private InstallBaseSyncInterfaceErrorConstants installBaseSyncInterfaceErrorConstants;
	
	private ItemGroupService itemGroupService;
	
	private MarketingInformation marketingInformation;
	
	private WarrantyService warrantyService; 
	
	private List<RegisteredPolicy> availablePolicies;
	
	private ConfigParamService configParamService;	
	
	private ServiceProvider installingServiceProvider;
	
	private SecurityHelper securityHelper;
		
	private DealershipRepository dealershipRepository;


	
	public List<SyncResponse> sync(
			final InstallBaseSyncDocumentDTO installBaseSyncDocumentDTO) {
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		final Map<String,String> errorMessageCodes = new HashMap<String,String>();
		InstallBaseSyncDocumentDTO.InstallBaseSync.ApplicationArea applicationArea = installBaseSyncDocumentDTO
				.getInstallBaseSync().getApplicationArea();
		SyncResponse response = new SyncResponse();
		if (installBaseSyncDocumentDTO.getInstallBaseSync().getDataArea()
				.getInventorySync().getInventoryDetail() != null
				&& installBaseSyncDocumentDTO.getInstallBaseSync()
						.getDataArea().getInventorySync().getInventoryDetail()
						.getSerialNumber() != null
				&& !installBaseSyncDocumentDTO.getInstallBaseSync()
						.getDataArea().getInventorySync().getInventoryDetail()
						.getSerialNumber().isEmpty()) {
			response.setBusinessId(installBaseSyncDocumentDTO.getInstallBaseSync()
					.getDataArea().getInventorySync().getInventoryDetail()
					.getSerialNumber().trim());
		}
		String itemNumber=null;
		if (installBaseSyncDocumentDTO.getInstallBaseSync().getDataArea()
				.getInventorySync().getInventoryDetail() != null
				&& installBaseSyncDocumentDTO.getInstallBaseSync()
						.getDataArea().getInventorySync().getInventoryDetail()
						.getItemNumber() != null
				&& !installBaseSyncDocumentDTO.getInstallBaseSync()
						.getDataArea().getInventorySync().getInventoryDetail()
						.getSerialNumber().isEmpty()) {
			 itemNumber=installBaseSyncDocumentDTO.getInstallBaseSync()
					.getDataArea().getInventorySync().getInventoryDetail()
					.getSerialNumber().trim();
			 response.setUniqueIdValue(itemNumber);
		}
		response.setUniqueIdName(IntegrationConstants.INSTALLBASE_UNIQUE_ID);
		response.setBusinessUnitName(setBuName(installBaseSyncDocumentDTO
				.getInstallBaseSync().getDataArea().getInventorySync()
				.getDivisionCode().trim()));
		response.setLogicalId(applicationArea.getSender().getLogicalId());
		response.setTask(applicationArea.getSender().getTask());
		response.setReferenceId(applicationArea.getSender().getReferenceId());
		response.setInterfaceNumber(applicationArea.getInterfaceNumber());
		response.setBodId(applicationArea.getBODId());
		response.setCreationDateTime(Calendar.getInstance());
		installBaseValidator.validateCommonFields(installBaseSyncDocumentDTO
				.getInstallBaseSync().getDataArea().getInventorySync(),
				errorMessageCodes);
		if(!errorMessageCodes.isEmpty()){
	         response = buildErrorResponse(response,null,response.getBusinessId(),
	     			  itemNumber,errorMessageCodes);
		}
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(
						TransactionStatus transactionStatus) {
					syncInventory(installBaseSyncDocumentDTO
							.getInstallBaseSync().getDataArea()
							.getInventorySync(),errorMessageCodes);
				}
			});
			if(!errorMessageCodes.isEmpty()){
				 response = buildErrorResponse(response, null,response.getBusinessId(),
		     			  itemNumber,errorMessageCodes);
            }else{
         	   response.setSuccessful(true);
            }
		} catch(IllegalArgumentException  ex){
			String error = ex.getMessage();
			if(error != null){
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0063, "Xml Validation Error:"+error);
			}
			response = buildErrorResponse(response, ex.getMessage(),response.getBusinessId(),
	     			  itemNumber,errorMessageCodes);
		}catch (RuntimeException e) {
			logger.error("Exception Occurred in GlobalInstallBaseSync!!", e);
			
			if(!errorMessageCodes.isEmpty()){
     		   response.setErrorMessages(errorMessageCodes);
     	   }else{
     		   if(e.getMessage()!=null){
     			   String errorMessage = e.getMessage();
     			   if(errorMessage!=null){
     				   String[] message = errorMessage.split(":");
     				   if(message.length>1){
     					  String messageKey=installBaseSyncInterfaceErrorConstants.getErrorMessage(message[0]);
     					  if(messageKey!=null){
     					   errorMessageCodes.put(message[0],message[1]);
     					  }
     				   }
     			   }
     		   }
     		   response.setErrorMessages(errorMessageCodes);
     	   }
     	   response = buildErrorResponse(response, e.getMessage(),response.getBusinessId(),
     			  itemNumber,errorMessageCodes);
     	   response.setErrorMessages(errorMessageCodes);
        }
		responses.add(response);
		return responses;
	}

	private SyncResponse buildErrorResponse(SyncResponse response,
			String message, String serialNumber, String itemNumber,Map<String,String> errorMessageCodes) {
		response.setSuccessful(false);
		response.setException(new StringBuilder()
				.append(" Error Syncing Install Base, with Serial Number: ")
				.append(serialNumber).append(" and Item Number: ")
				.append(itemNumber).append("\n")
				.append(" The Reason for the Error is : ").append(message)
				.append("\n").append("\n").toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		if (errorMessageCodes.isEmpty()) {
			errorMessageCodes
					.put(InstallBaseSyncInterfaceErrorConstants.I0058,
							installBaseSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0058));
		}
		response.setErrorMessages(errorMessageCodes);
		return response;
	}

	private void syncInventory(InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {
		InventoryItem inventoryItem=null;
		setBuName(inventorySyncTypeDTO.getDivisionCode().trim());
		boolean createInventoryItem = false;
		Item item = null;
		if(errorMessageCodes.isEmpty()){
		item = getItem(inventorySyncTypeDTO.getInventoryDetail()
				.getItemNumber().trim(),errorMessageCodes,IntegrationConstants.MACHINE);
		String serialNumber = String.valueOf((inventorySyncTypeDTO
				.getInventoryDetail().getSerialNumber().trim()));
		try {
			inventoryItem = inventoryService.findItemBySerialNumberAndProduct(
					serialNumber.trim(), inventorySyncTypeDTO.getProductCode());
		} catch (ItemNotFoundException e) {
			if (IntegrationConstants.RMA.equalsIgnoreCase(inventorySyncTypeDTO
					.getSyncType().toString())||IntegrationConstants.TRUE.equalsIgnoreCase(inventorySyncTypeDTO.getComponentSerialUpdated().toString())) {
				String syncType=null;
				if(IntegrationConstants.RMA.equalsIgnoreCase(inventorySyncTypeDTO
					.getSyncType().toString())){
					syncType=IntegrationConstants.RMA;
				}else{
					syncType=IntegrationConstants.COMPONENT_SERIAL_UADATED_INSTALL_BASE;
				}
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0045, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
						InstallBaseSyncInterfaceErrorConstants.I0045,
						new String[]{serialNumber,syncType}));
				
				throw new RuntimeException(
						installBaseSyncInterfaceErrorConstants.getPropertyMessage(
								InstallBaseSyncInterfaceErrorConstants.I0045,
								new String[]{serialNumber,syncType}), e);
			}
			inventoryItem = new InventoryItem();
			createInventoryItem = true;
		}
			if (IntegrationConstants.MACHINE_SALE
					.equalsIgnoreCase(inventorySyncTypeDTO.getSyncType().toString())
					|| IntegrationConstants.RETAILED.equalsIgnoreCase(inventorySyncTypeDTO.getSyncType().toString())) {
				syncMechineSaleInstallBase(item, inventoryItem,	inventorySyncTypeDTO, createInventoryItem,errorMessageCodes);
				if (IntegrationConstants.RETAILED.equalsIgnoreCase(inventorySyncTypeDTO.getSyncType().toString())&&!IntegrationConstants.TRUE.equalsIgnoreCase(inventorySyncTypeDTO.getComponentSerialUpdated().toString())) {
					final Map<String,String[]> errorcodes = new HashMap<String,String[]>();
					autoInstallForGovtTrucks(inventoryItem,inventorySyncTypeDTO,errorcodes);
				}
				return;

			}
			if (createInventoryItem == false
				&& IntegrationConstants.CONSIGNMENT
						.equalsIgnoreCase(inventorySyncTypeDTO.getInventoryDetail()
								.getItemCondition().toString())) {
			setConsignmentItem(inventoryItem,
					inventorySyncTypeDTO.getShipToNumber(),errorMessageCodes);
		} else if (createInventoryItem == false
				&& null != inventoryItem.getCurrentOwner()
				&& inventoryItem.getCurrentOwner().isInterCompany()) {
		
			updateInventoryItemDetails(inventoryItem, inventorySyncTypeDTO,
					item, createInventoryItem,errorMessageCodes);
		} else if (SyncType.RMA.equals(inventorySyncTypeDTO.getSyncType())
				&& createInventoryItem == false) {
			if (!inventoryItem.getType().getType()
					.equalsIgnoreCase(InventoryType.OEM_STOCK.getType())) {
			
				createRMAInventoryTransaction(inventoryItem,
						inventorySyncTypeDTO,errorMessageCodes);
				inventoryService.updateInventoryItem(inventoryItem);
				return ;
			} else if (createInventoryItem == false) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0048, installBaseSyncInterfaceErrorConstants
						.getPropertyMessage(
								InstallBaseSyncInterfaceErrorConstants.I0048,
								new String[]{serialNumber}));
				throw new RuntimeException(
						installBaseSyncInterfaceErrorConstants
								.getPropertyMessage(
										InstallBaseSyncInterfaceErrorConstants.I0048,
										new String[]{serialNumber}));
			}
		}
		else if (createInventoryItem == false) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0047, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0047,
					new String[]{serialNumber}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0047,
							new String[]{serialNumber}));
		}
		}
	}

	private void autoInstallForGovtTrucks(InventoryItem installingItem,
			InventorySync inventorySyncTypeDTO,final Map<String,String[]> errorMessageCodes) {
		marketingInformation=prepareMktInformation(inventorySyncTypeDTO);
		warrantyUtil.validateMajorComponents(installingItem, errorMessageCodes);
		warrantyUtil.validateAdditionalComponents(installingItem, errorMessageCodes);
		 installingServiceProvider = orgService
				.findServiceProviderByNumber(inventorySyncTypeDTO.getInstallingDealerNumber().trim());
		availablePolicies = this.warrantyUtil.fetchAvailablePolicies(installingItem, IntegrationConstants.END_CUSTOMER, installingServiceProvider, false);
		List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(availablePolicies.size());
		for (RegisteredPolicy registeredPolicy : availablePolicies) {
			selectedPolicyDefs.add(registeredPolicy
					.getPolicyDefinition());
		}
		installingItem.setInstallingDealer(installingServiceProvider);
		installingItem.setShipmentDate(convertToCalendarDate(inventorySyncTypeDTO.getDeliveryDateTime()));
		warrantyUtil.createPolicies(installingServiceProvider, selectedPolicyDefs, installingItem, true);
		Warranty warranty = new Warranty();
		warranty.setMarketingInformation(marketingInformation);
		warranty.setInventoryItem(installingItem);
		warranty.setDraft(false);
		warranty.setFiledDate(Clock.today());
		warranty.setMultiDRETRNumber(warrantyService.getWarrantyMultiDRETRNumber());
		setWarrantyAttributes(warranty, installingItem);
		/*if(isDisclaimerAvailable(inventoryItemMapping)){
			if (inventoryItemMapping.isWaiverInformationEditable() && 
					inventoryItemMapping.getDieselTierWaiver()!=null) {
				inventoryItemMapping.getDieselTierWaiver().setInventoryItem(
						inventoryItem);
				inventoryItemMapping.getDieselTierWaiver().setDestinationCountry(
						customer.getAddress().getCountry());
				inventoryItemMapping.getDieselTierWaiver().setCountryEmissionRating(
						inventoryItem.getDieselTier());
				if(inventoryItemMapping.getDieselTierWaiver().getId()==null) {
					try {
						dieselTierWaiverService.save(inventoryItemMapping.getDieselTierWaiver());
						inventoryItemMapping.setDieselTierWaiver(dieselTierWaiverService
								.findLatestDieselTierWaiver(inventoryItem));
					} catch (Exception e) {
						logger.error("Error while saving data - DieselTierWaiver"
								+ inventoryItemMapping.getDieselTierWaiver());
					}
				}
			} 
			inventoryItem.getLatestWarranty().setDieselTierWaiver(inventoryItemMapping.getDieselTierWaiver());
			inventoryItem.setDisclaimerInfo(inventoryItemMapping.getDieselTierWaiver().getDisclaimer());
   		}else{
   			inventoryItem.getLatestWarranty().setDieselTierWaiver(null);
   			inventoryItem.setDisclaimerInfo(null);
   		}*/
		
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
		itemsForTask.add(installingItem);
		warrantyService.createInventoryAndCreateWarrantyReport(itemsForTask, false);			
	
	}	


	private MarketingInformation prepareMktInformation(InventorySync inventorySyncTypeDTO) {
		marketingInformation=new MarketingInformation();
		marketingInformation.setContractCode(warrantyService.findCCode("Sale") );
		marketingInformation.setInternalInstallType(warrantyService.findInternalInstallTypeByName(IntegrationConstants.GOVERNMENT_ACCOUNT));
		marketingInformation.setMaintenanceContract(warrantyService.findMaintenanceContractByName(IntegrationConstants.FULL_SERVICE_CONTRACT));
		marketingInformation.setDealerRepresentative(IntegrationConstants.NA);
		marketingInformation.setCustomerRepresentative(IntegrationConstants.NA);
		marketingInformation.setUlClassification(IntegrationConstants.NA);
		marketingInformation.setIndustryCode(warrantyService.findIndustryCodeByIndustryCode(inventorySyncTypeDTO.getCustomerInfo().getSICode()));		
		return marketingInformation;
	}

	private void setWarrantyAttributes(Warranty warranty,
		InventoryItem  installingItem) {
		
		
		warranty.setInstallationDate(installingItem.getShipmentDate());
		
		if (isAdditionalInformationDetailsApplicable()) {
			warranty.setMarketingInformation(this.marketingInformation);
		}		
		warranty.setRegistrationComments("Auto Install For Govt Trucks");
		warranty.getPolicies().clear();
		WarrantyAudit warrantyAudit = new WarrantyAudit();
		warrantyAudit.setSelectedPolicies(createSelectedPolicies(installingItem));
		warrantyAudit.setExternalComments("Auto Install For Govt Trucks");
		warranty.getWarrantyAudits().add(warrantyAudit);
		warranty.setDeliveryDate(convertToCalendarDate(Calendar.getInstance()));
		
		//todo need to set
//		warranty.setAddressForTransfer(customer.getAddress().);
		Party latestBuyer=installingItem.getLatestBuyer();
		warranty.setCustomer(latestBuyer);
		AddressForTransfer addressForTransfer=new AddressForTransfer();
		Address address=latestBuyer.getAddress();
		addressForTransfer.setAddressLine(address.getAddressLine1());
		addressForTransfer.setAddressLine2(address.getAddressLine2());
		addressForTransfer.setAddressLine3(address.getAddressLine3());
		addressForTransfer.setCountry(address.getCountry());
		addressForTransfer.setState(address.getState());
		addressForTransfer.setCity(address.getCity());
		addressForTransfer.setCounty(address.getCounty());
		addressForTransfer.setEmail(address.getEmail());
		addressForTransfer.setFax(address.getFax());
		addressForTransfer.setPhone(address.getPhone());
		warranty.setAddressForTransfer(addressForTransfer);

		warranty.setCustomerType(IntegrationConstants.END_CUSTOMER);
		Customer customer=customerService.findCustomerById(installingItem.getLatestBuyer().getId());
		warranty.setOperator(customer);
		/*warranty.setOperator(this.operator);
		warranty.setOperatorType(this.addressBookTypeForOperator);
		warranty.setOperatorAddressForTransfer(this.operatorAddressForTransfer);*/
		warranty.setFiledBy(securityHelper.getLoggedInUser());
		warranty.setTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.DR.name()));
		warranty.setForDealer(installingServiceProvider);
		warranty.setFleetNumber(installingItem.getFleetNumber());
		warranty.setInstallingDealer(installingServiceProvider);	
		warranty.getForItem().setLatestWarranty(warranty);
		warranty.getMarketingInformation();
		
	}
	
	private List<RegisteredPolicy> createSelectedPolicies(InventoryItem inventoryItem) {
		List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(availablePolicies.size());
		for (RegisteredPolicy registeredPolicy :availablePolicies ) {
			if (registeredPolicy != null) {
				selectedPolicyDefs.add(registeredPolicy.getPolicyDefinition());
			}
		}
		try {			
				return this.warrantyUtil.createPolicies(installingServiceProvider, selectedPolicyDefs, inventoryItem, true);
							
			} 
		 catch (PolicyException e) {
			//addActionError("error.registeringWarrantyForItem");
			return null;
		}
	}
	
	public boolean isAdditionalInformationDetailsApplicable() {
		return this.configParamService
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}

	
	private void syncMechineSaleInstallBase(Item item,
			InventoryItem inventoryItem, InventorySync inventorySyncTypeDTO,
			boolean createInventoryItem,final Map<String,String> errorMessageCodes) {
		if(IntegrationConstants.RETAILED.equalsIgnoreCase(inventorySyncTypeDTO.getSyncType().toString())&&createInventoryItem&&!(inventorySyncTypeDTO
				.getComponentSerialUpdated()!=null && IntegrationConstants.TRUE
			.equalsIgnoreCase(inventorySyncTypeDTO
					.getComponentSerialUpdated().toString())))	{
			installBaseValidator.validateForRetailedInventory(inventorySyncTypeDTO, createInventoryItem, errorMessageCodes);
		}
		installBaseValidator.validate(inventorySyncTypeDTO,createInventoryItem,errorMessageCodes);
		if(inventorySyncTypeDTO
				.getComponentSerialUpdated()!=null && IntegrationConstants.TRUE
			.equalsIgnoreCase(inventorySyncTypeDTO
					.getComponentSerialUpdated().toString())){
			setComponents(inventorySyncTypeDTO, inventoryItem,createInventoryItem,errorMessageCodes);
		}else{
			if (!createInventoryItem
					&& inventoryItem.getType() != null
					&& inventoryItem.getType().equals(InventoryType.RETAIL)
					&& IntegrationConstants.MACHINE_SALE
							.equalsIgnoreCase(inventorySyncTypeDTO
									.getSyncType().toString())) {
				errorMessageCodes
						.put(
								InstallBaseSyncInterfaceErrorConstants.I0068,
								installBaseSyncInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0068));

				throw new RuntimeException(
						installBaseSyncInterfaceErrorConstants
								.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0068));
			} else {
				merge(inventoryItem, inventorySyncTypeDTO, item,
						createInventoryItem, errorMessageCodes);
			}
		}
		if (createInventoryItem) {
			inventoryService.createInventoryItem(inventoryItem);
			if(inventoryItem.getWaiverDuringDr()!=null){
			inventoryItem.getWaiverDuringDr().setInventoryItem(inventoryItem);
			}
			setComponentAuditHistory(inventorySyncTypeDTO,inventoryItem,createInventoryItem);
			inventoryService.updateInventoryItem(inventoryItem);
		} else {
			if ((inventoryItem.getType().getType()
					.equalsIgnoreCase(InventoryType.OEM_STOCK.getType()) || (inventorySyncTypeDTO
							.getReshipped().toString().isEmpty()||inventorySyncTypeDTO
					.getReshipped().toString()
					.equalsIgnoreCase(IntegrationConstants.TRUE)))||createInventoryItem==false)
				inventoryService.updateInventoryItem(inventoryItem);
			setComponentAuditHistory(inventorySyncTypeDTO,inventoryItem,createInventoryItem);
		}
		
		
	
	}

	private void setComponentAuditHistory(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem,boolean createInventoryItem) {
		if ((inventorySyncTypeDTO.getComponentSerialUpdated() != null && IntegrationConstants.TRUE
				.equalsIgnoreCase(inventorySyncTypeDTO
						.getComponentSerialUpdated().toString()))
				|| createInventoryItem == true) {
		if (inventorySyncTypeDTO.getComponents() != null
				&& inventorySyncTypeDTO.getComponents().getComponentArray() != null
				&& inventorySyncTypeDTO.getComponents().getComponentArray().length > 0) {
			for (InventorySync.Components.Component componentDTO : inventorySyncTypeDTO
					.getComponents().getComponentArray()) {
				ComponentAuditHistory componentAuditHistory = new ComponentAuditHistory();
				componentAuditHistory.setComponentPartNumber(componentDTO
						.getComponentPartNumber());
				componentAuditHistory.setComponentPartSerialNumber(componentDTO
						.getComponentPartSerialNumber());
				componentAuditHistory.setComponentSerialType(componentDTO
						.getComponentSerialType());
				componentAuditHistory.setTransactionType(componentDTO
						.getTransactionType().toString());
				componentAuditHistory.setSequenceNumber(componentDTO
						.getSequenceNumber());
				componentAuditHistory.setInventoryItem(inventoryItem);
				componentAuditHistory.setManufacturer(componentDTO
						.getManufacturer());
				componentAuditHistory.setSerialTypeDescription(componentDTO
						.getSerialTypeDescription());
				if(IntegrationConstants.DELETE_COMPONENT.equalsIgnoreCase(componentDTO.getTransactionType().toString())){
				componentAuditHistory.getD().setActive(false);
			}else{
				componentAuditHistory.getD().setActive(true);
			}
				inventoryItem.includeAuditHystory(inventoryItem,componentAuditHistory);
			}
		}
			
		 }

	}

	private void createRMAInventoryTransaction(InventoryItem inventoryItem,
			InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {
		InventoryTransaction inventoryTransaction = new InventoryTransaction();
		if (inventorySyncTypeDTO.getInvoiceDate() != null) {
			inventoryTransaction
					.setInvoiceDate(convertToCalendarDate(inventorySyncTypeDTO
							.getInvoiceDate()));
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getInvoiceNumber())) {
			inventoryTransaction.setInvoiceNumber(inventorySyncTypeDTO
					.getInvoiceNumber().trim());
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getSalesOrderNumber())) {
			inventoryTransaction.setSalesOrderNumber(inventorySyncTypeDTO
					.getSalesOrderNumber().trim());
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getInvoiceComments())) {
			inventoryTransaction.setInvoiceComments(inventorySyncTypeDTO
					.getInvoiceComments().trim());
		}
		inventoryTransaction.setTransactionDate(Clock.today());
		inventoryTransaction.setTransactionOrder(new Long(inventoryItem
				.getTransactionHistory().size() + 1));
		Organization buyer = orgService
				.findOrganizationByName(IntegrationConstants.OEM);
		if (buyer == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0049, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0049,
					new String[]{IntegrationConstants.OEM}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0049,
							new String[]{IntegrationConstants.OEM}));
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getShipFromNumber())) {
			String serviceProviderNumber = inventorySyncTypeDTO
					.getShipFromNumber().trim();
			ServiceProvider serviceProvider = orgService
					.findServiceProviderByNumber(serviceProviderNumber.trim());
			if (serviceProvider == null) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0046, installBaseSyncInterfaceErrorConstants
						.getPropertyMessage(
								InstallBaseSyncInterfaceErrorConstants.I0046,
								new String[]{serviceProviderNumber}));
				throw new RuntimeException(
						installBaseSyncInterfaceErrorConstants
								.getPropertyMessage(
										InstallBaseSyncInterfaceErrorConstants.I0046,
										new String[]{serviceProviderNumber}));
			}
			setInventoryTransactionItem(
					inventoryTransaction,
					serviceProvider,
					inventoryItem,
					buyer,
					invTransactionService
							.getTransactionTypeByName(IntegrationConstants.RMA),
					IntegrationConstants.RMA);
		} else {
			setInventoryTransactionItem(
					inventoryTransaction,
					inventoryItem.getCurrentOwner(),
					inventoryItem,
					buyer,
					invTransactionService
							.getTransactionTypeByName(IntegrationConstants.RMA),
					IntegrationConstants.RMA);
		}
	}

	private void merge(InventoryItem inventoryItem,
			InventorySync inventorySyncTypeDTO, Item item,boolean createInventoryItem,final Map<String,String> errorMessageCodes) {
		inventoryItem.setSerialNumber(String.valueOf(inventorySyncTypeDTO
				.getInventoryDetail().getSerialNumber().trim()));
		if (inventorySyncTypeDTO.getInventoryDetail().getDieselTier() != null
				&& StringUtils.hasText(inventorySyncTypeDTO
						.getInventoryDetail().getDieselTier().toString())) {
			inventoryItem.setDieselTier(String.valueOf(inventorySyncTypeDTO
					.getInventoryDetail().getDieselTier().toString()));
		}
		inventoryItem.setMarketingGroupCode(String.valueOf(inventorySyncTypeDTO
				.getInventoryDetail().getMktGroupCode().trim()));
		if (inventorySyncTypeDTO.getBillToPurchaseOrder() != null
				&& StringUtils.hasText(inventorySyncTypeDTO
						.getBillToPurchaseOrder())) {
			inventoryItem.setBillToPurchaseOrder(inventorySyncTypeDTO
					.getBillToPurchaseOrder().trim());
		}
		setDateFields(inventorySyncTypeDTO, inventoryItem);
		if (inventorySyncTypeDTO.getDiscAuthorizationNumber() != null
				&& StringUtils.hasText(inventorySyncTypeDTO
						.getDiscAuthorizationNumber())) {
			inventoryItem.setDiscAuthorizationNumber(inventorySyncTypeDTO
					.getDiscAuthorizationNumber().trim());

		}
		inventoryItem.setDiscountPercent(inventorySyncTypeDTO
				.getDiscountPercent());
		if (inventorySyncTypeDTO.getOrderType() != null
				&& StringUtils.hasText(inventorySyncTypeDTO.getOrderType())) {
			inventoryItem.setOrderType(inventorySyncTypeDTO.getOrderType()
					.trim());
		}
		inventoryItem.setMdeCapacity(inventorySyncTypeDTO.getMDECapacity());
		inventoryItem.setModelPower(inventorySyncTypeDTO.getModelPower());

		if (inventorySyncTypeDTO.getBrandType() != null
				&& StringUtils.hasText(inventorySyncTypeDTO.getBrandType()
						.toString())) {
			inventoryItem.setBrandType(inventorySyncTypeDTO.getBrandType()
					.toString());
		}
		// setting invoice date as shipment date
		if (inventorySyncTypeDTO
				.getDeliveryDateTime() != null) {
			inventoryItem
					.setShipmentDate(convertToCalendarDate(inventorySyncTypeDTO
							.getDeliveryDateTime()));
		}
		// Hours On Machine hardcoded to zero
		inventoryItem.setHoursOnMachine(new Long(0));
		inventoryItem.setOfType(item);
		inventoryItem.getOfType().setMake(item.getMake());
		if (inventorySyncTypeDTO.getSyncType() != null) {
			inventoryItem.setType(getInventoryType(inventorySyncTypeDTO
					.getSyncType().toString()));
		}
		if (inventorySyncTypeDTO.getFactoryOrderNumber() != null
				&& StringUtils.hasText(inventorySyncTypeDTO
						.getFactoryOrderNumber())) {
			inventoryItem.setFactoryOrderNumber(inventorySyncTypeDTO
					.getFactoryOrderNumber().trim());
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getInventoryDetail()
				.getManufacturingSite())) {
			setManufacturingSite(inventorySyncTypeDTO, inventoryItem);
		}
		OwnershipState ownershipState = ownershipStateRepository
				.findOwnershipStateByName(OwnershipState.FIRST_OWNER.getName());
		inventoryItem.setOwnershipState(ownershipState);
		if (inventoryItem.getType().getType()
				.equals(InventoryType.RETAIL.getType())) {
			if (inventorySyncTypeDTO.getDeliveryDateTime() != null) {
				inventoryItem
						.setDeliveryDate(convertToCalendarDate(Calendar.getInstance()));
				inventoryItem
						.setRegistrationDate(convertToCalendarDate(inventorySyncTypeDTO
								.getDeliveryDateTime()));
			}
		}
		if (inventorySyncTypeDTO.getInventoryDetail().getBuildDate() != null) {
			inventoryItem.setBuiltOn(convertToCalendarDate(inventorySyncTypeDTO
					.getInventoryDetail().getBuildDate()));
		}
		setSourceWarehouse(inventorySyncTypeDTO.getShipFromOrgCode(),
				inventoryItem);
		setItemConditionType(inventorySyncTypeDTO, inventoryItem);
		if (StringUtils.hasText(inventorySyncTypeDTO.getBillToNumber())) {
			setBillToAddress(inventorySyncTypeDTO, inventoryItem,errorMessageCodes);
		}
		if (inventorySyncTypeDTO.getOperatingUnit() != null) {
			inventoryItem.setOperatingUnit(inventorySyncTypeDTO
					.getOperatingUnit());
		}
		if (inventorySyncTypeDTO.getOrderReceivedDate() != null) {
			inventoryItem.setOrderReceivedDate(inventorySyncTypeDTO
					.getOrderReceivedDate().getTime());
		}
		if (inventorySyncTypeDTO.getActualCTSDate() != null) {
			inventoryItem.setActualCtsDate(inventorySyncTypeDTO
					.getActualCTSDate().getTime());
		}
		setOptionsInformation(inventorySyncTypeDTO.getOptions(), inventoryItem);
		setPartGroupsInformation(inventorySyncTypeDTO.getPartGroups(),
				inventoryItem);
		setComponents(inventorySyncTypeDTO, inventoryItem,createInventoryItem,errorMessageCodes);
		setWaiversForInventory(inventorySyncTypeDTO,
				inventoryItem);
		inventoryItem.setStdwReserveAmountYear1(inventorySyncTypeDTO.getStandardWarrantyReserveAmountYear1());
		inventoryItem.setStdwReserveAmountYear2(inventorySyncTypeDTO.getStandardWarrantyReserveAmountYear2());
		inventoryItem.setAopRate1(inventorySyncTypeDTO.getAOPRate1());
		inventoryItem.setAopRate2(inventorySyncTypeDTO.getAOPRate2());
		inventoryItem.setAopTargetRate1(inventorySyncTypeDTO.getAOPTargetRate1());
		inventoryItem.setAopTargetRate2(inventorySyncTypeDTO.getAOPTargetRate2());
		inventoryItem.setOrderGrossValue(inventorySyncTypeDTO.getOrderGrossValue());
		inventoryItem.setOrderNetValue(inventorySyncTypeDTO.getOrderNetValue());
		inventoryItem.setNomenClature(inventorySyncTypeDTO.getNomenClature());
		
		// create inventory transaction
		if (!(SyncType.MACHINE_SALE.equals(inventorySyncTypeDTO.getSyncType()) && inventorySyncTypeDTO
				.getReshipped().toString()
				.equalsIgnoreCase(IntegrationConstants.FALSE))
				|| createInventoryItem) {
			createInventoryTransaction(inventoryItem, inventorySyncTypeDTO,errorMessageCodes);
		}
	}

	private void setPartGroupsInformation(PartGroups partGroups,
			InventoryItem inventoryItem) {
		if (partGroups != null && partGroups.getPartGroupArray() != null
				&& partGroups.getPartGroupArray().length > 0) {
			final List<PartGroup> partGroupList = new ArrayList<PartGroup>();
			for (InventorySync.PartGroups.PartGroup partGroupDTO : partGroups
					.getPartGroupArray()) {
				PartGroup partGroup = new PartGroup();
				partGroup.setPartGroupDescription(partGroupDTO
						.getPartGroupDescription());
				partGroup.setQty(partGroupDTO.getQuantity());
				partGroup.setStandardCost(partGroupDTO.getStandardCost());
				partGroup.setPartGroupCode(partGroupDTO.getPartGroupCode());
				partGroupList.add(partGroup);
			}
			inventoryItem.setPartGroups(partGroupList);
		}
	}

	private InventoryItem setPartInventory(InventoryItem inventoryItem,
			Item component, InventorySync.Components.Component componentDTO) {
		InventoryItem partInventory;
		partInventory = new InventoryItem();
		partInventory.setHoursOnMachine(new Long(0));
		partInventory.setSerialNumber(componentDTO
				.getComponentPartSerialNumber().trim());
		partInventory.setOwnershipState(inventoryItem.getOwnershipState());
		partInventory.setType(inventoryItem.getType());
		partInventory.setOfType(component);
		partInventory.setConditionType(inventoryItem.getConditionType());
		partInventory.setCurrentOwner(inventoryItem.getCurrentOwner());
		partInventory.setLatestBuyer(inventoryItem.getLatestBuyer());
		partInventory.setBusinessUnitInfo(inventoryItem.getBusinessUnitInfo());
		partInventory.setManufacturingSiteInventory(inventoryItem
				.getManufacturingSiteInventory());
		partInventory.setSourceWarehouse(inventoryItem.getSourceWarehouse());
		partInventory.setFactoryOrderNumber(inventoryItem
				.getFactoryOrderNumber());
		partInventory.setBuiltOn(inventoryItem.getBuiltOn());
		partInventory.setDeliveryDate(inventoryItem.getDeliveryDate());
		partInventory.setFactoryOrderNumber(inventoryItem
				.getFactoryOrderNumber());
		partInventory.setShipmentDate(inventoryItem.getShipmentDate());
		partInventory
				.setInstallationDate(inventoryItem.getInstallationDate() != null
						? inventoryItem.getInstallationDate()
						: inventoryItem.getBuiltOn());
		/*partInventory.setSerialTypeDescription(componentDTO
				.getSerialTypeDescription());*/
		partInventory.setSerializedPart(true);
		partInventory.setSource(InventoryItemSource.INSTALLBASE);
		if (componentDTO
				.getTransactionType()
				.toString()
				.equalsIgnoreCase(
						IntegrationConstants.DELETE_COMPONENT)){
			partInventory.getD().setActive(false);
		}else{
			partInventory.getD().setActive(true);
		}
		return partInventory;
	}
	private InventoryItem updatePartInventory(InventoryItem partInventory,
			Item component, InventorySync.Components.Component componentDTO) {
		partInventory.setHoursOnMachine(new Long(0));
		partInventory.setSerialNumber(componentDTO
				.getComponentPartSerialNumber().trim());
		partInventory.setOfType(component);
		/*partInventory.setSerialTypeDescription(componentDTO
				.getSerialTypeDescription());*/
		partInventory.setSerializedPart(true);
		partInventory.setSource(InventoryItemSource.INSTALLBASE);
		return partInventory;
	}
	
	

	private void setDateFields(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem) {
		Calendar orderReceivedDate = null;
		if (inventorySyncTypeDTO.getOrderReceivedDate() != null) {
			orderReceivedDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getOrderReceivedDate().getTime()));
			inventoryItem.setOrderReceivedDate(orderReceivedDate.getTime());
		}
		Calendar actualCTSDate = null;
		if (inventorySyncTypeDTO.getActualCTSDate() != null) {
			actualCTSDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getActualCTSDate().getTime()));
			inventoryItem.setActualCtsDate(actualCTSDate.getTime());
		}
		Calendar itaBookDate = null;
		if (inventorySyncTypeDTO.getITABookDate() != null) {
			itaBookDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getITABookDate().getTime()));
			inventoryItem.setItaBookDate((itaBookDate.getTime()));
		}
		Calendar itaBookReportDate = null;
		if (inventorySyncTypeDTO.getITABookReportDate() != null) {
			itaBookReportDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getITABookReportDate().getTime()));
			inventoryItem.setItaBookReportDate(itaBookReportDate.getTime());
		}
		Calendar itaDeliveryDate = null;
		if (inventorySyncTypeDTO.getITADeliveryDate() != null) {
			itaDeliveryDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getITADeliveryDate().getTime()));
			inventoryItem.setItaDeliveryDate(itaDeliveryDate.getTime());
		}
		Calendar itaDeliveryReportDate = null;
		if (inventorySyncTypeDTO.getITADeliveryRptDate() != null) {
			itaDeliveryReportDate = CalendarUtil
					.convertDateToCalendar((inventorySyncTypeDTO
							.getITADeliveryRptDate().getTime()));
			inventoryItem.setItaDeliveryReportDate(itaDeliveryReportDate
					.getTime());
		}

	}

	private void setWaiversForInventory(
			InventorySync inventorySyncTypeDto,
			InventoryItem inventoryItem) {
		InventorySync.DieselTierWaiver waiverDTO = inventorySyncTypeDto
				.getDieselTierWaiver();
		if (waiverDTO != null) {
			DieselTierWaiver dieselTierWaiver = new DieselTierWaiver();
			SimpleDateFormat sdf = new SimpleDateFormat(
					IntegrationConstants.DATE_FORMAT);
			try {
				Date approvedDateTime = sdf.parse(waiverDTO.getApprovedDate()
						+ " " + waiverDTO.getApprovedTime());
				dieselTierWaiver.setApprovedDateTime(approvedDateTime);
			} catch (Exception e) {
				logger.error(
						"Given approved date from install base sync cannot be parsed ",
						e);
				throw new RuntimeException(
						InstallBaseSyncInterfaceErrorConstants.I0062
								+ ":"
								+ installBaseSyncInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(
												InstallBaseSyncInterfaceErrorConstants.I0062
												));
			}
			dieselTierWaiver.setApprovedByAgentName(waiverDTO
					.getApprovedByAgentName());
			dieselTierWaiver.setAgentTitle(waiverDTO.getAgentTitle());
			dieselTierWaiver.setAgentTelephone(waiverDTO
					.getAgentTelephoneNumber());
			dieselTierWaiver.setAgentEmailAddress(waiverDTO
					.getAgentEmailAddress());
			dieselTierWaiver.setDestinationCountry(waiverDTO
					.getDestinationCountry());
			dieselTierWaiver.setCountryEmissionRating(waiverDTO
					.getCountryEmissionRating());
			String dealerNumber=null;
			if (inventorySyncTypeDto.getBillToNumber() != null) {
				dealerNumber = inventorySyncTypeDto.getBillToNumber();
			} else if (inventorySyncTypeDto.getShipToNumber() != null) {
				dealerNumber = inventorySyncTypeDto.getShipToNumber();
			}
			if (dealerNumber != null) {
				Dealership dealer = orgService
						.findDealerDetailsByNumber(dealerNumber);
                  String dealerLoclae=LocaleUtil.getLocale(dealer.getLanguage());
					inventoryItem.setDisclaimerInfo(getDisClaimerInfo(
							dealerLoclae, waiverDTO));
			}
			dieselTierWaiver.setI18NDisclaimers(getDisClaimerInfo(waiverDTO));
			dieselTierWaiver.setInventoryItem(inventoryItem);
			dieselTierWaiver.setDisclaimer(inventoryItem.getDisclaimerInfo());
			inventoryItem.setWaiverDuringDr(dieselTierWaiver);
			inventoryItem.setIsDisclaimer(true);
		}
	}

	private List<I18NDisclaimer> getDisClaimerInfo(
			tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.DieselTierWaiver waiverDTO) {
		List<I18NDisclaimer> i18nDisclaimers = new ArrayList<I18NDisclaimer>();
		for (Disclaimers disclaimers : waiverDTO.getDisclaimersArray()) {
			for (DisclaimerInformation disclaimerInformation : disclaimers
					.getDisclaimerInformationArray()) {
				I18NDisclaimer disclaimer = new I18NDisclaimer();
				disclaimer.setLocale(LocaleUtil.getLocale(disclaimerInformation.getLanguageCode()));
				disclaimer.setDescription(disclaimerInformation.getDisclaimer());
				i18nDisclaimers.add(disclaimer);
			}
		}
		return i18nDisclaimers;
	}
	
	private String getDisClaimerInfo(
			String dealerLocale,
			tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO.InstallBaseSync.DataArea.InventorySync.DieselTierWaiver waiverDTO) {
		for (Disclaimers disclaimers : waiverDTO.getDisclaimersArray()) {
			for (DisclaimerInformation disclaimerInformation : disclaimers
					.getDisclaimerInformationArray()) {
				if (dealerLocale.equalsIgnoreCase(LocaleUtil
						.getLocale(disclaimerInformation.getLanguageCode()))) {
					return disclaimerInformation.getDisclaimer();
				}
			}
		}
		return null;
	}

	private InventoryType getInventoryType(String description) {
		String inventoryType = null;
		if (description.trim().equalsIgnoreCase(
				IntegrationConstants.MACHINE_SALE)) {
			return InventoryType.STOCK;
		} else if (description.trim()
				.equalsIgnoreCase(IntegrationConstants.RMA)) {
			return InventoryType.OEM_STOCK;
		}
		else if (description.trim()
				.equalsIgnoreCase(IntegrationConstants.RETAILED)) {
			return InventoryType.RETAIL;
		}
		return new InventoryType(inventoryType);
	}

	private Item getItem(String number,
			final Map<String, String> errorMessageCodes,String itemType) {
		Item item;
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Looking up item with item number " + number);
			}
			item = catalogService.findItemByItemNumberOwnedByManuf(number.trim(),itemType);
		} catch (CatalogException e) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0044,
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0044,
							new String[]{number}));
			throw new RuntimeException(
					InstallBaseSyncInterfaceErrorConstants.I0044
							+ ":"
							+ installBaseSyncInterfaceErrorConstants
									.getPropertyMessage(
											InstallBaseSyncInterfaceErrorConstants.I0044,
											new String[]{number}), e);
		}
		return item;
	}

	private void createInventoryTransaction(InventoryItem inventoryItem,
			InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {

		InventoryTransaction inventoryTransaction = new InventoryTransaction();
		inventoryTransaction.setTransactedItem(inventoryItem);
		if (inventorySyncTypeDTO.getInvoiceDate() != null) {
			inventoryTransaction
					.setInvoiceDate(convertToCalendarDate(inventorySyncTypeDTO
							.getInvoiceDate()));
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getInvoiceNumber())) {
			inventoryTransaction.setInvoiceNumber(inventorySyncTypeDTO
					.getInvoiceNumber().trim());
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getSalesOrderNumber())) {
			inventoryTransaction.setSalesOrderNumber(inventorySyncTypeDTO
					.getSalesOrderNumber().trim());
		}
		if (StringUtils.hasText(inventorySyncTypeDTO.getInvoiceComments())) {
			inventoryTransaction.setInvoiceComments(inventorySyncTypeDTO
					.getInvoiceComments().trim());
		}
		inventoryTransaction.setTransactionDate(Clock.today());
		inventoryTransaction.setTransactionOrder(new Long(inventoryItem
				.getTransactionHistory().size() + 1));
		if (null != inventorySyncTypeDTO.getShipToLocation()) {
			inventoryTransaction.setShipToSiteNumber(inventorySyncTypeDTO
					.getShipToLocation());
		}
		Party seller = orgService
				.findOrganizationByName(IntegrationConstants.OEM);
		if (seller == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0049, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0049,
					new String[]{IntegrationConstants.OEM}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0049,
							new String[]{IntegrationConstants.OEM}));
		}
		inventoryTransaction.setSeller(seller);
		String serviceProviderNumber = null;

		if (inventoryItem.getType().getType()
				.equalsIgnoreCase(InventoryType.STOCK.getType())||inventoryItem.getType().getType()
				.equalsIgnoreCase(InventoryType.OEM_STOCK.getType())) {
			if (logger.isInfoEnabled()) {
				logger.info("Looking up dealer with dealer number "
						+ inventorySyncTypeDTO.getShipToNumber());
			}
			if (StringUtils.hasText(inventorySyncTypeDTO.getShipToNumber())) {
				serviceProviderNumber = inventorySyncTypeDTO.getShipToNumber()
						.trim();
			}
		} else if (inventoryItem.getType().getType()
				.equalsIgnoreCase(InventoryType.RETAIL.getType())) {
					serviceProviderNumber = inventorySyncTypeDTO
						.getInstallingDealerNumber();
			
		}
		ServiceProvider serviceProvider = orgService
				.findServiceProviderByNumber(serviceProviderNumber);
		Dealership dealer = (Dealership) orgService
				.findServiceProviderByNumber(inventorySyncTypeDTO.getBillToNumber().trim());
		inventoryItem.setCurrency(dealer.getPreferredCurrency().toString());
		ServiceProvider shipToServiceProvider = orgService
		.findServiceProviderByNumber(inventorySyncTypeDTO
				.getShipToNumber().trim());
		if (serviceProviderNumber == null
				|| !StringUtils.hasText(serviceProviderNumber)) {
			errorMessageCodes
					.put(
							InstallBaseSyncInterfaceErrorConstants.I0066,
							installBaseSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0066));
			throw new RuntimeException(
					InstallBaseSyncInterfaceErrorConstants.I0066
							+ ":"
							+ installBaseSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(InstallBaseSyncInterfaceErrorConstants.I0066));
		}
		else if (serviceProvider == null&&serviceProviderNumber!=null&&StringUtils.hasText(serviceProviderNumber)) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0046, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0046,
					new String[]{serviceProviderNumber}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0046,
							new String[]{serviceProviderNumber}));
		}
		inventoryTransaction.setBuyer(serviceProvider);
		inventoryTransaction.setOwnerShip(serviceProvider);
		InventoryTransactionType inventoryTransactionType = invTransactionService
				.getTransactionTypeByName(IntegrationConstants.IB);
		inventoryTransaction.setInvTransactionType(inventoryTransactionType);
		inventoryTransaction.setStatus(BaseDomain.ACTIVE);
		inventoryItem.getTransactionHistory().add(inventoryTransaction);
		boolean preOrderBooking = inventorySyncTypeDTO.getPreOrderBooking();
		inventoryItem.setPreOrderBooking(preOrderBooking);
		inventoryItem.setShipTo(shipToServiceProvider);
		if ((inventoryItem.getType().getType()
				.equalsIgnoreCase(InventoryType.STOCK.getType()) && (SyncType.MACHINE_SALE
				.equals(inventorySyncTypeDTO.getSyncType()) && preOrderBooking))||(inventoryItem.getType().getType()
						.equalsIgnoreCase(InventoryType.RETAIL.getType()) && (SyncType.RETAILED
								.equals(inventorySyncTypeDTO.getSyncType())))) {
			String name = null;
			String firstName = null;
			String lastName = null;
			String customerNumber = null;
			if (inventorySyncTypeDTO.getCustomerInfo() != null) {
				if (StringUtils.hasText(inventorySyncTypeDTO.getCustomerInfo()
						.getFirstName())) {
					firstName = inventorySyncTypeDTO.getCustomerInfo()
							.getFirstName();
				}
				if (StringUtils.hasText(inventorySyncTypeDTO.getCustomerInfo()
						.getLastName())) {
					lastName = inventorySyncTypeDTO.getCustomerInfo()
							.getLastName();
				}
				if (inventorySyncTypeDTO.getCustomerInfo().getCustomerNumber()!= null
						&& StringUtils.hasText(inventorySyncTypeDTO
								.getCustomerInfo().getCustomerNumber())) {
					customerNumber = inventorySyncTypeDTO.getCustomerInfo()
							.getCustomerNumber().trim();
				}
				if (StringUtils.hasText(firstName)
						&& StringUtils.hasText(lastName)) {
					name = firstName + " " + lastName;
				} else if (StringUtils.hasText(firstName)) {
					name = firstName;
				} else if (StringUtils.hasText(lastName)) {
					name = lastName;
				}
				Customer customer = customerService
						.findCustomerByCustomerId(customerNumber);
				if (customer != null) {
					setCustomerData(inventorySyncTypeDTO, name, customer);
					customerService.updateCustomer(customer);
					createCustomerServiceProviderMapping(customer,
							serviceProvider);
					inventoryItem.setLatestBuyer(customer);
				} else {
					customer = new Customer();
					customer.setCustomerId(customerNumber);
					setCustomerData(inventorySyncTypeDTO, name, customer);
					customerService.createCustomer(customer);
					createCustomerServiceProviderMapping(customer,
							serviceProvider);
					inventoryItem.setLatestBuyer(customer);
				}

			}
		}else{
			inventoryItem.setLatestBuyer(serviceProvider);
		}
		
	}

	private void setCustomerData(InventorySync inventorySyncTypeDTO,
			String name, Customer customer) {
		boolean customerUpdated=checkForCustomerModifications(inventorySyncTypeDTO,name,customer);
		if(customerUpdated){
		customer.setName(name);
		customer.setCompanyName(name);
		customer.setCorporateName(name);
		customer.setSiCode(inventorySyncTypeDTO.getCustomerInfo().getSICode());
		if (inventorySyncTypeDTO.getCustomerInfo() != null
				&& inventorySyncTypeDTO.getCustomerInfo().getSICode() != null) {
			customer.setSiCode(inventorySyncTypeDTO.getCustomerInfo()
					.getSICode());
		}
		customer.setIndividual(Boolean.FALSE);
		if(customer.getAddress()==null){
			customer.setAddress(new Address());
			setCustomerAddressesList(customer);
		}
		Address address = customer.getAddress();
		address.setAddressLine1(inventorySyncTypeDTO.getCustomerInfo()
				.getAddressLine1());
		address.setAddressLine2(inventorySyncTypeDTO.getCustomerInfo()
				.getAddressLine2());
		address.setAddressLine3(inventorySyncTypeDTO.getCustomerInfo()
				.getAddressLine3());
		address.setAddressLine4(inventorySyncTypeDTO.getCustomerInfo()
				.getAddressLine4());
		address.setCity(inventorySyncTypeDTO.getCustomerInfo().getCity());
		if(inventorySyncTypeDTO.getCustomerInfo().getState()!=null){
		address.setState(inventorySyncTypeDTO.getCustomerInfo().getState());
		}
		address.setCounty(inventorySyncTypeDTO.getCustomerInfo()
				.getCountyCode());
		address.setZipCode(inventorySyncTypeDTO.getCustomerInfo()
				.getPostalCode());
		address.setCountry(inventorySyncTypeDTO.getCustomerInfo().getCountry());
		}
		
	}

	private boolean checkForCustomerModifications(
			InventorySync inventorySyncTypeDTO, String name, Customer customer) {
		if (!customerDetailsUpdated(customer, name)) {
			if (customer.getAddress() != null) {
				if (!isAddressDetailsUpdated(customer.getAddress(),
						inventorySyncTypeDTO.getCustomerInfo())) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isAddressDetailsUpdated(Address address,
			CustomerInfo customerInfo) {
		if (!addressLineFiledsUpdated(address, customerInfo)) {
			if ((address.getCity() != null & customerInfo.getCity() != null && address
					.getCity().equals(customerInfo.getCity()))
					|| (address.getCity() == null && (customerInfo.getCity() == null || customerInfo
							.getCity().isEmpty()))) {
				if ((address.getState() != null
						& customerInfo.getState() != null && address.getState()
						.equals(customerInfo.getState()))
						|| (address.getState() == null && (customerInfo
								.getState() == null || customerInfo.getState()
								.isEmpty()))) {
					if ((address.getCounty() != null
							& customerInfo.getCountyCode() != null && address
							.getCounty().equals(customerInfo.getCountyCode()))
							|| (address.getCounty() == null && (customerInfo
									.getCountyCode() == null || customerInfo
									.getCountyCode().isEmpty()))) {
						if ((address.getZipCode() != null
								& customerInfo.getPostalCode() != null && address
								.getZipCode().equals(
										customerInfo.getPostalCode()))
								|| (address.getZipCode() == null && (customerInfo
										.getPostalCode() == null || customerInfo
										.getPostalCode().isEmpty()))) {
							if ((address.getCountry() != null
									& customerInfo.getCountry() != null && address
									.getCountry().equals(
											customerInfo.getCountry()))
									|| (address.getCountry() == null && (customerInfo
											.getCountry() == null || customerInfo
											.getCountry().isEmpty()))) {

								return false;

							}

						}

					}
				}
			}

		}
		return true;
	}

	private boolean addressLineFiledsUpdated(Address address,
			CustomerInfo customerInfo) {
		if ((address.getAddressLine1() != null
				& customerInfo.getAddressLine1() != null && address
				.getAddressLine1().equals(customerInfo.getAddressLine1()))
				|| (address.getAddressLine1() == null && (customerInfo
						.getAddressLine1() == null || customerInfo
						.getAddressLine1().isEmpty()))) {
			if ((address.getAddressLine2() != null
					& customerInfo.getAddressLine2() != null && address
					.getAddressLine2().equals(customerInfo.getAddressLine2()))
					|| (address.getAddressLine2() == null && (customerInfo
							.getAddressLine2() == null || customerInfo
							.getAddressLine2().isEmpty()))) {
				if ((address.getAddressLine3() != null
						& customerInfo.getAddressLine3() != null && address
						.getAddressLine3().equals(
								customerInfo.getAddressLine3()))
						|| (address.getAddressLine3() == null && (customerInfo
								.getAddressLine3() == null || customerInfo
								.getAddressLine3().isEmpty()))) {
					if ((address.getAddressLine4() != null
							& customerInfo.getAddressLine4() != null && address
							.getAddressLine4().equals(
									customerInfo.getAddressLine4()))
							|| (address.getAddressLine4() == null && (customerInfo
									.getAddressLine4() == null || customerInfo
									.getAddressLine4().isEmpty()))) {
						return false;
					}

				}

			}

		}

		return true;
	}

	private boolean customerDetailsUpdated(Customer customer, String name) {
		if (((name == null || name.isEmpty()) && customer.getName() == null
				&& customer.getCompanyName() == null && customer
				.getCorporateName() == null)
				|| (name != null && customer.getName() != null
						&& customer.getName().equals(name)
						&& customer.getCompanyName() != null
						&& customer.getCompanyName().equals(name) && customer
						.getCorporateName().equals(name))) {
			return false;

		}
		return true;
	}

	private void createCustomerServiceProviderMapping(Customer customer,
			ServiceProvider serviceProvider) {
		AddressBook addressBook = addressBookService
				.getAddressBookByOrganizationAndType(serviceProvider,
						AddressBookType.ENDCUSTOMER);
		if (addressBook == null) {
			addressBook = createNewAddressBook(serviceProvider);
			AddressBookAddressMapping addressBookAddressMapping = createAddressBookAddressMappingForCustomer(
					customer, addressBook);
			addressBookAddressMapping.setEndCustomer(Boolean.TRUE);
			List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
			mappings.add(addressBookAddressMapping);
			addressBook.setAddressBookAddressMapping(mappings);
			addressBookService.createAddressBook(addressBook);
		} else {
			AddressBookAddressMapping mapping = createAddressBookAddressMappingForCustomer(
					customer, addressBook);
			mapping.setEndCustomer(Boolean.TRUE);
			if (addressBook.getAddressBookAddressMapping() == null) {
				List<AddressBookAddressMapping> mappings = new ArrayList<AddressBookAddressMapping>();
				addressBook.setAddressBookAddressMapping(mappings);
			}
			addressBook.getAddressBookAddressMapping().add(mapping);
			addressBookService.update(addressBook);
		}
	}

	private AddressBook createNewAddressBook(ServiceProvider dealer) {
		AddressBook addressBook = new AddressBook();
		addressBook.setBelongsTo(dealer);
		addressBook.setType(AddressBookType.ENDCUSTOMER);
		return addressBook;
	}

	private void setCustomerAddressesList(Customer customer) {
		List<Address> addresses = new ArrayList<Address>();
		addresses.add(customer.getAddress());
		customer.setAddresses(addresses);
	}
	private AddressBookAddressMapping createAddressBookAddressMappingForCustomer(
			Customer customer, AddressBook addressBook) {
		AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
		addressBookAddressMapping.setAddress(customer.getAddress());
		addressBookAddressMapping.setAddressBook(addressBook);
		addressBookAddressMapping.setPrimary(Boolean.TRUE);
		addressBookAddressMapping.setType(AddressType.SHIPPING);
		return addressBookAddressMapping;
	}

	private void createOrUpdateShipToAddress(ServiceProvider billTo,
			InventorySync inventorySyncTypeDTO,final Map<String,String> errorMessageCodes) {

		/*List<OrganizationAddress> existingActiveOrgAddress = orgService
				.getAddressesForOrganization(billTo);
		InventorySync.ShipToAddress shipAddressTypeDTO = inventorySyncTypeDTO
				.getShipToAddress();

		String siteNumber = inventorySyncTypeDTO.getShipToLocation();
		OrganizationAddress organizationAddress = null;

		for (OrganizationAddress oa : existingActiveOrgAddress) {
			if(oa.getSiteNumber()!=null){
				if (oa.getSiteNumberForDisplay(oa.getSiteNumber()).equals(siteNumber)) {
					organizationAddress = oa;
					break;
				}
			}
		}
		if (organizationAddress == null){
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0028, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0028,
					new String[]{siteNumber}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0028,
							new String[]{siteNumber}));
		}*/
		/*if (organizationAddress == null)
			organizationAddress = new OrganizationAddress();
		if (shipAddressTypeDTO != null) {
			StringBuffer location = new StringBuffer();
			if (StringUtils.hasText(shipAddressTypeDTO.getAddress1())) {
				organizationAddress.setAddressLine1(shipAddressTypeDTO
						.getAddress1().trim());
				location.append(shipAddressTypeDTO.getAddress1().trim());
				location.append("-");
			}
			if (StringUtils.hasText(shipAddressTypeDTO.getAddress2())) {
				organizationAddress.setAddressLine2(shipAddressTypeDTO
						.getAddress2().trim());
				location.append(shipAddressTypeDTO.getAddress2().trim());
				location.append("-");
			}
			if (StringUtils.hasText(shipAddressTypeDTO.getCountry())) {
				organizationAddress.setCountry(shipAddressTypeDTO.getCountry()
						.trim());
				location.append(shipAddressTypeDTO.getCountry());
				location.append("-");
			}
			if (StringUtils.hasText(shipAddressTypeDTO.getCity())) {
				organizationAddress
						.setCity(shipAddressTypeDTO.getCity().trim());
				location.append(shipAddressTypeDTO.getCity());
				location.append("-");
			}
			if (StringUtils.hasText(shipAddressTypeDTO.getState())) {
				organizationAddress.setState(shipAddressTypeDTO.getState()
						.trim());
				location.append(shipAddressTypeDTO.getState());
				location.append("-");
			}
			if (StringUtils.hasText(shipAddressTypeDTO.getZipCode())) {
				organizationAddress.setZipCode(shipAddressTypeDTO.getZipCode()
						.trim());
				location.append(shipAddressTypeDTO.getZipCode());
				location.append("-");
			}
			organizationAddress.setAddressIdOnRemoteSystem(siteNumber);

			if (location != null) {
				organizationAddress.setLocation(location.toString());
			}
			organizationAddress.setAddressActive(Boolean.TRUE);
			organizationAddress.setSiteNumber(siteNumber);
			orgService.createOrgAddressForDealer(organizationAddress,
					billTo.getId());
		}*/
	}

	public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
		this.warrantyUtil = warrantyUtil;
	}

	private String setBuName(String divivsionCode) {
		if (divivsionCode.equalsIgnoreCase(IntegrationConstants.US)) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(IntegrationConstants.NMHG_US);
		} else if (divivsionCode.equalsIgnoreCase(IntegrationConstants.EMEA)) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(IntegrationConstants.NMHG_EMEA);
		}
		return SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
	}

	// for consignment inventory : if machine is sold, SAP updates inventory
	// item condition to "NEW".
	// if sold to different dealer, then perform D2D transaction
	private void setConsignmentItem(InventoryItem inventoryItem,
			String shipToNumber,final Map<String,String> errorMessageCodes) {
		String serviceProviderNumber = null;
		if (StringUtils.hasText(shipToNumber)
				&& inventoryItem.getCurrentOwner() != null) {
			serviceProviderNumber = shipToNumber.trim();
			ServiceProvider newOwner = orgService
					.findServiceProviderByNumber(serviceProviderNumber.trim());
			if (newOwner == null) {
				errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0046, installBaseSyncInterfaceErrorConstants
						.getPropertyMessage(
								InstallBaseSyncInterfaceErrorConstants.I0046,
								new String[]{serviceProviderNumber}));
				throw new RuntimeException(
						installBaseSyncInterfaceErrorConstants
								.getPropertyMessage(
										InstallBaseSyncInterfaceErrorConstants.I0046,
										new String[]{serviceProviderNumber}));
			}

			ServiceProvider currentOwner = InstanceOfUtil.isInstanceOfClass(
					ServiceProvider.class, inventoryItem.getCurrentOwner())
					? new HibernateCast<ServiceProvider>().cast(inventoryItem
							.getCurrentOwner()) : null;

			if (null != currentOwner
					&& !currentOwner.getDealerNumber().equalsIgnoreCase(
							newOwner.getDealerNumber())) {
				warrantyUtil.performD2D(inventoryItem, newOwner, true);
			}
			inventoryItem.setConditionType(InventoryItemCondition.NEW);
			inventoryService.updateInventoryItem(inventoryItem);
		}
	}

	// if service provider is intercompany and ib notification receives on
	// existing serial number
	// 1. do RMA on inventory
	// 2. do IB transaction with latest buyer
	private void updateInventoryItemDetails(InventoryItem inventoryItem,
			InventorySync inventorySyncTypeDTO, Item item,
			boolean createInventoryItem,final Map<String,String> errorMessageCodes) {
		if (inventoryItem.getType().getType()
				.equalsIgnoreCase(InventoryType.STOCK.getType())
				&& SyncType.MACHINE_SALE.equals(inventorySyncTypeDTO
						.getSyncType())) {
			createRMAInventoryTransaction(inventoryItem, inventorySyncTypeDTO,errorMessageCodes);
			inventoryService.updateInventoryItem(inventoryItem);
			
			merge(inventoryItem, inventorySyncTypeDTO, item,createInventoryItem,errorMessageCodes);
			inventoryService.updateInventoryItem(inventoryItem);
		} else if (SyncType.RMA.equals(inventorySyncTypeDTO.getSyncType())) {
			createRMAInventoryTransaction(inventoryItem, inventorySyncTypeDTO,errorMessageCodes);
			inventoryService.updateInventoryItem(inventoryItem);
		} else if (!createInventoryItem) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0047, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0047,
					new String[]{inventorySyncTypeDTO
							.getInventoryDetail().getSerialNumber()
							.trim()}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0047,
							new String[]{inventorySyncTypeDTO
									.getInventoryDetail().getSerialNumber()
									.trim()}));
		}
	}

	private void setManufacturingSite(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem) {
		String manufacturingSiteString = inventorySyncTypeDTO.getBuildPlant().trim();
		ListOfValues listOfValues = lovRepository.findActiveValuesByCode(
				ManufacturingSiteInventory.class.getSimpleName(),
				manufacturingSiteString);
		if (listOfValues != null
				&& listOfValues instanceof ManufacturingSiteInventory) {
			inventoryItem
					.setManufacturingSiteInventory(((ManufacturingSiteInventory) listOfValues));
		} else {
			ManufacturingSiteInventory manSiteInventory = new ManufacturingSiteInventory();
			manSiteInventory.setCode(manufacturingSiteString);
			manSiteInventory.setDescription(manufacturingSiteString);
			List<I18NLovText> i18nLovTexts = new ArrayList<I18NLovText>();
			I18NLovText i18nLovText = new I18NLovText();
			i18nLovText.setLocale(IntegrationConstants.DEFAULT_LOCALE);
			i18nLovText.setDescription(manufacturingSiteString);
			i18nLovTexts.add(i18nLovText);
			manSiteInventory.setI18nLovTexts(i18nLovTexts);
			lovRepository.save(manSiteInventory);
			inventoryItem.setManufacturingSiteInventory(manSiteInventory);
		}
	}

	private void setComponents(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem,boolean createInventoryItem,final Map<String,String> errorMessageCodes){
		InventoryItem partInventory = null;
		Item component = null;
		if(inventorySyncTypeDTO.getComponents()!=null && inventorySyncTypeDTO
				.getComponents().getComponentArray()!=null &&inventorySyncTypeDTO
						.getComponents().getComponentArray().length>0 ){
		for (InventorySync.Components.Component componentDTO : inventorySyncTypeDTO
				.getComponents().getComponentArray()) {
			component = getItem(componentDTO.getComponentPartNumber(),errorMessageCodes,IntegrationConstants.PART_ITEM_TYPE);
			 if ((inventorySyncTypeDTO
						.getComponentSerialUpdated()!=null && IntegrationConstants.TRUE
					.equalsIgnoreCase(inventorySyncTypeDTO
							.getComponentSerialUpdated().toString()))||createInventoryItem==true) {
				 boolean createComponent=Boolean.TRUE;
				 InventoryItem componentInventoryItem=null;
				if(inventoryItem.getComposedOf()!=null&&!inventoryItem.getComposedOf().isEmpty()){
					for(InventoryItemComposition inventoryItemComposition:inventoryItem.getComposedOf()){
						if(inventoryItemComposition.getSequenceNumber().equalsIgnoreCase(componentDTO.getSequenceNumber().trim())){
							createComponent=Boolean.FALSE;
							componentInventoryItem=inventoryItemComposition.getPart();
							updateComponent(inventoryItem,inventoryItemComposition,componentDTO);
							break;
						}
					}
				}
					if ((componentDTO
							.getTransactionType()
							.toString()
							.equalsIgnoreCase(
									IntegrationConstants.CHANGE_COMPONENT)||componentDTO
									.getTransactionType()
									.toString()
									.equalsIgnoreCase(
											IntegrationConstants.NEW_COMPONENT))&&componentInventoryItem!=null&&createComponent==Boolean.FALSE) {
						componentInventoryItem = updatePartInventory(
								componentInventoryItem, component, componentDTO);
						componentInventoryItem.getD().setActive(true);
						inventoryService.updateInventoryItem(componentInventoryItem);
					} else if (componentDTO
							.getTransactionType()
							.toString()
							.equalsIgnoreCase(
									IntegrationConstants.DELETE_COMPONENT)&&componentInventoryItem!=null&&createComponent==Boolean.FALSE) {
						componentInventoryItem.getD().setActive(false);
						inventoryService.updateInventoryItem(componentInventoryItem);
					}
				 else if (createComponent) {
						partInventory = setPartInventory(inventoryItem,
								component, componentDTO);
						partInventory.getD().setActive(true);
						inventoryService.createInventoryItem(partInventory);
						InventoryItemComposition componentObj=getComponet(componentDTO);
						inventoryItem.include(partInventory,componentObj);
				}
			}
		}
	}
	}
	private void updateComponent(InventoryItem inventoryItem,
			InventoryItemComposition existingComponent, Component componentDTO) {
		existingComponent.setPartOf(inventoryItem);
		existingComponent.setManufacturer(componentDTO.getManufacturer());
		existingComponent.setSequenceNumber(componentDTO
				.getSequenceNumber());
		existingComponent.setComponentSerialType(componentDTO
				.getComponentSerialType());
		if(IntegrationConstants.DELETE_COMPONENT.equalsIgnoreCase(componentDTO.getTransactionType().toString())){
			existingComponent.getD().setActive(false);
			existingComponent.setStatus(IntegrationConstants.INACTIVESTATUS);
		}else{
			existingComponent.getD().setActive(true);
			existingComponent.setStatus(IntegrationConstants.ACTIVESTATUS);
		}
		existingComponent.setSerialTypeDescription(componentDTO.getSerialTypeDescription());
		
	}

	private InventoryItemComposition getComponet(Component componentDTO) {
		InventoryItemComposition componentObj=new InventoryItemComposition();
		componentObj.setManufacturer(componentDTO.getManufacturer());
		componentObj.setComponentSerialType(componentDTO.getComponentSerialType());
		componentObj.setSequenceNumber(componentDTO.getSequenceNumber());
		componentObj.setSerialTypeDescription(componentDTO.getSerialTypeDescription());
		if(IntegrationConstants.DELETE_COMPONENT.equalsIgnoreCase(componentDTO.getTransactionType().toString())){
			componentObj.getD().setActive(false);
			componentObj.setStatus(IntegrationConstants.INACTIVESTATUS);
		}else{
			componentObj.getD().setActive(true);
			componentObj.setStatus(IntegrationConstants.ACTIVESTATUS);
		}
		return componentObj;
	}
	private void setBillToAddress(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem,final Map<String,String> errorMessageCodes) {
		String billToNumber = inventorySyncTypeDTO.getBillToNumber();
		ServiceProvider billTo = orgService
				.findServiceProviderByNumber(billToNumber.trim());
		if (billTo == null) {
			errorMessageCodes.put(InstallBaseSyncInterfaceErrorConstants.I0052, installBaseSyncInterfaceErrorConstants.getPropertyMessage(
					InstallBaseSyncInterfaceErrorConstants.I0052,
					new String[]{billToNumber}));
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0052,
							new String[]{billToNumber}));
		}
		inventoryItem.setCurrentOwner(billTo);
		/*createOrUpdateShipToAddress(billTo, inventorySyncTypeDTO,errorMessageCodes);*/
	}

	private void setItemConditionType(InventorySync inventorySyncTypeDTO,
			InventoryItem inventoryItem) {
		String itemCondition = null;
		if (inventorySyncTypeDTO.getInventoryDetail().getItemCondition() != null) {
			itemCondition = inventorySyncTypeDTO.getInventoryDetail()
					.getItemCondition().toString();
		}
		if (itemCondition != null) {
			if (itemCondition.trim().equalsIgnoreCase(IntegrationConstants.NEW))
				inventoryItem.setConditionType(InventoryItemCondition.NEW);
			else if (itemCondition.trim().equalsIgnoreCase(
					IntegrationConstants.REFURBISHED))
				inventoryItem
						.setConditionType(InventoryItemCondition.REFURBISHED);
			else if (itemCondition.trim().equalsIgnoreCase(
					IntegrationConstants.SCRAP))
				inventoryItem.setConditionType(InventoryItemCondition.SCRAP);
			else if (itemCondition.trim().equalsIgnoreCase(
					IntegrationConstants.CONSIGNMENT))
				inventoryItem.setConditionType(InventoryItemCondition.CONSIGNMENT);
			else if (itemCondition.trim().equalsIgnoreCase(
					IntegrationConstants.PREMIUM_RENTAL))
				inventoryItem
						.setConditionType(InventoryItemCondition.PREMIUM_RENTAL);
			else if (itemCondition.trim().equalsIgnoreCase(
					IntegrationConstants.PREOWNED))
				inventoryItem.setConditionType(InventoryItemCondition.PREOWNED);
		} else {
			inventoryItem.setConditionType(InventoryItemCondition.NEW);
		}
	}

	private void setInventoryTransactionItem(
			InventoryTransaction inventoryTransaction, Organization seller,
			InventoryItem inventoryItem, Organization buyer,
			InventoryTransactionType inventoryTransactionType,
			String inventoryType) {
		inventoryTransaction.setSeller(seller);
		inventoryTransaction.setBuyer(buyer);
		inventoryTransaction.setOwnerShip(buyer);
		inventoryTransaction.setInvTransactionType(inventoryTransactionType);
		inventoryTransaction.setStatus(BaseDomain.ACTIVE);
		inventoryItem.getTransactionHistory().add(inventoryTransaction);
		inventoryItem.setPreOrderBooking(Boolean.FALSE);
		inventoryItem.setCurrentOwner(buyer);
		inventoryItem.setLatestBuyer(buyer);
		inventoryItem.setType(getInventoryType(inventoryType));
		inventoryTransaction.setTransactedItem(inventoryItem);
	}

	private void setSourceWarehouse(String ShipFromOrgCode,
			InventoryItem inventoryItem) {
		if(ShipFromOrgCode!=null){
		SourceWarehouse sourceWarehouse = sourceWarehouseService
				.findSourceWarehouseByCode(ShipFromOrgCode);
		if (null != sourceWarehouse) {
			inventoryItem.setSourceWarehouse(sourceWarehouse);
		} 

		}/*else {
			throw new RuntimeException(
					installBaseSyncInterfaceErrorConstants.getPropertyMessage(
							InstallBaseSyncInterfaceErrorConstants.I0050,
							new String[]{ShipFromOrgCode}));
		}*/
	}

	private void setOptionsInformation(final Options options,
			InventoryItem inventoryItem) {
		if (options != null && options.getOptionArray() != null
				&& options.getOptionArray().length > 0) {
			final List<Option> optionsList = new ArrayList<Option>();
			for (InventorySync.Options.Option optionObj : options
					.getOptionArray()) {
				final Option option = new Option();
				option.setOptionCode(optionObj.getOptionCode());
				option.setOrderOptionLineNumber(optionObj
						.getOrderOptionLineNumber());
				option.setOptionType(optionObj.getOptionType().toString());
				option.setOptionDescription(optionObj.getOptionDescription());
				option.setOptionGrossPrice(optionObj.getOptionGrossPrice());
				option.setOptionNetPrice(optionObj.getOptionNetPrice());
				option.setOptionGrossValue(optionObj.getOptionGrossValue());
				option.setOptionDiscountValue(optionObj
						.getOptionDiscountValue());
				option.setOptionNetValue(optionObj.getOptionNetValue());
				option.setOptionDiscountPercent(optionObj
						.getOptionDiscountPercent());
				option.setActiveInactiveStatus(optionObj
						.getActiveInactiveStatus());
				option.setSpecialOptionStatus(optionObj
						.getSpecialOptionStatus());
				option.setDieselTier(optionObj.getDieselTier().toString());
				option.setMastType(optionObj.getMastType());
				option.setTireType(optionObj.getTireType());
				optionsList.add(option);
			}
			inventoryItem.setOptions(optionsList);
		}
	}

	public void setInstallBaseValidator(
			InstallBaseValidator installBaseValidator) {
		this.installBaseValidator = installBaseValidator;
	}


	public void setInstallBaseSyncInterfaceErrorConstants(
			InstallBaseSyncInterfaceErrorConstants installBaseSyncInterfaceErrorConstants) {
		this.installBaseSyncInterfaceErrorConstants = installBaseSyncInterfaceErrorConstants;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setTransactionTemplate(TransactionTemplate tt) {
		this.transactionTemplate = tt;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setOwnershipStateRepository(
			OwnershipStateRepository ownershipStateRepository) {
		this.ownershipStateRepository = ownershipStateRepository;
	}



	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}


	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setSourceWarehouseService(
			SourceWarehouseService sourceWarehouseService) {
		this.sourceWarehouseService = sourceWarehouseService;
	}
	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public MarketingInformation getMarketingInformation() {
		return marketingInformation;
	}

	public void setMarketingInformation(MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public List<RegisteredPolicy> getAvailablePolicies() {
		return availablePolicies;
	}

	public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
		this.availablePolicies = availablePolicies;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ServiceProvider getInstallingServiceProvider() {
		return installingServiceProvider;
	}

	public void setInstallingServiceProvider(
			ServiceProvider installingServiceProvider) {
		this.installingServiceProvider = installingServiceProvider;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public static Logger getLogger() {
		return logger;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public OwnershipStateRepository getOwnershipStateRepository() {
		return ownershipStateRepository;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public AddressBookService getAddressBookService() {
		return addressBookService;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public SourceWarehouseService getSourceWarehouseService() {
		return sourceWarehouseService;
	}

	public WarrantyUtil getWarrantyUtil() {
		return warrantyUtil;
	}

	public InstallBaseValidator getInstallBaseValidator() {
		return installBaseValidator;
	}

	public InstallBaseSyncInterfaceErrorConstants getInstallBaseSyncInterfaceErrorConstants() {
		return installBaseSyncInterfaceErrorConstants;
	}


	public DealershipRepository getDealershipRepository() {
		return dealershipRepository;
	}

	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}



}

package tavant.twms.web.warranty;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.DieselTierWaiverService;
import tavant.twms.domain.inventory.EngineTierCtryMapping;
import tavant.twms.domain.inventory.EngineTierCtryMappingService;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.inventory.InventoryItemAttributeValueService;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemSource;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TierTierMapping;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.policy.*;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.domain.campaign.CampaignRepository;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.actions.SortedHashMap;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.typeconverters.InvTransactionTypeConverter;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.interceptor.PrepareInterceptor;

import tavant.twms.web.util.DocumentTransportUtils;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;

@SuppressWarnings("serial")
public class ModifyTransferReport extends I18nActionSupport implements
		Preparable, Validateable, ServletRequestAware {

	private static Logger logger = LogManager.getLogger(WarrantyAction.class);
	
	private MultipleInventoryAttributesMapper inventoryAttributesMapper;

	public MultipleInventoryAttributesMapper getInventoryAttributesMapper() {
		return inventoryAttributesMapper;
	}

	public void setInventoryAttributesMapper(
			MultipleInventoryAttributesMapper inventoryAttributesMapper) {
		this.inventoryAttributesMapper = inventoryAttributesMapper;
	}

	private ServiceProvider forDealer;

	
	public ServiceProvider getForDealer() {
		return forDealer;
	}

	public void setForDealer(ServiceProvider forDealer) {
		this.forDealer = forDealer;
	}
	
	private List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>(
			5);

	public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
		return inventoryItemMappings;
	}

	public void setInventoryItemMappings(
			List<MultipleInventoryAttributesMapper> inventoryItemMappings) {
		this.inventoryItemMappings = inventoryItemMappings;
	}

	private Long transactionId = null;

	private Warranty warranty = null;

	private WarrantyService warrantyService = null;

	private InventoryService inventoryService;

	private String modifyDeleteReason;

	private boolean deleteWarranty;

	private Party customer;
	
	private Customer operator;

	private AddressForTransfer addressForTransfer;

	private MarketingInformation marketingInformation;
	
	private WarrantyCoverageRequestService warrantyCoverageRequestService;

	private InvTransactionTypeConverter invTransactionTypeConverter;

	private boolean deliveryReport;
	
	private CampaignRepository campaignRepository;

	List<RegisteredPolicy> selectedPolicies = new ArrayList<RegisteredPolicy>();

	private PolicyService policyService;

	private InventoryItemAttributeValueService inventoryItemAttributeValueService;

	private AttributeService attributeService;

	private HttpServletRequest request;	

	private ConfigParamService configParamService;

	private Long warrantyTransactionId = null;

	private ClaimService claimService;
	
	 private Market marketType;
	 
	 private CustomerService customerService;

	private boolean isModifyDRorETR;
	private Map<Object, Object> customerTypes = new SortedHashMap<Object, Object>();
	private String addressBookType;
	private AddressBookService addressBookService;
	private OrganizationRepository organizationRepository;
	private boolean validateMarketingInfo;
	
	InventoryItem inventoryItem;

	private ServiceProvider installingDealer;
	
	private EngineTierCtryMappingService engineTierCtryMappingService;


	private DieselTierWaiverService dieselTierWaiverService;
	
	private List<RegisteredPolicy> availablePolicies;
	
	  private List<ListOfValues> additionalComponentTypes;
	    private List<ListOfValues> additionalComponentSubTypes;
		private LovRepository lovRepository;

		private BuSettingsService buSettingsService;
		
		public BuSettingsService getBuSettingsService() {
			return buSettingsService;
		}

		public void setBuSettingsService(BuSettingsService buSettingsService) {
			this.buSettingsService = buSettingsService;
		}

		public LovRepository getLovRepository() {
			return lovRepository;
		}

		public void setLovRepository(LovRepository lovRepository) {
			this.lovRepository = lovRepository;
		}

		public List<ListOfValues> getAdditionalComponentSubTypes() {
			return additionalComponentSubTypes;
		}

		public void setAdditionalComponentSubTypes(
				List<ListOfValues> additionalComponentSubTypes) {
			this.additionalComponentSubTypes = additionalComponentSubTypes;
		}

		public List<ListOfValues> getAdditionalComponentTypes() {
			return additionalComponentTypes;
		}

		public void setAdditionalComponentTypes(List<ListOfValues> additionalComponentTypes) {
			this.additionalComponentTypes = additionalComponentTypes;
		}


	public List<RegisteredPolicy> getAvailablePolicies() {
		return availablePolicies;
	}

	public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
		this.availablePolicies = availablePolicies;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public void prepare() {

        if(this.transactionId != null){
            setWarranty(this.warrantyService.findByTransactionId(this.transactionId));
        }
        if(getWarranty()!=null){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getWarranty().getForItem().getBusinessUnitInfo().getName());
            inventoryItem = this.warranty.getForItem();
   		 if(!isLoggedInUserAnInternalUser()) {
   				forDealer = getLoggedInUsersDealership();
   	            if(forDealer!=null){
   	                forDealer.setAddress(warrantyUtil.getDealersAddress(forDealer.getId()));
   	          }

   			}
   			else
   			{
   				if(inventoryItem!=null && inventoryItem.getOwner()!=null){
   					forDealer = inventoryItem.getOwner();
   					if(forDealer!=null){
   						forDealer.setAddress(warrantyUtil.getDealersAddress(forDealer.getId()));
   					}
   				}
   			}    
   	        if (this.inventoryItem != null) {
   				prepareInventoryItem(this.inventoryItem);
   				if(addressBookType==null){
   	   				this.addressBookType = this.warrantyUtil.populateExistingCustomerType(warranty,transactionId,customerTypes,addressBookType);
   				}
   				listApplicablePolicy();
   			}
   	        if(this.transactionId == null && warranty.getForTransaction().getId()!=null){
   	        	this.setTransactionId(warranty.getForTransaction().getId());
   	        }
        }
        if (listIfPreviousOwner.isEmpty()) {
			listIfPreviousOwner.put(SWITCHING,getText("label.wntyreg.prevowner.switch"));
			listIfPreviousOwner.put(CONTINUING,getText("label.wntyreg.prevowner.continue"));
			listIfPreviousOwner.put(UNKNOWN_NOT_PROVIDED,getText("label.wntyreg.prevowner.Unknown/NotProvided"));
		}

		listOfTransactionTypes = this.warrantyService.listTransactionTypes();
        listOfMarketTypes = this.marketService.listMarketTypes();
        if(this.warranty!=null && this.warranty.getMarketingInformation()!=null && this.warranty.getMarketingInformation().getMarket()!=null)
        listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.warranty.getMarketingInformation().getMarket().getId());
        additionalComponentTypes = this.lovRepository.findAllActive("AdditionalComponentType");
   		additionalComponentSubTypes = this.lovRepository.findAllActive("AdditionalComponentSubType");
        if(isCustomerDetailsNeededForDR_Rental()){
			if (this.addressBookType==null || (!this.addressBookType.equalsIgnoreCase(AdminConstants.DEMO)
					/*&& !this.addressBookType
							.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)*/)) {
				Iterator<TransactionType> transactionTypeIterator=listOfTransactionTypes.iterator();
				while(transactionTypeIterator.hasNext()){
					TransactionType transactionType = transactionTypeIterator.next();
					if(/*transactionType.getType().equalsIgnoreCase(AdminConstants.TRANSACTION_TYPE_DEALER_RENTAL) ||*/ transactionType.getType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						transactionTypeIterator.remove();
					}
				}
				Iterator<Market> marketTypeIterator = listOfMarketTypes.iterator();
				while(marketTypeIterator.hasNext()){
					Market marketType = marketTypeIterator.next();
					if(/*marketType.getTitle().equalsIgnoreCase(AdminConstants.RENTAL) ||*/ marketType.getTitle().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						marketTypeIterator.remove();
					}
				}
				Iterator<Market> marketApplicationIterator = listOfMarketApplications.iterator();
				while(marketApplicationIterator.hasNext()){
					Market marketApplication = marketApplicationIterator.next();
					if(/*marketApplication.getTitle().equalsIgnoreCase(AdminConstants.RENTAL) || */marketApplication.getTitle().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						marketApplicationIterator.remove();
					}
				}
			}
		}
		else{
			if (this.addressBookType==null || (!this.addressBookType.equalsIgnoreCase(AdminConstants.DEMO)
					&& !this.addressBookType
							.equalsIgnoreCase(AdminConstants.DEALER_RENTAL))) {
				Iterator<TransactionType> transactionTypeIterator=listOfTransactionTypes.iterator();
				while(transactionTypeIterator.hasNext()){
					TransactionType transactionType = transactionTypeIterator.next();
					if(transactionType.getType().equalsIgnoreCase(AdminConstants.TRANSACTION_TYPE_DEALER_RENTAL) || transactionType.getType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						transactionTypeIterator.remove();
					}
				}
				Iterator<Market> marketTypeIterator = listOfMarketTypes.iterator();
				while(marketTypeIterator.hasNext()){
					Market marketType = marketTypeIterator.next();
					if(marketType.getTitle().equalsIgnoreCase(AdminConstants.RENTAL) || marketType.getTitle().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						marketTypeIterator.remove();
					}
				}
				Iterator<Market> marketApplicationIterator = listOfMarketApplications.iterator();
				while(marketApplicationIterator.hasNext()){
					Market marketApplication = marketApplicationIterator.next();
					if(marketApplication.getTitle().equalsIgnoreCase(AdminConstants.RENTAL) || marketApplication.getTitle().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)){
						marketApplicationIterator.remove();
					}
				}
			}
		}
		
        
        listOfCompetitorMakes = this.warrantyService.listCompetitorMake();
		listOfCompetitorModels = this.warrantyService.listCompetitorModel();
        listOfCompetitionTypes = this.warrantyService.listCompetitionTypes();
        this.isModifyDRorETR = true;
       // listOfSalesPersons = this.warrantyUtil.getSalesPersonsForDealer(getWarranty().getForItem(), null);
        listOfContractCodes =this.warrantyService.listContractCode();
        listOfInternalInstallTypes = this.warrantyService.listInternalInstallType();
        listofMaintenanceContracts=this.warrantyService.listMaintenanceContract();
        listOfIndustryCodes=this.warrantyService.listIndustryCode();
        setTransferReportType();
		populateCustomerTypes();
		
	}

	public String displayTransferReportDetails() {
		this.isModifyDRorETR = Boolean.FALSE;
		if (this.transactionId != null) {
			this.warranty = this.warrantyService
					.findByTransactionId(this.transactionId);
			operator=customerService.findCustomerById(warranty.getCustomer().getId());
			this.warranty.setOperator(customerService.findCustomerById(warranty.getCustomer().getId()));
			if (isAdditionalInformationDetailsApplicable() || getWarranty().getMarketingInformation()!=null) {
				this.marketingInformation = this.warranty.getMarketingInformation();
			}
			setTransferReportType();			
		}

		return SUCCESS;
	}

	public boolean canModifyDRorETR() {
		return canMondifyBasedOnWindowPeriod() && canMondifyBasedOnOutStandingFMod()
		 			&& canMondifyBasedOnClaims();
	}
	
	public boolean canMondifyBasedOnClaims(){
		return this.claimService
		.findAllPreviousClaimsForItem(warranty.getForItem().getId()).size() == 0 ? true:false;		
	}
	
	public boolean canMondifyBasedOnOutStandingFMod(){
		return this.campaignRepository
			.findPendingNotificationsForItem(warranty.getForItem()).size() == 0 ? true:false;
	}
	
	
	public boolean canMondifyBasedOnWindowPeriod(){
		String dateToBeConsideredForDRDeletion = configParamService
		.getStringValue(ConfigName.DATE_TO_BE_CONSIDERED_FOR_DELIVERY_REPORT_DELETION
				.getName());
		int daysToBeConsideredForDRDeletion = configParamService.getLongValue(
				ConfigName.DAYS_TO_BE_CONSIDERED_FOR_DELIVERY_REPORT_DELETION
				.getName()).intValue();
		CalendarDate todaysDate = Clock.today();		
		if ("Delivery Date".equals(dateToBeConsideredForDRDeletion)
				&& this.warranty.getDeliveryDate() != null) {
			CalendarDate deleteDRDateBasedOnDD = this.warranty.getDeliveryDate()
			.plusDays(daysToBeConsideredForDRDeletion);
			if (!deleteDRDateBasedOnDD.isBefore(todaysDate)) {
				return true;
			} else {
				return false;
			}
		} else if (this.warranty.getForTransaction().getTransactionDate() != null) {
			CalendarDate deleteDRDateBasedOnSubmitDate = this.warranty
			.getForTransaction().getTransactionDate().plusDays(
					daysToBeConsideredForDRDeletion);
			if (!deleteDRDateBasedOnSubmitDate.isBefore(todaysDate)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	private void setTransferReportType() {
		if (InvTransationType.DR.getTransactionType().equals(
				this.warranty.getForTransaction().getInvTransactionType()
						.getTrnxTypeValue())
				|| InvTransationType.DR_MODIFY.getTransactionType().equals(
						this.warranty.getForTransaction()
								.getInvTransactionType().getTrnxTypeValue())
				|| (isCustomerDetailsNeededForDR_Rental()?InvTransationType.DR_RENTAL.getTransactionType().equals(
										this.warranty.getForTransaction()
												.getInvTransactionType().getTrnxTypeValue()):false)
				||  InvTransationType.DEMO.getTransactionType().equals(
						this.warranty.getForTransaction()
						.getInvTransactionType().getTrnxTypeValue())) {
			this.deliveryReport = true;
		}
	}
	
	private void resetAdditionalAttributes(InventoryItem inventoryItem)
	{
		inventoryItem.setInstallationDate(null);
		inventoryItem.setOem(null);
		inventoryItem.setInstallingDealer(null);
		inventoryItem.setVinNumber(null);
		inventoryItem.setOperator(null);
		inventoryItem.setFleetNumber(null);
		inventoryItem.setInstallationDate(null);
		inventoryItem.setDeliveryDate(null);
	}

	public String modifyWarranty()  {
		List<InventoryItem> inventoriesToBeDeleted = new ArrayList<InventoryItem>();
		setTransferReportType();
		if (this.customer != null) {
			this.warranty.setCustomer(this.customer);
		}

		if (this.addressForTransfer != null) {
			this.warranty.setAddressForTransfer(this.addressForTransfer);
		}
		if (this.deleteWarranty) {
			deleteWarranty();
			if (this.deliveryReport) {
			resetAdditionalAttributes(warranty.getForItem());
			List<InventoryItem> inventoryItems = inventoryService.getPartsToBeDeleted(warranty.getForItem());		
			if (inventoryItems.size() > 0) {
				inventoriesToBeDeleted.addAll(inventoryItems);
			}  
			}
		} else if (this.customer != null
				&& this.warranty.getAddressForTransfer() != null) {
			// Creating a DR_MODIFY
			if(this.deliveryReport){
				register();
			}
			//setWarranty(inventoryItem.getLatestWarranty());
			else {
				InventoryTransaction transaction = new InventoryTransaction();
				transaction
				.setInvTransactionType(this.invTransactionTypeConverter
						.fetchByName(InventoryTransaction.ETR_MODFY));
			transaction.setTransactionOrder(new Long(this.warranty.getForItem()
					.getTransactionHistory().size() + 1));
			transaction
					.setSeller(this.warranty.getForTransaction().getSeller());
			transaction.setBuyer(this.customer);
			transaction.setOwnerShip(this.warranty.getForTransaction()
					.getOwnerShip());
			transaction.setTransactionDate(Clock.today());
			transaction.setTransactedItem(this.warranty.getForItem());
			transaction.setStatus(BaseDomain.ACTIVE);
			this.warranty.setForTransaction(transaction);
			this.warranty.getForItem().getTransactionHistory().add(transaction);
			this.warranty.getForItem().setLatestBuyer(this.customer);
		  }
		}		
		setCoverageOnWarranty();
		if (isAdditionalInformationDetailsApplicable() || getWarranty().getMarketingInformation()!=null) { 
			this.warranty.setMarketingInformation(this.marketingInformation);			
		}	
		//updating sic code of a customer based on selected industry code
		updateCustomersSicCode();
        DocumentTransportUtils.markDocumentsAsAttached(inventoryItem.getLatestWarranty().getAttachments());
		getWarrantyService().update(this.warranty);
		this.inventoryService.deleteInventoryItems(inventoriesToBeDeleted);	
		if(hasErrors()){
			return INPUT;
		}
		else{
			if (this.deleteWarranty) {
				if (this.deliveryReport) {
					addActionMessage("message.deleteDR.success");
				} else {
					addActionMessage("message.deleteETR.success");
				}

			} else {
				if (this.deliveryReport) {
					addActionMessage("message.modifyDR.success");
				} else {
					addActionMessage("message.modifyETR.success");
				}
			}
			return SUCCESS;

		}
	}
	
	private void updateCustomersSicCode() {	
			if(null != this.marketingInformation && null != this.marketingInformation.getIndustryCode() && null != customer){
				operator=customerService.findCustomerById(customer.getId());
				operator.setSiCode(marketingInformation.getIndustryCode().getIndustryCode());
				customerService.updateCustomer(operator);
			}
		
	}

	private void register() {
		confirmRegistration();
		InventoryItem inventoryItem = this.warranty.getForItem();
		//prepareInventoryItem(inventoryItem);
		if((isCustomerDetailsNeededForDR_Rental()?false:getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL)) /*|| getAddressBookType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)*/){//SLMSPROD-1174, demo should work in the same way as that of End customer
			this.marketingInformation = new MarketingInformation();
			if(isCustomerDetailsNeededForDR_Rental() && this.addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)){
				this.marketingInformation.setTransactionType(this.warrantyService.findTransactionType(AdminConstants.TRANSACTION_TYPE_DEALER_RENTAL));
				this.marketingInformation.setMarket(this.marketService.findMarketTypeByTitle(AdminConstants.RENTAL));
                if(this.marketingInformation.getMarket() != null){
				    this.marketingInformation.setApplication(this.marketService.findMarketApplicationByTitle(this.marketingInformation.getMarket().getId(), AdminConstants.RENTAL));
                }
			}else if(this.addressBookType.equalsIgnoreCase(AdminConstants.DEMO)){
				this.marketingInformation.setTransactionType(this.warrantyService.findTransactionType(AdminConstants.DEMO));
				this.marketingInformation.setMarket(this.marketService.findMarketTypeByTitle(AdminConstants.DEMO));
                if(this.marketingInformation.getMarket() != null){
				    this.marketingInformation.setApplication(this.marketService.findMarketApplicationByTitle(this.marketingInformation.getMarket().getId(), AdminConstants.DEMO));
                }
			}
			this.getMarketingInformation().setCustomerFirstTimeOwner(Boolean.TRUE);
		}
		boolean isManualApproval=false;
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
       	for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {  
       		inventoryItemMapping.setWarrantyDeliveryDate(this.warranty.getForItem().getDeliveryDate());
       		inventoryItemMapping.getInventoryItem().setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
		//	inventoryItem = setInventoryItemAttributes(inventoryItem, inventoryItemMapping);
			createWarranty(inventoryItemMapping,inventoryItem); 
			//this.warranty.getPolicies().clear();
			inventoryItem.setPendingWarranty(true);
			if(isDisclaimerAvailable(inventoryItemMapping)){
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
       		}
          DocumentTransportUtils.markDocumentsAsAttached(warranty.getAttachments());
            itemsForTask.add(inventoryItem);
		}
		if (!itemsForTask.isEmpty() && !hasErrors()) {			
			warrantyService.createInventoryAndCreateWarrantyReport(itemsForTask, isManualApproval);		
		} 
	}

	private void confirmRegistration() {
		// TODO Auto-generated method stub
 		this.inventoryItemMappings.removeAll(Collections.singletonList(null));
		if ((isAdditionalInformationDetailsApplicable() || getMarketingInformation()!=null) &&(isCustomerDetailsNeededForDR_Rental()?true:!this.addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)) /*&& !this.addressBookType.equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)*/) {
				validateMarketingInfo();
		}
		//boolean isCustomerSet = warrantyAction.validateCustomer();
	//	removeUnselectedItems();
		if (this.inventoryItemMappings.isEmpty()) {
			addActionError("error.noItemsSelected");
		} else {
			List<InventoryItem> inventories = new ArrayList<InventoryItem>();
			for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
				Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
				InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
				this.warrantyUtil.validateStolenInventory(inventoryItemMapping.getWarrantyDeliveryDate(), inventoryItemMapping.getInstallationDate(), inventoryItem, errorCodeMap);
				if(!errorCodeMap.isEmpty()){
					setErrorCodesMapToActionErrors(errorCodeMap);
				}
				this.warrantyUtil.validateMajorComponents(inventoryItem, errorCodeMap);
				this.warrantyUtil.validateAdditionalComponents(inventoryItem, errorCodeMap);
				setErrorCodesMapToActionErrors(errorCodeMap);		
				if (isMarketInfoApplicable()) {
					validateMarketInfo(inventoryItemMapping.getSelectedMarketingInfo());
				}
                inventoryItem.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
              //  removeUnselectedPolicies(inventoryItemMapping);
				// We can't inline this in the next if check, since it won't get
				// called if customer is not set.
                if(!inventories.contains(inventoryItem)) {	    			
                	inventories.add(inventoryItem);
    			} else {
    				addActionError("error.common.duplicateUnit");
    			}
				boolean inventoryItemIsValid = validateInventoryItemMapping(inventoryItemMapping);
				//Collections.sort(inventoryItemMapping.getSelectedPolicies());
				/*if (validateCustomer() && inventoryItemIsValid) {
					if(isDisclaimerAvailable(inventoryItemMapping) && !inventoryItemMapping.isDisclaimerAccepted()){
	                	addActionError("error.disclaimer.notAccepted");
	                }
					if(inventoryItemMapping.isWaiverInformationEditable()
								&& inventoryItemMapping.isDisclaimerAccepted())
						validateWaiverInformation(inventoryItemMapping.getDieselTierWaiver());
					}*/
				List<RegisteredPolicy> selectedPolicies = inventoryItemMapping.getSelectedPolicies();
				List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(selectedPolicies.size());
				for (RegisteredPolicy registeredPolicy : selectedPolicies) {
					selectedPolicyDefs.add(registeredPolicy
							.getPolicyDefinition());
				}
				if (!hasErrors()) {
					try {
						if (isInstallingDealerEnabled()) {
							inventoryItemMapping.setSelectedPolicies(this.warrantyUtil.createPolicies(
									this.installingDealer, selectedPolicyDefs, inventoryItem, true));
						} else {
							inventoryItemMapping.setSelectedPolicies(this.warrantyUtil.createPolicies(
									this.forDealer, selectedPolicyDefs, inventoryItem, true));
						}
					} catch (PolicyException e) {
						addActionError("error.registeringWarrantyForItem");
					}
				}
			}
		}
	}
	


	private void createWarranty(
			MultipleInventoryAttributesMapper inventoryItemMapping,
			InventoryItem invItem) {

		String multiDRETRNumber = null;
		if (!inventoryItemMappings.get(0).getInventoryItem()
				.getPendingWarranty()) {
			multiDRETRNumber = warrantyService.getWarrantyMultiDRETRNumber();
		}
		Warranty warranty = invItem.getDraftWarranty() == null
				&& this.warranty != null && this.warranty.getId() != null ? this.warranty
				: invItem.getDraftWarranty();
		if ((isCustomerDetailsNeededForDR_Rental()?false:getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL))
				/*|| getAddressBookType().equalsIgnoreCase(
						AdminConstants.CUSTOMER_TYPE_DEMO)*/) {
			// this.addressForTransfer =
			// warrantyUtil.populateAddressForTransfer(forDealer.getAddress());
			this.setCustomer(forDealer);
			// this.setCustomerAddress(forDealer.getAddress());
			// showCustomer();
		}
		if (invItem.getDraftWarranty() == null) {
			warranty = new Warranty();
			 warranty.setDraft(false);
			warranty.setInventoryItem(invItem);
			if (this.warranty != null && this.warranty.getId() != null)
				multiDRETRNumber = this.warranty.getMultiDRETRNumber();
		} else {
			multiDRETRNumber = warranty.getMultiDRETRNumber();
		}
		warranty = setWarrantyAttributes(warranty, inventoryItemMapping);
		warranty.setFiledDate(Clock.today());
		//warranty.setMultiDRETRNumber(multiDRETRNumber);
		warranty.setStatus(WarrantyStatus.SUBMITTED);
	}
			
	/*private InventoryItem setInventoryItemAttributes(
			InventoryItem invItem,
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		if(inventoryItemMapping.getOem()!=null && inventoryItemMapping.getOem().getCode()!=null)
		invItem.setOem(inventoryItemMapping.getOem());
		invItem.setInstallingDealer(this.warranty.getInstallingDealer());
		invItem.setVinNumber(inventoryItemMapping.getEquipmentVIN());
		invItem.setOperator(this.getOperator());
		invItem.setFleetNumber(inventoryItemMapping.getFleetNumber());
		invItem.setInstallationDate(inventoryItemMapping.getInstallationDate());
		//invItem.setDelivery);
		return invItem;
	}*/

	

	private Warranty setWarrantyAttributes(Warranty warranty,
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		warranty.getAttachments().clear();
		if (inventoryItemMapping.getAttachments() != null)
			warranty.getAttachments().addAll(inventoryItemMapping.getAttachments());
	//	if (inventoryItemMapping.getOem() != null && inventoryItemMapping.getOem().getCode() != null)
	//		warranty.setOem(inventoryItemMapping.getOem());
		warranty.setInstallationDate(inventoryItemMapping.getInstallationDate());
		if (!inventoryItemMapping.getSelectedMarketingInfo().isEmpty()) {
			for (SelectedAdditionalMarketingInfo selectedMarketingInfo : inventoryItemMapping
					.getSelectedMarketingInfo()) {
				selectedMarketingInfo.setForWarranty(warranty);
			}
			warranty.getSelectedAddtlMktInfo().clear();
			warranty.getSelectedAddtlMktInfo().addAll(inventoryItemMapping.getSelectedMarketingInfo());			
		}
		if (isAdditionalInformationDetailsApplicable()) {
			warranty.setMarketingInformation(this.marketingInformation);
		}		
/*		if(AdminConstants.CUSTOMER_TYPE_DEMO.equals(this.getAddressBookType())){
			warranty.setTransactionType(this.invTransactionTypeConverter
					.fetchByName(InvTransationType.DEMO.name()));
		}
		else{*/
			warranty.setTransactionType(this.invTransactionTypeConverter
					.fetchByName(InvTransationType.DR_MODIFY.name()));	
		//}
		warranty.getPolicies().clear();
		WarrantyAudit warrantyAudit = new WarrantyAudit();
		warrantyAudit.setSelectedPolicies(createSelectedPolicies(inventoryItemMapping));
		warranty.getWarrantyAudits().add(warrantyAudit);
		warranty.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
		warranty.setAddressForTransfer(this.addressForTransfer);
		warranty.setCustomer(this.customer);
		warranty.setCustomerType(this.addressBookType);
		warranty.setOperator(this.operator);
	//	warranty.setOperatorType(this.addressBookTypeForOperator);
	//	warranty.setOperatorAddressForTransfer(this.operatorAddressForTransfer);
		warranty.setFiledBy(getLoggedInUser());
		warranty.setForDealer(forDealer);
		warranty.setEquipmentVIN(inventoryItemMapping.getEquipmentVIN());
		warranty.setFleetNumber(inventoryItemMapping.getFleetNumber());
		if(this.getInstallingDealer()!=null && this.getInstallingDealer().getId()!=null){
			warranty.setInstallingDealer(this.getInstallingDealer());	
		}
		warranty.getForItem().setLatestWarranty(warranty);
		warranty.getMarketingInformation();
		//warranty.setSalesPerson(this.salesPerson);
		return warranty;
	
	}
	
	
	private List<RegisteredPolicy> createSelectedPolicies(MultipleInventoryAttributesMapper inventoryItemMapping) {
		List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(inventoryItemMapping
				.getSelectedPolicies().size());
		for (RegisteredPolicy registeredPolicy : inventoryItemMapping.getSelectedPolicies()) {
			if (registeredPolicy != null) {
				selectedPolicyDefs.add(registeredPolicy.getPolicyDefinition());
			}
		}
		try {
			if (isInstallingDealerEnabled()) {
				if(this.installingDealer!=null && this.installingDealer.getId()!=null)
				{
				return this.warrantyUtil.createPolicies(this.installingDealer, selectedPolicyDefs, inventoryItemMapping
						.getInventoryItem(), true);
				}
				else
				{
					return Collections.emptyList() ;
				}
			} else {
				if(this.forDealer!=null && this.forDealer.getId()!=null)
				{
				return this.warrantyUtil.createPolicies(this.forDealer, selectedPolicyDefs, inventoryItemMapping
						.getInventoryItem(), true);
				}
				else
				{
					 return Collections.emptyList() ;
				}
			}
		} catch (PolicyException e) {
			addActionError("error.registeringWarrantyForItem");
			return null;
		}
	}
	
	private void prepareInventoryItem(InventoryItem inventoryItem) {
			MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
			mapper.setInventoryItem(inventoryItem);
            mapper.setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
            mapper.setInstallationDate(inventoryItem.getInstallationDate());
            Warranty warranty = inventoryItem.getLatestWarranty();
            if(this.warranty.getInstallingDealer()!=null){
                setInstallingDealer(new HibernateCast<ServiceProvider>().cast(this.warranty.getInstallingDealer()));
            }
            //this.installingDealer=(ServiceProvider) this.warranty.getInstallingDealer();
			/*if (warranty != null) {
				for (RegisteredPolicy registeredPolicy : warranty.getPolicies()) {
					mapper.getSelectedPolicies().add(registeredPolicy);
				}
			}*/
	        mapper.setAttachments(warranty.getAttachments());
			this.setSelectedPolicies(mapper.getSelectedPolicies());
			prepareApplicablePolicies(mapper);
			/*if(inventoryItem.getPreOrderBooking()==Boolean.TRUE && isCustomerExists()){
	        	addressBookType=AdminConstants.END_CUSTOMER;
	        	this.setCustomer(inventoryItem.getLatestBuyer());
	        	//this.setCustomerAddress(inventoryItem.getLatestBuyer().getAddress());
	        	//showCustomer();
	        	//this.setPreOrderBooking("true");
	        	mapper.setDieselTierWaiver(inventoryItem.getWaiverDuringDr());
	        	if(inventoryItem.getWaiverDuringDr() != null)
	        	mapper.getDieselTierWaiver().setDisclaimer(
	        			inventoryItem.getWaiverDuringDr().getI18NDisclaimer());
	        	addActionWarning("warning.preOrderBooking");
	        }*/
			this.inventoryItemMappings.add(mapper);
		}
	


	private boolean isCustomerExists() {
		if(customer!=null && InstanceOfUtil.isInstanceOfClass(Customer.class, customer)){
			return true;
		}
	return false;
	}

	private void prepareApplicablePolicies(
			MultipleInventoryAttributesMapper mapper) {

		InventoryItem inventoryItem = mapper.getInventoryItem();
		Long hoursOnMachine = inventoryItem.getHoursOnMachine();
		if (inventoryItem.getSerialNumber()!=null&&inventoryItem.getDeliveryDate() != null
                && hoursOnMachine != null && hoursOnMachine > 0 && this.installingDealer!=null) {
			
			mapper.setAvailablePolicies(this.warrantyUtil.fetchAvailablePolicies(inventoryItem, this.addressBookType, this.installingDealer, false));
			mapper.setExtendedPolicies(this.warrantyUtil.fetchPurchasedExtendedPolicies(inventoryItem));
			for (RegisteredPolicy regPolicy : mapper.getExtendedPolicies()) {
				if (!mapper.getAvailablePolicies().contains(regPolicy))
					mapper.getAvailablePolicies().add(regPolicy);
			}		
		}
			
	}
	
	private boolean isPolicyApplicableForDemo(InventoryItem inventoryItem){
		if(isAdditionalInformationDetailsApplicable()
				&& noPolicyForDemoTruckWithMoreThan80Hours()
				&& this.marketingInformation != null
				&& this.marketingInformation.getContractCode() != null
				&& this.marketingInformation.getContractCode().getContractCode().equalsIgnoreCase(AdminConstants.DEMO)
				&& inventoryItem.getHoursOnMachine() > 80){
			return false;
		}
		return true;
	}
	
	public String listApplicablePolicy() {
		
		//InventoryItem inventory = this.inventoryItem;
		validateInventoryItem(this.inventoryItem);
        /*MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
        mapper.setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
        mapper.setInstallationDate(inventoryItem.getInstallationDate());
        mapper.setInventoryItem(inventoryItem);
        validateDeliveryAndInstallationDate(mapper);
        */
		if(this.getInventoryItemMappings().size()>0)
		{
			this.getInventoryItemMappings().get(0).setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
			this.getInventoryItemMappings().get(0).setInstallationDate(inventoryItem.getInstallationDate());
			this.getInventoryItemMappings().get(0).setInventoryItem(inventoryItem);
		    validateDeliveryAndInstallationDate(this.getInventoryItemMappings().get(0));
		}		
        if (hasErrors()) {
			return INPUT;
		}

        	if(isPolicyApplicable(this.inventoryItem) && isPolicyApplicableForDemo(this.inventoryItem)){

        		if(this.isInstallingDealerEnabled() && this.installingDealer!=null) {
        			this.availablePolicies = this.warrantyUtil.fetchAvailablePolicies(this.inventoryItem, this.addressBookType, this.installingDealer, false);
        		} else {
        			ServiceProvider dealer = this.forDealer==null?new HibernateCast<ServiceProvider>()
        					.cast(inventoryItem.getDealer()):this.forDealer;
        					this.availablePolicies = this.warrantyUtil.fetchAvailablePolicies(this.inventoryItem, this.addressBookType, dealer, false);
        		}
        		/*this.extendedPolicies = this.warrantyUtil.fetchPurchasedExtendedPolicies(this.inventoryItem);

        		for (RegisteredPolicy regPolicy :extendedPolicies) {

        			if (!this.availablePolicies.contains(regPolicy))
        				this.availablePolicies.add(regPolicy);
        		}*/
        		this.inventoryItemMappings.get(0).setSelectedPolicies(availablePolicies);
            if(isPolicyMandatory() && this.availablePolicies.isEmpty()){
            	addActionError("message.noApplicablePolicy");
            }
        }
		return SUCCESS;
	}

private boolean isPolicyApplicable(InventoryItem inventoryItem){
	return !((!getLoggedInUser().isInternalUser() 
			|| (getLoggedInUser().isInternalUser() 
					&& inventoryItem.getLatestWarranty()!=null && !inventoryItem.getLatestWarranty().getStatus().getStatus().equals("Submitted")))
					/*&& isDemoInventory(inventoryItem)*/ && inventoryItem.getHoursOnMachine().intValue() > 80);
}

// returning policy end date based on inventory delivery date and months coverage
public CalendarDate getPolicyEndDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
	CalendarDate endDateBasedOnMachineShipmentDateAndMonthsCoveredBySD = inventoryItem.getShipmentDate().
							plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment());
	
	CalendarDate endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD = inventoryItem.getDeliveryDate().plusMonths(
			policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery());
	
	CalendarDate endDateBasedOnMachineBuildDateAndMonthsCoveredByBD = inventoryItem.getBuiltOn() !=null ?inventoryItem.getBuiltOn().
			plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromBuildDate()) : endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD;
	
	CalendarDate warrantyStartDate = inventoryItem.getDeliveryDate();
	CalendarDate warrantyEndDate;		
	if (endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD.isBefore(endDateBasedOnMachineShipmentDateAndMonthsCoveredBySD) && endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD.isBefore(endDateBasedOnMachineBuildDateAndMonthsCoveredByBD)) {
		warrantyEndDate= endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD;
	
} else if(endDateBasedOnMachineShipmentDateAndMonthsCoveredBySD.isBefore(endDateBasedOnMachineDeliveryDateAndMonthsCoveredByDD) && endDateBasedOnMachineShipmentDateAndMonthsCoveredBySD.isBefore(endDateBasedOnMachineBuildDateAndMonthsCoveredByBD)) {
	warrantyEndDate= endDateBasedOnMachineShipmentDateAndMonthsCoveredBySD;
}else {
	warrantyEndDate=endDateBasedOnMachineBuildDateAndMonthsCoveredByBD;
}
	
	
	return warrantyStartDate.isAfter(warrantyEndDate)?
		warrantyStartDate:warrantyEndDate;		
}

private boolean isDemoInventory(InventoryItem inventoryItem){
	if(inventoryItem != null && !CollectionUtils.isEmpty(inventoryItem.getTransactionHistory())){
		for(InventoryTransaction inventoryTransaction : inventoryItem.getTransactionHistory()){
			if(InvTransationType.DEMO.getTransactionType().equals(inventoryTransaction.getInvTransactionType().getTrnxTypeValue())){
				return true;
			}
		}
	}
	return false;
}

	private boolean validateDeliveryAndInstallationDate(
		MultipleInventoryAttributesMapper inventoryItemMapper) {
		 CalendarDate deliveryDate =  inventoryItemMapper.getWarrantyDeliveryDate();
	        InventoryItem inventoryItem =  inventoryItemMapper.getInventoryItem();
	        CalendarDate installationDate = inventoryItemMapper.getInstallationDate();
	        
	        Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
	        this.warrantyUtil.validateDeliveryAndInstallationDate(deliveryDate, installationDate, inventoryItem, errorCodeMap);
	        setErrorCodesMapToActionErrors(errorCodeMap);
	        if(this.isInstallingDealerEnabled() && installationDate==null)
	        {
	        	addActionError("error.warranty.installationDateRequired");
	        	return false;
	        }
	        if(deliveryDate==null)
	        {
	        	addActionError("error.deliveryDateNotFound",inventoryItem.getSerialNumber());
	        }
	        return errorCodeMap.isEmpty();
	
}

	public String deleteWarrantyForMajorComponent() {
		if (this.transactionId != null) {
			this.warranty = this.warrantyService.findByTransactionId(this.transactionId);
			InventoryItem inventoryItem = this.warranty.getForItem();
			InventoryTransaction transaction = createTransactionByType(this.invTransactionTypeConverter
					.fetchByName(InvTransationType.DR_DELETE.name()));
			inventoryItem.setRegistrationDate(null);
			this.warranty.setForTransaction(transaction);
			this.warranty.setStatus(WarrantyStatus.DELETED);
			inventoryItem.getTransactionHistory().add(transaction);
			inventoryItem.setSerialNumber(inventoryItem.getSerialNumber() + "_" + TWMSWebConstants.DEACTIVATED + "_"
					+ inventoryItem.getId());
			this.warranty.getForItem().getD().setActive(false);
			InventoryItem machine = inventoryService.findInventoryItemForMajorComponent(inventoryItem.getId());
			if (machine != null) {
				Iterator<InventoryItemComposition> iterator = machine.getComposedOf().iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getPart().equals(inventoryItem))
						iterator.remove();
				}
			}
			this.warrantyService.updateWarrantyAndInventoryBOM(machine, warranty);
			addActionMessage("message.deleteDR.success");
		}
		return SUCCESS;
	}

	private InventoryTransaction createTransactionByType(InventoryTransactionType inventoryTransactionType) {
		InventoryTransaction transaction = new InventoryTransaction();
		this.warranty.getForItem().setRegistrationDate(null);
		transaction.setInvTransactionType(inventoryTransactionType);
		transaction.setSeller(this.warranty.getCustomer());
		transaction.setBuyer(this.warranty.getForTransaction().getSeller());
		transaction.setOwnerShip(this.warranty.getForTransaction().getOwnerShip());
		transaction.setStatus(BaseDomain.ACTIVE);
		transaction.setTransactionOrder(new Long(this.warranty.getForItem().getTransactionHistory().size() + 1));
		transaction.setTransactionDate(Clock.today());
		transaction.setTransactedItem(this.warranty.getForItem());
		return transaction;
	}

	private void setCoverageOnWarranty() {
		for (RegisteredPolicy selectedPolicy : this.selectedPolicies) {
			if (selectedPolicy != null
					&& selectedPolicy.getPolicyDefinition() != null) {
				RegisteredPolicy policy = new RegisteredPolicy();
				policy
						.setPolicyDefinition(selectedPolicy
								.getPolicyDefinition());
				try {
					policy.setWarrantyPeriod(selectedPolicy
							.getPolicyDefinition().warrantyPeriodFor(
									this.warranty.getForItem()));
					computePriceForPolicy(this.warranty.getForItem(), policy);
					this.warrantyService.register(this.warranty, selectedPolicy
							.getPolicyDefinition(), policy.getWarrantyPeriod(),
							policy.getPrice(), warranty.getModifyDeleteComments(), null, null);
				} catch (PolicyException e) {
					logger.error("Error in adding policy " + e.getMessage());
				}
			}
		}
	}

	private void deleteWarranty() {

		InventoryTransaction transaction = new InventoryTransaction();
		List<String> transactionTypes = new ArrayList<String>();
		transactionTypes.add(InvTransationType.IB.getTransactionType());
		transactionTypes.add(InvTransationType.DEALER_TO_DEALER
				.getTransactionType());
		
		if (this.deliveryReport) {
			// Creating a DR_DELETE with buyer,seller inversed
			this.warranty.getForItem().setType(new InventoryType("STOCK"));
			this.warranty.getForItem().setRegistrationDate(null);
			transaction.setInvTransactionType(this.invTransactionTypeConverter
					.fetchByName(InvTransationType.DR_DELETE.name()));
			transaction.setSeller(this.warranty.getCustomer());
			transaction.setBuyer(this.warranty.getForTransaction().getSeller());
			transaction.setOwnerShip(this.warranty.getForTransaction()
					.getOwnerShip());
			this.warranty.getForItem().setLatestBuyer(warranty.getForItem().getCurrentOwner());
			InventoryTransaction latestIBD2DTrnx = warranty.getForItem()
					.getLatestTransactionForATransactionType(transactionTypes);
			warranty.getForItem()
					.setCurrentOwner( this.orgService
									.findOrgById(latestIBD2DTrnx.getOwnerShip()
											.getId()));			
			deleteRequestForExtension();
			
			InventoryTransaction latestIBTransaction = this.warranty.getForItem().getLatestTransactionForATransactionType("IB");
			this.warranty.getForItem().setHoursOnMachine(latestIBTransaction.getHoursOnMachine() == null? new Long(0) : latestIBTransaction.getHoursOnMachine());
			
		} else {
			Long etrCounter = this.warranty.getForItem().getNumberOfETRs();
			if (etrCounter != null && etrCounter.longValue() == 1) {
				// admin is deleting the last ETR, so ownership state shld be
				// reverted back
				this.warranty.getForItem().setOwnershipState(
						getInventoryService().findOwnershipStateByName(
								OwnershipState.FIRST_OWNER.getName()));
			}

			// Creating a ETR_DELETE with buyer,seller inversed
			transaction.setInvTransactionType(this.invTransactionTypeConverter
					.fetchByName(InventoryTransaction.ETR_DELETE));
			transaction.setSeller(this.warranty.getForTransaction().getBuyer());
			transaction.setBuyer(this.warranty.getForTransaction().getSeller());
			transaction.setOwnerShip(this.warranty.getForTransaction()
					.getOwnerShip());
			this.warranty.getForItem().setLatestBuyer(
					this.warranty.getForTransaction().getSeller());
			InventoryTransaction latestDRTransaction = this.warranty.getForItem().getLatestTransactionForATransactionType(InvTransationType.DR.getTransactionType());
			this.warranty.getForItem().setHoursOnMachine(latestDRTransaction.getHoursOnMachine() == null? new Long(0) : latestDRTransaction.getHoursOnMachine());
		}
		transaction.setStatus(BaseDomain.ACTIVE);
		transaction.setTransactionOrder(new Long(this.warranty.getForItem()
				.getTransactionHistory().size() + 1));
		transaction.setTransactionDate(Clock.today());
		transaction.setTransactedItem(this.warranty.getForItem());
		this.warranty.setForTransaction(transaction);
		this.warranty.setStatus(WarrantyStatus.DELETED);
		this.warranty.getForItem().getTransactionHistory().add(transaction);
	}

	private void deleteRequestForExtension() {
		WarrantyCoverageRequest wcr = warrantyCoverageRequestService
				.findByInventoryItemId(this.warranty.getForItem().getId());
		if (wcr != null) {
			try {
				warrantyCoverageRequestService.delete(wcr);
			} catch (Exception e) {
				logger.error(
						"Error deleting Warranty Coverage Request for inventory item with id "
								+ this.warranty.getForItem().getId(), e);
			}
		}
	}

	public List<RegisteredPolicy> getTransferablePoliciesFromPrevWarranty() {
		List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
		Warranty earlierWarranty = this.warranty.getForItem()
				.getPreviousWarranty(this.warranty.getId());
		List<String> etrTransactionTypes = new ArrayList<String>();
		etrTransactionTypes.add(InvTransationType.ETR.getTransactionType());
		etrTransactionTypes.add(InvTransationType.ETR_MODIFY
				.getTransactionType());
		List<Warranty> etrWarranties = this.warranty.getForItem()
				.getWarranties(etrTransactionTypes);
		List<String> drTransactionTypes = new ArrayList<String>();
		drTransactionTypes.add(InvTransationType.DR.getTransactionType());
		drTransactionTypes
				.add(InvTransationType.DR_MODIFY.getTransactionType());
		drTransactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		Warranty deliveryWarranty = this.warranty.getForItem().getWarranties(
				drTransactionTypes).get(0);
		if (earlierWarranty != null) {
			for (RegisteredPolicy registeredPolicy : earlierWarranty
					.getPolicies()) {
				if (registeredPolicy.getPolicyDefinition().getTransferDetails()
						.isTransferable()
						&& !registeredPolicy.getWarrantyPeriod().getTillDate()
								.isBefore(
										this.warranty.getForItem()
												.getDeliveryDate())
						&& isWithinWindowPeriod(deliveryWarranty,
								registeredPolicy, this.warranty.getForItem())
						&& isWithinMaxNoTransfer(etrWarranties,
								registeredPolicy, this.warranty.getForItem())
						&& !isPresentInCurrentWarranty(registeredPolicy)
						&& isApplicableCustomerType(registeredPolicy
								.getPolicyDefinition())) {
					policies.add(registeredPolicy);
				}
			}
		}
		return policies;
	}

	private boolean isPresentInCurrentWarranty(
			RegisteredPolicy earliesWarrantyPolicy) {
		for (RegisteredPolicy registeredPolicy : this.warranty.getPolicies()) {
			if (registeredPolicy.getPolicyDefinition().getId().longValue() == earliesWarrantyPolicy
					.getPolicyDefinition().getId().longValue()) {
				return true;
			}
		}
		return false;
	}

	// TODO the following 3 APIs should be moved to a common place.

	private boolean isWithinWindowPeriod(Warranty deliveryWarranty,
			RegisteredPolicy registeredPolicy, InventoryItem forItem) {
		boolean isWithinWindowPeriod = true;

		Long windowPeriod = registeredPolicy.getPolicyDefinition()
				.getTransferDetails().getWindowPeriod();
		if (windowPeriod != null) {
			isWithinWindowPeriod = deliveryWarranty.getDeliveryDate()
					.plusMonths(windowPeriod.intValue()).isAfter(
							forItem.getDeliveryDate());
		}

		return isWithinWindowPeriod;
	}

	private boolean isWithinMaxNoTransfer(List<Warranty> etrWarranties,
			RegisteredPolicy registeredPolicy, InventoryItem forItem) {
		boolean isWithinMaxNoTransfer = true;
		int noOfTimesTransferred = 0;
		Long maxTransfer = registeredPolicy.getPolicyDefinition()
				.getTransferDetails().getMaxTransfer();
		if (etrWarranties != null && etrWarranties.size() > 0) {
			if (maxTransfer != null) {
				isWithinMaxNoTransfer = false;
				for (Warranty warranty : etrWarranties) {
					if (warranty.getForTransaction().getBuyer().isCustomer()) {
						Set<RegisteredPolicy> policies = warranty.getPolicies();
						for (RegisteredPolicy policy : policies) {
							if (policy.getPolicyDefinition()
									.getPolicyDefinition().getId().equals(
											registeredPolicy
													.getPolicyDefinition()
													.getId())) {
								noOfTimesTransferred++;
							}
						}
					}
				}
			}
			if (maxTransfer != null && noOfTimesTransferred < maxTransfer) {
				isWithinMaxNoTransfer = true;
			}
		}
		return isWithinMaxNoTransfer;
	}

	private void computePriceForPolicy(InventoryItem forItem,
			RegisteredPolicy policy) {
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
		DealerCriterion dealerCriterion = new DealerCriterion();
		dealerCriterion.setDealer(new HibernateCast<ServiceProvider>()
				.cast(this.warranty.getForTransaction().getOwnerShip()));
		criteria.setDealerCriterion(dealerCriterion);
		criteria.setProductType(forItem.getOfType().getProduct());
		criteria.setWarrantyRegistrationType(WarrantyRegistrationType.TRANSFER);
		criteria.setCustomerState(this.warranty.getAddressForTransfer()
				.getState());
		Money price = this.policyService.getTransferFeeForPolicyDefinition(
				policy.getPolicyDefinition(), criteria, forItem
						.getDeliveryDate());
		policy.setPrice(price);
	}

	@Override
	public void validate() {
		if (!(orgService.isDealer(getSecurityHelper().getLoggedInUser()))) {
			if (!StringUtils.hasText(warranty.getModifyDeleteComments())) {
				addActionError(getText("error.modifyDRETR.reasonMandatory"));
			}
			if (isAdditionalInformationDetailsApplicable() && validateMarketingInfo && !deleteWarranty) {
				 validateMarketingInfo();
			}
		}
	}
	
	private void validateContractCode(){
        if (getMarketingInformation().getContractCode() == null) {
			addActionError("error.marketInfo.contractCode");
		}
        else if(getMarketingInformation().getContractCode() != null && 
         !listOfContractCodes.contains(getMarketingInformation().getContractCode())){
			addActionError("error.marketInfo.contractCode");
		}
    }
	
	private void validateIndustryCode(){
        if (getMarketingInformation().getIndustryCode() == null) {
			addActionError("error.marketInfo.industryCode");
		}
        else if(getMarketingInformation().getIndustryCode()!=null &&
        		!listOfIndustryCodes.contains(getMarketingInformation().getIndustryCode())){
			addActionError("error.marketInfo.industryCode");
		}
    }
	
	private void validateMaintenanceContract(){
        if (getMarketingInformation().getMaintenanceContract() == null) {
			addActionError("error.marketInfo.maintenanceContract");
		}
        else if(getMarketingInformation().getMaintenanceContract()!=null && 
        		!listofMaintenanceContracts.contains(getMarketingInformation().getMaintenanceContract())){
        	addActionError("error.marketInfo.maintenanceContract");
		}
    }
	
	private void validateMarketingInfo() {
		if (this.marketingInformation != null){
            validateSalesPerson();
            validateCustomerRepresentative();
        }else{
			addActionError("error.warranty.marketInfo");
		}
		if (!getLoggedInUser().isInternalUser() || this.marketingInformation.getInternalInstallType() == null){
			validateContractCode();
		}
        validateIndustryCode();
        validateMaintenanceContract();
	}
	
	private void validateSalesPerson(){
        if (!StringUtils.hasText(getMarketingInformation().getDealerRepresentative())) {
			addActionError("error.marketInfo.salesMan");
		}
        
    }
	private void validateCustomerRepresentative(){
        if (!StringUtils.hasText(getMarketingInformation().getCustomerRepresentative())) {
			addActionError("error.marketInfo.customerRepresentative");
		}
        
    }
	

    private void validateTransactionTypeDetails(){
            if (getMarketingInformation().getTransactionType() == null) {
				addActionError("error.marketInfo.transactionType");
			}
    }

    
    private void validateMarketDetails(){
        if(getMarketingInformation().getMarket()==null){
                addActionError("error.marketInfo.marketType");
        }
    if(getMarketingInformation().getApplication()==null)
    addActionError("error.marketInfo.application");
}

    private void validateCustomerFirtTimeOwner(){
			if (!StringUtils.hasText(getMarketingInformation().getIfPreviousOwner())) {
				addActionError("error.marketInfo.previousOwner");
			}
			if (getMarketingInformation().getCompetitionType()==null) {
				addActionError("error.marketInfo.competitionType");
			}
			if ( getMarketingInformation().getCompetitorMake() == null) {
				addActionError("error.marketInfo.competitiorMake");
			}
			if (getMarketingInformation().getCompetitorModel() == null) {
				addActionError("error.marketInfo.competitiorModel");
			}
    }

	public String getJSONifiedAttachmentsList() {
		try {
			List<Document> attachments = this.warranty.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}

	public String displayWarrantyDetails() {
		if (this.warrantyTransactionId != null) {
			warranty = this.warrantyService
					.findByTransactionId(this.warrantyTransactionId);			
			return SUCCESS;
		}
		return ERROR;
	}

	public boolean isContinuingWithClubCar() {
		if ("Continuing with Club Car".equals(this.marketingInformation
				.getIfPreviousOwner()))
			return true;
		return false;
	}

	public boolean isSwitchingToClubCar() {
		if ("Switching to Club Car".equals(this.marketingInformation
				.getIfPreviousOwner()))
			return true;
		return false;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		ModifyTransferReport.logger = logger;
	}

	public Long getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public WarrantyService getWarrantyService() {
		return this.warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public Warranty getWarranty() {
		return this.warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	/*
	 * protected TransactionType getPersistentTransactionType(String type) {
	 * return this.typeToTransactionType.get(type); }
	 * 
	 * protected CompetitionType getPersistentCompetitionType(String type) {
	 * return this.typeToCompetitionType.get(type); }
	 * 
	 * protected MarketType getPersistentMarketType(String type) { return
	 * this.typeToMarketType.get(type); }
	 * 
	 * protected CompetitorMake getPersistentCompetitorMake(String make) {
	 * return this.makeToCompetitorMake.get(make); }
	 * 
	 * protected CompetitorModel getPersistentCompetitorModel(String model) {
	 * return this.modelToCompetitorModel.get(model); }
	 * 
	 * public Set<String> getAllCompetitionTypes() { return
	 * this.typeToCompetitionType.keySet(); }
	 * 
	 * public Set<String> getAllMarketTypes() { return
	 * this.typeToMarketType.keySet(); }
	 * 
	 * public Set<String> getAllTransactionTypes() { return
	 * this.typeToTransactionType.keySet(); }
	 * 
	 * public Set<String> getAllCompetitorMakes(){ return
	 * this.makeToCompetitorMake.keySet(); }
	 * 
	 * public Set<String> getAllCompetitorModels(){ return
	 * this.modelToCompetitorModel.keySet(); }
	 */

	public Party getCustomer() {
		return this.customer;
	}

	public void setCustomer(Party customer) {
		this.customer = customer;
	}

	public AddressForTransfer getAddressForTransfer() {
		return addressForTransfer;
	}

	public void setAddressForTransfer(AddressForTransfer addressForTransfer) {
		this.addressForTransfer = addressForTransfer;
	}

	public MarketingInformation getMarketingInformation() {
		return this.marketingInformation;
	}

	public void setMarketingInformation(
			MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public InventoryService getInventoryService() {
		return this.inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	/*
	 * public Map<String, TransactionType> getTypeToTransactionType() { return
	 * this.typeToTransactionType; }
	 * 
	 * public Map<String, CompetitionType> getTypeToCompetitionType() { return
	 * this.typeToCompetitionType; }
	 * 
	 * public Map<String, MarketType> getTypeToMarketType() { return
	 * this.typeToMarketType; }
	 */
	public String getModifyDeleteReason() {
		return this.modifyDeleteReason;
	}

	public void setModifyDeleteReason(String modifyDeleteReason) {
		this.modifyDeleteReason = modifyDeleteReason;
	}

	public boolean isDeleteWarranty() {
		return this.deleteWarranty;
	}

	public void setDeleteWarranty(boolean deleteWarranty) {
		this.deleteWarranty = deleteWarranty;
	}

	public InvTransactionTypeConverter getInvTransactionTypeConverter() {
		return this.invTransactionTypeConverter;
	}

	public void setInvTransactionTypeConverter(
			InvTransactionTypeConverter invTransactionTypeConverter) {
		this.invTransactionTypeConverter = invTransactionTypeConverter;
	}

	public boolean isDeliveryReport() {
		return this.deliveryReport;
	}

	public void setDeliveryReport(boolean deliveryReport) {
		this.deliveryReport = deliveryReport;
	}

	public List<RegisteredPolicy> getSelectedPolicies() {
		return this.selectedPolicies;
	}

	public void setSelectedPolicies(List<RegisteredPolicy> selectedPolicies) {
		this.selectedPolicies = selectedPolicies;
	}

	public PolicyService getPolicyService() {
		return this.policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public InventoryItemAttributeValueService getInventoryItemAttributeValueService() {
		return inventoryItemAttributeValueService;
	}

	public void setInventoryItemAttributeValueService(
			InventoryItemAttributeValueService inventoryItemAttributeValueService) {
		this.inventoryItemAttributeValueService = inventoryItemAttributeValueService;
	}

	public AttributeService getAttributeService() {
		return attributeService;
	}

	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	public void setServletRequest(HttpServletRequest req) {
		this.request = req;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isInternalUserModifying() {
		return this.orgService.isInternalUser(getLoggedInUser());
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public Long getWarrantyTransactionId() {
		return warrantyTransactionId;
	}

	public void setWarrantyTransactionId(Long warrantyTransactionId) {
		this.warrantyTransactionId = warrantyTransactionId;
	}

	public void populateCustomerTypes() {
		

		if (this.configParamService == null) {
			initDomainRepository();
		}
		Map<Object, Object> keyValueOfCustomerTypes= new HashMap<Object, Object>();
		// need to put BU filter
		if (!this.deliveryReport) {
			keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_ETR
						.getName());
		}
		else
		{
			keyValueOfCustomerTypes = this.configParamService
					.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_DR
							.getName());
		}
		if (keyValueOfCustomerTypes != null
				&& !keyValueOfCustomerTypes.isEmpty()) {
			customerTypes.putAll(keyValueOfCustomerTypes);
		}
	}

	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.configParamService = (ConfigParamService) beanLocator
				.lookupBean("configParamService");
	}

	public Map<Object, Object> getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(Map<Object, Object> customerTypes) {
		this.customerTypes = customerTypes;
	}

	public String getAddressBookType() {
		return addressBookType;
	}

	public void setAddressBookType(String addressBookType) {
		this.addressBookType = addressBookType;
	}

	public AddressBookService getAddressBookService() {
		return addressBookService;
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public OrganizationRepository getOrganizationRepository() {
		return organizationRepository;
	}

	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public boolean isModifyDRorETR() {
		return isModifyDRorETR;
	}

	public void setModifyDRorETR(boolean isModifyDRorETR) {
		this.isModifyDRorETR = isModifyDRorETR;
	}

	

	public boolean isAdditionalInformationDetailsApplicable() {
		return this.configParamService
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}

	private boolean isApplicableCustomerType(PolicyDefinition policyDefinition) {
		boolean isApplicableCustomerType = false;
		for (ApplicableCustomerTypes customerType : policyDefinition
				.getCustomertypes()) {
			if (addressBookType.equalsIgnoreCase(customerType.getType())) {
				isApplicableCustomerType = true;
				break;
			}
		}

		return isApplicableCustomerType;
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

    public boolean isMarketingInfoEntered(){
        return marketingInformation!=null;
    }

    private MarketService marketService;

    @Required
    public void setMarketService(MarketService marketService) {
        this.marketService = marketService;
    }

    /* Marketing information related variables and logic */
    private List<TransactionType> listOfTransactionTypes = new ArrayList<TransactionType>();
	private List<CompetitorMake> listOfCompetitorMakes = new ArrayList<CompetitorMake>();
	private List<CompetitorModel> listOfCompetitorModels = new ArrayList<CompetitorModel>();
    private List<Market> listOfMarkets = new ArrayList<Market>();
    private List<Market> listOfMarketTypes = new ArrayList<Market>();
    public List<Market> getListOfMarketTypes() {
		return listOfMarketTypes;
	}

	public void setListOfMarketTypes(List<Market> listOfMarketTypes) {
		this.listOfMarketTypes = listOfMarketTypes;
	}

	private Map<String,String> listIfPreviousOwner = new HashMap<String,String>();
    private List<String> listOfAdditionalInfo = new ArrayList<String>();
    private List<CompetitionType> listOfCompetitionTypes = new ArrayList<CompetitionType>();
    private final Map<String, User> nameToSalesMan = new LinkedHashMap<String, User>();
    private List<Market> listOfMarketApplications = new ArrayList<Market>();
    private List<User> listOfSalesPersons = new ArrayList<User>();
    private List<ContractCode> listOfContractCodes =new ArrayList<ContractCode>();
    private List<InternalInstallType> listOfInternalInstallTypes = new ArrayList<InternalInstallType>();
   
	private List<MaintenanceContract> listofMaintenanceContracts=new ArrayList<MaintenanceContract>();
	private List<IndustryCode> listOfIndustryCodes=new ArrayList<IndustryCode>();
    private WarrantyUtil warrantyUtil;
    private ItemGroupService itemGroupService;

    public List<TransactionType> getListOfTransactionTypes() {
        return listOfTransactionTypes;
    }

    public void setListOfTransactionTypes(List<TransactionType> listOfTransactionTypes) {
        this.listOfTransactionTypes = listOfTransactionTypes;
    }

    public List<CompetitorMake> getListOfCompetitorMakes() {
        return listOfCompetitorMakes;
    }

    public void setListOfCompetitorMakes(List<CompetitorMake> listOfCompetitorMakes) {
        this.listOfCompetitorMakes = listOfCompetitorMakes;
    }

    public List<CompetitorModel> getListOfCompetitorModels() {
        return listOfCompetitorModels;
    }

    public void setListOfCompetitorModels(List<CompetitorModel> listOfCompetitorModels) {
        this.listOfCompetitorModels = listOfCompetitorModels;
    }

    public List<Market> getListOfMarkets() {
        return listOfMarkets;
    }

    public void setListOfMarkets(List<Market> listOfMarkets) {
        this.listOfMarkets = listOfMarkets;
    }

    public Map<String, String> getListIfPreviousOwner() {
        return listIfPreviousOwner;
    }

    public void setListIfPreviousOwner(Map<String, String> listIfPreviousOwner) {
        this.listIfPreviousOwner = listIfPreviousOwner;
    }

    public List<String> getListOfAdditionalInfo() {
        return listOfAdditionalInfo;
    }

    public void setListOfAdditionalInfo(List<String> listOfAdditionalInfo) {
        this.listOfAdditionalInfo = listOfAdditionalInfo;
    }

    public List<CompetitionType> getListOfCompetitionTypes() {
        return listOfCompetitionTypes;
    }

    public void setListOfCompetitionTypes(List<CompetitionType> listOfCompetitionTypes) {
        this.listOfCompetitionTypes = listOfCompetitionTypes;
    }

    @Required
    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public CampaignRepository getCampaignRepository() {
		return campaignRepository;
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public boolean isValidateMarketingInfo() {
		return validateMarketingInfo;
	}

	public void setValidateMarketingInfo(boolean validateMarketingInfo) {
		this.validateMarketingInfo = validateMarketingInfo;
	}

	public Market getMarketType() {
		return marketType;
	}

	public void setMarketType(Market marketType) {
		this.marketType = marketType;
	}
	
	public String getMarketApplicationForMarketType() throws JSONException {
		listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketType().getId());
		return SUCCESS;
	}

	public List<Market> getListOfMarketApplications() {
		return listOfMarketApplications;
	}

	public void setListOfMarketApplications(List<Market> listOfMarketApplications) {
		this.listOfMarketApplications = listOfMarketApplications;
	}

	public List<User> getListOfSalesPersons() {
		return listOfSalesPersons;
	}

	public void setListOfSalesPersons(List<User> listOfSalesPersons) {
		this.listOfSalesPersons = listOfSalesPersons;
	}

	public WarrantyUtil getWarrantyUtil() {
		return warrantyUtil;
	}

	public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
		this.warrantyUtil = warrantyUtil;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	 public List<ContractCode> getListOfContractCodes() {
			return listOfContractCodes;
		}

	public void setListOfContractCodes(List<ContractCode> listOfContractCodes) {
		this.listOfContractCodes = listOfContractCodes;
	}

	public List<MaintenanceContract> getListofMaintenanceContracts() {
		return listofMaintenanceContracts;
	}

	public void setListofMaintenanceContracts(
			List<MaintenanceContract> listofMaintenanceContracts) {
		this.listofMaintenanceContracts = listofMaintenanceContracts;
	}

	public List<IndustryCode> getListOfIndustryCodes() {
		return listOfIndustryCodes;
	}

	public void setListOfIndustryCodes(List<IndustryCode> listOfIndustryCodes) {
		this.listOfIndustryCodes = listOfIndustryCodes;
	}

	public Customer getOperator() {
		return operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

	public CalendarDate getWarrantyStartDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if (deliveryDate.isAfter(shipmentDate.plusMonths(6))) {
				return shipmentDate;
			} else {
				return deliveryDate;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate, policyDefinition);
			if(deliveryDate.isBefore(cutOffDate)) {
				return deliveryDate;
			} else {
				return cutOffDate;
			}
		}
	}
	
	public CalendarDate getWarrantyEndDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
		CalendarDate warrantyEndDate;
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				warrantyEndDate = shipmentDate.plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment());
			}else{
				warrantyEndDate = deliveryDate.plusMonths(policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery());
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate, policyDefinition);
			Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
			if(deliveryDate.isBefore(cutOffDate)) {
				warrantyEndDate = deliveryDate.plusMonths(monthsCoveredFromDelivery);
			} else {
				warrantyEndDate = cutOffDate.plusMonths(monthsCoveredFromDelivery);
			}
		}
		return warrantyEndDate;
	}
	
	public String getMonthsCovered(InventoryItem inventoryItem, PolicyDefinition policyDefinition){
		StringBuffer monthsCovered = new StringBuffer();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		String monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery().toString();
		String monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment().toString();
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(inventoryItem.getShipmentDate().plusMonths(6))){
				monthsCovered.append(monthsCoveredFromShipment);
				monthsCovered.append(" Months from Shipment");
			}else{
				monthsCovered.append(monthsCoveredFromDelivery);
				monthsCovered.append(" Months from Delivery");
			}
		} else {
			monthsCovered.append(monthsCoveredFromDelivery);
			monthsCovered.append(" Months from Warranty Start Date");
		}
		return monthsCovered.toString();
	}

	private void setErrorCodesMapToActionErrors(Map<String, String[]> errorCodeMap) {
		 if(!errorCodeMap.isEmpty()) {	
		        Iterator<String> iterator = errorCodeMap.keySet().iterator();
		        while (iterator.hasNext()) {
		            String errorKey = iterator.next();
		            String[] errorValue = errorCodeMap.get(errorKey);
		            if(errorValue == null) {
		            	addActionError(errorKey);
		            } else {
		            	addActionError(errorKey, errorValue);
		            }
		        }
	     }		
	}
	private boolean validateInventoryItemMapping(
			MultipleInventoryAttributesMapper mapper) {
		InventoryItem inventoryItem = mapper.getInventoryItem();
		List<RegisteredPolicy> selectedPolicies = mapper.getSelectedPolicies();
		// Note: the single "&" is not a typo! We cant use && since we want
		// *both* validations to happen,
		// irrespective of each other.
		return validateDeliveryAndInstallationDate(mapper) & validateInventoryItem(inventoryItem);
				//& validateMandatoryAttachments(inventoryItem,mapper.getAttachments());
	}
	

	private boolean validateMandatoryAttachments(InventoryItem inventoryItem,
			List<Document> attachments) {
		boolean valid = false;
		if(attachments != null) {
			attachments.removeAll(Collections.singleton(null));
			valid = !attachments.isEmpty();
		}
		if(!valid) {
			addActionError("error.deliveryReport.attachments.mandatory",inventoryItem.getSerialNumber());
		}
		return valid;
	}
	
	
	private boolean validateInventoryItem(InventoryItem inventoryItem) {
		boolean isValid = validateCustomerType(this.addressBookType);
		if(this.isInstallingDealerEnabled())
	    isValid = validateInstallingDealer(this.installingDealer);
		if (inventoryItem.getHoursOnMachine() == null
				|| inventoryItem.getHoursOnMachine() < 0) {
			// User needs to input hoursOnMachine
			addActionError("error.hoursOnMachineShouldBePositive",new String[] { inventoryItem.getSerialNumber() });
			isValid = false;
		}
		
		/*if(AdminConstants.CUSTOMER_TYPE_DEMO.equals(this.addressBookType)){
			 if(!CollectionUtils.isEmpty(inventoryItem.getTransactionHistory())){
				 for(InventoryTransaction inventoryTransaction : inventoryItem.getTransactionHistory()){
					 if(InvTransationType.DEMO.getTransactionType().equals(inventoryTransaction.getInvTransactionType().getTrnxTypeValue())){
						 addActionError("error.reWarrantyOnDemoMachine", new String[] { inventoryItem.getSerialNumber() });
						 break;
					 }
				 }
			 }
		}*/

		return isValid;
	}

	public boolean isInstallingDealerEnabled() {
			return this.configParamService
			.getBooleanValue(ConfigName.ENABLE_DEALER_AND_INSTALLATION_DATE
					.getName());
		}
	     

	private boolean validateCustomerType(String addressBookType) {
		boolean isValid = true;
		if (addressBookType == null || SELECT.equalsIgnoreCase(addressBookType)) {
			addActionError("error.customerTypeNotSelected",new String[] { inventoryItem.getSerialNumber() });
			return isValid = false;
		}

		return isValid;
	}
	
	private boolean validateInstallingDealer(Party installingDealer)
	{
	boolean isValid = true;
	if(installingDealer == null){	
		addActionError("error.common.installingDealer");
		return isValid = false;		
	}
	return isValid;
	}

	private boolean validateCustomer() {
        if ((isCustomerDetailsNeededForDR_Rental()?false:!(getAddressBookType().toUpperCase().equals(AdminConstants.DEALER_RENTAL)) 
        		||getAddressBookType().toUpperCase().equals("NATIONALACCOUNT") 
        		|| getAddressBookType().equals(AdminConstants.CUSTOMER_TYPE_DEMO))
        		&& getCustomer() == null)
        {
            addActionError("error.addCustomerInfo");
            return false;
        }
		return true;
	}
	
	private void validateWaiverInformation(DieselTierWaiver dieselTierWaiver){
		if(dieselTierWaiver==null || dieselTierWaiver.getApprovedByAgentName().isEmpty()){
			addActionError("error.waiver.AgentName");
		}
		/*if(dieselTierWaiver.getAgentTitle().isEmpty()){
			addActionError("error.waiver.AgentTitle");
		}
		if(dieselTierWaiver.getAgentTelephone().isEmpty()){
			addActionError("error.waiver.AgentTelephone");
		}
		if(dieselTierWaiver.getAgentEmailAddress().isEmpty()){
			addActionError("error.waiver.AgentEmail");
		}*/
	}
	
	public boolean isCustomerDetailsNeededForDR_Rental(){
		return this.configParamService
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
	}
	
	public ServiceProvider getInstallingDealer() {
		return installingDealer;
	}

	public void setInstallingDealer(ServiceProvider installingDealer) {
		this.installingDealer = installingDealer;
	}
	
	public boolean isMarketInfoApplicable() {
		return configParamService.getBooleanValue(
				ConfigName.IS_MARKETING_INFO_APPLICABLE.getName())
				.booleanValue();
	}

	private boolean validateMarketInfo(List<SelectedAdditionalMarketingInfo> selectedMarketingInfo) {

		for (SelectedAdditionalMarketingInfo selectedAdditionalMarketingInfo : selectedMarketingInfo) {
			if (!selectedAdditionalMarketingInfo.getValue().isEmpty() && selectedAdditionalMarketingInfo.getAddtlMarketingInfo().getInfoType().equals(
					AdditionalMarketingInfoType.Number)) {
				try {
					Double.parseDouble(selectedAdditionalMarketingInfo.getValue());
				} catch (NumberFormatException e) {
					addActionError("error.marketInfo",new String[] {selectedAdditionalMarketingInfo.getAddtlMarketingInfo().getFieldName() });
					return false;
				}
			}
		}
		return true;
	}

	public Boolean isDisclaimerAvailable(MultipleInventoryAttributesMapper mapper){
		InventoryItem curItem = this.inventoryItem;
		this.inventoryItem = mapper.getInventoryItem();
		boolean isAvailable = engineTierCountryMapping(mapper).equals(SUCCESS);
		this.inventoryItem = curItem;
		return isAvailable;
	
}

	private Object engineTierCountryMapping(
			MultipleInventoryAttributesMapper mapper) {

		if (!addressBookType.equalsIgnoreCase("EndCustomer"))
			return INPUT;
		Party customer = this.customer;
		if (inventoryItem.getPreOrderBooking()
				&& inventoryItem.getIsDisclaimer() && isCustomerExists()) {
			if (inventoryItem.getLatestBuyer().equals(customer)) {
				mapper.setWaiverInformationEditable(false);
				mapper.setDieselTierWaiver(inventoryItem.getWaiverDuringDr());
				mapper.getDieselTierWaiver().setDisclaimer(inventoryItem.getWaiverDuringDr().getI18NDisclaimer());
				return SUCCESS;
			}
		}
		if (customer != null && customer.getAddress() != null
				&& inventoryItem.getDieselTier() != null ) {
			Country customerCountry = engineTierCtryMappingService
					.findCountryByName(customer.getAddress().getCountry());
			EngineTierCtryMapping engineTierCountryMapping = engineTierCtryMappingService
					.findDieselTierByCountry(customerCountry);
			TierTierMapping tierTierMapping = null;
			if (engineTierCountryMapping != null) {
				tierTierMapping = engineTierCtryMappingService
						.findTierTierMappingByInventoryTierAndCustomerTier(
								inventoryItem.getDieselTier(),
								engineTierCountryMapping);
			}
			if (tierTierMapping != null) {
				mapper.setWaiverInformationEditable(true);
				if(mapper.getDieselTierWaiver() == null)
					mapper.setDieselTierWaiver(new DieselTierWaiver());
				mapper.getDieselTierWaiver().setDisclaimer(tierTierMapping.getI18NWaiverText());
				return SUCCESS;
			}
		}
		return INPUT;
	
	}
	
	public EngineTierCtryMappingService getEngineTierCtryMappingService() {
		return engineTierCtryMappingService;
	}

	public void setEngineTierCtryMappingService(
			EngineTierCtryMappingService engineTierCtryMappingService) {
		this.engineTierCtryMappingService = engineTierCtryMappingService;
	}
	
	public DieselTierWaiverService getDieselTierWaiverService() {
		return dieselTierWaiverService;
	}

	public void setDieselTierWaiverService(
			DieselTierWaiverService dieselTierWaiverService) {
		this.dieselTierWaiverService = dieselTierWaiverService;
	}

	public List<InternalInstallType> getListOfInternalInstallTypes() {
		return listOfInternalInstallTypes;
	}

	public void setListOfInternalInstallTypes(
			List<InternalInstallType> listOfInternalInstallTypes) {
		this.listOfInternalInstallTypes = listOfInternalInstallTypes;
	}
	
	public boolean displayInternalInstallType() {
		return configParamService
				.getBooleanValue(ConfigName.DISPLAY_INTERNAL_INSTALL_TYPE
						.getName());
	}
	
	private boolean isPolicyMandatory(){
		return configParamService.getBooleanValue(ConfigName.POLICY_MANDATORY.getName());
	}
	
	public boolean noPolicyForDemoTruckWithMoreThan80Hours() {
		return isBuConfigAMER();
	}
	
	private CalendarDate getCutOffDate(CalendarDate shipmentDate, PolicyDefinition policyDefinition) {
		Integer monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment();
		Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
	}
}

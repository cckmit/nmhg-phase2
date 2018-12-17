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
package tavant.twms.web.warranty;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getUnitDocumentListJSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstance;
import tavant.twms.domain.catalog.ItemGroupService;
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
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TierTierMapping;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ServiceProviderCertificationStatus;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.policy.AdditionalMarketingInfo;
import tavant.twms.domain.policy.AdditionalMarketingInfoOptions;
import tavant.twms.domain.policy.AdditionalMarketingInfoService;
import tavant.twms.domain.policy.AdditionalMarketingInfoType;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.ApplicableCustomerTypes;
import tavant.twms.domain.policy.CompetitionType;
import tavant.twms.domain.policy.CompetitorMake;
import tavant.twms.domain.policy.CompetitorModel;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.Market;
import tavant.twms.domain.policy.MarketService;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyFees;
import tavant.twms.domain.policy.PolicyRatesCriteria;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.SelectedAdditionalMarketingInfo;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyRegistrationType;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.actions.SortedHashMap;
import tavant.twms.web.multipleinventorypicker.MultipleInventoryPickerAction;
import tavant.twms.web.util.DocumentTransportUtils;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class WarrantyTransfer extends MultipleInventoryPickerAction implements
		Preparable, ServletRequestAware {

	private static Logger logger = LogManager.getLogger(WarrantyTransfer.class);

	private InventoryItem inventoryItem;

	private String customerType = COMPANY;

	//private Customer customer;
	private Party customer;
	
	private Customer operator;
	
	private AddressForTransfer operatorAddressForTransfer;	

	private AddressForTransfer addressForTransfer;

	private int inventoryItemIndex = -1;
	private MarketingInformation marketingInformation;

	private WarrantyService warrantyService;

	private InventoryTransaction invTransaction;
	
	private final String transactionType = InventoryTransaction.ETR;
	private List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>(
			5);
	private Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> marketingInfo= new HashMap<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>>();
	private List<RegisteredPolicy> availablePolicies;
	private List<RegisteredPolicy> selectedPolicies;
	private PolicyService policyService;
	private boolean confirmTransfer;
	private boolean saveAsDraft;
	private String registrationComments;
	private boolean allowInventorySelection;
	private static final String INDIVIDUAL = "Individual";
	private boolean forETR;
	private Address customerAddress;
	private Address operatorAddress;	
	private boolean checkboxInstallDate;
	private Organization dealerOrganization;
	private AddressBookService addressBookService;
	private static final String COMPANY = "Company";
	private UserRepository userRepository;
	private ServiceProvider forDealer;
	private Market marketType;
	private DealershipRepository dealershipRepository;
	private boolean dealerNameSelected = true;
	private boolean forDealerSelected = false;
	private InventoryTransactionService invTransactionService;
	private int inventoryItemMappingIndex = 0;
	private Warranty warranty = null;
	private CalendarDate copyInstallDate;
	private HttpServletRequest request;
	private Map<Object, Object> customerTypes = new SortedHashMap<Object, Object>();
	private String addressBookType;
	private String addressBookTypeForOperator;
	private ConfigParamService configParamService;
	private boolean isModifyDRorETR = false;
	private boolean firstTimeETR;
	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
	private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;
	private boolean duplicateSerialNumber = false;
    private WarrantyTaskInstance warrantyTaskInstance;
    private WarrantyCoverageRequestService warrantyCoverageRequestService;
    private List<Document> commonAttachments = new ArrayList<Document>();
    private ServiceProvider installingDealer;
    private LovRepository lovRepository;
    private String jsonString;
    private static JSONArray EMPTY_INVENTORY_DETAIL;
    private AdditionalMarketingInfoService additionalMarketingInfoService;
    private EngineTierCtryMappingService engineTierCtryMappingService;
	private DieselTierWaiverService dieselTierWaiverService;
	private String behalfDealer;
	private ServiceProvider forBehalfDealer;
	
	private BuSettingsService buSettingsService;
	private CustomerService customerService;

    public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public BuSettingsService getBuSettingsService() {
        return buSettingsService;
    }

    public void setBuSettingsService(BuSettingsService buSettingsService) {
        this.buSettingsService = buSettingsService;
    }

    public boolean isPrintPdf(){
    	return this.buSettingsService.getBooleanSetting(BUSetting.PRINT_PDI,getCurrentBusinessUnit().getName());
    }
	
	private List<ListOfValues> additionalComponentTypes;
	private List<ListOfValues> additionalComponentSubTypes;
	 private boolean forPrintPDI;
		private boolean pdiGeneration;
		
		public boolean isForPrintPDI() {
			return forPrintPDI;
		}
	public String defaultCustomerType(){
		if(isBuConfigAMER()){
			return AdminConstants.END_CUSTOMER;
		}
        return null;
    }
	
	public String defaultMaintenanceContract(){
		if(isBuConfigAMER()){
			return AdminConstants.FULL_SERVICE_CONTRACT;
		}
		return null;
	}

		public void setForPrintPDI(boolean forPrintPDI) {
			this.forPrintPDI = forPrintPDI;
		}

		public boolean isPdiGeneration() {
			return pdiGeneration;
		}

		public void setPdiGeneration(boolean pdiGeneration) {
			this.pdiGeneration = pdiGeneration;
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
	
	public String getBehalfDealer() {
		return behalfDealer;
	}

	public void setBehalfDealer(String behalfDealer) {
		this.behalfDealer = behalfDealer;
		forBehalfDealer= orgService.findDealerById(Long.parseLong(behalfDealer));

	}
    
	 static {
		 	EMPTY_INVENTORY_DETAIL = new JSONArray();
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("");
			
		}
	 
    @Override
	public String handleInventorySelection() throws IOException {
		super.handleInventorySelection();

		for (InventoryItem inventoryItem : getInventoryItems()) {
			prepareInventoryItem(inventoryItem);
		}

		return SUCCESS;
	}

    @Override
	public String searchInventories() throws IOException {
		super.searchInventories();
		return SUCCESS;
	}

    public boolean isMarketInfoApplicable() {
		return this.configParamService.getBooleanValue(
				ConfigName.IS_MARKETING_INFO_APPLICABLE.getName())
				.booleanValue();
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
    
    private void prepareInventoryItem(InventoryItem inventoryItem) {
    	if( !duplicateSerialNumber ) {
			MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();		
			mapper.setInventoryItem(inventoryItem);
			if(isMarketInfoApplicable())
			{				
			List<AdditionalMarketingInfo> additionalMarketingInfo = this.getAdditionalMarketingInfoService()
					.getAdditionalMarketingInfoByAppProduct(inventoryItem.getOfType().getProduct());
			for(AdditionalMarketingInfo marketInfo : additionalMarketingInfo)
			{
				Map<String,List<AdditionalMarketingInfoOptions>> fieldDetailsForMarketingInfo = new HashMap<String,List<AdditionalMarketingInfoOptions>>();
				fieldDetailsForMarketingInfo.put(marketInfo.getInfoType().toString(), marketInfo.getOptions());
				marketingInfo.put(marketInfo, fieldDetailsForMarketingInfo);
				mapper.setMarketingInfo(marketingInfo);
			}
			}
            mapper.setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
            mapper.setEquipmentVIN(inventoryItem.getVinNumber());
            mapper.setFleetNumber(inventoryItem.getFleetNumber());
            mapper.setInstallationDate(inventoryItem.getInstallationDate());
            mapper.setOem(inventoryItem.getOem());           
            Warranty warranty = inventoryItem.getDraftWarranty();
            prepareApplicablePolicies(mapper);
			if (warranty != null && !WarrantyStatus.DELETED.equals(warranty.getStatus())) {
				mapper.setSelectedMarketingInfo(warranty.getSelectedAddtlMktInfo());
				for (RegisteredPolicy registeredPolicy : warranty.getPolicies()) {
				if(mapper.getAvailablePolicies().contains(registeredPolicy) && !mapper.getSelectedPolicies().contains(registeredPolicy))
					mapper.getSelectedPolicies().add(registeredPolicy);
				}
                mapper.setWarrantyDeliveryDate(warranty.getDeliveryDate());
                            mapper.setAttachments(warranty.getAttachments());
            }
			preselectFreePolicies(mapper);
			this.setSelectedPolicies(mapper.getSelectedPolicies());	
			this.inventoryItemMappings.add(mapper);
    	}
	}

	private void prepareApplicablePolicies(
			MultipleInventoryAttributesMapper mapper) {
		InventoryItem inventoryItem = mapper.getInventoryItem();
		Long hoursOnMachine = inventoryItem.getHoursOnMachine();
		if (inventoryItem.getDeliveryDate() != null && hoursOnMachine != null
				&& hoursOnMachine >= 0 && !(addressBookType == null
					|| SELECT.equalsIgnoreCase(addressBookType)) && !(getAddressBookType().equalsIgnoreCase(AdminConstants.DEMO))) {
			mapper.setAvailablePolicies(fetchExistingPoliciesForUsedItem(inventoryItem));
		}

	}

	private void preselectFreePolicies(MultipleInventoryAttributesMapper mapper) {
		for (RegisteredPolicy registeredPolicy : mapper.getAvailablePolicies()) {
			PolicyDefinition policyDefinition = registeredPolicy
					.getPolicyDefinition();
			if (policyDefinition.getTransferDetails().isTransferable()
					&& fetchPriceForPolicyForCurrency(policyDefinition,mapper.getInventoryItem()) && !mapper.getSelectedPolicies().contains(registeredPolicy)) {
				mapper.getSelectedPolicies().add(registeredPolicy);
			}
		}
	}

	@Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		getInventorySearchCriteria().setWarrantyCheck(true);
		getInventorySearchCriteria().setInventoryType(InventoryType.RETAIL);
		getInventorySearchCriteria().setConditionTypeNot(
				InventoryItemCondition.SCRAP);isLoggedInUserADealer();
		//getInventorySearchCriteria().setDealerId(null);
	}

	public void prepare() throws Exception {
		/*if (isLoggedInUserADealer()) {
			forDealer = getLoggedInUsersDealership();
		}*/
		if (!isLoggedInUserAnInternalUser()) {
            forDealer = getLoggedInUsersDealership();
            if(forDealer!=null){
            	forDealerSelected = true;
            	forDealer.setAddress(warrantyUtil.getDealersAddress(forDealer.getId()));
            }
      }
		else if(isLoggedInUserAnInternalUser())
		{
			if(inventoryItem!=null && inventoryItem.getOwner()!=null){
				forDealer = inventoryItem.getOwner();
				if(forDealer!=null){
					forDealerSelected = true;
					forDealer.setAddress(warrantyUtil.getDealersAddress(forDealer.getId()));
				}
			}
		}
        if(!StringUtils.hasText(SelectedBusinessUnitsHolder.getSelectedBusinessUnit())
                && inventoryItem!=null){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem.getBusinessUnitInfo().getName());
        }
        if ( listIfPreviousOwner.isEmpty()) {
			listIfPreviousOwner.put(SWITCHING,getText("label.wntyreg.prevowner.switch"));
			listIfPreviousOwner.put(CONTINUING,getText("label.wntyreg.prevowner.continue"));
			listIfPreviousOwner.put(UNKNOWN_NOT_PROVIDED,getText("label.wntyreg.prevowner.Unknown/NotProvided"));
		}
        listOfTransactionTypes = this.warrantyService.listTransactionTypes();
        listOfMarketTypes = this.marketService.listMarketTypes();
        listOfContractCodes =this.warrantyService.listContractCode();
        listOfInternalInstallTypes = this.warrantyService.listInternalInstallType();
        listofMaintenanceContracts=this.warrantyService.listMaintenanceContract();
        listOfIndustryCodes=this.warrantyService.listIndustryCode();
        if(this.getMarketingInformation()!=null && this.getMarketingInformation().getMarket()!=null)
        listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketingInformation().getMarket().getId());
        
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
        
        listOfCompetitorModels = this.warrantyService.listCompetitorModel();
		listOfCompetitorMakes = this.warrantyService.listCompetitorMake();
        listOfCompetitionTypes = this.warrantyService.listCompetitionTypes();
        additionalComponentTypes = this.lovRepository.findAllActive("AdditionalComponentType");
		additionalComponentSubTypes = this.lovRepository.findAllActive("AdditionalComponentSubType");
        if (this.inventoryItem != null) {
        	if(InstanceOfUtil.isInstanceOfClass(ServiceProvider.class,  inventoryItem.getInstallingDealer()))
    			setInstallingDealer(new HibernateCast<ServiceProvider>().cast(inventoryItem.getInstallingDealer()));	
			prepareInventoryItem(this.inventoryItem);
		}
		        if(defaultCustomerType() != null){
        	addressBookType=defaultCustomerType();
        }
		if (isAdditionalInformationDetailsApplicable() && defaultMaintenanceContract() != null) {
			this.marketingInformation = new MarketingInformation();
			this.marketingInformation.setMaintenanceContract(warrantyService
								.findMaintenanceContractByName(defaultMaintenanceContract()));
		}
        
//        listOfSalesPersons = this.warrantyUtil.getSalesPersonsForDealer(this.inventoryItem, this.forDealer);
		populateCustomerTypes();

	}

        protected void prepareCommonAttachments() {
            if(commonAttachments == null) {
                commonAttachments = new ArrayList<Document>();
            }
            
        List<List<Document>> listOfDocList = new ArrayList<List<Document>>();

        for (MultipleInventoryAttributesMapper invItemMapper :
            inventoryItemMappings) {
            final List<Document> attachments = invItemMapper.getAttachments();
            
            if(!attachments.isEmpty()) {
                listOfDocList.add(attachments);                
            }
            
            if(listOfDocList.size() > 1) {
                break;
            }
        }

        if (listOfDocList.size() > 1) {
            for (Document attach1 : listOfDocList.get(0)) {
                for (Document attach2 : listOfDocList.get(1)) {
                    if (attach2.getId().equals(attach1.getId())) {
                        commonAttachments.add(attach1);
                        break;
                    }
                }
            }
        }

        for (MultipleInventoryAttributesMapper invItemMapper :
            inventoryItemMappings) {
                invItemMapper.getAttachments().removeAll(commonAttachments);
        }
    }

	private List<RegisteredPolicy> fetchExistingPoliciesForUsedItem(
			InventoryItem inventoryItem) {
		List<RegisteredPolicy> existingPolices = new ArrayList<RegisteredPolicy>();
		Warranty earlierWarranty = inventoryItem.getWarranty();
		Long hoursOnMachine = inventoryItem.getHoursOnMachine();
		List<String> etrTransactionTypes = new ArrayList<String>();
		etrTransactionTypes.add(InvTransationType.ETR.getTransactionType());
		etrTransactionTypes.add(InvTransationType.ETR_MODIFY.getTransactionType());
		List<Warranty> etrWarranties = inventoryItem.getWarranties(etrTransactionTypes);
		List<String> drTransactionTypes = new ArrayList<String>();
		drTransactionTypes.add(InvTransationType.DR.getTransactionType());
		drTransactionTypes.add(InvTransationType.DR_MODIFY.getTransactionType());
		drTransactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		for (RegisteredPolicy registeredPolicy : earlierWarranty.getPolicies()) {
            if (registeredPolicy.getPolicyDefinition().getTransferDetails().isTransferable()&& 
                    isWithinMaxNoTransfer(etrWarranties, registeredPolicy,inventoryItem) && 
                    isApplicableCustomerType(registeredPolicy.getPolicyDefinition()) && 
                    isApplicableForInstallingDealer(registeredPolicy, installingDealer)) {
                if (RegisteredPolicyStatusType.ACTIVE.getStatus().equals(registeredPolicy.getLatestPolicyAudit().getStatus()) || 
                        RegisteredPolicyStatusType.SUSPENDED.getStatus().equals(registeredPolicy.getLatestPolicyAudit().getStatus())) {
                    existingPolices.add(registeredPolicy);
                }
            }
        }
		Collections.sort(existingPolices);
		if (existingPolices.isEmpty() || !isInstallingDealerEnabled()) {
			return existingPolices;
		} else {
			return policyService.filterPolicyByServiceProvider(existingPolices, installingDealer);
		}
	}
	
	private boolean isTransferrableBasedOnHours(RegisteredPolicy policy,
			Long hoursOnMachine){
		if(!WarrantyType.POLICY.getType().
				equals(policy.getPolicyDefinition().getWarrantyType().getType())){
		 return hoursOnMachine <= policy.getPolicyDefinition()
		 			.getCoverageTerms().getServiceHoursCovered();
		}else{
			if(policy.getLatestPolicyAudit().getServiceHoursCovered() != null){
				return hoursOnMachine <= policy.getLatestPolicyAudit().getServiceHoursCovered();				
			}else{
				return true;
			}			
		}		
	}
	
	private boolean isApplicableForInstallingDealer(RegisteredPolicy policy, Party installingDealer) {
		if (!isInstallingDealerEnabled()) {
			return true;
		}
		else {
			boolean isApplicableForInstallingDealer = false;
			ServiceProvider sp = null;
			if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, installingDealer)) {
				sp = new HibernateCast<ServiceProvider>().cast(installingDealer);
				if ((policy.getPolicyDefinition().getCertificationStatus().equals(
						ServiceProviderCertificationStatus.CERTIFIED) && sp.getCertified())
						|| (policy.getPolicyDefinition().getCertificationStatus().equals(
								ServiceProviderCertificationStatus.NOTCERTIFIED) && !sp.getCertified())
						|| policy.getPolicyDefinition().getCertificationStatus().equals(
								ServiceProviderCertificationStatus.ANY)) {
					isApplicableForInstallingDealer = true;
				}

			}
			
			return isApplicableForInstallingDealer;
		}
	}
	
	private List<RegisteredPolicy> createPolicies(
			List<PolicyDefinition> withDefinitions, InventoryItem forItem,
			boolean computePrice) throws PolicyException {
		List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
		List<String> drTransactionTypes = new ArrayList<String>();
		drTransactionTypes.add(InvTransationType.DR.getTransactionType());
		drTransactionTypes
				.add(InvTransationType.DR_MODIFY.getTransactionType());
		drTransactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		Warranty deliveryWarranty = forItem.getWarranties(drTransactionTypes)
				.get(0);		
		Warranty latestWarranty = forItem.getLatestWarranty();		
		for (PolicyDefinition definition : withDefinitions) {			
			String policyName = definition.getCode();
			if (!StringUtils.hasText(policyName)) {
				continue; // ignore this policy definition
			}
			RegisteredPolicy policy = new RegisteredPolicy();
			policy.setPolicyDefinition(definition);

			for(RegisteredPolicy existingPolicy:latestWarranty.getPolicies()){
				if(existingPolicy.getPolicyDefinition().getId()==definition.getId()){
					policy.setWarrantyPeriod(existingPolicy.getWarrantyPeriod());		
				}				
			}
			if (computePrice) {
				computePriceForPolicy(forItem, policy);
			}

			if (definition.getTransferDetails().getWindowPeriod() != null) {
				CalendarDate warrantyDeliveryDate = deliveryWarranty
						.getDeliveryDate();
				int specifiedWindow = warrantyDeliveryDate.through(
						forItem.getDeliveryDate()).lengthInMonthsInt();

				if (definition.getTransferDetails().getWindowPeriod() >= specifiedWindow) {
					policies.add(policy);
				}
			} else {
				policies.add(policy);
			}
		}

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
		return policies;
	}

	private void computePriceForPolicy(InventoryItem forItem,
			RegisteredPolicy policy) {
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
		DealerCriterion dealerCriterion = new DealerCriterion();
		if (forDealer == null) {
			dealerCriterion.setDealer(getLoggedInUsersDealership());
		} else {
			dealerCriterion.setDealer(forDealer);
		}
		criteria.setDealerCriterion(dealerCriterion);
		criteria.setProductType(forItem.getOfType().getProduct());
		criteria.setWarrantyRegistrationType(WarrantyRegistrationType.TRANSFER);
		Money price = this.policyService.getTransferFeeForPolicyDefinition(
				policy.getPolicyDefinition(), criteria, forItem
						.getDeliveryDate());
		policy.setPrice(price);
	}

	public String getPolicyFeeTotalForInv(Long inventoryItemId) {
		Double policyFeeSum = 0D;
		String currencySymbol = "";

		List<RegisteredPolicy> firstInventoryMappingPolicies = this.inventoryItemMappings
				.get(0).getSelectedPolicies();

		if (firstInventoryMappingPolicies.size() > 0) {
			Money price = firstInventoryMappingPolicies.get(0).getPrice();
			currencySymbol = price.breachEncapsulationOfCurrency().getSymbol();
		}

		if (inventoryItemId != null) {
			for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
				if (inventoryItemMapping.inventoryItem.getId().equals(
						inventoryItemId)) {
					for (RegisteredPolicy selectedPolicy : inventoryItemMapping
							.getSelectedPolicies()) {
						policyFeeSum += selectedPolicy.getPrice()
								.breachEncapsulationOfAmount().doubleValue();
					}

					return currencySymbol + " " + policyFeeSum;
				}
			}
		}

		return null;
	}

	public String getPolicyFeeTotal() {
		Double policyFeeSum = 0D;
		String currencySymbol = "";

		List<RegisteredPolicy> firstInventoryMappingPolicies = this.inventoryItemMappings
				.get(0).getSelectedPolicies();

		if (firstInventoryMappingPolicies.size() > 0) {
			Money price = firstInventoryMappingPolicies.get(0).getPrice();
			currencySymbol = price.breachEncapsulationOfCurrency().getSymbol();
		}

		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
			for (RegisteredPolicy selectedPolicy : inventoryItemMapping
					.getSelectedPolicies()) {
				policyFeeSum += selectedPolicy.getPrice()
						.breachEncapsulationOfAmount().doubleValue();
			}
		}

		return currencySymbol + " " + policyFeeSum;
	}

	public String show() {
		this.inventoryItemMappings.removeAll(Collections.singletonList(null));
		if (!this.inventoryItemMappings.isEmpty()) {
			Iterator<MultipleInventoryAttributesMapper> iterator = this.inventoryItemMappings.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().inventoryItem.getId() == null) {
					iterator.remove();
				}
			}
		}
		if (getInventoryItem() != null
				&& getInventoryItem().getPendingWarranty()) {
			if(getInventoryItem().getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
				addActionError("error.inventory.etrSavedAsDraft",getInventoryItem().getSerialNumber());
			} else {
				addActionError("error.inventory.pendingWarrantyForETR",getInventoryItem().getSerialNumber());
			}
			return NONE;
		}
		if(getInventoryItem() != null){
			getInventoryItem().getLatestWarranty().setPdiGenerated(Boolean.FALSE);
			WarrantyCoverageRequest coverageRequest = 
				warrantyCoverageRequestService.findByInventoryItemId(getInventoryItem().getId());
            if(coverageRequest != null && coverageRequest.isRequestPending() ){
				addActionError("message.manageExtensionForApproval.preview");
				return NONE;
			}
		}
		if(hasActionErrors()){
			return NONE;
		}
		return SUCCESS;
	}
	
	public String showWarrantyTransferFromQuickSearch() {
		if(getInventoryItem() != null){
			getInventoryItem().getLatestWarranty().setPdiGenerated(Boolean.FALSE);
			WarrantyCoverageRequest coverageRequest = 
				warrantyCoverageRequestService.findByInventoryItemId(getInventoryItem().getId());
            if(coverageRequest != null && coverageRequest.isRequestPending() ){
				addActionError("message.manageExtensionForApproval.preview");
				return NONE;
			}
		}
		if (getInventoryItem() != null
				&& getInventoryItem().getPendingWarranty()) {
			if(getInventoryItem().getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
				addActionError("error.inventory.etrSavedAsDraft",getInventoryItem().getSerialNumber());
			} else {
				addActionError("error.inventory.pendingWarrantyForETR",getInventoryItem().getSerialNumber());
			}
			return NONE;
		}
		if (this.inventoryItem != null && !this.inventoryItem.getPendingWarranty()) {
			prepareInventoryItem(this.inventoryItem);
		}
		inventoryItem.getOwner();
		return SUCCESS;
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
		InventoryItem inventory = this.inventoryItem;
		validateInventoryItem(inventory);
        List<RegisteredPolicy> transferablePolicies = new ArrayList<RegisteredPolicy>();
        MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
        mapper.setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
        mapper.setInstallationDate(inventoryItem.getInstallationDate());
        mapper.setInventoryItem(inventoryItem);   
        
        if(inventoryItem.getDeliveryDate() == null) {
        	addActionError("error.deliveryDateNotFound",inventoryItem.getSerialNumber());
        }
        if (hasErrors()) {
			return INPUT;
		}            
        if(isPolicyApplicableForDemo(this.inventoryItem)){
            transferablePolicies = fetchExistingPoliciesForUsedItem(this.inventoryItem);
        }
        if("AMER".equals(inventoryItem.getBusinessUnitInfo().getName()) && isPolicyApplicableForDemo(this.inventoryItem)){ // for americas new transferable policies should also be listed
            List<PolicyDefinition> policyDefns = policyService.findPoliciesAvailableForRegistration(inventoryItem, addressBookType, getLoggedInUsersDealership());
            filterPolicyDefns(policyDefns, transferablePolicies);
            List<RegisteredPolicy> newPolicies = warrantyUtil.createPolicies(forDealer, policyDefns, inventoryItem, false);
            if(newPolicies != null){
                for (RegisteredPolicy newPolicy : newPolicies) {
                    // new Policy which is picked up during ETR should have wrnty start date as today and end date computation remains same
                    newPolicy.setWarrantyPeriod(newPolicy.getPolicyDefinition().warrantyPeriodFor(inventoryItem, Clock.today())); 
                    transferablePolicies.add(newPolicy);
                }
            }
        }
        this.availablePolicies = transferablePolicies;
		return SUCCESS;
	}

	private boolean validateInventoryItem(InventoryItem inventoryItem) {
		boolean isValid = validateCustomerType(addressBookType);
		if(isInstallingDealerEnabled())
		isValid = validateInstallingDealer(this.installingDealer);
		if (inventoryItem.getHoursOnMachine() == null
				|| inventoryItem.getHoursOnMachine() < 0) {
			// User needs to input hoursOnMachine
			addFieldError("hoursOnMachine",
					"error.hoursOnMachineShouldBePositive", inventoryItem
							.getSerialNumber());
			isValid = false;
		}

		Warranty prevWarranty = inventoryItem.getWarranty();
		if (prevWarranty != null
				&& prevWarranty.getCustomer() != null
				&& getCustomer() != null
				&& prevWarranty.getCustomer().getId().longValue() == getCustomer()
						.getId().longValue()) {
			addFieldError("customer", "error.sameEndCustomer", inventoryItem
					.getSerialNumber());
			isValid = false;
		}

		return isValid;
	}

	private boolean validateInstallingDealer(ServiceProvider installingDealer)
	{
	boolean isValid = true;
	if(installingDealer == null){	
		addActionError("error.common.installingDealer");
		return isValid = false;		
	}
	return isValid;
	}
	
	private void validateMajorComponents(InventoryItem inventoryItem) {

		List<InventoryItemComposition> invComposition = inventoryItem.getComposedOf();
		if (invComposition != null && invComposition.size() > 0) {
			invComposition.removeAll(Collections.singleton(null));
			inventoryItem.getComposedOf().removeAll(Collections.singleton(null));
			Set<String> serialNumber = new HashSet<String>();
			Set<String> partNumber = new HashSet<String>();
			for (InventoryItemComposition part : invComposition) {
				validateSerialNumbersForParts(part.getPart().getSerialNumber());
				validatePartNumber(part.getPart());
				validateInstallationDate(inventoryItem,part.getPart());

				if ((part.getPart().getInstallationDate() != null || (part.getPart().getSerialNumber() != null && !part
						.getPart().getSerialNumber().equals("")))
						&& part.getPart().getOfType() != null) {
					validatePart(part.getPart(), inventoryItem);
					/*if (serialNumber.contains(part.getPart().getSerialNumber())
							&& partNumber.contains(part.getPart().getOfType().getNumber())) {
						addActionError("error.common.duplicatePart");

						break;

					} else {*/
						partNumber.add(part.getPart().getOfType().getNumber());
						serialNumber.add(part.getPart().getSerialNumber());
					/*}*/
				}
			}
		}
	}
	
	private boolean validatePart(InventoryItem part,InventoryItem machine)  {
		// TODO Auto-generated method stub
		try
		{
			List<InventoryItem> parts = new ArrayList<InventoryItem>();
			for(InventoryItemComposition composition:machine.getComposedOf())
			{
				parts.add(composition.getPart());
			}
		getInventoryService().findItemBySerialNumberAndModelNumber(part.getSerialNumber(), part.getOfType());
		
		if(!parts.contains(part))
		{
		addActionError("foc.widget.invalidPart");
		}
		return true;
		}
		catch(Exception e)
		{
		return true;
		}
		
	}

	private void validateSerialNumbersForParts(String serialNumber) {
		// TODO Auto-generated method stub
		if(serialNumber.equals(""))
		{
			addActionError("foc.widget.serialNumberMandatory");
		}
	}
	private void validatePartNumber(InventoryItem part) {
		// TODO Auto-generated method stub
		if(part.getOfType() == null ||part.getOfType().getNumber() == null || part.getOfType().getNumber().equals("")){
			 addActionError("foc.widget.partNumberMandatory");
		} 
		
	}

	private void validateInstallationDate(InventoryItem machine,InventoryItem part) {
		// TODO Auto-generated method stub
	
       
   
        if (part.getInstallationDate() == null) {
			// User needs to choose delivery date
			addActionError("error.installationDateNotFound",new String[] { part.getSerialNumber() });
			
		} else {
            if (part.getInstallationDate().isAfter(Clock.today())) {
				// The delivery date chosen cannot be a future date
				addActionError("error.installationDateCannotBeInFuture",new String[] { part.getSerialNumber() });
			
			}
            /*if(machine.getBuiltOn()!=null && part.getInstallationDate().isBefore(machine.getBuiltOn()))
			{
            	addActionError("error.partInstallationDateCannotBeAfterBuildDate", new String[]{part.getSerialNumber()});	
			}*/
		}
	}

	private boolean validateTransferDate(MultipleInventoryAttributesMapper inventoryItemMapper) {
		boolean isValid = true;
        CalendarDate transferDate =  inventoryItemMapper.getWarrantyDeliveryDate();
        CalendarDate installationDate = inventoryItemMapper.getInstallationDate();
        InventoryItem inventoryItem =  inventoryItemMapper.getInventoryItem();
        
		if (isBuConfigAMER() && inventoryItem.getLatestWarranty() != null
				&& (inventoryItem.getLatestWarranty().getPdiGenerated() == null || !inventoryItem
						.getLatestWarranty().getPdiGenerated())) {
			addActionError("dealerAPI.warrantyRegistration.pdiNotGenerated",
					new String[] { inventoryItem.getSerialNumber() });

			return false;
		}
        
		if (isInstallingDealerEnabled() && installationDate == null) {
			addActionError("error.warranty.installationDateRequired");
			return false;
		}

		if (isInstallingDealerEnabled() && installationDate != null && installationDate.isBefore(inventoryItem.getBuiltOn())) {
			// The installation date chosen cannot be a future date
			addActionError("dealerAPI.warrantyRegistration.installationDateCannotBeforeBuildDate",
					new String[] { inventoryItem.getSerialNumber() });

			isValid = false;
		}

		if (isInstallingDealerEnabled() && installationDate != null && installationDate.isAfter(Clock.today())) {
			// The installation date chosen cannot be a future date
			addActionError("dealerAPI.warrantyRegistration.installationDateCannotBeInFuture",
					new String[] { inventoryItem.getSerialNumber() });

			isValid = false;
		}

		if (isInstallingDealerEnabled() && installationDate != null && installationDate.isBefore(inventoryItem.getShipmentDate())) {
			// The installation date chosen by the user is before shipment date

			addActionError("dealerAPI.warrantyRegistration.installationDateBeforeShipment",
					new String[] { inventoryItem.getSerialNumber() });

			isValid = false;
		}		

		if (isInstallingDealerEnabled() && transferDate != null && transferDate.isBefore(installationDate)) {
			// The transfer date chosen by the user is before installation date
			addActionError("dealerAPI.warrantyRegistration.transferDateBeforeInstallation", new String[]{inventoryItem.getSerialNumber()});	
			
			isValid = false;
		}       
		if (transferDate == null) {
			// User needs to choose delivery date
			addFieldError("deliveryDate", "error.transferDateNotFound",
					inventoryItem.getSerialNumber());
			isValid = false;
		} else {
			if (transferDate.isAfter(Clock.today())) {
				// The delivery date chosen by the user is not on or before
				// today.
				addFieldError("deliveryDate",
						"error.transferDateCannotBeInFuture", inventoryItem
								.getSerialNumber());
				isValid = false;
			}

			Warranty earlierWarranty = inventoryItem.getWarranty();
			if (transferDate.isBefore(
					earlierWarranty.getDeliveryDate())) {
				// The delivery date chosen by the user is before the delivery
				// date of the last done warranty.
				addFieldError("deliveryDate",
						"error.transferDateBeforeLastDeliveryDate",
						inventoryItem.getSerialNumber());
				isValid = false;
			}
			if(inventoryItem.getInventoryItemAttrVals() != null  && !inventoryItem.getInventoryItemAttrVals().isEmpty()){
				CalendarDate scrapDate = null;
		        CalendarDate unScrapDate = null;
		        CalendarDate stolenDate = null;
		        CalendarDate unStolenDate = null;
		        for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem.getInventoryItemAttrVals()) {
		        	if(AttributeConstants.SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
	        			InventoryScrapTransaction scrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						scrapDate = scrap.getDateOfScrapOrUnscrap();
	        		 }
	        		 if(AttributeConstants.UN_SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
	        			 InventoryScrapTransaction unScrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
			                .convertXMLToObject(inventoryItemAttrVal.getValue());
	                     unScrapDate = unScrap.getDateOfScrapOrUnscrap();
	        		 }
	        		 if(scrapDate != null && unScrapDate != null){
	        			 if ((transferDate.isAfter(scrapDate) || transferDate.equals(scrapDate))
								&& (transferDate.isBefore(unScrapDate) || transferDate.equals(unScrapDate))
								&& !scrapDate.equals(unScrapDate)) {
							addActionError("message.scrap.machineScrapped",
									new String[] { inventoryItem
											.getSerialNumber() });
							isValid = false;
						} else {
							scrapDate = null;
							unScrapDate = null;
						}
	        		 }

			        	if(AttributeConstants.STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
		        			InventoryStolenTransaction stolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
									.convertXMLToObject(inventoryItemAttrVal.getValue());
							stolenDate = stolen.getDateOfStolenOrUnstolen();
		        		 }
		        		 if(AttributeConstants.UN_STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
		        			 InventoryStolenTransaction unStolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
				                .convertXMLToObject(inventoryItemAttrVal.getValue());
		                     unStolenDate = unStolen.getDateOfStolenOrUnstolen();
		        		 }
		        		 if(stolenDate != null && unStolenDate != null){
		        			 if ((transferDate.isAfter(stolenDate) || transferDate.equals(stolenDate))
									&& (transferDate.isBefore(unStolenDate) || transferDate.equals(unStolenDate))
									&& !stolenDate.equals(unStolenDate)) {
								addActionError("message.stole.machineStolen",
										new String[] { inventoryItem
												.getSerialNumber() });
								isValid = false;
							} else {
								stolenDate = null;
								unStolenDate = null;
							}
		        		 }
			        
		        }
		        if (scrapDate != null && unScrapDate == null
						&& (transferDate.isAfter(scrapDate) || transferDate.equals(scrapDate))) {
	        		 addActionError("message.scrap.machineScrapped",
								new String[] { inventoryItem.getSerialNumber() });
					 isValid = false;
				}
		        if (stolenDate != null && unStolenDate == null
						&& (transferDate.isAfter(stolenDate) || transferDate.equals(stolenDate))) {
	        		 addActionError("message.stole.machineStolen",
								new String[] { inventoryItem.getSerialNumber() });
					 isValid = false;
				}
			}
		}

		return isValid;
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
            if (!getLoggedInUser().isInternalUser() || this.marketingInformation.getInternalInstallType() == null){
				validateContractCode();
			}
            validateIndustryCode();
            validateMaintenanceContract();
        }else{
			addActionError("error.warranty.marketInfo");
		}
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
			if (getMarketingInformation().getCompetitionType() == null) {
				addActionError("error.marketInfo.competitionType");
			}
			if ( getMarketingInformation().getCompetitorMake() == null) {
				addActionError("error.marketInfo.competitiorMake");
			}
			if (getMarketingInformation().getCompetitorModel() == null) {
				addActionError("error.marketInfo.competitiorModel");
			}
    }

    private boolean validateMarketInfo(List<SelectedAdditionalMarketingInfo> selectedMarketingInfo) {

		for (SelectedAdditionalMarketingInfo selectedAdditionalMarketingInfo : selectedMarketingInfo) {
			if (selectedAdditionalMarketingInfo.getAddtlMarketingInfo().getInfoType().equals(
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
    
    private Integer getMaxSequenceNumber(InventoryItem inventoryItem){
		int maxSequenceNumber = -1;
		for(InventoryItemComposition composition : inventoryItem.getComposedOf()){
			if(composition.getSequenceNumber() != null && Integer.parseInt(composition.getSequenceNumber()) > maxSequenceNumber){
				maxSequenceNumber = Integer.parseInt(composition.getSequenceNumber());
			}
		}
		return maxSequenceNumber;
	}
    
    private boolean isDocumentAttachedOfType(
			List<Document> attachments, String documentType) {
		boolean itdrAttached = false;
		for (Document attachment : attachments) {
			if (attachment.getUnitDocumentType() != null
					&& attachment.getUnitDocumentType().getDescription().equalsIgnoreCase(documentType)) {
				itdrAttached = true;
			}
		}
		return itdrAttached;
	}
	
	private void validateDocumentType(InventoryItem inventoryItem, List<Document> attachments) {
		attachments.removeAll(Collections.singleton(null));
		for (Document attachment : attachments){
			if(attachment.getUnitDocumentType() == null){
				addActionError("error.document.documentTypeMandatory", attachment.getFileName());
			}
		}
	}
    
	private boolean validateMandatoryAttachments(InventoryItem inventoryItem,
			List<Document> attachments) {
		attachments.removeAll(Collections.singleton(null));
		boolean documentAttached = true;
		if (attachments == null || attachments.isEmpty()) {
			documentAttached = false;
		}
		if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
			boolean pdiAttached = true;
			boolean authorizationAttached = true;
			if (!documentAttached || !isDocumentAttachedOfType(attachments, AdminConstants.PDI)) {
				pdiAttached = false;
			}
			if (!documentAttached || !isDocumentAttachedOfType(attachments, AdminConstants.AUTHORIZATION)) {
				authorizationAttached = false;
			}
			if(isAdditionalInformationDetailsApplicable() && marketingInformation.getInternalInstallType() != null){
				pdiAttached = true;
				authorizationAttached = true;
			}
			if (!pdiAttached) {
				addActionError("error.amerDeliveryReport.pdi.mandatory", inventoryItem.getSerialNumber());
				documentAttached = false;
			}
			if (!authorizationAttached) {
				addActionError("error.amerDeliveryReport.authorization.mandatory", inventoryItem.getSerialNumber());
				documentAttached = false;
			}
		} else if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(!documentAttached || !isDocumentAttachedOfType(attachments, AdminConstants.ITDR)) {
				addActionError("error.emeaDeliveryReport.itdr.mandatory", inventoryItem.getSerialNumber());
				documentAttached = false;
			}
		}
		return documentAttached;
	}
    
	private boolean isPolicyMandatory(){
		return configParamService.getBooleanValue(ConfigName.POLICY_MANDATORY.getName());
	}
	
	private void validateAddressBookType(){
		if(!StringUtils.hasText(this.addressBookType) || customerTypes.get(this.addressBookType) == null){
			addActionError("error.customerTypeNotSelected");
		}
	}
	
	private boolean isTransferReportSubmitted(InventoryItem inventoryItem) {
		List<String> transactionTypes = new ArrayList<String>();
		transactionTypes.add(InvTransationType.ETR.getTransactionType());
		for(Warranty warranty : inventoryItem.getWarranties(transactionTypes)) {
			if (warranty.isValidStatus()) {
				return true;
			}
		}
		return false;
	}
	
	public String confirmTransfer() {
 		this.inventoryItemMappings.removeAll(Collections.singletonList(null));		
		boolean isCustomerSet = validateCustomer();
		removeUnselectedItems();
		if(isLoggedInUserAnInternalUser() && !isForDealerInvCurrentOwner()){
			addActionError("error.cannotSubmitWithOtherDealer");
		}
		if (this.inventoryItemMappings.isEmpty()) {
			addActionError("error.noItemsSelected");
		} else {
			List<InventoryItem> inventories = new ArrayList<InventoryItem>();
			for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
				Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
				InventoryItem inventoryItem = inventoryItemMapping
						.getInventoryItem();
				if (isTransferReportSubmitted(inventoryItem)) {
					addActionError("message.etrSavedForItem", inventoryItem.getSerialNumber());
				}
				if (!hasErrors() && inventoryItem != null
								&& inventoryItem.getPendingWarranty()) {
					if(inventoryItem.getLatestWarranty().getStatus().getStatus().equals(WarrantyStatus.DRAFT.getStatus())) {
						addActionError("error.inventory.etrSavedAsDraft",inventoryItem.getSerialNumber());
					} else {
						addActionError("error.inventory.pendingWarrantyForETR",inventoryItem.getSerialNumber());
					}
					return INPUT;
				}
				
			//	validateMajorComponents(inventoryItem);
				this.warrantyUtil.validateAdditionalComponents(inventoryItem, errorCodeMap);
				if(isBuConfigAMER()){
					this.warrantyUtil.validateInstallType(inventoryItem,marketingInformation,errorCodeMap);
				}
				setErrorCodesMapToActionErrors(errorCodeMap);	
				if (isMarketInfoApplicable()) {
					validateMarketInfo(inventoryItemMapping.getSelectedMarketingInfo());
				}
                inventoryItem.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
				removeUnselectedPolicies(inventoryItemMapping);
				if(!inventories.contains(inventoryItem))
    			{
    			inventories.add(inventoryItem);
    			}
    			else
    			{
    			addActionError("error.common.duplicateUnit");
    			}	

				
				// We can't inline this in the next if check, since it won't get
				// called if customer is not set.
				validateAddressBookType();
				boolean isValidItem = validateInventoryItemMapping(inventoryItemMapping);
				validateDocumentType(inventoryItem, inventoryItemMapping.getAttachments());
				validateMandatoryAttachments(inventoryItem, inventoryItemMapping.getAttachments());
				Collections.sort(inventoryItemMapping.getSelectedPolicies());
				if (isCustomerSet & isValidItem) {
					List<RegisteredPolicy> selectedPolicies = inventoryItemMapping
							.getSelectedPolicies();
					List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(
							selectedPolicies.size());
					if(isPolicyMandatory() && selectedPolicies.isEmpty()){
						addActionError("error.inventory.noTranferablePlans", inventoryItem.getSerialNumber());
					}
					for (RegisteredPolicy registeredPolicy : selectedPolicies) {
						selectedPolicyDefs.add(registeredPolicy
								.getPolicyDefinition());
					}
					if (!hasErrors()) {
						try {
							inventoryItemMapping.setSelectedPolicies(createPolicies(selectedPolicyDefs, inventoryItem,
									true));
						} catch (PolicyException e) {
							addActionError("error.registeringEtrForItem");
						}
					}
					if(isDisclaimerAvailable(inventoryItemMapping) && !inventoryItemMapping.isDisclaimerAccepted()){
	                	addActionError("error.disclaimer.notAccepted");
	                }
					if(inventoryItemMapping.isWaiverInformationEditable()
								&& inventoryItemMapping.isDisclaimerAccepted())
						validateWaiverInformation(inventoryItemMapping.getDieselTierWaiver());
				}
			}
		}
		if (isAdditionalInformationDetailsApplicable() || getMarketingInformation()!=null) {
			validateMarketingInfo();
		}
		if (hasErrors()) {
			return INPUT;
		}

		this.confirmTransfer = true;	
		return SUCCESS;
	}	


	private void validateWaiverInformation(DieselTierWaiver dieselTierWaiver){
		if(dieselTierWaiver==null || dieselTierWaiver.getApprovedByAgentName().isEmpty()){
			addActionError("error.waiver.AgentName");
		}
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
	
	public String engineTierCountryMapping() {
		MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
		mapper.setInventoryItem(inventoryItem);
		inventoryItemMappings.add(inventoryItemMappingIndex,mapper);
		return engineTierCountryMapping(mapper);
	}
	
	public String engineTierCountryMapping(MultipleInventoryAttributesMapper mapper) {
		if (!addressBookType.equalsIgnoreCase("EndCustomer"))
			return INPUT;
		Party customer = this.customer;
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
				if(mapper.getDieselTierWaiver()==null)
					mapper.setDieselTierWaiver(new DieselTierWaiver());
				mapper.getDieselTierWaiver().setDisclaimer(tierTierMapping.getI18NWaiverText());
				return SUCCESS;
			}
		}
		return INPUT;
	}

	public Boolean isDisclaimerAvailable(MultipleInventoryAttributesMapper mapper){
		InventoryItem curItem = this.inventoryItem;
		this.inventoryItem = mapper.getInventoryItem();
		boolean isAvailable = engineTierCountryMapping(mapper).equals(SUCCESS);
		this.inventoryItem = curItem;
		return isAvailable;
	}
	
	private void removeUnselectedItems() {
		for (Iterator<MultipleInventoryAttributesMapper> mapperIterator = this.inventoryItemMappings
				.iterator(); mapperIterator.hasNext();) {
			if (mapperIterator.next().getInventoryItem().getId() == null) {
				mapperIterator.remove();
			}
		}
	}

	private boolean validateInventoryItemMapping(
			MultipleInventoryAttributesMapper mapper) {
		InventoryItem inventoryItem = mapper.getInventoryItem();

		return validateTransferDate(mapper) & validateInventoryItem(inventoryItem);
	}

	private void removeUnselectedPolicies(
			MultipleInventoryAttributesMapper mapper) {
		for (Iterator<RegisteredPolicy> iterator = mapper.getSelectedPolicies()
				.iterator(); iterator.hasNext();) {
			if (iterator.next() == null) {
				iterator.remove();
			}
		}
	}

	private boolean validateCustomer() {
		/*if (getCustomer() == null) {
			addActionError("error.addCustomerInfo");
			return false;
		}

		return true;*/
	    if (!(getAddressBookType().toUpperCase().equals(AdminConstants.DEALER_RENTAL) 
        		||getAddressBookType().toUpperCase().equals("NATIONALACCOUNT")) 
        		&& getCustomer() == null)
        {
            addActionError("error.addCustomerInfo");
            return false;
        }
		return true;
	}

	private InventoryItem setInventoryItemAttributes(InventoryItem invItem,
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		if(inventoryItemMapping.getOem()!=null && inventoryItemMapping.getOem().getCode()!=null)
		invItem.setOem(inventoryItemMapping.getOem());
		invItem.setInstallingDealer(this.getInstallingDealer());
		invItem.setVinNumber(inventoryItemMapping.getEquipmentVIN());
		invItem.setOperator(this.getOperator());
		invItem.setFleetNumber(inventoryItemMapping.getFleetNumber());
		invItem.setInstallationDate(inventoryItemMapping.getInstallationDate());
		return invItem;
	}
	
	private List<PolicyDefinition> createSelectedPolicies(MultipleInventoryAttributesMapper inventoryItemMapping) {
		List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(inventoryItemMapping
				.getSelectedPolicies().size()); 
		for (RegisteredPolicy registeredPolicy : inventoryItemMapping.getSelectedPolicies()) {
			if (registeredPolicy != null) {
				selectedPolicyDefs.add(registeredPolicy.getPolicyDefinition());
			}
		}
		return selectedPolicyDefs;
	}
	
	
	private void createWarranty(MultipleInventoryAttributesMapper inventoryItemMapping, InventoryItem invItem) {
		String multiDRETRNumber = null;
		if (!inventoryItemMappings.get(0).getInventoryItem().getPendingWarranty()) {
			multiDRETRNumber = warrantyService.getWarrantyMultiDRETRNumber();
		}
		Warranty warranty = invItem.getDraftWarranty() == null && this.warranty != null
				&& this.warranty.getId() != null && this.warranty.getForItem() != null
				&& this.warranty.getForItem().equals(invItem) ? this.warranty : invItem.getDraftWarranty();
		if(getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL)){
            this.addressForTransfer = warrantyUtil.populateAddressForTransfer(forDealer.getAddress());
		}
		if (warranty == null) {
			warranty = new Warranty();
			warranty.setDraft(this.saveAsDraft);
			warranty.setInventoryItem(invItem);
		} else {
			multiDRETRNumber = warranty.getMultiDRETRNumber();
		}
		warranty = setWarrantyAttributes(warranty, inventoryItemMapping);
		warranty.setFiledDate(Clock.today());
		warranty.setMultiDRETRNumber(multiDRETRNumber);
		if (!this.saveAsDraft) {
			warranty.setMultiDRETRNumber(multiDRETRNumber);
			if(isManualApprovalRequiredForETR()){
				warranty.setDraft(true);
			}	
			else{
				warranty.setDraft(false);
			}
			
			if(!warranty.isDraft() && manualApprovalRequiredForDemoTruck(warranty.getForItem())){
				warranty.setDraft(true);
			}
			
			if(!warranty.isDraft() && (inventoryItemMapping.getSelectedPolicies() == null
					|| inventoryItemMapping.getSelectedPolicies().isEmpty())){
				warranty.setDraft(true);
			}
			
			warranty.setStatus(WarrantyStatus.SUBMITTED);
		} else {
			//commenting this line because when the Equipment Transfer is saved in Draft mode ,if this set to true then It cannot be saved
			//In normal flow like Submitting the Transfer,first this flag will be false and after creating warranty it will be True and Finally
			//once if it submitted again it will be set to false,because of this flow this has been changed accordingly
			//invItem.setPendingWarranty(true);
			warranty.setDraft(true);
			warranty.setStatus(WarrantyStatus.DRAFT);
			warranty.setStatus(WarrantyStatus.DRAFT);
			try {
				warrantyService.createPoliciesForWarranty(warranty);
			} catch (PolicyException ex) { // FIXME: Narrow this exception
				// down
				logger.error("Error saving warranty for " + invItem, ex);
				addActionError("error.registeringWarrantyForItem");
			}
		}
		if(isForPrintPDI()){
			warranty.setPdiGenerated(Boolean.TRUE);
		}
	}

	public String transfer() {
		for (MultipleInventoryAttributesMapper mapper : inventoryItemMappings) {
			if (isTransferReportSubmitted(mapper.getInventoryItem())) {
				addActionError("message.etrSavedForItem", mapper.getInventoryItem().getSerialNumber());
			}
		}
		if (hasErrors()) {
			return INPUT;
		}
		this.forETR = true;
		StringBuffer serialNumbers = new StringBuffer(100);
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
		boolean isManualApproval = isManualApprovalRequiredForETR();
		if(!isBuConfigAMER() && (isLoggedInUserAnAdmin()||isLoggedInUserAnInvAdmin())){
			isManualApproval = false;
		}
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
            inventoryItemMapping.getInventoryItem().setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
            try {
				inventoryItemMapping
						.setSelectedPolicies(createPolicies(
								createSelectedPolicies(inventoryItemMapping), inventoryItemMapping.getInventoryItem(), true));
			} catch (PolicyException e) {
				addActionError("error.registeringEtrForItem");
			}
            
            if(!isManualApproval && manualApprovalRequiredForDemoTruck(inventoryItemMapping.getInventoryItem())){
				isManualApproval = true;
			}
            
            if(!isManualApproval && (inventoryItemMapping.getSelectedPolicies() == null
					|| inventoryItemMapping.getSelectedPolicies().isEmpty())){
				isManualApproval = true;
			}
            
			removeUnselectedPolicies(inventoryItemMapping);
			InventoryItem inventoryItem = inventoryItemMapping
					.getInventoryItem();
			if (!isSaveAsDraft() && !isManualApproval) {
				inventoryItem = setInventoryItemAttributes(inventoryItem, inventoryItemMapping);
			}			
			//this.customer.setIndividual(INDIVIDUAL.equals(this.customerType));
			createWarranty(inventoryItemMapping,inventoryItem);					
			inventoryItem.setPendingWarranty(true);
			inventoryItem.setDeliveryDate(inventoryItem.getDeliveryReportDate());
			if(isDisclaimerAvailable(inventoryItemMapping)){
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
				inventoryItem.getLatestWarranty().setDieselTierWaiver(inventoryItemMapping.getDieselTierWaiver());
				inventoryItem.setDisclaimerInfo(inventoryItemMapping.getDieselTierWaiver().getDisclaimer());
       		}else{
       			inventoryItem.getLatestWarranty().setDieselTierWaiver(null);
       			inventoryItem.setDisclaimerInfo(null);
       		}
			try {
				inventoryItem.setOwnershipState(getInventoryService()
						.findOwnershipStateByName(
								OwnershipState.PRE_OWNED.getName()));
				if (!this.saveAsDraft) {
					itemsForTask.add(inventoryItem);
				}
				serialNumbers.append(inventoryItem.getSerialNumber());
				serialNumbers.append(", ");
			} catch (Exception ex) {
				logger.error("Error transfering ETR for " + inventoryItem,
						ex);
				addActionError("error.transferingEtrForItem",
						new String[] { inventoryItem.getSerialNumber() });
				return INPUT;
			}
			Integer maxSequenceNumber = getMaxSequenceNumber(inventoryItem);
    		for(InventoryItemComposition composition : inventoryItem.getComposedOf()){
    			if(composition.getSequenceNumber() == null){
    				maxSequenceNumber = maxSequenceNumber + 2;
    				composition.setSequenceNumber(maxSequenceNumber.toString());
    			}
    		}
		}

		int serialNumbersLength = serialNumbers.length();
		if (serialNumbersLength >= 2) {
			serialNumbers.delete(serialNumbersLength - 2, serialNumbersLength);
		}

		if (!isSaveAsDraft() && !itemsForTask.isEmpty() && !hasErrors()) {

			warrantyService.createInventoryAndCreateWarrantyReport(itemsForTask, isManualApproval);		

           

			addActionMessage("message.etrSavedForItem", serialNumbers);
		} else {
			addActionMessage("message.warrantyTransferredForItem",
					serialNumbers);
		}

		return SUCCESS;
	}

	public String showAttachments() {
		return SUCCESS;
	}

	public String attachDocs() {

		return SUCCESS;
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

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	public List<ListOfValues> getOems(String className) {
		List<ListOfValues> oems = null;
		oems = lovRepository.findAllActive(className);
		return oems;
	}
	
	public String getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public Set<String> getAllSalesMan() {
		return this.nameToSalesMan.keySet();
	}

	public InventoryItem getInventoryItem() {
		return this.inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public InventoryTransaction getInvTransaction() {
		return this.invTransaction;
	}

	public void setInvTransaction(InventoryTransaction invTransaction) {
		this.invTransaction = invTransaction;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

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
        if(maxTransfer!=null && maxTransfer.longValue()==0){
            isWithinMaxNoTransfer = false;
            return isWithinMaxNoTransfer;
        }
        if (etrWarranties != null && etrWarranties.size() > 0) {
			if (maxTransfer != null) {
				isWithinMaxNoTransfer = false;
				for (Warranty warranty : etrWarranties) {
					if (warranty.getForTransaction()!=null &&
                            warranty.getForTransaction().getBuyer().isCustomer()) {
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

	public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
		return this.inventoryItemMappings;
	}

	public void setInventoryItemMappings(
			List<MultipleInventoryAttributesMapper> inventoryItemMappings) {
		this.inventoryItemMappings = inventoryItemMappings;
	}

	public List<RegisteredPolicy> getAvailablePolicies() {
		return this.availablePolicies;
	}

	public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
		this.availablePolicies = availablePolicies;
	}

	public boolean isConfirmTransfer() {
		return this.confirmTransfer;
	}

	public void setConfirmTransfer(boolean confirmTransfer) {
		this.confirmTransfer = confirmTransfer;
	}

	public boolean isSaveAsDraft() {
		return this.saveAsDraft;
	}

	public void setSaveAsDraft(boolean saveAsDraft) {
		this.saveAsDraft = saveAsDraft;
	}

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

	public int getInventoryItemIndex() {
		return this.inventoryItemIndex;
	}

	public void setInventoryItemIndex(int inventoryItemIndex) {
		this.inventoryItemIndex = inventoryItemIndex;
	}

	public MarketingInformation getMarketingInformation() {
		return this.marketingInformation;
	}

	public void setMarketingInformation(
			MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public String getRegistrationComments() {
		return this.registrationComments;
	}

	public void setRegistrationComments(String registrationComments) {
		this.registrationComments = registrationComments;
	}

	public boolean isAllowInventorySelection() {
		return this.allowInventorySelection;
	}

	public void setAllowInventorySelection(boolean allowInventorySelection) {
		this.allowInventorySelection = allowInventorySelection;
	}

	public boolean isFirstTimeETR() {
		return firstTimeETR;
	}

	public void setFirstTimeETR(boolean firstTimeETR) {
		this.firstTimeETR = firstTimeETR;
	}

	@Override
	public boolean isSendInventoryId() {
		return true;
	}

	// FIXME: Any exception that occurs comes up as a java script
	// Hence returning "{}" for any exception that occurs.
	public String getJSONifiedAttachmentList() {
		try {
			Warranty warranty = this.inventoryItemMappings.get(
					this.inventoryItemIndex).getInventoryItem()
					.getDraftWarranty();
			List<Document> attachments;
			if (warranty != null && warranty.getAttachments() != null) {
				this.inventoryItemMappings.get(this.inventoryItemIndex)
						.getAttachments().addAll(warranty.getAttachments());
			}
			attachments = this.inventoryItemMappings.get(
					this.inventoryItemIndex).getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getUnitDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public String getJSONifiedAttachmentList(int inventoryItemMappingsIndex) {
		try {
			//Warranty warranty = this.inventoryItemMappings.get(inventoryItemMappingsIndex).getInventoryItem().getDraftWarranty();
			List<Document> attachments;
			/*if (warranty != null
					&& warranty.getAttachments() != null
					&& (this.inventoryItemMappings.get(inventoryItemMappingsIndex)
							.getAttachments() == null || this.inventoryItemMappings
							.get(inventoryItemMappingsIndex).getAttachments()
							.size() <= 0)) {
				this.inventoryItemMappings.get(inventoryItemMappingsIndex)
						.getAttachments().addAll(warranty.getAttachments());
			} not needed since mappings' attachments are set from warranties during prepare
			  and causes problems from draft inboxes */
			attachments = this.inventoryItemMappings.get(inventoryItemMappingsIndex).getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getUnitDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public String getJSONifiedCommonAttachmentList() {
		try {
			return getUnitDocumentListJSON(commonAttachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}

	public boolean isForETR() {
		return this.forETR;
	}

	public void setForETR(boolean forETR) {
		this.forETR = forETR;
	}

	public boolean isSelectedPolicy(Long avaliablePolicyDefId,
			List<RegisteredPolicy> selectedPolicies) {
		for (RegisteredPolicy selectedPolicy : selectedPolicies) {
			if (avaliablePolicyDefId.equals(selectedPolicy
					.getPolicyDefinition().getId())) {
				return true;
			}
		}
		return false;
	}

	public CalendarDate getWarrantyStartDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				return shipmentDate;
			}else{
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
	
	// returning policy end date based on inventory delivery date and months coverage
	public CalendarDate getPolicyEndDate(InventoryItem inventoryItem, PolicyDefinition policyDefinition) {
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
		return warrantyEndDate.isAfter(deliveryDate) ? warrantyEndDate : deliveryDate;
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
	
	public Address getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(Address customerAddress) {
		this.customerAddress = customerAddress;
	}

	public Organization getDealerOrganization() {
		return dealerOrganization;
	}

	public void setDealerOrganization(Organization dealerOrganization) {
		this.dealerOrganization = dealerOrganization;
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setServletRequest(HttpServletRequest req) {
		this.request = req;
	}

	public ServiceProvider getForDealer() {
		return forDealer;
	}

	public void setForDealer(ServiceProvider forDealer) {
		this.forDealer = forDealer;
	}

	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isDealerNameSelected() {
		return dealerNameSelected;
	}

	public void setDealerNameSelected(boolean dealerNameSelected) {
		this.dealerNameSelected = dealerNameSelected;
	}

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
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

	public void populateCustomerTypes() {

		if (this.configParamService == null) {
			initDomainRepository();
		}

		// need to put BU filter

		Map<Object, Object> keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_ETR
						.getName());

		if (keyValueOfCustomerTypes != null
				&& !keyValueOfCustomerTypes.isEmpty()) {
			customerTypes.putAll(keyValueOfCustomerTypes);
		}
	}

	private boolean validateCustomerType(String addressBookType) {
		boolean isValid = true;
			if (addressBookType == null
					|| SELECT.equalsIgnoreCase(addressBookType)) {
				if (!firstTimeETR){
					addActionError("error.customerTypeNotSelected");
				}
				return isValid = false;
			}

		return isValid;
	}

	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.configParamService = (ConfigParamService) beanLocator
				.lookupBean("configParamService");
	}

	private boolean isApplicableCustomerType(PolicyDefinition policyDefinition) {
		boolean isApplicableCustomerType = false;
		for (ApplicableCustomerTypes customerType : policyDefinition
				.getCustomertypes()) {
			if ((addressBookType.equalsIgnoreCase(customerType.getType())))//||(addressBookType.equalsIgnoreCase("Dealer Rental")))
				{
				isApplicableCustomerType = true;
				break;
			}
		}

		return isApplicableCustomerType;
	}

	public boolean isModifyDRorETR() {
		return isModifyDRorETR;
	}

	public void setModifyDRorETR(boolean isModifyDRorETR) {
		this.isModifyDRorETR = isModifyDRorETR;
	}
	
	public boolean canModifyDRorETR() {
		return true;
	}

	public boolean isManualApprovalRequiredForETR() {
		return configParamService.getBooleanValue(
				ConfigName.MANUAL_APPROVAL_FLOW_FOR_ETR.getName())
				.booleanValue();
	}

	
	private boolean isForDealerInvCurrentOwner() {
		for(MultipleInventoryAttributesMapper mapper : inventoryItemMappings){
			if(mapper.getInventoryItem()!=null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, mapper.getInventoryItem().getCurrentOwner())){
				if(this.forDealer.equals(new HibernateCast<ServiceProvider>().cast(mapper.getInventoryItem().getCurrentOwner()))){
					return true;
				}
			}
		}
		return false;
	}
	
	public String saveAsDraft() {
		this.inventoryItemMappings.removeAll(Collections.singletonList(null));
		removeUnselectedItems();
		if (this.inventoryItemMappings.isEmpty()) {
			addActionError("error.noItemsSelected");
			if(forPrintPDI) {
				setSaveAsDraft(Boolean.FALSE);
			}
			return INPUT;
		}		
		if(isLoggedInUserAnInternalUser() && !isForDealerInvCurrentOwner()){
			addActionError("error.etr.cannotSaveWithOtherDealer");
		}
		this.forETR = true;
		StringBuffer serialNumbers = new StringBuffer();		
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();			
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
			try {
				inventoryItemMapping.setSelectedPolicies(createPolicies(createSelectedPolicies(inventoryItemMapping),
						inventoryItemMapping.getInventoryItem(), true));
			} catch (PolicyException e) {
				addActionError("error.registeringEtrForItem");
			}
			removeUnselectedPolicies(inventoryItemMapping);
			InventoryTransaction newTransaction = new InventoryTransaction();
			InventoryItem inventoryItem = inventoryItemMapping
					.getInventoryItem();
			if (isTransferReportSubmitted(inventoryItem)) {
				addActionError("message.etrSavedForItem", inventoryItem.getSerialNumber());
			}
			if(forPrintPDI && !BrandType.UTILEV.getType().equalsIgnoreCase(inventoryItem.getBrandType())) {
				String dealerBrand = new HibernateCast<Dealership>()
					.cast(getForDealer()).getBrand();
				if(null!=dealerBrand && !dealerBrand.equals(inventoryItem.getBrandType())) {
					addActionError("error.pdi.differentBrand", inventoryItem.getSerialNumber());
				}
			}
			
			//validateMajorComponents(inventoryItem);
			Integer maxSequenceNumber = getMaxSequenceNumber(inventoryItem);
    		for(InventoryItemComposition composition : inventoryItem.getComposedOf()){
    			if(composition.getSequenceNumber() == null){
    				maxSequenceNumber = maxSequenceNumber + 2;
    				composition.setSequenceNumber(maxSequenceNumber.toString());
    			}
    		}
			if (isMarketInfoApplicable()) {
				validateMarketInfo(inventoryItemMapping.getSelectedMarketingInfo());
			}			
			//inventoryItem.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());        
            Collections.sort(inventoryItem.getTransactionHistory());
			newTransaction.setTransactionOrder(new Long(inventoryItem
					.getTransactionHistory().size() + 1));
			createWarranty(inventoryItemMapping, inventoryItem);			
			if(!itemsForTask.contains(inventoryItem))
			{
			itemsForTask.add(inventoryItem);
			}
			else
			{
			addActionError("error.common.duplicateUnit");
			}			
			 // retrieving customer object for displaying customer details
			if(null != customer){
				operator=customerService.findCustomerById(customer.getId());
			}
			serialNumbers.append(inventoryItem.getSerialNumber());
			serialNumbers.append(", ");
			if(inventoryItem.getDieselTier() != null && customer != null && customer.getAddress() != null 
					&& isDisclaimerAvailable(inventoryItemMapping)){
				inventoryItemMapping.getDieselTierWaiver().setInventoryItem(
						inventoryItem);
				inventoryItemMapping.getDieselTierWaiver().setDestinationCountry(
						customer.getAddress().getCountry());
				inventoryItemMapping.getDieselTierWaiver().setCountryEmissionRating(
						inventoryItem.getDieselTier());
				try {
					dieselTierWaiverService.save(inventoryItemMapping.getDieselTierWaiver());
					inventoryItemMapping.setDieselTierWaiver(dieselTierWaiverService
							.findLatestDieselTierWaiver(inventoryItem));
				} catch (Exception e) {
					logger.error("Error while saving data - DieselTierWaiver"
							+ inventoryItemMapping.getDieselTierWaiver());
				}
				inventoryItem.getLatestWarranty().setDieselTierWaiver(inventoryItemMapping.getDieselTierWaiver());
				inventoryItem.setDisclaimerInfo(inventoryItemMapping.getDieselTierWaiver().getDisclaimer());
       		}else{
       			inventoryItem.getLatestWarranty().setDieselTierWaiver(null);
       			inventoryItem.setDisclaimerInfo(null);
       		}
		}
		if(!hasErrors())
		{		
		try {
			warrantyService.submitWarrantyReport(itemsForTask);
		} catch (PolicyException ex) { // FIXME: Narrow this exception down
			logger.error("Error saving ETR for " + inventoryItem, ex);
			addActionError("error.registeringEtrForItem");
			if(forPrintPDI) {
				setSaveAsDraft(Boolean.FALSE);
			}
			return INPUT;
		}
		}
		int serialNumbersLength = serialNumbers.length();
		if (serialNumbersLength >= 2) {
			serialNumbers.delete(serialNumbersLength - 2, serialNumbersLength);
		}
		if(hasErrors())
		{
			if(forPrintPDI) {
				setSaveAsDraft(Boolean.FALSE);
			}
		return INPUT;	
		}
		else
		{
			if(isForPrintPDI() && !hasErrors()){
				
				this.pdiGeneration = true;
				addActionMessage("message.draftEtrSavedForItems", serialNumbers);
				if(forPrintPDI) {
					setSaveAsDraft(Boolean.FALSE);
				}
			return INPUT;	
			}
			else{
				addActionMessage("message.draftEtrSavedForItems", serialNumbers);
				if(forPrintPDI) {
					setSaveAsDraft(Boolean.FALSE);
				}
				return SUCCESS;
			}
		}
	}

	
	private Warranty setWarrantyAttributes(Warranty warranty,
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		warranty.getAttachments().clear();
		if (getCommonAttachments() != null)
			warranty.getAttachments().addAll(getCommonAttachments());
		if (inventoryItemMapping.getAttachments() != null)
			warranty.getAttachments().addAll(inventoryItemMapping.getAttachments());

		if (inventoryItemMapping.getOem() != null && inventoryItemMapping.getOem().getCode() != null)
			warranty.setOem(inventoryItemMapping.getOem());
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
		warranty.setRegistrationComments(getRegistrationComments());
		warranty.getPolicies().clear();
		WarrantyAudit warrantyAudit = new WarrantyAudit();
		warrantyAudit.setSelectedPolicies(inventoryItemMapping.getSelectedPolicies());		
		warrantyAudit.setExternalComments(getRegistrationComments());
		warranty.getWarrantyAudits().add(warrantyAudit);
		warranty.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());
		warranty.setAddressForTransfer(this.addressForTransfer);
		warranty.setCustomer(this.customer);
		warranty.setCustomerType(this.addressBookType);
		warranty.setOperator(this.operator);
		warranty.setOperatorType(this.addressBookTypeForOperator);
		warranty.setOperatorAddressForTransfer(this.operatorAddressForTransfer);
		warranty.setFiledBy(getLoggedInUser());
		warranty.setTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.ETR.name()));
		if (isLoggedInUserADealer()) {
			if(isLoggedInUserAParentDealer() && !inventoryItemMapping.getInventoryItem().getCurrentOwner().equals(getLoggedInUsersOrganization())){
				if(behalfDealer!=null && !behalfDealer.isEmpty()){
					warranty.setForDealer(forBehalfDealer);
				}else{
					warranty.setForDealer(getLoggedInUsersDealership());
				}
			}
			else{
                warranty.setForDealer(getLoggedInUsersDealership());
            }
		} else {
			warranty.setForDealer(forDealer);
		}
		warranty.setEquipmentVIN(inventoryItemMapping.getEquipmentVIN());
		warranty.setFleetNumber(inventoryItemMapping.getFleetNumber());
		warranty.setInstallingDealer(this.getInstallingDealer());
		warranty.getForItem().setLatestWarranty(warranty);
		DocumentTransportUtils.markDocumentsAsAttached(warranty.getAttachments());
		return warranty;
	}
	
	
	public boolean isAdditionalInformationDetailsApplicable() {
		return this.configParamService
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
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

	public boolean isDuplicateSerialNumber() {
		return duplicateSerialNumber;
	}

	public void setDuplicateSerialNumber(boolean duplicateSerialNumber) {
		this.duplicateSerialNumber = duplicateSerialNumber;
	}

    public String getSelectedBusinessUnit() {
        return SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
    }

    public WarrantyTaskInstance getWarrantyTaskInstance() {
        return warrantyTaskInstance;
    }

    public void setWarrantyTaskInstance(WarrantyTaskInstance warrantyTaskInstance) {
        this.warrantyTaskInstance = warrantyTaskInstance;
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
    private Map<String,String> listIfPreviousOwner = new HashMap<String,String>();
    private List<String> listOfAdditionalInfo = new ArrayList<String>();
    private List<CompetitionType> listOfCompetitionTypes = new ArrayList<CompetitionType>();
    private final Map<String, User> nameToSalesMan = new LinkedHashMap<String, User>();
   // private List<MarketType> listOfMarketTypes = new ArrayList<MarketType>();
    private List<Market> listOfMarketTypes = new ArrayList<Market>();
    private List<ContractCode> listOfContractCodes =new ArrayList<ContractCode>();
    private List<InternalInstallType> listOfInternalInstallTypes = new ArrayList<InternalInstallType>();
	private List<MaintenanceContract> listofMaintenanceContracts=new ArrayList<MaintenanceContract>();
	private List<IndustryCode> listOfIndustryCodes=new ArrayList<IndustryCode>();
    private List<Market> listOfMarketApplications = new ArrayList<Market>();
    private List<User> listOfSalesPersons = new ArrayList<User>();
    
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

/*    public List<MarketType> getListOfMarketTypes() {
        return listOfMarketTypes;
    }

    public void setListOfMarketTypes(List<MarketType> listOfMarketTypes) {
        this.listOfMarketTypes = listOfMarketTypes;
    }*/

    @Required
    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }
    
    public boolean fetchPriceForPolicyForCurrency(PolicyDefinition policyDefinition,
			InventoryItem inventoryItem) {
        for (PolicyFees transferFees : policyDefinition.getPolicyFees()) {
          if(transferFees.getIsTransferable() && inventoryItem.getCurrentOwner()!=null &&
                  inventoryItem.getCurrentOwner().getPreferredCurrency().getCurrencyCode().
                  equalsIgnoreCase(transferFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode())){
              return transferFees.getPolicyFee().isZero();
          }
        }
		return false;
	}

	public WarrantyCoverageRequestService getWarrantyCoverageRequestService() {
		return warrantyCoverageRequestService;
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}    

    public boolean isGenericAttachmentRequired() {
        final boolean genericAttachmentAllowedForBU =
                configParamService.getBooleanValue(ConfigName.ENABLE_GENERIC_ATTACHMENT.getName()).booleanValue();
        return genericAttachmentAllowedForBU && (inventoryItemMappings == null ||
                inventoryItemMappings.size() > 1);
    }

    public List<Document> getCommonAttachments() {
        return commonAttachments;
    }

    public void setCommonAttachments(List<Document> commonAttachments) {
        this.commonAttachments = commonAttachments;
    }
    
    public void setOperatorAddressForTransfer(AddressForTransfer operatorAddressForTransfer) {
		this.operatorAddressForTransfer = operatorAddressForTransfer;
	}

	public AddressForTransfer getOperatorAddressForTransfer() {
		return operatorAddressForTransfer;
	}

	public void setInstallingDealer(ServiceProvider installingDealer) {
		this.installingDealer = installingDealer;
	}

	public ServiceProvider getInstallingDealer() {
		return installingDealer;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getJsonString() {
		return jsonString;
	} 
	
	public int getInventoryItemMappingIndex() {
		return inventoryItemMappingIndex;
	}

	public void setInventoryItemMappingIndex(int inventoryItemMappingIndex) {
		this.inventoryItemMappingIndex = inventoryItemMappingIndex;
	}

	public AdditionalMarketingInfoService getAdditionalMarketingInfoService() {
		return additionalMarketingInfoService;
	}

	public void setAdditionalMarketingInfoService(AdditionalMarketingInfoService additionalMarketingInfoService) {
		this.additionalMarketingInfoService = additionalMarketingInfoService;
	}

	public Map<AdditionalMarketingInfo, Map<String, List<AdditionalMarketingInfoOptions>>> getMarketingInfo() {
		return marketingInfo;
	}

	public void setMarketingInfo(Map<AdditionalMarketingInfo, Map<String, List<AdditionalMarketingInfoOptions>>> marketingInfo) {
		this.marketingInfo = marketingInfo;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public CalendarDate getCopyInstallDate() {
		return copyInstallDate;
	}

	public void setCopyInstallDate(CalendarDate copyInstallDate) {
		this.copyInstallDate = copyInstallDate;
	}

	public boolean isCheckboxInstallDate() {
		return checkboxInstallDate;
	}

	public void setCheckboxInstallDate(boolean checkboxInstallDate) {
		this.checkboxInstallDate = checkboxInstallDate;
	}
	
	public String getAddressBookTypeForOperator() {
		return addressBookTypeForOperator;
	}

	public void setAddressBookTypeForOperator(String addressBookTypeForOperator) {
		this.addressBookTypeForOperator = addressBookTypeForOperator;
	}
	
	public Customer getOperator() {
		return this.operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}
	
	 public String getEquipmentTransferTemplate() {
	        return SUCCESS;
	 }

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setSelectedPolicies(List<RegisteredPolicy> selectedPolicies) {
		this.selectedPolicies = selectedPolicies;
	}

	public List<RegisteredPolicy> getSelectedPolicies() {
		return selectedPolicies;
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

	public List<Market> getListOfMarketTypes() {
		return listOfMarketTypes;
	}

	public void setListOfMarketTypes(List<Market> listOfMarketTypes) {
		this.listOfMarketTypes = listOfMarketTypes;
	}

	public Market getMarketType() {
		return marketType;
	}

	public void setMarketType(Market marketType) {
		this.marketType = marketType;
	}
	
	public String getMarketApplicationForMarketType(){
		listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketType().getId());
		return SUCCESS;
	}

	public List<Market> getListOfMarketApplications() {
		return listOfMarketApplications;
	}

	public void setListOfMarketApplications(List<Market> listOfMarketApplications) {
		this.listOfMarketApplications = listOfMarketApplications;
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
	
	private Set<Organization> childDealers = new HashSet<Organization>();
	public void setChildDealers(Set<Organization> childDealers) {
		this.childDealers = childDealers;
	}

	public Set<Organization> getChildDealers(){
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings){
            if(getLoggedInUsersDealership()!=null && orgService.getChildOrganizationsIds(getLoggedInUsersOrganization().getId())!=null
                    && !orgService.getChildOrganizationsIds(getLoggedInUsersOrganization().getId()).isEmpty()
                    && inventoryItemMapping.getInventoryItem()!=null &&
                    !(inventoryItemMapping.getInventoryItem().getCurrentOwner().getName().equals(getLoggedInUsersDealership().getName()))){
				if (!childDealers.contains(inventoryItemMapping
						.getInventoryItem().getCurrentOwner())) {
					childDealers.add(inventoryItemMapping.getInventoryItem()
							.getCurrentOwner());
				}
			}
		}
		childDealers.add(getLoggedInUsersDealership());
		return childDealers;
	}
	
	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className,
			Warranty warranty) {
		return warrantyService.getLovsForClass(className, warranty);
	}	

	public Address getOperatorAddress() {
		return operatorAddress;
	}

	public void setOperatorAddress(Address operatorAddress) {
		this.operatorAddress = operatorAddress;
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
	
	private boolean manualApprovalRequiredForDemoTruck(InventoryItem inventoryItem) {
		if(isAdditionalInformationDetailsApplicable()
				&& isBuConfigAMER()){
			if(this.marketingInformation != null && this.marketingInformation.getContractCode() != null
					&& this.marketingInformation.getContractCode().getContractCode().equalsIgnoreCase(AdminConstants.DEMO)
					&& inventoryItem.getHoursOnMachine() > 80){
				return true;
			}
		}
		return false;
	}
	
	public boolean noPolicyForDemoTruckWithMoreThan80Hours() {
		return isBuConfigAMER();
	}

    private void filterPolicyDefns(List<PolicyDefinition> policyDefns, List<RegisteredPolicy> transferablePolicies) {
        if(transferablePolicies == null || transferablePolicies.isEmpty()){
            for (Iterator<PolicyDefinition> it = policyDefns.iterator(); it.hasNext();) {
                PolicyDefinition policyDefinition = it.next();
                if (policyDefinition.getTransferDetails().isTransferable()&& 
                    isApplicableCustomerType(policyDefinition)){
                    continue;
                }    
                it.remove();
            }
        }else{
            for (RegisteredPolicy registeredPolicy : transferablePolicies) {
                for (Iterator<PolicyDefinition> it = policyDefns.iterator(); it.hasNext();) {
                    PolicyDefinition policyDefinition = it.next();
                    if (registeredPolicy.getPolicyDefinition().getId().equals(policyDefinition.getId())){
                        it.remove();
                    }else if (!(policyDefinition.getTransferDetails().isTransferable() && 
                        isApplicableCustomerType(policyDefinition))){
                        it.remove();
                    }      
                }
            }
        }
    }
    
    private CalendarDate getCutOffDate(CalendarDate shipmentDate, PolicyDefinition policyDefinition) {
		Integer monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment();
		Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
	}

	public boolean isForDealerSelected() {
		return forDealerSelected;
	}

	public void setForDealerSelected(boolean forDealerSelected) {
		this.forDealerSelected = forDealerSelected;
	}
	
	public boolean displayStockUnitDiscountDetails() {
		return this.configParamService
				.getBooleanValue(ConfigName.ENABLE_STOCK_UNIT_DISCOUNT_DETAILS.getName());
	}
}

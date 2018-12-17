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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.util.ServletContextAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstance;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.DieselTierWaiverService;
import tavant.twms.domain.inventory.EngineTierCtryMapping;
import tavant.twms.domain.inventory.EngineTierCtryMappingService;
import tavant.twms.domain.inventory.I18NWaiverText;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TierTierMapping;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AddressBookAddressMapping;
import tavant.twms.domain.orgmodel.AddressBookService;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.policy.AdditionalMarketingInfo;
import tavant.twms.domain.policy.AdditionalMarketingInfoOptions;
import tavant.twms.domain.policy.AdditionalMarketingInfoService;
import tavant.twms.domain.policy.AdditionalMarketingInfoType;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.CompetitionType;
import tavant.twms.domain.policy.CompetitorMake;
import tavant.twms.domain.policy.CompetitorModel;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.Market;
import tavant.twms.domain.policy.MarketService;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyFees;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.SelectedAdditionalMarketingInfo;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
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
public class WarrantyAction extends MultipleInventoryPickerAction implements
		Preparable, ServletRequestAware, ServletContextAware, TWMSWebConstants {
	private static Logger logger = LogManager.getLogger(WarrantyAction.class);
	private String customerType = CUSTOMER_TYPE_COMPANY;
	private static JSONArray EMPTY_INVENTORY_DETAIL;
	private InventoryTransactionService invTransactionService;
	private ConfigParamService configParamService;	
	private PolicyDefinitionService policyDefinitionService;
	private AdditionalMarketingInfoService additionalMarketingInfoService;
	private Party customer;
	private Customer operator;
	private String type;
	private AddressForTransfer addressForTransfer;
	private AddressForTransfer operatorAddressForTransfer;
	private MarketingInformation marketingInformation;
	private boolean saveAsDraft;
	private int inventoryItemIndex = -1;
	private int inventoryItemMappingIndex = 0;
	private boolean allowInventorySelection;
	
	private CalendarDate copyInstallDate;
	private WarrantyService warrantyService;  
	private List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>(
			5);
	private Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> marketingInfo= new TreeMap<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>>();
	private List<Document> commonAttachments = new ArrayList<Document>();

	private List<RegisteredPolicy> availablePolicies;
	private List<RegisteredPolicy> selectedPolicies;
	private List<RegisteredPolicy> extendedPolicies;	
	private InventoryItem inventoryItem;
	private boolean confirmRegistration;
	private Long inventoryItemId;
    private Market marketType;
	private String deleteModifyReason;
	private boolean checkboxInstallDate;
	private String registrationComments;	
	private static final String CUSTOMER_TYPE_COMPANY = "Company";	
	private LovRepository lovRepository;
	private boolean forDR;
	private Address customerAddress;
	private Address operatorAddress;	
	private WarrantyUtil warrantyUtil;
	private String selectedBusinessUnit;
	private CustomerService customerService;
	private List<ContractCode> listOfContractCodes =new ArrayList<ContractCode>();
	private List<MaintenanceContract> listofMaintenanceContracts=new ArrayList<MaintenanceContract>();
	private List<IndustryCode> listOfIndustryCodes=new ArrayList<IndustryCode>();
	private List<InternalInstallType> listOfInternalInstallTypes = new ArrayList<InternalInstallType>();
	private String pdiName;
	private HttpServletResponse servletResponse;
	private boolean pdiLinksDisplay = true;
	private ServletContext servletContext;
	private String preOrderBooking;
	private String behalfDealer;
	private ServiceProvider forBehalfDealer;
    private InventoryTransaction invTransaction;
    private CampaignService campaignService;
    private List<ListOfValues> additionalComponentTypes;
    private List<ListOfValues> additionalComponentSubTypes;
    private boolean forPrintPDI;
	private boolean pdiGeneration;
	
	private BuSettingsService buSettingsService;
	private Long industryCodeId;
	
	private boolean forETR;
	
	private MSAService msaService;
	
    public boolean isForETR() {
		return forETR;
	}

	public void setForETR(boolean forETR) {
		this.forETR = forETR;
	}

	public Long getIndustryCodeId() {
		return industryCodeId;
	}

	public void setIndustryCodeId(Long industryCodeId) {
		this.industryCodeId = industryCodeId;
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
   

   	public boolean isForPrintPDI() {
		return forPrintPDI;
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

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

    public InventoryTransaction getInvTransaction() {
        return this.invTransaction;
    }

    public void setInvTransaction(InventoryTransaction invTransaction) {
        this.invTransaction = invTransaction;
    }

	
	public String getBehalfDealer() {
		return behalfDealer;
	}

	public void setBehalfDealer(String behalfDealer) {
		this.behalfDealer = behalfDealer;
		forBehalfDealer= orgService.findDealerById(Long.parseLong(behalfDealer));

	}


	//Added for the bug TWMS4.3U-28 as a part of 4.3 upgrade
	private CustomReportService customReportService;
	private EngineTierCtryMappingService engineTierCtryMappingService;
	private DieselTierWaiverService dieselTierWaiverService;

	public void setCustomReportService(CustomReportService customReportService) {
		this.customReportService = customReportService;
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

	public Address getOperatorAddress() {
		return operatorAddress;
	}

	public void setOperatorAddress(Address operatorAddress) {
		this.operatorAddress = operatorAddress;
	}

	private Organization dealerOrganization;
	private AddressBookService addressBookService;

	private UserRepository userRepository;
	private List<User> listOfSalesPersons = new ArrayList<User>();
	private Warranty warranty;

	private ServiceProvider forDealer;
	
	private boolean enterpriseDealer = false;
	
	private DealershipRepository dealershipRepository;

	private HttpServletRequest request;

	private boolean dealerNameSelected = true;

	private Long warrantyTransactionId = null;
	private String jsonString;
	
	private Map<Object, Object> customerTypes = new SortedHashMap<Object, Object>();

	private String addressBookType;
	private String addressBookTypeForOperator;
	private InventoryService inventoryService;
	
	private Long actualInventoryForDR;

	public Long getActualInventoryForDR() {
		return actualInventoryForDR;
	}

	public void setActualInventoryForDR(Long actualInventoryForDR) {
		this.actualInventoryForDR = actualInventoryForDR;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	private String serialNumber;
	public String getAddressBookTypeForOperator() {
		return addressBookTypeForOperator;
	}

	public void setAddressBookTypeForOperator(String addressBookTypeForOperator) {
		this.addressBookTypeForOperator = addressBookTypeForOperator;
	}

	private boolean isModifyDRorETR = false;
	private WarrantyCoverageRequestService warrantyCoverageRequestService;
	private boolean reductionInCoverage;
	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
	private boolean duplicateSerialNumber = false;
    private WarrantyTaskInstance warrantyTaskInstance;
    public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	private CatalogService catalogService;
	private ServiceProvider installingDealer;


	static {
		 	EMPTY_INVENTORY_DETAIL = new JSONArray();
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("-");
		 	EMPTY_INVENTORY_DETAIL.put("");
			
		}

    @Override
	protected void addSearchConstraints() {
		super.addSearchConstraints();
		getInventorySearchCriteria().setWarrantyCheck(true);
		getInventorySearchCriteria().setInventoryType(InventoryType.STOCK);
		getInventorySearchCriteria().setConditionTypeNot(
				InventoryItemCondition.SCRAP);
		if (getForDealer() == null) {
            if (getInventorySearchCriteria().getDealerId() != null
                    && getInventorySearchCriteria().getDealerId() > 0) {
                forDealer = dealershipRepository
                        .findByDealerId(getInventorySearchCriteria()
                            .getDealerId());
            } else if (StringUtils.hasText(getInventorySearchCriteria().getDealerName())) {
                forDealer = dealershipRepository
                        .findByDealerName(getInventorySearchCriteria()
                                .getDealerName());
            } else if (StringUtils.hasText(getInventorySearchCriteria().getDealerNumber())) {
                forDealer = dealershipRepository
                        .findByDealerNumber(getInventorySearchCriteria()
                                .getDealerNumber());
            }
		}
		getInventorySearchCriteria().setDealerId(forDealer.getId());
    }

	@Override
	public String searchInventories() throws IOException {
		super.searchInventories();
		return SUCCESS;
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
	public boolean isSendInventoryId() {
		return true;
	}
	
	public boolean isInstallingDealerEnabled() {
		return this.configParamService
		.getBooleanValue(ConfigName.ENABLE_DEALER_AND_INSTALLATION_DATE
				.getName());
	}
     
	public boolean isCustomerDetailsNeededForDR_Rental(){
		return this.configParamService
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
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
	
	public void prepare() {		
		if (!isLoggedInUserAnInternalUser()) {
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
		if (listIfPreviousOwner.isEmpty()) {
			listIfPreviousOwner.put(SWITCHING,getText("label.wntyreg.prevowner.switch"));
			listIfPreviousOwner.put(CONTINUING,getText("label.wntyreg.prevowner.continue"));
			listIfPreviousOwner.put(UNKNOWN_NOT_PROVIDED,getText("label.wntyreg.prevowner.Unknown/NotProvided"));
		}      
        if (this.inventoryItem != null) {
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
        if(!StringUtils.hasText(SelectedBusinessUnitsHolder.getSelectedBusinessUnit())
                && inventoryItem!=null){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem.getBusinessUnitInfo().getName());
        }
		listOfTransactionTypes = this.warrantyService.listTransactionTypes();
		listOfMarketTypes =this.marketService.listMarketTypes();
		if(this.getMarketingInformation()!=null && this.getMarketingInformation().getMarket()!=null)
	    listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketingInformation().getMarket().getId());
		
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
        
       // listOfSalesPersons = this.warrantyUtil.getSalesPersonsForDealer(this.inventoryItem, this.forDealer);
        listOfContractCodes = this.warrantyService.listContractCode();
        listOfInternalInstallTypes = this.warrantyService.listInternalInstallType();
        listofMaintenanceContracts=this.warrantyService.listMaintenanceContract();
        listOfIndustryCodes=this.warrantyService.listIndustryCode();
        
        Comparator<IndustryCode> icc = new Comparator<IndustryCode>() {
			@Override
			public int compare(IndustryCode o1, IndustryCode o2) {
				return o1.getDisplayIndustryCode().compareTo(o2.getDisplayIndustryCode());
			}
		};
        
        Collections.sort(listOfIndustryCodes, icc);        		                
        populateCustomerTypes();
        enterpriseDealer = isLoggedInUserAnEnterpriseDealer();
        
        if(inventoryItem!=null){
        	setActualInventoryForDR(inventoryItem.getId());
        }
        
        additionalComponentTypes = this.lovRepository.findAllActive("AdditionalComponentType");
		additionalComponentSubTypes = this.lovRepository.findAllActive("AdditionalComponentSubType");
		
		if (inventoryItemMappings != null) {
			StringBuffer serialNumbers = new StringBuffer();
			String prefix="";
		    int count=0;
		    boolean flag=false;
			for (MultipleInventoryAttributesMapper multipleInventoryAttributesMapper : inventoryItemMappings) {
				 
				if (multipleInventoryAttributesMapper!=null && multipleInventoryAttributesMapper.getInventoryItem()
						.getPreOrderBooking() == Boolean.TRUE
						&& !(multipleInventoryAttributesMapper.getInventoryItem()
								.getId().equals(actualInventoryForDR))) {
				    serialNumbers.append(prefix);
				    prefix=",";
					serialNumbers.append(multipleInventoryAttributesMapper.getInventoryItem().getSerialNumber());
					count++;
					if(count>=2){
						flag=true;
					    break;
					}
				}
				
		      }
			if(flag)
				addActionError("error.multipleInventoryDR.preOrderBooking",serialNumbers);
			}	
		}
	
	public String getDieselTierDescription(InventoryItem inventoryItem,Party customer,boolean editable) {
		if(!editable && inventoryItem.getWaiverDuringDr() != null)
			return inventoryItem.getWaiverDuringDr().getI18NDisclaimer();
		else if(editable) {
			Country customerCountry = engineTierCtryMappingService.findCountryByName(customer.getAddress().getCountry());
			EngineTierCtryMapping engineTierCtryMapping = engineTierCtryMappingService.findDieselTierByCountry(customerCountry);
			TierTierMapping tierTierMapping = null;
			if(engineTierCtryMapping != null){
				tierTierMapping = engineTierCtryMappingService.findTierTierMappingByInventoryTierAndCustomerTier(inventoryItem.getDieselTier(), engineTierCtryMapping);
			}
			return tierTierMapping.getI18NWaiverText();
		}
		return null;
	}
	
	public String getDieselTierDescription(InventoryItem inventoryItem, Party customer){
		if(inventoryItem.getIsDisclaimer() && !inventoryItem.getPreOrderBooking()){
			return inventoryItem.getWaiverDuringDr().getI18NDisclaimer();
		}
		if(inventoryItem.getPreOrderBooking() && inventoryItem.getIsDisclaimer() && isCustomerExists()){
				if(customer.equals(inventoryItem.getLatestBuyer())){
					return inventoryItem.getWaiverDuringDr().getI18NDisclaimer();
				}else{
					Country customerCountry = engineTierCtryMappingService.findCountryByName(customer.getAddress().getCountry());
					EngineTierCtryMapping engineTierCtryMapping = engineTierCtryMappingService.findDieselTierByCountry(customerCountry);
					TierTierMapping tierTierMapping = null;
					if(engineTierCtryMapping != null){
						tierTierMapping = engineTierCtryMappingService.findTierTierMappingByInventoryTierAndCustomerTier(inventoryItem.getDieselTier(), engineTierCtryMapping);
					}
					return tierTierMapping.getI18NWaiverText();
				}
		}
		else{
			Country customerCountry = engineTierCtryMappingService.findCountryByName(customer.getAddress().getCountry());
			EngineTierCtryMapping engineTierCtryMapping = engineTierCtryMappingService.findDieselTierByCountry(customerCountry);
			TierTierMapping tierTierMapping = null;
			if(engineTierCtryMapping != null){
				tierTierMapping = engineTierCtryMappingService.findTierTierMappingByInventoryTierAndCustomerTier(inventoryItem.getDieselTier(), engineTierCtryMapping);
			}
			return tierTierMapping.getI18NWaiverText();
		}
	}
	
	public String getDisclaimer(TierTierMapping tiertierMapping){
		if(tiertierMapping != null && tiertierMapping.getI18NWaiverTexts() != null){
			for(I18NWaiverText waiverText : tiertierMapping.getI18NWaiverTexts()){
				if(waiverText.getLocale().toString().equalsIgnoreCase(warrantyUtil.getLoggedInUser().getLocale().toString())){
					return waiverText.getDescription();
				}
			}
		}
		return null;
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

	public InventoryItem performD2D(InventoryItem inventoryItem) {

		InventoryTransaction newTransaction = new InventoryTransaction();
		newTransaction.setTransactionOrder(new Long(inventoryItem.getTransactionHistory().size() + 1));
		Collections.sort(inventoryItem.getTransactionHistory());
		newTransaction.setTransactedItem(inventoryItem);
		inventoryItem.getTransactionHistory().get(0).setStatus(BaseDomain.INACTIVE);
		newTransaction.setSeller(inventoryItem.getTransactionHistory().get(0).getBuyer());
		newTransaction.setOwnerShip(getLoggedInUser().getBelongsToOrganization());
		newTransaction.setBuyer(getLoggedInUser().getBelongsToOrganization());
		newTransaction.setInvTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.DEALER_TO_DEALER.getTransactionType()));
		newTransaction.setTransactionDate(Clock.today());
		newTransaction.setStatus(BaseDomain.ACTIVE);
		inventoryItem.getTransactionHistory().add(newTransaction);
		inventoryItem.setLatestBuyer(getLoggedInUser().getBelongsToOrganization());
		inventoryItem.setCurrentOwner(getLoggedInUser().getBelongsToOrganization());
		return inventoryItem;
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
	
	private boolean validateDeliveryAndInstallationDate(MultipleInventoryAttributesMapper inventoryItemMapper) {
		
        CalendarDate deliveryDate =  inventoryItemMapper.getWarrantyDeliveryDate();
        InventoryItem inventoryItem =  inventoryItemMapper.getInventoryItem();
        CalendarDate installationDate = inventoryItemMapper.getInstallationDate();
        
        if (isBuConfigAMER() && (inventoryItem.getLatestWarranty()==null || inventoryItem.getLatestWarranty() != null
				&& (inventoryItem.getLatestWarranty().getPdiGenerated() == null || !inventoryItem
						.getLatestWarranty().getPdiGenerated()))) {
			addActionError("dealerAPI.warrantyRegistration.pdiNotGenerated",
					new String[] { inventoryItem.getSerialNumber() });

			return false;
		}       
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
		
	

	private void validateMarketingInfo() {
		if (this.marketingInformation != null){
			if (!getLoggedInUser().isInternalUser() || this.marketingInformation.getInternalInstallType() == null){
				validateContractCode();
			}
            validateIndustryCode();
            validateMaintenanceContract();
            validateSalesPerson();
            validateCustomerRepresentative();
        }else{
			addActionError("error.warranty.marketInfo");
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
	
	
	  private void validateMarketDetails(){
          if(getMarketingInformation().getMarket()==null){
                  addActionError("error.marketInfo.marketType");
          }
      if(getMarketingInformation().getApplication()==null)
      addActionError("error.marketInfo.application");
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
			}else if(!listOfTransactionTypes.contains(getMarketingInformation().getTransactionType())){
				addActionError("error.marketInfo.transactionType");
			}
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
    public String preview() {

		return SUCCESS;
	}

	public String detail() {
		return SUCCESS;
	}

	public String showAttachments() {
		return SUCCESS;
	}

	public String create() {
		this.inventoryItemMappings.removeAll(Collections.singletonList(null));
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
		if (!this.inventoryItemMappings.isEmpty()) {
			Iterator<MultipleInventoryAttributesMapper> iterator = this.inventoryItemMappings.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().inventoryItem.getId() == null) {
					iterator.remove();
				}
			}
		}
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		String noneORSuccess = this.warrantyUtil.checkForInventoryAlreadyRetailedAndPendingWarranty(this.inventoryItem,
				errorCodeMap);
		setErrorCodesMapToActionErrors(errorCodeMap);

//		pdiLinksDisplay = isPDIlinkDisplayEnabled();
		try{
			printPdi();
		}catch(Exception e){
			logger.error("Item is Null", e);
		}
		return noneORSuccess;
	}


	public String createFromQuickSearch() {
		if (this.inventoryItem != null && this.inventoryItem.isRetailed()) {
			addActionError("error.inventory.drfiled", this.inventoryItem
					.getSerialNumber());
			return NONE;
		}
		if (getInventoryItem() != null
				&& getInventoryItem().getPendingWarranty()) {
			if (getInventoryItem().getLatestWarranty().getStatus().getStatus()
					.equals(WarrantyStatus.DRAFT.getStatus())) {
				addActionError("error.inventory.drsavedasdraft",
						this.inventoryItem.getSerialNumber());
			} else {
				addActionError("error.inventory.drfiled",
						this.inventoryItem.getSerialNumber());
			}
			return NONE;
		}
		if (this.inventoryItem != null) {			
			prepareInventoryItem(this.inventoryItem);			
		}
		return SUCCESS;
	}

	private void prepareInventoryItem(InventoryItem inventoryItem) {
		if(!isDuplicateSerialNumber()) {
			MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
			mapper.setInventoryItem(inventoryItem);	
			if(isMarketInfoApplicable())
			{	
			 List<AdditionalMarketingInfo> additionalMarketingInfo = this.getAdditionalMarketingInfoService()
					.getAdditionalMarketingInfoByAppProduct(inventoryItem.getOfType().getProduct());
              Collections.sort(additionalMarketingInfo);
			for(AdditionalMarketingInfo marketInfo : additionalMarketingInfo)
			{
				Map<String,List<AdditionalMarketingInfoOptions>> fieldDetailsForMarketingInfo = new TreeMap<String,List<AdditionalMarketingInfoOptions>>();
				fieldDetailsForMarketingInfo.put(marketInfo.getInfoType().toString(), marketInfo.getOptions());
			    
               marketingInfo.put(marketInfo, fieldDetailsForMarketingInfo);
				mapper.setMarketingInfo(marketingInfo);
				}
			}
			
            mapper.setWarrantyDeliveryDate(inventoryItem.getDeliveryDate());
            Warranty warranty = inventoryItem.getDraftWarranty();
			if (warranty != null) {
				for (RegisteredPolicy registeredPolicy : warranty.getPolicies()) {
					mapper.getSelectedPolicies().add(registeredPolicy);
				}
			}
			this.setSelectedPolicies(mapper.getSelectedPolicies());
			prepareApplicablePolicies(mapper);
			
			if(inventoryItem.getPreOrderBooking()==Boolean.TRUE && isCustomerExists()){
	        	addressBookType=AdminConstants.END_CUSTOMER;
	        	this.setCustomer(inventoryItem.getLatestBuyer());
	        	this.setCustomerAddress(inventoryItem.getLatestBuyer().getAddress());
	        	showCustomer();
	        	this.setPreOrderBooking("true");
	        	mapper.setDieselTierWaiver(inventoryItem.getWaiverDuringDr());
	        	if(inventoryItem.getWaiverDuringDr() != null)
	        	mapper.getDieselTierWaiver().setDisclaimer(
	        			inventoryItem.getWaiverDuringDr().getI18NDisclaimer());
	        	addActionWarning("warning.preOrderBooking");
	        }
			this.inventoryItemMappings.add(mapper);
		}
	}

	private boolean isCustomerExists() {
		if(customer!=null && InstanceOfUtil.isInstanceOfClass(Customer.class, customer)){
			return true;
		}
	return false;
	}
	

	@SuppressWarnings("unchecked")
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

	private Integer getMaxSequenceNumber(InventoryItem inventoryItem){
		int maxSequenceNumber = -1;
		for(InventoryItemComposition composition : inventoryItem.getComposedOf()){
			if(composition.getSequenceNumber() != null && Integer.parseInt(composition.getSequenceNumber()) > maxSequenceNumber){
				maxSequenceNumber = Integer.parseInt(composition.getSequenceNumber());
			}
		}
		return maxSequenceNumber;
	}
	
	private void validateAddressBookType(){
		if(!StringUtils.hasText(this.addressBookType) || customerTypes.get(this.addressBookType) == null){
			addActionError("error.customerTypeNotSelected");
		}
	}
	
	private boolean isDeliveryReportSubmitted(InventoryItem inventoryItem) {
		if (inventoryItem.getWarranty() != null) {
			return true;
		}
		return false;
	}
	
	public String confirmRegistration() {
 		this.inventoryItemMappings.removeAll(Collections.singletonList(null));
 		if(null!=customer){
 			operator=customerService.findCustomerById(customer.getId());
 		} 		
		if ((isAdditionalInformationDetailsApplicable() || getMarketingInformation()!=null) && (isCustomerDetailsNeededForDR_Rental()?true:!this.addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)) /*&& !this.addressBookType.equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)*/) {
				validateMarketingInfo();
		}
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
				InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
				
				if (isDeliveryReportSubmitted(inventoryItem)) {
					addActionError("error.inventory.drfiled", inventoryItem.getSerialNumber());
				}
				if(allowInventorySelection) { // allowInventorySelection=true  (if request comes from other than draft inbox warranty submission)
					this.warrantyUtil.checkForInventoryAlreadyRetailedAndPendingWarranty(inventoryItem, errorCodeMap);				
					if (!errorCodeMap.isEmpty()) {
						setErrorCodesMapToActionErrors(errorCodeMap);
						return INPUT;
					}
				}
				this.warrantyUtil.validateStolenInventory(inventoryItemMapping.getWarrantyDeliveryDate(), inventoryItemMapping.getInstallationDate(), inventoryItem, errorCodeMap);
				if(!errorCodeMap.isEmpty()){
					setErrorCodesMapToActionErrors(errorCodeMap);
					return INPUT;
				}
				this.warrantyUtil.validateMajorComponents(inventoryItem, errorCodeMap);
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
				// We can't inline this in the next if check, since it won't get
				// called if customer is not set.
                if(!inventories.contains(inventoryItem)) {	    			
                	inventories.add(inventoryItem);
    			} else {
    				addActionError("error.common.duplicateUnit");
    			}
                if(warranty.getDiscountType() != null && warranty.getDiscountNumber().isEmpty()){
                	addActionError("error.common.discountNumberIsRequired");
                }
                validateDocumentType(inventoryItem, inventoryItemMapping.getAttachments());
        		validateMandatoryAttachments(inventoryItem, inventoryItemMapping.getAttachments());
                
                validateAddressBookType();
				boolean inventoryItemIsValid = validateInventoryItemMapping(inventoryItemMapping);
				Collections.sort(inventoryItemMapping.getSelectedPolicies());
				if (isCustomerSet && inventoryItemIsValid) {
					if(isDisclaimerAvailable(inventoryItemMapping) && !inventoryItemMapping.isDisclaimerAccepted()){
	                	addActionError("error.disclaimer.notAccepted");
	                }
					if(inventoryItemMapping.isWaiverInformationEditable()
								&& inventoryItemMapping.isDisclaimerAccepted())
						validateWaiverInformation(inventoryItemMapping.getDieselTierWaiver());
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
		if (hasErrors()) {
			return INPUT;
		}
	
		this.confirmRegistration = true;   
		return SUCCESS;
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
		List<RegisteredPolicy> selectedPolicies = mapper.getSelectedPolicies();
		// Note: the single "&" is not a typo! We cant use && since we want
		// *both* validations to happen,
		// irrespective of each other.
		return validateDeliveryAndInstallationDate(mapper) & validateInventoryItem(inventoryItem)
				& validateSelectedPolicies(selectedPolicies, inventoryItem);
		
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
	
	private boolean isDemoTruckMoreThan80Hours(InventoryItem inventoryItem) {
		if (isAdditionalInformationDetailsApplicable()
				&& marketingInformation.getContractCode() != null
				&& marketingInformation.getContractCode().getContractCode().equalsIgnoreCase(AdminConstants.DEMO)
				&& inventoryItem.getHoursOnMachine() > 80){
			return true;
		}
		return false;
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
		if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(!documentAttached || !isDocumentAttachedOfType(attachments, AdminConstants.ITDR)) {
				addActionError("error.emeaDeliveryReport.itdr.mandatory", inventoryItem.getSerialNumber());
				documentAttached = false;
			}
		} else if (inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
			boolean pdiAttached = true;
			boolean authorizationAttached = true;
			if (!documentAttached || !isDocumentAttachedOfType(attachments, AdminConstants.PDI)) {
				pdiAttached = false;
			}
			if (isDemoTruckMoreThan80Hours(inventoryItem)
					&& (!documentAttached || !isDocumentAttachedOfType(
							attachments, AdminConstants.AUTHORIZATION))) {
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
		}
		return documentAttached;
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
	
	private boolean isPolicyMandatory(){
		return configParamService.getBooleanValue(ConfigName.POLICY_MANDATORY.getName());
	}
	
	private boolean validateSelectedPolicies(
			List<RegisteredPolicy> selectedPolicies, InventoryItem inventoryItem) {
		if(!isPolicyMandatory()){
			return true;
		}
		if (selectedPolicies.isEmpty()) { //SLMSPROD-1174, demo should work in the same way as that of End customer
			if(isPolicyApplicable(inventoryItem)){
				addActionError("error.noPolicySelected", inventoryItem
						.getSerialNumber());
				return false;
			}
		}
		return true;
	}
	
	private boolean validateDealerRentalCustomer(){
		if(getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL) && !isCustomerDetailsNeededForDR_Rental()){
			return true;
		}
		return false;
	}
	private boolean validateCustomer() {
        if (getCustomer() == null && !validateDealerRentalCustomer())
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
	
	private Warranty setWarrantyAttributes(Warranty warranty,
			MultipleInventoryAttributesMapper inventoryItemMapping) {
		warranty.getAttachments().clear();
		if (getCommonAttachments() != null)
			warranty.getAttachments().addAll(getCommonAttachments());
		if (inventoryItemMapping.getAttachments() != null)
			warranty.getAttachments().addAll(inventoryItemMapping.getAttachments());
		if(this.warranty.getDiscountType() != null)
			warranty.setDiscountType(this.warranty.getDiscountType());
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
		warrantyAudit.setSelectedPolicies(createSelectedPolicies(inventoryItemMapping));
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
		if(AdminConstants.CUSTOMER_TYPE_DEMO.equals(this.getAddressBookType())){
			warranty.setTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.DEMO.name()));
		} else if(AdminConstants.DEALER_RENTAL.equalsIgnoreCase(this.getAddressBookType()))
		{
			warranty.setTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.DR_RENTAL.name()));
		}
		
		else{
			warranty.setTransactionType(invTransactionService.getTransactionTypeByName(InvTransationType.DR.name()));
		}
		if (isLoggedInUserADealer()) {
			if(!inventoryItemMapping.getInventoryItem().getCurrentOwner().equals(getOrganization()) && allowWROnOthersStock() && !inventoryItemMapping.getInventoryItem().getTransactionHistory().get(inventoryItemMapping.getInventoryItem().getTransactionHistory().size()-1)
					.getInvTransactionType().getTrnxTypeValue().
					equals(InvTransationType.DEALER_TO_DEALER.getTransactionType())){
				performD2D(inventoryItemMapping.getInventoryItem());
			}
			if(isLoggedInUserAParentDealer()){ //TODO : Need to check if d2d wont happen twice (above and below) 
				if(inventoryItemMapping != null && inventoryItemMapping.getInventoryItem() != null && forBehalfDealer!=null && !forBehalfDealer.getId().equals(inventoryItemMapping.getInventoryItem().getCurrentOwner().getId())){
					performD2D(inventoryItemMapping.getInventoryItem());
	            }
				if(behalfDealer!=null && !behalfDealer.isEmpty()){
					warranty.setForDealer(forBehalfDealer);
				}
				else{
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
		if(this.getInstallingDealer()!=null && this.getInstallingDealer().getId()!=null)
		warranty.setInstallingDealer(this.getInstallingDealer());	
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
	
	private void createWarranty(MultipleInventoryAttributesMapper inventoryItemMapping,InventoryItem invItem) {
		String multiDRETRNumber = null;
		if (!inventoryItemMappings.get(0).getInventoryItem()
				.getPendingWarranty()) {
			multiDRETRNumber = warrantyService.getWarrantyMultiDRETRNumber();
		}	
		Warranty warranty = invItem.getDraftWarranty() == null && this.warranty != null
				&& this.warranty.getId() != null ? this.warranty : invItem.getDraftWarranty();
		if(isCustomerDetailsNeededForDR_Rental()?false:getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL) /*|| getAddressBookType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)*/){
            //this.addressForTransfer = warrantyUtil.populateAddressForTransfer(forDealer.getAddress());
			this.setCustomer(forDealer);
        	this.setCustomerAddress(forDealer.getAddress());
        	showCustomer();
		}
		if (invItem.getDraftWarranty() == null) {
			warranty = new Warranty();
			warranty.setDraft(this.saveAsDraft);
			warranty.setInventoryItem(invItem);
			if(this.warranty != null
					&& this.warranty.getId() != null)
			multiDRETRNumber = this.warranty.getMultiDRETRNumber();	
		} else {			
			multiDRETRNumber = warranty.getMultiDRETRNumber();
		}
		warranty = setWarrantyAttributes(warranty, inventoryItemMapping);
		warranty.setFiledDate(Clock.today());
		warranty.setMultiDRETRNumber(multiDRETRNumber);
		if (!this.saveAsDraft) {
			warranty.setMultiDRETRNumber(multiDRETRNumber);
			if(isManualApprovalRequiredForDR() && (isCustomerDetailsNeededForDR_Rental()?true:!addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)))
				warranty.setDraft(true);
			else
				warranty.setDraft(false);
			
			if(!warranty.isDraft() && manualApprovalRequiredForDemoTruck(warranty.getForItem())){
				warranty.setDraft(true);
			}
			if(!warranty.isDraft() && (inventoryItemMapping.getSelectedPolicies() == null
					|| inventoryItemMapping.getSelectedPolicies().isEmpty())){
				warranty.setDraft(true);
			}
			
			warranty.setStatus(WarrantyStatus.SUBMITTED);
		} else {
			invItem.setPendingWarranty(true);
			warranty.setDraft(true);
			warranty.setStatus(WarrantyStatus.DRAFT);
			try {
				warrantyService.createPoliciesForWarranty(warranty);
			} catch (PolicyException ex) { // FIXME: Narrow this exception
				// down
				logger.error("Error saving warranty for " + invItem, ex);
				addActionError("error.registeringWarrantyForItem");			
			}
		}
		if(checkWRCount())
		{
			warranty.setManualFlagDr(true);
		}
		else{
			warranty.setManualFlagDr(false);
		}
		if(isForPrintPDI()){
			warranty.setPdiGenerated(Boolean.TRUE);
		}
	}
	
	private void validateSerialNumbers() {
		StringBuffer invalidSerialNumbers = new StringBuffer();
		for (Iterator<MultipleInventoryAttributesMapper> mapperIterator = this.inventoryItemMappings.iterator(); mapperIterator
				.hasNext();) {
			MultipleInventoryAttributesMapper mapper = mapperIterator.next();
			if (!mapper.getInventoryItem().getPendingWarranty() && mapper.getInventoryItem().isRetailed()) {
				if (invalidSerialNumbers.length() > 0)
					invalidSerialNumbers.append(",");
				invalidSerialNumbers.append(mapper.getInventoryItem().getSerialNumber());
				mapperIterator.remove();
			}
		}
		if (invalidSerialNumbers.length() > 0)
			addActionError("error.inventory.drfiled", invalidSerialNumbers.toString());
	}
	
	public String register() throws ItemNotFoundException {
		for(MultipleInventoryAttributesMapper mapper : inventoryItemMappings) {
			if (isDeliveryReportSubmitted(mapper.getInventoryItem())) {
				addActionError("error.inventory.drfiled", mapper.getInventoryItem().getSerialNumber());
			}
		}
		if (hasErrors()) {
			 return INPUT;
		}
		if((isCustomerDetailsNeededForDR_Rental()?false:getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL))/* || getAddressBookType().equalsIgnoreCase(AdminConstants.CUSTOMER_TYPE_DEMO)*/){
			this.marketingInformation = new MarketingInformation();
			if(!isCustomerDetailsNeededForDR_Rental() && this.addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)){
				this.marketingInformation.setTransactionType(this.warrantyService.findTransactionType(AdminConstants.TRANSACTION_TYPE_DEALER_RENTAL));
				this.marketingInformation.setMarket(this.marketService.findMarketTypeByTitle(AdminConstants.RENTAL));
                if(this.marketingInformation.getMarket() != null){
				    this.marketingInformation.setApplication(this.marketService.findMarketApplicationByTitle(this.marketingInformation.getMarket().getId(), AdminConstants.RENTAL));
                }
			}else if(this.addressBookType.equalsIgnoreCase(AdminConstants.DEMO)){ //TODO: need to re factor as control won't come here
				this.marketingInformation.setTransactionType(this.warrantyService.findTransactionType(AdminConstants.DEMO));
				this.marketingInformation.setMarket(this.marketService.findMarketTypeByTitle(AdminConstants.DEMO));
                if(this.marketingInformation.getMarket() != null){
				    this.marketingInformation.setApplication(this.marketService.findMarketApplicationByTitle(this.marketingInformation.getMarket().getId(), AdminConstants.DEMO));
                }
			}
			this.getMarketingInformation().setCustomerFirstTimeOwner(Boolean.TRUE);
		}
		
		boolean isManualApproval;
		this.forDR = true;
		StringBuffer serialNumbers = new StringBuffer();
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
		if(checkWRCount())
		{
			isManualApproval = true;
		}
		else if(isCustomerDetailsNeededForDR_Rental()?false:this.addressBookType.equalsIgnoreCase(AdminConstants.DEALER_RENTAL)){
			 isManualApproval = false;
		}
		else{
		 isManualApproval = isManualApprovalRequiredForDR();
		}
		
		if(/*getAddressBookType().equals(AdminConstants.CUSTOMER_TYPE_DEMO) || */(isCustomerDetailsNeededForDR_Rental()?false:getAddressBookType().equalsIgnoreCase(AdminConstants.DEALER_RENTAL))){
			isManualApproval = false;
		}
		validateSerialNumbers();
       	for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
       		
            inventoryItemMapping.getInventoryItem().setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());    
			removeUnselectedPolicies(inventoryItemMapping);
			InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();			
			if(!isPolicyApplicable(inventoryItem)){
				isManualApproval = true;
			}
			
			if(!isManualApproval && manualApprovalRequiredForDemoTruck(inventoryItem)){
				isManualApproval = true;
			}
			
			if(!isManualApproval && (inventoryItemMapping.getSelectedPolicies() == null
					|| inventoryItemMapping.getSelectedPolicies().isEmpty())){
				isManualApproval = true;
			}
			
			if (!isSaveAsDraft() && !isManualApproval) {
				inventoryItem = setInventoryItemAttributes(inventoryItem, inventoryItemMapping);
			}
			createWarranty(inventoryItemMapping,inventoryItem);           
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
			
			// If the warranty is submitted then only warrantyTaskInstance is
			// created
			if (!this.saveAsDraft) {
				
	               List<Long> parentDealers = orgService.getParentOrganizationsIds(getLoggedInUsersOrganization().getId());
	               /* for(Organization org : inventoryItem.getCurrentOwner().getParentOrgs()){
	                    parentDealers.add(org.getId());
	                }*/
				if (!inventoryItem.getCurrentOwner().equals(getOrganization())
						&& !parentDealers.contains(getOrganization().getId())
						&& forBehalfDealer != null
						&& !forBehalfDealer.getId().equals(
								inventoryItemMapping.getInventoryItem()
										.getCurrentOwner().getId())
						&& !inventoryItem.getTransactionHistory().get(0)
								.getInvTransactionType()
								.equals(InvTransationType.DEALER_TO_DEALER))
	             inventoryItem = this.warrantyUtil.performD2D(inventoryItem, getLoggedInUser().getBelongsToOrganization(), false);
	        itemsForTask.add(inventoryItem);
			}
			serialNumbers.append(inventoryItem.getSerialNumber());
			serialNumbers.append(", ");

            DocumentTransportUtils.markDocumentsAsAttached(warranty.getAttachments());
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
			addActionMessage("message.warrantyRegisteredForItems",
					serialNumbers);
		} 		
		//updating sic code of a customer based on selected industry code
		if(null != marketingInformation && null != marketingInformation.getIndustryCode()){
			operator=customerService.findCustomerById(customer.getId());
			operator.setSiCode(marketingInformation.getIndustryCode().getIndustryCode());
			customerService.updateCustomer(operator);
		}				
     if(!isManualApproval){
		if (doesRequestHaveInventoriesWithReducedCoverage()) {
			addActionMessage("message.reduced.coverage.info.messsage");
			addActionMessage("message.reduced.coverage.screen.info");
			reductionInCoverage = true;
		}
     }
		if (hasErrors()) {
			return INPUT;
		} else {
			return SUCCESS;
		}
	}
	
	public boolean isPdiReportConfigured(){
		
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
			if(customReportService.isPdiReportLinkAvailable(inventoryItemMapping.inventoryItem)){
				return true;
			}
		}
		return false;
	}
	
	public String getDetailsForInventory() throws JSONException {
		JSONArray details=new JSONArray();
		try {
			
			InventoryItem inventoryItem = this.inventoryService.findInventoryBySerialNumberAndType(serialNumber,new InventoryType(type));
			if(inventoryItem!=null)
			{
				String product = inventoryItem.getOfType().getProduct().getName();
				String model = inventoryItem.getOfType().getModel().getName();
				String sipmentDate = inventoryItem.getShipmentDate()!=null?inventoryItem.getShipmentDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser())+"":"";
				String buildOn = inventoryItem.getBuiltOn()!=null?inventoryItem.getBuiltOn().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser())+"":"";
				String id = inventoryItem.getId()+"";
				Integer hoursOnMachine=0;
				String oem=inventoryItem.getBrandType();
				details.put(product);
				details.put(model);
				details.put(sipmentDate);
				details.put(buildOn);
				details.put(id);
				details.put(hoursOnMachine);
				details.put(oem);
			}
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			details = EMPTY_INVENTORY_DETAIL;
		}
		jsonString = details.toString();
		return SUCCESS;
	}
	
	
	public String getMarketApplicationForMarketType() {
		listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketType().getId());
		return SUCCESS;
	}
	
	public String getMarketingInformationForInventory() throws ItemNotFoundException {
		if(isMarketInfoApplicable() && StringUtils.hasText(serialNumber)){
            InventoryItem invItem = this.inventoryService.findInventoryItemBySerialNumber(serialNumber);		
            List<AdditionalMarketingInfo> additionalMarketingInfo = this.getAdditionalMarketingInfoService()
				.getAdditionalMarketingInfoByAppProduct(invItem.getOfType().getProduct());
            for(AdditionalMarketingInfo marketInfo : additionalMarketingInfo){
                Map<String,List<AdditionalMarketingInfoOptions>> fieldDetailsForMarketingInfo = new HashMap<String,List<AdditionalMarketingInfoOptions>>();
                fieldDetailsForMarketingInfo.put(marketInfo.getInfoType().toString(), marketInfo.getOptions());
                marketingInfo.put(marketInfo, fieldDetailsForMarketingInfo);
            }
		}
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

        		if(this.isInstallingDealerEnabled()) {
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
        }
		return SUCCESS;
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
	
	private boolean isPolicyApplicable(InventoryItem inventoryItem){
		return !((!getLoggedInUser().isInternalUser() 
    			|| (getLoggedInUser().isInternalUser() 
    					&& inventoryItem.getLatestWarranty()!=null && !inventoryItem.getLatestWarranty().getStatus().getStatus().equals("Submitted")))
    					&& isDemoInventory(inventoryItem) && inventoryItem.getHoursOnMachine().intValue() > 80);
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
	
	public String getMajorComponents(){		
		MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
		mapper.setInventoryItem(this.inventoryItem);
				inventoryItemMappings.add(inventoryItemMappings.size(), mapper);
		return SUCCESS;
	}

	public String showCustomer() {
		addressForTransfer = new AddressForTransfer();
		if(isCustomerExists()){
			operator=customerService.findCustomerById(customer.getId());
			addressForTransfer = setAddressForTransferDetails(addressForTransfer,customer,customerAddress);			
			industryCodeId = this.warrantyService.getIndustryCode(operator.getSiCode());
		}
		if(null==industryCodeId){
			industryCodeId=0l;
		}		
		return SUCCESS;
	}
	
	public String showOperator() {
		operatorAddressForTransfer = new AddressForTransfer();
		operatorAddressForTransfer = setAddressForTransferDetails(operatorAddressForTransfer,operator,operatorAddress);		
		return SUCCESS;
	}

	private AddressForTransfer setAddressForTransferDetails(AddressForTransfer addressForTransfer,Party customer,Address address)
	{
		addressForTransfer.setAddressLine(address.getAddressLine1());
		addressForTransfer.setAddressLine2(address.getAddressLine2());
		addressForTransfer.setAddressLine3(address.getAddressLine3());
		addressForTransfer.setCity(address.getCity());
		addressForTransfer.setState(address.getState());
		addressForTransfer.setCountry(address.getCountry());
		addressForTransfer.setZipCode(address.getZipCode());
		addressForTransfer.setPhone(address.getPhone());
		addressForTransfer.setEmail(address.getEmail());
		addressForTransfer.setSecondaryPhone(address
				.getSecondaryPhone());
		addressForTransfer.setFax(address
				.getFax());
		addressForTransfer.setCounty(address.getCounty());
		if(address.getCountyCodeWithName()!=null){
			addressForTransfer.setCountyCodeWithName(address.getCountyCodeWithName());
		}
		else if(address.getState()!=null && address.getCounty()!=null)
		{
			String countyName = msaService.findCountyNameByStateAndCode(address.getState(),address.getCounty());
			addressForTransfer.setCountyCodeWithName(address.getCounty()+"-"+countyName);
		}
		/*if (!customer.isIndividual()) {
			addressForTransfer.setContactPersonName(address
					.getContactPersonName());
		}*/
		 Customer cust = null;
	        if(customer!=null && InstanceOfUtil.isInstanceOfClass(Customer.class, customer)){
	        	cust = customerService.findCustomerById(customer.getId());
	        }
	        if(cust!=null && !cust.isIndividual()){
	        	addressForTransfer.setContactPersonName(address
						.getContactPersonName());
	        	addressForTransfer.setCustomerContactTitle(address.getCustomerContactTitle());
	        }
		Organization organizationForSearch = null;
		if (this.dealerOrganization != null) {
			organizationForSearch = this.dealerOrganization;
		} else {
			if (forDealer != null) {
				organizationForSearch = forDealer;
			} /*else {
				// admin is modifying DR/ETR
				organizationForSearch = new HibernateCast<ServiceProvider>()
						.cast(this.warranty.getForTransaction().getOwnerShip());
			}*/
		}
		AddressBookAddressMapping addressBookAddressMapping = null;
		if(organizationForSearch == null) {
			addressBookAddressMapping = addressBookService.getAddressBookAddressMappingByAddress(address);
		} else {
			addressBookAddressMapping = addressBookService
				.getAddressBookAddressMappingByOrganizationAndAddress(
						address, organizationForSearch);
		}
		
		
		if (addressBookAddressMapping != null) {
			addressForTransfer.setType(addressBookAddressMapping.getType());
		}
		return addressForTransfer;
	}
	private boolean doesRequestHaveInventoriesWithReducedCoverage() {
		boolean hasInventoryWithReducedCoverage = false;
		for (Iterator<MultipleInventoryAttributesMapper> miapIter = this.inventoryItemMappings
				.iterator(); miapIter.hasNext();) {
			MultipleInventoryAttributesMapper miap = miapIter.next();
			WarrantyCoverageRequest wcr = warrantyCoverageRequestService
					.findByInventoryItemId(miap.inventoryItem.getId());
			if (wcr != null) {
				return true;
			}
		}
		return hasInventoryWithReducedCoverage;
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

	/*
	 * public boolean isContinuingWithClubCar(){ if("Continuing with Club
	 * Car".equals(this.marketingInformation.getIfPreviousOwner())) return true;
	 * return false; }
	 *
	 * public boolean isSwitchingToClubCar(){ if("Switching to Club
	 * Car".equals(this.marketingInformation.getIfPreviousOwner())) return true;
	 * return false; }
	 */

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public boolean areNonDefaultPoliciesAvailable() {
		// the available policies are sorted on "by-defaultness"
		// if the last policy on the list is available by default, we know
		// that there are no non-default policies available
		return !this.availablePolicies.get(this.availablePolicies.size() - 1)
				.getPolicyDefinition().isAvailableByDefault();
	}

	public String getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public PolicyDefinitionService getPolicyDefinitionService() {
		return this.policyDefinitionService;
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	public Party getCustomer() {
		return this.customer;
	}

	public void setCustomer(Party customer) {
		this.customer = customer;
	}
	
	public Customer getOperator() {
		return this.operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

	public AddressForTransfer getAddressForTransfer() {
		return addressForTransfer;
	}

	public void setAddressForTransfer(AddressForTransfer addressForTransfer) {
		this.addressForTransfer = addressForTransfer;
	}

	public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
		return this.inventoryItemMappings;
	}

	public void setInventoryItemMappings(
			List<MultipleInventoryAttributesMapper> inventoryItemMappings) {
		this.inventoryItemMappings = inventoryItemMappings;
	}

	public MarketingInformation getMarketingInformation() {
		return this.marketingInformation;
	}

	public void setMarketingInformation(
			MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public boolean isSaveAsDraft() {
		return this.saveAsDraft;
	}

	public void setSaveAsDraft(boolean saveAsDraft) {
		this.saveAsDraft = saveAsDraft;
	}

	public int getInventoryItemIndex() {
		return this.inventoryItemIndex;
	}

	public void setInventoryItemIndex(int inventoryItemIndex) {
		this.inventoryItemIndex = inventoryItemIndex;
	}

	public List<RegisteredPolicy> getAvailablePolicies() {
		return this.availablePolicies;
	}

	public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
		this.availablePolicies = availablePolicies;
	}

	public InventoryItem getInventoryItem() {
		return this.inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public boolean isAllowInventorySelection() {
		return this.allowInventorySelection;
	}

	public void setAllowInventorySelection(boolean allowInventorySelection) {
		this.allowInventorySelection = allowInventorySelection;
	}

	public boolean isConfirmRegistration() {
		return this.confirmRegistration;
	}

	public void setConfirmRegistration(boolean confirmRegistration) {
		this.confirmRegistration = confirmRegistration;
	}

	public String getRegistrationComments() {
		return this.registrationComments;
	}

	public void setRegistrationComments(String registrationComments) {
		this.registrationComments = registrationComments;
	}

	// FIXME: Any exception that occurs comes up as a java script
	// Hence returning "{}" for any exception that occurs.
	public String getJSONifiedAttachmentList() {
		try {
			Warranty warranty = this.inventoryItemMappings.get(
					this.inventoryItemIndex).getInventoryItem()
					.getDraftWarranty();
			List<Document> attachments;
			if (warranty != null
					&& warranty.getAttachments() != null
					&& (this.inventoryItemMappings.get(this.inventoryItemIndex)
							.getAttachments() == null || this.inventoryItemMappings
							.get(this.inventoryItemIndex).getAttachments()
							.size() <= 0)) {
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
			operator=customerService.findCustomerById(warranty.getCustomer().getId());
			for (InventoryItemAttributeValue attributeValue : warranty
					.getForItem().getInventoryItemAttrVals()) {
				if (AttributeConstants.MODIFY_DELETE_REASON
						.equals(attributeValue.getAttribute().getName())) {
					this.deleteModifyReason = attributeValue.getValue();
				}
			}
			//to display countycode-name in view DR/ETR page and set it in address for transfer
			if (warranty.getForItem()!=null && warranty.getForItem().getOwnedBy() != null
					&& warranty.getForItem().getOwnedBy().getAddress() != null) {
				if (warranty.getForItem().getOwnedBy().getAddress()
						.getCountyCodeWithName() != null) {
					warranty.getAddressForTransfer().setCountyCodeWithName(
							warranty.getForItem().getOwnedBy().getAddress()
									.getCountyCodeWithName());
				} else if (warranty.getForItem().getOwnedBy().getAddress().getState() != null
						&& warranty.getForItem().getOwnedBy().getAddress().getCounty() != null) {
					String countyName = msaService
							.findCountyNameByStateAndCode(warranty.getForItem()
									.getOwnedBy().getAddress().getState(),
									warranty.getForItem().getOwnedBy().getAddress()
											.getCounty());
					warranty.getAddressForTransfer().setCountyCodeWithName(
							warranty.getForItem().getOwnedBy().getAddress().getCounty()
									+ "-" + countyName);
				}
			}
			return SUCCESS;
		}
		return ERROR;
	}

	public boolean isForDR() {
		return this.forDR;
	}

	public void setForDR(boolean forDR) {
		this.forDR = forDR;
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

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
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

	public void setServletRequest(HttpServletRequest req) {
		this.request = req;
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

	public Long getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Long inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public String getDeleteModifyReason() {
		return deleteModifyReason;
	}

	public void setDeleteModifyReason(String deleteModifyReason) {
		this.deleteModifyReason = deleteModifyReason;
	}

	public Long getWarrantyTransactionId() {
		return warrantyTransactionId;
	}

	public void setWarrantyTransactionId(Long warrantyTransactionId) {
		this.warrantyTransactionId = warrantyTransactionId;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public boolean isManualApprovalRequiredForDR() {
		return configParamService.getBooleanValue(
				ConfigName.MANUAL_APPROVAL_FLOW_FOR_DR.getName())
				.booleanValue();
	}
	
	public boolean checkWRCount() {
		int wrFlagValue = configParamService.getLongValue(
				ConfigName.FLAG_FOR_MANUAL_REVIEW.getName()).intValue();
		if(wrFlagValue>0)
		{
		 int wrCount=  warrantyService.findWRCount(getSelectedBusinessUnit()).intValue();
		 
		 if((wrCount+1)% wrFlagValue == 0)
		 {
		 return true;
		 }
		}
	 return false;
		
	}
	public boolean isMarketInfoApplicable() {
		return configParamService.getBooleanValue(
				ConfigName.IS_MARKETING_INFO_APPLICABLE.getName())
				.booleanValue();
	}

	public boolean isGenericAttachmentRequired() {
            final boolean genericAttachmentAllowedForBU =
                    configParamService.getBooleanValue(ConfigName.ENABLE_GENERIC_ATTACHMENT.getName()).booleanValue();
            return genericAttachmentAllowedForBU && (
                    inventoryItemMappings == null ||
                    inventoryItemMappings.size() > 1);
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public List<Document> getCommonAttachments() {
		return commonAttachments;
	}

	public void setCommonAttachments(List<Document> commonAttachments) {
		this.commonAttachments = commonAttachments;
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
	
	private boolean isCurrentOwner(){
		for(MultipleInventoryAttributesMapper mapper : inventoryItemMappings){
			if(mapper.getInventoryItem()!=null && !getLoggedInUser().getBelongsToOrganization().getId().equals(mapper.getInventoryItem().getCurrentOwner().getId())){
				return false;
			}
		}
		return true;
	}
	
	public boolean isLoggedInDealerShipToDealer() {
		for(MultipleInventoryAttributesMapper mapper : inventoryItemMappings){
			if(mapper.getInventoryItem()!=null  && mapper.getInventoryItem().getShipTo()!=null && !getLoggedInUser().getBelongsToOrganization().getId().equals(mapper.getInventoryItem().getShipTo().getId())){
				return false;
			}
		}
		return true;
	}
	
	
	private boolean isForDealerInvCurrentOwner() {
		for(MultipleInventoryAttributesMapper mapper : inventoryItemMappings){
			if( mapper.getInventoryItem()!=null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, mapper.getInventoryItem().getCurrentOwner())){
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
				
		if (!isCurrentOwner() && !isForPrintPDI() && !allowWROnOthersStock()) {
			addActionError("error.dr.saveAsDraft");
		}
		
		if(isLoggedInUserAnInternalUser() && !isForDealerInvCurrentOwner()){
			addActionError("error.dr.cannotSaveWithOtherDealer");
		}
		
		if (this.inventoryItemMappings.isEmpty()) {
			addActionError("error.noItemsSelected");
			if(forPrintPDI) {
				setSaveAsDraft(Boolean.FALSE);
			}
			return INPUT;
		}
			
		if (isAdditionalInformationDetailsApplicable() || getMarketingInformation()!=null) {
			if (this.marketingInformation != null){
				 if(getMarketingInformation().getTransactionType()!=null && !listOfTransactionTypes.contains(getMarketingInformation().getTransactionType())){
						addActionError("error.marketInfo.transactionType");
						if(forPrintPDI) {
							setSaveAsDraft(Boolean.FALSE);
						}
						return INPUT;
					}
			}
		}
		this.forDR = true;
		StringBuffer serialNumbers = new StringBuffer();		
		List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();	
		validateSerialNumbers();
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
			removeUnselectedPolicies(inventoryItemMapping);
			if (isMarketInfoApplicable()) {
				validateMarketInfo(inventoryItemMapping.getSelectedMarketingInfo());
			}
			InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
			if (isDeliveryReportSubmitted(inventoryItem)) {
				addActionError("error.inventory.drfiled", inventoryItem.getSerialNumber());
			}
			if(forPrintPDI && !BrandType.UTILEV.getType().equalsIgnoreCase(inventoryItem.getBrandType())) {
				String dealerBrand = new HibernateCast<Dealership>()
					.cast(getForDealer()).getBrand();
				if(null!=dealerBrand && !dealerBrand.equals(inventoryItem.getBrandType())) {
					addActionError("error.pdi.differentBrand", inventoryItem.getSerialNumber());
				}
			}
			
			Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
			Integer maxSequenceNumber = getMaxSequenceNumber(inventoryItem);
			for(InventoryItemComposition composition : inventoryItem.getComposedOf()){
				if(composition.getSequenceNumber() == null){
					maxSequenceNumber = maxSequenceNumber + 2;
					composition.setSequenceNumber(maxSequenceNumber.toString());
				}
			}
			this.warrantyUtil.validateMajorComponents(inventoryItem, errorCodeMap);
			this.warrantyUtil.validateAdditionalComponents(inventoryItem, errorCodeMap);
			setErrorCodesMapToActionErrors(errorCodeMap);			
            inventoryItem.setDeliveryDate(inventoryItemMapping.getWarrantyDeliveryDate());     
			createWarranty(inventoryItemMapping, inventoryItem);
			
			if(!itemsForTask.contains(itemsForTask))
			{	
				if (!inventoryItem.getCurrentOwner().equals(getOrganization()))
					inventoryItem = this.warrantyUtil.performD2D(inventoryItem, getLoggedInUser().getBelongsToOrganization(), false);
				
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
				if (inventoryItemMapping.isWaiverInformationEditable() && 
						inventoryItemMapping.getDieselTierWaiver()!=null) {
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
				}
				inventoryItem.getLatestWarranty().setDieselTierWaiver(inventoryItemMapping.getDieselTierWaiver());
				inventoryItem.setDisclaimerInfo(inventoryItemMapping.getDieselTierWaiver().getDisclaimer());
       		}else{
       			inventoryItem.getLatestWarranty().setDieselTierWaiver(null);
       			inventoryItem.setDisclaimerInfo(null);
       		}
		}
		if (!hasErrors()) {
			warrantyService.createInventoryAndCreateWarrantyReport(itemsForTask, true);
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
				addActionMessage("message.draftWarrantySavedForItems", serialNumbers);
				if(forPrintPDI) {
					setSaveAsDraft(Boolean.FALSE);
				}
                if(allowInventorySelection){
                    return SUCCESS;
                }
                if(WarrantyStatus.DRAFT.equals(this.warranty.getStatus())){
                	return NONE;
                } else {
                	return INPUT;
                }
			}
			else{
				addActionMessage("message.draftWarrantySavedForItems", serialNumbers);
				if(forPrintPDI) {
					setSaveAsDraft(Boolean.FALSE);
				}
				return SUCCESS;
			}
			
		}
	
	}	

	public List<ListOfValues> getOems(String className) {		
			return lovRepository.findAllActive(className);		
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

	private Organization getOrganization() {
		Organization organization = (isLoggedInUserAnAdmin() || isLoggedInUserAnInvAdmin())? getForDealer()
				: getLoggedInUser().getBelongsToOrganization();
		return organization;
	}
	
	
	public void populateCustomerTypes() {
	Map<Object, Object> keyValueOfCustomerTypes=new HashMap<Object, Object>();

		if (this.configParamService == null) {
			initDomainRepository();
		}
         
		if (getLoggedInUsersDealership() !=null && getLoggedInUsersDealership().isNationalAccount()){
			keyValueOfCustomerTypes.put(AdminConstants.NATIONAL_ACCOUNT,AdminConstants.NATIONAL_ACCOUNT);
			
			}
		// need to put BU filter
	else{
		keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_DR
						.getName());
		}
		if (keyValueOfCustomerTypes != null
				&& !keyValueOfCustomerTypes.isEmpty()) {
			customerTypes.putAll(keyValueOfCustomerTypes);
		}
	}

	private boolean validateCustomerType(String addressBookType) {
		boolean isValid = true;
		if (addressBookType == null || SELECT.equalsIgnoreCase(addressBookType)) {
			addActionError("error.customerTypeNotSelected",new String[] { inventoryItem.getSerialNumber() });
			return isValid = false;
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

	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.configParamService = (ConfigParamService) beanLocator
				.lookupBean("configParamService");
	}

	public boolean isModifyDRorETR() {
		return isModifyDRorETR;
	}
	
	public boolean canModifyDRorETR() {
		return true;
	}
	
	public void setModifyDRorETR(boolean isModifyDRorETR) {
		this.isModifyDRorETR = isModifyDRorETR;
	}

	public boolean isAdditionalInformationDetailsApplicable() {
		return this.configParamService
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}

	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}

	public boolean isReductionInCoverage() {
		return reductionInCoverage;
	}

	public void setReductionInCoverage(boolean reductionInCoverage) {
		this.reductionInCoverage = reductionInCoverage;
	}

	public InventoryScrapTransactionXMLConverter getInventoryScrapTransactionXMLConverter() {
		return inventoryScrapTransactionXMLConverter;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
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
    private List<Market> listOfMarketApplications = new ArrayList<Market>();
    private Map<String,String> listIfPreviousOwner = new HashMap<String,String>();
    private List<String> listOfAdditionalInfo = new ArrayList<String>();
    private List<CompetitionType> listOfCompetitionTypes = new ArrayList<CompetitionType>();  
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

    public boolean fetchPriceForPolicyForCurrency(PolicyDefinition policyDefinition,
			InventoryItem inventoryItem) {
        for (PolicyFees registrationFees : policyDefinition.getPolicyFees()) {
          if(inventoryItem.getCurrentOwner().getPreferredCurrency().getCurrencyCode().
                  equalsIgnoreCase(registrationFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode())){
              return registrationFees.getPolicyFee().isZero();
          }
        }
		return false;
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
	
	 public String getUnitRegistrationTemplate() {
		
		// inventoryItem.setHoursOnMachine(new Integer(0));
		
	        return SUCCESS;
	    }
	 
	 public String getMajorComponentsTemplate() {
	        return SUCCESS;
	    }
	 
	 public String getAdditionalComponentsTemplate() {
	        return SUCCESS;
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

	public List<RegisteredPolicy> getExtendedPolicies() {
		return extendedPolicies;
	}

	public void setExtendedPolicies(List<RegisteredPolicy> extendedPolicies) {
		this.extendedPolicies = extendedPolicies;
	}

	public final WarrantyUtil getWarrantyUtil() {
		return warrantyUtil;
	}

	public final void setWarrantyUtil(WarrantyUtil warrantyUtil) {
		this.warrantyUtil = warrantyUtil;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSelectedPolicies(List<RegisteredPolicy> selectedPolicies) {
		this.selectedPolicies = selectedPolicies;
	}

	public List<RegisteredPolicy> getSelectedPolicies() {
		return selectedPolicies;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public List<User> getListOfSalesPersons() {
		return listOfSalesPersons;
	}

	public void setListOfSalesPersons(List<User> listOfSalesPersons) {
		this.listOfSalesPersons = listOfSalesPersons;
	}

	public boolean isEnterpriseDealer() {
		return enterpriseDealer;
	}

	public void setEnterpriseDealer(boolean isEnterpriseDealer) {
		this.enterpriseDealer = isEnterpriseDealer;
	}

	public void setListOfMarketTypes(List<Market> listOfMarketTypes) {
		this.listOfMarketTypes = listOfMarketTypes;
	}

	public List<Market> getListOfMarketTypes() {
		return listOfMarketTypes;
	}

	public void setListOfMarketApplications(List<Market> listOfMarketApplications) {
		this.listOfMarketApplications = listOfMarketApplications;
	}

	public List<Market> getListOfMarketApplications() {
		return listOfMarketApplications;
	}

	public void setMarketType(Market marketType) {
		this.marketType = marketType;
	}

	public Market getMarketType() {
		return marketType;
	}
	
	public String listCustomerDetails() {
		  return SUCCESS;
         
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	public String getPdiName() {
		return pdiName;
	}

	public void setPdiName(String pdiName) {
		this.pdiName = pdiName;
	}
	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}
	public boolean isPdiLinksDisplay() {
		return pdiLinksDisplay;
	}

	public void setPdiLinksDisplay(boolean pdiLinksDisplay) {
		this.pdiLinksDisplay = pdiLinksDisplay;
	}
	
	public void printPdi() throws ItemNotFoundException{
		if(this.inventoryItem != null){
			serialNumber = this.inventoryItem.getSerialNumber();
			String contextPath = request.getContextPath();
			final Item item = inventoryService.findPdiNameBySerialNumber(serialNumber);
			if(item != null){
				if(StringUtils.hasText(item.getPdiFormName())){
					String pdiPath = item.getPdiFormName()+".pdf";
					pdiName = contextPath+"/pdiForms/"+pdiPath;
				}
				else{
					pdiName = contextPath+"/pdiForms/PDIForm1.pdf";
				}
			}
		}
	}
	
	public boolean isPDIlinkDisplayEnabled() {
		try{
			return this.configParamService.getBooleanValue(ConfigName.DISPLAY_PDI_LINKS.getName());
		} catch (NoValuesDefinedException ex){
			return false;
		}
	}

	public void downloadPdiForm() {
		FileInputStream fileStream = null;
		String pdiFormName = null;
		try {
			final Item item = inventoryService.findPdiNameBySerialNumber(serialNumber);
			if(item != null){
					pdiFormName = item.getPdiFormName();	
					servletContext = this.getServletContext();
					pdiName = servletContext.getRealPath("pdiForms\\"+pdiFormName+".pdf");
					fileStream = new FileInputStream(new File(pdiName));
			}
		} catch (Exception exception) {
			pdiFormName = "PDIForm1";
			pdiName = servletContext.getRealPath("pdiForms\\"+pdiFormName+".pdf");
			try{
				fileStream = new FileInputStream(new File(pdiName));
			}catch(Exception e){
				logger.error("Failed to write file to output stream", exception);
			}
		}finally{
			streamFile(fileStream, pdiFormName);
		}
	}

	private void streamFile(FileInputStream fileInputStream, String fileName) {
		servletResponse.setContentType("application/pdf");
		servletResponse.setHeader("Content-disposition",
				"attachment; filename=" + fileName.replaceAll(" ", ""));
		try {
			FileCopyUtils.copy(fileInputStream,servletResponse.getOutputStream());
		} catch (Exception e) {
			logger.error("Failed to write file to output stream", e);
		}
	}

	public String getPreOrderBooking() {
		return preOrderBooking;
	}

	public void setPreOrderBooking(String preOrderBooking) {
		this.preOrderBooking = preOrderBooking;
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

	public Boolean isDisclaimerAvailable(MultipleInventoryAttributesMapper mapper){
		InventoryItem curItem = this.inventoryItem;
		this.inventoryItem = mapper.getInventoryItem();
		boolean isAvailable = engineTierCountryMapping(mapper).equals(SUCCESS);
		this.inventoryItem = curItem;
		return isAvailable;
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
	
	private CalendarDate getCutOffDate(CalendarDate shipmentDate, PolicyDefinition policyDefinition) {
		Integer monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment();
		Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
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
	
	public boolean displaySaveAsDraftButton() {
		return allowWROnOthersStock()
				|| isCurrentOwner() || isLoggedInDealerShipToDealer();
	}
	
	private Set<Organization> childDealers = new HashSet<Organization>();
	public void setChildDealers(Set<Organization> childDealers) {
		this.childDealers = childDealers;
	}

	public Set<Organization> getChildDealers() {
		for (MultipleInventoryAttributesMapper inventoryItemMapping : this.inventoryItemMappings) {
			if (getLoggedInUsersDealership() != null
					&& orgService
							.getChildOrganizationsIds(getLoggedInUsersOrganization()
									.getId()) != null
					&& !orgService.getChildOrganizationsIds(
							getLoggedInUsersOrganization().getId()).isEmpty()
					&& inventoryItemMapping.getInventoryItem() != null
					&& !(inventoryItemMapping.getInventoryItem()
							.getCurrentOwner().getName()
							.equals(getLoggedInUsersDealership().getName()))) {
					childDealers.add(inventoryItemMapping.getInventoryItem()
							.getCurrentOwner());
				
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
		
	public boolean displayInternalInstallType() {
		return configParamService
				.getBooleanValue(ConfigName.DISPLAY_INTERNAL_INSTALL_TYPE
						.getName());
	}

	public List<InternalInstallType> getListOfInternalInstallTypes() {
		return listOfInternalInstallTypes;
	}

	public void setListOfInternalInstallTypes(
			List<InternalInstallType> listOfInternalInstallTypes) {
		this.listOfInternalInstallTypes = listOfInternalInstallTypes;
	}
	
	private boolean manualApprovalRequiredForDemoTruck(InventoryItem inventoryItem) {
		if(isAdditionalInformationDetailsApplicable()
				&& isBuConfigAMER()){
			if(this.marketingInformation != null && this.marketingInformation.getContractCode()!= null
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
	
	public boolean displayStockUnitDiscountDetails() {
		return this.configParamService
				.getBooleanValue(ConfigName.ENABLE_STOCK_UNIT_DISCOUNT_DETAILS.getName());
	}
	
	public boolean allowWROnOthersStock() {
		return this.configParamService.getBooleanValue(ConfigName.ALLOW_WNTY_REG_ON_OTHERS_STOCKS.getName());
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}
	
}


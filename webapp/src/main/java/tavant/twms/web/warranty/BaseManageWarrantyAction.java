package tavant.twms.web.warranty;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.json.JSONArray;
import org.json.JSONException;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.policy.*;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstance;
import tavant.twms.domain.WarrantyTask.WarrantyTaskInstanceService;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.web.actions.SortedHashMap;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getUnitDocumentListJSON;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import java.util.*;
import java.math.BigDecimal;

import javax.mail.search.AddressStringTerm;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Sep 5, 2008
 * Time: 6:10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseManageWarrantyAction extends SummaryTableAction {
	private static final String STOCK = "STOCK";
	private static final String RETAIL = "RETAIL";
    private static Logger logger = LogManager.getLogger(BaseManageWarrantyAction.class);
    private InventoryItem inventoryItem;
    private int inventoryItemIndex = -1;
    protected Party customer;
    private Customer operator;
	private AddressForTransfer addressForTransfer;
	private AddressForTransfer operatorAddressForTransfer;
    private List<TransactionType> listOfTransactionTypes = new ArrayList<TransactionType>();
    private List<CompetitionType> listOfCompetitionTypes = new ArrayList<CompetitionType>();
    private List<Market> listOfMarkets = new ArrayList<Market>();
    private List<Market> listOfMarketTypes = new ArrayList<Market>();
    private List<ContractCode> listOfContractCodes =new ArrayList<ContractCode>();
    private List<InternalInstallType> listOfInternalInstallTypes =new ArrayList<InternalInstallType>();
    private List<MaintenanceContract> listofMaintenanceContracts=new ArrayList<MaintenanceContract>();
	private List<IndustryCode> listOfIndustryCodes=new ArrayList<IndustryCode>();
	private List<Market> listOfMarketApplications = new ArrayList<Market>();
    private List<CompetitorMake> listOfCompetitorMakes = new ArrayList<CompetitorMake>();
    private List<CompetitorModel> listOfCompetitorModels = new ArrayList<CompetitorModel>();
    private Map<String,String> listIfPreviousOwner = new HashMap<String,String>();
    private List<String> listOfAdditionalInfo = new ArrayList<String>();
    private final Map<String, User> nameToSalesMan = new LinkedHashMap<String, User>();
    private ItemGroupService itemGroupService;
    protected WarrantyTaskInstanceService warrantyTaskInstanceService;
    protected WarrantyService warrantyService;
 

	private List<MultipleInventoryAttributesMapper> inventoryItemMappings = new ArrayList<MultipleInventoryAttributesMapper>();
    private List<RegisteredPolicy> availablePolicies;
    private List<RegisteredPolicy> selectedPolicies;
    private WarrantyStatus status;
    protected PolicyService policyService;
    private WarrantyTaskInstance warrantyTaskInstance;
    private String transactionType;
    private List<Document> commonAttachments = new ArrayList<Document>();
    private Map<Object,Object> customerTypes= new SortedHashMap<Object,Object>();
    private String addressBookType;
    private String addressBookTypeForOperator;
    private ConfigParamService configParamService;
    private AddressBookService addressBookService;
    private OrganizationRepository organizationRepository;
    private boolean isModifyDRorETR = false;
	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
	private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;
    private MarketingInformation marketingInformation;
    protected LovRepository lovRepository;
    private ServiceProvider installingDealer;
    private ServiceProvider dealer;
    private AdditionalMarketingInfoService additionalMarketingInfoService;
    private String selectedBusinessUnit;
    private Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> marketingInfo= new TreeMap<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>>();
    private UserRepository userRepository;
    private List<User> listOfSalesPersons = new ArrayList<User>();
    protected WarrantyUtil warrantyUtil;
    private Market marketType;
    private CustomerService customerService ;
    protected BuSettingsService buSettingsService;
    
    protected MSAService msaService;

	public BuSettingsService getBuSettingsService() {
		return buSettingsService;
	}

	public void setBuSettingsService(BuSettingsService buSettingsService) {
		this.buSettingsService = buSettingsService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	private InventoryService inventoryService;
    @Override
    protected PageResult<?> getBody() {
        if(WarrantyStatus.REJECTED.getStatus().equals(folderName)
                ||WarrantyStatus.FORWARDED.getStatus().equals(folderName)
                ||WarrantyStatus.DELETED.getStatus().equals(folderName)
                ||WarrantyStatus.DRAFT.getStatus().equals(folderName)){
          return this.warrantyTaskInstanceService.findWarrantiesForFolder(createCrieria(getLoggedInUser()));
        }else{
         return this.warrantyTaskInstanceService.findWarrantiesForFolder(createCrieria(null));
        }
    }

   public boolean isInstallingDealerEnabled() {
		return this.getConfigParamService()
		.getBooleanValue(ConfigName.ENABLE_DEALER_AND_INSTALLATION_DATE
				.getName());
	}
   
   public boolean isMarketInfoApplicable() { 
		return getConfigParamService().getBooleanValue(
				ConfigName.IS_MARKETING_INFO_APPLICABLE.getName())
				.booleanValue();
	}
    
    private WarrantyListCriteria createCrieria(User user) {
            WarrantyListCriteria criteria = new WarrantyListCriteria();
            criteria.setStatus(getStatus());
            criteria.setTransactionType(transactionType);
            criteria.setFiledBy(user);
            criteria.setDealer(getLoggedInUsersDealership());
            criteria.setPageSpecification(new PageSpecification(this.page - 1, this.pageSize));
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Folder Name : " + this.folderName);
            }
            addFilterCriteria(criteria);
            addSortCriteria(criteria);
            if (this.logger.isInfoEnabled()) {
                this.logger
                        .info("page size " + this.pageSize + " page to be fetched "
                                + this.page);
            }
            return criteria;
        }

    private void addFilterCriteria(WarrantyListCriteria criteria) {
		for (Iterator<String> iter = this.filters.keySet().iterator(); iter.hasNext();) {
			String filterName = iter.next();
			String filterValue = this.filters.get(filterName).toUpperCase();
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Adding filter criteria " + filterName + " : "
						+ filterValue);
			}
			 if(filterName.contains("itemGroupDescription")){
	             	filterName=filterName.replace("itemGroupDescription","description");
	             }
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

        private void addSortCriteria(WarrantyListCriteria criteria) {
            for (Iterator<String[]> iter = this.sorts.iterator(); iter.hasNext();) {
                String[] sort = iter.next();
                String sortOnColumn = sort[0];
                boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Adding sort criteria " + sortOnColumn + " "
                            + (ascending ? "ascending" : "descending"));
                }
                criteria.addSortCriteria(sortOnColumn, ascending);
            }
        }


    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0,
                "String", "id", false, true, true, false));
        header.add(new SummaryTableColumn("columnTitle.inventoryAction.serial_no",
                "warrantyAudit.forWarranty.forItem.serialNumber", 15, "String", "warrantyAudit.forWarranty.forItem.serialNumber", true,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.common.product",
                "warrantyAudit.forWarranty.forItem.ofType.product.groupCode", 15, "String", "warrantyAudit.forWarranty.forItem.ofType.product.groupCode", false,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.inventoryAction.item_model",
                "warrantyAudit.forWarranty.forItem.ofType.model.itemGroupDescription", 15, "String", "warrantyAudit.forWarranty.forItem.ofType.model.itemGroupDescription", false,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.inventoryAction.hours_on_machine",
                "warrantyAudit.forWarranty.forItem.hoursOnMachine", 15, "String", "warrantyAudit.forWarranty.forItem.hoursOnMachine", false,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.inventoryAction.delivery_date",
                "warrantyAudit.forWarranty.deliveryDate", 10, "date", "warrantyAudit.forWarranty.deliveryDate", false,
                false, false, true));
        header.add(new SummaryTableColumn("Filed By",
                "warrantyAudit.forWarranty.filedBy.name", 10, "String", "warrantyAudit.forWarranty.filedBy.name", false,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.dcap.createDate",
                "warrantyAudit.forWarranty.filedDate", 20, "date", "warrantyAudit.forWarranty.filedDate", false,
                false, false, true));
        return header;
    }

    protected void prepareCommonAttachments() {
        List<List<Document>> listOfDocList = new ArrayList<List<Document>>();

        for (MultipleInventoryAttributesMapper invItemMapper :
                inventoryItemMappings) {
            final List<Document> attachments = invItemMapper.getAttachments();

            if (!attachments.isEmpty()) {
                listOfDocList.add(attachments);
            }

            if (listOfDocList.size() > 1) {
                break;
            }
        }

        if (listOfDocList.size() > 1) {
            for (Document attach1 : listOfDocList.get(0)) {
                for (Document attach2 : listOfDocList.get(1)) {
                    if (attach2 != null && attach1 != null && attach2.getId().equals(attach1.getId())) {
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

    protected void prepareMarketingInformation(InventoryItem inventoryItem){
        if (listIfPreviousOwner.isEmpty()) {
			listIfPreviousOwner.put(SWITCHING,getText("label.wntyreg.prevowner.switch"));
			listIfPreviousOwner.put(CONTINUING,getText("label.wntyreg.prevowner.continue"));
			listIfPreviousOwner.put(UNKNOWN_NOT_PROVIDED,getText("label.wntyreg.prevowner.Unknown/NotProvided"));
		}        
        if(!StringUtils.hasText(SelectedBusinessUnitsHolder.getSelectedBusinessUnit())
                && inventoryItem!=null){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem.getBusinessUnitInfo().getName());
        }
		listOfTransactionTypes = this.warrantyService.listTransactionTypes();
        listOfMarketTypes = this.marketService.listMarketTypes();
        listOfContractCodes =this.warrantyService.listContractCode();
        listOfInternalInstallTypes = this.warrantyService.listInternalInstallType();
        listofMaintenanceContracts=this.warrantyService.listMaintenanceContract();
        listOfIndustryCodes=this.warrantyService.listIndustryCode();
        if(this.getMarketingInformation()!=null && this.getMarketingInformation().getMarket()!=null)
        listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketingInformation().getMarket().getId());
/*        if(inventoryItem != null){
       	listOfSalesPersons=warrantyUtil.getSalesPersonsForDealer(inventoryItem, null);
        }
*/       
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
    }

    protected void prepareInventoryItem(InventoryItem inventoryItem) {
    	if(!doesMapperContainTheInventoryItem(inventoryItem)){
        MultipleInventoryAttributesMapper mapper = new MultipleInventoryAttributesMapper();
		Warranty warranty = inventoryItem.getLatestWarranty();
        mapper.setWarrantyDeliveryDate(warranty.getDeliveryDate()); 
        mapper.setSelectedMarketingInfo(warranty.getSelectedAddtlMktInfo());
        mapper.setEquipmentVIN(warranty.getEquipmentVIN());
        mapper.setFleetNumber(warranty.getFleetNumber());
        mapper.setInstallationDate(warranty.getInstallationDate());
        mapper.setWarrantyDeliveryDate(warranty.getDeliveryDate());
        mapper.setOem(warranty.getOem());
        this.addressBookType = this.warrantyUtil.populateExistingCustomerType(warranty,0L,customerTypes,addressBookType);
        populateExistingOperatorType(warranty);
        mapper.setInventoryItem(inventoryItem);   
      //  listOfSalesPersons = this.warrantyUtil.getSalesPersonsForDealer(inventoryItem, null);
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
		for (RegisteredPolicy registeredPolicy : inventoryItem.getLatestWarranty().getPolicies()) {
			mapper.getSelectedPolicies().add(registeredPolicy);
		}
        mapper.setAttachments(warranty.getAttachments());
        if (InvTransationType.DR.getTransactionType().equalsIgnoreCase(
				inventoryItem.getLatestWarranty().getTransactionType().getTrnxTypeValue()))
        prepareApplicablePolicies(mapper);
        this.setSelectedPolicies(mapper.getSelectedPolicies());	
        if(isLoggedInUserAnInternalUser()){
        	if(mapper.getSelectedPolicies() == null || mapper.getSelectedPolicies().isEmpty()) {
        		addActionMessage("message.noPolicySelected", inventoryItem.getSerialNumber());
        	}
        	if(isAdditionalInformationDetailsApplicable() && marketingInformation != null && marketingInformation.getContractCode()!=null     //null check for contract code, when internal install type is selected
        			&& marketingInformation.getContractCode().getContractCode().equals(AdminConstants.DEMO)
        			&& inventoryItem.getHoursOnMachine() > 80) {
        		addActionMessage("message.demoTruck.authorizationAttached", inventoryItem.getSerialNumber());
        	}
        }
        this.inventoryItemMappings.add(mapper);
    	}
    }	
    
    private boolean doesMapperContainTheInventoryItem(InventoryItem inventoryItem) {
    	if(!this.inventoryItemMappings.isEmpty()){
    		this.inventoryItemMappings.removeAll(Collections.singleton(null));
    	}
    	for (MultipleInventoryAttributesMapper mapper : this.inventoryItemMappings) {
			if (inventoryItem!=null && inventoryItem.equals(mapper.getInventoryItem())) {
				return true;
			}
		}
		return false;
	}

	protected void prepareApplicablePolicies(MultipleInventoryAttributesMapper mapper) {
		InventoryItem inventoryItem = mapper.getInventoryItem();
		Long hoursOnMachine = inventoryItem.getHoursOnMachine();
		if (inventoryItem.getDeliveryDate() != null && hoursOnMachine != null && hoursOnMachine >= 0) {
			mapper.setAvailablePolicies(fetchAvailablePolicies(inventoryItem, false));
			if (InvTransationType.DR.getTransactionType().equalsIgnoreCase(
					inventoryItem.getLatestWarranty().getTransactionType().getTrnxTypeValue())) {
				mapper.setExtendedPolicies(fetchPurchasedExtendedPolicies(inventoryItem));
				for (RegisteredPolicy regPolicy : mapper.getExtendedPolicies()) {
						if (!mapper.getAvailablePolicies().contains(regPolicy))
							mapper.getAvailablePolicies().add(regPolicy);
					}				
			}
		}
	}

    private List<RegisteredPolicy> fetchPurchasedExtendedPolicies(InventoryItem inventoryItem) {
		List<PolicyDefinition> extendedWarranty = new ArrayList<PolicyDefinition>();
		List<RegisteredPolicy> registeredPolicy = new ArrayList<RegisteredPolicy>();
		List<ExtendedWarrantyNotification> extnWrntyList = warrantyService
				.findAllStagedExtnWntyPurchaseNotificationForInv(inventoryItem);
		for (ExtendedWarrantyNotification extendedWarrantyNotification : extnWrntyList) {
			extendedWarranty.add(extendedWarrantyNotification.getPolicy());
		}

		registeredPolicy = createPolicies(extendedWarranty, inventoryItem, false);

		return registeredPolicy;
	}
    
    protected void preselectFreePolicies(MultipleInventoryAttributesMapper mapper) {
        for (RegisteredPolicy registeredPolicy : mapper.getAvailablePolicies()) {
            if (registeredPolicy.getPolicyDefinition().isAvailableByDefault()) {
                mapper.getSelectedPolicies().add(registeredPolicy);
            }
        }
    }

	public List<RegisteredPolicy> fetchAvailablePolicies(InventoryItem inventoryItem, boolean computePrice) {
		List<RegisteredPolicy> availablePolicies = new ArrayList<RegisteredPolicy>();
		try {
			if (inventoryItem != null && inventoryItem.getLatestWarranty() != null
					&& inventoryItem.getLatestWarranty().getInstallingDealer() != null) {
				List<PolicyDefinition> policyDefinitions = this.policyService.findPoliciesAvailableForRegistration(
						inventoryItem, this.addressBookType, new HibernateCast<ServiceProvider>().cast(inventoryItem
								.getLatestWarranty().getInstallingDealer()));
				availablePolicies = createPolicies(policyDefinitions, inventoryItem, computePrice);
				Collections.sort(availablePolicies);
				return availablePolicies;
			} else {
				return availablePolicies;
			}
		} catch (PolicyException pex) {
			logger.error("Error fetching policies available for InventoryItem[slNo:" + inventoryItem.getSerialNumber()
					+ "]", pex);
			addActionError("error.fetchingApplicablePolicies");
			return availablePolicies;
		}
	}

    protected void computePriceForPolicy(InventoryItem forItem,
                                       RegisteredPolicy policy) {
        PolicyRatesCriteria criteria = new PolicyRatesCriteria();
        DealerCriterion dealerCriterion = new DealerCriterion();
        dealerCriterion.setDealer(forItem.getLatestWarranty().getForDealer());
        criteria.setDealerCriterion(dealerCriterion);
        criteria.setProductType(forItem.getOfType().getProduct());
        criteria
                .setWarrantyRegistrationType(WarrantyRegistrationType.REGISTRATION);
        if(forItem.getLatestWarranty()!=null && forItem.getLatestWarranty().getAddressForTransfer()!=null && forItem.getLatestWarranty().getAddressForTransfer().getState()!=null)
        criteria.setCustomerState(forItem.getLatestWarranty().getAddressForTransfer().getState());
        Money price = this.policyService.getPolicyFeeForPolicyDefinition(policy
                .getPolicyDefinition(), criteria, forItem.getDeliveryDate());
        policy.setPrice(price);
    }

    protected List<RegisteredPolicy> createPolicies(
            List<PolicyDefinition> withDefinitions, InventoryItem forItem,
            boolean computePrice) throws PolicyException {    			
		Warranty latestWarranty = forItem.getLatestWarranty();
		boolean isETRWarranty = InvTransationType.ETR.getTransactionType()
						.equals(latestWarranty.getTransactionType().getTrnxTypeValue());		
        List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
        for (PolicyDefinition definition : withDefinitions) {
            String policyName = definition.getCode();
            if ((policyName == null) || (policyName.trim().equals(""))) {
                continue; // ignore this policy definition
            }
            RegisteredPolicy policy = new RegisteredPolicy();
            policy.setPolicyDefinition(definition);
            if(latestWarranty != null)
                for(RegisteredPolicy existingPolicy:latestWarranty.getPolicies()){
    				if(existingPolicy.getPolicyDefinition().getId()==definition.getId()){
    					policy.setWarrantyPeriod(existingPolicy.getWarrantyPeriod());    					
    				}				
    			}
          if(!isETRWarranty)
            policy.setWarrantyPeriod(definition.warrantyPeriodFor(forItem));

            if (computePrice) {
                computePriceForPolicy(forItem, policy);
            }

            policies.add(policy);
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

    public String listApplicablePolicies() {
		validateInventoryItem(this.inventoryItem);
		if (hasErrors()) {
			return INPUT;
		}
		this.availablePolicies = fetchAvailablePolicies(inventoryItem, false);
		return SUCCESS;
	}

    private boolean validateInventoryItem(InventoryItem inventoryItem) {
		boolean isValid = validateDeliveryAndInstallationDate(inventoryItem);
		isValid = validateCustomerType(this.addressBookType);
		
		if (inventoryItem.getHoursOnMachine() == null || inventoryItem.getHoursOnMachine() < 0) {
			// User needs to input hoursOnMachine
			addActionError("error.hoursOnMachineShouldBePositive", new String[]{inventoryItem
							.getSerialNumber()});
			isValid = false;
		}
		return isValid;
	}

    private void validateMajorComponents(InventoryItem inventoryItem) {

		List<InventoryItemComposition> invComposition = inventoryItem
				.getComposedOf();
		if (invComposition != null && invComposition.size() > 0) {
			invComposition.removeAll(Collections.singleton(null));
			inventoryItem.getComposedOf().removeAll(Collections.singleton(null));
			Set<String> serialNumber = new HashSet<String>();
			Set<String> partNumber = new HashSet<String>();
			for (InventoryItemComposition part : invComposition) {
				validateSerialNumbersForParts(part.getPart().getSerialNumber());
				validatePartNumber(part.getPart());
				validateInstallationDate(inventoryItem,part.getPart());
				
				if ((part.getPart().getInstallationDate() != null
						|| (part.getPart().getSerialNumber() != null && !part
								.getPart().getSerialNumber().equals("")))
						&& part.getPart().getOfType() != null) {
					validatePart(part.getPart(),inventoryItem);
					if (serialNumber.contains(part.getPart().getSerialNumber())&&partNumber.contains(part.getPart().getOfType().getNumber())) {
						 addActionError("Duplicate Part within a section");
						 
						break;

					} else {
						partNumber.add(part.getPart().getOfType().getNumber());
						serialNumber.add(part.getPart().getSerialNumber());
					}

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
		inventoryService.findItemBySerialNumberAndModelNumber(part.getSerialNumber(), part.getOfType());
		
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
    private boolean validateDeliveryAndInstallationDate(InventoryItem inventoryItem) {
		boolean isValid = true;
		if (inventoryItem.getInstallationDate() == null) {
			// User needs to choose installation date
			addActionError("error.installationDateNotFound", new String[] { inventoryItem.getSerialNumber() });
			isValid = false;

		} else {
			if (inventoryItem.getInstallationDate().isAfter(Clock.today())) {
				// The delivery date chosen cannot be a future date
				addActionError("error.installationDateCannotBeInFuture", new String[] { inventoryItem.getSerialNumber() });
				isValid = false;
			}

			if (inventoryItem.getInstallationDate().isBefore(inventoryItem.getShipmentDate())) {
				// The delivery date chosen by the user is before shipment date
				addActionError("error.installationDateBeforeShipment", new String[] { inventoryItem.getSerialNumber() });
				isValid = false;
			}	
		
		}
		if (inventoryItem.getDeliveryDate() == null) {
			addActionError("error.deliveryDateNotFound",
					new String[]{inventoryItem.getSerialNumber()});
			isValid = false;
		} else {
			if (inventoryItem.getDeliveryDate().isAfter(Clock.today())) {
				addActionError("error.deliveryDateCannotBeInFuture", new String[]{inventoryItem.getSerialNumber()});
				isValid = false;
			}
			 else if (inventoryItem.getDeliveryDate().isBefore(inventoryItem.getInstallationDate())) {
					// The delivery date chosen by the user is before installation date
					addActionError("error.deliveryDateBeforeInstallation", new String[] { inventoryItem.getSerialNumber() });
					isValid = false;
			} 
			if (inventoryItem.getDeliveryDate().isBefore(
					inventoryItem.getShipmentDate())) {
				addActionError("error.deliveryDateBeforeShipment", new String[]{inventoryItem.getSerialNumber()});
				isValid = false;
			}			
			if(inventoryItem.getInventoryItemAttrVals() != null && !inventoryItem.getInventoryItemAttrVals().isEmpty()){
				 CalendarDate scrapDate = null;
		         CalendarDate unScrapDate = null;
		         CalendarDate stolenDate = null;
		         CalendarDate unStolenDate = null;
		         String previousItemCondition = null;
		         for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem.getInventoryItemAttrVals()) {
		        	 if(AttributeConstants.SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
		        			InventoryScrapTransaction scrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
									.convertXMLToObject(inventoryItemAttrVal.getValue());
							scrapDate = scrap.getDateOfScrapOrUnscrap();
							previousItemCondition = scrap.getPreviousItemCondition();
		        		 }
		        		 if(AttributeConstants.UN_SCRAP_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
		        			 InventoryScrapTransaction unScrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
				                .convertXMLToObject(inventoryItemAttrVal.getValue());
		                     unScrapDate = unScrap.getDateOfScrapOrUnscrap();
		        		 }
		        		 if(scrapDate != null && unScrapDate != null){
		        			 if((inventoryItem.getDeliveryDate().isAfter(scrapDate) || inventoryItem.getDeliveryDate().equals(scrapDate)) &&
		        					 (inventoryItem.getDeliveryDate().isBefore(unScrapDate) || inventoryItem.getDeliveryDate().equals(unScrapDate))
		                				&& !scrapDate.equals(unScrapDate)){
		        				 addActionError("message.scrap.machineScrapped",new String[] { inventoryItem.getSerialNumber() });
								isValid = false;
								break;
		        			 } 
		        		 }

			        	 if(AttributeConstants.STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
			        			InventoryStolenTransaction stolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
										.convertXMLToObject(inventoryItemAttrVal.getValue());
								stolenDate = stolen.getDateOfStolenOrUnstolen();
								previousItemCondition = stolen.getPreviousItemCondition();
			        		 }
			        		 if(AttributeConstants.UN_STOLEN_COMMENTS.equals(inventoryItemAttrVal.getAttribute().getName())){
			        			 InventoryStolenTransaction unStolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
					                .convertXMLToObject(inventoryItemAttrVal.getValue());
			                     unStolenDate = unStolen.getDateOfStolenOrUnstolen();
			        		 }
			        		 if(stolenDate != null && unStolenDate != null){
			        			 if((inventoryItem.getDeliveryDate().isAfter(stolenDate) || inventoryItem.getDeliveryDate().equals(stolenDate)) &&
			        					 (inventoryItem.getDeliveryDate().isBefore(unStolenDate) || inventoryItem.getDeliveryDate().equals(unStolenDate))
			                				&& !stolenDate.equals(unStolenDate)){
			        				 addActionError("message.stole.machineStolen",new String[] { inventoryItem.getSerialNumber() });
									isValid = false;
									break;
			        			 } 
			        		 }
			         
		         }
		         if (scrapDate != null && unScrapDate == null){
	        		 if (inventoryItem.getDeliveryDate().isAfter(scrapDate) || inventoryItem.getDeliveryDate().equals(
									scrapDate)) {
						addActionError("message.scrap.machineScrapped",new String[] { inventoryItem.getSerialNumber() });
						isValid = false;
					}
	        		if(inventoryItem.getDeliveryDate().isBefore(scrapDate)){
	        			inventoryItem.setConditionType(new InventoryItemCondition(previousItemCondition));		        			
	        		}
				}
		         if (stolenDate != null && unStolenDate == null){
	        		 if (inventoryItem.getDeliveryDate().isAfter(stolenDate) || inventoryItem.getDeliveryDate().equals(
	        				 stolenDate)) {
						addActionError("message.stole.machineStolen",new String[] { inventoryItem.getSerialNumber() });
						isValid = false;
					}
	        		if(inventoryItem.getDeliveryDate().isBefore(stolenDate)){
	        			inventoryItem.setConditionType(new InventoryItemCondition(previousItemCondition));		        			
	        		}
				}
			}
		}
		return isValid;
	}

    public List<MultipleInventoryAttributesMapper> getInventoryItemMappings() {
        return inventoryItemMappings;
    }

    public void setInventoryItemMappings(List<MultipleInventoryAttributesMapper> inventoryItemMappings) {
        this.inventoryItemMappings = inventoryItemMappings;
    }

    public WarrantyStatus getStatus() {
        return status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }

    public List<RegisteredPolicy> getAvailablePolicies() {
        return availablePolicies;
    }

    public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
        this.availablePolicies = availablePolicies;
    }

    public Map<String, String> getListIfPreviousOwner() {
        return listIfPreviousOwner;
    }

    public void setListIfPreviousOwner(Map<String, String> listIfPreviousOwner) {
        this.listIfPreviousOwner = listIfPreviousOwner;
    }

    public List<CompetitorModel> getListOfCompetitorModels() {
        return listOfCompetitorModels;
    }

    public void setListOfCompetitorModels(List<CompetitorModel> listOfCompetitorModels) {
        this.listOfCompetitorModels = listOfCompetitorModels;
    }

    public List<CompetitorMake> getListOfCompetitorMakes() {
        return listOfCompetitorMakes;
    }

    public void setListOfCompetitorMakes(List<CompetitorMake> listOfCompetitorMakes) {
        this.listOfCompetitorMakes = listOfCompetitorMakes;
    }

    public List<CompetitionType> getListOfCompetitionTypes() {
        return listOfCompetitionTypes;
    }

    public void setListOfCompetitionTypes(List<CompetitionType> listOfCompetitionTypes) {
        this.listOfCompetitionTypes = listOfCompetitionTypes;
    }

    public List<Market> getListOfMarkets() {
        return listOfMarkets;
    }

    public void setListOfMarkets(List<Market> listOfMarkets) {
        this.listOfMarkets = listOfMarkets;
    }

    public List<String> getListOfAdditionalInfo() {
        return listOfAdditionalInfo;
    }

    public void setListOfAdditionalInfo(List<String> listOfAdditionalInfo) {
        this.listOfAdditionalInfo = listOfAdditionalInfo;
    }

    public ItemGroupService getItemGroupService() {
        return itemGroupService;
    }

    @Required
    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public List<TransactionType> getListOfTransactionTypes() {
        return listOfTransactionTypes;
    }

    public void setListOfTransactionTypes(List<TransactionType> listOfTransactionTypes) {
        this.listOfTransactionTypes = listOfTransactionTypes;
    }

    public WarrantyTaskInstanceService getWarrantyTaskInstanceService() {
        return warrantyTaskInstanceService;
    }

    public void setWarrantyTaskInstanceService(WarrantyTaskInstanceService warrantyTaskInstanceService) {
        this.warrantyTaskInstanceService = warrantyTaskInstanceService;
    }

    public WarrantyService getWarrantyService() {
        return warrantyService;
    }

    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    public PolicyService getPolicyService() {
        return policyService;
    }

    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public WarrantyTaskInstance getWarrantyTaskInstance() {
        return warrantyTaskInstance;
    }

    public void setWarrantyTaskInstance(WarrantyTaskInstance warrantyTaskInstance) {
        this.warrantyTaskInstance = warrantyTaskInstance;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public int getInventoryItemIndex() {
        return inventoryItemIndex;
    }

    public void setInventoryItemIndex(int inventoryItemIndex) {
        this.inventoryItemIndex = inventoryItemIndex;
    }

    public Party getCustomer() {
        return customer;
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

    protected WarrantyStatus warrantyStatusForFolder(String action) {
        if (WarrantyStatus.SUBMITTED.getStatus().equals(action)) {
            return WarrantyStatus.SUBMITTED;
        } else if (WarrantyStatus.FORWARDED.getStatus().equals(action)) {
            return WarrantyStatus.FORWARDED;
        } else if (WarrantyStatus.REPLIED.getStatus().equals(action)) {
            return WarrantyStatus.REPLIED;
        } else if (WarrantyStatus.REJECTED.getStatus().equals(action)) {
            return WarrantyStatus.REJECTED;
        } else if (WarrantyStatus.RESUBMITTED.getStatus().equals(action)) {
            return WarrantyStatus.RESUBMITTED;
        } else if (WarrantyStatus.DELETED.getStatus().equals(action)) {
            return WarrantyStatus.DELETED;
        }
        return null;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public List<Document> getCommonAttachments() {
		return commonAttachments;
	}

	public void setCommonAttachments(List<Document> commonAttachments) {
		this.commonAttachments = commonAttachments;
	}
    public String showAttachments() {
		return SUCCESS;
	}

	public String getJSONifiedCommonAttachmentList() {
            prepareCommonAttachments();
	
                try {
			return getDocumentListJSON(commonAttachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
    public String getJSONifiedAttachmentList() {
    	List<List<Document>> listOfDocList = new ArrayList<List<Document>>();
        List<Document> commonList = new ArrayList<Document>();
        List<Document> deleteList = new ArrayList<Document>();
        for (InventoryItem invItem : getWarrantyTaskInstance().getForItems()) {
            prepareInventoryItem(invItem);
            listOfDocList.add(invItem.getLatestWarranty().getAttachments());
        }
        int size = listOfDocList.size();
		if(size >1){
			for(Document atch1 : listOfDocList.get(0)) {
				if(listOfDocList.get(1).contains(atch1)) {
					commonList.add(atch1);
				}
			}
		}
		if(size >2) {
			for (int i = 2; i < listOfDocList.size(); i++) {
				for(Document cl: commonList){
					if(!listOfDocList.get(i).contains(cl)){
						deleteList.add(cl);
					}
		        }
			}
		}
		commonList.removeAll(deleteList);
		commonAttachments.addAll(commonList);		
        try {
			Warranty warranty = this.inventoryItemMappings.get(
					this.inventoryItemIndex).getInventoryItem()
					.getLatestWarranty();
			List<Document> attachments;
			if (warranty != null && warranty.getAttachments() != null) {
				this.inventoryItemMappings.get(this.inventoryItemIndex)
					.getAttachments().removeAll(warranty.getAttachments());
				
			}
			this.inventoryItemMappings.get(this.inventoryItemIndex)
				.getAttachments().addAll(warranty.getAttachments());
			attachments = this.inventoryItemMappings.get(this.inventoryItemIndex).getAttachments();
			attachments.removeAll(commonList);
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
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
			} not needed since mappings' attachments are set from warranties during prepare */
			attachments = this.inventoryItemMappings.get(inventoryItemMappingsIndex).getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getUnitDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
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

	protected void populateCustomerTypes() {    	
		if(transactionType.equals("ETR")){
			populateCustomerTypesForETR();
		}else{
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
    }
	
	protected void populateCustomerTypesForETR(){
		Map<Object, Object> keyValueOfCustomerTypes=new HashMap<Object, Object>();

		if (this.configParamService == null) {
			initDomainRepository();
		}
		keyValueOfCustomerTypes = this.configParamService
				.getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_ETR
						.getName());
		if (keyValueOfCustomerTypes != null
				&& !keyValueOfCustomerTypes.isEmpty()) {
			customerTypes.putAll(keyValueOfCustomerTypes);
		}
	}
	
	private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.configParamService = (ConfigParamService) beanLocator.lookupBean("configParamService");
    }
	
	private boolean validateCustomerType(String addressBookType){
		boolean isValid = true;
		
		if(addressBookType == null || "--Select--".equalsIgnoreCase(addressBookType)){
			addActionError("error.customerTypeNotSelected",
					new String[]{inventoryItem.getSerialNumber()});
			return isValid = false;
		}
		
		return isValid;
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
	
	

	protected void populateExistingOperatorType(Warranty warranty){
		if (addressBookService != null && warranty.getOperator() != null
				&& warranty.getOperator().getAddresses() != null
				&& warranty.getForDealer() != null) {
			List<AddressBookAddressMapping> addressBookAddressMappings = addressBookService
					.getAddressBookAddressMappingByOrganizationAndListOfAddresses(
							warranty.getOperator().getAddresses(), warranty
									.getForDealer());

			for (AddressBookAddressMapping addressBookAddressMapping : addressBookAddressMappings) {
				if(AddressBookType.SELF.getType().equalsIgnoreCase(addressBookAddressMapping
						.getAddressBook().getType().getType())){
					
					if(customerTypes.containsValue(AddressBookType.DEALER.getType())){
						this.addressBookType = AddressBookType.DEALER.getType();
					} else if(customerTypes.containsValue(AddressBookType.DEALERRENTAL.getType())){
						this.addressBookType = AddressBookType.DEALERRENTAL.getType();
					}
				}else{
				
					this.addressBookTypeForOperator = addressBookAddressMapping
							.getAddressBook().getType().getType();
				}
				
				break;
			}

		}
		// in case of National/Government Account
		if (this.addressBookTypeForOperator == null && addressBookService != null
				&& warranty.getOperator() != null
				&& warranty.getOperator().getAddresses() != null
				&& warranty.getForDealer() != null) {
			List<AddressBookAddressMapping> addressBookAddressMappings = addressBookService
					.getAddressBookAddressMappingByOrganizationAndListOfAddresses(
							warranty.getOperator().getAddresses(),
							organizationRepository.findByName("OEM"));

			for (AddressBookAddressMapping addressBookAddressMapping : addressBookAddressMappings) {
				if(AddressBookType.SELF.getType().equalsIgnoreCase(addressBookAddressMapping
						.getAddressBook().getType().getType())){
					
					if(customerTypes.containsValue(AddressBookType.DEALER.getType())){
						this.addressBookType = AddressBookType.DEALER.getType();
					} else if(customerTypes.containsValue(AddressBookType.DEALERRENTAL.getType())){
						this.addressBookTypeForOperator = AddressBookType.DEALERRENTAL.getType();
					}
				}else{
				
					this.addressBookTypeForOperator = addressBookAddressMapping
							.getAddressBook().getType().getType();
				}
				break;
			}
		}

	}
	
    public void computePriceForPolicies(MultipleInventoryAttributesMapper inventoryAttributesMapper) {
        List<PolicyDefinition> selectedPolicyDefs = new ArrayList<PolicyDefinition>(
                inventoryAttributesMapper.getSelectedPolicies().size());
        for (RegisteredPolicy registeredPolicy : inventoryAttributesMapper.getSelectedPolicies()) {
            selectedPolicyDefs.add(registeredPolicy
                    .getPolicyDefinition());
        }
        try {
            inventoryAttributesMapper
                    .setSelectedPolicies(createPolicies(
                            selectedPolicyDefs, inventoryAttributesMapper.getInventoryItem(), true));
        } catch (PolicyException e) {
            addActionError("error.registeringWarrantyForItem");
        }
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
	
    protected void validateMarketingInfo() {
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

    private void validateCustomerRepresentative() {
    	 if (!StringUtils.hasText(getMarketingInformation().getCustomerRepresentative())) {
 			addActionError("error.marketInfo.customerRepresentative");
 		}
		
	}

	private void validateTransactionTypeDetails(){
            if (getMarketingInformation().getTransactionType() == null) {
				addActionError("error.marketInfo.transactionType");
			}
            else if(!listOfTransactionTypes.contains(getMarketingInformation().getTransactionType())){
				addActionError("error.marketInfo.transactionType");
			}
            
    }
    private void validateSalesPerson(){
        if (!StringUtils.hasText(getMarketingInformation().getDealerRepresentative())) {
			addActionError("error.marketInfo.salesMan");
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

    public MarketingInformation getMarketingInformation() {
        return marketingInformation;
    }

    public void setMarketingInformation(MarketingInformation marketingInformation) {
        this.marketingInformation = marketingInformation;
    }

    public String getSelectedBusinessUnit() {
        return SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
    }

    public void deleteDraftWarranty(){
        List<InventoryItem> itemsForTask = new ArrayList<InventoryItem>();
        List<InventoryItem> inventoriesToBeDeleted = new ArrayList<InventoryItem>();
        for (MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
        	 InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
            inventoryItemMapping.getInventoryItem().getLatestWarranty().setStatus(WarrantyStatus.DELETED);
            WarrantyAudit latestAudit = new WarrantyAudit();
            latestAudit.setExternalComments("Deleted the draft DR");
            latestAudit.setStatus(WarrantyStatus.DRAFT_DELETE);
            inventoryItemMapping.getInventoryItem().getLatestWarranty().getWarrantyAudits().add(latestAudit);
            itemsForTask.add(inventoryItemMapping.getInventoryItem());
            inventoryItem.setDisclaimerInfo(null);
            inventoryItem.setWaiverDuringDr(null);
            inventoryItem.setIsDisclaimer(Boolean.FALSE);
			List<InventoryItem> inventoryItems = inventoryService.getPartsToBeDeleted(inventoryItem);			
			if (inventoryItems.size() > 0) {
				inventoriesToBeDeleted.addAll(inventoryItems);
			}  
        }
        try {
			if (!itemsForTask.isEmpty()) {
				
				warrantyService.removeInventoryAndWarranty(itemsForTask,inventoriesToBeDeleted);
                addActionMessage("message.success.warrantyETRProcess",itemsForTask.get(0).getLatestWarranty().getStatus());
            }
		} catch (PolicyException ex) {
			logger.error("Error deleting warranty for items" + itemsForTask,ex);
		}
    }

    private MarketService marketService;

    @Required
    public void setMarketService(MarketService marketService) {
        this.marketService = marketService;
    }

    /* This part of code is to fetch the market types and market application based on the selected market */
    private String marketId;

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String listMarketTypes() {
        List<Market> marketTypes = marketService.listAllMarketTypesForMarket(new Long(marketId));
        return generateAndWriteComboboxJson(marketTypes,"id","name");
    }

    public String listMarketApplication() {
        List<Market> applications = marketService.listAllApplicationsForMarketType(new Long(marketId));
        return generateAndWriteComboboxJson(applications,"id","name");
    }

    public String listMarketDescription() throws JSONException {
        JSONArray description = new JSONArray();
        try{
        Market application = marketService.findById(new Long(marketId));
        description.put(application.getDescription());
        }catch(NumberFormatException exception){
            description.put("");
        }
        jsonString = description.toString();
        return SUCCESS;
    }

    private PolicyDefinitionRepository policyDefinitionRepository;

    private PolicyDefinition policyDefinition;

    private Long policyId;

    @Required
    public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
        this.policyDefinitionRepository = policyDefinitionRepository;
    }

    public String getPolicyDetails(){
        policyDefinition = this.policyDefinitionRepository.findById(policyId);
        return SUCCESS;
    }

    public PolicyDefinition getPolicyDefinition() {
        return policyDefinition;
    }

    public void setPolicyDefinition(PolicyDefinition policyDefinition) {
        this.policyDefinition = policyDefinition;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public List<ListOfValues> getOems(String className) {
		return lovRepository.findAllActive(className);
	}
    
    private boolean isPolicyMandatory(){
		return configParamService.getBooleanValue(ConfigName.POLICY_MANDATORY.getName());
	}
    
    protected void validateInventoryItemMappingForPolicy() {
    	if(isPolicyMandatory()){
    		for (MultipleInventoryAttributesMapper inventoryItemMapping : getInventoryItemMappings()) {
    			InventoryItem inventoryItem = inventoryItemMapping.getInventoryItem();
    			//validateMajorComponents(inventoryItem);
    			List<RegisteredPolicy> selectedPolicies = inventoryItemMapping.getSelectedPolicies();
    			// Note: the single "&" is not a typo! We cant use && since we want
    			// *both* validations to happen,
    			// irrespective of each other.
    			if (selectedPolicies.isEmpty()) {
    				addActionError("error.noPolicySelected", inventoryItem.getSerialNumber());
    			}
    		}
    	}
    }

    public boolean fetchPriceForPolicyForCurrency(PolicyDefinition policyDefinition,
			InventoryItem inventoryItem) {
        for (PolicyFees registrationFees : policyDefinition.getPolicyFees()) {

          if( ( inventoryItem.getLatestWarranty()!=null &&
                  InvTransationType.DR.getTransactionType().equalsIgnoreCase(inventoryItem.getLatestWarranty().getTransactionType().getTrnxTypeValue()))
                  && inventoryItem.getCurrentOwner().getPreferredCurrency().getCurrencyCode().
                  equalsIgnoreCase(registrationFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode())){
              return registrationFees.getPolicyFee().isZero();
          }else if(( inventoryItem.getLatestWarranty()!=null &&
                  InvTransationType.ETR.getTransactionType().equalsIgnoreCase(inventoryItem.getLatestWarranty().getTransactionType().getTrnxTypeValue()))
                  && registrationFees.getIsTransferable()
                  && inventoryItem.getCurrentOwner().getPreferredCurrency().getCurrencyCode().
                  equalsIgnoreCase(registrationFees.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode())){
              return registrationFees.getPolicyFee().isZero();
          }
        }
		return false;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	public String listAllStockSerialsStartingWith() {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getSelectedBusinessUnit());
        return findAllStockSerialsStartingWith(getSearchPrefix());
    }
	
	public String listAllRetailSerialsStartingWith() {
        return findAllRetailSerialsStartingWith(getSearchPrefix());
    }
	
	public String findAllStockSerialsStartingWith(String prefix)
	{
		List<InventoryItem> items = new ArrayList<InventoryItem>();
        List<InventoryItem> uniqueItems = new ArrayList<InventoryItem>();
		if (StringUtils.hasText(prefix)) {
			Long dealerId = (isLoggedInUserAnAdmin()||isLoggedInUserAParentDealer())? dealer.getId().longValue(): getLoggedInUser().getBelongsToOrganization().getId().longValue();
			if (allowWROnOthersStock()) {
				dealerId = null;
            }

            List<Long> dealers = orgService.getChildOrganizationsIds(dealerId);
            //Add the old code , whatever dealer id we get
            dealers.add(dealerId);
            //instead of passing single dealerID we are passing multiple ids
			//items = inventoryService.findAllInventoriesByTypeStartingWith(prefix.toUpperCase(), dealerId,STOCK, 0, 10);
            items = inventoryService.findAllInventoriesByTypeForChildDealersTooStartingWith(prefix.toUpperCase(), dealers,STOCK, 0, 10);

            for(InventoryItem item : items){
                if(dealers.contains(item.getCurrentOwner().getId()) && !item.getPendingWarranty())
                {
                    uniqueItems.add(item);
                }
            }
            
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
		return generateAndWriteComboboxJsonForSerialNumber(uniqueItems,"id","serialNumber");
	}
	
	public String findAllRetailSerialsStartingWith(String prefix)
	{
		List<InventoryItem> items = new ArrayList<InventoryItem>();
		if (StringUtils.hasText(prefix)) {
			Long dealerId = (isLoggedInUserAnAdmin()||isLoggedInUserAParentDealer())? dealer.getId().longValue(): getLoggedInUser().getBelongsToOrganization().getId().longValue();
            List<Long> dealers = new ArrayList<Long>();
            //Add the old code , whatever dealer id we get
            dealers.add(dealerId);
            //Plus add the child dealers. Since if he has then he can file dr for them too
            dealers.addAll(orgService.getChildOrganizationsIds(dealerId));
           /* for(Organization org : getLoggedInUser().getBelongsToOrganization().getChildOrgs()){
                dealers.add(org.getId());
            }*/
			items =
                    inventoryService.findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(prefix.toUpperCase(),dealers, dealerId,RETAIL, 0, 10);
            
        } else {
            return generateAndWriteEmptyComboboxJson();
        }
		return generateAndWriteComboboxJsonForSerialNumber(items,"id","serialNumber");
	}
	
	//changes
	
	public String getMarketApplicationForMarketType() throws JSONException {
		listOfMarketApplications =this.marketService.listAllApplicationsForMarketType(this.getMarketType().getId());
		return SUCCESS;
	}

	public ServiceProvider getInstallingDealer() {
		return installingDealer;
	}

	public void setInstallingDealer(ServiceProvider installingDealer) {
		this.installingDealer = installingDealer;
	}

	public Customer getOperator() {
		return operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

	public AddressForTransfer getOperatorAddressForTransfer() {
		return operatorAddressForTransfer;
	}

	public void setOperatorAddressForTransfer(AddressForTransfer operatorAddressForTransfer) {
		this.operatorAddressForTransfer = operatorAddressForTransfer;
	}

	public String getAddressBookTypeForOperator() {
		return addressBookTypeForOperator;
	}

	public void setAddressBookTypeForOperator(String addressBookTypeForOperator) {
		this.addressBookTypeForOperator = addressBookTypeForOperator;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
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

	public void setMarketingInfo(
			Map<AdditionalMarketingInfo, Map<String, List<AdditionalMarketingInfoOptions>>> marketingInfo) {
		this.marketingInfo = marketingInfo;
	}
	
	public ServiceProvider getDealer() {
		return dealer;
	}

	public void setDealer(ServiceProvider dealer) {
		this.dealer = dealer;
	}
		
	public boolean allowWROnOthersStock() {
		return configParamService.getBooleanValue(ConfigName.ALLOW_WNTY_REG_ON_OTHERS_STOCKS.getName());
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

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
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

	public List<Market> getListOfMarketApplications() {
		return listOfMarketApplications;
	}

	public void setListOfMarketApplications(List<Market> listOfMarketApplications) {
		this.listOfMarketApplications = listOfMarketApplications;
	}

	public Market getMarketType() {
		return marketType;
	}

	public void setMarketType(Market marketType) {
		this.marketType = marketType;
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
	
	public boolean isCustomerDetailsNeededForDR_Rental() {
		return this.getConfigParamService()
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className,
			Warranty warranty) {
		return warrantyService.getLovsForClass(className, warranty);
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
	
	public boolean noPolicyForDemoTruckWithMoreThan80Hours() {
		return isBuConfigAMER();
	}
	
	public boolean isAdditionalInformationDetailsApplicable() {
		return this.configParamService
				.getBooleanValue(ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}
	
	protected CalendarDate getCutOffDate(CalendarDate shipmentDate, PolicyDefinition policyDefinition) {
		Integer monthsCoveredFromShipment = policyDefinition.getCoverageTerms().getMonthsCoveredFromShipment();
		Integer monthsCoveredFromDelivery = policyDefinition.getCoverageTerms().getMonthsCoveredFromDelivery();
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}
}

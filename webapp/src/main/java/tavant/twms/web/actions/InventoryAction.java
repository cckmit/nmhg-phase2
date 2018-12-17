package tavant.twms.web.actions;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryComment;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCompositionComparator;
import tavant.twms.domain.inventory.InventoryItemOptionsComparator;
import tavant.twms.domain.inventory.InventoryItemSource;
import tavant.twms.domain.inventory.InventoryItemUtil;
import tavant.twms.domain.inventory.InventoryListCriteria;
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.inventory.PartGroup;
import tavant.twms.domain.inventory.RetailItemsService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.ExtendedWarrantyNotification;
import tavant.twms.domain.policy.PolicyAdminService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyAudit;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyCoverageRequest;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.domain.policy.WarrantyListCriteria;
import tavant.twms.domain.policy.WarrantyRepository;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.admin.warranty.InventoryTransactionDisplayObject;
import tavant.twms.web.common.DisplayImagePropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import tavant.twms.web.util.DocumentTransportUtils;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import java.text.SimpleDateFormat;

/**
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class InventoryAction extends SummaryTableAction implements Preparable {

        private InventoryItem inventoryItem;

        private InventoryService inventoryService;

        private Collection<Claim> previousClaimsForItem;

        private ClaimService claimService;
        
        private CatalogService catalogService;
        
        private InventoryItem installedOnItem;
        
        private WarrantyRepository warrantyRepository;
        
        private MSAService msaService;
        
        public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
        	this.warrantyRepository = warrantyRepository;
        }
        
		public InventoryItem getInstalledOnItem() {
			return installedOnItem;
		}

		public void setInstalledOnItem(InventoryItem installedOnItem) {
			this.installedOnItem = installedOnItem;
		}

		public boolean isPageReadOnly() {
    		return false;

    	}
        
        public boolean isPageReadOnlyAdditional() {
    		boolean isReadOnlyDealer = false;
    		Set<Role> roles = getLoggedInUser().getRoles();
    		for (Role role : roles) {
    			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
    				isReadOnlyDealer = true;
    				break;
    			}
    		}
    		return isReadOnlyDealer;
    	}

     
        private boolean isDifferentDealerAndOwner;

        private boolean isRetailingDealer = false;

        private boolean isInternalUser = false;

        private boolean isUserInventoryFullView = false;

        private List<RegisteredPolicy> registeredPolicies = new ArrayList<RegisteredPolicy>();

        private PolicyService policyService;

        private List<CampaignNotification> campaignNotifications = new ArrayList<CampaignNotification>();

        private boolean draft;

        private final Logger logger = Logger.getLogger(this.getClass());

        private String scrapComments;
        
        private String stolenComments;

        private Organization retailingDealer;

        private boolean canViewOwner;

        private ServiceProvider serviceDealer;

        private String scrapReason;
        
        private String stolenReason;

        private boolean loggedinDealerOwner;

        private boolean enterpriseDealer = false;

        private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
        
        private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;

        private WarrantyService warrantyService;

        private PolicyAdminService policyAdminService;

        private Long policyId;
        
        private RegisteredPolicy policy;
        private PolicyDefinition policyDefinition;

        private List<RegisteredPolicyAudit> policyAudits;

        private final String terminatedStatus = RegisteredPolicyStatusType.TERMINATED
                        .getStatus();

        private final String activeStatus = RegisteredPolicyStatusType.ACTIVE
                        .getStatus();

        private RetailItemsService retailItemsService;

        private List<RegisteredPolicy> addedPolicies = new ArrayList<RegisteredPolicy>();

        private List<RegisteredPolicy> availablePolicies = new ArrayList<RegisteredPolicy>();
        
        private List<PolicyDefinition> invisiblePolicies = new ArrayList<PolicyDefinition>();
        
		private boolean editable;
        private int originalPoliciesSize;

        private ConfigParamService configParamService;

        private String inventoryItemId;

        private ConfigParam configParam;

        private List<Campaign> campaign;

        private CampaignService campaignService;

        private List<Document> attachments = new ArrayList<Document>();
        
        private List<Document> internalTruckDocs = new ArrayList<Document>();
        
        public final static String ATTACHMENTS = "attachments";


		public List<Document> getInternalTruckDocs() {
			return internalTruckDocs;
		}

		public void setInternalTruckDocs(List<Document> internalTruckDocs) {
			this.internalTruckDocs = internalTruckDocs;
		}


		private Boolean isEquipHistoryPage = false;

        private CustomReportService customReportService;

        private WarrantyCoverageRequestService warrantyCoverageRequestService;
        private WarrantyCoverageRequest warrantyCoverageRequest;
        private InventoryItemUtil inventoryItemUtil;
        private CalendarDate scrapDate;
        private CalendarDate stolenDate;
        private InventoryItem partOf;
        private Map<String,String> inventoryAttributes = new HashMap<String,String>();
        
        private Collection<InventoryItemComposition> inventoryItemCompositon;
		private Collection<InventoryItemAdditionalComponents> invItemAdditionalComponents;
		private Collection<Option> inventoryItemOptions; 
        private Collection<BrandItem> itemBrands; 
        private Collection<String> dealerBrands;
		private Collection<String> dealerMarketingGroupCodes;
		private Collection<PartGroup> inventoryPartGroups;
		private String orderReceivedDate;
		private String ctsDate;
		private static final String DATE_FORMAT = "dd/MM/yyyy";
		private InventoryComment inventoryComment;
		private List<ListOfValues> documentTypes = new ArrayList<ListOfValues>();
		private Boolean isAttachmentEditable = false;
		
		public Boolean getIsAttachmentEditable() {
			return isAttachmentEditable;
		}

		public void setIsAttachmentEditable(Boolean isAttachmentEditable) {
			this.isAttachmentEditable = isAttachmentEditable;
		}

		private BuSettingsService buSettingsService;

	    public BuSettingsService getBuSettingsService() {
	        return buSettingsService;
	    }

	    public void setBuSettingsService(BuSettingsService buSettingsService) {
	        this.buSettingsService = buSettingsService;
	    }
		
		public List<ListOfValues> getDocumentTypes() {
			return documentTypes;
		}

		public void setDocumentTypes(List<ListOfValues> documentTypes) {
			this.documentTypes = documentTypes;
		}
		
		public InventoryComment getInventoryComment() {
			return inventoryComment;
		}

		public void setInventoryComment(InventoryComment inventoryComment) {
			this.inventoryComment = inventoryComment;
		}

		public Collection<String> getDealerBrands() {
				return dealerBrands;
		}
		public void setDealerBrands(Collection<String> dealerBrands) {
				this.dealerBrands = dealerBrands;
		}
    	public Collection<String> getDealerMarketingGroupCodes() {
			return dealerMarketingGroupCodes;
		}

		public void setDealerMarketingGroupCodes(
				Collection<String> dealerMarketingGroupCodes) {
			this.dealerMarketingGroupCodes = dealerMarketingGroupCodes;
		}

		public Collection<BrandItem> getItemBrands() {
			return itemBrands;
		}

		public void setItemBrands(Collection<BrandItem> itemBrands) {
			this.itemBrands = itemBrands;
		}
        
		public Collection<Option> getInventoryItemOptions() {
			List<Option> list;
			if (inventoryItemOptions instanceof List)
			{
			  list = (List<Option>)inventoryItemOptions;
			}
			else
			{
			  list = new ArrayList<Option>(inventoryItemOptions);
			}
    		Collections.sort(list, new InventoryItemOptionsComparator());
    		return list;
		}

		public void setInventoryItemOptions(Collection<Option> inventoryItemOptions) {
			this.inventoryItemOptions = inventoryItemOptions;
		}

		public Collection<InventoryItemComposition> getInventoryItemCompositon() {
			List<InventoryItemComposition> list;
			if (inventoryItemCompositon instanceof List)
			{
			  list = (List<InventoryItemComposition>)inventoryItemCompositon;
			}
			else
			{
			  list = new ArrayList<InventoryItemComposition>(inventoryItemCompositon);
			}
    		Collections.sort(list, new InventoryItemCompositionComparator());
    		return list;
    	}

    	public void setInventoryItemCompositon(
    			Collection<InventoryItemComposition> inventoryItemCompositon) {
    		this.inventoryItemCompositon = inventoryItemCompositon;
    	}

		public InventoryItem getPartOf() {
			return partOf;
		}

		public void setPartOf(InventoryItem partOf) {
			this.partOf = partOf;
		}

		@Override
        public String execute() throws Exception {
                super.execute();
                if (draft)
                        return INPUT;
                return SUCCESS;

        }

        public String displayFleetManagementActions() {
                return SUCCESS;
        }

        public String forwardToScrap() {
        	if(isStolen()){
        		addActionError("error.stolenInventory.alreadyMarked");
        		return INPUT;
        	}
        	if(isScrap()){
        		addActionError("error.scrapInventory.alreadyMarked");
        		return INPUT;
        	}
                return SUCCESS;
        }

        public String forwardToUnScrap() {
        	if(!isScrap()){
        		addActionError("error.UnScrapInventory.alreadyMarked");
        		return INPUT;
        	}
            return SUCCESS;
        }
        
        public String forwardToStolen() {
        	if(isStolen()){
        		addActionError("error.stolenInventory.alreadyMarked");
        		return INPUT;
        	}
        	if(isScrap()){
        		addActionError("error.scrapInventory.alreadyMarked");
        		return INPUT;
        	}
            return SUCCESS;
        }
        
        public String forwardToNotStolen(){
        	if(!isStolen()){
        		addActionError("error.notStolenInventory.alreadyMarked");
        		return INPUT;
        	}
        	return SUCCESS;
        }
        
        private List<InventoryTransactionDisplayObject> transactionsToBeDisplayed = new ArrayList<InventoryTransactionDisplayObject>(
                        5);


        @Override
        protected PageResult<?> getBody() {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Fetching inventory list for " + this.folderName);
            }

            Organization loggedInUserOrg = getLoggedInUser()
                    .getBelongsToOrganization();
            if (loggedInUserOrg == null) {
                addFieldError("emtpyOrg",
                        "The logged in user is not a organization.");
                return null;
            }


            return this.inventoryService
                    .findAllInventoryItemsOfTypeForDealer(createCrieria(loggedInUserOrg));
        }

        @Override
                protected List<SummaryTableColumn> getHeader() {
                        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();

                        if (isStockInventory()) {                               
                                tableHeadData.add(new SummaryTableColumn("", "imageCol", 3, IMAGE,
                                                Constants.WARNING_IMG_FOR_STOCK_INV, false, false, false,
                                                true));
                                incrementDefaultHeaderSize();
                        }

                        tableHeadData.add(new SummaryTableColumn(
                                        "label.inboxView.serialNumber", "serialNumber", 8,
                                        "string", "serialNumber", true, false, false, false));
                        incrementDefaultHeaderSize();
                        tableHeadData.add(new SummaryTableColumn(
                                        "columnTitle.inventoryAction.hidden", "id", 0, "string",
                                        "id", false, true, true, false));
                        
                        
                        
                        
                        if (inboxViewFields())
                        addInboxViewFieldsToHeader(tableHeadData, LABEL_COLUMN_WIDTH);
                else {
                                tableHeadData.add(new SummaryTableColumn(
                                                "columnTitle.inventoryAction.product_type",
                                                "ofType.product.groupCode", 8, "String", SummaryTableColumnOptions.NO_SORT));
                                tableHeadData.add(new SummaryTableColumn(
                                                "columnTitle.inventoryAction.item_model", "ofType.model.itemGroupDescription",
                                                16, "String", SummaryTableColumnOptions.NO_SORT));
                                /*tableHeadData.add(new SummaryTableColumn(
                                                "label.inboxView.itemNumber", "ofType.number", 8,
                                                "Number", SummaryTableColumnOptions.NO_SORT)); */
                                tableHeadData.add(new SummaryTableColumn(
                                                "label.common.seriesDescription", "ofType.product.itemGroupDescription", 10,
                                                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));                                 
                                if (isRetailInventory()) {
                                	tableHeadData.add(new SummaryTableColumn(
                                                    "label.common.owningDealer", "currentOwner.name", 7,
                                                    "String", SummaryTableColumnOptions.NO_SORT));
                                	tableHeadData.add(new SummaryTableColumn(
                                                    "label.warrantyAdmin.customerType", "latestWarranty.customerType", 7,
                                                    "String"));
                                	tableHeadData.add(new SummaryTableColumn(
                                                    "columnTitle.inventoryAction.delivery_date", "deliveryDate", 7,
                                                    "date"));
                                	tableHeadData.add(new SummaryTableColumn(
                                                    "Submitted Date", "latestWarranty.filedDate", 7,
                                                    "date"));
                                	tableHeadData.add(new SummaryTableColumn(
                                                    "label.dealerUser.salesPerson", "latestWarranty.marketingInformation.dealerRepresentative", 5,
                                                    "date", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
                                	
                                	tableHeadData.add(new SummaryTableColumn(
                                            "CustomerRepresentative", "latestWarranty.marketingInformation.customerRepresentative", 5,
                                            "date", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
                                }
                                if (isStockInventory()) {
                                	    tableHeadData.add(new SummaryTableColumn(
                                                    "label.common.owningDealer", "currentOwner.name", 10,
                                                    "String", SummaryTableColumnOptions.NO_SORT));
                                		tableHeadData.add(new SummaryTableColumn(
                                                    "label.transactionType", "latestWarranty.transactionType.trnxTypeKey", 10,
                                                    "String", SummaryTableColumnOptions.NO_SORT));
                                		tableHeadData.add(new SummaryTableColumn(
                                                    "columnTitle.common.status", "latestWarranty.status.status", 10,
                                                    "String", SummaryTableColumnOptions.NO_SORT));
                                        tableHeadData.add(new SummaryTableColumn(
                                                        "columnTitle.inventoryAction.shipment_date",
                                                        "shipmentDate", 10, "date"));
                                        tableHeadData.add(new SummaryTableColumn(
                                                        "label.common.machineAge",
                                                        "machineAge", 6, "string",
                                                        SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
                                } else {
                                        tableHeadData.add(new SummaryTableColumn(
                                                        "columnTitle.common.warrantyStartDate",
                                                        "wntyStartDate", 8, "date"));
                                        tableHeadData.add(new SummaryTableColumn(
                                                        "columnTitle.common.warrantyEndDate", "wntyEndDate",
                                                        8, "date"));
                                        tableHeadData.add(new SummaryTableColumn(
                                                        "label.inboxView.endCustName", "latestBuyer.name",
                                                        8, "String", SummaryTableColumnOptions.NO_SORT));
                                }
                    }
                        return tableHeadData;
        }

        public boolean isStockInventory() {
        
                if (this.inventoryItem == null) {
                        return getFolderName().equals("STOCK")|| getFolderName().equals("VINTAGE_STOCK");
                } else {
                        return this.inventoryItem.getType().getType().equals("STOCK");
                }
        }

        public boolean isIri_StockInventory() {
        	if (this.inventoryItem == null) {
                return getFolderName().equals("OEM_STOCK");
        	}else{ 
        		return this.inventoryItem.getType().getType().equals("OEM_STOCK");
        	}
    }
        
        public boolean isRetailInventory() {
        	if (this.inventoryItem == null) {
        		return getFolderName().equals("RETAIL");
        	}
        	else {
        		return this.inventoryItem.getType().getType().equals(
        				InventoryType.RETAIL.getType());
        	}
        }
        
    private InventoryListCriteria createCrieria(Organization loggedInUserOrg) {
                   InventoryListCriteria criteria = new InventoryListCriteria();
                   if(this.folderName.equals("VINTAGE_STOCK")){
                	   criteria.setType(InventoryType.STOCK);
                	   criteria.setVintageStockShipmentDate(getVintageStockShipmentDate());
                	   criteria.setVintageStock(true);
                   }else{
                   criteria.setType(new InventoryType(this.folderName));
                	   criteria.setVintageStock(false);
                	   if(displayVintageStockInbox()){
                		   criteria.setVintageStockShipmentDate(getVintageStockShipmentDate());
                	   }
                   }
                   criteria.setLoggedInUserOrg(loggedInUserOrg);
                   criteria.setDraft(this.draft);
                   criteria.setPageSpecification(new PageSpecification(this.page - 1,
                                   this.pageSize));
                   if (this.logger.isInfoEnabled()) {
                           this.logger.info("Folder Name : " + this.folderName);
                   }
                   addFilterCriteria(criteria);
                   addSortCriteria(criteria);
                   if (this.logger.isInfoEnabled()) {
                           this.logger.info("page size " + this.pageSize
                                           + " page to be fetched " + this.page);
                   }
                   return criteria;
    }

        @SuppressWarnings("unused")
        private WarrantyListCriteria createWarrantyCriteria(ServiceProvider dealer) {
                WarrantyListCriteria criteria = new WarrantyListCriteria();
                criteria.setType(new WarrantyType(this.folderName));
                criteria.setDealer(dealer);
                criteria.setPageSpecification(new PageSpecification(this.page - 1,
                                this.pageSize));
                addFilterCriteria(criteria);
                addSortCriteria(criteria);
                return criteria;
        }

        private void addSortCriteria(InventoryListCriteria criteria) {
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

        private void addFilterCriteria(InventoryListCriteria criteria) {
                for (Iterator<String> iter = this.filters.keySet().iterator(); iter
                                .hasNext();) {
                        String filterName = iter.next();
                        String filterValue = this.filters.get(filterName).toUpperCase();
                        if(filterName.contains("itemGroupDescription")){
                        	filterName=filterName.replace("itemGroupDescription","description");
                        }
                        if (this.logger.isInfoEnabled()) {
                                this.logger.info("Adding filter criteria " + filterName + " : "
                                                + filterValue);
                        }
                        criteria.addFilterCriteria(filterName, filterValue);
                }
        }

        private void addFilterCriteria(WarrantyListCriteria criteria) {
                for (Iterator<String> iter = this.filters.keySet().iterator(); iter
                                .hasNext();) {
                        String filterName = iter.next();
                        String filterValue = this.filters.get(filterName);
                        if (this.logger.isInfoEnabled()) {
                                this.logger.info("Adding filter criteria " + filterName + " : "
                                                + filterValue);
                        }
                        criteria.addFilterCriteria(filterName, filterValue);
                }
        }

        public void prepare() throws Exception {
                if (inventoryItemId != null) {                  
                        if (this.logger.isInfoEnabled()) {
                                this.logger
                                                .info("The serial number of inventory item to be viewed is: "
                                                                + id);
                        }
                        try {
                                if (this.inventoryItem == null) {
                                        this.inventoryItem = this.inventoryService
                                                        .findSerializedItem(inventoryItemId);                                   
                                }
                                if (!this.inventoryItem.getTransactionHistory().isEmpty()) {
                                        Collections
                                                        .sort(this.inventoryItem.getTransactionHistory());
                                }
                                populateInventoryAttributes();
                                setServicingDealer();
                                setRetailedDealer();
                                canViewOwnerInfo();
                                if (this.getRetailingDealer() != null) {
                                        if (this.getRetailingDealer().getId().longValue() != this.inventoryItem
                                                        .getOwnedBy().getId().longValue()) {
                                                this.isDifferentDealerAndOwner = true;
                                        }
                                } else {
                                        this.isDifferentDealerAndOwner = true;
                                }
                                this.isRetailingDealer = inventoryItemUtil.isRetailedDealer();
                                this.isInternalUser = inventoryItemUtil.isInternalUser();
                                this.isUserInventoryFullView = inventoryItemUtil.isInventoryFullView(this.inventoryItem);
                                this.previousClaimsForItem = inventoryItemUtil.getClaimsToBeViewed(inventoryItem);
                                
                                //Added for major component TKTSA-1251                                
                                this.partOf = this.inventoryService.findInventoryItemForMajorComponent(inventoryItem.getId());

                                this.registeredPolicies = this.policyService
                                                .findRegisteredPolicies(this.inventoryItem);
                               // this.campaignNotifications = this.campaignService.findNotificationsForItem(this.inventoryItem);
                                this.campaignNotifications = this.inventoryItem.getCampaignNotifications();

				if (!(InventoryType.STOCK.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()) || InventoryType.OEM_STOCK
						.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()))) {
					initRegisteredPolicies();
					this.originalPoliciesSize = this.registeredPolicies.size();
					//Fix for SLMSPROD-1683
					if (getLoggedInUser().hasRole(Role.ADMIN) || getLoggedInUser().hasRole(Role.INVENTORY_ADMIN)){
						initAvailablePolicies();
						initInvisiblePolicies();
					}
				}
				

                                setTransactionsForLoggedInUser();
                                if (isLoggedInUserADealer()) {
                                        if (this.inventoryItem.getCurrentOwner() != null
                                                        && this.inventoryItem.getCurrentOwner().getId()
                                                                        .longValue() == getLoggedInUsersDealership()
                                                                        .getId().longValue()) {
                                                this.loggedinDealerOwner = true;
                                        }
                                }
                        } catch (ItemNotFoundException e) {

                        } catch (PolicyException e) {
                                // Need to figure out if the flow needs to be stopped for a
                                // PolicyException.
                                // Logging an error for now and returning success.
                                this.logger
                                                .error(
                                                                "Policy Exception: findAllApplicablePolicies for the Item",
                                                                e);

                        }
                }
            enterpriseDealer = isLoggedInUserAnEnterpriseDealer();
        }
        
        private void populateInventoryAttributes(){
                List<InventoryItemAttributeValue> attributes = this.inventoryItem.getInventoryItemAttrVals();
                if(attributes != null)
                for(InventoryItemAttributeValue attribute:attributes){
                        if(attributes!= null){
                                getInventoryAttributes().put(attribute.getAttribute().getName(), attribute.getValue());
                        }
                }
        }

        public String previewDetailView() throws IOException, JSONException {
                // Id here is the serail NUmber of item not the item id
                WarrantyCoverageRequest warrantyCoverageRequest = null;
                if (getId() != null) {
                        if (this.logger.isInfoEnabled()) {
                                this.logger
                                                .info("The serial number of inventory item to be viewed is: "
                                                                + getId());
                        }
                        try {
                                if (this.inventoryItem == null) {
                                        this.inventoryItem = this.inventoryService
                                                        .findSerializedItem(getId());                                   
                                }
                                populateInventoryAttributes();
                                warrantyCoverageRequest = this.warrantyCoverageRequestService
                                                .findByInventoryItemId(Long.valueOf(getId()));
                                if (warrantyCoverageRequest != null
                                                && warrantyCoverageRequest.getStatus()
                                                                .equalsIgnoreCase("SUBMITTED")) {
                                        addActionWarning("message.manageExtensionForApproval.preview");
                                }
                                if (!this.inventoryItem.getTransactionHistory().isEmpty()) {
                                        Collections
                                                        .sort(this.inventoryItem.getTransactionHistory());
                                }
                                setServicingDealer();
                                setRetailedDealer();
                                Organization retailingDealer = getRetailingDealer();
                                if(retailingDealer!=null && retailingDealer instanceof Dealership){
                                	Dealership dealer = (Dealership)retailingDealer;
                                	dealerBrands=new ArrayList<String>();
                                	dealerBrands.add(dealer.getBrand());
                                	dealerMarketingGroupCodes=new ArrayList<String>();
                                    dealerMarketingGroupCodes.add(dealer.getMarketingGroup());
                                }
                                canViewOwnerInfo();
								populateDocumentTypes();
								this.setIsAttachmentEditable(checkAttachmentsEditable());
                                this.isDifferentDealerAndOwner = inventoryItemUtil.isDifferentDealerAndOwner(this.inventoryItem);
                                this.isRetailingDealer = inventoryItemUtil.isRetailedDealer();
                                this.isInternalUser = inventoryItemUtil.isInternalUser();
                                this.isUserInventoryFullView = inventoryItemUtil.isInventoryFullView(this.inventoryItem);
                                this.previousClaimsForItem = inventoryItemUtil.getClaimsToBeViewed(inventoryItem);
                                this.registeredPolicies = this.policyService
                                                .findRegisteredPolicies(this.inventoryItem);
                                if (!(InventoryType.STOCK.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()) || InventoryType.OEM_STOCK
                						.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()))) {
                                        initRegisteredPolicies();
                                        this.originalPoliciesSize = this.registeredPolicies.size();                                      
                                        if(getLoggedInUser().hasRole(Role.ADMIN) || getLoggedInUser().hasRole(Role.INVENTORY_ADMIN))
                                        initAvailablePolicies();
                                        initInvisiblePolicies();
                                }
                                
                                if(inventoryItem.isInStock() && (isInternalUser || isRetailingDealer)) {
                                	List<ExtendedWarrantyNotification> extnWrntyList = warrantyService.findAllStagedExtnWntyPurchaseNotificationForInv(inventoryItem);
                                	if(extnWrntyList != null && !extnWrntyList.isEmpty()) {
                                		addActionMessage("message.inventory.extenededWntyNotification");
                                	}
                                }

                                this.inventoryItemCompositon = this.inventoryItem
        						.getComposedOf();
                                this.invItemAdditionalComponents = this.inventoryItem.getAdditionalComponents();
                                this.inventoryItemOptions=this.inventoryItem.getOptions();
                                this.inventoryPartGroups=this.inventoryItem.getPartGroups();
                                if(inventoryItem.getOrderReceivedDate()!=null){
                                	   if(!isBuConfigAMER())
                                		   this.orderReceivedDate = new SimpleDateFormat(DATE_FORMAT).format(inventoryItem.getOrderReceivedDate()).toString();
                                	   else
                                          this.orderReceivedDate = new SimpleDateFormat(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN).format(inventoryItem.getOrderReceivedDate()).toString();
                                }
                                if(inventoryItem.getActualCtsDate()!=null){                               	
                                    if(!isBuConfigAMER())
                                        this.ctsDate = new SimpleDateFormat(DATE_FORMAT).format(inventoryItem.getActualCtsDate()).toString();
                                    else
                                    	 this.ctsDate = new SimpleDateFormat(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN).format(inventoryItem.getActualCtsDate()).toString();
                                    
                                    
                                }
                                if(this.inventoryItem.getOfType()!=null){
                                  this.itemBrands=catalogService.fetchItemBrands(this.inventoryItem.getOfType());
                                }
                                setTransactionsForLoggedInUser();
                                if (isLoggedInUserADealer()) {
                                        if (this.inventoryItem.getCurrentOwner() != null
                                                        && this.inventoryItem.getCurrentOwner().getId()
                                                                        .longValue() == getLoggedInUsersDealership()
                                                                        .getId().longValue()) {
                                                this.loggedinDealerOwner = true;
                                        }
                                }
                                if (isEquipHistoryPage) {
                                        campaignNotificationWarning();
                                        //this.campaignNotifications = campaignService.findNotificationsForItem(this.inventoryItem);
                                        this.campaignNotifications = this.inventoryItem.getCampaignNotifications();
                                }
                                if(this.inventoryItem!=null && this.inventoryItem.getOwnedBy()!=null && this.inventoryItem.getOwnedBy().getAddress()!=null){
                                	 if(this.inventoryItem.getOwnedBy().getAddress().getCountyCodeWithName()==null && this.inventoryItem.getOwnedBy().getAddress().getState()!=null && this.inventoryItem.getOwnedBy().getAddress().getCounty()!=null){
                             			String countyName = msaService.findCountyNameByStateAndCode(this.inventoryItem.getOwnedBy().getAddress().getState(),this.inventoryItem.getOwnedBy().getAddress().getCounty());
                                     	this.inventoryItem.getOwnedBy().getAddress().setCountyCodeWithName(this.inventoryItem.getOwnedBy().getAddress().getCounty()+"-"+countyName);
                             		}	
                                }
                                return SUCCESS;
                        } catch (ItemNotFoundException e) {
                                return displayFieldError("invalidserialnumber",
                                                "The serial number is invalid");
                        } catch (PolicyException e) {
                                // Need to figure out if the flow needs to be stopped for a
                                // PolicyException.
                                // Logging an error for now and returning success.
                                this.logger.error("Policy Exception: findAllApplicablePolicies for the Item",e);
                                return SUCCESS;
                        }
                }
                return displayFieldError("emptyserialnumber",
                                "The serial number cannot be empty");
        }
        
	public String showMajorComponentDetail() throws IOException, JSONException {

		try {
			
			this.inventoryItem = this.inventoryService.findSerializedItem(getId());
			
			//showing warning message if pending request for Reduced Warranty Coverage is there on major component
			warrantyCoverageRequest = this.warrantyCoverageRequestService
					.findByInventoryItemId(Long.valueOf(getId()));
			if (warrantyCoverageRequest != null
					&& warrantyCoverageRequest.getStatus().equalsIgnoreCase(
							"SUBMITTED")) {
				addActionWarning("message.manageExtensionForApproval.preview");
			}
			
			this.previousClaimsForItem = inventoryItemUtil.getClaimsToBeViewed(inventoryItem);
			
			this.partOf = this.installedOnItem;
			
			this.isUserInventoryFullView = inventoryItemUtil.isInventoryFullView(this.inventoryItem);
			setTransactionsForLoggedInUser(); 
			if(this.partOf!=null){
			 this.inventoryItemCompositon = this.partOf.getComposedOf();
			}else{
				List<InventoryItem> inventoryItemsList=new ArrayList<InventoryItem>();
				inventoryItemsList.add(this.inventoryItem);
				this.inventoryItemCompositon = inventoryService.getComponentDetailForMajorComponent(inventoryItemsList);;
			}
			
			this.registeredPolicies = this.policyService
                            .findRegisteredPolicies(this.inventoryItem);
			if (!(InventoryType.STOCK.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()) || InventoryType.OEM_STOCK
					.getType().equalsIgnoreCase(this.inventoryItem.getType().getType()))) {
				initRegisteredPolicies();
				this.originalPoliciesSize = this.registeredPolicies.size();				
				if (getLoggedInUser().hasRole(Role.ADMIN) || getLoggedInUser().hasRole(Role.INVENTORY_ADMIN))
					initAvailablePolicies();
			}
		} catch (ItemNotFoundException e) {
			logger.error(e);
		} catch (PolicyException e) {
			this.logger.error("Policy Exception: findAllApplicablePolicies for the Item",
                            e);
		}
		return SUCCESS;
	}

        // To show detail and preview given the ID of the Invetory item
        public String showPreviewDeatil() throws IOException, JSONException {
                Long idToBeUsed = null;
                if (this.id != null) {
                        idToBeUsed = Long.parseLong(this.id);
                } else if ((this.inventoryItem != null)
                                && (this.inventoryItem.getId() != null)) {
                        idToBeUsed = this.inventoryItem.getId();
                }

                if (idToBeUsed != null) {
                        this.inventoryItem = this.retailItemsService.findById(idToBeUsed);
                }
                return previewDetailView();
        }

        public String getJSONifiedAttachmentList() {
                try {
                        inventoryItem = inventoryService.findInventoryItem(Long
                                        .parseLong(id));
                        if (attachments != null) {
                                attachments.removeAll(inventoryItem.getWarranty()
                                                .getAttachments());
                        }
                        attachments = inventoryItem.getWarranty().getAttachments();
                        if (attachments == null || attachments.size() <= 0) {
                                return "[]";
                        }
                        return getDocumentListJSON(attachments).toString();
                } catch (Exception e) {
                        return "[]";
                }
        }
        
        public String getJSONifiedInternalTruckDocsList() {
            try {
            	
            	List<Document> attachments = this.inventoryItem.getAttachments();
            	attachments = this.inventoryItem.getAttachments();
             if (attachments == null || attachments.size() <= 0) {
                     return "[]";
             }
             return getDocumentListJSON(attachments).toString();
            } catch (Exception e) {
             return "[]";
     
            }
    }
        
        public boolean canDeleteWarrantyForMajorComponent() {
    		if (this.claimService.isAnyActiveClaimOnMajorComponent(this.inventoryItem)) {
    			return false;
    		} else {
    			return true;
    		}
    	}

        public String showAttachments() {
                return SUCCESS;
        }

        public String getScrapReason() {
        		InventoryItemAttributeValue scrapAttribute = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals()); 
                InventoryScrapTransaction scrapComments = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
                                .convertXMLToObject(scrapAttribute.getValue());
                this.scrapReason = scrapComments.getComments();
                return this.scrapReason;
        }

        public CalendarDate getScrapDate() {
                if (inventoryItem.getInventoryItemAttrVals().size() > 0) {                        
                        InventoryItemAttributeValue scrapAttribute = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());                        
                        InventoryScrapTransaction scrapComments = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
                                        .convertXMLToObject(scrapAttribute.getValue());
                        this.scrapDate = scrapComments.getDateOfScrapOrUnscrap();

                }
                return this.scrapDate;
        }
        
        public String getStolenReason() {
    		InventoryItemAttributeValue stolenAttribute = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals()); 
            InventoryStolenTransaction stolenComments = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
                            .convertXMLToObject(stolenAttribute.getValue());
            this.stolenReason = stolenComments.getComments();
            return this.stolenReason;
    }

    public CalendarDate getStolenDate() {
            if (inventoryItem.getInventoryItemAttrVals().size() > 0) {                        
                    InventoryItemAttributeValue stolenAttribute = getLatestInventoryItemAttributeValue(inventoryItem.getInventoryItemAttrVals());                        
                    InventoryStolenTransaction stolenComments = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
                                    .convertXMLToObject(stolenAttribute.getValue());
                    this.stolenDate = stolenComments.getDateOfStolenOrUnstolen();

            }
            return this.stolenDate;
    }

        private InventoryItemAttributeValue getLatestInventoryItemAttributeValue(List<InventoryItemAttributeValue> invItemAttrVals) {		
    		List<InventoryItemAttributeValue> invItemAttrVals2 = new ArrayList<InventoryItemAttributeValue>();
    		for (InventoryItemAttributeValue iiav : invItemAttrVals) {
    			if (iiav.getAttribute().getName() != null && !TWMSWebConstants.DATA_SOURCE.equals(iiav.getAttribute().getName())) {
    				invItemAttrVals2.add(iiav);
    			}
    		}
    		Assert.isTrue(!invItemAttrVals2.isEmpty(),"No Latest Inventory Item Attribute");
    		return invItemAttrVals2.get(invItemAttrVals2.size()-1);
    		
    	}
        
        public boolean isScrap() {
                if ("SCRAP".equals(this.inventoryItem.getConditionType()
                                .getItemCondition())) {
                        return true;
                }
                return false;
        }
        
        public boolean isStolen() {
            if ("STOLEN".equals(this.inventoryItem.getConditionType()
                            .getItemCondition())) {
                    return true;
            }
            return false;
    }
     
         
       public boolean isInventoryWithPDI() {
    	   boolean isInventoryWithPDI = false;
    	   for(CustomReportAnswer customReportAnswer : this.inventoryItem.getReportAnswers()){
    		   if("PDI".equalsIgnoreCase(customReportAnswer.getCustomReport().getReportType().getCode()));
    		   isInventoryWithPDI = true;
    	   }           
    	   return isInventoryWithPDI;
        }

        public String showPolicyAudits() {

                 policy = (RegisteredPolicy) this.policyAdminService
                                .findPolicy(this.policyId);
                this.policyAudits = policy.getPolicyAudits();
                return SUCCESS;
        }
        
        public String getPolicyStatus(CalendarDate fromDate, CalendarDate tillDate, String status) {
        	CalendarDate today = Clock.today();
        	if(status.equals("Active")
        			&& (today.isBefore(fromDate) || today.isAfter(tillDate))) {
        		return "Inactive";
        	}
        	return status;
        }

        public String showPolicyDetails() {
                policy = (RegisteredPolicy) this.policyAdminService
                                .findPolicy(this.policyId);
                policyDefinition = policy.getPolicyDefinition();
                return SUCCESS;
        }

        public boolean isFactoryOrderNumberRequired() {
                return this.configParamService
                                .getBooleanValue(ConfigName.ENABLE_FACTORY_ORDER_NUMBER
                                                .getName());
        }

        public ConfigParamService getConfigParamService() {
                return this.configParamService;
        }

        public void setConfigParamService(ConfigParamService configParamService) {
                this.configParamService = configParamService;
        }

        public String updatePolicies() {
                for (RegisteredPolicy policy : this.registeredPolicies) {
                        // TODO set the price for the policy defination
                        DocumentTransportUtils.markDocumentsAsAttached(policy.getAttachments());
                        Warranty warranty = this.inventoryItem.getWarranty();
                        if (warranty == null) {
                                this.inventoryItem.setWarranty(new Warranty());
                        }
			if (policy.getId() == null) {

				this.warrantyService.createRegisteredPolicyForAdmin(this.inventoryItem.getWarranty(), policy);
			} else {
                                if (policy.getLatestPolicyAudit() != null) {
                                        String auditStatus = policy.getLatestPolicyAudit()
                                                        .getStatus();
                                        if (this.getTerminatedStatus().equals(auditStatus)) {
                                                // admin is trying to terminate an active coverage    
                                        	if(!claimService.isAnyOpenClaimWithPolicyOnInventoryItem(this.inventoryItem, policy))
                                        	{
                                                this.warrantyService.terminateRegisteredPolicyForAdmin(
                                                                this.inventoryItem.getWarranty(), policy);
                                        	}
                                        	else
                                        	{
                                        		//Set back to active becaz the value is getting persisted when the inventory is updated
                                        		policy.getLatestPolicyAudit().setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
                                        		policy.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
                                        		addActionError("error.adminTerminatePolicy.openClaim",policy.getCode());
                                        	}
                                        } else if (this.getActiveStatus().equals(auditStatus)) {
                                                // admin is trying to activate a terminated coverage or
                                                // apply a new coverage
                                                this.warrantyService.activateRegisteredPolicyForAdmin(
                                                                this.inventoryItem.getWarranty(), policy);
                                        } else if (policy.getPolicyAudits().size() >= 2) {
                                                // no status change.only enddate/comments updated
                                                this.warrantyService.updateRegisteredPolicyForAdmin(
                                                                this.inventoryItem.getWarranty(), policy,
                                                                policy.getPolicyAudits().get(
                                                                                policy.getPolicyAudits().size() - 2)
                                                                                .getStatus());
                                        }
                                }
                        }
                }
                try {
                		Warranty latestAcceptedWarranty = warrantyRepository.findBy(inventoryItem);
                        this.warrantyService.updateInventoryForWarrantyDates(this.inventoryItem, latestAcceptedWarranty);
                        this.retailItemsService.update(this.inventoryItem);
                       
                } catch (Exception e) {
                        logger.error("error occurred - " + e.getMessage());
                }
                initRegisteredPolicies();
                if(getLoggedInUser().hasRole(Role.ADMIN))
                initAvailablePolicies();
                this.addedPolicies = new ArrayList<RegisteredPolicy>();
                campaignNotificationWarning();
		if (hasErrors()) {
			return INPUT;
		} else {
			addActionMessage("message.warrantyAdmin.updateSuccess");
			return SUCCESS;
		}
        }       
        
        public void campaignNotificationWarning() {
        List<ListOfValues> campaignClasses = configParamService.getListOfValues(ConfigName.CAMPAIGN_CLASS_FOR_WARNING_ON_EHP.getName());
        List<String> campaignClassesList = new ArrayList<String>();
        if (campaignClasses != null && campaignClasses.size() > 0) {
            for (ListOfValues campaignClass : campaignClasses) {
                campaignClassesList.add(campaignClass.getCode());
            }        
                campaign = campaignService.findPendingCampaigns(this.inventoryItem, campaignClassesList);       
                if (campaign != null && !campaign.isEmpty()) {
                    StringBuffer campaignValue = new StringBuffer();
                    for (Campaign campaignList : campaign) {
                        campaignValue.append(campaignList.getCampaignClass().getDescription() + ",");
                    }
                    String campaignClassesPending = campaignValue.substring(0, campaignValue
                            .length() - 1);
                    addActionWarning("ehp.campaign.warning", campaignClassesPending);
                }
        }
        }

        private void initRegisteredPolicies() {
        	this.registeredPolicies = new ArrayList<RegisteredPolicy>();
            Warranty warranty = this.inventoryItem.getWarranty();
            if (warranty != null) {
               for (RegisteredPolicy policy : warranty.getPolicies()) {
                   this.registeredPolicies.add(policy);
               }
            }
        }
        
        /**
         * Initialises the available policies by fetching all the available policies
         * and removing the ones already registered for the warranty.
         *
         * @throws PolicyException
         */
        private void initAvailablePolicies() {
        	if(!this.inventoryItem.getSerializedPart() || (this.inventoryItem.getSerializedPart() && InventoryItemSource.MAJORCOMPREGISTRATION.equals(this.inventoryItem.getSource())))
        			{
                this.availablePolicies = new ArrayList<RegisteredPolicy>();
                List<PolicyDefinition> policyDefinitions = null;               
                try {               
                		policyDefinitions = this.policyService.findPoliciesForAdminWarrantyReg(this.inventoryItem);                		
                        List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
                        policies = createPolicies(policyDefinitions, this.inventoryItem);
                        List<PolicyDefinition> definitions = new ArrayList<PolicyDefinition>();
                        Warranty warranty = this.inventoryItem.getWarranty();
                        if (warranty != null) {
                                for (RegisteredPolicy pol : warranty.getPolicies()) {
                                        definitions.add(pol.getPolicyDefinition());
                                }
                        }
                        for (RegisteredPolicy policy : policies) {
                                if (!definitions.contains(policy.getPolicyDefinition())) {
                                        this.availablePolicies.add(policy);
                                }
                        }
                } catch (PolicyException e) {
                        logger.error("error while setting up available policies - "
                                        + e.getMessage());
                	}
        		}
        	}
        
	private void initInvisiblePolicies() {
		if (!this.inventoryItem.getSerializedPart()) {
			try {
				this.invisiblePolicies = policyService
						.findInvisiblePoliciesForAdminWarrantyReg(this.inventoryItem);
			} catch (PolicyException e) {
				logger.error("error while setting up invisible policies - "
						+ e.getMessage());
			}
		}
	}

        private List<RegisteredPolicy> createPolicies(
                        List<PolicyDefinition> withDefinitions, InventoryItem forItem)
                        throws PolicyException {
                List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
                for (PolicyDefinition definition : withDefinitions) {
                        String policyName = definition.getCode();
                        if ((policyName == null) || (policyName.trim().equals(""))) {
                                continue; // ignore this policy definition
                        }
                        RegisteredPolicy policy = new RegisteredPolicy();
                        policy.setPolicyDefinition(definition);
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

        private void initAddedPolicies(List<PolicyDefinition> definitions) {
                this.addedPolicies = new ArrayList<RegisteredPolicy>();
                for (RegisteredPolicy policy : this.availablePolicies) {
                        if (definitions.contains(policy.getPolicyDefinition())) {
                                for (RegisteredPolicy registeredPolicy : this.registeredPolicies) {
                                        if (registeredPolicy.getPolicyDefinition().getId()
                                                        .longValue() == policy.getPolicyDefinition()
                                                        .getId().longValue()) {
                                                this.addedPolicies.add(registeredPolicy);
                                        }
                                }
                        }
                }
        }

        private String displayFieldError(String errorId, String errorMessage) {
                addFieldError(errorId, errorMessage);
                return INPUT;
        }

        public InventoryItem getInventoryItem() {
                return this.inventoryItem;
        }

        public void setInventoryItem(InventoryItem inventoryItem) {
                this.inventoryItem = inventoryItem;
        }

        public Collection<Claim> getPreviousClaimsForItem() {
                return this.previousClaimsForItem;
        }

        public void setPreviousClaimsForItem(Collection<Claim> previousClaimsForItem) {
                this.previousClaimsForItem = previousClaimsForItem;
        }

        public ClaimService getClaimService() {
                return this.claimService;
        }

        public void setClaimService(ClaimService claimService) {
                this.claimService = claimService;
        }

        public boolean isRetailingDealer() {
                return this.isRetailingDealer;
        }

        public void setRetailingDealer(boolean isRetailingDealer) {
                this.isRetailingDealer = isRetailingDealer;
        }

        public List<RegisteredPolicy> getRegisteredPolicies() {
                return registeredPolicies;
        }

        public void setRegisteredPolicies(List<RegisteredPolicy> registeredPolicies) {
                this.registeredPolicies = registeredPolicies;
        }

        public PolicyService getPolicyService() {
                return this.policyService;
        }

        public void setPolicyService(PolicyService policyService) {
                this.policyService = policyService;
        }

        public void setInventoryService(InventoryService inventoryService) {
                this.inventoryService = inventoryService;
        }
        
        public List<CampaignNotification> getCampaignNotifications() {
                return campaignNotifications;
        }

        public void setCampaignNotifications(
                        List<CampaignNotification> campaignNotifications) {
                this.campaignNotifications = campaignNotifications;
        }

        public boolean isDraft() {
                return this.draft;
        }

        public void setDraft(boolean draft) {
                this.draft = draft;
        }

        public boolean isInternalUser() {
                return this.isInternalUser;
        }

        public boolean isDifferentDealerAndOwner() {
                return this.isDifferentDealerAndOwner;
        }

        public void setDifferentDealerAndOwner(boolean isDifferentDealerAndOwner) {
                this.isDifferentDealerAndOwner = isDifferentDealerAndOwner;
        }

        public void setInternalUser(boolean isInternalUser) {
                this.isInternalUser = isInternalUser;
        }

        public void setWarrantyCoverageRequestService(
                        WarrantyCoverageRequestService warrantyCoverageRequestService) {
                this.warrantyCoverageRequestService = warrantyCoverageRequestService;
        }

        public WarrantyCoverageRequest getWarrantyCoverageRequest() {
                return warrantyCoverageRequest;
        }

        public void setWarrantyCoverageRequest(
                        WarrantyCoverageRequest warrantyCoverageRequest) {
                this.warrantyCoverageRequest = warrantyCoverageRequest;
        }

        public String getScrapComments() {
                return this.scrapComments;
        }

        public void setScrapComments(String scrapComments) {
                this.scrapComments = scrapComments;
        }
        
        public String getStolenComments() {
            return this.stolenComments;
    }

    public void setStolenComments(String stolenComments) {
            this.stolenComments = stolenComments;
    }


        public Organization getRetailingDealer() {
                return retailingDealer;
        }
        
	public String getShipToDealerAddress() {
		Organization shipTo = inventoryItem.getShipTo();
		Address address = null;
		if (shipTo != null)
			address = shipTo.getAddress();
		StringBuffer shipToaddress = new StringBuffer();
		if (address != null) {
			if (address.getAddressLine1() != null) {
				shipToaddress.append(address.getAddressLine1());
			}
			if (address.getCity() != null) {
				shipToaddress.append(", " + address.getCity());
			}
			if (address.getState() != null) {
				shipToaddress.append(", " + address.getState());
			}
			if (address.getZipCode() != null) {
				shipToaddress.append(", " + address.getZipCode());
			}
			if (address.getCountry() != null) {
				shipToaddress.append(", " + address.getCountry());
			}
		}

		return shipToaddress.toString();
	}

        public void setRetailingDealer(Organization retailingDealer) {
                this.retailingDealer = retailingDealer;
        }

        public boolean isCanViewOwner() {
                return this.canViewOwner;
        }

        public void setCanViewOwner(boolean canViewOwner) {
                this.canViewOwner = canViewOwner;
        }

        public ServiceProvider getServiceDealer() {
                return this.serviceDealer;
        }

        public void setServiceDealer(ServiceProvider serviceDealer) {
                this.serviceDealer = serviceDealer;
        }

        public List<InventoryTransactionDisplayObject> getTransactionsToBeDisplayed() {
                return this.transactionsToBeDisplayed;
        }

        public void setTransactionsToBeDisplayed(
                        List<InventoryTransactionDisplayObject> toSet) {
                this.transactionsToBeDisplayed = toSet;
        }

        protected void setServicingDealer() {
                for (InventoryTransaction invTransaction : this.inventoryItem
                                .getTransactionHistory()) {
                        if (InvTransationType.RMT.getTransactionType().equals(
                                        invTransaction.getInvTransactionType().getTrnxTypeKey())) {
                                // person to whom RMT is done is viewing EHP
                                setServiceDealer(new HibernateCast<ServiceProvider>()
                                                .cast(invTransaction.getSeller()));
                             break;
                        }
                }
        }

        public void setInventoryScrapTransactionXMLConverter(
                        InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
                this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
        }
        public void setInventoryStolenTransactionXMLConverter(
                InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter) {
        this.inventoryStolenTransactionXMLConverter = inventoryStolenTransactionXMLConverter;
}

        public void canViewOwnerInfo() {
                setCanViewOwner(inventoryItemUtil.isCanViewOwnerInfo(inventoryItem));
        }

        protected void setRetailedDealer() {
                setRetailingDealer(inventoryItemUtil.getRetailedDealer(inventoryItem));
        }

        protected void setTransactionsForLoggedInUser() {
                List<InventoryTransactionDisplayObject> transactions = new ArrayList<InventoryTransactionDisplayObject>(
                                5);
                Collections.sort(this.inventoryItem.getTransactionHistory());
                boolean isLatestTrnxEncountered = false;
                boolean isModifyActionSet = false;
                for (Iterator<InventoryTransaction> iter = this.inventoryItem
                                .getTransactionHistory().iterator(); iter.hasNext();) {
                        InventoryTransaction inventoryTransaction = iter.next();

                        InventoryTransactionDisplayObject displayObject = new InventoryTransactionDisplayObject();

                        displayObject.setId(inventoryTransaction.getId());
                        displayObject.setInvTransactionType(inventoryTransaction
                                        .getInvTransactionType());
                        displayObject.setInvoiceNumber(inventoryTransaction.getInvoiceNumber());
                        displayObject.setOwnerShip(inventoryTransaction.getOwnerShip());
                        if (inventoryItemUtil.isLoggedInUserOwnerOfTrnx(inventoryTransaction) 
                                || (this.isUserInventoryFullView)) {
                                displayObject.setBuyer(inventoryTransaction.getBuyer());
                                displayObject.setSeller(inventoryTransaction.getSeller());
                                displayObject.setTransactionDate(inventoryTransaction
                                                .getTransactionDate());
                                displayObject.setInvTransactionType(inventoryTransaction
                                                .getInvTransactionType());
                                displayObject.setTransactedItem(this.inventoryItem);
                                displayObject
                                .setLatestTransactionForAType(isLatestTransactionForATransactionType(inventoryTransaction));
                                if (isDROrETRTransaction(inventoryTransaction)) {
                                        // a dr/etr transaction , now see if there's a corresponding
                                        // warranty
                                        Warranty warranty = this.warrantyService
                                        .findByTransactionId(inventoryTransaction.getId());
                                        if (warranty != null) {
                                                displayObject.setWarrantyPresent(true);
                                        }
                                } else {
                                        displayObject.setWarrantyPresent(false);
                                }

                                if (/* this.isInternalUser() && */!isModifyActionSet
                                                && this.inventoryItem.isRetailed()
                                                && canTransactionBeModified(inventoryTransaction)) {
                                        displayObject.setModifyAllowed(true);
                                        isModifyActionSet = true;
                                }
                        } else {
                                displayObject.setWarrantyPresent(false);
                                displayObject.setSeller(null);
                                if (!isLatestTrnxEncountered) {
                                        isLatestTrnxEncountered = true;
                                        displayObject.setBuyer(inventoryTransaction.getBuyer());
                                } else {
                                        displayObject.setBuyer(null);
                                }
                                displayObject.setTransactionDate(null);
                                displayObject.setInvTransactionType(inventoryTransaction
                                                .getInvTransactionType());
                                displayObject.setTransactedItem(this.inventoryItem);
                                displayObject
                                .setLatestTransactionForAType(isLatestTransactionForATransactionType(inventoryTransaction));
                        }
                        transactions.add(displayObject);

                }
                setTransactionsToBeDisplayed(transactions);

        }

        private boolean canTransactionBeModified(
                        InventoryTransaction inventoryTransaction) {
                if ((InvTransationType.DR.getTransactionType()
                                .equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())
                                || InvTransationType.DR_MODIFY.getTransactionType().equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())
                                || (isCustomerDetailsNeededForDR_Rental()?InvTransationType.DR_RENTAL.getTransactionType().equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue()):false)
                                || InvTransationType.DEMO.getTransactionType().equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())                               
                                || InvTransationType.ETR.getTransactionType().equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue()) || InvTransationType.ETR_MODIFY
                                .getTransactionType().equals(
                                                inventoryTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue()))
                                && (this.previousClaimsForItem == null || this.previousClaimsForItem
                                                .isEmpty())) {
                        // Only if a corresponding warranty exist in valid status,
                        // modification can be done
                        if (this.inventoryItem.getWarranty() != null
                                        && this.inventoryItem.getWarranty().getForTransaction() != null
                                        && this.inventoryItem.getWarranty().getForTransaction()
                                                        .getId().longValue() == inventoryTransaction
                                                        .getId().longValue()) {
                                return true;
                        }
                }
                return false;
        }

        public boolean isLoggedinDealerOwner() {
                return this.loggedinDealerOwner;
        }

        public void setLoggedinDealerOwner(boolean loggedinDealerOwner) {
                this.loggedinDealerOwner = loggedinDealerOwner;
        }

        private boolean isLatestTransactionForATransactionType(
                        InventoryTransaction invTransaction) {
                List<String> transactionTypes = new ArrayList<String>();
                InventoryItem invItem = invTransaction.getTransactedItem();
                InventoryTransaction latestInvTransactionForAType = null;
                if (InvTransationType.DR.getTransactionType().equals(
                                invTransaction.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.DR_MODIFY.getTransactionType().equals(
                                                invTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())
                                || InvTransationType.DR_DELETE.getTransactionType().equals(
                                                invTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())
                                ||(isCustomerDetailsNeededForDR_Rental() && InvTransationType.DR_RENTAL.getTransactionType().equals(
                                        invTransaction.getInvTransactionType()
                                        .getTrnxTypeValue())) 
                                || InvTransationType.DEMO.getTransactionType().equals(
                                                invTransaction.getInvTransactionType()
                                                .getTrnxTypeValue())) {
                        transactionTypes.add(InvTransationType.DR.getTransactionType());
                        transactionTypes.add(InvTransationType.DR_MODIFY
                                        .getTransactionType());
                        transactionTypes.add(InvTransationType.DR_DELETE
                                        .getTransactionType());
                        transactionTypes.add(InvTransationType.DR_RENTAL
                        				.getTransactionType());
                        transactionTypes.add(InvTransationType.DEMO
                				.getTransactionType());
                }
                else if (InvTransationType.ETR.getTransactionType().equals(
                                invTransaction.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.ETR_MODIFY.getTransactionType().equals(
                                                invTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())
                                || InvTransationType.ETR_DELETE.getTransactionType().equals(
                                                invTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue())) {
                        transactionTypes.add(InvTransationType.ETR.getTransactionType());
                        transactionTypes.add(InvTransationType.ETR_MODIFY
                                        .getTransactionType());
                        transactionTypes.add(InvTransationType.ETR_DELETE
                                        .getTransactionType());
                }
                latestInvTransactionForAType = invItem
                                .getLatestTransactionForATransactionType(transactionTypes);
                if (latestInvTransactionForAType != null
                                && latestInvTransactionForAType.getId().equals(
                                                invTransaction.getId())) {
                        return true;
                }
                return false;
        }

        public WarrantyService getWarrantyService() {
                return warrantyService;
        }

        public void setWarrantyService(WarrantyService warrantyService) {
                this.warrantyService = warrantyService;
        }

       
        public boolean isEverScrapped() {
                for (InventoryItemAttributeValue attribute : this.inventoryItem
                                .getInventoryItemAttrVals()) {
                        if (AttributeConstants.SCRAP_COMMENTS.equals(attribute
                                        .getAttribute().getName())) {
                                return true;
                        }
                }
                return false;
        }
        
        public boolean isEverStolen() {
            for (InventoryItemAttributeValue attribute : this.inventoryItem
                            .getInventoryItemAttrVals()) {
                    if (AttributeConstants.STOLEN_COMMENTS.equals(attribute
                                    .getAttribute().getName())) {
                            return true;
                    }
            }
            return false;
    }

        public InventoryScrapTransaction getScrapTransaction(String attrValue) {
                return (InventoryScrapTransaction) inventoryScrapTransactionXMLConverter
                                .convertXMLToObject(attrValue);
        }
        public InventoryStolenTransaction getStolenTransaction(String attrValue) {
            return (InventoryStolenTransaction) inventoryStolenTransactionXMLConverter
                            .convertXMLToObject(attrValue);
    }

        public void setPolicyAdminService(PolicyAdminService policyAdminService) {
                this.policyAdminService = policyAdminService;
        }

        public Long getPolicyId() {
                return policyId;
        }

        public void setPolicyId(Long policyId) {
                this.policyId = policyId;
        }

        public List<RegisteredPolicyAudit> getPolicyAudits() {
                return policyAudits;
        }

        public void setPolicyAudits(List<RegisteredPolicyAudit> policyAudits) {
                this.policyAudits = policyAudits;
        }

        public String getTerminatedStatus() {
                return terminatedStatus;
        }

        public String getActiveStatus() {
                return activeStatus;
        }

        public void setRetailItemsService(RetailItemsService retailItemsService) {
                this.retailItemsService = retailItemsService;
        }

        public void setAddedPolicies(List<RegisteredPolicy> addedPolicies) {
                this.addedPolicies = addedPolicies;
        }

        public List<RegisteredPolicy> getAvailablePolicies() {
                return availablePolicies;
        }

        public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
                this.availablePolicies = availablePolicies;
        }

        public boolean isUserInventoryFullView() {
                return isUserInventoryFullView;
        }

        public void setUserInventoryFullView(boolean isUserInventoryFullView) {
                this.isUserInventoryFullView = isUserInventoryFullView;
        }

        public List<Document> getAttachments() {
                return attachments;
        }

        public void setAttachments(List<Document> attachments) {
                this.attachments = attachments;
        }

        @Override
        protected BeanProvider getBeanProvider() {
                return new DisplayImagePropertyResolver();
        }

        private boolean isDROrETRTransaction(InventoryTransaction invTrnx) {
                if (InvTransationType.DR.getTransactionType().equals(
                                invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.DR_MODIFY.getTransactionType().equals(
                                                invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.DR_DELETE.getTransactionType().equals(
                                                invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.DEMO.getTransactionType().equals(
                                        invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || (isCustomerDetailsNeededForDR_Rental()?InvTransationType.DR_RENTAL.getTransactionType().equals(
                                                        invTrnx.getInvTransactionType().getTrnxTypeValue()):false)) {
                        return true;
                } else if (InvTransationType.ETR.getTransactionType().equals(
                                invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.ETR_MODIFY.getTransactionType().equals(
                                                invTrnx.getInvTransactionType().getTrnxTypeValue())
                                || InvTransationType.ETR_DELETE.getTransactionType().equals(
                                                invTrnx.getInvTransactionType().getTrnxTypeValue())) {
                        return true;
                } else {
                        return false;
                }
        }

        public List<RegisteredPolicy> getAddedPolicies() {
                return this.addedPolicies;
        }

        public String getJSONStringForAvailablePolicies() {
                return getJsonStringForPolicyList(this.availablePolicies);
        }

        @SuppressWarnings("unchecked")
        public String getJSONStringForRegisteredPolicies() {
                Warranty warranty = inventoryItem.getWarranty();
                if (warranty != null) {
                        registeredPolicies = new ArrayList(warranty.getPolicies());
                } else {
                        new ArrayList<RegisteredPolicy>();
                }

                Collections.sort(registeredPolicies,
                                new Comparator<RegisteredPolicy>() {
                                        public int compare(RegisteredPolicy p1, RegisteredPolicy p2) {
                                                return p1.getPolicyDefinition().getPriority()
                                                                .compareTo(
                                                                                p2.getPolicyDefinition().getPriority());
                                        }
                                });

                return getJsonStringForPolicyList(registeredPolicies);

        }

        public String getJSONStringForAddedPolicies() {
                return getJsonStringForPolicyList(this.addedPolicies);
        }

        private String getJsonStringForPolicyList(
                        Collection<RegisteredPolicy> policies) {
            JSONArray array = new JSONArray();
            List<Map<String, Object>> policyList = inventoryItemUtil.getPolicyDetails(policies,
                        inventoryItem, this.editable);            
            for(Map<String, Object> policyMap : policyList) {
            	if(policyMap.get("type") != null){
            		String warrantyType = null;
            		warrantyType= (String) policyMap.get("type");
            		policyMap.put("type", getText(warrantyType));
            	}
            	if(policyMap.get("id")!=null) 
            	{
            	policyMap.put(ATTACHMENTS, getJSONifiedPolicyAttachments((Long) policyMap.get("id")));
            	}
                array.put(policyMap);
            }
            return array.toString();
        }
        
        private boolean isPolicyCommentsUpdate(RegisteredPolicy policy){
                if(policy.getLatestButOnePolicyAudit().getComments() ==null
                                && policy.getLatestPolicyAudit().getComments()!= null){
                        return true;
                }else{
                        return !policy.getLatestButOnePolicyAudit().getComments()
                        .equals(policy.getLatestPolicyAudit().getComments());
                }
                
        }
        
        private boolean isPolicyEndDateModified(RegisteredPolicy policy){
                return 
                ! policy.getLatestButOnePolicyAudit().getWarrantyPeriod().getTillDate().
                equals(policy.getLatestPolicyAudit().getWarrantyPeriod().getTillDate());
        }
        
        @Override
        public void validate() {
                List<PolicyDefinition> definitions = new ArrayList<PolicyDefinition>();
                /*
                 * Earlier Implementation - When admin is managing warranty coverage on inventories, start date
                 * will always be defaulted to delivery date.Only end date will be
                 * captured from him.
                 * For Nacco, the start date will also be passed from the jsp
                 */
                for (RegisteredPolicy policy : this.registeredPolicies) {
                        PolicyDefinition policyDefinition = policy.getPolicyDefinition();
                        definitions.add(policyDefinition);

                        String policyCode = policyDefinition.getCode();
                        
                        //FIX:User cannot modify suspended policy.
                        if (policy.getLatestButOnePolicyAudit() != null
                                        && RegisteredPolicyStatusType.SUSPENDED.getStatus()                                     
                                                        .equalsIgnoreCase(policy.getLatestButOnePolicyAudit().getStatus())
                                        && (isPolicyEndDateModified(policy) || isPolicyCommentsUpdate(policy))) {
                                addActionError("error.warrantyAdmin.policyNotEditable",
                                                policyCode);
                        }
                                                
                        CalendarDate startDate = null;
                        CalendarDate tillDate = null;
						if (this.inventoryItem.getWntyStartDate() == null) {
							startDate = inventoryItem.getDeliveryDate();
						} else {
							startDate = this.inventoryItem.getWntyStartDate();
						}
                        if(policy.getWarrantyPeriod()!=null)
                        {
                        //policy.getWarrantyPeriod().setFromDate(startDate);
                        if(policy.getWarrantyPeriod().getFromDate()==null){
                        	policy.getWarrantyPeriod().setFromDate(startDate);
                        }	
                        startDate = policy.getWarrantyPeriod().getFromDate();
                        tillDate = policy.getWarrantyPeriod().getTillDate();
                        }
                        // this validation is to ensure that admin never extends the
                        // warranty period beyond its original limit
                        CalendarDate initialEndDate = null;
                        if (policy.getFirstPolicyAudit() != null) {
                                initialEndDate = policy.getFirstPolicyAudit()
                                                .getWarrantyPeriod().getTillDate();
                        }

                        if (tillDate == null) {
                                addActionError("error.warrantyAdmin.emptyTillDate",
                                                new String[] { policyCode });
                        }

                        if (WarrantyType.POLICY.getType().equals(
                                        policy.getPolicyDefinition().getWarrantyType().getType()) && policy.getLatestPolicyAudit()!=null) {
                                Integer serviceHoursCovered = policy.getLatestPolicyAudit()
                                                .getServiceHoursCovered();
                                if (!this.inventoryItem.getSerializedPart() && (serviceHoursCovered == null
                                                || serviceHoursCovered.intValue() < 0)) {
                                        addActionError("error.warrantyAdmin.emptyServiceHours",
                                                        new String[] { policyCode });
                                }
                        }

                        if (tillDate != null) {  
                        	
                        	String auditStatus = policy.getLatestPolicyAudit().getStatus();
                        	if (!this.getTerminatedStatus().equals(auditStatus)){														
                    			                        	
                                if (tillDate.isBefore(startDate)) {
                                        addActionError(
                                                        "error.warrantyAdmin.invalidTillEndDateRange",
                                                        new String[] { tillDate.toString(), policyCode,
                                                                        startDate.toString() });
                                }
                                /*if (initialEndDate != null && tillDate.isAfter(initialEndDate)) {
                                        addActionError("error.warrantyAdmin.invalidTillEndDate",
                                                        new String[] { tillDate.toString(), policyCode,
                                                                        initialEndDate.toString() });
                                }*/
                                
                        	}    
                        }
                        
                        if (!hasActionErrors()) {
                                if (policy.getLatestPolicyAudit() != null) {
                                        policy.getLatestPolicyAudit().setWarrantyPeriod(
                                                        new CalendarDuration(startDate, tillDate));
                                } else {
                                        policy.setWarrantyPeriod(new CalendarDuration(startDate,
                                                        tillDate));
                                }
                        } else {
                                this.editable = true;
                        }
                }

                initAddedPolicies(definitions);
        }

        public boolean isEditable() {
                return this.editable;
        }

        public void setEditable(boolean editable) {
                this.editable = editable;
        }

        public int getOriginalPoliciesSize() {
                return this.originalPoliciesSize;
        }

        public void setOriginalPoliciesSize(int originalPoliciesSize) {
                this.originalPoliciesSize = originalPoliciesSize;
        }

        public String getInventoryItemId() {
                return inventoryItemId;
        }

        public void setInventoryItemId(String inventoryItemId) {
                this.inventoryItemId = inventoryItemId;
        }

        public PolicyDefinition getPolicyDefinition() {
                return policyDefinition;
        }

        public void setPolicyDefinition(PolicyDefinition policyDefinition) {
                this.policyDefinition = policyDefinition;
        }

        /*
         * (non-Javadoc)
         *
         * @see tavant.twms.web.inbox.SummaryTableAction#getInboxViewContext()
         */
        protected String getInboxViewContext() {
                return BusinessObjectModelFactory.INVENTORY_SEARCHES;
        }

        public List<Campaign> getCampaign() {
                return campaign;
        }

        public void setCampaign(List<Campaign> campaign) {
                this.campaign = campaign;
        }

        public CampaignService getCampaignService() {
                return campaignService;
        }

        public boolean isReportAvailable() {
                boolean isAvailable = false;
                List<CustomReport> reports = customReportService
                                .findReportsForInventory(inventoryItem);
                if (reports != null && !reports.isEmpty()) {
                        isAvailable = true;
                }
                return isAvailable;
        }

        public void setCustomReportService(CustomReportService customReportService) {
                this.customReportService = customReportService;
        }

        public void setCampaignService(CampaignService campaignService) {
                this.campaignService = campaignService;
        }

        public ConfigParam getConfigParam() {
                return configParam;
        }

        public void setConfigParam(ConfigParam configParam) {
                this.configParam = configParam;
        }

        public Boolean getIsEquipHistoryPage() {
                return isEquipHistoryPage;
        }

        public void setIsEquipHistoryPage(Boolean isEquipHistoryPage) {
                this.isEquipHistoryPage = isEquipHistoryPage;
        }

        public Boolean getClaimSubmissionAllowed() {
                if (this.inventoryItem != null) {
                        if (isRetailInventory()) {
                                return new Boolean(true);
                        } else {
                                if ((null != getLoggedInUsersDealership() && isLoggedInDealerCurrentOwnerOrParentDealer())
                                                || inventoryService
                                                                .customersBelongsToConfigParam(inventoryItem) || getLoggedInUser().hasRole("processor")) {
                                        return new Boolean(true);
                                }
                        }

                }
                return new Boolean(false);
        }

        public Boolean getWntyRegAllowed() {
                if (this.inventoryItem != null) {
                    if (!isInternalUser) {
                        if(isLoggedInUserAnEnterpriseDealer()){
                            List<Long> childDealers = new ArrayList<Long>();
                            if(!getLoggedInUsersDealership().getChildDealersIds().isEmpty())
                            	childDealers.addAll(getLoggedInUsersDealership().getChildDealersIds());                            	
                            else
                            	childDealers.add(getLoggedInUsersDealership().getId());
                            if(childDealers.contains(inventoryItem.getCurrentOwner().getId())
                                    || inventoryItemUtil.stockBelongsToOEM(inventoryItem)
                                    && inventoryService.customersBelongsToConfigParam(inventoryItem)) {
                                return new Boolean(true);
                            }
                        }else if(isLoggedInDealerCurrentOwnerOrParentDealer()
                                || inventoryItemUtil.stockBelongsToOEM(inventoryItem)
                                && inventoryService.customersBelongsToConfigParam(inventoryItem)){
                            return new Boolean(true);
                        }else if(isLoggedInDealerCurrentOwnerOrParentDealer() || isLoggedInDealerShipToDealer() || allowWROnOthersStock()){
                            return new Boolean(true);
                        }
                    } else if (isLoggedInUserAnInvAdmin()) {
                        return new Boolean(true);
                    }
                }
                return new Boolean(false);
        }

        public boolean allowWROnOthersStock() {
    		return configParamService.getBooleanValue(ConfigName.ALLOW_WNTY_REG_ON_OTHERS_STOCKS.getName());
    	}
        
		public boolean isLoggedInDealerCurrentOwnerOrParentDealer() {
			if(getLoggedInUsersDealership().getId().longValue() == inventoryItem.getCurrentOwner().getId().longValue()){
				return true;
			}
			/*for(Organization organization : getLoggedInUsersOrganization().getChildOrgs()){
				if(organization.getId().longValue() == inventoryItem.getCurrentOwner().getId().longValue()){
					return true;
				}
			}*/
            if(orgService.getChildOrganizationsIds(getLoggedInUsersOrganization().getId()).contains(inventoryItem.getCurrentOwner().getId())){
                return true;
            }
			return false;
		}
		
		public boolean isLoggedInDealerParentDealer() {
			/*for(Organization organization : getLoggedInUsersOrganization().getChildOrgs()){
				if(organization.getId().longValue() == inventoryItem.getCurrentOwner().getId().longValue()){
					return true;
				}
			}*/
            if(orgService.getChildOrganizationsIds(getLoggedInUsersOrganization().getId()).contains(inventoryItem.getCurrentOwner().getId())){
                return true;
            }
			return false;
		}
		
		public boolean isLoggedInDealerShipToDealer() {
			if(inventoryItem.getShipTo()!=null && getLoggedInUsersDealership().getId().longValue() == inventoryItem.getShipTo().getId().longValue()){
				return true;
			}
			return false;
		}

        public Map<String, String> getInventoryAttributes() {
                return inventoryAttributes;
        }

        public void setInventoryAttributes(Map<String, String> inventoryAttributes) {
                this.inventoryAttributes = inventoryAttributes;
        }

	public boolean isEligibleForExtendedWarrantyPurchase() {
		if (getInventoryItem() != null) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(getInventoryItem()
							.getBusinessUnitInfo().getName());
		}
		boolean isEligible = false;
		Map<String, List<Object>> buValues = configParamService
				.getValuesForAllBUs(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY
						.getName());
		for (String buName : buValues.keySet()) {
			Boolean booleanValue = new Boolean(buValues.get(buName).get(0)
					.toString());
			if (booleanValue) {
				isEligible = true;
				break;
			}
		}
		return isEligible;
	}
    
    public boolean isDealerEligibleToPerformRMT(){
        boolean isEligible = false;
        Map<String, List<Object>> buValues = configParamService.
                getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }

    public boolean modifyWRAllowed() {
    	boolean isETRFiled=false;
    	for (InventoryTransaction transaction : this.inventoryItem.getTransactionHistory()) {
    		if(InvTransationType.ETR.getTransactionType().
    				equalsIgnoreCase(transaction.getInvTransactionType().getTrnxTypeValue())){
    			isETRFiled = true;
    			break;
    		}
    	}
    	return (!isETRFiled);
    }

    public boolean isEligibleForRetailMachineTransfer(){
        if(getInventoryItem()!=null){
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getInventoryItem().getBusinessUnitInfo().getName());
        }
        boolean isEligible = false;
        if(!isLoggedInUserAnInternalUser()){
            Map<String, List<Object>> buValues = configParamService.
                    getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            for (String buName : buValues.keySet()) {
                Boolean booleanValue = new Boolean(buValues.get(buName).get(0).toString());
                if (booleanValue) {
                    isEligible = true;
                    break;
                }
            }
            return isEligible;
        }
        return isEligible;
    }

    public boolean isStockClaimAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED
                                .getName());
    }
    
    
    public boolean isD2DAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.D2D_ALLOWED
                                .getName());
    }
    public BigDecimal hoursInServiceForClaimsOnInventory(Long claimId) {
    		ClaimedItem item = this.claimService.findClaimedItem(claimId, this.inventoryItem.getId());
    		return item.getHoursInService();
	}
    
    public String showMajorComponentsForUnit(){
		inventoryItemCompositon = this.inventoryItem.getComposedOf();
		return SUCCESS;    	
    }
    
    public String showAdditionalComponentsForUnit(){
    	invItemAdditionalComponents = this.inventoryItem.getAdditionalComponents();
		return SUCCESS;    	
    }

    public boolean isDRDoneByLoggedInUser() {
        return inventoryItemUtil.isDRDoneByLoggedInUser(this.inventoryItem);
    }
    
    public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
        this.inventoryItemUtil = inventoryItemUtil;
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
				&& this.inventoryItem.getWarranty().getDeliveryDate() != null) {
			CalendarDate deleteDRDateBasedOnDD = this.inventoryItem.getWarranty().getDeliveryDate()
			.plusDays(daysToBeConsideredForDRDeletion);
			if (deleteDRDateBasedOnDD.isAfter(todaysDate)) {
				return true;
			} else {
				return false;
			}
		} else if (this.inventoryItem.getWarranty().getForTransaction().getTransactionDate() != null) {
			CalendarDate deleteDRDateBasedOnSubmitDate = this.inventoryItem.getWarranty()
			.getForTransaction().getTransactionDate().plusDays(
					daysToBeConsideredForDRDeletion);
			if (deleteDRDateBasedOnSubmitDate.isAfter(todaysDate)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
    
	public Boolean getWntyRegAllowedStock() {
		//if (this.inventoryItem != null) {
			if (isLoggedInUserAnInternalUser()) {
				if (!isLoggedInUserAnInvAdmin())
					return new Boolean(false);
			}
		//}
		return new Boolean(true);
	}

	public RegisteredPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(RegisteredPolicy policy) {
		this.policy = policy;
	}

    	public boolean isEnterpriseDealer() {
		return enterpriseDealer;
	}

	public void setEnterpriseDealer(boolean isEnterpriseDealer) {
		this.enterpriseDealer = isEnterpriseDealer;
	}
	
	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	

    public Collection<PartGroup> getInventoryPartGroups() {
		return inventoryPartGroups;
	}

	public void setInventoryPartGroups(Collection<PartGroup> inventoryPartGroups) {
		this.inventoryPartGroups = inventoryPartGroups;
	}
	

	public String getOrderReceivedDate() {
		return orderReceivedDate;
	}

	public void setOrderReceivedDate(String orderReceivedDate) {
		this.orderReceivedDate = orderReceivedDate;
	}
	
	public String getCtsDate() {
		return ctsDate;
	}

	public void setCtsDate(String ctsDate) {
		this.ctsDate = ctsDate;
	}
	
	public String newTruckComment(){
		return SUCCESS;
	}
	
	public String saveInventoryComment(){
		validateInventoryComment();
		if(hasErrors()){
			return INPUT;
		}
		int nextCommentSequenceNumber = getNextCommentSequenceNumber();
		inventoryComment.setUserId(getLoggedInUser().getName().toUpperCase());
		inventoryComment.setSequenceNumber(nextCommentSequenceNumber);
		inventoryItem.getInventoryComments().add(inventoryComment);
		inventoryService.updateInventoryItem(inventoryItem);
		return SUCCESS;
	}
	
	public String saveInventory() throws IOException, JSONException {
		if (internalTruckDocs != null || internalTruckDocs.size() != 0) {
			inventoryItem = inventoryService.findInventoryItem(Long
					.parseLong(id));
			inventoryItem.setAttachments(internalTruckDocs);
			inventoryService.updateInventoryItem(inventoryItem);
		}
		return SUCCESS;
	}
	
	
	private void validateInventoryComment(){
		if(inventoryComment.getDateOfComment() == null){
			addActionError("error.inventoryComment.dateOfComment");
		}
		if(inventoryComment.getComment() == null || !StringUtils.hasText(inventoryComment.getComment())){
			addActionError("error.inventoryComment.comment");
		}
	}
	
	private int getNextCommentSequenceNumber(){
		int sequenceNumber = 1;
		if(inventoryItem != null && inventoryItem.getInventoryComments() != null && inventoryItem.getInventoryComments().size() > 0){
			sequenceNumber = inventoryItem.getInventoryComments().get(inventoryItem.getInventoryComments().size() - 1).getSequenceNumber() + 1;
		}
		return sequenceNumber;
	}
	
	public List<PolicyDefinition> getInvisiblePolicies() {
		return invisiblePolicies;
	}

	public void setInvisiblePolicies(List<PolicyDefinition> invisiblePolicies) {
		this.invisiblePolicies = invisiblePolicies;
	}
	
	public boolean isCustomerDetailsNeededForDR_Rental(){
		return this.configParamService
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
	}
	
	public Collection<InventoryItemAdditionalComponents> getInvItemAdditionalComponents() {
		return invItemAdditionalComponents;
	}

	public void setInvItemAdditionalComponents(
			Collection<InventoryItemAdditionalComponents> invItemAdditionalComponents) {
		this.invItemAdditionalComponents = invItemAdditionalComponents;
	}
	
	public String viewTruckComments(){
		return SUCCESS;
	}


	public String getInventorySerialNumberForMajorComponent(Long majorComponentId){
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = inventoryService.findInventoryItemForMajorComponent(majorComponentId);
		} catch (Exception e) {
			logger.error("Could Not Fetch Inventory Item for the Major Component Id : " + majorComponentId);
			return null;
}
		if(inventoryItem != null){
			return inventoryItem.getSerialNumber();
		}else{
			return null;
		}
	}

	private void populateDocumentTypes(){
		List<ListOfValues> docTypes = this.inventoryService.getLovsForClass("DocumentType");
		for(ListOfValues type : docTypes){
			this.documentTypes.add(type);
	 	}
	}	
	
	public boolean displayVintageStockInbox(){
		return configParamService.getBooleanValue(ConfigName.DISPLAY_VINTAGE_STOCK.getName());
		}

	private Boolean checkAttachmentsEditable() {
		if (isLoggedInUserAnInvAdmin()
				&& (inventoryItem.getType().getType().equals("STOCK")
						|| inventoryItem.getType().getType().equals("RETAIL") || inventoryItem
						.getType().getType().equals("OEM_STOCK")))
			return true;
		else
			return false;
	}
	
	
	public CalendarDate getVintageStockShipmentDate(){
		if(displayVintageStockInbox()){
			String dateString = configParamService.getStringValue(ConfigName.SHIPMENT_DATE_FOR_VINTAGE_STOCK.getName());
			if(dateString != null){
				return CalendarDate.from(dateString, "dd/MM/yyyy");
			}
		}
		return null;
	}
	
	public boolean isCustomerExists() {
		if(inventoryItem != null) {
			Party customer = inventoryItem.getLatestBuyer();
			if(customer != null && InstanceOfUtil.isInstanceOfClass(Customer.class, customer)) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean isCreateClaimAllowed(){
		 boolean canSearchOtherDealersRetail  = configParamService
	                .getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		 boolean createClaim = false;
		if (this.inventoryItem.getType().equals(InventoryType.RETAIL)
				&& canSearchOtherDealersRetail && !isLoggedInUserAnInternalUser() && isBuConfigAMER()) {
			Dealership loggedInUser = new HibernateCast<Dealership>()
					.cast(getLoggedInUsersDealership());
			if (isLoggedInUserDualBrandDealer() || BrandType.UTILEV.getType().equalsIgnoreCase(this.inventoryItem.getBrandType())
					|| (loggedInUser!=null && loggedInUser.getBrand()!=null && loggedInUser.getBrand().equalsIgnoreCase(this.inventoryItem.getBrandType()))) {
				createClaim = true;
			}else
				createClaim = false;
		}
		return createClaim;
	}
	
	public boolean displayShipToDealerDetails(){
		return configParamService.getBooleanValue(ConfigName.ENABLE_SHIP_TO_DEALER_ON_EH_PAGE.getName());
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}
	
	public String getJSONifiedPolicyAttachments(Long policyId) {
		try {
			List<Document> policyAttachments;
			policy = (RegisteredPolicy) this.policyAdminService.findPolicy(policyId);
			policyAttachments = policy.getAttachments();
			if (policyAttachments == null || policyAttachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(policyAttachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}

}

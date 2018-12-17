/*
 *   Codieyright (c)2006 Tavant Technologies
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
package tavant.twms.web.claim;

import static tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST;
import static tavant.twms.domain.claim.ClaimState.TRANSFERRED;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.COMPLETE_CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ID;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.JOB_CODE_DESCRIPTION;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.LABEL;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.LABOUR_HRS;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_LEAF;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SERVICE_PROCEDURE_ID;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSONForRecoveryClaim;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.DocumentException;
import org.hibernate.classic.Validatable;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.BrandItemRepository;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimNumberPatternService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MarketingGroupsLookup;
import tavant.twms.domain.claim.MatchReadInfo;
import tavant.twms.domain.claim.MatchReadMultiplyFactor;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.ReplacedInstalledPartsService;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.SourceWarehouseRepository;
import tavant.twms.domain.claim.TransitionTaken;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.payment.AdditionalPaymentType;
import tavant.twms.domain.claim.payment.IndividualLineItem;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.PutOnHoldReason;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.RequestInfoFromUser;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.common.Suppliers;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.laborType.LaborSplit;
import tavant.twms.domain.loa.LimitOfAuthorityLevel;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserCluster;
import tavant.twms.domain.orgmodel.UserClusterService;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyProductMapping;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.stateMandates.StateMandatesService;
import tavant.twms.domain.supplier.ItemMappingRepository;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.integration.layer.util.ExceptionUtil;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartReturnProcessingServiceImpl;
import tavant.twms.process.ProcessService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.actions.SortedHashMap;
import tavant.twms.web.common.DisplayWarningPropertyResolver;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.supplier.RecoverablePartsBean;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.common.BuSettingsService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
//import tavant.twms.domain.configuration.ConfigParamService;
//import tavant.twms.domain.inventory.InventoryComment;

/**
 *
 */
@SuppressWarnings( { "serial", "unused" })
public class ClaimsAction extends BaseClaimsAction implements Preparable,
		Validatable, ServletRequestAware, ConfigOptionConstants {
	private static final Logger logger = Logger.getLogger(ClaimsAction.class);

	public static final String USER_KEY = "user";

	public static final String CREATE_CLAIM = "create_claim";
	
	public static final String CLAIM_SUBMIT = "Submit";

	public static final String FATAL_ERROR = "fatalError";

    private static final String DIRTY_READ_CLAIM = "dirtyReadClaim";
	
	public static final String SYNC_TYPE = "Claim";
	
	public static final String POLICY = "Policy";
	
	public static final String STOCK = "Stock";
	
	
	protected TaskViewService taskViewService;

	protected PartReturnProcessingService partReturnProcessingService;

	protected ContractService contractService;

	protected RuleAdministrationService ruleAdministrationService;

	private PolicyService policyService;

	protected ClaimSubmissionUtil claimSubmissionUtil;	

	private PaymentService paymentService;
	
	private PartReplacedService partReplacedService;
	
	protected TaskView task;

	private Claim claimDetails;
	
	private Claim claim;
	
	private PaymentAsyncService paymentAsyncService;
	
	private CountryStateService countryStateService;
	
	//private InventoryComment inventoryComment;
	
    public void setCountryStateService(CountryStateService countryStateService) {
		this.countryStateService = countryStateService;
	}

	public PaymentAsyncService getPaymentAsyncService() {
		return paymentAsyncService;
	}

	public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
		this.paymentAsyncService = paymentAsyncService;
	}

	private String processorTakenTransition;

    private ValidationResults messages;

	protected List<PaymentCondition> paymentConditions;

	protected PartReturnService partReturnService;

	protected Contract selectedContract = new Contract();

	private List<Contract> contracts = new ArrayList<Contract>();

	private MatchReadMultiplyFactor matchReadMultiplyFactor;

	private ClaimNumberPatternService claimNumberPatternService;

	private List<ClaimAttributes> claimAttributes = new ArrayList<ClaimAttributes>();

	private HttpServletRequest request;

	private String commentsForDisplay;
	
	private String testToEvaluate;
	
	private String transientTravelHours;
	
	
	
	public String getTransientTravelHours() {
		return transientTravelHours;
	}

	public void setTransientTravelHours(String transientTravelHours) {
		this.transientTravelHours = transientTravelHours;
	}

	public static final String ADDITIONAL_HOURS = "additionalLaborHours",
			SPECIFIED_HOURS = "specifiedHoursInCampaign",
			WRAPPER_ID = "wrapperId", JOB_REASON = "jobReason";

	public static final String ASSIGN_RULES_STATE = "assign";
	
	private final String CANADA_COUNTRY_CODE="CA";

	private List<String> mandatedComments = new ArrayList<String>();

	private boolean isWarningRequired = false;

	private final int jsonCounter = 0;

	private String nextLOAProcessor;

    private UserRepository userRepository;

	private String faultCodeID;

	private AttributeAssociationService attributeAssociationService;

	private String serProcedureId;
	
	private String fetchJobCode; // Gonna have 'true' or 'false' as values

	private List<ClaimAttributes> serviceJobCodeAttributesList;
	
	//private InventoryItem inventoryItem;

	private String indexId;

	private String partNumber;

	private boolean toPrint;

	private String dsmAdvisor;
	
	private String cpAdvisor;

	private String thirdPartyDealerName;

	protected CampaignService campaignService;
	
	private StateMandatesService stateMandatesService;

	private Campaign campaign;

	protected WorkListItemService workListItemService;

	protected ReplacedInstalledPartsService replacedInstalledPartsService;

	private Long procReviewTaskId;
	
	private String baseFormName;	

	protected MSAService msaService;
	protected final SortedHashMap<String, String> countryList = new SortedHashMap<String, String>();
	private String countryCode;
	private String stateCode;
	private String cityCode;
	private String zipCode;
	protected List<String> countriesFromMSA = new ArrayList<String>();
	protected String selectedBusinessUnit;
	private String lateNightVariable;
	private PartReturnDefinitionRepository partReturnDefinitionRepository;
	protected int rowIndex;
	private String forSerialized;
	protected Boolean isThirdParty = false;
	private DocumentService documentService;
	protected int subRowIndex;
	private boolean reProcess = false;	
	private Boolean skipActionMessage=false;
	private boolean isLoggedInUserEligibleForLateFeeApproval = false;
	
	protected Boolean authorizeAllowedAction;
	
	private MiscellaneousItemConfigService miscellaneousItemConfigService;
	
	private DealerGroupService dealerGroupService;
	
    protected SourceWarehouseRepository sourceWarehouseRepository;

    private boolean autoCheckRecoveryFlag = false;
    
    private boolean incidentalsAvaialable;
    
    private String anonymousThirdPartyNumber;
    
    private boolean supplierRecovery;
	
	private Suppliers  suppliers;
    
	public static Map<String, String> actionPerformed = new HashMap<String, String>(10);

	Map<String, List<Object>> dateForDraftDeletion = null;

	Map<String, List<Object>> daysForDraftDeletion;

	Map<String, List<Object>> daysForForwardedClaimDenial;
	
	List<OrganizationAddress> orgAddresses = new ArrayList<OrganizationAddress>();
	
	private String nonOEMPartDescription;
	
	private SyncTrackerService syncTrackerService;
	
	private ItemMappingRepository itemMappingRepository;
	
	private List<Item> supplierItems;
	
	private SupplierService supplierService;
	
	private List<String> policyCodes = new ArrayList<String>();
	
	private BrandItemRepository brandItemRepository;
	
	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;
	
	public BuSettingsService buSettingsService;

	protected List<Long> putOnHoldReasonList = new ArrayList<Long>();
	   
	protected List<Long> requestInfoFromDealerList = new ArrayList<Long>();
	
	protected List<Long> rejectionReasonsList = new ArrayList<Long>();
	
	protected List<String> reasonsList = new ArrayList<String>();
	
	private String claimState;
	
	private String selectedReasons;
	
	private PolicyDefinitionRepository policyDefinitionRepository;
	
	private ProcessService processService;	
	public PolicyDefinitionRepository getPolicyDefinitionRepository() {
		return policyDefinitionRepository;
	}

	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}

	public String getSelectedReasons() {
		return selectedReasons;
	}

	public void setSelectedReasons(String selectedReasons) {
		this.selectedReasons = selectedReasons;
	}

	public String getClaimState() {
		return claimState;
	}

	public void setClaimState(String claimState) {
		this.claimState = claimState;
	}

	public List<String> getReasonsList() {
		return reasonsList;
	}

	public void setReasonsList(List<String> reasonsList) {
		this.reasonsList = reasonsList;
	}

	public PartReplacedService getPartReplacedService() {
		return partReplacedService;
	}

	public void setPartReplacedService(PartReplacedService partReplacedService) {
		this.partReplacedService = partReplacedService;
	}

	public List<Long> getRejectionReasonsList() {
		return rejectionReasonsList;
	}

	public void setRejectionReasonsList(List<Long> rejectionReasonsList) {
		this.rejectionReasonsList = rejectionReasonsList;
	}

	public List<Long> getRequestInfoFromDealerList() {
		return requestInfoFromDealerList;
	}

	public void setRequestInfoFromDealerList(List<Long> requestInfoFromDealerList) {
		this.requestInfoFromDealerList = requestInfoFromDealerList;
	}

	public List<Long> getPutOnHoldReasonList() {
		return putOnHoldReasonList;
	}

	public void setPutOnHoldReasonList(List<Long> putOnHoldReasonList) {
		this.putOnHoldReasonList = putOnHoldReasonList;
	}

	public BuSettingsService getBuSettingsService() {
        return buSettingsService;
    }

    public void setBuSettingsService(BuSettingsService buSettingsService) {
        this.buSettingsService = buSettingsService;
    }
		
	private boolean isLateFeeChanged=false;	
  
	public boolean getIsLateFeeChanged() {
		return isLateFeeChanged;
	}

	public void setIsLateFeeChanged(boolean isLateFeeChanged) {
		this.isLateFeeChanged = isLateFeeChanged;
	}

	public void setBrandItemRepository(BrandItemRepository brandItemRepository) {
		this.brandItemRepository = brandItemRepository;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public List<Item> getSupplierItems() {
		return supplierItems;
	}

	public void setSupplierItems(List<Item> supplierItems) {
		this.supplierItems = supplierItems;
	}

	public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
		this.itemMappingRepository = itemMappingRepository;
	}

	public String getNonOEMPartDescription() {
		return nonOEMPartDescription;
	}

	public void setNonOEMPartDescription(String nonOEMPartDescription) {
		this.nonOEMPartDescription = nonOEMPartDescription;
	}
	
	public boolean isReProcess() {
		return reProcess;
	}

	public void setReProcess(boolean reProcess) {
		this.reProcess = reProcess;
	}
	
	public boolean isLoggedInUserEligibleForLateFeeApproval() {
		return isLoggedInUserEligibleForLateFeeApproval;
	}

	public void setLoggedInUserEligibleForLateFeeApproval(
			boolean isLoggedInUserEligibleForLateFeeApproval) {
		this.isLoggedInUserEligibleForLateFeeApproval = isLoggedInUserEligibleForLateFeeApproval;
	}
	
	static {
		// Please refer to actions.jsp; Please dont change the names
		actionPerformed.put("Hold", UserClusterService.ACTION_PUT_ON_HOLD);
		actionPerformed.put("Forward to Dealer", UserClusterService.ACTION_FORWARDED_TO_DEALER);
		actionPerformed.put("Hold", UserClusterService.ACTION_PUT_ON_HOLD);
		actionPerformed.put("Seek Advice", UserClusterService.ACTION_REQUEST_FOR_ADVICE);
		actionPerformed.put("Transfer", UserClusterService.ACTION_TRANSFER_TO);
		actionPerformed.put("ApproveAndTransferToNextUser", UserClusterService.ACTION_APPROVE_AND_TRANSFER_TO_NEXT_USER);
		actionPerformed.put("Deny", UserClusterService.ACTION_DENY);
		actionPerformed.put("Accept", UserClusterService.ACTION_ACCEPT);
		actionPerformed.put("Re-process", UserClusterService.ACTION_RE_PROCESS);
	}

	protected String claimId;
	private String contextValue;
	private Claim claimDetail;
	private boolean partShippedNotRcvd = false;
    private boolean showReRequestsForSMR=true;
    protected List<String> loaWarningMessages = new ArrayList<String>();
	protected List<OEMPartReplaced> initialReplacedParts = new ArrayList<OEMPartReplaced>();
    protected Map<Long, String> technicians = new HashMap<Long, String>();
    private List<OEMPartReplaced> initialOEMReplacedParts = new ArrayList<OEMPartReplaced>();  
    private ItemGroup model;
    private InventoryService inventoryService;
    
    private static final int addDays60 =60,addDays90=90,addDays120=120;
    public String setupForCreate() {
		return "new_claim_screen";
	}

	@Override
	protected BeanProvider getBeanProvider() {
		
		return new DisplayWarningPropertyResolver();
	}

        public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
            this.claimSubmissionUtil = claimSubmissionUtil;
        }

    @Override
	protected PageResult<?> getBody() {
    	WorkList workList = this.workListService.getWorkList(createCrieria());
    	Set<TaskInstance> tasks = new LinkedHashSet<TaskInstance>(workList.getTaskList());
    	return new PageResult<TaskInstance>(new LinkedList<TaskInstance>(tasks), new PageSpecification(
    			this.page, this.pageSize,workList.getTaskListCount()), getTotalNumberOfPages(workList));
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        /*if("WPRA To be Generated".equalsIgnoreCase(getFolderName())){
           header.add(new SummaryTableColumn("columnTitle.dueParts.return_location",
                   "location.code", 16, "string"));
           header.add(new SummaryTableColumn("columnTitle.common.dealerName",
                   "serviceprovider.name", 16, "string"));
           header.add(new SummaryTableColumn("columnTitle.dealerRequestedPart.location_id", "serviceprovider.id", 10,
                   "number", "serviceprovider.id", false, true, false, false));
           return header;
        }*/
		if (ClaimState.FORWARDED.getState().equals(getFolderName())
				&& isLoggedInUserADealer()) {
			header.add(new SummaryTableColumn("", "imageCol", 3, IMAGE,
					"warningImg", false, false, false, true));
			incrementDefaultHeaderSize();
		}
		if (getLoggedInUser().hasRole("processor")) {
			if (!("Draft Claim".equalsIgnoreCase(getFolderName()))) {
				header.add(new SummaryTableColumn("", "imageCol", 3, IMAGE,
						"warningImgForProcessor", false, false, false, true));
				incrementDefaultHeaderSize();
			}
		}

		if (!("Draft Claim".equalsIgnoreCase(getFolderName()))) {
			header.add(new SummaryTableColumn("label.inboxView.claimNumber",
					"claim.claimNumber", 8, "String", "claim.claimNumber",
					true, false, false, false));
			incrementDefaultHeaderSize();
		} else if ("Draft Claim".equalsIgnoreCase(getFolderName())) {
				header.add(new SummaryTableColumn(
					"label.inboxView.workOrderNumber", "claim.activeClaimAudit.workOrderNumber",
					12, "String", "claim.activeClaimAudit.workOrderNumber", true, false, false,
					false));
				incrementDefaultHeaderSize();
			}

		if("WaitingForLabor".equalsIgnoreCase(getFolderName())){
			header.add(new SummaryTableColumn(
					"label.inboxView.workOrderNumber", "claim.activeClaimAudit.workOrderNumber",
					12, "String", "claim.activeClaimAudit.workOrderNumber", true, false, false,
					false));
				incrementDefaultHeaderSize();
		}

		if ("Draft Claim".equals(getFolderName())) {
			header.add(new SummaryTableColumn("columnTitle.newClaim.createdOn",
					"claim.filedOnDate", 10, "date"));
			incrementDefaultHeaderSize();
		}

		header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0,
				"String", "id", false, true, true, false));

		header.add(new SummaryTableColumn("label.inboxView.claimNumber",
				"claim.id", 0, "String", "claim.id", false, false,true, false));

		if (inboxViewFields())
        	addInboxViewFieldsToHeader(header, LABEL_COLUMN_WIDTH);
        else {
	    	if (!isLoggedInUserADealer()) {
				header.add(new SummaryTableColumn("label.inboxView.servProviderName",
						"claim.forDealer.name", 24, "string"));
			}
			header.add(new SummaryTableColumn("label.inboxView.claimType",
					"claim.clmTypeName", 15, "string"));			
			if (isExportAction()) {
				header.add(new SummaryTableColumn("columnTitle.newClaim.failureCode",					
						"claim.activeClaimAudit.serviceInformation.faultCode", 12, "String"));
			}
			header.add(new SummaryTableColumn("columnTitle.newClaim.causalPart",
					"claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", 12, "String"));
				header.add(new SummaryTableColumn("label.inboxView.failureDate",
						"claim.activeClaimAudit.failureDate", 10, "date"));
				header.add(new SummaryTableColumn("label.inboxView.repairDate",
						"claim.activeClaimAudit.repairDate", 10, "date"));
		}
		return header;
	}

	public WorkListCriteria createCrieria() {
		WorkListCriteria criteria = new WorkListCriteria(getLoggedInUser());
		criteria.setTaskName(getFolderName());
		if (logger.isInfoEnabled()) {
			logger.info("Folder Name : " + getFolderName());
		}
		ServiceProvider loggedInUsersDealership = getLoggedInUsersDealership();
		if(loggedInUsersDealership == null || isLoggedInUserAnEnterpriseDealer()){
			ServiceProvider s = new ServiceProvider();
			s.setId(-1L);
			s.setVersion(0);
			criteria.setServiceProvider(s);
		}else{
			criteria.setServiceProvider(loggedInUsersDealership);
		}
		criteria.setServiceProviderList(getLoggedInUser().getBelongsToOrganizations());
		addFilterCriteria(criteria);
		addSortCriteria(criteria);
        // added to aviod showing duplicate results
        if(this.sorts.size() > 0)
            criteria.addSortCriteria("claim.id", false);
		if (logger.isInfoEnabled()) {
			logger.info("page size " + this.pageSize + " page to be fetched "
					+ getPage());
		}
		criteria.setPageSize(this.pageSize);
		criteria.setPageNumber(getPage() - 1);
        if((getFolderName().equalsIgnoreCase(WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME) && getConfigParamService().getBooleanValue(ConfigName.DISPLAY_NEW_CLAIMS_TO_ALL_PROCESSOR.getName()))
                 || getFolderName().equalsIgnoreCase(WorkflowConstants.REJECTED_PART_RETURN)){
        	List<User> userList=orgService.findAllAvailableProcessors();
        	List<User> ncrUsers=new ArrayList<User>();
        	ListIterator<User> users=userList.listIterator();
        	while(users.hasNext())
            {
            	User user=users.next();
            	if(user.hasRole(Role.NCR_PROCESSOR))     
            	{
            		ncrUsers.add(user);
            		users.remove();
            	}
        		
            }
        	  if(getLoggedInUser().hasRole(Role.NCR_PROCESSOR))
                {
        	 	  criteria.setUserGroup(ncrUsers);
                }
              else
                {
            	  criteria.setUserGroup(userList);
                }
             }
		return criteria;
	}

	protected void addSortCriteria(WorkListCriteria criteria) {
		for (Iterator<String[]> iter = this.sorts.iterator(); iter.hasNext();) {
			String[] sort = iter.next();
			String sortOnColumn = sort[0];
			boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
			if (logger.isInfoEnabled()) {
				logger.info("Adding sort criteria " + sortOnColumn + " "
						+ (ascending ? "ascending" : "descending"));
			}
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	protected void addFilterCriteria(WorkListCriteria criteria) {
		for (Iterator<String> iter = this.filters.keySet().iterator(); iter
				.hasNext();) {
			String filterName = iter.next();
			String filterValue = this.filters.get(filterName);
			if (logger.isInfoEnabled()) {
				logger.info("Adding filter criteria " + filterName + " : "
						+ filterValue);
			}
			if (isBuConfigAMER() && filterName.equals("claim.clmTypeName") && filterValue.toUpperCase().startsWith("U")){
				Pattern pattern = Pattern.compile("\\b(U|UN|UNI|UNIT)\\b");
				Matcher matcher = pattern.matcher(filters.get(filterName)
				.toUpperCase());
				if (matcher.find()){
					filterValue = "machine";
					}
				}
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	public String preview() throws ServletException, DocumentException,
			IOException {
		if (getId() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("The task id obtained to be viewed is: " + getId());
			}
			this.task = this.taskViewService.getTaskView(Long.valueOf(getId()));
			Claim claim = this.task.getClaim();
			setQtyForUIView(claim);
			setTotalLaborHoursForUiView(claim);
			setConfiguredCostCategories(claim);
			return SUCCESS;
		}
		return ERROR;
	}

	public String submit() {
		this.toPrint = true;
		return processClaim();
	}

	protected void isThirdPartyUser(Claim claim){
		if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, claim
				.getForDealer())) {
			Boolean isThirdPartyLogin = orgService
					.isThirdPartyDealerWithLogin(claim.getForDealer().getId());
			if (!isThirdPartyLogin && !claim.isServiceProviderSameAsFiledByOrg()) {
				isThirdParty = true;
			} else if (!isThirdPartyLogin && claim.getFiledBy().isInternalUser()) {
				isThirdParty = true;
			}
		}	
	}
/*	
	public void isDeductableApplicable()
	{
		this.task.getClaim().getApplicablePolicy().getPolicyDefinition().getAvailability().getProducts();
		
	}*/
	public String saveDraft() {
		Claim claim;
		try {
			claim = this.task.getClaim();
			
		} catch (Exception e) {
			logger.error("Exception EX : " + e);
			return FATAL_ERROR;
		}
	    if((claim.getCompetitorModelBrand()!=null && !claim.getCompetitorModelBrand().isEmpty() && !claim.getCompetitorModelDescription().isEmpty() && !claim.getCompetitorModelTruckSerialnumber().isEmpty()))
        {

        claim.getClaimedItems().get(0).getItemReference().setSerialized(false);
        }
	    setActionErrors(claimSubmissionUtil.validateReplacedInstalledParts(claim,isProcessorReview(), hasActionErrors()));
	    setActionErrors(claimSubmissionUtil.validateMiscelleanousParts(claim));
	   	    
		isThirdPartyUser(claim);
        setTotalLaborHoursForClaim(claim);
        if (isLaborSplitEnabled()) {
			if (!validateLaborType(task.getClaim())) {
				return INPUT;
			}
		}
        
		if (task.getClaim().getClaimedItems().get(0).getItemReference()
				.isSerialized() && task.getClaim().getClaimedItems().get(0)
						.getItemReference().getReferredInventoryItem() != null
				&& InventoryType.RETAIL.getType().equals(
						task.getClaim().getClaimedItems().get(0)
								.getItemReference().getReferredInventoryItem()
								.getType().getType())
				&& isMatchReadApplicable()
				&& !(ClaimType.CAMPAIGN.getType().equals(task.getClaim()
						.getType().getType()))) {
			prepareMatchReadInfo();
			prepareMatchReadInfoForShow();
		}


		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledParts(claim);
		}
		
		if(claim.getItemDutyConfig()||claim.getMealsConfig()||claim.getPerDiemConfig()
    			||claim.getParkingConfig()||claim.getRentalChargesConfig()||claim.getLocalPurchaseConfig()
    			||claim.getTollsConfig()||claim.getOtherFreightDutyConfig()||claim.getOthersConfig() || claim.getHandlingFeeConfig() || claim.getTransportation()){
    		this.incidentalsAvaialable=true;
    	}

		prepareAttributesForClaim(claim);
		
		if (isProcessorReview()) {
            getClaimService().updateOEMPartInformation(claim, getInitialReplacedParts());
		}
		
		getClaimService().updateClaim(claim);
		if (claim.isOfType(ClaimType.CAMPAIGN)){
			campaign = claim.getCampaign();
		}
		if(!skipActionMessage){
			addActionMessage("message.newClaim.saveClaimSuccess", claim.getId());
		}
	    
		setPartForCrossReference(claim);
		setQtyForUIView(claim);
		setTotalLaborHoursForUiView(claim);
		return SUCCESS;
		
	}

	public String deleteDraft() {
		this.task.setTakenTransition("Delete Draft");
		StringBuffer messageKey = new StringBuffer(50);
		String formattedTransition = StringUtils.uncapitalize(StringUtils
				.trimAllWhitespace(this.task.getTakenTransition()));
		messageKey.append("message.newClaim.").append(formattedTransition)
				.append("Success");
		this.taskViewService.submitTaskView(this.task);
		addActionMessage(messageKey.toString());
		return SUCCESS;
	}

	// if changed, please modify processClaim() API in other claim action class
	// too
	@SuppressWarnings("deprecation")
	private String processClaim() {
		if (logger.isDebugEnabled()) {
			logger.debug(MessageFormat.format("Submiting task with id {0}",
					this.task.getTask()));
			logger.debug(MessageFormat.format("Action choosen {0}", this.task
					.getTakenTransition()));

		}
		
		
		// Message key would be of the form "message.newClaim.submitSuccess"
		// etc.s
		StringBuffer messageKey = new StringBuffer(50);
		String formattedTransition = StringUtils.uncapitalize(StringUtils
				.trimAllWhitespace(this.task.getTakenTransition()));
		messageKey.append("message.newClaim.").append(formattedTransition)
				.append("Success");

		Claim claim = null;
		try {
			claim = this.task.getClaim();
		} catch (Exception e) {
			logger.error("Exception EX : " + e);
			return FATAL_ERROR;
		}
		claim.setCreditDate(null);//nullifying the previously populated credit date.
       // return DIRTY_READ_CLAIM;
        if(this.task != null && this.task.getTask() != null && this.task.getTask().hasEnded()) {
            //Fetch the latest claim
           // Claim latestClaim = claimService.findClaim(this.task.getClaim().getId());
            User user = claim.getLatestAudit() != null ? claim.getLatestAudit().getUpdatedBy(): null;
            if(user != null){
                addActionError("message.claim.state.changed.by",user.getCompleteNameAndLogin());
            }else{
                addActionError("message.claim.state.changed.by.otheruser");
            }
            addActionError("message.claim.refresh.button");
            return DIRTY_READ_CLAIM;
        }
		if(claim!=null&&claim.getClaimNumber()!=null){
		syncTrackerService.setInactiveStatusForexistingSyncTrackerIds(claim.getClaimNumber(), SYNC_TYPE);
	     }
        claimSubmissionUtil.removeInactiveJobcodes(claim.getServiceInformation());
        claimSubmissionUtil.removeInvalidJobCodes(claim.getServiceInformation(), getFailureStructure());
		if(task.getTakenTransition() != null &&
				"Accept".equalsIgnoreCase(task.getTakenTransition())&& claim.getItemReference().isSerialized() && !(ClaimState.SERVICE_MANAGER_REVIEW.getState().equalsIgnoreCase(claim.getState().getState())))
		{
			getClaimService().roundUpLaborOnClaim(claim);
		}	
        if((claim.getCompetitorModelBrand()!=null && claim.getCompetitorModelBrand() != null && claim.getCompetitorModelDescription() != null && claim.getCompetitorModelTruckSerialnumber() != null))
        {
        	claim.getClaimedItems().get(0).getItemReference()
                .setSerialized(false);

        }
        
        if(!claim.isSupplierRecovery())
		 {
		 claim.setSuppliers(null);
		 }


		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			prepareReplacedInstalledParts(claim);
		}


		//TODO
		/*if(! InstanceOfUtil.isInstanceOfClass( ThirdParty.class , claim.getForDealer())) {
			prepareOEMPartCrossRef(claim);
		}*/

		// Simple hack to get the For Dealer for the third party dealer.
		if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, claim.getForDealer()) &&
				StringUtils.hasText(thirdPartyDealerName)) {
			claim.setForDealer(orgService.findServiceProviderByName(thirdPartyDealerName));
		}

		if ((ClaimState.DRAFT.getState().equals(claim.getState().getState())
            || ClaimState.SERVICE_MANAGER_RESPONSE.getState().equals(claim.getState().getState()))
				&& !"Delete Draft".equals(this.task.getTakenTransition())
				// ESESA-1114 - The claim date stayed as draft date for all non-serialized claims causing the claim date < submitted date audit
				//&& claim.getItemReference().isSerialized()) {
			// claim filed on date should be the claim submit date not draft
			// date
		) {
			claim.setFiledOnDate(Clock.today());
		}

		if (!"Delete Draft".equals(this.task.getTakenTransition())
				&& isMatchReadApplicable()
				&& claim.getClaimedItems().get(0).getItemReference()
						.isSerialized()) {
			computeMatchReadScore(claim);
		}
		if ((isProcessorReview() || isDraftClaim() || isForwarded() || isServiceManagerReview())) {
			setTotalQtyForReplacedParts(claim);
		}
		if (claim.getServiceInformation().getServiceDetail()
				.getLaborPerformed() != null) {
			for (LaborDetail labor : claim.getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				if (labor.getEmptyAdditionalHours() != null
						&& labor.getEmptyAdditionalHours()) {
					labor.setAdditionalLaborHours(null);
				}
			}
		}
		if (!isCPReview() && !isAdviceRequest() && !isServiceManagerResponse()) {
			setTotalLaborHoursForClaim(claim);
		}
		
		setClaimReasons(claim,this.task.getTakenTransition());
		if (!ClaimState.DRAFT.getState().equals(claimDetail.getState().getState())) {
			if(getLoggedInUser().hasRole(Role.WARRANTY_SUPERVISOR)){
				isLoggedInUserEligibleForLateFeeApproval = true;
			}
		}
		
		if ((null != task.getTakenTransition() && task.getTakenTransition()
				.equalsIgnoreCase("Transfer"))
				|| isLoggedInUserEligibleForLateFeeApproval) {
			claim.getActiveClaimAudit().setIsLateFeeApprovalRequired(
					isLateFeeChanged);
		}
		
		claimSubmissionUtil.setLateFeeForClaim(claim);

		/*if (isProcessorReview()||isRepliesOrForwarded()) {
			setEligibilityToShare(claim);
		}*/
		
		
		if(ClaimState.CP_REVIEW.getState().equalsIgnoreCase(claim.getState().getState())){
			claim.setCpReviewed(true);
		}
		if(!claim.getNcrClaimCheck() && ClaimState.DRAFT.getState().equalsIgnoreCase(claim.getState().getState())){
			claimSubmissionUtil.setPolicyForClaim(claim);
		}
		isComputationRequired(claim);
		//TODO: Temp fix, Hidden parameter is not working for modifier amount
		  claim.setCanUpdatePayment(true);
        if(!isCPReview() && claim.isCanUpdatePayment()){
		computePayment(claim);
		if(checkForPaymentSystemErrors(claim,this.task.getTakenTransition(),true)){
			return INPUT;	
		}
		}

		// contractService.updateCausalSupplierPartReturn(claim);
        /*The function is included here so that any change in the claim by processor,resets the 
         * customReportAnswers.
         */
    	isFailureReportsPending(claim);
        if ((isProcessorReview() || isRepliesOrForwarded()) &&
                null != task.getTakenTransition() && (task.getTakenTransition().equalsIgnoreCase("Accept") || task.getTakenTransition().equalsIgnoreCase("Hold")  || task.getTakenTransition().equalsIgnoreCase("Transfer"))) {
        	if(isClaimIsForCanadianDealer(claim.getForDealer().getId())){
        		 getClaimService().updateOEMPartInformation(claim, getInitialReplacedParts());
                 List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, getInitialReplacedParts());
                 List<OEMPartReplaced> replacedParts = claim.getServiceInformation().getServiceDetail().getReplacedParts();
                 if (!removedParts.isEmpty()) {
                	 for(OEMPartReplaced part : removedParts){
                		 for(PartReturn partReturn: part.getPartReturns()){
                		  partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);	 
                		 }
                	 }
                     partReturnProcessingService.endTasksForParts(removedParts);
                     }
                Location defaultReturnLocation = claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation());
                List<RecoverablePartsBean> returnsToBeInitiated = new ArrayList<RecoverablePartsBean>();
                for(RecoveryClaim recoveryClaim : claim.getRecoveryClaims()){
                    RecoveryClaimInfo recoveryInfo = recoveryClaim.getRecoveryClaimInfo();
                    if(recoveryInfo!=null){
                        List<RecoverablePart> recoverableParts = recoveryInfo.getRecoverableParts();
                        for(RecoverablePart recPart : recoverableParts){
                            if(recPart.getOemPart().isReturnDirectlyToSupplier()){
                                recPart.setSupplierReturnNeeded(true);
                                RecoverablePartsBean recoverablePartsBean = new RecoverablePartsBean();
                                recoverablePartsBean.setRecoverablePart(recPart);
                                recoverablePartsBean.setRecClaim(recoveryClaim);
                                recoverablePartsBean.setSupplierReturnLocation(recPart.getOemPart().getActivePartReturn().getReturnLocation());
                                returnsToBeInitiated.add(recoverablePartsBean);
                            }
                        }
                    }
                }

                for (OEMPartReplaced replacedPart : replacedParts) {
                    if(replacedPart.isReturnDirectlyToSupplier()){
                        replacedPart.setReturnDirectlyToSupplier(false);
                        replacedPart.getPartReturn().setReturnLocation(defaultReturnLocation);
                        for(PartReturn partReturn : replacedPart.getPartReturns()){
                            partReturn.setReturnLocation(defaultReturnLocation);

                        }
                    }
                }
                //For EMEA we don't need the part return to be triggered now. It will be through WPRA process
                // if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                 if(!claim.isNcr()){
                     this.partReturnProcessingService.startPartReturnProcessForAllParts(claim);
                 }
                 //Now initiate the supply recovery part return
                for(RecoverablePartsBean bean: returnsToBeInitiated){
                    this.partReturnProcessingService.startRecoveryPartReturnProcessForRecPart(bean.getRecoverablePart(), bean.getRecClaim(), bean.getSupplierReturnLocation());
                }


        	}else{
        		 getClaimService().updateOEMPartInformation(claim, getInitialReplacedParts());
                 List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, getInitialReplacedParts());
                 if (!removedParts.isEmpty()) {
                	 for(OEMPartReplaced part : removedParts){
                		 for(PartReturn partReturn: part.getPartReturns()){
                		  partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);	 
                		 }
                	 }
                     partReturnProcessingService.endTasksForParts(removedParts);
                     //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                     //and prepare due parts inbox too.
                     //Anyway we have a cr that says processor can edit the claim even after claim is accepted, so if he is making some changes
                     //in the returnable parts section then let's update the inbox for wpra too.
                    /* if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                         partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                     }*/
                     }
                 //For EMEA we don't need the part return to be triggered now. It will be through WPRA process
                // if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                 if(!claim.isNcr()){
                     this.partReturnProcessingService.startPartReturnProcessForAllParts(claim);
                 }
     		for (OEMPartReplaced removedPart : removedParts) {
                    removedPart.setPartReturns(new ArrayList<PartReturn>());
                 }
        	}           
        } else if(isProcessorReview()){
            //Update if any change to part return, don;t trigger the return but should update it
            getClaimService().updateOEMPartInformation(claim, getInitialReplacedParts());
            //remove if return disabled
            List<OEMPartReplaced> removedParts = partReturnService.fetchRemovedParts(claim, getInitialReplacedParts());
            for (OEMPartReplaced removedPart : removedParts) {
                removedPart.setPartReturns(new ArrayList<PartReturn>());
        }
        
            if (!removedParts.isEmpty()) {
                partReturnProcessingService.endTasksForParts(removedParts);
                //If claim is re-opened and processor is removing some part then let's remove it from the wpra
                //and prepare due parts inbox too.
                //Anyway we have a cr that says processor can edit the claim even after claim is accepted, so if he is making some changes
                //in the returnable parts section then let's update the inbox for wpra too.
                /*if(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
                    partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
                }*/
            }
        }
        //Logic for initiate part return for dealer
        initiatePartReturnToDealer(this.task);
    
        //Fix for SLMS-2043
        if(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)){
        	PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        	if(partsClaim.getPartInstalled() && 
        			(partsClaim.getCompetitorModelBrand() == null || partsClaim.getCompetitorModelBrand().isEmpty())){
        		if(claim.getHoursInService() != null && claim.getHoursOnTruck() != null){
        			claim.setHoursOnPart(claim.getHoursInService().subtract(claim.getHoursOnTruck()));
        		}
        	}
        }
        
        //FIX ME : for internal issue no-TWMS4.3-925 and HUSS-591
        //Re-look at this code
        if(isServiceManagerReview()) {
        	this.claimService.updateClaim(claim);
        }
       
        /**
         * Updating claimed inventory items's BOM on claim acceptance
         */           
        // Added for Testing Purpose .. This Logic will be removed
    	
    	if(task.getTakenTransition() != null &&
				"Re-requests for SMR".equalsIgnoreCase(task.getTakenTransition())){
			Integer noOfResubmits = this.task.getClaim().getNoOfResubmits();
			noOfResubmits++;
			this.task.getClaim().setNoOfResubmits(noOfResubmits);
		}
		// This has to be evaluated before the next call, since the claim state
		// changes when the task
		// is submitted.
		boolean warning = ClaimState.DRAFT.getState().equalsIgnoreCase(
				claim.getState().getState());
		if (task.getSeekAdviceFrom() != null) {
			this.task.getTask().setVariable("dsmAdvisor",
					task.getSeekAdviceFrom());
		}
		
		if (task.getSeekReviewFrom() != null) {
			this.task.getTask().setVariable("cpAdvisor",
					task.getSeekReviewFrom());
		}
		
		if(WorkflowConstants.FORWARD_TO_DEALER.equals(task.getTakenTransition())){
		  task.setRepliesTo(getLoggedInUser().getName());
		}			
		setWarningMessages(this.task.getClaim());
		this.taskViewService.submitTaskView(this.task);
		
		//SLMSPROD-381( make the amount zero if claim is warranty order claim
		if (this.task.getClaim().getWarrantyOrder()&&this.task.getClaim().getState().equals(ClaimState.PROCESSOR_REVIEW)) {
			Payment payment = null;
			 try {
			payment = this.paymentService.calculatePaymentForWarrantyOrderClaim(this.task.getClaim());
			 } catch (PaymentCalculationException e) {
		            throw new RuntimeException("Error occured while performing payment calculation for Warranty Order claim", e);
		        }
			this.task.getClaim().setPayment(payment);	
			claimService.updateClaim(claim);
		}
    //--End of SLMSPROD-381
		
		if (ClaimState.PROCESSOR_REVIEW.equals(this.task.getClaim().getState())) {
            //TaskInstance task = workListItemService.find
            TaskInstance taskInstance = this.workListItemService
					.findTaskForClaimWithTaskName(this.task.getClaim().getId(),
							WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME);
			procReviewTaskId = taskInstance.getId();
		}
		updatePartReceivedCount(this.task.getClaim());
		if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(this.task.getClaim().getType().getType())){
			if(task.getTakenTransition() != null &&(
					"Deny".equalsIgnoreCase(task.getTakenTransition()) || "Delete".equalsIgnoreCase(task.getTakenTransition()))){
				campaignService.deleteDraftCampaignClaim(task.getClaim());
			}
			List<InventoryItem> items = new ArrayList<InventoryItem>();
			for (ClaimedItem claimedItem : claim.getClaimedItems()) {
				items.add(claimedItem.getItemReference()
						.getReferredInventoryItem());
			}
			if (claim.getId() != null && claim.getCampaign() != null
					&& !claim.getState().equals(ClaimState.DENIED)
					&& !claim.getState().equals(ClaimState.ACCEPTED)) {
                if(task.getTakenTransition().equalsIgnoreCase(TransitionTaken.DENY.getTransitionTaken())){
				campaignService.updateCampaignNotifications(items, claim,
                            claim.getCampaign(),CampaignNotification.PENDING);
			} else {
				campaignService.updateCampaignNotifications(items, claim,
                            claim.getCampaign(),CampaignNotification.INPROCESS);
			}
		}       
			else if (!claim.getPayment().isPaymentToBeMade()
					&& !claim.getState().equals(ClaimState.DENIED)
					&& claim.getActiveClaimAudit().getState()
							.equals(ClaimState.ACCEPTED)) {
				campaignService.updateCampaignNotifications(items, claim,
						claim.getCampaign(),CampaignNotification.COMPLETE);
			}
		}       

        if (StringUtils.hasText(claim.getClaimNumber()) && 
        		(claim.getFailureReportPending() == null || (claim.getFailureReportPending() != null && !claim.getFailureReportPending()))) {
			addActionMessage(messageKey.toString(), claim.getClaimNumber());
		} else if(claim.getFailureReportPending() == null || (claim.getFailureReportPending() != null && !claim.getFailureReportPending())) {
			addActionMessage(messageKey.toString());
		}
        if(claim.getFailureReportPending() != null && claim.getFailureReportPending()){
            addActionWarning("success.claim.failureReportPending");
        }
        setWarningRequired(warning);
		if (!claim.getType().getType()
				.equalsIgnoreCase(ClaimType.CAMPAIGN.getType())
				&& !claim.getState().getState()
						.contains(ClaimState.DENIED.getState())
				&& !claim.getState().getState()
						.contains(ClaimState.CLOSED.getState())
				&& !claim.getState().getState()
						.contains(ClaimState.DENIED.getState())
				&& !claim.getState().getState()
						.contains(ClaimState.INVALID.getState())) {
			if (contractService.canAutoInitiateRecovery(claim)) {
				partReturnProcessingService.autoStartRecoveryProcess(claim);
			}
		}		
		claimService.updateClaim(claim);
		/*if(!isLoggedInUserADealer()){
		paymentAsyncService.startAsyncPayment(claim);
		}*/
		return SUCCESS;
	}
	

	private boolean isClaimIsForCanadianDealer(Long id) {
		ServiceProvider dealership = orgService.findDealerById(id);
		return dealership.getAddress().getCountry().equalsIgnoreCase(CANADA_COUNTRY_CODE);
	}

	protected void initiatePartReturnToDealer(TaskView task)
		{	   
        if(null != task.getTakenTransition() && (task.getTakenTransition().equalsIgnoreCase("Accept") || 
        		task.getTakenTransition().equalsIgnoreCase("Hold")  || 
        		task.getTakenTransition().equalsIgnoreCase("Transfer") || task.getTakenTransition().equalsIgnoreCase("Deny")))
        		{
        	//Capture the part to be returned
        	for(HussmanPartsReplacedInstalled hussManPart : task.getClaim().getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()){
        		for(OEMPartReplaced replacedPart : hussManPart.getReplacedParts()){
        			if(replacedPart.isPartReturnToDealer()){
        				//Initiate return request
        				partReturnProcessingService.initiateReturnToDealer(task.getClaim(), replacedPart);        			
        			//To set the status of part return
        			replacedPart.setPartAction1(new PartReturnAction(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER.getStatus()
             				,replacedPart.getNumberOfUnits()));
        			this.partReturnService.updatePartStatus(replacedPart);
        			this.partReplacedService.updateOEMPartReplaced(replacedPart);
        			}
        		}
        	}        	
        }
	}

	protected void setReasonsList(Claim claim) {
		Iterator<Long> resonItr = putOnHoldReasonList.iterator();		
		Long tempId;
		List<PutOnHoldReason> putOnHoldList = new ArrayList<PutOnHoldReason>();
		List<RequestInfoFromUser> reqInfoList = new ArrayList<RequestInfoFromUser>();
		List<RejectionReason> rejList = new ArrayList<RejectionReason>();
		if(putOnHoldReasonList.size()>0 && (!(putOnHoldReasonList.size()==1 && putOnHoldReasonList.get(0)==null))){			
			while(resonItr.hasNext()){				
				tempId = resonItr.next();
				if(null!=tempId){
					PutOnHoldReason putOnHoldReason = new PutOnHoldReason();
					putOnHoldReason.setId(tempId);
					putOnHoldList.add(putOnHoldReason);					
				}				
			}
			claimState="label.common.putOnHold";					
			this.task.getClaim().getActiveClaimAudit().setPutOnHoldReasons(putOnHoldList);
			claim.getActiveClaimAudit().setPutOnHoldReasons(putOnHoldList);
		}
		
		if(requestInfoFromDealerList.size()>0 && (!(requestInfoFromDealerList.size()==1 && requestInfoFromDealerList.get(0)==null))){
			resonItr = requestInfoFromDealerList.iterator();
			while(resonItr.hasNext()){
				tempId = resonItr.next();
				if(null!=tempId){
					RequestInfoFromUser requestInfoFromUser = new RequestInfoFromUser();
					requestInfoFromUser.setId(tempId);
					reqInfoList.add(requestInfoFromUser);
				}
			}
			claimState="label.common.reqInfoFromDealer";			
			this.task.getClaim().getActiveClaimAudit().setRequestInfoFromUser(reqInfoList);		
			claim.getActiveClaimAudit().setRequestInfoFromUser(reqInfoList);
		}
		
		if(rejectionReasonsList.size()>0 && (!(rejectionReasonsList.size()==1 && rejectionReasonsList.get(0)==null))){
			resonItr = rejectionReasonsList.iterator();
			while(resonItr.hasNext()){
				tempId = resonItr.next();								
				if(null!=tempId){
					RejectionReason rejectionReason = new RejectionReason();
					rejectionReason.setId(tempId);
					rejList.add(rejectionReason);
				}
			}
			claimState="label.common.denyReasons";
			this.task.getClaim().getActiveClaimAudit().setRejectionReasons(rejList);
			claim.getActiveClaimAudit().setRejectionReasons(rejList);
		}		
	}

	private void setEligibilityToShare(Claim claim) {
		List<Document> attachments = this.task.getClaim().getAttachments();
		if (attachments != null) {
			for (Document doc : attachments) {
				try {
					if(doc.getIsEligibilityToShare()){
						doc.setIsSharedWithSupplier(Boolean.TRUE);
					}
					documentService.save(doc);
				} catch (Exception e) {
					logger.error("setEligibilityToShare EX : " , e);
				}
			}

		}

	}

	protected void prepareReplacedInstalledParts(Claim claim) {
		claimSubmissionUtil.prepareReplacedInstalledParts(claim);
	}

	public String refreshOnEdit() {
		Claim theClaim = null;
		try {
			theClaim = this.task.getClaim();
		} catch (Exception e) {
			logger.error("Exception EX : " + e);
			return FATAL_ERROR;
		}
		logger.info("UNX: IN refreshOnEdit Claim " + theClaim);
		if (!isProcessorReview()) {
			
		}

		setPolicyOnClaimedItems(theClaim);
		if (theClaim.getServiceInformation().getServiceDetail()
				.getLaborPerformed() != null) {
			for (LaborDetail labor : theClaim.getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				if (labor.getEmptyAdditionalHours() != null
						&& labor.getEmptyAdditionalHours()) {
					labor.setAdditionalLaborHours(null);
				}
			}
		}
		setTotalLaborHoursForClaim(theClaim);
		computePayment(theClaim);
		if(checkForPaymentSystemErrors(theClaim,null,false)){
			return INPUT;	
		}

		if (theClaim.getForMultipleItems()) {
			setQtyForMultipleItems(theClaim);
		}
		if (isProcessorReview() &&
                null != task.getTakenTransition() && (task.getTakenTransition().equalsIgnoreCase("Accept") || task.getTakenTransition().equalsIgnoreCase("Hold") || task.getTakenTransition().equalsIgnoreCase("Transfer"))) {
			getClaimService().updateOEMPartInformation(theClaim, getInitialReplacedParts());
			this.partReturnProcessingService
					.startPartReturnProcessForAllParts(theClaim);
		}
		setClaimReasons(theClaim, null);
		getClaimService().updateClaim(theClaim);
		if (theClaim.isOfType(ClaimType.CAMPAIGN))
			campaign = theClaim.getCampaign();

		setQtyForUIView(theClaim);
		setTotalLaborHoursForUiView(theClaim);

		return SUCCESS;
	}

	public void prepare() {
		if (isMatchReadApplicable()) {
			List<Country> countries = this.msaService.getCountryList();
			for (Country country : countries) {
				this.countryList.put(country.getCode(), country.getName());
			}
			this.countriesFromMSA = this.msaService.getCountriesFromMSA();
		}


		// claims will not be null only if the user needs activePolicies
		if (this.claimDetails == null) {
			if (getId() != null) {
				this.task = this.taskViewService.getTaskView(Long
						.parseLong(getId()));
				claimDetail = this.task.getClaim();		
				if(ClaimState.ADVICE_REQUEST.equals(claimDetail.getState()))
				{
					this.task.setTakenTransition("Advice");
				}
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claimDetail.getBusinessUnitInfo().getName());
				selectedBusinessUnit  = claimDetail.getBusinessUnitInfo().getName();

				Long numberOfResubmits = getConfigParamService().getLongValue(ConfigName.NUMBER_OF_CLAIM_RESUBMIT_ALLOWED.getName());
				
				if(claimDetail.getNoOfResubmits() >= numberOfResubmits.intValue()){
					showReRequestsForSMR = false;
				}
				initialReplacedParts.addAll(getInitialOEMReplacedParts());
				preparePartReturn(claimDetail);
				
					try {
						getAttributesForClaim(claimDetail);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						logger.error("getAttributesForClaim EX : " , e);
					}
				if (this.selectedContract != null
						&& this.selectedContract.getId() != null) {
					claimDetail.getServiceInformation()
							.setContract(
									this.contractService
											.findContract(this.selectedContract
													.getId()));
				}

			} else if (StringUtils.hasText(getClaimId())
					&& contextValue != null) {
				this.claimDetail = getClaimService()
						.findClaim(new Long(getClaimId()));
				String[] taskNames = this.getTaskNameAsPerClaimState(this.claimDetail);
				TaskInstance taskInstance=null;
				try {
                    if(taskNames != null){
                        for (String taskName: taskNames) {
                            taskInstance = this.workListItemService.findTaskForClaimWithTaskName(
                                    this.claimDetail.getId(), taskName);
                            if (taskInstance != null)
                                break;

                        }
                    }
				} catch(Exception e) {
					logger.error("taskNames EX : " , e);
					//taskInstance = this.workListItemService.findAllClaimSubmissionOpenTasksForClaim(this.claimDetail.getId()).get(0);
				}
				
				//BEGIN
				//Added for ESESA-1791
				if (taskInstance == null){
                    List<String> openTaskNames = new ArrayList<String>();
                    openTaskNames.add(WorkflowConstants.PENDING_PAYMENT_RESPONSE);
                    openTaskNames.add(WorkflowConstants.PENDING_PART_RETURN);
                    taskInstance = this.workListItemService.findTaskForClaimWithTaskNames(this.claimDetail.getId(), openTaskNames);
					if(taskInstance != null)
					{
					    //taskInstance = this.workListItemService.findAllClaimSubmissionOpenTasksForClaim(this.claimDetail.getId()).get(0);
                        //if(taskInstance.getName().equals(WorkflowConstants.PENDING_PAYMENT_RESPONSE)||taskInstance.getName().equals(WorkflowConstants.PENDING_PART_RETURN))
                        //{
                            TaskView task = new TaskView(taskInstance);
                            task.setTakenTransition("editClaim");
                            taskViewService.submitTaskView(task);
                            taskInstance=this.workListItemService
                            .findTaskForClaimWithTaskName(this.claimDetail.getId(),
                                    WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME);
                            cancelPaymentSubmission();
                        //}
					}
				}
				//END
				this.task = new TaskView(taskInstance);
			}
			if (this.claimDetail!=null && this.claimDetail.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled() != null
					&& !this.claimDetail.getServiceInformation().getServiceDetail()
							.getHussmanPartsReplacedInstalled().isEmpty()) {
				prepareReplacedInstalledPartsDisplay(this.claimDetail);
			} else {
				setRowIndex(0);
			}
		    populateBusinessUnitConfigParameters();
            this.autoCheckRecoveryFlag = getConfigParamService().getBooleanValue(ConfigName.CHECK_RECOVERY_FLAG.getName());
            this.anonymousThirdPartyNumber = getConfigParamService().getStringValue(ConfigName.ANONYMOUS_THIRD_PARTY_NUMBER.getName());
            setSubRowIndex(0);
            populateSourceWarehouse();
            
            if(this.claimDetail != null){
            	this.supplierItems = getSupplierItemsForOEMItem(this.claimDetail);
            }
		}
		
		if(claimDetail != null && this.claimDetail.getLatestRecoveryClaim() != null && this.claimDetail.getRecoveryClaims().size() > 0 ){
			User recoveryClaimAssignee = this.workListService
					.getCurrentAssigneeForRecClaim(this.claimDetail
							.getLatestRecoveryClaim().getId());
			if (recoveryClaimAssignee != null
					&& recoveryClaimAssignee.getName() != null) {
				this.claimDetail
						.getLatestRecoveryClaim()
						.setCurrentAssignee(
								getDisplayNameForRecoveryClaimAssignedTo(recoveryClaimAssignee));
			}
		}
		
	}

	private String getDisplayNameForRecoveryClaimAssignedTo(User recoveryClaimAssignee){
		StringBuffer displayName = new StringBuffer();
		displayName.append(recoveryClaimAssignee.getFirstName());
		displayName.append(" ");
		displayName.append(recoveryClaimAssignee.getLastName());
		displayName.append(" (");
		displayName.append(recoveryClaimAssignee.getName());
		displayName.append(')');
		return displayName.toString();
	}
		private void populateSourceWarehouse(){
            if (this.task != null) {
                Claim claim = this.task.getClaim();
                if (claim != null) {
                    if (claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty()) {
                        if (claim.getClaimedItems().get(0).getItemReference() != null
                                && claim.getClaimedItems().get(0).getItemReference()
                                .getReferredInventoryItem() != null) {
                            claim.setSourceWarehouse(claim.getClaimedItems().get(0)
                                    .getItemReference().getReferredInventoryItem().getSourceWarehouse());
                        }
                    }
                }
            }
        }

	protected void prepareReplacedInstalledPartsDisplay(Claim claim) {		
		setRowIndex(claim.getServiceInformation()
				.getServiceDetail().getHussmanPartsReplacedInstalled().size());
	}

	protected void preparePartReturn(Claim claim) {
		if (claim.getServiceInformation() != null) {
			if (claim.getServiceInformation().getServiceDetail() != null) {
				List<OEMPartReplaced> partsReplaced = claim
						.getServiceInformation().getServiceDetail()
						.getReplacedParts();
				for (OEMPartReplaced partReplaced : partsReplaced) {
					if (partReplaced.isPartToBeReturned()) {
						partReplaced.setPartReturn(getPartReturn(partReplaced));
					}
				}
			}
		}
	}

	private String saveRmaInfo(){
		return SUCCESS;
	}
	
	private PartReturn getPartReturn(OEMPartReplaced partReplaced) {
		List<PartReturn> partReturns = partReplaced.getPartReturns();
		for (PartReturn partReturn : partReturns) {
			if (!partReturn.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR)) {
				return partReturn;
			}
		}
		PartReturn partReturn = null;
		if (CollectionUtils.isNotEmpty(partReplaced.getPartReturns())) {
			partReturn = partReplaced.getPartReturns().get(0);
		}
		return partReturn;
	}
   
	public String redirectDetailPage() {
		return detail();
	}

	public boolean isAllRecoveryClaimsClosed(){
		List<RecoveryClaim> supplierRecoveryClaims = this.task.getClaim().getRecoveryClaims();
		for(RecoveryClaim recoveryClaim : supplierRecoveryClaims){
			if (!recoveryClaim.getRecoveryClaimState().getState().toUpperCase().contains(AdminConstants.RECOVERY_CLAIM_STATE_CLOSED.toUpperCase())
					|| recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED.getState())
					|| recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED.getState())
					|| recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NOT_FOR_RECOVERY.getState())
					|| recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.NOT_FOR_RECOVERY_DISPUTED.getState())
					|| recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.REJECTED.getState())) {
				return false;
			}
		}
		return true;
	}

    public boolean isRecoveryClaimFlaggedFor2ndRecovery(){
        List<RecoveryClaim> listOfRecClaims = this.task.getClaim() != null ? this.task.getClaim().getRecoveryClaims() : new ArrayList<RecoveryClaim>();
        for(RecoveryClaim recoveryClaim : listOfRecClaims){
            if(recoveryClaim.isFlagForAnotherRecovery()){
                return  true;
            }
        }
        return false;
    }


    public boolean displayInitiateRecoveryButton() {
		if(this.task.getClaim().getType().equals(ClaimType.CAMPAIGN)){
			return false;
		}

        if(isRecoveryClaimFlaggedFor2ndRecovery()){
            return true;
        }

		if (!isAllRecoveryClaimsClosed()) {
			return false;
		}
		List<RecoveryClaim> supplierRecoveryClaims = this.task.getClaim().getRecoveryClaims();
		if(supplierRecoveryClaims == null || supplierRecoveryClaims.size() == 0){
			return true;
		}
		else{
			if (supplierRecoveryClaims.get(0).getRecoveryClaimInfo().isCausalPartRecovery()
					&& supplierRecoveryClaims.get(0).getRecoveryClaimInfo().getContract().getCollateralDamageToBePaid()) {
				return false;
			}
			ClaimAudit activeClaimAudit = this.task.getClaim().getActiveClaimAudit();
			ClaimAudit claimAuditDuringSubmission = new ClaimAudit();
			for (ClaimAudit claimAudit : this.task.getClaim().getClaimAudits()) {
				if (claimAudit.getState().equals(ClaimState.SUBMITTED)) {
					claimAuditDuringSubmission = claimAudit;
				}
			}
			List<HussmanPartsReplacedInstalled> partsReplacedDuringSubmission = claimAuditDuringSubmission.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
			List<HussmanPartsReplacedInstalled> partsReplacedInActiveClaimAudit = activeClaimAudit.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
			if(partsReplacedInActiveClaimAudit != null && partsReplacedDuringSubmission != null) {
				if (partsReplacedInActiveClaimAudit.size() > partsReplacedDuringSubmission.size()) {
					return true;
				}
				for(int index = 0; index < partsReplacedInActiveClaimAudit.size(); index++){
					if (partsReplacedInActiveClaimAudit.get(index)
							.getReplacedParts().size() > partsReplacedDuringSubmission
							.get(index).getReplacedParts().size()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String detail() {
		if (this.task != null) {						
			setId(Long.toString(this.task.getTaskId()));
			claimDetail = this.task.getClaim();
			syncTrackerService.setInactiveStatusForexistingSyncTrackerIds(claimDetail.getClaimNumber(), SYNC_TYPE);
			claimDetail.setCreditDate(null);
			//fix for SLMSPROD-1392
			TravelDetail travelDetails=claimDetail.getServiceInformation().getServiceDetail().getTravelDetails();
			if(null != travelDetails){
				transientTravelHours=new String();
				if(!org.apache.commons.lang.StringUtils.isEmpty(travelDetails.getHours()))
				{
					transientTravelHours = travelDetails.getHours()+"";
					transientTravelHours = transientTravelHours.replace('.', ':');
				}
			}			
			this.setCommentsForDisplay(claimDetail.getExternalComment());
			if(ClaimState.ADVICE_REQUEST.equals(claimDetail.getState()))
				this.setCommentsForDisplay(claimDetail.getInternalComment());	
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claimDetail.getBusinessUnitInfo().getName());
			if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, claimDetail
					.getForDealer())) {
				Boolean isThirdPartyLogin = orgService
						.isThirdPartyDealerWithLogin(claimDetail.getForDealer()
								.getId());
				if (!isThirdPartyLogin && !claimDetail.isServiceProviderSameAsFiledByOrg()) {
					isThirdParty = true;
				} else if (!isThirdPartyLogin && claimDetail.getFiledBy().isInternalUser()) {
					isThirdParty = true;
				}
			}
			if(claimDetail.getServiceInformation().getServiceDetail().getLaborPerformed()!=null){
				for (LaborDetail labor : claimDetail.getServiceInformation().getServiceDetail().getLaborPerformed()){
					if(labor.getServiceProcedure()!=null&&labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)){
						setRoundUpLaborDetail(labor);
					}
				}
			}
			if(isBuConfigAMER()){
			displayServicingLocationWithBrand(claimDetail); //to display servicinglocation along with brand
			}
			setQtyForUIView(claimDetail);
			setPartForCrossReference(claimDetail);
			setTotalLaborHoursForUiView(claimDetail);
			//setting warning message for business rules related to dual brand comparision
			setWarningMessages(claimDetail);			
			setConfiguredCostCategories(claimDetail);
			if(isReProcess()){
				claimDetail.setAcceptanceReason(null);
				claimDetail.setRejectionReasons(null);
				this.claimService.updateClaim(claimDetail);
			}
			//Changed to not show incidental part
			if(claimDetail.getItemDutyConfig()||claimDetail.getMealsConfig()||claimDetail.getPerDiemConfig()
	    			||claimDetail.getParkingConfig()||claimDetail.getRentalChargesConfig()||claimDetail.getLocalPurchaseConfig()
	    			||claimDetail.getTollsConfig()||claimDetail.getOtherFreightDutyConfig()||claimDetail.getOthersConfig() || claimDetail.getHandlingFeeConfig() || claimDetail.getTransportation()){
	    		this.incidentalsAvaialable=true;
	    	}
			if (!ClaimState.DRAFT.getState().equals(claimDetail.getState().getState()))
				authorizeAllowedActionsForProcessor(claimDetail, getLoggedInUser(), Boolean.TRUE);
			prepareAttributesForClaim(claimDetail);
            preparePartReturn(claimDetail);
            populateBusinessUnitConfigParameters(); 
            if ( AdminConstants.UPLOAD_WARRANTY_CLAIM.equalsIgnoreCase(claimDetail.getSource()) && !claimDetail.isFoc()
    				&& (ClaimState.DRAFT.getState().equalsIgnoreCase(claimDetail.getState().getState()))) {
    			this.messages = this.ruleAdministrationService
    					.executeClaimEntryValidationRules(claimDetail);
    			setValidationResultAsActionMessage();
    		}
            if(claimDetail.getServiceInformation().getPartClaimAttributes().size()>0)
            {
            	claimAttributes=claimDetail.getServiceInformation().getPartClaimAttributes();
            }
            if(claimDetail.getClaimAdditionalAttributes().size()>0 && claimSpecificAttributes.isEmpty()){
            	claimSpecificAttributes.addAll(claimDetail.getClaimAdditionalAttributes());
            }
            if(getLoggedInUser().hasRole("processor"))
            {
            	populateApplicablePolicyCodes(claimDetail);
            			
            }            
        	if (!ClaimState.DRAFT.getState().equals(claimDetail.getState().getState())) {
    			if(getLoggedInUser().hasRole(Role.WARRANTY_SUPERVISOR)){
    				isLoggedInUserEligibleForLateFeeApproval = true;
    			}
    		}
        	if(!claimDetail.getState().equals(ClaimState.DRAFT))
        	{   
        		claimDetail.setStateMandate(claimSubmissionUtil.getStateMandate(claimDetail));
        		claimSubmissionUtil.getDeductableAmount(claimDetail);
        	}
    		// End
    		// preparing list for displaying reasons of putonhold, rejected and request info from dealer claims
    		if(null != claimDetail.getRejectionReasons() && claimDetail.getRejectionReasons().size()>0 && (claimDetail.getState().equals(ClaimState.DENIED) || claimDetail.getState().equals(ClaimState.DENIED_AND_CLOSED))){
    			for(RejectionReason rejectionReason : claimDetail.getRejectionReasons()){
    				reasonsList.add(rejectionReason.getDescription());
    			}			
    		}else if(null != claimDetail.getRequestInfoFromUser() && claimDetail.getRequestInfoFromUser().size()>0  && (claimDetail.getState().equals(ClaimState.FORWARDED) || claimDetail.getState().equals(ClaimState.EXTERNAL_REPLIES))){
    			for(RequestInfoFromUser requestInfoFromUser : claimDetail.getRequestInfoFromUser()){
    				reasonsList.add(requestInfoFromUser.getDescription());
    			}
    		}else if(null != claimDetail.getPutOnHoldReasons() && claimDetail.getPutOnHoldReasons().size()>0  && claimDetail.getState().equals(ClaimState.ON_HOLD)){
    			for(PutOnHoldReason putOnHoldReason : claimDetail.getPutOnHoldReasons()){
    				reasonsList.add(putOnHoldReason.getDescription());
    			}
    		}	
    		//method for populating values of multi select dropdowns
    		prepareReasonsList();    		    		    		
            return SUCCESS;
		}
		if (getId() == null) {
			return ERROR;
		}
		if (InventoryType.RETAIL.getType().equals(
				task.getClaim().getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getType().getType())
				&& isMatchReadApplicable()
				&& !(ClaimType.CAMPAIGN.getType().equals(task.getClaim()
						.getType().getType()))) {
			prepareMatchReadInfoForShow();
		}
		this.task = this.taskViewService.getTaskView(Long.valueOf(getId()));		
		return SUCCESS;
	}

	private void setWarningMessages(Claim theClaim) {
		StringBuffer replacedPartsBrand = new StringBuffer();
		StringBuffer replacedPartNumbers= new StringBuffer();	
		StringBuffer installedPartBrands = new StringBuffer();
		StringBuffer installedPartNumbers = new StringBuffer();
		if (theClaim.getServiceInformation() != null) {
			if (theClaim.getServiceInformation().getServiceDetail() != null) {
				List<OEMPartReplaced> partsReplaced = theClaim
						.getServiceInformation().getServiceDetail()
						.getReplacedParts();
					for (OEMPartReplaced partReplaced : partsReplaced) {					        
						 if(null != partReplaced.getBrandItem() && null != partReplaced.getBrandItem().getBrand() && !validateBrandType(partReplaced.getBrandItem().getBrand(),theClaim.getBrand())){									 
							 replacedPartsBrand.append(", "+partReplaced.getBrandItem().getBrand());											 
							 replacedPartNumbers.append(", "+partReplaced.getBrandItem().getItemNumber()+"-"+partReplaced.getBrandItem().getItem().getDescription());								
							}									
					}                                
				if(replacedPartsBrand.length()>1){
								theClaim.setReplacedPartsBrand(replacedPartsBrand.substring(1).toString());
								theClaim.setReplacedPartNumbers(replacedPartNumbers.substring(1).toString());
							}
				List<InstalledParts> partsInstalled = theClaim
						.getServiceInformation().getServiceDetail()
						.getInstalledParts();					
				for (InstalledParts partInstalled : partsInstalled) {					
					 if(null != partInstalled.getBrandItem() && null != partInstalled.getBrandItem().getBrand() && !validateBrandType(partInstalled.getBrandItem().getBrand(),theClaim.getBrand())){								
						 installedPartBrands.append(", "+partInstalled.getBrandItem().getBrand()) ;						 
						 installedPartNumbers.append(", "+partInstalled.getBrandItem().getItemNumber()+"-"+partInstalled.getBrandItem().getItem().getDescription());
						}					
				}				
				if(installedPartBrands.length()>1){
					theClaim.setInstalledPartBrands(installedPartBrands.substring(1).toString());
					theClaim.setInstalledPartNumbers(installedPartNumbers.substring(1).toString());
				}
				}
			}
	}

	private void prepareReasonsList() {
		//populating reasons list for put on hold 
		Long id=0l;
		List dropDownList =claimDetail.getPutOnHoldReasons();
		Iterator dropDownItr = dropDownList.iterator();
		if(null !=dropDownList  && claimDetail.getState().equals(ClaimState.ON_HOLD)){
			while(dropDownItr.hasNext()){
				id = ((PutOnHoldReason)dropDownItr.next()).getId();
				putOnHoldReasonList.add(id);
				selectedReasons = selectedReasons+","+id;
			}
			claimState="label.common.putOnHold";	
		}
		//populating list for requesting information from dealer
		dropDownList.clear();
		dropDownList =claimDetail.getRequestInfoFromUser();
		if(null !=dropDownList   && (claimDetail.getState().equals(ClaimState.FORWARDED) || claimDetail.getState().equals(ClaimState.EXTERNAL_REPLIES))){
			dropDownItr = dropDownList.iterator();
			while(dropDownItr.hasNext()){
				requestInfoFromDealerList.add(((RequestInfoFromUser)dropDownItr.next()).getId());
				selectedReasons = selectedReasons+","+id;
			}
			claimState="label.common.reqInfoFromDealer";	
		}
		//populating reasons list for rejection
		dropDownList.clear();
		dropDownList =claimDetail.getRejectionReasons();
		if(null !=dropDownList && (claimDetail.getState().equals(ClaimState.DENIED) || claimDetail.getState().equals(ClaimState.DENIED_AND_CLOSED))){
			dropDownItr = dropDownList.iterator();
			while(dropDownItr.hasNext()){
				rejectionReasonsList.add(((RejectionReason)dropDownItr.next()).getId());
				selectedReasons = selectedReasons+","+id;
			}
			claimState="label.common.denyReasons";	
		}
		if(claimDetail.getState().getState().equals(ClaimState.FORWARDED)){
			claimState="label.common.forwarded";
		}
	}

	private void populateApplicablePolicyCodes(Claim claimDetail) {	
	
		if (!claimDetail.isOfType(ClaimType.CAMPAIGN) && !claimDetail.isNcr() && !claimDetail.isNcrWith30Days()) {
			List<String> codes = claimSubmissionUtil
					.fetchApplicablePolicyCodesForClaim(claimDetail);
			if (codes != null && codes.size() > 0) {
				this.policyCodes = codes;
			}
			this.policyCodes.add(POLICY);
		}
		//Fix for SLMSPROD-1268 
		if(!claimDetail.isOfType(ClaimType.CAMPAIGN) && claimDetail.getClaimedItems() != null && claimDetail.getClaimedItems().size()>0){
			ClaimedItem claimedItem = claimDetail.getClaimedItems().get(0);
			if(null != claimedItem.getItemReference() && claimedItem.getItemReference().isSerialized()){
				InventoryItem referredInventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
				if(null != referredInventoryItem)				{
				if(InventoryType.STOCK.getType().equalsIgnoreCase(referredInventoryItem.getType().getType())
						|| (null !=referredInventoryItem.getDeliveryDate() && claimDetail.getRepairDate().isBefore(referredInventoryItem.getDeliveryDate().nextDay()))){
					this.policyCodes.add(STOCK);
				}				}
			}
		}
	}

	public boolean getIncidentalsAvaialable() {
		return incidentalsAvaialable;
	}

	public void setIncidentalsAvaialable(boolean incidentalsAvaialable) {
		this.incidentalsAvaialable = incidentalsAvaialable;
	}

	public boolean isJobCodeFaultCodeEditable() {
		String formName = task.getBaseFormName();
		if(formName != null) {
			if(formName.equalsIgnoreCase(WorkflowConstants.DRAFT_CLAIM.replaceAll(" ", "_"))
					|| (formName.equalsIgnoreCase(WorkflowConstants.SERVICE_MANAGER_REVIEW.replaceAll(" ", "_")) ))
				return true;
			else if(formName.equalsIgnoreCase(WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME.replaceAll(" ", "_"))
					&& isEditableForProcessor())
				return true;
			else if(formName.equalsIgnoreCase(WorkflowConstants.FORWARDED.replaceAll(" ", "_"))
					&&isEditableForDealer())
				return true;
			else if(formName.equalsIgnoreCase(WorkflowConstants.PART_SHIPPED_NOT_RECEIVED.replaceAll(" ", "_"))
					&& isPartShippedNotRcvd())
					return true;
		}
		return false;
	}

    private void prepareAttributesForClaim(Claim claim) {
    	claimSubmissionUtil.prepareAttributesForClaim(claim, this.task.getBaseFormName(), isPartShippedNotRcvd());
    }

	@SuppressWarnings("unchecked")
	protected Boolean authorizeAllowedActionsForProcessor(Claim claim,
			User loggedInUser, Boolean warningDisplayReq) {
		Boolean userAllowedToPerformAction = Boolean.FALSE;
		authorizeAllowedAction = Boolean.FALSE;
		if (claim.getLoaScheme() == null
				&& allowedStateForLOA(claim.getState())) {
			ValidationResults validationResults = this.ruleAdministrationService
					.executeProcessorAuthorityRules(claim, loggedInUser);
			// Checking whether user can perform the action or not
			Set<User> listOfAllowedUsers = new HashSet<User>(10);
			Set<UserCluster> assignedToList = (Set<UserCluster>) validationResults
			.getAssignStateToUserGroupMap().get(ASSIGN_RULES_STATE);
			if (assignedToList != null && !assignedToList.isEmpty()) {
				int intersectIter = 0;
				Set<User> topUsers = new HashSet<User>(10);
				for (Iterator iterator = assignedToList.iterator(); iterator.hasNext();)
				{
					UserCluster userCluster = (UserCluster) iterator.next();
			        if (intersectIter>0)
			        	topUsers = new HashSet(CollectionUtils.intersection(topUsers, userCluster.getIncludedUsers()));
			        else
			        	topUsers = userCluster.getIncludedUsers();
			        intersectIter++;
				}
				userAllowedToPerformAction = new Boolean(topUsers.contains(loggedInUser));
			}

			if (warningDisplayReq)
				authorizeClaimAndPopulateUserMessages(loggedInUser, validationResults);
		}
		return userAllowedToPerformAction;
	}

	protected boolean allowedStateForLOA(ClaimState claimState) {
		return ClaimState.PROCESSOR_REVIEW.equals(claimState)
				|| ClaimState.REPLIES.equals(claimState)
				|| ClaimState.REOPENED.equals(claimState)
				|| ClaimState.EXTERNAL_REPLIES.equals(claimState)
				|| ClaimState.ON_HOLD.equals(claimState)
				|| ClaimState.ON_HOLD_FOR_PART_RETURN.equals(claimState)
				|| ClaimState.REJECTED_PART_RETURN.equals(claimState)
				|| ClaimState.TRANSFERRED.equals(claimState)
				|| ClaimState.APPROVED.equals(claimState)
				|| ClaimState.APPEALED.equals(claimState)
				|| ClaimState.CP_TRANSFER.equals(claimState)
				|| ClaimState.REJECTED_PART_RETURN.equals(claimState)
				|| ClaimState.CP_REVIEW.equals(claimState);
	}

	@SuppressWarnings("unchecked")
	protected void authorizeClaimAndPopulateUserMessages(User loggedInUser, ValidationResults validationResults) {
		Set<UserCluster> assignedToList = (Set<UserCluster>) validationResults
				.getAssignStateToUserGroupMap().get(ASSIGN_RULES_STATE);
		for (String warning : validationResults.getWarnings()) {
			if (StringUtils.hasText(warning))
			{
				int pos1 = warning.indexOf(")", 1);
				int pos2 = warning.indexOf("[");
				/*String ruleContext = "";
				if (warning.indexOf("[")!=-1)
					ruleContext = warning.substring(warning.indexOf("[")+1,warning.indexOf("]")-1) + ": ";*/
				warning = warning.substring(pos1 + 1, pos2);
				loaWarningMessages.add(warning);
			}
		}
		Set<User> listOfAllowedUsers = new HashSet<User>(10);
		if (assignedToList != null && !assignedToList.isEmpty()) {
			int intersectIter = 0;
			Set<User> topUsers = new HashSet<User>(10);
			for (Iterator iterator = assignedToList.iterator(); iterator.hasNext();)
			{
				UserCluster userCluster = (UserCluster) iterator.next();
		        if (intersectIter>0)
		        	topUsers = new HashSet(CollectionUtils.intersection(topUsers, userCluster.getIncludedUsers()));
		        else
		        	topUsers = userCluster.getIncludedUsers();
		        intersectIter++;
			}
			authorizeAllowedAction = new Boolean(topUsers.contains(loggedInUser));
		}
	}

	protected String getSelectedJobsJSON() {
		JSONArray selectedJobs = new JSONArray();
		ServiceInformation serviceInformation = this.task.getClaim()
				.getServiceInformation();
		if (serviceInformation == null) {
			return "[]";
		}
		List<LaborDetail> labourDetails = serviceInformation.getServiceDetail()
				.getLaborPerformed();
		for (LaborDetail detail : labourDetails) {
			Map<String, Object> row = new HashMap<String, Object>();
			ServiceProcedure serviceProcedure = detail.getServiceProcedure();			
			if(serviceProcedure.getD().isActive()){				
				row.put(CODE, serviceProcedure.getDefinedFor().getDefinition()
						.getCode());
				row.put(COMPLETE_CODE, serviceProcedure.getDefinition()
						.getCode());
				row.put(ID, serviceProcedure.getDefinedFor().getId());
				row.put(SERVICE_PROCEDURE_ID, serviceProcedure.getId());
				row.put(LABOUR_HRS, serviceProcedure.getSuggestedLabourHours());
				row.put(LABEL, serviceProcedure.getDefinedFor().getDefinition()
						.getName());
				row.put(ADDITIONAL_HOURS, detail.getAdditionalLaborHours());
				row.put(SPECIFIED_HOURS, detail.getSpecifiedHoursInCampaign());
				row.put(NODE_TYPE, NODE_TYPE_LEAF);
				row.put(WRAPPER_ID, detail.getId());
				row.put(JOB_REASON, detail.getReasonForAdditionalHours());
				row.put(JOB_CODE_DESCRIPTION, serviceProcedure.getDefinedFor().getJobCodeDescription());
                row.put(LABOR_HRS_ENTERED, detail.getLaborHrsEntered());
                row.put(HAS_ADDITIONAL_ATTRIBUTES, (detail.getClaimAttributes().isEmpty()?"false":"true"));
                selectedJobs.put(row);
			}		
		}
		return selectedJobs.toString();
	}

	public String findAttributesForFaultCode() throws JSONException {
		if (this.faultCodeID != null) {
			List<AdditionalAttributes> additionalAttributes = getClaimService().findAdditionalAttributesForFaultCode(claimDetails, new Long(faultCodeID));
			JSONArray attrArray = new JSONArray();
			JSONObject attributePresent = new JSONObject();
			if (!additionalAttributes.isEmpty()) {
				attributePresent.append("Claim", this.claimDetails);
			} else {
				attributePresent.append("Claim", "-");
			}
			attrArray.put(attributePresent);
			this.jsonString = attrArray.toString();
		}
		return SUCCESS;
	}

	public String getAttributesForFaultCode() throws JSONException {
		if (this.faultCodeID != null) {
			getClaimService().prepareAttributesForFaultCode(claimDetails, new Long(faultCodeID));
			this.claimAttributes = this.claimDetails
					.getServiceInformation().getFaultClaimAttributes();
		}
		return SUCCESS;
	}

	public String findAttributesForCausalPart() throws JSONException,
			CatalogException {
		if (this.partNumber != null) {
			Item item = getCatalogService().findItemOwnedByManuf(this.partNumber);
			List<AdditionalAttributes> additionalAttributes = this.attributeAssociationService
					.findAttributesForItem(item.getId(), this.claimDetails
							.getType());
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.claimDetails.getBusinessUnitInfo().getName());
			//perf fix: look for contracts only if any attribute is defined for supplier at all
			if(attributeAssociationService.isAnyAttributeConfiguredForSupplier())
			{
				List<Contract> applicableContracts = this.contractService
						.findContract(this.claimDetails, item, true);
				if (applicableContracts != null && applicableContracts.size() == 1) {
					Supplier supplier = applicableContracts.get(0).getSupplier();
					List<AdditionalAttributes> supplierAttributes = this.attributeAssociationService
							.findAttributesForSupplier(supplier.getId(),
									this.claimDetails.getType());
					if (supplierAttributes != null && !supplierAttributes.isEmpty()) {
						additionalAttributes.addAll(supplierAttributes);
					}
				}
			}
			for (AdditionalAttributes additionalAttribute : additionalAttributes) {
				this.claimAttributes.add(new ClaimAttributes(
						additionalAttribute, null));
			}
			JSONObject attributePresent = new JSONObject();
			if (!additionalAttributes.isEmpty()) {
				attributePresent.put("Claim", this.claimDetails);
				this.jsonString = attributePresent.toString();
			}
		}
		return SUCCESS;
	}

	public String getAttributesForCausalPart() throws JSONException,
			CatalogException {
		if (this.partNumber != null) {
			getClaimService().prepareAttributesForCausalPart(claimDetails, partNumber);
			claimAttributes = claimDetails.getServiceInformation().getPartClaimAttributes();
		}
		return SUCCESS;
	}
	
	public String findAttributesForJobCode() throws JSONException {
		if (this.serProcedureId != null) {
			List<AdditionalAttributes> additionalAttributes = this.attributeAssociationService
					.findAttributesForJobCode(Long
							.parseLong(this.serProcedureId), this.claimDetails
							.getType());
			JSONArray attrArray = new JSONArray();
			JSONObject attributePresent = new JSONObject();
			if (!additionalAttributes.isEmpty()) {
				attributePresent.append("Claim", this.claimDetails);
			} else {
				attributePresent.append("Claim", "-");
			}
			attributePresent.append("indexId", getIndexId());
			attrArray.put(attributePresent);
			this.jsonString = attrArray.toString();
		}
		return SUCCESS;
	}
	
	
	
	
	public String getAttributesForJobCode() throws JSONException {
		LaborDetail laborDetail = null;
		try {
				laborDetail = claimDetails.getServiceInformation().getServiceDetail()
										.getLaborPerformed().get(Integer.parseInt(getIndexId()));
		}catch(Exception e) {
			laborDetail = new LaborDetail();
		}
		getClaimService().prepareAttributesForJobCode(claimDetails, laborDetail, new Long(serProcedureId));
		claimAttributes = laborDetail.getClaimAttributes();
		return SUCCESS;
	}

	public String getAttributesForPart() throws JSONException, CatalogException {
		if (this.partNumber != null) {
			OEMPartReplaced oemPartReplaced = null;
			try {
				oemPartReplaced = claimDetails.getServiceInformation().getServiceDetail()
											.getOEMPartsReplaced().get(Integer.parseInt(getIndexId()));
			}catch(Exception e) {
				oemPartReplaced = new OEMPartReplaced();
			}
			getClaimService().prepareAttributesForReplacedPart(claimDetails, oemPartReplaced, partNumber);
			claimAttributes = oemPartReplaced.getClaimAttributes();
		}
		return SUCCESS;
	}

	public void setTaskViewService(TaskViewService taskViewService) {
		this.taskViewService = taskViewService;
	}

    public TaskViewService getTaskViewService() {
        return taskViewService;
    }

    /**
	 * Dummy method which is used to show the active policies. Jsp's will
	 * directly call method {@link #getActivePolicies(Claim)}
	 */
	public String showActivePolicies() {
		return SUCCESS;
	}

	public Collection<PolicyVO> getActivePolicies() throws PolicyException {
		if (this.task == null && this.claimDetails == null) {
			return new ArrayList<PolicyVO>();
		}
		if (this.claimDetails != null) {
			return getActivePolicies(this.claimDetails);
		} else {
			return getActivePolicies(this.task.getClaim());
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<PolicyVO> getActivePolicies(final Claim claim)
			throws PolicyException {
		// non-serialized Machine Claims
		/*
		 * if(claim.getType().equals("Machine") &&
		 * !claim.getItemReference().isSerialized()){ return new ArrayList<PolicyVO>(); }
		 */
		Collection<PolicyVO> policyVOs = new ArrayList<PolicyVO>();
		Transformer policyTransformer = new Transformer() {
			public Object transform(Object policy) {
				return new PolicyVO((Policy) policy, claim);
			}
		};

		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			for (Policy policy : this.policyService
					.findActivePolicies(claimedItem)) {
				policyVOs.add((PolicyVO) policyTransformer.transform(policy));
			}
		}

		return policyVOs;
	}

	static class PolicyVO {
		public String code;

		public String description;

		public String warrantyType;

		public CalendarDuration warrantyPeriod;

		public int monthsCovered;

		public int hoursCovered;

		public PolicyVO(Policy policy, Claim claim) {
			this.code = policy.getCode();
			this.description = policy.getDescription();
			this.warrantyType = policy.getWarrantyType().getType();

			PolicyDefinition policyDefinition = null;
			if (policy instanceof PolicyDefinition) {
				policyDefinition = (PolicyDefinition) policy;
				CalendarDate purchaseDate = claim.getInstallationDate();
				CalendarDate warrantyEndDate = purchaseDate.plus(
						Duration.months(policyDefinition.getCoverageTerms()
								.getMonthsCoveredFromDelivery())).previousDay();
				this.warrantyPeriod = new CalendarDuration(purchaseDate,
						warrantyEndDate);
				this.hoursCovered = policyDefinition.getCoverageTerms()
						.getServiceHoursCovered();
			} else {
				RegisteredPolicy registeredPolicy = (RegisteredPolicy) policy;
				policyDefinition = (registeredPolicy).getPolicyDefinition();
				this.warrantyPeriod = registeredPolicy.getWarrantyPeriod();
				this.hoursCovered = ((RegisteredPolicy) policy)
						.getLatestPolicyAudit().getServiceHoursCovered();
			}
			this.monthsCovered = policyDefinition.getCoverageTerms()
					.getMonthsCoveredFromDelivery();

		}
	}

	public String getSelectedJobsJsonString() {
		return getSelectedJobsJSON();
	}
	

	protected void validateTechnician(Claim claim){
		if (claim.getServiceInformation().getServiceDetail() != null){
			if((claim.getServiceInformation().getServiceDetail().getTechnician() == null ||
					(claim.getServiceInformation().getServiceDetail().getTechnician() != null 
							&& claim.getServiceInformation().getServiceDetail().getTechnician().getId() == null))){
				addActionError("error.newClaim.technicianRequired");
			}
		}	
	}
	
	protected void validateTechnicianForAMER(Claim claim) {
		if (isBuConfigAMER() && !(isLAMDealer(claim) && !isTechRequiredForLAMDealers())) {
			if (claim.getServiceInformation().getServiceDetail() != null) {
				if (!claim.getType().equals(ClaimType.PARTS)
						&& !StringUtils.hasText(claim.getServiceInformation()
								.getServiceDetail().getServiceTechnician())) {
					addActionError("error.newClaim.technicianRequired");
				}
				if (claim.getType().equals(ClaimType.PARTS)
						&& isTechRequiredForPartsClaim()
						&& !StringUtils.hasText(claim.getServiceInformation()
								.getServiceDetail().getServiceTechnician())) {
					addActionError("error.newClaim.technicianRequired");
				}
			}
		}
	}
	
	public boolean isLAMDealer(Claim claim) {
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		DealerGroup forDealerGroup = dealerGroupService
				.findDealerGroupsForWatchedDealership(forDealer);
		boolean isLamDealer = false;
		if (forDealerGroup != null) {
			isLamDealer = (forDealerGroup.getName().equalsIgnoreCase(
					AdminConstants.LATIN_AMERICAN_DEALERS) || forDealerGroup
					.getDescription()
					.equalsIgnoreCase(AdminConstants.LATIN_AMERICAN_DEALERS)) ? true : false;
		}
		return isLamDealer;
	}
	
	private boolean isTechRequiredForLAMDealers() {
		return getConfigParamService().getBooleanValue(
				ConfigName.TECHNICIAN_CERTIFICATION_FOR_LAM_DEALERS.getName());
	}


	protected void checkPayment(Claim claim){
		if(claim.getPayment() != null) {
			for (Iterator<LineItemGroup> iter = claim.getPayment().getLineItemGroups().iterator(); iter.hasNext();) {
				LineItemGroup lineItemGroup = iter.next();
				if(lineItemGroup.getName() == null) {
					iter.remove();
				}
			}
		}
	}
	
	protected void checkAuthNumber(Claim claim){
				
    	if( claim!= null && claim.isCmsAuthCheck()!=null && claim.isCmsAuthCheck() && !StringUtils.hasText(claim.getAuthNumber())){
		    addActionError("error.newClaim.authNumberRequired");
		}
    }
	
	protected void validateMandatedComments(Claim claim){
	if (((isProcessorReview() || isServiceManagerReview()
			|| isServiceManagerResponse()) && !isBuConfigAMER()) || isAdviceRequest() || isForwarded()) {
		if ((this.mandatedComments != null)
				&& !this.mandatedComments.isEmpty()) {
			if (this.mandatedComments.contains("internalComments")) {
				if (((claim.getInternalComment() == null)
						|| "".equals(claim.getInternalComment().trim())) && (!("Hold".equalsIgnoreCase(this.task
								.getTakenTransition()) || "Forward to Dealer".equalsIgnoreCase(this.task
										.getTakenTransition())))) {
					addActionError("error.newClaim.internalCommentRequired");
				}
			}
			if (this.mandatedComments.contains("externalComments")) {
				if ((claim.getExternalComment() == null)
						|| "".equals(claim.getExternalComment().trim())
						&& !("Transfer".equalsIgnoreCase(this.task.getTakenTransition()))) {
					addActionError("error.newClaim.externalCommentRequired");
				}
			}
		}
	  }
	}
	
	protected void validateAccountabilityCode(Claim claim){
		if (isProcessorReview()
				&& ("Accept".equalsIgnoreCase(this.task.getTakenTransition())
						|| "Deny".equalsIgnoreCase(this.task.getTakenTransition()))
				&& (claim.getAccountabilityCode() == null || (claim.getAccountabilityCode() != null
						&& claim.getAccountabilityCode().getCode() == null))) {
			addActionError("error.newClaim.accountabilityCode");
		}
	}
	
	protected void validateActionToBeTaken(Claim claim){
		if (isProcessorReview()
				&& !StringUtils.hasText(this.task.getTakenTransition())) {
			addActionError("error.newClaim.action");
		} else {
			if (isProcessorReview()
					&& (("Seek Advice".equalsIgnoreCase(this.task
							.getTakenTransition()) && !StringUtils
							.hasText(this.task.getSeekAdviceFrom()))
							||("Seek Review".equalsIgnoreCase(this.task
							.getTakenTransition()) && !StringUtils
							.hasText(this.task.getSeekReviewFrom()))
							|| ("Transfer".equalsIgnoreCase(this.task
									.getTakenTransition()) && !StringUtils
									.hasText(this.task.getTransferTo()))
							|| ("Deny".equalsIgnoreCase(this.task
									.getTakenTransition()) && (rejectionReasonsList.size()==0 || ((rejectionReasonsList.size()==1 && rejectionReasonsList.get(0)==null))))																				
							|| ("Hold".equalsIgnoreCase(this.task
									.getTakenTransition()) && (putOnHoldReasonList.size()==0 || ((putOnHoldReasonList.size()==1 && putOnHoldReasonList.get(0)==null))))									
												
							|| ("Forward to Dealer".equalsIgnoreCase(this.task
									.getTakenTransition()) &&  (requestInfoFromDealerList.size()==0 || ((requestInfoFromDealerList.size()==1 && requestInfoFromDealerList.get(0)==null))))																			
											|| ("Accept"
							.equalsIgnoreCase(this.task
									.getTakenTransition()) && (claim
							.getAcceptanceReason() == null || (claim
							.getAcceptanceReason() != null && !StringUtils
							.hasText(claim.getAcceptanceReason()
									.getDescription())))) || checkActionForCP(claim))) {
				addActionError("error.newClaim.actionValue");				
			}
		}		
	}
	@Override	
	public void validate() {
		Claim claim = null;	
		String action = ActionContext.getContext().getName();
		try {
			claim = this.task.getClaim();
			if(loaSchemeApplicable && (claim.getLoaScheme() == null || !StringUtils
					.hasText(claim.getLoaScheme().getCode()))){
				addActionError("error.loaSchemeRequired");
			}
			if(claim.getLoaScheme() != null && eligibleLOAProcessors.size()==0){
				eligibleLOAProcessors = claim.getLoaScheme().getEligibleLOAProcessorList();
			}
				claim.setCreditDate(null);
			if (!ClaimType.CAMPAIGN.getType().equals(claim.getType().getType()) && this.task != null
					&& (("Accept".equalsIgnoreCase(this.task
							.getTakenTransition()) || ("Re-process"
							.equalsIgnoreCase(this.task.getTakenTransition())))
							&& org.apache.commons.lang.StringUtils.isEmpty(this.task.getClaim()
											.getPolicyCode())) && !this.task.getClaim().isNcr() && !this.task.getClaim().isNcrWith30Days() && !amerClaimAcceptedEarlier(this.task.getClaim())) {
				addActionError("error.claim.invalidPolicyCode");
			}
		} catch (Exception ex) {
			logger.error("LOGGING REQUEST PARAM: "
					+ request.getParameter("task"));
			return;
		}
		
//		if (getLoggedInUser().hasRole("processor")) {
//            if (!StringUtils.hasText(claim.getWorkOrderNumber())) {
//            	addActionError("error.newClaim.workorderNoRequired");
//            }
//        }
		// to check payment only when not a draft claim. Changes done for issue HUSS-863
		checkPayment(claim);
	
		
		//validation check to allow percentage with 6 decimal places TKTSA-1284
		    if(claim.getPayment() != null){
			for(Iterator<LineItemGroup> iter=claim.getPayment().getLineItemGroups().iterator(); iter.hasNext(); ){
				LineItemGroup lineItemGroup = iter.next();
				if(lineItemGroup.getPercentageAcceptance().scale()>6){
			            addActionError("label.error.acceptancePercentageDecimal");
			            break;
				}
			}
		    }
		
		    if(isProcessorReview() && task.getTakenTransition() != null &&
					"Accept".equalsIgnoreCase(task.getTakenTransition())){
			if (getLoggedInUser().hasRole(Role.PROCESSOR)
					&& getConfigParamService()
							.getBooleanValue(
									ConfigName.VALIDATE_MARKETING_GROUP_CODES
											.getName()) && claim.getSerialNumber()!=null) {
				validateMarketingGroupCodes(claim, true);
			}
			}
		
        if (!TWMSWebConstants.REFRESH_PAYMENT.equals(action) && !TWMSWebConstants.REFRESH_ACTIONS.equals(action)) {
        	validateMandatedComments(claim);
			validateAccountabilityCode(claim);
			if ("Accept".equalsIgnoreCase(this.task.getTakenTransition()) && claim.getLoaScheme() != null && !eligibleLOAProcessors.contains(getLoggedInUser().getName())) {
				addActionError("error.claim.currentUserNotEligibleToAccept");
			}
			if (isProcessorReview()
					&& ("Forward to Dealer".equalsIgnoreCase(this.task.getTakenTransition())
							|| "Deny".equalsIgnoreCase(this.task.getTakenTransition()))
					&& org.apache.commons.lang.StringUtils.isBlank(claim.getExternalComment())) {
				addActionError("error.newClaim.externalCommentRequired");
			}


			if (isProcessorReview()) {
				validatePRC(claim);
			}
			if(task != null && task.getClaim() !=null  && task.getClaim().getPayment() != null && task.getClaim().getPayment().isFlatAmountApplied()){
				validateNegativeFlatAmount();
			}
			//calling method for saving the reasons selected for denying or requesting info or put on hold 
			setReasonsList(claim);
			validateActionToBeTaken(claim);
		}
        setActionErrors(claimSubmissionUtil.validate(claim, campaign));

        // QC-182.
        // Scenario: Claim sent for CP Review.
        // Gets Auto replied due to lapse of window period
        // Processor should not be able to accept the CP part.
        // Can be accepted with 0 CP amount though.
        // Any other action can be taken.
       
        if (isCPAdvisorEnabled()) {
            setActionErrors(claimSubmissionUtil.validateForNoCPOnAutoReply(claim, task.getTakenTransition()));
            
        }
        
	    if (isSourceWarehouseToBeCaptured(claim)
				&& claim.getSourceWarehouse() == null) {
			addActionError("error.newClaim.sourceWarehouseRequired");
		}
		if (isSellingEntityToBeCaptured(claim)
				&& claim.getSellingEntity() == null) {
			addActionError("error.newClaim.sellingEntityRequired");
		}
		
		// Add ActionErrors for CR06: Multi-unit claims with R6 Part Installed/Removed widget.
		//		Perform this validation for claims with multiple serial numbers.
		if(claim.getForMultipleItems()) {
	        List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalledList = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
			for(HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled: hussmanPartsReplacedInstalledList) {
		        if(hussmanPartsReplacedInstalled.getInventoryLevel() == null) {
		        	addActionError("error.claim.partQuantityNotSpecified");
		        }
			}
		}	 		
		validateFailureInformation();
		validateNonOEMPartNumbers();
		validatePartsClaimforStandardPolicy(claim);
		validateTechnicianForAMER(claim);
		validateDocumentTypeForAttachment(this.task.getClaim());
		//validateTransportation();
        }
	
	//To validate Transportation, when we entered transportation amount with no invoice attach and without selecting no invoice available check box	
	private void validateTransportation(){
		
		if (task.getClaim().getServiceInformation().getServiceDetail().getTransportationAmt() != null					
					&& !task.getClaim().getServiceInformation().getServiceDetail().isInvoiceAvailable()
					&& task.getClaim().getServiceInformation().getServiceDetail().getTransportationInvoice() == null) {
			if(task.getClaim().getServiceInformation().getServiceDetail().getTransportationAmt().breachEncapsulationOfAmount().floatValue() > 0){
				addActionError("error.noInvoiceAttached");
			}
		}		
	}
	
	private void validateFailureInformation(){
		if(task.isPartsClaim() && (new HibernateCast<PartsClaim>().cast(task.getClaim()).getPartInstalled() && !(StringUtils.hasText(task.getClaim().getCompetitorModelBrand())))){
			if(task.getClaim().getServiceInformation().getFaultCodeRef()==null){
				if(!this.getFieldErrors().containsKey("task.claim.serviceInformation.faultCodeRef")){
				 addActionError("error.newClaim.faultCodeRequired");
				}
			}	
			if(task.getClaim().getServiceInformation().getFaultFound()==null){
				if(!this.getFieldErrors().containsKey("task.claim.serviceInformation.faultFound")){
				addActionError("error.newClaim.faultFoundRequired");
				}
			}
			if(task.getClaim().getServiceInformation().getCausedBy()==null){
				if(!this.getFieldErrors().containsKey("task.claim.serviceInformation.causedBy")){
				addActionError("error.newClaim.causedByRequired");
				}
			}
		}
		
	}
	
	private void validateNegativeFlatAmount(){
		for(LineItemGroup lineItemGroup : task.getClaim().getPayment().getLineItemGroups()){
			if(lineItemGroup.getFlatCpAmount().breachEncapsulationOfAmount().doubleValue() < 0){
				addActionError("error.FlatAmount.NegativeValue");
				break;
			}
			if(lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount().doubleValue() < 0){
				addActionError("error.FlatCPAmount.NegativeValue");
				break;
			}
		}
	}
	
	private void validateNonOEMPartNumbers() {
		if (task.getClaim().getServiceInformation().getServiceDetail()
				.getNonOEMPartsReplaced() != null) {
			for (NonOEMPartReplaced nonOEMPartReplaced : task.getClaim()
					.getServiceInformation().getServiceDetail()
					.getNonOEMPartsReplaced()) {
				try {
					BrandItem brand = brandItemRepository.findUniqueBrandItemByNMHGItemNumber(nonOEMPartReplaced.getDescription());
					if(brand != null && brand.getItem() != null){
						if (getCatalogService()
								.findItemByItemNumberOwnedByManuf(
										brand.getItem().getNumber())
									.getOwnedBy().getId() == 1) {
							addActionError("error.claim.oemPartInNonOemSection",
									nonOEMPartReplaced.getDescription());
						}
					}
				} catch (CatalogException e) {
					logger.error(e);
				}
			}
		}
	}
	
	public String validateNonOEMPartNumber(){
		try {
			BrandItem brand = brandItemRepository.findUniqueBrandItemByNMHGItemNumber(nonOEMPartDescription);
			if(brand != null && brand.getItem() != null){
				if (getCatalogService()
						.findItemByItemNumberOwnedByManuf(
								brand.getItem().getNumber()).getOwnedBy().getId() == 1) {
					this.jsonString="true";
				}
			}else{
				this.jsonString = "false";
			}
		} catch (CatalogException e) {
			this.jsonString="false";
			logger.error(e);
		}
		return SUCCESS;
	}
	
	public String submitSupplier() {
		Long idTobeUsed=Long.parseLong(claimId);
	
		Claim claim = this.claimService.findClaim(idTobeUsed);
	     claim.setSuppliers(suppliers);
	     claim.setSupplierRecovery(supplierRecovery);
		getClaimService().updateClaim(claim);
		return SUCCESS;
	}

    public boolean checkActionForCP(Claim claim) {
        if (isProcessorReview()
                && ((ClaimState.REPLIES.equals(this.task.getClaim().getState())
                || ClaimState.TRANSFERRED.equals(this.task.getClaim().getState())) &&
                "Accept".equalsIgnoreCase(this.task.getTakenTransition()) && claim.getCpReviewed() && (claim
                .getAcceptanceReasonForCp() == null || (claim
                .getAcceptanceReasonForCp() != null && !StringUtils
                .hasText(claim.getAcceptanceReasonForCp()
                        .getDescription()))))) {
            if (claim.getPayment() != null && !claim.getPayment().getLineItemGroups().isEmpty()) {
                List<LineItemGroup> lineItemGroups = claim.getPayment().getLineItemGroups();
                for (LineItemGroup lineItemGroup : lineItemGroups) {
                    if (lineItemGroup.getPercentageAcceptanceForCp().doubleValue() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

	private void validatePRC(Claim claim) {
		List<OEMPartReplaced> oEMPartsReplaced = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
		for (OEMPartReplaced partReplaced : oEMPartsReplaced ) {
            //if disable no need to check
          if(!partReplaced.isPartShippedOrCannotBeShipped()){
			PartReturn partReturn = partReplaced.getPartReturn();
			if (partReturn != null) {
				if (partReturn.getStatus() == null
						|| (partReturn.getStatus() != null && !partReplaced.isShipmentGenerated())) {
					if (partReplaced.isPartToBeReturned()
							&& (partReturn.getReturnLocation() == null)) {
						addActionError(
								"error.partReturnConfiguration.returnLocationRequired",
								new String[] { partReplaced.getItemReference()
										.getReferredItem().getNumber() });
					}
					try {
						if (partReplaced.isPartToBeReturned()
								&& (partReturn.getPaymentCondition() == null)
								&& partReturn.getPaymentCondition().getCode() != null) {
							addActionError(
									"error.partReturnConfiguration.paymentConditionRequired",
									new String[] { partReplaced
											.getItemReference()
											.getReferredItem().getNumber() });
						}
					} catch (Exception e) {
						addActionError(
								"error.partReturnConfiguration.paymentConditionRequired",
								new String[] { partReplaced.getItemReference()
										.getReferredItem().getNumber() });
					}

					if (partReplaced.isPartToBeReturned()
							&& partReturn.getDueDays() <= 0 && !partReturn.isDueDaysReadOnly()) {
						addActionError("error.partReturnConfiguration.dueDays",
								new String[] { partReplaced.getItemReference()
										.getReferredItem().getNumber() });
					}

                    if (partReplaced.isPartToBeReturned()
                            && partReturn.getDealerPickupLocation() == null ) {
                        addActionError("error.partReturnConfiguration.dealerPickUpLocationNotSpecified",
                                new String[] { partReplaced.getItemReference()
                                        .getReferredItem().getNumber() });
                    }
				}
			}
		 }
        }
	}

	protected void computeMatchReadScore(Claim claim) {
		long matchReadScore = 0;
		InventoryItem inventoryItem = claim.getClaimedItems().get(0)
				.getItemReference().getReferredInventoryItem();

		if (inventoryItem.getType().getType().equals(
				InventoryType.RETAIL.getType())
				&& ClaimState.DRAFT.getState().equals(
						claim.getState().getState())
				&& !(ClaimType.CAMPAIGN.getType().equals(claim.getType()
						.getType()))) {
			prepareMatchReadInfo();

			Address latestEndCustomerAddress = inventoryItem.getOwnedBy().getAddress();
			MatchReadInfo matchReadInfo = claim.getMatchReadInfo();

			if (matchReadInfo.getOwnerCity().equalsIgnoreCase(
					latestEndCustomerAddress.getCity())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_CITY
						.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerName().equalsIgnoreCase(
					inventoryItem.getOwnedBy().getName())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_NAME
						.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerState().equalsIgnoreCase(
					latestEndCustomerAddress.getState())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_STATE
						.getMultiplyFactor();
			}
			if (matchReadInfo.getOwnerCountry().equalsIgnoreCase(
					latestEndCustomerAddress.getCountry())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_COUNTRY
						.getMultiplyFactor();
			}

			if (matchReadInfo.getOwnerZipcode().equalsIgnoreCase(
					latestEndCustomerAddress.getZipCode())) {
				matchReadScore += MatchReadMultiplyFactor.OWNER_ZIPCODE
						.getMultiplyFactor();
			}
			matchReadInfo.setScore(matchReadScore);
		}
	}


	void setPolicyOnClaimedItems(Claim theClaim) {
		claimSubmissionUtil.setPolicyOnClaimedItems(theClaim);
	}


	void setPolicyOnClaimedParts(Claim theClaim) {
		claimSubmissionUtil.setPolicyOnClaimedParts(theClaim);
	}

	@SuppressWarnings("deprecation")
	public void setClaimProcessedAsForClaimedParts(Claim claim) {
		claimSubmissionUtil.setClaimProcessedAsForClaimedParts(claim);
	}

	protected void validateProcessorApprovalLimit(Claim theClaim){
		if (task.getTakenTransition() != null && "Accept".equalsIgnoreCase(task.getTakenTransition())) {
			// Claim amount compared with processor approval limit.
			if (eligibleLOAProcessors != null && eligibleLOAProcessors.size() != 0
					&& eligibleLOAProcessors.contains(getLoggedInUser().getName())) {
				boolean approvalLimitForCurrency = false;
					approvalLimit:	
				for (LimitOfAuthorityLevel limitOfAuthorityLevel : theClaim.getLoaScheme().getLoaLevels()) {
					if(limitOfAuthorityLevel.getLoaUser().getName().equalsIgnoreCase(getLoggedInUser().getName())){
					for (Money approvalLimit : limitOfAuthorityLevel.getApprovalLimits()) {
						if (approvalLimit.breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(
								theClaim.getCurrencyForCalculation().getCurrencyCode())) {
							approvalLimitForCurrency = true;
							if (approvalLimit.breachEncapsulationOfAmount().doubleValue() < theClaim.getPayment()
									.getTotalAmount().breachEncapsulationOfAmount().doubleValue()) {
								addActionError("error.claim.approvedAmountLessThanClaimAmount");
								break approvalLimit;
							}
						}
					}}
				}					
				if(!approvalLimitForCurrency){						
					addActionError("error.claim.approvalLimitIsNotAvailableForThisCurrency", new String[] {theClaim.getCurrencyForCalculation().getCurrencyCode()});
				}
			}
		}
	}
	
	public String validateClaim() {
		try {
			Claim theClaim = null;
			try {
				theClaim = this.task.getClaim();
				
			} catch (Exception e) {
				logger.error("Exception EX : " + e);
				return FATAL_ERROR;
			}
			/**
	         * Address is mandatory for non serialized claims
	         */
			
			//code updated for Technician validation
			//technician not mandatory for parts, field modification claim
			/*if (!theClaim.getType().getType().equals(ClaimType.PARTS.getType())
					&& !theClaim.getType().getType().equals(ClaimType.CAMPAIGN.getType())) {*/
				//validateTechnician(theClaim);
			/*}*/
			
			if(!isProcessorReview() && theClaim.getManualReviewConfigured() && ClaimState.FORWARDED.equals(theClaim.getState()) && isBuConfigAMER()){ // The error message should be triggered only for AMER,SLMSPROD-1281, but the audit claim flag should be there for EMEA
                //First check documents are there in the audit, if not check the current attachments
                if(!isWrkOrdTrmCrdAttached(theClaim)){
                        addActionError("error.forwardedClaim.message");
                }
            }
	        if (ClaimType.PARTS.getType().equals(theClaim.getType().getType())) {
	        	
	             PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(theClaim);
	            if (partsClaim.getPartInstalled()
	                    && (!theClaim.getClaimedItems().get(0).getItemReference().isSerialized() && !theClaim.getPartItemReference().isSerialized())
	                    && ClaimState.DRAFT.getState().equals(theClaim.getState().getState())) {
	                if (theClaim.getOwnerInformation() == null) {
	                	addActionError("error.newClaim.ownerInfoRequired");
	                }
	            }
	        } else if (!theClaim.getClaimedItems().get(0).getItemReference().isSerialized()
	                && ClaimState.DRAFT.getState().equals(theClaim.getState().getState())) {
	            if (theClaim.getOwnerInformation() == null) {
	            	addActionError("error.newClaim.ownerInfoRequired");
	            }
	        }
	        validateRootCauseForClaim(theClaim);
            setActionErrors(claimSubmissionUtil.validateClaim(theClaim,
                     isProcessorReview(), this.task.getTakenTransition(), hasActionErrors(),
                     this.task.getBaseFormName(), isPartShippedNotRcvd()));

			boolean isValidMatchReadInfo = false;
			if (theClaim.getClaimedItems().get(0).getItemReference()
					.isSerialized()
					&& InventoryType.RETAIL.getType().equals(
							theClaim.getClaimedItems().get(0)
									.getItemReference()
									.getReferredInventoryItem().getType()
									.getType())
					&& theClaim.getMatchReadInfo() != null
					&& isMatchReadApplicable()
					&& !(ClaimType.CAMPAIGN.getType().equals(theClaim.getType()
							.getType())) && isClaimInDraft()) {
				isValidMatchReadInfo = validateMatchReadOwnerInfo();
			}
			if (isValidMatchReadInfo) {
				prepareMatchReadInfo();
				prepareMatchReadInfoForShow();
			}
			if(task.getTakenTransition() != null &&
					"Accept".equalsIgnoreCase(task.getTakenTransition())&& theClaim.getItemReference().isSerialized() && !(ClaimState.SERVICE_MANAGER_REVIEW.getState().equalsIgnoreCase(theClaim.getState().getState())))
			{
				getClaimService().roundUpLaborOnClaim(theClaim);
			}

			if (theClaim.getClaimedItems().get(0).getItemReference()
					.isSerialized()
					&& InventoryType.RETAIL.getType().equals(
							theClaim.getClaimedItems().get(0)
									.getItemReference()
									.getReferredInventoryItem().getType()
									.getType())
					&& isMatchReadApplicable()
					&& (theClaim.getMatchReadInfo() == null || (theClaim
							.getMatchReadInfo() != null && !isValidMatchReadInfo))
					&& !(ClaimType.CAMPAIGN.getType().equals(theClaim.getType()
							.getType())) && isClaimInDraft()) {
				addActionError("error.newClaim.ownerInfoRequired");
			}
			
            if (isFailureReportsPendingOnClaim(theClaim)) {
                addActionWarning("claim.pendingFailureReports");
            }
            
            if(isProcessorReview()
            		&& isFailureReportsPending(theClaim)){
            	addActionWarning("claim.failureReports.incomplete");
            }
            if (isLaborSplitEnabled()) {
				if (!validateLaborType(theClaim)) {
					return INPUT;
				}
			}
            String source = theClaim.getSource();
            if(source != null) {
            	setActionErrors(claimSubmissionUtil.validateRequiredFields(theClaim));
            }
            validateAlarmCodes(theClaim);
			if (hasActionErrors() || hasFieldErrors()) {
				addActionError("error.newClaim.errorsInSubmit");
				return INPUT;
			}

			/*
			 * These 2 API's sets the total quantity of oem parts replaced and
			 * labor hours for multi car claims
			 */
			setTotalQtyForReplacedParts(theClaim);
			if (!isProcessorReview()) {
				
			}
			// Copied this API to set the policy while saving claim as initial
			// draft
			// in saveDraft of
			// AbstractNewClaimAction and the local saveDraft APIs as well. Bug
			// Fix
			// : 132422
			if(!theClaim.getNcrClaimCheck()){
				claimSubmissionUtil.setPolicyForClaim(theClaim);
			}
			// Not moved this piece of validation to the claim validator, as
			// there
			// CAN be business
			// conditions set based on the applicable policies on the claim.
			theClaim.getActiveClaimAudit().getAttachments();
			computePayment(theClaim);
			if (isBuConfigAMER() && task.getTakenTransition() != null
					&& task.getTakenTransition().equalsIgnoreCase(
							UserClusterService.ACTION_ACCEPT)
					&& (theClaim.getPayment().getTotalAmount().isNegative() || theClaim
							.getPayment().getTotalAmount().isZero()))
			{
				addActionError("error.payment.negativeAmount");
				return INPUT;
			}
			if(checkForPaymentSystemErrors(theClaim, task.getTakenTransition(),false)){
				return INPUT;	
			}
			validateProcessorApprovalLimit(theClaim);			
			
			// Check whether action is allowed for the current user or not
			// As per the discussion with Manju, we have the following states
			// to restrict the action
			if (allowedStateForLOA(theClaim.getState()))
				validateAllowedProcessClaim(theClaim);
			if (hasActionErrors() || hasFieldErrors()) {
				//addActionError("error.newClaim.errorsInSubmit");
				return INPUT;
			}

			getClaimService().updateOEMPartInformation(theClaim, getInitialReplacedParts());
			if (theClaim.getServiceInformation() != null) {
				if (theClaim.getServiceInformation().getServiceDetail() != null) {
					List<OEMPartReplaced> partsReplaced = theClaim
							.getServiceInformation().getServiceDetail()
							.getReplacedParts();
					String partsToBeReturned="";
					String partsToBeReturnedToSupplier="";
					StringBuffer replacedPartsBrand = new StringBuffer();
					StringBuffer replacedPartNumbers= new StringBuffer();					
					for (OEMPartReplaced partReplaced : partsReplaced) {
						Item referredItem = partReplaced.getItemReference().getReferredItem();
						//if((!(ClaimType.PARTS.getType().equals(theClaim.getType().getType()) && null != theClaim.getCompetitorModelBrand()))){							
							 if(null != partReplaced.getBrandItem().getBrand() && !validateBrandType(partReplaced.getBrandItem().getBrand(),theClaim.getBrand())){									 
								 replacedPartsBrand.append(", "+partReplaced.getBrandItem().getBrand());
								 replacedPartNumbers.append(", "+partReplaced.getBrandItem().getItemNumber()+"-"+partReplaced.getBrandItem().getItem().getDescription());								
								}
						//}
						if (partReplaced.isPartToBeReturned()) {
							if(!partsToBeReturnedToSupplier.isEmpty() && partReplaced.isReturnDirectlyToSupplier()){
								partsToBeReturnedToSupplier=partsToBeReturnedToSupplier+","+partReplaced.getItemReference().getReferredItem().getNumber();
							}
							else if(partReplaced.isReturnDirectlyToSupplier()){
								partsToBeReturnedToSupplier=partReplaced.getItemReference().getReferredItem().getNumber();
							}
							if(!partsToBeReturned.isEmpty() && !partReplaced.isReturnDirectlyToSupplier())
							{
								if(theClaim.getBrand()==null || theClaim.getBrand().isEmpty())
								{
							partsToBeReturned=partsToBeReturned+","+partReplaced.getItemReference().getReferredItem().getNumber();
								}
								else
								{
									partsToBeReturned=partsToBeReturned+","+partReplaced.getItemReference().getReferredItem().getBrandItemNumber(theClaim.getBrand());	
								}
							}
							else if(!partReplaced.isReturnDirectlyToSupplier())
							{
								if(theClaim.getBrand()==null || theClaim.getBrand().isEmpty())
								{
								partsToBeReturned=partReplaced.getItemReference().getReferredItem().getNumber();
								}
								else
								{
									partsToBeReturned=partReplaced.getItemReference().getReferredItem().getBrandItemNumber(theClaim.getBrand());	
								}
							}
							}
					}
					if(replacedPartsBrand.length()>1){
						theClaim.setReplacedPartsBrand(replacedPartsBrand.substring(1).toString());
						theClaim.setReplacedPartNumbers(replacedPartNumbers.substring(1).toString());
					}										
					if(!partsToBeReturned.isEmpty())
					{
						addActionMessage("message.newClaim.partReturn",
								partsToBeReturned);
					}
					if(!partsToBeReturnedToSupplier.isEmpty())
					{
						addActionMessage("message.newClaim.partReturnToSupplier",
								partsToBeReturnedToSupplier);
					}
				}
				if (theClaim.getServiceInformation().getServiceDetail() != null) {
					List<InstalledParts> partsInstalled = theClaim
							.getServiceInformation().getServiceDetail()
							.getInstalledParts();
					StringBuffer installedPartBrands = new StringBuffer();
					StringBuffer installedPartNumbers = new StringBuffer();
					for (InstalledParts partInstalled : partsInstalled) {
						//if((!(ClaimType.PARTS.getType().equals(theClaim.getType().getType()) && null != theClaim.getCompetitorModelBrand()))){						
						 if(null != partInstalled.getBrandItem().getBrand() && !validateBrandType(partInstalled.getBrandItem().getBrand(),theClaim.getBrand())){								
							 installedPartBrands.append(", "+partInstalled.getBrandItem().getBrand()) ;	
							 installedPartNumbers.append(", "+partInstalled.getBrandItem().getItemNumber()+"-"+partInstalled.getBrandItem().getItem().getDescription());
							}
						//}
					}				
					if(installedPartBrands.length()>1){
						theClaim.setInstalledPartBrands(installedPartBrands.substring(1).toString());
						theClaim.setInstalledPartNumbers(installedPartNumbers.substring(1).toString());
					}					
					
				}
			}

			populateProcessorTransition();

			if (!theClaim.isFoc()
					&& (ClaimState.DRAFT.getState().equalsIgnoreCase(theClaim.getState().getState())
					|| ClaimState.FORWARDED.getState().equalsIgnoreCase(theClaim.getState().getState())
					|| ClaimState.SERVICE_MANAGER_RESPONSE.getState().equalsIgnoreCase(theClaim.getState().getState()))) {
				this.messages = this.ruleAdministrationService
						.executeClaimEntryValidationRules(theClaim);
				setValidationResultAsActionMessage();
				if (this.messages.hasErrors()) {
					return INPUT;
				}
			}
			if (theClaim.getForMultipleItems()) {
				addActionMessage("message.newClaim.calculationMessage");
			}
			if(isProcessorReview()){
				List<LineItemGroup> lineItemGroups = theClaim.getPayment().getLineItemGroups();
			}
			
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("Exception EX : " + ExceptionUtil.getStackTrace(ex));
			return FATAL_ERROR;
		}
	}

	private boolean validateBrandType(String causalPartBrandName, String truckBrandName) {		
		if(!causalPartBrandName.equalsIgnoreCase(truckBrandName)){							
			/*if(getLoggedInUsersDealership()!=null && null != orgService.checkLoggedInDealerForDualBrand(getLoggedInUsersDealership().getId()))
				return true;
			else*/
				return false;			
		}else{
			return true;
		}
	}
	
	public boolean checkForPaymentSystemErrors(Claim claim, String transition,
			boolean isSubmit) {
		if (claim.getActiveClaimAudit().getIsPriceFetchDown()&&isErrorMessageShowOnEPODown()) {
			addActionError("label.common.epo.system.down");
			return true;
		}
		if (displayWarningIfPartPricesDifferent()&&
				isLoggedInUserAnInternalUser() && !isSubmit && null != claim.getPaymentForDealerAudit()
				&& (claim.getPaymentForDealerAudit()!=null&&claim.getPayment()!=null)){
			LineItemGroup oemPart=claim.getPayment().getLineItemGroup(Section.OEM_PARTS);
			Money acceptedAmount=null;
			Money requestedAmount=claim.getPaymentForDealerAudit().getLineItemGroup(Section.OEM_PARTS).getGroupTotal();
			if(claim.getPayment().getTotalAcceptStateMdtChkbox()!=null&&claim.getPayment().getTotalAcceptStateMdtChkbox())
			{
				acceptedAmount=	oemPart.getGroupTotalStateMandateAmount();
			}
			else				
			{
				acceptedAmount=	oemPart.getAcceptedTotal();
			}	
			if(!requestedAmount.equals(acceptedAmount))
			{
			addActionWarning(
					"label.common.warning.differentPrices.pricefetch",
					"label.common.warning.differentPrices.pricefetch",
					new String[]{requestedAmount.toString(),
							acceptedAmount.toString()});
			}
		}
		
		if (claim.getActiveClaimAudit().getIsPriceFetchReturnZero()
				&& isLoggedInUserADealer()
				&& !(transition != null && transition
						.equalsIgnoreCase(CLAIM_SUBMIT))) {
			String[] zeroPricedPars = claim.getActiveClaimAudit()
					.getPriceFetchErrorMessage().split("#");
			if ((ClaimState.DRAFT.getState().equalsIgnoreCase(
					claim.getState().getState()))||ClaimState.FORWARDED.getState().equalsIgnoreCase(
							claim.getState().getState())) {
				if (!isInvoiceDocumentAttched(claim)){
					addActionError(
							"label.common.epo.system.has.return.zero.values",
							zeroPricedPars[1]);
				return true;
				}
			} 
		} else if (claim.getActiveClaimAudit().getIsPriceFetchReturnZero()
				&& !isLoggedInUserADealer() && !isSubmit && claim.getActiveClaimAudit().isPriceZero()) {
			String[] zeroPricedPars = claim.getActiveClaimAudit()
					.getPriceFetchErrorMessage().split("#");
			if (ClaimState.DRAFT.getState().equalsIgnoreCase(
					claim.getState().getState())) {
				if (!isInvoiceDocumentAttched(claim)){
					addActionError(
							"label.common.epo.system.has.return.zero.values",
							zeroPricedPars[1]);
				return true;
				}
			} else {
				if(isBuConfigAMER()){
					addActionError("label.common.epo.system.has.return.zero.values.processor",zeroPricedPars[1]);
				}else{
					addActionWarning("label.common.epo.system.has.return.zero.values.processor",zeroPricedPars[1]);
				}
			}
		}
		return false;
	}
	
	public boolean isErrorMessageShowOnEPODown() {
		if (!getConfigParamService()
				.getBooleanValue(ConfigName.IS_DEALER_ALLOWED_TO_TAKE_ACTIONS_ON_CLAIM_DURING_EPO_DOWN
						.getName())
				&& isLoggedInUserADealer()) {
			return true;
		}
		if (!getConfigParamService()
				.getBooleanValue(ConfigName.IS_PROCESSOR_ALLOWED_TO_TAKE_ACTIONS_ON_CLAIM_DURING_EPO_DOWN
						.getName())
				&& !isLoggedInUserADealer()) {
			return true;
		}
		return false;
	}

	private boolean isInvoiceDocumentAttched(Claim claim) {
		// TODO Auto-generated method stub
		if (claim.getActiveClaimAudit().getAttachments() != null
				&& claim.getActiveClaimAudit().getAttachments().size() > 0) {
			for (Document document : claim.getActiveClaimAudit()
					.getAttachments()) {
				if (document.getDocumentType() != null
						&& document.getDocumentType().getName() != null
						&& document.getDocumentType().getName()
								.equalsIgnoreCase("INVOICE")) {
					return true;
				}
			}
		}
		return false;
	}	
	
	/**
	 * @param claim
	 * @return
	 * This method is for checking whether Work Order & Trum Card
	 * attachment is attached for the X number of claim
	 * Added for NMHGSLMS-576: RTM-156, 682
	 */
	public boolean isWrkOrdTrmCrdAttached(Claim claim) {
		boolean timeCardAttached = false;
		boolean workOrderAttached = false;
		if (claim.getActiveClaimAudit().getAttachments() != null) {
			for (Document document : claim.getActiveClaimAudit().getAttachments()) {
				if(document.getDocumentType() != null
						&& document.getDocumentType().getName() != null){
					if (document.getDocumentType().getName().toLowerCase()
							.equals("WORK ORDER".toLowerCase())) {
						workOrderAttached = true;
					}
					if (document.getDocumentType().getName().toLowerCase()
							.equals("TIME CARD".toLowerCase())) {
						timeCardAttached = true;
					}
				}
			}
		}
		return timeCardAttached && workOrderAttached;
	}

	protected boolean isSerializedClaim(Claim claim) {
        if( claim.getClaimedItems() != null 
                        && !claim.getClaimedItems().isEmpty()
                        && claim.getClaimedItems().get(0).getItemReference() != null
                        && claim.getClaimedItems().get(0).getItemReference().isSerialized())
                return true;
        return false;
	}

	protected void validateAllowedProcessClaim(Claim theClaim) {
		Claim newClaim = theClaim;
		if (authorizeAllowedActionsForProcessor(newClaim, getLoggedInUser(), Boolean.FALSE) &&
				newClaim.getAllowedActionsList()!=null && !newClaim.getAllowedActionsList().isEmpty() &&
				!newClaim.getAllowedActionsList().contains(actionPerformed.get(this.task.getTakenTransition())))
		{
			addActionError("error.newClaim.notAllowedToProcess", new String[] {this.task.getTakenTransition()});
		}
	}


	private boolean isReopenedClaim(Claim theClaim) {
		Boolean reopened = theClaim.getReopened() == null ? Boolean.FALSE
				: theClaim.getReopened();
		return reopened.booleanValue();
	}

	private boolean isClaimAppealed(Claim theClaim) {
		Boolean appealed = theClaim.getAppealed() == null ? Boolean.FALSE
				: theClaim.getAppealed();
		return appealed.booleanValue();
	}

	protected boolean doesClaimContainRejectedPart(Claim claim) {
		return this.partReturnService.doesClaimHaveRejectedParts(claim);
	}

	public boolean isFullyRejectedMultiCarClaim() {
		Claim claim = this.task.getClaim();

		if (!claim.getForMultipleItems()) {
			return false;
		}

		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			if (claimedItem.isProcessorApproved()) {
				return false;
			}
		}

		return true;
	}

	protected void populateProcessorTransition() {
		if (this.task.getBaseFormName().equals("processor_review")) {
			String takenTrans = this.task.getTakenTransition();
			if (takenTrans.equals("Hold")) {
				setProcessorTakenTransition(getText("message.newClaim.onHold"));
			} else if (takenTrans.equals("Forward to Dealer")) {
				setProcessorTakenTransition(getText("message.newClaim.requestMoreInfo"));
			} else if (takenTrans.equals("Seek Advice")) {
				setProcessorTakenTransition(getText(
						"message.newClaim.seekAdvice", new String[] { this.task.getSeekAdviceFrom()}));
			} else if (takenTrans.equals("Seek Review")) {
				setProcessorTakenTransition(getText(
						"message.newClaim.seekReview", new String[] { this.task
								.getSeekReviewFrom() }));
			} else if (takenTrans.equals("Transfer")) {
				setProcessorTakenTransition(getText(
						"message.newClaim.transferClaim",
						new String[] { this.task.getTransferTo() }));
			} else if (takenTrans.equals("Deny")) {
				setProcessorTakenTransition(getText("message.newClaim.deny"));
			} else if (takenTrans.equals("Accept")) {
				setProcessorTakenTransition(getText("message.newClaim.accept"));
			} else if (takenTrans.equals("ApproveAndTransferToNextUser")) {
				setProcessorTakenTransition(getText("message.newClaim.approveAndTransferClaim",new String[] { this.task.getTransferTo()}));
			}
		}
	}

	public boolean isProcessorReview() {
		return (this.task ==null || this.task.getTask() == null) ? getBaseFormName().equals("processor_review") : this.task.getBaseFormName().equals(
						"processor_review");
	}

	public boolean isClaimInWaitingForLabor(){
		return this.task.getBaseFormName().equals("draft_claim") && this.task.getClaim().isFoc();
	}

	public boolean isDraftClaim() {
		return this.task.getBaseFormName().equals("draft_claim") ;
	}

	public boolean isCPReview() {
		return this.task.getBaseFormName().equals("review_request");
	}

	public boolean isRepliesOrForwarded()// fix for bug CCI-839
	{
		return ClaimState.REPLIES.equals(this.task.getClaim().getState())
				|| ClaimState.FORWARDED.equals(this.task.getClaim().getState());
	}

	public boolean isServiceManagerReview(){
		return ClaimState.SERVICE_MANAGER_REVIEW.equals(this.task.getClaim().getState());
	}

	public boolean isServiceManagerResponse(){
		return ClaimState.SERVICE_MANAGER_RESPONSE.equals(this.task.getClaim().getState());
	}

	public boolean isForwarded(){
		return ClaimState.FORWARDED.equals(this.task.getClaim().getState());
	}

	public boolean isAdviceRequest(){
		return ClaimState.ADVICE_REQUEST.equals(this.task.getClaim().getState());
	}

	public boolean isCPReviewed(){
		return ClaimState.CP_REVIEW.equals(this.task.getClaim().getState());
	}

	public boolean isClaimInDraft() {
		return (this.task.getClaim().getState().ordinal() < ClaimState.SUBMITTED
				.ordinal());
	}

	void computePayment(Claim theClaim) {
	    claimSubmissionUtil.computePayment(theClaim, this.task.getTakenTransition());
	}

    void setValidationResultAsActionMessage() {
		List<String> errors = this.messages.getErrors();
		ValidationResults result = new ValidationResults();
		for (String error : errors) {
			if (StringUtils.hasText(error))
			{
				int pos1 = error.indexOf(")", 1);
				int pos2 = error.indexOf("[");
				error = error.substring(pos1 + 1, pos2);
				result.addErrorMessage(error);
				addActionError(error);
			}
		}
		List<String> warnings = this.messages.getWarnings();
		for (String warning : warnings) {
			if (StringUtils.hasText(warning))
			{
				int pos1 = warning.indexOf(")", 1);
				int pos2 = warning.indexOf("[");
				warning = warning.substring(pos1 + 1, pos2);
				result.addWarningMessage(warning);
				addActionMessage(warning);
			}
		}
		messages = result;
	}

    public Map<String, String> getDistrictServiceManagers() {
		SortedMap<String, String> serviceMgrList = new TreeMap<String, String>();
		for (String name : findUsersBelongingToRole(Role.DSM, this.task.getClaim().getBusinessUnitInfo().getName())) {
            if(getLoggedInUser()!=null &&
                    !name.equalsIgnoreCase(getLoggedInUser().getCompleteNameAndLogin())){
                serviceMgrList.put(name.substring(name.indexOf("(")+1, name.indexOf(")")), name);
            }
        }
		return serviceMgrList;
	}

    @SuppressWarnings("unchecked")
	private Collection<String> findEligibleDSMUsers(Claim claim) {
		List<String> serviceManagers = this.assignmentRuleExecutor
				.fetchEligibleDSMsUsingAssignmentRules(claim);
        if (serviceManagers != null) {
            return serviceManagers;
        } else {
            return findUsersBelongingToRole(Role.DSM, claim.getBusinessUnitInfo().getName());
        }
	}
    
    public Map<String, String> getCPAdvisor() {
		SortedMap<String, String> advisorList = new TreeMap<String, String>();
		for (String name : findEligibleUsersForCPAdvice(this.task.getClaim())) {
			if(!name.equalsIgnoreCase(getLoggedInUser().getName())){
				advisorList.put(name, name);
			}
		}
		return advisorList;
	}

	protected Collection<String> findEligibleUsersForCPAdvice(Claim claim) {
		List<String> cpAdvisors = this.assignmentRuleExecutor
				.fetchEligibleCPAdvisorsUsingAssignmentRules(claim);
		if (cpAdvisors != null)
			return cpAdvisors;
		else
			return findUsersBelongingToCPAdvisorRole("cpAdvisor");
	}

	@SuppressWarnings("unchecked")
	protected Collection<String> findUsersBelongingToCPAdvisorRole(String role) {
		Set<User> cpAdvisors = this.orgService
				.findUsersBelongingToRole(role);
		return CollectionUtils.collect(cpAdvisors, new Transformer() {
			public Object transform(Object input) {
				return ((User) input).getName();
			}
		});
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	public TaskView getTask() {
		return this.task;
	}

	public void setTask(TaskView task) {
		this.task = task;
	}

	@SuppressWarnings("unchecked")
	public List<ClaimAudit> getClaimAudits() {
		if ((this.task != null) && (this.task.getClaim() != null)) {
			List result = new ArrayList(this.task.getClaim().getClaimAudits());
			Collections.reverse(result);
			return result;
		}
		return new ArrayList();
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public List<String> getMandatedComments() {
		return this.mandatedComments;
	}

	public void setMandatedComments(List<String> mandatedComments) {
		this.mandatedComments = mandatedComments;
	}

	public Claim getClaimDetails() {
		return this.claimDetails;
	}

	public void setClaimDetails(Claim claimDetails) {
		this.claimDetails = claimDetails;
	}

	// FIXME: Any exception that occurs comes up as a java script
	// Hence returning "{}" for any exception that occurs.
	public String getJSONifiedAttachmentList() {
		try {
			List<Document> attachments = this.task.getClaim().getAttachments();
			List<Document> sharedAttachments = new ArrayList<Document>();
			List<Document> supplierSharedAttachments = new ArrayList<Document>();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			logger.error("getJSONifiedAttachmentList EX : " , e);
			return "[]";
		}
	}

	public String getJSONifiedCampaignAttachmentList() {
		try {
			if(this.task.getClaim().getCampaign() != null){
				List<Document> attachments = this.task.getClaim().getCampaign().getAttachments();
				if (attachments == null || attachments.size() <= 0) {
					return "[]";
				}
				return getDocumentListJSON(attachments).toString();
			}
			return "[]";
		} catch (Exception e) {
			return "[]";
		}
	}

	public String getProcessorTakenTransition() {
		return this.processorTakenTransition;
	}

	public void setProcessorTakenTransition(String processorTakenTransition) {
		this.processorTakenTransition = processorTakenTransition;
	}

	public ValidationResults getMessages() {
		return this.messages;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	
	public int OEMPartsReplacedUpdatedCount(int totalQty) {
		int noOfClaimedItems = getApprovedClaimedItems(this.task.getClaim());
		int qtyToBeDisplayed = 0;
		if (isLoggedInUserADealer()
				&& !ClaimState.DRAFT.getState().equals(
						this.task.getClaim().getState().getState())) {
			qtyToBeDisplayed = totalQty / noOfClaimedItems;
		} else {
			qtyToBeDisplayed = totalQty;
		}
		return qtyToBeDisplayed;
	}

	public BigDecimal additionalLaborHoursUpdated(BigDecimal additionalLaborHrs) {
		BigDecimal noOfClaimedItems = new BigDecimal(getApprovedClaimedItems(this.task.getClaim()));
		BigDecimal additionalLaborHrsDisplayed = BigDecimal.ZERO;
		if (isLoggedInUserADealer()
				&& !ClaimState.DRAFT.getState().equals(
						this.task.getClaim().getState().getState())) {
			additionalLaborHrsDisplayed = additionalLaborHrs.divide(noOfClaimedItems);
		} else {
			additionalLaborHrsDisplayed = additionalLaborHrs;
		}
		return additionalLaborHrsDisplayed;
	}

	public List<PaymentCondition> getPaymentConditions() {
		return this.partReturnService
		.findAllPaymentConditions();
	}

	public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
		this.paymentConditions = paymentConditions;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}
	
	protected List<String> getDisclaimerActions() {
		List<String> disclaimerActions = new ArrayList<String>();
		disclaimerActions.add(getText("legal.disclaimer.accept"));
		disclaimerActions.add(getText("legal.disclaimer.reject"));
		return disclaimerActions;
	}

	public MatchReadMultiplyFactor getMatchReadMultiplyFactor() {
		return this.matchReadMultiplyFactor;
	}

	public void setMatchReadMultiplyFactor(
			MatchReadMultiplyFactor matchReadMultiplyFactor) {
		this.matchReadMultiplyFactor = matchReadMultiplyFactor;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setClaimNumberPatternService(
			ClaimNumberPatternService claimNumberPatternService) {
		this.claimNumberPatternService = claimNumberPatternService;
	}

	public Contract getSelectedContract() {
		return this.selectedContract;
	}

	public void setSelectedContract(Contract selectedContract) {
		this.selectedContract = selectedContract;
	}

	public List<Contract> getContracts() {
		return this.contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public boolean isWarningRequired() {
		return this.isWarningRequired;
	}

	public void setWarningRequired(boolean isWarningRequired) {
		this.isWarningRequired = isWarningRequired;
	}

	public List<FailureTypeDefinition> prepareFaultFoundList(String invItemId) {

		List<FailureTypeDefinition> possibleFailures = this.failureStructureService
		.findFaultFoundOptions(invItemId);
if (possibleFailures.isEmpty()) {
	possibleFailures = this.failureStructureService
			.findFaultFoundOptionsAtProduct(invItemId);

}
        Collections.sort(possibleFailures,new Comparator()
        {
            public int compare(Object obj1 , Object obj2){
                FailureTypeDefinition failure1 = (FailureTypeDefinition)obj1;
                FailureTypeDefinition failure2 = (FailureTypeDefinition)obj2;
                return failure1.getName().compareTo(failure2.getName());
            }
        });
        return possibleFailures;
	}

	public List<FailureTypeDefinition> prepareFaultFoundListForModels(
			ItemGroup model) {

		List<FailureTypeDefinition> possibleFailures = this.failureStructureService
		.findFaultFoundOptionsForModels(model.getId().toString());
if (possibleFailures.isEmpty()) {
	if(model.getIsPartOf().getItemGroupType().equalsIgnoreCase("PRODUCT")){
	possibleFailures=this.failureStructureService.findFaultFoundOptionsForModels(
			model.getIsPartOf().getId().toString());}
	else
	{
		
			possibleFailures=this.failureStructureService.findFaultFoundOptionsForModels(
					model.getIsPartOf().getIsPartOf().getId().toString());
	}
}
        Collections.sort(possibleFailures,new Comparator()
        {
            public int compare(Object obj1 , Object obj2){
                FailureTypeDefinition failure1 = (FailureTypeDefinition)obj1;
                FailureTypeDefinition failure2 = (FailureTypeDefinition)obj2;
                return failure1.getName().compareTo(failure2.getName());
            }
        });
        return possibleFailures;
	}

	private void prepareOEMPartCrossRef(Claim claim) {

		//TODO
		/*Organization organization = getLoggedInUser()
				.getBelongsToOrganization();

		if (InstanceOfUtil.isInstanceOfClass(Dealership.class, organization)) {
			prepareOEMCrossRefForDealer(claim);
		} else {
			prepareOEMCrossRefForInternalUser(claim);
		}*/
	}

	private void validateRootCauseForClaim(Claim theClaim) {
        if (isRootCauseAllowed() && theClaim != null && theClaim.getServiceInformation() != null) {
            /**
             * Root cause is mandatory only if configured for a given FF
             */
            Long modelId = null;
            Long faultFoundId = null;

            if (theClaim.getClaimedItems() != null && !theClaim.getClaimedItems().isEmpty()
                    && theClaim.getClaimedItems().get(0) != null
                    && theClaim.getClaimedItems().get(0).getItemReference() != null
                    && theClaim.getClaimedItems().get(0).getItemReference().isSerialized()) {
                modelId = theClaim.getClaimedItems().get(0).getItemReference()
                        .getReferredInventoryItem().getOfType().getModel().getId();
            } else if (theClaim.getItemReference() != null
                    && theClaim.getItemReference().getModel() != null) {
                modelId = theClaim.getItemReference().getModel().getId();
            }
            if (theClaim.getServiceInformation() != null
                    && theClaim.getServiceInformation().getFaultFound() != null) {
                faultFoundId = theClaim.getServiceInformation().getFaultFound().getId();
            }
            if (modelId != null && faultFoundId != null) {
                List<FailureRootCauseDefinition> possibleRootCauses = failureStructureService
                        .findRootCauseOptionsByModel(modelId.toString(), faultFoundId.toString());
                if (possibleRootCauses != null && !possibleRootCauses.isEmpty()) {
                    if (theClaim.getServiceInformation().getRootCause() == null) {
                    	addActionError("error.newClaim.rootCauseRequired");
                    }
                }
            }
        }
    }

	private void prepareOEMCrossRefForDealer(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = getCatalogService().findPartForOEMDealerPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealer());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(
						claim.getServiceInformation().getCausalPart());
				claim.getServiceInformation().setCausalPart(item);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
					.getServiceInformation().getServiceDetail()
					.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
						.next();
				Item item = getCatalogService().findPartForOEMDealerPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealer());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(oemPartReplaced
							.getItemReference().getReferredItem());
					oemPartReplaced.getItemReference().setReferredItem(item);
				}
			}
		}
	}

	private void prepareOEMCrossRefForInternalUser(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = getCatalogService().findOEMDealerPartForPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealer());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(item);
			} else {
				claim.getServiceInformation().setOemDealerCausalPart(null);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
					.getServiceInformation().getServiceDetail()
					.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
						.next();
				Item item = getCatalogService().findOEMDealerPartForPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealer());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(item);
				} else {
					oemPartReplaced.setOemDealerPartReplaced(null);
				}
			}
		}
	}

	public int getJsonCounter() {
		return this.jsonCounter;

	}

	protected void validateAdditionalAttributes(Claim claim) {
	    setActionErrors(claimSubmissionUtil.validateAdditionalAttributes(claim, this.task.getBaseFormName(), isPartShippedNotRcvd()));
	}

	private boolean isOEMDealerPartExistForPart(Item fromItem,
			Organization organization) {
		//Commenting to do a perf fix for IRI and Hussmann
		//This functionality is for club car only. It would be uncommented and used when
		//we merge code with club car or when it has been fixed.
		//TODO
/*		Item item = this.catalogService.findOEMDealerPartForPart(fromItem,
				organization);
		if (item != null) {
			return true;
		}*/
		return false;
	}
	
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public boolean isMatchReadApplicable() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.MATCH_READ_APPLICABLITY.getName());
	}

	public boolean isRootCauseAllowed() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.IS_ROOT_CAUSE_ALLOWED.getName());
	}

	public boolean isLaborSplitEnabled() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName());
	}

	public boolean isEnableStandardLaborHours() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_STANDARD_LABOR_HOURS.getName());
	}
	
	public boolean isCPAdvisorEnabled() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_CP_ADVISOR.getName());
	}

	public String getLaborSplitOption() {
		return getConfigParamService()
				.getStringValue(ConfigName.LABOR_SPLIT_DISTRIBUTION.getName());
	}	
	
	public void populateTechnicians(Organization organization,String businessUnitName) {
		ServiceProvider dealer = new HibernateCast<ServiceProvider>().cast(organization);
		technicians = this.userRepository.findTechnicianForDealer(dealer.getId(),businessUnitName);		
	}

	public Map<Long, String> getTechnicians() {
		return this.technicians;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}

	public Map<Boolean, String> getListOfLevelsForParts() {
		Map<Boolean, String> list = new HashMap<Boolean, String>();
		list.put(new Boolean(false),
				getText("claim.prieview.ContentPane.claim"));
		list.put(new Boolean(true),
				getText("accordion_jsp.accordionPane.inventory"));
		return list;
	}

	protected void setTotalQtyForReplacedParts(Claim claim) {
		claimSubmissionUtil.setTotalQtyForReplacedParts(claim);
	}
	
	protected void setTotalLaborHoursForClaim(Claim claim) {
	    claimSubmissionUtil.setTotalLaborHoursForClaim(claim);
	}

	protected void setQtyForMultipleItems(Claim claim) {
		claimSubmissionUtil.setQtyForMultipleItems(claim);
	}

	protected void setTotalLaborHoursForUiView(Claim claim) {
		List<LaborDetail> LaborDetails = claim.getServiceInformation()
				.getServiceDetail().getLaborPerformed();
		for (LaborDetail labor : LaborDetails) {
			if (labor != null) {
				labor.setHoursSpentForMultiClaim(labor.getHoursSpent());
				if (ClaimType.CAMPAIGN.equals(claim.getType()))
				{
					if(labor.getSpecifiedHoursInCampaign()!=null){
						labor.setHoursSpentForMultiClaim(labor.getSpecifiedHoursInCampaign());
					}
						if(claim.getCampaign().getCampaignServiceDetail()
							.getCampaignLaborLimits().size() != 0){
							if(!claim.getCampaign().getCampaignServiceDetail()
									.getCampaignLaborLimits().get(0)
									.isLaborStandardsUsed()){
							 if(labor.getSpecifiedHoursInCampaign()!= null){
									 labor.setSpecifiedHoursInCampaign(labor.getSpecifiedHoursInCampaign()
										.divide(new BigDecimal(
									getApprovedClaimedItems(claim)),
									4, 2));
							}
						}
				}
				}
				if (labor.getAdditionalLaborHours() != null) {
					if (getApprovedClaimedItems(claim) != 0) {
						labor
								.setAdditionalHoursSpentForMultiClaim(labor
										.getAdditionalLaborHours()
										.divide(
												new BigDecimal(
														getApprovedClaimedItems(claim)),
												4, 2));
					} else {
						labor
								.setAdditionalHoursSpentForMultiClaim(new BigDecimal(
										0));
					}
				}
			}
		}
	}

	public boolean isEditableForDealer() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.CAN_DEALER_EDIT_FWDED_CLMS
						.getName());
	}

	public boolean isEditableForProcessor() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.CAN_PROCESSOR_EDIT_CLMS.getName());
	}

	public String getFaultCodeID() {
		return this.faultCodeID;
	}

	public void setFaultCodeID(String faultCodeID) {
		this.faultCodeID = faultCodeID;
	}

	public String getSerProcedureId() {
		return this.serProcedureId;
	}

	public void setSerProcedureId(String serProcedureId) {
		this.serProcedureId = serProcedureId;
	}

	public List<ClaimAttributes> getClaimAttributes() {
		return this.claimAttributes;
	}

	public void setClaimAttributes(List<ClaimAttributes> claimAttributes) {
		this.claimAttributes = claimAttributes;
	}

	public String getIndexId() {
		return this.indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getFetchJobCode() {
		return fetchJobCode;
	}

	public void setFetchJobCode(String fetchJobCode) {
		this.fetchJobCode = fetchJobCode;
	}

	public List<ClaimAttributes> getServiceJobCodeAttributesList() {
		return serviceJobCodeAttributesList;
	}

	public void setServiceJobCodeAttributesList(
			List<ClaimAttributes> serviceJobCodeAttributesList) {
		this.serviceJobCodeAttributesList = serviceJobCodeAttributesList;
	}

	public String getPartNumber() {
		return this.partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public boolean isToPrint() {
		return this.toPrint;
	}

	public void setToPrint(boolean toPrint) {
		this.toPrint = toPrint;
	}

	public String getDsmAdvisor() {
		return dsmAdvisor;
	}

	public void setDsmAdvisor(String dsmAdvisor) {
		this.dsmAdvisor = dsmAdvisor;
	}	
	
	public void populateOrgAddress(Organization organization){
		this.orgAddresses = orgService.getAddressesForOrganization(organization);
		List<OrganizationAddress> orgAddressOfDualDealer = new ArrayList<OrganizationAddress>();
		if(isBuConfigAMER()){
			orgAddressOfDualDealer = getDualDealerOrganizations(organization,
					orgAddressOfDualDealer);	
		}
		this.orgAddresses.addAll(orgAddressOfDualDealer);
	}

	private List<OrganizationAddress> getDualDealerOrganizations(
			Organization organization,
			List<OrganizationAddress> orgAddressOfDualDealer) {
		Dealership dealer = new HibernateCast<Dealership>().cast(organization);
		if(isOrgDualBrandDealer(organization)){
			orgAddressOfDualDealer= orgService.getAddressesForOrganization(dealer.getDualDealer());
			String dualBrand = orgService
					.findMarketingGroupCodeBrandByDealership(dealer.getDualDealer());
			for(OrganizationAddress eachAddress:orgAddressOfDualDealer){
				eachAddress.setLocationWithBrand(dualBrand +" - "+eachAddress.getShipToCodeAppended());
			}
		}
		String brand = orgService
				.findMarketingGroupCodeBrandByDealership(dealer);
		if (null != dealer && null != dealer.getBrand()
				&& null != organization.getName()) {
			for(OrganizationAddress eachAddress:this.orgAddresses){
				eachAddress.setLocationWithBrand(brand +" - "+eachAddress.getShipToCodeAppended());
			}
		}
		return orgAddressOfDualDealer;
	}	

	private boolean isOrgDualBrandDealer(Organization org) {
		if(null !=new HibernateCast<Dealership>().cast(org).getDualDealer())
			return true;
		else 
			return false;
	
	}

	@SuppressWarnings("unchecked")
	public List<OrganizationAddress> getOrgAddresses() {
		if(isBuConfigAMER()){
		if(this.orgAddresses != null){
			Collections.sort(this.orgAddresses, new Comparator(){
				public int compare(Object obj0, Object obj1){
					OrganizationAddress address0 = (OrganizationAddress)obj0;
					OrganizationAddress address1 = (OrganizationAddress)obj1;
					return address0.getLocationWithBrand().compareTo(address1.getLocationWithBrand());
				}
			});
		}
	}
		else {
			if(this.orgAddresses != null){
				Collections.sort(this.orgAddresses, new Comparator(){
					public int compare(Object obj0, Object obj1){
						OrganizationAddress address0 = (OrganizationAddress)obj0;
						OrganizationAddress address1 = (OrganizationAddress)obj1;
						return address0.getShipToCodeAppended().compareTo(address1.getShipToCodeAppended());
					}
				});
			}
		}
		return this.orgAddresses;
	}

	public void setOrgAddresses(List<OrganizationAddress> orgAddresses) {
		this.orgAddresses = orgAddresses;
	}

	public Long getPrimaryOrganizationAddressForOrganization(
			Organization organization) {
		return orgService.getPrimaryOrganizationAddressForOrganization(
				organization).getId();
	}


	public OrganizationAddress getPrimaryOrganizationAddressForOrg(
			Organization organization) {
		return orgService.getPrimaryOrganizationAddressForOrganization(
				organization);
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Long getProcReviewTaskId() {
		return procReviewTaskId;
	}

	public void setProcReviewTaskId(Long procReviewTaskId) {
		this.procReviewTaskId = procReviewTaskId;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public boolean isCommentViewableByDealer(ClaimState claimState) {
		List<ClaimState> notViewableClaimStatesForDealer = new ArrayList<ClaimState>();
		notViewableClaimStatesForDealer.add(TRANSFERRED);
		notViewableClaimStatesForDealer.add(ADVICE_REQUEST);
		if (notViewableClaimStatesForDealer.contains(claimState)) {
			return false;
		}
		return true;
	}

	



	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public List<String> getCountriesFromMSA() {
		return countriesFromMSA;
	}

	public void setCountriesFromMSA(List<String> countriesFromMSA) {
		this.countriesFromMSA = countriesFromMSA;
	}

	public SortedHashMap<String, String> getCountryList() {
		return countryList;
	}

	public boolean checkForValidatableCountry(String country) {
		if (this.countriesFromMSA.contains(country)) {
			return true;
		}
		return false;
	}

	private void prepareMatchReadInfo() {
		if (this.task.getClaim()
				.getMatchReadInfo()!=null && !this.countriesFromMSA.contains(this.task.getClaim()
				.getMatchReadInfo().getOwnerCountry())) {
			this.task.getClaim().getMatchReadInfo().setOwnerState(
					getStateCode());
			this.task.getClaim().getMatchReadInfo().setOwnerCity(getCityCode());
			this.task.getClaim().getMatchReadInfo().setOwnerZipcode(
					getZipCode());
		}
	}

	private boolean validateMatchReadOwnerInfo() {
		boolean validState = true;
		boolean validCity = true;
		boolean validZip = true;
		boolean validAddressCombination = true;
		MatchReadInfo matchReadInfo = this.task.getClaim().getMatchReadInfo();
		if (!this.countriesFromMSA.contains(matchReadInfo.getOwnerCountry())) {
			if (getCityCode() == null || "".equals(getCityCode().trim())) {
				validCity = false;
			}
		} else {
			if (matchReadInfo.getOwnerState() == null
					|| "".equals(matchReadInfo.getOwnerState().trim())) {
				validState = false;
			}
			if (matchReadInfo.getOwnerCity() == null
					|| "".equals(matchReadInfo.getOwnerCity().trim())) {
				validCity = false;
			}
			if (matchReadInfo.getOwnerZipcode() == null
					|| "".equals(matchReadInfo.getOwnerZipcode().trim())) {
				validZip = false;
			}
			Address address = new Address();
			address.setCountry(matchReadInfo.getOwnerCountry());
			address.setState(matchReadInfo.getOwnerState());
			address.setCity(matchReadInfo.getOwnerCity());
			address.setZipCode(matchReadInfo.getOwnerZipcode());
			if (!validateAddressCombination(address)) {
				validAddressCombination = false;
			}
		}

		if (!validState) {
			addActionError("error.manageProfile.requiredState");
		}
		if (!validCity) {
			addActionError("error.manageProfile.requiredCity");
		}
		if (!validZip) {
			addActionError("error.manageProfile.requiredZipcode");
		}

		if (validState && validCity && validZip && !validAddressCombination) {
			addActionError("error.manageCustomer.invalidAddressCombination");
		}

		if (validState && validCity && validZip) {
			return true;
		}
		return false;
	}

	public void prepareMatchReadInfoForShow() {
		MatchReadInfo matchReadInfo = this.task.getClaim().getMatchReadInfo();
		if (!this.countriesFromMSA.contains(matchReadInfo.getOwnerCountry())) {
			setStateCode(matchReadInfo.getOwnerState());
			setCityCode(matchReadInfo.getOwnerCity());
			setZipCode(matchReadInfo.getOwnerZipcode());
		}
	}

	private boolean validateAddressCombination(Address address) {
		return this.msaService.isValidAddressCombination(address.getCountry(),
				address.getState(), address.getCity(), address.getZipCode());
	}

	protected void validateClaimForLaborDetail(Claim claim) {
	    setActionErrors(claimSubmissionUtil.validateClaimForLaborDetail(claim));
	}

	protected void setPartForCrossReference(Claim claim) {
		Organization organization = getLoggedInUser().getCurrentlyActiveOrganization();
		if (InstanceOfUtil.isInstanceOfClass(Dealership.class, organization)) {
			if (claim.getServiceInformation().getCausalPart() != null) {
				boolean isCrossRef = getCatalogService().isOEMDealerPartExistForPart(claim
						.getServiceInformation().getCausalPart(), organization);
				if (isCrossRef) {
					claim.getServiceInformation().setCausalPart(
							claim.getServiceInformation()
									.getOemDealerCausalPart());
				}
			}
			if (claim.getServiceInformation().getServiceDetail() != null
					&& claim.getServiceInformation().getServiceDetail()
							.getOEMPartsReplaced() != null) {
				for (OEMPartReplaced part : claim.getServiceInformation()
						.getServiceDetail().getOEMPartsReplaced()) {
					boolean isCrossRef = getCatalogService().isOEMDealerPartExistForPart(part
							.getItemReference().getReferredItem(), organization);
					if (isCrossRef) {
						part.getItemReference().setReferredItem(
								part.getOemDealerPartReplaced());
					}
				}
			}
		}
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public String getLateNightVariable() {
		return lateNightVariable;
	}

	public void setLateNightVariable(String lateNightVariable) {
		this.lateNightVariable = lateNightVariable;
	}

	
	protected void validateClaimForDuplicateJobCodes(Claim claim) {
	    addActionError(claimSubmissionUtil.validateClaimForDuplicateJobCodes(claim));
	}

	public String refreshPayment() {
		Claim theClaim = null;
		try {
			theClaim = this.task.getClaim();
		} catch (Exception e) {
			logger.error("Exception EX : " + e);
			return FATAL_ERROR;
		}
		logger.info("UNX: IN refreshOnEdit Claim " + theClaim);
		if (theClaim.getPayment() != null
				&& !theClaim.getPayment().getLineItemGroups().isEmpty()) {
			List<LineItemGroup> lineItemGroups = theClaim.getPayment()
					.getLineItemGroups();
		}
		//setTotalQtyForReplacedParts(theClaim);
		//setTotalLaborHoursForClaim(theClaim);

				if (isLaborSplitEnabled()) {
			if (!validateLaborType(theClaim)) {
				return INPUT;
			}
		}				
		computePayment(theClaim);
		if(checkForPaymentSystemErrors(theClaim,null,false)){
			return INPUT;	
		}
		return SUCCESS;
	}


	public String refreshActions() {
		Claim theClaim = null;
		try {
			theClaim = this.task.getClaim();
		} catch (Exception e) {
			logger.error("Exception EX : " + e);
			return FATAL_ERROR;
		}
		logger.info("UNX: IN refreshActionsOnEdit Claim " + theClaim);
		if (theClaim.getPayment() != null
				&& !theClaim.getPayment().getLineItemGroups().isEmpty()) {
			List<LineItemGroup> lineItemGroups = theClaim.getPayment()
					.getLineItemGroups();
		}
		setTotalQtyForReplacedParts(theClaim);
		setTotalLaborHoursForClaim(theClaim);

		if (!isLaborSplitEnabled() || (isLaborSplitEnabled() && validateLaborType(theClaim))) {
			computePayment(theClaim);
			if(checkForPaymentSystemErrors(theClaim,null,false)){
				return INPUT;	
			}
		}
		authorizeAllowedActionsForProcessor(theClaim, getLoggedInUser(), Boolean.TRUE);
		return SUCCESS;
	}

	


	private boolean validateLaborType(Claim claim) {
		ServiceInformation serviceInformation = claim.getServiceInformation();
		ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
		List<LaborDetail> laborDetails = serviceDetail.getLaborPerformed();
		List<LaborSplit> laborSplit = serviceDetail.getLaborSplit();
		BigDecimal totalInclusiveHrs = new BigDecimal(0.0);
		BigDecimal totalStdPlusAdditionalHrs = new BigDecimal(0.0);
		if (laborDetails.size() > 0) {
			for (LaborDetail laborDetail : laborDetails) {
				if (claim.getType().equals(ClaimType.CAMPAIGN)) {
					if (null != laborDetail.getSpecifiedHoursInCampaign()) {
						if(null != laborDetail.getAdditionalLaborHours())
						{
							totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
									.add(laborDetail
											.getSpecifiedHoursInCampaign()
											.add(laborDetail
													.getAdditionalLaborHours()));
						}
						else
						{
							totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
							.add(laborDetail
									.getSpecifiedHoursInCampaign());
						}
					} else {
						totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
								.add(laborDetail.getTotalHours(serviceDetail.getStdLaborEnabled()));
					}

				} else {
					BigDecimal totalHours = laborDetail.getTotalHours(serviceDetail.getStdLaborEnabled());
					if(totalHours != null)
					{
						totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs.add(totalHours);
					}
				}
			}
		}
		if (laborSplit.size() > 0 || claim.getType().equals(ClaimType.CAMPAIGN)) {
			for (LaborSplit lbrSplit : laborSplit) {
				if (lbrSplit != null) {
					if (null == lbrSplit.getLaborType().getLaborType()) {
						addActionError("error.laborType.laborTypeName");
						return false;
					}

					if (!StringUtils.hasText(lbrSplit.getReason())) {
						addActionError("error.laborType.reason");
						return false;
					}

					if (null == lbrSplit.getHoursSpent()
							|| !(lbrSplit.getHoursSpent().signum() == 1)) {
						addActionError("error.laborType.hoursSpent");
						return false;
					}
					if (lbrSplit.getInclusive()) {
						totalInclusiveHrs = totalInclusiveHrs.add(lbrSplit
								.getHoursSpent());
					}
				}
			}
		}
		if (totalInclusiveHrs.doubleValue() > totalStdPlusAdditionalHrs
				.doubleValue()) {
			addActionError("error.laborType.validate.message");
			return false;
		}
		return true;
	}

	 public LineItemGroup getLineItemGroupAuditForGlobalLevel(Claim claim) {
			if (claim.getPayment() != null && !claim.getPayment().getLineItemGroups().isEmpty()) {
				
				return this.paymentService.computeSummationSectionForDisplay(claim);
			}
			return null;
	 }

	public boolean isLineItemPercentageChanged(Claim claim) {
		boolean isLineItemPercentageChanged = false;
		for (LineItemGroup lineItemGroup : claim.getPayment()
				.getLineItemGroups()) {
			if (lineItemGroup.getLatestAudit()!=null && !Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& lineItemGroup.getLatestAudit().getPercentageAcceptance()
							.longValue() != new Long(100).longValue()) {
				isLineItemPercentageChanged = true;
				break;
			}
		}
		return isLineItemPercentageChanged;
	}

    public boolean isLineItemForCPPercentageChanged(Claim claim) {
		boolean isLineItemForCPPercentageChanged = false;
		for (LineItemGroup lineItemGroup : claim.getPayment()
				.getLineItemGroups()) {
				if (lineItemGroup.getName()!=null && !Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& lineItemGroup.getPercentageAcceptedForAdditionalInfo(AdditionalPaymentType.ACCEPTED_FOR_CP)
							.longValue() != new Long(0).longValue()) {
				isLineItemForCPPercentageChanged = true;
				break;
			}
		}
		return isLineItemForCPPercentageChanged;
	}

    private void updatePartReceivedCount(Claim claim) {
		if (ON_PART_RETURN.equalsIgnoreCase(getConfigParamService()
						.getStringValue(ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY
								.getName()))) {
			List<OEMPartReplaced> oEMPartReplaced = claim
					.getServiceInformation().getServiceDetail()
					.getReplacedParts();
			for (OEMPartReplaced oemPartReplaced : oEMPartReplaced) {
				if (oemPartReplaced != null
						&& oemPartReplaced.getItemReference() != null) {
					PartReturnConfiguration partReturnConfiguration = oemPartReplaced
							.getPartReturnConfiguration();
					if (partReturnConfiguration != null
							&& oemPartReplaced.getPartReturns() != null
							&& !oemPartReplaced.getPartReturns().isEmpty()
							&& partReturnConfiguration.getMaxQuantity() != null) {
						partReturnConfiguration
								.setQuantityReceived(partReturnConfiguration
										.getQuantityReceived()
										+ oemPartReplaced.getPartReturns()
												.size());
						partReturnService
								.updatePartReturnConfiguration(partReturnConfiguration);
					}
				}
			}
		}

	}



	public void setPartReturnDefinitionRepository(
			PartReturnDefinitionRepository partReturnDefinitionRepository) {
		this.partReturnDefinitionRepository = partReturnDefinitionRepository;
	}

	public String getForSerialized() {
		return forSerialized;
	}
	public int getRowIndex() {
		return rowIndex;
	}

	public Boolean actionAllowedForUser(String action) {
		if (this.task.getClaim() == null
				|| this.task.getClaim().getAllowedActionsList() == null
				|| this.task.getClaim().getAllowedActionsList().isEmpty())
			return Boolean.FALSE;
		return this.task.getClaim().getAllowedActionsList().contains(action);
	}

	public void setForSerialized(String forSerialized) {
		this.forSerialized = forSerialized;
	}

	public Boolean getAuthorizeAllowedAction() {
		return authorizeAllowedAction;
	}

	@SuppressWarnings("deprecation")
	public void setClaimProcessedAs(Claim claim) {
		claimSubmissionUtil.setClaimProcessedAs(claim);
	}

	public String getClaimProcessedAsForDisplay(Claim claim) {
		if (!org.apache.commons.lang.StringUtils.isEmpty(claim.getPolicyCode())) {
			return claim.getPolicyCode();
		}
		if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_STOCK.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.itemInStock");
		} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.outOfWarranty");
		}
		return claim.getClaimedItems().get(0).getApplicablePolicy().getCode();
	}

	public Boolean getIsThirdParty() {
		return isThirdParty;
	}

	public Boolean isThirdPartyClaim() {
		return this.task!=null && this.task.getClaim()!=null &&
		( InstanceOfUtil.isInstanceOfClass(ThirdParty.class, this.task.getClaim().getForDealer()));
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public void setIsThirdParty(Boolean isThirdParty) {
		this.isThirdParty = isThirdParty;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	protected boolean isMachineClaim(Claim claim) {
		return this.claimSubmissionUtil.isMachineClaim(claim);
	}

	private boolean isPartsClaim(Claim claim) {
		return claimSubmissionUtil.isPartsClaim(claim);
	}

	public boolean isSellingEntityToBeCaptured(Claim claim) {
		if (isProcessorReview()) {
			// machine non-ser
			if (isMachineClaim(claim) && !isSerializedClaim(claim)) {
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
				return getConfigParamService().getBooleanValue(ConfigName.CAPTURE_SELLING_ENTITY.getName());
			}
		}
		return false;
	}

	public boolean isSourceWarehouseToBeCaptured(Claim claim) {
		if (isProcessorReview()) {
			// machine non-ser/parts with host non-ser/parts without host
			if ((isMachineClaim(claim) || isPartsClaim(claim)) && !isSerializedClaim(claim)) {
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
				return getConfigParamService().getBooleanValue(ConfigName.CAPTURE_SOURCE_WAREHOUSE.getName());
			}
		}
		return false;
	}

    public List<SourceWarehouse> findAllSourceWareHouse(){
        List<SourceWarehouse> warehouses = sourceWarehouseRepository.findAll();
        if(warehouses != null && !warehouses.isEmpty())
        {
        Collections.sort(warehouses);
        }
        return warehouses;
    }

    public void setSourceWarehouseRepository(SourceWarehouseRepository sourceWarehouseRepository) {
        this.sourceWarehouseRepository = sourceWarehouseRepository;
    }

	/*
	 * (non-Javadoc)
	 * @see tavant.twms.web.inbox.SummaryTableAction#getInboxViewContext()
	 */
	protected String getInboxViewContext() {
		return BusinessObjectModelFactory.CLAIM_SEARCHES;
	}

	public boolean isDateCodeEnabled() {
		return getConfigParamService().getBooleanValue(ConfigName.IS_DATE_CODE_ENABLED.getName());
	}

	public int getSubRowIndex() {
		return subRowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public void setSubRowIndex(int subRowIndex) {
		this.subRowIndex = subRowIndex;
	}

	public ReplacedInstalledPartsService getReplacedInstalledPartsService() {
		return replacedInstalledPartsService;
	}

	public void setReplacedInstalledPartsService(
			ReplacedInstalledPartsService replacedInstalledPartsService) {
		this.replacedInstalledPartsService = replacedInstalledPartsService;
	}	

    public boolean isMultipleJobCodeAllowed(){
        return getConfigParamService().getBooleanValue(ConfigName.MULTIPLE_JOB_CODE_ALLOWED.getName());
    }

     public boolean isLegalDisclaimerAllowed(){
        return getConfigParamService().getBooleanValue(ConfigName.LEGAL_DISCLAIMER_ALLOWED.getName());
     }

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getContextValue() {
		return contextValue;
	}

	public void setContextValue(String contextValue) {
		this.contextValue = contextValue;
	}

	public Claim getClaimDetail() {
		return claimDetail;
	}

	public void setClaimDetail(Claim claimDetail) {
		this.claimDetail = claimDetail;
	}

	private String[] getTaskNameAsPerClaimState(Claim claim) {
		if (ClaimState.PROCESSOR_REVIEW.equals(claim.getState()) || ClaimState.REOPENED.equals(claim.getState())) {
			return new String[] {"Processor Review", "Part Shipped Not Received"};
		} else if (ClaimState.REJECTED_PART_RETURN
				.equals(claim.getState())) {
			return new String[] {"Rejected Part Return"};

		} else if (ClaimState.APPEALED.equals(claim.getState())) {
			return new String[]{"Appeals"};

		} else if (ClaimState.ON_HOLD.equals(claim.getState())) {
			return new String[] {"On Hold"};

		} else if (ClaimState.ON_HOLD_FOR_PART_RETURN.equals(claim
				.getState())) {
			return new String[] {"On Hold For Part Return"};
		} else if (ClaimState.REPLIES.equals(claim.getState()) || ClaimState.EXTERNAL_REPLIES.equals(claim.getState()) ) {
			return new String[]{"Replies"};
		}
		else if (ClaimState.TRANSFERRED.equals(claim.getState())) {
			return new String[] {"Transferred"};
		}else if (ClaimState.APPROVED.equals(claim.getState())) {
			return new String[] {"Pending Authorization"};
		}
		return null;
	}

	public boolean isAutoCheckRecoveryFlag() {
		return autoCheckRecoveryFlag;
	}

	public void setAutoCheckRecoveryFlag(boolean autoCheckRecoveryFlag) {
		this.autoCheckRecoveryFlag = autoCheckRecoveryFlag;
	}

	public String getAnonymousThirdPartyNumber() {
		return anonymousThirdPartyNumber;
	}

	public void setThirdPartyDealerName(String thirdPartyDealerName) {
		this.thirdPartyDealerName = thirdPartyDealerName;
	}

	public boolean isPartShippedNotRcvd() {
		return partShippedNotRcvd;
	}

	public void setPartShippedNotRcvd(boolean partShippedNotRcvd) {
		this.partShippedNotRcvd = partShippedNotRcvd;
	}


   
	public boolean isShowReRequestsForSMR() {
		return showReRequestsForSMR;
	}

	public void setShowReRequestsForSMR(boolean showReRequestsForSMR) {
		this.showReRequestsForSMR = showReRequestsForSMR;
	}


    protected void isComputationRequired(Claim claim){
        boolean canUpdatePayment=false;
        if (ClaimState.DRAFT.getState().equals(claim.getState().getState())
                && "Submit Claim".equals(getTask().getTakenTransition())) {
            canUpdatePayment = true;
        } else if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equals(claim.getState().getState())) {
            canUpdatePayment = true;
        } else if (isProcessorReview()) {
            canUpdatePayment = true;
        } else if(ClaimState.FORWARDED.getState().equals(claim.getState().getState())
                && getConfigParamService().getBooleanValue(ConfigName.CAN_DEALER_EDIT_FWDED_CLMS.getName()).booleanValue()){
            canUpdatePayment = true;
        }
        claim.setCanUpdatePayment(canUpdatePayment);
    }
    public boolean isDealerEligibleToFillSmrClaim(){
        	 boolean isEligible = false;
        Map<String, List<Object>> buValues = getConfigParamService().
                getValuesForAllBUs(ConfigName.SMR_CLAIM_ALLOWED.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }
 
public boolean isTechnicianEnable(){
        boolean isEligible = false;
        Map<String, List<Object>> buValues = getConfigParamService().
                getValuesForAllBUs(ConfigName.ENABLE_TECHNICIAN.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }
    
    
    public boolean isStdLaborHrsToDisplay(Claim claim){
        boolean toReturn = isEnableStandardLaborHours();
        if (ClaimState.DRAFT.getState().equalsIgnoreCase(claim.getState().getState())) {
            if (toReturn) {
                for (LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
                    laborDetail.setLaborHrsEntered(null);
                }
            }
            return toReturn;
        } else {
            return claim.getServiceInformation().getServiceDetail().getStdLaborEnabled();
        }
    }

	public List<String> getLoaWarningMessages() {
		return loaWarningMessages;
	}

	public void setLoaWarningMessages(List<String> loaWarningMessages) {
		this.loaWarningMessages = loaWarningMessages;
	}

    public List<OEMPartReplaced> getInitialReplacedParts() {
        return initialReplacedParts;
    }

    public void setInitialReplacedParts(List<OEMPartReplaced> initialReplacedParts) {
        this.initialReplacedParts = initialReplacedParts;
    }

	public String getCostPriceType(){
		return getConfigParamService().getStringValue(ConfigName.COST_PRICE_CONFIGURATION.getName());
	}

	public String getDraftClaimWindowPeriodForBU(String buName)
    {
    	String windowPeriod = null;
    	if(daysForDraftDeletion == null)
    	{
	    	daysForDraftDeletion = getConfigParamService()
				.getValuesForAllBUs(ConfigName.DAYS_FOR_DRAFTCLAIM_DELETION
						.getName());
    	}
    	if (daysForDraftDeletion != null && daysForDraftDeletion.get(buName) != null)
    	{
    		windowPeriod = (String)daysForDraftDeletion.get(buName).get(0);
    	}
    	return windowPeriod;
    }

	public String getDateToUseForDraftClaimDeletionBU(String buName)
    {
    	String toReturn = null;
    	if(dateForDraftDeletion == null)
    	{
	    	dateForDraftDeletion = getConfigParamService()
				.getValuesForAllBUs(ConfigName.DATE_TOUSER_FOR_DRAFTCLAIM_DELETION
						.getName());
    	}
    	if (dateForDraftDeletion != null && dateForDraftDeletion.get(buName) != null)
    	{
    		toReturn = (String)dateForDraftDeletion.get(buName).get(0);
    	}
    	return toReturn;
    }

	public String getForwardedClaimDenialWindowPeridoForBU(String buName)
    {
    	String toReturn = null;
    	if(daysForForwardedClaimDenial == null)
    	{
	    	daysForForwardedClaimDenial = getConfigParamService()
				.getValuesForAllBUs(ConfigName.DAYS_FOR_FORWARDED_CLAIM_DENIED
						.getName());
    	}
    	if (daysForForwardedClaimDenial != null && daysForForwardedClaimDenial.get(buName) != null)
    	{
    		toReturn = (String)daysForForwardedClaimDenial.get(buName).get(0);
    	}
    	return toReturn;
    }

	public String getDefaultAcceptanceReason()
	{

		List<ListOfValues> configValues = getConfigParamService().getListOfValues(ConfigName.DEFAULT_ACCEPTANCE_REASON.getName());
		if( configValues != null)
		{
			return configValues.get(0).getCode();
		}
		else
		{
			return null;
		}

	}
	
	public String getDefaultAccountabilityCode()
	{
		List<ListOfValues> configValues = getConfigParamService().getListOfValues(ConfigName.DEFAULT_ACCOUNTABILITY_CODE.getName());
		if( configValues != null)
			return configValues.get(0).getCode();
		else{
			return null;
		}
	}
	
	public String getDefaultRejectionReason()
	{
		List<ListOfValues> configValues = getConfigParamService().getListOfValues(ConfigName.DEFAULT_REJECTION_REASON.getName());
		if( configValues != null)
			return configValues.get(0).getCode();
		else{
			return null;
		}
	}

	public String getCurrentClaimAssignee(){
        return getCurrentClaimAssignee(this.task.getClaim().getAssignToUser());
    }

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public List<String> getEligibleLOAProcessors() {
		if(this.task != null && this.task.getClaim().getLoaScheme() != null && eligibleLOAProcessors.size()==0 ){
			eligibleLOAProcessors = this.task.getClaim().getLoaScheme().getEligibleLOAProcessorList();
		}
		return eligibleLOAProcessors;
	}
	
	public void setNextLOAProcessor(String nextLOAProcessor) {
		this.nextLOAProcessor = nextLOAProcessor;
	}
	
	public String getNextNewLOAProcessor() {
    return SUCCESS;
	}
	
	private Set<User> getUsersForLOALevel (List<LimitOfAuthorityLevel> loaLevels, int requiredLevel) {
		Set<User> usersForLevel = new HashSet<User>();
		for (LimitOfAuthorityLevel level : loaLevels) {
			if (level.getLoaLevel().intValue() == requiredLevel) {
				usersForLevel.add(level.getLoaUser());
			}
			if (level.getLoaLevel().intValue() > requiredLevel) {
				break; // this loop can be stopped since the loaLevels are sorted
			}
		}
		return usersForLevel;
	}
	
	private String getNextLevelProcessor (List<LimitOfAuthorityLevel> loaLevels, List<Integer> distinctLOALevels, int currentProcessorLevel) {
		String nextLevelProcessor = null;
		for (Integer loaLevel : distinctLOALevels) {
			Set<User> levelUsers = new HashSet<User>();
			List<String> levelUserNames = new ArrayList<String>();
			if (loaLevel.intValue() > currentProcessorLevel) {
				levelUsers = getUsersForLOALevel(loaLevels, loaLevel.intValue());
				filterAvailableUsers(levelUsers, this.task.getClaim().getBusinessUnitInfo().getName(),
						Role.PROCESSOR);
				for (User user : levelUsers) {
					levelUserNames.add(user.getName());
				}
				nextLevelProcessor = levelUsers.size() > 0 ? getUserWithLeastLoad(levelUserNames) : null;
			}
			if (nextLevelProcessor != null) {
				break;
			}
		}
		return nextLevelProcessor;
	}
	
	private String getNextLevelLOAProcessor(List<LimitOfAuthorityLevel> loaLevels, String currentProcessor) {	
		List<Integer> distinctLOALevels = new ArrayList<Integer>();
		int currentProcessorLevel = 0;
		Integer currentLevel = loaLevels.get(0).getLoaLevel().intValue();
		distinctLOALevels.add(currentLevel);
		for (LimitOfAuthorityLevel loaLevel : loaLevels) {
			if (loaLevel.getLoaLevel().intValue() > currentLevel.intValue()) {
				currentLevel = loaLevel.getLoaLevel().intValue();
				distinctLOALevels.add(currentLevel);
			}
			if (loaLevel.getLoaUser().getName().equals(currentProcessor)) {
				currentProcessorLevel = loaLevel.getLoaLevel().intValue();
			}
		}
		return getNextLevelProcessor(loaLevels, distinctLOALevels, currentProcessorLevel);
	}
	
	// This will assign next LOA Processor depending on availability and load.
	public String getNextLOAProcessor() {
		if ((this.task != null) && (this.task.getClaim() != null) && this.task.getClaim().getLoaScheme() != null) {
			String currentLOAProcessor = null;
			if (eligibleLOAProcessors.size() == 0) {
				eligibleLOAProcessors = this.task.getClaim().getLoaScheme().getEligibleLOAProcessorList();
			}
			if (eligibleLOAProcessors.contains(getLoggedInUser().getName())) {
				currentLOAProcessor = getLoggedInUser().getName();
			} else {
				for (ClaimAudit claimAudit : getClaimAudits()) {
					if (eligibleLOAProcessors.contains(claimAudit.getUpdatedBy().getName())) {
						currentLOAProcessor = claimAudit.getUpdatedBy().getName();
						break;
					}
				}
			}

			List<LimitOfAuthorityLevel> levels = this.task.getClaim().getLoaScheme().getLoaLevels();
			Collections.sort(levels);
			if (this.task.getClaim().getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
				return getNextLevelLOAProcessor(levels, currentLOAProcessor);
			}
			List<String> userList = new ArrayList();
			int currentLevel = 0, nextLevel = 0;
			Set<User> nextLevelUsers = new HashSet<User>();

			for (LimitOfAuthorityLevel limitOfAuthorityLevel : levels) {
				if (limitOfAuthorityLevel.getLoaUser().getName().equalsIgnoreCase(currentLOAProcessor)) {
					currentLevel = limitOfAuthorityLevel.getLoaLevel().intValue();
					int i = 0;
					for (LimitOfAuthorityLevel limitOfAuthorityLevel2 : levels) {
						i++;
						if ((nextLevel == 0 || (nextLevel != 0 && nextLevel == limitOfAuthorityLevel2.getLoaLevel()
								.intValue()))
								&& limitOfAuthorityLevel2.getLoaLevel().intValue() > currentLevel) {

							nextLevel = limitOfAuthorityLevel2.getLoaLevel().intValue();
							nextLevelUsers.add(limitOfAuthorityLevel2.getLoaUser());
						} else if (nextLevel != 0) {
							filterAvailableUsers(nextLevelUsers, this.task.getClaim().getBusinessUnitInfo().getName(),
									Role.PROCESSOR);

							if (nextLevelUsers.size() == 0) {
								currentLevel = nextLevel;
								nextLevel = 0;
								nextLevelUsers.add(limitOfAuthorityLevel2.getLoaUser());
							} else {
								for (User user : nextLevelUsers) {
									userList.add(user.getName());
								}
								return (userList.size()==0) ? null:getUserWithLeastLoad(userList);
							}
						}
						if (levels.size() == i && nextLevelUsers != null && nextLevelUsers.size() > 0) {
							filterAvailableUsers(nextLevelUsers, this.task.getClaim().getBusinessUnitInfo().getName(),
									Role.PROCESSOR);
							for (User user : nextLevelUsers) {
								userList.add(user.getName());
							}
							return (userList.size()==0) ? null:getUserWithLeastLoad(userList);
						}
					}
				}
			}
		}
		return null;
	}

	public boolean isLOAProcessor() {

		if (eligibleLOAProcessors != null) {
			return eligibleLOAProcessors.contains(getLoggedInUser());
		} else {
			if (this.task != null && this.task.getClaim().getLoaScheme() != null && eligibleLOAProcessors.size() == 0) {
				eligibleLOAProcessors = this.task.getClaim().getLoaScheme().getEligibleLOAProcessorList();
				return eligibleLOAProcessors.contains(getLoggedInUser());
			} else {
				return false;
			}
		}
	}

	public String getCommentsForDisplay() {
		return commentsForDisplay;
	}

	public void setCommentsForDisplay(String commentsForDisplay) {
		this.commentsForDisplay = commentsForDisplay;
	}
	
	public String getTestToEvaluate() {
		return testToEvaluate;
	}

	public void setTestToEvaluate(String testToEvaluate) {
		this.testToEvaluate = testToEvaluate;
	}
	
	public Boolean getSkipActionMessage() {
		return skipActionMessage;
	}

	public void setSkipActionMessage(Boolean skipActionMessage) {
		this.skipActionMessage = skipActionMessage;
	}
	
	public PaymentService getPaymentService() {
		return paymentService;
	}

	public PartReturnService getPartReturnService() {
		return partReturnService;
	}

	public List<OEMPartReplaced> getInitialOEMReplacedParts() {
		return initialOEMReplacedParts;
	}

	public void setInitialOEMReplacedParts(List<OEMPartReplaced> initialOEMReplacedParts) {
		this.initialOEMReplacedParts = initialOEMReplacedParts;
	}
	
	public void setClaim(Claim claimDetail){
		this.task = this.task == null ? new TaskView() : this.task;//Used to set the task value for the ajax call for removed/installed widget
		this.getTask().setClaim(claimDetail);
	}
	
	public String getOemRemovedInstalledPartTemplate() {
		 return SUCCESS;
	}
	
	public String getOemRemovedPartTemplate() {
		 return SUCCESS;
	}
	
	public String getOemInstalledPartTemplate() {
		 return SUCCESS;
	}
	
	public String getBaseFormName() {
		return baseFormName;
	}

	public void setBaseFormName(String baseFormName) {
		this.baseFormName = baseFormName;
	}
	
	public boolean isSupplierRecovery() {
		return supplierRecovery;
	}

	public void setSupplierRecovery(boolean supplierRecovery) {
		this.supplierRecovery = supplierRecovery;
	}

	public Suppliers getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(Suppliers suppliers) {
		this.suppliers = suppliers;
	}
	
	
	/**
	 * Method called in jsp to calculate net claim amount, after deduction of late fees.
	 * @param claimAmount :{@link com.domainlanguage.money.Money}
	 * @param lateFee :{@link com.domainlanguage.money.Money}
	 * @return net claim amount :{@link com.domainlanguage.money.Money}
	 * @author santiswaroop.k
	 */
	public Money computeClaimAmountAfterLateFee(Money claimAmount,Money lateFee){
		return claimAmount.minus(lateFee); // deduction of late fee from claim amount.
	}
	
	private void cancelPaymentSubmission() {

		SyncTracker syncTracker = syncTrackerService
				.findByStatusSyncTypeAndUniqueIdValue(
						SyncStatus.TO_BE_PROCESSED.getStatus(), "Claim",
						this.claimDetail.getClaimNumber());
		if (syncTracker != null) {
			syncTracker.setStatus(SyncStatus.CANCELLED);
			syncTracker.setUpdateDate(new Date());
			syncTrackerService.update(syncTracker);
		}

	}

	public SyncTrackerService getSyncTrackerService() {
		return syncTrackerService;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}

    public boolean isStateNew(){
        return this.task.getClaim().getState().equals(ClaimState.PROCESSOR_REVIEW)?Boolean.TRUE:Boolean.FALSE;
    }

	public ItemGroup getModel() {
		return model;
	}

	public void setModel(ItemGroup model) {
		this.model = model;
	}

	private List<Item> getSupplierItemsForOEMItem(Claim claim){
		if(claim.getServiceInformation() != null && claim.getServiceInformation().getCausalPart() != null)
			return itemMappingRepository.findSupplierItemsForOEMItem(claim.getServiceInformation().getCausalPart());
		else
			return null;
	}
	private List<RegisteredPolicy> listApplicablePolicies(InventoryItem inventoryItem,Claim claim) {
		List<RegisteredPolicy> existingPolices = new ArrayList<RegisteredPolicy>();
		Warranty earlierWarranty = inventoryItem.getWarranty();
        if(earlierWarranty != null){
                for (RegisteredPolicy registeredPolicy : earlierWarranty.getPolicies()) {
                    if (registeredPolicy.getPolicyDefinition().getTransferDetails()
                            .isTransferable()
                            && !registeredPolicy.getWarrantyPeriod().getTillDate()
                            .isBefore(inventoryItem.getDeliveryDate())){
                        if(claim.getFailureDate().isBefore(registeredPolicy.getWarrantyPeriod().getTillDate().nextDay())){
                            if (RegisteredPolicyStatusType.ACTIVE.getStatus().
                                equals(registeredPolicy.getLatestPolicyAudit().getStatus())) {
                                    if(claim.getHoursInService().intValue() <= registeredPolicy.getLatestPolicyAudit().getServiceHoursCovered())
                                    	if(registeredPolicy.getPolicyDefinition().getApplicabilityTerms().isEmpty())
                            existingPolices.add(registeredPolicy);
                        }
                    }
                }
            }
        }
		Collections.sort(existingPolices);
			return existingPolices;

}
	public Supplier getSupplierById(Long id){
		Supplier supplier = supplierService.findById(id);
		if(supplier!=null && StringUtils.hasText(supplier.getStatus())
				&& STATUS_ACTIVE.equals(supplier.getStatus())){
			return supplier;
	}
		return null;
	}
	
	public boolean enableGoogleMapsForTravelHours(){
		if(getConfigParamService().getBooleanValue(ConfigName.GOOGLE_MAPS_FOR_TRAVEL_HOURS.getName())){
			return true;
		}else{
			return false;
		}
	}
	
	public List<String> getPolicyCodes() {
		return policyCodes;
	}

	public void setPolicyCodes(List<String> policyCodes) {
		this.policyCodes = policyCodes;
	}	

	public StateMandatesService getStateMandatesService() {
		return stateMandatesService;
	}

	public void setStateMandatesService(StateMandatesService stateMandatesService) {
		this.stateMandatesService = stateMandatesService;
	}

	public Double getConfigValueForTransportation(){		
		return getConfigParamService().getBigDecimalValue(ConfigName.TRANSPORTATION_RATE_PER_LOADED_MILE.getName()).doubleValue();
	}
	
    private void validatePartsClaimforStandardPolicy(Claim claim) {
		if(claim.getType().equals(ClaimType.PARTS)){
			if(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem()!=null){
				List<RegisteredPolicy> policies=listApplicablePolicies(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem(),claim);
				if(!policies.isEmpty()){
					for(RegisteredPolicy policy:policies){
						if(policy.getWarrantyType().getType().equals(WarrantyType.STANDARD.getType())){
							if (getConfigParamService().getBooleanValue(
									ConfigName.VALIDATE_STD_WRNTY_ON_PARTS_CLAIMS.getName())) 
							addActionError("error.partsClaim.standardWarrantyPeriodExists");
							else
								addActionWarning("warning.partsClaim.standardWarrantyPeriodExists");
							break;	
						}
					}
				}
			}
			
		}
	}
    public String getCpAdvisor() {
		return cpAdvisor;
	}

	public void setCpAdvisor(String cpAdvisor) {
		this.cpAdvisor = cpAdvisor;
	}	
	
	public boolean isCreateTechnicianEnabled() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_CREATE_TECHNICIAN_HYPERLINK.getName());
	}
	
	private boolean isTechRequiredForPartsClaim() {
		return getConfigParamService().getBooleanValue(
				ConfigName.TECHNICIAN_CERTIFICATION_FOR_PARTS_CLAIMS.getName());
	}
	
    public boolean isViewUnitComments(){
    	return getConfigParamService().getBooleanValue(ConfigName.VIEW_UNIT_COMMENTS.getName());
    }
    
    private void validateMarketingGroupCodes(Claim claim,boolean forProcessor){
    	MarketingGroupsLookup lookup = new MarketingGroupsLookup();
		lookup.setClaimType(claim.getType().getType());
    	lookup.setTruckMktgGroupCode(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getMarketingGroupCode());
    	if(StringUtils.hasText(claim.getPolicyCode())){
    		if(claim.getPolicyCode().equals("Policy"))
        		lookup.setWarrantyType(WarrantyType.POLICY.getType());
    		else if(claim.getPolicyCode().equals("Stock"))
    		lookup.setWarrantyType(WarrantyType.STANDARD.getType());
    		else{
    	    	PolicyDefinition policyDefinition  = policyDefinitionRepository.findPolicyDefinitionByCode(claim.getPolicyCode());
    	    	lookup.setWarrantyType(policyDefinition.getWarrantyType().getType());
    	    	}
    	}
    	if(claim.getType().equals(ClaimType.CAMPAIGN))
    		lookup.setWarrantyType("FPI");
    	
		
		lookup.setDealerMktgGroupCode(new HibernateCast<Dealership>()
				.cast(claim.getForDealer()).getMarketingGroup());
		
		List<MarketingGroupsLookup> lookUpResult = getClaimService().lookUpMktgGroupCodes(lookup,forProcessor);
		if(null == lookUpResult || lookUpResult.isEmpty()){
				addActionError("error.claim.applicablePolicy");
		}
    }
    
	public List<String> displayJobCodeDescription() {
		List<String> jobCodeDescription = new ArrayList<String>();
		if (task.getClaim().getServiceInformation() != null
				&& task.getClaim().getServiceInformation().getServiceDetail() != null
				&& !task.getClaim().getServiceInformation().getServiceDetail()
						.getLaborPerformed().isEmpty())
			for (LaborDetail laborPerformed : task.getClaim()
					.getServiceInformation().getServiceDetail()
					.getLaborPerformed()) {
				jobCodeDescription.add(laborPerformed.getServiceProcedure()
						.getDefinedFor().getJobCodeDescription());
			}
		return jobCodeDescription;
	}
	
}

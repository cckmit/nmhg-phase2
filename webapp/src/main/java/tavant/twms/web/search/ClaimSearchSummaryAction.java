/**
 *
 */
package tavant.twms.web.search;

import static tavant.twms.domain.claim.ClaimState.PROCESSOR_REVIEW;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSONForRecoveryClaim;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.DocumentException;
import org.json.JSONException;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimFolderNames;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimXMLConverter;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.LineItem;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PutOnHoldReason;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.RequestInfoFromUser;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.loa.LimitOfAuthorityLevel;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.PolicyProductMapping;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.stateMandates.StateMandatesService;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.integration.layer.util.CurrencyConvertor;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.claim.BaseClaimsAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.WorkListService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

/**
 * @author kaustubhshobhan.b
 * 
 */
@SuppressWarnings("serial")
public class ClaimSearchSummaryAction extends BaseClaimsAction implements
		ServletRequestAware, Preparable {

	private final Logger logger = Logger.getLogger(this.getClass());
	private HttpServletRequest servletRequest;
	private String domainPredicateId;
	private String savedQueryId;
	public static final String SHOW_SEARCH_PARAM = "show_search_param";
	protected TaskView task;
	protected TaskViewService taskViewService;
	private Claim claim;
	private ClaimAudit claimAudit;
	private RecoveryClaim recoveryClaim;
	private ClaimXMLConverter claimXMLConverter;
	
	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;

	private String claimNumber; // Used for quick based on claim Number
	private WorkListItemService workListItemService;
	private boolean claimWithLoggedInUser;

	private List<CostCategory> configuredCostCategories = new ArrayList<CostCategory>();
	private CostCategoryRepository costCategoryRepository;
	private Boolean showClaimAudit = new Boolean(false);
	private WorkListService workListService;
	private ClaimProcessService claimProcessService;
	private String context;
	private CampaignService campaignService;
	private ItemGroupService itemGroupService;
	private StateMandatesService stateMandatesService;
	private List<String> reasonsList = new ArrayList<String>();
	private String claimState;
	private CountryStateService countryStateService;
	private PolicyDefinitionRepository policyDefinitionRepository;
	private boolean incidentalsAvaialable;
	
	public boolean getIncidentalsAvaialable() {
		return incidentalsAvaialable;
	}

	public void setIncidentalsAvaialable(boolean incidentalsAvaialable) {
		this.incidentalsAvaialable = incidentalsAvaialable;
	}
	
	public void setCountryStateService(CountryStateService countryStateService) {
			this.countryStateService = countryStateService;
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
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isPageReadOnly() {
		return false;
	}

	public boolean isClaimWithLoggedInUser() {
		return claimWithLoggedInUser;
	}

	public void setClaimWithLoggedInUser(boolean claimWithLoggedInUser) {
		this.claimWithLoggedInUser = claimWithLoggedInUser;
	}	
	
	public ClaimCurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}
	
	public PolicyDefinitionRepository getPolicyDefinitionRepository() {
		return policyDefinitionRepository;
	}

	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	public ClaimSearchSummaryAction() {
		super();
	}

	@Override
	protected PageResult<?> getBody() {

		PageResult<Claim> claims = null;

		if (this.domainPredicateId != null
				&& !("".equals(this.domainPredicateId.trim()))) {
			ListCriteria criteria = getCriteria();
			criteria.setShowClaimStatusToDealer(isClaimStatusShownToDealer());
			claims = this.claimService.findAllClaimsMatchingQuery(
					Long.parseLong(this.domainPredicateId), criteria);
		} else {
			this.logger.error("domain Predicate Id is null ");
		}
		return claims;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		// following is not done in prepare method since params interceptor is
		// called after prepare
		// which resets the params.
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			this.savedQueryId = getServletRequest()
					.getAttribute("savedQueryId").toString();
		}
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			this.domainPredicateId = getServletRequest().getAttribute(
					"domainPredicateId").toString();
		}

		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimNumber", "claimNumber", 16, "string",
				"claimNumber", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		if (folderName != null && folderName.endsWith(ClaimFolderNames.SEARCH)
				&& inboxViewFields()) {
			addInboxViewFieldsToHeader(tableHeadData, LABEL_COLUMN_WIDTH);
		} else {
			if (isLoggedInUserAnInternalUser() || isClaimStatusShownToDealer())
				this.tableHeadData.add(new SummaryTableColumn(
						"label.inboxView.claimStatus", "enum:ClaimState:state",
						12, "string", "activeClaimAudit.state.state"));
			else
				this.tableHeadData.add(new SummaryTableColumn(
						"label.inboxView.claimStatus", "enum:ClaimState:state",
						12, "string", "activeClaimAudit.state.displayStatus",
						SummaryTableColumnOptions.NO_SORT));

			this.tableHeadData.add(new SummaryTableColumn(
					"label.claim.historicalClaimNumber", "histClmNo", 12,
					"string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.servProviderName", "forDealer.name", 12,
					"string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.model",
					"itemReference.unserializedItem.model.name", 12, "String",
					SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.failureCode",
					"activeClaimAudit.serviceInformation.faultCode", 12,
					"String"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.causalPart",
					"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber",
					12, "String", "causalPartBrandItemNumber"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.failureDate",
					"activeClaimAudit.failureDate", 12, "date"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.repairDate",
					"activeClaimAudit.repairDate", 12, "date"));
		}

		if (!isLoggedInUserAnInternalUser() && !isClaimStatusShownToDealer()) {
			List<SummaryTableColumn> headerData = new ArrayList<SummaryTableColumn>();
			for (SummaryTableColumn col : tableHeadData) {
				if (col.getId().equalsIgnoreCase("enum:ClaimState:state")) {
					col = new SummaryTableColumn("label.inboxView.claimStatus",
							"enum:ClaimState:state", 12, "string",
							"activeClaimAudit.state.displayStatus",
							SummaryTableColumnOptions.NO_SORT);
				}
				headerData.add(col);
			}
			tableHeadData = headerData;
		}
		return this.tableHeadData;
	}

	public void prepare() throws Exception {
		// for quick claim search
		if (StringUtils.hasText(claimNumber)) {
			this.claim = this.claimService.findClaimByNumber(claimNumber.trim()
					.toUpperCase());
		}
		// for claim details from define search query & pre-defined claim search
		else if (getId() != null) {
			if (getShowClaimAudit()) { // if request is Claim Audit history
										// opening
				this.claimAudit = this.claimService.findClaimAudit(Long
						.parseLong(getId()));
				if (this.claimAudit != null) {
					this.claim = this.claimAudit.getForClaim();
				}
			} else {
				this.claim = claimService
						.findClaimWithServiceInfoAttributes(Long.valueOf(id));
			}
		}
		if (this.claim != null) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.claim
					.getBusinessUnitInfo().getName());
		}
		
		// NMHGSLMS-425(State mandate changes) and deductible
		if (this.claim != null && claim.getServicingLocation() != null) {
			StateMandates stateMandate =null;
			String StateCode=claim.getServicingLocation().getState();
			CountryState state=null;
			if(StateCode!=null)
			{
				state = countryStateService.fetchState(StateCode,"US");
				if(state!=null)
					stateMandate = stateMandatesService.findActiveByName(state.getState());
			}
			if(stateMandate==null)
			{	
				Address customerAddress=null;
				if(claim.getClaimedItems().get(0).getItemReference().isSerialized()){
					customerAddress=claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOwnedBy().getAddress();				
					StateCode=customerAddress.getState();
					if(StateCode!=null)
					{
						state= countryStateService.fetchState(StateCode,"US");
						if(state!=null)
							stateMandate = stateMandatesService.findActiveByName(state.getState());
					}
				}else{
					customerAddress=claim.getOwnerInformation();
					StateCode=customerAddress.getState();
					if(StateCode!=null)
					{
						state = countryStateService.fetchState(StateCode,"US");
						if(state!=null)
							stateMandate = stateMandatesService.findActiveByName(state.getState());
					}
				}
			}
		
			claim.setStateMandate(stateMandate);
			
			//Deductible changes
			if(!claim.getState().equals(ClaimState.DRAFT))
    		{
			Money deductible=getDeductableAmount();
    		
			if(claim.getPayment()!=null)
				claim.getPayment().setDeductibleAmount(deductible);
    		}
			displayServicingLocationWithBrand(claim);	
		}
		// End
		
		//Incidental changes(NMHGSLMS-1308 Fix)
		if(claim!=null&&(claim.getItemDutyConfig()||claim.getMealsConfig()||claim.getPerDiemConfig()
    			||claim.getParkingConfig()||claim.getRentalChargesConfig()||claim.getLocalPurchaseConfig()
    			||claim.getTollsConfig()||claim.getOtherFreightDutyConfig()||claim.getOthersConfig() || claim.getHandlingFeeConfig() || claim.getTransportation())){
    		this.incidentalsAvaialable=true;
    	}
		
	}

	public String preview() throws ServletException, DocumentException,
			IOException {
		if (getId() != null) {
			if (this.logger.isInfoEnabled()) {
				this.logger.info("The claim id obtained to be viewed is: "
						+ getId());
			}
			populateBusinessUnitConfigParameters();
			setQtyForUIView(this.claim);
			setTotalLaborHoursForUiView(claim);
			setConfiguredCostCategories(claim);
			return SUCCESS;
		}
		return ERROR;
	}

	public String detail() throws ServletException, DocumentException,
			IOException {
		if (claim != null && this.claim.getLatestRecoveryClaim() != null
				&& this.claim.getRecoveryClaims().size() > 0) {
			User recoveryClaimAssignee = this.workListService
					.getCurrentAssigneeForRecClaim(this.claim
							.getLatestRecoveryClaim().getId());
			if (recoveryClaimAssignee != null
					&& recoveryClaimAssignee.getName() != null) {
				this.claim
						.getLatestRecoveryClaim()
						.setCurrentAssignee(
								getDisplayNameForRecoveryClaimAssignedTo(recoveryClaimAssignee));
			}
		}		
	
		
		if (getId() != null) {
			this.logger.info("The claim id obtained to be viewed is: "
					+ getId());
			/*
			 * this.claim.getServiceInformation().getFaultClaimAttributes();
			 * loadServiceInformationFaultClaimAttribute();
			 */
			populateBusinessUnitConfigParameters();
			setQtyForUIView(this.claim);
			setTotalLaborHoursForUiView(claim);
			setConfiguredCostCategories(claim);
			return SUCCESS;
		}
		return ERROR;
	}

	public boolean isIntiatiateRecoveryButtonToBeShown() {
		return !(claim.getCommercialPolicy() || claim.isPendingRecovery());
	}

	private String getDisplayNameForRecoveryClaimAssignedTo(
			User recoveryClaimAssignee) {
		StringBuffer displayName = new StringBuffer();
		displayName.append(recoveryClaimAssignee.getFirstName());
		displayName.append(" ");
		displayName.append(recoveryClaimAssignee.getLastName());
		displayName.append(" (");
		displayName.append(recoveryClaimAssignee.getName());
		displayName.append(')');
		return displayName.toString();
	}

	public String showDetailUsingClaimNumber() throws ServletException,
			DocumentException, IOException {
		if (claim != null && this.claim.getLatestRecoveryClaim() != null
				&& this.claim.getRecoveryClaims().size() > 0) {
			User recoveryClaimAssignee = this.workListService
					.getCurrentAssigneeForRecClaim(this.claim
							.getLatestRecoveryClaim().getId());
			if (recoveryClaimAssignee != null
					&& recoveryClaimAssignee.getName() != null) {
				this.claim
						.getLatestRecoveryClaim()
						.setCurrentAssignee(
								getDisplayNameForRecoveryClaimAssignedTo(recoveryClaimAssignee));
			}
		}
		if (StringUtils.hasText(claimNumber)) {
			this.logger.info("The claim id obtained to be viewed is: "
					+ getId());
			this.claim = this.claimService.findClaimByNumber(claimNumber.trim()
					.toUpperCase());

			if (this.claim == null) {
				addActionError("error.common.noResults");
				return ERROR;
			}
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.claim
					.getBusinessUnitInfo().getName());
			populateBusinessUnitConfigParameters();
			if (isLoggedInUserADealer()
					&& !this.claim
							.getForDealerShip()
							.getDealerNumber()
							.equals(getLoggedInUsersDealership()
									.getDealerNumber())) {
				if (!isClaimFiledByOrgsInLoggedInUsersOrgs()
						&& !claim.getForDealerShip().isThirdParty()) {
					addActionError("error.claim.notAuthorized");
					return ERROR;
				}
			}
			try {
				getAttributesForClaim(this.claim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setId(claim.getId().toString());
			setQtyForUIView(this.claim);
			setTotalLaborHoursForUiView(claim);
			setConfiguredCostCategories(claim);
			// preparing list for displaying reasons of putonhold, rejected and request info from dealer claims
    		if(null != claim.getRequestInfoFromUser() && claim.getRequestInfoFromUser().size()>0 && claim.getState().equals(ClaimState.FORWARDED)){
    			for(RequestInfoFromUser requestInfoFromUser : claim.getRequestInfoFromUser()){
    				reasonsList.add(requestInfoFromUser.getDescription());
    			}
    			claimState="label.common.reqInfoFromDealer";
    		}else if(null != claim.getPutOnHoldReasons() && claim.getPutOnHoldReasons().size()>0 && claim.getState().equals(ClaimState.ON_HOLD)){
    			for(PutOnHoldReason putOnHoldReason : claim.getPutOnHoldReasons()){
    				reasonsList.add(putOnHoldReason.getDescription());
    			}
    			claimState="label.common.putOnHold";
    		}	
    		
			return SUCCESS;
		}
		addActionError("error.common.emptyClaimNumber");
		return ERROR;
	}

	private boolean isClaimFiledByOrgsInLoggedInUsersOrgs() {
		for (Organization filedByOrg : this.claim.getFiledBy()
				.getBelongsToOrganizations())
			for (Organization liuOrg : getLoggedInUser()
					.getBelongsToOrganizations())
				if (filedByOrg.getId().longValue() == liuOrg.getId()
						.longValue())
					return true;
		return false;
	}

	public boolean isRootCauseAllowed() {
		return getConfigParamService().getBooleanValue(
				ConfigName.IS_ROOT_CAUSE_ALLOWED.getName());
	}

	public boolean isLaborSplitEnabled() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_LABOR_SPLIT.getName());
	}

	public String getLaborSplitOption() {
		return getConfigParamService().getStringValue(
				ConfigName.LABOR_SPLIT_DISTRIBUTION.getName());
	}

	public boolean checkLoggedInDealerOwner(Claim claim) {
		if (isLoggedInUserADealer()) {
			if (getLoggedInUsersDealership().getId().equals(
					claim.getForDealer().getId())) {
				return true;
			}
		}
		return false;

	}

	public String claimVersionDetail() throws IOException, DocumentException,
			ServletException {
		if (getId() != null) {
			if (logger.isInfoEnabled()) {
				this.logger.info("The claimAudit id obtained to be viewed is: "
						+ getId());
			}
			if (this.claimAudit != null) {
				populateBusinessUnitConfigParameters();
				setHussReplacedInstalledParts(this.claim);
				setQtyForUIView(this.claim);
				setTotalLaborHoursForAuditUiView(claim);
				/*
				 * This is for non-serialized Machine claims when opening claim
				 * audits
				 */
				if (!claim.getItemReference().isSerialized()
						&& claim.getItemReference().getModel() != null) {
					claim.getItemReference().setModel(
							itemGroupService.findById(claim.getItemReference()
									.getModel().getId()));
				}
				setConfiguredCostCategories(claim);
			} else {
				// populateBusinessUnitConfigParameters();
				addActionError("claim.audit.xml.not.found");
				return ERROR;
			}
			return SUCCESS;
		}
		return ERROR;
	}

	private void setHussReplacedInstalledParts(Claim claim) {
		if (isPartsReplacedInstalledSectionVisible()) {
			List<OEMPartReplaced> oemPartReplacedList = claim
					.getServiceInformation().getServiceDetail()
					.getOemPartsReplaced();
			if (oemPartReplacedList != null && !oemPartReplacedList.isEmpty()) {
				HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled = new HussmanPartsReplacedInstalled();
				hussmanPartsReplacedInstalled
						.setReplacedParts(oemPartReplacedList);
				List<InstalledParts> hussmanInstalledParts = new ArrayList<InstalledParts>(
						oemPartReplacedList.size());
				for (OEMPartReplaced oemPartReplaced : oemPartReplacedList) {
					InstalledParts installedPart = new InstalledParts();
					installedPart.setNumberOfUnits(oemPartReplaced
							.getNumberOfUnits());
					installedPart.setPricePerUnit(oemPartReplaced
							.getCostPricePerUnit());
					installedPart.setItem(oemPartReplaced.getItemReference()
							.getReferredItem());
					hussmanInstalledParts.add(installedPart);
				}
				hussmanPartsReplacedInstalled
						.setHussmanInstalledParts(hussmanInstalledParts);
				claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled()
						.add(hussmanPartsReplacedInstalled);
			}
		}
	}

	public String setupSearchView() {
		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;

	}

	public String getDomainPredicateId() {
		return this.domainPredicateId;
	}

	public void setDomainPredicateId(String domainPredicateId) {
		this.domainPredicateId = domainPredicateId;
	}

	public String getSavedQueryId() {
		return this.savedQueryId;
	}

	public void setSavedQueryId(String savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}

	public TaskView getTask() {
		return this.task;
	}

	public void setTask(TaskView task) {
		this.task = task;
	}

	public void setTaskViewService(TaskViewService taskViewService) {
		this.taskViewService = taskViewService;
	}

	public Claim getClaim() {
		return this.claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public ClaimAudit getClaimAudit() {
		return this.claimAudit;
	}

	public void setClaimAudit(ClaimAudit claimAudit) {
		this.claimAudit = claimAudit;
	}

	@SuppressWarnings("unchecked")
	public List<ClaimAudit> getClaimAudits() {
		if (getClaim() != null) {
			List result = new ArrayList(getClaim().getClaimAudits());
			Collections.reverse(result);
			return result;
		}
		return new ArrayList();
	}

	public String getJSONifiedAttachmentList() throws JSONException {
		try {
			List<Document> attachments = this.claim.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public String getJSONifiedRecoveryClaimAttachmentList()
			throws JSONException {
		try {
			List<RecoveryClaim> recClaim = this.claim.getRecoveryClaims();
			List<Document> attachments = new ArrayList<Document>();
			List<Document> dealerSharedAttachments = new ArrayList<Document>();

			if (this.claim.getRecoveryClaims() != null) {
				for (RecoveryClaim recoveryClaim : recClaim) {
					attachments.addAll(recoveryClaim.getAttachments());
					for (Document doc : attachments) {
						if (doc.getIsSharedWithDealer() == Boolean.TRUE
								&& isLoggedInUserADealer()) {
							dealerSharedAttachments.add(doc);
						}
					}
				}
			}
			if (isLoggedInUserADealer()) {
				return getDocumentListJSONForRecoveryClaim(
						dealerSharedAttachments).toString();
			} else {
				return getDocumentListJSONForRecoveryClaim(attachments)
						.toString();
			}
		} catch (Exception e) {
			return "[]";
		}
	}

	public int OEMPartsReplacedUpdatedCount(int totalQty) {
		int noOfClaimedItems = getApprovedClaimedItems(this.claim);
		int qtyToBeDisplayed = 0;
		if (!getLoggedInUser().getUserType().equals(
				AdminConstants.SUPPLIER_USER)
				&& this.orgService.isDealer(getLoggedInUser())
				&& !ClaimState.DRAFT.getState().equals(
						this.claim.getState().getState())) {
			qtyToBeDisplayed = totalQty / noOfClaimedItems;
		} else {
			qtyToBeDisplayed = totalQty;
		}
		return qtyToBeDisplayed;
	}

	public BigDecimal additionalLaborHoursUpdated(BigDecimal additionalLaborHrs) {
		BigDecimal noOfClaimedItems = new BigDecimal(
				getApprovedClaimedItems(this.claim));
		BigDecimal additionalLaborHrsDisplayed = BigDecimal.ZERO;
		if (isLoggedInUserADealer()
				&& !ClaimState.DRAFT.getState().equals(
						this.claim.getState().getState())) {
			additionalLaborHrsDisplayed = additionalLaborHrs
					.divide(noOfClaimedItems);
		} else {
			additionalLaborHrsDisplayed = additionalLaborHrs;
		}
		return additionalLaborHrsDisplayed;
	}

	public TaskViewService getTaskViewService() {
		return this.taskViewService;
	}

	public void setClaimXMLConverter(ClaimXMLConverter claimXMLConverter) {
		this.claimXMLConverter = claimXMLConverter;
	}

	/*
	 * Code Cleanup protected void convertXMLToObject(Object entity) {
	 * ClaimAudit claimAudit = (ClaimAudit) entity; if (claimAudit != null &&
	 * StringUtils.hasText(claimAudit.getPreviousClaimSnapshotAsString())) {
	 * unmarshalClaim(claimAudit); } }
	 * 
	 * private Claim unmarshalClaim(ClaimAudit claimAudit) { Claim claim =
	 * (Claim) this.claimXMLConverter.convertXMLToObject(claimAudit.
	 * getPreviousClaimSnapshotAsString());
	 * claim.setPayment(claimAudit.getPayment());
	 * claim.setState(claimAudit.getPreviousState());
	 * claimAudit.setPreviousClaimSnapshot(claim); return claim; }
	 */

	public boolean isEligibleForResubmission() {
		boolean canResubmit = false;
		Long daysAllowedForResubmission;
		// Refer SLMSPROD-73
		/*
		 * if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(this.claim.getType().
		 * getType())){ return false; }
		 */
		if (ClaimState.DENIED_AND_CLOSED.getState().equals(
				this.claim.getState().getState())) { // Denied Claim
														// Resubmission Flow
			daysAllowedForResubmission = getConfigParamService().getLongValue(
					ConfigName.DENIED_CLAIM_RESUBMISSION_WINDOW_PERIOD
							.getName());
			if (!Clock.today().isAfter(
					lastAcceptedOrDeniedDate().plusDays(
							new Integer((int) daysAllowedForResubmission
									.longValue())))) {
				canResubmit = true;
			}

		} else if (ClaimState.ACCEPTED_AND_CLOSED.getState().equals(
				this.claim.getState().getState())
				&& !claim.isClaimSubmittedByThirdParty()) {
			// Partially Accepted Claim flow
			LineItemGroup totalClaimAmountGrp = claim.getPayment()
					.getLineItemGroup(Section.TOTAL_CLAIM);
			Payment dealerPayment = claim.getPaymentForDealerAudit();
			Money dealerClaimedAmount = dealerPayment.getLineItemGroup(
					Section.TOTAL_CLAIM).getAcceptedTotal();
			Money latestClaimedAmount = totalClaimAmountGrp.getAcceptedTotal();
			// Allow re-submit if latest claim is less than dealer claimed
			// amount
			if (latestClaimedAmount.breachEncapsulationOfAmount().compareTo(
					dealerClaimedAmount.breachEncapsulationOfAmount()) == -1) {
				daysAllowedForResubmission = getConfigParamService()
						.getLongValue(
								ConfigName.PARTIAL_ACCEPTED_CLAIM_RESUBMISSION_WINDOW_PERIOD
										.getName());
				if (!Clock.today().isAfter(
						lastAcceptedOrDeniedDate().plusDays(
								new Integer((int) daysAllowedForResubmission
										.longValue())))) {
					canResubmit = true;
				}
			}
		}
		return canResubmit;
	}

	private CalendarDate lastAcceptedOrDeniedDate() {
		List<ClaimAudit> claimAudits = this.claim.getClaimAudits();
		int auditSize = claimAudits.size();
		ClaimAudit lastAudit = claimAudits.get(auditSize - 1);
		return TimePoint.from(lastAudit.getUpdatedTime()).calendarDate(
				TimeZone.getDefault());
	}

	public boolean isMaximumResubmissionExceeded() {
		Long maxResubmissionAllowed;
		boolean toReturn = false;
		maxResubmissionAllowed = getConfigParamService().getLongValue(
				ConfigName.MAXIMUM_RESUBMISSION_ALLOWED.getName());
		int noOfResubmissionsDone = 0;
		for (ClaimAudit audit : this.claim.getClaimAudits()) {
			if (ClaimState.APPEALED.getState().equals(
					audit.getPreviousState().getState())) {
				noOfResubmissionsDone++;
			}
		}
		if (noOfResubmissionsDone >= maxResubmissionAllowed) {
			toReturn = true;
		}
		return toReturn;
	}

	protected void setTotalLaborHoursForAuditUiView(Claim claim) {
		List<LaborDetail> LaborDetails = claim.getServiceInformation()
				.getServiceDetail().getLaborPerformed();
		for (LaborDetail labor : LaborDetails) {
			if (labor != null) {
				labor.setHoursSpentForMultiClaim(labor.getHoursSpent());
				if (ClaimType.CAMPAIGN.equals(claim.getType())) {
					if (labor.getSpecifiedHoursInCampaign() != null) {
						labor.setSpecifiedHoursInCampaign(labor
								.getSpecifiedHoursInCampaign()
								.divide(new BigDecimal(
										getApprovedClaimedItems(claim)), 4, 2));
					}
				}
				if (labor.getAdditionalLaborHours() != null) {
					if (getApprovedClaimedItems(claim) != 0) {
						labor.setAdditionalHoursSpentForMultiClaim(labor
								.getAdditionalLaborHours()
								.divide(new BigDecimal(
										getApprovedClaimedItems(claim)), 4, 2));
					} else {
						labor.setAdditionalHoursSpentForMultiClaim(new BigDecimal(
								0));
					}
				}
			}
		}
	}

	protected void setTotalLaborHoursForUiView(Claim claim) {
		List<LaborDetail> LaborDetails = claim.getServiceInformation()
				.getServiceDetail().getLaborPerformed();
		for (LaborDetail labor : LaborDetails) {
			if (labor != null) {
				labor.setHoursSpentForMultiClaim(labor.getHoursSpent());
				if (ClaimType.CAMPAIGN.equals(claim.getType())) {
					if (claim.getCampaign().getCampaignServiceDetail()
							.getCampaignLaborLimits().size() != 0) {
						if (!claim.getCampaign().getCampaignServiceDetail()
								.getCampaignLaborLimits().get(0)
								.isLaborStandardsUsed()) {
							if (labor.getSpecifiedHoursInCampaign() != null) {
								labor.setSpecifiedHoursInCampaign(labor
										.getSpecifiedHoursInCampaign()
										.divide(new BigDecimal(
												getApprovedClaimedItems(claim)),
												4, 2));
							}
						}
					}
				}
				if (labor.getAdditionalLaborHours() != null) {
					if (getApprovedClaimedItems(claim) != 0) {
						labor.setAdditionalHoursSpentForMultiClaim(labor
								.getAdditionalLaborHours()
								.divide(new BigDecimal(
										getApprovedClaimedItems(claim)), 4, 2));
					} else {
						labor.setAdditionalHoursSpentForMultiClaim(new BigDecimal(
								0));
					}
				}
			}
		}
	}

	public boolean isCommentViewable(ClaimState claimState) {
		List<ClaimState> notViewableClaimStates = new ArrayList<ClaimState>();
		notViewableClaimStates.add(PROCESSOR_REVIEW);
		if (notViewableClaimStates.contains(claimState)) {
			return false;
		}
		return true;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public LineItemGroup getLineItemGroupAuditForGlobalLevel(Claim claim) {
		if (claim.getPayment() != null
				&& !claim.getPayment().getLineItemGroups().isEmpty()) {
			LineItemGroup claimAmountGroup = claim.getPayment()
					.getLineItemGroup(Section.TOTAL_CLAIM);
			LineItemGroup claimedAmountAudit = claimAmountGroup;
			LineItemGroup displayAudit = new LineItemGroup();
			displayAudit.setBaseAmount(claim.getPayment()
					.getLineItemGroupsTotal());
			Money previousLevelAmount = displayAudit.getBaseAmount();
			for (LineItem modifier : claimedAmountAudit.getModifiers()) {
				LineItem newModifier = new LineItem();
				Money modifiedAmt;
				if (modifier.getIsFlatRate()) {
					modifiedAmt = Money.valueOf(
							modifier.getModifierPercentage(),
							claim.getCurrencyForCalculation());
				} else {
					modifiedAmt = previousLevelAmount.times(
							modifier.getModifierPercentage()).dividedBy(100);

				}
				newModifier.setValue(modifiedAmt);
				newModifier.setName(modifier.getName());
				newModifier.setModifierPercentage(modifier
						.getModifierPercentage());
				newModifier.setIsFlatRate(modifier.getIsFlatRate());
				displayAudit.getModifiers().add(newModifier);
				if (displayAudit.getModifierAmount() == null) {
					displayAudit.setModifierAmount(Money.valueOf(0,
							claim.getCurrencyForCalculation()));
				}
				displayAudit.setModifierAmount(displayAudit.getModifierAmount()
						.plus(modifiedAmt));
			}
			displayAudit.setGroupTotal();
			return displayAudit;
		}
		return null;
	}

	public boolean isLineItemPercentageChanged(Claim claim) {
		boolean isLineItemPercentageChanged = false;
		for (LineItemGroup lineItemGroup : claim.getPayment()
				.getLineItemGroups()) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& lineItemGroup.getPercentageAcceptance().longValue() != new Long(
							100).longValue()) {
				isLineItemPercentageChanged = true;
				break;
			}
		}
		return isLineItemPercentageChanged;
	}

	public String getClaimProcessedAsForDisplay(Claim claim) {
		if (!org.apache.commons.lang.StringUtils.isEmpty(claim.getPolicyCode())) {
			return claim.getPolicyCode();
		}
		if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim
				.getClaimProcessedAs())) {
			return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_STOCK.equals(claim
				.getClaimProcessedAs())) {
			return getText("label.common.itemInStock");
		} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim
				.getClaimProcessedAs())) {
			return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim
				.getClaimProcessedAs())) {
			return getText("label.common.outOfWarranty");
		}
		return claim.getClaimedItems().get(0).getApplicablePolicy().getCode();
	}
	
	public boolean isCPAdvisorEnabled() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_CP_ADVISOR.getName());
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public boolean isPartsClaim() {
		return InstanceOfUtil.isInstanceOfClass(PartsClaim.class, getClaim());
	}

	public boolean isStdLaborHrsToDisplay(Claim claim) {
		boolean toReturn = isEnableStandardLaborHours();
		if (ClaimState.DRAFT.getState().equalsIgnoreCase(
				claim.getState().getState())) {
			if (toReturn) {
				for (LaborDetail laborDetail : claim.getServiceInformation()
						.getServiceDetail().getLaborPerformed()) {
					laborDetail.setLaborHrsEntered(null);
				}
			}
			return toReturn;
		} else {
			return claim.getServiceInformation().getServiceDetail()
					.getStdLaborEnabled();
		}
	}

	public boolean isEnableStandardLaborHours() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_STANDARD_LABOR_HOURS.getName());
	}

	public boolean isDateCodeEnabled() {
		return getConfigParamService().getBooleanValue(
				ConfigName.IS_DATE_CODE_ENABLED.getName());
	}

	public void setConfiguredCostCategories(
			List<CostCategory> configuredCostCategories) {
		this.configuredCostCategories = configuredCostCategories;
	}

	public Boolean getShowClaimAudit() {
		return showClaimAudit;
	}

	public void setShowClaimAudit(Boolean showClaimAudit) {
		this.showClaimAudit = showClaimAudit;
	}

	public String getCostPriceType() {
		return getConfigParamService().getStringValue(
				ConfigName.COST_PRICE_CONFIGURATION.getName());
	}

	public String getJSONifiedCampaignAttachmentList() {
		try {
			if (this.claim.getCampaign() != null) {
				List<Document> attachments = this.claim.getCampaign()
						.getAttachments();
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

	public String getCurrentClaimAssignee() {
		if (this.claim == null || this.claim.getId() == null) {
			return "";
		}
		return getCurrentClaimAssignee(this.claim.getAssignToUser());
	}

	public String deactivateClaim() {
		if (ClaimType.CAMPAIGN.getType().equalsIgnoreCase(
				claim.getType().getType())) {
			campaignService.deleteDraftCampaignClaim(claim);
		}
		claimProcessService.stopClaimProcessing(claim);
		addActionMessage("message.claim.deactivated");
		return SUCCESS;
	}

	public void setWorkListService(WorkListService workListService) {
		this.workListService = workListService;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}

	public boolean isAllAuditsHistoryShownToDealer() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ALL_AUDITS_HISTORY_SHOWN_TO_DEALER.getName());
	}

	public boolean isClaimStatusShownToDealer() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ALL_CLAIM_STATUS_SHOWN_TO_DEALER.getName());
	}

	public boolean isClaimAssigneeShownToDealer() {
		return getConfigParamService().getBooleanValue(
				ConfigName.CLAIM_ASSIGNEE_SHOWN_TO_DEALER.getName());
	}

	public boolean isDefaultUserShownForAutoReplies() {
		return getConfigParamService().getBooleanValue(
				ConfigName.DEFAULT_USER_SHOWN_ON_AUTO_REPLIES.getName());
	}

	public String defaultProcessorForBU(String businessUnit) {
		User user = orgService.findDefaultUserBelongingToRoleForSelectedBU(
				businessUnit, "processor");
		return user.getName();
	}

	@Override
	public Claim getClaimDetail() {
		return this.claim;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public boolean isLOAProcessor() {
		if (this.claim.getLoaScheme() != null
				&& this.claim.getLoaScheme().getLoaLevels() != null) {
			for (LimitOfAuthorityLevel limitOfAuthorityLevel : this.claim
					.getLoaScheme().getLoaLevels()) {
				if (limitOfAuthorityLevel.getLoaUser().getName()
						.equalsIgnoreCase(getLoggedInUser().getName()))
					return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public boolean isAllRecoveryClaimsClosed() {
		List<RecoveryClaim> supplierRecoveryClaims = this.claim
				.getRecoveryClaims();
		for (RecoveryClaim recoveryClaim : supplierRecoveryClaims) {
			if (!recoveryClaim
					.getRecoveryClaimState()
					.getState()
					.toUpperCase()
					.contains(
							AdminConstants.RECOVERY_CLAIM_STATE_CLOSED
									.toUpperCase())
					|| recoveryClaim
							.getRecoveryClaimState()
							.getState()
							.equalsIgnoreCase(
									RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED
											.getState())
					|| recoveryClaim
							.getRecoveryClaimState()
							.getState()
							.equalsIgnoreCase(
									RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED
											.getState())
					|| recoveryClaim
							.getRecoveryClaimState()
							.getState()
							.equalsIgnoreCase(
									RecoveryClaimState.NOT_FOR_RECOVERY
											.getState())
					|| recoveryClaim
							.getRecoveryClaimState()
							.getState()
							.equalsIgnoreCase(
									RecoveryClaimState.NOT_FOR_RECOVERY_DISPUTED
											.getState())
					|| recoveryClaim
							.getRecoveryClaimState()
							.getState()
							.equalsIgnoreCase(
									RecoveryClaimState.REJECTED.getState())) {
				return false;
			}
		}
		return true;
	}

    public boolean isRecoveryClaimFlaggedFor2ndRecovery(){
        List<RecoveryClaim> listOfRecClaims = this.claim.getRecoveryClaims();
        for(RecoveryClaim recoveryClaim : listOfRecClaims){
            if(recoveryClaim.isFlagForAnotherRecovery()){
                return  true;
            }
        }
        return false;
    }

	public boolean displayInitiateRecoveryButton() {
		if (this.claim.getType().equals(ClaimType.CAMPAIGN)) {
			return false;
		}

        if(isRecoveryClaimFlaggedFor2ndRecovery()){
            return  true;
        }

		if (!isAllRecoveryClaimsClosed()) {
			return false;
		}
		List<RecoveryClaim> supplierRecoveryClaims = this.claim
				.getRecoveryClaims();
		if (supplierRecoveryClaims == null
				|| supplierRecoveryClaims.size() == 0) {
			return true;
		} else {
			if (supplierRecoveryClaims.get(0).getRecoveryClaimInfo()
					.isCausalPartRecovery()
					&& supplierRecoveryClaims.get(0).getRecoveryClaimInfo()
							.getContract().getCollateralDamageToBePaid()) {
				return false;
			}
			ClaimAudit activeClaimAudit = this.claim.getActiveClaimAudit();
			ClaimAudit claimAuditDuringSubmission = new ClaimAudit();
			for (ClaimAudit claimAudit : this.claim.getClaimAudits()) {
				if (claimAudit.getState().equals(ClaimState.SUBMITTED)) {
					claimAuditDuringSubmission = claimAudit;
				}
			}
			List<HussmanPartsReplacedInstalled> partsReplacedDuringSubmission = claimAuditDuringSubmission
					.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled();
			List<HussmanPartsReplacedInstalled> partsReplacedInActiveClaimAudit = activeClaimAudit
					.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled();
			if (partsReplacedInActiveClaimAudit != null
					&& partsReplacedDuringSubmission != null) {
				if (partsReplacedInActiveClaimAudit.size() > partsReplacedDuringSubmission
						.size()) {
					return true;
				}
				for (int index = 0; index < partsReplacedInActiveClaimAudit
						.size(); index++) {
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

	public boolean isDealerEligibleToFillSmrClaim() {
		boolean isEligible = false;
		Map<String, List<Object>> buValues = getConfigParamService()
				.getValuesForAllBUs(ConfigName.SMR_CLAIM_ALLOWED.getName());
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

	public boolean isTechnicianEnable() {
		boolean isEligible = false;
		Map<String, List<Object>> buValues = getConfigParamService()
				.getValuesForAllBUs(ConfigName.ENABLE_TECHNICIAN.getName());
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

	public boolean isInternalCommentsExists(RecoveryClaim recoveryClaim) {

		for (RecoveryClaimAudit recoveryClaimAudit : recoveryClaim.getRecoveryClaimAudits()) {
			if (StringUtils.hasText(recoveryClaimAudit.getComments())) {
				return true;
			}
		}
		return false;
	}
	
	public Money getDeductableAmount()
	{
		//To Get series details of applicable policy for which deductible amount should apply
		ItemReference itemReference=this.claim.getClaimedItems().get(0).getItemReference();		
		String seriesName=null;
		if(itemReference.isSerialized())
		{
			seriesName=itemReference.getUnserializedItem().getProduct().getName();
		}
		else
		{
			if(itemReference.getModel()==null)
				return null;
			seriesName=itemReference.getModel().getIsPartOf().getName();
		}

		//TO get deductible amount of series of applicable policy
		Currency baseCurrency = null;
		List<Money> acceptedTotal = new ArrayList<Money>();
		Money dealerPreferMoney=null;
		Money total=null;
		PolicyDefinition policyDefinition  = policyDefinitionRepository.findPolicyDefinitionByCode(claim.getPolicyCode());
		if(WarrantyType.POLICY.getType().equalsIgnoreCase(claim.getPolicyCode())||"Stock".equalsIgnoreCase(claim.getPolicyCode())||(policyDefinition!=null&&!policyDefinition.getWarrantyType().getType().equals(WarrantyType.EXTENDED.getType())))
		{
			dealerPreferMoney=null;
		}
		else
		{
			if(this.claim.getApplicablePolicy()!=null)
			{
				List <PolicyProductMapping>  policyForProducts= this.claim.getApplicablePolicy().getPolicyDefinition().getAvailability().getProducts();
				for(PolicyProductMapping policyProductMapping : policyForProducts){			
					if(policyProductMapping.getProduct().getName().equals(seriesName)&&policyProductMapping.getDeductibleFee()!=null)
					{
						acceptedTotal.add(policyProductMapping.getDeductibleFee());
						baseCurrency=policyProductMapping.getDeductibleFee().breachEncapsulationOfCurrency();
					}
				}
				if(!acceptedTotal.isEmpty())
				{
					if(!this.claim.getCurrencyForCalculation().equals(baseCurrency))
					{
						total=Money.sum(acceptedTotal);
						dealerPreferMoney = claimCurrencyConversionAdvice
								.convertMoneyFromBaseToNaturalCurrency(total,
										claim.getRepairDate(),
										claim.getCurrencyForCalculation());
					}
					else
					{
						dealerPreferMoney=Money.sum(acceptedTotal);
					}
				}
			}
			else
			{
				dealerPreferMoney=null;

			}
		}

		return dealerPreferMoney;
	}

	public RecoveryClaim getRecoveryClaim() {
		return recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}

	public StateMandatesService getStateMandatesService() {
		return stateMandatesService;
	}

	public void setStateMandatesService(StateMandatesService stateMandatesService) {
		this.stateMandatesService = stateMandatesService;
	}
	
	public int getDaysAfterPartShippedForNotification() {
		int daysAfterPartShippedForNotification = Integer.parseInt(getConfigParamService().getStringValue(
				ConfigName.DAYS_FOR_REOPEN_CLAIM_NOIFICATION_AFTER_PART_SHIPPED
						.getName()));
		return daysAfterPartShippedForNotification;
	}
	
	public boolean isRecoveryClaimAttachmentAvailable() {
		List<RecoveryClaim> recClaim = this.claim.getRecoveryClaims();
		List<Document> attachments = new ArrayList<Document>();
		if (this.claim.getRecoveryClaims() != null) {
			for (RecoveryClaim recoveryClaim : recClaim) {
				attachments.addAll(recoveryClaim.getAttachments());
				for (Document doc : attachments) {
					if (doc.getIsSharedWithDealer()
							&& recoveryClaim.getAttachments() != null) {
						return true;
					}

				}
			}
		}
		return false;
	}

	public List<String> displayJobCodeDescription() {
		List<String> jobCodeDescription = new ArrayList<String>();
		if (claim.getServiceInformation() != null
				&& claim.getServiceInformation().getServiceDetail() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getLaborPerformed().isEmpty())
			for (LaborDetail laborPerformed : claim.getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				jobCodeDescription.add(laborPerformed.getServiceProcedure()
						.getDefinedFor().getJobCodeDescription());
			}
		return jobCodeDescription;
	}
		
	public List<String> displayRejectioReasons() {
		List<String> reasonsListForDisplay = new ArrayList<String>();
		if (null != claim.getRejectionReasons()
				&& claim.getRejectionReasons().size() > 0
				&& (claim.getState().equals(ClaimState.DENIED) || claim
						.getState().equals(ClaimState.DENIED_AND_CLOSED))) {
			for (RejectionReason rejectionReason : claim.getRejectionReasons()) {
				reasonsListForDisplay.add(rejectionReason.getDescription());
			}
		}
		return reasonsListForDisplay;
	}
}

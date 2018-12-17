/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.web.search;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSONForRecoveryClaim;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.dom4j.DocumentException;
import org.json.JSONException;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.definition.PaymentSectionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.PageResult;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class RecoveryClaimSearchAction extends SummaryTableAction implements
		ServletRequestAware, Preparable {

	private static Logger logger = Logger
			.getLogger(RecoveryClaimSearchAction.class);

	RecoveryClaimService recoveryClaimService;

	ClaimService claimService;

	String domainPredicateId;

	String savedQueryId;

	private Claim claim;

	private String contextName;

	private HttpServletRequest httpRequest;

	private RecoveryClaim recoveryClaim;

	private CatalogService catalogService;

	private ConfigParamService configParamService;

	private PaymentSectionRepository paymentSectionRepository;

	private Map<String, List<Object>> configParamValueForAllBus;
	
	private LovRepository lovRepository;

	public RecoveryClaimSearchAction() {
		this.contextName = BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES;
	}

	public String getContextName() {
		return this.contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
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

	public void setServletRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpServletRequest getServletRequest() {
		return this.httpRequest;
	}

	public String preview() throws ServletException, DocumentException,
			IOException {
		if (getId() != null) {
			this.recoveryClaim = this.recoveryClaimService
					.findRecoveryClaim(Long.parseLong(getId()));
			this.claim = this.recoveryClaim.getClaim();
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(recoveryClaim
					.getBusinessUnitInfo().getName());
			return SUCCESS;
		}
		return ERROR;
	}

	public void prepare() throws Exception {
		if (this.id != null) {
			this.recoveryClaim = this.recoveryClaimService
					.findRecoveryClaim(Long.parseLong(this.id));
			populateCurrentClaimAssignee(this.recoveryClaim);
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(recoveryClaim
					.getBusinessUnitInfo().getName());
			this.claim = this.recoveryClaim.getClaim();
		}
	}

	public String detail() {
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		PageResult<?> obj = null;
		if (this.domainPredicateId != null
				&& !("".equals(this.domainPredicateId.trim()))) {
			obj = this.recoveryClaimService.findAllRecoveryClaimsMatchingQuery(
					Long.parseLong(this.domainPredicateId), getCriteria());
		}
		return obj;
	}

	public boolean isDisputeValid() {
		List<RecoveryClaimAudit> recoveryClaimAudits = getRecoveryClaim()
				.getRecoveryClaimAudits();
		int noOfDisputesDone = 0;
		boolean canDispute = true;
		Long maxDisputeAllowed = null;
		RecoveryClaimAudit recoveryClaimAudit = recoveryClaimAudits
				.get(recoveryClaimAudits.size() - 1);
		CalendarDate applicableDate = recoveryClaimAudit.getCreatedOn()
				.plusDays(
						getRecoveryClaim().getContract()
								.getSupplierDisputePeriod());
		// added by arindam for restricting the disputation number
		maxDisputeAllowed = this.configParamService
				.getLongValue(ConfigName.MAXIMUM_DISPUTATION_ALLOWED.getName());
		for (RecoveryClaimAudit recAudit : getRecoveryClaim()
				.getRecoveryClaimAudits()) {
			if (RecoveryClaimState.REJECTED.getState().equals(
					recAudit.getRecoveryClaimState().getState())) {
				noOfDisputesDone++;
			}
		}
		if (noOfDisputesDone >= maxDisputeAllowed) {
			canDispute = false;
		}
		return (Clock.today().compareTo(applicableDate) == -1) && canDispute;
	}

	public String getAcceptanceReason() {
		RecoveryClaim recClaim = getRecoveryClaim();
		if (recClaim.getRecoveryClaimState() != null
				&& recClaim.getRecoveryClaimState().getState()
						.contains("Closed")
				&& !RecoveryClaimState.CLOSED_UNRECOVERED.equals(recClaim
						.getRecoveryClaimState().getState()))
			return recClaim.getRecoveryClaimAcceptanceReason().getDescription();
		else
			return null;
	}

	public String getRejectionReason() {
		RecoveryClaim recClaim = getRecoveryClaim();
		if (recClaim.getRecoveryClaimState() != null
				&& RecoveryClaimState.REJECTED.equals(recClaim
						.getRecoveryClaimState().getState())
				&& RecoveryClaimState.CLOSED_UNRECOVERED.equals(recClaim
						.getRecoveryClaimState().getState()))
			return recClaim.getRecoveryClaimAcceptanceReason().getDescription();
		else
			return null;
	}

	public Money getCostForSection(String costType, String sectionName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting sum of [" + costType
					+ "] costs for section [" + sectionName + "]");
		}
		List<Money> amounts = new ArrayList<Money>();
		Money m = null;
		if ("actualCost".equals(costType)) {
			m = this.recoveryClaim.getAcutalCostForSection(sectionName);
		} else if ("costAfterApplyingContract".equals(costType)) {
			m = this.recoveryClaim
					.getCostAfterApplyingContractForSection(sectionName);
		} else if ("recoveredCost".equals(costType)) {
			m = this.recoveryClaim.getRecoveredCostForSection(sectionName);
		} else if ("supplierCost".equals(costType)) {
			m = this.recoveryClaim.getSupplierCostForSection(sectionName);
		} else {
			throw new RuntimeException("Cost type [" + costType
					+ "] is invalid");
		}
		if (m != null) {
			amounts.add(m);
		}

		Money sum = Money.sum(amounts);
		if (logger.isDebugEnabled()) {
			logger.debug("Sum is [" + sum + "]");
		}
		return sum;
	}

	public Money getTotalCostForSection(String costType) {
		List<Section> sections = getAllSections();
		List<Money> amounts = new ArrayList<Money>();
		for (Section s : sections) {
			if (!s.getName().equals(Section.TOTAL_CLAIM)) {
				amounts.add(getCostForSection(costType, s.getName()));
			}
		}
		return Money.sum(amounts);
	}

	public Money getAcceptedCostForSection(String costType, String currencyCode) {

		List<RecoveryClaimAudit> recClaimAudit = recoveryClaim
				.getRecoveryClaimAudits();
		Money acceptedAmount = null;
		if (getRecoveryClaim().getRecoveryClaimState().equals(
				RecoveryClaimState.REOPENED)) {
			acceptedAmount = recClaimAudit.get(recClaimAudit.size() - 2)
					.getAcceptedAmount();
		} else {
			acceptedAmount = recClaimAudit.get(recClaimAudit.size() - 1)
					.getAcceptedAmount();
		}

		if (acceptedAmount == null) {
			acceptedAmount = getTotalCostForSection(costType, currencyCode);
		}
		return acceptedAmount;
	}

	public Money getCostForSection(String costType, String sectionName,
			String currencyCode) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting sum of [" + costType
					+ "] costs for section [" + sectionName + "]");
		}
		List<Money> amounts = new ArrayList<Money>();
		Money m = null;
		if ("actualCost".equals(costType)) {
			m = this.recoveryClaim.getAcutalCostForSection(sectionName);
		} else if ("costAfterApplyingContract".equals(costType)) {
			m = this.recoveryClaim
					.getCostAfterApplyingContractForSection(sectionName);
		} else if ("recoveredCost".equals(costType)) {
			m = this.recoveryClaim.getRecoveredCostForSection(sectionName);
		} else if ("supplierCost".equals(costType)) {
			m = this.recoveryClaim.getSupplierCostForSection(sectionName);
		} else {
			throw new RuntimeException("Cost type [" + costType
					+ "] is invalid");
		}
		if (m != null) {
			amounts.add(m);
		} else {
			m = Money.valueOf(0.0, Currency.getInstance(currencyCode));
			amounts.add(m);
		}

		Money sum = Money.sum(amounts);
		if (logger.isDebugEnabled()) {
			logger.debug("Sum is [" + sum + "]");
		}
		return sum;
	}

	public Money getTotalCostForSection(String costType, String currencyCode) {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem cli : recoveryClaim.getCostLineItems()) {
			if (cli.getSection().getName() != null
					&& !Section.TOTAL_CLAIM.equals(cli.getSection().getName())) {
				amounts.add(getCostForSection(costType, cli.getSection()
						.getName(), currencyCode));
			}
		}
		return Money.sum(amounts);
	}

	public List<Section> getAllSections() {
		return this.paymentSectionRepository.getSections();
	}

	public Money getTotalCostForClaim(Long id, String costType) {
		this.recoveryClaim = this.recoveryClaimService.findRecoveryClaim(id);
		if ("recoveredCost".equals(costType)) {
			return this.recoveryClaim.getTotalRecoveredCost();
		} else if ("costAfterApplyingContract".equals(costType)) {
			return this.recoveryClaim.getTotalCostAfterApplyingContract();
		} else if ("actualCost".equals(costType)) {
			return this.recoveryClaim.getTotalActualCost();
		} else if ("supplierCost".equals(costType)) {
			return this.recoveryClaim.getTotalSupplierCost();
		} else {
			throw new RuntimeException("Cost type [" + costType
					+ "] is invalid");
		}
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			this.savedQueryId = getServletRequest()
					.getAttribute("savedQueryId").toString();
		}
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			this.domainPredicateId = getServletRequest().getAttribute(
					"domainPredicateId").toString();
		}
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.common.Hidden", "id",
				20, true, true, true, false));
		header.add(new SummaryTableColumn("columnTitle.common.recClaimNo",
				"recoveryClaimNumber", 15, "string", "recoveryClaimNumber",
				true, false, false, false));

		header.add(new SummaryTableColumn("columnTitle.common.status",
				"recoveryClaimState.state", 15, "string",
				SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		if (getLoggedInUser().isInternalUser()) {
			header.add(new SummaryTableColumn(
					"columnTitle.listContracts.supplier_name",
					"contract.supplier.name", 20, "string"));
			header.add(new SummaryTableColumn(
					"columnTitle.supplier.supplierNumber",
					"contract.supplier.supplierNumber", 10, "string"));
		}
		header.add(new SummaryTableColumn(
				"columnTitle.supplier.recoveryAmount",
				"acceptedCostForSection", 15, "Money",
				SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		header.add(new SummaryTableColumn(
				"label.inboxView.claimPartReturnStatus", "partReturnStatus",
				15, "String", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		header.add(new SummaryTableColumn("columnTitle.newClaim.createdOn",
				"d.createdOn", 10, "date"));

		if (this.isExportAction()) {
			header.add(new SummaryTableColumn(
					"label.section.replacedPartsCost",
					"getCostLineItem('Oem Parts').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn(
					"label.section.nonReplacedPartsCost",
					"getCostLineItem('Non Oem Parts').recoveredCost", 10,
					"string"));
			header.add(new SummaryTableColumn("label.recovery.excel.miscTotal",
					"getCostLineItem('Miscellaneous Parts').recoveredCost", 10,
					"string"));
			header.add(new SummaryTableColumn(
					"label.recovery.excel.LaborTotal",
					"getCostLineItem('Labor').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn(
					"label.recovery.excel.incidentalTotal",
					"getCostLineItem('Non Oem Parts').recoveredCost", 10,
					"string"));
			header.add(new SummaryTableColumn(
					"label.recovery.excel.incidentalTotal", "incidentalCost",
					10, "string"));
			header.add(new SummaryTableColumn(
					"label.recovery.excel.TravelTotal", "travelCost", 10,
					"string"));
		}
		return header;
	}

	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization organization) {
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, organization,
				loggedInUserOrganization, Supplier.class);
	}

	public BigDecimal additionalLaborHoursUpdated(BigDecimal additionalLaborHrs) {
		BigDecimal noOfClaimedItems = new BigDecimal(
				getApprovedClaimedItems(getClaim()));
		BigDecimal additionalLaborHrsDisplayed = BigDecimal.ZERO;
		if (isLoggedInUserADealer()
				&& !ClaimState.DRAFT.getState().equals(
						getClaim().getState().getState())) {
			additionalLaborHrsDisplayed = additionalLaborHrs
					.divide(noOfClaimedItems);
		} else {
			additionalLaborHrsDisplayed = additionalLaborHrs;
		}
		return additionalLaborHrsDisplayed;
	}

	private int getApprovedClaimedItems(Claim claim) {
		int approvedClaimedItems = 0;
		List<ClaimedItem> claimedItems = claim.getClaimedItems();
		for (ClaimedItem claimedItem : claimedItems) {
			if (claimedItem.isProcessorApproved()) {
				approvedClaimedItems++;
			}
		}
		return approvedClaimedItems;
	}

	public ClaimService getClaimService() {
		return this.claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public RecoveryClaim getRecoveryClaim() {
		return this.recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaimSummary) {
		this.recoveryClaim = recoveryClaimSummary;
	}

	public Claim getClaim() {
		return this.claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
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

	public PaymentSectionRepository getPaymentSectionRepository() {
		return paymentSectionRepository;
	}

	public void setPaymentSectionRepository(
			PaymentSectionRepository paymentSectionRepository) {
		this.paymentSectionRepository = paymentSectionRepository;
	}

	private void populateCurrentClaimAssignee(RecoveryClaim recoveryClaim) {
		User claimAssignee = this.workListService
				.getCurrentAssigneeForRecClaim(recoveryClaim.getId());
		String firstName = null;
		String lastName = null;
		String login = null;
		StringBuffer assignee = new StringBuffer();

		if (claimAssignee == null) {
			recoveryClaim.setCurrentAssignee("");
		}
		if (claimAssignee != null) {
			firstName = claimAssignee.getFirstName();
			lastName = claimAssignee.getLastName();

			if (login == null) {
				login = claimAssignee.getName();
			}
		}
		// Moved the code under single condition
		assignee.append(firstName == null ? "" : firstName);
		assignee.append(" ");
		assignee.append(lastName == null ? "" : lastName);

		if (!StringUtils.isEmpty(login)) {
			assignee.append(" (");
			assignee.append(login);
			assignee.append(")");
		}
		recoveryClaim.setCurrentAssignee(assignee.toString());
	}

	public String getCostPriceType() {
		return this.configParamService
				.getStringValue(ConfigName.COST_PRICE_CONFIGURATION.getName());
	}

	public boolean isPartsReplacedInstalledSectionVisible() {
		return configParamService
				.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
						.getName());
	}

	public boolean buPartReplaceableByNonBUPart() {
		return configParamService
				.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART
						.getName());
	}

	public boolean isClaimCampaign() {
		return ClaimType.CAMPAIGN.equals(claim.getType());
	}

	public Map<String, List<Object>> getConfigParamValueForAllBus() {
		return configParamValueForAllBus;
	}

	public void setConfigParamValueForAllBus(
			Map<String, List<Object>> configParamValueForAllBus) {
		this.configParamValueForAllBus = configParamValueForAllBus;
	}
	
	public boolean isTaskNameofCurrentRecoveryClaim() {
		if (this.contextName == BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES) {
			return true;
		}
		return false;
	}
	
	public String getJSONifiedRecoveryClaimAttachmentList() throws JSONException {
		try {
			List<Document> attachments = this.getRecoveryClaim().getAttachments();
			List<Document> supplierSharedAttachments =new ArrayList<Document>();
			List<Document> dealerSharedAttachments =new ArrayList<Document>();
			for(Document doc: attachments){								
				if(doc.getIsSharedWithSupplier() && getLoggedInUser().hasRole("supplier")){
					supplierSharedAttachments.add(doc);
				} 
				
				if(doc.getIsSharedWithDealer()==Boolean.TRUE && isLoggedInUserADealer()){
					dealerSharedAttachments.add(doc);
				} 
			}
			if (getLoggedInUser().hasRole("supplier")) {
				return getDocumentListJSONForRecoveryClaim(supplierSharedAttachments).toString();
			} else if (isLoggedInUserADealer()) {
				return getDocumentListJSONForRecoveryClaim(dealerSharedAttachments).toString();
			} else {
				return getDocumentListJSONForRecoveryClaim(attachments).toString();
			}			
		} catch (Exception e) {
			return "[]";
		}
	}

	
	public String getJSONifiedClaimAttachmentList() {
		try {			
			List<Document> attachments = this.getRecoveryClaim().getClaim()
					.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
				return getDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public List<ListOfValues> getLovsForClass(String className,
			RecoveryClaim recClaim) {
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();
		if (this.lovRepository == null) {
			initDomainRepository();
		}
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(recClaim
				.getBusinessUnitInfo().getName());
		return this.lovRepository.findAllActive(className);
	}
	
	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.lovRepository = (LovRepository) beanLocator
				.lookupBean("lovRepository");
	}
	
	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}


	public String getTaskName() {
		return null;
	}

	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization organization, String brand) {
		if (brand != null && !brand.isEmpty() && isNumber) {
			return item.getBrandItemNumber(brand);
		}
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, organization,
				loggedInUserOrganization, Supplier.class);
	}

	public boolean isCommentsExist(Claim claim) {
		for (ClaimAudit claimAudit : claim.getClaimAudits()) {

			if (!StringUtils.isEmpty(claimAudit.getInternalComments())  && !claimAudit.getUpdatedBy().hasRole(Role.SYSTEM)) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> displayJobCodeDescription() {
		List<String> jobCodeDescription = new ArrayList<String>();
		if (this.getRecoveryClaim().getClaim().getServiceInformation() != null
				&& this.getRecoveryClaim().getClaim().getServiceInformation().getServiceDetail() != null
				&& !this.getRecoveryClaim().getClaim().getServiceInformation().getServiceDetail()
						.getLaborPerformed().isEmpty())
			for (LaborDetail laborPerformed : this.getRecoveryClaim().getClaim().getServiceInformation()
					.getServiceDetail().getLaborPerformed()) {
				jobCodeDescription.add(laborPerformed.getServiceProcedure()
						.getDefinedFor().getJobCodeDescription());
			}
		return jobCodeDescription;
	}
	
	public boolean isShowPartSerialNumber() {
		return this.configParamService.getBooleanValue(
							ConfigName.SHOW_PART_SN_ON_INSTALLED_REMOVED_SECTION
									.getName());
	}
}

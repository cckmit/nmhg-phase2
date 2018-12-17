/**
 *
 */
package tavant.twms.web.supplier;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSONForRecoveryClaim;

import java.math.BigDecimal;
import java.util.*;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.definition.PaymentSectionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.partsreturn.OEMPartReplacedBean;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.domain.orgmodel.User;
import com.domainlanguage.money.Money;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

/**
 * @author kannan.ekanath
 * 
 */
public abstract class AbstractSupplierActionSupport extends SummaryTableAction {

	private static Logger logger = Logger
			.getLogger(AbstractSupplierActionSupport.class);

    private final String CANADA_COUNTRY_CODE="CA";

	// This field will be injected from webapp-context, a definition of fields
	private Map partReturnFields;

    protected String comments;

	private String actionUrl;

	private String taskName;

	private String switchButtonActionName;

	private String switchButtonTabLabel;

	private String inboxViewType = null;

	private List<OEMPartReplaced> shipOEMParts = new ArrayList<OEMPartReplaced>();

	private Contract contract;

	private OEMPartReplaced oemPartReplaced;

	private PartReplacedService partReplacedService;

	protected PartReturnProcessingService partReturnProcessingService;

	private PartReturnService partReturnService;

	protected RecoveryClaimService recoveryClaimService;

	private PaymentSectionRepository paymentSectionRepository;

	private List<List<Contract>> contractList = new ArrayList<List<Contract>>();

	private List<Long> partsRecoverable = new ArrayList<Long>();

	private CarrierRepository carrierRepository;

	private Claim claim;

	protected RecoveryClaim recoveryClaim;

	private ContractService contractService;
	
	private String trackingNumber;

	private String carrierId;

	private User transferToUser;

	private List<Location> selectedLocations = new ArrayList<Location>();

	private List<Carrier> selectedCarriers = new ArrayList<Carrier>();

	private List<String> rgaNumbersProvided = new ArrayList<String>();

	private CatalogService catalogService;

	protected ConfigParamService configParamService;

	// Used by javascript operations
	private Integer oemPartIndex;

	protected LovRepository lovRepository;

	protected RecoveryClaim fetchedRecoveryClaim;

	private Map<String, List<Object>> configParamValueForAllBus;

	private Boolean anyValidationError = Boolean.FALSE;

    private List<Location> oemReturnLocations = new ArrayList<Location>();

    private List<Location> supplierReturnLocations = new ArrayList<Location>();

    protected SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

    private List<RecoverablePartsBean> recoverablePartsBeans = new ArrayList<RecoverablePartsBean>();

    private List<RecoverablePartsBean> uiRecoverablePartsBeans = new ArrayList<RecoverablePartsBean>();
    
    protected Shipment shipment;

	/**
	 * How to get the inbox item list?
	 */
	protected abstract InboxItemList getInboxItemList(WorkListCriteria criteria);

	protected final String DEFAULT_VIEW_ID = "-1";

	protected abstract PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages);

	private boolean showPartSerialNumber;
	
	private boolean partReturnRequested = false;
	
	/*private Shipment supplierShipment;

	public Shipment getSupplierShipment() {
		return supplierShipment;
	}

	public void setSupplierShipment(Shipment supplierShipment) {
		this.supplierShipment = supplierShipment;
	}
*/
	public boolean isShowPartSerialNumber() {
		return configParamService
				.getBooleanValue(ConfigName.SHOW_PART_SN_ON_INSTALLED_REMOVED_SECTION
						.getName());
	}

	public boolean isEnableStandardLaborHours() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_STANDARD_LABOR_HOURS.getName());
	}

	public boolean isStdLaborHrsToDisplay(Claim claim) {
		return claim.getServiceInformation().getServiceDetail()
				.getStdLaborEnabled();
	}

    protected String transitionTaken;

    public static final String MARK_FOR_INSPECTION = "Mark for Inspection";

    public static final String MARK_NOT_RECEIVED = "Mark not received";

    public static final String ACCEPT = "Accept";

    public static final String REJECT = "Reject";

	protected String getInboxViewContext() {
		return BusinessObjectModelFactory.SUPPLIER_RECOVERY_FOLDERS;
	}

	@Override
	protected PageResult<?> getBody() {
		WorkListCriteria criteria = createCriteria();
		InboxItemList inboxItemList = getInboxItemList(criteria);
		List inboxItems = inboxItemList.getInboxItems();
		inboxItems = removeDuplicates(inboxItems);
		PageSpecification pageSpecification = criteria.getPageSpecification();
		int noOfPages = criteria.getPageSpecification().convertRowsToPages(
				inboxItemList.getTotalCount());
		return getPageResult(inboxItems, pageSpecification, noOfPages);
	}

	private List<?> removeDuplicates(List<?> inboxItems) {
		Set<Object> newSet = new LinkedHashSet(inboxItems);
		return new LinkedList(newSet);
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("id", "id", 0, "string", "id",
				false, true, true, false));
		incrementDefaultHeaderSize();

		// CR TKTSA-923
		if (inboxViewFields()) {
			tableHeadData
					.add(new SummaryTableColumn(
							"columnTitle.common.recClaimNo",
							"recoveryClaimNumber", 15, "string",
							"recoveryClaimNumber", true, false, false, false));
			addInboxViewFieldsToHeader(tableHeadData, LABEL_COLUMN_WIDTH);
		} else {

			Assert.state(this.partReturnFields != null,
					"Column definitions are not being configured");
			Assert.hasText(this.taskName,
					"Task name hasn't been set for getting column definitions");
			Assert.state(this.partReturnFields.containsKey(this.taskName
					+ (inboxViewType == null ? "" : "_" + inboxViewType)),
					"The configured column definitions ["
							+ this.partReturnFields.keySet()
							+ "] doesnt have a key for [" + this.taskName + "]");
			tableHeadData
					.addAll((Collection<? extends SummaryTableColumn>) this.partReturnFields
							.get(this.taskName
									+ (inboxViewType == null ? "" : "_"
											+ inboxViewType)));
		}
		int colCountLimit = "Supplier Accepted".equalsIgnoreCase(this.taskName) ? 8
				: 8;
		
		colCountLimit = WorkflowConstants.SUPPLIER_PARTS_RECEIPT.equalsIgnoreCase(this.taskName) ? 9 : colCountLimit; 

		if (isExportAction())
			return tableHeadData;
		List<SummaryTableColumn> tableHeadDataUI = new ArrayList<SummaryTableColumn>();
		int colCount = 0;
		for (SummaryTableColumn col : tableHeadData) {
			tableHeadDataUI.add(col);
			if (!col.isHidden())
				colCount++;
			if (colCount == colCountLimit)
				break;
		}
		return tableHeadDataUI;
	}

	private WorkListCriteria createCriteria() {
		WorkListCriteria criteria = new WorkListCriteria(getLoggedInUser());

		// Copy the ListCriteria to WorkListCriteria
		// TODO find a better way for this
		ListCriteria listCriteria = super.getCriteria();
		Map<String, String> filterCriteria = listCriteria.getFilterCriteria();
		for (String columnName : filterCriteria.keySet()) {
			criteria.addFilterCriteria(columnName,
					filterCriteria.get(columnName));
		}
		Map<String, String> sortCriteria = listCriteria.getSortCriteria();
		for (String columnName : sortCriteria.keySet()) {
			criteria.addSortCriteria(columnName, sortCriteria.get(columnName)
					.equals("asc") ? true : false);
		}
		criteria.setPageSpecification(listCriteria.getPageSpecification());
		if (inboxViewType == null) {
			this.folderName = getFolderName().equals("") ? this.taskName
					: getFolderName();
			criteria.setTaskName(getFolderName());
		} else
			criteria.setTaskName(this.taskName);

		if (logger.isInfoEnabled()) {
			logger.info("Folder Name : " + getFolderName());
		}
		return criteria;
	}

	public void updatePayment() {
		RecoveryClaim recClaim = getRecoveryClaim();
		this.recoveryClaimService.updatePayment(recClaim);
	}

	public void fetchSuppliers() {
		Assert.notNull(this.claim,
				"The claim should be fetched before initialising suppliers");
		List<OEMPartReplaced> parts = this.claim.getServiceInformation()
				.getServiceDetail().getOEMPartsReplaced();
		for (OEMPartReplaced part : parts) {
			// Show the part only if it has an associated Task in this flow
			if (this.partsRecoverable.contains(part.getId())) {
				Item item = part.getItemReference().getUnserializedItem();
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
						.getBusinessUnitInfo().getName());
				List<Contract> contracts = this.contractService.findContract(
						this.claim, item, true);
				if (logger.isDebugEnabled()) {
					logger.debug("Found contracts" + contracts + " for item ["
							+ item + "]");
				}
				this.contractList.add(contracts);
			} else {
				// just add dummy list to make the UI happy
				this.contractList.add(new ArrayList<Contract>());
			}
		}
	}
	
	public void startRecoveryRoutingFlow() {
		List<RecoverablePart> recoverableParts = this.recoveryClaim
				.getRecoveryClaimInfo().getRecoverableParts();
		for (int i = 0; i < recoverableParts.size(); i++) {
			if (recoverableParts.get(i).isSupplierReturnNeeded()) {
				if (this.getSupplierReturnLocations().size() > i || this.getSelectedLocations().size() > i) {
					String rgaNumber = null;
					if (this.getRgaNumbersProvided().size() > i)
						rgaNumber = this.getRgaNumbersProvided().get(i);
                    if(this.getSupplierReturnLocations().size() > i){
                        this.contractService.updateSupplierPartReturn(
                                recoverableParts.get(i), this
                                        .getSupplierReturnLocations().get(i), this
                                        .getSelectedCarriers().get(i), rgaNumber);
                    }else if(this.getSelectedLocations().size() > i) {
                        this.contractService.updateSupplierPartReturn(
                                recoverableParts.get(i), this
                                .getSelectedLocations().get(i), this
                                .getSelectedCarriers().get(i), rgaNumber);
                    }
					this.partReturnProcessingService
							.startRecoveryRoutingProcess(
									recoverableParts.get(i), this.recoveryClaim);
				}
			}
		}
	}

    public PartReturnService getPartReturnService() {
        return partReturnService;
    }

    public PartReplacedService getPartReplacedService() {
        return partReplacedService;
    }

    public void startSupplyPartReturnForClaim() {
		List<RecoverablePart> recoverableParts = this.recoveryClaim
				.getRecoveryClaimInfo().getRecoverableParts();
		for (int i = 0; i < recoverableParts.size(); i++) {
			if (recoverableParts.get(i).isSupplierReturnNeeded()) {
				if (this.getSelectedLocations().size() > i)// means that the row
															// on the GUI was
															// not disabled
				{
					partReturnRequested = true;
					String rgaNumber = null;
					if (this.getRgaNumbersProvided().size() > i)
						rgaNumber = this.getRgaNumbersProvided().get(i);
					this.contractService.updateSupplierPartReturn(
							recoverableParts.get(i), this
									.getSelectedLocations().get(i), this
									.getSelectedCarriers().get(i), rgaNumber);
					this.partReturnProcessingService
							.startRecoveryPartReturnProcess(
									recoverableParts.get(i), this.recoveryClaim);
				}
			} else {// this part of the code is yet to be tested.
				if (!recoverableParts.get(i).getOemPart().getPartReturns()
						.isEmpty()) {
					if (!this.partReturnService.isPartShipped(recoverableParts
							.get(i).getOemPart())
							&& recoverableParts.get(i).getOemPart()
									.isPartReturnInitiatedBySupplier()) {
						this.partReturnProcessingService
								.endRecoveryPartReturnProcess(recoverableParts
										.get(i).getOemPart());
					} else if (this.partReturnService
							.isPartShipped(recoverableParts.get(i).getOemPart())
							&& !recoverableParts.get(i).getOemPart()
									.isPartReturnInitiatedBySupplier()) {
						this.partReturnProcessingService
								.endPartReturnNotIntiatedBySupplier(this.recoveryClaim);
					}
				} else {
					this.partReturnProcessingService
							.endRecoveryPartReturnProcess(recoverableParts.get(
									i).getOemPart());
				}
			}
		}

	}

	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization organization) {
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, organization,
				loggedInUserOrganization, Supplier.class);
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

	private void fetchPartsRecoverable() {
		Assert.notNull(this.claim,
				"The claim should be fetched before initialising suppliers");
		List<OEMPartReplaced> parts = this.claim.getServiceInformation()
				.getServiceDetail().getOEMPartsReplaced();
		for (OEMPartReplaced part : parts) {

			this.partsRecoverable.add(part.getId());
			// }
		}
	}

    public boolean isRecoveryClaimReopened(RecoveryClaim recoveryClaim){
        for(RecoveryClaimAudit audit : recoveryClaim.getRecoveryClaimAudits()){
            if(audit.getRecoveryClaimState().equals(RecoveryClaimState.REOPENED)){
                return true;
            }
        }
        return false;
    }

	public List<ListOfValues> getLovsForClass(String className,
			RecoveryClaim recClaim) {
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();
		if (this.lovRepository == null) {
			initDomainRepository();
		}
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(recClaim
				.getBusinessUnitInfo().getName());
        lovs = this.lovRepository.findAllActive(className);
        List<ListOfValues> lovsExceptCannotRecover = new ArrayList<ListOfValues>();
        if(className.equalsIgnoreCase("RecoveryClaimCannotRecoverReason") && !this.getTaskName().equalsIgnoreCase(WorkflowConstants.SRA_REVIEW)){
            for(ListOfValues value : lovs){
                if(!value.getCode().equalsIgnoreCase(Constants.FLAG_FOR_2ND_RECOVERY)){
                    lovsExceptCannotRecover.add(value);
                }
            }
            return lovsExceptCannotRecover;
        }
        else if(className.equalsIgnoreCase("RecoveryClaimCannotRecoverReason") && this.getTaskName().equalsIgnoreCase(WorkflowConstants.SRA_REVIEW) && isRecoveryClaimReopened(recClaim)){
            for(ListOfValues value : lovs){
                if(!value.getCode().equalsIgnoreCase(Constants.FLAG_FOR_2ND_RECOVERY)){
                    lovsExceptCannotRecover.add(value);
                }
            }
            return lovsExceptCannotRecover;
        }
        return lovs;
	}

	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.lovRepository = (LovRepository) beanLocator
				.lookupBean("lovRepository");
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

	public List<Location> getLocations() {
		List<Location> locations = new ArrayList<Location>();
		Location location = recoveryClaim.getContract().getLocation();
		Collection<Location> tempLocations = recoveryClaim.getContract()
				.getSupplier().getLocations();
		if (tempLocations != null && location != null) {
			tempLocations.remove(location);
			locations.add(location);
		}
		locations.addAll(tempLocations);
		return locations;
	}

	public String getSupplierContractDetails() {
		JSONArray oneEntry = new JSONArray();
		oneEntry.put(getSerializedContract());
		this.jsonString = oneEntry.toString();
		return SUCCESS;
	}

	/**
	 * Serializes the important portions of an Contract to a JSONObject.
	 * 
	 * 
	 */
	private JSONObject getSerializedContract() {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			if (this.contract != null) {
				jsonObj.append("supplierName", this.contract.getSupplier()
						.getName());
				Long id = this.oemPartReplaced.getId();
				jsonObj.append("contractValue",
						getTotalCostForSection(id, "costAfterApplyingContract")
								.toString());
				Money recoveredCost = getTotalCostForSection(id,
						"recoveredCost");
				jsonObj.append("totalCostSymbol", recoveredCost
						.breachEncapsulationOfCurrency().getSymbol());
				jsonObj.append("totalCost", recoveredCost
						.breachEncapsulationOfAmount().toString());
			} else {
				jsonObj.append("supplierName", "");
				jsonObj.append("contractValue", "");
				jsonObj.append("totalCostSymbol", "");
				jsonObj.append("totalCost", "");
			}
		} catch (JSONException ignored) {
			logger.warn("Error serializing " + this.contract
					+ " to JSON; ignoring this contract", ignored);
		}
		return jsonObj;
	}

	/**
	 * For getting total cost for the part
	 * 
	 * @param costType
	 * @return
	 */
	public Money getTotalCostForSection(Long id, String costType) {

		setRecoveryClaim(this.recoveryClaimService.findRecoveryClaim(id));
		Money totalCost = null;
		Money sectionCost = null;
		for (CostLineItem costLineItem : getRecoveryClaim().getCostLineItems()) {
			if ("recoveredCost".equals(costType)) {
				sectionCost = getRecoveryClaim().getRecoveredCostForSection(
						costLineItem.getSection().getName());
			} else if ("costAfterApplyingContract".equals(costType)) {
				sectionCost = getRecoveryClaim()
						.getCostAfterApplyingContractForSection(
								costLineItem.getSection().getName());
			} else if ("actualCost".equals(costType)) {
				sectionCost = getRecoveryClaim().getAcutalCostForSection(
						costLineItem.getSection().getName());
			} else if ("supplierCost".equals(costType)) {
				sectionCost = getRecoveryClaim().getSupplierCostForSection(
						costLineItem.getSection().getName());
			}
			if (totalCost == null && sectionCost != null) {
				totalCost = sectionCost;
			} else if (totalCost != null && sectionCost != null) {
				totalCost = totalCost.plus(sectionCost);
			}
		}
		return totalCost;
	}

	/**
	 * Can i cache this ? cos it is needed in total cost...other way is use
	 * ww:set to add values
	 * 
	 * @param costType
	 * @param sectionName
	 * @return
	 */
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

	/**
	 * For getting the contract value
	 * 
	 * @param id
	 * @param costType
	 * @param sectionName
	 * @return
	 */
	public Money getCostForSection(Long id, String costType, String sectionName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting sum of [" + costType
					+ "] costs for section [" + sectionName + "]");
		}
		this.recoveryClaim = this.recoveryClaimService.findRecoveryClaim(id);
		Money money = null;
		if ("recoveredCost".equals(costType)) {
			money = this.recoveryClaim.getRecoveredCostForSection(sectionName);
		} else if ("costAfterApplyingContract".equals(costType)) {
			money = this.recoveryClaim
					.getCostAfterApplyingContractForSection(sectionName);
		} else if ("actualCost".equals(costType)) {
			money = this.recoveryClaim.getAcutalCostForSection(sectionName);
		} else if ("supplierCost".equals(costType)) {
			money = this.recoveryClaim.getSupplierCostForSection(sectionName);
		} else {
			throw new RuntimeException("Cost type [" + costType
					+ "] is invalid");
		}
		return money;
	}

	public boolean isOemPartRecoverable(OEMPartReplaced part) {
		return part.isPartRecoveredFromSupplier(
				this.recoveryClaim.getContract(), this.recoveryClaim.getClaim()
						.getServiceInformation().getCausalPart(),
				this.recoveryClaim.getClaim().getType());
	}

	/**
	 * Note: This method is directly called from JSP.
	 * 
	 * @return
	 */
	public List<Section> getAllSections() {
		return this.paymentSectionRepository.getSections();
	}

	public final void setPartReturnFields(Map partReturnFields) {
		this.partReturnFields = partReturnFields;
	}

	public final String getActionUrl() {
		return this.actionUrl;
	}

	public final void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public final String getTaskName() {
		return this.taskName;
	}

	public final void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setPartReplacedService(PartReplacedService partReplacedService) {
		this.partReplacedService = partReplacedService;
	}

	public Contract getContract() {
		return this.contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public OEMPartReplaced getOemPartReplaced() {
		return this.oemPartReplaced;
	}

	public void setOemPartReplaced(OEMPartReplaced oemPartReplaced) {
		this.oemPartReplaced = oemPartReplaced;
	}

	public List<OEMPartReplaced> getShipOEMParts() {
		return this.shipOEMParts;
	}

	public void setShipOEMParts(List<OEMPartReplaced> shipOEMParts) {
		this.shipOEMParts = shipOEMParts;
	}

	public void setPaymentSectionRepository(
			PaymentSectionRepository paymentSectionRepository) {
		this.paymentSectionRepository = paymentSectionRepository;
	}

	public List<List<Contract>> getContractList() {
		return this.contractList;
	}

	public void setContractList(List<List<Contract>> contractList) {
		this.contractList = contractList;
	}

	public List<Long> getPartsToBeShown() {
		return this.partsRecoverable;
	}

	public void setPartsToBeShown(List<Long> partsToBeShown) {
		this.partsRecoverable = partsToBeShown;
	}

	public Claim getClaim() {
		return this.claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public Integer getOemPartIndex() {
		return this.oemPartIndex;
	}

	public void setOemPartIndex(Integer oemPartIndex) {
		this.oemPartIndex = oemPartIndex;
	}

	public List<String> getAllTransitions(RecoveryClaim recClaim)
			throws JSONException {
		List<String> allTransitions = new ArrayList<String>();
		if (recClaim == null
				|| !recClaim.getRecoveryClaimState().equals(
						RecoveryClaimState.ACCEPTED)) {
			allTransitions.add("On Hold");
			allTransitions.add("Cannot Recover");
			allTransitions.add("Send To Supplier");
			allTransitions.add("Not For Recovery");
		} else if (!recClaim.getRecoveryClaimState().equals(
				RecoveryClaimState.REJECTED)) {
			allTransitions.add("Closed Recovered");
		}
		return allTransitions;
	}

	public List<User> getAvailableRecoveryProcessors() {
		List<User> users = this.orgService.findAllAvailableRecoveryProcessors();
		users.remove(getLoggedInUser());
		return users;
	}

	public String getCarrierInfo() throws JSONException {
		if (carrierId != null) {
			Carrier carrier = carrierRepository.findCarrierById(Long
					.parseLong(carrierId));
			JSONArray oneEntry = new JSONArray();
			oneEntry.put(new JSONObject().append("carrierCode",
					carrier.getCode()));
			jsonString = oneEntry.toString();
		}
		return SUCCESS;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

    public PartReturnProcessingService getPartReturnProcessingService() {
        return partReturnProcessingService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public RecoveryClaim getRecoveryClaim() {
		return this.recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}

	@Required
	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return this.recoveryClaimService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setCarrierRepository(CarrierRepository carrierRepository) {
		this.carrierRepository = carrierRepository;
	}

	public List<Carrier> getCarriers() {
		List<Carrier> carriers = new ArrayList<Carrier>();
		Carrier carrier = recoveryClaim.getContract().getCarrier();
		List<Carrier> tempCarriers = this.carrierRepository.findAllCarriers();
		if (tempCarriers != null && carrier != null) {
			tempCarriers.remove(carrier);
			carriers.add(carrier);
		}
		carriers.addAll(tempCarriers);
		return carriers;
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

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getJSONifiedRecoveryClaimAttachmentList() throws JSONException {
		try {
			List<Document> attachments = this.getRecoveryClaim().getAttachments();
			List<Document> supplierSharedAttachments =new ArrayList<Document>();
			List<Document> dealerSharedAttachments =new ArrayList<Document>();
			for(Document doc: attachments){								
				if(doc.getIsSharedWithSupplier()){
					supplierSharedAttachments.add(doc);
				} 
				
				if(doc.getIsSharedWithDealer()){
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

	public boolean isPartsReplacedInstalledSectionVisible() {
		return configParamService
				.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE
						.getName());
	}

	public boolean isClaimCampaign() {
		return ClaimType.CAMPAIGN.equals(claim.getType());
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public RecoveryClaim getFetchedRecoveryClaim() {
		return fetchedRecoveryClaim;
	}

	public void setFetchedRecoveryClaim(RecoveryClaim fetchedRecoveryClaim) {
		this.fetchedRecoveryClaim = fetchedRecoveryClaim;
	}

	public String fetchCurrentClaimAssignee(Long recoveryClaimId) {
		User claimAssignee = this.workListService
				.getCurrentAssigneeForRecClaim(recoveryClaimId);
		if (claimAssignee == null) {
			return "";
		}
		StringBuffer assignee = new StringBuffer();
		String firstName = claimAssignee.getFirstName();
		String lastName = claimAssignee.getLastName();
		String login = claimAssignee.getName();
		assignee.append(firstName == null ? "" : firstName);
		assignee.append(" ");
		assignee.append(lastName == null ? "" : lastName);
		assignee.append(" (");
		assignee.append(login);
		assignee.append(")");
		return assignee.toString();
	}

	public void clearSelectedLocations() {
		this.selectedLocations.clear();
	}

	public String getCostPriceType() {
		return this.configParamService
				.getStringValue(ConfigName.COST_PRICE_CONFIGURATION.getName());
	}

	public List<Location> getSelectedLocations() {
		if (getAnyValidationError() && !selectedLocations.isEmpty()) {
			selectedLocations.clear();
		}
		return selectedLocations;
	}

	public void setSelectedLocations(List<Location> selectedLocations) {
		this.selectedLocations = selectedLocations;
	}

	public List<Carrier> getSelectedCarriers() {
		if (getAnyValidationError() && !selectedCarriers.isEmpty()) {
			selectedCarriers.clear();
		}
		return selectedCarriers;
	}

	public void setSelectedCarriers(List<Carrier> selectedCarriers) {
		this.selectedCarriers = selectedCarriers;
	}

	public List<String> getRgaNumbersProvided() {
		return rgaNumbersProvided;
	}

	public void setRgaNumbersProvided(List<String> rgaNumbersProvided) {
		this.rgaNumbersProvided = rgaNumbersProvided;
	}

	public User getTransferToUser() {
		return transferToUser;
	}

	public void setTransferToUser(User transferToUser) {
		this.transferToUser = transferToUser;
	}

	public Map<String, List<Object>> getConfigParamValueForAllBus() {
		return configParamValueForAllBus;
	}

	public void setConfigParamValueForAllBus(
			Map<String, List<Object>> configParamValueForAllBus) {
		this.configParamValueForAllBus = configParamValueForAllBus;
	}

	public Boolean getAnyValidationError() {
		return anyValidationError;
	}

	public void setAnyValidationError(Boolean anyValidationError) {
		this.anyValidationError = anyValidationError;
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

	public boolean isNotPartsView() {
		return !"part".equals(getInboxViewType());
	}

	public boolean getIsSwitchViewEnabled() {
		if (this.taskName.equals(WorkflowConstants.NEW)
				|| this.taskName.equals(WorkflowConstants.ON_HOLD_FOR_PART_RETURN)) {
			return false;
		}
		return (this.switchButtonActionName != null);
	}

    protected ClaimService claimService;

    public ClaimService getClaimService() {
        return this.claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    protected WarehouseService warehouseService;

    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public WarehouseService getWarehouseService() {
        return warehouseService;
    }

    public List<Location> getOemLocations(){

           List<Location> oem_locations = new ArrayList<Location>();
            try {
                oem_locations.addAll(warehouseService.findWarehouseLocationsStartingWith("%%%"));
                return oem_locations;
            } catch (Exception e) {
                logger.error("Error while generating JSON", e);
                throw new RuntimeException("Error while generating JSON", e);
            }
    }

    public boolean displayOemAsReturnLocation(String partNumber){

        return false;
    }

    public void startPartReturnForRecoveryClaim() {
    	partReturnRequested = false;
        List<RecoverablePart> recoverableParts = this.recoveryClaim
                .getRecoveryClaimInfo().getRecoverableParts();
       // boolean isDealerCanadian = isClaimFiledByCanadianDealer(getRecoveryClaim().getClaim().getForDealer().getId());
        //Location defaultNMHGWareHouseLocation = claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation());
        for (int i = 0; i < recoverableParts.size(); i++) {
            if (recoverableParts.get(i).getOemPart().isPartToBeReturned() && !recoverableParts.get(i).getOemPart().isPartReturnsPresent()) {
                if (this.getOemReturnLocations().size() > i || this.getSupplierReturnLocations().size() > i)// means that the row
                // on the GUI was
                // not disabled
                {
                    String rgaNumber = null;
                    if (this.getRgaNumbersProvided().size() > i)
                        rgaNumber = this.getRgaNumbersProvided().get(i);

                    if(recoverableParts.get(i).getOemPart().getPartReturn() == null){
                        recoverableParts.get(i).getOemPart().setPartReturn(new PartReturn());
                    }
                    if(recoverableParts.get(i).getOemPart().getPartReturn().getDueDays() == 0){
                        recoverableParts.get(i).getOemPart().getPartReturn().setDueDays(getConfigParamService().getLongValue(
                                ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName()).intValue());
                    }
                    recoverableParts.get(i).getOemPart().getPartReturn().setRmaNumber(rgaNumber);
                    //Set the default payment condition
                    PaymentCondition condition = new PaymentCondition();
                    condition.setCode("PAY");
                    condition.setDescription("Pay without Part Return");
                    recoverableParts.get(i).getOemPart().getPartReturn().setPaymentCondition(condition);
                    recoverableParts.get(i).getOemPart().getPartReturn().setOemPartReplaced(recoverableParts.get(i).getOemPart());
                    if(recoverableParts.get(i).getOemPart().isReturnDirectlyToSupplier()){
                    	partReturnRequested = true;
                        recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(getSupplierReturnLocations().get(i));
                        //hack for canadian dealers here
                            if(isClaimFiledByCanadianDealer(getRecoveryClaim().getClaim().getForDealer().getId())){
                                //2 return required -- dealer -> nmhg, nmhg-> supplier scheduler
                                //initiate the dealer --> nmhg data -- > return location change
                                recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation()));
                                //initiate recovery return
                                recoverableParts.get(i).setSupplierReturnNeeded(true);
                                partReturnProcessingService.startRecoveryPartReturnProcessForRecPart(recoverableParts.get(i),this.recoveryClaim, getSupplierReturnLocations().get(i));
                                //Make direct return to false
                                recoverableParts.get(i).getOemPart().setReturnDirectlyToSupplier(false);

                            }
                        }else{
                        recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(getOemReturnLocations().get(i));
                        //Since shipping is happening for dealer -> nmhg one more shipment is required from nmhg -> supplier
                        recoverableParts.get(i).setSupplierReturnNeeded(false);
                    }
                    partReturnService.updateExistingPartReturns(recoverableParts.get(i).getOemPart(), claim);
                    this.partReturnProcessingService.startPartReturnProcessForPart(this.recoveryClaim.getClaim(),recoverableParts.get(i).getOemPart());
                }
            }

            else if(recoverableParts.get(i).isSupplierReturnNeeded()){
                startSupplyPartReturnForClaim();
            }else if(!recoverableParts.get(i).isSupplierPartReturnModificationAllowed()){
            	partReturnRequested = true;
            }
        }

    }

    public boolean isClaimFiledByCanadianDealer(Long id) {
        ServiceProvider dealership = orgService.findDealerById(id);
        return dealership.getAddress().getCountry().equalsIgnoreCase(CANADA_COUNTRY_CODE);
    }

	public void setSwitchButtonActionName(String switchButtonActionName) {
		this.switchButtonActionName = switchButtonActionName;
	}

	public String getSwitchButtonActionName() {
		return switchButtonActionName;
	}

	public void setSwitchButtonTabLabel(String switchButtonTabLabel) {
		this.switchButtonTabLabel = switchButtonTabLabel;
	}

	public String getSwitchButtonTabLabel() {
		return switchButtonTabLabel;
	}

	public String getInboxViewType() {
		return inboxViewType;
	}

	public void setInboxViewType(String inboxViewType) {
		this.inboxViewType = inboxViewType;
	}

    public List<Location> getOemReturnLocations() {
        return oemReturnLocations;
    }

    public void setOemReturnLocations(List<Location> oemReturnLocations) {
        this.oemReturnLocations = oemReturnLocations;
    }

    public boolean isReturnThroughDealerDirectly(){
        return configParamService.getBooleanValue(ConfigName.PART_RECOVERY_DIRECTLY_THROUGH_DEALER.getName());
    }

    public List<Location> getSupplierReturnLocations() {
        return supplierReturnLocations;
    }

    public void setSupplierReturnLocations(List<Location> supplierReturnLocations) {
        this.supplierReturnLocations = supplierReturnLocations;
    }

    public int getDefaultDueDays(){
        return configParamService.getLongValue(ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName()).intValue();
    }

    public boolean useDefaultDueDays(){
        return configParamService.getBooleanValue(ConfigName.ENABLE_DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName());
    }

    public String showPreview(){
       generatePreview();
       return SUCCESS;
    }

    protected void generatePreview(){
        preparePreviewAndCount(getTaskInstancesForShipper(getId()));
        //Set recovery claim details
        fetchedRecoveryClaim = getRecoveryClaimService().findRecoveryClaim(
                Long.parseLong(getId()));
        setRecoveryClaim(fetchedRecoveryClaim);
        setClaim(getRecoveryClaim().getClaim());
       // shipment = getShipment();
    }

    protected String resultingView() {
        generatePreview();
        if (getRecoverablePartsBeans().size() == 0) {
            addActionMessage("message.itemStatus.updated");
            return SUCCESS;
        }
        if (!hasActionErrors()) {
            addActionMessage("message.itemStatus.updated");
            addActionMessage("message.itemStatus.continue_next_step");
        }
        return INPUT;
    }

    protected void preparePreviewAndCount(List<TaskInstance> instanceList){
        //set the bean and task instance
        Map<RecoverablePart, RecoverablePartsBean> recPartAndItsPartReturns = new HashMap<RecoverablePart, RecoverablePartsBean>();
        for (TaskInstance partTask : instanceList) {
            RecoveryPartTaskBean partTaskBean = new RecoveryPartTaskBean(partTask);
            if (recPartAndItsPartReturns.get(partTaskBean.getRecoverablePart()) == null) {
                recPartAndItsPartReturns.put(partTaskBean.getRecoverablePart(),
                        new RecoverablePartsBean(partTaskBean));
            } else {
                recPartAndItsPartReturns.get(partTaskBean.getRecoverablePart())
                        .getRecoveryPartTaskBeans().add(partTaskBean);
            }

        }
        for(RecoverablePartsBean recoverablePartsBean : recPartAndItsPartReturns.values()){
            prepareCountForRecvoverablePartsBean(recoverablePartsBean);
        }
        //this.recoverablePartsBeans.clear();
        this.recoverablePartsBeans.addAll(recPartAndItsPartReturns.values());
        if(!recoverablePartsBeans.isEmpty() && null != recoverablePartsBeans.get(0).getRecoveryPartTaskBean()
                && null != recoverablePartsBeans.get(0).getRecoveryPartTaskBean().getSupplierPartReturn() ){
        	this.shipment = recoverablePartsBeans.get(0).getRecoveryPartTaskBean().getSupplierPartReturn().getSupplierShipment();
        }
    }
    
    /*public Shipment getShipment()
    {    	
	    List<RecoveryPartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
	    SupplierPartReturn supplierPartReturn = partTaskBeans.get(0).getSupplierPartReturn();
        Shipment shipment = supplierPartReturn.getSupplierShipment();
        return shipment;
    }*/

    public Shipment getShipment() {
        return shipment;
    }

    public void updateShipmentInfo(Shipment shipment)
    {    	
	    List<RecoveryPartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
	    List<SupplierPartReturn> parts = getPartReturnsFromPartTaskBeans(partTaskBeans);
        parts.get(0).setSupplierShipment(shipment);	
   
    }
    
    public List<SupplierPartReturn> getPartReturnsFromPartTaskBeans(
   		 List<RecoveryPartTaskBean> partTaskBeans) {
       List<SupplierPartReturn> parts = new ArrayList<SupplierPartReturn>();
       for (RecoveryPartTaskBean partTaskBean : partTaskBeans) {
           parts.add(partTaskBean.getSupplierPartReturn());
       }
       return parts;
   }


    private void prepareCountForRecvoverablePartsBean(RecoverablePartsBean recoverablePartsBean){

        RecoveryPartTaskBean recoveryPartTaskBean = recoverablePartsBean.getRecoveryPartTaskBeans()
                .iterator().next();
            for(SupplierPartReturn supplierPartReturn : recoveryPartTaskBean.getRecoverablePart().getSupplierPartReturns()){
                if(supplierPartReturn.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.getStatus())){
                    recoverablePartsBean.setToBeShipped(recoverablePartsBean.getToBeShipped()+1);
                }
                else if(supplierPartReturn.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER.getStatus())){
                           recoverablePartsBean.setShipmentGenerated(recoverablePartsBean.getShipmentGenerated()+1);
                }

                else if(supplierPartReturn.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PARTS_MARKED_AS_CAN_NOT_SHIPPED_BY_SUPPLIER.getStatus())){
                       recoverablePartsBean.setCannotShip(recoverablePartsBean.getCannotShip()+1);
                }

                else {
                    if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(supplierPartReturn.getStatus())) &&
                            PartReturnStatus.PARTS_SHIPPED_BY_SUPPLIER_TO_NMHG.ordinal() <= supplierPartReturn.getStatus().ordinal()) {
                        recoverablePartsBean
                                .setShipped((recoverablePartsBean.getShipped() + 1));
                    }
                    if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(supplierPartReturn.getStatus())) && PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER.ordinal() <= supplierPartReturn
                            .getStatus().ordinal()) {
                        recoverablePartsBean
                                .setReceived(recoverablePartsBean.getReceived() + 1);
                    }
                    if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(supplierPartReturn.getStatus())) && (PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_ACCEPTED.ordinal() <= supplierPartReturn
                            .getStatus().ordinal()
                            || PartReturnStatus.PARTS_RECEIVED_FROM_SUPPLIER_MARKED_AS_REJECTED.ordinal() <= supplierPartReturn
                            .getStatus().ordinal())) {
                        recoverablePartsBean.setInspected(recoverablePartsBean
                                .getInspected() + 1);

                    }
                }
            }
        recoverablePartsBean.setQtyForShipment(recoverablePartsBean.getRecoveryPartTaskBeans().size());
        recoverablePartsBean.setNotReceived(recoverablePartsBean.getShipmentGenerated()
                + recoverablePartsBean.getToBeShipped());
        recoverablePartsBean.setTotalNoOfParts(recoveryPartTaskBean.getRecoverablePart().getQuantity());

    }

    protected List<TaskInstance> getTaskInstancesForShipper(String claimId) {
        String actorId = getLoggedInUser().getName();
        String[] ids = claimId.split(":");
        if(getTaskName().equals(WorkflowConstants.SUPPLIER_PARTS_RECEIPT)
        		|| getTaskName().equals(WorkflowConstants.SUPPLIER_PARTS_INSPECTION)
        		|| getTaskName().equals(WorkflowConstants.PART_FOR_RETURN_TO_NMHG)
                || getTaskName().equals(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)
                || getTaskName().equals(WorkflowConstants.PARTS_SHIPPED_TO_NMHG)){
        	return supplierRecoveryWorkListDao.getPreviewPaneForClaimLocation(
                    new Long(ids[0]), getTaskName());
        }
        return supplierRecoveryWorkListDao.getPreviewPaneForClaimLocation(
                new Long(ids[0]), getTaskName(), actorId);

    }

    public List<RecoveryPartTaskBean> getSelectedPartTaskBeans() {
        return getSelectedPartTaskBeans(getUiRecoverablePartsBeans());
    }

    public List<RecoveryPartTaskBean> getSelectedPartTaskBeans(
            List<RecoverablePartsBean> partReplacedBeans) {
        return getSelectedPartTaskBeans(partReplacedBeans, true);
    }

    public List<RecoveryPartTaskBean> getSelectedPartTaskBeans(
            boolean processPartTaskBean) {
        return getSelectedPartTaskBeans(getUiRecoverablePartsBeans(), processPartTaskBean);
    }

    protected void processPartTaskBean(RecoveryPartTaskBean partTaskBean) {

    }


    public List<RecoveryPartTaskBean> getSelectedPartTaskBeans( List<RecoverablePartsBean> partReplacedBeans, boolean processPartTaskBean){
        List<RecoveryPartTaskBean> prunedPartTasks = new ArrayList<RecoveryPartTaskBean>();
        for (RecoverablePartsBean partReplacedBean : partReplacedBeans) {
            if (partReplacedBean.isSelected()) {
                prepareTaskBeans(partReplacedBean, processPartTaskBean);
                List<RecoveryPartTaskBean> taskBeans = partReplacedBean.getRecoveryPartTaskBeans();
                for (RecoveryPartTaskBean partTaskBean : taskBeans) {
                    if (partTaskBean != null
                            && (partTaskBean.isSelected()
                            || "Supplier Parts Receipt".equals(getTaskName()) || "Supplier Parts Inspection"
                            .equals(getTaskName()))
                            ) {
                        if(processPartTaskBean)
                            processPartTaskBean(partTaskBean);
                        prunedPartTasks.add(partTaskBean);
                    }
                }
            }
        }
        return prunedPartTasks;
    }

    private void prepareTaskBeans(RecoverablePartsBean partReplacedBean, boolean processPartTaskBean) {
        Iterator<RecoveryPartTaskBean> partTaskIttr = partReplacedBean.getRecoveryPartTaskBeans().iterator();
        RecoveryPartTaskBean partTaskBean;
        if (WorkflowConstants.PART_FOR_RETURN_TO_NMHG.equals(this.taskName)) {
            if(this.transitionTaken != null && "Generate Shipment".equalsIgnoreCase(this.transitionTaken)) {
                for (int i = 0; i < partReplacedBean.getShip()
                        && partTaskIttr.hasNext(); i++) {
                    partTaskBean = partTaskIttr.next();
                    partTaskBean.setSelected(true);
                    if(processPartTaskBean){
                        partTaskBean
                                .setTriggerStatus(PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT);
                        //partTaskBean.getSupplierPartReturn().setStatus(PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER);
                    }

                }

                for (int i = 0; i < partReplacedBean.getCannotShip()
                        && partTaskIttr.hasNext(); i++) {
                    partTaskBean = partTaskIttr.next();
                    partTaskBean.setSelected(true);
                    if(processPartTaskBean){
                        partTaskBean
                                .setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
                        //partTaskBean.getSupplierPartReturn().setStatus(PartReturnStatus.PARTS_MARKED_AS_CAN_NOT_SHIPPED_BY_SUPPLIER);
                    }
                }

            }else {
                for (;partTaskIttr.hasNext();) {
                    partTaskBean = partTaskIttr.next();
                    partTaskBean.setSelected(true);
                }
            }
        }
        else if (WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG.equals(this.taskName)) {
                for (int i = 0; i < partReplacedBean.getShipmentGenerated()
                        && partTaskIttr.hasNext(); i++) {
                    partTaskBean = partTaskIttr.next();
                    partTaskBean.setSelected(true);
                    if(processPartTaskBean){
                        partTaskBean
                                .setTriggerStatus(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED);
                        //partTaskBean.getSupplierPartReturn().setStatus(PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER);
                    }
                }
            }

        else if (WorkflowConstants.SUPPLIER_PARTS_RECEIPT.equals(this.taskName)) {
            for (int i = 0; i < partReplacedBean.getReceive()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(MARK_FOR_INSPECTION);
                partTaskBean.setWarehouseLocation(partReplacedBean
                        .getWarehouseLocation());
            }
            for (int i = 0; i < partReplacedBean.getDidNotReceive()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(MARK_NOT_RECEIVED);
            }
        }
        if (WorkflowConstants.SUPPLIER_PARTS_INSPECTION.equals(this.taskName)
                || (partReplacedBean.isToBeInspected())) {
            partTaskIttr = partReplacedBean.getRecoveryPartTaskBeans().iterator();
            for (int i = 0; i < partReplacedBean.getAccepted()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(ACCEPT);
                partTaskBean.setReturnToDealer(partReplacedBean.isReturnToDealer());
                partTaskBean.setScrap(partReplacedBean.isScrap());
                partTaskBean.setReturnToSupplier(partReplacedBean.isReturnToSupplier());
                partTaskBean.setAcceptanceCause(partReplacedBean.getAcceptanceCause());
                partTaskBean.setReturnToDealer(partReplacedBean.isReturnToDealer());
                partTaskBean.setScrap(partReplacedBean.isScrap());
            }
            for (int i = 0; i < partReplacedBean.getRejected()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(REJECT);
                partTaskBean.setReturnToSupplier(partReplacedBean.isReturnToSupplier());
                partTaskBean.setFailureCause(partReplacedBean
                            .getFailureCause());
                partTaskBean.setReturnToSupplier(partReplacedBean.isReturnToSupplier());

            }
        }
    }



    public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
        return supplierRecoveryWorkListDao;
    }

    public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
        this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
    }

    public List<RecoverablePartsBean> getRecoverablePartsBeans() {
        return recoverablePartsBeans;
    }

    public void setRecoverablePartsBeans(List<RecoverablePartsBean> recoverablePartsBeans) {
        this.recoverablePartsBeans = recoverablePartsBeans;
    }
    
    public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public Boolean isTaskShipmentGeneratedToNMHG(){
    	return this.taskName.equals(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG);
    }


    public String getTransitionTaken() {
        return transitionTaken;
    }

    public void setTransitionTaken(String transitionTaken) {
        this.transitionTaken = transitionTaken;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

	public CarrierRepository getCarrierRepository() {
		return carrierRepository;
	}


    public boolean doNotDisplayCustomInbox(){
        return getNotSupportedCustomViewInbox().contains(this.taskName);
    }

    private List<String> getNotSupportedCustomViewInbox(){

        List<String> inboxList = new ArrayList<String>();
        inboxList.add(Constants.CONFIRM_PART_RETURNS);
        inboxList.add(Constants.SUPPLIER_PARTS_RECEIPT);
        inboxList.add(Constants.SUPPLIER_PARTS_INSPECTION);
        inboxList.add(Constants.SHIPMENT_GENERATED_TO_NMHG);
        inboxList.add(Constants.PARTS_SHIPPED_TO_NMHG);
        inboxList.add(Constants.PARTS_FOR_RETURN_TO_NMHG);
        return inboxList;
    }

    public List<RecoverablePartsBean> getUiRecoverablePartsBeans() {
        return uiRecoverablePartsBeans;
    }

    public void setUiRecoverablePartsBeans(List<RecoverablePartsBean> uiRecoverablePartsBeans) {
        this.uiRecoverablePartsBeans = uiRecoverablePartsBeans;
    }
    
    protected void initiateReturnToDealer(RecoverablePartsBean partBean){
        RecoverablePart recoverablePart =  partBean.getRecoverablePart();
        OEMPartReplaced oemPartReplaced = recoverablePart.getOemPart();
        if(oemPartReplaced.getPartReturns() != null && oemPartReplaced.getPartReturns().size() >0){
            //get the oem return location
            Location retLocation = null;
            if(!recoverablePart.getOemPart().isReturnDirectlyToSupplier()){
                retLocation = recoverablePart.getOemPart().getActivePartReturn().getReturnLocation();
            }else{
                //If direct ship then we need to pick from the supplier return.
                retLocation = recoverablePart.getRetrunLocationObjectForSupplier();
            }
            partReturnProcessingService.initiateReturnToDealer(partBean.getRecoveryPartTaskBeans().get(0).getRecoveryClaim().getClaim(), oemPartReplaced, retLocation);
        }
    }
    
	public boolean enableComponentDateCode() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_COMPONENT_DATE_CODE.getName());
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

    protected List<TaskInstance> allOpenInstancesForTask(List<TaskInstance> instances, String taskName){
        List<RecoverablePart> selectedParts = new ArrayList<RecoverablePart>();
        for (TaskInstance taskInstance : instances) {
            selectedParts.add(((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn")).getRecoverablePart());
        }

        return this.supplierRecoveryWorkListDao.findAllShipmentAwaitedTasks(selectedParts, taskName);
    }

    protected List<TaskInstance> filterTaskInstancesBasedOnPart(List<TaskInstance> instances){
        List<RecoverablePart> selectedParts = new ArrayList<RecoverablePart>();
        Map<Long, TaskInstance> tempMap = new HashMap<Long, TaskInstance>();
        for(TaskInstance instance : instances){
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) instance.getVariable("supplierPartReturn");
            if(supplierPartReturn != null && tempMap.get(supplierPartReturn.getRecoverablePart().getId()) == null){
                tempMap.put(supplierPartReturn.getRecoverablePart().getId(), instance);
            }
        }
        return new ArrayList<TaskInstance>(tempMap.values());
    }

	public boolean isPartReturnRequested() {
		return partReturnRequested;
	}

	public void setPartReturnRequested(boolean partReturnRequested) {
		this.partReturnRequested = partReturnRequested;
	}

    public String getDefaultReturnLocationForNMHG(){
        return getWarehouseService().getDefaultReturnLocation(getConfigParamService().getStringValue(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName())).getCode();
    }
	
}

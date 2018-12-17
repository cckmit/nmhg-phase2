package tavant.twms.web.admin.stateMandates;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.stateMandates.StateMandateAudit;
import tavant.twms.domain.stateMandates.StateMandateAuditRepository;
import tavant.twms.domain.stateMandates.StateMandateOtherCategories;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.stateMandates.StateMandatesService;
import tavant.twms.domain.stateMandates.StateMndteCostCtgyMapping;
import tavant.twms.infra.PageResult;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

public class ListStateMandatesAction extends SummaryTableAction implements
		Preparable, TWMSWebConstants, Validateable, BUSpecificSectionNames {

	private StateMandatesService stateMandatesService;
	private static final Logger logger = Logger
			.getLogger(ListStateMandatesAction.class);
	private StateMandates stateMandates;
	private List<StateMandateOtherCategories> stateMandateOtherCategory = new ArrayList<StateMandateOtherCategories>();
	private List<CostCategory> costCategoryList = new ArrayList<CostCategory>();
	private List<String> otherCategories = new ArrayList<String>();
	private static final String MESSAGE_KEY_CREATE = "message.manage.createStateMandateSuccess";
	private static final String MESSAGE_KEY_UPDATE = "message.manage.updateStateMandateSuccess";
	private static final String MESSAGE_KEY_DEACTIVATE = "message.manage.deactivateStateMandateSuccess";
	private static final String MESSAGE_KEY_ACTIVATE = "message.manage.activateStateMandateSuccess";
	private static final String LATE_FEE_CATEGORY="Late Fee";
	public static final String DEDUCTIBLE_CATEGORY="Deductible";
	
	private CountryStateService countryStateService;
	
	protected LovRepository lovRepository;
	
	private String stateMandateComments;
	
	private Long stateMandateAuditID;
	
	private StateMandateAudit stateMandateAudit;
	
	private User createdBy;
	
	private StateMandateAuditRepository stateMandateAuditRepository;
	
	private CostCategoryRepository costCategoryRepository;
	
	@Required
	public void setCostCategoryRepository(
			CostCategoryRepository costCategoryRepository) {
		this.costCategoryRepository = costCategoryRepository;
	}
	
	@Required
	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	@Required
	public void setCountryStateService(CountryStateService countryStateService) {
		this.countryStateService = countryStateService;
	}
	
	public StateMandateAuditRepository getStateMandateAuditRepository() {
		return stateMandateAuditRepository;
	}

	@Required
	public void setStateMandateAuditRepository(
			StateMandateAuditRepository stateMandateAuditRepository) {
		this.stateMandateAuditRepository = stateMandateAuditRepository;
	}

	public StateMandateAudit getStateMandateAudit() {
		return stateMandateAudit;
	}

	public void setStateMandateAudit(StateMandateAudit stateMandateAudit) {
		this.stateMandateAudit = stateMandateAudit;
	}

	public Long getStateMandateAuditID() {
		return stateMandateAuditID;
	}

	public void setStateMandateAuditID(Long stateMandateAuditID) {
		this.stateMandateAuditID = stateMandateAuditID;
	}

	public String getStateMandateComments() {
		return stateMandateComments;
	}

	public void setStateMandateComments(String stateMandateComments) {
		this.stateMandateComments = stateMandateComments;
	}

	public List<StateMandateOtherCategories> getStateMandateOtherCategory() {
		return stateMandateOtherCategory;
	}

	public void setStateMandateOtherCategory(
			List<StateMandateOtherCategories> stateMandateOtherCategory) {
		this.stateMandateOtherCategory = stateMandateOtherCategory;
	}

	public List<CostCategory> getCostCategoryList() {
		return costCategoryList;
	}

	public void setCostCategoryList(List<CostCategory> costCategoryList) {
		this.costCategoryList = costCategoryList;
	}

	public StateMandates getStateMandates() {
		return stateMandates;
	}

	public void setStateMandates(StateMandates stateMandates) {
		this.stateMandates = stateMandates;
	}
	
	public List<String> getOtherCategories() {
		return otherCategories;
	}

	public void setOtherCategories(List<String> otherCategories) {
		this.otherCategories = otherCategories;
	}

	@Required
	public void setStateMandatesService(
			StateMandatesService stateMandatesService) {
		this.stateMandatesService = stateMandatesService;
	}

	@Override
	public PageResult<?> getBody() {
		PageResult<StateMandates> pageResult = stateMandatesService
				.findAll(getCriteria());
		return pageResult;
	}

	@Override
	public void validate() {
		this.validateStateMandate();
	}

	private void validateStateMandate() {
		if (stateMandates != null) {
			if (!StringUtils.hasText(stateMandates.getState())) {
				addActionError("error.stateMandates.state");
			}
			if (null == stateMandates.getEffectiveDate()) {
				addActionError("error.stateMandates.effectiveDate");
			}

			this.getAllCostCategories();
			this.loadOtherCategories();
		}

	}
	
	@Override
	public List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.id", "id",
				0, "String", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("State", "state", 15,
				"String", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn("Status", "status", 15,
				"String", false, false, false, false));
		tableHeadData.add(new SummaryTableColumn("Effective Date",
				"effectiveDate", 15, "String"));
		tableHeadData.add(new SummaryTableColumn("Last modified date",
				"d.updatedOn", 15, "String"));
		tableHeadData.add(new SummaryTableColumn("Last modified user",
				"d.lastUpdatedBy.getCompleteNameAndLogin()",15, "String",
				SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		return tableHeadData;

	}

	public void prepare() throws Exception {
		if (null != id && id.trim().length() != 0) {
			stateMandates = (StateMandates) stateMandatesService
					.findById(new Long(id));
		}

	}

	public String load() {
		this.getAllCostCategories();
		this.loadOtherCategories();
		return SUCCESS;
	}
	
	public String preview() {
		return SUCCESS;
	}

	public String detail() {
		stateMandates = (StateMandates) stateMandatesService.findById(new Long(
				id));
		
		return SUCCESS;
	}

	public List<CountryState> fetchCountryStates(String country) {
		return this.countryStateService.fetchCountryStates(country);
	}

	public List<ListOfValues> getLaborRateTypes(String laborRateType) {
		return this.lovRepository.findAllActive(laborRateType);
	}

	private List<CostCategory> getAllCostCategories() {
		costCategoryList = this.costCategoryRepository.findAllStateMandateCostCategories();
		return costCategoryList;
	}

	public String createStateMandate() throws Exception {
		createdBy = getLoggedInUser();
		if(stateMandatesService.findByName(stateMandates.getState())!=null){
			addActionError("message.manage.createStateMandate.duplicate");
			return INPUT;
		}
		List<StateMndteCostCtgyMapping> othersList = new ArrayList<StateMndteCostCtgyMapping>();
		for(StateMandateOtherCategories others : this.getStateMandateOtherCategory()){
			StateMndteCostCtgyMapping stateMndteCostCtgyMapping = new StateMndteCostCtgyMapping();
			stateMndteCostCtgyMapping.setOthers(others.getOthers());
			stateMndteCostCtgyMapping.setMandatory(others.getMandatory());
			othersList.add(stateMndteCostCtgyMapping);
		}
		stateMandates.getStateMandateCostCatgs().addAll(othersList);
		for(StateMndteCostCtgyMapping stateMndtCostCatg :stateMandates.getStateMandateCostCatgs()){
			stateMndtCostCatg.setStateMandates(stateMandates);
		}
		stateMandates.setStatus(TWMSWebConstants.STATUS_ACTIVE);
		stateMandatesService.saveStateMandates(stateMandates,this.stateMandateComments,createdBy);
		addActionMessage(MESSAGE_KEY_CREATE);
		return SUCCESS;
	}

	public String updateStateMandates() throws Exception {
		stateMandatesService.updateStateMandates(stateMandates,this.stateMandateComments,createdBy);
		addActionMessage(MESSAGE_KEY_UPDATE);
		return SUCCESS;
	}

	public String deactivateStateMandates() throws Exception {
		stateMandates.setStatus(TWMSWebConstants.STATUS_INACTIVE);
		stateMandatesService.updateStateMandates(stateMandates,this.stateMandateComments,createdBy);
		addActionMessage(MESSAGE_KEY_DEACTIVATE);
		return SUCCESS;
	}
	
	public String activateStateMandates() throws Exception{
		stateMandates.setStatus(TWMSWebConstants.STATUS_ACTIVE);
		stateMandatesService.updateStateMandates(stateMandates,this.stateMandateComments,createdBy);
		addActionMessage(MESSAGE_KEY_ACTIVATE);
		return SUCCESS;
	}
	
	   public String getStateMandateHistory(){
	    	setStateMandateAudit(stateMandateAuditRepository.find(stateMandateAuditID));
	        return "success";
	    }
	   
	   private void loadOtherCategories(){
		   this.otherCategories.add(LATE_FEE_CATEGORY);
			this.otherCategories.add(DEDUCTIBLE_CATEGORY);
	   }
	   
		public String getMessageKey(String messageKey) {
	        return NAMES_AND_KEY.get(messageKey);
	    }
	   
}

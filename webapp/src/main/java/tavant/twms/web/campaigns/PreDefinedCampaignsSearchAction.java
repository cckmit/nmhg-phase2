package tavant.twms.web.campaigns;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignClass;
import tavant.twms.domain.campaign.FieldModificationInventoryStatus;
import tavant.twms.domain.campaign.CampaignCriteria;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.DisplayImageResolverForCampaign;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PreDefinedCampaignsSearchAction extends SummaryTableAction {

	private static Logger logger = Logger
			.getLogger(PreDefinedCampaignsSearchAction.class);
	private CampaignCriteria campaignCriteria;
	private SavedQueryService savedQueryService;
	private CampaignAssignmentService campaignAssignmentService;
	private Long savedQueryId;
	private boolean notATemporaryQuery;
	private CampaignAdminService campaignAdminService;
	private List<CampaignClass> campaignClasses;
	private List<FieldModificationInventoryStatus> fieldModificationInventoryStatus;
	private String searchQueryName;
	private String context;
	private String searchString;
	private List<String> campaignStatus = new ArrayList<String>();
	private boolean isInternalUser;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String showSearchPage() {
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		populateCampaignClasses();
		populateCampaignReason();
		campaignStatus = campaignAssignmentService.findAllStatusForCampaign();
		return SUCCESS;
	}
	
	public boolean isPageReadOnly() {
		return false;
	}
	public String deletePredefinedQuery(){
		if(savedQueryId != null){
			savedQueryService.deleteQueryWithId(savedQueryId);
		}
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		if (campaignCriteria == null) {
			if (notATemporaryQuery) {
				fetchSearchCriteriaFromDB();
			} else {
				campaignCriteria = (CampaignCriteria) session
						.get("campaignFieldModSearch");
			}
		}
		ServiceProvider dealerShip = null;
		if (isLoggedInUserADealer()) {
			dealerShip = getLoggedInUsersDealership();
		}

		return campaignAssignmentService.findCampaignsForPredefinedSearches(
				campaignCriteria, getCriteria(), dealerShip);
	}

	private void fetchSearchCriteriaFromDB() {
		String xml = savedQueryService.findById(savedQueryId).getSearchQuery();
		XStream xstream = new XStream(new DomDriver());
		campaignCriteria = (CampaignCriteria) xstream.fromXML(xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (campaignCriteria != null) {
			if (notATemporaryQuery) {
				persistSearchConditions();
			} else {
				session.put("campaignFieldModSearch", campaignCriteria);
			}
		}
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();

		tableHeadData.add(new SummaryTableColumn("label.dcap.warnings",
				"imageCol", 3, IMAGE, Constants.WARNING_IMG_FOR_CAMPAIGN,
				false, false, false, true));
		tableHeadData.add(new SummaryTableColumn("Serial Number",
				"item.serialNumber", 13, "string", "item.serialNumber", true,
				false, false, false));
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.code",
				"campaign.code", 13, "String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.description",
				"campaign.description", 18, "String",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		tableHeadData.add(new SummaryTableColumn("label.common.startDate",
				"campaign.fromDate", 11, "Date"));
		tableHeadData.add(new SummaryTableColumn("label.common.endDate",
				"campaign.tillDate", 11, "Date"));
		tableHeadData.add(new SummaryTableColumn("label.warrantyAdmin.campaignComplete",
				"notificationStatus", 12, "String"));
		tableHeadData.add(new SummaryTableColumn("label.campaign.search.dealerName",
				"dealership.name", 20, "String"));
		return tableHeadData;
	}

	public String showSearchConstraints() {		
		this.isInternalUser = this.orgService.isInternalUser(getLoggedInUser());
		populateCampaignClasses();
		populateCampaignReason();
		campaignStatus = campaignAssignmentService.findAllStatusForCampaign();
		if (campaignCriteria == null) {
			if (notATemporaryQuery) {
				fetchSearchCriteriaFromDB();
			} else {
				campaignCriteria = (CampaignCriteria) session
						.get("campaignFieldModSearch");
			}
		}
		return SUCCESS;
	}

	public String validateSearchFields() throws Exception {		
		if (notATemporaryQuery) {
			if (StringUtils.isBlank(searchQueryName)) {
				addActionError("error.predefinedsearch.queryLabel.mandatory");
				return INPUT;
			} else if (savedQueryService
					.isQueryNameUniqueForUserAndContext(searchQueryName,context)) {
				//Fix for NMHGSLMS-992
				if(null == savedQueryId){
					addActionError("error.predefinedsearch.queryLabel.duplicate");
					return INPUT;
				}								
			}
		}
		return SUCCESS;
	}

	private void persistSearchConditions() {
		if (campaignCriteria != null && savedQueryId == null) {
			XStream xstream = new XStream(new DomDriver());
			SavedQuery savedQuery = new SavedQuery();
			savedQuery.setSearchQuery(xstream.toXML(campaignCriteria));
			savedQuery.setSearchQueryName(searchQueryName);
			savedQuery.setContext(context);
			try {
				savedQueryService.saveSearchQuery(savedQuery);
			} catch (Exception e) {
				logger.error("Error saving query in database" + e);
			}
			savedQueryId = savedQuery.getId();

		}
		if (savedQueryId != null) {
			try {
				SavedQuery savedQuery = savedQueryService
						.findById(savedQueryId);
				XStream xstream = new XStream(new DomDriver());
				savedQuery.setSearchQuery(xstream.toXML(campaignCriteria));
				savedQuery.setSearchQueryName(searchQueryName);
				savedQuery.setContext(context);
				savedQueryService.update(savedQuery);
			} catch (Exception e) {
				logger.error("Exception occured is", e);
				e.printStackTrace();
			}
		}

	}

	private void populateCampaignClasses() {
		this.campaignClasses = this.campaignAdminService.getAllClasses();

	}
	
	private void populateCampaignReason() {
		this.fieldModificationInventoryStatus = this.campaignAdminService.getFieldModificationInventoryStatus();

	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public Long getSavedQueryId() {
		return savedQueryId;
	}

	public void setSavedQueryId(Long savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public CampaignCriteria getCampaignCriteria() {
		return campaignCriteria;
	}

	public void setCampaignCriteria(CampaignCriteria campaignCriteria) {
		this.campaignCriteria = campaignCriteria;
	}

	public List<CampaignClass> getCampaignClasses() {
		return campaignClasses;
	}

	public void setCampaignAdminService(
			CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}

	public boolean isNotATemporaryQuery() {
		return notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean notATemporaryQuery) {
		this.notATemporaryQuery = notATemporaryQuery;
	}

	public String getSearchQueryName() {
		return searchQueryName;
	}
	
	/** 
	* @param searchQueryName 
	* Updated for SLMSPROD-747 
	* Handled the single,double quote and other types of single quotes 
	* which can harm opening the saved query. 
	*/ 
	public void setSearchQueryName(String searchQueryName) {
		if (searchQueryName != null) {
			String updatedQueryName = searchQueryName.replaceAll(
					"\\u0022|\u0026|\u0027|\u00B4|\u2018|\u2019|\u201C|\u201D|\u201E", "");
			this.searchQueryName = updatedQueryName;
		} else {
			this.searchQueryName = searchQueryName;
		}
	}

	@Override
	protected BeanProvider getBeanProvider() {
		return new DisplayImageResolverForCampaign();
	}

	public List<String> getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(List<String> campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public boolean isInternalUser() {
		return isInternalUser;
	}

	public void setInternalUser(boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
	}
	
	public List<FieldModificationInventoryStatus> getFieldModificationInventoryStatus() {
		return fieldModificationInventoryStatus;
	}


}

package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.partreturn.PartReturnSearchCriteria;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class PreDefinedPartReturnSearchAction extends SummaryTableAction {

	PartReturnService partReturnService;
	private PartReturnSearchCriteria partReturnSearchCriteria;
	private String searchString;
	private Long queryId;
	private boolean notATemporaryQuery;
	ClaimService claimService;
	private Claim claim;
	private SavedQueryService savedQueryService;
	private String context;
	private String searchQueryName;
	private List<PartReturnStatus> partReturnStatus = new ArrayList<PartReturnStatus>();
	private List<ClaimState>claimsStatus = new ArrayList<ClaimState>();
	private List<String> partReturnLocation = new ArrayList<String>();

	private static Logger logger = LogManager
			.getLogger(PreDefinedPartReturnSearchAction.class);

	public String getSearchString() {
		return searchString;
	}
	
	public String deletePredefinedQuery(){
		if(queryId != null){
			savedQueryService.deleteQueryWithId(queryId);
		}
		return SUCCESS;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String searchExpression() {	
		populateListsForSearch();
		return SUCCESS;
	}
	
	public boolean isPageReadOnly() {
		return false;
	}
	
	private void populateListsForSearch(){
		partReturnStatus = PartReturnStatus.getStatusListForSearch(isLoggedInUserAnInternalUser());
		claimsStatus=ClaimState.getStatusListForSearch(isLoggedInUserAnInternalUser());
		partReturnLocation = partReturnService.findAllLocationsForPartReturn();
	}

	@Override
	protected PageResult<?> getBody() {
		if (partReturnSearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				partReturnSearchCriteria = (PartReturnSearchCriteria) xstream
						.fromXML(searchString);
			} else {
				partReturnSearchCriteria = (PartReturnSearchCriteria) session
						.get("partReturnSearchCriteria");
			}
		}
		partReturnSearchCriteria.setPageSpecification(getCriteria()
				.getPageSpecification());
		addSortCriteria(partReturnSearchCriteria);
		addFilterCriteria(partReturnSearchCriteria);
		PageResult<?> partReturns = null;
		try {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getCurrentBusinessUnit().getName());
			partReturns = this.partReturnService
					.findAllClaimsMatchingCriteria(partReturnSearchCriteria);
		} catch (Exception e) {
			logger.error("Exception Occurred is ", e);
			e.printStackTrace();
		}

		return partReturns;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (partReturnSearchCriteria != null) {
			if (notATemporaryQuery) {
				domainCriteriaToString();
			} else {
				session.put("partReturnSearchCriteria",
						partReturnSearchCriteria);
			}
		}
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.dueParts.part_no",
				"detailId", 2, true, true, true, false));

		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.partNumber",
				"partNumber", 10));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.returnLocation",
				"locationCode", 15, "string"));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.partReturnConfiguration.claimNo",
				"claimNumber", 10, "string"));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.dueDate",
				"dueDate", 10, "date",SummaryTableColumnOptions.NO_FILTER));
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.status",
				"status.status", 10, "string",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.viewClaim.claimStatus",
				"claimAudit.state.displayStatus", 10, "string",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.shipment_no",
				"shipmentNumber", 10, "string"));
	    this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.dealerName",
                "dealerName", 15, "string"));
        this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.wpra",
                "wpraNumber", 8, "string"));

		return this.tableHeadData;

	}

	public void domainCriteriaToString() {
		if (queryId == null) {
			if (partReturnSearchCriteria != null) {
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(partReturnSearchCriteria);
				SavedQuery savedQuery = new SavedQuery();
				savedQuery.setSearchQuery(searchString);
				savedQuery.setSearchQueryName(searchQueryName);
				savedQuery.setContext(context);
				try {
					savedQueryService.saveSearchQuery(savedQuery);
					queryId = savedQuery.getId();
				} catch (Exception e) {
					logger.error("Exception occured is", e);
					e.printStackTrace();
				}
			}
		} else {
			try {
				SavedQuery savedQuery = savedQueryService.findById(queryId);
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(partReturnSearchCriteria);
				savedQuery.setSearchQuery(searchString);
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(searchQueryName);
				savedQueryService.update(savedQuery);
			} catch (Exception e) {
				logger.error("Exception occured is", e);
				e.printStackTrace();
			}
		}
	}

	private void addSortCriteria(ListCriteria criteria) {
		criteria.removeSortCriteria();
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(sortOnColumn, ascending);
		}		
	}

	public String showPreDefinedPartReturnSearchQuery() {
		populateListsForSearch();
		if (partReturnSearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService.findById(queryId);								
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				partReturnSearchCriteria = (PartReturnSearchCriteria) xstream
						.fromXML(searchString);
			} else {
				partReturnSearchCriteria = (PartReturnSearchCriteria) session
						.get("partReturnSearchCriteria");
			}
		}
		return SUCCESS;
	}

	private void addFilterCriteria(ListCriteria criteria) {
		criteria.removeFilterCriteria();
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	public String detail() throws Exception {
		if (id != null) {
			// todo-this is a temp arrangement required for SummaryTableAction.
			// Need to remove once SummaryTableAction starts
			// supporting multiple id columns.

			StringTokenizer tokenizer = new StringTokenizer(id, "and");
			String claimId = tokenizer.nextToken();
			claim = claimService.findClaim(Long.parseLong(claimId));
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
				if(null == queryId){
					addActionError("error.predefinedsearch.queryLabel.duplicate");
					return INPUT;
				}								
			}
		}
		return SUCCESS;
	}

	public PartReturnSearchCriteria getPartReturnSearchCriteria() {
		return partReturnSearchCriteria;
	}

	public void setPartReturnSearchCriteria(
			PartReturnSearchCriteria partReturnSearchCriteria) {
		this.partReturnSearchCriteria = partReturnSearchCriteria;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public boolean isNotATemporaryQuery() {
		return notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean notATemporaryQuery) {
		this.notATemporaryQuery = notATemporaryQuery;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
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
					"\\u0022|\u0026|\u0027|\u00B4|\u2018|\u2019|\u201C|\u201D|\u201E",
					"");
			this.searchQueryName = updatedQueryName;
		} else {
			this.searchQueryName = searchQueryName;
		}
	}

	public List<PartReturnStatus> getPartReturnStatus() {
		return partReturnStatus;
	}

	public void setPartReturnStatus(List<PartReturnStatus> partReturnStatus) {
		this.partReturnStatus = partReturnStatus;
	}

	public List<String> getPartReturnLocation() {
		return partReturnLocation;
	}

	public void setPartReturnLocation(List<String> partReturnLocation) {
		this.partReturnLocation = partReturnLocation;
	}

	public List<ClaimState> getClaimsStatus() {
		return claimsStatus;
	}

	public void setClaimsStatus(List<ClaimState> claimsStatus) {
		this.claimsStatus = claimsStatus;
	}
	
}

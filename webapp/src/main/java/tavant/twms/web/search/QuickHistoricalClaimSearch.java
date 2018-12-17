package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import tavant.twms.domain.claim.ClaimSearchCriteria;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

public class QuickHistoricalClaimSearch extends SummaryTableAction implements
ServletRequestAware {
	
	private ClaimService claimService;
	ClaimSearchCriteria claimSearchCriteria;
	private ConfigParamService configParamService;
	String historicalClaimNumber;
	
	private static Logger logger = LogManager
	.getLogger(PreDefinedClaimsSearchAction.class);

	
	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		session.put("claimSearchCriteria", claimSearchCriteria);
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimNumber", "claimNumber", 14, "string",
				"claimNumber", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		
		if (inboxViewFields())
        	addInboxViewFieldsToHeader(tableHeadData, LABEL_COLUMN_WIDTH);
        else {
				this.tableHeadData.add(new SummaryTableColumn(
						"label.inboxView.claimStatus", "enum:ClaimState:state", 12,
						"string", "activeClaimAudit.state.displayStatus", SummaryTableColumnOptions.NO_SORT));
			
			
			this.tableHeadData.add(new SummaryTableColumn(
					"label.claim.historicalClaimNumber", "histClmNo", 12, "string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.servProviderName", "forDealer.name", 12, "string"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.common.model",
					"itemReference.model.name", 12, "String"));
			
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.failureCode",
					"activeClaimAudit.serviceInformation.faultCode", 12, "String"));
			this.tableHeadData.add(new SummaryTableColumn(
					"columnTitle.newClaim.causalPart",
					"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", 12, "String", "causalPartBrandItemNumber"));
			this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.failureDate", "activeClaimAudit.failureDate", 12, "date"));
	        this.tableHeadData.add(new SummaryTableColumn(
					"label.inboxView.repairDate", "activeClaimAudit.repairDate",12, "date"));
        }
		
		return this.tableHeadData;
	}
	
	@Override
	protected PageResult<?> getBody() {
		ServiceProvider loggedInUser=getLoggedInUsersDealership();
		return claimService.findAllHistClaimsMatchingCriteria(getCriteria(),loggedInUser);
	}
	
	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = getListCriteria();
		addFilterCriteria(listCriteria);
		listCriteria.setHistoricalClaimNumber(historicalClaimNumber);
		addSortCriteria(listCriteria);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public ClaimSearchCriteria getClaimSearchCriteria() {
		return claimSearchCriteria;
	}

	public void setClaimSearchCriteria(ClaimSearchCriteria claimSearchCriteria) {
		this.claimSearchCriteria = claimSearchCriteria;
	}
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public String getHistoricalClaimNumber() {
		return historicalClaimNumber;
	}

	public void setHistoricalClaimNumber(String historicalClaimNumber) {
		this.historicalClaimNumber = historicalClaimNumber;
	}

	public boolean isClaimStatusShownToDealer(){
        return configParamService.getBooleanValue(ConfigName.ALL_CLAIM_STATUS_SHOWN_TO_DEALER.getName());
    }
	
	private void addSortCriteria(ListCriteria criteria) {
		criteria.removeSortCriteria();
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		criteria.removeFilterCriteria();
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}
	

}

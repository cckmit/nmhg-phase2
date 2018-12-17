package tavant.twms.web.search;
import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class PartShippedNotReceivedSearchAction extends SummaryTableAction {
	
	private ClaimService claimService;
	
	private ConfigParamService configParamService;
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}
	
	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimNumber", "claim.claimNumber", 10, "string",
				"claimNumber", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.dealerName", "claim.forDealer.name", 30, "string",
				"forDealer.name", false, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimType", "claim.clmTypeName", 15, "string",
				"clmTypeName", false, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.newClaim.causalPart", "claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", 12, "string",
				"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", false, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.failureDate", "claim.activeClaimAudit.failureDate", 10, "date",
				"activeClaimAudit.failureDate", false, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.repairDate", "claim.activeClaimAudit.repairDate", 10, "date",
				"activeClaimAudit.repairDate", false, false, false, false));
		return this.tableHeadData;
	}

	@Override
	protected PageResult<?> getBody() {
		Long daysForActingOnClaim = configParamService.getLongValue(ConfigName.DAYS_FOR_PARTSSHIIPED_NOTRECEIVED.getName());
		return claimService.getAllPartShippedNotReceivedClaims(getCriteria(), daysForActingOnClaim);
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
	
	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = getListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}
	
}
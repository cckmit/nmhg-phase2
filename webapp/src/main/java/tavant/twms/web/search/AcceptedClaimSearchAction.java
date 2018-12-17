package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.interceptor.ServletRequestAware;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

public class AcceptedClaimSearchAction extends SummaryTableAction implements ServletRequestAware{
	
	private ClaimService claimService;
	
	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}
	
	public String detail(){
		return SUCCESS;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn(
				"label.inboxView.claimNumber", "claimNumber", 10, "String",
				"claimNumber", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn(
                 "label.viewClaim.workOrderNumber", "activeClaimAudit.workOrderNumber", 10,
                 "String"));
		this.tableHeadData.add(new SummaryTableColumn(
                "columnTitle.common.serialNo", "serialNumber", 10,
                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.inboxView.endCustName", "customerName", 20,
                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.claim.dealerClaimNumber", "histClmNo", 10,
                "String"));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.inboxView.removedPartNumber", "removedPartNumber", 10,
                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.inboxView.removedPartDescription", "removedPartDescription", 18,
                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.inboxView.days", "numberOfDaysFromDatePaid", 5,
                "String", SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
		this.tableHeadData.add(new SummaryTableColumn(
                "label.inboxView.claimPaidDate", "activeClaimAudit.payment.activeCreditMemo.creditMemoDate", 8,
                "date"));
		return this.tableHeadData;
	}

	@Override
	protected PageResult<?> getBody() {
		return claimService.getAllAcceptedClaimsMatchingCriteriaForDealer(getCriteria(), getLoggedInUsersDealership());
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

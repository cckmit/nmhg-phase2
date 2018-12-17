package tavant.twms.web.warranty;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyCoverageRequestService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
@SuppressWarnings("serial")
public class ListRequestsForCoverageExtension extends SummaryTableAction {
	
	private static final String ADMIN_VIEW = "AdminView";
	
	private String view;
	
	private WarrantyCoverageRequestService warrantyCoverageRequestService;
			
	@Override
	protected PageResult<?> getBody() {
		if(ADMIN_VIEW.equals(view)){
		return warrantyCoverageRequestService.findPageForAdminPendingRequests(getCriteria());
		} else{
		 return warrantyCoverageRequestService.findPageForDealerRequests(getCriteria(),getLoggedInUser(), getLoggedInUsersDealership());			
		}
	}
	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("Serial Number",
				"inventoryItem.serialNumber", 20, "String","inventoryItem.serialNumber",
				true,false,false,false));
		tableHeadData.add(new SummaryTableColumn("Hours Covered",
				"inventoryItem.hoursOnMachine", 10, "Number"));
        tableHeadData.add(new SummaryTableColumn("Status",
				"status", 20, "String"));
        if(isLoggedInUserAnInternalUser() || isLoggedInUserAnInvAdmin()){
            tableHeadData.add(new SummaryTableColumn("Dealer Name",
				"requestedBy.name", 20, "String"));
        tableHeadData.add(new SummaryTableColumn("Dealer Number",
				"requestedBy.serviceProviderNumber", 10, "String"));
        tableHeadData.add(new SummaryTableColumn("Business Unit",
				"inventoryItem.businessUnitInfo", 10, "String"));
        tableHeadData.add(new SummaryTableColumn("Last Updated Date",
				"updatedOnDate", 10, "Date"));
        }

        return tableHeadData;
	}
	
	public void setWarrantyCoverageRequestService(
			WarrantyCoverageRequestService warrantyCoverageRequestService) {
		this.warrantyCoverageRequestService = warrantyCoverageRequestService;
	}		
	
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
		
	@Override
	protected String getAlias() {
		return "wcr";
	}
	
}

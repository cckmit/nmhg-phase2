/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.campaigns;

import org.springframework.beans.factory.annotation.Required;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentCriteria;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.FieldModUpdateStatus;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.common.SessionUtil;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ListDeniedCampaigns extends SummaryTableAction {

	private Map session;

	private CampaignAssignmentService campaignAssignmentService;

    private OrgService orgService;
	
	@Override
	protected PageResult<?> getBody() {
		List<Organization> dealersWhoseFPICanBeViewed=new ArrayList<Organization>();
		ServiceProvider dealer = getLoggedInUsersDealership();
		String businessUnitName=getLoggedInUser().getBusinessUnits().first().getName();
        if (dealer == null) {
			addFieldError("emptydealership",
					"The logged in user is not a dealer.");
			return null;
		}
    	dealersWhoseFPICanBeViewed.add(dealer);
		dealersWhoseFPICanBeViewed.addAll(orgService.getChildOrganizations(getLoggedInUsersOrganization().getId()));
        return campaignAssignmentService
		.findAllCampaignNotificationRequestsByStatus(createNotificationCriteria(
				dealersWhoseFPICanBeViewed, new PageSpecification(page - 1, pageSize)),FieldModUpdateStatus.REJECTED,isLoggedInUserADealer(),businessUnitName);
	}
	
	public boolean isPageReadOnly() {
		return false;
	}


	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("Serial Number",
				"item.serialNumber", 8, "string"));
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String",
				"id", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.code",
				"campaign.code", 8, "String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.description",
				"campaign.description", 12, "String",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.fromDate",
				"campaign.fromDate", 8, "Date"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.tillDate",
				"campaign.tillDate", 8, "Date"));
		//Below line of code added by Mohan for TKTSA-1524
		tableHeadData.add(new SummaryTableColumn("columnTitle.listRegisteredWarranties.customer_name","item.latestBuyer.name",12,"String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.compaign_class","campaign.campaignClass.description",8,"String","campaign.campaignClass.description",true,false,false,false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.model","item.ofType.model.name",8,"String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.dealer","item.currentOwner.name",12,"String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.field_mod_age","campaign.getFieldModAge()",8,"String",SummaryTableColumnOptions.NO_FILTER));
	    tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.status","campaign.status",11,"String","getDealerUpdateStatuswithReason()"));
	    
		return tableHeadData;
	}

	private CampaignAssignmentCriteria createNotificationCriteria(
			List<Organization> dealership, PageSpecification pageSpecification) {
		CampaignAssignmentCriteria listCriteria = new CampaignAssignmentCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		listCriteria.setPageSpecification(pageSpecification);
		listCriteria.setDealersList(dealership);
		listCriteria.setDealer(getLoggedInUsersDealership());
		return listCriteria;
	}

	private void addSortCriteria(ListCriteria criteria) {
		for (Iterator<String[]> iter = sorts.iterator(); iter.hasNext();) {
			
			String[] sort = iter.next();
			String sortOnColumn=null;
			if(sort[0].equalsIgnoreCase("campaign.getFieldModAge()"))
			{
				sortOnColumn="campaign.fromDate";
			}
			else
			{
			sortOnColumn = sort[0];
			}
			boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		for (Iterator<String> iter = filters.keySet().iterator(); iter
				.hasNext();) {
			String filterName = iter.next();
			String filterValue = filters.get(filterName);
			filterName="campaignNotification."+filterName;
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	@Required
	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

    public OrgService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}


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
package tavant.twms.web.admin.campaign;

import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignRepository;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class ListCampaignsAction extends SummaryTableAction {
    
    private CampaignRepository campaignRepository;

    @Override
    public PageResult<?> getBody() {
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(pageSize);
        pageSpecification.setPageNumber(getPage()-1);
        PageResult<Campaign> pageResult = campaignRepository.findAllCampaigns(
        										"from Campaign campaign", getCriteria());
        return pageResult;
    }

    @Override
    public List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.id",
        		"id", 0, "String", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.campaign.code",
        		"code", 20, "String", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("label.warrantyAdmin.class",
        		"campaignClass.description", 10, "String", false, false, false, false,
        		SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.description",
        		"description", 20, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.status",
        		"status", 10, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.startDate",
        		"fromDate", 9, "Date"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.endDate",
        		"tillDate", 9, "Date"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.total.units.affected",
        		"getCampaignNotifications().size()", 10, "Number",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        tableHeadData.add(new SummaryTableColumn("columnTitle.total.units.fixed",
        		"getTotalSerialNumberFixed()", 8, "Number",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));        
        tableHeadData.add(new SummaryTableColumn("", "imageCol", 4, IMAGE,
				"labelsImg", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        
        return tableHeadData;
    }
    
    public String listActiveCampaigns() {
		try {

			List<Campaign> campaigns = campaignRepository
					.findAllActiveCampaignsWithCodeLike(getSearchPrefix(), 0, 10);
			return generateAndWriteComboboxJson(campaigns, "id", "code");
		} catch (Exception e) {
			// logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}
    

    @Required
    public void setCampaignRepository(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }    
   
    @Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver();
	}
}
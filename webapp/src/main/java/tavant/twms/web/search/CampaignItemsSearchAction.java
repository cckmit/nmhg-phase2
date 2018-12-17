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
package tavant.twms.web.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.util.StringUtils;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.CampaignItemsSearchCriteria;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignStatus;
import tavant.twms.domain.campaign.CampaignStatusService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;

/**
 * 
 */
@SuppressWarnings("serial")
public class CampaignItemsSearchAction extends SummaryTableAction implements
		Preparable {

	private final Logger logger = Logger
			.getLogger(CampaignItemsSearchAction.class);

	private CampaignItemsSearchCriteria searchCriteria = new CampaignItemsSearchCriteria();

	private CampaignAssignmentService campaignAssignmentService;

	public static final String SHOW_SEARCH_PARAM = "show_search_param";

	private List<ServiceProvider> dealers;

	private List<CampaignStatus> statuses;

	private CampaignStatusService campaignStatusService;

	private Campaign campaign;

	private Map session;

	private CampaignNotification campaignNotification;

	public CampaignItemsSearchAction() {
		super();
	}

	@SuppressWarnings("unchecked")
	public String setupSearchView() {
		if (campaign != null) {
			dealers = campaignAssignmentService
					.findAllAssignedDealersForCampaign(campaign);
		}

		statuses = campaignStatusService.findAll();
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		searchCriteria.setPageSpecification(new PageSpecification(
				getPage() - 1, pageSize));
		addFilterCriteria();
		addSortCriteria();

		return campaignAssignmentService
				.findAllItemsMatchingCriteria(searchCriteria);

	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.serialNo",
				"item.serialNumber", 20, "string", "item.serialNumber", true,
				false, false, false));
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.dealerName", "dealership.name", 50,
				"String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.status",
				"notificationStatus", 30, "String"));
		return tableHeadData;
	}

	public String detail() throws IOException, DocumentException,
			ServletException {
		return preview();
	}

	public String preview() throws IOException, DocumentException,
			ServletException {
		if (StringUtils.hasText(getId())) {
			campaignNotification = campaignAssignmentService.findById(new Long(
					getId()));
			return SUCCESS;
		}
		return displayFieldError("emptyserialnumber",
				getText("error.campaign.nonEmptySerialNumber"));
	}

	private void addSortCriteria() {
		for (Iterator<String[]> iter = sorts.iterator(); iter.hasNext();) {
			String[] sort = iter.next();
			String sortOnColumn = sort[0];
			boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
			if (logger.isInfoEnabled()) {
				logger.info("Adding sort criteria " + sortOnColumn + " "
						+ (ascending ? "ascending" : "descending"));
			}
			searchCriteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria() {
		for (Iterator<String> iter = filters.keySet().iterator(); iter
				.hasNext();) {
			String filterName = iter.next();
			String filterValue = filters.get(filterName);
			if (logger.isInfoEnabled()) {
				logger.info("Adding filter criteria " + filterName + " : "
						+ filterValue);
			}
			searchCriteria.addFilterCriteria(filterName, filterValue);
		}
	}

	private String displayFieldError(String fieldName, String errorKey) {
		addFieldError(fieldName, errorKey);
		return INPUT;
	}

	public List<ServiceProvider> getDealers() {
		return dealers;
	}

	public void setDealers(List<ServiceProvider> dealers) {
		this.dealers = dealers;
	}

	public List<CampaignStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<CampaignStatus> statuses) {
		this.statuses = statuses;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public CampaignItemsSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setCampaignItemsSearchCriteria(
			CampaignItemsSearchCriteria campaignItemsSearchCriteria) {
		searchCriteria = campaignItemsSearchCriteria;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public void setCampaignStatusService(
			CampaignStatusService campaignStatusService) {
		this.campaignStatusService = campaignStatusService;
	}

	public Map getSession() {
		return session;
	}

	public void prepare() throws Exception {
		setupSearchView();
	}

	public CampaignNotification getCampaignNotification() {
		return campaignNotification;
	}

	public void setCampaignNotification(
			CampaignNotification campaignNotification) {
		this.campaignNotification = campaignNotification;
	}

	public String saveStatusChange() {
		try {
			campaignAssignmentService.update(campaignNotification);
			addActionMessage("message.itemStatus.updated");
			return SUCCESS;
		} catch (Exception e) {
			String errorMessage = "Exception while trying to save status change : ";
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}
}

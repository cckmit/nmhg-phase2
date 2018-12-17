/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.web.admin.failuretype;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("serial")
public class ManageFailureTypeRootCause extends SummaryTableAction {

	private Logger logger = Logger.getLogger(ManageFailureTypeRootCause.class);
	private ItemGroupService itemGroupService;
	
	private ItemGroup model;
	private String view; // View of the Detail. Whether detail/preview
	
	public static String RESULT_VIEW_DETAIL = "detail";
	
	@Override
	protected PageResult<?> getBody() {
		return itemGroupService.findPageForModels(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.rootCauseFailure.product", "isPartOf.groupCode", 20,
				"string", "isPartOf.name", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.rootCauseFailure.productDescription", "isPartOf.groupCode", 20,
				"string", "isPartOf.name", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.rootCauseFailure.model", "groupCode",
				20, "String"));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.rootCauseFailure.url", "machineUrl",
				50, "String"));

		return this.tableHeadData;
	}
	
	public String update()
	{
		try {
			if (!validMachineUrl(this.model.getMachineUrl()))
			{
				addActionError("label.rootCauseFailure.error");
				findModelForUpdate(this.model.getId());
				return INPUT;
			}
			this.itemGroupService.saveMachineUrlForModel(this.model);
			findModelForUpdate(this.model.getId());
			addActionMessage("label.rootCauseFailure.success");
		} catch (Exception exception) {
			logger.debug("Exception in ManageFailureTypeRootCause.java and exception: " + exception.getMessage());
			addActionError("label.rootCauseFailure.error");
			findModelForUpdate(this.model.getId());
			return INPUT;
		}
		return SUCCESS;
	}
	
	private Boolean validMachineUrl(String machineUrl) {
		Boolean machineUrlValid = Boolean.TRUE;
		machineUrlValid = (machineUrl.indexOf("http://") == -1 ? Boolean.FALSE : Boolean.TRUE) && 
							!machineUrl.equalsIgnoreCase("http://");
		return machineUrlValid;
	}

	// We will be using this for both Preview/Detail views
	public String detail()
	{
		findModelForUpdate();
		return SUCCESS;
	}
	
	private void findModelForUpdate() {
		this.model = this.itemGroupService.findById(Long.parseLong(id));
	}
	
	private void findModelForUpdate(Long modelId) {
		this.model = this.itemGroupService.findById(modelId);
	}
	
	public Boolean isDetailView()
	{
		return RESULT_VIEW_DETAIL.equalsIgnoreCase(getView())? Boolean.TRUE : Boolean.FALSE; 
	}

	public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public ItemGroup getModel() {
		return model;
	}

	public void setModel(ItemGroup model) {
		this.model = model;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
}

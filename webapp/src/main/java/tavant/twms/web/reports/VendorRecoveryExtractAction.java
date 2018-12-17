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
package tavant.twms.web.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.reports.VendorRecoveryListCriteria;
import tavant.twms.domain.reports.VendorRecoveryService;
import tavant.twms.infra.PageResult;
import tavant.twms.security.model.OrgAwareUserDetails;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.domainlanguage.timeutil.Clock;

@SuppressWarnings("serial")
public class VendorRecoveryExtractAction extends SummaryTableAction{

	private static final String SETUP = "setup";

	private VendorRecoveryService vendorRecoveryService;

	private ReportSearchBean reportSearchBean;

	private RecoveryClaimState[] recoveryClaimStates ;

	public String displaySearch(){
		if (this.getReportSearchBean()==null){
			this.reportSearchBean = new ReportSearchBean();
		}
		this.getReportSearchBean().setFromDate(Clock.today().firstOfMonth());
		this.getReportSearchBean().setToDate(Clock.today());
		populateClaimStates();
		return SETUP;
	}

	private void populateClaimStates() {
		recoveryClaimStates = RecoveryClaimState.values();
	}

	public void setVendorRecoveryService(VendorRecoveryService vendorRecoveryService) {
		this.vendorRecoveryService = vendorRecoveryService;
	}

	@Override
	protected PageResult<?> getBody() {
		return vendorRecoveryService.findAllRecoveryClaimsForRange(populateVendorRecoveryListCriteria());
	}

	@Override
	protected Long getListingSize() {
		return vendorRecoveryService.findRecoveryClaimsCountForRange(populateVendorRecoveryListCriteria());
	}

	private VendorRecoveryListCriteria populateVendorRecoveryListCriteria() {
		VendorRecoveryListCriteria criteria = new VendorRecoveryListCriteria();
		criteria.setStartRepairDate(reportSearchBean.getFromDate());
		criteria.setEndRepairDate(reportSearchBean.getToDate().nextDay());
		criteria.setBussinesUnitNames(fetchSelectedBusinessUnits());
		String recClaimStatus = reportSearchBean.getClaimStatus();
		if(!(recClaimStatus == null || recClaimStatus.equalsIgnoreCase("ALL"))){
			criteria.setRecoveryClaimState(reportSearchBean.getClaimStatus());
		}
		criteria.addSortCriteria("recClaimCreatedDate", true);
		criteria.addSortCriteria("id", true);
		//criteria.addSortCriteria("primaryLine", true);
		//criteria.addSortCriteria("replacedPartNumber", true);
		criteria.getPageSpecification().setPageSize(pageSize);
		criteria.getPageSpecification().setPageNumber(getPage());
		return criteria;
	}

	private List<String> fetchSelectedBusinessUnits(){
		User user = getLoggedInUser();
		List<String> buNames = new ArrayList<String>();
		for(BusinessUnit bu : user.getBusinessUnits()) {
			buNames.add(bu.getName());
		}
		return buNames;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		ArrayList<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		if(fetchSelectedBusinessUnits().size() > 1)
			header.add(new SummaryTableColumn("label.common.businessUnitName", "businessUnitInfo", 12, "String"));
		header.add(new SummaryTableColumn("columnTitle.common.claimNo", "claimNumber", 12, "String"));
		header.add(new SummaryTableColumn("label.common.recoveryClaimNumber", "recoveryClaimNumber", 12, "String"));
		header.add(new SummaryTableColumn("label.recoveryClaim.documentNumber", "documentNumber", 12, "String"));
		header.add(new SummaryTableColumn("columnTitle.common.claim.createdDate", "filedOnDate", 12, "date"));
		header.add(new SummaryTableColumn("columnTitle.common.claimType", "claimType", 12, "String"));
		header.add(new SummaryTableColumn("label.recoveryClaim.recoveryClaimState", "recoveryClaimState", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.createdOn","recClaimCreatedDate", 12, "date"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.updatedOn","recClaimUpdatedDate", 12, "date"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.updatedBy","recClaimUpdatedBy", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.lastModifiedOn","recClaimModifiedDate", 12, "date"));
		header.add(new SummaryTableColumn("columnTitle.newClaim.failureDate", "failureDate", 12, "date"));
		header.add(new SummaryTableColumn("columnTitle.newClaim.repairDate", "repairDate", 12, "date"));
		header.add(new SummaryTableColumn("label.common.dealerNumber", "dealerNumber", 12, "String"));
		header.add(new SummaryTableColumn("label.common.dealerName", "dealerName", 12, "String"));
		header.add(new SummaryTableColumn("label.supplierNumber", "supplierNumber", 12, "String"));
		header.add(new SummaryTableColumn("columnTitle.partSource.supplier_name", "supplierName", 12, "String"));
		header.add(new SummaryTableColumn("columnTitle.listContracts.contract_name","contractName", 12, "String"));
		header.add(new SummaryTableColumn("label.common.causalPartNumber", "causalPartNumber", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.partNo", "replacedPartNumber", 12, "String"));
		header.add(new SummaryTableColumn("columnTitle.common.serialNo", "serialNumber", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.modelDesc", "modelDesc", 12, "String"));
		header.add(new SummaryTableColumn("label.retailMachineTransfer.buildDate", "buildDate", 12, "date"));
		header.add(new SummaryTableColumn("label.inventory.invoiceDate", "invoiceDate", 12, "date"));
		header.add(new SummaryTableColumn("columnTitle.inventoryAction.delivery_date", "deliveryDate", 12, "date"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.jobCodesDesc", "jobCode", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.hrsInService","hoursInService", 12, "String"));
		header.add(new SummaryTableColumn("label.common.faultFound","faultFound", 12, "String"));
		header.add(new SummaryTableColumn("label.newClaim.causedBy","causedBy", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.dlrComments","dealerComments", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.processorComments","processorComments", 12, "String"));
		header.add(new SummaryTableColumn("label.common.recoveryComments","recoveryComments", 12, "String"));
		header.add(new SummaryTableColumn("title.attributes.acceptanceReason","recoveryAcceptanceReason", 12, "String"));
		header.add(new SummaryTableColumn("label.common.creditMemoDate","creditMemoDate", 12, "date"));
		header.add(new SummaryTableColumn("label.common.creditMemoNumber","creditMemoNumber", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.supCur","supplierCurrency", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.tkCost","materialCostTotal", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.nonTkCost","nonTkPartsTotal", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.miscPartsCost","materialPartsTotal", 12, "String"));
		header.add(new SummaryTableColumn("label.labor.totalLaborHours","totalLaborHours", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.laborCost","laborCostTotal", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.miscCostTotal","miscCostTotal", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.totalActualCost","totalActualAmt", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.totalContractCost","totalContractAmt", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.actualValueAcceptedAmount","actualValueAcceptedAmount", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.creditMemoAmount","creditMemoAmount", 12, "String"));
		header.add(new SummaryTableColumn("column.creditNote.accepted.currency","creditNoteAcceptedCurr", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.totalWarrantyCost","totalWarrantyAmt", 12, "String"));
		header.add(new SummaryTableColumn("column.title.recovery.extract.dealer.claim.currency","dealerCurrency", 12, "String"));
		return header;
	}

	public ReportSearchBean getReportSearchBean() {
		return reportSearchBean;
	}

	public void setReportSearchBean(ReportSearchBean reportSearchBean) {
		this.reportSearchBean = reportSearchBean;
	}

	public RecoveryClaimState[] getRecoveryClaimStates() {
		if(recoveryClaimStates == null)
			populateClaimStates();
		return recoveryClaimStates;
	}

	public void setRecoveryClaimStates(RecoveryClaimState[] recoveryClaimStates) {
		this.recoveryClaimStates = recoveryClaimStates;
	}


}

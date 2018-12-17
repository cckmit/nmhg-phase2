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
package tavant.twms.domain.download.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MethodNotSupportedException;

import org.springframework.util.StringUtils;

import tavant.twms.domain.download.ReportGenerator;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.download.WarrantyReportGenerator;
import tavant.twms.domain.download.WarrantyReportHelper;

/**
 * @author kuldeep.patil
 *
 */
public class ExtensionsReportGenerator extends WarrantyReportGenerator implements ReportGenerator {

	public String reportContext;
	private static List<String> extensionDataColumnsHeading = new ArrayList<String>(10);
	private static List<String> extensionDataColumns = new ArrayList<String>(10);

	static {
		//Populating the column headers for Customer Report
		extensionDataColumnsHeading.add("Dealer Number");
		extensionDataColumnsHeading.add("Serial Number");
		extensionDataColumnsHeading.add("Item Number");
		extensionDataColumnsHeading.add("Delivery Date");
		extensionDataColumnsHeading.add("Policy Code");
		extensionDataColumnsHeading.add("Policy End Date");
		extensionDataColumnsHeading.add("Hours Covered");
		extensionDataColumnsHeading.add("Months covered by policy");
		
		//Populating the data columns for Customer Report
		extensionDataColumns.add("sp.service_provider_number");
		extensionDataColumns.add("invitm.serial_number");
		extensionDataColumns.add("to_char(itm.item_number) as item_number");
		extensionDataColumns.add("to_char(invitm.delivery_date, 'YYYYMMDD') as delivery_date");
		extensionDataColumns.add("plcydfn.code");
		extensionDataColumns.add("to_char(plcya.till_date, 'YYYYMMDD') as policy_end_date");
		extensionDataColumns.add("plcydfn.service_hrs_covered");
		extensionDataColumns.add("plcydfn.months_frm_delivery as months_covered");
	}
	
	@Override
	protected List<String> getReportColumnHeading(
			ReportSearchBean reportSearchBean)
			throws MethodNotSupportedException {
		return extensionDataColumnsHeading;
	}

	@Override
	public String getReportFileName() throws MethodNotSupportedException {
		return "Pending Requests for Extension";
	}
	
	protected String getReportQuery(ReportSearchBean reportSearchBean) {
		StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(getProjectionClause(reportSearchBean.getDelimiter()));
		buffer.append(prepareWhereClause(reportSearchBean));
		return buffer.toString();
	}

	public String getProjectionClause(String delimiter) {
		return populateSelectClause(extensionDataColumns);
	}

	public String prepareWhereClause(ReportSearchBean reportSearchBean){
		StringBuilder whereClause = new StringBuilder(" FROM service_provider sp, request_wnty_cvg cvg," 
				+" inventory_item invitm, "
				+" item itm , "
				+" policy_definition plcydfn, "
				+" warranty wnty, "
				+" policy plcy, "
				+" policy_audit plcya "
				+" WHERE sp.id = cvg.requested_by "
				+" AND cvg.inventory_item = invitm.id "
				+" AND invitm.of_type = itm.id "
				+" AND invitm.id = wnty.for_item "
				+" AND wnty.id =  plcy.warranty "
				+" AND plcy.policy_definition = plcydfn.id "
				+" AND plcya.for_policy = plcy.id "
				+" AND PLCYDFN.WARRANTY_TYPE != 'POLICY' "
				+" AND cvg.STATUS in ('SUBMITTED','REPLIED') ");
		whereClause.append(whereClauseForDealers(reportSearchBean, "sp"));
		return whereClause.toString();
	}

	protected String whereClauseForDealers(ReportSearchBean reportSearchBean, String alias) {
		if (StringUtils.hasText(reportSearchBean.getDealerNumber()))
			return " AND "+alias+".service_provider_number in (" 
					+ WarrantyReportHelper
							.populateDealerNumberAsCSVForQuery(reportSearchBean
									.getDealerNumber()) + ") ";
		return "";
	}
	
	public String setParametersList() throws MethodNotSupportedException {
		return null;
	}

	protected String getOrderByClause() {
		return " ORDER BY sp.service_provider_number, invitm.serial_number, itm.item_number, plcydfn.code";
	}
	
	protected String getGroupByClause() {
		return "";
	}
	
	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
	}
}

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
package tavant.twms.domain.download.customer;

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
public class CustomerReportGenerator extends WarrantyReportGenerator implements
		ReportGenerator {
	
	public String reportContext;
	private static List<String> customerDataColumnsHeading = new ArrayList<String>(10);
	private static List<String> customerDataColumns = new ArrayList<String>(10);

	static{
		//Populating the column headers for Customer Report
		customerDataColumnsHeading.add("Customer Number");
		customerDataColumnsHeading.add("Company Name");
		customerDataColumnsHeading.add("Address");
		customerDataColumnsHeading.add("City");
		customerDataColumnsHeading.add("State");    
		customerDataColumnsHeading.add("Postal/Zip Code"); 
		customerDataColumnsHeading.add("Country");
		customerDataColumnsHeading.add("Customer Type");
		
		//Populating the data columns for Customer Report
		customerDataColumns.add("cust.customer_id");
		customerDataColumns.add("cust.company_name");
		customerDataColumns.add("addr.address_line1");
		customerDataColumns.add("addr.city");
		customerDataColumns.add("addr.state");
		customerDataColumns.add("addr.zip_code");
		customerDataColumns.add("addr.country");
		customerDataColumns.add("adbook.type");
	}

	@Override
	protected List<String> getReportColumnHeading(ReportSearchBean reportSearchBean)
			throws MethodNotSupportedException {
		return customerDataColumnsHeading;
	}

	@Override
	public String getReportFileName() throws MethodNotSupportedException {
		return "Customer Report";
	}
	
	@Override
	protected String getReportQuery(ReportSearchBean reportSearchBean) {
		StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(getProjectionClause(reportSearchBean.getDelimiter()));
		buffer.append(prepareWhereClause(reportSearchBean));
		return buffer.toString();
	}

	public String getProjectionClause(String delimiter){
		return populateSelectClause(customerDataColumns);
	}

	public String prepareWhereClause(ReportSearchBean reportSearchBean){
		StringBuilder whereClause = new StringBuilder(" FROM customer cust, customer_addresses custaddr," 
				+" address addr, "
				+" address_book_address_mapping addBookMap, "
				+" address_book adBook, "
				+" service_provider sp "
				+" WHERE cust.id = custaddr.customer "
				+" AND custaddr.addresses = addr.id "
				+" AND addbookmap.address_book_id = adbook.id "
				+" AND addbookmap.address_id = addr.id "
				+" AND adbook.belongs_to = sp.id");
		whereClause.append(whereClauseForDealers(reportSearchBean, "sp"));
		whereClause.append(whereClauseForCustomerType(reportSearchBean, "adbook"));
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
	
	protected String whereClauseForCustomerType(ReportSearchBean reportSearchBean, String alias) {
		if (StringUtils.hasText(reportSearchBean.getCustomerType()) && !"ALL".equalsIgnoreCase(reportSearchBean.getCustomerType()))
			return " AND "+alias+".type = '"+reportSearchBean.getCustomerType().toUpperCase()+"'";
		return "";
	}
	
	protected String getOrderByClause() {
		return " ORDER BY adbook.type, cust.company_name";
	}
	
	protected String getGroupByClause() {
		return "";
	}

	public String setParametersList() throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
		"Exception: setParametersList() is not supported for CustomerReportGenerator");
	}

	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
	}

}

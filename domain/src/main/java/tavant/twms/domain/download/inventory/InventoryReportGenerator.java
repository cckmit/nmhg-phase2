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

package tavant.twms.domain.download.inventory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import tavant.twms.domain.download.InventoryReportSearchBean;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.download.WarrantyReportGenerator;
import tavant.twms.domain.download.WarrantyReportHelper;

/**
 * @author jhulfikar.ali
 *
 */
public class InventoryReportGenerator extends WarrantyReportGenerator {

	private String reportContext;
	
	@Override
	public boolean isPaginationSupported() {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext))
			return true;
		return false;
	}

	@Override
	protected List<String> getReportColumnHeading(ReportSearchBean reportSearchBean) {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext))
			return ewpReportColumnsHeading;
		return machineRetailColumnsHeading;
	}

	@Override
	public String getReportFileName() {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext))
			return "Extended Warranty Purchase" ;
		return "Delivery Report Data";
	}

	@Override
	public String getProjectionClause(ReportSearchBean reportSearchBean) {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext))
			return populateSelectClause(ewpReportOuterColumns);
		return populateSelectClause(machineRetailColumns);
	}
	
	@Override
	public String getProjectionClauseForPagination(ReportSearchBean reportSearchBean) {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext))
			return populateSelectClause(ewpReportInnerColumns);
		return populateSelectClause(machineRetailColumns);
	}

	@Override
	public String getFromClause(ReportSearchBean reportSearchBean) {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext)) {
			return " from inventory_item ii, item item, item_group model, inventory_transaction dr," +
					"customer_addresses ca, address addr ";
		}else {
			return MACHINE_RETAIL_REPORT_QUERY_FROM_CLAUSE;
		}
	}
	
	@Override
	public String getWhereClause(ReportSearchBean reportSearchBean) {
		
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext)) {
			InventoryReportSearchBean searchBean = (InventoryReportSearchBean)reportSearchBean;
			StringBuffer buffer = new StringBuffer(" where ii.d_active=1 and ii.type='RETAIL'");
			buffer.append(" and ii.of_type=item.id and item.model=model.id ")
				.append(" and dr.transacted_item=ii.id and dr.id=(select max(id) from inventory_transaction " +
						"where transacted_item=ii.id and inv_transaction_type=2)")
				.append(" and dr.buyer=ca.customer and ca.addresses=addr.id ")
				.append(" and ii.id in ( select i.id ")
				.append(" from inventory_item i, inventory_transaction it")
				.append(",inventory_transaction_type itt,warranty w");
			if(StringUtils.hasText(reportSearchBean.getDealerNumber()))
				buffer.append(",service_provider sp");
			if(InventoryReportSearchBean.CVG_COVERED.equalsIgnoreCase(searchBean.getCoveredOrTerminated())) {
				buffer.append(",policy p,policy_definition pd");
			}else if(InventoryReportSearchBean.CVG_TERMINATED.equalsIgnoreCase(searchBean.getCoveredOrTerminated())) {
				buffer.append(",policy p,policy_definition pd,policy_audit pa");
			}
			
			buffer.append(" where i.d_active=1 and i.type='RETAIL' ")
				.append(whereClauseForBusinessUnits(reportSearchBean,"i"))
				.append(" and i.id=it.transacted_item and it.inv_transaction_type=itt.id ")
				.append(" and itt.trnx_type_key in ('DR','DR_MODIFY','DR_DELETE','DR_RENTAL','RMT')")
				.append(" and i.id=w.for_item");
			if (!reportSearchBean.isAllDealerSelected() 
					&& StringUtils.hasText(reportSearchBean.getDealerNumber())) {
				buffer.append(" and sp.id=it.owner_ship")
				.append(" and sp.service_provider_number in (")
				.append(WarrantyReportHelper.populateDealerNumberAsCSVForQuery(
						reportSearchBean.getDealerNumber()))
				.append(") "); 
			}
			if(searchBean.getStartWindowPeriodFromDeliveryDate() != null) {
				buffer.append(" and add_months(i.delivery_date,")
				.append(searchBean.getStartWindowPeriodFromDeliveryDate())
				.append(") <= sysdate ");
			}
			if(searchBean.getEndWindowPeriodFromDeliveryDate() != null) {
				buffer.append(" and add_months(i.delivery_date,")
				.append(searchBean.getEndWindowPeriodFromDeliveryDate())
				.append(") > sysdate ");
			}
			
			String selectedPlans = "";
			if(!searchBean.isAllExtendedPlansSelected() && searchBean.getPolicyDefinitionIds().size()>0) {
				selectedPlans = " and pd.id in (" +
					WarrantyReportHelper.commaSeparatedIdsForQuery(searchBean.getPolicyDefinitionIds()) +
					") ";
			}
			
			if(InventoryReportSearchBean.CVG_COVERED.equalsIgnoreCase(searchBean.getCoveredOrTerminated())) {
				buffer.append(" and w.id=p.warranty and p.policy_definition=pd.id")
				.append(" and pd.warranty_type='EXTENDED'")
				.append(selectedPlans);
			}else if(InventoryReportSearchBean.CVG_NOT_COVERED.equalsIgnoreCase(searchBean.getCoveredOrTerminated())) {
				buffer.append(" and (select count(p.id) from policy p,policy_definition pd")
				.append(" where w.id=p.warranty and p.policy_definition=pd.id")
				.append(selectedPlans)
				.append(" and pd.warranty_type='EXTENDED')=0 ");
			}else {
				buffer.append(" and w.id=p.warranty and p.policy_definition=pd.id")
				.append(" and pd.warranty_type='EXTENDED' and p.id= pa.for_policy")
				.append(selectedPlans)
				.append(" and pa.status='Terminated' and pa.created_on=")
				.append("  (select max(t.created_on) from policy_audit t where t.for_policy=p.id) ");
			}
			buffer.append(" ) ");
			if(searchBean.getSelectedProducts() != null && searchBean.getSelectedProducts().size()>0) {
				buffer.append(" and (item.model in (")
				.append(WarrantyReportHelper.commaSeparatedIdsForQuery(searchBean.getSelectedProducts()))
				.append(") or item.product in (")
				.append(WarrantyReportHelper.commaSeparatedIdsForQuery(searchBean.getSelectedProducts()))
				.append(")) ");
			}
			return buffer.toString();
		}else {
			StringBuffer buffer = new StringBuffer(MACHINE_RETAIL_QUERY_BASIC_WHERE_CLAUSE);
			buffer.append(betweenClauseForDateColumn(reportSearchBean,"it.transaction_date"))
			.append(" and lower(ii.business_unit_info) = '" + 
					reportSearchBean.getBusinessUnitName().toLowerCase() + SQL_APPEND_STR);
			return buffer.toString();
		}
	}
	
	@Override
	public String getOrderByClauseForPagination(ReportSearchBean reportSearchBean) {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext)) {
			return " order by ii.id ";
		}else {
			return "";
		}
	}
	
	@Override
	public String getOrderByClause() {
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(reportContext)) {
			return " order by serial_number ";
		}else {
			return "";
		}
	}

	public static final String EWP_REPORT_FROM_CLAUSE = 
		" from inventory_item ii, inventory_transaction it ";
	
	public static final String EWP_REPORT_WHERE_CLAUSE = 
		" where ii.d_active=1 and ii.type='RETAIL'" +
		" and ii.id=it.transacted_item" +
		" and it.id = (select max(it1.id) from inventory_transaction it1,inventory_transaction_type it2" +
		"	where it1.transacted_item=ii.id and it1.inv_transaction_type=it2.id" +
		"	and it2.trnx_type_key in ('DR','DR_MODIFY','DR_DELETE','DR_RENTAL','RMT'))";
	
	public static final String MACHINE_RETAIL_REPORT_QUERY_FROM_CLAUSE = 
		" from inventory_item ii, party buyer, dealership dealer, party dealerparty, "
		+ " address dealeraddress, service_provider servicedealer, warranty wnty, "
		+ " inventory_transaction it, inventory_transaction_type itt, "
		+ " item item, item_group model, item_group product ";
	
	public static final String MACHINE_RETAIL_QUERY_BASIC_WHERE_CLAUSE = 
		" where ii.latest_buyer = buyer.id and ii.current_owner = dealer.id and  "
		+ " ii.current_owner = dealerparty.id and dealerparty.address = dealeraddress.id and "
		+ " ii.current_owner = servicedealer.id and wnty.for_transaction = it.id and "
		+ " it.transacted_item = ii.id and itt.trnx_type_key = 'DR' and it.inv_transaction_type = itt.id and "
		+ " ii.of_type = item.id and item.model = model.id and item.product = product.id ";

	private static List<String> machineRetailColumns = new ArrayList<String>(10);
	
	private static List<String> machineRetailColumnsHeading = new ArrayList<String>(10);
	
	private static List<String> ewpReportOuterColumns = new ArrayList<String>(10);
	
	private static List<String> ewpReportInnerColumns = new ArrayList<String>(10);
	
	private static List<String> ewpReportColumnsHeading = new ArrayList<String>(10);
	
	static {
		machineRetailColumns.add("ii.serial_number");
		machineRetailColumns.add("model.name");
		machineRetailColumns.add("product.name");
		machineRetailColumns.add("dealer.dealer_number");
		machineRetailColumns.add("dealerparty.name");
		machineRetailColumns.add("dealeraddress.country");
		machineRetailColumns.add("buyer.name");
		machineRetailColumns.add("ii.delivery_date");
		machineRetailColumns.add("it.transaction_date");
		machineRetailColumns.add("ii.wnty_end_date");
		machineRetailColumns.add("item.item_type");
		machineRetailColumns.add("decode (ii.condition_type, 'SCRAP', 'SCRAPPED', upper(ii.condition_type))");
		
		machineRetailColumnsHeading.add("Serial Number");
		machineRetailColumnsHeading.add("Model");
		machineRetailColumnsHeading.add("Product Type");
		machineRetailColumnsHeading.add("Dealer Number");
		machineRetailColumnsHeading.add("Dealer");
		machineRetailColumnsHeading.add("Dealer Country");
		machineRetailColumnsHeading.add("Customer");
		machineRetailColumnsHeading.add("Delivery Date");
		machineRetailColumnsHeading.add("Receiving Date");
		machineRetailColumnsHeading.add("Warranty Expiry Date");
		machineRetailColumnsHeading.add("Equipment Type");
		machineRetailColumnsHeading.add("New/Used");
		
		ewpReportInnerColumns.add("ii.serial_number");
		ewpReportOuterColumns.add("serial_number");
		ewpReportColumnsHeading.add("Serial Number");
		
		ewpReportInnerColumns.add("ii.delivery_date");
		ewpReportOuterColumns.add("delivery_date");
		ewpReportColumnsHeading.add("Delivery Date");
		
		ewpReportInnerColumns.add("model.description model_desc");
		ewpReportOuterColumns.add("model_desc");
		ewpReportColumnsHeading.add("Model");
		
		ewpReportInnerColumns.add("ii.wnty_end_date");
		ewpReportOuterColumns.add("wnty_end_date");
		ewpReportColumnsHeading.add("Warranty End Date");
		
		ewpReportInnerColumns.add("dr.owner_ship owner");
		ewpReportOuterColumns.add("(select name from party where id=owner)");
		ewpReportColumnsHeading.add("Dealer Name");
		ewpReportOuterColumns.add("(select service_provider_number from service_provider where id=owner)");
		ewpReportColumnsHeading.add("Dealer Number");
		
		ewpReportInnerColumns.add("(select name from party where id=dr.buyer) cust_name");
		ewpReportOuterColumns.add("cust_name");
		ewpReportColumnsHeading.add("End Customer Name");
		
		ewpReportInnerColumns.add("addr.address_line1");
		ewpReportOuterColumns.add("address_line1");
		ewpReportColumnsHeading.add("End Customer Address 1");
		
		ewpReportInnerColumns.add("addr.address_line2");
		ewpReportOuterColumns.add("address_line2");
		ewpReportColumnsHeading.add("End Customer Address 2");
		
		ewpReportInnerColumns.add("addr.country");
		ewpReportOuterColumns.add("country");
		ewpReportColumnsHeading.add("End Customer Country");
		
		ewpReportInnerColumns.add("addr.state");
		ewpReportOuterColumns.add("state");
		ewpReportColumnsHeading.add("End Customer State");
		
		ewpReportInnerColumns.add("addr.city");
		ewpReportOuterColumns.add("city");
		ewpReportColumnsHeading.add("End Customer City");
		
		ewpReportInnerColumns.add("addr.contact_person_name");
		ewpReportOuterColumns.add("contact_person_name");
		ewpReportColumnsHeading.add("End Customer Contact");
		
		ewpReportInnerColumns.add("addr.email");
		ewpReportOuterColumns.add("email");
		ewpReportColumnsHeading.add("End Customer Email");
		
		ewpReportInnerColumns.add("addr.phone");
		ewpReportOuterColumns.add("phone");
		ewpReportColumnsHeading.add("End Customer Phone");
		
		ewpReportInnerColumns.add("addr.fax");
		ewpReportOuterColumns.add("fax");
		ewpReportColumnsHeading.add("End Customer Fax");
	}

	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
	}

}

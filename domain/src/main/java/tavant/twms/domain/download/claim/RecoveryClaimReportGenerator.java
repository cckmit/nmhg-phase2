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

package tavant.twms.domain.download.claim;

import org.springframework.util.StringUtils;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.download.WarrantyReportGenerator;
import tavant.twms.domain.download.WarrantyReportHelper;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import javax.mail.MethodNotSupportedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jhulfikar.ali
 *
 */
public class RecoveryClaimReportGenerator extends WarrantyReportGenerator {

	private ConfigParamService configParamService;

	private String reportContext;
	private boolean partsReplacedInstalledSectionVisible = true;

	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	@Override
	public boolean isPaginationSupported() {
		return true;
	}
	
	public boolean isPartsReplacedInstalledSectionVisible() {
        return partsReplacedInstalledSectionVisible;
    }

    public void setPartsReplacedInstalledSectionVisible(boolean partsReplacedInstalledSectionVisible) {
        this.partsReplacedInstalledSectionVisible = partsReplacedInstalledSectionVisible;
    }

	@Override
	public String getProjectionClause(ReportSearchBean reportSearchBean) {
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext())) {
			return new StringBuffer(populateSelectClause(suppRecoveryClaimOuterColumns))
					.toString();
		}else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext())) {
			return new StringBuffer(populateSelectClause(suppRecoveryPartsOuterColumns))
			.toString();
		}
		return "";
	}

	@Override
	public String getProjectionClauseForPagination(ReportSearchBean reportSearchBean) {
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext())) {
			return new StringBuffer(populateSelectClause(suppRecoveryClaimInnerColumns))
					.toString();
		}else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext())) {
			return new StringBuffer(populateSelectClause(
					getSuppRecoveryPartsInnerColumns(reportSearchBean)))
			.toString();
		}

		return populateSelectClauseWithDelimiter(underWriterColumns, reportSearchBean.getDelimiter());
	}

	@Override
	public String getOrderByClauseForPagination(ReportSearchBean reportSearchBean) {
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(reportContext))
			return " ORDER BY rec_claim_created_on, recovery_id, is_primary, replaced_part_number ";
		else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(reportContext))
			return " ORDER BY r.d_created_on, r.id , i.item_number ";
		return "";
	}

	@Override
	protected List<String> getReportColumnHeading(ReportSearchBean reportSearchBean) {
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext()))
			return getSuppRecoveryClaimColumnsHeading(reportSearchBean);
		if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext()))
			return suppRecoveryPartsColumnsHeading;
		return underWriterColumnsHeading;
	}

	@Override
	public String getReportFileName() {
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext()))
			return "Vendor Recovery Extract";
		else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext()))
			return "Supplier Recovery Parts Report";
		return "Underwriter Claim Report"; // by default it is Underwriter Claim report
	}


	protected String whereClauseForSubmitUpdateDate(ReportSearchBean reportSearchBean, String alias, String submitDateCol, String updatedDateCol) {
		if("submitDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate()))
			return betweenClauseForDateColumn(reportSearchBean, alias+"."+submitDateCol);
		if("updateDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate()))
			return betweenClauseForDateColumn(reportSearchBean, alias+"."+updatedDateCol);
		return "";
	}

	protected String whereClauseForDealers(ReportSearchBean reportSearchBean, String alias) {
		if (StringUtils.hasText(reportSearchBean.getDealerNumber()))
			return " AND "+alias+".supplier_number in ("
					+ WarrantyReportHelper
							.populateDealerNumberAsCSVForQuery(reportSearchBean
									.getDealerNumber()) + ") ";
		return "";
	}

	protected String whereClauseForBusinessUnits(ReportSearchBean reportSearchBean, String alias, String column) {
		StringBuffer buff = new StringBuffer();
		buff.append(" and "+alias+"."+column+" in (" +
				WarrantyReportHelper.populateBusinessUnitAsCSVForQuery(reportSearchBean.getBusinessUnitName()) + ") ");
		return buff.toString();
	}

	protected String whereClauseForStatus(ReportSearchBean reportSearchBean, String alias) {
		if (StringUtils.hasText(reportSearchBean.getClaimStatus())
				&& !reportSearchBean.getClaimStatus().equalsIgnoreCase("All")) {
			StringBuffer buff = new StringBuffer();
			buff.append(" AND ").append(alias).append(".recovery_claim_state = '")
			.append(reportSearchBean.getClaimStatus()).append("' ");
			return buff.toString();
		}
		return "";
	}


	private String prepareWhereClauseForVendorRecoveryExtract(ReportSearchBean reportSearchBean) {
		StringBuffer filter = new StringBuffer(" ")
			.append(whereClauseForBusinessUnits(reportSearchBean, "r", "business_unit_info"))
			.append(whereClauseForSubmitUpdateDate(reportSearchBean, "r", "d_created_on", "d_updated_on"))
			.append(whereClauseForStatus(reportSearchBean, "r"))
			.append(whereClauseForDealers(reportSearchBean, "sup"));
		StringBuffer buffer = new StringBuffer(" FROM ( ")
			.append(prepareOemPartsClauseForVRExtract(filter.toString(),reportSearchBean.getBusinessUnitName()))
			.append(" UNION ALL ")
			.append(prepareNonOemPartsClauseForVRExtract(filter.toString()))
			.append(" UNION ALL ")
			.append(prepareMiscPartsClauseForVRExtract(filter.toString()))
			.append(" UNION ALL ")
			.append(prepareNoPartsClauseForVRExtract(filter.toString()))
			.append(" )t ");
		return buffer.toString();
	}

	@Override
	public String getFromClause(ReportSearchBean reportSearchBean) {
		partsReplacedInstalledSectionVisible = configParamService.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName());
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext())) {
			return prepareWhereClauseForVendorRecoveryExtract(reportSearchBean);
		}else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext())) {
			StringBuffer buffer;
			buffer = new StringBuffer(" from claim c,claim_audit ca,recovery_claim r,contract ct,supplier s,party p,service_information si,oem_part_replaced opr, item i, uom_mappings um, ");
			if(partsReplacedInstalledSectionVisible)
			{
			buffer.append("huss_parts_replaced_installed hpri");
			}
			else
			{
				buffer.append("service_oemparts_replaced sopr");	
			}
			return buffer.toString();
		}else {
			StringBuffer buffer = new StringBuffer(CLAIM_REPORT_QUERY_FROM_CLAUSE)
				.append(MODEL_PRODUCT_DATA_FROM_CLAUSE)
				.append(CLAIM_REPORT_FAILURE_INFO_FROM_CLAUSE)
				.append(CLAIM_REPORT_QUERY_PART_FROM_CLAUSE);
			return buffer.toString();
		}
	}

	/*@Override
	public String getWhereClause(ReportSearchBean reportSearchBean) {
			
		if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext())) {
			return "";
		}else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext())) {
			StringBuffer buffer;
			buffer = new StringBuffer(
			" where c.id=r.claim and r.contract=ct.id and ct.supplier=s.id and s.id=p.id and c.service_information=si.id")
	          .append("  and opr.item_ref_item=i.id and opr.uom_mapping=um.id(+) ");
			if(partsReplacedInstalledSectionVisible)
			{
			 
			buffer.append(" and si.service_detail=hpri.service_detail and hpri.id=opr.oem_replaced_parts ");
							
			}else
			{
			
		buffer.append("and si.service_detail=sopr.service and sopr.oemparts_replaced=opr.id");
			}
		buffer.append(whereClauseForBusinessUnits(reportSearchBean, "c"));
		buffer.append(whereClauseForSubmitUpdateDate(reportSearchBean, "r", "d_created_on", "d_updated_on"));
		buffer.append(whereClauseForStatus(reportSearchBean, "r"));
		buffer.append(whereClauseForDealers(reportSearchBean, "s"));	
			return buffer.toString();
		}else {
			StringBuffer buffer = new StringBuffer(CLAIM_REPORT_QUERY_BASIC_WHERE_CLAUSE)
				.append(MODEL_PRODUCT_DATA_WHERE_CLAUSE)
				.append(CLAIM_REPORT_FAILURE_INFO_WHERE_CLAUSE)
				.append(CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE)
				.append(whereClauseForUnderwriterClaimReporting(reportSearchBean));
			return buffer.toString();
		}
	}

	*/
	
	
	
	 @Override
	    public String getWhereClause(ReportSearchBean reportSearchBean) {
	            
	        if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getReportContext())) {
	            return "";
	        }else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getReportContext())) {
	            StringBuffer buffer;
	            buffer = new StringBuffer(
	            " where c.id=r.claim and r.contract=ct.id and ct.supplier=s.id and s.id=p.id and ca.service_information=si.id")
	              .append("  and opr.item_ref_item=i.id and opr.uom_mapping=um.id(+) ");
	            if(partsReplacedInstalledSectionVisible)
	            {
	             
	            buffer.append(" and si.service_detail=hpri.service_detail and hpri.id=opr.oem_replaced_parts ");
	                            
	            }else
	            {
	            
	        buffer.append("and si.service_detail=sopr.service and sopr.oemparts_replaced=opr.id");
	            }
	        buffer.append(whereClauseForBusinessUnits(reportSearchBean, "c"));
	        buffer.append(whereClauseForSubmitUpdateDate(reportSearchBean, "r", "d_created_on", "d_updated_on"));
	        buffer.append(whereClauseForStatus(reportSearchBean, "r"));
	        buffer.append(whereClauseForDealers(reportSearchBean, "s"));    
	            return buffer.toString();
	        }else {
	            StringBuffer buffer = new StringBuffer(CLAIM_REPORT_QUERY_BASIC_WHERE_CLAUSE)
	                .append(MODEL_PRODUCT_DATA_WHERE_CLAUSE)
	                .append(CLAIM_REPORT_FAILURE_INFO_WHERE_CLAUSE)
	                .append(CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE)
	                .append(whereClauseForUnderwriterClaimReporting(reportSearchBean));
	            return buffer.toString();
	        }
	    }
	private static String prepareCommonPartsClauseForVRExtract() {
		String sql =
			" r.id recovery_id," +
			" r.recovery_claim_state," +
			" r.d_created_on rec_claim_created_on," +
			" r.d_updated_on rec_claim_updated_on," +
			" r.CONTRACT contract_id," +
			" r.BUSINESS_UNIT_INFO," +
			" c.id claim_id," +
			" c.claim_number," +
			" c.clm_type_name claim_type," +
			" c.failure_date," +
			" c.repair_date," +
			" c.for_dealer," +
			" c.external_comment," +
			" c.internal_comment," +
			" s.causal_part," +
			" s.service_detail," +
			" s.fault_found," +
			" s.CAUSED_BY," +
			" sup.id supplier_id," +
			" sup.supplier_number supplier_number," +
			" (select preferred_currency from organization" +
			"    where id =(select supplier from contract where id = r.contract)) supplier_currency," +
			" (select max(id) from claim_audit where for_claim=c.id and previous_state='ACCEPTED') accepted_audit," ;
		return sql;
	}

	private boolean useMaterialCost(String businessUnit) {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnit);
		String costPriceType =
			configParamService.getStringValue(ConfigName.COST_PRICE_CONFIGURATION.getName());
		boolean useMaterialCost = false;
		if(CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType))
			useMaterialCost = true;
		return useMaterialCost;
	}

	private String prepareOemPartsClauseForVRExtract(String filter, String businessUnit) {
		boolean useMaterialCost = useMaterialCost(businessUnit);
		String sql =
			" select " +
				prepareCommonPartsClauseForVRExtract() +
				" case when (h.primary_part_replaced=0 and opr.item_ref_item=s.causal_part)" +
				"    or h.primary_part_replaced=opr.id then 1 else 0 end is_primary," +
				" 'Y' is_ir_part," +
				" case when opr.item_ref_item=s.causal_part then 'Y' else 'N' end is_causal_part," +
				" i.item_number replaced_part_number," +
				" i.description replaced_part_desc," +
				" opr.number_of_units replaced_part_qty," +
				" case when um.id is null then i.uom else um.mapped_uom end uom," +
				" case when um.id is null then opr."+
					(useMaterialCost ? "material_cost_amt" : "cost_price_per_unit_amt") +
					" else opr."+(useMaterialCost ? "material_cost_amt" : "cost_price_per_unit_amt") +
					"/um.mapping_fraction end part_unit_cost," +
				" opr.material_cost_curr unit_cost_curr" +
			" from claim c,recovery_claim r,contract ct,supplier sup,service_information s," +
				(isPartsReplacedInstalledSectionVisible() ? 
						" huss_parts_replaced_installed hpr" : " service_oemparts_replaced sopr") +
				", oem_part_replaced opr," +
				" uom_mappings um, item i, vendor_recovery_extract_helper h" +
			" where c.id=r.claim and c.service_information=s.id" +
				" and r.contract = ct.id and ct.supplier=sup.id" +
				(isPartsReplacedInstalledSectionVisible() ?
					" and s.service_detail= hpr.service_detail and hpr.id=opr.oem_replaced_parts" :
					" and s.service_detail= sopr.service and sopr.oemparts_replaced=opr.id") +
				" and opr.uom_mapping=um.id(+) and opr.item_ref_item=i.id" +
				" and r.id=h.recovery_claim" +
				filter ;
		return sql;
	}

	private String prepareNonOemPartsClauseForVRExtract(String filter) {
		String sql =
			" select " +
				prepareCommonPartsClauseForVRExtract() +
				" case when h.primary_part_replaced=opr.id then 1 else 0 end is_primary," +
				" 'N' is_ir_part," +
				" 'N' is_causal_part," +
				" null replaced_part_number," +
				" opr.description replaced_part_desc," +
				" opr.number_of_units replaced_part_qty," +
				" 'EACH' uom," +
				" opr.price_per_unit_amt part_unit_cost," +
				" opr.price_per_unit_curr unit_cost_curr" +
			" from claim c,recovery_claim r,contract ct,supplier sup,service_information s," +
				" service_nonoemparts_replaced sopr, non_oem_part_replaced opr," +
				" vendor_recovery_extract_helper h" +
			" where c.id=r.claim and c.service_information=s.id" +
				" and r.contract = ct.id and ct.supplier=sup.id" +
				" and s.service_detail= sopr.service and sopr.nonoemparts_replaced=opr.id" +
				" and r.id=h.recovery_claim"+
				filter;
		return sql;
	}

	private String prepareMiscPartsClauseForVRExtract(String filter) {
		String sql =
			"select " +
				prepareCommonPartsClauseForVRExtract() +
				" case when h.primary_part_replaced=opr.id then 1 else 0 end is_primary," +
				" 'N' is_ir_part," +
				" 'N' is_causal_part," +
				" mi.part_number replaced_part_number," +
				" mi.description replaced_part_desc," +
				" opr.number_of_units replaced_part_qty," +
				" 'EACH' uom," +
				" opr.price_per_unit_amt part_unit_cost," +
				" opr.price_per_unit_curr unit_cost_curr" +
			" from claim c,recovery_claim r,contract ct,supplier sup,service_information s," +
				" service_misc_parts_replaced sopr, non_oem_part_replaced opr," +
				" misc_item_config mic, misc_item mi," +
				" vendor_recovery_extract_helper h" +
			" where c.id=r.claim and c.service_information=s.id" +
				" and r.contract = ct.id and ct.supplier=sup.id" +
				" and s.service_detail= sopr.service and sopr.misc_parts_replaced=opr.id" +
				" and opr.misc_item_config=mic.id and mic.miscellaneous_item=mi.id" +
				" and r.id=h.recovery_claim"+
				filter;
		return sql;
	}

	private String prepareNoPartsClauseForVRExtract(String filter) {
		String sql =
			"select " +
				prepareCommonPartsClauseForVRExtract() +
				" 1 is_primary," +
				" 'N' is_ir_part," +
				" 'N' is_causal_part," +
				" null replaced_part_number," +
				" null replaced_part_desc," +
				" 0 replaced_part_qty," +
				" null uom," +
				" null part_unit_cost," +
				" null unit_cost_curr" +
			" from claim c,recovery_claim r,contract ct,supplier sup,service_information s," +
				" vendor_recovery_extract_helper h" +
			" where c.id=r.claim and c.service_information=s.id" +
				" and r.contract = ct.id and ct.supplier=sup.id" +
				" and r.id=h.recovery_claim and h.primary_part_replaced is null"+
				filter;
		 return sql;
	}

	private String whereClauseForUnderwriterClaimReporting(ReportSearchBean reportSearchBean) {
		return betweenClauseForDateColumn(reportSearchBean, "cmem.credit_memo_date");
	}

	public static final String CLAIM_REPORT_QUERY_FROM_CLAUSE =
		" from claim cl, service_information si, item it, dealership dealer, "
		+ " payment pay, credit_memo cmem, claimed_item ci, inventory_item ii, "
		+ " applicable_policy ap, policy pol, policy_definition pd ";

	public static final String CLAIM_REPORT_FAILURE_INFO_FROM_CLAUSE =
		" , Failure_Type_Definition ftd, fault_code fc ";

	public static final String CLAIM_REPORT_QUERY_PART_FROM_CLAUSE =
		" , line_item_groups ligs, Line_Item_Group lig, current_part_info cpi, Part_Payment_Info ppi ";

	public static final String CLAIM_REPORT_QUERY_BASIC_WHERE_CLAUSE =
		" where cl.service_information (+) = si.id and si.causal_part (+) = it.id and "
		+ " cl.for_dealer = dealer.id and upper(cl.state) NOT IN ('DRAFT ', 'DRAFT_DELETED', 'DELETED') and "
		+ " pay.ACTIVE_CREDIT_MEMO (+) = cmem.id and cl.payment = pay.id and "
		+ " ci.claim = cl.id and ci.ITEM_REF_INV_ITEM = ii.id and "
		+ " ci.applicable_policy (+) = ap.id and ap.registered_policy (+) = pol.id "
		+ " and pol.policy_definition (+) = pd.id ";

	public static final String CLAIM_REPORT_FAILURE_INFO_WHERE_CLAUSE =
		" and si.fault_code_ref = fc.id and si.fault_found = ftd.id (+) ";

	public static final String CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE =
		" and ligs.for_payment (+) = pay.id and ligs.line_item_groups = lig.id (+) " +
		" and cpi.line_item_group (+) = lig.id and cpi.current_part_payment_info = ppi.id (+) ";

	public static final String MODEL_PRODUCT_DATA_WHERE_CLAUSE =
		" and ii.current_owner = dealerparty.id and dealerparty.address = dealeraddress.id and "
		+ " ii.of_type = item.id and item.model = model.id and item.product = product.id ";

	public static final String MODEL_PRODUCT_DATA_FROM_CLAUSE =
		", party dealerparty, address dealeraddress,  item item, item_group model, item_group product ";

	public static final String SUPPLIER_RECOVERY_DATA_WHERE_CLAUSE =
		" and si.caused_by = cb.id (+) and rc.claim = cl.id and " +
		" rc.contract (+) = con.id and con.supplier = supparty.id ";

	public static final String SUPPLIER_RECOVERY_DATA_FROM_CLAUSE =
		", failure_cause_definition cb, recovery_claim rc, contract con, party supparty ";

	private static List<String> underWriterColumns = new ArrayList<String>(10);

	private static List<String> underWriterColumnsHeading = new ArrayList<String>(10);

	private static List<String> suppRecoveryClaimOuterColumns = new ArrayList<String>(10);

	private static List<String> suppRecoveryClaimInnerColumns = new ArrayList<String>(10);

	//private static List<String> suppRecoveryClaimColumnsHeading = new ArrayList<String>(10);

	//private static List<String> suppRecoveryPartsInnerColumns = new ArrayList<String>(10);

	private static List<String> suppRecoveryPartsColumnsHeading = new ArrayList<String>(10);

	private static List<String> suppRecoveryPartsOuterColumns = new ArrayList<String>(10);

	static {
		// Populate list Data for Underwriter Report Data Reporting
		underWriterColumns.add("cl.claim_number "); // cl.claim_number 'Claim Number'
		underWriterColumns.add("(select count(id) claim_sequence from claim_audit " +
				" where for_claim = cl.id and multi_claim_maintenance=0)" );
		underWriterColumns.add("ii.serial_number "); // ii.serial_number 'Serial Number'
		underWriterColumns.add("model.name"); // 'Model Number'
		underWriterColumns.add("product.name"); // 'Product Type'
		underWriterColumns.add("dealer.dealer_number"); // 'Dealer Number'
		underWriterColumns.add("dealerparty.name"); // 'Dealer'
		underWriterColumns.add("cl.state "); // 'External Claim Status'
		underWriterColumns.add("ii.delivery_date "); // ii.delivery_date 'Delivery Date'
		underWriterColumns.add("cl.failure_date "); // cl.failure_date 'Failure Date'
		underWriterColumns.add("cmem.credit_memo_date "); // cmem.credit_memo_date 'Acceptance Date'
		underWriterColumns.add("it.item_number "); // it.item_number 'Causal Part Number'
		underWriterColumns.add("si.fault_code "); // si.fault_code 'Failure Group'
		underWriterColumns.add("NVL(replace((select (select name from Assembly_Definition where id in " +
				" (select components from fault_code_def_comps where fault_code_definition= fc.definition) " +
				" and assembly_level =1) || '-' || (select name from Assembly_Definition where id in " +
				" (select components from fault_code_def_comps where fault_code_definition=fc.definition) " +
				" and assembly_level =2) || '-' || (select name from Assembly_Definition where id in " +
				" (select components from fault_code_def_comps where fault_code_definition=fc.definition) " +
				" and assembly_level =3) || '-' || (select name from Assembly_Definition where id in " +
				" (select components from fault_code_def_comps where fault_code_definition=fc.definition) " +
				" and assembly_level =4) from dual), '---',''), '-')"); // 'Failure Code'
		underWriterColumns.add("ftd.description "); // 'Failure Type'
		underWriterColumns.add("(select accepted_amt from Line_Item_Group where " +
				" name in ('Oem Parts','Non Oem Parts') and id = ligs.line_item_groups) "); // 'Part Amount Accepted'
		underWriterColumns.add("(select accepted_amt from Line_Item_Group where " +
				" name in ('Labor') and id = ligs.line_item_groups) "); // 'Labor Amount Accepted'
		underWriterColumns.add("(select accepted_amt from Line_Item_Group where " +
				" name in ('Travel By Distance', 'Travel by Hours', 'Travel By Trip') and id = ligs.line_item_groups) "); // 'Travel Amount Accepted'
		underWriterColumns.add("cmem.paid_amount_curr "); // cmem.paid_amount_curr 'Currency'
		underWriterColumns.add("cl.other_comments "); // cl.other_comments 'Remarks'
		//underWriterColumns.add(""); // 'Input Code'
		underWriterColumns.add("pd.warranty_type"); // pd.warranty_type 'Warranty Extension Code'
		underWriterColumns.add("dealeraddress.country"); // 'Country'

		// Populate header list Data for Machine Retail/Delivery Report Data Reporting
		underWriterColumnsHeading.add("Claim Number");
		underWriterColumnsHeading.add("Sequence Number");
		underWriterColumnsHeading.add("Serial Number");
		underWriterColumnsHeading.add("Model Number");
		underWriterColumnsHeading.add("Product Type");
		underWriterColumnsHeading.add("Dealer Number");
		underWriterColumnsHeading.add("Dealer");
		underWriterColumnsHeading.add("External Claim Status");
		underWriterColumnsHeading.add("Delivery Date");
		underWriterColumnsHeading.add("Failure Date");
		underWriterColumnsHeading.add("Acceptance Date");
		underWriterColumnsHeading.add("Causal Part Number");
		underWriterColumnsHeading.add("Failure Group");
		underWriterColumnsHeading.add("Failure Code");
		underWriterColumnsHeading.add("Failure Type");
		underWriterColumnsHeading.add("Part Amount Accepted");
		underWriterColumnsHeading.add("Labor Amount Accepted");
		underWriterColumnsHeading.add("Travel Amount Accepted");
		underWriterColumnsHeading.add("Currency");
		underWriterColumnsHeading.add("Remarks");
		underWriterColumnsHeading.add("Input Code");
		underWriterColumnsHeading.add("Warranty Extension Code");
		underWriterColumnsHeading.add("Country");

		// Populate list Data for Supplier Recovery Claim Data Reporting
		suppRecoveryClaimOuterColumns.add("business_unit_info");
		suppRecoveryClaimOuterColumns.add("claim_number");
		suppRecoveryClaimOuterColumns.add("claim_type");
		suppRecoveryClaimOuterColumns.add("recovery_claim_state");
		suppRecoveryClaimOuterColumns.add("rec_claim_created_on");
		suppRecoveryClaimOuterColumns.add("failure_date");
		suppRecoveryClaimOuterColumns.add("repair_date");
		suppRecoveryClaimOuterColumns.add("(select service_provider_number from service_provider where id = for_dealer) dealer_number");
		suppRecoveryClaimOuterColumns.add("(select name from party where id = for_dealer) dealer_name");
		suppRecoveryClaimOuterColumns.add("supplier_number");
		suppRecoveryClaimOuterColumns.add("(select name from party where id = supplier_id) supplier_name");
		suppRecoveryClaimOuterColumns.add("(select item_number from item where id = causal_part) causal_part_number");
		suppRecoveryClaimOuterColumns.add("is_causal_part");
		suppRecoveryClaimOuterColumns.add("is_ir_part");
		suppRecoveryClaimOuterColumns.add("replaced_part_number");
		suppRecoveryClaimOuterColumns.add("replaced_part_desc");
		suppRecoveryClaimOuterColumns.add("replaced_part_qty");
		suppRecoveryClaimOuterColumns.add("uom");
		suppRecoveryClaimOuterColumns.add("case when part_unit_cost is not null then" +
				" cast(convert_to_currency(claim_number,part_unit_cost,unit_cost_curr,supplier_currency) as NUMBER(19,2))" +
				" else null end replaced_part_cost");
		suppRecoveryClaimOuterColumns.add("supplier_currency replaced_part_cost_curr");
		suppRecoveryClaimOuterColumns.add("GET_SERIAL_NUMBER(claim_id) serial_number");
		suppRecoveryClaimOuterColumns.add("GET_MODEL_DESC(claim_id) model_desc");
		suppRecoveryClaimOuterColumns.add("(SELECT min(BUILT_ON) FROM INVENTORY_ITEM " +
				" WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = claim_id)) build_date");
		suppRecoveryClaimOuterColumns.add("(SELECT min(INVOICE_DATE) FROM INVENTORY_TRANSACTION" +
				" WHERE TRANSACTED_ITEM IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM" +
				" WHERE CLAIM = claim_id) AND INV_TRANSACTION_TYPE = 1) invoice_date");
		suppRecoveryClaimOuterColumns.add("(SELECT min(delivery_date) FROM INVENTORY_ITEM" +
				" WHERE ID IN (SELECT ITEM_REF_INV_ITEM FROM CLAIMED_ITEM WHERE CLAIM = claim_id)) delivery_date");
		suppRecoveryClaimOuterColumns.add("GET_JOB_CODE_DESC(claim_id) job_code");
		suppRecoveryClaimOuterColumns.add("(select sum(hours_in_service)" +
				" from claimed_item where claim = claim_id)hours_in_service");
		suppRecoveryClaimOuterColumns.add("(select name from failure_type_definition where id = fault_found) fault_found");
		suppRecoveryClaimOuterColumns.add("(select name from failure_cause_definition where id = caused_by) caused_by");
		//suppRecoveryClaimOuterColumns.add("(select external_comments from claim_audit where id=accepted_audit) dealer_comments");
		suppRecoveryClaimOuterColumns.add("(select NVL(condition_found,'') || ' ## ' || NVL(work_performed,'') || ' ## ' || NVL(other_comments,'') " +
								" from claim where id = claim_id) dealer_comments");
		suppRecoveryClaimOuterColumns.add("(select internal_comments from claim_audit where id=accepted_audit) processor_comments");
		suppRecoveryClaimOuterColumns.add("supplier_currency");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select li.recovered_cost_amt " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id " +
				" and li.section=sec.id and sec.name='Oem Parts') else null end material_cost_total");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select li.recovered_cost_amt " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id " +
				" and li.section=sec.id and sec.name='Non Oem Parts') else null end non_tk_parts_total");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select li.recovered_cost_amt " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id " +
				" and li.section=sec.id and sec.name='Miscellaneous Parts') else null end material_parts_total");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then GET_TOTAL_LABOR_HOURS(claim_id) " +
				" else null end total_labor_hours");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select li.recovered_cost_amt " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id " +
				" and li.section=sec.id and sec.name='Labor') else null end labor_cost_total");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select sum(li.recovered_cost_amt) " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id and li.section=sec.id" +
				" and sec.name not in ('Oem Parts','Non Oem Parts','Labor','Miscellaneous Parts'))" +
				" else null end misc_cost_total");
		suppRecoveryClaimOuterColumns.add("case when is_primary=1 then (select sum(li.recovered_cost_amt) " +
				" from cost_line_item li,rec_clm_cost_line_items rli, section sec" +
				" where li.id=rli.cost_line_items and rli.recovery_claim=recovery_id " +
				" and li.section=sec.id and sec.name != 'Claim Amount') else null end total_contract_amt");

		suppRecoveryClaimInnerColumns.add("business_unit_info");
		suppRecoveryClaimInnerColumns.add("claim_number");
		suppRecoveryClaimInnerColumns.add("claim_type");
		suppRecoveryClaimInnerColumns.add("recovery_claim_state");
		suppRecoveryClaimInnerColumns.add("rec_claim_created_on");
		suppRecoveryClaimInnerColumns.add("failure_date");
		suppRecoveryClaimInnerColumns.add("repair_date");
		suppRecoveryClaimInnerColumns.add("for_dealer");
		suppRecoveryClaimInnerColumns.add("supplier_number");
		suppRecoveryClaimInnerColumns.add("supplier_id");
		suppRecoveryClaimInnerColumns.add("causal_part");
		suppRecoveryClaimInnerColumns.add("is_causal_part");
		suppRecoveryClaimInnerColumns.add("is_ir_part");
		suppRecoveryClaimInnerColumns.add("replaced_part_number");
		suppRecoveryClaimInnerColumns.add("replaced_part_desc");
		suppRecoveryClaimInnerColumns.add("replaced_part_qty");
		suppRecoveryClaimInnerColumns.add("uom");
		suppRecoveryClaimInnerColumns.add("part_unit_cost");
		suppRecoveryClaimInnerColumns.add("unit_cost_curr");
		suppRecoveryClaimInnerColumns.add("claim_id");
		suppRecoveryClaimInnerColumns.add("fault_found");
		suppRecoveryClaimInnerColumns.add("caused_by");
		suppRecoveryClaimInnerColumns.add("supplier_currency");
		suppRecoveryClaimInnerColumns.add("accepted_audit");
		suppRecoveryClaimInnerColumns.add("is_primary");
		suppRecoveryClaimInnerColumns.add("recovery_id");

		// Populate header list Data for Supplier Recovery Claim Data Reporting


		suppRecoveryPartsColumnsHeading.add("Recovery Claim Number");
		suppRecoveryPartsColumnsHeading.add("Supplier Number");
		suppRecoveryPartsColumnsHeading.add("Part Number");
		suppRecoveryPartsColumnsHeading.add("Cost Price");
		suppRecoveryPartsColumnsHeading.add("Currency");
		suppRecoveryPartsColumnsHeading.add("UOM");
		suppRecoveryPartsColumnsHeading.add("Is Causal Part");
		suppRecoveryPartsColumnsHeading.add("Part Description");
		suppRecoveryPartsColumnsHeading.add("Repair Date");
		suppRecoveryPartsColumnsHeading.add("Rec Claim Created On");
		suppRecoveryPartsColumnsHeading.add("Supplier Name");

		suppRecoveryPartsOuterColumns.add("recovery_claim_number");
		suppRecoveryPartsOuterColumns.add("supplier_number");
		suppRecoveryPartsOuterColumns.add("item_number");
		suppRecoveryPartsOuterColumns.add("cost_price_amt");
		suppRecoveryPartsOuterColumns.add("cost_price_curr");
		suppRecoveryPartsOuterColumns.add("uom");
		suppRecoveryPartsOuterColumns.add("is_causal_part");
		suppRecoveryPartsOuterColumns.add("description");
		suppRecoveryPartsOuterColumns.add("repair_date");
		suppRecoveryPartsOuterColumns.add("d_created_on");
		suppRecoveryPartsOuterColumns.add("name");

	}

	private List<String> getSuppRecoveryClaimColumnsHeading(ReportSearchBean reportSearchBean) {
		List<String> suppRecoveryClaimColumnsHeading = new ArrayList<String>(10);
		suppRecoveryClaimColumnsHeading.add("Business Unit");
		suppRecoveryClaimColumnsHeading.add("Claim Number");
		suppRecoveryClaimColumnsHeading.add("Claim Type");
		suppRecoveryClaimColumnsHeading.add("Recovery Claim State");
		suppRecoveryClaimColumnsHeading.add("Recovery Claim Created On");
		suppRecoveryClaimColumnsHeading.add("Failure Date");
		suppRecoveryClaimColumnsHeading.add("Repair Date");
		suppRecoveryClaimColumnsHeading.add("Dealer Number");
		suppRecoveryClaimColumnsHeading.add("Dealer Name");
		suppRecoveryClaimColumnsHeading.add("Supplier Number");
		suppRecoveryClaimColumnsHeading.add("Supplier Name");
		suppRecoveryClaimColumnsHeading.add("Causal Part Number");
		suppRecoveryClaimColumnsHeading.add("Is Causal Part?");
		suppRecoveryClaimColumnsHeading.add("Is NMHG Part?");
		suppRecoveryClaimColumnsHeading.add("Replaced Part Number");
		suppRecoveryClaimColumnsHeading.add("Replaced Part Desc");
		suppRecoveryClaimColumnsHeading.add("Replaced Part Quantity");
		suppRecoveryClaimColumnsHeading.add("UOM");
		suppRecoveryClaimColumnsHeading.add("UOM Replaced Part Cost");
		suppRecoveryClaimColumnsHeading.add("Replaced Part Cost Currency");
		suppRecoveryClaimColumnsHeading.add("Serial Number");
		suppRecoveryClaimColumnsHeading.add("Model Desc");
		suppRecoveryClaimColumnsHeading.add("Build Date");
		suppRecoveryClaimColumnsHeading.add("Invoice Date");
		suppRecoveryClaimColumnsHeading.add("Delivery Date");
		suppRecoveryClaimColumnsHeading.add("Job Codes Desc");
		suppRecoveryClaimColumnsHeading.add("Machine Hours");
		suppRecoveryClaimColumnsHeading.add("Fault Found");
		suppRecoveryClaimColumnsHeading.add("Caused By");
		suppRecoveryClaimColumnsHeading.add("Dealer Comments");
		suppRecoveryClaimColumnsHeading.add("Processor Comments");
		suppRecoveryClaimColumnsHeading.add("Supplier Currency");
		suppRecoveryClaimColumnsHeading.add(reportSearchBean.getBusinessUnitName()
					+ " Parts Total");
		suppRecoveryClaimColumnsHeading.add("Non" + reportSearchBean.getBusinessUnitName()
					+ " Parts Total");
		suppRecoveryClaimColumnsHeading.add("Miscellaneous Parts Total");
		suppRecoveryClaimColumnsHeading.add("Total Labor Hours");
		suppRecoveryClaimColumnsHeading.add("Labor Cost Total");
		suppRecoveryClaimColumnsHeading.add("Incidental Cost Total");
		suppRecoveryClaimColumnsHeading.add("Total Cost");
		return suppRecoveryClaimColumnsHeading;
	}

	private List<String> getSuppRecoveryPartsInnerColumns(ReportSearchBean reportSearchBean) {
		boolean useMaterialCost = useMaterialCost(reportSearchBean.getBusinessUnitName());
		List<String> suppRecoveryPartsInnerColumns = new ArrayList<String>(10);
		suppRecoveryPartsInnerColumns.add("r.recovery_claim_number");
		suppRecoveryPartsInnerColumns.add("s.supplier_number");
		suppRecoveryPartsInnerColumns.add("i.item_number");
		String costCol = "opr.cost_price_per_unit_amt";
		String currencyCol = "opr.cost_price_per_unit_curr";
		if(useMaterialCost) {
			costCol = "opr.material_cost_amt";
			currencyCol = "opr.material_cost_curr";
		}
		suppRecoveryPartsInnerColumns.add(" case when um.mapping_fraction is null then " + costCol +
				" else CAST(("+costCol+"/um.mapping_fraction) AS NUMBER(19,2)) end cost_price_amt");
		suppRecoveryPartsInnerColumns.add(currencyCol + " cost_price_curr");

		suppRecoveryPartsInnerColumns.add(" case when um.id is null then i.uom else um.mapped_uom || ' [' || um.base_uom || ']' end uom");
		suppRecoveryPartsInnerColumns.add(" case when si.causal_part=i.id then 'YES' else 'NO' end is_causal_part ");
		suppRecoveryPartsInnerColumns.add("i.description");
		suppRecoveryPartsInnerColumns.add("ca.repair_date");
		suppRecoveryPartsInnerColumns.add("r.d_created_on");
		suppRecoveryPartsInnerColumns.add("p.name");
		return suppRecoveryPartsInnerColumns;
	}
}

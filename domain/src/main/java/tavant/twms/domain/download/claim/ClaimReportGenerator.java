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
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.download.*;

import javax.mail.MethodNotSupportedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jhulfikar.ali
 *
 */

public class ClaimReportGenerator extends WarrantyReportGenerator implements ReportGenerator {

	public String reportContext;

	@Override
	protected String getReportQuery(ReportSearchBean reportSearchBean) {
		StringBuffer buffer = new StringBuffer(getSelectClause())
				.append(getProjectionClause(reportSearchBean.getDelimiter()));
		buffer.append(prepareWhereClause(reportSearchBean));
		return buffer.toString();
	}
	
	@Override
	protected List<String> getReportColumnHeading(ReportSearchBean reportSearchBean) {
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return claimDataColumnsHeading;
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(reportContext))
			return claimPartsDataColumnsHeading;
		else if (DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return extWntyClaimPartsDataHeading;
		else if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			return claimFinancialDataColumnsHeading;
		return claimDetailDataColumnsHeading;
	}

	@Override
	protected String getOrderByClause() {
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return " ORDER BY c.business_unit_info, d.service_provider_number, c.filed_on_date ";
		else if (DOWNLOAD_CONTEXT_CLAIM_DETAIL_DATA.equalsIgnoreCase(reportContext))
			return " ORDER BY c.business_unit_info, p.name, c.claim_number ";
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(reportContext))
			return " ORDER BY c.business_unit_info, c.claim_number ";
		else if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			return " ORDER BY p.name ";
		return "";
	}
	
	@Override
	protected String getGroupByClause() {
		if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			return " GROUP BY p.name ";
		return "";
	}
	
	@Override
	public String getReportFileName() {
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return "Claim Report";
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(reportContext))
			return "Claim Parts Report";
		else if (DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return "Exntended Warranty Claim Report";
		else if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			return "Claim Financial Report";
		return "Claim Detail Report"; /// Only allowed report now is Claim Detail
	}

	public String getProjectionClause(String delimiter) {
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return populateSelectClause(claimDataColumns);
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(reportContext))
			return populateSelectClause(claimPartsDataColumns);
		else if (DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA.equalsIgnoreCase(reportContext))
			return populateSelectClauseWithDelimiter(extWntyClaimPartsData, delimiter);
		else if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			return populateSelectClause(claimFinancialDataColumns);
		return populateSelectClause(claimDetailDataColumns);
	}

	public String prepareWhereClause(ReportSearchBean reportSearchBean) {
		String whereClauseForQuery = null;
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(reportContext))
			whereClauseForQuery = whereClauseForClaim(reportSearchBean);
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(reportContext))
			whereClauseForQuery = whereClauseForClaimPartsReport(reportSearchBean);
		else if (DOWNLOAD_CONTEXT_CLAIM_DETAIL_DATA.equalsIgnoreCase(reportContext))
			whereClauseForQuery = whereClauseForClaimReport(reportSearchBean);
		else if (DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA.equalsIgnoreCase(reportContext))
			whereClauseForQuery = whereClauseForExtClaim(reportSearchBean);
		else if(DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(reportContext))
			whereClauseForQuery = whereClauseForClaimFinancialReport(reportSearchBean);
		
		return whereClauseForQuery;
	}

	private String whereClauseForClaimFinancialReport(
			ReportSearchBean reportSearchBean) {
		return " FROM " +
				" ( select id, item_ref_unszed_item, rejected," +
				" case when amountclaimed is not null and amountaccepted is null then 1 else 0 end as pending," +
				" (amountclaimed * factor) as amountclaimed,(amountpaid * factor) as amountpaid," +
				" case when rejected=1 then (amountclaimed * factor) else 0 end as amountrejected," +
				" case when amountclaimed is not null and amountaccepted is null then (amountclaimed * factor) else 0 end as amountpending" +
				" from " +
				" ( select c.id,c.claim_number,ci.item_ref_unszed_item," +
					" case when org.preferred_currency = '"+DEFAULT_CURRENCY_FOR_CLAIM_FINANCIAL_REPORT+"' then 1 else (" +
						" select cf.factor from currency_exchange_rate er, currency_conversion_factor cf" +
						" where er.from_currency=org.preferred_currency and er.to_currency='"+DEFAULT_CURRENCY_FOR_CLAIM_FINANCIAL_REPORT+"'" +
						" and cf.parent=er.id and sysdate between cf.from_date and cf.till_date and rownum=1) end as factor," +
					" abs(c_memo.paid_amount_amt) as amountpaid," +
					" case when UPPER(ca.state) in ('REJECTED','DENIED_AND_CLOSED','DENIED') then 1 else 0 end as rejected," +
					getPaymentComponentQuery(false,new String[]{"Claim Amount"}) + " as amountaccepted," +
					getPaymentComponentQuery(true,new String[]{"Claim Amount"}) + " as amountclaimed" +
					" from claim c,claimed_item ci, payment pmt,credit_memo c_memo, claim_audit ca," +
					" service_provider d, organization org" +
					" where upper(ca.state) NOT IN ('DRAFT ', 'DRAFT_DELETED', 'DELETED')" +
						" and c.for_dealer=d.id " +
						" and d.id=org.id " +
						" and ci.claim=c.id and ci.item_ref_unszed_item is not null" +
						" AND c.payment = pmt.id(+)" +
						" AND pmt.active_credit_memo = c_memo.id(+) " +
						whereClauseForDealers(reportSearchBean,"d") +
						whereClauseForSubmitDate(reportSearchBean,"c") +
				" ) t" +
				" ) f_data , item i, item_group m, item_group P" +
				" where f_data.item_ref_unszed_item=i.id " +
					" and i.model= m.id " +
					" and m.is_part_of=p.id " +
					whereClauseForBusinessUnits(reportSearchBean,"i");
	}

	private String whereClauseForClaimPartsReport(ReportSearchBean reportSearchBean) {
		
		String filterClause = whereClauseForBusinessUnits(reportSearchBean,"clm") +
							whereClauseForDealers(reportSearchBean,"d") +
							whereClauseForStatus(reportSearchBean,"ca") +
							whereClauseForSubmitDate(reportSearchBean,"clm");
		StringBuffer buffer = new StringBuffer(
				" FROM ( SELECT clm.id,clm.claim_number,clm.business_unit_info,pr.item_number,opr.number_of_units,ca.payment FROM " +
					" claim clm, service_provider d, service_information si, " +
					" service s, service_oemparts_replaced sor, " +
					" oem_part_replaced opr, item pr, " +
					"claim_audit ca" +
					" WHERE clm.for_dealer = d.id" +
					" AND ca.service_information = si.id" +
					" AND si.service_detail = s.id" +
					" AND s.id = sor.service" +
					" AND sor.oemparts_replaced = opr.id" +
					" AND opr.item_ref_unszed_item = pr.id" +
					" AND ca.id = clm.active_claim_audit" +
					filterClause +
					" UNION ALL " +
					//added to get part number for r6 code as oem_part_replaced is mapped with huss_parts_replaced_installed - (Upgrade QC 138)
					" SELECT clm.id,clm.claim_number,clm.business_unit_info,pr.item_number,opr.number_of_units,ca.payment FROM " +
						" claim clm, service_provider d, service_information si, " +
						" service s, huss_parts_replaced_installed hpri, " +
						" oem_part_replaced opr, item pr ,claim_audit ca" +
						" WHERE clm.for_dealer = d.id " +
						" AND ca.service_information = si.id " +
						" AND si.service_detail = s.id " +
						" AND s.id = hpri.service_detail " +
						" AND hpri.id = opr.oem_replaced_parts " +
						" AND opr.item_ref_unszed_item = pr.id " +
						" AND ca.id = clm.active_claim_audit" +
						filterClause +
					" UNION ALL " +
					//end
					" SELECT clm.id,clm.claim_number,clm.business_unit_info,opr.description as item_number,opr.number_of_units,ca.payment FROM " +
					" claim clm, service_provider d, service_information si, " +
					" service s, service_nonoemparts_replaced sor, " +
					" non_oem_part_replaced opr,claim_audit ca " +
					" WHERE clm.for_dealer = d.id" +
					" AND ca.service_information = si.id" +
					" AND si.service_detail = s.id" +
					" AND s.id = sor.service" +
					" AND sor.nonoemparts_replaced = opr.id" +
					" AND ca.id = clm.active_claim_audit" +
					filterClause +
					" UNION ALL " +
					" SELECT clm.id,clm.claim_number,clm.business_unit_info,mi.part_number as item_number,opr.number_of_units,ca.payment FROM " +
					" claim clm, service_provider d, service_information si, " +
					" service s, service_misc_parts_replaced sor," +
					" non_oem_part_replaced opr, misc_item mi,claim_audit ca " +
					" WHERE clm.for_dealer = d.id" +
					" AND ca.service_information = si.id" +
					" AND si.service_detail = s.id" +
					" AND s.id = sor.service" +
					" AND sor.misc_parts_replaced = opr.id" +
					" AND mi.id = opr.misc_item" +
					" AND ca.id = clm.active_claim_audit" +
					filterClause +
					" ) c, " +
					" claimed_item ci,"+
					" applicable_policy ap,"+
				    " policy reg_policy,"+
				    " policy_definition reg_pd,"+
				    " policy_definition unreg_pd,"+
				    " payment pmt," +
					" credit_memo c_memo, claim_audit ca ,claim clm" +
				    " WHERE clm.id = ci.claim " +
					" AND ci.applicable_policy = ap.id(+)" +
				    " AND ap.registered_policy = reg_policy.id(+)" +
				    " AND reg_policy.policy_definition = reg_pd.id(+)" +
				    " AND ap.policy_definition = unreg_pd.id(+)" +
				    " AND ca.payment = pmt.id" +
				    " AND ca.id = clm.active_claim_audit" +
					" AND pmt.active_credit_memo = c_memo.id(+)"
				    );
		/*buffer.append(whereClauseForBusinessUnits(reportSearchBean,"c"))
			.append(whereClauseForDealers(reportSearchBean,"d"))
			.append(whereClauseForStatus(reportSearchBean,"c"))
			.append(whereClauseForSubmitDate(reportSearchBean,"c"));*/
		buffer.append(whereClauseForCreditDate(reportSearchBean,"c_memo"));
		return buffer.toString();
	}
	
	private String whereClauseForClaimReport(ReportSearchBean reportSearchBean) {
		StringBuffer buffer = new StringBuffer(
				" FROM claim c, service_provider d," +
					"service_information si, " +
					"service s, " +
					"item cp, " +
					"claimed_item ci, " +
					"applicable_policy ap,"+
				    "policy reg_policy,"+
				    "policy_definition reg_pd,"+
				    "policy_definition unreg_pd,"+
					//"payment pmt, " +
					"credit_memo c_memo, " +
					"inventory_item inv, " +
					"item i, " +
					"item_group m, " +
					"item_group p, " +
					"claim_audit ca" +
				" WHERE c.for_dealer = d.id" +
					" AND ca.service_information = si.id" +
					" AND si.service_detail = s.id" +
					" AND si.causal_part = cp.id(+)" +
					" AND c.id = ci.claim" +
					" AND ci.applicable_policy = ap.id(+)" +
				    " AND ap.registered_policy = reg_policy.id(+)" +
				    " AND reg_policy.policy_definition = reg_pd.id(+)" +
				    " AND ap.policy_definition = unreg_pd.id(+)" +
					//" AND c.payment = pmt.id(+)" +
					//" AND pmt.active_credit_memo = c_memo.id(+)" +
				    " AND c.claim_number = c_memo.claim_number(+) " +
					" AND ci.item_ref_inv_item = inv.id(+)" +
					" AND ci.item_ref_unszed_item = i.id(+)" +
					" AND i.model = m.id(+)" +
					" AND m.is_part_of = p.id(+)");
		buffer.append(whereClauseForBusinessUnits(reportSearchBean,"c"))
			.append(whereClauseForDealers(reportSearchBean,"d"))
			.append(whereClauseForStatus(reportSearchBean,"ca"))
			.append(whereClauseForSubmitDate(reportSearchBean,"c"))
			.append(whereClauseForCreditDate(reportSearchBean,"c_memo"));
		return buffer.toString();
	}

	protected String whereClauseForClaim(ReportSearchBean reportSearchBean) {
		StringBuffer sb = new StringBuffer(" FROM claim c, service_provider d,"
			    +" service_information si,"
			    +" service s,"
			    +" failure_type_definition ftd,"
			    +" item cp,"
			    +" claimed_item ci,"
			    +" applicable_policy ap,"
			    +" policy reg_policy,"
			    +" policy_definition reg_pd,"
			    +" policy_definition unreg_pd,"
			   // +" payment pmt,"
			    +" credit_memo c_memo, "
			    +" inventory_item inv, "
			    +"claim_audit ca"
			+" WHERE c.for_dealer = d.id"
			    +" AND ca.service_information = si.id"
			    +" AND si.service_detail = s.id"
			    +" AND si.fault_found = ftd.id(+)"
			    +" AND si.causal_part = cp.id(+)"
			    +" AND c.id = ci.claim"
			    +" AND ci.applicable_policy = ap.id(+)"
			    +" AND ap.registered_policy = reg_policy.id(+)"
			    +" AND reg_policy.policy_definition = reg_pd.id(+)"
			    +" AND ap.policy_definition = unreg_pd.id(+)"
//			    +" AND c.payment = pmt.id(+)"
//			    +" AND pmt.active_credit_memo = c_memo.id(+)"
			    +" AND c.claim_number = c_memo.claim_number(+) "
			    +" AND ci.item_ref_inv_item = inv.id(+)" );
		sb.append(whereClauseForBusinessUnits(reportSearchBean,"c"))
			.append(whereClauseForDealers(reportSearchBean,"d"))
			.append(whereClauseForStatus(reportSearchBean,"ca"))
			.append(whereClauseForSubmitDate(reportSearchBean,"c"))
			.append(whereClauseForCreditDate(reportSearchBean,"c_memo"));
		return sb.toString();
	}

	private String whereClauseForExtClaim(ReportSearchBean reportSearchBean) {
		StringBuffer buffer = new StringBuffer(CLAIM_REPORT_QUERY_FROM_CLAUSE_MINIMAL)
				.append(CLAIM_REPORT_QUERY_PART_FROM_CLAUSE)
				.append(CLAIM_REPORT_QUERY_BASIC_WHERE_CLAUSE_MINIMAL)
				.append(CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE)
				.append(EXT_WNTY_CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE)
				.append(betweenClauseForDateColumn(reportSearchBean, "cmem.credit_memo_date"))
				.append(whereClauseForBusinessUnits(reportSearchBean));
		return buffer.toString();
	}

	public String setParametersList() throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
				"Exception: setParametersList() is not supported for ClaimReportGenerator");
	}

	protected String whereClauseForClaimReporting(ReportSearchBean reportSearchBean) {
		StringBuffer whereClause = new StringBuffer("");
		
		String submitOrCreditWhereCriteria = "submitDate"
				.equalsIgnoreCase(reportSearchBean
						.getSubmitOrCreditOrUpdateDate()) ? "cl.filed_on_date"
				: "cmem.credit_memo_date";
		whereClause.append(super.betweenClauseForDateColumn(reportSearchBean, submitOrCreditWhereCriteria));
		
		String claimState = WarrantyReportHelper.claimStatesForUser(reportSearchBean.getClaimStatus());
		if (StringUtils.hasText(claimState))
			whereClause.append("and cl.state in (" + claimState + ") ");		 
		
		if (StringUtils.hasText(reportSearchBean.getDealerNumber()))
			whereClause.append("and dealer.dealer_number in ("
					+ WarrantyReportHelper
							.populateDealerNumberAsCSVForQuery(reportSearchBean
									.getDealerNumber()) + ") ");
		return whereClause.toString();
	}
	
	protected String whereClauseForSubmitDate(ReportSearchBean reportSearchBean, String alias) {
		if("submitDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate()))
			return betweenClauseForDateColumn(reportSearchBean, alias+".filed_on_date");
		return "";
	}
	
	protected String whereClauseForCreditDate(ReportSearchBean reportSearchBean, String alias) {
		if("creditDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate()))
			return betweenClauseForDateColumn(reportSearchBean, alias+".credit_memo_date");
		return "";
	}
	
	protected String whereClauseForStatus(ReportSearchBean reportSearchBean, String alias) {
		if(DownloadClaimState.ALL.toString().equalsIgnoreCase(reportSearchBean.getClaimStatus()))
			return " AND "+alias+".state not in (" + WarrantyReportHelper
				.commaSeparatedArrayForQuery(new ClaimState[]{ClaimState.DRAFT, ClaimState.DRAFT_DELETED}) 
				+ ") ";
		else {
			String claimState = WarrantyReportHelper.claimStatesForUser(reportSearchBean.getClaimStatus());
			if (StringUtils.hasText(claimState))
				return " AND "+alias+".state in (" + claimState + ") ";
		}
		return "";
	}
	
	protected String whereClauseForDealers(ReportSearchBean reportSearchBean, String alias) {
		if (StringUtils.hasText(reportSearchBean.getDealerNumber()))
			return " AND "+alias+".service_provider_number in ("
					+ WarrantyReportHelper
							.populateDealerNumberAsCSVForQuery(reportSearchBean
									.getDealerNumber()) + ") ";
		return "";
	}

	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
	}

	public static final String CLAIM_REPORT_QUERY_FROM_CLAUSE_MINIMAL = 
		" from claim cl, service_information si, item it, payment pay, credit_memo cmem,claim_audit ca ";
	
	public static final String CLAIM_REPORT_QUERY_BASIC_WHERE_CLAUSE_MINIMAL = 
		" where ca.service_information (+) = si.id and si.causal_part (+) = it.id " +
		" and pay.ACTIVE_CREDIT_MEMO (+) = cmem.id and cl.payment = pay.id ";
	
	public static final String CLAIM_PART_REPORT_QUERY_FROM_CLAUSE = 
		" from claim cl, service_information si, item it, dealership dealer, "
		+ " payment pay, credit_memo cmem, claimed_item ci, "
		+ " applicable_policy ap, policy pol, policy_definition pd,claim_audit ca ";
	
	public static final String CLAIM_PART_REPORT_QUERY_BASIC_WHERE_CLAUSE = 
		" where ca.service_information (+) = si.id and si.causal_part (+) = it.id and "
		+ " cl.for_dealer = dealer.id and "
		+ " pay.ACTIVE_CREDIT_MEMO (+) = cmem.id and cl.payment = pay.id and "
		+ " ci.claim = cl.id and ci.applicable_policy (+) = ap.id and ap.registered_policy (+) = pol.id "
		+ " and pol.policy_definition (+) = pd.id ";

	public static final String CLAIM_REPORT_QUERY_PART_FROM_CLAUSE = 
		" , line_item_groups ligs, Line_Item_Group lig, current_part_info cpi, Part_Payment_Info ppi ";
	
	public static final String CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE = 
		" and ligs.for_payment (+) = pay.id and ligs.line_item_groups = lig.id (+) " +
		" and cpi.line_item_group (+) = lig.id and cpi.current_part_payment_info = ppi.id (+) ";
	
	public static final String EXT_WNTY_CLAIM_REPORT_QUERY_PART_WHERE_CLAUSE =
		" and lig.name='Oem Parts' ";
	
	private static List<String> claimDataColumns = new ArrayList<String>(10);
	
	private static List<String> claimDataColumnsHeading = new ArrayList<String>(10);
	
	private static List<String> claimPartsDataColumns = new ArrayList<String>(10);
	
	private static List<String> claimPartsDataColumnsHeading = new ArrayList<String>(10);
	
	private static List<String> claimDetailDataColumns = new ArrayList<String>(10);

	private static List<String> claimDetailDataColumnsHeading = new ArrayList<String>(10);
	
	private static List<String> claimFinancialDataColumns = new ArrayList<String>(10);

	private static List<String> claimFinancialDataColumnsHeading = new ArrayList<String>(10);
	
	private static List<String> extWntyClaimPartsData = new ArrayList<String>(10);
	
	private static List<String> extWntyClaimPartsDataHeading = new ArrayList<String>(10);
	
	private static String getPaymentComponentQuery(boolean dealerAudit, String[] lineItemGroups) {
		StringBuffer sb = new StringBuffer("(SELECT ");
		sb.append(lineItemGroups.length > 1 ? " SUM(" : "")
		.append(dealerAudit ? " lig.base_amt " : " lig.accepted_amt " )
		.append(lineItemGroups.length > 1 ? ") " : "")
		.append(" FROM claim_audit ca,line_item_group lig,line_item_groups ligs ")
		.append(" WHERE ca.for_claim=c.id ")
		.append(" AND ca.multi_claim_maintenance=0 ")
		.append(" AND ca.payment = ligs.for_payment ")
		.append(" AND ligs.line_item_groups=lig.id ")
		.append(" AND ca.list_index=(SELECT "+(dealerAudit?"MIN":"MAX")+"(list_index) from claim_audit cad  ")
		.append(" WHERE cad.for_claim = c.id AND cad.previous_state IN ( ")
		.append((dealerAudit?"'SUBMITTED','SERVICE_MANAGER_RESPONSE','EXTERNAL_REPLIES'":
			"'DENIED','ACCEPTED'")).append("))")
		.append(" AND lig.name ").append(lineItemGroups.length > 1 ? " IN ( " : " = ");
		
		for(int i=0 ; i<lineItemGroups.length ; i++) {
			sb.append(i>0 ? ",'" : "'")
			.append(lineItemGroups[i])
			.append("'");
		}
		sb.append(lineItemGroups.length > 1 ? " )) " : " ) ");
		
		
		return sb.toString();
	}
	
	private static ClaimState[] getInProgressStates() {
		List<ClaimState> list = ClaimState.getStateListInProgress();
		ClaimState[] claimStates = new ClaimState[list.size()];
		int idx = 0;
		for(ClaimState state : list)
			claimStates[idx++] = state;
		return claimStates;
	}
	
	static {
		// Populate select clause Data for Claim Data Reporting
		claimDataColumns.add("cp.item_number");
		claimDataColumns.add("c.filed_on_date");
		claimDataColumns.add("inv.delivery_date");
		claimDataColumns.add("ca.failure_date");
		claimDataColumns.add("c.claim_number");
		claimDataColumns.add("c_memo.d_created_on");
		claimDataColumns.add("ca.repair_date");
		claimDataColumns.add("c.clm_type_name");
		claimDataColumns.add("(SELECT till_date FROM policy_audit pa WHERE for_policy = reg_policy.id "
		        +" AND created_on=(SELECT MAX(created_on) FROM policy_audit "
		            +" WHERE for_policy = reg_policy.id) AND ROWNUM=1)" );
		claimDataColumns.add("c_memo.credit_memo_date");
		claimDataColumns.add("c_memo.credit_memo_number");
		claimDataColumns.add("c_memo.cr_dr_flag");		
		claimDataColumns.add("abs(c_memo.paid_amount_amt)");
		claimDataColumns.add("c_memo.paid_amount_curr");
		claimDataColumns.add(getPaymentComponentQuery(false,new String[]{Section.LABOR}));
		claimDataColumns.add("(SELECT CASE WHEN NVL(lig.rate,0)>0 THEN lig.accepted_amt/lig.rate ELSE 0 END AS accepted_hours "
		        +" FROM claim_audit ca,line_item_group lig,line_item_groups ligs "
		        +" WHERE ca.for_claim=c.id  "
		        	+" AND ca.multi_claim_maintenance=0 "
		            +" AND ca.payment = ligs.for_payment "
		            +" AND ligs.line_item_groups=lig.id "   
		            +" AND lig.name='Labor' "
		            +" AND ca.list_index=(SELECT MAX(list_index) from claim_audit cad  "
		                        +" WHERE cad.for_claim = c.id AND cad.previous_state IN  "
		                        +" ('DENIED','ACCEPTED')))" );
		claimDataColumns.add(getPaymentComponentQuery(false,new String[]{Section.OEM_PARTS}));
		claimDataColumns.add(getPaymentComponentQuery(false,new String[]{Section.MISCELLANEOUS_PARTS}));
		claimDataColumns.add(getPaymentComponentQuery(false,new String[]{Section.TOTAL_CLAIM}));
		claimDataColumns.add(getPaymentComponentQuery(false,new String[]{Section.TRAVEL_BY_HOURS,Section.TRAVEL_BY_DISTANCE,Section.TRAVEL_BY_TRIP}));
		claimDataColumns.add(" (select v.value from inv_item_attr_vals iv ,attr_value v, attribute a " +
				" where iv.inv_item_id=inv.id and iv.inv_item_attr_val_id=v.id " +
				" and v.attribute=a.id and a.name='EngineSerialNo') as eng_serial");
		claimDataColumns.add("getFaultCodeName(si.fault_code_ref)");
		claimDataColumns.add("si.fault_code");
		claimDataColumns.add("ftd.name");
		//claimDataColumns.add("'KM'");
		claimDataColumns.add(getPaymentComponentQuery(true,new String[]{"Labor"}));
		claimDataColumns.add("(SELECT CASE WHEN NVL(lig.rate,0)>0 THEN lig.base_amt/lig.rate ELSE 0 END AS accepted_hours "
		        +" FROM claim_audit ca,line_item_group lig,line_item_groups ligs "
		        +" WHERE ca.for_claim=c.id  "
		        	+" AND ca.multi_claim_maintenance=0 "
		            +" AND ca.payment = ligs.for_payment "
		            +" AND ligs.line_item_groups=lig.id  "   
		            +" AND lig.name='Labor' "
		            +" AND ca.list_index=(SELECT MIN(list_index) from claim_audit cad  "
		                        +" WHERE cad.for_claim = c.id AND cad.previous_state IN  "
		                        +" ('SUBMITTED','SERVICE_MANAGER_RESPONSE','EXTERNAL_REPLIES')))" );
		claimDataColumns.add("ca.other_comments");
		claimDataColumns.add("inv.serial_number");
		claimDataColumns.add("ci.hours_in_service");
		claimDataColumns.add(" CASE WHEN ca.state IN (" +
				" 'MANUAL_REVIEW','ON_HOLD','ON_HOLD_FOR_PART_RETURN','FORWARDED'," +
				" 'TRANSFERRED', 'ADVICE_REQUEST', 'REPLIES', 'PROCESSOR_REVIEW'," +
				" 'REJECTED_PART_RETURN','PENDING_PAYMENT_SUBMISSION','PENDING_PAYMENT_RESPONSE','REOPENED') " +
				" THEN 'IN_PROGRESS' ELSE ca.state END AS state");
		claimDataColumns.add("ca.work_order_number");
		claimDataColumns.add("c.condition_found");
		claimDataColumns.add("ca.work_performed");
		claimDataColumns.add("ca.probable_cause");
		claimDataColumns.add("c.claim_number");
		claimDataColumns.add("(SELECT CASE WHEN NVL(lig.rate,0)>0 THEN lig.accepted_amt/lig.rate ELSE 0 END AS accepted_hours "
		        +" FROM claim_audit ca,line_item_group lig,line_item_groups ligs "
		        +" WHERE ca.for_claim=c.id  "
		        	+" AND ca.multi_claim_maintenance=0 "
		            +" AND ca.payment = ligs.for_payment "
		            +" AND ligs.line_item_groups=lig.id  "   
		            +" AND lig.name='Travel by Hours' "
		            +" AND ca.list_index=(SELECT MAX(list_index) from claim_audit cad  "
		                        +" WHERE cad.for_claim = c.id AND cad.previous_state IN  "
		                        +" ('DENIED','ACCEPTED')))" );
		claimDataColumns.add("d.service_provider_number");
		claimDataColumns.add("NVL(reg_pd.warranty_type, unreg_pd.warranty_type)");
		claimDataColumns.add("c.business_unit_info");
		
		// Populate header list Data for Claim Data Reporting
		claimDataColumnsHeading.add("Causal Part Number");
		claimDataColumnsHeading.add("Date of Claim Input");
		claimDataColumnsHeading.add("Delivery Date");
		claimDataColumnsHeading.add("Failure Date");
		claimDataColumnsHeading.add("Claim Number");    
		claimDataColumnsHeading.add("Date of Payment"); 
		claimDataColumnsHeading.add("Date of Repair");
		claimDataColumnsHeading.add("Claim Type");
		claimDataColumnsHeading.add("Warranty End Date");
		claimDataColumnsHeading.add("Credit Date");
		claimDataColumnsHeading.add("Memo Number");
		claimDataColumnsHeading.add("Credit/Debit");
		claimDataColumnsHeading.add("Total Paid Amount"); 
		claimDataColumnsHeading.add("Currency"); 
		claimDataColumnsHeading.add("Labor Amount Accepted");
		claimDataColumnsHeading.add("Labor Hours Accepted");
		claimDataColumnsHeading.add("Part Amount Accepted");
		claimDataColumnsHeading.add("MiscPart Amount Accepted");
		claimDataColumnsHeading.add("Total Amount Accepted");
		claimDataColumnsHeading.add("Travel Amount Accepted");
		claimDataColumnsHeading.add("Engine Serial Number");
		claimDataColumnsHeading.add("Failure Code");
		claimDataColumnsHeading.add("Failure Group");
		claimDataColumnsHeading.add("Failure Type");
		//claimDataColumnsHeading.add("KM Counter");
		claimDataColumnsHeading.add("Labor Amount Asked");
		claimDataColumnsHeading.add("Labor Hours Asked");
		claimDataColumnsHeading.add("Remarks");
		claimDataColumnsHeading.add("Unit Serial Number");
		claimDataColumnsHeading.add("Unit of Service");
		claimDataColumnsHeading.add("External Claim Status");
		claimDataColumnsHeading.add("Dealer Job Number");
		claimDataColumnsHeading.add("Condition Found");
		claimDataColumnsHeading.add("Work Performed");
		claimDataColumnsHeading.add("Probable Cause");
		claimDataColumnsHeading.add("Approval Number");
		claimDataColumnsHeading.add("Travel Hours Accepted");
		claimDataColumnsHeading.add("Dealer Number");
		claimDataColumnsHeading.add("Warranty Type");
		claimDataColumnsHeading.add("Business Unit");

		// Populate select clause Data for Claim Parts Data Reporting
		claimPartsDataColumns.add("clm.claim_number"); 
		claimPartsDataColumns.add("(select count(id) claim_sequence from claim_audit " +
				" where for_claim = clm.id and multi_claim_maintenance=0) "); 
		claimPartsDataColumns.add("item_number");
		claimPartsDataColumns.add("number_of_units"); 
		claimPartsDataColumns.add("NVL(reg_pd.warranty_type, unreg_pd.warranty_type)");
		claimPartsDataColumns.add("clm.business_unit_info");
		
		// Populate header list Data for Claim Parts Data Reporting
		claimPartsDataColumnsHeading.add("Claim Number");
		claimPartsDataColumnsHeading.add("Claim Sequence");
		claimPartsDataColumnsHeading.add("Part Number");
		claimPartsDataColumnsHeading.add("Quantity");
		claimPartsDataColumnsHeading.add("Warranty Type");
		claimPartsDataColumnsHeading.add("Business Unit");
		
		// Populate select clause Data for Claim Detail Data Reporting
		claimDetailDataColumns.add("p.name");
		claimDetailDataColumns.add("m.name");
		claimDetailDataColumns.add("(select count(id) claim_sequence from claim_audit " +
				" where for_claim = c.id and multi_claim_maintenance=0) ");
		claimDetailDataColumns.add("c.claim_number");
		claimDetailDataColumns.add("ci.hours_in_service");
		claimDetailDataColumns.add(getPaymentComponentQuery(true,new String[]{"Claim Amount"}));
		claimDetailDataColumns.add("case when c_memo.cr_dr_flag='CR' then abs(c_memo.paid_amount_amt) " +
									" else -abs(c_memo.paid_amount_amt) end as paid_amount_amt");
		claimDetailDataColumns.add("getFaultCodeName(si.fault_code_ref)");
		claimDetailDataColumns.add("inv.serial_number");
		claimDetailDataColumns.add("c.filed_on_date");
		claimDetailDataColumns.add("c_memo.credit_memo_date");
		claimDetailDataColumns.add("c_memo.credit_memo_number");
		claimDetailDataColumns.add("ca.other_comments");
		claimDetailDataColumns.add("cp.item_number");
		claimDetailDataColumns.add(" CASE WHEN ca.state IN (" +
				WarrantyReportHelper.commaSeparatedArrayForQuery(getInProgressStates())+
				" ) " +
				" THEN 'IN_PROGRESS' ELSE ca.state END AS state");
		claimDetailDataColumns.add("c_memo.paid_amount_curr");
		claimDetailDataColumns.add("d.service_provider_number");
		claimDetailDataColumns.add("NVL(reg_pd.warranty_type, unreg_pd.warranty_type)"); 
		claimDetailDataColumns.add("ca.Work_Order_Number ");
		claimDetailDataColumns.add("c.business_unit_info");
		
		// Populate header list Data for Claim Detail Data Reporting
		claimDetailDataColumnsHeading.add("Product");
		claimDetailDataColumnsHeading.add("Model Number");
		claimDetailDataColumnsHeading.add("Claim Sequence");
		claimDetailDataColumnsHeading.add("Claim Number");
		claimDetailDataColumnsHeading.add("Unit Hours");
		claimDetailDataColumnsHeading.add("Amount Asked");
		claimDetailDataColumnsHeading.add("Amount Paid");
		//claimDetailDataColumnsHeading.add("Amount Pending");
		claimDetailDataColumnsHeading.add("Failure Code");
		claimDetailDataColumnsHeading.add("Unit Serial Number");
		claimDetailDataColumnsHeading.add("Date of Claim Input");
		claimDetailDataColumnsHeading.add("Acceptance Date");
		claimDetailDataColumnsHeading.add("Credit Memo Number");
		claimDetailDataColumnsHeading.add("Remarks");
		claimDetailDataColumnsHeading.add("Causal Part Number");
		claimDetailDataColumnsHeading.add("Status");
		claimDetailDataColumnsHeading.add("Currency");
		claimDetailDataColumnsHeading.add("Dealer Number");
		claimDetailDataColumnsHeading.add("Warranty Type");
		claimDetailDataColumnsHeading.add("Dealer Job Number");
		claimDetailDataColumnsHeading.add("Business Unit");	
		
		// Populate header list Data for Ext Wnty Claim Parts Data Reporting
		extWntyClaimPartsData.add("cl.claim_number "); // cl.claim_number 'Claim Number'
		extWntyClaimPartsData.add("(select counr(id) claim_sequence from claim_audit " +
				" where for_claim = cl.id and multi_claim_maintenance=0) "); // Claim Sequence
		extWntyClaimPartsData.add("it.item_number "); // it.item_number 'Part Number'
		extWntyClaimPartsData.add("ppi.quantity "); // ppi.quantity 'Quantity'
		extWntyClaimPartsData.add("ppi.total_amt "); // ppi.total_amt 'Price'
				
		// Populate list Data for Ext Wnty Claim Parts Data Reporting
		extWntyClaimPartsDataHeading.add("Claim Number"); // 'Claim Number'
		extWntyClaimPartsDataHeading.add("Claim Sequence"); // 'Claim Sequence'
		extWntyClaimPartsDataHeading.add("Part Number");
		extWntyClaimPartsDataHeading.add("Quantity");
		extWntyClaimPartsDataHeading.add("Price");
		
		claimFinancialDataColumns.add("p.name");
		claimFinancialDataColumns.add("count(distinct f_data.id) as Qty");
		claimFinancialDataColumns.add("cast(sum(f_data.amountclaimed) as number(10,2)) as claimedAmt");
		claimFinancialDataColumns.add("cast(sum(f_data.amountpaid) as number(10,2)) as paidAmt");
		//claimFinancialDataColumns.add("sum(f_data.amountpending) as pendingAmt");
		claimFinancialDataColumns.add("sum(f_data.pending) as pending");
		claimFinancialDataColumns.add("sum(f_data.rejected) as rejected");
		claimFinancialDataColumns.add("cast(sum(f_data.amountrejected) as number(10,2)) as rejectedAmt");
		claimFinancialDataColumns.add("'"+DEFAULT_CURRENCY_FOR_CLAIM_FINANCIAL_REPORT+"'");
		
		claimFinancialDataColumnsHeading.add("Product");
		claimFinancialDataColumnsHeading.add("Qty Claimed");
		claimFinancialDataColumnsHeading.add("Amount Claimed");
		claimFinancialDataColumnsHeading.add("Amount Paid");
		//claimFinancialDataColumnsHeading.add("Amount Pending");
		claimFinancialDataColumnsHeading.add("Total Claims Pending");
		claimFinancialDataColumnsHeading.add("Total Qty Rejected");
		claimFinancialDataColumnsHeading.add("Total Amount Rejected");
		claimFinancialDataColumnsHeading.add("Currency");

	}

}

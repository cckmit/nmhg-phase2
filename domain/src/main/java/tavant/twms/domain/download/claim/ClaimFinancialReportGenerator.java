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

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.mail.MethodNotSupportedException;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.download.ReportGenerator;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.download.WarrantyReportGenerator;
import tavant.twms.domain.download.WarrantyReportHelper;

/**
 * @author jhulfikar.ali
 *
 */
public class ClaimFinancialReportGenerator extends WarrantyReportGenerator implements ReportGenerator {

	public Clob getDownloadData(ReportSearchBean reportSearchBean) throws SQLException
	{   Connection conn = null; 
		CallableStatement cstmt = null;
		try
		{
			String uploadProcedureCall = new StringBuffer("CALL ").append("PREPARE_CLAIM_FINANCIAL_REPORT").
			append("(?, ?, ?, ?, ?, ?)").toString();
			conn = getReportTaskDAO().getSQLConnection();
			cstmt = conn.prepareCall(uploadProcedureCall);			
			cstmt.setString(1, WarrantyReportHelper.populateStringValAsDelimitedForQuery(
					reportSearchBean.getDealerNumber(), "::"));
			cstmt.setString(2, reportSearchBean.getFromDate().toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY));
			cstmt.setString(3, reportSearchBean.getToDate().toString(TWMSDateFormatUtil.DATE_FORMAT_CALENDAR_DD_MMM_YYYY));
			cstmt.setString(4, reportSearchBean.getDelimiter());
			cstmt.setInt(5, getDataUploadConfig().getExportRecordsLimit());
			cstmt.registerOutParameter(6, Types.CLOB);
			cstmt.execute();
		}
		finally
		{
			if(cstmt != null) {
				cstmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		return cstmt.getClob(6);
	}

	@Override
	public String getReportFileName() throws MethodNotSupportedException {
		return "Claim Financial Report";
	}

	@Override
	protected List<String> getReportColumnHeading(ReportSearchBean reportSearchBean) throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
		"Exception: getReportColumnHeading() is not supported for ClaimFinancialReportGenerator");
	}

	public String getProjectionClause(String delimiter) throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
		"Exception: getProjectionClause() is not supported for ClaimFinancialReportGenerator");
	}

	public String prepareWhereClause(ReportSearchBean reportSearchBean) throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
		"Exception: prepareWhereClause() is not supported for ClaimFinancialReportGenerator");
	}

	public String setParametersList() throws MethodNotSupportedException {
		throw new MethodNotSupportedException(
		"Exception: setParametersList() is not supported for ClaimFinancialReportGenerator");
	}
	
	
}

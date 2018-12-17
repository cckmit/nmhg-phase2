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

package tavant.twms.domain.upload.staging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tavant.twms.domain.upload.connection.ReportTaskDAO;
import tavant.twms.domain.upload.controller.UploadStatusDetail;

/**
 * @author jhulfikar.ali
 *
 */
public abstract class StagingDAO {

	private ReportTaskDAO reportTaskDAO;

	public UploadStatusDetail getUploadStatusDetail() throws SQLException {
		UploadStatusDetail usd = new UploadStatusDetail();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = reportTaskDAO.getSQLConnection();
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select count(*) from "+getStagingTableName());
			if (rs.next())
				usd.setTotalRecords(rs.getInt(1));
			rs = stmt
					.executeQuery("select count(*) from "+getStagingTableName()+" where upload_status='Y'");
			if (rs.next())
				usd.setSuccessRecords(rs.getInt(1));
			rs = stmt
					.executeQuery("select count(*) from "+getStagingTableName()+" where error_status='N'");
			if (rs.next())
				usd.setErrorRecords(rs.getInt(1));
		}
		finally {
			if (rs!=null)
				rs.close();
			if (stmt!=null)
				stmt.close();
			if (conn !=null)
				conn.close();
		}
		return usd;
	}

	public void clean() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = reportTaskDAO.getSQLConnection();
			pstmt = conn.prepareStatement("delete from "+getStagingTableName());
			pstmt.executeUpdate();
		}
		finally {
			if (pstmt!=null)
				pstmt.close();
			if (conn !=null)
				conn.close();
		}
	}
	
	public abstract String getStagingTableName();

	public ReportTaskDAO getReportTaskDAO() {
		return reportTaskDAO;
	}

	public void setReportTaskDAO(ReportTaskDAO reportTaskDAO) {
		this.reportTaskDAO = reportTaskDAO;
	}

}

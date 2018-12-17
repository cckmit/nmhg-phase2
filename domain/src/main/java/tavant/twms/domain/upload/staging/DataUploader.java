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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class DataUploader {
	private static Map<String, String> templateNameBasedUploadProc = new HashMap<String, String>();
	static {
		templateNameBasedUploadProc.put("partSourceHistory", "STG_Part_Source_History_UPLOAD");
	}
	
	@Transactional(readOnly=false)
	public boolean uploadData(Connection conn, String templateName, 
			long uploadLogId, String uploadProcedure) throws SQLException {
		String uploadProcedureCall = "";
		if (hasText(uploadProcedure))
		{
			uploadProcedureCall = new StringBuffer("CALL ")
										.append(uploadProcedure).append("()").toString();
		}
		else if (hasText(templateNameBasedUploadProc.get(templateName)))
		{
			uploadProcedureCall = new StringBuffer("CALL ")
									.append(templateNameBasedUploadProc.get(templateName))
									.append("()").toString();
		}
		else return false;
		
		return executeUploadProcedure(conn, uploadLogId, uploadProcedureCall);
	}

	private boolean hasText(String uploadProcedure) {
		return uploadProcedure!=null && !"".equals(uploadProcedure);
	}

	private boolean executeUploadProcedure(Connection conn, long uploadLogId,
			String uploadProcedureCall) throws SQLException {
		CallableStatement cstmt = null;
		try {			
			cstmt = conn.prepareCall(uploadProcedureCall);
			//cstmt.setInt(1, uploadLogId);
			cstmt.execute();
			return true;
		}
		catch (SQLException exception) {
			return Boolean.FALSE;
		}
		finally {
			if (cstmt != null)
				cstmt.close();
		}
	}

}

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

/**
 * @author jhulfikar.ali
 *
 */
public class DataValidator {

	private static Map<String, String> templateNameBasedValidationProc = new HashMap<String, String>();
	static {
		// TODO: Change the validation procedures if not available in the table
		templateNameBasedValidationProc.put("Job Code Upload", "JOB_CODE_VALIDATION");
	}
	
	@Transactional(readOnly=false)
	public boolean validate(Connection conn, String templateName, String validationProcedure) throws SQLException {
		String validationProcedureCall = "";
		if (hasText(validationProcedure))
		{
			validationProcedureCall = new StringBuffer("CALL ")
										.append(validationProcedure).append("()").toString();
		}
		else if (hasText(templateNameBasedValidationProc.get(templateName)))
		{
			validationProcedureCall = new StringBuffer("CALL ")
										.append(templateNameBasedValidationProc.get(templateName))
										.append("()").toString();
		}
		CallableStatement cstmt = null;
		try {
			cstmt = conn.prepareCall(validationProcedureCall);
			return cstmt.execute();
		}
		finally {
			if (cstmt != null)
				cstmt.close();
		}
	}

	private boolean hasText(String uploadProcedure) {
		return uploadProcedure!=null && !"".equals(uploadProcedure);
	}

}

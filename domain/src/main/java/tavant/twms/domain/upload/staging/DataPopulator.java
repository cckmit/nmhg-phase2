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

import org.springframework.transaction.annotation.Transactional;

/**
 * @author jhulfikar.ali
 *
 */

public class DataPopulator {

	@Transactional(readOnly=false)
	public boolean populate(Connection conn, String templateName, String populationProcedure) throws SQLException {
		CallableStatement cstmt = null;
		try {
			cstmt = conn.prepareCall("CALL " + populationProcedure + "()");
			return cstmt.execute();
		}
		finally {
			if (cstmt != null)
				cstmt.close();
		}
	}

}

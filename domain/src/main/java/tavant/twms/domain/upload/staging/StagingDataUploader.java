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

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jhulfikar.ali
 *
 */
public class StagingDataUploader {

	@Transactional(readOnly=false)
	public void uploadStagingData(Connection conn, File outFile, String schemaName) 
	throws DataSetException, IOException, SQLException, DatabaseUnitException {
		XlsDataSet ds = new XlsDataSet(outFile);
		
		IDatabaseConnection connection = new DatabaseConnection(conn,schemaName);
		DatabaseOperation.INSERT.execute(connection, ds);
	}
	
	@Transactional(readOnly=false)
	public boolean stageData(Connection conn, String stagingProcedure) throws SQLException {
		CallableStatement cstmt = null;
		try {
			cstmt = conn.prepareCall(
					"CALL " + stagingProcedure + "()");
			return cstmt.execute();
		}
		finally {
			if (cstmt != null)
				cstmt.close();
		}
	}

}

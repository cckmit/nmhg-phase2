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

package tavant.twms.domain.upload.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author jhulfikar.ali
 *
 */
public class DbLock {

	private static Logger logger = Logger.getLogger(DbLock.class
			.getName());
	
	private ReportTaskDAO reportTaskDAO;
	
	private Connection conn;
	
	private boolean status;
	
	public boolean acquireLock()throws Exception{
		Thread t=new Thread(){

			@Override
			public void run(){
				Statement stmt = null;				
				try{
					conn = reportTaskDAO.getSQLConnection();
					logger.debug("Acquiring lock on conn " + conn
							+ " by thread " + Thread.currentThread().getId());
					stmt = conn.createStatement();
					stmt.execute("LOCK TABLE lock_table IN EXCLUSIVE MODE NOWAIT");
					logger.debug("Acquired lock on conn " + conn
							+ " by thread " + Thread.currentThread().getId());
					status = true;
				}catch(Exception e){
					logger.error("Unable to acquire lock",e);
					status=false;
				}
				finally {
					try {
						if (stmt != null)
							stmt.close();
					} catch (SQLException e) {
						logger.error("Unable to close the statement: ", e);
					}
				}
			}
			
		};
		t.start();
		t.join();
		return status;
	}
	
	public void releaseLock()throws Exception{
		if(conn!=null)
		{
			logger.debug("Released lock acquired on conn "+conn);
			conn.close();
		}
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public ReportTaskDAO getReportTaskDAO() {
		return reportTaskDAO;
	}

	public void setReportTaskDAO(ReportTaskDAO reportTaskDAO) {
		this.reportTaskDAO = reportTaskDAO;
	}
	
}

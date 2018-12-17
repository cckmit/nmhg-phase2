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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import tavant.twms.domain.upload.controller.BlobUtil;
import tavant.twms.domain.upload.controller.ReceivedFileDetails;
import tavant.twms.domain.upload.controller.UploadStatusDetail;

/**
 * @author jhulfikar.ali
 *
 */
public class FileReceiverImpl implements FileReceiver {

	private static Logger logger = Logger.getLogger(FileReceiverImpl.class
			.getName());

	private BlobUtil blobUtil;

	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.upload.staging.FileReceiver#persistFileContents(java.io.File, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public void persistFileContents(final File file, final String templateName,
			final String fileName, final String currentUserBusinessUnit, final Long currentUser) throws SQLException, IOException {
		
		jdbcTemplate.execute(new ConnectionCallback() {

			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
					PreparedStatement ps = null;
					try {
						long pk = getPrimaryKeyValue(conn);
						ps = conn
								.prepareStatement("insert into file_upload_mgt(id,file_name,template_name,received_on," +
										"upload_status,total_records,success_records,error_records,business_unit_info,uploaded_by,file_content,error_file_content)"
										+ " values (?,?,?,?,?,?,?,?,?,?,empty_blob(),empty_blob())");
						ps.setLong(1, pk);
						ps.setString(2, fileName);
						ps.setString(3, templateName);
						ps.setTimestamp(4, getCurrentTimeStamp());
						ps.setInt(5, 0);
						ps.setInt(6, 0);
						ps.setInt(7, 0);
						ps.setInt(8, 0);
						ps.setString(9, currentUserBusinessUnit);
						ps.setLong(10, currentUser);
						int updatedCount = ps.executeUpdate();
						logger.debug("Updated Count is " + updatedCount);
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt
								.executeQuery("select file_content from file_upload_mgt where id = "
										+ pk + " for update");
						if (!rs.next())
							throw new RuntimeException("Unable to update file contents.");
						/*
						 * oracle.sql.BLOB blob = ((oracle.jdbc.OracleResultSet) rs)
						 * .getBLOB(1);
						 */
						if (rs.getBlob(1) instanceof Blob)
						{
							Blob blob = (Blob) rs.getBlob(1);
							blobUtil.writeToBlob(new FileInputStream(file), blob);
						}
						else if (rs.getBlob(1) instanceof oracle.sql.BLOB)
						{
							oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);
							blobUtil.writeToBlob(new FileInputStream(file), blob);
						}
					} catch (Exception e) {
						logger.error("Error while trying to upload file", e);
					} finally {
						if (ps != null)
							ps.close();
					}
				return null;
			}
			
		});
	}
	
	private static java.sql.Timestamp getCurrentTimeStamp() {
	    java.util.Date date = new java.util.Date();
	    return new java.sql.Timestamp(date.getTime());
	  }


	private long getPrimaryKeyValue(Connection conn) throws SQLException {
		Statement st = null;
		ResultSet result = null;
		try {
			st = conn.createStatement();
			result = st
					.executeQuery("select file_upload_mgt_seq.nextval from dual");
			if (!result.next())
				throw new RuntimeException(
				"Unable to get next sequence value for sequence [file_upload_mgt_seq]");
			return result.getLong(1);
		}
		finally {
			if (result!=null)
				result.close();
			if (st!=null)
				st.close(); 
		}
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.upload.staging.FileReceiver#getDataForStagingDBUpload(java.sql.Connection)
	 */
	@SuppressWarnings("unchecked")
	public List<ReceivedFileDetails> getDataForStagingDBUpload(Connection conn) throws SQLException {
		
//		return (List<ReceivedFileDetails>) jdbcTemplate.execute(new ConnectionCallback() {
//
//			public Object doInConnection(Connection conn) throws SQLException,
//					DataAccessException {
				Statement stmt = null;
				ResultSet rs = null;
				List<ReceivedFileDetails> receivedFiles = new ArrayList<ReceivedFileDetails>();
					try {
						stmt = conn.createStatement();
						rs = stmt
								.executeQuery("select fum.id, fum.template_name, u.login, u.locale, fum.file_content  " +
										" from file_upload_mgt fum, org_user u "
										+ " where  fum.retry_count<2 " +
												" and fum.uploaded_by = u.id " +
												" and fum.id NOT IN (SELECT id FROM file_upload_mgt cur WHERE cur.template_name=fum.template_name " +
													" AND cur.upload_status NOT IN ( " +
														UploadStatusDetail.STATUS_NOT_PROCESSED + "," +
														UploadStatusDetail.STATUS_UPLOADED + "," +
														UploadStatusDetail.STATUS_FAILED + " ) )" +
												" and fum.id = (SELECT MIN(id) FROM file_upload_mgt t WHERE t.template_name=fum.template_name " +
													" AND t.upload_status="+UploadStatusDetail.STATUS_NOT_PROCESSED+" ) " +
										" order by fum.received_on asc");
						while (rs.next()) {
							logger.info("Got a record to upload in staging db. Id: " + rs.getLong(1) + "; Template: " + rs.getString(2));
							ReceivedFileDetails data = new ReceivedFileDetails();
							data.setId(rs.getLong(1));
							data.setTemplateName(rs.getString(2));
							data.setUploadedBy(rs.getString(3));
							data.setUploaderLocale(rs.getString(4));
							if (rs.getBlob(5) instanceof Blob)
							{
								Blob blob = (Blob) rs.getBlob(5);
								data.setFileContents(blob.getBinaryStream());
							}
							else if (rs.getBlob(5) instanceof oracle.sql.BLOB)
							{
								oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(5);					
								data.setFileContents(blob.getBinaryStream());
							}			
							
							receivedFiles.add(data);
						}
					}
					finally {
						if (rs != null)
							rs.close();
						if (stmt != null)
							stmt.close();
					}
				return receivedFiles;
//			}
//			
//		});
		
	}

	
	@SuppressWarnings("unchecked")
	public ReceivedFileDetails getFileReceivedById(final long id,Connection conn) throws SQLException {
				Statement stmt = null;
				ResultSet rs = null;
				ReceivedFileDetails receivedFile = null;
					try {
						stmt = conn.createStatement();
						rs = stmt
								.executeQuery("select fum.id, fum.template_name, u.login, u.locale, fum.file_content " +
										" from file_upload_mgt fum, org_user u "
										+ " where fum.uploaded_by=u.id and fum.id = "+id);
						if (rs.next()) {
							receivedFile = new ReceivedFileDetails();
							receivedFile.setId(rs.getLong(1));
							receivedFile.setTemplateName(rs.getString(2));
							receivedFile.setUploadedBy(rs.getString(3));
							receivedFile.setUploaderLocale(rs.getString(4));
							if (rs.getBlob(5) instanceof Blob)
							{
								Blob blob = (Blob) rs.getBlob(5);
								receivedFile.setFileContents(blob.getBinaryStream());
							}
							else if (rs.getBlob(5) instanceof oracle.sql.BLOB)
							{
								oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(5);					
								receivedFile.setFileContents(blob.getBinaryStream());
							}			
						}
					}
					finally {
						if (rs != null)
							rs.close();
						if (stmt != null)
							stmt.close();
					}
				return receivedFile;
			
		
	}
	
	/* (non-Javadoc)
	 * @see tavant.twms.domain.upload.staging.FileReceiver#prepareAuditRecord(java.sql.Connection, int, tavant.twms.domain.upload.controller.UploadStatusDetail)
	 */
	public void prepareAuditRecord(final long pk, final UploadStatusDetail usd)
			throws SQLException {
		jdbcTemplate.execute(new ConnectionCallback() {

			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				PreparedStatement ps = null;
				try {
					ps = conn.prepareStatement(
							"update file_upload_mgt set upload_status=?,total_records=?,"
									+ "success_records=?,error_records=?,error_message=? where id = ?");
					ps.setInt(1, usd.getUploadStatus());
					ps.setInt(2, usd.getTotalRecords());
					ps.setInt(3, usd.getSuccessRecords());
					ps.setInt(4, usd.getErrorRecords());
					ps.setString(5, usd.getErrorMessage());
					ps.setLong(6, pk);
					ps.executeUpdate();
				} finally {
					 if (ps!=null)
						 ps.close();
				}
				return null;
			}
			
		});
	}

	public void updateFileUploadStatus(final long pk, final int status)
	throws SQLException {
		jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				PreparedStatement ps = null;
				try {
					ps = conn.prepareStatement(
							"update file_upload_mgt set upload_status = ? where id = ?");
					ps.setInt(1, status);
					ps.setLong(2, pk);
					ps.executeUpdate();
				} finally {
					 if (ps!=null)
						 ps.close();
				}
				return null;
			}
		});
	}
	
	public BlobUtil getBlobUtil() {
		return blobUtil;
	}

	public void setBlobUtil(BlobUtil blobUtil) {
		this.blobUtil = blobUtil;
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.upload.staging.FileReceiver#getErrorBlob(java.sql.Connection, int)
	 */
	public Blob getErrorBlob(Connection conn, final long pk) throws SQLException {
		return getErrorFile(conn, pk, Boolean.TRUE);
	}

	private Blob getErrorFile(Connection conn, final long pk, Boolean forUpdate)
			throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet errorRs = stmt.executeQuery("select error_records from file_upload_mgt where id = " + pk);
			if (!errorRs.next())
				throw new RuntimeException("Unable to update error file contents.");
			//No point in writing an error file if there are no error records.
			if (errorRs.getInt(1) > 0) {
				StringBuffer sqlQuery = new StringBuffer("select error_file_content " +
						" from file_upload_mgt where id = " + pk);
				if (forUpdate)
					sqlQuery.append(" for update");
					
				ResultSet rs = stmt.executeQuery(sqlQuery.toString());
				if (!rs.next())
					throw new RuntimeException("Unable to update error file contents.");
				// return ((oracle.jdbc.OracleResultSet) rs).getBLOB(1);
				
				return rs.getBlob(1);
			}
		} finally {
			 if (stmt!=null)
				 stmt.close();
		}
		return null;
	}

	private File writeBlobToTempFile(Blob blob, String fileName, String ext)
			throws IOException, SQLException {
		File tempFile = File.createTempFile(fileName, ext);
		OutputStream out = new FileOutputStream(tempFile);
		FileCopyUtils.copy(blob.getBinaryStream(), out);
		out.close();
		return tempFile;
	}

	public File getErrorBlobReadOnly(Connection conn, Long id)
			throws SQLException, IOException {
		Blob errorBlob = getErrorFile(conn, id.longValue(), Boolean.FALSE);
		return writeBlobToTempFile(errorBlob, "error" + id, "xls");
	}

	public File getUploadedBlobReadOnly(Connection conn, Long id)
			throws SQLException, IOException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sqlQuery = "select file_content "
					+ " from file_upload_mgt where id = " + id;
			ResultSet rs = stmt.executeQuery(sqlQuery);
			if (!rs.next())
				throw new RuntimeException(
						"Unable to get uploaded file contents.");
			return writeBlobToTempFile(rs.getBlob(1), "upload" + id, "xls");
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see tavant.twms.domain.upload.staging.FileReceiver#cleanStagingTable(java.sql.Connection, java.lang.String)
	 */
	public void cleanStagingTable(final String stagingTable) throws SQLException {
		
		jdbcTemplate.execute(new ConnectionCallback() {

			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
		
				PreparedStatement ps = null;
				try {
					ps = conn.prepareStatement("delete from " + stagingTable);
					ps.executeUpdate();
				} finally {
					 if (ps!=null)
						 ps.close();
				}
				return null;
			}
			
		});
	}

	public void backupStagingTable(final String stagingTable, final String backupTable) throws SQLException {
		jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				PreparedStatement ps = null;
				try {
					ps = conn.prepareStatement("insert into "+ backupTable+" select * from " + stagingTable);
					ps.executeUpdate();
				}catch(Exception e){
				} finally {
					 if (ps!=null)
						 ps.close();
				}
				return null;
			}
		});
	}

	public void writeErrorContentsToBlob(Connection conn ,long fileUploadId, File tempFile) {
		try {
			Blob blob = getErrorBlob(conn, fileUploadId);
			blobUtil.writeToBlob(new FileInputStream(tempFile), blob);
		} catch (Exception e) {
			logger.error("Error while trying to upload file", e);
		} 
	}
}
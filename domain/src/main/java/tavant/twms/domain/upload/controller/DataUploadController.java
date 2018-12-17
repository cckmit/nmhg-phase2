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

package tavant.twms.domain.upload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import tavant.twms.domain.common.I18NUploadErrorText;
import tavant.twms.domain.upload.connection.DbLock;
import tavant.twms.domain.upload.connection.ReportTaskDAO;
import tavant.twms.domain.upload.errormgt.ErrorReportGeneratorFactory;
import tavant.twms.domain.upload.staging.DataPopulator;
import tavant.twms.domain.upload.staging.DataUploader;
import tavant.twms.domain.upload.staging.DataValidator;
import tavant.twms.domain.upload.staging.FileReceiver;
import tavant.twms.domain.upload.staging.StagingDAO;
import tavant.twms.domain.upload.staging.StagingDAOFactory;
import tavant.twms.domain.upload.staging.StagingDataUploader;
import tavant.twms.security.SecurityHelper;

/**
 * @author jhulfikar.ali
 *
 */
public class DataUploadController {

	private static Logger logger = Logger.getLogger(DataUploadController.class.getName());
	
	private ReportTaskDAO reportTaskDAO;

	private TemplateTransformerFactory templateTransformerFactory;

	private ErrorReportGeneratorFactory errorReportGeneratorFactory;

	private DataValidator dataValidator;

	private DataPopulator dataPopulator;
	
	private DataUploader dataUploader;

	private FileReceiver fileReceiver;
	
	private StagingDataUploader stagingDataUploader;

	private StagingDAOFactory stagingDAOFactory;

	private BlobUtil blobUtil;
	
	private PostUplaodActionFactory postUplaodActionFactory;
	
	private BeanFactory beanFactory;
	
	private DbLock dbLock;
	
	private DataUploadConfig dataUploadConfig;
	
	private JdbcTemplate jdbcTemplate;
	
	
	
	@SuppressWarnings("unchecked")
	public synchronized void uploadData() throws Exception {

		jdbcTemplate.execute(new ConnectionCallback() {

			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				logger.debug("Starting upload process by thread "
						+ Thread.currentThread().getId());
				logger.debug("Starting upload process in exclusive locking mode "
						+ dataUploadConfig.isExclusiveLockingMode());

				File formattedFile = null;
				File errorFile = null;
				
				try {
					populateDummyAuthentication();
					List<UploadManagement> allAvailableUploads = findAllUploadManagementServices(conn);
					UploadManagement currentDataUpload = null;
					
					// TODO: Fix ME: Acquiring lock has some issue
					/*
				if(dataUploadConfig.isExclusiveLockingMode())
				{
					if(!dbLock.acquireLock())
					{
						logger.info("Couldn't acquire lock. Will try after some time.");
						return;
					}
				} */
									
					logger.debug("Starting upload process by thread "
							+ Thread.currentThread().getId()+" and the connection object is "+conn);
					
					List<ReceivedFileDetails> receivedFiles = fileReceiver.getDataForStagingDBUpload(conn);
					for (Iterator<ReceivedFileDetails> filesIterator = receivedFiles.iterator(); filesIterator
							.hasNext();) {
						try {
							ReceivedFileDetails data = (ReceivedFileDetails) filesIterator.next();
							
							UploadStatusDetail uploadStatus = new UploadStatusDetail();
							uploadStatus.setErrorMessage(null);
							
							if (data != null) {
								String templateName = data.getTemplateName();
								logger.info("Starting upload process for template[" + templateName + "]");
				
								try {
									int status = UploadStatusDetail.STATUS_PROCESSING;
									fileReceiver.updateFileUploadStatus(data.getId(), status);
									// Get the current uploads properties for further activities
									currentDataUpload = getCurrentDataUpload(allAvailableUploads, templateName);
									List<UploadError> uploadErrors = getUploadErrorsForUploadMgt(conn, currentDataUpload.getId());
									currentDataUpload.setUploadErrors(uploadErrors);
									List<UploadManagementMetaData> uploadManagementMetaDatas = getUploadMgtMetaDataForUploadMgt(conn, currentDataUpload.getId());
									//currentDataUpload.setUploadManagementMetaDatas(uploadManagementMetaDatas);
									
									status = UploadStatusDetail.STATUS_STAGING;
									File dir=new File(dataUploadConfig.getTempLocation());
									formattedFile = File.createTempFile("outdata",  "xls", dir);
									uploadStatus = cleanStagedAndPrepareUploadStatus(
											conn, currentDataUpload, formattedFile, data,
											templateName,uploadManagementMetaDatas);
									
									if(uploadStatus.getUploadStatus() == UploadStatusDetail.STATUS_FAILED)
										throw new RuntimeException(uploadStatus.getErrorMessage());
									
									try {
										stagingDataUploader.uploadStagingData(conn, formattedFile, dataUploadConfig.getSchemaName());
										logger.info("Uploaded data to satging db");
									}catch(Exception e) {
										uploadStatus.setErrorMessage("Invalid template");
										throw e;
									}
									
									status = UploadStatusDetail.STATUS_VALIDATING;
									uploadStatus.setUploadStatus(status);
									fileReceiver.prepareAuditRecord(data.getId(), uploadStatus);
				
									// Stage the data, Validate the data and populate the data
									processUserData(conn, currentDataUpload, templateName);
									status = UploadStatusDetail.STATUS_UPLOADING;
									fileReceiver.updateFileUploadStatus(data.getId(), status);
									
									// Upload Procedure to upload the data into real data for use
									boolean dataUploaded = dataUploader.uploadData(conn, templateName, data.getId(), currentDataUpload
											.getUploadProcedure());
									
									// Do upload post processing
									processPostUploadActivities(data, templateName);
									
									if (dataUploaded || templateName.equalsIgnoreCase("Supplier Decision Upload")) {
										// Generating error file
										status = UploadStatusDetail.STATUS_ERR_REPORTING;
										fileReceiver.updateFileUploadStatus(data.getId(), status);
										generateErrorFileForUser(data.getId(), currentDataUpload);
										status = UploadStatusDetail.STATUS_UPLOADED;
									}
									else {
										status = UploadStatusDetail.STATUS_WAITING_FOR_UPLOAD;
									}
									
									fileReceiver.updateFileUploadStatus(data.getId(), status);

									logger.info("Executed upload process");
								} catch (Exception exception) {
									logger.error("Error while uploading " + templateName
											+ " data for id: " + data.getId(), exception);
									//uploadStatus.setTotalRecords(0);
									uploadStatus.setSuccessRecords(0);
									uploadStatus.setErrorRecords(0);
									uploadStatus.setUploadStatus(UploadStatusDetail.STATUS_FAILED);
									fileReceiver.prepareAuditRecord(data.getId(), uploadStatus);
								}
							}
							else {
								logger.info("No records found for upload.");
							}
						}
						catch (Exception exception)
						{
							logger.error("Exception in DataUploadController: " + exception.getMessage());
						}
						finally {
							
						}
					} // End of FOR LOOP (ReceivedFiles collection)
				}
				catch (Exception exception)
				{
					logger.error("Exception in DataUploadController: " + exception.getMessage());
				}
				finally {
					if (conn!=null)
						try {
							conn.close();
							// TODO: Fix ME: Use the below code after fixing the acquire lock issue
							/*if(dbLock!=null) {
								dbLock.releaseLock();
							}*/
						} catch (SQLException e) {
							logger.error("Exception in DataUploadController: " + e.getMessage());
						}
					if (formattedFile != null)
						formattedFile.delete();
				}
				return null; // No need for any return statements as of now
			}
		});
	}

	private List<UploadManagement> findAllUploadManagementServices(Connection conn) throws SQLException {
		List<UploadManagement> uploadServices = new ArrayList<UploadManagement>();
		PreparedStatement ps = conn.prepareStatement("SELECT name_of_template, name_to_display, " +
				" description, staging_table, staging_procedure, validation_procedure, " +
				" upload_procedure, population_procedure, columns_to_capture, consume_rows_from, " +
				" header_row_to_capture, id, backup_table " +
				" from upload_mgt");
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			UploadManagement uploadService = new UploadManagement();
			uploadService.setNameOfTemplate(rs.getString(1));
			uploadService.setNameToDisplay(rs.getString(2));
			uploadService.setDescription(rs.getString(3));
			uploadService.setStagingTable(rs.getString(4));			
			uploadService.setStagingProcedure(rs.getString(5));			
			uploadService.setValidationProcedure(rs.getString(6));
			uploadService.setUploadProcedure(rs.getString(7));
			uploadService.setPopulationProcedure(rs.getString(8));
			uploadService.setColumnsToCapture(rs.getLong(9));
			uploadService.setConsumeRowsFrom(rs.getLong(10));
			uploadService.setHeaderRowToCapture(rs.getLong(11));
			uploadService.setId(rs.getLong(12));
			uploadService.setBackupTable(rs.getString(13));
			uploadServices.add(uploadService);
		}
		return uploadServices;
	}
	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

	private void processUserData(Connection conn, UploadManagement currentDataUpload,
			String templateName) throws SQLException {
		// Staging Procedure for future support activities
		if ( hasText(currentDataUpload.getStagingProcedure()) )
		{
			stagingDataUploader.stageData(conn, currentDataUpload.getStagingProcedure());
			logger.info("Populated stage data into Staging Table using procedure: " 
					+ currentDataUpload.getStagingProcedure());
		}

		// Validation Procedure to validate the given data by user
		dataValidator.validate(conn, templateName, currentDataUpload.getValidationProcedure());
		logger.info("Executed validation process using procedure: " + 
				currentDataUpload.getValidationProcedure());

		// Population Procedure to populate the data for upload
		if (hasText(currentDataUpload.getPopulationProcedure()))
		{
			dataPopulator.populate(conn, templateName, currentDataUpload.getPopulationProcedure());
			logger.info("Executed validation process using procedure: " + 
					currentDataUpload.getPopulationProcedure());
		}
	}

	private UploadStatusDetail cleanStagedAndPrepareUploadStatus(
			Connection conn, UploadManagement currentDataUpload, File formattedFile,
			ReceivedFileDetails data, String templateName,List<UploadManagementMetaData> uploadManagementMetaDatas) throws SQLException,
			IOException, FileNotFoundException {
		// Clean the existing records in the staging table and 
		// prepare the upload status based on new file
		UploadStatusDetail uploadStatus = new UploadStatusDetail();
		if (hasText(currentDataUpload.getStagingTable()))
		{
			if (hasText(currentDataUpload.getBackupTable())) 
				fileReceiver.backupStagingTable(currentDataUpload.getStagingTable(),currentDataUpload.getBackupTable());
			fileReceiver.cleanStagingTable(currentDataUpload.getStagingTable());
			TemplateTransformer templateTransformer = templateTransformerFactory.getTemplateTransformer(templateName);
			if(templateTransformer==null)
				templateTransformer = templateTransformerFactory.getGenericTemplateTransformer(currentDataUpload.getStagingTable());
			uploadStatus = templateTransformer
				.transform(data.getFileContents(),
					new FileOutputStream(formattedFile), data.getId(), 
					dataUploadConfig.getUploadRecordsLimit(), 
					currentDataUpload,uploadManagementMetaDatas);
		}
		else
		{// Will search in the app-context.xml for the staging table; May not be required in future...! :)
			StagingDAO stagingDAO = stagingDAOFactory.getStagingDAO(templateName);
			stagingDAO.clean();
			uploadStatus = templateTransformerFactory.getTemplateTransformer(
					templateName).transform(data.getFileContents(),
					new FileOutputStream(formattedFile), data.getId(), 
					dataUploadConfig.getUploadRecordsLimit(), 
					currentDataUpload,uploadManagementMetaDatas);
		}
		return uploadStatus;
	}

	private UploadManagement getCurrentDataUpload(List<UploadManagement> allAvailableUploads,
			String templateName) {
		UploadManagement currentDataUpload = null;
		// Get me the proper and required data upload for the current upload activity
		for (Iterator<UploadManagement> iterator = allAvailableUploads.iterator(); iterator
				.hasNext();) {
			UploadManagement uploadManagement = (UploadManagement) iterator.next();
			if (templateName.equals(uploadManagement.getNameToDisplay()))
				currentDataUpload = uploadManagement;
		}
		return currentDataUpload;
	}

	private void generateErrorFileForUser(Connection conn, ReceivedFileDetails data, String stagingTable) 
	throws SQLException, Exception {
		// Generating error file
		try {
			Blob errorFile = fileReceiver.getErrorBlob(conn, data.getId());
			if (errorFile!=null)
			{
				File dir=new File(dataUploadConfig.getTempLocation());
				File tempFile = File.createTempFile("errorFile", "xls",dir);
				errorReportGeneratorFactory.getErrorReportGenerator()
						.generateErrorReport(conn, new FileOutputStream(tempFile), 
								populateErrorReportQuery(stagingTable));
				blobUtil.writeToBlob(new FileInputStream(tempFile), errorFile);
				logger.info("Finished error report generation for Stagin table ["+ stagingTable + "]");
			}
		} catch (Exception exception) {
			logger.error("Error while generating error file for "+ stagingTable +
					" upload for id: " + data.getId(), exception);
			throw exception;
		}
	}
	
	private void generateErrorFileForUser(final long dataFileId, final UploadManagement dataUpload) 
	throws SQLException {
		jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				String stagingTable = dataUpload.getStagingTable();
				ReceivedFileDetails data = fileReceiver.getFileReceivedById(dataFileId,conn);
				try {
					Blob errorFile = fileReceiver.getErrorBlob(conn, data.getId());
					if (errorFile!=null){
						File dir=new File(dataUploadConfig.getTempLocation());
						File tempFile = File.createTempFile("errorFile", "xls",dir);
						errorReportGeneratorFactory.getErrorReportGenerator()
								.generateErrorReport(conn, new FileOutputStream(tempFile), 
										data, dataUpload);
						blobUtil.writeToBlob(new FileInputStream(tempFile), errorFile);
						logger.info("Finished error report generation for Stagin table ["+ stagingTable + "]");
					}
				} catch (Exception exception) {
					logger.error("Error while generating error file for "+ stagingTable + 
							" upload for id: " + data.getId(), exception);
				}
				return null;
			}
		});
	}
	
	private void generateErrorFileForUser(Connection conn, long dataFileId, UploadManagement dataUpload) 
	throws SQLException, Exception {
		// Generating error file
		String stagingTable = dataUpload.getStagingTable();
		ReceivedFileDetails data = this.fileReceiver.getFileReceivedById(dataFileId,conn);
		try {
			Blob errorFile = fileReceiver.getErrorBlob(conn, data.getId());
			if (errorFile!=null)
			{   File dir=new File(dataUploadConfig.getTempLocation());
				File tempFile = File.createTempFile("errorFile", "xls",dir);
				errorReportGeneratorFactory.getErrorReportGenerator()
						.generateErrorReport(conn, new FileOutputStream(tempFile), 
								data, dataUpload);
				blobUtil.writeToBlob(new FileInputStream(tempFile), errorFile);
				logger.info("Finished error report generation for Stagin table ["+ stagingTable + "]");
			}
		} catch (Exception exception) {
			logger.error("Error while generating error file for "+ stagingTable + 
					" upload for id: " + data.getId(), exception);
			throw exception;
		}
	}

	private List<UploadError> getUploadErrorsForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException {
		Statement stmt = null;
		List<UploadError> uploadErrors = new ArrayList<UploadError>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT e.id, e.code, e.upload_field, t.locale, t.description " +
					"FROM upload_mgt_upload_errors me, upload_error e, " +
					"    i18nupload_error_text t " +
					"WHERE e.id = t.upload_error AND e.id = me.upload_errors " +
					"      AND me.upload_mgt = "+uploadMgtId +
					" ORDER BY e.id ");
			UploadError currentError = null;
			while(rs.next()) {
				Long id = rs.getLong(1);
				if(currentError != null && currentError.getId() != id) {
					uploadErrors.add(currentError);
					currentError = null;
				}
				if(currentError == null) {
					currentError = new UploadError();
					currentError.setId(id);
					currentError.setCode(rs.getString(2));
					currentError.setUploadField(rs.getString(3));
				}
				I18NUploadErrorText text = new I18NUploadErrorText();
				text.setLocale(rs.getString(4));
				text.setDescription(rs.getString(5));
				currentError.getI18nUploadErrorTexts().add(text);			
			}
			if(currentError != null)
				uploadErrors.add(currentError);
		} finally {
			 if (stmt!=null)
				 stmt.close();
		}
		return uploadErrors;
	}

	private List<UploadManagementMetaData> getUploadMgtMetaDataForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException {
		Statement stmt = null;
		List<UploadManagementMetaData> uploadManagementMetaDatas = new ArrayList<UploadManagementMetaData>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select id, column_name, column_type, column_order from upload_mgt_meta_data where upload_mgt=" +
					uploadMgtId +
					" order by column_order ");
			
			while(rs.next()) {
				UploadManagementMetaData currentData = new UploadManagementMetaData();
				currentData.setId(rs.getLong("id"));
				currentData.setColumnName(rs.getString("column_name"));
				currentData.setColumnType(rs.getString("column_type"));
				currentData.setColumnOrder(rs.getShort("column_order"));
				uploadManagementMetaDatas.add(currentData);
			}
		} finally {
			 if (stmt!=null)
				 stmt.close();
		}
		return uploadManagementMetaDatas;
	}

	
	private String populateErrorReportQuery(String stagingTable) {
		return new StringBuffer("SELECT * FROM ")
				.append(stagingTable)
				.append(" WHERE NVL(ERROR_STATUS, '-') = 'N' ").toString(); 
	}
	
	private void processPostUploadActivities(ReceivedFileDetails data,
			String templateName) {
		// Post upload activities
		PostUploadAction postUploadAction = postUplaodActionFactory.getPostUploadAction(templateName);
		try{
			if(postUploadAction!=null)
				postUploadAction.doUplaodPostProcessing(data.getId());
		}catch(Exception e){
			logger.error("Error in performing post upload action",e);
		}
	}

	private boolean hasText(String uploadProcedure) {
		return uploadProcedure!=null && !"".equals(uploadProcedure);
	}

	public ReportTaskDAO getReportTaskDAO() {
		return reportTaskDAO;
	}

	public void setReportTaskDAO(ReportTaskDAO reportTaskDAO) {
		this.reportTaskDAO = reportTaskDAO;
	}

	public TemplateTransformerFactory getTemplateTransformerFactory() {
		return templateTransformerFactory;
	}

	public void setTemplateTransformerFactory(
			TemplateTransformerFactory templateTransformerFactory) {
		this.templateTransformerFactory = templateTransformerFactory;
	}

	public DataValidator getDataValidator() {
		return dataValidator;
	}

	public void setDataValidator(DataValidator dataValidator) {
		this.dataValidator = dataValidator;
	}

	public DataUploader getDataUploader() {
		return dataUploader;
	}

	public void setDataUploader(DataUploader dataUploader) {
		this.dataUploader = dataUploader;
	}

	public FileReceiver getFileReceiver() {
		return fileReceiver;
	}

	public void setFileReceiver(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;
	}

	public StagingDataUploader getStagingDataUploader() {
		return stagingDataUploader;
	}

	public void setStagingDataUploader(StagingDataUploader stagingDataUploader) {
		this.stagingDataUploader = stagingDataUploader;
	}

	public StagingDAOFactory getStagingDAOFactory() {
		return stagingDAOFactory;
	}

	public void setStagingDAOFactory(StagingDAOFactory stagingDAOFactory) {
		this.stagingDAOFactory = stagingDAOFactory;
	}

	public ErrorReportGeneratorFactory getErrorReportGeneratorFactory() {
		return errorReportGeneratorFactory;
	}

	public void setErrorReportGeneratorFactory(
			ErrorReportGeneratorFactory errorReportGeneratorFactory) {
		this.errorReportGeneratorFactory = errorReportGeneratorFactory;
	}

	public BlobUtil getBlobUtil() {
		return blobUtil;
	}

	public void setBlobUtil(BlobUtil blobUtil) {
		this.blobUtil = blobUtil;
	}
	
	public PostUplaodActionFactory getPostUplaodActionFactory() {
		return postUplaodActionFactory;
	}

	public void setPostUplaodActionFactory(
			PostUplaodActionFactory postUplaodActionFactory) {
		this.postUplaodActionFactory = postUplaodActionFactory;
	}

	public DbLock getDbLock() {
		return dbLock;
	}

	public void setDbLock(DbLock dbLock) {
		this.dbLock = dbLock;
	}

	public DataUploadConfig getDataUploadConfig() {
		return dataUploadConfig;
	}

	public void setDataUploadConfig(DataUploadConfig dataUploadConfig) {
		this.dataUploadConfig = dataUploadConfig;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public DataPopulator getDataPopulator() {
		return dataPopulator;
	}

	public void setDataPopulator(DataPopulator dataPopulator) {
		this.dataPopulator = dataPopulator;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
}

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
 *
 */
package tavant.twms.integration.layer.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import tavant.twms.domain.upload.StagingData;
import tavant.twms.domain.upload.StagingError;
import tavant.twms.domain.upload.controller.UploadManagement;
import tavant.twms.domain.upload.controller.UploadManagementService;
import tavant.twms.domain.upload.controller.UserFileUpload;
import tavant.twms.domain.upload.staging.FileReceiver;

/**
 * An helper class for upload, as the spring will not inject the services required to a Job class
 * @author prasad.r
 */
public class UploadService {
    private JdbcTemplate jdbcTemplate;
    private UploadManagementService uploadManagementService;
    private FileReceiver fileReceiver;
    
    public UploadManagement getUploadMgtDetail(String templateName){
        return uploadManagementService.findByTemplateName(templateName);
    }

    public FileReceiver getFileReceiver() {
        return fileReceiver;
    }

    public void setFileReceiver(FileReceiver fileReceiver) {
        this.fileReceiver = fileReceiver;
    }

    public UploadManagementService getUploadManagementService() {
        return uploadManagementService;
    }

    public void setUploadManagementService(UploadManagementService uploadManagementService) {
        this.uploadManagementService = uploadManagementService;
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> loadReceivedFileDetails(String templateName) {
        return uploadManagementService.loadIdsByTemplateNameForProcessing(templateName);
    }
    
    public void backupStagingTable(final String stagingTable, final String backupTable) {
        this.jdbcTemplate.execute("INSERT INTO " + backupTable + " SELECT * FROM " + stagingTable);
    }
    
    public void cleanStagingTable(final String stagingTable) {
        this.jdbcTemplate.execute("DELETE FROM " + stagingTable);
    }

    public void insertIntoTable(List<String> insertQuries) {
        getJdbcTemplate().batchUpdate(insertQuries.toArray(new String[insertQuries.size()]));
        
    }

    public void updateStagingDBRecords(String stagingTable, final List<? extends StagingData> stagingDataBeans) {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(stagingTable);
        sb.append(" set ERROR_CODE = ?");
        sb.append(", ERROR_STATUS = ?");
        sb.append(", UPLOAD_STATUS = ?");
        sb.append(" where id = ?");
        this.jdbcTemplate.batchUpdate(sb.toString(), new BatchPreparedStatementSetter(){
             public int getBatchSize(){
                 return stagingDataBeans.size();
             }
        
             public void setValues(PreparedStatement   ps, int i) throws SQLException  {
                StagingData bean = stagingDataBeans.get(i);
                 ps.setString(1, bean.getErrorCode() );
                 ps.setString(2, bean.getErrorStatus());
                 ps.setString(3, bean.getUploadStatus());
                 ps.setLong(4, bean.getId());
             }
        });
    }

    public void createErrorFile(InputStream fileContents, List<StagingError> stagingErrors) {
        
    }

    public void updateUploadFileStatus(long id, long totalRedcords, long errorCount, int status, String errorMessage) {
        StringBuilder sb = new StringBuilder("UPDATE file_upload_mgt set TOTAL_RECORDS = ");
        sb.append(totalRedcords);
        sb.append(", ERROR_RECORDS = ").append(errorCount);
        sb.append(", SUCCESS_RECORDS = ").append((totalRedcords-errorCount));
        sb.append(", UPLOAD_STATUS = ").append(status);
        if(StringUtils.isNotEmpty(errorMessage))
            sb.append(", ERROR_MESSAGE = '").append(errorMessage).append("'");
        sb.append(" where id = ").append(id);
        this.jdbcTemplate.execute(sb.toString());
    }
    
    public void updateUploadFileStatus(long id, int status){
        StringBuilder sb = new StringBuilder("UPDATE file_upload_mgt set UPLOAD_STATUS = ");
        sb.append(status).append(" where id = ").append(id);
        this.jdbcTemplate.execute(sb.toString());
    }

    public UserFileUpload findFileUploadById(Long fileUploadId){
        return this.uploadManagementService.findFileUploadedById(fileUploadId);
    }

    public List<StagingError> loadErrorRecords(String stagingTable, Long id) {
        StringBuilder builder = new StringBuilder("SELECT ID, FILE_UPLOAD_MGT_ID,ERROR_CODE from ");
        builder.append(stagingTable);
        builder.append(" WHERE NVL(error_status, '-') = 'Y'");
        return jdbcTemplate.query(builder.toString(), new RowMapper<StagingError>() {
            public StagingError mapRow(ResultSet rs, int i) throws SQLException {
                StagingError se = new StagingError();
                se.setId(rs.getLong(1));
                se.setFileUploadMgtId(rs.getLong(2));
                se.setErrorCode(rs.getString(3));
                return se;
            }
        });
    }

    public Long getTotalRowCount(Long id, String stagingTable) {
        StringBuilder builder = new StringBuilder("SELECT count(ID) from ");
        builder.append(stagingTable);
        builder.append(" WHERE FILE_UPLOAD_MGT_ID = ");
        builder.append(id);
        return this.jdbcTemplate.queryForLong(builder.toString());
    }

    public String getUploadedFilePath(Long fileId) throws IOException, SQLException {
    	File f = this.uploadManagementService.getUploadedFileById(fileId);
        return f.getAbsolutePath();
    }
}

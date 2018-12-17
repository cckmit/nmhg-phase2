package tavant.twms.domain.upload.staging;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.upload.controller.ReceivedFileDetails;
import tavant.twms.domain.upload.controller.UploadStatusDetail;

@Transactional(readOnly=false)
public interface FileReceiver {

	public void persistFileContents(File file, String templateName,
			String fileName, String currentUserBusinessUnit, Long currentUser)
			throws SQLException, IOException;

	public List<ReceivedFileDetails> getDataForStagingDBUpload(Connection con) throws SQLException;

	public ReceivedFileDetails getFileReceivedById(final long id,Connection con) throws SQLException;
	
	public void prepareAuditRecord(long pk, UploadStatusDetail usd) throws SQLException;

	public void updateFileUploadStatus(final long pk, final int status) throws SQLException;
	
	public Blob getErrorBlob(Connection conn, long pk) throws SQLException;

	@Transactional
	public void cleanStagingTable(String stagingTable) throws SQLException;

	@Transactional
	public void backupStagingTable(String stagingTable, String backupTable) throws SQLException;
	
	public File getErrorBlobReadOnly(Connection conn, Long id) throws SQLException, IOException;
	
	public File getUploadedBlobReadOnly(Connection conn, Long id) throws SQLException, IOException;

	public void writeErrorContentsToBlob(Connection conn ,long fileUploadId, File tempFile);
}

package tavant.twms.domain.upload.history;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class UploadHistoryServiceImpl extends GenericServiceImpl<UploadHistory, Long, Exception>
        implements UploadHistoryService {
	private static final Logger logger = Logger	.getLogger(UploadHistoryServiceImpl.class);
    private UploadHistoryRepository uploadHistoryRepository;

    public List<UploadHistory> findUploadHistory(int pageSize, String type) {
        return uploadHistoryRepository.findUploadHistory(pageSize, type);
    }

    public void setUploadHistoryRepository(UploadHistoryRepository uploadHistoryRepository) {
        this.uploadHistoryRepository = uploadHistoryRepository;
    }

    @Override
    public GenericRepository<UploadHistory, Long> getRepository() {
        return uploadHistoryRepository;
    }
    
    public UploadHistory findPartInventoryUploadHistory(Long id){
    	return this.uploadHistoryRepository.findPartInventoryUploadHistory(id);
    }
    
    public Blob getErrorFileContentById(Long id) {
		return this.uploadHistoryRepository.getErrorFileContentById(id);
	}
    public void downloadFile(Long docId, OutputStream downloadStream){
		Blob content = getErrorFileContentById(docId);
		try {
			FileCopyUtils.copy(content.getBinaryStream(), downloadStream);
		} catch (IOException e) {
			logger.error("IOException while downloading File In downloadFile()" + e.getMessage(), e);
		} catch (SQLException e) {
			logger.error("SQLException while downloading File In downloadFile()" + e.getMessage(), e);
		}
	}
}

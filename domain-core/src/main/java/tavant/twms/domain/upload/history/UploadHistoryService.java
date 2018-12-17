package tavant.twms.domain.upload.history;

import java.io.OutputStream;
import java.sql.Blob;
import java.util.List;

import tavant.twms.infra.GenericService;

public interface UploadHistoryService extends GenericService<UploadHistory, Long, Exception> {

    public List<UploadHistory> findUploadHistory(final int pageSize, final String type);
    
    public UploadHistory findPartInventoryUploadHistory(Long id);
    
    public void downloadFile(Long docId, OutputStream downloadStream);
    
    public Blob getErrorFileContentById(Long id);

}

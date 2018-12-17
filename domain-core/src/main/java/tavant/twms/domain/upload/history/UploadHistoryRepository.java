package tavant.twms.domain.upload.history;

import java.sql.Blob;
import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface UploadHistoryRepository extends GenericRepository<UploadHistory, Long> {

    public List<UploadHistory> findUploadHistory(final int pageSize, final String type);
    public UploadHistory findPartInventoryUploadHistory(Long id);
    public Blob getErrorFileContentById(final Long id);

}

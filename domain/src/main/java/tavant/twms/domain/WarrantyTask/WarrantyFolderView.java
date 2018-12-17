package tavant.twms.domain.WarrantyTask;

import tavant.twms.domain.policy.WarrantyStatus;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Sep 3, 2008
 * Time: 12:59:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class WarrantyFolderView {

    private Long folderCount;

    private String folderName;

    private WarrantyStatus status;

    public Long getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(Long folderCount) {
        this.folderCount = folderCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public WarrantyStatus getStatus() {
        return status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }
}

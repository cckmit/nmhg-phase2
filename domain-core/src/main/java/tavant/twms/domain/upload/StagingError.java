/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.domain.upload;

/**
 *
 * @author prasad.r
 */
public class StagingError {
    
    private Long id;
    private Long fileUploadMgtId;
    private String errorStatus;
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Long getFileUploadMgtId() {
        return fileUploadMgtId;
    }

    public void setFileUploadMgtId(Long fileUploadMgtId) {
        this.fileUploadMgtId = fileUploadMgtId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

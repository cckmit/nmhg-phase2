/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.domain.upload;

import tavant.twms.domain.orgmodel.User;

/**
 *
 * @author prasad.r
 */
public interface StagingData {
 
    public String getErrorStatus();
    
    public String getErrorCode();
            
    public String getUploadStatus();
    
    public Long getId();
    
    public Long getFileUploadMgtId();

    public User getUploadedBy();
    
    public void setFileUploadMgtId(Long fileId);
    
    public void setErrorStatus(String errorStatus);
    
    public void setId(Long id);

    public void setErrorCode(String errorCodes);

    public void setUploadedBy(User uploadedBy);
}

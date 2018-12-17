/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.upload;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.util.FileCopyUtils;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;

import javax.servlet.http.HttpServletResponse;
import java.sql.Blob;
import java.util.List;

/**
 * @author kaustubhshobhan.b
 *
 */
public class DownloadUploadedClaim implements ServletResponseAware {
    private List<UploadHistory> uploadedClaimHistory;

    private HttpServletResponse servletResponse;

    private UploadHistoryService uploadHistoryService;

    private Long id;

    private int pageSize = 7; // default value

    private static Logger logger = LogManager.getLogger(DownloadTemplate.class);

    public DownloadUploadedClaim() {
        super();
    }

    public String generateHistory() {
        uploadedClaimHistory = uploadHistoryService.findUploadHistory(pageSize, "Claim");
        return "success";
    }

    public void downloadInputFile() {
        servletResponse.setContentType("excel/ms-excel");
        UploadHistory claimHistory = uploadHistoryService.findById(getId());
        String fileName = "claim_upload_" + claimHistory.getDateOfUpload().getTime() + ".xls";
        servletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);
        Blob inputFile = claimHistory.getInputFile();
        try {
            FileCopyUtils.copy(inputFile.getBinaryStream(), servletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }

    }

    public void downloadErrorFile() {
        servletResponse.setContentType("excel/ms-excel");
        UploadHistory claimHistory = uploadHistoryService.findById(getId());
        String fileName = "error_file_" + claimHistory.getDateOfUpload().getTime() + ".xls";
        servletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);
        Blob errorFile = claimHistory.getErrorFile();
        try {
            FileCopyUtils.copy(errorFile.getBinaryStream(), servletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }
    }

    public List<UploadHistory> getUploadedClaimHistory() {
        return uploadedClaimHistory;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.servletResponse = response;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}

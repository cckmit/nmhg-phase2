/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.upload.history;

import com.opensymphony.xwork2.ActionContext;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;
import tavant.twms.web.actions.TwmsActionSupport;

import javax.servlet.http.HttpServletResponse;
import java.sql.Blob;
import java.util.List;

/**
 * UploadHistoryAction is to be used where ever the history of a upload
 * is to be view.
 * 
 * @author vineeth.varghese
 * @date Jun 28, 2007
 */
public class UploadHistoryAction extends TwmsActionSupport implements ServletResponseAware {

    private static final Logger logger = Logger.getLogger(UploadHistoryAction.class);

    Long id;

    private String type;

    private int pageSize = 7; //default list size

    private List<UploadHistory> uploadHistory;

    private UploadHistoryService uploadHistoryService;

    private HttpServletResponse servletResponse;

    public String execute() throws Exception {
        type = (String)ActionContext.getContext().getValueStack().findValue("type");
        if (StringUtils.hasText(type)) {
            uploadHistory = uploadHistoryService.findUploadHistory(pageSize, type);
            return SUCCESS;
        } else {
            throw new IllegalStateException("Upload History type is required!!!");
        }

    }

    public void downloadInputFile() {
        UploadHistory claimHistory = getSelectedUploadHistory();
        String fileName = "uploaded_" + claimHistory.getDateOfUpload().getTime() + ".xls";
        Blob inputFile = claimHistory.getInputFile();
        streamFile(inputFile, fileName);
    }

    public void downloadErrorFile() {
        UploadHistory claimHistory = getSelectedUploadHistory();
        String fileName = "error_file_" + claimHistory.getDateOfUpload().getTime() + ".xls";
        Blob errorFile = claimHistory.getErrorFile();
        streamFile(errorFile, fileName);
    }

    UploadHistory getSelectedUploadHistory() {
        if (id == null) {
            throw new IllegalStateException("Need the id of the history for download!!!");
        }
        return uploadHistoryService.findById(id);
    }

    void streamFile(Blob blob, String fileName) {
        servletResponse.setContentType("excel/ms-excel");
        servletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);
        try {
            FileCopyUtils.copy(blob.getBinaryStream(), servletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<UploadHistory> getUploadHistory() {
        return uploadHistory;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

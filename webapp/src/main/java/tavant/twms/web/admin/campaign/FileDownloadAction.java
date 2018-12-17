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
package tavant.twms.web.admin.campaign;

import com.opensymphony.xwork2.Action;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * @author Kiran.Kollipara
 */
public class FileDownloadAction implements Action {
    
    private static Logger logger = LogManager.getLogger(FileDownloadAction.class);
    
    public String execute() throws Exception {
        return SUCCESS;
    }

    private String inputPath = "/pages/secure/admin/campaign/SerialNumbers.xls";

    public InputStream getInputStream() throws Exception {
        ServletContext servletContext = ServletActionContext.getServletContext();
        if (logger.isDebugEnabled()) {
            logger.debug("Downloading File : " + servletContext.getRealPath(inputPath));
        }
        return servletContext.getResourceAsStream(inputPath);
    }
    
    public Integer getBufferSize(){
        return new Integer(4096);
    }
}
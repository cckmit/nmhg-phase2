/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.web.admin.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Clob;

import javax.mail.MethodNotSupportedException;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.FileCopyUtils;

import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.upload.HeaderUtil;

/**
 * @author jhulfikar.ali
 *
 */
public abstract class ExportAction extends I18nActionSupport {
	
	public HttpServletResponse response;			
	
	public String downloadData() throws Exception {	
		try {
			HSSFWorkbook book = getDownloadData();
			if (book == null)
				throw new Exception("There are some errors in your input.");
			setHeader(this.response, getDataFileName() + ".csv", HeaderUtil.EXCEL); // MIME_TYPE_EXCEL
			addActionMessage(getText("label.downloadMgt.downloadSuccess"));
			book.write(this.response.getOutputStream());
		} catch (Exception exception) {
			addActionError(exception.getMessage());
			return INPUT;
		}
		return null;		
	}
	
	protected abstract String getDataFileName() throws MethodNotSupportedException;

	protected abstract HSSFWorkbook getDownloadData();
	
	protected void getDownloadData(OutputStream os, int downloadPageNumber) {}
	
	protected long getDownloadDataCount() { return -1; }

	public static final void setHeader(HttpServletResponse response, String fileName, String mimeType) {
        response.setContentType(mimeType);
        fileName = fileName.replace(' ', '_');
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    }
	
	public void setServletResponse(HttpServletResponse httpServletResponse) {
        response = httpServletResponse;
    }

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
}

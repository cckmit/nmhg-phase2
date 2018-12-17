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
package tavant.twms.web.download;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.upload.HeaderUtil;
/**
 * @author vijay kamalnath M anand
 * 
 */

public class DownloadPartsCatalogAction extends TwmsActionSupport implements
		ServletResponseAware {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager
			.getLogger(DownloadPartsCatalogAction.class);

	private Long id;

	private DocumentService documentService;

	private HttpServletResponse httpServletResponse;

	private static final String PARTS_CATALOG_DOWNLOAD = "PartsCatalogDownload";

	public void downloadPartsCatalogDocument() {
		try {
			
			Document document = documentService.findByDocumentType(PARTS_CATALOG_DOWNLOAD);
			HeaderUtil.setHeader(httpServletResponse,  document.getFileName(),
					 document.getContentType());
			OutputStream downloadStream = httpServletResponse.getOutputStream();
			documentService.downLoadDocument(downloadStream);
			
		} catch (Exception e) {
			logger.error("Exception in DownloadPartsCatalog.downloadPartsCatalogDocument()");
			e.printStackTrace();
		}
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}
}

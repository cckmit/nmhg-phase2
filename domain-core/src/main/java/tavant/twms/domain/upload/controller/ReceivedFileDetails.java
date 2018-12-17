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

package tavant.twms.domain.upload.controller;

import java.io.InputStream;

/**
 * @author jhulfikar.ali
 *
 */
public class ReceivedFileDetails {

	private long id;
	private String templateName;
	private InputStream fileContents;
	private String uploadedBy;
	private String uploaderLocale;

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getUploaderLocale() {
		return uploaderLocale;
	}

	public void setUploaderLocale(String uploaderLocale) {
		this.uploaderLocale = uploaderLocale;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public InputStream getFileContents() {
		return fileContents;
	}

	public void setFileContents(InputStream fileContents) {
		this.fileContents = fileContents;
	}

}

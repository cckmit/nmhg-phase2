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

import java.sql.Date;

import oracle.sql.BLOB;

public class UploadStatusDetail {
	
	public static final int STATUS_NOT_PROCESSED = 0;
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_STAGING = 2;
    public static final int STATUS_VALIDATING = 3;
    public static final int STATUS_UPLOADING = 4;
    public static final int STATUS_ERR_REPORTING = 5;
    public static final int STATUS_WAITING_FOR_UPLOAD = 6;
    public static final int STATUS_UPLOADING_DRAFT_CLAIMS = 7;
    public static final int STATUS_FAILED = 9;
    public static final int STATUS_UPLOADED = 10;

	private int totalRecords;
	
	private int successRecords;
	
	private int errorRecords;
	
	private Date receivedOn;
	
	private String fileName;
	
	private String templateName;
	
	private int id;
	
	private BLOB blob;
	
	private int uploadStatus;

	private String errorMessage;

	public BLOB getBlob() {
		return blob;
	}

	public void setBlob(BLOB blob) {
		this.blob = blob;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}

	public int getErrorRecords() {
		return errorRecords;
	}

	public void setErrorRecords(int errorRecords) {
		this.errorRecords = errorRecords;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getUploadStatusString() {
		switch (uploadStatus) {
		case 0:
			return "Under Progress";
		case 1:
			return "Finished";
		case 2:
			return "Unsuccessful";
		default:
			return "Unknown";
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public static String getUploadStatusText(int uploadStatus) {
		switch (uploadStatus) {
		case STATUS_NOT_PROCESSED:
			return "label.common.notProcessed";
		case STATUS_PROCESSING:
			return "message.upload.processing";
		case STATUS_STAGING:
			return "message.upload.staging";
		case STATUS_VALIDATING:
			return "message.upload.validating";
		case STATUS_UPLOADING:
			return "message.upload.uploading";
		case STATUS_ERR_REPORTING:
			return "message.upload.errReporting";
		case STATUS_WAITING_FOR_UPLOAD:
			return "message.upload.waitingForUpload";
		case STATUS_UPLOADING_DRAFT_CLAIMS:
			return "message.upload.uploading";
		case STATUS_UPLOADED:
			return "message.upload.uploaded";
		case STATUS_FAILED:
			return "message.upload.failed";
		default :
			return "label.common.error";
		}
	}

}

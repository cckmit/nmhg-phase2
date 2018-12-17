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

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.orgmodel.User;

import com.domainlanguage.time.CalendarDate;

/**
 * @author jhulfikar.ali
 *
 */
@Entity
@Table( name = "FILE_UPLOAD_MGT")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class UserFileUpload implements BusinessUnitAware {
	
	@Id
	@GeneratedValue(generator = "UserFileUpload")
	@GenericGenerator(name = "UserFileUpload", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FILE_UPLOAD_MGT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String fileName;
	
	private String templateName;
	
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate receivedOn;
    
    private Integer uploadStatus;
    
    @Lob
    @Column(length = 1048576)
    private Blob fileContent;
    
    @Lob
    @Column(length = 1048576)
    private Blob errorFileContent;
    
    private Integer totalRecords;

    private Integer successRecords;

    private Integer errorRecords;

    private Integer retryCount;

    private String errorMessage;

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
	
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User uploadedBy;
    
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public CalendarDate getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(CalendarDate receivedOn) {
		this.receivedOn = receivedOn;
	}

	public Integer getUploadStatus() {
		return uploadStatus;
	}
	
	public String getUploadStatusText() {
		return UploadStatusDetail.getUploadStatusText(uploadStatus);
	}

	public void setUploadStatus(Integer uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public Blob getFileContent() {
		return fileContent;
	}

	public void setFileContent(Blob fileContent) {
		this.fileContent = fileContent;
	}

	public Blob getErrorFileContent() {
		return errorFileContent;
	}

	public void setErrorFileContent(Blob errorFileContent) {
		this.errorFileContent = errorFileContent;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Integer getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(Integer successRecords) {
		this.successRecords = successRecords;
	}

	public Integer getErrorRecords() {
		return errorRecords;
	}

	public void setErrorRecords(Integer errorRecords) {
		this.errorRecords = errorRecords;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Long getId() {
		return id;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}

	public User getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	public boolean isErrorReportGenerated() {
		return (uploadStatus > UploadStatusDetail.STATUS_ERR_REPORTING);
	}

}

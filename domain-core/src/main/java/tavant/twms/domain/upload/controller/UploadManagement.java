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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.orgmodel.Role;

/**
 * @author jhulfikar.ali
 *
 */
@Entity
@Table(name = "upload_mgt")
public class UploadManagement {

	@Id
	@GeneratedValue(generator = "UploadManagement")
	@GenericGenerator(name = "UploadManagement", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UPLOAD_MGT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	public void setId(Long id) {
		this.id = id;
	}

	private String nameOfTemplate;
	
	private String nameToDisplay;
	
	private String description;
	
	private String templatePath;
	
	private String stagingTable;
	
	private String backupTable;
	
	private String stagingProcedure;
	
	private String validationProcedure;
	
	private String uploadProcedure;
	
	private String populationProcedure;
	
	private Long columnsToCapture;
	
	private Long consumeRowsFrom;
	
	private Long headerRowToCapture;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "upload_roles", joinColumns = { @JoinColumn(name = "upload_mgt") }, inverseJoinColumns = { @JoinColumn(name = "roles") })
	private Set<Role> roles = new HashSet<Role>();

	@OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<UploadError> uploadErrors = new ArrayList<UploadError>(10);
	
	@OneToMany(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @IndexColumn(name = "COLUMN_ORDER", nullable = false)
    private List<UploadManagementMetaData> uploadManagementMetaDatas = new ArrayList<UploadManagementMetaData>();

	public Long getId() {
		return id;
	}

	public String getNameOfTemplate() {
		return nameOfTemplate;
	}

	public void setNameOfTemplate(String nameOfTemplate) {
		this.nameOfTemplate = nameOfTemplate;
	}

	public String getNameToDisplay() {
		return nameToDisplay;
	}

	public void setNameToDisplay(String nameToDisplay) {
		this.nameToDisplay = nameToDisplay;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String stagingTable) {
		this.stagingTable = stagingTable;
	}

	public String getStagingProcedure() {
		return stagingProcedure;
	}

	public void setStagingProcedure(String stagingProcedure) {
		this.stagingProcedure = stagingProcedure;
	}

	public String getValidationProcedure() {
		return validationProcedure;
	}

	public void setValidationProcedure(String validationProcedure) {
		this.validationProcedure = validationProcedure;
	}

	public String getUploadProcedure() {
		return uploadProcedure;
	}

	public void setUploadProcedure(String uploadProcedure) {
		this.uploadProcedure = uploadProcedure;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getPopulationProcedure() {
		return populationProcedure;
	}

	public void setPopulationProcedure(String populationProcedure) {
		this.populationProcedure = populationProcedure;
	}

	public Long getColumnsToCapture() {
		return columnsToCapture;
	}

	public void setColumnsToCapture(Long columnsToCapture) {
		this.columnsToCapture = columnsToCapture;
	}

	public Long getConsumeRowsFrom() {
		return consumeRowsFrom;
	}

	public void setConsumeRowsFrom(Long consumeRowsFrom) {
		this.consumeRowsFrom = consumeRowsFrom;
	}

	public Long getHeaderRowToCapture() {
		return headerRowToCapture;
	}

	public void setHeaderRowToCapture(Long headerRowToCapture) {
		this.headerRowToCapture = headerRowToCapture;
	}

	public List<UploadError> getUploadErrors() {
		return uploadErrors;
	}

	public void setUploadErrors(List<UploadError> uploadErrors) {
		this.uploadErrors = uploadErrors;
	}
	
	public String getBackupTable() {
		return backupTable;
	}

	public void setBackupTable(String backupTable) {
		this.backupTable = backupTable;
	}
	
	public List<UploadManagementMetaData> getUploadManagementMetaDatas() {
		return uploadManagementMetaDatas;
	}

	public void setUploadManagementMetaDatas(
			List<UploadManagementMetaData> uploadManagementMetaDatas) {
		this.uploadManagementMetaDatas = uploadManagementMetaDatas;
	}


}

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
package tavant.twms.domain.upload.history;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;


/**
 * @author kaustubhshobhan.b
 *
 */

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class UploadHistory implements BusinessUnitAware,AuditableColumns{

	@Id
	@GeneratedValue(generator = "UploadHistory")
	@GenericGenerator(name = "UploadHistory", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UPLOAD_HISTORY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String type;

    private Blob inputFile;

    private Date dateOfUpload;

    private int numberOfSuccessfulUploads;

    private int numberOfErrorUploads;

    private Blob errorFile;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Date getDateOfUpload() {
        return this.dateOfUpload;
    }

    public void setDateOfUpload(Date dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    public Blob getErrorFile() {
        return this.errorFile;
    }

    public void setErrorFile(Blob errorFile) {
        this.errorFile = errorFile;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Blob getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(Blob inputFile) {
        this.inputFile = inputFile;
    }

    public int getNumberOfErrorUploads() {
        return this.numberOfErrorUploads;
    }

    public void setNumberOfErrorUploads(int numberOfErrorClaims) {
        this.numberOfErrorUploads = numberOfErrorClaims;
    }

    public int getNumberOfSuccessfulUploads() {
        return this.numberOfSuccessfulUploads;
    }

    public void setNumberOfSuccessfulUploads(int numberOfSuccessfulClaims) {
        this.numberOfSuccessfulUploads = numberOfSuccessfulClaims;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
}

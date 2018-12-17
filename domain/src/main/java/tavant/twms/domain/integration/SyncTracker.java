package tavant.twms.domain.integration;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.User;

@SuppressWarnings("serial")
@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class SyncTracker implements Serializable,BusinessUnitAware {

	@Id
	@GeneratedValue
	private Long id;

	private String syncType;

	private String businessId;

	private String uniqueIdName;

	private String uniqueIdValue;

	private String errorType;

	@Lob
	private String bodXML;

	@Lob
	private String record;

	@ManyToOne()
	private SyncStatus status;

	@Lob
	private String errorMessage;

	private Integer noOfAttempts;

	private Date startTime;

	private Date createDate;

	private Date updateDate;
	
    private Date hiddenOn;

    private String hiddenBy;
    
    @Column(name="D_UPDATED_ON")
    private Date dUpdatedOn;
    @ManyToOne(fetch=FetchType.LAZY)
    private User dLastUpdatedBy;

	@Version
	private Integer version;
	
	@Transient
	private Boolean selected = Boolean.FALSE; 
	
	private String processing_status;
	
	public static String SUCCESS="SUCCESS";
	
	public static String FAILURE="FAILURE";

    private static final Pattern businessIDPattern =
            Pattern.compile("(<LogicalId>)(.*?)(</LogicalId>)",
                            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    @Column(name="IS_DELETED")
    @Type(type="yes_no")
    private boolean deleted;
    
	// Needed by hibernate
	@SuppressWarnings("unused")
	private SyncTracker() {

	}

	public SyncTracker(String businessId, String syncType, String bod) {
		super();
		assert (businessId != null && syncType != null && bod != null);
		this.businessId = businessId;
		this.syncType = syncType;
		// TODO: revisit this and see if raw record serves any real purpose.
		this.bodXML = this.record = bod;
		this.createDate = this.startTime = this.updateDate = new Date();
		this.noOfAttempts = 0;
		this.errorMessage = null;
		this.status = SyncStatus.TO_BE_PROCESSED;
        this.deleted = false;
	}

	public SyncTracker(String syncType, String bod) {
		super();
		assert (syncType != null && bod != null);
        Matcher m = businessIDPattern.matcher(bod);
        if(m.find()){
            this.businessId = m.group(2);
        }
		this.syncType = syncType;
		this.bodXML = this.record = bod;
		this.createDate = this.startTime = this.updateDate = new Date();
		this.noOfAttempts = 0;
		this.errorMessage = null;
		if(syncType.equalsIgnoreCase(AdminConstants.CMS)||syncType.equalsIgnoreCase(AdminConstants.SSO)){
			this.status = SyncStatus.COMPLETED;
		}else{
		this.status = SyncStatus.TO_BE_PROCESSED;
		}
        this.deleted = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public SyncStatus getStatus() {
		return status;
	}

	public void setStatus(SyncStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getNoOfAttempts() {
		return noOfAttempts;
	}

	public void setNoOfAttempts(Integer failureCount) {
		this.noOfAttempts = failureCount;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getBodXML() {
		return bodXML;
	}

	public void setBodXML(String bodXML) {
		this.bodXML = bodXML;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("sync type",
				syncType).append("business id", businessId).append("status",
				status.getStatus()).append("error message", errorMessage)
				.append("retry count", noOfAttempts).append("start time",
						startTime).append("create date", createDate).append(
						"update date", updateDate).append("version", version)
				.toString();
	}

	public String getUniqueIdName() {
		return uniqueIdName;
	}

	public void setUniqueIdName(String uniqueIdName) {
		this.uniqueIdName = uniqueIdName;
	}

	public String getUniqueIdValue() {
		return uniqueIdValue;
	}

	public void setUniqueIdValue(String uniqueIdValue) {
		this.uniqueIdValue = uniqueIdValue;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public String getProcessing_status() {
		return processing_status;
	}

	public void setProcessing_status(String processing_status) {
		this.processing_status = processing_status;
	}

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getHiddenBy() {
        return hiddenBy;
    }

    public void setHiddenBy(String hiddenBy) {
        this.hiddenBy = hiddenBy;
    }

    public Date getHiddenOn() {
        return hiddenOn;
    }

    public void setHiddenOn(Date hiddenOn) {
        this.hiddenOn = hiddenOn;
    }
    
    public User getDLastUpdatedBy() {
        return dLastUpdatedBy;
    }

    public void setDLastUpdatedBy(User lastUpdatedBy) {
        this.dLastUpdatedBy = lastUpdatedBy;
    }

    public Date getDUpdatedOn() {
        return dUpdatedOn;
    }

    public void setDUpdatedOn(Date updatedOn) {
        this.dUpdatedOn = updatedOn;
    }
    
}

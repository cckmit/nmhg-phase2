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

package tavant.twms.integration.server.common.dataaccess;

import org.springframework.core.style.ToStringCreator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import org.hibernate.annotations.Type;
@Entity

public class SyncTracker implements Serializable {

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

    @Version
    private Integer version;
    
    private String businessUnitInfo;
    
    private String processing_status;
	
	public static String SUCCESS="SUCCESS";
	
	public static String FAILURE="FAILURE";
	
	public static String TOBEPROCESSED="To be processed";
	
	@Column(name="IS_DELETED")
    @Type(type="yes_no")
    private boolean deleted;
	
    private static final Pattern businessIDPattern =
        Pattern.compile("(<LogicalId>)(.*?)(</LogicalId>)",
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    //Needed by hibernate
    private SyncTracker(){
        
    }
    
    public SyncTracker(String businessId, String syncType, String bod) {
        super();
        assert (businessId != null && syncType != null && bod != null);
        this.businessId = businessId;
        this.syncType = syncType;
        this.bodXML = bod;
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
        this.bodXML = bod;
        this.createDate = this.startTime = this.updateDate = new Date();
        this.noOfAttempts = 0;
        this.errorMessage = null;
        this.status = SyncStatus.TO_BE_PROCESSED;
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

	public String getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(String businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
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
	
}

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

package tavant.twms.integration.adapter;

import org.springframework.core.style.ToStringCreator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
public class SyncTracker implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String syncType;

    private String businessId;

    @ManyToOne()
    private SyncStatus status;

    @Lob
    private String errorMessage;

    private Integer noOfAttempts;

    private Date startTime;

    private Date createDate;

    private Date updateDate;

    @Version
    private Integer version;

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

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", id)
                .append("sync type", syncType)
                .append("business id", businessId)
                .append("status", status.getStatus())
                .append("error message", errorMessage)
                .append("no. of attempts", noOfAttempts)
                .append("start time", startTime)
                .append("create date", createDate)
                .append("update date", updateDate)
                .append("version", version)
                .toString();
    }
}

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

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SyncStatus implements Serializable {

    public static final SyncStatus COMPLETED = new SyncStatus("Completed");
    public static final SyncStatus IN_PROGRESS = new SyncStatus("In Progress");
    public static final SyncStatus RESPONSESENT = new SyncStatus("Response Sent");
    public static final SyncStatus FAILED = new SyncStatus("Failed");
    public static final SyncStatus TO_BE_PROCESSED = new SyncStatus("To be Processed");
    public static final SyncStatus CLAIM_STATE_UPDATION_FAILED = new SyncStatus("Claim State Updation Failed");
    public static final SyncStatus ERROR = new SyncStatus("Errored");
    public static final SyncStatus RECTIFIED = new SyncStatus("Rectified");
    public static final SyncStatus CANCELLED = new SyncStatus("Cancelled");

    @Id
    private String status;

    public SyncStatus(String status) {
        super();
        this.status = status;
    }

    public SyncStatus() {
        super();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncStatus that = (SyncStatus) o;

        return status.equals(that.status);

    }

    public int hashCode() {
        return status.hashCode();
    }
}

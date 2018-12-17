package tavant.twms.domain.integration;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class SyncStatus implements Serializable, AuditableColumns {

    public static final SyncStatus COMPLETED = new SyncStatus("Completed");
    public static final SyncStatus IN_PROGRESS = new SyncStatus("In Progress");
    public static final SyncStatus FAILED = new SyncStatus("Failed");
    public static final SyncStatus TO_BE_PROCESSED = new SyncStatus("To be Processed");
    public static final SyncStatus ERROR = new SyncStatus("Errored");
    public static final SyncStatus RECTIFIED = new SyncStatus("Rectified");
    public static final SyncStatus CANCELLED = new SyncStatus("Cancelled");

    @Id
    private String status;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

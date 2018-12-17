package tavant.twms.domain.policy;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name = "policy_audit")
public class RegisteredPolicyAudit implements Comparable<RegisteredPolicyAudit>, AuditableColumns {
	@Id
	@GeneratedValue(generator = "PolicyAudit")
	@GenericGenerator(name = "PolicyAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint createdOn;
  
    private Date createdTime;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date")),
            @AttributeOverride(name = "tillDate", column = @Column(name = "till_date")) })
    private CalendarDuration warrantyPeriod;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User createdBy;

    private String status;

    private String comments;


    private Integer serviceHoursCovered;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TimePoint getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(TimePoint createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public CalendarDuration getWarrantyPeriod() {
        return this.warrantyPeriod;
    }

    public void setWarrantyPeriod(CalendarDuration warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public RegisteredPolicyAudit() {
        super();
        this.createdOn = Clock.now();
    }

    public int compareTo(RegisteredPolicyAudit other) {
        if (other == null) {
            return 1;
        }
        int dateCompare = this.createdOn.compareTo(other.createdOn);
        return dateCompare;
    }

    public Integer getServiceHoursCovered() {
        return this.serviceHoursCovered;
    }

    public void setServiceHoursCovered(Integer serviceHoursCovered) {
        this.serviceHoursCovered = serviceHoursCovered;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getDisplayStatus()
	{
		return this.status;
	}
}

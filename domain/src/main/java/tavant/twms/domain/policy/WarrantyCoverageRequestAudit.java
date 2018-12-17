package tavant.twms.domain.policy;

import java.util.TimeZone;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

@Entity
@Table(name = "REQUEST_WNTY_CVG_AUDIT")
public class WarrantyCoverageRequestAudit implements AuditableColumns{
			
	public static final String WAITING_FOR_YOUR_RESPONSE= "WAITING_FOR_YOUR_RESPONSE";
	
	public static final String EXTENSION_NOT_REQUESTED= "EXTENSION_NOT_REQUESTED";	
	
	public static final String DENIED= "DENIED";
	
	public static final String APPROVED= "APPROVED";
	
	public static final String FORWARDED= "FORWARDED";
	
	public static final String REPLIED= "REPLIED";
	
	public static final String SUBMITTED= "SUBMITTED";
	
	@Id
    @GeneratedValue(generator = "Warranty")
	@GenericGenerator(name = "Warranty", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REQUEST_WNTY_CVG_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User assignedTo;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User assignedBy;
	
	private String comments;
	
	private String status;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public User getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(User assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	
	public CalendarDate getCreateDateAsCalendarDate(){
		return CalendarDate.from(TimePoint.from(this.d.getCreatedTime()),TimeZone.getDefault());
	}
	

}

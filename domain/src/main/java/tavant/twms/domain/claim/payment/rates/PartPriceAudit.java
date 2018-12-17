package tavant.twms.domain.claim.payment.rates;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
public class PartPriceAudit implements Comparable<PartPriceAudit>, AuditableColumns {
	@Id
	@GeneratedValue(generator = "PartPriceAudit")
	@GenericGenerator(name = "PartPriceAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_PRICE_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();
	
	private String comments;
	private String status;
	
	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({
		org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		@JoinColumn(name = "part_price_audit")
		private List<PartPriceRepairDateAudit> PartPriceRepairDateAudits = new ArrayList<PartPriceRepairDateAudit>();	
	
	public List<PartPriceRepairDateAudit> getPartPriceRepairDateAudits() {
		return PartPriceRepairDateAudits;
	}


	public void setPartPriceRepairDateAudits(
			List<PartPriceRepairDateAudit> partPriceRepairDateAudits) {
		PartPriceRepairDateAudits = partPriceRepairDateAudits;
	}


	@Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint createdOn;
	
	public TimePoint getCreatedOn() {
		return createdOn;
	}


	public void setCreatedOn(TimePoint createdOn) {
		this.createdOn = createdOn;
	}


	
	public PartPriceAudit() {
		super();
		this.createdOn = Clock.now();
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
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
	
	
	public int compareTo(PartPriceAudit partPriceAudit) {
    	if (partPriceAudit == null) {
            return 1;
        }
    	int codeCompare = this.createdOn.compareTo(partPriceAudit.createdOn);
    	return codeCompare;
     }

	

}

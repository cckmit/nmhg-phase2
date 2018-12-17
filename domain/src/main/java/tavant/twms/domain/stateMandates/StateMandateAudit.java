package tavant.twms.domain.stateMandates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.LaborRateType;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name="STATE_MANDATE_AUDIT")
public class StateMandateAudit implements Comparable<StateMandateAudit>,
		AuditableColumns {

	@Id
	@GeneratedValue(generator = "StateMandateAudit")
	@GenericGenerator(name = "StateMandateAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "STATE_MANDATE_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String comments;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private User createdBy;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate effectiveDate;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@Cascade({ CascadeType.ALL })
	private LaborRateType laborRateType;
	
	@Column(name="OEM_PARTS_PERCENT")
	private BigDecimal oemPartsPercent;
	
	@Column(name="STATE")
	private String state;
	
	@OneToMany(mappedBy="stateMandateAudit",fetch = FetchType.LAZY)
	@Cascade({ CascadeType.ALL })
	private List<StateMndteCostCtgyAudit> stateMandateCostCtgAudit = new ArrayList<StateMndteCostCtgyAudit>();
	
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public StateMandateAudit() {
		super();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	public CalendarDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(CalendarDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public LaborRateType getLaborRateType() {
		return laborRateType;
	}

	public void setLaborRateType(LaborRateType laborRateType) {
		this.laborRateType = laborRateType;
	}

	public BigDecimal getOemPartsPercent() {
		return oemPartsPercent;
	}

	public void setOemPartsPercent(BigDecimal oemPartsPercent) {
		this.oemPartsPercent = oemPartsPercent;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<StateMndteCostCtgyAudit> getStateMandateCostCtgAudit() {
		return stateMandateCostCtgAudit;
	}

	public void setStateMandateCostCtgAudit(
			List<StateMndteCostCtgyAudit> stateMandateCostCtgAudit) {
		this.stateMandateCostCtgAudit = stateMandateCostCtgAudit;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "STATE_MANDATE", insertable = false, updatable = false)
	private StateMandates stateMandate;

	public StateMandates getStateMandate() {
		return stateMandate;
	}

	public void setStateMandate(StateMandates stateMandate) {
		this.stateMandate = stateMandate;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int compareTo(StateMandateAudit stateMandateAudit) {
		if (stateMandateAudit == null) {
			return 1;
		}
		int codeCompare = this.d.getUpdatedTime().compareTo(
				stateMandateAudit.getD().getUpdatedTime());
		return codeCompare;
	}

}

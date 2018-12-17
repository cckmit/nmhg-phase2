package tavant.twms.domain.stateMandates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.claim.payment.CostCategory;

@Entity
@Table(name = "STATE_MNDTE_COST_CTGY_AUDIT")
public class StateMndteCostCtgyAudit {
	public StateMndteCostCtgyAudit(){
		
	}
	
	@Id
	@GeneratedValue(generator = "StateMndteCostCtgyAudit")
	@Column(name = "ID")
	@GenericGenerator(name = "StateMndteCostCtgyAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "STATE_MNDTE_COST_CTGY_AUD_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@Column(name = "MANDATORY")
	private Boolean mandatory=Boolean.FALSE;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private CostCategory costCategory;
	
	@Column(name="OTHERS")
	private String others;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public CostCategory getCostCategory() {
		return costCategory;
	}

	public void setCostCategory(CostCategory costCategory) {
		this.costCategory = costCategory;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_mandate_audit",nullable = false)
	private StateMandateAudit stateMandateAudit;

	public StateMandateAudit getStateMandateAudit() {
		return stateMandateAudit;
	}

	public void setStateMandateAudit(StateMandateAudit stateMandateAudit) {
		this.stateMandateAudit = stateMandateAudit;
	}
	
	
}

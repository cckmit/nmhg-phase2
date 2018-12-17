/**
 * 
 */
package tavant.twms.domain.stateMandates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.inventory.InventoryItem;

/**
 * @author arpitha.ar
 *
 */

@Entity
@Table(name = "STATE_MNDTE_COST_CTGY_MAPPING")
public class StateMndteCostCtgyMapping {
	
	public StateMndteCostCtgyMapping(){
		
	}
	
	@Id
	@GeneratedValue(generator = "StateMndteCostCtgyMapping")
	@Column(name = "ID")
	@GenericGenerator(name = "StateMndteCostCtgyMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "STATE_MNDTE_COST_CTGY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_mandate",nullable = false)
	private StateMandates stateMandates;
	
	@Column(name = "MANDATORY")
	private Boolean mandatory=Boolean.FALSE;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private CostCategory costCategory;
	
	@Column(name="OTHERS")
	private String others;
	
	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public CostCategory getCostCategory() {
		return costCategory;
	}

	public void setCostCategory(CostCategory costCategory) {
		this.costCategory = costCategory;
	}

	public StateMandates getStateMandates() {
		return stateMandates;
	}

	public void setStateMandates(StateMandates stateMandates) {
		this.stateMandates = stateMandates;
	}
	
	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

}

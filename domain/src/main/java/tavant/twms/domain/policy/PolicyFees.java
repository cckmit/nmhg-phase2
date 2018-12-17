package tavant.twms.domain.policy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import com.domainlanguage.money.Money;

@Entity
public class PolicyFees {
	@Id
	@GeneratedValue(generator = "PolicyFee")
	@GenericGenerator(name = "PolicyFee", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_FEES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "amount"), @Column(name = "currency") })
	private Money policyFee;
	
    private Boolean isTransferable = Boolean.FALSE;
    
    @Transient 
	private Money currency;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Money getPolicyFee() {
		return policyFee;
	}

	public void setPolicyFee(Money policyFee) {
		this.policyFee = policyFee;
	}

	public Boolean getIsTransferable() {
		return isTransferable;
	}

	public void setIsTransferable(Boolean isTransferable) {
		this.isTransferable = isTransferable;
	}

	public Money getCurrency() {
		return currency;
	}

	public void setCurrency(Money currency) {
		this.currency = currency;
	}

	
}

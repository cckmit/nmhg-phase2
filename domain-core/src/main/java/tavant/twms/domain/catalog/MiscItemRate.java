package tavant.twms.domain.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

@Entity
@Table(name="MISC_ITEM_RATE")
public class MiscItemRate {

	@Id
	@GeneratedValue(generator = "MiscItemRate")
	@GenericGenerator(name = "MiscItemRate", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MISC_ITEM_RATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "amount"), @Column(name = "currency") })
	private Money rate;
	
	@Transient 
	private Money currency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Money getRate() {
		return rate;
	}

	public void setRate(Money rate) {
		this.rate = rate;
	}

	public Money getCurrency() {
		return currency;
	}

	public void setCurrency(Money currency) {
		this.currency = currency;
	}
}

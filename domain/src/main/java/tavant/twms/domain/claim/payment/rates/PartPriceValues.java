package tavant.twms.domain.claim.payment.rates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

@Entity
public class PartPriceValues {
	@Id
	@GeneratedValue(generator = "PartPriceValue")
	@GenericGenerator(name = "PartPriceValue", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_PRICE_VALUES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "DEALER_NET_PRICE"), @Column(name = "DEALER_NET_PRICE_CURR") })
	private Money dealerNetPrice;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "STANDARD_COST_PRICE"), @Column(name = "STANDARD_COST_PRICE_CURR") })
	private Money standardCostPrice;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "PLANT_COST_PRICE"), @Column(name = "PLANT_COST_PRICE_CURR") })
	private Money plantCostPrice;
	
	@Transient 
	private Money currency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Money getDealerNetPrice() {
		return dealerNetPrice;
	}

	public void setDealerNetPrice(Money dealerNetPrice) {
		this.dealerNetPrice = dealerNetPrice;
	}

	public Money getStandardCostPrice() {
		return standardCostPrice;
	}

	public void setStandardCostPrice(Money standardCostPrice) {
		this.standardCostPrice = standardCostPrice;
	}

	public Money getPlantCostPrice() {
		return plantCostPrice;
	}

	public void setPlantCostPrice(Money plantCostPrice) {
		this.plantCostPrice = plantCostPrice;
	}

	public Money getCurrency() {
		return currency;
	}

	public void setCurrency(Money currency) {
		this.currency = currency;
	}
	
	
}

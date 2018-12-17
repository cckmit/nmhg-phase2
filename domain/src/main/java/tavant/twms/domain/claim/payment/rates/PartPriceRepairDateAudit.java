package tavant.twms.domain.claim.payment.rates;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "PART_PRICE_REPAIR_DATE_HISTORY")
public class PartPriceRepairDateAudit implements AuditableColumns {

	@Id
	@GeneratedValue(generator = "PartPriceRepairDateAudit")
	@GenericGenerator(name = "PartPriceRepairDateAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_PRICE_RD_HISTORY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	 @Embedded
	    @AttributeOverrides({
	            @AttributeOverride(name="fromDate",column=@Column(name="from_date",nullable=false)),
	            @AttributeOverride(name="tillDate",column=@Column(name="till_date",nullable=false))
	    })
	    private CalendarDuration duration;
			
		public CalendarDuration getDuration() {
			return duration;
		}

		public void setDuration(CalendarDuration duration) {
			this.duration = duration;
		}

	protected AuditableColEntity d = new AuditableColEntity();

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "DEALER_NET_PRICE"),
			@Column(name = "DEALER_NET_PRICE_CURR") })
	private Money dealerNetPrice;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "STANDARD_COST_PRICE"),
			@Column(name = "STANDARD_COST_PRICE_CURR") })
	private Money standardCostPrice;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "PLANT_COST_PRICE"),
			@Column(name = "PLANT_COST_PRICE_CURR") })
	private Money plantCostPrice;

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

}

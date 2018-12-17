package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.sql.Types;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import com.domainlanguage.money.Money;

@Entity
@Table(name="ADDITIONAL_PAYMENT_INFO")
public class AdditionalPaymentInfo {
	 
	@Id
	@GeneratedValue(generator = "AdditionalPaymentInfo")
	@GenericGenerator(name = "AdditionalPaymentInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDITIONAL_PAYMENT_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.claim.payment.AdditionalPaymentType"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
	private AdditionalPaymentType type;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "additional_amt", nullable = true),
			@Column(name = "additional_curr", nullable = true) })
	private Money additionalAmount;
	
	private BigDecimal percentageAcceptance;

	public Money getAdditionalAmount() {
		return additionalAmount;
	}

	public void setAdditionalAmount(Money additionalAmount) {
		this.additionalAmount = additionalAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPercentageAcceptance() {
		return percentageAcceptance;
	}

	public void setPercentageAcceptance(BigDecimal percentageAcceptance) {
		this.percentageAcceptance = percentageAcceptance;
	}

	public AdditionalPaymentType getType() {
		return type;
	}

	public void setType(AdditionalPaymentType type) {
		this.type = type;
	}
	
	public AdditionalPaymentInfo(){
		//For Hibernate
	}
	
	public AdditionalPaymentInfo(Money additionalAmt, BigDecimal percentage, AdditionalPaymentType type){
		this.additionalAmount = additionalAmt;
		this.percentageAcceptance = percentage;
		this.type = type;
	}
	
	@Override
	public AdditionalPaymentInfo clone(){
		AdditionalPaymentInfo info = new AdditionalPaymentInfo();
		info.setAdditionalAmount(this.additionalAmount);
		info.setPercentageAcceptance(this.percentageAcceptance);
		info.setType(this.type);
		return info;
	}
	
}


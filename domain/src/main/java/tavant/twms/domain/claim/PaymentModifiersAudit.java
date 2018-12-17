package tavant.twms.domain.claim;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;

@Entity
public class PaymentModifiersAudit {

	@Id
	@GeneratedValue(generator = "PaymentModifiersAudit")
	@GenericGenerator(name = "PaymentModifiersAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PMT_MODIFIERS_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;
	
	@Version
	private int version;
	
	private String name;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "claimed_amount_amt"),
			@Column(name = "claimed_amount_curr") })
	private Money claimedAmount;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "paid_amount_amt"),
			@Column(name = "paid_amount_curr") })
	private Money paidAmount;
	
	private Double modifierPercentage;
	
	private Date updatedTime;
	
	@Column(name = "modifier_level")
	private Integer level;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Money getClaimedAmount() {
		return claimedAmount;
	}

	public void setClaimedAmount(Money claimedAmount) {
		this.claimedAmount = claimedAmount;
	}

	public Money getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Money paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Double getModifierPercentage() {
		return modifierPercentage;
	}

	public void setModifierPercentage(Double modifierPercentage) {
		this.modifierPercentage = modifierPercentage;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
}

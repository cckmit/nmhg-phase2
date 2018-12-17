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
import com.domainlanguage.time.TimePoint;

@Entity
public class PartInformationAudit {

	@Id
	@GeneratedValue(generator = "PartInformationAudit")
	@GenericGenerator(name = "PartInformationAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_INFO_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;

	@Version
	private int version;

	private String partNumber;

	private Integer quantity;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "unit_price_amt"),
			@Column(name = "unit_price_curr") })
	private Money unitPrice;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "cost_price_amt"),
			@Column(name = "cost_price_curr") })
	private Money costPrice;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "material_price_amt"),
			@Column(name = "material_price_curr") })
	private Money materialPrice;

	@Type(type = "tavant.twms.infra.CalendarTimeUserType")
	private TimePoint updatedOn;
	
	private Date updatedTime;

	private Long claimAuditId;

	private Long claimId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Money getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Money getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Money costPrice) {
		this.costPrice = costPrice;
	}

	public Money getMaterialPrice() {
		return materialPrice;
	}

	public void setMaterialPrice(Money materialPrice) {
		this.materialPrice = materialPrice;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public TimePoint getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(TimePoint updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Long getClaimAuditId() {
		return claimAuditId;
	}

	public void setClaimAuditId(Long claimAuditId) {
		this.claimAuditId = claimAuditId;
	}

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
}

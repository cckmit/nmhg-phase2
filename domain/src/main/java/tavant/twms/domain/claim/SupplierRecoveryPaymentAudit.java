package tavant.twms.domain.claim;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

@Entity
@Table(name = "SUP_REC_PMT_AUDIT")
public class SupplierRecoveryPaymentAudit {

	@Id
	@GeneratedValue(generator = "SupRecPmtAuditSec")
	@GenericGenerator(name = "SupRecPmtAuditSec", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SUP_REC_PMT_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;

	@Version
	private int version;

	private Long claimId;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "claimed_amount_amt"),
			@Column(name = "claimed_amount_curr") })
	private Money claimedAmount;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "recovered_amount_amt"),
			@Column(name = "recovered_amount_curr") })
	private Money recoveredAmount;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "tax_amount_amt"),
			@Column(name = "tax_amount_curr") })
	private Money taxAmount;

	private String creditMemoNumber;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate creditMemoDate;

	@Type(type = "tavant.twms.infra.CalendarTimeUserType")
	private TimePoint updatedOn;
	
	private Date updatedTime;
	
	private Long recClaimAuditId;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="FOR_PMT_AUDIT")
	private List<SupplierRecCostCategories> costCategories;

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

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public Money getClaimedAmount() {
		return claimedAmount;
	}

	public void setClaimedAmount(Money claimedAmount) {
		this.claimedAmount = claimedAmount;
	}

	public Money getRecoveredAmount() {
		return recoveredAmount;
	}

	public void setRecoveredAmount(Money recoveredAmount) {
		this.recoveredAmount = recoveredAmount;
	}

	public Money getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Money taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getCreditMemoNumber() {
		return creditMemoNumber;
	}

	public void setCreditMemoNumber(String creditMemoNumber) {
		this.creditMemoNumber = creditMemoNumber;
	}

	public CalendarDate getCreditMemoDate() {
		return creditMemoDate;
	}

	public void setCreditMemoDate(CalendarDate creditMemoDate) {
		this.creditMemoDate = creditMemoDate;
	}

	public TimePoint getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(TimePoint updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Long getRecClaimAuditId() {
		return recClaimAuditId;
	}

	public void setRecClaimAuditId(Long recClaimAuditId) {
		this.recClaimAuditId = recClaimAuditId;
	}

	public List<SupplierRecCostCategories> getCostCategories() {
		return costCategories;
	}

	public void setCostCategories(List<SupplierRecCostCategories> costCategories) {
		this.costCategories = costCategories;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

}

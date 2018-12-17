package tavant.twms.domain.claim;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@SuppressWarnings("serial")
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name = "SUP_REC_COST_CATEGORY")
public class SupplierRecCostCategories implements Serializable,AuditableColumns{

	@Id
	@GeneratedValue(generator = "SupRecCostCategoriesAudit")
	@GenericGenerator(name = "SupRecCostCategoriesAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SUP_REC_CC_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;

	@Version
	private int version;

	private String costCategoryName;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "claimed_amount_amt"),
			@Column(name = "claimed_amount_curr") })
	private Money claimedAmount;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "recovered_amount_amt"),
			@Column(name = "recovered_amount_curr") })
	private Money recoveredAmount;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
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

	public String getCostCategoryName() {
		return costCategoryName;
	}

	public void setCostCategoryName(String costCategoryName) {
		this.costCategoryName = costCategoryName;
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

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}

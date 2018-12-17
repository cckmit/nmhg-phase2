package tavant.twms.domain.claim.payment;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class PartPaymentInfo implements AuditableColumns{

	@Id
	@GeneratedValue(generator = "PartPaymentInfo")
	@GenericGenerator(name = "PartPaymentInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_PMT_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String partNumber;

	private Long quantity;
	
	/* Change the null constraints on the table in DB also
	 * 
	 */

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "total_amt", nullable = true),
			@Column(name = "total_curr", nullable = true) })
	private Money unitPrice;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Money getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object arg0) {
		PartPaymentInfo newPartPmtInfo = (PartPaymentInfo) arg0;
		return newPartPmtInfo.getPartNumber().equalsIgnoreCase(this.partNumber);
	}

	// This is similar to an equals. Need to have the delta of quantity on the bean comparison.
	// Added for repayment logic of credit submission.
	// Used in ProcessClaim.java.
	
	public PartPaymentInfo diff(PartPaymentInfo oldBean) {
		PartPaymentInfo newPartPaymentInfo = this.clone();
		int diff = this.quantity.intValue() - oldBean.getQuantity().intValue();
		newPartPaymentInfo.setQuantity((new Long(diff)).longValue());
		return newPartPaymentInfo;
	}
	
	public PartPaymentInfo clone(){
		PartPaymentInfo clone = new PartPaymentInfo();
		clone.setPartNumber(partNumber);
		clone.setQuantity(quantity);
		clone.setUnitPrice(unitPrice);
		return clone;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	} 
}

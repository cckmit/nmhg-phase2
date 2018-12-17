package tavant.twms.domain.claim.payment.rates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import org.hibernate.annotations.GenericGenerator;

import org.hibernate.annotations.Parameter;


import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.security.AuditableColumns;


@Entity
@Table(name="LABOR_RATE_AUDIT")
public class LaborRateAudit implements Comparable<LaborRateAudit>, AuditableColumns{

    @Id
    @GeneratedValue(generator = "LaborRateAudit")
	@GenericGenerator(name = "LaborRateAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LABOR_RATE_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();
	
    
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LaborRateAudit() {
		super();
	}

	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({
		org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		@JoinColumn(name = "labor_audit")
		private List<LaborRateRepairDateAudit> laborRateRepairDateAudits = new ArrayList<LaborRateRepairDateAudit>();	
	
	
	
	@Embedded
    @AssociationOverrides( {
            @AssociationOverride(name = "dealer", joinColumns = @JoinColumn(name = "dealer")),
            @AssociationOverride(name = "dealerGroup", joinColumns = @JoinColumn(name = "dealer_group")) })
    private DealerCriterion dealerCriterion;

	

	public DealerCriterion getDealerCriterion() {
		return dealerCriterion;
	}

	public void setDealerCriterion(DealerCriterion dealerCriterion) {
		this.dealerCriterion = dealerCriterion;
	}

	@Column(name="PRODUCT_NAME")
	private String productName;


	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name="WARRANTY_TYPE")
	private String warrantyType;

	public String getWarrantyType() {
		return warrantyType;
	}

	public void setWarrantyType(String warrantyType) {
		this.warrantyType = warrantyType;
	}

	@Column(name="CLAIM_TYPE")
	private String claimType;

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	@Column(name="CUSTOMER_TYPE")
	private String customerType;

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	private String comments;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<LaborRateRepairDateAudit> getLaborRateRepairDateAudits() {
		return laborRateRepairDateAudits;
	}

	public void setLaborRateRepairDateAudits(
			List<LaborRateRepairDateAudit> laborRateRepairDateAudits) {
		this.laborRateRepairDateAudits = laborRateRepairDateAudits;
	}
	
	public int compareTo(LaborRateAudit laborRateAudit) {
    	if (laborRateAudit == null) {
            return 1;
        }
    	int codeCompare = this.d.getUpdatedTime().compareTo(laborRateAudit.getD().getUpdatedTime());
    	return codeCompare;
     }

}
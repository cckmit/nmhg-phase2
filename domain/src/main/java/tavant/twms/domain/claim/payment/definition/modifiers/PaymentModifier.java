/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim.payment.definition.modifiers;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValues;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.security.AuditableColumns;

/**
 * @author Kiran.Kollipara
 * 
 */
@Entity
@Filters({
	  @Filter(name="excludeInactive")
	})
public class PaymentModifier extends TimeBoundValues<Double, CriteriaBasedValue> implements AuditableColumns {

	@Id
	@GeneratedValue(generator = "PaymentModifier")
	@GenericGenerator(name = "PaymentModifier", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PAYMENT_MODIFIER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "relevanceScore", column = @Column(name = "relevance_score")),
			@AttributeOverride(name = "warrantyType", column = @Column(name = "warranty_type")),
			@AttributeOverride(name = "claimType", column = @Column(name = "claim_type")),
			@AttributeOverride(name = "productName", column = @Column(name = "product_name")),
			@AttributeOverride(name = "identifier", column = @Column(name = "identifier")),
			@AttributeOverride(name = "wntyTypeName", column = @Column(name = "wnty_type_name")),
			@AttributeOverride(name = "clmTypeName", column = @Column(name = "clm_type_name")) })
	private Criteria forCriteria = new Criteria();

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@Sort(type = SortType.NATURAL)
	private SortedSet<CriteriaBasedValue> entries = new TreeSet<CriteriaBasedValue>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private PaymentVariable forPaymentVariable;

	private String customerType;	
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	
	private Boolean landedCost = Boolean.FALSE;


	@Override
    public SortedSet<CriteriaBasedValue> getEntries() {
        return this.entries;
    }
	
	@ManyToOne(fetch = FetchType.LAZY)
	private OrganizationAddress servicingLocation;
	
	public OrganizationAddress getServicingLocation() {
		return servicingLocation;
	}

	public void setServicingLocation(OrganizationAddress servicingLocation) {
		this.servicingLocation = servicingLocation;
	}

	public Criteria getCriteria() {
		return this.forCriteria;
	}

	// ************************ Accesssors & Mutators
	// ***********************//
	public Criteria getForCriteria() {
		return this.forCriteria;
	}

	public void setForCriteria(Criteria forCriteria) {
		this.forCriteria = forCriteria;
	}

	public PaymentVariable getForPaymentVariable() {
		return this.forPaymentVariable;
	}

	public void setForPaymentVariable(PaymentVariable forPaymentVariable) {
		this.forPaymentVariable = forPaymentVariable;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setEntries(SortedSet<CriteriaBasedValue> entries) {
		this.entries = entries;
	}

	public void computeCriteriaRelevanceScore() {
		int score = 0;
		int weight = 1;

		if (this.forCriteria.isProductTypeSpecified()) {
			score = score + weight;
		}

		weight = weight * 2;
		if (this.forCriteria.isWarrantyTypeSpecified()) {
			score = score + weight;
		}

		weight = weight * 2;
		if (this.forCriteria.isClaimTypeSpecified()) {
			score = score + weight;
		}

		weight = weight * 2;
		if (this.forCriteria.isDealerGroupSpecified()) {
			score = score + weight;
		}

		weight = weight * 2;
		if (this.forCriteria.isDealerSpecified()) {
			score = score + weight;
		}

		this.forCriteria.setRelevanceScore(score);
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.id).append(
				"forCriteria", this.forCriteria).toString();
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@Override
	public CriteriaBasedValue newTimeBoundValue(Double value,
			CalendarDuration forDuration) {
		// TODO Auto-generated method stub
		return null;
	}

	public CriteriaBasedValue newTimeBoundValueModifier(Double value,
			CalendarDuration forDuration, Boolean isFlatRate) {
		return new CriteriaBasedValue(value, forDuration, isFlatRate);
	}


	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	public Boolean isLandedCost() {
		return landedCost;
	}

	public void setLandedCost(Boolean landedCost) {
		this.landedCost = landedCost;
	}

    
}

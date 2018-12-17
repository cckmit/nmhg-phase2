/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.claim.payment.definition;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.policy.Policy;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class PaymentDefinition implements BusinessUnitAware,AuditableColumns{
	@Id
	@GeneratedValue(generator = "PaymentDefintion")
	@GenericGenerator(name = "PaymentDefintion", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PAYMENT_DEFINITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    // Has Warranty type, Policies categories
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private PolicyCriteria criteria = new PolicyCriteria();

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "sections_in_pymt_defn", joinColumns = { @JoinColumn(name = "pymt_defn") }, inverseJoinColumns = { @JoinColumn(name = "pymt_section") })
    @IndexColumn(name = "display_position", nullable = true)
    private List<PaymentSection> paymentSections = new ArrayList<PaymentSection>();

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date")),
            @AttributeOverride(name = "tillDate", column = @Column(name = "till_date")) })
    private CalendarDuration forDuration = new CalendarDuration();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

    public PolicyCriteria getCriteria() {
        return this.criteria;
    }

    public void setCriteria(PolicyCriteria criteria) {
        this.criteria = criteria;
    }

    public List<PaymentSection> getPaymentSections() {
        return this.paymentSections;
    }

    public void setPaymentSections(List<PaymentSection> paymentSections) {
        this.paymentSections = paymentSections;
    }

    public PaymentSection addSection(Section section) {
        PaymentSection paymentSection = new PaymentSection();
        paymentSection.setSection(section);
        this.paymentSections.add(paymentSection);
        return paymentSection;
    }

    public PaymentSection getSectionForName(String name) {
        for (PaymentSection s : this.paymentSections) {
            if (s.getSection().getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public CalendarDuration getForDuration() {
        return this.forDuration;
    }

    public void setForDuration(CalendarDuration forDuration) {
        this.forDuration = forDuration;
    }

    public int getSuitabilityScore(Claim claim, Policy policy) {
        return this.criteria.getSuitabilityScore(claim, policy);
    }

    public void computeCriteriaRelevanceScore() {
        int score = 0;
        int weight = 1;

        if (this.criteria.isProductTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.criteria.isWarrantyTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.criteria.isClaimTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.criteria.isDealerGroupSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.criteria.isDealerSpecified()) {
            score = score + weight;
        }

        this.criteria.setRelevanceScore(score);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("forDuration", this.forDuration)
                .append("paymentSections", this.paymentSections).toString();
    }

    public PaymentVariableLevel getPaymentVariableLevel(String section, Long varibaleId) {
        PaymentVariableLevel paymentVarLevel = new PaymentVariableLevel();
        for (PaymentSection paymentSection : getPaymentSections()) {
            if (section.equals(paymentSection.getSection().getName())) {
                for (PaymentVariableLevel paymentVariableLevel : paymentSection
                        .getPaymentVariableLevels()) {
                    if (varibaleId.equals(paymentVariableLevel.getPaymentVariable().getId())) {
                        paymentVarLevel = paymentVariableLevel;
                    }
                }
            }
        }
        return paymentVarLevel;

    }

    public boolean doesCoverSection(String section) {
        boolean doesCover = false;
        for (PaymentSection paymentSection : getPaymentSections()) {
            if (section.equals(paymentSection.getSection().getName())) {
                doesCover = true;
            }
        }
        return doesCover;
    }

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


}

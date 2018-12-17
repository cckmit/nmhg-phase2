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
package tavant.twms.domain.policy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.TimeBoundValues;

/**
 * @author kiran.sg
 * 
 */
@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class PolicyRates extends TimeBoundValues<BigDecimal, PolicyRate>
		implements BusinessUnitAware{
	@Id
	@GeneratedValue(generator = "PolicyRates")
	@GenericGenerator(name = "PolicyRates", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_RATES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "relevanceScore", column = @Column(name = "relevance_score")),
            @AttributeOverride(name = "warrantyType", column = @Column(name = "warranty_type")),
            @AttributeOverride(name = "customerState", column = @Column(name = "customer_state")),
            @AttributeOverride(name = "warrantyRegistrationType", column = @Column(name = "warranty_registration_type", nullable = false)),
            @AttributeOverride(name = "claimType", column = @Column(name = "claim_type")),
            @AttributeOverride(name = "productName", column = @Column(name = "product_name")),
            @AttributeOverride(name = "identifier", column = @Column(name = "identifier")),
            @AttributeOverride(name = "wntyTypeName", column = @Column(name = "wnty_type_name")),
            @AttributeOverride(name = "clmTypeName", column = @Column(name = "clm_type_name"))}) 
    private PolicyRatesCriteria forCriteria = new PolicyRatesCriteria();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "policy_rates_policy_defs")
    private List<PolicyDefinition> policyDefinitions = new ArrayList<PolicyDefinition>();

    @ManyToMany
    private List<Label> policyLabels = new ArrayList<Label>();

    @OneToMany(mappedBy = "policyRates", fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Sort(type = SortType.NATURAL)
    private SortedSet<PolicyRate> rates = new TreeSet<PolicyRate>();

    @Override
    public SortedSet<PolicyRate> getEntries() {
        return this.rates;
    }

    @Override
    public PolicyRate newTimeBoundValue(BigDecimal value, CalendarDuration forDuration) {
        return new PolicyRate(value, forDuration);
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

    public List<PolicyDefinition> getPolicyDefinitions() {
        return this.policyDefinitions;
    }

    public void setPolicyDefinitions(List<PolicyDefinition> policyDefinitions) {
        this.policyDefinitions = policyDefinitions;
    }

    public SortedSet<PolicyRate> getRates() {
        return this.rates;
    }

    public void setRates(SortedSet<PolicyRate> rates) {
        this.rates = rates;
    }

    public PolicyRatesCriteria getForCriteria() {
        return this.forCriteria;
    }

    public void setForCriteria(PolicyRatesCriteria forCriteria) {
        this.forCriteria = forCriteria;
    }

    /*
     * (non-Javadoc)
     * 
     * @see tavant.twms.domain.common.TimeBoundValues#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("forCriteria").append('=').append(this.forCriteria);
        buf.append(" ").append(super.toString());
        return buf.toString();
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

    public List<Label> getPolicyLabels() {
        return this.policyLabels;
    }

    public void setPolicyLabels(List<Label> policyLabels) {
        this.policyLabels = policyLabels;
    }
    
    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	@Override
	public PolicyRate newTimeBoundValueModifier(BigDecimal value,
			CalendarDuration forDuration, Boolean isFlatRate) {
		// TODO Auto-generated method stub
		return null;
	}
}

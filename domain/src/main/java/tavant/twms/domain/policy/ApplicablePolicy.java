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
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ApplicablePolicy implements Policy, AuditableColumns {

    @Id
    @GeneratedValue(generator = "ApplicablePolicy")
	@GenericGenerator(name = "ApplicablePolicy", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "APPLICABLE_POLICY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private PolicyDefinition policyDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    private RegisteredPolicy registeredPolicy;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date", nullable = true)),
            @AttributeOverride(name = "tillDate", column = @Column(name = "till_date", nullable = true)) })
    private CalendarDuration warrantyPeriodIfUnregisteredPolicy;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    // for frameworks
    public ApplicablePolicy() {
    }

    public ApplicablePolicy(PolicyDefinition policyDefinition, CalendarDuration warrantyPeriod) {
        Assert.notNull(policyDefinition, "Policy Definition cannot be null");
        Assert.notNull(warrantyPeriod, "Warranty period cannot be null");
        this.policyDefinition = policyDefinition;
        this.warrantyPeriodIfUnregisteredPolicy = warrantyPeriod;
    }

    public ApplicablePolicy(RegisteredPolicy registeredPolicy) {
        Assert.notNull(registeredPolicy, "Registered policy cannot be null");
        this.registeredPolicy = registeredPolicy;
        this.policyDefinition = registeredPolicy.getPolicyDefinition();
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

    public PolicyDefinition getPolicyDefinition() {
        return getDelegate().getPolicyDefinition();
    }

    public void setPolicyDefinition(PolicyDefinition policyDefinition) {
        this.policyDefinition = policyDefinition;
    }

    public RegisteredPolicy getRegisteredPolicy() {
        return this.registeredPolicy;
    }

    public void setRegisteredPolicy(RegisteredPolicy registeredPolicy) {
        this.registeredPolicy = registeredPolicy;
    }

    public boolean covers(ClaimedItem claimedItem, Integer serviceHoursCovered) throws PolicyException {
        return getDelegate().covers(claimedItem, serviceHoursCovered);
    }

    public boolean covers(Claim claimedItem, Integer serviceHoursCovered) throws PolicyException {
        return getDelegate().covers(claimedItem, serviceHoursCovered);
    }
    
    public String getDescription() {
        return getDelegate().getDescription();
    }

    public CalendarDuration getWarrantyPeriod() {
        if (isARegisteredPolicy()) {
            return this.registeredPolicy.getWarrantyPeriod();
        } else {
            return this.warrantyPeriodIfUnregisteredPolicy;
        }
    }

    public boolean isAvailable(InventoryItem inventoryItem, CalendarDate asOfDate)
            throws PolicyException {
        return getDelegate().isAvailable(inventoryItem, asOfDate);
    }

    public boolean isStillAvailableFor(InventoryItem inventoryItem) throws PolicyException {
        return getDelegate().isStillAvailableFor(inventoryItem);
    }

    public WarrantyType getWarrantyType() {
        return getDelegate().getWarrantyType();
    }

    private Policy getDelegate() {
        return isARegisteredPolicy() ? this.registeredPolicy : this.policyDefinition;
    }

    public boolean isARegisteredPolicy() {
        return this.registeredPolicy != null;
    }

    public String getCode() {
        return getDelegate().getCode();
    }

    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#isApplicable(tavant.twms.domain.claim.Claim,
     * tavant.twms.domain.rules.RuleExecutionTemplate)
     */
    public boolean isApplicable(Claim claim, RuleExecutionTemplate ruleExecutionTemplate) {
        return getDelegate().isApplicable(claim, ruleExecutionTemplate);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("policyDefinition",
                                                                      this.policyDefinition)
                .append("registeredPolicy", this.registeredPolicy)
                .append("warrantyPeriodIfUnregisteredPolicy",
                        this.warrantyPeriodIfUnregisteredPolicy).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public boolean covers(ClaimedItem claimedItem,
			CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
		 return getDelegate().covers(claimedItem, warrantyPeriod,serviceHoursCovered);
	}
}

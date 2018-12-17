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
package tavant.twms.domain.claim.payment.definition;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyDefinition;

/**
 * @author kannan.ekanath
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PolicyCriteria extends Criteria {
    private static final Logger logger = Logger.getLogger(PolicyCriteria.class);

    @Id
	@GeneratedValue(generator = "PolicyCriteria")
	@GenericGenerator(name = "PolicyCriteria", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_CRITERIA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne
    private Label label;

    @ManyToOne(fetch = FetchType.LAZY)
    private PolicyDefinition policyDefinition;
    
    private boolean applForCommPolicyClaims  = false;

    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSuitabilityScore(Claim claim, Policy policy) {
        if(logger.isDebugEnabled())
        {
            logger.debug("Finding suitability score for [" + this + "] claim [" + claim + "]");
            logger.debug("The Claims policy is [" + policy + "]");
        }
        int score = 0;
        boolean isAllClaimTypesCovered = (getClaimType() == ClaimType.ALL);
        boolean doesClaimTypeMatch = (!isAllClaimTypesCovered && getClaimType()
                .equals(claim.getType()));
        // If both are not true then dont evaluate policy further
        if (!doesClaimTypeMatch && !isAllClaimTypesCovered) {
            if(logger.isDebugEnabled()) {
                logger.debug("Returning -1 since claim type mismatch");
            }
            return -1;
        }

        boolean isAllPoliciesCovered = ((this.label == null) && (this.policyDefinition == null));
        boolean doesLabelMatch = (this.label != null) && (policy != null)
                && (policy.getPolicyDefinition().getLabels().contains(this.label));
        boolean doesPolicyExactlyMatch = (this.policyDefinition != null) && (policy != null)
                && (this.policyDefinition.equals(policy.getPolicyDefinition()));
        // If none of this is true then return -1;
        if (!isAllPoliciesCovered && !doesLabelMatch && !doesPolicyExactlyMatch) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Returning -1 since policy/group mismatch");
            }
            return -1;
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Criteria has label [" + this.label + "] policy defn ["+ this.policyDefinition + "]");
        }
        // Add scores now
        score += doesPolicyExactlyMatch ? 16 : 0;
        score += doesLabelMatch ? 8 : 0;
        score += isAllPoliciesCovered ? 1 : 0;

        score += doesClaimTypeMatch ? 4 : 0;
        score += isAllClaimTypesCovered ? 1 : 0;
        if(logger.isDebugEnabled())
        {
            logger.debug("Score is [" + score + "]");
        }
        return score;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append(super.toString()).append("id", this.id).toString();
    }

    public PolicyDefinition getPolicyDefinition() {
        return this.policyDefinition;
    }

    public void setPolicyDefinition(PolicyDefinition policyDefinition) {
        this.policyDefinition = policyDefinition;
    }

    public Label getLabel() {
        return this.label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

	public void setApplForCommPolicyClaims(boolean applForCommPolicyClaims ) {
		this.applForCommPolicyClaims  = applForCommPolicyClaims ;
	}

	public boolean isApplForCommPolicyClaims() {
		return applForCommPolicyClaims ;
	}

}

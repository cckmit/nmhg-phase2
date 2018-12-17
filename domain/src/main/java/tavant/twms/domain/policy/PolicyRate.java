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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValue;

/**
 * @author kiran.sg
 * 
 */
@Entity
public class PolicyRate extends TimeBoundValue<BigDecimal> {

	@Id
	@GeneratedValue(generator = "PolicyRate")
	@GenericGenerator(name = "PolicyRate", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_RATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private BigDecimal value;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PolicyRates policyRates;

    public PolicyRate() {
        super();
    }

    public PolicyRate(BigDecimal value, CalendarDuration duration) {
        super(duration);
        this.value = value;
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

    @Override
    public void setParent(Object parent) {
        this.policyRates = (PolicyRates) parent;
    }

    @Override
    public void setValue(BigDecimal newValue) {
        this.value = newValue;
    }

    @Override
    public BigDecimal getValue() {
        return this.value;
    }

    public BigDecimal getRate() {
        return this.value;
    }

    public void setRate(BigDecimal rate) {
        this.value = rate;
    }

    public PolicyRates getPolicyRates() {
        return this.policyRates;
    }

    public void setPolicyRates(PolicyRates policyRates) {
        this.policyRates = policyRates;
    }
}
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
package tavant.twms.domain.claim.payment.definition.modifiers;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import javax.validation.constraints.NotNull;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValue;

@Entity
@SuppressWarnings("unused")
public class CriteriaBasedValue extends TimeBoundValue<Double> {

	@Id
	@GeneratedValue(generator = "CriteriaBasedValue")
	@GenericGenerator(name = "CriteriaBasedValue", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CRITERIA_BASED_VALUE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@NotNull
	private Double value;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private PaymentModifier parent;

	private Boolean isFlatRate;

	public CriteriaBasedValue() {
		super();
	}

	public CriteriaBasedValue(Double value, CalendarDuration forDuration, Boolean isFlatRate ) {
		super(forDuration);
		this.value = value;
		this.isFlatRate = isFlatRate;
	}

	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public void setParent(Object parent) {
		this.parent = (PaymentModifier) parent;
	}

	@Override
	public void setValue(Double newValue) {
		this.value = newValue;
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

	public void setPercentage(Double doubleValue) {
		this.value = doubleValue;
	}

	public Double getPercentage() {
		return this.value;
	}

	public Boolean getIsFlatRate() {
		return isFlatRate;
	}

	public void setIsFlatRate(Boolean isFlatRate) {
		this.isFlatRate = isFlatRate;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.id).append("value",
				this.value).append("parent",
				(this.parent != null) ? this.parent.getId() : null).toString();
	}

    public PaymentModifier getParent() {
        return parent;
    }
}
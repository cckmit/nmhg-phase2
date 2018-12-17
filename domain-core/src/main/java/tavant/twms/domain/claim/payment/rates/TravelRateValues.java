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
package tavant.twms.domain.claim.payment.rates;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
public class TravelRateValues {
	
	@Id
	@GeneratedValue(generator = "TravelRateValue")
	@GenericGenerator(name = "TravelRateValue", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "TRAVEL_RATE_VALUES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "DISTANCE_RATE_AMT"), @Column(name = "DISTANCE_RATE_CURR") })
	private Money distanceRate;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "HOURLY_RATE_AMT"), @Column(name = "HOURLY_RATE_CURR") })
	private Money hourlyRate;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "TRIP_RATE_AMT"), @Column(name = "TRIP_RATE_CURR") })
	private Money tripRate;
	
	@Transient 
	private Money currency;
	
    
    public Money getDistanceRate() {
        return this.distanceRate;
    }

    public void setDistanceRate(Money distanceRate) {
        this.distanceRate = distanceRate;
    }

    public Money getHourlyRate() {
        return this.hourlyRate;
    }

    public void setHourlyRate(Money hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Money getTripRate() {
        return this.tripRate;
    }

    public void setTripRate(Money tripRate) {
        this.tripRate = tripRate;
    }

       
    public String toString() {
        return new ToStringCreator(this).append("Hourly Rate", this.hourlyRate).append(
                "Distance Rate", this.distanceRate).append("Trip Rate", this.tripRate).toString();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Money getCurrency() {
		return currency;
	}

	public void setCurrency(Money currency) {
		this.currency = currency;
	}
	
	public String getCurrencyCode() {
		Money money = null;
		if(currency != null)
			money = currency;
		else if(hourlyRate!=null)
			money = hourlyRate;
		else if(distanceRate != null)
			money = distanceRate;
		else if(tripRate != null)
			money = tripRate;
		if(money != null)
			return money.breachEncapsulationOfCurrency().getCurrencyCode();
		return null;
	}
}
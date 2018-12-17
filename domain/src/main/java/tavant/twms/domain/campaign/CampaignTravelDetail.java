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
package tavant.twms.domain.campaign;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class CampaignTravelDetail implements AuditableColumns{
	@Id
	@GeneratedValue(generator = "CampaignTravelDetail")
	@GenericGenerator(name = "CampaignTravelDetail", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_TRAVEL_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private BigDecimal distance = BigDecimal.ZERO;

	private BigDecimal hours = BigDecimal.ZERO;
	
	private BigDecimal additionalHours = BigDecimal.ZERO;

	private int trips;

	private String location;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "distance_charge_amt"),
			@Column(name = "distance_charge_curr") })
	private Money distanceCharge;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "time_charge_amt"),
			@Column(name = "time_charge_curr") })
	private Money timeCharge;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "trip_charge_amt"),
			@Column(name = "trip_charge_curr") })
	private Money tripCharge;

	private String uom;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	/**
	 * @return the distanceCharge
	 */
	public Money getDistanceCharge() {
		return distanceCharge;
	}

	/**
	 * @param distanceCharge
	 *            the distanceCharge to set
	 */
	public void setDistanceCharge(Money distanceCharge) {
		this.distanceCharge = distanceCharge;
	}

	/**
	 * @return the timeCharge
	 */
	public Money getTimeCharge() {
		return timeCharge;
	}

	/**
	 * @param timeCharge
	 *            the timeCharge to set
	 */
	public void setTimeCharge(Money timeCharge) {
		this.timeCharge = timeCharge;
	}

	/**
	 * @return the tripCharge
	 */
	public Money getTripCharge() {
		return tripCharge;
	}

	/**
	 * @param tripCharge
	 *            the tripCharge to set
	 */
	public void setTripCharge(Money tripCharge) {
		this.tripCharge = tripCharge;
	}
	
	/**
	 * @return the distance
	 */
	public BigDecimal getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(BigDecimal distance) {
		this.distance = distance;		
	}

	/**
	 * @return the hours
	 */
	public BigDecimal getHours() {
		return hours;
	}

	/**
	 * @param hours the hours to set
	 */
	public void setHours(BigDecimal hours) {
	    this.hours = hours;

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getSpeed() {
		if (hours==null||hours.equals(BigDecimal.ZERO)) {
			return 0;
		}
		return distance.divide(hours).doubleValue();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getTrips() {
		return trips;
	}

	public void setTrips(int trips) {
		this.trips = trips;
	}

	public Money cost() {
		Money travelCost = Money.valueOf(0.0D, timeCharge
				.breachEncapsulationOfCurrency());
		travelCost = travelCost.plus(timeCharge.times(hours));
		travelCost = travelCost.plus(distanceCharge.times(distance));
		travelCost = travelCost.plus(tripCharge.times(trips));
		return travelCost;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("distance",
				distance).append("hours", hours).append("trips", trips).append(
				"distance charge", distanceCharge).append("time charge",
				timeCharge).append("trip charge", tripCharge).toString();
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public BigDecimal getAdditionalHours() {
		return additionalHours;
	}

	public void setAdditionalHours(BigDecimal additionalHours) {
		this.additionalHours = additionalHours;
	}
}
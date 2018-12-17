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
package tavant.twms.domain.claim;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
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
        @Filter(name = "excludeInactive")
})
public class TravelDetail implements AuditableColumns {
    @Id
    @GeneratedValue(generator = "TravelDetail")
    @GenericGenerator(name = "TravelDetail", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "TRAVEL_DETAIL_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @Version
    private int version;

    private BigDecimal distance;
    
    private BigDecimal baseDistance;

    private String hours;
    
    private String baseHours;

    private BigDecimal additionalHours;

    private Integer trips;

    private String location;
    
    private BigDecimal additionalDistance;
    
    private String additionalDistanceReason;
    // Added for NMHGSLMS-577: RTM-165: START
    private String additionalHoursReason;
    
    private Boolean travelAddressChanged=Boolean.FALSE;
    // Added for NMHGSLMS-577: RTM-165: END
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "distance_charge_amt"),
            @Column(name = "distance_charge_curr")})
    private Money distanceCharge;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "time_charge_amt"), @Column(name = "time_charge_curr")})
    private Money timeCharge;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "trip_charge_amt"), @Column(name = "trip_charge_curr")})
    private Money tripCharge;

    private String uom;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    /**
     * @return the distanceCharge
     */
    public Money getDistanceCharge() {
        return this.distanceCharge;
    }

    /**
     * @param distanceCharge the distanceCharge to set
     */
    public void setDistanceCharge(Money distanceCharge) {
        this.distanceCharge = distanceCharge;
    }

    /**
     * @return the timeCharge
     */
    public Money getTimeCharge() {
        return this.timeCharge;
    }

    /**
     * @param timeCharge the timeCharge to set
     */
    public void setTimeCharge(Money timeCharge) {
        this.timeCharge = timeCharge;
    }

    /**
     * @return the tripCharge
     */
    public Money getTripCharge() {
        return this.tripCharge;
    }

    /**
     * @param tripCharge the tripCharge to set
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
    public String getHours() {
        return hours;
    }

    /**
     * @param hours the hours to set
     */
    public void setHours(String hours) {
        this.hours = hours;
    }

    public BigDecimal getBaseDistance() {
		return baseDistance;
	}

	public void setBaseDistance(BigDecimal baseDistance) {
		this.baseDistance = baseDistance;
	}

	public String getBaseHours() {
		return baseHours;
	}

	public void setBaseHours(String baseHours) {
		this.baseHours = baseHours;
	}

	/**
     * @return the id
     */
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

    public double getSpeed() {    	
        if (StringUtils.isEmpty(this.hours)||this.hours.equals("0")) {
            return 0;
        }

        return (this.distance.divide(new BigDecimal(this.hours))).doubleValue();
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTrips() {
        return trips;
    }

    public void setTrips(Integer trips) {
        this.trips = trips;
    }

    public Money cost() {
        Money travelCost = Money.valueOf(0.0D, this.timeCharge.breachEncapsulationOfCurrency());
        travelCost = travelCost.plus(getTravelCostByTime());
        travelCost = travelCost.plus(getTravelCostByDistance());
        travelCost = travelCost.plus(getTravelCostByTrips());
        return travelCost;
    }

    private Money getTravelCostByTrips() {
        return this.tripCharge.times(this.trips);
    }

    private Money getTravelCostByDistance() {
        return this.distanceCharge.times(this.distance);
    }

    private Money getTravelCostByTime() {
    	Money cost=this.timeCharge;
    	if(!StringUtils.isEmpty(this.hours))
    		cost=cost.times(new BigDecimal(this.hours));
    	return cost;
    }

    public String getUom() {
        return this.uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("distance", this.distance)
                .append("hours", this.hours).append("trips", this.trips)
                .append("distance charge", this.distanceCharge).append("time charge",
                        this.timeCharge).append("additional hours",this.additionalHours)
                        .append("travel address changed",this.travelAddressChanged)
                .append("trip charge", this.tripCharge).toString();
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

    public TravelDetail clone() {
        TravelDetail travelDetail = new TravelDetail();
        travelDetail.setAdditionalHours(additionalHours);
        travelDetail.setDistance(distance);
        travelDetail.setDistanceCharge(distanceCharge);
        travelDetail.setHours(hours);
        travelDetail.setLocation(location);
        travelDetail.setTimeCharge(timeCharge);
        travelDetail.setTripCharge(tripCharge);
        travelDetail.setTrips(trips);
        travelDetail.setAdditionalHours(additionalHours);
        travelDetail.setUom(uom);
        travelDetail.setAdditionalDistance(additionalDistance);
        travelDetail.setAdditionalDistanceReason(additionalDistanceReason);
        travelDetail.setAdditionalHoursReason(additionalHoursReason); // Added for NMHGSLMS-577: RTM-165
        travelDetail.setTravelAddressChanged(travelAddressChanged); // Added for NMHGSLMS-577: RTM-165
        return travelDetail;
    }

	public BigDecimal getAdditionalDistance() {
		return additionalDistance;
	}

	public void setAdditionalDistance(BigDecimal additionalDistance) {
		this.additionalDistance = additionalDistance;
	}

	public String getAdditionalDistanceReason() {
		return additionalDistanceReason;
	}

	public void setAdditionalDistanceReason(String additionalDistanceReason) {
		this.additionalDistanceReason = additionalDistanceReason;
	}
	
	/* Added for NMHGSLMS-577: RTM-165 : START */
	public String getAdditionalHoursReason() {
		return additionalHoursReason;
	}

	public void setAdditionalHoursReason(String additionalHoursReason) {
		this.additionalHoursReason = additionalHoursReason;
	}
	
    public Boolean isTravelAddressChanged() {
        return this.travelAddressChanged;
    }

    public Boolean getTravelAddressChanged() {
        return travelAddressChanged;
    }
    
    public void setTravelAddressChanged(Boolean travelAddressChanged) {
        this.travelAddressChanged = travelAddressChanged;
    }
    
	/* Added for NMHGSLMS-577: RTM-165 : END */
}

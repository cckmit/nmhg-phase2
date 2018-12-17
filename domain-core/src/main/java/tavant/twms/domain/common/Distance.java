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
package tavant.twms.domain.common;

import static tavant.twms.domain.common.DistanceUnit.kilometer;
import static tavant.twms.domain.common.DistanceUnit.mile;
import static tavant.twms.infra.BigDecimalFactory.bigDecimalOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.springframework.util.Assert;

/**
 * @author radhakrishnan.j
 */
@Embeddable
public class Distance implements Comparable<Distance> {
    private static final BigDecimal ONE = bigDecimalOf(1D);
    private static final BigDecimal TEN = bigDecimalOf(10D);
    private static final BigDecimal HUNDRED = bigDecimalOf(100D);    
    private static final BigDecimal ZERO = bigDecimalOf(0D);    
    
    private BigDecimal quantity = ZERO;
    
    @Embedded
    private DistanceUnit unit;

    private static Map<Conversion, BigDecimal> conversionFactors = new HashMap<Conversion, BigDecimal>();

    static {
        //If the conversion were to be externalized, how would things
        //look :?
        Conversion conversion = new Conversion(mile, kilometer);
        //1 mi = 1.609344 km.        
        conversionFactors.put(conversion, bigDecimalOf(1.609344D) );
        
        //1 km = 0.62137119 mi.
        conversion = new Conversion(kilometer, mile);
        conversionFactors.put(conversion, bigDecimalOf(0.62137119D) );
    }

    private static class Conversion {
        private DistanceUnit from;

        private DistanceUnit to;

        public Conversion(DistanceUnit from, DistanceUnit to) {
            super();
            this.from = from;
            this.to = to;
        }

        public DistanceUnit getFrom() {
            return from;
        }

        public void setFrom(DistanceUnit from) {
            this.from = from;
        }

        public DistanceUnit getTo() {
            return to;
        }

        public void setTo(DistanceUnit to) {
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((from == null) ? 0 : from.hashCode());
            result = PRIME * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Conversion other = (Conversion) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }
        
        
    }

    private Distance(BigDecimal newQuantity, DistanceUnit newUnit) {
        Assert.notNull(newQuantity);
        Assert.notNull(newUnit);        
        quantity = newQuantity.setScale(2,RoundingMode.HALF_EVEN);
        unit = newUnit;
    }

    public static Distance valueOf(BigDecimal quantity,DistanceUnit unit) {
        return new Distance(quantity,unit);
    }
    
    public static Distance valueOf(double newQuantity,DistanceUnit unit) {
        return valueOf(bigDecimalOf(newQuantity),unit);
    }    
    
    public static Distance miles(double distanceInMiles) {
        return valueOf(distanceInMiles,mile);
    }

    public static Distance miles(BigDecimal distanceInMiles) {
        return valueOf(distanceInMiles,mile);
    }
    
    public static Distance kilometers(double distanceInKm) {
        return valueOf(distanceInKm,kilometer);
    }

    public static Distance kilometers(BigDecimal distanceInKm) {
        return valueOf(distanceInKm,kilometer);
    }

    public static Distance _1_Mile() {
        return valueOf(ONE,mile);
    }    

    public static Distance _10_Miles() {
        return valueOf(ONE,mile).times(TEN);
    }

    public static Distance _100_Miles() {
        return valueOf(ONE,mile).times(HUNDRED);
    }

    public static Distance _1_Kilometers() {
        return valueOf(ONE,kilometer);
    }    
    
    public static Distance _10_Kilometers() {
        return valueOf(ONE,kilometer).times(TEN);
    }

    public static Distance _100_Kilometers() {
        return valueOf(ONE,kilometer).times(HUNDRED);
    }

    public Distance times(double factor) {
        return times(bigDecimalOf(factor));
    }

    public Distance times(BigDecimal byFactor) {
        Assert.notNull(byFactor);        
        BigDecimal newQuantity = quantity.multiply(byFactor).setScale(2,RoundingMode.HALF_EVEN);
        return Distance.valueOf(newQuantity, unit);
    }


    public Distance dividedBy(BigDecimal byFactor) {
        Assert.notNull(byFactor);        
        BigDecimal newQuantity = quantity.divide(byFactor,2,RoundingMode.HALF_EVEN);
        return Distance.valueOf(newQuantity, unit);
    }

    public BigDecimal dividedBy(Distance aDistance) {
        Assert.notNull(aDistance);
        if( hasSameUnitAs(aDistance)) {
            return quantity.divide(aDistance.quantity,2,RoundingMode.HALF_EVEN);
        } else {
            Distance normalizedDistance = aDistance.to(unit);
            return quantity.divide(normalizedDistance.quantity,2,RoundingMode.HALF_EVEN);
        }
    }    
    
    public Distance add(Distance anotherDistance) {
        Assert.notNull(anotherDistance);        
        Distance normalizedDistance = hasSameUnitAs(anotherDistance) ?  anotherDistance : anotherDistance.to(unit);
        BigDecimal newQuantity = quantity.add(normalizedDistance.quantity);
        return Distance.valueOf(newQuantity, unit);
    }
    
    
    public Distance to(DistanceUnit anotherUnit) {
        Assert.notNull(anotherUnit);
        if( unit.equals(anotherUnit) ) {
            return this;
        }
        Conversion newConversion = new Conversion(unit, anotherUnit);
        BigDecimal conversionFactor = conversionFactors.get(newConversion);
        if( conversionFactor==null ) {
            throw new RuntimeException("Conversion from unit ["+unit+"] to unit ["+anotherUnit+"] not supported");
        }
        BigDecimal newQuantity = quantity.multiply(conversionFactor).setScale(2,RoundingMode.HALF_EVEN);
        return valueOf(newQuantity,anotherUnit);
    }
    
    public BigDecimal units() {
        return quantity;
    }
    
    public BigDecimal units(DistanceUnit inUnit) {
        return to(inUnit).units();
    }    
    
    @Override    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(quantity);
        buf.append(" ");
        buf.append(unit);
        if( quantity.abs().compareTo( ONE ) >= 0 ) {
            buf.append('s');            
        }
        return buf.toString();
    }

    //These accessors are for frameworks like webwork / hibernate
    //and are not meant for explicit use in the domain.
    //TODO: Need to define check in PMD to report usages of these API
    
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public void setUnit(DistanceUnit unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((quantity == null) ? 0 : quantity.hashCode());
        result = PRIME * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Distance otherDistance = (Distance) obj;
        Distance normalizedDistance = null;
        if( hasSameUnitAs(otherDistance) ) {
            normalizedDistance = otherDistance;            
        } else {
            normalizedDistance = otherDistance.to(unit);            
        }
        return quantity.equals(normalizedDistance.quantity);
    }

    /**
     * @param otherDistance
     * @return
     */
    private boolean hasSameUnitAs(final Distance otherDistance) {
        return unit.equals(otherDistance.unit);
    }

    public int compareTo(Distance otherDistance) {
        if( otherDistance==null) {
            return -1;
        } else if( equals(otherDistance) ) {
            return 0;
        } else {
            if( hasSameUnitAs(otherDistance)) {
                return quantity.compareTo( otherDistance.quantity );
            } else {
                return quantity.compareTo( otherDistance.to(unit).quantity );
            }
        }
    }
}

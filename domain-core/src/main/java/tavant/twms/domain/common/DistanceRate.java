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
package tavant.twms.domain.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import tavant.twms.infra.BigDecimalFactory;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 *
 */
@Embeddable
public class DistanceRate {
    @Type(type="tavant.twms.infra.MoneyUserType")
    @Columns(columns = {
            @Column(name="amount",nullable=false),
            @Column(name="currency",nullable=false)})    
    private Money rate;

    @Embedded
    private Distance benchmarkDistance = Distance._10_Kilometers().dividedBy(BigDecimalFactory.bigDecimalOf(10));

    public static DistanceRate valueOf(Money aRate,Distance aDistance) {
        DistanceRate distanceRate = new DistanceRate(aRate,aDistance);
        return distanceRate; 
    }
    
    public DistanceRate() {
    }
    
    public DistanceRate(Money aRate,Distance aDistance) {
        rate = aRate;
        benchmarkDistance = aDistance;
    }
    
    public Money getRate() {
        return rate;
    }

    public void setRate(Money rate) {
        this.rate = rate;
    }

    public Money costFor(Distance aDistance) {
        return rate.times( aDistance.dividedBy(benchmarkDistance) );
    }

    
    
    public Distance getBenchmarkDistance() {
        return benchmarkDistance;
    }

    public void setBenchmarkDistance(Distance benchmarkDistance) {
        this.benchmarkDistance = benchmarkDistance;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("rate").append('=').append(rate);
        buf.append("benchmarkDistance").append('=').append(benchmarkDistance);
        return buf.toString();
    }
    
    
}

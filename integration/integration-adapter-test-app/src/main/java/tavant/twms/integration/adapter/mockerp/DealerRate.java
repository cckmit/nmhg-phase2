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

package tavant.twms.integration.adapter.mockerp;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DealerRate {

    @Id
    @GeneratedValue
    private Long id;

    // For criteria
    private String warrantyType;

    private String claimType;

    private String productType;

    private String dealerNumber;

    // For duration
    private Date fromDate;

    private Date toDate;

    private String currency;

    private BigDecimal laborAmount;

    private BigDecimal perKmAmount;

    private BigDecimal perMileAmount;

    private BigDecimal perTripAmount;

    private BigDecimal perHourAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDealerNumber() {
        return dealerNumber;
    }

    public void setDealerNumber(String dealerNumber) {
        this.dealerNumber = dealerNumber;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getLaborAmount() {
        return laborAmount;
    }

    public void setLaborAmount(BigDecimal laborAmount) {
        this.laborAmount = laborAmount;
    }

    public BigDecimal getPerKmAmount() {
        return perKmAmount;
    }

    public void setPerKmAmount(BigDecimal perKmAmount) {
        this.perKmAmount = perKmAmount;
    }

    public BigDecimal getPerMileAmount() {
        return perMileAmount;
    }

    public void setPerMileAmount(BigDecimal perMileAmount) {
        this.perMileAmount = perMileAmount;
    }

    public BigDecimal getPerTripAmount() {
        return perTripAmount;
    }

    public void setPerTripAmount(BigDecimal perTripAmount) {
        this.perTripAmount = perTripAmount;
    }

    public BigDecimal getPerHourAmount() {
        return perHourAmount;
    }

    public void setPerHourAmount(BigDecimal perHourAmount) {
        this.perHourAmount = perHourAmount;
    }
}

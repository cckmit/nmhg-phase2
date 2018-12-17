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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String customerType;

    private String name;

    private String dealerNumber;

    private String currency;

    private String customerId;

    private String companyName;

    private String corporateName;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String zipcode;

    private String email;

    private String secondaryEmail;

    private String phone;

    private String secondaryPhone;

    private String additionalAddressLine1;

    private String additionalAddressLine2;

    private String additionalCity;

    private String additionalState;

    private String additionalCountry;

    private String additionalZipcode;

    private String additionalEmail;

    private String additionalSecondaryEmail;

    private String additionalPhone;

    private String additionalSecondaryPhone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDealerNumber() {
        return dealerNumber;
    }

    public void setDealerNumber(String dealerNumber) {
        this.dealerNumber = dealerNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getAdditionalAddressLine1() {
        return additionalAddressLine1;
    }

    public void setAdditionalAddressLine1(String additionalAddressLine1) {
        this.additionalAddressLine1 = additionalAddressLine1;
    }

    public String getAdditionalAddressLine2() {
        return additionalAddressLine2;
    }

    public void setAdditionalAddressLine2(String additionalAddressLine2) {
        this.additionalAddressLine2 = additionalAddressLine2;
    }

    public String getAdditionalCity() {
        return additionalCity;
    }

    public void setAdditionalCity(String additionalCity) {
        this.additionalCity = additionalCity;
    }

    public String getAdditionalState() {
        return additionalState;
    }

    public void setAdditionalState(String additionalState) {
        this.additionalState = additionalState;
    }

    public String getAdditionalCountry() {
        return additionalCountry;
    }

    public void setAdditionalCountry(String additionalCountry) {
        this.additionalCountry = additionalCountry;
    }

    public String getAdditionalZipcode() {
        return additionalZipcode;
    }

    public void setAdditionalZipcode(String additionalZipcode) {
        this.additionalZipcode = additionalZipcode;
    }

    public String getAdditionalEmail() {
        return additionalEmail;
    }

    public void setAdditionalEmail(String additionalEmail) {
        this.additionalEmail = additionalEmail;
    }

    public String getAdditionalSecondaryEmail() {
        return additionalSecondaryEmail;
    }

    public void setAdditionalSecondaryEmail(String additionalSecondaryEmail) {
        this.additionalSecondaryEmail = additionalSecondaryEmail;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public String getAdditionalSecondaryPhone() {
        return additionalSecondaryPhone;
    }

    public void setAdditionalSecondaryPhone(String additionalSecondaryPhone) {
        this.additionalSecondaryPhone = additionalSecondaryPhone;
    }
}

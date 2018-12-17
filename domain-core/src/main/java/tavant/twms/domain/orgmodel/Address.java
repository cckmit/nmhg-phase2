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
package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ADDRESS")
public class Address implements Serializable, AuditableColumns{
	@Id
	@GeneratedValue(generator = "Address")
	@GenericGenerator(name = "Address", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDRESS_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

    @JsonIgnore
    @Version
	private int version;
	// Adding location for deserializing historical claim audits XML properly. 
	@Transient
    @JsonIgnore
	private String location;
	
	private String addressLine1;

	private String addressLine2;

    @JsonIgnore
	private String addressLine3;

    @JsonIgnore
    private String addressLine4;

    @JsonIgnore
	private String addressIdOnRemoteSystem;

	private String city;

	private String state;

	private String country;

	private String zipCode;

	@Column(nullable = true)
	private String email;

    @JsonIgnore
	@Column(nullable = true)
	private String secondaryEmail;

	private String phone;

	@Column(nullable = true)
	private String phoneExt;

    @JsonIgnore
    @Column(nullable = true)
	private String secondaryPhone;

    @JsonIgnore
    @Column(nullable = true)
	private String secondaryPhoneExt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "belongs_to")
	private Party belongsTo;

	private String contactPersonName;
	
	private String customerContactTitle;

	private String status;

	@Column(nullable = true)
	private String fax;
    
	private String deliveryPointCode;

	private String zipcodeExtension;
	
	@JsonIgnore
    private String customerCompanyWebsite;
	
	private String county;

    @JsonIgnore
    private String sicCode;

    @JsonIgnore
    private String emailForSapNotifications;
	
	@Column(name="SUB_COUNTY")
	private String subCounty;
	
	@Column(name="COUNTY_CODE_NAME")
	private String countyCodeWithName;
	

	public String getSicCode() {
		return sicCode;
	}

	public void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public String getSubCounty() {
		return subCounty;
	}

	public void setSubCounty(String subCounty) {
		this.subCounty = subCounty;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

    @NotEmpty
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

    @NotEmpty
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

    @NotEmpty
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

    @NotEmpty
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    @NotEmpty
    public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getSecondaryPhoneExt() {
		return secondaryPhoneExt;
	}

	public void setSecondaryPhoneExt(String secondaryPhoneExt) {
		this.secondaryPhoneExt = secondaryPhoneExt;
	}

	public String getPhoneExt() {
		return phoneExt;
	}

	public void setPhoneExt(String phoneExt) {
		this.phoneExt = phoneExt;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressIdOnRemoteSystem() {
		return addressIdOnRemoteSystem;
	}

	public void setAddressIdOnRemoteSystem(String addressIdOnRemoteSystem) {
		this.addressIdOnRemoteSystem = addressIdOnRemoteSystem;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append(
				"address line1", addressLine1).append("address line2",
				addressLine2).append("city", city).append("state", state)
			    .append("country", country).append("zip code", zipCode)
				.toString();
	}

	public Party getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(Party belongsTo) {
		this.belongsTo = belongsTo;
	}

	@JsonIgnore
    public String getLocationForGoogleMap() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(this.addressLine1)) {
            sb.append(this.addressLine1.trim());
        }
        if (StringUtils.hasText(this.addressLine2)) {
            sb.append(this.addressLine2.trim());
        }
        if (StringUtils.hasText(this.city)) {
            sb.append(",").append(this.city.trim());
        }
        if (StringUtils.hasText(this.state)) {
            sb.append(",").append(this.state.trim());
        }
        if (StringUtils.hasText(this.zipCode)) {
            sb.append(",").append(this.zipCode.trim());
        }
        if (StringUtils.hasText(this.country)) {
            sb.append(",").append(this.country.trim());
        }
        return sb.toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getDeliveryPointCode() {
		return deliveryPointCode;
	}

	public void setDeliveryPointCode(String deliveryPointCode) {
		this.deliveryPointCode = deliveryPointCode;
	}

	public String getZipcodeExtension() {
		return zipcodeExtension;
	}

	public void setZipcodeExtension(String zipcodeExtension) {
		this.zipcodeExtension = zipcodeExtension;
	}

	public String getCustomerCompanyWebsite() {
		return customerCompanyWebsite;
	}

	public void setCustomerCompanyWebsite(String customerCompanyWebsite) {
		this.customerCompanyWebsite = customerCompanyWebsite;
	}
	

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmailForSapNotifications() {
		return emailForSapNotifications;
	}

	public void setEmailForSapNotifications(String emailForSapNotifications) {
		if(emailForSapNotifications != null && !emailForSapNotifications.equals("") && !"null".equals(emailForSapNotifications)){
			this.emailForSapNotifications = emailForSapNotifications;
		}
	}

	public String getCustomerContactTitle() {
		return customerContactTitle;
	}

	public void setCustomerContactTitle(String customerContactTitle) {
		this.customerContactTitle = customerContactTitle;
	}

	@JsonIgnore
	public String getCountyCodeWithName() {
		return countyCodeWithName;
	}
     
	@JsonIgnore
	public void setCountyCodeWithName(String countyCodeWithName) {
		if (StringUtils.hasText(countyCodeWithName)) {
			String countyWithName[]=StringUtils.delimitedListToStringArray(countyCodeWithName, "-");	
			setCounty(countyWithName[0]);
			this.countyCodeWithName = countyCodeWithName;
		}
	}

  	
}


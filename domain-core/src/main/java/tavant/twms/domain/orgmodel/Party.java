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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import tavant.twms.common.Views;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
@FilterDef(name = "party_bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "party_bu_name", condition = "id in (select bom.org from bu_org_mapping bom where bom.bu in (:name))")
@JsonAutoDetect
public class Party implements Serializable, AuditableColumns {
	@Id
	@GeneratedValue(generator = "Party")
	@GenericGenerator(name = "Party", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PARTY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
    @JsonIgnore
    private int version;

	private String name;

	@JsonView(value=Views.Public.class)
	@OneToOne(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@Valid
	private Address address;
	
	@OneToOne(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Address shipmentAddress;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private Organization isPartOfOrganization;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@Transient
    @JsonIgnore
    private String nameWithBrand;
	
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}	

	public Address getShipmentAddress() {
		return shipmentAddress;
	}

	public void setShipmentAddress(Address shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("name", name)
				.toString();
	}

	public void setIsPartOfOrganization(Organization isPartOfOrganization) {
		this.isPartOfOrganization = isPartOfOrganization;
	}

	public Organization getIsPartOfOrganization() {
		return isPartOfOrganization;
	}

    @JsonIgnore
    public boolean isDealer() {
		return (InstanceOfUtil.isInstanceOfClass(Dealership.class, this));
	}

    @JsonIgnore
	public boolean isCustomer() {
		return (InstanceOfUtil.isInstanceOfClass(Customer.class, this));
	}

    @JsonIgnore
	public boolean isThirdParty() {
		return (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, this));
	}

    @JsonIgnore
    public boolean isOriginalEquipManufacturer() {
		return (InstanceOfUtil.isInstanceOfClass(
				OriginalEquipManufacturer.class, this));
	}

    @JsonIgnore
	public boolean isDirectCustomer() {
		return (InstanceOfUtil.isInstanceOfClass(DirectCustomer.class, this));
	}

    @JsonIgnore
	public boolean isInterCompany() {
		return (InstanceOfUtil.isInstanceOfClass(InterCompany.class, this));
	}

    @JsonIgnore
	public boolean isNationalAccount() {
		return (InstanceOfUtil.isInstanceOfClass(NationalAccount.class, this));
	}

    @JsonIgnore
    private String customerClassification;

	public String getCustomerClassification() {
		return customerClassification;
	}

	public void setCustomerClassification(String customerClassification) {
		this.customerClassification = customerClassification;
	}

    @JsonIgnore
    public String getCompanyName() {
		try {
			return ((Customer) this).getCompanyName();
		} catch (ClassCastException e) {
			// to do
		}
		return null;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    @JsonIgnore
    public String getType() {
		if ((InstanceOfUtil.isInstanceOfClass(Dealership.class, this)))
			return "DEALERSHIP";
		else if ((InstanceOfUtil.isInstanceOfClass(Customer.class, this)))
			return "CUSTOMER";
		else if ((InstanceOfUtil.isInstanceOfClass(ThirdParty.class, this)))
			return "THIRDPARTY";
		else if ((InstanceOfUtil.isInstanceOfClass(
				OriginalEquipManufacturer.class, this)))
			return "OEM";
		else if ((InstanceOfUtil.isInstanceOfClass(DirectCustomer.class, this)))
			return "DIRECTCUSTOMER";
		else if ((InstanceOfUtil.isInstanceOfClass(InterCompany.class, this)))
			return "INTERCOMPANY";
		else if ((InstanceOfUtil.isInstanceOfClass(NationalAccount.class, this)))
			return "NATIONALACCOUNT";
		return null;

	}

	@Transient
	public String getAddressBookType() {
		return "";
	}

    @JsonIgnore
    public String getDisplayName() {
		if (StringUtils.hasText(this.name)) {
			if (this.name.equalsIgnoreCase("OEM")) {
				return "OEM";
			}
		}
		return this.name;
	}
	
	public void setNameWithBrand(String nameWithBrand) {
		this.nameWithBrand = nameWithBrand;
	}

	public String getNameWithBrand() {
		return nameWithBrand;
	}
	
	// condition if 'this is instance of Customer' -- added based on review comments  
    @JsonIgnore
    public String getSiCode() {
		String siCode = "";
		if ((InstanceOfUtil.isInstanceOfClass(Customer.class, this))) {
			Customer operator = new HibernateCast<Customer>().cast(this);
			siCode = operator.getSiCode();
		}
		return siCode;
	}
}

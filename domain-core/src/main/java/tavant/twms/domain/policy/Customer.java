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
package tavant.twms.domain.policy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Role;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Customer extends Party implements Serializable{
	private boolean individual = true;

	private String customerId;
	private Locale locale;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Role> roles = new HashSet<Role>();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private List<Address> addresses;

	private String companyName;

	private String corporateName;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "customer_associated_orgs")
	private Set<Organization> associatedOrganizations = new HashSet<Organization>();
	
	private String siCode;

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

	public boolean isIndividual() {
		return individual;
	}

	public void setIndividual(boolean individual) {
		this.individual = individual;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Set<Organization> getAssociatedOrganizations() {
		return associatedOrganizations;
	}

	public void setAssociatedOrganizations(
			Set<Organization> associatedOrganizations) {
		this.associatedOrganizations = associatedOrganizations;
	}

	/**
	 * This method returns the primary address, of the customer.
	 * 
	 * @return
	 */
	@Transient
	public Address getPrimaryAddress() {
		return getAddress();
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	

	public String getSiCode() {
		return siCode;
	}

	public void setSiCode(String siCode) {
		this.siCode = siCode;
	}

}
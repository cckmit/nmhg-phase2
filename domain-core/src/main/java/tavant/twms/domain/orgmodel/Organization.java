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
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitComparator;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
@JsonAutoDetect
public class Organization extends Party implements Serializable{

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "org_user_belongs_to_orgs", joinColumns = { @JoinColumn(name = "belongs_to_organizations") }, inverseJoinColumns = { @JoinColumn(name = "org_user") })	
	@Sort(type = SortType.NATURAL)
	private SortedSet<User> users = new TreeSet<User>();

	@OneToMany(mappedBy = "belongsTo")
	private Set<AddressBook> addressBooks;
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@Filter(name = "excludeInactiveAddress")
	private List<OrganizationAddress> orgAddresses;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "bu_org_mapping", joinColumns = { @JoinColumn(name = "org") }, inverseJoinColumns = { @JoinColumn(name = "bu") })
	@Sort(type = SortType.COMPARATOR, comparator = BusinessUnitComparator.class)
	private SortedSet<BusinessUnit> businessUnits;
	
	private Currency preferredCurrency;

	public SortedSet<BusinessUnit> getBusinessUnits() {
		return businessUnits;
	}

	public void setBusinessUnits(TreeSet<BusinessUnit> businessUnits) {
		this.businessUnits = businessUnits;
	}

	public SortedSet<User> getUsers() {
		return users;
	}

	public void setUsers(SortedSet<User> users) {
		this.users = users;
	}

	public Set<AddressBook> getAddressBooks() {
		return addressBooks;
	}

	public void setAddressBooks(Set<AddressBook> addressBooks) {
		this.addressBooks = addressBooks;
	}

	public List<OrganizationAddress> getOrgAddresses() {
		return orgAddresses;
	}

	public void setOrgAddresses(List<OrganizationAddress> orgAddresses) {
		this.orgAddresses = orgAddresses;
	}
	

	public Currency getPreferredCurrency() {
		return preferredCurrency;
	}

	public void setPreferredCurrency(Currency preferredCurrency) {
		this.preferredCurrency = preferredCurrency;
	}
	
	@Override
	public boolean equals(Object organization) {
        if (organization!=null&& ((Organization) organization).getId()!=null&&((Organization) organization).getId().equals(this.getId())) {
			return true;
		} else {
			return false;
		}
	}

    @OneToMany(cascade = {javax.persistence.CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(name = "ORG_OWNER_ASSOCIATION", joinColumns = { @JoinColumn(name = "PARENT_ORG") }, inverseJoinColumns = { @JoinColumn(name = "CHILD_ORG") })
    private Set<Organization> childOrgs = new HashSet<Organization>();

    public Set<Organization> getChildOrgs() {
        return childOrgs;
    }

    public void setChildOrgs(Set<Organization> childOrgs) {
        this.childOrgs = childOrgs;
    }

    @ManyToMany(mappedBy="childOrgs", fetch = FetchType.LAZY)
    private Set<Organization> parentOrgs = new HashSet<Organization>();

    public Set<Organization> getParentOrgs() {
        return parentOrgs;
    }

    public void setParentOrgs(Set<Organization> parentOrgs) {
        this.parentOrgs = parentOrgs;
    }
}

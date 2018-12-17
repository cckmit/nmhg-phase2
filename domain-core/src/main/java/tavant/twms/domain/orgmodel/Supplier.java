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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.partreturn.Location;
/**
 * @author kannan.ekanath
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@SuppressWarnings("serial")
public class Supplier extends Organization implements Serializable{

	@OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
	@Cascade( {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	private Set<SupplierLocation> locations = new HashSet<SupplierLocation>();

	private String preferredLocationType;

    private String supplierNumber;
    
    private String status;
    
    private String firstName;

    private String lastName;

    @ManyToMany
    private Set<Label> labels = new HashSet<Label>();

    public List<Location> getLocations() {
    	List<Location> suppLocations = new ArrayList<Location>();
    	for (Iterator iter = locations.iterator(); iter.hasNext();) {
			SupplierLocation supplierLocation = (SupplierLocation) iter.next();
			if (supplierLocation!=null)
				suppLocations.add(supplierLocation.getLocations());
		}

        Collections.sort(suppLocations,
                new Comparator<Location>() {
                    public int compare(Location obj1,
                                       Location obj2) {
                        return obj1.getCode().compareToIgnoreCase(obj2.getCode());
                    }
                });

        return suppLocations;

    }

    public void setLocations(Set<SupplierLocation> locations) {
        this.locations = locations;
    }

    public void addSupplierLocation(String type, Location location) {
    	SupplierLocation supplierLoc = new SupplierLocation();
    	supplierLoc.setLocations(location);
    	supplierLoc.setSupplier(this);
    	supplierLoc.setLocationsMapkey(type);
        locations.add(supplierLoc);
    }
    
    public Set<SupplierLocation> getSupplierLocations(){
		return locations;  	
    }
    

    public String getPreferredLocationType() {
        return preferredLocationType;
    }

    public void setPreferredLocationType(String preferredLocationType) {
        this.preferredLocationType = preferredLocationType;
    }

    public Location getPreferredLocation() {
    	Location suppPreferredLocation = null;        
        for (Iterator iter = locations.iterator(); iter.hasNext();) {
			SupplierLocation suppLoc = (SupplierLocation) iter.next();
			if (preferredLocationType.equalsIgnoreCase(suppLoc.getLocationsMapkey()))
				suppPreferredLocation = suppLoc.getLocations();
		}
        return suppPreferredLocation;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayAddress() {
        if (this.getAddress() == null) {
            return "";
        }
        StringBuffer sbAddress = new StringBuffer();
        String addressLine1 = this.getAddress().getAddressLine1();
        sbAddress.append(StringUtils.hasText(addressLine1) ? addressLine1 + ", " : "");
        String addressLine2 = this.getAddress().getAddressLine2();
        sbAddress.append(StringUtils.hasText(addressLine2) ? addressLine2 + ", " : "");
        String city = this.getAddress().getCity();
        sbAddress.append(StringUtils.hasText(city) ? city + ", " : "");
        String state = this.getAddress().getState();
        sbAddress.append(StringUtils.hasText(state) ? state + ", " : "");
        String zipCode = this.getAddress().getZipCode();
        sbAddress.append(StringUtils.hasText(zipCode) ? zipCode : "");
        return sbAddress.toString();
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
    
}
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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.partreturn.Location;

/**
 * @author jhulfikar.ali
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "supplier_locations")
public class SupplierLocation implements Serializable {
	
	@Id
	@GeneratedValue(generator = "SupplierLocation")
	@GenericGenerator(name = "SupplierLocation", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "Supplier_Locations_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Supplier supplier;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE })
	private Location locations;
    
	@Column
	private String locationsMapkey;
	
	public Location getLocations() {
		return locations;
	}

	public void setLocations(Location locations) {
		this.locations = locations;
	}

	public String getLocationsMapkey() {
		return locationsMapkey;
	}

	public void setLocationsMapkey(String locationsMapkey) {
		this.locationsMapkey = locationsMapkey;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Long getId() {
		return id;
	}
	
}
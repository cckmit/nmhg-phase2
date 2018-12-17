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

package tavant.twms.domain.inventory;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemGroup;

/**
 * Represents a class to which an inventory belongs. A dealer code can be configured/enabled to file 
 * 30 Day NCR claims for one or more of these classes.
 * 
 * This class has been implemented as part of SLMSPROD-970 (CR). Also this class is required as
 * inventory class is captured currently as part of product family for an {@link InventoryItem} in {@link ItemGroup#getGroupCode()}.
 * As classification for the purpose of 30 Day NCR can be different from product family classes (e.g. BT - Big Truck), this
 * entity has been implemented which will be mapped with Service Providers. Further classes in product family hierarchy
 * do not have an identity of their own to enable one-to-many association with a service provider
 * 
 * @author ravi.sinha
 */
@Entity
@FilterDef(name = "bu_name", parameters = {@ParamDef(name = "name", type = "string")})
@Filters({
    @Filter(name = "bu_name", condition = "business_unit_info in (:name)")
})
public class InventoryClass implements BusinessUnitAware, 
	Comparator<InventoryClass>, Comparable<InventoryClass> {
	
	@Id
	@GeneratedValue(generator = "InventoryClass")
	@GenericGenerator(name = "InventoryClass", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INV_CLASS_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20"),
			@Parameter(name = "optimizer", value = "pooled") })
	private Long id;
	
	@Column(unique = true)
	private String name;
	
	private String description;	
	
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public InventoryClass() {
		// No-Arg Constructor
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append(
				"id", id).append(
				"name", name).append(
				"description", description).toString();
	}
	
	@Override
	public boolean equals(Object inventoryClass) {
		
		if (inventoryClass == null || false == (inventoryClass instanceof InventoryClass)) {
			return false;
		}
		
		if (this.getId().equals(((InventoryClass)inventoryClass).getId())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(InventoryClass other) {
		return this.name.compareTo(other.getName());
		
	}

	@Override
	public int compare(InventoryClass first, InventoryClass second) {
		return first.compareTo(second);
	}
	
	
}

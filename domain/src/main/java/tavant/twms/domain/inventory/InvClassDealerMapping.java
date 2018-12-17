package tavant.twms.domain.inventory;
/*
 *   Copyright (c) 2014 Tavant Technologies
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
import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.security.AuditableColumns;

/**
 * This class captures the association between an {@link InventoryClass} and a dealer {@link ServiceProvider}. If a dealer
 * has been configured to file 30 Day NCR for an inventory class, then this mapping would exist.
 * 
 * This class has been implemented as part of SLMSPROD-970 (CR)
 * @author ravi.sinha
 */
@Entity
@Filters({ @Filter(name="excludeInactive") })
public class InvClassDealerMapping implements Serializable, AuditableColumns {
	
	private static final long serialVersionUID = -3911880143172105884L;

	@Id
	@GeneratedValue(generator = "InvClassDealerMapping")
	@GenericGenerator(name = "InvClassDealerMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INV_CLASS_DEALER_MAPPING_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20"),
			@Parameter(name = "optimizer", value = "pooled") })
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_class_id")
	private InventoryClass inventoryClass;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_provider_id")
	private ServiceProvider serviceProvider;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@SuppressWarnings("deprecation")
	private AuditableColEntity d = new AuditableColEntity();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public InventoryClass getInventoryClass() {
		return inventoryClass;
	}

	public void setInventoryClass(InventoryClass inventoryClass) {
		this.inventoryClass = inventoryClass;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append(
				"id", id).append(
				"inventoryClass", inventoryClass != null ? inventoryClass.getId() + "-" + inventoryClass.getName() : "null").append(
				"serviceProvider", serviceProvider != null ? serviceProvider.getId() + "-" + serviceProvider.getServiceProviderNumber() : "null").toString();
	}
	
	@Override
	public boolean equals(Object icdMapping) {
		
		if (icdMapping == null || false == (icdMapping instanceof InvClassDealerMapping)) {
			return false;
		}
		
		InvClassDealerMapping icdm2 = (InvClassDealerMapping) icdMapping;
		
		if (this.getId().equals(icdm2.getId())) {
			return true;
		} else {
			return false;
		}
	}

}

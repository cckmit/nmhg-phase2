/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  partOf such  license and with the
 *   inclusion partOf the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership partOf  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.inventory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.ItemComposition;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({ @Filter(name = "excludeInactive") })
@Table(name = "inventory_additional_comp")
public class InventoryItemAdditionalComponents implements AuditableColumns {

	@Id
	@GeneratedValue(generator = "InventoryItemComposition")
	@GenericGenerator(name = "InventoryItemComposition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_InventoryItemComposition"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item" , referencedColumnName="ID")
	private InventoryItem item;

	private String partNumber;

	private String partDescription;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	private String serialNumber;
	private String type;
	private String subType;

	private String manufacturer;

	private String model;

	private String dateCode;

	public InventoryItemAdditionalComponents() {
		super();
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public InventoryItem getItem() {
		return this.item;
	}

	public void setItem(InventoryItem of) {
		this.item = of;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getPartDescription() {
		return partDescription;
	}

	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDateCode() {
		return dateCode;
	}

	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}

}

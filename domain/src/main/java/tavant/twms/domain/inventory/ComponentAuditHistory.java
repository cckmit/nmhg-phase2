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

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

/**
 * @author kalyani
 * 
 */
@Entity
public class ComponentAuditHistory implements AuditableColumns{
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item", insertable = false, updatable = false)
	private InventoryItem inventoryItem;
	private String sequenceNumber;
	private String componentSerialType;
	private String manufacturer;
	private String componentPartSerialNumber;
	private String serialTypeDescription;
	private String componentPartNumber;
	private String transactionType;
	
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getComponentSerialType() {
		return componentSerialType;
	}

	public void setComponentSerialType(String componentSerialType) {
		this.componentSerialType = componentSerialType;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getComponentPartSerialNumber() {
		return componentPartSerialNumber;
	}

	public void setComponentPartSerialNumber(String componentPartSerialNumber) {
		this.componentPartSerialNumber = componentPartSerialNumber;
	}

	public String getSerialTypeDescription() {
		return serialTypeDescription;
	}

	public void setSerialTypeDescription(String serialTypeDescription) {
		this.serialTypeDescription = serialTypeDescription;
	}

	public String getComponentPartNumber() {
		return componentPartNumber;
	}

	public void setComponentPartNumber(String componentPartNumber) {
		this.componentPartNumber = componentPartNumber;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	public ComponentAuditHistory() {
		super();
	}
	public ComponentAuditHistory(InventoryItem inventoryItem) {
		super();
		this.inventoryItem = inventoryItem;
	}
	
}

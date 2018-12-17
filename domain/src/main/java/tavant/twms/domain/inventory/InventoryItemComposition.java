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
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.catalog.ItemComposition;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters( { @Filter(name = "excludeInactive") })
public class InventoryItemComposition implements AuditableColumns {
	@Id
	@GeneratedValue(generator = "InventoryItemComposition")
	@GenericGenerator(name = "InventoryItemComposition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_InventoryItemComposition"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private InventoryItem partOf;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private InventoryItem part;

	@OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_composition", nullable = false, updatable = false, insertable = true)
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @IndexColumn(name = "in_order")
    private List<SerializedItemReplacement> replacementHistory = new ArrayList<SerializedItemReplacement>();
		
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	private String sourceOfPart;
	
	private String sequenceNumber;
	private String componentSerialType;
	private String manufacturer;
	
	private String status;
	private String serialTypeDescription;

	public String getSourceOfPart() {
		return sourceOfPart;
	}

	public void setSourceOfPart(String sourceOfPart) {
		this.sourceOfPart = sourceOfPart;
	}

	public InventoryItemComposition(InventoryItem part) {
		super();
		this.part = part;
	}

	public InventoryItemComposition() {
		super();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the partOf
	 */
	public InventoryItem getPartOf() {
		return this.partOf;
	}

	/**
	 * @param partOf
	 *            the partOf to set
	 */
	public void setPartOf(InventoryItem of) {
		this.partOf = of;
	}

	/**
	 * @return the part
	 */
	public InventoryItem getPart() {
		return this.part;
	}

	/**
	 * @param part
	 *            the part to set
	 */
	public void setPart(InventoryItem part) {
		this.part = part;
	}

	
	public SerializedItemReplacement replacePart(InventoryItem newPart, ItemReplacementReason dueTo) {
        SerializedItemReplacement serializedItemReplacement = new SerializedItemReplacement(
                getPart(), newPart, dueTo);
        serializedItemReplacement.setForComposition(this);
        getReplacementHistory().add(serializedItemReplacement);
        setPart(newPart);
        return serializedItemReplacement;
    }

	@Override
	public String toString() {
		return MessageFormat.format("(part={0}, partOf={1})", this.part,
				this.partOf);
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<SerializedItemReplacement> getReplacementHistory() {
		return replacementHistory;
	}

	public void setReplacementHistory(
			List<SerializedItemReplacement> replacementHistory) {
		this.replacementHistory = replacementHistory;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSerialTypeDescription() {
		return serialTypeDescription;
	}

	public void setSerialTypeDescription(String serialTypeDescription) {
		this.serialTypeDescription = serialTypeDescription;
	}
	

}

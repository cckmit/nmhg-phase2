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
package tavant.twms.worklist;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;

/**
 * @author kannan.ekanath
 * 
 */
public class InboxItem {

	private Claim claim;

	private RecoveryClaim recoveryClaim;

	private Shipment shipment;

	private Grouping grouping;

	private Supplier supplier;

	private Location location;

	public enum Grouping {
		CLAIM_BASED, PART_BASED, SHIPMENT_BASED, SUPPLIER_BASED, LOCATION_BASED
	}

	public InboxItem(Claim claim) {
		this.claim = claim;
		this.grouping = Grouping.CLAIM_BASED;
	}

	public InboxItem(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
		this.grouping = Grouping.CLAIM_BASED;
	}

	public InboxItem(Shipment shipment) {
		this.shipment = shipment;
		this.grouping = Grouping.SHIPMENT_BASED;
	}

	public InboxItem(Supplier supplier) {
		this.supplier = supplier;
		this.grouping = Grouping.SUPPLIER_BASED;
	}

	public InboxItem(Claim claim, Shipment shipment) {
		this.shipment = shipment;
		this.claim = claim;
		this.grouping = Grouping.SHIPMENT_BASED;
	}

	public InboxItem(Supplier supplier, Location location) {
		this.supplier = supplier;
		this.location = location;
		this.grouping = Grouping.SUPPLIER_BASED;
	}

	public InboxItem(Location location) {
		this.location = location;
		this.grouping = Grouping.LOCATION_BASED;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public Grouping getGrouping() {
		return grouping;
	}

	public void setGrouping(Grouping grouping) {
		this.grouping = grouping;
	}

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getClaimShipmentConcatenatedId() {
		return claim.getId() + ":" + shipment.getTransientId();
	}

	public String getSupplierLocationConcatenatedId() {
		return supplier.getId() + ":" + location.getId();
	}

	public Location getLocation() {
		return location;
	}

	public RecoveryClaim getRecoveryClaim() {
		return recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}
}
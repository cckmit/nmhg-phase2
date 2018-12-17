/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.Location;

@Entity
@Table(name = "oem_part_to_replace")
public class OEMPartToReplace extends PartsToReplace {

	@ManyToOne(fetch = FetchType.LAZY)
	private Item item;

	private boolean shippedByOem;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Location returnLocation;

	private String paymentCondition;

	@Column(nullable = true)
	private Integer dueDays;
	
	
	/**
	 *TODO Not being used anymore need to be removed
	 * 
	 */
	@Transient
	private Boolean isHussPartInstalled;
	
	
	public OEMPartReplaced fetchRemovedPart(CampaignClaim claim) {
		OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
		oemPartReplaced.setNumberOfUnits(this.getNoOfUnits());
		oemPartReplaced.setItemReference(new ItemReference(this
				.getItem()));
		oemPartReplaced.setBrandItem(this.getItem().getBrandItem(claim.getBrand()));
		
		return oemPartReplaced;
	}
	
	public InstalledParts fetchHussmannInstalledParts(CampaignClaim claim) {
		InstalledParts oemPartReplaced = new InstalledParts();
		oemPartReplaced.setNumberOfUnits(this.getNoOfUnits());
		oemPartReplaced.setBrandItem(this.getItem().getBrandItem(claim.getBrand()));
		oemPartReplaced.setShippedByOem(this.isShippedByOem());
		return oemPartReplaced;
	}
	
	
	

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		if (item == null) {
			return null;
		}
		return new ToStringCreator(this).append("partNumber", item.getNumber())
				.append("description", item.getDescription()).toString();
	}

	public boolean isShippedByOem() {
		return shippedByOem;
	}

	public void setShippedByOem(boolean freeShipment) {
		shippedByOem = freeShipment;
	}

	public Integer getDueDays() {
		return dueDays;
	}

	public void setDueDays(Integer dueDays) {
		this.dueDays = dueDays;
	}

	public String getPaymentCondition() {
		return paymentCondition;
	}

	public void setPaymentCondition(String paymentCondition) {
		this.paymentCondition = paymentCondition;
	}

	public Location getReturnLocation() {
		return returnLocation;
	}

	public void setReturnLocation(Location newReturnLocation) {
		if (newReturnLocation.getId() == null) {
			returnLocation = null;
		} else {
			returnLocation = newReturnLocation;
		}
	}

	public Boolean getIsHussPartInstalled() {
		return isHussPartInstalled;
	}

	public void setIsHussPartInstalled(Boolean isHussPartInstalled) {
		this.isHussPartInstalled = isHussPartInstalled;
	}

}
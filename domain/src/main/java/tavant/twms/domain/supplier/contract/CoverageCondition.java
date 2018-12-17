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
package tavant.twms.domain.supplier.contract;

import java.sql.Types;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.security.AuditableColumns;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.HibernateCast;

import com.domainlanguage.time.CalendarDate;

/**
 *
 * @author kannan.ekanath
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@AccessType("field")
public class CoverageCondition implements AuditableColumns{

    private static final Logger logger = Logger.getLogger(CoverageCondition.class);

    public static enum ComparisonWith {
        DATE_OF_MANUFACTURE, DATE_OF_DELIVERY, DATE_OF_PURCHASE, ENERGY_UNITS
    }

    @Id
    @GeneratedValue(generator = "CoverageCondition")
	@GenericGenerator(name = "CoverageCondition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "COVERAGE_CONDITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private Integer units;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.supplier.contract.CoverageCondition$ComparisonWith"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private ComparisonWith comparedWith;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();  

    public CoverageCondition() {
        // for hibernate
    }

    public CoverageCondition(Integer units, ComparisonWith comparedWith) {
        this.units = units;
        this.comparedWith = comparedWith;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ComparisonWith getComparedWith() {
        return this.comparedWith;
    }

    public void setComparedWith(ComparisonWith comparedWith) {
        this.comparedWith = comparedWith;
    }

    public Integer getUnits() {
        return this.units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return "[" + this.units + "] months from [" + this.comparedWith + "]";
    }

    public boolean isApplicable(ClaimedItem claimedItem,boolean doesSerializedPartsHaveShipmentDate) {
        if(logger.isDebugEnabled()){
            logger.debug("Checking for applicability");
        }
        CalendarDate domOrDodOrDop = getStartingDate(claimedItem,doesSerializedPartsHaveShipmentDate);
        if(logger.isDebugEnabled()){
            logger.debug("Starting date is [" + domOrDodOrDop + "]");
        }
        if (domOrDodOrDop != null) {
            CalendarDate contractApplicableTill = domOrDodOrDop.plusMonths(this.units);
            CalendarDuration calendarDuration = new CalendarDuration(domOrDodOrDop,
                    contractApplicableTill);
            if(logger.isDebugEnabled()){
                logger.debug("Duration of checking is [" + calendarDuration + "]");
            }
            if(claimedItem.getClaim().getType().equals(ClaimType.CAMPAIGN) && 
            		claimedItem.getClaim().getRepairDate()!=null){
            	return calendarDuration.includes(claimedItem.getClaim().getRepairDate());
            }
            return calendarDuration.includes(claimedItem.getClaim().getFailureDate());
        } else {
            return false;
        }
    }

    public boolean isEnergyUnitsOrHoursApplicable(Claim claim) {
        if(logger.isDebugEnabled()){
            logger.debug("Checking for applicability");
        }
        boolean isCovered =true;
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            if (! isClaimedItemCovered(claimedItem)) {
            	isCovered=false;
            }
        }
        return isCovered;
    }

    public boolean isClaimedItemCovered(ClaimedItem claimedItem) {
        if (this.units == null) {
            return true;
        }
        return claimedItem.getHoursInService().intValue() <= this.units;
    }

    public boolean isCovered(ClaimedItem claimedItem){
    	if (this.units == null) {
            return true;
        }
        return claimedItem.getClaim().getHoursOnPart().intValue() <= this.units;
    }
    
    protected CalendarDate getStartingDate(ClaimedItem claimedItem,boolean doesSerializedPartsHaveShipmentDate) {
		if (this.comparedWith == ComparisonWith.DATE_OF_MANUFACTURE) {
			if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claimedItem.getClaim())) {
				return claimedItem.getClaim().getInstallationDate();
			} else if (claimedItem.getItemReference().isSerialized()
					&& claimedItem.getItemReference().getReferredInventoryItem() != null) {
				InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
				return inventoryItem.getBuiltOn();
			} else {
				return null;
			}
		}else{
			 if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claimedItem.getClaim())) {
		            PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claimedItem.getClaim());
		            // Changes made based on CR QC-167 wrt Parts Claim
					if (partsClaim.getPartItemReference().isSerialized()) {
						return partsClaim.getInstallationDate();
					} else {
						if (partsClaim.getPartInstalled()) {
							return partsClaim.getInstallationDate(); // Non Serialized Parts Claim Installed on anything will always return the Installation Date
						} else {
							return partsClaim.getPurchaseDate();  // Non Serialized Parts Claim Not -Installed  will always return the Purchase Date
						}
					}
		            } else{
                if(!claimedItem.getItemReference().isSerialized()){
                    return claimedItem.getClaim().getPurchaseDate();
                }else{
                    InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
                    if(InventoryType.RETAIL.getType().equalsIgnoreCase(inventoryItem.getType().getType())
                    		&& !stockClaim(claimedItem)){
                        return inventoryItem.getDeliveryDate();
                    }else{
                        if (inventoryItem.getBuiltOn() != null) {
                            return inventoryItem.getBuiltOn();
                        } else {
                            return inventoryItem.getShipmentDate();
                        }
                    }
                }
            }
        }
    }

    // returns true if the inventory item was in stock when the claim was filed
    // for retail inventory, there is a validation in claim page 1 which stops dealers from entering failure date before delivery date
	private boolean stockClaim(ClaimedItem claimedItem) {
		return claimedItem.getClaim().getFailureDate()
				.isBefore(claimedItem.getItemReference().getReferredInventoryItem().getDeliveryDate());
	}
    
	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}	
}

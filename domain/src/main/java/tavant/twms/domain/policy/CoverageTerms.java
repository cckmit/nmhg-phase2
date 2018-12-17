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
package tavant.twms.domain.policy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.inventory.InventoryItem;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
@Embeddable
public class CoverageTerms {
    private static Logger logger = LogManager.getLogger(CoverageTerms.class);

    @Column(nullable = false)
    private Integer serviceHoursCovered;

    @Column(nullable = false)
    private Integer monthsCoveredFromShipment;

    @Column(nullable = false)
    private Integer monthsCoveredFromBuildDate;
    
    @Column(nullable = false)
    private Integer monthsCoveredFromOriginalDeliveryDate;
    
	@Column(nullable = false)
    private Integer monthsCoveredFromDelivery;
	
    private Integer monthsFromShipmentForEWP;

    private Integer monthsFromDeliveryForEWP;
    
    private Integer minMonthsFromDeliveryForEWP;

    public Integer getMinMonthsFromDeliveryForEWP() {
		return minMonthsFromDeliveryForEWP;
	}

	public void setMinMonthsFromDeliveryForEWP(Integer minMonthsFromDeliveryForEWP) {
		this.minMonthsFromDeliveryForEWP = minMonthsFromDeliveryForEWP;
	}

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "assemblies_included", joinColumns = { @JoinColumn(name = "assembly_included") }, inverseJoinColumns = { @JoinColumn(name = "in_policy") })
    private final Set<Assembly> inclusions = new HashSet<Assembly>();

    public void includeAssembly(Assembly assembly) {
        this.inclusions.add(assembly);
    }

    /**
     * @param claimedItem
     * @param warrantyPeriod
     * @param serviceHoursCovered TODO
     * @return
     */
    public boolean covers(ClaimedItem claimedItem, CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
        ICoverageTerms terms = ICoverageTermsFactory.getInstance().getICoverageTerms(claimedItem.getClaim().getType());
        if(serviceHoursCovered != null)
        {
            return terms.covers(claimedItem, warrantyPeriod, serviceHoursCovered);
        }
        return terms.covers(claimedItem, warrantyPeriod, this.serviceHoursCovered);
    }

    /**
     * @param inventoryItem
     * @return
     */
    protected CalendarDate firstDayOfCoverage(InventoryItem inventoryItem) {
        return inventoryItem.getRegistrationDate();
    }

    public boolean includes(Assembly assembly) {
        return this.inclusions.contains(assembly);
    }

    public Integer getMonthsCoveredFromDelivery() {
        return this.monthsCoveredFromDelivery;
    }

    public void setMonthsCoveredFromDelivery(Integer monthsCoveredFromRegistration) {
        this.monthsCoveredFromDelivery = monthsCoveredFromRegistration;
    }

    public Integer getMonthsCoveredFromShipment() {
        return this.monthsCoveredFromShipment;
    }

    public void setMonthsCoveredFromShipment(Integer monthsCoveredFromShipment) {
        this.monthsCoveredFromShipment = monthsCoveredFromShipment;
    }

    public Integer getServiceHoursCovered() {
        return this.serviceHoursCovered;
    }

    public void setServiceHoursCovered(Integer serviceHoursCovered) {
        this.serviceHoursCovered = serviceHoursCovered;
    }

    public boolean isHoursOnMachineWithinCoverageLimit(InventoryItem inventoryItem) {
        return inventoryItem.getHoursOnMachine() <= this.serviceHoursCovered;
    }

    public Integer getMonthsFromShipmentForEWP() {
        return monthsFromShipmentForEWP;
    }

    public void setMonthsFromShipmentForEWP(Integer monthsFromShipmentForEWP) {
        this.monthsFromShipmentForEWP = monthsFromShipmentForEWP;
    }

    public Integer getMonthsFromDeliveryForEWP() {
        return monthsFromDeliveryForEWP;
    }

    public void setMonthsFromDeliveryForEWP(Integer monthsFromDeliveryForEWP) {
        this.monthsFromDeliveryForEWP = monthsFromDeliveryForEWP;
    }
    
    public Integer getMonthsCoveredFromBuildDate() {
		return monthsCoveredFromBuildDate;
	}

	public void setMonthsCoveredFromBuildDate(Integer monthsCoveredFromBuildDate) {
		this.monthsCoveredFromBuildDate = monthsCoveredFromBuildDate;
	}
	
	public Integer getMonthsCoveredFromOriginalDeliveryDate() {
		return monthsCoveredFromOriginalDeliveryDate;
	}

	public void setMonthsCoveredFromOriginalDeliveryDate(
			Integer monthsCoveredFromOriginalDeliveryDate) {
		this.monthsCoveredFromOriginalDeliveryDate = monthsCoveredFromOriginalDeliveryDate;
	}

    /**
     * The Warranty End date for Delivery Report will be the earlier of
     * (Shipment Date + Months Covered From Shipment - 1 day) or ( Delivery Date +
     * Months Covered From Delivery - 1 day).
     *
     * @param inventoryItem
     * @return
     * @throws PolicyException
     */
    public CalendarDate warrantyEndDate(InventoryItem inventoryItem) throws PolicyException {
        if (!inventoryItem.isDelivered()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing registration date.");
		}
        CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		CalendarDate warrantyEndDate;
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				warrantyEndDate = this.monthsCoveredFromShipment!=null?shipmentDate.plusMonths(this.monthsCoveredFromShipment):deliveryDate;
			}else{
				warrantyEndDate =this.monthsCoveredFromDelivery!=null? deliveryDate.plusMonths(this.monthsCoveredFromDelivery): deliveryDate;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate);;
			if(deliveryDate.isBefore(cutOffDate)) {
				warrantyEndDate = deliveryDate.plusMonths(monthsCoveredFromDelivery);
			} else {
				warrantyEndDate = cutOffDate.plusMonths(monthsCoveredFromDelivery);
			}
		}
		return warrantyEndDate;
		
		//return warrantyEndDate.isAfter(deliveryDate) ? warrantyEndDate : deliveryDate;
    }

    public boolean isAppliableForEWP(InventoryItem inventoryItem, CalendarDate purchaseDate){
    	if(purchaseDate == null)
    		purchaseDate = Clock.today();
        CalendarDate endDateBasedOnShipmentForEWP = inventoryItem.getShipmentDate().plusMonths(getMonthsFromShipmentForEWP()).previousDay();
        CalendarDate endDateBasedOnDeliveryForEWP = inventoryItem.getDeliveryDate().plusMonths(getMonthsFromDeliveryForEWP()).previousDay();
        CalendarDate startDateBasedOnDeliveryForEWP = inventoryItem.getDeliveryDate();
        if(getMinMonthsFromDeliveryForEWP() != null)
        	startDateBasedOnDeliveryForEWP = inventoryItem.getDeliveryDate().plusMonths(getMinMonthsFromDeliveryForEWP()).previousDay();
        if(!endDateBasedOnDeliveryForEWP.isBefore(purchaseDate)
                && !endDateBasedOnShipmentForEWP.isBefore(purchaseDate)
                && startDateBasedOnDeliveryForEWP.isBefore(purchaseDate)){
            return true;
        }
        return false;
    }

    public CalendarDate computeWarrantyEndDateForEWP(InventoryItem inventoryItem){
    	CalendarDate endDateBasedOnShipment = null;
    	CalendarDate endDateBasedOnDelivery = null;
    	if(inventoryItem.getShipmentDate() != null && getMonthsCoveredFromShipment() != null)
    		endDateBasedOnShipment = inventoryItem.getShipmentDate().plusMonths(getMonthsCoveredFromShipment()).previousDay();
    	if(inventoryItem.getDeliveryDate() != null && getMonthsCoveredFromDelivery() != null)
    		endDateBasedOnDelivery = inventoryItem.getDeliveryDate().plusMonths(getMonthsCoveredFromDelivery()).previousDay();
        if(endDateBasedOnShipment==null
        		|| endDateBasedOnDelivery.isBefore(endDateBasedOnShipment)){
            return endDateBasedOnDelivery;
        }else if(endDateBasedOnShipment != null){
            return endDateBasedOnShipment;
        }
        return null;
    }
    
    public CalendarDate warrantyEndDate(InventoryItem inventoryItem, CalendarDate installationDate) 
    	throws PolicyException {
        if (!inventoryItem.isDelivered()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing registration date.");
		}
        
        if (!inventoryItem.isShipped()) {
     			throw new PolicyException(
     					"Warranty end date cannot be computed without knowing shipment date.");
     		}
        CalendarDate buildDate = inventoryItem.getBuiltOn();
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate deliveryDate = null;
		if(installationDate != null){
			deliveryDate = installationDate;
		}else{
			deliveryDate = inventoryItem.getDeliveryDate();
		}
		
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			CalendarDate endDateBasedOnDelivery = deliveryDate.plusMonths(
					this.monthsCoveredFromDelivery).previousDay();
			CalendarDate endDateBasedOnShipment = null;
			if (this.monthsCoveredFromShipment != null) {
				endDateBasedOnShipment = shipmentDate.plusMonths(
						this.monthsCoveredFromShipment).previousDay();
			}
			CalendarDate endDateBasedOnBuild = null;
			if(this.monthsCoveredFromBuildDate != null && buildDate != null)
			{
				endDateBasedOnBuild = buildDate.plusMonths(this.monthsCoveredFromBuildDate).previousDay();
			}else{
				endDateBasedOnBuild = endDateBasedOnDelivery ;
			}
			// Based on NMHGSLMS-366 
		
			if(inventoryItem.getDeliveryDate().isAfter(inventoryItem.getShipmentDate().plusMonths(6))){
				return endDateBasedOnShipment;
			}
			if (endDateBasedOnShipment == null || endDateBasedOnDelivery.isBefore(endDateBasedOnShipment)) {
				return  endDateBasedOnDelivery.isBefore(endDateBasedOnBuild) ? endDateBasedOnDelivery : endDateBasedOnBuild ;
			} else {
				return  endDateBasedOnShipment.isBefore(endDateBasedOnBuild) ? endDateBasedOnShipment : endDateBasedOnBuild ;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate);;
			if(deliveryDate.isBefore(cutOffDate)) {
				return deliveryDate.plusMonths(monthsCoveredFromDelivery);
			} else {
				return cutOffDate.plusMonths(monthsCoveredFromDelivery);
			}
		}
		
    }
    
    private CalendarDate getCutOffDate(CalendarDate shipmentDate) {
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
	}

    private static interface ICoverageTerms {
        public boolean covers(ClaimedItem claimedItem, CalendarDuration warrantyPeriod, Integer serviceHoursCovered);
    }

    private static class MachineClaimCoverageTerms implements ICoverageTerms {
        public boolean covers(ClaimedItem claimedItem, CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
            CalendarDate failureDate = claimedItem.getClaim().getFailureDate();

            if (logger.isDebugEnabled()) {
                logger.debug(" coverage period [ " + warrantyPeriod + " ]");
                logger.debug(" date of failure " + failureDate);
            }

            boolean failureInWarrantyPeriod = warrantyPeriod.includes(failureDate);
            return failureInWarrantyPeriod && claimedItem.getHoursInService().intValue() <= serviceHoursCovered.intValue();
        }
    }

    private static class PartsClaimCoverageTerms implements ICoverageTerms {
        public boolean covers(ClaimedItem claimedItem, CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
            CalendarDate failureDate = claimedItem.getClaim().getFailureDate();

            if (logger.isDebugEnabled()) {
                logger.debug(" coverage period [ " + warrantyPeriod + " ]");
                logger.debug(" date of failure " + failureDate);
            }
			if (claimedItem.getHoursInService() == null)
				claimedItem.setHoursInService(BigDecimal.ZERO);
			
			if (claimedItem.getClaim().getHoursOnPart() == null)
				claimedItem.getClaim().setHoursOnPart((BigDecimal.ZERO));
			
            boolean failureInWarrantyPeriod = warrantyPeriod.includes(failureDate);
            return failureInWarrantyPeriod && claimedItem.getClaim().getHoursOnPart().intValue() <= serviceHoursCovered.intValue();
        }
    }

    private static class ICoverageTermsFactory {
        private static ICoverageTermsFactory instance = new ICoverageTermsFactory();

        private final Map<ClaimType, ICoverageTerms> coverageTermsMap = new HashMap<ClaimType, ICoverageTerms>();

        public static ICoverageTermsFactory getInstance() {
            return instance;
        }

        public ICoverageTermsFactory() {
            this.coverageTermsMap.put(ClaimType.MACHINE, new MachineClaimCoverageTerms());
            this.coverageTermsMap.put(ClaimType.PARTS, new PartsClaimCoverageTerms());
            this.coverageTermsMap.put(ClaimType.ATTACHMENT, new MachineClaimCoverageTerms());
        }

        public ICoverageTerms getICoverageTerms(ClaimType claimType) {
            Assert.notNull(claimType, "cannot be null.");
            if (!this.coverageTermsMap.containsKey(claimType)) {
                throw new IllegalArgumentException("There are no coverage terms defined for claim "
                        + "type[" + claimType + "].");
            }
            return this.coverageTermsMap.get(claimType);
        }
    }

}

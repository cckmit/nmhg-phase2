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
package tavant.twms.domain.partreturn;

import java.sql.Types;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import com.domainlanguage.timeutil.Clock;

/**
 * @author vineeth.varghese
 */
@Entity
public class PartReturn extends BasePartReturn {
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private InspectionResult inspectionResult;

    // TODO : Need to revisit the next two fields. Adding it to part return
    // since we still
    // don't have the concept of a warehouse.
    private String warehouseLocation;



    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.partreturn.PartReturnStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private PartReturnStatus actionTaken;

    private static final Logger logger = Logger.getLogger(PartReturn.class);

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Shipment shipment;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Wpra wpra;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private PartReturnTaskTriggerStatus triggerStatus = PartReturnTaskTriggerStatus.TO_BE_TRIGGERED;
    
	/**
     * @return the inspectionResult
     */
    public InspectionResult getInspectionResult() {
        return this.inspectionResult;
    }

    /**
     * @param inspectionResult the inspectionResult to set
     */
    public void setInspectionResult(InspectionResult inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public void acceptPartAfterInspection(String comments, PartAcceptanceReason acceptanceReason) {
        // always create a new Inspection result object
        if (logger.isDebugEnabled()) {
            logger.debug("Accepting part [" + this + "]");
        }
        InspectionResult result = new InspectionResult();
        result.setAccepted(true);
        result.setAcceptanceReason(acceptanceReason);
        result.setComments(comments);

        this.inspectionResult = result;
        this.setStatus(PartReturnStatus.PART_ACCEPTED);
    }

    public void rejectPartAfterInspection(FailureReason failureReason, String comments) {
        // always create a new Inspection result object
        InspectionResult result = new InspectionResult();
        result.setAccepted(false);
        result.setComments(comments);
        result.setFailureReason(failureReason);
        if (logger.isDebugEnabled()) {
            logger.debug("Rejecting part [" + this + "] for reason [" + failureReason + "]");
        }
        this.inspectionResult = result;
        this.setStatus(PartReturnStatus.PART_REJECTED);
    }

    public boolean isPartReceived() {
        if(getOemPartReplaced() != null && getOemPartReplaced().isReturnDirectlyToSupplier()) {
            return (PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal() <= getStatus().ordinal());
        }else{
            return (PartReturnStatus.PART_RECEIVED.ordinal() <= getStatus().ordinal());
        }
    }

    public boolean canClamWithPartReturnBeAccepted() {
        if (PartReturnStatus.PART_TO_BE_SHIPPED.equals(getStatus())
                || PartReturnStatus.SHIPMENT_GENERATED.equals(getStatus())
                || PartReturnStatus.PART_SHIPPED.equals(getStatus())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPartOverDue() {
        return !(getDueDate().isAfter(Clock.today()));
    }

    public void setPartReceived(boolean b) {
        if (b) {
            setStatus(PartReturnStatus.PART_RECEIVED);
        }
    }

    public PartReturnStatus getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(PartReturnStatus actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getWarehouseLocation() {
        return this.warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }



    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Wpra getWpra() {
        return wpra;
    }

    public void setWpra(Wpra wpra) {
        this.wpra = wpra;
    }

    public PartReturnTaskTriggerStatus getTriggerStatus() {
        return this.triggerStatus;
    }

    public void setTriggerStatus(PartReturnTaskTriggerStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public PartReturn clone() {
        PartReturn partReturn = new PartReturn();
        partReturn.setActionTaken(actionTaken);
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setPartReceived(isPartReceived());
        partReturn.setShipment(shipment);
        partReturn.setWpra(wpra);
        partReturn.setTriggerStatus(triggerStatus);
        partReturn.setWarehouseLocation(warehouseLocation);
        partReturn.setDueDate(getDueDate());
        partReturn.setDueDateUpdated(isDueDateUpdated());
        partReturn.setActionTaken(actionTaken);
        partReturn.setInspectionResult(inspectionResult);
        partReturn.setOemPartReplaced(getOemPartReplaced());
        partReturn.setPaymentCondition(getPaymentCondition());
        partReturn.setReturnedBy(getReturnedBy());
        partReturn.setReturnLocation(getReturnLocation());
        partReturn.setStatus(getStatus());
        partReturn.setRmaNumber(getRmaNumber());
        partReturn.setDealerPickupLocation(getDealerPickupLocation());
        return partReturn;
    }
    
    public String getRecClaimfromWarrantyClaim(){
    	Long partReturnId = this.getId();
    	String recClaim =  recoveryClaimService.findRecClaimFromPartReturn(partReturnId);
		return recClaim;
    	
    }
}

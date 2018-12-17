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
package tavant.twms.domain.supplier;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.recovery.RecoverablePart;

import java.sql.Types;

/**
 * 
 * @author kannan.ekanath
 * 
 */
@Entity
public class SupplierPartReturn extends BasePartReturn {

    // These two fields have be to be UserComment objects actually
    private String supplierComment;

    private String sraComment;
    
    @ManyToOne
    private Carrier carrier;
    
    private String rgaNumber;

    @ManyToOne(fetch = FetchType.LAZY)
	private RecoverablePart recoverablePart;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    private Shipment supplierShipment;

    public SupplierPartReturn() {
		// TODO Auto-generated constructor stub
	}
    
    public SupplierPartReturn(RecoverablePart part, int partIndex){
        this.setRecoverablePart(part);
    	this.setStatus(PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED);
		part.getSupplierPartReturns().add(partIndex, this);
    }

    public void updateSupplierPartReturn(Carrier carrier, Location location, String rgaNumber) {
		this.setCarrier(carrier);
		this.setReturnLocation(location);
		this.setRgaNumber(rgaNumber);
	}
	
    public String getSraComment() {
        return sraComment;
    }

    public void setSraComment(String sraComment) {
        this.sraComment = sraComment;
    }

    public String getSupplierComment() {
        return supplierComment;
    }

    public void setSupplierComment(String supplierComment) {
        this.supplierComment = supplierComment;
    }

    public boolean isPartReturnGenrated() {
        return (getStatus().ordinal() < PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED.ordinal());
    }

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	public String getRgaNumber() {
		return rgaNumber;
	}

	public void setRgaNumber(String rgaNumber) {
		this.rgaNumber = rgaNumber;
	}

	public Shipment getSupplierShipment() {
		return supplierShipment;
	}

	public void setSupplierShipment(Shipment supplierShipment) {
		this.supplierShipment = supplierShipment;
	}

	public RecoverablePart getRecoverablePart() {
		return recoverablePart;
	}

	public void setRecoverablePart(RecoverablePart recoverablePart) {
		this.recoverablePart = recoverablePart;
	}

    @Override
    public void setStatus(PartReturnStatus status) {
        super.setStatus(status);
        String shipmentNo = null;
        String trackingNo = null;
        if(this.recoverablePart != null){
            if(this.supplierShipment != null) {
               shipmentNo =  supplierShipment.getId().toString();
               trackingNo = supplierShipment.getTrackingId();
            }
            if(sraComment != null){
                this.recoverablePart.setStatus(status,getSraComment(),shipmentNo,trackingNo);
            }else if(supplierComment != null){
                this.recoverablePart.setStatus(status,getSupplierComment(),shipmentNo,trackingNo);
            }else if(status.getStatus().equals(PartReturnStatus.PART_SHIPPED.getStatus()) && this.supplierShipment != null && this.supplierShipment.getComments() != null){
                this.recoverablePart.setStatus(status,getSupplierShipment().getComments(),shipmentNo,trackingNo);
            }
            else{
                this.recoverablePart.setStatus(status,null,shipmentNo,trackingNo);
            }
        }
    }

    public void setBasePartReturnStatus(PartReturnStatus status){
        super.setStatus(status);
    }

    private String warehouseLocation;


    /*@OneToOne
    private SupplierPartAcceptanceReason supplierPartAcceptanceReason;

    @OneToOne
    private SupplierPartRejectionReason supplierPartRejectionReason;
*/
   /* public SupplierPartAcceptanceReason getSupplierPartAcceptanceReason() {
        return supplierPartAcceptanceReason;
    }

    public void setSupplierPartAcceptanceReason(SupplierPartAcceptanceReason supplierPartAcceptanceReason) {
        this.supplierPartAcceptanceReason = supplierPartAcceptanceReason;
    }

    public SupplierPartRejectionReason getSupplierPartRejectionReason() {
        return supplierPartRejectionReason;
    }

    public void setSupplierPartRejectionReason(SupplierPartRejectionReason supplierPartRejectionReason) {
        this.supplierPartRejectionReason = supplierPartRejectionReason;
    }
*/
    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }
}
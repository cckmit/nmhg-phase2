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

import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Parameter;
import org.springframework.util.Assert;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.timeutil.Clock;

/**
 * @author vineeth.varghese
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Shipment implements AuditableColumns{
	@Id
	@GeneratedValue(generator = "Shipment")
	@GenericGenerator(name = "Shipment", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SHIPMENT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Carrier carrier;

    private Date shipmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location destination;

	private String contactPersonName;

    private String trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceProvider shippedBy;

    @Column(length = 4000)
    private String comments;
    
    @Column(length = 4000)
    private String cevaComments;
    
	// Need to change this to a sorted set later - Vineeth.
    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    private final List<PartReturn> parts = new ArrayList<PartReturn>();

    @OneToMany(mappedBy = "supplierShipment", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE})
    private List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();
    
    private Boolean logicalShipment = false;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<ShipmentLoadDimension> shipmentLoadDimension = new ArrayList<ShipmentLoadDimension>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    private String transientId;
    
    private Date receiptDate;

    public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public Shipment() {
        // ONLY FOR HIBERNATE
    }

    public Shipment(Location destination) {
        super();
        Assert.notNull(destination, "No destination provided for creating Shipment");
        this.destination = destination;
        this.shipmentDate = new Date();
    }

    /**
     * @return the carrier
     */
    public Carrier getCarrier() {
        return this.carrier;
    }

    /**
     * @param carrier the carrier to set
     */
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    /**
     * @return the trackingId
     */
    public String getTrackingId() {
        return this.trackingId;
    }

    /**
     * @param trackingId the trackingId to set
     */
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getShipmentDate() {
        return this.shipmentDate;
    }
    
    public String getShipmentDateForDisplay() {        
        if (shipmentDate != null) {
			return shipmentDate.toString();
		}
        return null;
    }
    
    //To display shipment date "MM-dd-yyyy" format for AMER in Parts Collected inbox of Dealer
	public String getShipmentDateForPartsCollected() {
		String shipDate = "";
		if (shipmentDate != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			shipDate = formatter.format(shipmentDate);
		}
		return shipDate;
	}

    public String getReceiptDateForDisplay() {
    	if (receiptDate != null) {
    		return receiptDate.toString();
    	}
    	return null;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    /**
     * @return the parts
     */
    public List<PartReturn> getParts() {
        return this.parts;
    }

    public void addParts(List<PartReturn> parts) {
        for (PartReturn replaced : parts) {
            addPart(replaced);
        }
    }

    public void addPart(PartReturn part) {
        if (this.destination == null) {
            throw new IllegalStateException(
                    "Shipment is not valid since it does not have a destination");
        }
        if (!part.getTriggerStatus().equals(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED_FOR_DEALER) && !(this.destination.equals(part.getReturnLocation()))) {
            throw new IllegalArgumentException(
                    "Part with different destination being added to shipment");
        }
        if (part.getTriggerStatus().equals(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED_FOR_DEALER) && !(this.shippedBy.equals(part.getReturnedBy()))) {
            throw new IllegalArgumentException(
                    "Part with different destination being added to shipment");
        }

        this.parts.add(part);
        part.setShipment(this);
    }

    public void removePart(PartReturn part) {
        Assert.notNull(part, "The part cannot be null for the remove part flow.");
        this.parts.remove(part);
        part.setShipment(null);
    }

    public void addSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns){
    	this.getSupplierPartReturns().addAll(supplierPartReturns);
    	for(SupplierPartReturn supplierPartReturn : supplierPartReturns){
    		supplierPartReturn.setSupplierShipment(this);
    	}
    }
    
    public Location getDestination() {
        return this.destination;
    }

    public void setDestination(Location location) {
        this.destination = location;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String remarks) {
        this.comments = remarks;
    }

    public ServiceProvider getShippedBy() {
        return this.shippedBy;
    }

    public void setShippedBy(ServiceProvider shippedBy) {
        this.shippedBy = shippedBy;
    }

    public Boolean getLogicalShipment() {
        return this.logicalShipment;
    }

    public void setLogicalShipment(Boolean logicalShipment) {
        this.logicalShipment = logicalShipment;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getTransientId() {
        return transientId;
    }

    public void setTransientId(String transientId) {
        this.transientId = transientId;
    }

	public List<SupplierPartReturn> getSupplierPartReturns() {
		return supplierPartReturns;
	}

	public void setSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns) {
		this.supplierPartReturns = supplierPartReturns;
	}

    public List<ShipmentLoadDimension> getShipmentLoadDimension() {
        return shipmentLoadDimension;
    }

    public void setShipmentLoadDimension(List<ShipmentLoadDimension> shipmentLoadDimension) {
        this.shipmentLoadDimension = shipmentLoadDimension;
    }

    public String getContactPersonName() {
		return contactPersonName;
}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public String getCevaComments() {
		return cevaComments;
	}

	public void setCevaComments(String cevaComments) {
		this.cevaComments = cevaComments;
	}

    public List<SupplierPartReturn> returnWithUniquePart(){
        Map<Long, SupplierPartReturn> supplierPartReturnList = new HashMap<Long,SupplierPartReturn>();
        for(SupplierPartReturn partReturn : this.getSupplierPartReturns()){
            if(partReturn.getRecoverablePart() != null && supplierPartReturnList.get(partReturn.getRecoverablePart().getId()) == null){
                supplierPartReturnList.put(partReturn.getRecoverablePart().getId(), partReturn);
            }
        }
        return new ArrayList<SupplierPartReturn>(supplierPartReturnList.values());
    }


}

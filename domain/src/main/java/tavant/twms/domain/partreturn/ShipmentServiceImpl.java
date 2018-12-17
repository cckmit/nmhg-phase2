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

import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.supplier.shipment.ContractShipmentService;

public class ShipmentServiceImpl implements ShipmentService {

    private ShipmentRepository shipmentRepository;
    
    private ContractShipmentService contractShipmentService;

    private static final Logger LOGGER = Logger.getLogger(ShipmentServiceImpl.class);

    public Shipment createShipmentForParts(List<PartReturn> parts) {
        validateParts(parts);
        PartReturn aPart = parts.get(0);
        /**
         * TODO: Kannan, If one part is marked for direct supplier recovery
         * all the parts belonging to that location will be marked like that.
        */
            Shipment shipment = new Shipment(aPart.getReturnLocation());
            shipment.setShippedBy(aPart.getReturnedBy());
            shipment.addParts(parts);
	        shipmentRepository.save(shipment);
	        return shipment;
       }


    public void addPartsToShipment(Shipment shipment, List<PartReturn> parts) {
        validateParts(parts);

        /**
         * TODO: Kannan, If one part is marked for direct supplier recovery
         * all the parts belonging to that location will be marked like that.
         */
        	shipment.addParts(parts);

    }

    public void addPartsToShipment(Long shipmentId, List<PartReturn> parts) {
        addPartsToShipment(this.shipmentRepository.findById(shipmentId), parts);
    }

    public void removePartsFromItsShipment(List<PartReturn> parts) {
        validateParts(parts);        
        /**
         * TODO: Kannan, If one part is marked for direct supplier recovery
         * all the parts belonging to that location will be marked like that.
         */
        	for (PartReturn part : parts) {
        		if(part.getShipment()!=null)
	            part.getShipment().removePart(part);
        		else 
        		 if(LOGGER.isDebugEnabled()){
        			 LOGGER.debug("The shipper for part  [" + part.toString() + "] is set to null");
        		 }
	        }

    }

    public void updateCarrierInfoToShipment(Shipment shipment, Carrier carrier, String trackingNumber) {
        if(LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Setting carrier [" + carrier + "] and tracking id ["
                    + trackingNumber + "] to selected parts");
        }
        shipment.setCarrier(carrier);
        shipment.setTrackingId(trackingNumber);

    }
    
    public void reloadShipments(List<Shipment> shipments) {
    	this.shipmentRepository.reloadShipments(shipments);
    }

    private void validateParts(List<PartReturn> parts) {
        if(parts == null || parts.size() == 0) {
            throw new IllegalArgumentException("There are no parts selected to update shipment info.");
        }
    }

    public void setShipmentRepository(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

	public void setContractShipmentService(
			ContractShipmentService contractShipmentService) {
		this.contractShipmentService = contractShipmentService;
	}

    public void updateShipmentsWithLoadDimension(List<Shipment> shipments){

        if(LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Updating shipments for load dimensions");
        }

        this.shipmentRepository.updateShipments(shipments);
    }
}

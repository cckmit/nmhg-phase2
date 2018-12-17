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

import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=false)
public interface ShipmentService {
    
    public Shipment createShipmentForParts(List<PartReturn> parts);
    
    public void addPartsToShipment(Shipment s, List<PartReturn> parts);
    
    public void addPartsToShipment(Long shipmentId, List<PartReturn> parts);
    
    public void removePartsFromItsShipment(List<PartReturn> parts);
    
    public void updateCarrierInfoToShipment(Shipment shipment, Carrier carrier, String trackingNumber);
    
    public void reloadShipments(List<Shipment> shipments);

    public void updateShipmentsWithLoadDimension(List<Shipment> shipments);
}

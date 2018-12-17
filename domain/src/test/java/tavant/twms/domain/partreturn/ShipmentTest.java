package tavant.twms.domain.partreturn;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.infra.DomainRepositoryTestCase;

public class ShipmentTest extends DomainRepositoryTestCase {
    
    private LocationRepository locationRepository;
    
    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }    
    
    public void testShipmentCreationWithoutLocation() {
        try {
            new Shipment(null);
            fail("Shouldn't be able to create a shipment without a valid location");
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        }        
    }
    
    public void testAddingPartToShipment() {
        Location destination = locationRepository.findByLocationCode("IR_LOC");
        Shipment shipment = new Shipment(destination);        
        OEMPartReplaced replacedPart = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setReturnLocation(destination);
        List <PartReturn> partReturns=new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        replacedPart.setPartReturns(partReturns);
        shipment.addPart(partReturns.get(0));
        assertEquals(1, shipment.getParts().size());
        assertEquals(replacedPart.getPartReturns().get(0), shipment.getParts().get(0));
    }
    
    public void testAddingPartToInvalidShipment() {
        Shipment shipment = new Shipment();
        Location location = locationRepository.findByLocationCode("IR_LOC");
        OEMPartReplaced replacedPart = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setReturnLocation(location);
        List <PartReturn> partReturns=new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        replacedPart.setPartReturns(partReturns);
        try {
            shipment.addPart(partReturns.get(0));
            fail("Shouldn't be able to add a part to an invalid(no destination) Shipment");
        } catch (IllegalStateException e) {            
        }
    }
    
    public void testAddingWrongPartToShipment() {
        Location destination = locationRepository.findByLocationCode("IR_LOC");
        Shipment shipment = new Shipment(destination);
        Location returnLocation = locationRepository.findByLocationCode("IR_KEN");
        OEMPartReplaced replacedPart = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setReturnLocation(returnLocation);
        List <PartReturn> partReturns=new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        replacedPart.setPartReturns(partReturns);
        try {
            shipment.addPart(partReturns.get(0));
            fail("Parts should be allowed to be added to a shipment only if the" 
                    + " parts return location and shipment destination are the same");
        } catch (IllegalArgumentException e) {            
        }
    }
    
    public void testRemovePartFromShipment() {
        Location destination = locationRepository.findByLocationCode("IR_LOC");
        Shipment shipment = new Shipment(destination);        
        OEMPartReplaced replacedPart = new OEMPartReplaced();
        PartReturn partReturn = new PartReturn();
        partReturn.setReturnLocation(destination);
        List <PartReturn> partReturns=new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        replacedPart.setPartReturns(partReturns);
        shipment.addPart(partReturns.get(0));
        assertEquals(1, shipment.getParts().size());
        assertEquals(replacedPart.getPartReturns().get(0), shipment.getParts().get(0));
        shipment.removePart(partReturns.get(0));
        assertEquals(0, shipment.getParts().size());
        assertNull(replacedPart.getPartReturns().get(0).getShipment());
    }
}

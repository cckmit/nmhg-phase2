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
package tavant.twms.domain.supplier.shipment;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.CarrierRepository;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;

/**
 * @author kannan.ekanath
 * 
 */
public class ContractShipmentServiceImpl implements ContractShipmentService {

    private CarrierRepository carrierRepository;

    public Shipment generateSupplierContractShipment(List<SupplierPartReturn> supplierPartReturns) {
        Assert.notEmpty(supplierPartReturns, "Please pass atleast one valid part");
        // TODO check if all the parts have the same location
        Shipment shipment = new Shipment(supplierPartReturns.get(0).getReturnLocation());
        shipment.addSupplierPartReturns(supplierPartReturns);
        return shipment;
    }

    public Shipment generateSupplierContractShipment(List<SupplierPartReturn> supplierPartReturns, Location returnLocation) {
        Assert.notEmpty(supplierPartReturns, "Please pass atleast one valid part");
        // TODO check if all the parts have the same location
        Shipment shipment = new Shipment(returnLocation);
        shipment.addSupplierPartReturns(supplierPartReturns);
        return shipment;
    }
	
	public void removeSupplierPartReturnFromSupplierShipment(List<SupplierPartReturn> supplierPartReturns) {
		for (SupplierPartReturn supplierPartReturn: supplierPartReturns) {
			supplierPartReturn.setSupplierShipment(null);
		}
	}

	public void addPartsToSupplierShipment(Shipment shipment, List<SupplierPartReturn> supplierPartReturns) {
		shipment.addSupplierPartReturns(supplierPartReturns);
	}

	public CarrierRepository getCarrierRepository() {
        return carrierRepository;
    }

    @Required
    public void setCarrierRepository(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

}

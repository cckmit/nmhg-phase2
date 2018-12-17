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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;

/**
 * @author kannan.ekanath
 *
 */
@Transactional(readOnly=false)
public interface ContractShipmentService {
	
	@Transactional(readOnly=false)
	public Shipment generateSupplierContractShipment(List<SupplierPartReturn> supplierPartReturns);
	
	@Transactional(readOnly=false)
	public void removeSupplierPartReturnFromSupplierShipment(List<SupplierPartReturn> supplierPartReturns);

	@Transactional(readOnly=false)
	public void addPartsToSupplierShipment(Shipment shipment, List<SupplierPartReturn> supplierPartReturns);

    @Transactional(readOnly=false)
    public Shipment generateSupplierContractShipment(List<SupplierPartReturn> supplierPartReturns, Location returnLocation);
}

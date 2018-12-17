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

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class CarrierServiceImpl extends
		GenericServiceImpl<Carrier, Long, Exception> implements CarrierService {

	private CarrierRepository carrierRepository;

	@Override
	public GenericRepository<Carrier, Long> getRepository() {
		return carrierRepository;
	}

	public void setCarrierRepository(CarrierRepository carrierRepository) {
		this.carrierRepository = carrierRepository;
	}

	public Carrier findCarrierByName(String name) {
		return carrierRepository.findCarrierByName(name);
	}
}

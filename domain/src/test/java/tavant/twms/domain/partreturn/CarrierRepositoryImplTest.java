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

import tavant.twms.infra.DomainRepositoryTestCase;

public class CarrierRepositoryImplTest extends DomainRepositoryTestCase {

    CarrierRepository carrierRepository;

    public void testFindCarrierByName() {
        Carrier fedEx = carrierRepository.findCarrierByName("FedEx");
        assertNotNull(fedEx);
        assertEquals("312", fedEx.getCode());
        
    }

    public void testFindCarrierById() {
        Carrier fedEx = carrierRepository.findCarrierById(new Long(1));
        assertNotNull(fedEx);
        assertEquals("FedEx", fedEx.getName());
    }

    /**
     * @return the carrierRepository
     */
    public CarrierRepository getCarrierRepository() {
        return carrierRepository;
    }

    /**
     * @param carrierRepository
     *            the carrierRepository to set
     */
    public void setCarrierRepository(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

}

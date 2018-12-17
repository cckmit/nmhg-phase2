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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Mar 7, 2007
 * Time: 9:07:42 PM
 */

package tavant.twms.domain.orgmodel;

import tavant.twms.infra.DomainRepositoryTestCase;

public class OrganizationRepositoryImplTest extends DomainRepositoryTestCase {
    OrganizationRepository organizationRepository;

    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

   public void testFindByName() {
       String orgName = "Test Organization";

       Organization organization = createOrganization(orgName);
       organizationRepository.save(organization);

       getSession().flush();

       Organization fetchedOrg = organizationRepository.findByName(orgName);

       assertNotNull(fetchedOrg);
       assertEquals(orgName, fetchedOrg.getName());
       assertEquals("city", fetchedOrg.getAddress().getCity());
    }
   public void testFindAllBus() {
		String orgName = "OEM"; // AIRDYNE INC
		Organization fetchedOrg = organizationRepository.findByName(orgName);
		assertNotNull(fetchedOrg);
		assertEquals(1,fetchedOrg.getBusinessUnits().size());
	}

    /**
     * @return
     */
    private Organization createOrganization(String organizationName) {
        Organization organization = new Organization();
        organization.setName(organizationName);
        
        Address newAddress = new Address();
        newAddress.setAddressLine1("line1");
        newAddress.setAddressLine2("line2");
        newAddress.setCity("city");
        newAddress.setCountry("country");
        newAddress.setState("state");
        newAddress.setZipCode("zipcode");
        organization.setAddress(newAddress);
        return organization;
    }

}

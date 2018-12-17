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
package tavant.twms.domain.orgmodel;

import java.util.Currency;
import java.util.List;

import tavant.twms.infra.DomainRepositoryTestCase;


public class DealershipRepositoryImplTest extends DomainRepositoryTestCase {
    DealershipRepository dealershipRepository;
    UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    public void testCreateDealership() {
        Dealership newDealership = createDealership("Test Dealer","123456");
        
        this.dealershipRepository.createDealership(newDealership);
        
        assertNotNull(newDealership.getId());
        assertNotNull(newDealership.getAddress().getId());
        
        getSession().flush();
        getSession().clear();
        Dealership reloadedEntity = (Dealership)getSession().get(Dealership.class, newDealership.getId());
        assertEquals(newDealership.getPreferredCurrency(),reloadedEntity.getPreferredCurrency() );
    }

    public void testFindByDealerId() {
        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        
        getSession().flush();
        getSession().flush();
        
        assertNotNull(this.dealershipRepository.findByDealerId(dealership.getId()));
    }

    public void testFindByDealerName() {
        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        
        getSession().flush();
        getSession().flush();
        
        assertNotNull(this.dealershipRepository.findByDealerName("Test Dealer"));
    }

    public void testFindByDealerNumber() {
        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        
        getSession().flush();
        getSession().flush();
        
        assertNotNull(this.dealershipRepository.findByDealerNumber("123456"));
    }
    
    public void testFindDealersByName() {
        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        
        getSession().flush();
        getSession().flush();
        
        assertNotNull(this.dealershipRepository.findDealersByNumberOrName(null,"Test"));
    }
    
    public void testFindDealersByNumber() {
        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        
        getSession().flush();
        getSession().flush();
        
        assertNotNull(this.dealershipRepository.findDealersByNumberOrName("34",null));
    }

    /**
     * @return
     */
    private Dealership createDealership(String dealerName,String dealerNumber) {
        Dealership newDealership = new Dealership();
        newDealership.setName(dealerName);
        newDealership.setDealerNumber(dealerNumber);
        newDealership.setPreferredCurrency(Currency.getInstance("USD"));
        
        Address newAddress = new Address();
        newAddress.setAddressLine1("line1");
        newAddress.setAddressLine2("line2");
        newAddress.setCity("city");
        newAddress.setCountry("country");
        newAddress.setState("state");
        newAddress.setZipCode("zipcode");
        newDealership.setAddress(newAddress);
        return newDealership;
    }
    
    public void testFindDealersWithNameLike() {
/*        Dealership dealership = createDealership("Test Dealer","123456");
        this.dealershipRepository.createDealership(dealership);
        List<String> dealerNames = this.dealershipRepository.findDealerNamesStartingWith("T", 0, 2);
        assertTrue(dealerNames.contains("Test Dealer"));
        dealerNames = this.dealershipRepository.findDealerNamesStartingWith("U", 0, 2);
        assertTrue(dealerNames.isEmpty());
		*/
    }
            
    public void testIsDealer() {
    	User user = this.userRepository.findByName("michael");
    	assertNotNull(user);
    	boolean isDealer = this.dealershipRepository.isDealer(user);
    	assertFalse(isDealer);
    	user = this.userRepository.findByName("sandy");
    	assertNotNull(user);
    	isDealer = this.dealershipRepository.isDealer(user);
    	assertTrue(isDealer);
    }
    
    public void testUpdateDealership() {
        Dealership newDealership = createDealership("Test Dealer","123456");        
        dealershipRepository.createDealership(newDealership);       
        getSession().flush();
        getSession().clear();
        Dealership reloadedEntity = dealershipRepository.findByDealerId(newDealership.getId());
        DealerUser user = new DealerUser();
        user.setFirstName("Mr");
        user.setLastName("Michael");
        List<DealerUser> users = reloadedEntity.getDealerUsers();
        users.add(user);
        DealerUser user2 = new DealerUser();
        user2.setFirstName("Ms");
        user2.setLastName("Angel");
        users.add(user2);
        dealershipRepository.updateDealership(reloadedEntity);
        
        getSession().flush();
        getSession().clear();
        Dealership modiEntity = dealershipRepository.findByDealerId(reloadedEntity.getId());
        assertEquals(2, modiEntity.getDealerUsers().size());
    }    
}

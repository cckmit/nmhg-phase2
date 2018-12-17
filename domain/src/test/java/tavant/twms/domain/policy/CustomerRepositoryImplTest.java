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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.infra.DomainRepositoryTestCase;

public class CustomerRepositoryImplTest extends DomainRepositoryTestCase {
    CustomerRepository customerRepository; 

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void testCreate() {
        Customer cust = new Customer();
        cust.setName("TestingABCDEFG");
        cust.setIndividual(true);
        customerRepository.createCustomer(cust);
        List<Customer> matchingIndividualCustomers = new ArrayList<Customer>();
        matchingIndividualCustomers = customerRepository.findCustomersForNameLike("Testing", 1);
        assertTrue(matchingIndividualCustomers.size()==1);
        Customer cust1 = new Customer();
        cust1.setCompanyName("ABCDEFG12");
        cust1.setIndividual(false);
        customerRepository.createCustomer(cust1);
        List<Customer> matchingCompanyCustomers = new ArrayList<Customer>();
        matchingCompanyCustomers = customerRepository.findCustomersForCompanyNameLike("ABCDEFG", 1);
        assertTrue(matchingCompanyCustomers.size()==1);
        Long id = cust1.getId();
        Customer customer = customerRepository.findCustomerById(id);
        assertEquals(customer, cust1);
    }

    
}

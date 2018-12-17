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
package tavant.twms.infra;

import junit.framework.TestCase;

public class OgnlNullSafeBeanProviderTest extends TestCase{
    private BeanProvider beanProvider;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        beanProvider = new OgnlNullSafeBeanProvider();
    }
    
    public void testGetPropertyWithNull() {
        try {
            beanProvider.getProperty(null, null);         
            fail("Expected Exception with both parameters as null");
        } catch (IllegalArgumentException e) {}
        try {
            beanProvider.getProperty(null, "");
            fail("Expected Exception with the ognl expression as null");
        } catch (IllegalArgumentException e) {}
        try {
            beanProvider.getProperty("", null);
            fail("Expect Exception with the ognl target as null");
        } catch (IllegalArgumentException e) {}
    }
    
    public void testGetPropertyWithValidProperty() {
        User customer = new User();
        customer.setName("Schumacher");
        Loan loan = new Loan();
        loan.setCustomer(customer);
        assertEquals("Expected to get the name from the loan bean", 
                "Schumacher", beanProvider.getProperty("customer.name", loan));
    }
    
    public void testGetPropertyWithInvalidProperty() {
        User customer = new User();
        customer.setName("Schumacher");
        Loan loan = new Loan();
        loan.setCustomer(customer);
        //For now you are swallowing both NoSuchPropertyException
        //as well as NPE
        assertNull(beanProvider.getProperty("customer.age", loan));
    }
    
    
    
    public class User {
        private String name;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public class Loan {
        private User customer;

        public User getCustomer() {
            return customer;
        }

        public void setCustomer(User customer) {
            this.customer = customer;
        }
    }
}

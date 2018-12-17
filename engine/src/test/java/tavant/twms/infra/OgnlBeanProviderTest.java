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

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * @author vineeth.varghese
 * @date Aug 22, 2006
 */
public class OgnlBeanProviderTest extends TestCase {
    
    private BeanProvider beanProvider;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        beanProvider = new OgnlBeanProvider();
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
        try {
            beanProvider.getProperty("customer.age", loan);
            fail("Expected to a RuntimeException for try to use an invalid ognl expression");
        } catch (RuntimeException e) {
            //Expected
        }
        
    }
    
    public void testGetPropertyOverASet() {
        User u = new User();
        u.setName("user");
        final Loan first = new Loan("l1");
        u.addLoan(first);
        u.addLoan(new Loan("l2"));
        assertEquals(first, beanProvider.getProperty("loans.first()", u));
    }
    
    
    
    public class User {
        private String name;
        private SortedSet<Loan> loans = new TreeSet<Loan>();
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void addLoan(Loan loan) {
            this.loans.add(loan);
            loan.setCustomer(this);
        }

        public final SortedSet<Loan> getLoans() {
            return loans;
        }
    }
    
    public class Loan implements Comparable<Loan>{
        
        private String name;
        
        private User customer;

        public Loan() {
        }
        
        public Loan(String name) {
            this.name = name;
        }
        
        public final String getName() {
            return name;
        }

        public User getCustomer() {
            return customer;
        }

        public void setCustomer(User customer) {
            this.customer = customer;
        }

        public int compareTo(Loan o) {
            return name.compareTo(o.getName());
        }
        
        
    }
  
}

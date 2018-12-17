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
package tavant.twms.jbpm.infra;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

public class ContextVariableProviderTest extends TestCase {
    
    private ExecutionContext ctx;
    private ContextVariableProvider variableProvider;
    private User user;
    private Loan loan;
    
    String processdef = "<process-definition xmlns='' name='ClaimCreation'>" + 
                        "  <start-state name='Start'>" + 
                        "    <transition name='' to='ClaimEntry'></transition>" + 
                        "  </start-state>" + 
                        "  <task-node name='ClaimEntry'>" + 
                        "    <task name='Draft'/>" + 
                        "    <transition name='Submit' to='End'/>" + 
                        "  </task-node>" +                         
                        "  <end-state name='End'/>" + 
                        "</process-definition>";

    protected void setUp() throws Exception {
        super.setUp();
        ProcessDefinition def = ProcessDefinition.parseXmlInputStream(
                new ByteArrayInputStream(processdef.getBytes()));
        ProcessInstance processInstance = def.createProcessInstance();
        Token token = processInstance.getRootToken();        
        ctx = new ExecutionContext(token);
        user = new User();
        user.setCity("Bangalore");
        user.setEmail("schumacher@ferrari.com");
        user.setName("Schumacher");        
        loan = new Loan();
        loan.setCustomer(user);
        loan.setType("Long");
        ctx.setVariable("loan", loan);
        variableProvider = new ContextVariableProvider(ctx);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetContextVariable() {
        assertEquals("Schumacher" , variableProvider.getContextVariable("loan.customer.name"));
        assertEquals("Bangalore" , variableProvider.getContextVariable("loan.customer.city"));
        assertEquals("schumacher@ferrari.com" , 
                variableProvider.getContextVariable("loan.customer.email"));
        assertEquals(user , variableProvider.getContextVariable("loan.customer"));
        assertEquals(loan , variableProvider.getContextVariable("loan"));
        user.setCity(null);
        assertEquals(null , variableProvider.getContextVariable("loan.customer.city"));        
    }
    
    public void testGetContextVariableWithInvalidArgs() {
        try {
            variableProvider.getContextVariable(null);
            fail("Expected IllegalArgumentException for expression as null");
        } catch (IllegalArgumentException e) {}        
    }
    
    public void testGetContextVariables() {
        List<String> expressions = new ArrayList<String>();
        expressions.add("loan.customer.name");
        expressions.add("loan.customer.city");
        expressions.add("loan.customer.email");
        expressions.add("loan.customer");
        expressions.add("loan");
        Collection<Object> expectedValues = new ArrayList<Object>();
        expectedValues.add("Schumacher");
        expectedValues.add("Bangalore");
        expectedValues.add("schumacher@ferrari.com");
        expectedValues.add(user);
        expectedValues.add(loan);
        Collection actualValues = variableProvider.getContextVariables(expressions);
        assertEquals(expectedValues, actualValues);
    }
    
    public void testGetContextVariablesWithInvalidArgs() {
        try {
            variableProvider.getContextVariables(null);
            fail("Expected IllegalArgumentException for expression collection as null");
        } catch (IllegalArgumentException e) {}        
    }
    
    public void testGetContextVariablesWithNoExpressions() {
        Collection values = variableProvider.getContextVariables(new ArrayList<String>());
        assertTrue(values.size() == 0);
    }
    
    @SuppressWarnings("unchecked")
    public void testGetExplodedContextVariables() {
        PhoneNumber homeNumber = new PhoneNumber();
        homeNumber.setType("HOME");
        homeNumber.setNumber("232323");
        PhoneNumber workNumber = new PhoneNumber();
        homeNumber.setType("WORK");
        homeNumber.setNumber("454545");
        Collection numbers = new ArrayList();
        numbers.add(homeNumber);
        numbers.add(workNumber);
        user.setPhoneNumbers(numbers);
        List<String> expressions = new ArrayList<String>();
        expressions.add("loan.customer.phoneNumbers");  
        expressions.add("loan.customer.name");
        Collection<Object> expectedValues = new ArrayList<Object>();        
        expectedValues.add(homeNumber);
        expectedValues.add(workNumber);        
        expectedValues.add("Schumacher");
        Collection actualVariablesObtained 
                = variableProvider.getExplodedContextVariables(expressions);        
        assertEquals(expectedValues, actualVariablesObtained);
        
    }
    
    public class User {
        
        private String name;
        
        private String email;
        
        private String city;
        
        private Collection phoneNumbers;
        
        public Collection getPhoneNumbers() {
            return phoneNumbers;
        }

        public void setPhoneNumbers(Collection phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }       
    }
    
    public class Loan {
        private User customer;
        
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public User getCustomer() {
            return customer;
        }

        public void setCustomer(User customer) {
            this.customer = customer;
        }
    }
    
    public class PhoneNumber {
        String type;
        String number;
        
        public String getNumber() {
            return number;
        }
        
        public void setNumber(String number) {
            this.number = number;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }        
    }

}

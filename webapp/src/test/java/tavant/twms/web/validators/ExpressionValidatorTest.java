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
package tavant.twms.web.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;
import junit.framework.TestCase;
import tavant.twms.domain.policy.CoverageTerms;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.RecoveryFormula;
import tavant.twms.web.admin.policy.EditPolicy;
import tavant.twms.web.admin.supplier.ManageContract;

/**
 * 
 * @author kannan.ekanath
 * 
 */

public class ExpressionValidatorTest extends TestCase {

    public static class MyRequiredValidator extends ValidatorSupport {
        
        @Override
        //make the method public 
        public Object getFieldValue(String name, Object object) throws ValidationException {
            return super.getFieldValue(name, object);
        }

        public void validate(Object object) throws ValidationException {
        }
    }

    public void testRequiredValidator() throws ValidationException {
        Contract c = new Contract();
        RecoveryFormula formula1 = new RecoveryFormula();
        CompensationTerm term1 = new CompensationTerm(null, formula1);
        formula1.setPercentageOfCost(30);

        RecoveryFormula formula2 = new RecoveryFormula();
        CompensationTerm term2 = new CompensationTerm(null, formula2);
        formula2.setPercentageOfCost(45);

        c.addCompensationTerm(term1);
        c.addCompensationTerm(term2);

        ManageContract m = new ManageContract();
        m.setContract(c);
        //m.setSectionName("ABCD");

        MyRequiredValidator v = new MyRequiredValidator();
        assertEquals(Boolean.TRUE, v.getFieldValue("contract.compensationTerms.{? #this.recoveryFormula.percentageOfCost == null}.size == 0", m));
        
        formula1.setPercentageOfCost(null);
        assertEquals(Boolean.FALSE, v.getFieldValue("contract.compensationTerms.{? #this.recoveryFormula.percentageOfCost == null}.size == 0", m));
    }
    
    public void testExpressionValidatorForManagePolicy() throws ValidationException {
    	EditPolicy mp = new EditPolicy();
    	String newEval = "type.equals('GOODWILL') || policyDefinition.coverageTerms.serviceHoursCovered!=null";
    	
    	//When WarrantyType is not GoodWill then service hours must not be null.
    	mp.setType("STANDARD");
    	PolicyDefinition pd = new PolicyDefinition();
    	CoverageTerms ct =new CoverageTerms();
    	pd.setCoverageTerms(ct);
    	mp.setPolicyDefinition(pd);
    	
    	//case1 hours in service is null
    	ct.setServiceHoursCovered(null);
    	MyRequiredValidator v = new MyRequiredValidator();
    	assertEquals(Boolean.FALSE, v.getFieldValue(newEval, mp));
    	
    	//case2 positive value
    	ct.setServiceHoursCovered(2);
    	assertEquals(Boolean.TRUE, v.getFieldValue(newEval, mp));
    	
    	//When WarrantyType is GoodWill then service hours may be null.
    	mp.setType("GOODWILL");
    	
    	//case1 hours in service is null
    	ct.setServiceHoursCovered(null);
    	assertEquals(Boolean.TRUE, v.getFieldValue(newEval, mp));
    	
    	//case2 positive value
    	ct.setServiceHoursCovered(2);
    	assertEquals(Boolean.TRUE, v.getFieldValue(newEval, mp));
    }
}

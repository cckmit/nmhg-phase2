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
package tavant.twms.domain.claim.payment;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;

import java.util.Currency;

import junit.framework.TestCase;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.DomainTestHelper;

import com.domainlanguage.money.Money;

public class MealsExpenseComputerTest extends TestCase {

    public void testCompute() {
        Claim claim = new MachineClaim(){

            @Override
            public Currency getCurrencyForCalculation() {
                return Currency.getInstance("USD");
            }
            
        };

        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);

        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.setMealsExpense(Money.dollars(20));
        ServiceInformation serviceInformation = new ServiceInformation();
        
        serviceInformation.setServiceDetail(serviceDetail);
        claim.setServiceInformation(serviceInformation);
        
        MealsExpenseComputer fixture = new MealsExpenseComputer();
        assertEquals(Money.dollars(20),fixture.compute(claimedItem, null));
    }

}

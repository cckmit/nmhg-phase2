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

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.DomainTestHelper;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;

public class NonOEMPaymentComputerTest extends DomainRepositoryTestCase {
    
    public void testComputeAndUpdateWithoutPayment() {
        NonOEMPaymentComputer fixture = new NonOEMPaymentComputer();
        
        Claim theClaim = new MachineClaim() {

            @Override
            public Currency getCurrencyForCalculation() {
                return GlobalConfiguration.getInstance().getBaseCurrency();
            }
            
        };

        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);
        
        Dealership dealership = new Dealership();
        dealership.setId(7L);
        theClaim.setForDealerShip(dealership);
        
        theClaim.setServiceInformation(new ServiceInformation());
        ServiceDetail serviceDetail = new ServiceDetail();

        theClaim.getServiceInformation().setServiceDetail(serviceDetail);
        Item item = new Item();
        item.setId(2L);
        
        NonOEMPartReplaced partReplaced = new NonOEMPartReplaced();
        partReplaced.setNumberOfUnits(2);
        partReplaced.setPricePerUnit(Money.dollars(12.0D));
        serviceDetail.getNonOEMPartsReplaced().add(partReplaced);
        
        Money baseAmnt = fixture.compute(claimedItem, null);
        
        assertEquals(Money.dollars(24.0D), baseAmnt);
    }
}

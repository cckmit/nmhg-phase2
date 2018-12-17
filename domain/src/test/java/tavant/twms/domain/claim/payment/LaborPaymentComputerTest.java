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

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.rates.LaborRatesAdminService;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.DomainRepositoryTestCase;

import java.math.BigDecimal;
import java.util.Currency;

public class LaborPaymentComputerTest extends DomainRepositoryTestCase {
    private LaborRatesAdminService laborRatesAdminService;

    private CatalogRepository catalogRepository;

    private DealershipRepository dealershipRepository;

    /**
     * @param laborRatesAdminService the laborRatesAdminService to set
     */
    public void setLaborRatesAdminService(LaborRatesAdminService laborRatesAdminService) {
        this.laborRatesAdminService = laborRatesAdminService;
    }

    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public void disable_testCompute() throws Exception {
        Claim theClaim = new MachineClaim() {

            @Override
            public Currency getCurrencyForCalculation() {
                return GlobalConfiguration.getInstance().getBaseCurrency();
            }

        };

        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

        theClaim.setForDealerShip(this.dealershipRepository.findByDealerId(7L));
        theClaim.setRepairDate(CalendarDate.from(2006, 10, 10));
        ItemReference itemReference = new ItemReference();
        Item anotherItem = this.catalogRepository.findItem("D6060");
        itemReference.setReferredItem(anotherItem);
        claimedItem.setItemReference(itemReference);

        Policy policy = new RegisteredPolicy() {

            @Override
            public WarrantyType getWarrantyType() {
                return new WarrantyType("STANDARD");
            }

        };

        theClaim.setServiceInformation(new ServiceInformation());
        ServiceDetail serviceDetail = new ServiceDetail();
        theClaim.getServiceInformation().setServiceDetail(serviceDetail);

        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setHoursSpent(new BigDecimal(3));
        serviceDetail.getLaborPerformed().add(laborDetail);

        LaborPaymentComputer fixture = new LaborPaymentComputer();
        fixture.setLaborRatesAdminService(this.laborRatesAdminService);
        Money laborCost = fixture.compute(claimedItem, policy);
        assertEquals(Money.dollars(0.0D), laborCost);
    }
}

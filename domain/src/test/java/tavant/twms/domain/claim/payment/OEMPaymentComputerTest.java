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

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.DomainTestHelper;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class OEMPaymentComputerTest extends DomainRepositoryTestCase {
    private DealershipRepository dealershipRepository;
    private CatalogRepository catalogRepository;
    private ItemPriceAdminService itemPriceAdminService;

    @Required
    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Required
    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    /**
	 * @param itemPriceAdminService the itemPriceAdminService to set
	 */
    @Required
	public void setItemPriceAdminService(ItemPriceAdminService itemPriceAdminService) {
		this.itemPriceAdminService = itemPriceAdminService;
	}

	public void testComputeAndUpdateWithoutPayment() throws Exception {
        OEMPaymentComputer fixture = new OEMPaymentComputer();
        fixture.setItemPriceAdminService(itemPriceAdminService);        
        Claim theClaim = new MachineClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

        theClaim.setRepairDate(CalendarDate.from(2006,10,10));
        ItemReference itemReference = new ItemReference();
        Item anotherItem = catalogRepository.findItem("MC-COUGAR-50-HZ-1");
        itemReference.setReferredItem(anotherItem);
        claimedItem.setItemReference(itemReference);

        Dealership dealership = dealershipRepository.findByDealerId(7L);
        theClaim.setForDealerShip(dealership);
        
        theClaim.setServiceInformation(new ServiceInformation());
        ServiceDetail serviceDetail = new ServiceDetail();

        theClaim.getServiceInformation().setServiceDetail(serviceDetail);
       
        itemReference = new ItemReference(anotherItem);
        OEMPartReplaced partReplaced = new OEMPartReplaced(itemReference, 2);
        serviceDetail.getOEMPartsReplaced().add(partReplaced);
        
 //       Money baseAmnt = fixture.compute(claimedItem, null);
        
//        assertEquals(Money.dollars(56.0D), baseAmnt);
    }
    
    public void testComputeAndUpdateWithoutPaymentSerializedItem() throws Exception {
        OEMPaymentComputer fixture = new OEMPaymentComputer();
        fixture.setItemPriceAdminService(itemPriceAdminService);        
        
        Claim theClaim = new MachineClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(theClaim);

        theClaim.setRepairDate(CalendarDate.from(2006,10,10));
        InventoryItem inventoryItem = new InventoryItem();
        Item item = catalogRepository.findItem("MC-COUGAR-50-HZ-1");        
        inventoryItem.setOfType(item);

        ItemReference itemReference = new ItemReference(inventoryItem);
        claimedItem.setItemReference(itemReference);
        
        Dealership dealership = dealershipRepository.findByDealerId(7L);
        dealership.setPreferredCurrency(Currency.getInstance("USD"));
        theClaim.setForDealerShip(dealership);
        
        theClaim.setServiceInformation(new ServiceInformation());
        ServiceDetail serviceDetail = new ServiceDetail();

        theClaim.getServiceInformation().setServiceDetail(serviceDetail);
        serviceDetail.getOEMPartsReplaced().add(new OEMPartReplaced(new ItemReference(inventoryItem), 2));
        
  //    Money baseAmnt = fixture.compute(claimedItem, null);
        
 //       assertEquals(Money.dollars(56.0D), baseAmnt);
    }

}

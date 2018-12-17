/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.supplier.contract;

import junit.framework.TestCase;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.supplier.contract.CoverageCondition.ComparisonWith;

import com.domainlanguage.time.CalendarDate;

/**
 * @author kannan.ekanath
 * 
 */
public class CoverageConditionTest extends TestCase {

    public void testCondition() {
        CoverageCondition coverageCondition = new CoverageCondition(5,
                ComparisonWith.DATE_OF_MANUFACTURE);
        Claim claim = new MachineClaim();
        CalendarDate july5th = CalendarDate.date(2006, 7, 5);// 5th july
        claim.setInstallationDate(july5th);
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType(InventoryType.RETAIL);
        CalendarDate august4th = CalendarDate.date(2006, 8, 4);// 4th august
        CalendarDate sep3rd = CalendarDate.date(2006, 8, 3); // 3rd september
        ClaimedItem claimedItem = new ClaimedItem();
        inventoryItem.setShipmentDate(august4th);
        inventoryItem.setDeliveryDate(sep3rd);
        inventoryItem.setBuiltOn(july5th);
        ItemReference itemReference = new ItemReference();
        itemReference.setReferredInventoryItem(inventoryItem);
        claimedItem.setItemReference(itemReference);
        claim.addClaimedItem(claimedItem);
        claimedItem.setClaim(claim);
        Item item = new Item();
        OEMPartReplaced part = new OEMPartReplaced();

        // serialized part with DOD
        part.setItemReference(new ItemReference(inventoryItem));
        assertEquals(july5th, coverageCondition.getStartingDate(claimedItem));

        // non serialized part with DOD
        part.setItemReference(new ItemReference(item));
        assertEquals(july5th, coverageCondition.getStartingDate(claimedItem));

        coverageCondition.setComparedWith(ComparisonWith.DATE_OF_DELIVERY);
        // serialized part with DOM
        part.setItemReference(new ItemReference(inventoryItem));
        assertEquals(sep3rd, coverageCondition.getStartingDate(claimedItem));

        // non serialized part with DOM
        part.setItemReference(new ItemReference(item));
        assertEquals(sep3rd, coverageCondition.getStartingDate(claimedItem));

        // test if covers
        CalendarDate sep4th = CalendarDate.date(2006, 9, 4);
        // 5 months within july 5th from which coverage starts
        claim.setRepairDate(sep4th);
        assertTrue(coverageCondition.isApplicable(claimedItem));

        CalendarDate jan2nd2007 = CalendarDate.date(2007, 1, 21);
        // outside 5 months from july 5th
        claim.setRepairDate(jan2nd2007);
        assertFalse(coverageCondition.isApplicable(claimedItem));
    }
}

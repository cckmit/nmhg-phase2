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
package tavant.twms.domain.claim;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import junit.framework.TestCase;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.DomainTestHelper;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public class ClaimTest extends TestCase {
    public void testGetEquipmentBilledDate_SerializedItem() {
        InventoryItem invItem = new InventoryItem();
        
        CalendarDate shipmentDate = CalendarDate.date(2006,1,1);
        invItem.setShipmentDate(shipmentDate);
        
        Claim fixture = new MachineClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(fixture);

        ItemReference itemReference = new ItemReference(invItem);
        claimedItem.setItemReference(itemReference);
        
        assertEquals(shipmentDate,fixture.getEquipmentBilledDate());
        assertTrue(fixture.hasEnoughInfoToObtainPartReturnConfiguration());
    }
    
    public void testGetEquipmentBilledDate_UnSerializedItem() {
        CalendarDate installationDate = CalendarDate.date(2006,1,1);

        Claim fixture = new MachineClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(fixture);

        ItemReference itemReference = new ItemReference(new Item());
        claimedItem.setItemReference(itemReference);
        fixture.setInstallationDate(installationDate);
        
        assertEquals(installationDate,fixture.getEquipmentBilledDate());
        assertTrue(fixture.hasEnoughInfoToObtainPartReturnConfiguration());
    }    
    
    public void testUnserializedItem_noInstallationDate() {

        Claim fixture = new MachineClaim();
        ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(fixture);

        ItemReference itemReference = new ItemReference(new Item());
        claimedItem.setItemReference(itemReference);
        
        assertEquals(null,fixture.getEquipmentBilledDate());
        assertFalse(fixture.hasEnoughInfoToObtainPartReturnConfiguration());
    }    
}

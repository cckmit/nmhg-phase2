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
package tavant.twms.domain.watchlist;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.infra.DomainRepositoryTestCase;

public class WatchListRepositoryImplTest extends DomainRepositoryTestCase {
    WatchListRepository repository;
    
    public void setRepository(WatchListRepository repository) {
        this.repository = repository;
    }

    public void testIsWatchedDealership_NotWatched() {
        Dealership dealer = new Dealership();
        dealer.setId(new Long(1));
        assertFalse(repository.isWatched(dealer));
    }

    public void testIsWatchedDealership_Watched() {
        Dealership dealer = new Dealership();
        dealer.setId(new Long(10));
        assertTrue(repository.isWatched(dealer));
    }    
    
    public void testIsWatchedItem_Watched() {
        Item item = new Item();
        item.setId(new Long(28));
        assertTrue(repository.isWatched(item));
    }

    public void testIsWatchedItem_NotWatched() {
        Item item = new Item();
        item.setId(new Long(1));
        assertFalse(repository.isWatched(item));
    }    
}

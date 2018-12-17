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

import java.util.ArrayList;
import java.util.Collection;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.orgmodel.ServiceProvider;

/**
 * @author radhakrishnan.j
 * 
 */
public class WatchListServiceImpl implements WatchListService {
	WatchListRepository watchListRepository;

	public void setWatchListRepository(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	public boolean isDealerInWatchList(ServiceProvider dealer) {
		return watchListRepository.isWatched(dealer);
	}

	public boolean isPartInWatchList(Item item) {
		return watchListRepository.isWatched(item);
	}

	public Collection<Item> findPartsInWatchList(Collection<Item> items) {
		Collection<Item> result = new ArrayList<Item>();
		for (Item eachItem : items) {
			if (isPartInWatchList(eachItem)) {
				result.add(eachItem);
			}
		}
		return result;
	}

	public boolean isPartInWatchList(
			Collection<Item> items, boolean forEach) {
		Collection<Item> watchedItems = findPartsInWatchList(items);
		if(watchedItems!=null && watchedItems.size()>0){
			if(forEach && watchedItems.size()==items.size()){
				return true;
			}
			else if(!forEach){
				return true;
			}
		}
		return false;
	}

}

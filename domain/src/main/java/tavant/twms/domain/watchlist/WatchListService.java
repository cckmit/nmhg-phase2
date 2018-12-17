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

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.ServiceProvider;

@Transactional(readOnly = true)
public interface WatchListService {

	public boolean isDealerInWatchList(ServiceProvider dealer);

	public boolean isPartInWatchList(Item item);

	public Collection<Item> findPartsInWatchList(Collection<Item> item);

	public boolean isPartInWatchList(Collection<Item> items,
			boolean forEach);
}